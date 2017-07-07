package smartvip.mainStage.center.leftTopControl.fae;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import smartvip.mainStage.HostController;
import smartvip.mainStage.center.rightShow.fae.TabFAEShowController;
import smartvip.mainStage.center.leftBottomLog.SysLogController;
import tool.box.common.Tool;
import tool.box.popwindow.PopWindow;
import tool.box.poi.excel.File4ModelOption;
import tool.box.sms.SMS;
import tool.box.sms.SMSTask4CreateFile;
import tool.box.sms.SMSTask4Send;

public class TabFAEController implements Initializable  {
    private TabFAEShowController tabFAEShowController;
    private SysLogController sysLogController;
    private PopWindow popWindow;
    private Stage stage;
    private HostController hostController;
    private Toggle selectedModel;
    private Object[][] objs;  
    
    @FXML private Button btnReadParams;
    @FXML private Button btnSetUSB;
    @FXML private Button btnSetRS232;    
    
    @FXML private Label leftTip;
    @FXML private GridPane gridPaneFAEOption;
    @FXML private ToggleGroup faeSeleckGroup = new ToggleGroup();
    
     @Override
    public void initialize(URL location, ResourceBundle resources) { // url=xx.fxml文件   rb=xx.properties文件
        System.out.println("TabFAEController initialize");
        
        String resParseFile = File4ModelOption.parseFile(new File(resources.getString("SmartVIP_file")), 0 , "ModelOption");
        if(resParseFile.contains("ok")) {  // 读配置文件，决定布局的内容
            this.addUis2Pane(File4ModelOption.listExcel);
        } 
    }    
    
    @FXML
    private void onButtonMouseClicked(MouseEvent event) {
        switch (((Button) event.getSource()).getId()) {
            case "btnReadParams":   onBtnReadParams();  break;
            case "btnSetUSB":       onBtnSetUSB();      break;
            case "btnSetRS232":     onBtnSetRS232();    break;
            case "btnSetWIFI":     onBtnSetWIFI();    break;
            case "btnSetCAN":     onBtnSetCAN();    break;
            case "btnSetSMS":     onBtnSetSMS();    break;
            default: break;
        }
    }
    
    private void onBtnReadParams() {
        if(getSerialStatus()) {
            if(checkInputDatas(tabFAEShowController.getGridPane(), false, false, false)) {
                String resTask4RS232Read = new TabFAETask4RS232Read(stage, this).run(objs, selectedModel.getUserData().toString(), objs[0].length);        
                this.addLog(null, "读参数状态返回值： " + resTask4RS232Read);            
            }
        }
    }
    private void onBtnSetUSB() {
        if(checkInputDatas(tabFAEShowController.getGridPane(), true, true, true)) {
            new TabFAETask4USB(stage, this).run(objs, selectedModel.getUserData().toString(), objs[0].length);
        }
    }  
    private void onBtnSetRS232() {
        if(getSerialStatus()) {
            if(checkInputDatas(tabFAEShowController.getGridPane(), true, true, true)) {// startup thread send RS232 setting CMDs
                String resTask4RS232Set = new TabFAETask4RS232Set(stage, this).run(objs, selectedModel.getUserData().toString(), objs[0].length); 
                this.addLog(null, "串口设置参数状态返回值： " + resTask4RS232Set);
            }              
        }
    }  
    private void onBtnSetWIFI() {
        if(checkInputDatas(tabFAEShowController.getGridPane(), true, true, true)) {
            this.addLog("WIFI设置参数功能，敬请期待！", null);// do USB something
        }
    }    
    private void onBtnSetCAN() {
        if(checkInputDatas(tabFAEShowController.getGridPane(), true, true, true)) {
            this.addLog("CAN设置参数功能，敬请期待！", null);// do USB something
        }
    } 
    private void onBtnSetSMS() {
        if(checkInputDatas(tabFAEShowController.getGridPane(), true, true, true)) {
            String resParams = SMS.onShow(stage, "【短信设置】", "/tool/box/raw/images/_16_message.png");
            if(resParams != null) {
                if(checkInputDatas(tabFAEShowController.getGridPane(), true, true, true)) {// startup thread create SMS file setting CMDs
                    if(resParams.startsWith("F:")) { // create SMS file
                        new SMSTask4CreateFile(stage, this).run(objs, resParams.substring(2), objs[0].length); 
                    } else { // send SMS
                        String resTask4SMS = new SMSTask4Send(stage, this).run(objs, selectedModel.getUserData().toString(), resParams, objs[0].length);
                    }
                } 
            } 
        }
    }       
    
    private int checkSelectedCheckBox(GridPane gridPane) {
        int resCount = 0;
        for (int i = tabFAEShowController.getContentStartIndex(); i < gridPane.getChildren().size(); ) {
            CheckBox function = (CheckBox)gridPane.getChildren().get(i+1);
            resCount = resCount + ((function.isSelected()) ? 1 : 0); 
            i += tabFAEShowController.getContentRowCellsCount();
        }
        return resCount;
    }
    
    private boolean checkInputDatas(GridPane gridPane, boolean isCheckSelected, boolean isCheckContentEmpty, boolean isCheckModel) { 
            
        selectedModel = faeSeleckGroup.getSelectedToggle();
        objs = getTabFAEShowList(gridPane, selectedModel.getUserData().toString());
        
        if(isCheckModel) { // check：adapt model equal RadioButton        
            if(!checkModelValid(objs)) {
                return false;
            }  
        }

        if(isCheckSelected) { // check：have selected CheckBox
            int intTmp = checkSelectedCheckBox(gridPane); 
            if(intTmp == 0) {
                this.addLog("没有勾选任何要设置的参数，请确认", null);
                return false;
            } else {
                this.addLog("已选择要设置的参数个数为：" + intTmp, null);
            }
        }

        if(isCheckContentEmpty) { // check：TextField is NULL and is Valid;  
            if(!checkTextFieldValid(objs)) { 
                this.addLog(null, "存在输入内容异常，请确认");
                return false;
            }               
        }
        
        return true;
    }     
    
    private Object[][] getTabFAEShowList(GridPane gridPane, String radioStr) {
        Object[][] resObjs = new Object[tabFAEShowController.getContentRowLinesCount()][tabFAEShowController.getContentRowCellsCount()];
        int startOffset = tabFAEShowController.getContentStartIndex();
        
        for (int i = startOffset, j = 0; i < gridPane.getChildren().size(); ) {
           
            Label no = (Label)gridPane.getChildren().get(i);
            CheckBox function = (CheckBox)gridPane.getChildren().get(i+1);
            TextField content = (TextField)gridPane.getChildren().get(i+2); 
            
            Label JL = (Label)gridPane.getChildren().get(i+3);
            Label CG = (Label)gridPane.getChildren().get(i+4);
            Label CZ = (Label)gridPane.getChildren().get(i+5);
            Label ZB = (Label)gridPane.getChildren().get(i+6);
            Label memo = (Label)gridPane.getChildren().get(i+7);
            Label cmd = (Label)gridPane.getChildren().get(i+8);
            Label sendLen = (Label)gridPane.getChildren().get(i+9);
            Label receiveLen = (Label)gridPane.getChildren().get(i+10);
            
            resObjs[j][0] = no.getText();
            resObjs[j][1] = function.isSelected();
            resObjs[j][2] = function.getText();
            resObjs[j][3] = (content != null)? content.getText() : "";
            resObjs[j][4] = checkModel4RadioButton(JL.getText(), CG.getText(), CZ.getText(), ZB.getText(), radioStr);
            resObjs[j][5] = cmd.getText();
            resObjs[j][6] = sendLen.getText();
            resObjs[j][7] = receiveLen.getText();
            resObjs[j][8] = String.valueOf(i + 2); // offset
            
            i += tabFAEShowController.getContentRowCellsCount();
            j++;
        }
        return resObjs;
    }    
    
    // 0=No 1=CheckBox 2=Tip 3=Content 4=ModelType 5=SubCmd 6=SendLen 7=ReceiveLen 8=Offset  
    private boolean checkTextFieldValid(Object[][] objs) {
        boolean resBoolean = true;
        for (Object[] obj : objs) {
            if ((boolean) obj[1] && (boolean) obj[4]) {
                if (obj[3] == null || ((String) obj[3]).equals("")) {
                    addLog(((String) obj[2]) + " 设置内容为空", null);
                    resBoolean = false;
                    continue;
                } 
                
                String content = ((String) obj[3]).toUpperCase();
                
                switch(Integer.parseInt((String) obj[5], 16)) {
                    case 0x0215: // 管理平台类型		2	3
                        if(!Tool.isNumeric(content, 1, 1, "[0-1]")) { 
                            addLog(((String) obj[2]) + " 设置内容非【1】长度或者不为“1”、“0”", null);
                            resBoolean = false;                            
                        } break;
                    case 0x0013: // 服务器地址		2	34
                        if(!content.startsWith("JT") && !Tool.isIP(content)) {
                            addLog(((String) obj[2]) + " 设置内容非全国货运平台或合法IP地址", null);
                            resBoolean = false;                            
                        } break;
                    case 0x0018: ;// 服务器端口号		2	6
                        if(!Tool.isNumeric(content, 3, 5)) {
                            addLog(((String) obj[2]) + " 设置内容非【3-5】长度的端口号", null);
                            resBoolean = false;                            
                        } break;                    
                    case 0x0201: ;// SIM卡号		2	22
                        if(!Tool.isNumeric(content, 11, 11) && !Tool.isNumeric(content, 13, 13)) {
                            addLog(((String) obj[2]) + " 设置内容非【11、13】长度的SIM卡号", null);
                            resBoolean = false;                            
                        } break;                    
                    case 0x0083: // 车辆号牌号码		2	14
                        if(content.getBytes().length < 6 || content.getBytes().length > 12) {
                            addLog(((String) obj[2]) + " 设置内容非【6-12】长度的车辆号牌号码", null);
                            resBoolean = false;                            
                        } break;                     
                    case 0x0084: // 车辆号牌颜色		2	3
                        if(!Tool.isNumeric(content, 1, 1, "[1-5]")) {
                            addLog(((String) obj[2]) + " 设置内容非【1】长度或者不为“0-5”", null);
                            resBoolean = false;                            
                        } break;                      
                    case 0x0302: // 车辆类型		2	14
                        if(content.getBytes().length < 4 || content.getBytes().length > 12) {
                            addLog(((String) obj[2]) + " 设置内容非【4-12】长度的车辆类型", null);
                            resBoolean = false;                            
                        } break;                      
                    case 0x0207: // 六位行政区划省域		2	8
                        if(!Tool.isNumeric(content, 6, 6)) {
                            addLog(((String) obj[2]) + " 设置内容非【6】长度的行政区划", null);
                            resBoolean = false;                            
                        } break;                      
                    case 0x0301: // 车辆VIN编号		2	19   LFWSRXSJXG5S01027
                        if(!Tool.isCharAndNum(content, 17, 17)) {
                            addLog(((String) obj[2]) + " 设置内容非【17】长度的VIN号码", null);
                            resBoolean = false;                            
                        } break;                      
                    case 0x0303: // 发动机编号		2	30
                        if(content.getBytes().length < 4 || content.getBytes().length > 28) {
                            addLog(((String) obj[2]) + " 设置内容非【4-28】长度的车辆类型", null);
                            resBoolean = false;                            
                        } break;                     
                    case 0x0055: // 超速阈值		2	4
                        if(!Tool.isNumeric(content, 2, 3)) {
                            addLog(((String) obj[2]) + " 设置内容非【2-3】长度的超速阈值", null);
                            resBoolean = false;                            
                        } break;                        
                    case 0x0312: // 速度源选择		2	3
                        if(!Tool.isNumeric(content, 1, 1, "[0-2]")) {
                            addLog(((String) obj[2]) + " 设置内容非【1】长度或者不为“0-2”", null);
                            resBoolean = false;                            
                        } break;                        
                    case 0x0307: // 脉冲系数		2	4
                        if(!Tool.isNumeric(content, 4, 5)) {
                            addLog(((String) obj[2]) + " 设置内容非【4-5】长度的脉冲系数", null);
                            resBoolean = false;                            
                        } break;                        
                    case 0x020F: // 超时驾驶夜间模式		2	3
                        if(!Tool.isNumeric(content, 1, 1, "[0-1]")) {
                            addLog(((String) obj[2]) + " 设置内容非【1】长度或者不为“0-1”", null);
                            resBoolean = false;                            
                        } break;                       
                    case 0x02F6: // 四位客户代码		2	6
                        if(!Tool.isNumeric(content, 1, 4)) {
                            addLog(((String) obj[2]) + " 设置内容非【1-4】长度的客户代码", null);
                            resBoolean = false;                            
                        } break;                       
                    case 0x005E: // 侧翻角度		2	4
                        if(!Tool.isNumeric(content, 1, 2)) {
                            addLog(((String) obj[2]) + " 设置内容非【1-2】长度的侧翻角度阈值", null);
                            resBoolean = false;                            
                        } break;                       
                    case 0x0211: // 设置驾驶员卡		2	20
                        if(content.getBytes().length != 18) {
                            addLog(((String) obj[2]) + " 设置内容非【18】长度的身份识别卡", null);
                            resBoolean = false;                            
                        } break;                      
                    case 0x0029: // 行驶汇报间隔		2	6
                        if(!Tool.isNumeric(content, 2, 6)) {
                            addLog(((String) obj[2]) + " 设置内容非【2-6】长度的行驶中汇报间隔", null);
                            resBoolean = false;                            
                        } break;                    
                    case 0x0027: // 休眠汇报间隔		2	6
                        if(!Tool.isNumeric(content, 2, 6)) {
                            addLog(((String) obj[2]) + " 设置内容非【2-6】长度的休眠中汇报间隔", null);
                            resBoolean = false;                            
                        } break;                     
                    case 0x020D: // 延时关机		2	4
                        if(!Tool.isNumeric(content, 2, 5)) {
                            addLog(((String) obj[2]) + " 设置内容非【2-5】长度的延时关机阈值", null);
                            resBoolean = false;                            
                        } break;                     
                    case 0x0304: // 摄像头触发拍照		2	3
                        if(!Tool.isNumeric(content, 1, 1)) {
                            addLog(((String) obj[2]) + " 设置内容非【1】长度的摄像头拍照指令", null);
                            resBoolean = false;                            
                        } break;                     
                    case 0x0340: // 清除数据		2	3
                        if(!Tool.isNumeric(content, 1, 1, "[0-3]")) {
                            addLog(((String) obj[2]) + " 设置内容非【1】长度或者不为“0-3”", null);
                            resBoolean = false;                            
                        } break;                         
                    case 0x0213: // 无线升级指令		2	3
                        if(!Tool.isNumeric(content, 1, 1, "[0-1]")) {
                            addLog(((String) obj[2]) + " 设置内容非【1】长度或者不为“0-1”", null);
                            resBoolean = false;                            
                        } break;                      
                    case 0x0214: // 无线升级地址		2	26  
                        if(!Tool.isNumeric(content, 7, 24)) {
                            addLog(((String) obj[2]) + " 设置内容非【7-24】长度的无线升级指令", null);
                            resBoolean = false;                            
                        } break;                      
                    case 0x0210: // 十位设备编号		2	12
                        if(!Tool.isNumeric(content, 10, 10)) {
                            addLog(((String) obj[2]) + " 设置内容非【10】长度的设备唯一性编号", null);
                            resBoolean = false;                            
                        } break;                    
                    case 0x032D: // 十六位平台KEY值		2	18
                        if(!Tool.isCharAndNum(content, 16, 16)) {
                            addLog(((String) obj[2]) + " 设置内容非【16】长度的平台KEY值", null);
                            resBoolean = false;                            
                        } break;                    
                    case 0x032E: // 十六位平台IV值		2	18
                        if(!Tool.isCharAndNum(content, 16, 16)) {
                            addLog(((String) obj[2]) + " 设置内容非【16】长度的平台IV值", null);
                            resBoolean = false;                            
                        } break;                       
                    case 0x032F: // 十五位平台IMSI号		2	17
                        if(!Tool.isCharAndNum(content, 15, 15)) {
                            addLog(((String) obj[2]) + " 设置内容非【15】长度的平台IMSI值", null);
                            resBoolean = false;                            
                        } break;                       
                }                
            }
        }
        return resBoolean;
    }
    
    private boolean checkModel4RadioButton(String JL, String CG, String CZ, String ZB, String radioStr) {
        return !((radioStr.startsWith("JL") && JL.equals("-") || 
                (radioStr.startsWith("CG") && CG.equals("-")) ||
                (radioStr.startsWith("CZ") && CZ.equals("-")) ||
                (radioStr.startsWith("ZB") && ZB.equals("-"))));
    }
    
    private boolean checkModelValid(Object[][] objs) {
        boolean resBoolean = true;
        for (Object[] obj : objs) {
            if ((boolean) obj[1]) {
                if (!((boolean) obj[4])) {
                    addLog(((String) obj[2]) + "设备型号不支持该指令", null);
                    resBoolean = false;
                }
            }
        }
        return resBoolean;
    }    
    
    private void addUis2Pane(List<List<Object>> listExcel) {
        for(int i=1; i<listExcel.size(); i++) {
            for(int j=0; j<listExcel.get(i).size(); j++) {
                if(j == 0) {
                    Label tip = new Label(listExcel.get(i).get(j).toString());
                    tip.setStyle("-fx-font-size: 11pt; -fx-font-family: \"Segoe UI Semibold\"; -fx-graphic:url(/tool/box/raw/images/_16_node_1.png); -fx-padding:5 20 5 10; ");
                    this.gridPaneFAEOption.add(tip, j, i-1); // obj col row 
                } else {
                    RadioButton rdBtn = new RadioButton(listExcel.get(i).get(j).toString());
                    rdBtn.setSelected((i == 1));
                    rdBtn.setStyle("-fx-font-size: 10.5pt;  -fx-font-family: \"Segoe UI Semibold\"; "); 
                    rdBtn.setUserData(listExcel.get(i).get(j).toString()); 
                    rdBtn.setToggleGroup(faeSeleckGroup);
                    this.gridPaneFAEOption.add(rdBtn, j, i-1); // obj col row
                }
            }
        }
    }
    
    
    public void setTabFAEShowController(TabFAEShowController tabFAEShowController) {
        if(tabFAEShowController == null) {
            System.out.println("tabFAEShowController is null");
        } else {
            this.tabFAEShowController = tabFAEShowController;
        }
        
    }  
    public TabFAEShowController getTabFAEShowController() {
        return tabFAEShowController;
    }      
    public void setSysLogController(SysLogController sysLogController) {
        if(sysLogController == null) {
            System.out.println("sysLogController is null");
        } else {
            this.sysLogController = sysLogController;
        }
    }
    public void setPopWindow(PopWindow popWindow) {
        if(popWindow == null) {
            System.out.println("popWindow is null");
        } else {
            this.popWindow = popWindow;
        }  
    }
    public SysLogController getSysLogController() {
        return sysLogController;
    }
    public PopWindow getPopWindow() {
        return this.popWindow;
    }
    public void addLog(String sysLog, String popLog) {
        if(this.sysLogController != null && this.popWindow != null) {
            this.sysLogController.addInfo(sysLog); 
            this.popWindow.addInfo(popLog);            
        } else {
            System.out.println("sysLogController || popWindow is null");
        }
    }  
    
    public void setStage(Stage stage) {
        if(stage == null) {
            System.out.println("setStage is null!");
        } else {
            this.stage = stage;
        }
    } 
    public void setHostController(HostController hostController) {
        if(hostController == null) {
            System.out.println("hostController is null!");
        } else {
            this.hostController = hostController;
        }
    }    
    public HostController getHostController() {
        return this.hostController;
    }     
    
    public Label getLeftTip() {
        return leftTip;
    }
    
    public boolean getSerialStatus() {
        switch (HostController.serial.getStatus()) {
            case 0:sysLogController.addInfo("本计算机无可用串口资源，请确认......"); return false;
            case 2: sysLogController.addInfo("串口已关闭，请确认......"); return false;
            case -1:sysLogController.addInfo((HostController.serial).getAllPorts().get(0).getName() + "已被其他软件占用，请确认......"); return false;
            default: return true;
        }             
    }

    
    
}
