import java.io.*;

/**
 * Created by shiwangi on 12/3/16.
 */
public class Tesseract {
    public static void  main(String args[]) throws IOException {
        Runtime rt = Runtime.getRuntime();
       // Process pr = rt.exec("ls");

       // instance.setDatapath("/usr/share/tesseract-ocr");
        Process pr = rt.exec("tesseract ./resources/try2.png ./resources/outputtext");


        BufferedReader error = new BufferedReader(new InputStreamReader(pr.getErrorStream()));
        int exitVal = 0;
        String everything ="";
        try {
            exitVal = pr.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        BufferedReader br = new BufferedReader(new FileReader("./resources/outputtext.txt"));
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
           everything = sb.toString();
        } finally {
            br.close();
        }
        String m,s="";
        while((m=error.readLine())!=null){
            s+="\n"+m;
        }
        System.out.println("Exited with error code "+s);
        return ;
    }
}
