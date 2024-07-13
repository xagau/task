import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Sean
 */
public class BasicMQ {
    static Connection connection = null;

    public void establishConnection()
    {
        try {
            Log.info("Establish connection");
            String uri = System.getenv("CLOUDAMQP_URL");
            if (uri == null) { uri = Server.getProperty("cloudamqp_url"); }

            Log.info("rabbit uri:" + uri);

            ConnectionFactory factory = new ConnectionFactory();
            Log.info("Factory init:");
            factory.setUri(uri);
            Log.info("Factory set parameters");
            factory.setRequestedHeartbeat(30);
            Log.info("heart beat");
            factory.setConnectionTimeout(30000);
            Log.info("connection timeout");
            factory.setAutomaticRecoveryEnabled(true);
            Log.info("automatic recovery enabled");
            factory.setNetworkRecoveryInterval(10000);
            Log.info("network recovery interval set");
            factory.setTopologyRecoveryEnabled( true );
            Log.info("topology recovery enabled");
            try {
                connection = factory.newConnection();
            } catch(Exception e){
                e.printStackTrace();
            }
            connection = factory.newConnection();
            Log.info("new connection created");

            Log.info("Test connection");
            if( connection == null ){
                Log.info("Connection could not be established");
            } else {
                Log.info("Connection created");
            }
        } catch (URISyntaxException ex) {
            if( Globals.verbose ) {
                ex.printStackTrace();
                Logger.getLogger(BasicMQ.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (NoSuchAlgorithmException ex) {
            if( Globals.verbose ) {
                ex.printStackTrace();
                Logger.getLogger(BasicMQ.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (KeyManagementException ex) {
            if( Globals.verbose ) {
                ex.printStackTrace();
                Logger.getLogger(BasicMQ.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (IOException ex) {
            if( Globals.verbose ) {
                ex.printStackTrace();
                Logger.getLogger(BasicMQ.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch(Exception ex) {
            if( Globals.verbose ) {
                ex.printStackTrace();
            }
        } catch(Error e){
            e.printStackTrace();
        } finally {
            Log.info("Ended");
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
            try {
                connection.close();
                try {
                    connection.abort();
                } catch(Exception ex) { }

            } catch(Exception | Error e){}
            connection = null;
            establishConnection();
        }
    }
}
