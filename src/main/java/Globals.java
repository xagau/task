import java.util.HashMap;

public class Globals {
    public static int minor = 38;
    public static int major = 1;

    public static HashMap<String, String> map = new HashMap<String,String>();
    public static double payout = 0.0167002;
    public static boolean verbose = true;
    public static double payoutThreshold = 5;

    static {
        try {
            String ver = Server.getProperty("verbose");
            if (ver == null) {
                ver = "false";
            }
            Globals.verbose = Boolean.parseBoolean(ver);
            String payday = Server.getProperty("payout");
            if( payday != null ) {
                payout = Double.parseDouble(payday);
            }

        } catch(Exception ex) { }

    }
    public static void lag()
    {
        try {
            Thread.sleep(40);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
