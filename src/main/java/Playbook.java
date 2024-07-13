import java.io.*;
import java.util.ArrayList;

public class Playbook {
    ArrayList list = new ArrayList();
    public void add(String command){
        list.add(command);
    }

    int ptr = 0; //list.size()-1;
    public String getNextCommand() {
        try {
            return (String)list.get(ptr--);
        } catch(Exception ex) {
            ptr = list.size()-1;
        }
        return "";
    }

    public String[] getCommandList()
    {
        String[] ls = new String[list.size()];
        for(int i = 0; i < ls.length; i++ ){
            ls[i] = (String)list.get(i);
        }
        return ls;
    }

    public void load(String playbook)
    {
        try {
            list = new ArrayList();
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(playbook))));
            String line = null;;
            while((line = reader.readLine()) != null ){
                add(line);
            }

            ptr = list.size() -1;
        } catch(Exception ex) { }
    }


    public static void main(String[] args) throws IOException {
        File fI = new File("./playbook.phl");
        File fO = new File("./playbooknew.phl");

        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new FileOutputStream(fO));
        } catch (Exception e) {
            e.printStackTrace();
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fI)));
        String line = null;;
        while((line = reader.readLine()) != null ){
            writer.println("JSoupTask https://www.wikipedia.org/wiki/" + line);
        }
        writer.flush();
        writer.close();

    }


}
