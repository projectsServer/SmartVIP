package tool.box.login;

import java.io.File;
import java.io.IOException;
import java.util.ResourceBundle;
import tool.box.common.LocalStorage;
import tool.box.common.Tool;

/**
 * memo：
 *      1、自动补全支持最近5个用户
 *      2、五个输出入参数：name、password、isRememberMe、serverAdd、headIcon
 * 
 * @author dg
 */
public class Login {
    public String userAuthority = "";
    
    private String[] loginKeys = null;
    private String[] loginDefualtValues = null; 
    
//    private final String[] loginKeys4name = new String[]{"name_1", "name_2", "name_3", "name_4", "name_5"};
//    private final String[] loginNameDefaultValues = new String[]{"", "", "", "", ""};
//    private String[] historyNames = new String[5];
    
    private String name = "";
    private String password = "";
    private String isRememberMe = "false";
    private String serverAdd = "";
    private String headIcon = "";
    private ResourceBundle rb = null;
    
    public Login(ResourceBundle rb) {
        if(rb != null) {
            this.rb = rb;
            this.loginKeys = new String[]{"name", "password", "isRememberMe", "serverAdd", "headIcon"};
            this.loginDefualtValues = new String[]{"", "", "false", rb.getString("DefaultServerAdd"), rb.getString("DefaultHeadIcon")}; 
        }        
        
        String[] res = readCookies();
        
        this.name = res[0];
        this.password = res[1];
        this.isRememberMe = res[2];
        this.serverAdd = res[3];
        this.headIcon = res[4];
    }
    
    public String[] readCookies() {
        File file = new File(LocalStorage.PropertiesPath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {}
        }
        return LocalStorage.getProperies(loginKeys, loginDefualtValues);
    }
    public void writeCookies(String name, String password, String isRememberMe, String serverAdd, String headIcon) {
        String[] values = new String[]{name, password, isRememberMe, serverAdd, headIcon};
        LocalStorage.setProperies(loginKeys, values);
        
        updateHistoryNames(name);
    }

    public void writeCookies(String...params) {
        String[] keys = new String[params.length];
        String[] values = new String[params.length];        
        for(int i=0; i<params.length; i++) {
            keys[i] = loginKeys[i];
            values[i] = params[i];
        }
        LocalStorage.setProperies(keys, values);
        
        updateHistoryNames(name);
    }  
    public void writeCookie(String key, String value) {
        LocalStorage.setPropery(key, value);
    }
    public void reset() {
        LocalStorage.setProperies(loginKeys, loginDefualtValues);
    }
    
    private void updateHistoryNames(String name) {
        // 待完成
    }
    
    public String[] checkValid(String name, String password) {
        String[] res = new String[2];
        if (name.equals("")) {
            res[0] = "NO";
            res[1] = "邮箱为空，请输入......";
        } else if (!name.equals("") && password.equals("")) {
            res[0] = "NO";
            res[1] = "密码为空，请输入......";
        } else if (!name.equals("")) {
            if (Tool.isEmail(name)) {
                res[0] = "OK";
                res[1] = "";
            } else {
                res[0] = "NO";
                res[1] = "邮箱错误，请输入......";
            }
        }
        return res;
    }
    
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getIsRememberMe() {
        return isRememberMe;
    }

    public void setIsRememberMe(String isRememberMe) {
        this.isRememberMe = isRememberMe;
    }

    public String getServerAdd() {
        return serverAdd;
    }

    public void setServerAdd(String serverAdd) {
        this.serverAdd = serverAdd;
        writeCookie("serverAdd", serverAdd);
    }

    public String getHeadIcon() {
        return headIcon;
    }

    public void setHeadIcon(String headIcon) {
        this.headIcon = headIcon;
        writeCookie("headIcon", headIcon);
    }
    
    
    
}
