package smartvip.mainStage.center.leftTopControl.fae;

import java.util.List;
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
import smartvip.mainStage.HostController;
import tool.box.common.Tool;
import smartvip.mainStage.center.leftTopControl.maker.excel.FileCheck4CG901A;
import tool.box.rs232.diy.Utils;

public class TabFAETask4RS232Set {
    private Stage stage;
    private Scene scene; 
    private ProgressDialog progressDialog;
    private ProgressIndicator progressIndicator;
    private VBox vBox;
    private Label close;
    private static String ResStr = "";
    public static boolean ExitFlag = false;
    private TabFAEController tabFAEController;       
    private Task<String> progressTask;
    
    public TabFAETask4RS232Set(Stage stage, TabFAEController tabFAEController) {
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
                Object[] resObjs = null;
                String tmpStr = new String();
                
                ExitFlag = false;
                
                for(int i=0; i<objs.length; i++) {
//                for(int i=0; i<1; i++) {
                    if(ExitFlag) {
                        break;
                    } else {
                        if((boolean)objs[i][1] && (boolean)objs[i][4]) { // checkModel4RadioButton(JL.getText(), CG.getText(), CZ.getText(), ZB.getText(), radioStr);
                            String tip = (String)objs[i][0] + " - " + (String)objs[i][2]; // no.getText() + function.getText() 
                            byte cmd = (byte)paraseModel(model);
                            String content = (String)objs[i][3];
                            int subCmd = Integer.parseInt((String)objs[i][5], 16); // cmd.getText()
                            int sendLen = Integer.parseInt((String)objs[i][6], 10); // sendLen.getText();
                            int recieveLen = Integer.parseInt((String)objs[i][7], 10); // receiveLen.getText();
                            int offset = Integer.parseInt((String)objs[i][8], 10);

                            resObjs = Utils.sendAndReceive(tabFAEController.getHostController(), cmd, subCmd, content, tip, 2, recieveLen, recieveLen, true);
                            if(resObjs[0].toString().contains("_noNull")) { 
                                tabFAEController.addLog(tip + " 设置成功...", null);
                            } 
                        }                        
                    }
                }
                 
                return "DoneOnce";
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
                ExitFlag = true;
                ResStr = "NULL";
            }

        };
        
//        tabFAEController.getLeftTip().textProperty().bind(progressTask.messageProperty());
        if(progressDialog != null && progressTask != null) {
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
//        this.stage = stage;
//        this.tabFAEController = tabFAEController;
//        
//        this.progressIndicator = new ProgressIndicator();  
//        this.progressIndicator.setProgress(-1.0f);
//        this.progressIndicator.setPrefSize(100, 100);           
//        
//        this.vBox = new VBox();
//        this.vBox.setStyle("-fx-background:transparent;"); // VBox 要透明
//        this.vBox.setPadding(new Insets(20, 20, 20, 20));
//        this.vBox.getChildren().addAll(progressIndicator);        
//        
//        this.scene = new Scene(vBox);
//        this.scene.setFill(null);   // Scene要透明
//        
//        this.progressDialog = new ProgressDialog(stage, scene);        
    }
    
    private int paraseModel(String model) {
        if(model.startsWith("JL")) {
            return 0xD4;
        } else if(model.startsWith("CG")) {
            return 0xD1;
        } else if(model.startsWith("CZ")) {
            return 0xD2;
        } else if(model.startsWith("ZB")) {
            return 0xD3;
        }
        return 0x00;
    }
    
}

