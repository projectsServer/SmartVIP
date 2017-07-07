package tool.box.sms;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import tool.box.common.Tool;

public class SMSController implements Initializable  {
    private String resStrArray = null;
    private File fileSMSClient = null;
    private Stage stage;
    
    @FXML private TextField tfCode;
    @FXML private TextField tfResCode;
    
    @FXML
    private void onMouseClicked(MouseEvent event) {
        if (event.getSource() instanceof Button) {
            switch (((Button) event.getSource()).getId()) {
                case "btnFile": 
                    if (tfResCode.getText().length() != 11 && !tfResCode.getText().isEmpty()) {
                        tfResCode.setText("");
                        tfResCode.setPromptText("请输入正确的11位手机号码");
                    } else {
                        resStrArray = "F:" + tfResCode.getText();
                        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close(); // close parent scene                        
                    }
//                    resStrArray = "btnFile";
                    break;
                case "btnSMS":
                    if(tfCode.getText().isEmpty() || tfCode.getText().length() < 11) {
                        tfCode.setText("");
                        tfCode.setPromptText("请输入正确的11位手机号码");
                    } else {
                        resStrArray = "S:" + tfCode.getText();
                        if(resStrArray.contains("，")) {
                            resStrArray = resStrArray.replaceAll("，", ",");
                        } 
                        
                        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close(); // close parent scene
                    }
                    break;
            }
        } else if (event.getSource() instanceof Label) {
            if (((Label) event.getSource()).getId().equals("downSMSClient")) {
                Tool.copyFile(stage, "可执行文件 (*.exe)", "*.exe", fileSMSClient);
            }
        }
    }    
    
    @Override
    public void initialize(URL location, ResourceBundle resources) { // url=xx.fxml文件   rb=xx.properties文件
        fileSMSClient = new File(resources.getString("SMSClient_file"));
    }    
    
    public void setStage(Stage stage) {
        if(stage == null) {
            System.out.println("setStage is null!");
        } else {
            this.stage = stage;
        }
    }
    
    public String getResStrArray() {
        return resStrArray;
    }
}
