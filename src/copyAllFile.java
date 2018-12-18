import java.io.*;

public class copyAllFile {


    //??????
    static String url1="F:\\play\\startPicActivityHeng\\smali\\com\\example";
    //????????
    static String url2="F:\\aa\\smali\\com\\example";



    public static void main(String[] args) throws IOException {
        // TODO Auto-generated method stub
        //????????????
        File targetFile = new File(url2);
        if (!(targetFile.exists())){
            targetFile.mkdirs();
        }
        //????????§Ö????????????
        File[] file=(new File(url1)).listFiles();
        for (int i = 0; i < file.length; i++) {
            if(file[i].isFile()){
                //???????
                copyFile(file[i],new File(url2+File.separator+file[i].getName()));
            }
            if(file[i].isDirectory()){
                //??????
                String sorceDir=url1+File.separator+file[i].getName();
                String targetDir=url2+File.separator+file[i].getName();
                copyDirectiory(sorceDir, targetDir);
            }
        }

    }

    public static void copyStart(String url1,String url2) throws IOException{
        File targetFile = new File(url2);
        if (!(targetFile.exists())){
            targetFile.mkdirs();
        }
        //????????§Ö????????????
        File[] file=(new File(url1)).listFiles();
        for (int i = 0; i < file.length; i++) {
            if(file[i].isFile()){
                //???????
                copyFile(file[i],new File(url2+File.separator+file[i].getName()));
            }
            if(file[i].isDirectory()){
                //??????
                String sorceDir=url1+File.separator+file[i].getName();
                String targetDir=url2+File.separator+file[i].getName();
                copyDirectiory(sorceDir, targetDir);
            }
        }
    }

    public static void copyFile(File sourcefile,File targetFile) throws IOException{

        //?????????????????????§Ý???
        FileInputStream input=new FileInputStream(sourcefile);
        BufferedInputStream inbuff=new BufferedInputStream(input);

        //????????????????????§Ý???
        FileOutputStream out=new FileOutputStream(targetFile);
        BufferedOutputStream outbuff=new BufferedOutputStream(out);

        //????????
        byte[] b=new byte[1024*5];
        int len=0;
        while((len=inbuff.read(b))!=-1){
            outbuff.write(b, 0, len);
        }

        //?????????????
        outbuff.flush();

        //?????
        inbuff.close();
        outbuff.close();
        out.close();
        input.close();
    }

    public static void copyDirectiory(String sourceDir,String targetDir) throws IOException{

        //????????
        (new File(targetDir)).mkdirs();

        //????????§Ö???????????
        File[] file=(new File(sourceDir)).listFiles();

        for (int i = 0; i < file.length; i++) {
            if(file[i].isFile()){
                //????
                File sourceFile=file[i];
                //??????
                File targetFile=new File(new File(targetDir).getAbsolutePath()+File.separator+file[i].getName());

                copyFile(sourceFile, targetFile);

            }

            if(file[i].isDirectory()){
                //??????????????
                String dir1=sourceDir+file[i].getName();
                //????????????????
                String dir2=targetDir+"/"+file[i].getName();

                copyDirectiory(dir1, dir2);
            }
        }

    }
}
