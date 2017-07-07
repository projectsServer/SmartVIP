package smartvip.mainStage.center.leftTopControl.maker;

import smartvip.mainStage.center.leftTopControl.maker.thread.ThreadSetParams4CG901A;
import smartvip.mainStage.center.leftTopControl.maker.thread.ThreadSetParams4JL207A;
import smartvip.mainStage.center.leftTopControl.maker.thread.ThreadRS232;
import smartvip.mainStage.center.leftTopControl.maker.thread.ThreadCheck4CG901A;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import smartvip.FXMLLoginController;
import smartvip.mainStage.HostController;
import smartvip.mainStage.center.rightShow.maker.TabMakerShowController;
import smartvip.mainStage.center.leftBottomLog.SysLogController;
import smartvip.mainStage.center.leftTopControl.maker.excel.FileSetParams4CG901A;
import smartvip.mainStage.center.leftTopControl.maker.excel.FileSetParams4JL207A;
import smartvip.mainStage.center.leftTopControl.maker.thread.ThreadCheck4JL207A;
import tool.box.common.LocalStorage;
import tool.box.common.ModalDialog;
import tool.box.popwindow.PopWindow;
import tool.box.login.Login;

public class TabMakerController implements Initializable  {
    private final Login login = FXMLLoginController.login; 
    private Stage stage;
    private List<List<Object>> listExcel;
    private List<List<Object>> listExcelDefault = new ArrayList<>();
    private String[] validExcelRow = new String[11];
    private TabMakerShowController tabMakerShowController;
    private SysLogController sysLogController;
    private PopWindow popWindow;
    private HostController hostController;
    
    @FXML private Label leftTip, fileChoose, deviceType, makerStep, used, noUse, total;
    @FXML private Button btnDoOnce;
    @FXML private CheckBox cbDoOnce;

    @Override
    public void initialize(URL location, ResourceBundle resources) { // url=xx.fxml文件   rb=xx.properties文件
        System.out.println("TabMakerController  initialize");
//        btnDoOnce.setOnKeyReleased((KeyEvent event) -> { if (event.getCode() == KeyCode.ENTER) { doOnceClicked(event); } });       
        
        String filePath = LocalStorage.getPropery("OptionFilePath", "");
        if(!filePath.equals("")) {
            File fileTmp = new File(filePath);
            if (fileTmp.exists()) {
                if(!fileTmp.renameTo(fileTmp)){  
                    System.out.println("文件已经被打开，请先关闭......");
                } else {    // Tab addInfo excel content by dynamic thread 
                    String[] res = new String[] {LocalStorage.getPropery("OptionFilePathType", ""), LocalStorage.getPropery("OptionFilePathOption", "")};
                    String[][] params = new String[][] {{"cmd", res[0]},{"subCmd", res[1]}, {"user", FXMLLoginController.login.getName()}};

                    if(res[0].contains("CG901A") && res[1].contains("参数设置")) {
                        new ThreadSetParams4CG901A(this, fileTmp).run("", params, 1);                               
                    } else if(res[0].contains("CG901A") && res[1].contains("印制板检测")) {
                        new ThreadCheck4CG901A(this, fileTmp).run("PCB", params, 1);                            
                    } else if(res[0].contains("CG901A") && res[1].contains("总检测试")) {
                        new ThreadCheck4CG901A(this, fileTmp).run("TOTAL", params, 1);
                    } else if(res[0].contains("JL207A") && res[1].contains("参数设置")) {
                        new ThreadSetParams4JL207A(this, fileTmp).run("", params, 1); 
                    } else if(res[0].contains("JL207A") && res[1].contains("印制板检测")) {
                        new ThreadCheck4JL207A(this, fileTmp).run("PCB", params, 1);                             
                    } else if(res[0].contains("JL207A") && res[1].contains("总检测试")) {
                        new ThreadCheck4JL207A(this, fileTmp).run("TOTAL", params, 1); 
                    }
                    disableUIs(false);
                }
            }            
        }  
    }    
    
    @FXML
    private void onDoOnceClicked(ActionEvent event) {
        doOnceClicked(event);
    }
 
    
    @FXML
    private void onMouseClicked(MouseEvent event) {
        switch (((Label) event.getSource()).getId()) {
            case "fileChooseTip": 
            case "fileChoose": chooseFilePath(); break;
            default: break;
        }
    }
    
    private void doOnceClicked(Event event) {
        
        if(this.noUse.getText().equals("0")) {
            this.addLog("无可用的资源，请重新分配......", null);
            disableUIs(true);
        } else if(HostController.serial.getStatus() == 0) {
            this.addLog("本计算机无可用串口资源，请确认......", null);
        } else if(HostController.serial.getStatus() == -1) {
            this.addLog((HostController.serial).getAllPorts().get(0).getName() + "已被其他软件占用，请确认......", null);
        } else if(HostController.serial.getStatus() == 2) {
            this.addLog((HostController.serial).getAllPorts().get(0).getName() + "已关闭，请确认......", null);            
        } else {
            String cmd = "", exCmd = "";
            
            disableUIs(true);
            if(deviceType.getText().contains("CG901A")) {
                if(makerStep.getText().contains("参数设置")) {
                    cmd = "D1"; exCmd = "0001";          
                } else if(makerStep.getText().contains("印制板检测")) {
                    cmd = "71"; exCmd = "0002"; 
                } else if(makerStep.getText().contains("总检测试")) {
                    cmd = "71"; exCmd = "0003"; 
                } 
            } else if(deviceType.getText().contains("JL207A")) {
                if(makerStep.getText().contains("参数设置")) {
                    cmd = "D4"; exCmd = "0001";           
                } else if(makerStep.getText().contains("印制板检测")) {
                    cmd = "74"; exCmd = "0002"; 
                } else if(makerStep.getText().contains("总检测试")) {
                    cmd = "74"; exCmd = "0003"; 
                } 
            }
            
            String resReady = readyGo(cmd, exCmd);  // 线程启动前的准备工作
            String[][] params = new String[][]{{"cmd", cmd}, {"exCmd", exCmd}, {"code", resReady}};
            String resTask4RS232 = new ThreadRS232(stage, this).run(params); 
            if(resTask4RS232 != null && resTask4RS232.equals("Check_OK")) {
                tabMakerShowController.getTableView().scrollTo(tabMakerShowController.getTableView().getItems().size()-1); // scroll end line
                this.addLog("检测通过", null); 
                ModalDialog.showMessageType(stage, " 检 测 通 过 ", "检测结果", "/tool/box/raw/images/_16_make.png");
            }            
            
            disableUIs(false);
        }
    }    
    
    private void disableUIs(boolean flag) {
        this.btnDoOnce.setDisable(flag);    
        this.cbDoOnce.setDisable(flag); 
    }
    
    private String readyGo(String cmd, String exCmd) {  // 线程启动前的准备工作
        if(cmd.endsWith("74") && exCmd.endsWith("0003")) {  
            String code = ModalDialog.showInputType(stage, "", "请扫描待检品二维码", "/tool/box/raw/images/_16_make.png");
            if(code != null && !code.equals("") && code.length()==31 && code.toUpperCase().contains("JL207A")) {
                this.addLog("二维码：" + code, "二维码：" + code); 
                return code; // C000361JL207A201606231606012005
            } else {
                this.addLog("二维码内容异常，请确认扫码枪...", "二维码：" + code);            
            }
        }
        return "";
    }
    
    private void chooseFilePath() {
        this.clearHistoryRecord(); 
        
        try {
            String[] res = (new ModalDialog()).showInputCombobox(stage, new String[][] {{"请选择产品型号",  "JL207A", "CG901A"}, {"请选择生产工艺",  "印制板检测", "参数设置", "总检测试"}}, "生产检测选择操作目标",  "/tool/box/raw/images/_16_make.png");
            
            if (res[0] == null || res[1] == null) {
                this.addLog("取消了选择文件资源得操作", null);
            } else {
                try {
                    // 读取用户选择文件目录
                    String dirPath = LocalStorage.getPropery("OptionDirPath", System.getProperty("user.home"));
                    // 选择目标文件
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.setTitle("选择资源路径");
                    fileChooser.setInitialDirectory(new File(dirPath));
                    fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Excel文档", "*.xlsx;*.xls"));

                    String[][] params = new String[][] {{"cmd", res[0]},{"subCmd", res[1]}, {"user", FXMLLoginController.login.getName()}};
                    File fileTmp = fileChooser.showOpenDialog(stage);
                    if (fileTmp != null) {
                        if(!fileTmp.renameTo(fileTmp)){  
                            this.addLog("文件已经被打开，请先关闭......", null);
                        } else {    // Tab addInfo excel content by dynamic thread 
                            LocalStorage.setPropery("OptionDirPath", fileTmp.getParent());// 记录用户选择配置文件目录路径
                            LocalStorage.setPropery("OptionFilePath", fileTmp.getAbsolutePath());// 记录用户选择配置文件路径
                            LocalStorage.setPropery("OptionFilePathType", res[0]);
                            LocalStorage.setPropery("OptionFilePathOption", res[1]);

                            if(res[0].contains("CG901A") && res[1].contains("参数设置")) {
                                new ThreadSetParams4CG901A(this, fileTmp).run("", params, 1);                               
                            } else if(res[0].contains("CG901A") && res[1].contains("印制板检测")) {
                                new ThreadCheck4CG901A(this, fileTmp).run("PCB", params, 1);                            
                            } else if(res[0].contains("CG901A") && res[1].contains("总检测试")) {
                                new ThreadCheck4CG901A(this, fileTmp).run("TOTAL", params, 1);
                            } else if(res[0].contains("JL207A") && res[1].contains("参数设置")) {
                                new ThreadSetParams4JL207A(this, fileTmp).run("", params, 1); 
                            } else if(res[0].contains("JL207A") && res[1].contains("印制板检测")) {
                                new ThreadCheck4JL207A(this, fileTmp).run("PCB", params, 1);                             
                            } else if(res[0].contains("JL207A") && res[1].contains("总检测试")) {
                                new ThreadCheck4JL207A(this, fileTmp).run("TOTAL", params, 1); 
                            }
                        }
                    }                    
                } catch (Exception exception) {
                    LocalStorage.setPropery("OptionDirPath", System.getProperty("user.home"));// 记录用户选择配置文件目录路径
                    LocalStorage.setPropery("OptionFilePath", null);// 记录用户选择配置文件路径
                }
            }            
        } catch (IOException ex) {
            Logger.getLogger(TabMakerController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void showListExcel(String model, String step, List<List<Object>> list) {
        if(this.tabMakerShowController != null) {
            this.tabMakerShowController.addTabContent(model, step, list); 
        }
    }
    
    private void clearHistoryRecord() {
        disableUIs(true);

        this.fileChoose.setText("请选择资源路径"); // update_setParams_CG901A UIs
        this.deviceType.setText("");
        this.makerStep.setText("");
        this.used.setText("0");
        this.noUse.setText("0");
        this.total.setText("0");
    }
    
    public void setLeftTip(String tip) {
        if(tip == null) {
            System.out.println("tip is null!");
        } else {
            leftTip.setText(tip); 
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
    
    public void setTabMakerShowController(TabMakerShowController tabMakerShowController) {
        if(tabMakerShowController == null) {
            System.out.println("tabMakerShowController is null");
        }else {
            this.tabMakerShowController = tabMakerShowController;
        }
    }
    
    public List<List<Object>> getListExcel() {
        return listExcel;
    }
    
    public void setListExcel(List<List<Object>> listExcel) {
        if(listExcel == null) {
            System.out.println("listExcel is null!");
        } else {
            this.listExcel = listExcel;
        }
    }    
    
    public List<List<Object>> getListExcelDefault() {
        return listExcelDefault;
    }    
    
    public void setListExcelDefault(List<List<Object>> listExcelDefault) {
        if(listExcelDefault == null) {
            System.out.println("listExcel is null!");
        } else {
            this.listExcelDefault = listExcelDefault;
        }         
    }       

    public SysLogController getSysLogController() {
        return sysLogController;
    }
    
    public PopWindow getPopWindow() {
        return this.popWindow;
    }    

    public Label getUsed() {
        return used;
    }

    public Label getNoUse() {
        return noUse;
    }

    public TabMakerShowController getTabMakerShowController() {
        return tabMakerShowController;
    }

    public Stage getStage() {
        return stage;
    }

    public Login getLogin() {
        return login;
    }

    public Label getLeftTip() {
        return leftTip;
    }

    public Label getFileChoose() {
        return fileChoose;
    }

    public Label getDeviceType() {
        return deviceType;
    }

    public Label getMakerStep() {
        return makerStep;
    }

    public Label getTotal() {
        return total;
    }

    public Button getBtnDoOnce() {
        return btnDoOnce;
    }
    public CheckBox getCbDoOnce() {
        return cbDoOnce;
    }    
    
    public void addLog(String sysLog, String popLog) {
        if(this.sysLogController != null && this.popWindow != null) {
            this.sysLogController.addInfo(sysLog); 
            this.popWindow.addInfo(popLog);            
        } else {
            System.out.println("sysLogController || popWindow is null");
        }        
    }
    
    public void clearLog(boolean sysLog, boolean popLog) {
        if(sysLog && sysLogController != null) {
            this.sysLogController.clearInfo(); 
        }
        if(popLog && popWindow != null) {
            this.popWindow.clearInfo();
        }
    }    
    
    public String[] getValidExcelRow(String imsi) {
        String[] excelRow = new String[11]; 
        for (int i = 0; i < listExcel.size(); i++) {
            List<Object> tmpList = listExcel.get(i);
            if(imsi.contains(tmpList.get(FileSetParams4CG901A.DateIndex-1).toString())) {
                addLog(null, "找到匹配的IMSI");
                if(tmpList.get(FileSetParams4CG901A.DateIndex).equals("-")) {
                    for (int j = 0; j < excelRow.length; j++) {
                        excelRow[j] = tmpList.get(j).toString().trim();
                    }

                    if(excelRow[1].length() != 10 || excelRow[4].length() != 16 || excelRow[5].length() != 16 || excelRow[6].length() != 4 || excelRow[7].length() != 15) {
                        addLog("Excel 内容长度异常，请确认", excelRow[1] + "/" + excelRow[2] + "/" + excelRow[3] + "/" + excelRow[4] + "/" + excelRow[5] + "/" + excelRow[6]);
                        return null;
                    } else {
                        return excelRow;
                    }                    
                } else {
                    addLog("IMSI已经被设置，请确认", null);
                    return null;
                }
            }
        }
        return null;
    }
    
    public String[] getValidExcelRow() {
        String[] excelRow = new String[11]; 
        for (int i = 0; i < listExcel.size(); i++) {
            List<Object> tmpList = listExcel.get(i);
            
            if(tmpList.get(FileSetParams4JL207A.DateIndex).equals("-")) {
                for (int j = 0; j < excelRow.length; j++) {
                    excelRow[j] = tmpList.get(j).toString();
                }

                if(excelRow[1].length() != 10 || excelRow[2].length() != 11 || excelRow[3].length() > 5 || excelRow[4].length() > 32 || excelRow[5].length() > 5 || excelRow[6].length() != 4 || excelRow[7].length() > 10) {
                    addLog("Excel 内容长度异常，请确认", excelRow[1] + "/" + excelRow[2] + "/" + excelRow[3] + "/" + excelRow[4] + "/" + excelRow[5] + "/" + excelRow[6]);
                    return null;
                } else {
                    return excelRow;
                }                    
            } 
        }
        return null;
    }    
  
    
}
