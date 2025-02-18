import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author Sean
 */
public class MoneyMQ {
    static Connection connection = null;

    public void establishConnection()
    {
        try {

            String uri = "amqp://mc:yX2K3UYcnB7u@peppy-silver-mosquito.rmq.cloudamqp.com/mc";
            if (uri == null) {

                Log.info("Unable to send");
                return;
            }

            ConnectionFactory factory = new ConnectionFactory();
            factory.setUri(uri);
            factory.setRequestedHeartbeat(30);
            factory.setConnectionTimeout(30000);
            factory.setAutomaticRecoveryEnabled(true);
            factory.setNetworkRecoveryInterval(10000);
            factory.setTopologyRecoveryEnabled( true );

            connection = factory.newConnection();
        } catch (URISyntaxException ex) {
            if( Globals.verbose ) {
                Logger.getLogger(MoneyMQ.class.getName()).log(Level.SEVERE, null, ex);
                ex.printStackTrace();
            }
        } catch (NoSuchAlgorithmException ex) {
            if( Globals.verbose ) {
                Logger.getLogger(MoneyMQ.class.getName()).log(Level.SEVERE, null, ex);
                ex.printStackTrace();
            }
        } catch (KeyManagementException ex) {
            if( Globals.verbose ) {
                Logger.getLogger(MoneyMQ.class.getName()).log(Level.SEVERE, null, ex);
                ex.printStackTrace();
            }
        } catch (IOException ex) {
            if( Globals.verbose ) {
                Logger.getLogger(MoneyMQ.class.getName()).log(Level.SEVERE, null, ex);
                ex.printStackTrace();
            }
        } catch(Exception ex) {
            if( Globals.verbose ) {
                ex.printStackTrace();
            }
        }

    }

    public boolean isConnectionDead()
    {
        if ( connection == null ){
            return true;
        }
        if( !connection.isOpen() ){
            return true;
        }
        return false;
    }

    public void ensureConnection()
    {
        if( isConnectionDead() ){
            establishConnection();
        }
    }

    public static void main(String args[])
    {
        MoneyMQ mq = new MoneyMQ();
        mq.send("FMqEG2AK1WXSoYucVvNSFnA9AFoDQtRmPU", "1.000");
    }

    public void send(String payoutAddress, String money) {
        Channel channel = null;
        try {
            ensureConnection();

            channel = connection.createChannel();

            Transaction t = new Transaction();
            t.setCurrency("PHL");
            t.setOtp(Server.getProperty("otp"));
            t.setClientId("NONE");
            t.setTerminalId(Server.getProperty("terminalid"));
            t.setAmount(Double.parseDouble(money));
            t.setReceipent(payoutAddress);
            t.setTransactionId("MC" + System.currentTimeMillis());

            String queue = "transactions-mc";     //queue name
            Log.info("Publish to Queue:" + queue);
            boolean durable = true;    //durable - RabbitMQ will never lose the queue if a crash occurs
            boolean exclusive = false;  //exclusive - if queue only will be used by one connection
            boolean autoDelete = false; //autodelete - queue is deleted when last consumer unsubscribes

            Gson gson = new Gson();

            // 2. Java object to JSON string
            String json = gson.toJson(t);

            try {
                channel.queueDeclare(queue, durable, exclusive, autoDelete, null);
            } catch (Exception ex) {
            }

            //AesGcmJce agjEncryption = new AesGcmJce(key.getBytes("ISO-8859-1"));
            //byte[] encrypted = agjEncryption.encrypt(json.getBytes("ISO-8859-1"), aad.getBytes("ISO-8859-1"));

            String exchangeName = "";
            String routingKey = "transactions-mc";
            Log.info("Routing Key:" + routingKey);
            channel.basicPublish(exchangeName, routingKey, null, json.getBytes());
            Log.info(" [x] Sent '" + new String(json) + "'");

            channel.close();
        } catch (Exception ex) {
            Log.info("ERROR: publish failed:" + ex.getMessage());
            ex.printStackTrace();
        } finally {
            try {
                channel.close();
                Globals.lag();
            } catch (Exception ex) {
            }
        }
    }
}
