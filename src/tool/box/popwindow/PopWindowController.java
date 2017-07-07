package tool.box.popwindow;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import tool.box.common.Tool;

public class PopWindowController implements Initializable  {
    @FXML private TextArea logArea;
    
    private Stage stage;
    
     @Override
    public void initialize(URL location, ResourceBundle resources) { // url=xx.fxml文件   rb=xx.properties文件
    }    
    @FXML
    public void onMouseClicked4clear(MouseEvent me) {
        logArea.clear();
    }
    @FXML
    public void onMouseClicked4save(MouseEvent me) {
        String resStr = Tool.saveFile(stage, "记事本 (*.txt)", "*.txt", "SmartVIP_log_" + Tool.getTime("yyyyMMddHHmmss"), logArea.getText());
        if(resStr.startsWith("false")) {
            addInfo("短信内容未保存......");
        } else if(resStr.startsWith("error")) {
            addInfo("创建短信内容文件时出现系统异常");
        } else {
            addInfo("短信内容路径为：" + resStr);
        }
    }
    
    public void addInfo(String log) {
        if(!(log == null || log.equals(""))) {
            this.logArea.appendText(new SimpleDateFormat("HH:mm:ss").format(new Date()) + "  " + log + "\r\n");
        }
    }
    
    public void setStage(Stage stage) {
        if(stage == null) {
            System.out.println("setStage is null!");
        } else {
            this.stage = stage;
        }
    }
    
    public TextArea getTextArea() {
        return logArea;
    }
}
