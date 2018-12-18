import javax.swing.*;
import java.io.*;

/**
 * Create By Qiujuer
 * 2014-07-26
 * <p/>
 * ???????????�E????????
 */
public class ProcessModel {
    //???��?
    private static final String BREAK_LINE;
    //??????????
    private static final byte[] COMMAND_EXIT;
    //?????
    private static byte[] BUFFER;

    /**
     * ????????????
     */
    static {
        BREAK_LINE = "\n";
        COMMAND_EXIT = "\nexit\n".getBytes();
        BUFFER = new byte[32];
    }

    /**
     * ???????
     *
     *
     *               <pre> eg: "/system/bin/ping", "-c", "4", "-s", "100","www.qiujuer.net"</pre>
     * @return ??��??
     */
    public static void execute(String command) {
        Process process = null;
        StringBuilder sbReader = null;

        BufferedReader bReader = null;
        InputStreamReader isReader = null;

        InputStream in = null;
        InputStream err = null;
        OutputStream out = null;

        try{
            process = new ProcessBuilder().command("cmd").redirectErrorStream(true).start();
            out = process.getOutputStream();
            in = process.getInputStream();
            err = process.getErrorStream();

            byte[] bytesCmd = ("\n"+command+"\n").getBytes();
            out.write(bytesCmd);
            out.write(COMMAND_EXIT);

            out.flush();

//            process.waitFor();

            isReader = new InputStreamReader(in);
            bReader = new BufferedReader(isReader);

            String s;
            if ((s = bReader.readLine()) != null){
                sbReader = new StringBuilder();
                sbReader.append(s);
                sbReader.append(BREAK_LINE);
                while ((s = bReader.readLine()) != null){
                    System.out.println(s);
                    sbReader.append(s);
                    sbReader.append(BREAK_LINE);
                }
            }
            while((err.read(BUFFER)) > 0){
                System.out.println("err");

            }
        } catch (IOException e) {
            JOptionPane.showConfirmDialog(null, "文件路径有误", "结束", JOptionPane.DEFAULT_OPTION);
            e.printStackTrace();
        } /*catch (InterruptedException e) {
            e.printStackTrace();
        }*/finally {
            closeAllStream(out,err,in,isReader,bReader);

            if (process != null){
                processDestroy(process);
                process =null;
            }
        }
        /*if (sbReader == null){
            return null;
        }else {
            return sbReader.toString();
        }*/
    }

    /**
     * ?????????
     *
     * @param out       ?????
     * @param err       ??????
     * @param in        ??????
     * @param isReader  ?????????
     * @param bReader   ?????????
     */
    private static void closeAllStream(OutputStream out,InputStream err,InputStream in,
                                       InputStreamReader isReader,BufferedReader bReader){
        if (out != null){
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (err != null){
            try {
                err.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (in != null){
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (isReader != null){
            try {
                isReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (bReader != null){
            try {
                bReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * ???Android???????????
     *
     * @param process ????
     */
    private static void killProcess(Process process){
        int pid = getProcessId(process);
        if (pid != 0){
            try {
                killProcess(process);
            }catch (Exception ex){

            }
        }
    }

    /**
     * ????????ID
     *
     * @param process ????
     * @return
     */
    private static int getProcessId(Process process){
        String str = process.toString();
        try{
            int i = str.indexOf("=") + 1;
            int j = str.indexOf("]");
            str = str.substring(i,j);
            return Integer.parseInt(str);
        }catch (Exception e){
            return 0;
        }
    }

    /**
     * ???????
     *
     * @param process ????
     */
    private static void processDestroy(Process process){
        if (process != null){
            try{
                //?��???????????
                if (process.exitValue() != 0){
//                    killProcess(process);
                    try {
                        Runtime.getRuntime().exec(process.toString());
                    } catch (IOException e) {
                        JOptionPane.showConfirmDialog(null, "文件路径有误", "结束", JOptionPane.DEFAULT_OPTION);
                        e.printStackTrace();
                    }
                }
            }catch (IllegalThreadStateException e){
//                killProcess(process);
                try {
                    Runtime.getRuntime().exec(process.toString());
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

}
