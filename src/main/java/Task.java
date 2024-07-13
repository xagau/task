import java.io.*;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.io.File;
import java.nio.file.Files;

public class Task {

    private UUID uuid = null;
    private long sequence = 0;
    private double bounty = 0;
    private String artifact = "";
    private String className = "";
    private String parameter = "";

    public double getBounty() {
        return bounty;
    }

    public void setBounty(double bounty) {
        this.bounty = bounty;
    }

    public String getArtifact() {
        return artifact;
    }

    public void setArtifact(String artifact) {
        this.artifact = artifact;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void start()
    {
        run();
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public static class ByteClassLoader extends URLClassLoader {
        private final Map<String, byte[]> extraClassDefs;

        public ByteClassLoader(URL[] urls, ClassLoader parent, Map<String, byte[]> extraClassDefs) {
            super(urls, parent);
            this.extraClassDefs = new HashMap<String, byte[]>(extraClassDefs);
        }

        @Override
        protected Class<?> findClass(final String name) throws ClassNotFoundException {
            byte[] classBytes = this.extraClassDefs.remove(name);
            if (classBytes != null) {
                return defineClass(name, classBytes, 0, classBytes.length);
            }
            return super.findClass(name);
        }

    }

    public void download(String fileName)
    {
        try {
            String FILE_URL = Server.getProperty("repository") + getArtifact();

            Log.info("Going to load class from:" + FILE_URL);
            URLConnection urlConnection = new URL(FILE_URL).openConnection();
            urlConnection.addRequestProperty("User-Agent", "Mozilla");
            urlConnection.setReadTimeout(5000);
            urlConnection.setConnectTimeout(10000);


            Log.info("Going to load class from:" + FILE_URL);
            ReadableByteChannel rbc = Channels.newChannel(urlConnection.getInputStream());
            FileOutputStream fos = new FileOutputStream("./tasks/" + fileName);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            fos.flush();
            fos.close();

            Log.info("Finished Download");

        } catch(Exception ex) {
            Log.info(ex);
        }

    }

    private static void addSoftwareLibrary(File file) throws Exception {
        Method method = URLClassLoader.class.getDeclaredMethod("addURL", new Class[]{URL.class});
        method.setAccessible(true);
        method.invoke(ClassLoader.getSystemClassLoader(), new Object[]{file.toURI().toURL()});
    }



    public void run() {
        try {
            Log.info("Starting Download");
            Log.info("Ending Starting Task");

            long startedOn = System.currentTimeMillis();

            String fileName = getClassName() + ".class";
            File file = new File("./tasks/" + fileName);
            if( !file.exists() ) {
                download(fileName);
                Thread.sleep(4000); // Allow system to write to disk.
            } // we already have this task so run the one we have.

            Log.info("Task Step 1");
            Class clazz = null;
            try {

                byte[] bytes = Files.readAllBytes(file.toPath());
                Map<String, byte[]> map = new HashMap<String, byte[]>();
                map.put(getClassName(), bytes);

                ByteClassLoader bcl = new ByteClassLoader(new URL[]{file.toURI().toURL()}, ClassLoader.getSystemClassLoader(), map);
                try { clazz = bcl.findClass(getClassName()); } catch(Exception ex) { Log.info(ex); }
                if( clazz == null ){
                    Log.info("Unable to load class:" + fileName);
                }
                else {
                    Log.info("Class is loaded");
                }
            } catch(Error e) { e.printStackTrace(); } catch(Exception ex) { Log.info(ex); }

            Log.info("Task Step 2");
            Object o = null;
            try{ o = clazz.newInstance(); } catch(Exception ex ){
                Log.info("Could not create new instance of class" + ex.getMessage());
            }
            Method[] m = clazz.getDeclaredMethods();
            boolean methodInvoked = false;
            Method method = null;
            try {
                method = clazz.getDeclaredMethod(
                        "setParameter", String.class);
            } catch(Exception ex) { Log.info(ex); }

            Log.info("Task Step 3");
            try {
                method.invoke(o, parameter);
            } catch(Exception ex) {
                ex.printStackTrace();
            }

            Log.info("Task Step 4");

            for(int i = 0; i < m.length; i++ ){
                if( m[i].getName().equals("data") ) {
                    Log.info("Task Step 5");

                    Result result = (Result)m[i].invoke(o);
                    result.setCompletedOn(System.currentTimeMillis());
                    result.setStartedOn(startedOn);
                    result.setUuid(getUuid());
                    result.setParameter(parameter);
                    result.setPayoutAddress(Server.getProperty("payoutAddress"));
                    Log.info(result.getData());
                    methodInvoked = true;
                    Log.info("Task Successfully to Invoke Method");
                    Worker.producer.publish(result);
                    Log.info("Task Successfully to Published Results");
                    Log.info("Task Step 6");
                    //producer.close();

                    return;
                }
            }
            Log.info("Task Failed to Invoke Method");
        } catch (Exception e) {
            Log.info(e);
        } finally {
            Log.info("Task completed");
        }
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public long getSequence() {
        return sequence;
    }

    public void setSequence(long sequence) {
        this.sequence = sequence;
    }
}



