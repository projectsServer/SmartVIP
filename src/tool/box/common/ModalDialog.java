package tool.box.common;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import tool.box.common.modaldialog.combobox.LayoutController;

public class ModalDialog {

    public enum Response {NO, YES, CANCEL};
    private static Response buttonSelected = Response.CANCEL;
    private static String ResStr = "";

    static class Dialog extends Stage {
        
        public Dialog(String title, Stage owner, Scene scene, String iconPath) {
            this.setTitle(title);
//            initStyle( StageStyle.UNIFIED );
            this.initModality(Modality.APPLICATION_MODAL);
            this.initOwner(owner);
            this.setResizable(false);
            this.setScene(scene);
            this.getIcons().add(new Image(this.getClass().getResourceAsStream(iconPath)));
        }
        
        public void onShow() {
            sizeToScene();
            centerOnScreen();
            showAndWait();
        }
    }

    static class Message extends Text {
        public Message(String msg) {
            super(msg);
            this.setWrappingWidth(250);
            this.setStyle("-fx-font-size: 12pt;-fx-effect: dropshadow(gaussian , rgba(255,255,255,0.5) , 0, 0, 0, 1 );");
        }
    }

    public static Response show(Stage parentStage, String message, String title, String iconPath) { 
        VBox vBox = new VBox();
        Scene scene = new Scene(vBox);
        final Dialog dial = new Dialog(title, parentStage, scene, iconPath);
        vBox.setPadding(new Insets(10,10,10,10));
        vBox.setSpacing(10);
        Button yesButton = new Button("Yes");
        yesButton.setOnAction((ActionEvent e) -> {
            dial.close();
            buttonSelected = Response.YES;
        });
        Button noButton = new Button("No");
        noButton.setOnAction((ActionEvent e) -> {
            dial.close();
            buttonSelected = Response.NO;
        });
        BorderPane bp = new BorderPane();
        HBox buttons = new HBox();
        buttons.setAlignment(Pos.CENTER);
// buttons.setSpacing( Layout.SPACING );
        buttons.getChildren().addAll(yesButton, noButton);
        bp.setCenter(buttons);
        HBox msg = new HBox();
// msg.setSpacing( Layout.SPACING_SMALL );
        msg.getChildren().addAll(new Message(message));
        vBox.getChildren().addAll(msg, bp);
        dial.onShow();
        return buttonSelected;
    }
    
    public static String[] showInputType(Stage parentStage, String[][] defaultInputAndPrompt, String title, String iconPath) { 
        VBox vBox = new VBox();
        Scene scene = new Scene(vBox);
        String[] ResStrArray = new String[defaultInputAndPrompt.length];
        
        Label tipTop = new Label("");
        Label tipBottom = new Label("");
        TextField input[] = new TextField[defaultInputAndPrompt.length];
        for(int i=0; i<input.length; i++) {
            input[i] = new TextField();
            input[i].setText(defaultInputAndPrompt[i][0]);             
            input[i].setPromptText(defaultInputAndPrompt[i][1]);
            input[i].setFocusTraversable(false);
            input[i].setPrefSize(200, 20);
            input[i].setAlignment(Pos.CENTER);              
        }
        
        final Dialog dial = new Dialog(title, parentStage, scene, iconPath);
        dial.setOnCloseRequest(new EventHandler() {
            @Override
            public void handle(Event event) {
                dial.close();
                ResStrArray[0] = "CANCEL";
            }
        }); 
                
        vBox.setPadding(new Insets(10,10,10,10));
        vBox.setSpacing(10);
        Button yesButton = new Button(" 确 定 ");
        yesButton.setOnAction((ActionEvent e) -> {
            for(int i=0; i<input.length; i++) {
                ResStrArray[i] = input[i].getText().trim();
                if(ResStrArray[i].equals("")) {
                    tipBottom.setText("输入内容存在未输入情况，请确认...");
                } else {
                    if(i == input.length-1) {
                        dial.close();
                    }
                }                
            }

        });
        Button noButton = new Button(" 取 消 ");
        noButton.setOnAction((ActionEvent e) -> {
            dial.close();
            ResStrArray[0] = "CANCEL";
        });
        
        BorderPane borderPane = new BorderPane();
        HBox btnHBox = new HBox(); 
        btnHBox.setAlignment(Pos.CENTER);
        btnHBox.getChildren().addAll(yesButton, noButton);
        btnHBox.setSpacing(20);
        borderPane.setCenter(btnHBox);
        
        VBox contentVBox = new VBox(); 
        contentVBox.setSpacing(10);
        contentVBox.getChildren().addAll(input);
        
        vBox.getChildren().addAll(tipTop, contentVBox, tipBottom, borderPane);
        
        dial.onShow();
        return ResStrArray;
    }    
    
    public static String showInputType(Stage parentStage, String inputFocus, String title, String iconPath) { 
        VBox vBox = new VBox();
        Scene scene = new Scene(vBox);
        String[] resInput = new String[1];
        
        Dialog dial = new Dialog(title, parentStage, scene, iconPath);
        
        TextField input = new TextField();
        input.setFocusTraversable(true);
        input.setPrefSize(400, 40);
        input.setAlignment(Pos.CENTER);
        input.setFont(Font.font("Segoe UI Semibold", FontWeight.NORMAL, 20));
        input.setText(title);
        input.selectAll();
        input.textProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue.length() == 31) {
                resInput[0] = newValue;
                dial.close();
            }
        });
        
        vBox.setPadding(new Insets(30,20,30,20));
        vBox.getChildren().addAll(input);
        
        dial.onShow();
        
        return resInput[0];
    }    
    
    public String[] showInputCombobox(Stage parentStage, String[][] tipAndItems, String title, String iconPath) throws IOException { 
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/tool/box/common/modaldialog/combobox/Layout.fxml"), ResourceBundle.getBundle("tool.box.raw.property.global", Locale.getDefault()));// 不要放到fxml文件中，未来可做国际化 
        Parent root = (Parent)loader.load();  
        LayoutController myController = loader.getController();
        myController.setParams(parentStage, tipAndItems);
        
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/tool/box/common/modaldialog/combobox/layout.css");
        
        new Dialog(title, parentStage, scene, iconPath).onShow();
        
        return myController.getResStrArray();
    }     
   
    public static String[] showInputPasswordType(Stage parentStage, String title, String[][] defaultInputAndPrompt, String iconPath) { 
        VBox vBox = new VBox();
        Scene scene = new Scene(vBox);
        String[] ResStrArray = new String[defaultInputAndPrompt.length];
        
        Label tipTop = new Label("");
        Label tipBottom = new Label("");
        PasswordField input[] = new PasswordField[defaultInputAndPrompt.length];
        for(int i=0; i<input.length; i++) {
            input[i] = new PasswordField();
            input[i].setText(defaultInputAndPrompt[i][0]);             
            input[i].setPromptText(defaultInputAndPrompt[i][1]);
            input[i].setFocusTraversable(false);
            input[i].setPrefSize(200, 20);
            input[i].setAlignment(Pos.CENTER);              
        }
        
        final Dialog dial = new Dialog(title, parentStage, scene, iconPath);
        dial.setOnCloseRequest(new EventHandler() {
            @Override
            public void handle(Event event) {
                dial.close();
                ResStrArray[0] = "CANCEL";
            }
        }); 
                
        vBox.setPadding(new Insets(10,10,10,10));
        vBox.setSpacing(10);
        Button yesButton = new Button(" 确 定 ");
        yesButton.setOnAction((ActionEvent e) -> {
            for(int i=0; i<input.length; i++) {
                ResStrArray[i] = input[i].getText().trim();
                if(ResStrArray[i].equals("")) {
                    tipBottom.setText("输入内容存在未输入情况，请确认...");
                } else {
                    if(i == input.length-1) {
                        if(!input[0].equals(input[1])) {
                            tipBottom.setText("输入两次原始密码不同，请确认...");
                        } else {
                            dial.close();
                        }                       
                    }
                }                
            }

        });
        Button noButton = new Button(" 取 消 ");
        noButton.setOnAction((ActionEvent e) -> {
            dial.close();
            ResStrArray[0] = "CANCEL";
        });
        
        BorderPane borderPane = new BorderPane();
        HBox btnHBox = new HBox(); 
        btnHBox.setAlignment(Pos.CENTER);
        btnHBox.getChildren().addAll(yesButton, noButton);
        btnHBox.setSpacing(20);
        borderPane.setCenter(btnHBox);
        
        VBox contentVBox = new VBox(); 
        contentVBox.setSpacing(10);
        contentVBox.getChildren().addAll(input);
        
        vBox.getChildren().addAll(tipTop, contentVBox, tipBottom, borderPane);
        
        dial.onShow();
        return ResStrArray;
    }    
    
    public static void showMessageType(Stage parentStage, String message, String title, String iconPath) {
        VBox vBox = new VBox(); 
        Scene scene = new Scene(vBox); 
        scene.getStylesheets().add("/smartvip/FXMLLogin.css");
        final Dialog dialog = new Dialog(title, parentStage, scene, iconPath);
        vBox.setPadding(new Insets(20,20,20,20));
        vBox.setSpacing(15);
        vBox.setAlignment(Pos.CENTER);
        
        BorderPane btnPane = new BorderPane(); 
        Button button = new Button(" 确 定 "); 
        button.setAlignment(Pos.CENTER);
        button.setPrefSize(90, 30); 
        button.setCursor(Cursor.HAND);
        button.setOnAction((ActionEvent e) -> { dialog.close(); }); 
        button.setOnKeyReleased((KeyEvent event) -> { if (event.getCode() == KeyCode.ENTER) { dialog.close(); } });       
        btnPane.setCenter(button);
        
        BorderPane messagePane = new BorderPane();
        Label mes = new Label(message);
        mes.setId("mes4passCheck"); 
        messagePane.setCenter(mes);
        
        vBox.getChildren().addAll(messagePane, btnPane);
        dialog.onShow();
    }
    
    static class ProgressDialog extends Stage {
        public ProgressDialog(Stage owner, Scene scene) {
            this.initStyle( StageStyle.TRANSPARENT ); // Stage 没有窗口装饰
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
    public static String showProgressType(Stage parentStage, boolean isIndicator, int timeout) {
        String resStr = "";
        VBox vBox = new VBox(); 
        vBox.setStyle("-fx-background:transparent;"); // VBox 要透明
        
        Scene scene = new Scene(vBox);
        scene.setFill(null);   // Scene要透明
        
        final ProgressDialog progressDialog = new ProgressDialog(parentStage, scene);
        vBox.setPadding(new Insets(0,0,0,0));
        
        final ProgressIndicator pin = new ProgressIndicator();
        pin.setProgress(-1.0f);
            
        vBox.getChildren().addAll(pin);
        vBox.setCursor(Cursor.WAIT);
        
        new Timeline(new KeyFrame(Duration.millis(5000), ae -> {progressDialog.close();})).play();        
        
        progressDialog.onShow();
        
        return resStr;
    }    

    
}
