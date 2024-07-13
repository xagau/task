import com.google.gson.Gson;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.rabbitmq.client.*;
import com.google.crypto.tink.subtle.AesGcmJce;
import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;
import com.xagau.notification.Messenger;
import com.xagau.notification.WebhookDirectory;

import java.io.*;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;


import java.sql.Timestamp;

public class Producer extends BasicMQ {

    public Producer(boolean force) {
        ensureConnection();
    }

    public Producer() {

        ensureConnection();
    }

    public void publish(Task t) {
        Channel channel = null;
        try {
            ensureConnection();
            String queue = "qtasks";     //queue name
            Log.info("Publish to Queue:" + queue);
            boolean durable = true;    //durable - RabbitMQ will never lose the queue if a crash occurs
            boolean exclusive = false;  //exclusive - if queue only will be used by one connection
            boolean autoDelete = false; //autodelete - queue is deleted when last consumer unsubscribes

            Gson gson = new Gson();

            // 2. Java object to JSON string
            String json = gson.toJson(t);

            try {
                channel = connection.createChannel();
                channel.exchangeDeclare(queue, "direct", true);
                //channel.queueDeclarePassive(queue);//, true, false, false, null);
                channel.queueBind(queue, queue, queue);

            } catch (Exception ex) {
                Log.info("Unable to create channel:" + ex);
            }

            //AesGcmJce agjEncryption = new AesGcmJce(key.getBytes("ISO-8859-1"));
            //byte[] encrypted = agjEncryption.encrypt(json.getBytes("ISO-8859-1"), aad.getBytes("ISO-8859-1"));

            String exchangeName = queue;
            String routingKey = queue;
            Log.info("Routing Key:" + routingKey);
            if (channel.isOpen()) {
                channel.basicPublish(exchangeName, queue, MessageProperties.PERSISTENT_TEXT_PLAIN, json.getBytes());
                Log.info(" [x] Sent '" + new String(json) + "'");
                channel.close();
            }

        } catch (Exception ex) {
            Log.info("ERROR: publish failed:" + ex.getMessage());
            Log.info("connection:" + (connection==null));
            ex.printStackTrace();
        } finally {
            try {
                channel.close();
                Globals.lag();
            } catch (Exception ex) {
            }
        }
    }

    public void publish(Result t) {
        Channel channel = null;
        try {
            ensureConnection();


            String queue = "qresults";     //queue name
            Log.info("Publish to Queue:" + queue);
            boolean durable = true;    //durable - RabbitMQ will never lose the queue if a crash occurs
            boolean exclusive = false;  //exclusive - if queue only will be used by one connection
            boolean autoDelete = false; //autodelete - queue is deleted when last consumer unsubscribes

            Gson gson = new Gson();

            // 2. Java object to JSON string
            String json = gson.toJson(t);

            try {
                channel = connection.createChannel();
                channel.exchangeDeclare(queue, "direct", true);
                channel.queueDeclarePassive(queue); //queueDeclare(); //queue, true, false, false, null);
                channel.queueBind(queue, queue, queue);
                channel.basicQos(1);

                //channel.exchangeDeclarePassive(queue);
                //channel.queueDeclare(queue, durable, false, false, null);
            } catch (Exception ex) {
                Log.info(ex);
            }

            //AesGcmJce agjEncryption = new AesGcmJce(key.getBytes("ISO-8859-1"));
            //byte[] encrypted = agjEncryption.encrypt(json.getBytes("ISO-8859-1"), aad.getBytes("ISO-8859-1"));

            String exchangeName = queue;
            String routingKey = queue;
            Log.info("Routing Key:" + routingKey);
            if (channel.isOpen()) {
                channel.basicPublish(exchangeName, queue, MessageProperties.PERSISTENT_TEXT_PLAIN, json.getBytes());
                Log.info(" [x] Sent '" + new String(json) + "'");
                channel.close();
            }

        } catch (Exception ex) {
            Log.info("ERROR: publish failed:" + ex.getMessage());
        } finally {
            try {
                channel.close();
                Globals.lag();
            } catch (Exception ex) {
            }
        }
    }



    public static void main(String[] args) throws Exception {

        Log.info("WARNING: THIS METHOD SENDS A LIVE TEST TRANSACTION");
        Log.info("Running Producer: Version:" + Globals.major + "-" + Globals.minor);

        try {
            String wh = WebhookDirectory.lookup("discord-placeholder-incoming");
            Messenger.notify(wh, "Task Queue Halted", "Notification");
        } catch (Exception ex) {
        }


        final Consumer consumer = new Consumer();

        Thread t = new Thread() {
            public void run() {
                consumer.receiveAll();
            }
        };
        t.start();

        System.in.read();
        Producer producer = new Producer();
        int BATCH = 290000;
        int bc = 0;

        boolean running = true;
        int count = 0;

        Playbook playbook = new Playbook();
        playbook.load("./garlpb.phl");

        playbook.ptr = playbook.list.size();

        while (running) {

            try {

                if (bc > BATCH) {
                    running = false;
                }
                bc++;
                //int MAX_TRIES = (int) (Math.random() * 70);
                //MAX_TRIES = Math.max(10, MAX_TRIES);
                double sum = 0.0;
                double bounty = Double.parseDouble(Server.getProperty("payout")); //Globals.payout;

                Task task = new Task();
                task.setBounty(bounty);
                Thread.sleep(Long.parseLong(Server.getProperty("patience"))); // add to playbook / properties
                count++;
                sum += bounty; // add to properties
                Log.info(count);

                String line = playbook.getNextCommand(); // scanner.nextLine();
                String[] split = line.split(" ");
                String param = UUID.randomUUID().toString();
                String className = line;
                String artifact = TaskLookup.lookup(line);
                if( split.length >= 2 ){
                    className = split[0];
                    param = split[1];
                    artifact = TaskLookup.lookup(split[0]);
                }

                task.setArtifact(artifact);
                task.setClassName(className);
                task.setSequence(bc);
                task.setParameter(param);
                task.setUuid(UUID.randomUUID());
                producer.publish(task);
                bc++;

                Thread.sleep(Long.parseLong(Server.getProperty("patience"))); // add to playbook / properties

            } catch(Exception ex) {
                Log.info(ex);
            } catch(Error e) {
                e.printStackTrace();
            }
        }

        try {
            String wh = WebhookDirectory.lookup("discord-placeholder-incoming");
            Messenger.notify(wh, "Task Queue Halted", "Notification");
        } catch (Exception ex) {
        }

    }
}

/**
 *
 *                 //task.setArtifact("FPTXZbypFK66GfDY9iBHkK6Xfkqs9iNF6T");
 *                 //task.setArtifact("FNKFrcBXetmBMF5PpgW3cF7nMJo2WakNmY"); // Get ASIN task. (default)
 *                 //task.setArtifact("FStNKUGZkeghQjKUfRLtCFSrEfTVGNXv7z"); // Collect Data Task
 *                 //task.setArtifact("FNGfcqfd4Ls1ypEmW54ogm63iNM4nS1chK"); // Get ASIN Thallu CA
 *                 //task.setArtifact("FDdeZwuPekRKqnmsHXWm9GgV1TWL82SiSW"); // Get ASIN Thallu US
 *                 //task.setArtifact("FSCiKdnW9bNhafiq9Spz8BcsGNvjdxg2Lj"); // Old Adam Task
 *                 //task.setArtifact("F79K7nR2SP3Jpjb8xdFySx87L2sFkBg49S");
 *
 *                 //task.setArtifact("FSnmLZed76C8Xho5WA8tCtyAt8H38FptV7"); // VacuumTask
 *
 *                 //task.setArtifact("FL69boiryXM5BtasEvgoWgSrBJmf7gDBKL"); // DeleteCacheTask
 *
 *                 //task.setArtifact("FTyX5GfuXsjLgNZKe7T4zwD5kbUptSYxRp"); // CleanupTask
 *                 //task.setArtifact("FNGX2JJg5XULbRND4XqKHicSpn1zVCD3TE"); // DownloadLatestFrameworkTask
 *                 //task.setArtifact("FH3FgdRMcaTsvPkBAXJDm4XfUDXJBqWnD4"); // VerifyInstallVersionTask
 *
 *                 //task.setArtifact("FUw3bekSmSGrKrAMQ1EX81CHdX1fXH2ZXy");
 *
 *                 //task.setArtifact("FDvMJtpXc1TA1P6sDfeNeRdqYDWw6prNTg"); //EchoVersionCodeTask
 //task.setClassName("EchoVersionCodeTask");

 //task.setClassName("DeepLearningModelAdamTask");
 //task.setClassName("CollectDataTask");
 //task.setClassName("VerifyInstallVersionTask");
 //task.setClassName("RestartFrameworkTask");
 //task.setClassName("DownloadLatestFrameworkTask");
 //task.setClassName("CleanupTask");
 //task.setClassName("DeleteCacheTask");
 //task.setClassName("VacuumTask");

 */