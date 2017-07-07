package tool.box.rs232.diy;

import java.util.ArrayList;
import java.util.List;
import tool.box.common.Tool;

public class Protocol {
    private static final byte FrameStartA = (byte) 0x55;
    private static final byte FrameStartB = (byte) 0x7A;
    public static int FrameNodataSize = 7;              
    public static int FrameMinSize = 7;
    public static int FrameDataSizeOffset = 3;   

    private ArrayList<byte[]> dataList = new ArrayList<byte[]>();
//    private Rxtx rxtx = null;

//    public SerialProtocol(Rxtx rxtx) {
////        this.rxtx = rxtx;
//    }

    public int getProtocolPosition(byte[] buf, int start, int unDealCount) {
        int datalength, tmp;
        ++start;
        while (start < unDealCount) {
            if ((buf[start] == FrameStartB) && (buf[start - 1] == FrameStartA)) {
                tmp = unDealCount - start - 1 + 2; 
                if (tmp >= FrameMinSize) {     
                    datalength = ((buf[(start-1) + FrameDataSizeOffset] & 0xFF) << 8) | (buf[(start-1) + FrameDataSizeOffset + 1] & 0xFF);
                    if (tmp >= (FrameNodataSize + datalength)) { // frame length ok
                        return ((start - 1) << 16) | ((start - 1) + FrameNodataSize + datalength);
                    }
                }
            }
            start++;
        }
        return -1;	
    }

    public boolean protocolAnalays(byte[] frameBuf, int frameStart, int frameEnd) {
        int cmd;
        cmd = frameBuf[frameStart + 2] & 0xFF;
        
//        int tt = Tool.XORSum(frameStart, frameEnd-1, frameBuf);
//        int pp = frameBuf[frameEnd-1];

        if (Tool.XORSum(frameStart, frameEnd-1, frameBuf) == frameBuf[frameEnd-1]) {
            insert(frameStart, frameEnd, frameBuf);
            return true;
        } else {
            return true; // dg
//            return false;
//            rxtx.getDataParseView().getRunCmd().writeLogPanel("cmd = " + cmd + " ��������У���쳣", "error");
        }
    }

    public void insert(byte[] item) {
        this.dataList.add(item);
    }
    public void insert(int start, int end, byte[] buf) {
        byte[] tmp = new byte[end - start];
        for(int i=0; i<tmp.length; i++) {
            tmp[i] = buf[start+i];
        }
        this.insert(tmp);
    }

    public void remove(int index) {
        if(index < dataList.size()) {
            this.dataList.remove(index);
        }
    }

    public void clear() {
        if(!dataList.isEmpty()) {
            this.dataList.clear();
        }
    }

    public boolean isEmpty() {
        if(dataList.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    public byte[] getItem(int index, boolean isDelete) {
        if(index < dataList.size()) {
            if(isDelete) {
                byte[] tmp = this.dataList.get(index);
                this.dataList.remove(index);
                return tmp;
            } else {
                return this.dataList.get(index);
            }
        } else {
            return null;
        }
    }

    public byte[] response(int cmd) {
        byte[] buf = new byte[] {(byte)0xAB, (byte)0x9E,			
                                 (byte)(cmd>>8),(byte)cmd,			
                                 (byte)0x00,(byte)0x02,				
                                 (byte)0x00,(byte)0x00,				
                                 (byte)0x00,					
                                 (byte)0x00};					
        buf[buf.length-1] = Tool.XORSum(0, buf.length-1, buf);
        return buf;
    }
    public byte[] response(int cmd, byte status) {
        byte[] buf = new byte[] {(byte)0xAB, (byte)0x9E,			
                                 (byte)(cmd>>8),(byte)cmd,			
                                 (byte)0x00,(byte)0x02,				
                                 (byte)0x00,(byte)0x01,				
                                 (byte)0x00,					
                                 (byte)status,					
                                 (byte)0x00};					
        buf[buf.length-1] = Tool.XORSum(0, buf.length-1, buf);
        return buf;
    }
    public byte[] response(int cmd, byte[] data) {
        return null;
    }
    
    public byte[] getCmdFrame(int cmd, int subCmd, Object datas, int sendLen, int receiveLen) {
        if((cmd & 0x70) == 0x70) { // search cmd
            return getSearchCmdFrame(cmd, subCmd, datas, sendLen, receiveLen);
        } else if((cmd & 0xD0) == 0xD0) { // set cmd
            return getSetCmdFrame(cmd, subCmd, datas, sendLen, receiveLen); 
        }
        return null;

    }
    
    private byte[] getSearchCmdFrame(int cmd, int subCmd, Object datas, int sendLen, int receiveLen) {
        if(cmd == 0x71 && subCmd == 0x0316) { // get total status cmd
            List<List<Object>> listExcel = ( List<List<Object>>)datas;
            int size = listExcel.size()*2;
            byte[] tmp = new byte[7 + size];
            tmp[0] = (byte)0xAA;
            tmp[1] = (byte)0x75;
            tmp[2] = (byte)cmd;
            tmp[3] = (byte)(size>>8 & 0xFF);
            tmp[4] = (byte)(size & 0xFF);
            tmp[5] = (byte)0x00;
            
            for(int i=0, j=0; i<size-1; i=i+2, j++) {
                String subCmdHex = listExcel.get(j).get(2).toString();
                tmp[6+i] = (byte)Integer.parseInt(subCmdHex.substring(0, 2), 16);
                tmp[6+i+1] = (byte)Integer.parseInt(subCmdHex.substring(2, 4), 16);
            }
            
            tmp[tmp.length-1] = Tool.XORSum(tmp, 0, tmp.length-1);
            return tmp;            
        } else {
            byte[] tmp = new byte[]{(byte)0xAA, (byte)0x75, // lead
                                    (byte)cmd, // cmd 
                                    (byte)((sendLen>>8)&0xFF), (byte)(sendLen&0xFF), // size 
                                    (byte)0x00, // default
                                    (byte)((subCmd>>8)&0xFF), (byte)(subCmd&0xFF), // content 
                                    (byte)0x00}; // xor
            tmp[8] = Tool.XORSum(tmp, 0, 8);
            return tmp;                
        }
    }
    
    private byte[] getSearchCmdFrame(int cmd, int subCmd, Object datas, int len) {
        if(cmd == 0x71 && subCmd == 0x0316) { // get total status cmd
            List<List<Object>> listExcel = ( List<List<Object>>)datas;
            int size = listExcel.size()*2;
            byte[] tmp = new byte[7 + size];
            tmp[0] = (byte)0xAA;
            tmp[1] = (byte)0x75;
            tmp[2] = (byte)cmd;
            tmp[3] = (byte)(size>>8 & 0xFF);
            tmp[4] = (byte)(size & 0xFF);
            tmp[5] = (byte)0x00;
            
            for(int i=0, j=0; i<size-1; i=i+2, j++) {
                String subCmdHex = listExcel.get(j).get(2).toString();
                tmp[6+i] = (byte)Integer.parseInt(subCmdHex.substring(0, 2), 16);
                tmp[6+i+1] = (byte)Integer.parseInt(subCmdHex.substring(2, 4), 16);
            }
            
            tmp[tmp.length-1] = Tool.XORSum(tmp, 0, tmp.length-1);
            return tmp;            
        } else {
            byte[] tmp = new byte[]{(byte)0xAA, (byte)0x75, // lead
                                    (byte)cmd, // cmd 
                                    (byte)((len>>8)&0xFF), (byte)(len&0xFF), // size 
                                    (byte)0x00, // default
                                    (byte)((subCmd>>8)&0xFF), (byte)(subCmd&0xFF), // content 
                                    (byte)0x00}; // xor
            tmp[8] = Tool.XORSum(tmp, 0, 8);
            return tmp;                
        }
    }    
    
    private byte[] getSetCmdFrame(int cmd, int subCmd, Object datas, int sendLen, int receiveLen) {
        if((cmd & 0xD4) == 0xD4) { // JL207A
            byte[] mByte = new byte[(7) + sendLen];

            mByte[0] = (byte) 0xAA;
            mByte[1] = (byte) 0x75;
            mByte[2] = (byte) cmd;
            mByte[3] = (byte) ((sendLen >> 8) & 0xFF);
            mByte[4] = (byte) ((sendLen) & 0xFF);
            mByte[5] = (byte) (0x00);
            mByte[6] = (byte) ((subCmd >> 8) & 0xFF);
            mByte[7] = (byte) ((subCmd) & 0xFF);

            System.arraycopy((byte[]) datas, 0, mByte, 8, ((byte[]) datas).length);

            mByte[mByte.length - 1] = Tool.XORSum(mByte, 0, mByte.length - 1);

            return mByte;       
            
        } else if((cmd & 0xD1) == 0xD1) { // CG901
            if(subCmd == 0x0334) { // oneKeySetParams
                String[] mDatas = (String[])datas;
                byte[] mByte = new byte[(7) + 2 + (10+11+19+16+16+4)];
                int dataLength = mByte.length - 7;
                
                mByte[0] = (byte)0xAA;
                mByte[1] = (byte)0x75;
                mByte[2] = (byte)cmd;
                mByte[3] = (byte)((dataLength>>8)&0xFF);
                mByte[4] = (byte)((dataLength)&0xFF);
                mByte[5] = (byte)(0x00);
                mByte[6] = (byte)((subCmd>>8)&0xFF);
                mByte[7] = (byte)((subCmd)&0xFF);                
                
                System.arraycopy(mDatas[1].getBytes(), 0, mByte, 8, 10);
                System.arraycopy(mDatas[2].getBytes(), 0, mByte, 8+10, 11);
                System.arraycopy(mDatas[3].getBytes(), 0, mByte, 8+10+11, 19); 
                System.arraycopy(mDatas[4].getBytes(), 0, mByte, 8+10+11+19, 16);
                System.arraycopy(mDatas[5].getBytes(), 0, mByte, 8+10+11+19+16, 16);
                System.arraycopy(mDatas[6].getBytes(), 0, mByte, 8+10+11+19+16+16, 4);
                
                mByte[mByte.length-1] = Tool.XORSum(mByte, 0, mByte.length-1);
                
                return mByte;
            } else {
                byte[] mByte = new byte[(7) + sendLen]; 
                
                mByte[0] = (byte)0xAA;
                mByte[1] = (byte)0x75;
                mByte[2] = (byte)cmd;
                mByte[3] = (byte)((sendLen>>8)&0xFF);
                mByte[4] = (byte)((sendLen)&0xFF);
                mByte[5] = (byte)(0x00);
                mByte[6] = (byte)((subCmd>>8)&0xFF);
                mByte[7] = (byte)((subCmd)&0xFF);                
                
                System.arraycopy((byte[])datas, 0, mByte, 8, ((byte[])datas).length);
                
                mByte[mByte.length-1] = Tool.XORSum(mByte, 0, mByte.length-1);
                
                return mByte;                
            }
            
        }
        return null;
    }

    public ArrayList<byte[]> getDataList() {
        return dataList;
    }

    

}
