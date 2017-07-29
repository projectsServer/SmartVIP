package smartvip.mainStage.center.leftTopControl.maker.thread;

import java.io.File;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import smartvip.FXMLLoginController;
import smartvip.mainStage.HostController;
import smartvip.mainStage.center.leftBottomLog.SysLogController;
import smartvip.mainStage.center.leftTopControl.maker.TabMakerController;
import tool.box.popwindow.PopWindow;
import tool.box.common.Tool;
import smartvip.mainStage.center.leftTopControl.maker.excel.FileCheck4CG901A;
import smartvip.mainStage.center.leftTopControl.maker.excel.FileCheck4JL207A;
import smartvip.mainStage.center.leftTopControl.maker.excel.FileSetParams4CG901A;
import smartvip.mainStage.center.leftTopControl.maker.excel.FileSetParams4JL207A;
import tool.box.rs232.diy.Utils;

public class ThreadRS232 {
    private Stage stage;
    private Scene scene; 
    private ProgressIndicator progressIndicator;
    private ProgressDialog progressDialog;
    private VBox vBox;
    private Label close;
    private Task<String> progressTask;
    
    private static String ResStr = "";
    public static boolean ExitFlag = false;
    private static float Speed = 0.0f;
    private static String Code = "";   
    private static String ID_10 = "";
    private static String DeviceType = "";
    
    private SysLogController sysLogController;
    private PopWindow popWindow;
    private TabMakerController tabMakerController;
    
    public ThreadRS232(Stage stage, TabMakerController tabMakerController) {
        this.stage = stage;
        this.sysLogController = tabMakerController.getSysLogController();
        this.popWindow = tabMakerController.getPopWindow();
        this.tabMakerController = tabMakerController;
        
        this.progressIndicator = new ProgressIndicator();  
        this.progressIndicator.setProgress(-1.0f);
        this.progressIndicator.setPrefSize(120, 120);  
        this.progressIndicator.setPadding(new Insets(5, 5, 5, 5));
        
        this.vBox = new VBox();
        this.vBox.setStyle("-fx-background:transparent;"); // VBox 要透明
        
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.BASELINE_LEFT);
        StackPane stackPane = new StackPane();
        stackPane.setAlignment(Pos.BASELINE_RIGHT);
        this.close = new Label();
        this.close.setStyle("-fx-label-padding: 5 0 5 0; -fx-graphic:url(/tool/box/raw/images/_16_close_4.png); -fx-cursor:hand; "); 
        stackPane.getChildren().add(this.close);
        hBox.getChildren().add(stackPane);
        HBox.setHgrow(stackPane, Priority.ALWAYS);
        this.vBox.getChildren().addAll(hBox, progressIndicator);   
        this.close.setOnMouseClicked((Event event) -> {
            this.progressTask.cancel(); 
        });        
        
        this.scene = new Scene(vBox);
        this.scene.setFill(null);   // Scene要透明
        
        this.progressDialog = new ProgressDialog(stage, scene);
    }
    
    /**
     * 多线程工作
     * 
     * @param objs      参数列表key - value 模式
     * @return 
     */
    public String run(Object[][] objs) { // 单位 【秒】
        
        progressTask = new Task<String>() {
            @Override
            protected String call() throws Exception {
                String[][] params = (String[][])objs;
                
                ExitFlag = false;
                
                // CG901A
                if(Integer.parseInt(params[0][1], 16) == 0xD1 && Integer.parseInt(params[1][1], 16) == 0x0001) { // maker setting params
                    String ret = oneKeySetParams4CG901A();
                    if(ret != null && ret.contains("+")) {
                        updateMessage(ret);
                    }
                    return ret;
                } else if(Integer.parseInt(params[0][1], 16) == 0x71) {
                    return oneKeyCheck4CG901A(Integer.parseInt(params[0][1], 16), Integer.parseInt(params[1][1], 16)); 
                }
                
                // JL207A
                if(Integer.parseInt(params[0][1], 16) == 0xD4 && Integer.parseInt(params[1][1], 16) == 0x0001) { // maker setting params
                    String ret = oneKeySetParams4JL207A();
                    if(ret != null && ret.contains("-")) {
                        updateMessage(ret);
                    }
                    return ret;
                } else if(Integer.parseInt(params[0][1], 16) == 0x74 && Integer.parseInt(params[1][1], 16) == 0x0002) { 
                    return oneKeyCheck4JL207A(Integer.parseInt(params[0][1], 16), Integer.parseInt(params[1][1], 16));
                } else if(Integer.parseInt(params[0][1], 16) == 0x74 && Integer.parseInt(params[1][1], 16) == 0x0003) { 
                    if(!params[2][1].equals("")) {
                        Code = params[2][1];
                        return oneKeyCheck4JL207A(Integer.parseInt(params[0][1], 16), Integer.parseInt(params[1][1], 16));
                    } 
                }                

                return "NULL";
            }
            @Override
            protected void succeeded() {
                super.succeeded();
                ResStr = getValue();
                progressDialog.close();
                releaseResource();
            }
            @Override
            protected void cancelled() {
                super.cancelled(); //To change body of generated methods, choose Tools | Templates.
                progressDialog.close();
                releaseResource(); 
                ExitFlag = true;
                ResStr = "NULL";
            }
        };
        
        progressTask.messageProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(newValue.contains("+")) { // CG901A
                    String[] counter = newValue.split("\\+");
                    tabMakerController.getUsed().setText(counter[0]);
                    tabMakerController.getNoUse().setText(counter[1]); 
                    tabMakerController.getTabMakerShowController().getTableView().scrollTo(0);
                } else if(newValue.contains("-")) { // JL207A
                    String[] counter = newValue.split("\\-");
                    tabMakerController.getUsed().setText(counter[0]);
                    tabMakerController.getNoUse().setText(counter[1]); 
                    tabMakerController.getTabMakerShowController().getTableView().scrollTo(0);                    
                }
                
            }
        });

        if(progressDialog != null && progressTask != null && tabMakerController != null) {
            this.tabMakerController.clearLog(true, false); // 清空日志区
            new Thread(this.progressTask).start();
            this.progressDialog.onShow();
        }

        return ResStr;        
    }
    
    private void releaseResource() {
        this.stage = null;
        this.scene = null; 
        this.progressDialog = null;
        this.progressIndicator = null;
        this.vBox = null;  
        this.progressTask = null;
    }    

    class ProgressDialog extends Stage {

        public ProgressDialog(Stage owner, Scene scene) {
            this.initStyle(StageStyle.TRANSPARENT); // Stage 没有窗口装饰
            this.initModality(Modality.APPLICATION_MODAL);
            this.initOwner(owner);
            this.setScene(scene);
        }

        public void onShow() {
            try {
                sizeToScene();
                centerOnScreen();
                showAndWait();                  
            } catch(Exception ex) {
                System.out.println("ProgressDialog onShow error!");
            }
              
//            Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
//            stage.setX(primaryScreenBounds.getWidth() - width);
//            stage.setY(primaryScreenBounds.getHeight() - height);            
        }
    }
    
    // 测试数据： 55 7A 71 00 02 00 03 22 7D 55 7A D1 00 11 00 03 34 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 E8
    private String oneKeySetParams4CG901A() {
        if (!new File(FileSetParams4CG901A.AbsPath).renameTo(new File(FileSetParams4CG901A.AbsPath))) {
            tabMakerController.addLog("参数配置文件已经被打开，请先关闭...", null);
            return "FILE_OPENED";
        } else {
            List<List<Object>> listExcel = tabMakerController.getListExcel();
            String[] validExcelRow = null;
            String tmpStr = "";
            int timeout = 5;
            
            Object[] res = Utils.sendAndReceive(tabMakerController.getHostController(), (byte)0x71, 0x032F, null, "采集固定IMSI", timeout, 2, 2+15, true);    
            if(res[0].toString().contains("_noNull")) {
                validExcelRow = tabMakerController.getValidExcelRow(res[1].toString());// 判断返回的IMIS是否在excel表格中，同时判断内容长度是否正确
                if(validExcelRow == null) {
                    tabMakerController.addLog("参数配置文件已无可用参数或者没找到匹配的IMSI号，请确认...", null);
                    return "FILE_NoUsed";                         
                } else {
                   tabMakerController.addLog("找到匹配IMSI且未被使用，继续设置其他参数", null);
                }                
            } else {
                tabMakerController.addLog("采集固定IMSI终端返回无效IMSI号，请确认...", null);
                return "FILE_NoIMSI"; 
            }
            
            res = Utils.sendAndReceive(tabMakerController.getHostController(), (byte) 0xD1, 0x0210, validExcelRow[1].getBytes(), "设置10位ID", timeout, 2 + 10, 2 + 10, true);
            tmpStr = res[1].toString();           
            if(res[0].toString().contains("_noNull")) {
                if(tmpStr.contains(res[1].toString())) {
                    tabMakerController.addLog("设置10位ID成功：" + tmpStr, null);
                } else {
                    tabMakerController.addLog("设置10位ID返回值不相等，请确认...", null);
                    return null;
                }
            } else {
                tabMakerController.addLog("设置10位ID终端返回无效值，请确认...", null);
                return "FILE_NoID"; 
            }
            
            String sim = (validExcelRow[2].length() > 11) ? validExcelRow[2].substring(validExcelRow[2].length()-11) : validExcelRow[2];
            res = Utils.sendAndReceive(tabMakerController.getHostController(), (byte) 0xD1, 0x0201, sim.getBytes(), "设置11位SIM", timeout, 2 + 11, 2 + 20, true); // 终端 返回20字节SIM
            tmpStr = res[1].toString();
            if (res[0].toString().contains("_noNull")) {
                if (tmpStr.contains(res[1].toString())) {
                    tabMakerController.addLog("设置SIM成功：" + tmpStr, null);     
                } else {
                    tabMakerController.addLog("设置11位SIM返回值不相等，请确认...", null);
                    return null;
                }
            } else {
                tabMakerController.addLog("设置11位SIM终端返回无效值，请确认...", null);
                return "FILE_NoSIM";                  
            } 
            
            String vin = (validExcelRow[3].length() > 17) ? validExcelRow[3].substring(validExcelRow[3].length()-17) : validExcelRow[3];
            res = Utils.sendAndReceive(tabMakerController.getHostController(), (byte)0xD1, 0x0301, vin.getBytes(), "设置17位VIN", timeout, 2+17, 2+17, true); // 终端 返回17字节VIN
            tmpStr = res[1].toString();
            if(res[0].toString().contains("_noNull")) {
                if(tmpStr.contains(res[1].toString())) {
                    tabMakerController.addLog("设置17位VIN成功：" + tmpStr, null);
                } else {
                    tabMakerController.addLog("设置17位VIN返回值不相等，请确认...", null);
                    return null;
                }
            } else {
                tabMakerController.addLog("设置17位VIN终端返回无效值，请确认...", null);
                return "FILE_NoVIN";                  
            } 
                                
            res = Utils.sendAndReceive(tabMakerController.getHostController(), (byte)0xD1, 0x02F6, validExcelRow[6].getBytes(), "设置4位客户代码", timeout, 2+4, 2+4, true);
            tmpStr = res[1].toString();
            if(res[0].toString().contains("_noNull")) {
                if(tmpStr.contains(res[1].toString())) {
                    tabMakerController.addLog("设置4位客户代码成功：" + tmpStr, null);
                } else {
                    tabMakerController.addLog("设置4位客户代码返回值不相等，请确认...", null);
                    return null;
                }            
            } else {
                tabMakerController.addLog("设置4位客户代码终端返回无效值，请确认...", null);
                return "FILE_NoVIN";                  
            }
                                                
            res = Utils.sendAndReceive(tabMakerController.getHostController(), (byte) 0xD1, 0x032E, validExcelRow[5].getBytes(), "设置16位IV", timeout, 2 + 16, 2 + 16, true);
            tmpStr = res[1].toString();
            if (res[0].toString().contains("_noNull")) {
                if (tmpStr.contains(res[1].toString())) {
                    tabMakerController.addLog("设置16位IV成功：" + tmpStr, null);
                } else {
                    tabMakerController.addLog("设置16位IV返回值不相等，请确认...", null);
                    return null;
                }
            } else {
                tabMakerController.addLog("设置16位IV终端返回无效值，请确认...", null);
                return "FILE_NoVIN";                  
            }

            res = Utils.sendAndReceive(tabMakerController.getHostController(), (byte)0xD1, 0x032D, validExcelRow[4].getBytes(), "设置16位KEY", timeout, 2+16, 2+16, true); 
            tmpStr = res[1].toString();
            if(res[0].toString().contains("_noNull")) { 
                if(tmpStr.contains(res[1].toString())) { 
                    tabMakerController.addLog("设置16位KEY成功：" + tmpStr, null);
                }else {
                    tabMakerController.addLog("设置16位KEY返回值不相等，请确认...", null);
                    return null;
                }                    
            } else {
                tabMakerController.addLog("设置16位KEY终端返回无效值，请确认...", null);
                return "FILE_NoVIN";                  
            }                    
                    
            // update_setParams_CG901A Excel file and listExcel
            String[] mStrs = new String[] {Tool.getTime("yyyy-MM-dd HH:mm:ss:SSS"), FXMLLoginController.login.getName(), "正常"};
            FileSetParams4CG901A.changeRowCell(new File(FileSetParams4CG901A.AbsPath), 0, Integer.parseInt(validExcelRow[0])+3-1, FileSetParams4CG901A.DateIndex, mStrs);

            List<Object> tmpList = listExcel.get(Integer.parseInt(validExcelRow[0])-1);
            if(tmpList.get(FileSetParams4CG901A.DateIndex).equals("-")) {
                for(int k=0, j=FileSetParams4CG901A.DateIndex; k<mStrs.length; k++, j++) {
                    tmpList.add(j, mStrs[k]);
                }
                tabMakerController.getTabMakerShowController().update_setParams_CG901A(Integer.parseInt(validExcelRow[0])-1, tmpList, true);
                tabMakerController.addLog("生产参数设置指令执行一次，并更新Excel和界面", null);
            }                                                                

            int noUsedID = 0;
            for(int i=0; i<listExcel.size(); i++) {
                tmpList = listExcel.get(i);
                if(tmpList.get(FileSetParams4CG901A.DateIndex).equals("-")) {
                    noUsedID++;
                }
            } 

            return String.valueOf(listExcel.size() - noUsedID) + "+" + String.valueOf(noUsedID);
        }
    }
    
    // 多线程里运行本函数
    private String oneKeyCheck4CG901A(int _cmd, int exCmd) {
        if (!new File(FileCheck4CG901A.AbsPath).renameTo(new File(FileCheck4CG901A.AbsPath))) {
            if(exCmd == 0x0002) {
                this.sysLogController.addInfo("印制板检测文件已经被打开，请先关闭......");
            } else if(exCmd == 0x0003) {
                this.sysLogController.addInfo("总检检测文件已经被打开，请先关闭......");
            }
            return "FILE_OPENED";
        }       
        
        // clear history record
        List<List<Object>> listExcelDefault = tabMakerController.getListExcelDefault();
        for(int i=0; i<listExcelDefault.size(); i++) { // clear old status
            tabMakerController.getTabMakerShowController().update_check_CG901A(i, listExcelDefault.get(i), false);
        }
        if(!HostController.serial.getDataProtocol().getDataList().isEmpty()) {
            HostController.serial.getDataProtocol().getDataList().clear();
        }   
        
        // read all status， timeout = 10s
        List<List<Object>> listExcel = tabMakerController.getListExcel();        
        byte[] sentBytes ;
        int cmd,subCmd, sendLen, receiveLen, timeout;   
        String tip, tmpStr;
        List<Object> listExcelRow;
        String resStatus = "Check_OK";
        
        // 开始一键检测前，进行延时处理
        delayStart((exCmd == 0x0002) ? 15 : 60); // 延时启动检测
        // 检测开始，进入循环发送指令
        if((readyStart((exCmd == 0x0002) ? true : false)) && (!ExitFlag)) {     // 检测前准备
            for(int i=0; i<listExcel.size(); i++) {
                if(ExitFlag) {
                    break;
                } else {
                    listExcelRow = listExcel.get(i);
                    cmd = 0x71;
                    tip = listExcelRow.get(FileCheck4CG901A.DateIndex-1).toString();
                    subCmd = Integer.parseInt(listExcelRow.get(FileCheck4CG901A.DateIndex).toString(), 16); 
                    sendLen = Integer.parseInt(listExcelRow.get(FileCheck4CG901A.DateIndex+4).toString(), 10);
                    receiveLen = Integer.parseInt(listExcelRow.get(FileCheck4CG901A.DateIndex+5).toString(), 10);
                    timeout = Integer.parseInt(listExcelRow.get(FileCheck4CG901A.DateIndex+6).toString(), 10);

                    Object[] res = Utils.sendAndReceive(tabMakerController.getHostController(), (byte)(cmd&0xFF), subCmd, null, "采集" + tip, timeout, sendLen, receiveLen, true);    
                    if(res[0].toString().contains("_noNull")) {
                        String tmp = dealRXD(cmd, subCmd, exCmd, i, listExcelRow, res[2], 8, receiveLen-2) ; // 实际处理串口数据的地方
                        if(tmp.contains("Check_ERROR")) {
                            resStatus = tmp;
                        }     
                    } else {
                        tabMakerController.addLog("终端返回无效" + tip + "，请确认...", null);
                        resStatus = "FILE_NoValid";
                        continue;                    
                    }                     
                }
            }
            return resStatus;
        }

        return null;
    }    
    

    private String oneKeySetParams4JL207A() {
        if (!new File(FileSetParams4JL207A.AbsPath).renameTo(new File(FileSetParams4JL207A.AbsPath))) {
            tabMakerController.addLog("参数配置文件已经被打开，请先关闭...", null);
            return "FILE_OPENED";
        } else {
            List<List<Object>> listExcel = tabMakerController.getListExcel();
            String[] validExcelRow = null;
            String tmpStr = "";
            int timeout = 5;
            
            validExcelRow = tabMakerController.getValidExcelRow();// 判断返回的IMIS是否在excel表格中
            if(validExcelRow == null) {
                tabMakerController.addLog("参数配置文件已无可用参数，请确认...", null);
                return "FILE_NoUsed";                         
            } 
            
            Object[] res = Utils.sendAndReceive(tabMakerController.getHostController(), (byte) 0xD4, 0x0210, validExcelRow[1].getBytes(), "设置10位ID", timeout, 2 + 10, 2 + 10, true);
            tmpStr = res[1].toString();
            if (res[0].toString().contains("_noNull")) {
                if (tmpStr.contains(res[1].toString())) {
                    tabMakerController.addLog("设置10位ID成功：" + tmpStr, null);
                } else {
                    tabMakerController.addLog("设置10位ID返回值不相等，请确认...", null);
                    return null;
                }  
            } else {
                tabMakerController.addLog("设置10位ID终端返回无效值，请确认...", null);
                return "FILE_NoID"; 
            }  

            res = Utils.sendAndReceive(tabMakerController.getHostController(), (byte) 0xD4, 0x0201, validExcelRow[2].getBytes(), "设置11位SIM", timeout, 2 + 20, 2 + 20, true);
            tmpStr = res[1].toString();
            if (res[0].toString().contains("_noNull")) {
                if (tmpStr.contains(res[1].toString())) {
                    tabMakerController.addLog("设置11位SIM成功：" + tmpStr, null);
                } else {
                    tabMakerController.addLog("设置11位SIM返回值不相等，请确认...", null);
                    return null;
                }
            } else {
                tabMakerController.addLog("设置11位SIM终端返回无效值，请确认...", null);
                return "FILE_NoSIM"; 
            } 

            res = Utils.sendAndReceive(tabMakerController.getHostController(), (byte) 0xD4, 0x0307, Tool.string2byteBitEndian(validExcelRow[3], 2), "设置脉冲系数", timeout, 2 + 2, 2 + 2, true); 
            tmpStr = res[1].toString();
            if (res[0].toString().contains("_noNull")) {
                if (tmpStr.contains(res[1].toString())) {
                    tabMakerController.addLog("设置脉冲系数成功：" + String.valueOf(Tool.bytesBigEndian2int(Tool.string2byteBitEndian(validExcelRow[3], 2), 0, 2)), null);
                } else {
                    tabMakerController.addLog("设置脉冲系数返回值不相等，请确认...", null);
                    return null;
                }
            } else {
                tabMakerController.addLog("设置脉冲系数终端返回无效值，请确认...", null);
                return "FILE_NoPulse"; 
            }                   

            res = Utils.sendAndReceive(tabMakerController.getHostController(), (byte) 0xD4, 0x0013, Tool.stringCharacter2byteLittleEndian(validExcelRow[4], 32), "设置IP/域名", timeout, 2 + 32, 2 + 32, true); // 终端 返回17字节VIN
            tmpStr = res[1].toString();
            if (res[0].toString().contains("_noNull")) {
                if (tmpStr.contains(res[1].toString())) {
                    tabMakerController.addLog("设置IP/域名成功：" + tmpStr, null);
                } else {
                    tabMakerController.addLog("设置IP/域名返回值不相等，请确认...", null);
                    return null;
                }
            } else {
                tabMakerController.addLog("设置IP/域名终端返回无效值，请确认...", null);
                return "FILE_NoIP"; 
            }                      

            res = Utils.sendAndReceive(tabMakerController.getHostController(), (byte) 0xD4, 0x0018, Tool.string2byteBitEndian(validExcelRow[5], 4), "设置端口号", timeout, 2 + 4, 2 + 4, true);
            tmpStr = res[1].toString();
            if (res[0].toString().contains("_noNull")) {
                if (tmpStr.contains(res[1].toString())) {
                    tabMakerController.addLog("设置端口号成功：" + tmpStr, null);
                } else {
                    tabMakerController.addLog("设置端口号返回值不相等，请确认...", null);
                    return null;
                }
            } else {
                tabMakerController.addLog("设置端口终端返回无效值，请确认...", null);
                return "FILE_NoPort"; 
            }                     

            res = Utils.sendAndReceive(tabMakerController.getHostController(), (byte) 0xD4, 0x02F6, validExcelRow[6].getBytes(), "设置客户代码", timeout, 2 + 4, 2 + 4, true);
            tmpStr = res[1].toString();
            if (res[0].toString().contains("_noNull")) {
                if (tmpStr.contains(res[1].toString())) {
                    tabMakerController.addLog("设置客户代码成功：" + Tool.int2String(Integer.parseInt(tmpStr), 4), null);
                } else {
                    tabMakerController.addLog("设置客户代码返回值不相等，请确认...", null);
                    return null;
                }
            } else {
                tabMakerController.addLog("设置客户代码终端返回无效值，请确认...", null);
                return "FILE_CusCode"; 
            }                     

            res = Utils.sendAndReceive(tabMakerController.getHostController(), (byte) 0xD4, 0x0083, Tool.stringCharacter2byteLittleEndian(validExcelRow[7], 12), "设置号牌号码", timeout, 2 +  12, 2 +  12, true);
            tmpStr = res[1].toString();
            if (res[0].toString().contains("_noNull")) {
                if (tmpStr.contains(res[1].toString())) {
                    tabMakerController.addLog("设置号牌号码成功：" + tmpStr, null);
                } else {
                    tabMakerController.addLog("设置号牌号码返回值不相等，请确认...", null);
                    return null;
                }
            } else {
                tabMakerController.addLog("设置号牌号码终端返回无效值，请确认...", null);
                return "FILE_CarID"; 
            }          
            
            // update_setParams_JL207A Excel file and listExcel
            String[] mStrs = new String[]{Tool.getTime("yyyy-MM-dd HH:mm:ss:SSS"), FXMLLoginController.login.getName(), "正常"};
            FileSetParams4JL207A.changeRowCell(new File(FileSetParams4JL207A.AbsPath), 0, Integer.parseInt(validExcelRow[0]) + 3 - 1, FileSetParams4JL207A.DateIndex, mStrs);

            List<Object> tmpList = listExcel.get(Integer.parseInt(validExcelRow[0]) - 1);
            if (tmpList.get(FileSetParams4JL207A.DateIndex).equals("-")) {
                for (int k = 0, j = FileSetParams4JL207A.DateIndex; k < mStrs.length; k++, j++) {
                    tmpList.add(j, mStrs[k]);
                }
                tabMakerController.getTabMakerShowController().update_setParams_JL207A(Integer.parseInt(validExcelRow[0]) - 1, tmpList, true);
                tabMakerController.addLog("生产参数设置指令执行一次，并更新Excel和界面", null);
            }

            int noUsedID = 0;
            for (int i = 0; i < listExcel.size(); i++) {
                tmpList = listExcel.get(i);
                if (tmpList.get(FileSetParams4JL207A.DateIndex).equals("-")) {
                    noUsedID++;
                }
            }

            return String.valueOf(listExcel.size() - noUsedID) + "-" + String.valueOf(noUsedID);
        }            
        
//        return null;
    }    
    
    
    /**
        

        
        
            for(int i=0; i<listExcel.size(); i++) {

            }
            return resStatus;
        }
        return null;
    }    
     */
    // 多线程里运行本函数
    private String oneKeyCheck4JL207A(int _cmd, int exCmd) {
        if (!new File(FileCheck4JL207A.AbsPath).renameTo(new File(FileCheck4JL207A.AbsPath))) {
            if(exCmd == 0x0002) {
                this.sysLogController.addInfo("印制板检测文件已经被打开，请先关闭......");
            } else if(exCmd == 0x0003) {
                this.sysLogController.addInfo("总检检测文件已经被打开，请先关闭......");
            }
            return "FILE_OPENED";
        } 
        
        // clear history record
        List<List<Object>> listExcelDefault = tabMakerController.getListExcelDefault();
        for(int i=0; i<listExcelDefault.size(); i++) { // clear old status
            tabMakerController.getTabMakerShowController().update_check_JL207A(i, listExcelDefault.get(i), false);
        }
        if(!HostController.serial.getDataProtocol().getDataList().isEmpty()) {
            HostController.serial.getDataProtocol().getDataList().clear();
        }        
        
        // read all status， timeout = 10s
        List<List<Object>> listExcel = tabMakerController.getListExcel();        
        byte[] sentBytes ;
        int cmd,subCmd, sendLen, receiveLen, timeout;   
        String tip, tmpStr;
        List<Object> listExcelRow;
        String resStatus = "Check_OK";
        
        // 开始一键检测前，进行延时处理
        delayStart((exCmd == 0x0002) ? 3 : 3); // 延时启动检测
        // 检测开始，进入循环发送指令  
        if((readyStart((exCmd == 0x0002) ? false : false)) && (!ExitFlag)) {     // 检测前准备
            for(int i=0; i<listExcel.size(); i++) {
                if(ExitFlag) {
                    break;
                } else {
                    listExcelRow = listExcel.get(i);
                    cmd = 0x74;
                    tip = listExcelRow.get(FileCheck4JL207A.DateIndex-1).toString();
                    subCmd = Integer.parseInt(listExcelRow.get(FileCheck4JL207A.DateIndex).toString(), 16); 
                    sendLen = Integer.parseInt(listExcelRow.get(FileCheck4JL207A.DateIndex+4).toString(), 10);
                    receiveLen = Integer.parseInt(listExcelRow.get(FileCheck4JL207A.DateIndex+5).toString(), 10);
                    timeout = Integer.parseInt(listExcelRow.get(FileCheck4JL207A.DateIndex+6).toString(), 10);

                    Object[] res = Utils.sendAndReceive(tabMakerController.getHostController(), (byte)(cmd&0xFF), subCmd, null, "采集" + tip, timeout, sendLen, receiveLen, true);    
                    if(res[0].toString().contains("_noNull")) {
                        String tmp = dealRXD(cmd, subCmd, exCmd, i, listExcelRow, res[2], 8, receiveLen-2) ;
                        if(tmp.contains("Check_ERROR")) {
                            resStatus = tmp;
                        }                
                    } else {
                        tabMakerController.addLog("终端返回无效" + tip + "，请确认...", null);
                        resStatus = "FILE_NoValid";
                        continue;                  
                    }                    
                }                
            }
            return resStatus;
        }
        return null;
    }      
 
    private String dealRXD(int cmd, int subCmd, int exCmd, int lineNum, List<Object> listExcelRow, Object _frame, int offset, int receiveLen) { 
        String resStatus = "Check_OK";
        byte[] frame = (byte[])_frame;
        int offset4content = 0, offset4result = 0; // listView 显示的位置偏移量
        
        if((cmd&0x71) == 0x71) { // CG901
            offset4content = FileCheck4CG901A.DateIndex + 1;
            offset4result = FileCheck4CG901A.DateIndex + 2;
        } else if((cmd&0x74) == 0x74) { // JL207A
            offset4content = FileCheck4JL207A.DateIndex + 1;
            offset4result = FileCheck4JL207A.DateIndex + 2;
        }      
       
        if((frame[2] & cmd) == cmd) {
            int subCmdInt = ((frame[6]) << 8) | ((frame[7]) & 0x00FF);
            switch (subCmdInt) {
                case 0x0343:
                    String verHard = Tool.bytesLittleEndian2String(frame, offset, receiveLen);
                    listExcelRow.set(offset4content, verHard);
                    if((((cmd&0x71) == 0x71) && (verHard.contains(FileCheck4CG901A.verHard))) || (((cmd&0x74) == 0x74) && (verHard.contains(FileCheck4JL207A.verHard)))) {
                        listExcelRow.set(offset4result, "OK");
                    } else {
                        listExcelRow.set(offset4result, "ERROR");
                        resStatus = "Check_ERROR";
                    }                    
                    break;
                case 0x0344:
                    String verSoft = Tool.bytesLittleEndian2String(frame, offset, receiveLen);
                    listExcelRow.set(offset4content, verSoft);
                    if((((cmd&0x71) == 0x71) && (verSoft.contains(FileCheck4CG901A.verSoft))) || (((cmd&0x74) == 0x74) && (verSoft.contains(FileCheck4JL207A.verSoft)))) {
                        listExcelRow.set(offset4result, "OK");
                    } else {
                        listExcelRow.set(offset4result, "ERROR");
                        resStatus = "Check_ERROR";
                    }                      
                    break;
                case 0x033F:
                    String verInner = Tool.bytesLittleEndian2String(frame, offset, receiveLen);
                    listExcelRow.set(offset4content, verInner);
//                    if((((cmd&0x71) == 0x71) && (verInner.contains(FileCheck4CG901A.verInner))) || (((cmd&0x74) == 0x74) && (verInner.contains(FileCheck4JL207A.verInner)))) {
                    if(verInner.contains(FileCheck4CG901A.verInner) || verInner.contains(FileCheck4JL207A.verInner)) {
                        listExcelRow.set(offset4result, "OK");
                    } else {
                        listExcelRow.set(offset4result, "ERROR");
                        resStatus = "Check_ERROR";
                    }                     
                    break;
                case 0x0364:
                case 0x0365:
                case 0x034D:
                case 0x034E:
                case 0x034F: 
                    int tmpInt = ((0x000000FF&frame[offset]) << 8) | ((frame[offset+1]) & 0x000000FF) ;
//                    float ft = 0.0f + (((frame[offset]) << 8) | ((frame[offset+1]) & 0x00FF));
                    float ft = 0.0f + tmpInt;
                    ft /= 1000;
                    listExcelRow.set(offset4content, "DC " + ft + "V");
                    switch (subCmdInt) {
                        case 0x034D:
                            if(ft >= 2.7 && ft <= 3.3) {
                                listExcelRow.set(offset4result, "OK"); // 55 7A 71 00 04 00 03 31 00 1E 76
                            } else {
                                listExcelRow.set(offset4result, "ERROR"); // 55 7A 71 00 04 00 03 31 00 22 4A
                                resStatus = "Check_ERROR";
                            }
                            break;
                        case 0x0364:
                        case 0x0365: {
                            if(ft >= 2.7 && ft <= 60.0) {
                                listExcelRow.set(offset4result, "OK"); //
                            } else {
                                listExcelRow.set(offset4result, "ERROR"); //
                                resStatus = "Check_ERROR";
                            }
                            break; }
                        case 0x034E:
                        case 0x034F: {
//                            if(ft >= 2.7 && ft <= 48.0) {
                                listExcelRow.set(offset4content, "蓄电池电压");
                                listExcelRow.set(offset4result, "OK"); //
//                            } else {
//                                listExcelRow.set(offset4result, "ERROR"); //
//                                resStatus = "Check_ERROR";
//                            }
                            break; }                        
                        default:
                            if(ft >= 0.0 && ft <= 5.0) {
                                listExcelRow.set(offset4result, "OK"); //
                            } else {
                                listExcelRow.set(offset4result, "ERROR"); //
                                resStatus = "Check_ERROR";
                            }
                            break;
                    }
                    break;
                case 0x0201: // SIM卡号
                    String sim = Tool.bytesLittleEndian2String(frame, offset, receiveLen);
                    listExcelRow.set(offset4content, sim);
                    if(sim.length() == 11 || sim.length() == 13) {
                        listExcelRow.set(offset4result, "OK");
                    } else {
                        listExcelRow.set(offset4result, "ERROR");
                        resStatus = "Check_ERROR";
                    }
                    break;
                case 0x0210:// 10 位ID
                    String id = Tool.bytesLittleEndian2String(frame, offset, receiveLen);
                    listExcelRow.set(offset4content, id);
                    if(id.length() == 10) {
                        ID_10 = id;
                        listExcelRow.set(offset4result, "OK");
                    } else {
                        listExcelRow.set(offset4result, "ERROR");
                        resStatus = "Check_ERROR";
                    }                    
                    break;
                case 0x032E: // iv
                    String iv = Tool.bytesLittleEndian2String(frame, offset, receiveLen);
                    listExcelRow.set(offset4content, iv);
                    if(iv.length() == 16) {
                        listExcelRow.set(offset4result, "OK");
                    } else {
                        listExcelRow.set(offset4result, "ERROR");
                        resStatus = "Check_ERROR";
                    }
                    break;
                case 0x032D:// key
                    String key = Tool.bytesLittleEndian2String(frame, offset, receiveLen);
                    listExcelRow.set(offset4content, key);
                    if(key.length() == 16) {
                        listExcelRow.set(offset4result, "OK");
                    } else {
                        listExcelRow.set(offset4result, "ERROR");
                        resStatus = "Check_ERROR";
                    }
                    break;
                case 0x032F:// imsi
                    String imsi = Tool.bytesLittleEndian2String(frame, offset, receiveLen);
                    listExcelRow.set(offset4content, imsi);
                    if(imsi.length() == 15) {
                        listExcelRow.set(offset4result, "OK");
                    } else {
                        listExcelRow.set(offset4result, "ERROR");
                        resStatus = "Check_ERROR";
                    }
                    break;
                case 0x0301:// vin
                    String vin = Tool.bytesLittleEndian2String(frame, offset, receiveLen);
                    listExcelRow.set(offset4content, vin);
                    if(vin.length() == 17) {
                        listExcelRow.set(offset4result, "OK");
                    } else {
                        listExcelRow.set(offset4result, "ERROR");
                        resStatus = "Check_ERROR";
                    }
                    break;                          
                case 0x034A:
                    switch (frame[offset]) {
                        case 0x30: case 0x00:listExcelRow.set(offset4content, "无SIM卡");  break;// 55 7A 71 00 03 00 03 30 01 6F
                        case 0x31: case 0x01:listExcelRow.set(offset4content, "有SIM卡");  break;// 55 7A 71 00 03 00 03 30 00 6E
                        case 0x32: case 0x02:listExcelRow.set(offset4content, "建立连接"); break;// 55 7A 71 00 03 00 03 30 00 6E
                        case 0x33: case 0x03:listExcelRow.set(offset4content, "注册鉴权成功"); break;// 55 7A 71 00 03 00 03 30 00 6E
                        default:listExcelRow.set(offset4content, "其他状态"); break;
                    }
                    if(frame[offset] > 0x30 || frame[offset] > 0x00) {
                        listExcelRow.set(offset4result, "OK");
                    } else {
                        listExcelRow.set(offset4result, "ERROR");
                        resStatus = "Check_ERROR";                        
                    }
                    break;
                case 0x0348:
                    int tmp = Tool.bytesBigEndian2int(frame, offset, receiveLen);
                    switch ((tmp >=  0x30) ? tmp-0x30: tmp) { // 0x00=SHORT、0x01=OK、0x02=OPEN
                        case 0:
                            listExcelRow.set(offset4content,"天线短路");
                            listExcelRow.set(offset4result, "ERROR");
                            resStatus = "Check_ERROR";                            
                            break;                            
                        case 1:
                            listExcelRow.set(offset4content,"天线连接");
                            if(exCmd == 0x0002) { // PCB 检测
                                listExcelRow.set(offset4result, "ERROR");
                                resStatus = "Check_ERROR";                                 
                            } else { // 总检检测
                                listExcelRow.set(offset4result, "OK");                                  
                            }
                            break;
                        case 2:
                            listExcelRow.set(offset4content,"天线开路");
                            if(exCmd == 0x0002) { // PCB 检测
                                listExcelRow.set(offset4result, "OK");                                      
                            } else { // 总检检测
                                listExcelRow.set(offset4result, "ERROR");
                                resStatus = "Check_ERROR";                                    
                            }
                            break;                           
                        default:
                            listExcelRow.set(offset4content,"天线异常");
                            listExcelRow.set(offset4result, "ERROR");
                            resStatus = "Check_ERROR";
                            break;                            
                    }                        
                    break;                     
                case 0x02F6:
                    switch (frame[offset]) {
                        case 0x30: case 0x00:listExcelRow.set(offset4content, "江铃福特");  break;// 55 7A 71 00 03 00 03 30 01 6F
                        case 0x31: case 0x01:listExcelRow.set(offset4content, "一汽解放");  break;// 55 7A 71 00 03 00 03 30 00 6E
                        case 0x32: case 0x02:listExcelRow.set(offset4content, "通用客户");  break;
                        default:break;
                    }
                    listExcelRow.set(offset4result, "OK");
                    break;   
                case 0x036D:
                case 0x036E:
                case 0x036F:
                    float ft5v = 0.0f + (((frame[offset]) << 8) | ((frame[offset+1]) & 0x00FF));
                    ft5v /= 1000;
                    listExcelRow.set(offset4content, "DC " + ft5v + "V");                    
                    if(ft5v >= 4.8 && ft5v <= 5.2) {
                        listExcelRow.set(offset4result, "OK"); // 55 7A 71 00 04 00 03 31 00 1E 76
                    } else {
                        listExcelRow.set(offset4result, "ERROR"); // 55 7A 71 00 04 00 03 31 00 22 4A
                        resStatus = "Check_ERROR";
                    }   
                    break;    
                case 0x0353:    
                    int speed = frame[offset];
                    listExcelRow.set(offset4content, String.valueOf(speed) + "km/h");
                    if(speed > 0 && speed <= 120) {
                        Speed = speed; // for B6、B7
                        listExcelRow.set(offset4result, "OK"); 
                    } else {
                        Speed = 0.0f; 
                        listExcelRow.set(offset4result, "ERROR"); 
                        resStatus = "Check_ERROR";
                    }
                    break; 
//                case 0x0354:  
//                case 0x0356:  
//                    float ft67 = 0.0f + (((frame[offset]) << 8) | ((frame[offset+1]) & 0x00FF));
//                    ft67 /= 1000;
//                    listExcelRow.set(offset4content, ft67 + "km/h");
//                    if(Math.abs(ft67 - Speed) <= 1.0) {
//                        listExcelRow.set(offset4result, "OK");
//                    } else {
//                        listExcelRow.set(offset4result, "ERROR"); 
//                        resStatus = "Check_ERROR";
//                    }
//                    break; 
                case 0x0366:
                    float ftTemp = 0.0f + (((frame[offset]) << 8) | ((frame[offset+1]) & 0x00FF));
                    ftTemp /= 1000;
                    listExcelRow.set(offset4content, ftTemp + " ℃");                    
                    if(ftTemp >= 3 && ftTemp <= 30) {
                        listExcelRow.set(offset4result, "OK"); // 55 7A 71 00 04 00 03 31 00 1E 76
                    } else {
                        listExcelRow.set(offset4result, "ERROR"); // 55 7A 71 00 04 00 03 31 00 22 4A
                        resStatus = "Check_ERROR";
                    }   
                    break;     
                case 0x0055:
                    int overSpd = Tool.bytesBigEndian2int(frame, offset, 4);
                    
                    listExcelRow.set(offset4content, String.valueOf(overSpd) + " km/h");                    
                    if(overSpd == 100) {
                        listExcelRow.set(offset4result, "OK"); // 55 7A 71 00 04 00 03 31 00 1E 76
                    } else {
                        listExcelRow.set(offset4result, "ERROR"); // 55 7A 71 00 04 00 03 31 00 22 4A
                        resStatus = "Check_ERROR";
                    }   
                    break;  
                case 0x0307:
                    int pulse = Tool.bytesBigEndian2int(frame, offset, receiveLen);
                    listExcelRow.set(offset4content, String.valueOf(pulse)); 
                    if(pulse == 624*8) {
                        listExcelRow.set(offset4result, "OK");
                    } else {
                        listExcelRow.set(offset4result, "ERROR");
                        resStatus = "Check_ERROR";
                    }                    
                    break;   
                case 0x02FC:
                    int intDelay = Tool.bytesBigEndian2int(frame, offset, receiveLen);
                    listExcelRow.set(offset4content, intDelay + " 秒");                    
                    if(intDelay == 600) {
                        listExcelRow.set(offset4result, "OK"); // 55 7A 71 00 04 00 03 31 00 1E 76
                    } else {
                        listExcelRow.set(offset4result, "ERROR"); // 55 7A 71 00 04 00 03 31 00 22 4A
                        resStatus = "Check_ERROR";
                    }   
                    break;
                case 0x0013:
                    String ip = Tool.bytesLittleEndian2String(frame, offset, receiveLen);
                    listExcelRow.set(offset4content, ip);
                    if(!ip.equals("")) {
                        listExcelRow.set(offset4result, "OK");
                    } else {
                        listExcelRow.set(offset4result, "ERROR");
                        resStatus = "Check_ERROR";
                    }
                    break;                      
                case 0x0018:
                    int port = Tool.bytesBigEndian2int(frame, offset, receiveLen);
                    listExcelRow.set(offset4content, String.valueOf(port));
                    if(port >= 1000) {
                        listExcelRow.set(offset4result, "OK");
                    } else {
                        listExcelRow.set(offset4result, "ERROR");
                        resStatus = "Check_ERROR";
                    }                    
                    break; 
                case 0x02F7:
                    String type = Tool.bytesLittleEndian2String(frame, offset, receiveLen);
                    listExcelRow.set(offset4content, type);
                    if(type.equals("JL207A")) {
                        DeviceType = type;
                        listExcelRow.set(offset4result, "OK");
                    } else {
                        listExcelRow.set(offset4result, "ERROR");
                        resStatus = "Check_ERROR";
                    }
                    break;     
                case 0x0083:
                    String car = Tool.bytesLittleEndian2String(frame, offset, receiveLen, Charset.forName("GBK"));
                    listExcelRow.set(offset4content, car);
                    if(!car.equals("")) {
                        listExcelRow.set(offset4result, "OK");
                    } else {
                        listExcelRow.set(offset4result, "ERROR");
                        resStatus = "Check_ERROR";
                    }
                    break;   
                case 0x0211:
                    String driverID = new String(frame, offset, 18);
                    listExcelRow.set(offset4content, driverID);
                    if(!driverID.equals("000000000000000000") ) {
                        listExcelRow.set(offset4result, "OK");
                    } else {
                        listExcelRow.set(offset4result, "ERROR");
                        resStatus = "Check_ERROR";
                    }
                    break;   
                case 0x035C: // dg  默认通过，检测工装不支持  20170703
                    listExcelRow.set(offset4content, "0x01");
                    listExcelRow.set(offset4result, "OK");
                    break;                      
                case 0x0306:
                    String timeStr = Tool.GB19056BCDs2String(frame, offset, receiveLen);
                    Calendar c = Calendar.getInstance();  
                    {
                        try {
                            c.setTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(timeStr));  // 毫秒
                        } catch (ParseException ex) {
                            Logger.getLogger(ThreadRS232.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    long timeCount = c.getTimeInMillis();
                    long timePC = System.currentTimeMillis();
                    
                    listExcelRow.set(offset4content, timeStr);
                    if(Math.abs(timeCount - timePC)/1000000 <= 50) {
//                    if(timeStr.contains("2017-") || timeStr.contains("2018-") || timeStr.contains("2019-") ||timeStr.contains("2020-")) {
                        listExcelRow.set(offset4result, "OK");
                    } else {
                        listExcelRow.set(offset4result, "ERROR");
                        resStatus = "Check_ERROR";
                    }
                    break;  
                case 0x036B:
                    if(Code.contains(DeviceType) && Code.contains(ID_10)) { 
                        listExcelRow.set(offset4content, ID_10);
                        listExcelRow.set(offset4result, "OK");
                    } else {
                        listExcelRow.set(offset4content, Code);
                        listExcelRow.set(offset4result, "ERROR");
                        resStatus = "Check_ERROR";
                    }   
                    break; 
                case 0x0370: // RS485
                    if(frame[offset] == 0x01) {
                        listExcelRow.set(offset4content, "正常");
                        listExcelRow.set(offset4result, "OK"); 
                    } else {
                        listExcelRow.set(offset4content, "错误");
                        listExcelRow.set(offset4result, "ERROR");
                        resStatus = "Check_ERROR";
                    }
                    break;         
                case 0x0358: // 锂电池
                    if(frame[offset] == 0x00) {
                        listExcelRow.set(offset4content, "正常");
                        listExcelRow.set(offset4result, "OK"); 
                    } else {
                        listExcelRow.set(offset4content, "错误");
                        listExcelRow.set(offset4result, "欠压/无电池");
                        resStatus = "Check_ERROR";
                    }                    
                    break;                     
                default: 
                    // 0345 0346  0347  0349 034B  034C  0363  0359  0377 0376  0354 0356
                    String hexTmp = Integer.toHexString(frame[offset]);
                    listExcelRow.set(offset4content, (hexTmp.length() == 1)? "0x0" + hexTmp: "0x" + hexTmp);
                    if(frame[8] > 0x00) {
                        listExcelRow.set(offset4result, "OK"); // 55 7A 71 00 03 00 03 30 01 6F
                    } else {
                        listExcelRow.set(offset4result, "ERROR"); // 55 7A 71 00 03 00 03 30 00 6E
                        resStatus = "Check_ERROR";
                    }
                    break;
            }

            if((cmd&0x71) == 0x71) { // CG901
                tabMakerController.getTabMakerShowController().update_check_CG901A(lineNum, listExcelRow, true);
            } else if((cmd&0x74) == 0x74) { // JL207A
                tabMakerController.getTabMakerShowController().update_check_JL207A(lineNum, listExcelRow, true);
            }            
            
        }
        return resStatus;
    }    
    
    private boolean readyStart(boolean isEnter) {
        if(isEnter && !ExitFlag) {  // 进入检测状态
            Object[] resIn = Utils.sendAndReceive(tabMakerController.getHostController(), (byte) 0xD1, 0x0340, new byte[]{0x04}, "进入检测状态指令", 3, 2 + 1, 2 + 1, true);
            String tmpStr = resIn[1].toString();           
            if(resIn[0].toString().contains("_noNull")) {
                if(tmpStr.contains(resIn[1].toString())) {
                    tabMakerController.addLog("执行进入检测状态指令：" + tmpStr, null);
                    return true;
                } else {
                    tabMakerController.addLog("进入检测状态指令返回值不相等，请确认...", null);
                    return false;
                }
            } else if(resIn[0].toString().contains("res_timeout")) {
                tabMakerController.addLog("待检品无应答，确认串口连接是否正常", null);
                return false;             
            } else {
                tabMakerController.addLog("进入检测状态指令终端返回无效值，请确认...", null);
                return false; 
            } 
        }
        return true;
    }
    
    private void delayStart(int delaySecond) {
        if(!ExitFlag) {
            if(tabMakerController.getCbDoOnce().isSelected()) {  // 无延时处理，立即执行
                tabMakerController.addLog("自动检测即刻开始......", null);
            } else {
                tabMakerController.addLog("自动检测开始，系统初始化倒计时开始......", null);
                for(int i=delaySecond; i>0; i--) {
                    if(ExitFlag) {
                        break;
                    } else {
                        try {
                            Thread.sleep(1000);
                            
                            if(delaySecond > 30) {
                                if(i%5 == 0) {
                                    tabMakerController.addLog("倒计时：" + i + " 秒", null);
                                }
                            } else if(delaySecond > 10) {
                                if(i%2 == 0) {
                                    tabMakerController.addLog("倒计时：" + i + " 秒", null);
                                }
                            } else {
                                tabMakerController.addLog("倒计时：" + i + " 秒", null);
                            }
                        } catch (InterruptedException ex) {
                            System.out.println("Thread.sleep() was interrupted!");
                        }                     
                    }
                }                
            }
            
              
        }
    }
    
}

