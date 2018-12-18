import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.util.ArrayList;
import java.util.List;

/**
 * ???DOM?????XML???????????
 * @author Administrator
 *
 */

public class XMLCurdByDom {
    public static String screenOrientation = "";

    public static String getStartPackage(Document document){
        screenOrientation = "";
        String string = "";
        int i,j,k;
        Element element,childElement,parentElement;
        Node childNodeAction = null,childNodeCategory = null;
        NodeList nodeList,childNodelist;

        //查找程序入口activity
        nodeList = document.getElementsByTagName("intent-filter");
        for (i = 0;i < nodeList.getLength();i++){
            k = 0;
            element = (Element)nodeList.item(i);
            childNodelist = element.getChildNodes();
            for (j = 0;j < childNodelist.getLength();j++){
                //Node.ELEMENT_NODE表示当前节点是元素节点
                if (childNodelist.item(j).getNodeType()==Node.ELEMENT_NODE){
                    childElement = (Element) childNodelist.item(j);
                    if (childElement.getNodeName().equals("action") && childElement.getAttribute("android:name").equals("android.intent.action.MAIN")){
                        childNodeAction = childNodelist.item(j);
                        k++;
//                        System.out.println(k);
                    }
                    if (childElement.getNodeName().equals("category") && childElement.getAttribute("android:name").equals("android.intent.category.LAUNCHER")){
                        childNodeCategory = childNodelist.item(j);
                        k++;
//                        System.out.println(k);
                    }
                }
            }

            if (k == 2){
                parentElement = (Element)element.getParentNode();

                if (parentElement.getTagName().equals("activity-alias")){
                    string = parentElement.getAttribute("android:targetActivity");
                }else {
                    string = parentElement.getAttribute("android:name");
                }

                screenOrientation = parentElement.getAttribute("android:screenOrientation");

                if (!(string.contains("com.example.StartPicActivity"))){
                    element.removeChild(childNodeAction);
                    element.removeChild(childNodeCategory);
                }
            }
        }
        return string;
    }

    public static boolean deleteActivity(Document document){
        NodeList nodeList;
        Element element;
        int i;
        boolean b = false;

        //去google等广告，去掉activity
        nodeList = document.getElementsByTagName("activity");
        for (i = 0;i < nodeList.getLength();i++){
            element = (Element) nodeList.item(i);
            if (element.getAttribute("android:name").equals("com.google.android.gms.ads.AdActivity")
                    || element.getAttribute("android:name").equals("com.qq.e.ads.ADActivity")
                    || element.getAttribute("android:name").equals("com.unity3d.ads.android.view.UnityAdsFullscreenActivity")
                    || element.getAttribute("android:name").equals("com.unity3d.ads.android2.view.UnityAdsFullscreenActivity")
                    || element.getAttribute("android:name").equals("com.unity3d.ads.adunit.AdUnitActivity")
                    || element.getAttribute("android:name").equals("com.heyzap.sdk.ads.HeyzapInterstitialActivity")
                    || element.getAttribute("android:name").equals("com.vungle.publisher.FullScreenAdActivity")
                    || element.getAttribute("android:name").equals("com.vungle.publisher.VideoFullScreenAdActivity")
                    || element.getAttribute("android:name").equals("com.inmobi.rendering.InMobiAdActivity")
                    || element.getAttribute("android:name").equals("com.revmob.FullscreenActivity")
                    || element.getAttribute("android:name").equals("com.yandex.mobile.ads.AdActivity")
                    || element.getAttribute("android:name").equals("com.jirbo.adcolony.AdColonyFullscreen")
                    || element.getAttribute("android:name").equals("com.hg.framework.MoreGamesActivity")
                    || element.getAttribute("android:name").equals("com.baidu.mobads.AppActivity")
                    || element.getAttribute("android:name").equals("com.chartboost.sdk.CBImpressionActivity")
                    || element.getAttribute("android:name").equals("com.zynga.sdk.mobileads.InterstitialActivity")
                    || element.getAttribute("android:name").equals("com.tapjoy.TJAdUnitActivity")
                    //下面三个为百度多酷广告
                    || element.getAttribute("android:name").equals("com.duoku.platform.single.ui.DKRecommendActivity")
                    || element.getAttribute("android:name").equals("com.duoku.platform.single.ui.DKStartDownloadActivity")
                    || element.getAttribute("android:name").equals("com.duoku.platform.single.ui.DKLotteryDrawActivity")

                    || element.getAttribute("android:name").equals("com.tom.pkgame.activity.PkHallActivity")
                    || element.getAttribute("androdi:name").equals("com.supersonicads.sdk.controller.ControllerActivity")
                    || element.getAttribute("android:name").equals("com.ipeaksoft.ad.libadipeak.IpeakAdActivty")
                    || element.getAttribute("android:name").equals("com.pc.android.video.activity.VideoPlayActivity")
                    || element.getAttribute("android:name").equals("com.mopub.mobileads.MraidActivity")){
                element.getParentNode().removeChild(element);
                b = true;
            }

            //回编译出错
            if (element.hasAttribute("android:resizeableActivity")){
                element.setAttribute("resizeableActivity",element.getAttribute("android:resizeableActivity"));
                element.removeAttribute("android:resizeableActivity");
            }
            if(element.hasAttribute("android:supportsPictureInPicture")){
                element.setAttribute("supportsPictureInPicture",element.getAttribute("android:supportsPictureInPicture"));
                element.removeAttribute("android:supportsPictureInPicture");
            }
            if (element.hasAttribute("android:enableVrMode")){
                element.setAttribute("enableVrMode",element.getAttribute("android:enableVrMode"));
                element.removeAttribute("android:enableVrMode");
            }
        }

        //回编译出错
        nodeList = document.getElementsByTagName("layout");
        for (i = 0;i < nodeList.getLength();i++){
            element = (Element)nodeList.item(i);
            if (element.hasAttribute("android:defaultHeight")){
                element.setAttribute("defaultHeight",element.getAttribute("android:defaultHeight"));
            }
            if (element.hasAttribute("android:defaultWidth")){
                element.setAttribute("defaultWidth",element.getAttribute("android:defaultWidth"));
            }
        }

        //去地铁酷跑国外版闪退问题（在飞机城Airport City会闪退）
        nodeList = document.getElementsByTagName("service");
        for(i = 0;i < nodeList.getLength();i++){
            element = (Element)nodeList.item(i);
            if (/*element.getAttribute("android:name").equals("com.anamnionic.suzerainty.KilehPlashyService") &&*/
                    element.getAttribute("android:exported").equals("true")){
                element.getParentNode().removeChild(element);
                b = true;
            }
        }

        return b;
    }

    public static List getAllActivity(Document document){
        List list = new ArrayList<>();
        NodeList nodeList;
        Element element;

        nodeList = document.getElementsByTagName("activity");
        for (int i = 0;i < nodeList.getLength();i++){
            element = (Element) nodeList.item(i);
            list.add(element.getAttribute("android:name"));
        }

        return list;
    }

    public static String getRuanSkyPac(Document document){
        NodeList nodeList;
        Element element;
        String ruanSkyPac = "";

        nodeList = document.getElementsByTagName("meta-data");
        for (int i = 0;i < nodeList.getLength();i++){
            element = (Element) nodeList.item(i);
            if (element.getAttribute("android:name").equals("qqes_original")){
                ruanSkyPac = element.getAttribute("android:value");
            }
        }
        return ruanSkyPac;
    }

    public static String get7723Pac(Document document){
        NodeList nodeList;
        Element element;
        String pac7723 = "";

        nodeList = document.getElementsByTagName("meta-data");
        for (int i = 0;i < nodeList.getLength();i++){
            element = (Element) nodeList.item(i);
            if (element.getAttribute("android:name").equals("original_as")){
                pac7723 = element.getAttribute("android:value");
            }
        }
        return pac7723;
    }

    public static String getApplication(Document document){
        NodeList nodeList;
        Element element;

        String application = "null";
        nodeList = document.getElementsByTagName("application");
        element = (Element)nodeList.item(0);
        application = element.getAttribute("android:name");

        //回编译出错，将属性前的“android:”去掉
        if (element.hasAttribute("android:resizeableActivity")){
            element.setAttribute("resizeableActivity",element.getAttribute("android:resizeableActivity"));
            element.removeAttribute("android:resizeableActivity");
        }
        if(element.hasAttribute("android:supportsPictureInPicture")){
            element.setAttribute("supportsPictureInPicture",element.getAttribute("android:supportsPictureInPicture"));
            element.removeAttribute("android:supportsPictureInPicture");
        }
        if(element.hasAttribute("android:roundIcon")){
            element.setAttribute("roundIcon",element.getAttribute("android:roundIcon"));
            element.removeAttribute("android:roundIcon");
        }

        return application;
    }

    public static String getIcon(Document document){
        String icon = "";
        NodeList nodeList;
        Element element;

        nodeList = document.getElementsByTagName("application");
        element = (Element)nodeList.item(0);

        icon = element.getAttribute("android:icon");
        if (icon.contains("@") && icon.contains("/")){
            icon = icon.split("/")[0].split("@")[1];
        }else {
            icon = "";
        }

        return icon;
    }

    /*
    <activity android:name="com.wlb3733.xhd3733Activity" android:screenOrientation="portrait" android:theme="@android:style/Theme.NoTitleBar.Fullscreen" android:windowSoftInputMode="stateHidden">
	            <intent-filter>
	                <action android:name="android.intent.action.MAIN"/>
	                <category android:name="android.intent.category.LAUNCHER"/>
	            </intent-filter>
	    </activity>
	    <activity android:name="com.wlb3733.SplashActivity" android:screenOrientation="portrait" android:theme="@android:style/Theme.NoTitleBar.Fullscreen" android:windowSoftInputMode="stateHidden"/>
    */
    public static void addNode(Document document)
    {
        //????????????
        Element newNodeActivity=document.createElement("activity");
        //???<????>???
        NodeList nl=document.getElementsByTagName("application");
        //??????????
        newNodeActivity.setAttribute("android:name", "com.wlb3733.xhd3733Activity");
        newNodeActivity.setAttribute("android:screenOrientation","portrait");
        newNodeActivity.setAttribute("android:theme","@android:style/Theme.NoTitleBar.Fullscreen");
        newNodeActivity.setAttribute("android:windowSoftInputMode","stateHidden");

        //???????,??????application
        nl.item(0).appendChild(newNodeActivity);

        Element newNodeChild = document.createElement("intent-filter");
        newNodeActivity.appendChild(newNodeChild);

        Element newNode = document.createElement("action");
        newNode.setAttribute("android:name","android.intent.action.MAIN");
        newNodeChild.appendChild(newNode);

        newNode = document.createElement("category");
        newNode.setAttribute("android:name","android.intent.category.LAUNCHER");
        newNodeChild.appendChild(newNode);

        //加第二个activity
        Element newNodeActivity2=document.createElement("activity");

        newNodeActivity2.setAttribute("android:name", "com.wlb3733.SplashActivity");
        newNodeActivity2.setAttribute("android:screenOrientation","portrait");
        newNodeActivity2.setAttribute("android:theme","@android:style/Theme.NoTitleBar.Fullscreen");
        newNodeActivity2.setAttribute("android:windowSoftInputMode","stateHidden");

        //???????,??????application
        nl.item(0).appendChild(newNodeActivity2);
    }

    public static String getDrawId(Document document, String icon){
        String id = "";
        NodeList pubList = document.getElementsByTagName("public");
        for (int i = 0;i < pubList.getLength();i++){
            Element element = (Element)pubList.item(i);
            if (element.getAttribute("type").equals(icon)){
                id = element.getAttribute("id");
                id = id.substring(0,6);//截取前5个字符，例:0x7f02
                return id;
            }
        }
        return id;
    }

    //悅游添加图片id
    /*
    <public type="drawable" name="wlb_bg" id="0x7f020281" />
    <public type="drawable" name="wlb_install" id="0x7f020282" />
    <public type="drawable" name="wlb_splash" id="0x7f020283" />
     */
    public static void addPubId(Document document,String drawID, String icon)
    {
        Element newNodePub=document.createElement("public");
        newNodePub.setAttribute("type",icon);
        newNodePub.setAttribute("name","wlb_bg");
        newNodePub.setAttribute("id",drawID + "0281");

        Element newNodePub2=document.createElement("public");
        newNodePub2.setAttribute("type",icon);
        newNodePub2.setAttribute("name","wlb_install");
        newNodePub2.setAttribute("id",drawID + "0282");

        Element newNodePub3=document.createElement("public");
        newNodePub3.setAttribute("type",icon);
        newNodePub3.setAttribute("name","wlb_splash");
        newNodePub3.setAttribute("id",drawID + "0283");

        NodeList nodeList = document.getElementsByTagName("resources");
        nodeList.item(0).appendChild(newNodePub);
        nodeList.item(0).appendChild(newNodePub2);
        nodeList.item(0).appendChild(newNodePub3);
    }

    public static void addDeleteInfo(Document document,String firstActivity,String afterActivity){
        NodeList nodeList,childNodeList,grandsonNodeList;
        Element element,childElement,grandsonElement;
        int count = 0;
        String string;

        nodeList = document.getElementsByTagName("activity");
        for (int i = 0;i < nodeList.getLength();i++){

            element = (Element)nodeList.item(i);
            string = element.getAttribute("android:name");
            if (element.getAttribute("android:name").equals(firstActivity)){
                childNodeList = element.getChildNodes();

                for (int j = 0;j < childNodeList.getLength();j++){
                    //Node.ELEMENT_NODE表示当前节点是元素节点
                    if (childNodeList.item(j).getNodeType()==Node.ELEMENT_NODE){
                        childElement = (Element)childNodeList.item(j);
                        grandsonNodeList =  childElement.getChildNodes();

                        for (int k = 0;k < grandsonNodeList.getLength();k++){
                            //Node.ELEMENT_NODE表示当前节点是元素节点
                            if (grandsonNodeList.item(k).getNodeType() == Node.ELEMENT_NODE){
                                grandsonElement = (Element)grandsonNodeList.item(k);
                                if (grandsonElement.getNodeName().equals("category") && grandsonElement.getAttribute("android:name").equals("android.intent.category.LAUNCHER")){
                                    childElement.removeChild(grandsonElement);
                                    count++;
                                    break;
                                }
                            }
                        }

                        if (count != 0){
                            break;
                        }
                    }
                }

            }
            if (element.getAttribute("android:name").equals(afterActivity)){
                Element newNodeChild = document.createElement("intent-filter");
                element.appendChild(newNodeChild);

                Element newNode = document.createElement("action");
                newNode.setAttribute("android:name","android.intent.action.MAIN");
                newNodeChild.appendChild(newNode);

                newNode = document.createElement("category");
                newNode.setAttribute("android:name","android.intent.category.LAUNCHER");
                newNodeChild.appendChild(newNode);

                count++;
            }

            if (count == 2){
                break;
            }
        }
    }

    public static void addPermision(Document document,String string){
        Element newNode = document.createElement("uses-permission");
        NodeList nodeList = document.getElementsByTagName("manifest");
        newNode.setAttribute("android:name",string);
        nodeList.item(0).appendChild(newNode);
    }

    //???DOM????
    public static Document getXMLDocument(String string)
    {
        DocumentBuilder builder=null;
        Document document=null;
        try {
            builder=DocumentBuilderFactory.newInstance().newDocumentBuilder();
            document=builder.parse(string);
        } catch (Exception e) {
            JOptionPane.showConfirmDialog(null, "apk名称/路径有误，最好全部改成英文字母，然后重新运行程序", "结束", JOptionPane.DEFAULT_OPTION);
            e.printStackTrace();
        }

        return document;
    }

    /**
     * ??????????????????????��??????
     * @param document
     */
    public static void saveXML(Document document,String string)
    {
        Transformer tf=null;
        try{
            tf=TransformerFactory.newInstance().newTransformer();
            tf.transform(new DOMSource(document), new StreamResult(string));
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

