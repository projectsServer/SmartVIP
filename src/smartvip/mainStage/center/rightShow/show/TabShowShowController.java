package smartvip.mainStage.center.rightShow.show;

import smartvip.mainStage.bottom.*;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

public class TabShowShowController implements Initializable  {
    @FXML private Label leftTip;
    
     @Override
    public void initialize(URL location, ResourceBundle resources) { // url=xx.fxml文件   rb=xx.properties文件
    }    
    
    public void setLeftTip(String tip) {
        leftTip.setText(tip); 
    }
}
