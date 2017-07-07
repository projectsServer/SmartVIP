package smartvip;

import java.awt.SplashScreen;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class LoginStage extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLLogin.fxml"), ResourceBundle.getBundle("tool.box.raw.property.global", Locale.getDefault()));// 不要放到fxml文件中，未来可做国际化 
        Parent root = (Parent)loader.load(); // Parent root = FXMLLoader.load(getClass().getResource("loginStage/FXMLLogin.fxml"), ResourceBundle.getBundle("raw.property.global", Locale.getDefault()));   
        FXMLLoginController myController = loader.getController();
        myController.setStage(stage);
        
        Scene scene = new Scene(root, 16*30, 9*25);
        scene.getStylesheets().add("/smartvip/FXMLLogin.css"); // 不要放到fxml文件中，未来可做更换皮肤
        
//        stage.initStyle(StageStyle.UNIFIED); // 标题栏的风格 DECORATED、UNDECORATED、TRANSPARENT、UNIFIED、UTILITY 
        stage.getIcons().add(new Image(this.getClass().getResourceAsStream("/tool/box/raw/images/logo.png")));
        stage.setResizable(false);
        stage.setTitle("SmartVIP 用户登录");
        stage.setScene(scene);
        stage.setOnCloseRequest((WindowEvent event) -> { stage.close(); }); 
        stage.show();
        
        SplashScreen splashScreen = SplashScreen.getSplashScreen(); // 闪屏及退出闪屏
        if (splashScreen != null) {
            splashScreen.close();
        }   
    }

    /**
     * @param args the command line arguments
     */
    public static void main(java.lang.String[] args) {
        launch(args);
    }
    
}
