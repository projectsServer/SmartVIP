package tool.box.rs232.option;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import tool.box.rs232.SerialSetting;

public class SettingInputController implements Initializable  {
    private String[] resStrArray = null;
    
    @FXML private ComboBox portName;
    @FXML private ComboBox portBaud;
    @FXML private ComboBox portDataBits;
    @FXML private ComboBox portStopBits;
    @FXML private ComboBox portParity;

    
    @FXML
    private void onMouseClicked(MouseEvent event) {
        switch (((Button) event.getSource()).getId()) {
            case "yesBtn": 
                resStrArray = new String[5];
                resStrArray[0] = portName.getSelectionModel().getSelectedItem().toString().trim();
                resStrArray[1] = portBaud.getSelectionModel().getSelectedItem().toString().trim();
                resStrArray[2] = portDataBits.getSelectionModel().getSelectedItem().toString().trim();
                resStrArray[3] = portStopBits.getSelectionModel().getSelectedItem().toString().trim();
                
                String parity = portParity.getSelectionModel().getSelectedItem().toString().trim();
                if(parity.equals("None")) {
                    resStrArray[4] = String.valueOf(SerialPort.PARITY_NONE);
                } else if(parity.equals("Odd")) {
                    resStrArray[4] = String.valueOf(SerialPort.PARITY_ODD);
                } else if(parity.equals("Even")) {
                    resStrArray[4] = String.valueOf(SerialPort.PARITY_EVEN);
                } else {
                    resStrArray[4] = String.valueOf(SerialPort.PARITY_NONE);
                }
                ((Stage) ((Node) event.getSource()).getScene().getWindow()).close(); // close parent scene
                break;
            case "noBtn":
                ((Stage) ((Node) event.getSource()).getScene().getWindow()).close(); // close parent scene
                break;
            default: break;
        }
        
    }    
    
    @Override
    public void initialize(URL location, ResourceBundle resources) { // url=xx.fxml文件   rb=xx.properties文件
    }    
    
    public void setParams(SerialSetting serialSetting) {
        List<CommPortIdentifier> portsList = serialSetting.getPortsList();
        for(int i=0; i<portsList.size(); i++) {
            portName.getItems().add(portsList.get(i).getName());
        }
        portName.getSelectionModel().select(0);
        
        portBaud.getItems().addAll("115200", "9600");  
        portBaud.getSelectionModel().select((serialSetting.getBaudRate() == 115200)? 0 : 1);
        
        
        portDataBits.getItems().addAll("8", "7"); 
        portDataBits.getSelectionModel().select((serialSetting.getDatabits() == 8)? 0 : 1);
        
        portStopBits.getItems().addAll("1", "2");  
        portStopBits.getSelectionModel().select((serialSetting.getStopbits() == 1)? 0 : 1);
        
        portParity.getItems().addAll("None", "Odd", "Even"); 
        switch(serialSetting.getParity()) {
            case 0: portParity.getSelectionModel().select(0); break;
            case 1: portParity.getSelectionModel().select(1); break;
            case 2: portParity.getSelectionModel().select(2); break;
        }
        
    }
    
    public String[] getResStrArray() {
        return resStrArray;
    }
}
