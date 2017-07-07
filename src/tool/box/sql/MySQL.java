package tool.box.sql;

import com.mysql.jdbc.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MySQL {
    
    private String driver = "com.mysql.jdbc.Driver"; // 驱动程序名
//    private String url = "jdbc:mysql://127.0.0.1:3306/smartvip"; // URL指向要访问的数据库名"SmartVIP"
    private String url = "jdbc:mysql://192.168.20.131:3306/smartvip"; // URL指向要访问的数据库名 smartvip
    private String user = "root"; // MySQL配置时的用户名
    private String password = "mysqldg";  // MySQL配置时的密码
    
    public static String Table4Login = "table_login";
    public static String Table4LoginColumName = "name";
    public static String Table4LoginColumPassword = "password";
    public static String Table4LoginColumLevel = "level";
    
    private Connection connection = null;
    private Statement statement = null;
    private ResultSet resultSet = null;

    public MySQL() {
        try{
            Class.forName("com.mysql.jdbc.Driver"); //调用Class.forName()方法加载驱动程序
        }catch(ClassNotFoundException e1){
            System.out.println("找不到MySQL驱动!");
            return;
        }        
        
        try {
            connection = (Connection) DriverManager.getConnection(url, user, password);
        } catch (SQLException ex) {
            System.out.println("MySQL Connection SQLException!");
            return;
        }
        
        try {
            statement = this.connection.createStatement();
        } catch (SQLException ex) {
            System.out.println("MySQL Statement SQLException!");
        }
    }
    
    public boolean isConnected() {
        if(connection != null) {
            if (!connection.isClosed()) {
                System.out.println("Succeeded connecting to the Database!");
                return true;
            } else {
                System.out.println("Failed connecting to the Database!");
                return false;
            } 
        } else {
            System.out.println("DB Connecting is null, and exit!");
            return false;
        }
    }
    
    public ResultSet getTableItems(String tableName) {
        String sql = "SELECT * FROM " + tableName;
        this.resultSet = executeSQL(sql);
        return this.resultSet;
    }
    
    public ResultSet executeSQL(String sql) {
        if(statement != null) {
            try {
                this.resultSet = statement.executeQuery(sql);
                return this.resultSet;
            } catch (SQLException ex) {
                System.out.println("Failed execute SQL!");
                return null;
            }
        }
        return null;
    }
    
    public void close() {
        try {
            this.resultSet.close();
            this.statement.close();
            this.connection.close();
        } catch (SQLException ex) {
            Logger.getLogger(MySQL.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void add() {
        
    }
    public void delete() {
        
    }
    public void edit() {
        
    }
    public void get() {
        
    }
    
}
