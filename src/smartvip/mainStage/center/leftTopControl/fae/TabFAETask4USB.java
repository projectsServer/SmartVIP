package smartvip.mainStage.center.leftTopControl.fae;

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
import tool.box.common.Tool;

public class TabFAETask4USB {
    private Stage stage;
    private Scene scene; 
    private ProgressDialog progressDialog;
    private ProgressIndicator progressIndicator;
    private VBox vBox;
    private static String ResStr = "";
    private TabFAEController tabFAEController;     
    private Label close;
    public static boolean ExitFlag = false;
    private Task<String> progressTask;
    
    public TabFAETask4USB(Stage stage, TabFAEController tabFAEController) {
        this.initUIsAndParams(stage, tabFAEController);
    }

    /**
     * 多线程工作
     * 
     * @param objs      参数列表key - value 模式  0=No 1=CheckBox 2=Tip 3=Content 4=ModelType 5=SubCmd 6=SendLen 7=ReceiveLen 8=Offset  
     * @param model     产品型号
     * @param timeout   超时阈值
     * @return "OK"
     */
    public String run(Object[][] objs, String model, int timeout) { // 单位 【秒】
        
        progressTask = new Task<String>() {
            
            @Override
            protected String call() throws Exception {
                StringBuilder sb = new StringBuilder();
                
                ExitFlag = false;
                
                for(int i=0; i<objs.length; i++) {
                    if (ExitFlag) {
                        break;
                    } else {
                        if ((boolean) objs[i][1] && (boolean) objs[i][4]) { // CheckBox & ModelType
                            sb.append((String) objs[i][5]).append(":").append((String) objs[i][3]).append(";");
                        }
                    }
                }
                tabFAEController.addLog("USB内容为：" + sb.substring(0, sb.length()-1), null);
                
                return sb.substring(0, sb.length()-1);
            }
            @Override
            protected void succeeded() {
                super.succeeded();
                ResStr = getValue();
                progressDialog.close();
                releaseResource();
                
                String resStr = Tool.saveFile(stage, "记事本 (*.txt)", "*.txt", "SmartVIP_USB_" + Tool.getTime("yyyyMMddHHmmss"), getValue());
                if(resStr.startsWith("false")) {
                    tabFAEController.addLog("USB内容未保存......", null);
                } else if(resStr.startsWith("error")) {
                    tabFAEController.addLog("USB文件出现系统异常！", null);
                } else {
                    tabFAEController.addLog("USB内容路径为：" + resStr, null);
                } 
            }
            @Override
            protected void cancelled() {
                super.cancelled(); //To change body of generated methods, choose Tools | Templates.
                progressDialog.close();
                releaseResource(); 
                ExitFlag = true;
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
        this.stage = null;
        this.scene = null; 
        this.progressDialog = null;
        this.progressIndicator = null;
        this.vBox = null;  
        this.progressTask = null;
    }   
    
    class ProgressDialog extends Stage {

        public ProgressDialog(Stage owner, Scene scene) {
            this.initStyle(StageStyle.TRANSPARENT); // Stage 没有窗口装饰
            this.initModality(Modality.APPLICATION_MODAL);
            this.initOwner(owner);
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
        }
    }

    private void initUIsAndParams(Stage stage, TabFAEController tabFAEController) {
        this.stage = stage;
        this.tabFAEController = tabFAEController; 
        
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

    
}

