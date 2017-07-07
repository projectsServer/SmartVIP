package tool.box.common;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import javax.swing.filechooser.FileSystemView;
import smartvip.mainStage.center.leftBottomLog.SysLogController;
import tool.box.popwindow.PopWindowController;
import tool.box.sms.SMSController;

public class Tool {
    
    /**
     * 字节数组转换成GBK的字符串
     * @param bytes  字节数组  
     * @param offset  字节数组的偏移量
     * @param len  要转换的长度
     * @return  转换完毕的字符串
     * @throws UnsupportedEncodingException  
     */
    public static String bytes2StringLittleEndian(byte[] buff, int offset, int len, Charset charset) {
        int size = 0; 
        for (size = 0; size < len; size++) {
            if(buff[offset + size] == 0) {
                break;
            } 
        }
        return new String(buff, offset, size, charset); 
    }
    public static String bytesLittleEndian2String(byte[] buff, int offset, int len) {
        int i = 0;
        for(i=0; i<len; i++) {
            if(buff[offset+i] == 0) {
                break;
            } 
        }
        return new String(buff, offset, i);
    }
    public static String bytesLittleEndian2String(byte[] buff, int offset, int len, Charset charset) {
        int i = 0;
        for(i=0; i<len; i++) {
            if(buff[offset+i] == 0) {
                break;
            } 
        }
        return new String(buff, offset, i, charset);
    }    
    public static byte[] string2byteLittleEndian(String str, int bytesLen) {
        byte[] res = new byte[bytesLen]; 
        int intTmp = Integer.parseInt(str);
        
        for (int i = 0; i < res.length; i++) {
            res[i] = (byte)(intTmp >> i*8);
        }
        return res; 
    }  
    public static byte[] string2byteBitEndian(String str, int bytesLen) {
        byte[] res = new byte[bytesLen]; 
        int intTmp = Integer.parseInt(str, 10);      
        for(int i=res.length-1, j=0; i>=0; i--,j++) {
            res[i] = (byte)(intTmp >> j*8);
        }
        return res; 
    }  
    public static byte[] stringCharacter2byteBitEndian(String str, int bytesLen) {
        byte[] res = new byte[bytesLen]; 
        try {
            byte[] bs = str.getBytes("GBK");
            for(int i=res.length-1, j=0; i>=0; i--,j++) {
                if(j<bs.length) {
                    res[i] = bs[j];
                } else {
                    break;
                }
            }            
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Tool.class.getName()).log(Level.SEVERE, null, ex);
        }

        return res; 
    }
    public static byte[] stringCharacter2byteLittleEndian(String str, int bytesLen) {
        byte[] res = new byte[bytesLen];
        try {
            byte[] bs = str.getBytes("GBK");
            for(int i=0; i<bs.length; i++) {
                res[i] = bs[i];
            }
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Tool.class.getName()).log(Level.SEVERE, null, ex);
        }
        return res;
    }     
    public static String int2String(int _int, int strLen) {
        StringBuilder sb = new StringBuilder();
        for(int i=strLen; i>0; i--) {
            sb.append(new String(new byte[]{(byte)((_int>>((i-1)*8)))}, 0, 1));
        }
        return sb.toString(); 
    }      
    
    /**
     * 复制单个文件
     * @param srcFile   待复制的文件名 
     * @param destFileName  目标文件名 
     * @param overlay       如果目标文件存在，是否覆盖 
     * @return              如果复制成功返回true，否则返回false 
     */
    public static boolean copyFile(File srcFile, String destFileName, boolean overlay) {  
        if (!srcFile.exists()) {  // 判断源文件是否存在   
            System.out.println("复制源文件不存在");
            return false;  
        } else if (!srcFile.isFile()) { 
            System.out.println("复制源文件失败，因为它不是一个文件");
            return false;  
        }  
  
        // 判断目标文件是否存在  
        File destFile = new File(destFileName);  
        if (destFile.exists()) {  // 如果目标文件存在并允许覆盖  
            if (overlay) {    // 删除已经存在的目标文件，无论目标文件是目录还是单个文件  
                new File(destFileName).delete();  
            }  
        } else {  
            if (!destFile.getParentFile().exists()) {  // 如果目标文件所在目录不存在，则创建目录
                if (!destFile.getParentFile().mkdirs()) { // 目标文件所在目录不存在    
                    System.out.println("复制文件失败，因为创建目标文件所在目录失败");  
                    return false;  
                }  
            }  
        }  
  
        // 复制文件 
        InputStream in = null;  
        OutputStream out = null;         
        try { 
            in = new FileInputStream(srcFile);  
            out = new FileOutputStream(destFile);  
            byte[] buffer = new byte[1024];  
            int byteread;   // 读取的字节数
            while ((byteread = in.read(buffer)) != -1) {  
                out.write(buffer, 0, byteread);  
            }  
            return true;  
        } catch (Exception e) {  
            return false;  
        } finally {  
            try {  
                if(out != null)  out.close();  
                if(in != null)   in.close();  
            } catch (IOException e) {  
            }  
        }  
    }    
    
//    public static BufferedImage createResizedCopyImage(Image originalImage, int scaledWidth, int scaledHeight, boolean preserveAlpha) {
    public static boolean createResizedCopyImage(File originalFileImage, String newPngImagePath, int scaledWidth, int scaledHeight, boolean preserveAlpha) {        
        try {
            BufferedImage originalImage = ImageIO.read(originalFileImage);
            
            int imageType = preserveAlpha ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
            BufferedImage scaledBI = new BufferedImage(scaledWidth, scaledHeight, imageType);
            Graphics2D g = scaledBI.createGraphics();
            if (preserveAlpha) {
                g.setComposite(AlphaComposite.Src);
            }
            g.drawImage(originalImage, 0, 0, scaledWidth, scaledHeight, null);
            g.dispose();
            ImageIO.write(scaledBI, "png", new File(newPngImagePath));   
        } catch (Exception ex) {
            Logger.getLogger(Tool.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }   
    
    /**
     * 字节数速异或校验
     * @param array 异或目标数组  
     * @param offset  数组偏移量
     * @param count  覆盖数组的长度
     * @return  byte  异或的结果
     */
    public static byte XORSum(int start, int end, byte[] buf) {
        byte crc = 0;

        for (int i = start; i < end; i++) {
            crc ^= buf[i];
        }
        return (byte) (crc & 0xFF);
    }
    public static byte XORSum(byte[] array, int offset, int count) {
        byte res = (byte) 0;
        for (int i = 0; i < count; i++) {
            res ^= array[offset + i];
        }
        return (byte) (res & 0xFF);
    }
    public static byte XORSum(List<Byte> list, int len) {
        int res = 0;
        for (int i = 0; i < len; i++) {
            res ^= list.get(i);
        }
        return (byte) (res & 0xFF);
    } 
    
    public static String getPrintByteArr(byte[] buf) {
        StringBuilder strbuf = new StringBuilder(1024);
        for (int i = 0; i < buf.length; i++) {
            String tmp = Integer.toHexString(buf[i] & 0xFF);
            if (tmp.length() == 1) {
                tmp = "0" + tmp;
            }
            strbuf.append(tmp).append(" ");
        }
        
        return strbuf.toString().toUpperCase();
    } 
    
    public static String getPrintByteArr(byte[] buf, int offset, int len) {
        StringBuilder strbuf = new StringBuilder(1024);
        for (int i = offset; i < offset+len; i++) {
            String tmp = Integer.toHexString(buf[i] & 0xFF);
            if (tmp.length() == 1) {
                tmp = "0" + tmp;
            }
            strbuf.append(tmp).append(" ");
        }
        
        return strbuf.toString().toUpperCase();
    }    
    
    public static void printByteArr(byte[] buf) {
        StringBuilder strbuf = new StringBuilder(1024);
        for (int i = 0; i < buf.length; i++) {
            String tmp = Integer.toHexString(buf[i] & 0xFF);
            if (tmp.length() == 1) {
                tmp = "0" + tmp;
            }
            strbuf.append(tmp).append(" ");
        }
        System.out.println(strbuf.toString().toUpperCase());
    }

    public static void printByteArr(String lead, byte[] buf) {
        StringBuilder strbuf = new StringBuilder(1024);
        for (int i = 0; i < buf.length; i++) {
            String tmp = Integer.toHexString(buf[i] & 0xFF);
            if (tmp.length() == 1) {
                tmp = "0" + tmp;
            }
            strbuf.append(tmp).append(" ");
        }
        System.out.println(lead + strbuf.toString().toUpperCase());
    }

    public static void printByteArr(String lead, byte[] buf, int pos, int len) {
        StringBuilder strbuf = new StringBuilder(1024);
        for (int i = pos; i < pos + len; i++) {
            String tmp = Integer.toHexString(buf[i] & 0xFF);
            if (tmp.length() == 1) {
                tmp = "0" + tmp;
            }
            strbuf.append(tmp).append(" ");
        }
        System.out.println(lead + strbuf.toString().toUpperCase());
    }
    
    public static String byte2HexStr(byte[] buf, int pos, int len) {
        StringBuilder strbuf = new StringBuilder(1024);
        for (int i = pos; i < pos + len; i++) {
            String tmp = Integer.toHexString(buf[i] & 0xFF);
            if (tmp.length() == 1) {
                tmp = "0" + tmp;
            }
            strbuf.append(tmp).append(" ");
        }
        return strbuf.toString().toUpperCase();
    }
    
    /**
     * 大端模式的字节数组转换成int类型返回
     * @param array 目标数
     * @param len 转换放置的数组大小
     * @param offset 数组偏移量
     * @return 转换完成的 int 数 
     */
    public static int bytesBigEndian2int(byte[] array, int offset, int len) {
        int tmp = 0;

        for (int i = 0; i < len; i++) {
            tmp |= (((int) array[offset + i]) << (8 * (len - i - 1))) & (0xFF << (8 * (len - i - 1)));
        }

        return tmp;
    }

    /**
     * 小端模式的数组转换成int类型返回
     * @param array 目标数
     * @param len 转换放置的数组大小
     * @param offset 数组偏移量
     * @return 转换完成的 int 数 
     */
    public static int bytesLittleEndian2int(byte[] array, int offset, int len) {
        int tmp = 0;

        for (int i = 0; i < len; i++) {
            tmp |= (((int) array[offset + i]) << (8 * i)) & (0xFF << (8 * i));
        }

        return tmp;
    }   
    

    public static String getTime(String format) {
        SimpleDateFormat sDateFormat = null;
        String date = null;

        if (format.equals("yy-MM-dd HH:mm:ss")) {
            sDateFormat = new SimpleDateFormat(format, Locale.CHINA);
            date = sDateFormat.format(new java.util.Date());
        } else if (format.equals("yyyy-MM-dd HH:mm:ss")) {
            sDateFormat = new SimpleDateFormat(format, Locale.CHINA);
            date = sDateFormat.format(new java.util.Date());
        } else if (format.equals("yyyy-MM-dd HH:mm:ss:SSS")) {
            sDateFormat = new SimpleDateFormat(format, Locale.CHINA);
            date = sDateFormat.format(new java.util.Date());            
        } else if (format.equals("yyyyMMddHHmmss")) {
            sDateFormat = new SimpleDateFormat(format, Locale.CHINA);
            date = sDateFormat.format(new java.util.Date());
        } else if (format.equals("yyMMddHHmmss")) {
            sDateFormat = new SimpleDateFormat(format, Locale.CHINA);
            date = sDateFormat.format(new java.util.Date()).substring(2);
        } else if (format.equals("yyyyMMddHHmmssSSS")) {
            sDateFormat = new SimpleDateFormat(format, Locale.CHINA);
            date = sDateFormat.format(new java.util.Date());
        } else if (format.equals("yyyy-MM-dd HH:mm")) {
            sDateFormat = new SimpleDateFormat(format, Locale.CHINA);
            date = sDateFormat.format(new java.util.Date());
        } else if (format.equals("yyyy-MM-dd")) {
            sDateFormat = new SimpleDateFormat(format, Locale.CHINA);
            date = sDateFormat.format(new java.util.Date());
        } else if (format.equals("ago_7_SS")) {			// 得到当前时间前7天的时间串
            Date dDate = new Date((new Date()).getTime() - 7 * 24 * 60 * 60 * 1000);
            sDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
            date = sDateFormat.format(dDate);
        } else if (format.equals("ago_7")) {			// 得到当前时间前7天的时间串
            Date dDate = new Date((new Date()).getTime() - 7 * 24 * 60 * 60 * 1000);
            sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
            date = sDateFormat.format(dDate);
        } else if (format.equals("ago_7_S")) {		// 得到当前时间前7天的时间串
            Date dDate = new Date((new Date()).getTime() - 7 * 24 * 60 * 60 * 1000);
            sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
            date = sDateFormat.format(dDate);
        }

        return date;
    }
    
    public static List deepCopy(List src) {             
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = null;
        List dest = null;
        try {
            out = new ObjectOutputStream(byteOut);
            out.writeObject(src);
            ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
            ObjectInputStream in = null;
            in = new ObjectInputStream(byteIn);
            dest = (List) in.readObject();
        } catch (IOException ex) {
            Logger.getLogger(Tool.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Tool.class.getName()).log(Level.SEVERE, null, ex);
        }
        return dest;  
    }   
    
/**
     * 国标BCD时间格式转成时间字符串，形如 yyyy-MM-dd hh:mm:ss
     * @param BCDs  BCD字节数组
     * @param offset  字节数组的偏移量
     * @param len   要转换的长度
     * @return  转换完毕的字符串
     */
    public static String GB19056BCDs2String(byte[] BCDs, int offset, int len) {
        String str = "20";
        int i = 0;
        for (i = 0; i < len; i++) {
            if ((BCDs[offset + i] == (byte) 0xFF) || ((BCDs[offset + i] & (byte) 0x0F) > (byte) 0x09) || (((BCDs[offset + i] & (byte) 0xF0) >> 4) > (byte) 0x09)) {
                return "数据格式异常";
            } else {
                continue;
            }
        }

        for (i = 0; i < len; i++) {
            str += Byte.toString((byte) ((BCDs[i + offset] & 0xF0) >> 4)) + Byte.toString((byte) (BCDs[i + offset] & 0x0F));
            switch (i) {
                case 0:
                case 1:
                    str += "-";
                    break;
                case 2:
                    str += " ";
                    break;
                case 3:
                case 4:
                    str += ":";
                    break;
                default:
                    break;
            }
        }

        return str;
    }    
    
    
    public static String saveFile(Stage stage, String exNameTip, String exName, String fileName,String content) {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(exNameTip, exName);
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.setTitle("创建文件且保存为...");
        fileChooser.setInitialDirectory(FileSystemView.getFileSystemView().getHomeDirectory()); // 桌面地址
        fileChooser.setInitialFileName(fileName); 
        File saveFile = fileChooser.showSaveDialog(stage);
        
        if (saveFile == null) {
            return "false"; // addInfo("系统信息提示未保存");
        } else {
            try {
                saveFile.createNewFile();
                FileOutputStream fileOutputStream = new FileOutputStream(saveFile);
                new Thread(new Task<String>() {
                    @Override
                    protected String call() throws Exception {
                        fileOutputStream.write(content.getBytes());
                        fileOutputStream.close();
                        return null;
                    }
                }).start();
                return saveFile.getAbsolutePath();
            } catch (IOException ex) {
                Logger.getLogger(PopWindowController.class.getName()).log(Level.SEVERE, null, ex);
                return "error"; //addInfo("创建系统存储信息文件时出现系统异常");
            } 
        }
    }
    
    public static boolean copyFile(Stage stage, String exNameTip, String exName, File oldFile) {
        if (oldFile != null) {
            
            FileChooser fileChooser = new FileChooser();
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(exNameTip, exName);
            fileChooser.setTitle("文件保存为...");
            fileChooser.setInitialDirectory(FileSystemView.getFileSystemView().getHomeDirectory()); // 桌面地址
            fileChooser.getExtensionFilters().add(extFilter);
            fileChooser.setInitialFileName(oldFile.getName());

            File saveFile = fileChooser.showSaveDialog(stage); // 保存按钮
            if (saveFile != null) {
                try {
                    saveFile.createNewFile();
                    
                    new Thread(new Task<String>() {
                        @Override
                        protected String call() throws Exception {
                            int bytesum = 0;
                            int byteread = 0;
                            InputStream inputStream = new FileInputStream(oldFile.getAbsolutePath());      //读入原文件 
                            FileOutputStream fileOutputStream = new FileOutputStream(saveFile.getAbsolutePath());
                            byte[] buffer = new byte[1024];
                            int length;
                            while ((byteread = inputStream.read(buffer)) != -1) {
                                bytesum += byteread;            //字节数 文件大小 
                                System.out.println(bytesum);
                                fileOutputStream.write(buffer, 0, byteread);
                            } 
                            fileOutputStream.close();
                            inputStream.close();
                            buffer = null;
                            return null;
                        }
                    }).start();                    
                    return true;
                } catch (IOException ex) {
                    Logger.getLogger(SysLogController.class.getName()).log(Level.SEVERE, null, ex);
                    return false;
                } 
            }
        }
        return false;
    }
        
    public static boolean isNumeric(String str, int minLen, int maxLen) {
        if(str.length() < minLen || str.length() > maxLen) {
            return false;
        } else {
            if(!str.contains(".")) {
                Pattern pattern = Pattern.compile("^[0-9]*$");
                Matcher matcher = pattern.matcher(str); //以验证127.400.600.2为例
                return matcher.matches();                   
            } else {
                if(!(str.startsWith(".") || str.endsWith("."))) {
                    if(str.contains(".") && !(str.substring(str.indexOf(".")+1)).contains(".")) { 
                        Pattern pattern = Pattern.compile("^[0-9].*$");
                        Matcher matcher = pattern.matcher(str); //以验证127.400.600.2为例
                        return matcher.matches();                           
                    } 
                }                 
            }           
            return false;
        }
    }
    public static boolean isNumeric(String str, int minLen, int maxLen, String match) {
        if(str.length() < minLen || str.length() > maxLen) {
            return false;
        } else {
            Pattern pattern = Pattern.compile(match);
            Matcher matcher = pattern.matcher(str); //以验证127.400.600.2为例
            return matcher.matches();               
        }
    }    
    public static boolean isCharacters(String str, int minLen, int maxLen) {
        if(str.length() < minLen || str.length() > maxLen) {
            return false;
        } else {
            return Pattern.matches("^[a-zA-Z]+$", str);
        }        
    }
    public static boolean isCharAndNum(String str, int minLen, int maxLen) {
        if(str.length() < minLen || str.length() > maxLen) {
            return false;
        } else {
            return Pattern.matches("^[0-9a-zA-Z]+$", str);
        }        
    }    
    public static boolean isEmail(String email) {
        try {
            String check = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
            Pattern regex = Pattern.compile(check);
            Matcher matcher = regex.matcher(email);
            return(matcher.matches());
        } catch (Exception e) {
            return false;
        }
    }    
    public static boolean isIP(String addr) { 
        if (addr.length() < 7 || addr.length() > 15 || "".equals(addr)) {
            return false;
        } else {
            Pattern pattern = Pattern.compile("\\b((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\b");
            Matcher matcher = pattern.matcher(addr); //以验证127.400.600.2为例
            return matcher.matches();            
        }
    }     
        
}
