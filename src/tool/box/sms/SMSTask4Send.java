package tool.box.sms;

import smartvip.mainStage.center.leftTopControl.fae.*;
import java.util.List;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import smartvip.mainStage.HostController;
import tool.box.common.Tool;
import smartvip.mainStage.center.leftTopControl.maker.excel.FileCheck4CG901A;

public class SMSTask4Send {
    private Stage stage;
    private Scene scene; 
    private ProgressDialog progressDialog;
    private ProgressIndicator progressIndicator;
    private VBox vBox;
    private static String ResStr = "";
    private TabFAEController tabFAEController;       
    
    public SMSTask4Send(Stage stage, TabFAEController tabFAEController) {
        this.initUIsAndParams(stage, tabFAEController);
    }

    /**
     * 多线程工作
     * 
     * @param objs      参数列表key - value 模式  0=No 1=CheckBox 2=Tip 3=Content 4=ModelType 5=SubCmd 6=SendLen 7=ReceiveLen 8=Offset  
     * @param model     产品型号
     * @param simCodes  手机号码 数组
     * @param timeout   超时阈值
     * @return "OK"
     */
    public String run(Object[][] objs, String model, String simCodes, int timeout) { // 单位 【秒】
        
        Task<String> progressTask = new Task<String>() {
            @Override
            protected String call() throws Exception {
                StringBuilder sb = new StringBuilder();
                for(int i=0; i<objs.length; i++) {
                    if((boolean)objs[i][1] && (boolean)objs[i][4]) { // CheckBox & ModelType
                        sb.append((String)objs[i][5]).append(":").append((String)objs[i][3]).append(";");
                    }
                }
                tabFAEController.addLog("短信内容为：" + sb.substring(0, sb.length()-1), "短信内容长度：" + sb.substring(0, sb.length()-1).length());
                
                // 判断短信内容是否超过66
                if(sb.substring(0, sb.length()-1).length() > 66) {
                    tabFAEController.addLog("短信内容长度超过66个字节，重新编辑", null);
                    return "overLen";
                }
                
                // 判断手机号码是否11位，数量是否超过100
                String[] codes = simCodes.split(",");
                if(codes.length > 100) {
                    tabFAEController.addLog("手机号码超过100个，重新编辑", null);
                    return "overCodes";
                }
                for(int i=0; i<codes.length; i++) {
                    if(codes[i].length() != 11) {
                        tabFAEController.addLog("手机号码长度存在非11位，需重新编辑", null);
                        return "simCodeError";                        
                    }
                }
                
                HttpClient client = new HttpClient();
                PostMethod post = new PostMethod("http://gbk.sms.webchinese.cn");
                post.addRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=gbk");//在头文件中设置转码
                NameValuePair[] data = {new NameValuePair("Uid", "SmartVIP_SMS"), 
                                        new NameValuePair("Key", "4feb9a2821e47aeb7993"), 
                                        new NameValuePair("smsMob", simCodes), 
                                        new NameValuePair("smsText", sb.substring(0, sb.length() - 1))};
                post.setRequestBody(data);

                client.executeMethod(post);
                Header[] headers =  post.getResponseHeaders();
                int statusCode = post.getStatusCode();
                System.out.println("statusCode:" + statusCode);
                for (Header h : headers) {
                    System.out.println(h.toString());
                }
                String result = new String(post.getResponseBodyAsString().getBytes("gbk"));
                System.out.println(result); //打印返回消息状态

                post.releaseConnection();      
                
                return result; 
            }
            @Override
            protected void succeeded() {
                super.succeeded();
                if(getValue().equals("1")) {
                    tabFAEController.addLog("短信发送成功，号码为" + simCodes, null);
                } else {
                    tabFAEController.addLog("短信发送未成功.......", null);
                }
                progressDialog.close();
            }
            @Override
            protected void cancelled() {
                super.cancelled(); //To change body of generated methods, choose Tools | Templates.
            }
        };
        
        new Thread(progressTask).start();
        progressDialog.onShow();

        return ResStr;        
    }

    static class ProgressDialog extends Stage {

        public ProgressDialog(Stage owner, Scene scene) {
            this.initStyle(StageStyle.TRANSPARENT); // Stage 没有窗口装饰
            this.initModality(Modality.APPLICATION_MODAL);
            this.initOwner(owner);
            this.setScene(scene);
        }

        public void onShow() {
            sizeToScene();
            centerOnScreen();
            showAndWait();                
        }
    }
    
    private int doGetDataProtocol(int cmd, int subCmd, int[] checkCmds, List<List<Object>> listExcel, int count) {
        byte[] frame = HostController.serial.getDataProtocol().getItem(0, true);
        if(frame[2] == (byte)cmd) { 
            int subCmdInt = ((frame[6]| 0x0000) << 8) | ((frame[7] | 0x0000) & 0x00FF);
            for(int j=0; j<checkCmds.length; j++) {
                if(checkCmds[j] == subCmdInt) { 
                    if(subCmdInt == 0x0343) {
                        String verHard = Tool.bytesLittleEndian2String(frame, 8, 18);
                        listExcel.get(j).set(3, verHard);
                        listExcel.get(j).set(4, (verHard.equals(FileCheck4CG901A.verHard)) ? "OK" : "ERROR"); // 55 7A 71 00 13 00 03 18 35 30 34 30 2D 31 38 30 2D 32 30 31 35 31 32 32 35 41 2D
                    } else if (subCmdInt == 0x0344) {
                        String verSoft = Tool.bytesLittleEndian2String(frame, 8, 4);
                        listExcel.get(j).set(3, verSoft);
                        listExcel.get(j).set(4, (verSoft.equals(FileCheck4CG901A.verSoft)) ? "OK" : "ERROR"); // 55 7A 71 00 13 00 03 18 35 30 34 30 2D 31 38 30 2D 32 30 31 35 31 32 32 35 41 2D
                    } else if(subCmdInt == 0x033F) {
                        String verInner = Tool.bytesLittleEndian2String(frame, 8, 4);
                        listExcel.get(j).set(3, verInner);
                        listExcel.get(j).set(4, (verInner.equals(FileCheck4CG901A.verInner)) ? "OK" : "ERROR"); // 55 7A 71 00 06 00 03 11 31 2E 32 37 50
                    } else if(subCmdInt == 0x034D || subCmdInt == 0x034E || subCmdInt == 0x034F) {
                        float ft = 0.0f + (((frame[8]| 0x0000) << 8) | ((frame[9] | 0x0000) & 0x00FF));
                        ft /= 10;
                        listExcel.get(j).set(3, "DC " + ft + "V");
                        if(subCmdInt == 0x0331) {
                            if(ft >= 2.7 && ft <= 3.3) {
                                listExcel.get(j).set(4, "OK"); // 55 7A 71 00 04 00 03 31 00 1E 76
                            } else {
                                listExcel.get(j).set(4, "ERROR"); // 55 7A 71 00 04 00 03 31 00 22 4A
                            }
                        } else {
                            if(ft >= 16.0 && ft <= 32.0) {
                                listExcel.get(j).set(4, "OK"); //
                            } else {
                                listExcel.get(j).set(4, "ERROR"); //
                            }                                        
                        }
                    } else if(subCmdInt == 0x0201) {
                        String sim = Tool.bytesLittleEndian2String(frame, 8, 11);
                        listExcel.get(j).set(3, sim);
                        listExcel.get(j).set(4, (sim.length() == 11) ? "OK" : "ERROR"); // 55 7A 71 00 06 00 03 11 31 2E 32 37 50
                    } else if(subCmdInt == 0x0210) { // 528
                        String id = Tool.bytesLittleEndian2String(frame, 8, 10);
                        listExcel.get(j).set(3, id);
                        listExcel.get(j).set(4, (id.length() == 10) ? "OK" : "ERROR"); // 55 7A 71 00 06 00 03 11 31 2E 32 37 50
                    } else if(subCmdInt == 0x032E) {
                        String iv = Tool.bytesLittleEndian2String(frame, 8, 16);
                        listExcel.get(j).set(3, iv);
                        listExcel.get(j).set(4, (iv.length() == 16) ? "OK" : "ERROR"); // 55 7A 71 00 06 00 03 11 31 2E 32 37 50
                    } else if(subCmdInt == 0x032D) {
                        String key = Tool.bytesLittleEndian2String(frame, 8, 16);
                        listExcel.get(j).set(3, key);
                        listExcel.get(j).set(4, (key.length() == 16) ? "OK" : "ERROR"); // 55 7A 71 00 06 00 03 11 31 2E 32 37 50
                    } else if(subCmdInt == 0x032F) {
                        String imsi = Tool.bytesLittleEndian2String(frame, 8, 15);
                        listExcel.get(j).set(3, imsi);
                        listExcel.get(j).set(4, (imsi.length() == 15) ? "OK" : "ERROR"); // 55 7A 71 00 06 00 03 11 31 2E 32 37 50
                    } else if(subCmdInt == 0x0345) {
                        String imsiVar = Tool.bytesLittleEndian2String(frame, 8, 15);
                        listExcel.get(j).set(3, imsiVar);
                        listExcel.get(j).set(4, (imsiVar.length() == 15) ? "OK" : "ERROR"); // 55 7A 71 00 06 00 03 11 31 2E 32 37 50
                    } else if(subCmdInt == 0x034A) {
                        String hex = Integer.toHexString(frame[8]);
                        listExcel.get(j).set(3, (hex.length() == 1)? "0x0" + hex: "0x" + hex);
                        if(frame[8] == 0x00) { 
                            listExcel.get(j).set(4, "无SIM卡"); // 55 7A 71 00 03 00 03 30 01 6F
                        } else if(frame[8] == 0x01) { 
                            listExcel.get(j).set(4, "有SIM卡"); // 55 7A 71 00 03 00 03 30 00 6E 
                        } else if(frame[8] == 0x02) { 
                            listExcel.get(j).set(4, "建立连接"); // 55 7A 71 00 03 00 03 30 00 6E 
                        } else if(frame[8] == 0x03) { 
                            listExcel.get(j).set(4, "注册鉴权成功"); // 55 7A 71 00 03 00 03 30 00 6E 
                        }
                    } else { // 0346  0347  0348  0349 034B  034C
                        String hexTmp = Integer.toHexString(frame[8]);
                        listExcel.get(j).set(3, (hexTmp.length() == 1)? "0x0" + hexTmp: "0x" + hexTmp);
                        if(frame[8] == 0x01) { 
                            listExcel.get(j).set(4, "OK"); // 55 7A 71 00 03 00 03 30 01 6F
                        } else {
                            listExcel.get(j).set(4, "ERROR"); // 55 7A 71 00 03 00 03 30 00 6E 
                        }
                    }

//                    tabMakerController.getTabMakerShowController().update_check_CG901A(j, listExcel.get(j), true);
                    return ++count;
                }
            }
        }
        return count;        
    }
    
    private void initUIsAndParams(Stage stage, TabFAEController tabFAEController) {
        this.stage = stage;
        this.tabFAEController = tabFAEController;
        
        this.progressIndicator = new ProgressIndicator();  
        this.progressIndicator.setProgress(-1.0f);
        this.progressIndicator.setPrefSize(100, 100);           
        
        this.vBox = new VBox();
        this.vBox.setStyle("-fx-background:transparent;"); // VBox 要透明
        this.vBox.setPadding(new Insets(20, 20, 20, 20));
        this.vBox.getChildren().addAll(progressIndicator);        
        
        this.scene = new Scene(vBox);
        this.scene.setFill(null);   // Scene要透明
        
        this.progressDialog = new ProgressDialog(stage, scene);        
    }
    
    private int paraseModel(String model) {
        if(model.startsWith("JL")) {
            return 0xD4;
        } else if(model.startsWith("CG")) {
            return 0xD1;
        } else if(model.startsWith("CZ")) {
            return 0xD2;
        } else if(model.startsWith("ZB")) {
            return 0xD3;
        }
        return 0x00;
    }
    
}

