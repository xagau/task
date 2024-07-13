import java.io.*;
import java.util.HashMap;
import java.util.Iterator;

public class Chomper {
    public static void main(String[] args){
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("./_results.txt")));
            HashMap hm = new HashMap();
            String line = null;
            while( (line = reader.readLine()) != null ) {
                try {
                    String[] split = line.split(" ");
                    for (int i = 0; i < split.length; i++) {
                        try {
                            split[i] = split[i].replace("#", "");
                            split[i] = split[i].replace("\\?", "");
                            split[i] = split[i].replace(".", "");
                            split[i] = split[i].replace("$", "");
                            split[i] = split[i].replace("&", "");
                            split[i] = split[i].replace("\\.", "");
                            split[i] = split[i].replaceAll("\\[", "");
                            split[i] = split[i].replaceAll("]", "");
                            split[i] = split[i].replace("(", "");
                            split[i] = split[i].replace(")", "");
                            split[i] = split[i].replace("0", "");
                            split[i] = split[i].replace("1", "");
                            split[i] = split[i].replace("2", "");
                            split[i] = split[i].replace("3", "");
                            split[i] = split[i].replace("!", "");
                            split[i] = split[i].replace("'", "");
                            split[i] = split[i].replace("\"", "");
                            split[i] = split[i].replace("4", "");
                            split[i] = split[i].replace("5", "");
                            split[i] = split[i].replace("6", "");
                            split[i] = split[i].replace("7", "");
                            split[i] = split[i].replace("8", "");
                            split[i] = split[i].replace("9", "");
                            split[i] = split[i].replace(",", "");
                            split[i] = split[i].replace("\"", "");
                            split[i] = split[i].replace(":", "");
                            split[i] = split[i].replace(";", "");
                            split[i] = split[i].replace("<", "");
                            split[i] = split[i].replace(">", "");
                            split[i] = split[i].replace("+", "");
                            split[i] = split[i].replace("-", "");
                            split[i] = split[i].replace("=", "");
                            split[i] = split[i].replace("%", "");
                            split[i] = split[i].replace("^", "");
                            split[i] = split[i].replace("/", "");
                            split[i] = split[i].replace("\\", "");
                            split[i] = split[i].replace("{", "");
                            split[i] = split[i].replace("}", "");
                            split[i] = split[i].replace("|", "");

                            hm.put(split[i], split[i]);
                        } catch (Exception ex) {
                            Log.info(ex);
                            System.in.read();
                        }

                    }
                } catch(Exception ex) { if( Globals.verbose ) { ex.printStackTrace(); } System.in.read(); }
            }

            System.out.println(hm.size());
            System.in.read();

            Iterator iterator = hm.values().iterator();
            int head = 10;
            int ctr = 0;
            while(iterator.hasNext()){
                String s = (String)iterator.next();
                System.out.println(s);
                ctr++;
                if( ctr > head){
                    break;
                }
            }
            System.in.read();

            PrintWriter writer = new PrintWriter(new FileOutputStream(new File("./__npb.phl")));
            Iterator itr = hm.values().iterator();
            while(itr.hasNext()){
                String tok = (String)itr.next();
                if( !tok.contains("!@#$%^&*()_+=?")) {
                    writer.println("JSoupTask https://en.wikipedia.org/wiki/" + tok);
                }
            }
            writer.flush();
            writer.close();

        } catch(Exception ex) {
        }

    }
}
