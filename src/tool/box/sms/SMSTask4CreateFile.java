package tool.box.sms;

import smartvip.mainStage.center.leftTopControl.fae.*;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import tool.box.common.Tool;

public class SMSTask4CreateFile {
    private Stage stage;
    private Scene scene; 
    private ProgressDialog progressDialog;
    private ProgressIndicator progressIndicator;
    private VBox vBox;
    private static String ResStr = "";
    private TabFAEController tabFAEController;       
    
    public SMSTask4CreateFile(Stage stage, TabFAEController tabFAEController) {
        this.initUIsAndParams(stage, tabFAEController);
    }

    /**
     * 多线程工作
     * 
     * @param objs      参数列表key - value 模式  0=No 1=CheckBox 2=Tip 3=Content 4=ModelType 5=SubCmd 6=SendLen 7=ReceiveLen 8=Offset  
     * @param model     产品型号
     * @param timeout   超时阈值
     * @return "OK"
     */
    public String run(Object[][] objs, String resCode, int timeout) { // 单位 【秒】
        
        Task<String> progressTask = new Task<String>() {
            
            @Override
            protected String call() throws Exception {
                StringBuilder sb = new StringBuilder();
                
                for(int i=0; i<objs.length; i++) {
                    if(i==0 || i==1 || i==2 || i==3) {
                        String tmp = (String)objs[i][3]; 
                        switch (i) {
                            case 0:// 平台类型
                                sb.append("$$1/");
                                if((boolean)objs[i][1] && (boolean)objs[i][4]) {
                                    sb.append((tmp.equals("0") || tmp.equals("1"))? tmp : "1").append("/");
                                } else {
                                    sb.append("/");
                                }   break;
                            case 1:// 32字节 IP/域名
                                if((boolean)objs[i][1] && (boolean)objs[i][4]) {
                                    sb.append((tmp.length() >= 32)? (tmp.substring(tmp.length() - 32)): tmp).append("/");
                                } else {
                                    sb.append("/");
                                }   break;
                            case 2:// 5字节 端口号
                                if((boolean)objs[i][1] && (boolean)objs[i][4]) {
                                    sb.append((tmp.length() >= 5)? (tmp.substring(tmp.length() - 5)): tmp).append("/");
                                } else {
                                    sb.append("/");
                                }   break;
                            case 3:// 11字节 SIM
                                if((boolean)objs[i][1] && (boolean)objs[i][4]) {
                                    sb.append((tmp.length() >= 11)? (tmp.substring(tmp.length() - 11)): tmp).append("/" + ((resCode == null)? "":resCode) + "/##\r\n");
                                } else {
                                    sb.append("/" + ((resCode == null)? "":resCode) + "/##\r\n");
                                }   break;
                            default:
                                break;
                        }
                        
                    }
                    
                    if(i==4 || i==5 || i==6 || i==7 || i==8) {
                        String tmp = (String)objs[i][3];
                        switch (i) {
                            case 4: // 号牌号码
                                sb.append("$$2/");
                                if((boolean)objs[i][1] && (boolean)objs[i][4]) {
                                    if(tmp.contains("京")) { // 北京
                                        tmp = tmp.replace("京", "11");
                                    } else if(tmp.contains("津")) { // 天津
                                        tmp = tmp.replace("津", "12");
                                    } else if(tmp.contains("冀")) { // 河北
                                        tmp = tmp.replace("冀", "13");
                                    } else if(tmp.contains("晋")) { // 山西
                                        tmp = tmp.replace("晋", "14");
                                    } else if(tmp.contains("蒙")) { // 内蒙古
                                        tmp = tmp.replace("蒙", "14");
                                    } else if(tmp.contains("辽")) { // 辽宁
                                        tmp = tmp.replace("辽", "21");
                                    } else if(tmp.contains("吉")) { // 吉林
                                        tmp = tmp.replace("吉", "22");
                                    } else if(tmp.contains("黑")) { // 黑龙江
                                        tmp = tmp.replace("黑", "23");
                                    } else if(tmp.contains("沪")) { // 上海
                                        tmp = tmp.replace("沪", "31");
                                    } else if(tmp.contains("苏")) { // 江苏
                                        tmp = tmp.replace("苏", "32");
                                    } else if(tmp.contains("浙")) { // 浙江
                                        tmp = tmp.replace("浙", "33");
                                    } else if(tmp.contains("皖")) { // 安徽
                                        tmp = tmp.replace("皖", "34");
                                    } else if(tmp.contains("闽")) { // 福建
                                        tmp = tmp.replace("闽", "35");
                                    } else if(tmp.contains("赣")) { // 江西
                                        tmp = tmp.replace("赣", "36");
                                    } else if(tmp.contains("鲁")) { // 山东
                                        tmp = tmp.replace("鲁", "37");
                                    } else if(tmp.contains("豫")) { // 河南
                                        tmp = tmp.replace("豫", "41");
                                    } else if(tmp.contains("鄂")) { // 湖北
                                        tmp = tmp.replace("鄂", "42");
                                    } else if(tmp.contains("湘")) { // 湖南
                                        tmp = tmp.replace("湘", "43");
                                    } else if(tmp.contains("粤")) { // 广东
                                        tmp = tmp.replace("粤", "44");
                                    } else if(tmp.contains("桂")) { // 广西
                                        tmp = tmp.replace("桂", "45");
                                    } else if(tmp.contains("琼")) { // 海南
                                        tmp = tmp.replace("琼", "46");
                                    } else if(tmp.contains("渝")) { // 重庆
                                        tmp = tmp.replace("渝", "50");
                                    } else if(tmp.contains("川")) { // 四川
                                        tmp = tmp.replace("川", "51");
                                    } else if(tmp.contains("黔")) { // 贵州
                                        tmp = tmp.replace("黔", "52");
                                    } else if(tmp.contains("云")) { // 云南
                                        tmp = tmp.replace("云", "53");
                                    } else if(tmp.contains("藏")) { // 西藏
                                        tmp = tmp.replace("藏", "54");
                                    } else if(tmp.contains("陕")) { // 陕西
                                        tmp = tmp.replace("陕", "61");
                                    } else if(tmp.contains("甘")) { // 甘肃
                                        tmp = tmp.replace("甘", "62");
                                    } else if(tmp.contains("青")) { // 青海
                                        tmp = tmp.replace("青", "63");
                                    } else if(tmp.contains("宁")) { // 宁夏
                                        tmp = tmp.replace("宁", "64");
                                    } else if(tmp.contains("新")) { // 新疆
                                        tmp = tmp.replace("新", "65");
                                    }
                                    sb.append((tmp.length() >= 12)? (tmp.substring(tmp.length() - 12)): tmp).append("/");
                                } else {
                                    sb.append("/");
                                }   break;
                            case 5:// 号牌颜色
                                if((boolean)objs[i][1] && (boolean)objs[i][4]) {
                                    sb.append((tmp.length() >= 2)? (tmp.substring(tmp.length() - 2)): tmp).append("/");
                                } else {
                                    sb.append("/");
                                }   break;
                            case 6:// 号牌分类
                                if((boolean)objs[i][1] && (boolean)objs[i][4]) {
                                    sb.append((tmp.length() >= 12)? (tmp.substring(tmp.length() - 12)): tmp).append("/");
                                } else {
                                    sb.append("/");
                                }   break;
                            case 7:// 行政区划
                                if((boolean)objs[i][1] && (boolean)objs[i][4]) {
                                    sb.append((tmp.length() >= 6)? (tmp.substring(tmp.length() - 6)): tmp).append("/");
                                } else {
                                    sb.append("/");
                                }   break;
                            case 8:// VIN
                                if((boolean)objs[i][1] && (boolean)objs[i][4]) {
                                    sb.append((tmp.length() >= 17)? (tmp.substring(tmp.length() - 12)): tmp).append("/##\r\n");
                                } else {
                                    sb.append("/##\r\n");
                                }   break;
                            default:
                                break;                            
                        }
                        
                    }

                }
                tabFAEController.addLog("短信内容为：" + sb.substring(0, sb.length()-1), null);
                
                return sb.substring(0, sb.length()-1);
            }
            @Override
            protected void succeeded() {
                super.succeeded();
                progressDialog.close();
                
                String resStr = Tool.saveFile(stage, "记事本 (*.txt)", "*.txt", "SmartVIP_SMS_" + Tool.getTime("yyyyMMddHHmmss"), getValue());
                if(resStr.startsWith("false")) {
                    tabFAEController.addLog("短信内容未保存......", null);
                } else if(resStr.startsWith("error")) {
                    tabFAEController.addLog("短信文件出现系统异常！", null);
                } else {
                    tabFAEController.addLog("短信内容路径为：" + resStr, null);
                } 
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

    
}

