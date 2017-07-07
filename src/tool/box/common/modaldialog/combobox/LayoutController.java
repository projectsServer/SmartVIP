package tool.box.common.modaldialog.combobox;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LayoutController implements Initializable  {
    private String[] resStrArray ;
    private ComboBox comboBox[];
    private Stage stage;
    
    @FXML private VBox centerArea;
    @FXML private Label tipBottom;
    @FXML private Button noBtn;
    
    @FXML
    private void onMouseClicked(MouseEvent event) {
        switch (((Button) event.getSource()).getId()) {
            case "yesBtn": 
                for (int i = 0; i < resStrArray.length; i++) {
                    if(comboBox == null || comboBox[i].getSelectionModel().getSelectedItem() == null) {
                        tipBottom.setText("输入内容存在未输入情况，请确认..."); 
                        break;                        
                    } else {
                        resStrArray[i] = comboBox[i].getSelectionModel().getSelectedItem().toString().trim();
                        if(i == resStrArray.length-1) {
                            ((Stage) ((Node) event.getSource()).getScene().getWindow()).close(); // close parent scene
                        }                        
                    }
                }
                break;
            case "noBtn":
                resStrArray[0] = null;
                ((Stage) ((Node) event.getSource()).getScene().getWindow()).close(); // close parent scene
                break;
            default: break;
        }
        
    }    
    
    @Override
    public void initialize(URL location, ResourceBundle resources) { // url=xx.fxml文件   rb=xx.properties文件
    }    
    
    
    
    public void setParams(Stage stage, String[][] tipAndItems) {
        resStrArray = new String[tipAndItems.length];
        
        this.stage = stage;
                
        comboBox = new ComboBox[tipAndItems.length];
        for(int i=0; i<comboBox.length; i++) {
            comboBox[i] = new ComboBox();
            comboBox[i].setPromptText(tipAndItems[i][0]);
            
            for(int j=1; j<tipAndItems[i].length; j++) {
                comboBox[i].getItems().add(tipAndItems[i][j]);
            }
            
            comboBox[i].setPrefSize(250, 30);
        }       
        centerArea.getChildren().addAll(comboBox);
    }
    
    public String[] getResStrArray() {
        return resStrArray;
    }
}
