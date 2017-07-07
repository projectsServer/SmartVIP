package smartvip.mainStage.center.rightShow.rd;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

public class TabRDShowController implements Initializable  {
    @FXML private Label leftTip;
    
    @FXML private Label steer_ok;
    @FXML private Label steer_up;
    @FXML private Label steer_right;
    @FXML private Label steer_down;
    @FXML private Label steer_left;
    @FXML private Label steer_M;
    @FXML private Label steer_handon;
    @FXML private Label steer_handup;
    @FXML private Label steer_navi;
    @FXML private Label steer_OFF;
    @FXML private Label steer_RES;
    @FXML private Label steer_add;
    @FXML private Label steer_reduce;
    @FXML private Label steer_LIM;

    
     @Override
    public void initialize(URL location, ResourceBundle resources) { // url=xx.fxml文件   rb=xx.properties文件
    }    
    
    @FXML
    private void onMouseClicked(MouseEvent event) {
        switch (((Label) event.getSource()).getId()) {
            case "steer_ok": break;
            case "steer_up": break;
            case "steer_right": break;
            case "steer_down": break;
            case "steer_left": break;
            case "steer_M": break;                     
            case "steer_handon": break;
            case "steer_handup": break;
            case "steer_navi": break;
            case "steer_OFF": break;
            case "steer_RES": break;
            case "steer_add": break;
            case "steer_reduce": break;
            case "steer_LIM": break;                      
            default: break;
        }
    }
    
    
    public void setLeftTip(String tip) {
        leftTip.setText(tip); 
    }
}
