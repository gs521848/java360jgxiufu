
import org.w3c.dom.Document;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

/**
 * Created by Administrator on 2016/5/23.
 */
public class ReadThread extends Thread {
    private String filePath = "";
    private TextArea textArea = new TextArea();
    private String projectPath = "";

    public String startActivity;
    public String qsPackage = "";
    public String ruanSkyPac = "";
    public String pac7723 = "";
    public String ccPackage = "";

    String searchFile = "";
    public List<String> listActivity;

    int fileFlag;

    public ReadThread(String filePath, TextArea textArea,int fFlag){
        init();
        this.filePath = filePath;
        fileFlag = fFlag;
//        System.out.println("filePath:" + this.filePath);
        File file=new File("");
        projectPath = file.getAbsolutePath() + "\\out\\production";
        System.out.println("projtctPath: " + projectPath);
        this.textArea = textArea;
    }

    public void init(){
        startActivity = "";
        qsPackage = "";
        ruanSkyPac = "";
        pac7723 = "";
        ccPackage = "";

        searchFile = "";
    }

    @Override
    public void run() {
        long time1 = System.currentTimeMillis();
        if (filePath.endsWith("Project")){
            allSmali(filePath);

            //添加盒子
//            addBox(filePath);
        }else {
            System.out.println("文件路径不是以Project结尾！！！");
            textArea.append(filePath + "文件路径不是以Project结尾！！！" + "\n");
        }

        textArea.append(filePath + "文件夹处理完成" + "\n" + "\n");
        long time2 = System.currentTimeMillis();
        System.out.println("time:" + (time2 - time1));
    }

    public void allSmali(String projectPath){
        int i = 1;
        String path;
        do {
            if (i == 1){
                path = projectPath + "\\smali\\";
            }
            else{
                path = projectPath + "\\smali_classes" + i + "\\";
            }
            doAllFile(new File(path));

            i++;
        }while ( new File(projectPath + "\\smali_classes" + i).exists());

    }

    public void doAllFile(File file){
        if(file.isDirectory()){
            File[] files = file.listFiles();
            for (int i = 0;i < files.length;i++){
                doAllFile(files[i]);
            }
        }else {
            if (fileFlag == 1){
                readFile(file.getAbsolutePath());
            }else {
                compareDeleteFile(file.getAbsolutePath());
            }
        }
    }

    public void readFile(String filePath){
        boolean flag = false;//是否有匹配到规则
        boolean nextOrigFlag = false;//Orig下一行是否需要删除
        boolean nextOpcodeFlag = false;//Opcode下一行是否需要删除
        String beforeString = "";//前一行的数据
        try {
            BufferedReader File_pwd=new BufferedReader(new FileReader(filePath));
            java.util.List<String> list=new ArrayList<String>();
            String temp;
            do{
                temp=File_pwd.readLine();
                list.add(temp);
            }while(temp!=null);

            File_pwd.close();

            String s= "";
            List<String> listAfter=new ArrayList<String>();
            for(int j=0;j<list.size()-1;j++) {
                s = list.get(j);

                /*删除
                invoke-static/range {v0 .. v0}, Lcom/stub/StubApp;->getOrigApplicationContext(Landroid/content/Context;)Landroid/content/Context;
                move-result-object v0
                */
                if (s.contains("com/stub/StubApp;->getOrigApplicationContext")) {
                    nextOrigFlag = true;
                    flag = true;
                    continue;
                } else if (nextOrigFlag && !s.equals("")){
                    nextOrigFlag = false;
                    continue;
                }
                /*
                替换：
	            #disallowed odex opcode
	            #invoke-object-init/range {v0 .. v0}, Ljava/lang/Object;-><init>()V
                nop
	            替换成：
	            invoke-direct {p0}, Ljava/lang/Object;-><init>()V

	            替换：
	            #disallowed odex opcode
                #return-void-barrier
                nop
	            替换成：
	            return-void
                 */
                else if (s.contains("#disallowed odex opcode")){
                    nextOpcodeFlag = true;
                    beforeString = s;
                    continue;
                }else if (nextOpcodeFlag && !s.equals("")){
                    nextOpcodeFlag = false;
                    if (s.contains("invoke-object-init/range {p0 .. p0}, Ljava/lang/Object;-><init>()V")){
                        flag = true;
                        listAfter.add("invoke-direct {p0}, Ljava/lang/Object;-><init>()V");
                    }else if (s.contains("#return-void-barrier")){
                        flag = true;
                        listAfter.add("return-void");
                    }else {
                        listAfter.add(beforeString);//没有替换的把“#disallowed odex opcode”也写回去，方便以后的查找
                        listAfter.add(s);
                    }
                }
                //删除：invoke-static {v0}, Lcom/stub/StubApp;->interface11(I)V
                else if (s.contains("invoke-static {v0}, Lcom/stub/StubApp;->interface11(I)V")){
                    flag = true;
                    continue;
                }
                //删除：invoke-static {p1}, Lcom/stub/StubApp;->mark(Landroid/location/Location;)V
                else if (s.contains("invoke-static {p1}, Lcom/stub/StubApp;->mark(Landroid/location/Location;)V")){
                    flag = true;
                    continue;
                }
                /*替换invoke-static {v2, v3}, Lcom/stub/StubApp;->mark(Landroid/location/LocationManager;Ljava/lang/String;)Landroid/location/Location;
                    换成invoke-virtual {v2, v3}, Landroid/location/LocationManager;->getLastKnownLocation(Ljava/lang/String;)Landroid/location/Location;
                */
                else if (s.contains("Lcom/stub/StubApp;->mark(Landroid/location/LocationManager;Ljava/lang/String;)Landroid/location/Location")){
                    String prime = s.split("\\{")[1].split("}")[0];
                    listAfter.add("invoke-virtual {" + prime + "}, Landroid/location/LocationManager;->getLastKnownLocation(Ljava/lang/String;)Landroid/location/Location;");
                    flag = true;
                    continue;
                }

                else {
//                    File_bak.write(s + "\n");
                    listAfter.add(s);
                }
            }

            if (flag){
                BufferedWriter File_bak=new BufferedWriter(new FileWriter(new File(filePath)));
                for (int j = 0;j < listAfter.size();j++){
                    File_bak.write(listAfter.get(j) + "\n");
                }
                File_bak.flush();
                File_bak.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean compareDeleteFile(String fileName) {
        boolean nextOrigFlag = false;

        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(fileName, "rw");
            String line = null;
            long lastPoint = 0; //��ס��һ�ε�ƫ����
            while ((line = raf.readLine()) != null) {
                final long ponit = raf.getFilePointer();
                if (line.contains("com/stub/StubApp;->getOrigApplicationContext")) {
                    nextOrigFlag = true;
                    String str = "";
                    for (int i = 0;i < line.length();i++){
                        str = str + " ";
                    }
                    raf.seek(lastPoint);
                    raf.writeBytes(str);
                } else if (nextOrigFlag && !line.equals("")){
                    nextOrigFlag = false;
                    String str = "";
                    for (int i = 0;i < line.length();i++){
                        str = str + " ";
                    }
                    raf.seek(lastPoint);
                    raf.writeBytes(str);
                }
                else if (line.contains("invoke-static {p1}, Lcom/stub/StubApp;->mark(Landroid/location/Location;)V")){
                    String str = "";
                    for (int i = 0;i < line.length();i++){
                        str = str + " ";
                    }
                    raf.seek(lastPoint);
                    raf.writeBytes(str);
                }
                else if (line.contains("invoke-static {v0}, Lcom/stub/StubApp;->interface11(I)V")){
                    String str = "";
                    for (int i = 0;i < line.length();i++){
                        str = str + " ";
                    }
                    raf.seek(lastPoint);
                    raf.writeBytes(str);
                }
                /*else if (line.contains("Lcom/stub/StubApp;->mark(Landroid/location/LocationManager;Ljava/lang/String;)Landroid/location/Location;")){
                    String str = "";
                    String prime = line.split("\\{")[1].split("}")[0];
                    str = "invoke-virtual {" + prime + "}, Landroid/location/LocationManager;->getLastKnownLocation(Ljava/lang/String;)Landroid/location/Location;";
                    *//*for (int i = 0;i < line.length() - str.length();i++){
                        str = str + " ";
                    }*//*
                    raf.seek(lastPoint);
                    raf.writeBytes(str);
                }*/

                lastPoint = ponit;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                raf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public void addBox(String filePath){
        try {
//                    packageTxt.readTxt(startActivity,outputPath+listFileName.get(i)+"\\assets");
            copyAllFile.copyStart(projectPath + "\\wlb3733", filePath + "\\smali\\com\\wlb3733");

            if (!new File(filePath + "\\assets").exists()){
                new File(filePath + "\\assets").mkdirs();
            }
            copyAllFile.copyFile(new File(projectPath + "\\box.apk"), new File(filePath + "\\assets\\box.apk"));

            Document document = XMLCurdByDom.getXMLDocument(filePath+"\\AndroidManifest.xml");
            String icon = XMLCurdByDom.getIcon(document);
            if (icon.equals("")){
                icon = "drawable";
            }
            //复制图片
            String draw = "";
            if (new File(filePath + "\\res\\" + icon + "-hdpi-v4").exists()){
                draw = filePath + "\\res\\" + icon + "-hdpi-v4";
            }else if (new File(filePath + "\\res\\" + icon + "-ldpi-v4").exists()){
                draw = filePath + "\\res\\" + icon + "-ldpi-v4";
            }else if (new File(filePath + "\\res\\" + icon + "-hdpi").exists()){
                draw = filePath + "\\res\\" + icon + "-hdpi";
            }else if (new File(filePath + "\\res\\" + icon + "-ldpi").exists()){
                draw = filePath + "\\res\\" + icon + "-ldpi";
            }else if (new File(filePath + "\\res\\" + icon).exists()){
                draw = filePath + "\\res\\" + icon;
            }else {
                draw = getDrawFileName(new File(filePath + "\\res"),icon);
            }
            if (!draw.equals("")){
                copyAllFile.copyFile(new File(projectPath + "\\wlb_bg.png"), new File(draw + "\\wlb_bg.png"));
                copyAllFile.copyFile(new File(projectPath + "\\wlb_install.png"), new File(draw + "\\wlb_install.png"));
                copyAllFile.copyFile(new File(projectPath + "\\wlb_splash.png"), new File(draw + "\\wlb_splash.png"));
            }else {
                textArea.append("处理" + filePath + "时，复制图片没找到drawable相关的文件夹，请联系开发人员"+"\n");
            }

            //在public.xml文件添加图片的id
            Document publicDoc = XMLCurdByDom.getXMLDocument(filePath + "\\res\\values\\public.xml");
            String drawID = XMLCurdByDom.getDrawId(publicDoc,icon);
            XMLCurdByDom.addPubId(publicDoc,drawID,icon);

            //如果drawID不是0x7f02的话，代码里面也需要修改
            if (!drawID.contains("0x7f02")){
                doDrawID(filePath + "\\smali\\com\\wlb3733\\SplashActivity.smali",drawID);
                doDrawID(filePath + "\\smali\\com\\wlb3733\\xhd3733Activity.smali",drawID);
            }

            XMLCurdByDom.saveXML(publicDoc,filePath + "\\res\\values\\public.xml");

            String startFirstActivity = XMLCurdByDom.getStartPackage(document);
            startActivity = startFirstActivity;

            listActivity = XMLCurdByDom.getAllActivity(document);
            //软天空的真正启动项
            ruanSkyPac = XMLCurdByDom.getRuanSkyPac(document);
            //7723的真正启动项
            if (startActivity.equals("com.cx.again.MainActivity")){
                pac7723 = XMLCurdByDom.get7723Pac(document);
            }

            //虫虫游戏获取真正的activity
            if(new File(filePath + "\\smali\\ccplay\\cc\\a\\InitActivity.smali").exists()){
                readFile(filePath + "\\smali\\ccplay\\cc\\a\\InitActivity.smali","const-string v1, \"");
            }

            //骑士助手
            File f = new File(filePath + "\\smali\\com\\fx\\gg\\KPActivity.smali");
            if (f.exists()){
                readFile(filePath + "\\smali\\com\\fx\\gg\\KPActivity.smali","const-string");
            }
            if (new File(filePath + "\\smali\\com\\example\\assetexam\\Welcome.smali").exists()){
                readFile(filePath + "\\smali\\com\\example\\assetexam\\Welcome.smali","const-string");
            }
            if (new File(filePath + "\\smali\\com\\ex4ample\\youzi\\ADAD.smali").exists()){
                readFile(filePath + "\\smali\\com\\ex4ample\\youzi\\ADAD.smali","startService(Landroid/content/Intent;)Landroid/content/ComponentName;");
            }
            if (!qsPackage.equals("")){
                String qsPath = packageToAbsolutePath(qsPackage,filePath + "\\smali\\",0);
                if (new File(qsPath ).exists()){
                    readFile(qsPath,"startService(Landroid/content/Intent;)Landroid/content/ComponentName;");
                }
            }
            if (new File(filePath + "\\smali\\com\\nhncorp\\skundeadck\\SplashActivity$1.smali").exists()){
                readFile(filePath + "\\smali\\com\\nhncorp\\skundeadck\\SplashActivity$1.smali","const-class");
            }

            //纽扣助手
            f = new File(filePath + "\\smali\\com\\game\\god\\FaActivity.smali");
            if (f.exists()){
                readFile(filePath + "\\smali\\com\\game\\god\\FaActivity.smali","invoke-virtual {p0}, Lcom/game/god/FaActivity;->myDialog()V");

            }

            //爱吾（25az）去签名验证和图片广告
            f = new File(filePath + "\\smali\\com\\aiwu\\Splash.smali");
            if (f.exists()){
                if (new File(filePath + "\\smali\\cn\\egame\\terminal\\paysdk\\EgameLaunchActivity.smali").exists()){
                    readFile(filePath + "\\smali\\cn\\egame\\terminal\\paysdk\\EgameLaunchActivity.smali","invoke-static {v5}, Landroid/os/Process;->killProcess(I)V");
                    readFile(filePath + "\\smali\\cn\\egame\\terminal\\paysdk\\EgameLaunchActivity.smali","setContentView(Landroid/view/View;)V");
                }

                String s;
                for (int j = 0;j < listActivity.size();j++){
                    s = packageToAbsolutePath(listActivity.get(j),filePath + "\\smali\\",0);
                    f = new File(s);
                    if (f.exists()){
                        readFile(s,"->killProcess(I)V");
                    }
                }

                readFile(filePath + "\\smali\\com\\aiwu\\Splash.smali","setContentView(Landroid/view/View;)V");

            }


            if (startActivity.equals("com.qiqiersan.sdk.QqesShellActivity") && !(ruanSkyPac.equals(""))){
                startActivity = ruanSkyPac;
            } else if (!(qsPackage.equals(""))){
                startActivity = qsPackage;
            }else if (startActivity.equals("com.cx.again.MainActivity") && !pac7723.equals("")){
                startActivity = pac7723;
            }else if (startActivity.equals("ccplay.cc.a.InitActivity") && !ccPackage.equals("")){
                startActivity = ccPackage;
            }

            doNextAcitvity(filePath + "\\smali\\com\\wlb3733\\SplashActivity$1.smali");

            XMLCurdByDom.addNode(document);

            XMLCurdByDom.saveXML(document,filePath+"\\AndroidManifest.xml");

        } catch (IOException e) {
            textArea.append("处理"+filePath+"时，程序出现问题，请联系开发人员"+"\n");
            e.printStackTrace();
        }
    }

    public String getDrawFileName(File file,String icon){
        String drawName = "";
        String fileName;
        File[] files = file.listFiles();
        for (int i = 0;i < files.length;i++){
            fileName = files[i].getAbsolutePath();
            if (fileName.contains(icon)){
                drawName = fileName;
                return drawName;
            }
        }
        return drawName;
    }

    public void doDrawID(String filePath,String drawID) {
        //老方法需要重新写入全部
        try {
            BufferedReader File_pwd = new BufferedReader(new FileReader(filePath));
            List<String> list = new ArrayList<String>();
            String temp;
            do {
                temp = File_pwd.readLine();
                list.add(temp);
            } while (temp != null);

            File_pwd.close();

            BufferedWriter File_bak = new BufferedWriter(new FileWriter(new File(filePath)));
            String s = "";
            for (int j = 0; j < list.size() - 1; j++) {
                s = list.get(j);

                if (s.contains("0x7f02")) {
                    if (filePath.contains("SplashActivity.smali")){
                        s = "const v1, " + drawID + "0283";//const v1, 0x7f020283
                    }else if (filePath.contains("xhd3733Activity.smali")){
                        if (s.contains("0282")){
                            s = "const v3, " + drawID + "0282";//const v3, 0x7f020282
                        }else if (s.contains("0281")){
                            s = "const v3, " + drawID + "0281";//const v3, 0x7f020281
                        }
                    }
                    File_bak.write(s + "\n");
                    continue;
                } else {
                    File_bak.write(s + "\n");
                }
            }
            File_bak.flush();
            File_bak.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void doNextAcitvity(String filePath){
        //老方法需要重新写入全部
        try {
//            BufferedReader File_pwd=new BufferedReader(new InputStreamReader(new FileInputStream(filePath),"GBK"));
            BufferedReader File_pwd=new BufferedReader(new FileReader(filePath));
            List<String> list=new ArrayList<String>();
            String temp;
            do{
                temp=File_pwd.readLine();
                list.add(temp);
            }while(temp!=null);

            File_pwd.close();

//            BufferedWriter File_bak=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath)));
            BufferedWriter File_bak=new BufferedWriter(new FileWriter(new File(filePath)));
            String s= "";
            for(int j=0;j<list.size()-1;j++) {
                s = list.get(j);

                if (s.contains("com.zhizhuang.MainActivity")) {
                    s.replace("com.zhizhuang.MainActivity",startActivity);
                    s = "const-string v2, \"" + startActivity + "\"";
                    File_bak.write(s + "\n");
                    continue;
                } else {
                    File_bak.write(s + "\n");
                }
            }
            File_bak.flush();
            File_bak.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean readFile(String filePath,String str){
        boolean flag = false,mFlag = false,ruFlag = false,qsFlag = false,googleServiceFlag = false;
        try {
            //??????
            BufferedReader File_pwd=new BufferedReader(new FileReader(filePath));
            //??????????????list;
            List<String> list=new ArrayList<String>();
            List<String> listAfter=new ArrayList<String>();
            //????????????????????
            String temp;
            do{
                //?????????????
                temp=File_pwd.readLine();
//                System.out.println("????????????:"+temp);
                list.add(temp);
                //?????????????????????
            }while(temp!=null);

            File_pwd.close();

            //???????????????
//            BufferedWriter File_bak=new BufferedWriter(new FileWriter(new File(filePath)));
            String s=new String();
            //???????????
            int commentFlag = 0;
            for(int j=0;j<list.size()-1;j++) {
                //????????????????????,??????replaceall????,?????????????????????I
                s = list.get(j);

                //骑士助手
                if (str.equals("const-string") || str.equals("const-class")){
                    if (!qsFlag && s.contains("const-string")){
                        qsPackage = s.split("\"")[1];
                        qsFlag = true;
                        flag = true;
//                        File_bak.write(s + "\n");
                        listAfter.add(s);
                    }
                    if (s.contains("const-class")){
                        qsPackage = s.split("L")[1];
                        qsPackage = qsPackage.substring(0,qsPackage.length() - 1).replaceAll("/",".");
                        flag = true;
//                        File_bak.write(s + "\n");
                        listAfter.add(s);
                    }
//                    File_bak.write(s + "\n");
                    listAfter.add(s);
                }
                //虫虫游戏
                else if (str.equals("const-string v1, \"")){
                    if (s.contains(str)){
                        ccPackage = s.split("\"")[1];
                    }
                }
               else {
                    if (s.contains(str)){
                        flag = true;
                        //???????????????
                        if (str.contains("invoke-virtual {p0}, Lcom/game/god/FaActivity;->myDialog()V")){
//                            File_bak.write("    invoke-virtual {p0}, Lcom/game/god/FaActivity;->startGame()V" + "\n");
                            listAfter.add("    invoke-virtual {p0}, Lcom/game/god/FaActivity;->startGame()V");
                        }
                        continue;
                    }else {
//                        File_bak.write(s + "\n");
                        listAfter.add(s);
                    }
                }
            }

            if (flag){
                BufferedWriter File_bak=new BufferedWriter(new FileWriter(new File(filePath)));
                for (int j = 0;j < listAfter.size();j++){
                    File_bak.write(listAfter.get(j) + "\n");
                }
                File_bak.flush();
                File_bak.close();
            }

            if (flag){
                System.out.println(filePath + "： file write succeed");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return flag;
    }

    public String packageToAbsolutePath(String string,String path,int appActivityFlag){
        if (string.startsWith(".") || !(string.contains("."))){
            File file = new File(path);
            String s;
            if (string.contains(".")){
                s = string.substring(string.lastIndexOf(".") + 1,string.length()) + ".smali";
            }else if (string.startsWith(".")){
                s = string.substring(1,string.length()) + ".smali";
            }else {
                s = string + ".smali";
            }
            searchFile(file,s);
            path = searchFile;

            System.out.println("searchFile:" + searchFile);

            if (!(searchFile.equals(""))){
                String[] ss = path.split("\\\\");
                int flag = 0;
                String s1 = "";
                for (int k = 0;k < ss.length;k++){
                    if (ss[k].equals("smali")){
                        flag = 1;
                        continue;
                    }
                    if (flag == 0){
                        continue;
                    }
                    if (k == ss.length -1){
                        s1 += s.substring(0,s.length() - 6);
                    }else {
                        s1 += ss[k] + ".";
                    }
                }
                if (appActivityFlag == 0){
                    startActivity = s1;
                }/*else if (appActivityFlag == 1){
                    application = s1;
                }*/
            }
        }else {
            String[] s = string.split("\\.");
            System.out.println(s.length);
            for (int j = 0;j < s.length;j++){
                if (j < s.length-1){
                    path += s[j] + File.separator;
                }else {
                    path += s[j] + ".smali";
                }
            }
        }

        return path;
    }

    public void searchFile(File file,String string){
        if (file.isDirectory()){
            File[] files = file.listFiles();
            for (int i = 0;i < files.length;i++){
                searchFile(files[i],string);
            }
        }else {
            if (file.getName().equals(string)){
                searchFile = file.getAbsolutePath();
                return;
            }
        }
    }
}
