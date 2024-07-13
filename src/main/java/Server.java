import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class Server {

    static boolean first = true;
    static Properties p = new Properties();

    public static String getProperty(String property) {

        try {
            File f = new File("./server.properties");
            FileInputStream fis = new FileInputStream(f);
            p.load(fis);
            fis.close();
            String prop = p.getProperty(property);

            return prop;
        } catch (Exception ex) {
            Log.info(ex);
        }

        return "<NA>";
    }

    public Server() {
    }
}
