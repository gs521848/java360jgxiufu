import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class Start implements ActionListener{

    JFrame frame=new JFrame("360脱壳部分修复工具");

    JTabbedPane tabPane=new JTabbedPane();

    Container con=new Container();

    JLabel label1=new JLabel("选择文件夹");

    JTextField text1=new JTextField();

    TextArea textArea = new TextArea();

    JButton button1=new JButton("选择");

    JFileChooser jfc=new JFileChooser();

    JRadioButton fileButton1 = new JRadioButton("匹配1（较快）");//将文件读取到list，然后再将整个list写回去

    JRadioButton fileButton2 = new JRadioButton("匹配2（慢，通用）");//将要删除的行替换成空格，其他不变

    public static String inputPath = "C:\\input",txtPath;

    int fileFlag = 2;

    Start(){

        File file = new File(inputPath);
        if(!(file.exists())){
            file.mkdir();
        }
        jfc.setCurrentDirectory(file);


        double lx=Toolkit.getDefaultToolkit().getScreenSize().getWidth();

        double ly=Toolkit.getDefaultToolkit().getScreenSize().getHeight();

        frame.setLocation(new Point((int)(lx/2)-150,(int)(ly/2)-250));

        frame.setSize(500,600);

        frame.setContentPane(tabPane);

        label1.setBounds(10,10,100,20);

        text1.setBounds(100,10,200,20);

        textArea.setBounds(10,220,460,300);

        button1.setBounds(320,10,100,20);

        //选择用哪一种方式修改文件
        fileButton1.setBounds(150,70,150,20);
        fileButton2.setBounds(150,100,150,20);

        ActionListener a3 = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JRadioButton radioButton = (JRadioButton) e.getSource();
                if (radioButton == fileButton1){
                    fileFlag = 1;
                }else {
                    fileFlag = 2;
                }
            }
        };

        fileButton1.addActionListener(a3);
        fileButton2.addActionListener(a3);

        ButtonGroup group3 = new ButtonGroup();

        group3.add(fileButton1);
        group3.add(fileButton2);
        fileButton2.setSelected(true);

        button1.addActionListener(this);

        con.add(fileButton1);
        con.add(fileButton2);

        con.add(label1);

        con.add(text1);

        con.add(textArea);

        con.add(button1);

        con.add(jfc);

        tabPane.add("文件夹/文件选择",con);

        frame.setVisible(true);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

    public void actionPerformed(ActionEvent e){
        if(e.getSource().equals(button1)){
            jfc.setFileSelectionMode(1);

            int state=jfc.showOpenDialog(null);

            if(state==1){
                return;
            }
            else{
                File f=jfc.getSelectedFile();
                inputPath = f.getAbsolutePath().replaceAll(f.getName(),"");
                inputPath = inputPath.substring(0,inputPath.length()-1);
                System.out.println(inputPath);

                text1.setText(f.getAbsolutePath());

                ReadThread readThread = new ReadThread(f.getAbsolutePath(),textArea,fileFlag);
                readThread.start();

                saveFile();
            }

        }

    }

    public static void main(String[] args) {

        File file=new File("");
        System.out.println(file.getAbsolutePath());
        for(int i=0;i<args.length;i++){
            System.out.println("arg:"+i+":"+args[i]);
        }
        txtPath = file.getAbsolutePath() + "\\out\\production"+ "\\lujing.txt";
        if (new File("E:\\nixiang\\AndroidKiller_v1.3.1\\projects").exists()){
            inputPath = "E:\\nixiang\\AndroidKiller_v1.3.1\\projects";
        }

        openFile();
        new Start();

    }

    public static void openFile(){
        if (!(new File(txtPath).exists())){
            System.out.println("路径配置文件不存在");
            return;
        }
        try {
            BufferedReader bf = new BufferedReader(new FileReader(txtPath));
            String string;
            do {
                string = bf.readLine();
//                System.out.println(string);
                if (string != null && string.contains("input") && new File(inputPath).isDirectory()){
                     inputPath = string.split("=")[1].trim();
                    System.out.println("inputPath = " + inputPath);
                }
            }while (string!=null);
            bf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveFile(){
        if (!(new File(txtPath).exists())){
            System.out.println("路径配置文件不存在");
            return;
        }
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(txtPath));
            bw.write("input=" + inputPath + "\r\n");
            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

} 