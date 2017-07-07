package smartvip.mainStage;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import smartvip.FXMLLoginController;
import smartvip.mainStage.bottom.StatusBarController;
import smartvip.mainStage.center.rightShow.fae.TabFAEShowController;
import smartvip.mainStage.center.rightShow.maker.TabMakerShowController;
import smartvip.mainStage.center.leftBottomLog.SysLogController;
import smartvip.mainStage.center.leftTopControl.fae.TabFAEController;
import smartvip.mainStage.center.leftTopControl.maker.TabMakerController;
import smartvip.mainStage.top.MenuBarController;
import tool.box.common.GlobalVars;
import tool.box.popwindow.PopWindow;
import tool.box.login.Login;
import tool.box.rs232.Serial;

public class HostController implements Initializable {
    private final Login login = FXMLLoginController.login; 
    
    private Stage stage;
    private ResourceBundle rb;
    private PopWindow popWindow;
    public static Serial serial;
    
    public static String OptionDirPath;
    public static String OptionFilePath;
    
    @FXML private TabPane tabShowPane;
    @FXML private TabPane tabControlPane;
    @FXML private MenuBarController menuBarController;
    @FXML private StatusBarController statusBarController;
    @FXML private TabMakerController tabMakerController;
    @FXML private TabMakerShowController tabMakerShowController;
    @FXML private TabFAEController tabFAEController;
    @FXML private TabFAEShowController tabFAEShowController;
    @FXML private SysLogController sysLogController;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) { // url=xx.fxml文件   rb=xx.properties文件
        this.rb = rb;
        
        popWindow = new PopWindow("系统运行日志");
        switch (GlobalVars.RolePermission) {
            case "admin": popWindow.show(); break;
            case "maker": tabShowPane.getTabs().remove(1, 4); tabControlPane.getTabs().remove(1, 4); break;
            case "fae": tabShowPane.getTabs().remove(2, 4); tabControlPane.getTabs().remove(2, 4); tabShowPane.getTabs().remove(0); tabControlPane.getTabs().remove(0); break;
        }
        
        this.tabControlPane.getSelectionModel().selectedItemProperty().addListener((ov, oldTab, newTab) -> {
            switch(newTab.getId()) {
                case "tabMaker" :  this.tabShowPane.getSelectionModel().select(0); break;
                case "tabFAE" :  this.tabShowPane.getSelectionModel().select(1); break;
                case "tabRD" :  this.tabShowPane.getSelectionModel().select(2); break;
                case "tabShow" :  this.tabShowPane.getSelectionModel().select(3); break;
            }
        });
        this.tabShowPane.getSelectionModel().selectedItemProperty().addListener((ov, oldTab, newTab) -> {
            switch(newTab.getId()) {
                case "tabMakerShow" :  this.tabControlPane.getSelectionModel().select(0); break;
                case "tabFAEShow" :  this.tabControlPane.getSelectionModel().select(1); break;
                case "tabRDShow" :  this.tabControlPane.getSelectionModel().select(2); break;
                case "tabShowShow" :  this.tabControlPane.getSelectionModel().select(3); break;
            }
        });        
        
        serial = new Serial(rb, this); // serial port init
        String contents = "";
        switch (serial.open(null, serial.getSerialSetting().getBaudRate(), serial.getSerialSetting().getDatabits(), serial.getSerialSetting().getStopbits(), serial.getSerialSetting().getParity(), 0)) {
//        switch (serial.open(null, 115200, 8, 1, SerialPort.PARITY_NONE, 0)) {
            case -1: contents = serial.getSerialSetting().getSelectedPortName() + "已被其他软件占用，请确认！\r\n"; break;
            case 0: contents = "本计算机无可用串口资源，请确认！\r\n"; break;
            default: contents = serial.getSerialSetting().getSelectedPortName() + ":" + rb.getString("SerialOK"); break;
        }
        addLog(contents, null);        
        
        if(serial.getStatus() > 0) {
            statusBarController.setLeftTip(this.login.getName() + "  &  " + serial.getSerialSetting().getParams()); // 底部状态栏左侧内容
        } else {
            statusBarController.setLeftTip(this.login.getName() + "  &  " + "无有效串口"); // 底部状态栏左侧内容
        }
         
    }    
    
    public void setStage(Stage stage) {
        if(stage == null) {
            System.out.println("setStage is null!");
        } else {
            this.stage = stage;
            serial.setStage(stage);
            tabMakerController.setStage(stage); 
            tabFAEController.setStage(stage); 
            menuBarController.setStage(stage);

            sysLogController.setStage(stage);  

            menuBarController.setSysLogController(sysLogController);
            menuBarController.setPopWindow(popWindow);
            menuBarController.setStatusBarController(statusBarController);
            menuBarController.setLogin(login); 

            tabMakerController.setHostController(this); 
            tabMakerController.setTabMakerShowController(tabMakerShowController);
            tabMakerController.setSysLogController(sysLogController);
            tabMakerController.setPopWindow(popWindow); 

            tabFAEController.setHostController(this); 
            tabFAEController.setTabFAEShowController(tabFAEShowController);
            tabFAEController.setSysLogController(sysLogController);
            tabFAEController.setPopWindow(popWindow); 
        }
    }
    
    public void setHostController(HostController hostController) {
        if(hostController == null) {
            System.out.println("hostController is null!");
        } else {
            this.tabFAEController.setHostController(hostController); 
        }        
    }
    
    public void addLog(String sysLog, String popLog) {
        if(this.sysLogController != null && this.popWindow != null) {
            this.sysLogController.addInfo(sysLog); 
            this.popWindow.addInfo(popLog);            
        } else {
            System.out.println("sysLogController || popWindow is null");
        }
    }  
    
    public PopWindow getPopWindow() {
        return this.popWindow;
    }
    
    public StatusBarController getStatusBarController() {
        return this.statusBarController;
    }
    
}
