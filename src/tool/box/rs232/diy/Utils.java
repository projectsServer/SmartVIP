package tool.box.rs232.diy;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;
import smartvip.mainStage.HostController;
import smartvip.mainStage.center.leftTopControl.maker.thread.ThreadRS232;
import tool.box.common.Tool;

public class Utils {
    
    public static Object[] sendAndReceive(HostController hostController, byte cmd, int subCmd, Object sendObj, String tip, int timeout, int sendLen, int recieveLen, boolean checkReceiveNull) {
        Object[] resObj = new Object[] {String.valueOf(""), String.valueOf(""), String.valueOf("") }; 
        byte[] sendBytes = null;
        
        if(sendObj == null) {
            // no deal
        } else if(sendObj.getClass().getName().contains("String")) {
            sendBytes = sendString2Bytes(cmd, subCmd, (String)sendObj, sendLen);
        } else {
            sendBytes = (byte[])sendObj;
        }
        
        byte[] str = HostController.serial.getDataProtocol().getCmdFrame(cmd, subCmd, sendBytes, sendLen, recieveLen);
        HostController.serial.getRxtx().sendBytes(str);
        hostController.addLog("发送" + tip + "指令", "发送" + tip + "指令：" + Tool.byte2HexStr(str, 0, str.length));
        
        for (int i = 0; i < timeout*10; i++) {
            if(ThreadRS232.ExitFlag) {
                resObj[0] = "res_null";
                return resObj;                
            } else {
                try {
                    Thread.sleep(100);

                    if(!HostController.serial.getDataProtocol().getDataList().isEmpty()) {
                        byte[] frame = HostController.serial.getDataProtocol().getItem(0, true);
                        hostController.addLog(null, "接收串口数据：" + Tool.byte2HexStr(frame, 0, frame.length));
                        if(frame[2] == (byte)cmd) { 
                            if(frame[6] == (byte)((subCmd>>8)&0xFF) && frame[7] == (byte)(subCmd&0xFF)) {
                                if(recieveLen == -1 || ((((frame[3] & 0xFF) << 8) | frame[4]) == (byte)recieveLen) ) {
                                    if(checkReceiveNull) {
                                        if(frame[8] == (byte)0xFF) {
                                            resObj[0] = "res_null";
                                        } else {
                                            resObj[0] = "res_noNull";
                                        }    
                                        resObj[1] = receivedBytes2String(cmd, subCmd, frame, 8, recieveLen-2);
                                        resObj[2] = frame;
                                        return resObj;                                    
                                    }
                                } else {
                                    resObj[0] = "res_len_error";
                                    hostController.addLog("接收" + tip + "指令：接收数据长度与设计不符...", null);
                                    return resObj;
                                }
                            }
                        }
                    }

                    if(i%10 == 0) {
                        hostController.addLog("发送" + tip + "指令：等待检品应答..." + (i/10+1) + "秒", null);
                    }
                } catch (InterruptedException ex) {
                    resObj[0] = "res_sys_error";
                    System.out.println("Serial Recieve:Thread.sleep() was interrupted!");
                    return resObj;
                }                
            }
            
        } 
        resObj[0] = "res_timeout";
        hostController.addLog("发送" + tip + "指令：等待检品无应答...", null);
        return resObj;        
    }  
    
    private static String receivedBytes2String(byte cmd, int subCmd, byte[] frame, int offset, int len) {
        switch (subCmd) {
            case 0x0343: 
            case 0x0344: 
            case 0x033F: 
            case 0x0201: 
            case 0x0207:
            case 0x0210: 
            case 0x0303: 
            case 0x0013:
            case 0x02F7:  
            case 0x0083:    
            case 0x0301:   
            case 0x0302:
                return Tool.bytesLittleEndian2String(frame, offset, len, Charset.forName("GBK"));
            case 0x0364:
            case 0x0365:
            case 0x034D:
            case 0x034E:
            case 0x034F:
                return String.valueOf(((0.0f + (((frame[offset]) << 8) | ((frame[offset+1]) & 0x00FF))) / 1000));
            case 0x034A: 
            case 0x0348: return String.valueOf(frame[offset]);
            case 0x0211:
            case 0x0214:return new String(frame, offset, len);
            case 0x032E: 
            case 0x032D: 
            case 0x032F:            
            case 0x02F6: 
                if(frame[offset] == 0x00) {
                    return "null";
                } else {
                    return Tool.bytesLittleEndian2String(frame, offset, len, Charset.forName("GBK"));
                }
            case 0x036D:
            case 0x036E:
            case 0x0354:  
            case 0x0356:                 
            case 0x0353: 
            case 0x0366:
            case 0x036F:
                return String.valueOf(((0.0f + (((frame[offset]) << 8) | ((frame[offset+1]) & 0x00FF))) / 1000));
            case 0x0018:
            case 0x02FC:
            case 0x0055: 
            case 0x005E:
            case 0x0027:
            case 0x0029:
            case 0x0307: return String.valueOf(Tool.bytesBigEndian2int(frame, offset, len));
            case 0x0345: return Tool.GB19056BCDs2String(frame, offset, len);
            case 0x020F:
            case 0x0213:    
            case 0x0215: return Integer.toHexString(frame[offset]); 
            default: return Integer.toHexString(frame[offset]);
        }
    }
    
    private static byte[] sendString2Bytes(byte cmd, int subCmd, String str,int sendLen) {
        if((cmd & 0xD0) == 0xD0) {
            switch (subCmd) {
                case 0x0018:
                case 0x0027:
                case 0x0029:
                case 0x005E: 
                case 0x020F:
                case 0x0213:
                case 0x0215:
                case 0x02FC:
                case 0x0304:
                case 0x0307:
                case 0x0312:
                case 0x0340:
                case 0x0373:
                case 0x0374:
                case 0x0055: return Tool.string2byteBitEndian(str, sendLen-2);
                default: return str.getBytes();                 
            }            
        }
        return new byte[]{0x00};
    } 
    
    public boolean isNetLinked(String ip) { // 192.168.20.254
        try {
            Process process= Runtime.getRuntime().exec("ping " + ip);
            InputStreamReader ret = new InputStreamReader(process.getInputStream());
            LineNumberReader retData = new LineNumberReader (ret);        

            String line="";
            while((line=retData.readLine()) != null){
                System.out.println(line);
                if(line.contains("无法访问")) {
                    return false;
                } else if(line.contains("TTL=")) {
                    return true;
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
  
}
