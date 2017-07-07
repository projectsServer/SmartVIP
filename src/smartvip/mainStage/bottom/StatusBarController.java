package smartvip.mainStage.bottom;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

public class StatusBarController implements Initializable  {
    @FXML private Label leftTip;
    
     @Override
    public void initialize(URL location, ResourceBundle resources) { // url=xx.fxml文件   rb=xx.properties文件
        System.out.println("StatusBarController  initialize");
    }    
    
    public void setLeftTip(String tip) {
        if(tip == null) {
            System.out.println("tip is null");
        } else {
            leftTip.setText(tip); 
        }
    }
    
    public String getLeftTip() {
        return this.leftTip.getText();
    }
}
