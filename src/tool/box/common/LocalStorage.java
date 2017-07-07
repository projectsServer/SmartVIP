package tool.box.common;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LocalStorage {
    public final static String PropertiesPath = System.getProperty("user.home") + "\\SmartPM.properties";
    static Properties pps = new Properties();
    
    /**
     * 得到属性
     * @param key       关键字
     * @return  内容
     */
    public static String getPropery(String key, String defaultValue) {
        String res = null;
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(PropertiesPath);
            pps.load(inputStream);
            res = pps.getProperty(key, defaultValue);
        } catch (Exception ex) {
            Logger.getLogger(LocalStorage.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if(inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ex) {
                    Logger.getLogger(LocalStorage.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return res;
    }
    
    public static String[] getProperies(String[] key, String[] defaultValue) {
        String[] res = new String[key.length];
        
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(PropertiesPath);
            pps.load(inputStream);
            for(int i=0; i<res.length; i++) {
                res[i] = pps.getProperty(key[i], defaultValue[i]);
            }
        } catch (Exception ex) {
            Logger.getLogger(LocalStorage.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if(inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ex) {
                    Logger.getLogger(LocalStorage.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return res;
    }
    
    /**
     * 设置属性
     * @param key      关键字
     * @param value       内容
     */
    public static void setPropery(String key, String value) { 
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
//            inputStream = getClass().getResourceAsStream("aaa.properties");
            inputStream = new FileInputStream(PropertiesPath);
            pps.load(inputStream);
            outputStream = new FileOutputStream(PropertiesPath);
            pps.setProperty(key, value);
            pps.store(outputStream, "Update " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
//            pps.store(outputStream, "Update " + key + " name");
        } catch (Exception ex) {
            Logger.getLogger(LocalStorage.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if(inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ex) {
                    Logger.getLogger(LocalStorage.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            if(outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException ex) {
                    Logger.getLogger(LocalStorage.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    public static void setProperies(String[] keys, String[] values) { 
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
//            inputStream = getClass().getResourceAsStream("aaa.properties");
            inputStream = new FileInputStream(PropertiesPath);
            pps.load(inputStream);
            outputStream = new FileOutputStream(PropertiesPath);
            for(int i=0; i<keys.length; i++) {
                pps.setProperty(keys[i], values[i]);
//                pps.store(outputStream, "Update " + keys[i] + " name");
            }
            pps.store(outputStream, "Update " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        } catch (Exception ex) {
            Logger.getLogger(LocalStorage.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if(inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ex) {
                    Logger.getLogger(LocalStorage.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            if(outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException ex) {
                    Logger.getLogger(LocalStorage.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }    

    public static String getProperies(String userName) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}

