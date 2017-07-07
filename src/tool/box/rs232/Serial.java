package tool.box.rs232;

import tool.box.rs232.diy.Protocol;
import gnu.io.CommPortIdentifier;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.ResourceBundle;
import javafx.stage.Stage;
import smartvip.FXMLLoginController;
import smartvip.mainStage.HostController;
import tool.box.common.LocalStorage;
import tool.box.rs232.option.SettingInput;

public final class Serial {
    private SerialSetting serialSetting;
    private Protocol dataProtocol;
    private SerialRxtx rxtx;
    private Stage stage;
    private ResourceBundle rb;
    private HostController hostController;
    
    String[] serialParams = new String[]{"SerialBaud", "SerialDatabits", "SerialStopbits", "SerialParity"};
    
    private final List<CommPortIdentifier> portsList; 
    private int status = 0;   // 0 = 无可用串口    2 = 关闭状态   -1 = 被占用
    
    public Serial(ResourceBundle rb, HostController hostController) {
        this.portsList = this.getAllPorts();
        this.dataProtocol = new Protocol();
        this.rb = rb;
        this.hostController = hostController;
        
        String [] params = this.readDefaultSetting();
        this.serialSetting = new SerialSetting(Integer.parseInt(params[0]), Integer.parseInt(params[1]), Integer.parseInt(params[2]), Integer.parseInt(params[3]), portsList);
    }

    
    private String[] readDefaultSetting() {
        String[] paramsDefault = new String[]{rb.getString("SerialBaud"), rb.getString("SerialDatabits"), rb.getString("SerialStopbits"), rb.getString("SerialParity")};
        
        File file = new File(LocalStorage.PropertiesPath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {}
        }
        return LocalStorage.getProperies(serialParams, paramsDefault);
    }    
    public void writeDefaultSetting(String baud, String dataBits, String stopBits, String parity) {
        String[] values = new String[]{baud, dataBits, stopBits, parity};
        
        LocalStorage.setProperies(serialParams, values);
        
        this.serialSetting.setBaudRate(baud);
        this.serialSetting.setDatabits(dataBits);
        this.serialSetting.setStopbits(stopBits);
        this.serialSetting.setParity(parity);
    }    
    
    public List<CommPortIdentifier> getAllPorts() {
        List<CommPortIdentifier> list = new ArrayList();
        Enumeration portList = CommPortIdentifier.getPortIdentifiers();

        while (portList.hasMoreElements()) {
            CommPortIdentifier port = (CommPortIdentifier) portList.nextElement();
            if (port.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                list.add(port);
            }
        }
        System.out.println("valid serial port number: " + list.size());
//        this.hostController.addLog(null, "valid serial port number: " + list.size());
        return list;
    }   
    
    // 2 = close; 1 = ok, 0 = false, -1 = using, 
    public int open(String portName, int baud, int dataBits, int stopBits, int parity, int mode)  {
        if(!this.portsList.isEmpty()) {
            
            if(this.rxtx == null) { // 避免创建两个rxtx，在关闭时不能彻底关闭串口
                this.rxtx = new SerialRxtx(this);
            }
            
            switch(mode) {
                case 0: // 打开默认第一个串口
                    this.status = this.rxtx.open(portsList.get(0).getName(), baud, dataBits, stopBits, parity);
                    serialSetting.setSelectedPortName(portsList.get(0).getName());
                    break;                
                case 1: // 按参数打开指定的串口
                    this.status = this.rxtx.open(portName, baud, dataBits, stopBits, parity);
                    serialSetting.setSelectedPortName(portName);
                    break;                
                case 2: // 弹出参数设置页，按设置的参数打开串口
                    String[] params = SettingInput.onShow(stage, serialSetting, "【串口参数设置】", "/tool/box/raw/images/_16_port_option.png");
                    if(params != null) {
                        try {
                            this.rxtx.closePort();
                            this.status = this.rxtx.open(params[0], Integer.parseInt(params[1]), Integer.parseInt(params[2]), Integer.parseInt(params[3]), Integer.parseInt(params[4]));
                            serialSetting.setSelectedPortName(params[0]);
                        } catch (NumberFormatException e) {
                            this.status = this.rxtx.open(portsList.get(0).getName(), baud, dataBits, stopBits, parity);
                            serialSetting.setSelectedPortName(portsList.get(0).getName());
                            this.hostController.addLog("串口参数不为整数，请确认重新输入", null); 
                        }
                    } 
                    break;
            }
            return this.status;
        } else {
            return (this.status = 0);
        }
    }
    

    public SerialSetting getSerialSetting() {
        return serialSetting;
    }

    public Protocol getDataProtocol() {
        return dataProtocol;
    }

    public List<CommPortIdentifier> getPortsList() {
        return portsList;
    }

    public SerialRxtx getRxtx() {
        return rxtx;
    }

    public int getStatus() {
        return status;
    }
    
    public void setStatus(int flag) {
        status = flag;
    }
    
    public void setStage(Stage stage) {
        if(stage == null) {
            System.out.println("setStage is null!");
        } else {
            this.stage = stage;
        }
    }
    
    public HostController getHostController() {
        return this.hostController;
    } 

}
