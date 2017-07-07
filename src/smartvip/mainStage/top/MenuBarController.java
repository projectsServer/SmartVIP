package smartvip.mainStage.top;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Pagination;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import smartvip.FXMLLoginController;
import smartvip.mainStage.HostController;
import static smartvip.mainStage.HostController.serial;
import smartvip.mainStage.bottom.StatusBarController;
import smartvip.mainStage.center.leftBottomLog.SysLogController;
import tool.box.popwindow.PopWindow;
import tool.box.login.Login;

public class MenuBarController implements Initializable  {
//    private String userRolePermission;
    private Login login;
    private PopWindow popWindow;
    private SysLogController sysLogController;
    private StatusBarController statusBarController;
    
    private Stage stage;
//    private ResourceBundle rb;
//    private PopWindow popWindow;
//    private int serialStatus = 0;
    private String[] fonts = new String[]{};
//    
//    @FXML private Label bottomRightTip;
//    @FXML private TextArea textArea;
//    @FXML private RadioButton rememberMe, forgotMe;
//    @FXML private TextField name; 
//    @FXML private PasswordField password;
//    @FXML private Label tip, option, head;
//    @FXML private ContextMenu contextMenu4option;  
        @Override
    public void initialize(URL location, ResourceBundle resources) { // url=xx.fxml文件   rb=xx.properties文件
//        this.rb = rb;
//        
//        Serial serial = new Serial(); // serial port init
//        String comName = "";
//        if(serial.GetPortCount() > 0) {
//            serialStatus = serial.open((serial.getPortIds())[0].getName(), 115200, 8, 1, SerialPort.PARITY_NONE);
//            comName = (serial.getPortIds())[0].getName();
//        } 
//        
//        // popWind show exception
//        String contents = "";  
//        String strBottomRightTip = "";   
//        switch (serialStatus) {
//            case -1: contents = comName + "已被其他软件占用，请确认！\r\n"; strBottomRightTip = comName + ":" + rb.getString("SerialBusy"); break;
//            case 0: contents = "本计算机无可用串口资源，请确认！\r\n"; strBottomRightTip = rb.getString("SerialNull"); break;
//            default: strBottomRightTip = comName + ":" + rb.getString("SerialOK"); break;
//        }
//        
//        bottomRightTip.setText(strBottomRightTip);
//        
//        popWindow = new PopWindow("软件启动异常信息", contents);
//        popWindow.show();         
    }    
    
    @FXML
    private void onCheckMenuItemClicked(ActionEvent event) {
        CheckMenuItem item = (CheckMenuItem) event.getSource();
        switch (item.getId()) {
            case "sysLogFun": sysLogFun(item.isSelected()); break;
        }
    }
    
    @FXML 
    private void onMenuItemClicked (ActionEvent event) {
        MenuItem item = (MenuItem) event.getSource();
        switch (item.getId()) {
            case "closeSerial": closeSerial(); break;    
            case "openSerial": openSerial(); break;   
            case "setSerial": setSerial(); break;            
            case "setAddFun": setAddressFunction(); break; 
            case "pwdFun": changePW(); break; 
            case "noteFun": noteFun(); break;  
            case "exitFun": sysExit(); break;
            default: break;
        }
    }
    
    private void sysLogFun(boolean isShow) {
        if(isShow) {
            popWindow.show();
        } else {
            popWindow.hide();
        }        
    }
    private void noteFun() {
        showMenu4note("tool/box/raw/images/_16_task.png", "操作手册");
    }
    private void changePW() {
        String[] res = tool.box.common.ModalDialog.showInputPasswordType(stage, "修改用户密码", new String[][] {{"","请输入原始密码"}, {"","请再输入原始密码"}, {"","请输入新密码"}}, "/tool/box/raw/images/_16_lock.png");
        if(res == null || res[0].equals("CANCEL") || res[0].equals("")) {
        } else {
//                String resJson = getRemoteServerJson(); // 远程服务器——更新用户密码
        }        
    }
    private void setAddressFunction() {
        String[] res = tool.box.common.ModalDialog.showInputType(stage, new String[][] {{"", "请输入服务器地址"}}, "设置远程服务器地址",  "/tool/box/raw/images/_16_database.png");
        if(res == null || res[0].equals("CANCEL") || res[0].equals("")) {
        } else {
            this.login.writeCookie("ServerAdd", res[0]);
        }        
    }
    private void closeSerial() {
        if(HostController.serial.getStatus() > 0) {
            HostController.serial.setStatus(2);
            HostController.serial.getRxtx().closePort();
            addLog(serial.getSerialSetting().getSelectedPortName() + " 已关闭", null); 
            this.statusBarController.setLeftTip(this.login.getName() + "  &  " + serial.getSerialSetting().getSelectedPortName() + " 已关闭");
        } else {
            this.addLog("无有效串口，不需关闭", null);
        }        
    }
    private void openSerial() {
        if(HostController.serial.getStatus() > 0) {
            if(HostController.serial.getStatus() == 2) {
                HostController.serial.setStatus(1);
                HostController.serial.open(HostController.serial.getSerialSetting().getSelectedPortName(), 
                        HostController.serial.getSerialSetting().getBaudRate(), 
                        HostController.serial.getSerialSetting().getDatabits(), 
                        HostController.serial.getSerialSetting().getStopbits(), 
                        HostController.serial.getSerialSetting().getParity(), 
                        1);
                this.statusBarController.setLeftTip(FXMLLoginController.login.getName() + "  &  " + serial.getSerialSetting().getParams()); // 底部状态栏左侧内容
            } else {
                this.addLog("串口已打开，不需重复打开", null);
            }
        } else {
            this.addLog("无有效串口，无法打开", null);
        }         
    }
    private void setSerial() {
        if(HostController.serial.getStatus() > 0) {
            HostController.serial.open(HostController.serial.getSerialSetting().getSelectedPortName(), 
                        HostController.serial.getSerialSetting().getBaudRate(), 
                        HostController.serial.getSerialSetting().getDatabits(), 
                        HostController.serial.getSerialSetting().getStopbits(), 
                        HostController.serial.getSerialSetting().getParity(), 
                        2);
            this.statusBarController.setLeftTip(FXMLLoginController.login.getName() + "  &  " + serial.getSerialSetting().getParams()); // 底部状态栏左侧内容
        } else {
            this.addLog("无有效串口，无法设置", null);
        }         
    }
    private void sysExit() {
        if(HostController.serial.getRxtx() != null) {
            HostController.serial.getRxtx().closePort();
        }
        this.stage.close(); 
        this.popWindow.exit();
        System.out.println("系统关闭中......");        
    }
    
    
//    @FXML
//    private void onLoginAction(Event event) throws IOException {
//        loginAction(event);
//    }
//    
//    @FXML
//    private void onMouseClicked(MouseEvent event) {
//        switch (((Label) event.getSource()).getId()) {
//            case "head": changeHeadIcon(); break;
//            case "option": contextMenu4option.show(option, Side.RIGHT, 5, 5); break;
//            default: break;
//        }
//        
//    }
//
//    @FXML
//    private void onMouseEntered(MouseEvent event) {
//        switch (((Label) event.getSource()).getId()) {
//            case "head": head.setScaleX(1.05); head.setScaleY(1.05); break;
//            case "option": option.setScaleX(1.1); option.setScaleY(1.1); break;
//            default: break;
//        }
//    }
//
//    @FXML
//    private void onMouseExited(MouseEvent event) {
//        switch (((Label) event.getSource()).getId()) {
//            case "head": head.setScaleX(1.0); head.setScaleY(1.0); break;
//            case "option": option.setScaleX(1.0); option.setScaleY(1.0); break;
//            default: break;
//        }
//    }
//
//    @FXML
//    private void onAction4optionContextMenu(ActionEvent event) {
//        switch (((MenuItem) event.getSource()).getId()) {
//            case "newUser": MyModalDialog.showMessageType(stage, "【用户注册】功能敬请期待！", "【用户注册】", "/tool/box/raw/images/_16_user.png"); break;
//            case "changePW": MyModalDialog.showMessageType(stage, "【更改密码】功能敬请期待！", "【更改密码】", "/tool/box/raw/images/_16_lock.png"); break; 
//            case "systemSetting":
//                String[] res = MyModalDialog.showInputType(stage, new String[][]{{rb.getString("DefaultServerAdd"), "请输入服务器地址"}}, "【系统设置】", "/tool/box/raw/images/_16_database.png");
//                if (res[0].equals("CANCEL")) {
//                    tip.setText("取消设置操作......");
//                } else {
//                    login.setServerAdd(res[0]);
//                }
//                break;
//            case "systemReset":
//                login.reset();
//                tip.setText("恢复默认值......");
//                break;
//            case "declear": MyModalDialog.showMessageType(stage, rb.getString("Declaration"), "【免责声明】", "/tool/box/raw/images/_16_message.png"); break;
//            default: break;
//        }
//    }
//
//
//    
//    private void loginAction(Event event) throws IOException {
//        String[] res;
//        switch (event.getEventType().toString()) {
//            case "KEY_PRESSED":
//                if (((KeyEvent) event).getCode() == KeyCode.ENTER) {
//                    break;
//                }
//                return;
//            case "MOUSE_CLICKED": break;
//                default: break;
//            }
//            
//            res = login.checkValid(this.name.getText().trim(), this.password.getText().trim());
//            if (!res[0].equals("OK")) {
//                tip.setText(res[1]);
//            } else {
//                if (this.rememberMe.isSelected()) {
//                    this.login.writeCookies(this.name.getText().trim(), this.password.getText().trim(), String.valueOf(this.rememberMe.isSelected()));
//                } else {
//                    this.login.writeCookies("", "", "false");
//                }             
//
//                // 远程身份验证
////                String resJson = LoginTaskDialog.showProgressDialog(stage, 1);// 远程身份验证 5 秒超时
//                String[][] params = new String[][] {{"userName", this.name.getText()},{"userPW", this.password.getText()}};
//                String resJson = new CircleDialog(stage).run("login", params, 1);// 远程身份验证 1 秒超时
//                if (!resJson.equals("NULL")) {
//                    tip.setText("断网或者服务暂停，请联系管理员...");
//                } else {
//                    GlobalVars.RolePermission = "admin"; // 从 resJson 中解析出来权限
//                    stage.close();
//                    HostStage mainStage = new HostStage(GlobalVars.RolePermission, this.login);
//                }
//            }            
//    }
//    
//    private void changeHeadIcon() {
//        
//        try {
//            FileChooser fileChooser = new FileChooser();
//            fileChooser.setTitle("选择作为头像图片");
//            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
//            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("All Images", "*.jpg;*.png"));
//
//            File fileTmp = fileChooser.showOpenDialog(stage);
//            if (fileTmp != null) {
//                ChangeImage.resizePNG(fileTmp.getAbsolutePath(), System.getProperty("user.home") + "\\SmartTMHead.tmp", 128, 128, false); //改图片的大小
//                File tmpImage = new File(System.getProperty("user.home") + "\\SmartTMHead.tmp"); //加圆角
//
//                String headIconName = login.getName() != null ? login.getName() : "SmartIPHead";
//                File headIcon = new File(System.getProperty("user.home") + "\\" + headIconName + ".png");
//
//                BufferedImage icon = ImageIO.read(tmpImage);
//                BufferedImage rounded = ChangeImage.makeRoundedCorner(icon, 180);
//                ImageIO.write(rounded, "png", headIcon);
//                tmpImage.delete();
//
//                head.setGraphic(new ImageView(new Image(headIcon.toURI().toURL().toString(), true)));
//                login.setHeadIcon(headIcon.getAbsolutePath()); // 同时更新Cookie中的值
//
//                tip.setText("头像处理成功");
//            }
//        } catch (IOException ex) {
//            Logger.getLogger(MenuBarController.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
//
//    private void enabelTabKey() {
//        this.name.setFocusTraversable(true);
//        this.password.setFocusTraversable(true);
//        this.rememberMe.setFocusTraversable(true);
//        this.forgotMe.setFocusTraversable(true);
//    }
//
    public void setStage(Stage stage) {
        if(stage == null) {
            System.out.println("setStage is null!");
        } else {
            this.stage = stage;
        }
    }
//    public void setUserRolePermission(String userRolePermission) {
//        this.userRolePermission = userRolePermission;
//    }
//    
    
    private void showMenu4note(String iconUrl, String title) {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.WINDOW_MODAL);
        VBox vbox = new VBox();
        vbox.getChildren().add(addMenu4note());
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(5));
        dialogStage.setScene(new Scene(vbox));
        dialogStage.setTitle(title);
        dialogStage.getIcons().add(new Image(iconUrl));
        dialogStage.show();
    }    
    private Node addMenu4note() {
        Pagination pagination;
        int itemsPerPage = 10; // 每页包含的项目数        
        
        fonts = Font.getFamilies().toArray(fonts);
        
        pagination = new Pagination(fonts.length/itemsPerPage, 0);
        pagination.getStyleClass().add(Pagination.STYLE_CLASS_BULLET);       
        pagination.setPageFactory(new Callback<Integer, Node>() {
            @Override
            public Node call(Integer pageIndex) { // 这是第 pageIndex 页
                VBox box = new VBox(5);
                int page = pageIndex * itemsPerPage;
                for (int i = page; i < page + itemsPerPage; i++) {
                    Label font = new Label(fonts[i]);
                    box.getChildren().add(font);
                }
                return box;                
            }
        });

        AnchorPane anchor = new AnchorPane();
        AnchorPane.setTopAnchor(pagination, 10.0);
        AnchorPane.setRightAnchor(pagination, 10.0);
        AnchorPane.setBottomAnchor(pagination, 10.0);
        AnchorPane.setLeftAnchor(pagination, 10.0);
        anchor.getChildren().addAll(pagination);   
        
        return anchor;        
    }    
    
    public void setPopWindow(PopWindow popWindow) {
        if(popWindow == null) {
            System.out.println("popWindow is null");
        } else {
            this.popWindow = popWindow;
        }        
    }
    
    public void setSysLogController (SysLogController sysLogController) {
        if(sysLogController == null) {
            System.out.println("sysLogController is null");
        } else {
            this.sysLogController = sysLogController;
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
    
    public void setStatusBarController(StatusBarController statusBarController) {
        this.statusBarController = statusBarController;
    }
    
    public void setLogin(Login login) {
        
        this.login = login;
    }



}
