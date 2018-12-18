
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/5/25.
 */
public class packageTxt {
    public static void main(String[] args) {

    }

    public static void readTxt(String string,String path) throws IOException{
        File filepath = new File(path);
        if (!filepath.exists()){
            filepath.mkdirs();
        }
        File file = new File(path + "\\com.bin");
        if (!file.exists()){
            file.createNewFile();
        }

        FileWriter fw = new FileWriter(file);
        fw.write(string);
        fw.close();

        BufferedInputStream bis=new BufferedInputStream(new FileInputStream(file));
        List<Integer> list =new ArrayList<Integer>();
        int len;
        while((len=bis.read()) !=-1)
            list.add(len^592);
        bis.close();
        BufferedOutputStream bos =new BufferedOutputStream(new FileOutputStream(file));
        for(Integer i:list)
            bos.write(i);
        bos.close();
    }
}
