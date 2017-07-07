package tool.box.sms;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class SMS {

    static class Dialog extends Stage {
        
        public Dialog(String title, Stage owner, Scene scene, String iconPath) {
            this.setTitle(title);
//            initStyle( StageStyle.UNIFIED );
            this.initModality(Modality.APPLICATION_MODAL);
            this.initOwner(owner);
            this.setResizable(false);
            this.setScene(scene);
            this.getIcons().add(new Image(this.getClass().getResourceAsStream(iconPath)));
        }
        
        public void onShow() {
            sizeToScene();
            centerOnScreen();
            showAndWait();
        }
    }

    static class Message extends Text {
        public Message(String msg) {
            super(msg);
            this.setWrappingWidth(250);
            this.setStyle("-fx-font-size: 12pt;-fx-effect: dropshadow(gaussian , rgba(255,255,255,0.5) , 0, 0, 0, 1 );");
        }
    }

    public static String onShow(Stage parentStage, String title, String iconPath) { 
        try {
            FXMLLoader loader = new FXMLLoader(parentStage.getClass().getResource("/tool/box/sms/SMS.fxml"), ResourceBundle.getBundle("tool.box.raw.property.global", Locale.getDefault()));// 不要放到fxml文件中，未来可做国际化 
            Parent root = (Parent)loader.load();
            SMSController smsController = loader.getController();
            smsController.setStage(parentStage); 

            Scene scene = new Scene(root);
            scene.getStylesheets().add("/tool/box/sms/SMS.css");

            new Dialog(title, parentStage, scene, iconPath).onShow();

            return smsController.getResStrArray();  
            
        } catch (IOException ex) {
            Logger.getLogger(SMS.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }     
    
    public static boolean sendSMS(String user, String key, String[] codes, String content) {
        return false;
    }
    
}
