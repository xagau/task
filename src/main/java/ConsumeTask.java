import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;
import com.xagau.notification.Messenger;
import com.xagau.notification.WebhookDirectory;

import java.text.DecimalFormat;
import java.util.HashMap;

/**
 * @author Sean
 */
public class ConsumeTask extends BasicMQ {

    static HashMap map = new HashMap();

    public ConsumeTask(boolean force, boolean force2) {
        ensureConnection();
    }

    public ConsumeTask(boolean force) {
        ensureConnection();
    }

    public void close(){
        try {
            connection.close();
            try {
                connection.abort();
            } catch(Exception ex) {

            }

        } catch(Exception ex) {

        } catch(Error e){

        }
    }
    public ConsumeTask() {
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

                //System.in.read();

                return r;

            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
            }
        } catch (Exception ex) {
            Log.info("ERROR: Consume failed:" + ex.getMessage());
            ex.printStackTrace();
        } finally {
            try {
                channel.close();
                //close();
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

}

