package smartvip;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import smartvip.mainStage.HostStage;
import tool.box.common.ChangeImage;
import tool.box.common.GlobalVars;
import tool.box.common.ModalDialog;
import tool.box.login.Login;
import tool.box.uis.dialogCircle.TipCircle;
//import tool.box.uis.Dialog.TipCircle;
public class FXMLLoginController implements Initializable {
    private Stage stage;
    private ResourceBundle rb;
    public static Login login ; //  内含从Cookies中读取上次记录
    
    @FXML private RadioButton rememberMe, forgotMe;
    @FXML private TextField name; 
    @FXML private PasswordField password;
    @FXML private Label tip, option, head;
    @FXML private ContextMenu contextMenu4option;    
    
    @FXML
    private void onLoginAction(Event event) throws IOException {
        loginAction(event);
    }
    
    @FXML
    private void onMouseClicked(MouseEvent event) {
        switch (((Label) event.getSource()).getId()) {
            case "head": changeHeadIcon(); break;
            case "option": contextMenu4option.show(option, Side.RIGHT, 5, 5); break;
            default: break;
        }
    }

    @FXML
    private void onMouseEntered(MouseEvent event) {
        switch (((Label) event.getSource()).getId()) {
            case "head": head.setScaleX(1.05); head.setScaleY(1.05); break;
            case "option": option.setScaleX(1.1); option.setScaleY(1.1); break;
            default: break;
        }
    }

    @FXML
    private void onMouseExited(MouseEvent event) {
        switch (((Label) event.getSource()).getId()) {
            case "head": head.setScaleX(1.0); head.setScaleY(1.0); break;
            case "option": option.setScaleX(1.0); option.setScaleY(1.0); break;
            default: break;
        }
    }

    @FXML
    private void onAction4optionContextMenu(ActionEvent event) {
        switch (((MenuItem) event.getSource()).getId()) {
            case "newUser": ModalDialog.showMessageType(stage, "【用户注册】功能敬请期待！", "【用户注册】", "/tool/box/raw/images/_16_user.png"); break;
            case "changePW": ModalDialog.showMessageType(stage, "【更改密码】功能敬请期待！", "【更改密码】", "/tool/box/raw/images/_16_lock.png"); break; 
            case "systemSetting":
                String[] res = ModalDialog.showInputType(stage, new String[][]{{rb.getString("DefaultServerAdd"), "请输入服务器地址"}}, "【系统设置】", "/tool/box/raw/images/_16_database.png");
                if (res[0].equals("CANCEL")) {
                    tip.setText("取消设置操作......");
                } else {
                    login.setServerAdd(res[0]);
                }
                break;
            case "systemReset":
                login.reset();
                tip.setText("恢复默认值......");
                break;
            case "declear": ModalDialog.showMessageType(stage, rb.getString("Declaration"), "【免责声明】", "/tool/box/raw/images/_16_message.png"); break;
            default: break;
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) { // url=xx.fxml文件   rb=xx.properties文件
        this.rb = rb;
        login = new Login(rb);
        
        // 头像处理
        if(login.getHeadIcon().contains(this.rb.getString("DefaultHeadIcon"))) {
            head.setGraphic(new ImageView(new Image(getClass().getResourceAsStream(this.rb.getString("DefaultHeadIcon"))))); 
        } else if(!login.getName().equals("") &&  login.getHeadIcon().contains(login.getName())) {
            try {
                head.setGraphic(new ImageView(new Image(new File(login.getHeadIcon()).toURI().toURL().toString(),true)));
            } catch (MalformedURLException ex) {
                Logger.getLogger(FXMLLoginController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            head.setGraphic(new ImageView(new Image(getClass().getResourceAsStream(this.rb.getString("DefaultHeadIcon")))));            
        }       
        
        this.name.setText(login.getName()); // 设置从Cookie中的得到的默认值
        this.password.setText(login.getPassword()); // 设置从Cookie中的得到的默认值

        this.rememberMe.setSelected(Boolean.valueOf(login.getIsRememberMe())); // 设置从Cookie中的得到的默认值
        this.forgotMe.setSelected(!this.rememberMe.isSelected());
        
        this.name.addEventFilter(MouseEvent.MOUSE_CLICKED, (MouseEvent keyEvent) -> { // 使能 Tab 键
            enabelTabKey();
        });
        this.password.addEventFilter(MouseEvent.MOUSE_CLICKED, (MouseEvent keyEvent) -> { // 使能 Tab 键
            enabelTabKey();
        });        
        if (!(this.name.getText().trim().equals("") && this.password.getText().trim().equals(""))) { // 全选姓名
            enabelTabKey();
            this.name.selectAll();
        }
    }
    
    private void loginAction(Event event) throws IOException {
        String[] res;
        switch (event.getEventType().toString()) {
            case "KEY_PRESSED":
                if (((KeyEvent) event).getCode() == KeyCode.ENTER) {
                    break;
                }
                return;
            case "MOUSE_CLICKED": break;
                default: break;
            }
            
            res = login.checkValid(this.name.getText().trim(), this.password.getText().trim());
            if (!res[0].equals("OK")) {
                tip.setText(res[1]);
            } else {
                if (this.rememberMe.isSelected()) {
                    login.writeCookies(this.name.getText().trim(), this.password.getText().trim(), String.valueOf(this.rememberMe.isSelected()));
                } else {
                    login.writeCookies("", "", "false");
                }             

                // 远程身份验证
//                String resJson = LoginTaskDialog.showProgressDialog(stage, 1);// 远程身份验证 5 秒超时
                String[][] params = new String[][] {{"userName", this.name.getText()},{"userPW", this.password.getText()}};
                String resJson = new TipCircle(stage).run("login", params, 1);// 远程身份验证 1 秒超时
                if (resJson.equals("NULL")) {
                    tip.setText("断网或者服务暂停，请联系管理员...");
                } else if(resJson.contains("password")) {
                    tip.setText("密码错误，请确认...");
                } else if(resJson.contains("name")) {
                    tip.setText("用户名不存在，请联系管理管...");                    
                }else {
                    login.setName(this.name.getText());
                    GlobalVars.RolePermission = resJson; // 从 resJson 中解析出来权限
                    this.stage.close();
                    HostStage mainStage = new HostStage();
                }
            }            
    }
    
    private void changeHeadIcon() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("选择作为头像图片");
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("All Images", "*.jpg;*.png"));

            File fileTmp = fileChooser.showOpenDialog(stage);
            if (fileTmp != null) {
                ChangeImage.resizePNG(fileTmp.getAbsolutePath(), System.getProperty("user.home") + "\\SmartTMHead.tmp", 128, 128, false); //改图片的大小
                File tmpImage = new File(System.getProperty("user.home") + "\\SmartTMHead.tmp"); //加圆角

                String headIconName = login.getName() != null ? login.getName() : "SmartIPHead";
                File headIcon = new File(System.getProperty("user.home") + "\\" + headIconName + ".png");

                BufferedImage icon = ImageIO.read(tmpImage);
                BufferedImage rounded = ChangeImage.makeRoundedCorner(icon, 180);
                ImageIO.write(rounded, "png", headIcon);
                tmpImage.delete();

                head.setGraphic(new ImageView(new Image(headIcon.toURI().toURL().toString(), true)));
                login.setHeadIcon(headIcon.getAbsolutePath()); // 同时更新Cookie中的值

                tip.setText("头像处理成功");
            }
        } catch (IOException ex) {
            Logger.getLogger(FXMLLoginController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void enabelTabKey() {
        this.name.setFocusTraversable(true);
        this.password.setFocusTraversable(true);
        this.rememberMe.setFocusTraversable(true);
        this.forgotMe.setFocusTraversable(true);
    }

    public void setStage(Stage stage) {
        if(stage == null) {
            System.out.println("setStage is null!");
        } else {
            this.stage = stage;
        }
    }
}
