package smartvip.mainStage.center.leftTopControl.maker.thread;

import java.io.File;
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
import smartvip.mainStage.center.leftTopControl.maker.TabMakerController;
import tool.box.common.Tool;
import smartvip.mainStage.center.leftTopControl.maker.excel.FileCheck4CG901A;

public class ThreadCheck4CG901A {
    private TabMakerController tabMakerController;
    private File fileTmp;
    
    private Stage stage;
    private Scene scene; 
    private ProgressDialog progressDialog;
    private ProgressIndicator progressIndicator;
    private VBox vBox;
    private Label close;
    private Task<String> progressTask;
    
    private static String ResStr = "";
    public static boolean ExitFlag = false;
    
    public ThreadCheck4CG901A(TabMakerController tabMakerController, File fileTmp) {
        this.tabMakerController = tabMakerController;
        this.fileTmp = fileTmp;
        this.stage = this.tabMakerController.getStage();
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
    public String run(String key, String[][] objs, int timeout) { // 单位 【秒】
        
        progressTask = new Task<String>() {
            @Override
            protected String call() throws Exception {
                return  FileCheck4CG901A.parseFile(fileTmp, objs[0][1] , objs[1][1], objs[2][1]);
            }
            @Override
            protected void succeeded() {
                super.succeeded();
                if(getValue().contains("ok")) {
                    tabMakerController.setListExcel(FileCheck4CG901A.listExcel);
                    tabMakerController.setListExcelDefault(Tool.deepCopy(tabMakerController.getListExcel())); // deep copy
                    tabMakerController.getFileChoose().setText(fileTmp.getName()); // update_setParams_CG901A UIs
                    tabMakerController.getDeviceType().setText(objs[0][1]);
                    tabMakerController.getMakerStep().setText(objs[1][1]);  
                    tabMakerController.getUsed().setText("N/A");
                    tabMakerController.getNoUse().setText("N/A");
                    tabMakerController.getTotal().setText("N/A");
                    tabMakerController.getBtnDoOnce().setDisable(false); 
                    tabMakerController.showListExcel(objs[0][1], objs[1][1], tabMakerController.getListExcel());
                } else {
                    tabMakerController.getSysLogController().addInfo("打开文件内容格式不符合要求，重新选择......");
                }                 
                ResStr = getValue();
                progressDialog.close();
                releaseResource();
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

        new Thread(progressTask).start();
        progressDialog.onShow();

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
//            Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
//            stage.setX(primaryScreenBounds.getWidth() - width);
//            stage.setY(primaryScreenBounds.getHeight() - height);            
        }
    }

}


