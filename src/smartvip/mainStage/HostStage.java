package smartvip.mainStage;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class HostStage extends Stage {
    
    public HostStage() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Host.fxml"), ResourceBundle.getBundle("tool.box.raw.property.global", Locale.getDefault()));// 不要放到fxml文件中，未来可做国际化 
        Parent root = (Parent)loader.load(); // Parent root = FXMLLoader.load(getClass().getResource("loginStage/FXMLLogin.fxml"), ResourceBundle.getBundle("raw.property.global", Locale.getDefault()));   
        HostController myController = loader.getController();
        myController.setStage(this);

        Scene scene = new Scene(root);
        scene.getStylesheets().add("/smartvip/mainStage/Host.css");    // 不要放到fxml文件中，未来可做更换皮肤      
        
//        stage.initStyle(StageStyle.UNIFIED); // 标题栏的风格 DECORATED、UNDECORATED、TRANSPARENT、UNIFIED、UTILITY 
        this.getIcons().add(new Image(this.getClass().getResourceAsStream("/tool/box/raw/images/logo.png")));
        this.setResizable(true);
        this.setMaximized(true);
        this.setTitle("SmartVIP 智能车载信息综合平台");
        this.setScene(scene);
        this.setOnCloseRequest((WindowEvent event) -> { 
            if(HostController.serial.getRxtx() != null) {
                HostController.serial.getRxtx().closePort();
            }
            myController.getPopWindow().exit();
            this.close();
            System.out.println("系统关闭中......");
        }); 
        this.show();   
    }
}
