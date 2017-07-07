package tool.box.rs232;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;
import java.io.*;
import java.util.Enumeration;
import java.util.TooManyListenersException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SerialRxtx implements SerialPortEventListener {
//    private final static int MaxSerialCount = 40;
    private final static int MaxRxBufBytes = 1024 * 10; // 10MB��115200  80*50 ms = 4s �����

    private Serial serial;

    private Enumeration portList;
    private CommPortIdentifier[] portIds = new CommPortIdentifier[20];  
    private CommPortIdentifier selectedPort;       
    private SerialPort serialPort;
    private int portCount = 0;

    private boolean isOpen = false;
    private boolean isPause = false;
    private OutputStream outputStream;
    private InputStream inputStream;
    private static int numBytes = 0;
    private String str; 
    private int numCount = 0;
    private int RXCount = 0;

    private byte[] serialRevBuf =new byte[MaxRxBufBytes]; 
    private int unDealCount = 0; // no deal bytes in pipe
    
    private void dealRxData(byte[] buf) {
        int i = 0, j = 0;
        int posFindStart = 0, posFindEnd; // search frame leader from posFindStart to posFindEnd on serialRevBuf
        int protocolPos = 0;              // hight 8bit = posFindStart(byte[] footer), low 8bit = posFindEnd(next frame start)
        int size = buf.length;            // received byte counter

        if ((size + unDealCount) >= MaxRxBufBytes) {
            unDealCount = 0; // pipe datas too much to ignore all datas
            if (size > MaxRxBufBytes) {
                size = MaxRxBufBytes;
            }
        }

        for (i = 0, j = unDealCount; i < size; i++) {
            serialRevBuf[j++] = buf[i];
        }
        posFindStart = 0;
        size += unDealCount;
        while (posFindStart < size) {
            protocolPos = serial.getDataProtocol().getProtocolPosition(serialRevBuf, posFindStart, size);

            if (protocolPos == -1) {
                break; 
            } else {
                posFindStart = (protocolPos & 0xFFFF0000) >> 16;
                posFindEnd = protocolPos & 0x0000FFFF;
                
                if(serial.getDataProtocol().protocolAnalays(serialRevBuf, posFindStart, posFindEnd)) {
                    
                } else {
//                    System.out.println("XOR is not equal!");
                    this.serial.getHostController().addLog(null, "XOR is not equal!");
                }

                posFindStart = posFindEnd;
            }

        }
        unDealCount = size - posFindStart;
        for (i = 0, j = posFindStart; i < unDealCount; i++) {
            serialRevBuf[i] = serialRevBuf[j++];
        }
    }

    @Override  // watch serial COM, and receive data����
    public void serialEvent(SerialPortEvent e) {
        
        if (e.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
            try {
                int len;
                while((len = inputStream.available()) > 0) {
                    byte[] rxBufTmp = new byte[len];
                    if(inputStream.read(rxBufTmp) > 0) {
                        dealRxData(rxBufTmp);
//                        Tool.printByteArr(rxBufTmp);
//                        this.serial.getHostController().addLog(null, Tool.getPrintByteArr(rxBufTmp));
                        rxBufTmp = null;
                    }
                    Thread.sleep(100);  // should delay ,otherwise lose some datas
                }
            } catch (IOException | InterruptedException ex) {
                releasePort();
                System.out.println("Serial inner: Thread.sleep() was interrupted!");
            }
        } else if(e.getEventType() == SerialPortEvent.CTS) {
        }
    }

    public boolean isIsOpen() {
        return isOpen;
    }

    public void setIsOpen(boolean isOpen) {
        this.isOpen = isOpen;
    }

    public boolean isIsPause() {
        return isPause;
    }

    public void setIsPause(boolean isPause) {
        this.isPause = isPause;
    }

    public void setRXCount(int RXCount) {
        this.RXCount = RXCount;
    }

    public int getPortCount() {
        return portCount;
    }

    public void setPortCount(int portCount) {
        this.portCount = portCount;
    }  

    public SerialRxtx(Serial serial) {
        this.serial = serial;
    }
  

    // 1 = ok, 0 = false, -1 = using
    public int open(String portName, int baud, int dataBits, int stopBits, int parity)  {
        try {
            selectedPort = CommPortIdentifier.getPortIdentifier(portName);
            serialPort = (SerialPort) selectedPort.open("mySerialPort", 200); // 2000 second
            serialPort.setSerialPortParams(baud, dataBits, stopBits, parity); // SerialPort.PARITY_NONE/PARITY_EVEN/PARITY_ODD
            outputStream = serialPort.getOutputStream();
            inputStream = serialPort.getInputStream();
            serialPort.addEventListener(this);
            serialPort.notifyOnDataAvailable(true);
            isOpen = true; 
            
            this.serial.writeDefaultSetting(String.valueOf(baud), String.valueOf(dataBits), String.valueOf(stopBits), String.valueOf(parity)); 
            
            this.serial.getHostController().addLog("串口打开：" + selectedPort.getName() + " / " + serialPort.getBaudRate() + " / " + serial.getSerialSetting().getParityString(), null);
            
            return 1;
        } catch (NoSuchPortException ex) {
//            System.out.println("open " + portName + " no such port");
            this.serial.getHostController().addLog(null, "open " + portName + " no such port");
            return 0;
        } catch (TooManyListenersException ex) {
//            System.out.println("exception: TooManyListenersException");
            this.serial.getHostController().addLog(null, "exception: TooManyListenersException");
            return 0;
        } catch (IOException ex) {
//            System.out.println("exception: IOException");
            this.serial.getHostController().addLog(null, "exception: IOException");
            return 0;
        } catch (PortInUseException ex) {
//            System.out.println(portName + " is using!");
            this.serial.getHostController().addLog(null, portName + " is using!");
            return -1;
        } catch (UnsupportedCommOperationException ex) {
//            System.out.println("exception: UnsupportedCommOperationException");
            this.serial.getHostController().addLog(null, "exception: UnsupportedCommOperationException");
            return 0;            
        }
    }

    public void closePort() {
        if (isOpen) {
            if (serialPort != null) {
                releasePort();
            }
        }
    }

    public void sendBytes(byte[] sendBytes) {
        if(sendBytes != null) {
            try {
                outputStream.write(sendBytes);
                outputStream.flush();
            } catch (Exception e) {
                Logger.getLogger(SerialRxtx.class.getName()).log(Level.SEVERE, null, e);
                releasePort();
            }
        }
    }
    public void sendBytes(byte[] sendBytes, int off, int len) {
        if(sendBytes != null && sendBytes.length >= len + off) {
            try {
                outputStream.write(sendBytes, off, len);
                outputStream.flush();
            } catch (Exception e) {
                Logger.getLogger(SerialRxtx.class.getName()).log(Level.SEVERE, null, e);
                releasePort();
            }
        }
    }

    public void releasePort() {
        if(portList != null) {
            portList = null ;
        }

        if(portIds != null) {
            portIds = null ;
        }

        if(selectedPort != null) {
            serialPort.close();
        }

        portCount = 0;
        isOpen = false;
        isPause = false;
        
        if(outputStream != null) {
            try {
                outputStream.close();

                if(inputStream != null) {
                    inputStream.close() ;
                }
            } catch (IOException ex) {
                Logger.getLogger(SerialRxtx.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public void setOutputStream(OutputStream outputStream) {
        if(outputStream == null) {
            System.out.println("outputStream is null!");
        } else {
            this.outputStream = outputStream;
        }
    }
    
    public InputStream getInputStream() {
        return this.inputStream;
    }
    
    public void setInputStream(InputStream inputStream) {
        if(inputStream == null) {
            System.out.println("inputStream is null!");
        } else {
            this.inputStream = inputStream;
        }        
    }    

    public SerialPort getSerialPort() {
        return serialPort;
    }
    
    
    
    
}
