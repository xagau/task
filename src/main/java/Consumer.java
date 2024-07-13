import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;
import com.xagau.notification.Messenger;
import com.xagau.notification.WebhookDirectory;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Queue;

/**
 * @author Sean
 */
public class Consumer extends BasicMQ {

    static HashMap map = new HashMap();

    public Consumer(boolean force, boolean force2) {
        ensureConnection();
    }

    public Consumer(boolean force) {
        ensureConnection();
    }

    public Consumer() {
        ensureConnection();
    }

    // Requires refactoring.
    public Result receive() {

        Channel channel = null;

        try {

            ensureConnection();

            channel = connection.createChannel();

            String queue = "qresults";     //queue name
            Log.info("Consume from queue:" + queue);
            String json = "";
            QueueingConsumer consumer = new QueueingConsumer(channel);
            channel.basicConsume(queue, true, consumer);

            Gson gson = new Gson();

            try {
                QueueingConsumer.Delivery delivery = consumer.nextDelivery();

                byte[] encrypted = delivery.getBody();
                // Decryption
                //AesGcmJce agjDecryption = new AesGcmJce(key.getBytes("ISO-8859-1"));
                //byte[] decrypted = agjDecryption.decrypt(encrypted, aad.getBytes("ISO-8859-1"));

                json = new String(encrypted);
                Result r = gson.fromJson(json, Result.class);


                Log.info(" [x] Received '" + json + "'");

                System.in.read();

                return r;

            } catch (Exception ex) {
                Log.info(ex);
            } finally {
            }
        } catch (Exception ex) {
            Log.info("ERROR: Consume failed:" + ex.getMessage());
            Log.info(ex);
        } finally {
            try {
                channel.close();
            } catch (Exception ex) {
            }
        }
        return null;
    }

    // Requires refactoring.
    public Task consume() {

        Channel channel = null;

        try {

            ensureConnection();

            channel = connection.createChannel();

            String queue = "qtasks";     //queue name
            Log.info("Consume from Queue:" + queue);
            String json = "";
            QueueingConsumer consumer = new QueueingConsumer(channel);

            channel.basicConsume(queue, true, consumer);
            boolean flag = true;
            Gson gson = new Gson();
            while (flag) {
                try {
                    QueueingConsumer.Delivery delivery = consumer.nextDelivery();
                    long deliveryTag = delivery.getEnvelope().getDeliveryTag();

                    //Log.info("TAG:" + deliveryTag);
                    byte[] encrypted = delivery.getBody();
                    // Decryption

                    //AesGcmJce agjDecryption = new AesGcmJce(key.getBytes("ISO-8859-1"));
                    //byte[] decrypted = agjDecryption.decrypt(encrypted, aad.getBytes("ISO-8859-1"));

                    json = new String(encrypted);
                    //Log.info(json);
                    Task t = gson.fromJson(json, Task.class);

                    Log.info(" [x] Received '" + json + "'");

                    if (map.get(t.getUuid()) != null) {
                        channel.basicReject(deliveryTag, true);
                    } else {
                        map.put(t.getUuid(), t.getUuid());
                        channel.basicAck(deliveryTag, true);
                    }

                    return t;

                } catch (Exception ex) {
                    Log.info(ex);
                } finally {
                    Globals.lag();
                }
            }
        } catch (Exception ex) {
            Log.info("ERROR: Consume failed:" + ex.getMessage());
            Log.info(ex);
        } finally {
            try {
                channel.close();
            } catch (Exception ex) {
            }
            Globals.lag();
        }
        return null;
    }


    public void send(String address, String amount) {
        MoneyMQ mq = new MoneyMQ();
        mq.send(address, amount);
    }


    // Requires refactoring.
    public void receiveAll() {

        Channel channel = null;

        try {

            FileWriter writer = new FileWriter(new File("./output.log"));

            ensureConnection();

            channel = connection.createChannel();

            String queue = "qresults";     //queue name
            Log.info("Consume from Queue:" + queue);
            String json = "";
            QueueingConsumer consumer = new QueueingConsumer(channel);
            channel.basicConsume(queue, true, consumer);

            Gson gson = new Gson();

            HashMap<String, Double> balance = new HashMap<String, Double>();


            boolean flag = true;
            try {
                while (flag) {
                    try {

                        QueueingConsumer.Delivery delivery = consumer.nextDelivery();
                        if (delivery == null) {
                            Log.info("Next delivery was null");
                            flag = false;
                            continue;
                        }

                        byte[] encrypted = delivery.getBody();
                        // Decryption
                        //AesGcmJce agjDecryption = new AesGcmJce(key.getBytes("ISO-8859-1"));
                        //byte[] decrypted = agjDecryption.decrypt(encrypted, aad.getBytes("ISO-8859-1"));

                        json = new String(encrypted);


                        //Jsoup
                        //Log.info(" [x] Received '" + json + "'");
                        Result result = gson.fromJson(json, Result.class);

                        long k = result.getSequence();
                        String payoutAddress = result.getPayoutAddress();
                        Log.info("PayoutAddress:" + payoutAddress);

                        String text = result.getData();
                        Log.info("processed:" + k);
                        if( text.contains("::BEGIN_DATA::")){
                            continue;
                        } else {
                            Log.info(text);
                        }
                        writer.write(text + "\n");
                        writer.flush();

                        double bounty = Globals.payout;

                        DecimalFormat df = new DecimalFormat("0.00000000");

                        Double d = balance.get(payoutAddress);
                        if (d == null) {
                            balance.put(payoutAddress, Globals.payout);
                        } else {
                            balance.put(payoutAddress, d + Globals.payout);
                        }
                        double nb = balance.get(payoutAddress);
                        if (nb > Globals.payoutThreshold) {

                            send(payoutAddress, df.format(nb));

                            try {
                                String wh = WebhookDirectory.lookup("discord-placeholder-incoming");
                                Messenger.notify(wh, "TaskRunner [" + payoutAddress + "] Completed", "Total Payout " + df.format(nb) + " PHL");
                            } catch (Exception ex) {
                            }
                            balance.put(payoutAddress, 0d);
                        }
                        //}

                        Thread.sleep(50);
                    } catch (Exception ex) {
                        Log.info(ex);
                        Log.info("Problem in receive all:" + ex.getMessage());
                        flag = false;
                    }
                }

            } catch (Exception ex) {
                Log.info(ex);
            } finally {
            }
        } catch (Exception ex) {
            Log.info("ERROR: Consume failed:" + ex.getMessage());
            Log.info(ex);
        } finally {
            try {
                channel.close();
            } catch (Exception ex) {
            }
        }
    }

}

