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
import smartvip.mainStage.center.leftTopControl.maker.excel.FileSetParams4CG901A;
import smartvip.mainStage.center.leftTopControl.maker.excel.FileSetParams4JL207A;
import static smartvip.mainStage.center.leftTopControl.maker.thread.ThreadSetParams4CG901A.ExitFlag;
             
public class ThreadSetParams4JL207A {
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
    
    public ThreadSetParams4JL207A(TabMakerController tabMakerController, File fileTmp) {
        this.tabMakerController = tabMakerController;
        this.fileTmp = fileTmp;
        
        this.stage = this.tabMakerController.getStage();
        this.progressIndicator = new ProgressIndicator();  
        this.progressIndicator.setProgress(-1.0f);
        this.progressIndicator.setPrefSize(100, 100);           
        
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
                return FileSetParams4JL207A.parseFile(fileTmp, objs[0][1] , objs[1][1], objs[2][1]);
            }
            @Override
            protected void succeeded() {
                super.succeeded();
                
                if(getValue().contains("ok")) {
                    tabMakerController.setListExcel(FileSetParams4JL207A.listExcel);
                    tabMakerController.getFileChoose().setText(fileTmp.getName()); // update_setParams_CG901A UIs
                    tabMakerController.getDeviceType().setText(objs[0][1]);
                    tabMakerController.getMakerStep().setText(objs[1][1]);                     
                    updateCounter(tabMakerController);
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

        if(progressDialog != null && progressTask != null && tabMakerController != null) {
            this.tabMakerController.clearLog(true, false); // 清空日志区
            new Thread(this.progressTask).start();
            this.progressDialog.onShow();
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
    
    private void updateCounter(TabMakerController tabMakerController) {
        int counter = 0;
        String tmp ;
        for(int i=0; i<tabMakerController.getListExcel().size(); i++) {
            tmp = (tabMakerController.getListExcel().get(i).get(FileSetParams4JL207A.TimeIndex)).toString();
            if(tmp != null && tmp.length() == 23) {  // yyyy-MM-dd HH:mm:ss:SSS
                counter++;
            }
        }
        tabMakerController.getUsed().setText(String.valueOf(counter));
        tabMakerController.getNoUse().setText(String.valueOf(tabMakerController.getListExcel().size() - counter));
        tabMakerController.getTotal().setText(String.valueOf(tabMakerController.getListExcel().size()));
        
        if(tabMakerController.getListExcel().size() - counter > 0) {
            tabMakerController.getBtnDoOnce().setDisable(false); 
        }
    }

    static class ProgressDialog extends Stage {

        public ProgressDialog(Stage owner, Scene scene) {
            this.initStyle(StageStyle.TRANSPARENT); // Stage 没有窗口装饰
            this.initModality(Modality.APPLICATION_MODAL);
            this.initOwner(owner);
            this.setScene(scene);
        }

        public void onShow() {
            sizeToScene();
            centerOnScreen();
            showAndWait();
//            Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
//            stage.setX(primaryScreenBounds.getWidth() - width);
//            stage.setY(primaryScreenBounds.getHeight() - height);            
        }
    }

}

