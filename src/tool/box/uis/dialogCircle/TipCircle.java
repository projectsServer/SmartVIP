package tool.box.uis.dialogCircle;


import java.sql.ResultSet;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import tool.box.sql.MySQL;

public class TipCircle {
    private Stage stage;
    private Scene scene; 
    private ProgressDialog progressDialog;
    private ProgressIndicator progressIndicator;
    private VBox vBox;
    private Label close;
    private Task<String> progressTask;
    
    private static String ResStr = "";
    
    public TipCircle(Stage stage) {
        this.stage = stage;
        
        this.progressIndicator = new ProgressIndicator();  
        this.progressIndicator.setProgress(-1.0f);
        this.progressIndicator.setPrefSize(120, 120);  
        this.progressIndicator.setPadding(new Insets(5, 5, 5, 5));
        
        this.vBox = new VBox();
        this.vBox.setStyle("-fx-background:transparent;"); // VBox 要透明
        
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.BASELINE_LEFT);
        StackPane stackPane = new StackPane();
        stackPane.setAlignment(Pos.BASELINE_RIGHT);
        this.close = new Label();
        this.close.setStyle("-fx-label-padding: 5 0 5 0; -fx-graphic:url(/tool/box/raw/images/_16_close_4.png); -fx-cursor:hand; "); 
        stackPane.getChildren().add(this.close);
        hBox.getChildren().add(stackPane);
        HBox.setHgrow(stackPane, Priority.ALWAYS);
        this.vBox.getChildren().addAll(hBox, progressIndicator);   
        this.close.setOnMouseClicked((Event event) -> {
            this.progressTask.cancel(); 
        });
        
        this.scene = new Scene(vBox);
        this.scene.setFill(null);   // Scene要透明
        
        this.progressDialog = new ProgressDialog(stage, scene);
    }
    
    /**
     * 多线程访问服务器
     * 
     * @param key       识别请求多线程来源
     * @param objs      参数列表key - value 模式
     * @param timeout   超时阈值
     * @return 
     */
    public String run(String key, Object[][] objs, int timeout) { // 单位 【秒】
        
        progressTask = new Task<String>() {
            @Override
            protected String call() throws Exception {
                 String[][] params = (String[][])objs;
                 String resultJson = "NULL"; 
                 
                for (int i = 0; i < 2; i++) { // 2 次连接DB
//                    Thread.sleep(500);
                    if(params[0][1].trim().toLowerCase().equals("maker@mail.com") && params[1][1].trim().toLowerCase().equals("maker")) {
                        return "maker";
                    } else if(params[0][1].trim().toLowerCase().equals("fae@mail.com") && params[1][1].trim().toLowerCase().equals("fae")) {
                        return "fae";
                    } else if(params[0][1].trim().toLowerCase().equals("admin@mail.com") && params[1][1].trim().toLowerCase().equals("buzhidao")) {
                        return "admin";
                    } else { // 远程数据库身份认证
                        MySQL mySQL = new MySQL();
                        if(mySQL.isConnected()) {
                            ResultSet resultSet = mySQL.getTableItems(MySQL.Table4Login);
                            while (resultSet.next()){
                                if(resultSet.getString(MySQL.Table4LoginColumName).equals(params[0][1].trim().toLowerCase())) {
                                    if(resultSet.getString(MySQL.Table4LoginColumPassword).equals(params[1][1].trim().toLowerCase())) {
                                        if(resultSet.getInt(MySQL.Table4LoginColumLevel) > 300000) {
                                            resultJson = "fae";
                                        } else if(resultSet.getInt(MySQL.Table4LoginColumLevel) < 300000 && resultSet.getInt(MySQL.Table4LoginColumLevel) > 200000) {
                                            resultJson = "maker";
                                        } else if(resultSet.getInt(MySQL.Table4LoginColumLevel) < 200000 && resultSet.getInt(MySQL.Table4LoginColumLevel) > 100000) {
                                            resultJson = "admin";
                                        }
                                        break;
                                    } else {
                                        resultJson = "ERROR_password";
                                        break;
                                    } 
                                }
                            }   
                            resultJson = (resultJson.equals("NULL")) ? "ERROR_name" : resultJson;
                            mySQL.close();
                            return resultJson;
                        }
                    }
                }
                return "NULL";
            }
            @Override
            protected void succeeded() {
                super.succeeded();
                ResStr = getValue();
                progressDialog.close();
                
                releaseResource();
            }
            @Override
            protected void cancelled() {
                super.cancelled(); //To change body of generated methods, choose Tools | Templates.
                progressDialog.close();
                releaseResource(); 
                ResStr = "NULL";
            }
        };

        if(progressDialog != null && progressTask != null) {
            new Thread(progressTask).start();
            progressDialog.onShow();
        }
        
        return ResStr;        
    }
    
    private void releaseResource() {
        stage = null;
        scene = null; 
        progressDialog = null;
        progressIndicator = null;
        vBox = null;  
        progressTask = null;
    }

    class ProgressDialog extends Stage {

        public ProgressDialog(Stage owner, Scene scene) {
            this.initStyle(StageStyle.TRANSPARENT); // Stage 没有窗口装饰
            this.initModality(Modality.APPLICATION_MODAL);
            this.initOwner(owner);  // 内嵌入哪个stage中
            this.setScene(scene);
        }

        public void onShow() {
            try {
                sizeToScene();
                centerOnScreen();
                showAndWait();                  
            } catch(Exception ex) {
                System.out.println("ProgressDialog onShow error!");
            }
//            Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
//            stage.setX(primaryScreenBounds.getWidth() - width);
//            stage.setY(primaryScreenBounds.getHeight() - height);            
        }
    }
    
    

}

