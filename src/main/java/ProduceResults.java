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

public class ProduceResults extends BasicMQ {

    public ProduceResults(boolean force) {
        ensureConnection();
    }

    public ProduceResults() {

        ensureConnection();
    }


    public void close()
    {
        try {
            connection.close();
            try {
                connection.abort();
            } catch(Exception ex) {}
        } catch(Exception ex ) {

        } catch(Error e){

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
}
