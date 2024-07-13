import java.io.File;
import java.io.FileWriter;
import java.lang.management.ManagementFactory;
import java.text.DecimalFormat;
import java.util.UUID;

public class Worker {

    static ProduceResults producer = new ProduceResults();
    static ConsumeTask consumer = new ConsumeTask();

    public static Task receive() {
        // remove and take from queue.
        Task task = consumer.consume();
        return task;
    }

    public static void main(String[] args) {
        try {

            final File lock = new File("./LOCK");
            if (!lock.exists()) {
                FileWriter lockWriter = new FileWriter(new File("./LOCK"));
                lockWriter.write(ManagementFactory.getRuntimeMXBean().getName());
                lockWriter.flush();
                lockWriter.close();
            } else {
                Log.info("Task already running");
                System.exit(0);
                return;
            }

            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    lock.deleteOnExit();
                }
            });

            Log.info("Running worker: Version:" + Globals.major + "-" + Globals.minor);
            Log.info("Check if tasks directory exists");
            File file = new File("tasks");
            if (!file.exists()) {
                file.mkdir();
            }
            boolean running = true;
            DecimalFormat df = new DecimalFormat("0.00000000");
            long t = 0;
            try {
                t = Long.parseLong(Server.getProperty("timeout"));
            } catch (Exception ex) {
            }
            long timeOut = t == 0 ? 500 : t;

            while (running) {
                try {
                    Task task = receive();
                    UUID uuid = task.getUuid();

                    double bounty = task.getBounty();
                    String name = task.getClassName();
                    String artifact = task.getArtifact();
                    String parameter = task.getParameter();

                    Log.info("Executing Task:" + name.toString() + ":" + uuid.toString() + ":" + artifact + ":bounty:" + df.format(bounty) + " PHL");
                    task.start();
                    try {
                        Thread.sleep(timeOut);
                    } catch (Exception ex) {
                    }

                } catch(Exception ex) {

                } catch(Error er){
                    consumer.close();
                    consumer = new ConsumeTask();

                }

            }
        } catch (Exception ex) {
            Log.info("ERROR:" + ex.getMessage());
        }
    }
}
