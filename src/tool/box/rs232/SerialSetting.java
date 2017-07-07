package tool.box.rs232;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import java.util.List;

public class SerialSetting {
    private String selectedPortName;
    private int baudRate;
    private int flowControlIn;
    private int flowControlOut;
    private int dataBits;
    private int stopBits; 
    private int parity;
    
    private List<CommPortIdentifier> portsList;
    
    
    public SerialSetting() {
        
    }
    public SerialSetting(int baudRate, int dataBits, int stopBits, int parity, List<CommPortIdentifier> portsList) {
        this.baudRate = baudRate;
        this.dataBits = dataBits;
        this.stopBits = stopBits;
        this.parity = parity;
        
        this.portsList = portsList;
    }
    
    public void setSelectedPortName(String selectedPortName) {
	this.selectedPortName = selectedPortName;
    }

    public String getSelectedPortName() {
	return selectedPortName;
    }

    public void setBaudRate(int baudRate) {
	this.baudRate = baudRate;
    }

    public void setBaudRate(String baudRate) {
	this.baudRate = Integer.parseInt(baudRate);
    }

    public int getBaudRate() {
	return baudRate;
    }

    public String getBaudRateString() {
	return Integer.toString(baudRate);
    }

    public void setFlowControlIn(int flowControlIn) {
	this.flowControlIn = flowControlIn;
    }

    public void setFlowControlIn(String flowControlIn) {
	this.flowControlIn = stringToFlow(flowControlIn);
    }

    public int getFlowControlIn() {
	return flowControlIn;
    }

    public String getFlowControlInString() {
	return flowToString(flowControlIn);
    }

    public void setFlowControlOut(int flowControlOut) {
	this.flowControlOut = flowControlOut;
    }

    public void setFlowControlOut(String flowControlOut) {
	this.flowControlOut = stringToFlow(flowControlOut);
    }

    public int getFlowControlOut() {
	return flowControlOut;
    }

    public String getFlowControlOutString() {
	return flowToString(flowControlOut);
    }

    public void setDatabits(int databits) {
	this.dataBits = databits;
    }

    public void setDatabits(String databits) {
	if (databits.equals("5")) {
	    this.dataBits = SerialPort.DATABITS_5;
	}
	if (databits.equals("6")) {
	    this.dataBits = SerialPort.DATABITS_6;
	}
	if (databits.equals("7")) {
	    this.dataBits = SerialPort.DATABITS_7;
	}
	if (databits.equals("8")) {
	    this.dataBits = SerialPort.DATABITS_8;
	}
    }

    public int getDatabits() {
	return dataBits;
    }

    public String getDatabitsString() {
	switch(dataBits) {
	    case SerialPort.DATABITS_5:
		return "5";
	    case SerialPort.DATABITS_6:
		return "6";
	    case SerialPort.DATABITS_7:
		return "7";
	    case SerialPort.DATABITS_8:
		return "8";
	    default:
		return "8";
	}
    }

    public void setStopbits(int stopbits) {
	this.stopBits = stopbits;
    }

    public void setStopbits(String stopbits) {
	if (stopbits.equals("1")) {
	    this.stopBits = SerialPort.STOPBITS_1;
	}
	if (stopbits.equals("1.5")) {
	    this.stopBits = SerialPort.STOPBITS_1_5;
	}
	if (stopbits.equals("2")) {
	    this.stopBits = SerialPort.STOPBITS_2;
	}
    }

    public int getStopbits() {
	return stopBits;
    }

    public String getStopbitsString() {
	switch(stopBits) {
	    case SerialPort.STOPBITS_1:
		return "1";
	    case SerialPort.STOPBITS_1_5:
		return "1.5";
	    case SerialPort.STOPBITS_2:
		return "2";
	    default:
		return "1";
	}
    }

    public void setParity(int parity) {
	this.parity = parity;
    }

    public void setParity(String parity) {
	if (parity.equals("None") || parity.equals("0")) {
	    this.parity = SerialPort.PARITY_NONE;
	}
	if (parity.equals("Even") || parity.equals("2")) {
	    this.parity = SerialPort.PARITY_EVEN;
	}
	if (parity.equals("Odd") || parity.equals("1")) {
	    this.parity = SerialPort.PARITY_ODD;
	}
    }

    public int getParity() {
	return parity;
    }
    
    public String getParityString() {
	switch(parity) {
	    case SerialPort.PARITY_NONE:
		return "None";
 	    case SerialPort.PARITY_EVEN:
		return "Even";
	    case SerialPort.PARITY_ODD:
		return "Odd";
	    default:
		return "None";
	}
    }
    
    private int stringToFlow(String flowControl) {
	if (flowControl.equals("None")) {
	    return SerialPort.FLOWCONTROL_NONE;
	}
	if (flowControl.equals("Xon/Xoff Out")) {
	    return SerialPort.FLOWCONTROL_XONXOFF_OUT;
	}
	if (flowControl.equals("Xon/Xoff In")) {
	    return SerialPort.FLOWCONTROL_XONXOFF_IN;
	}
	if (flowControl.equals("RTS/CTS In")) {
	    return SerialPort.FLOWCONTROL_RTSCTS_IN;
	}
	if (flowControl.equals("RTS/CTS Out")) {
	    return SerialPort.FLOWCONTROL_RTSCTS_OUT;
	}
	return SerialPort.FLOWCONTROL_NONE;
    }

    String flowToString(int flowControl) {
	switch(flowControl) {
	    case SerialPort.FLOWCONTROL_NONE:
		return "None";
	    case SerialPort.FLOWCONTROL_XONXOFF_OUT:
		return "Xon/Xoff Out";
	    case SerialPort.FLOWCONTROL_XONXOFF_IN:
		return "Xon/Xoff In";
	    case SerialPort.FLOWCONTROL_RTSCTS_IN:
		return "RTS/CTS In";
	    case SerialPort.FLOWCONTROL_RTSCTS_OUT:
		return "RTS/CTS Out";
	    default:
		return "None";
	}
    }
    
    public List<CommPortIdentifier> getPortsList() {
        return this.portsList;
    }
    
    public String getParams() {
        return " " + selectedPortName + " / " + this.baudRate + " / " + this.getParityString();
    }
    
}
    
