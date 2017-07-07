package smartvip.mainStage.center.leftBottomLog;

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

public class SysLogController implements Initializable  {
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
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("记事本 (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.setInitialFileName("SmartVIP_log_" + Tool.getTime("yyyyMMddHHmmss")); 
        File saveFile = fileChooser.showSaveDialog(stage);
        if (saveFile == null) {
            addInfo("系统信息提示未保存");
        } else {
            try {
                saveFile.createNewFile();
                FileOutputStream fileOutputStream = null;
                fileOutputStream = new FileOutputStream(saveFile);
                fileOutputStream.write(logArea.getText().getBytes());
                fileOutputStream.close();
                addInfo("信息提示保存成功到：" + saveFile.getAbsolutePath());
            } catch (IOException ex) {
                addInfo("创建系统存储信息文件时出现系统异常");
                Logger.getLogger(SysLogController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void addInfo(String log) {
        if(!(log == null || log.equals(""))) {
            this.logArea.appendText(new SimpleDateFormat("HH:mm:ss").format(new Date()) + "  " + log + "\r\n");
        }
    }
    
    public void clearInfo() {
        logArea.clear();
    }    
    
    
    
    public void setStage(Stage stage) {
        if(stage == null) {
            System.out.println("setStage is null!");
        } else {
            this.stage = stage;
        }
    }
    
}
