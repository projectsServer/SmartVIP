package smartvip.mainStage.center.leftTopControl.rd;

import java.io.File;
import smartvip.mainStage.bottom.*;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import smartvip.mainStage.center.leftBottomLog.SysLogController;
import smartvip.mainStage.center.rightShow.rd.TabRDShowController;
import tool.box.popwindow.PopWindow;
import tool.box.poi.excel.File4ModelOption;

public class TabRDController implements Initializable  {
    private TabRDShowController tabRDShowController;
    private SysLogController sysLogController;
    private PopWindow popWindow;
    private Stage stage;
    private Toggle tgSelected;
    
    @FXML private GridPane gridPaneRDOption;
    @FXML private ToggleGroup faeSeleckGroup = new ToggleGroup();    
    
     @Override
    public void initialize(URL location, ResourceBundle resources) { // url=xx.fxml文件   rb=xx.properties文件
        String resParseFile = File4ModelOption.parseFile(new File(resources.getString("SmartVIP_file")), 0 , "ModelOption");
        if(resParseFile.contains("ok")) {  // 读配置文件，决定布局的内容
            this.addUis2Pane(File4ModelOption.listExcel);
        } else {
            System.out.println(resParseFile);
        }        
    }   
    
    private void addUis2Pane(List<List<Object>> listExcel) {
        for(int i=1; i<listExcel.size(); i++) {
            for(int j=0; j<listExcel.get(i).size(); j++) {
                if(j == 0) {
                    Label tip = new Label(listExcel.get(i).get(j).toString());
                    tip.setStyle("-fx-font-size: 11pt; -fx-font-family: \"Segoe UI Semibold\"; -fx-graphic:url(/tool/box/raw/images/_16_node_1.png); -fx-padding:5 20 5 10; ");
                    this.gridPaneRDOption.add(tip, j, i-1); // obj col row 
                } else {
                    RadioButton rdBtn = new RadioButton(listExcel.get(i).get(j).toString());
                    rdBtn.setSelected((i == 1));
                    rdBtn.setStyle("-fx-font-size: 10.5pt;  -fx-font-family: \"Segoe UI Semibold\"; "); 
                    rdBtn.setUserData(listExcel.get(i).get(j).toString()); 
                    rdBtn.setToggleGroup(faeSeleckGroup);
                    this.gridPaneRDOption.add(rdBtn, j, i-1); // obj col row
                }
            }
        }
    }    
    

}
