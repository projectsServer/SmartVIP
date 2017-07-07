package tool.box.popwindow;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class PopWindow {
    private TextArea text; 
    private final int width = 400;
    private final int height = 200;
    private final Stage stage = new Stage();
    private Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
    
    private Task t = new Task() {
                @Override
                protected Object call() throws Exception {
                    Thread.sleep(20000);
//                    Platform.runLater(stage::close);
                    return "";
                }
            };    
    
    public void addInfo(String info) {
        if(!(info == null || info.equals(""))) {
            this.text.appendText(new SimpleDateFormat("HH:mm:ss").format(new Date()) + "  " + info + "\r\n");
        }
    }
    
    public void clearInfo() {
        this.text.setText("");
    }    
    
    public PopWindow(String title) { 
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("PopWindow.fxml"), ResourceBundle.getBundle("tool.box.raw.property.global", Locale.getDefault()));// 不要放到fxml文件中，未来可做国际化 
            Parent root = (Parent)loader.load(); // Parent root = FXMLLoader.load(getClass().getResource("loginStage/FXMLLogin.fxml"), ResourceBundle.getBundle("raw.property.global", Locale.getDefault()));
            PopWindowController myController = loader.getController();
            text = myController.getTextArea();
        
            Scene scene = new Scene(root, width, height);
            scene.getStylesheets().add("/tool/box/popwindow/PopWindow.css");    // 不要放到fxml文件中，未来可做更换皮肤
            
            this.stage.initStyle(StageStyle.DECORATED); // UNIFIED 模式下不能设置stage 的图标，即便设置也不会显示
            this.stage.getIcons().add(new Image(this.getClass().getResourceAsStream("/tool/box/raw/images/logo.png")));
            this.stage.setScene(scene);
            this.stage.setX(primaryScreenBounds.getWidth() - width - 15);
            this.stage.setY(primaryScreenBounds.getHeight() - height - 35); 
            this.stage.setTitle(title); 
            this.stage.setAlwaysOnTop(true);    
        } catch (IOException ex) {
            Logger.getLogger(PopWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
        
//        
//        
//        BorderPane bp = new BorderPane();
//        Label clear = new Label("[ 清空 ]");
//        Label save = new Label("保存");
//        StackPane sp = new StackPane(save);
//        sp.setAlignment(Pos.CENTER_RIGHT);
//        sp.setPadding(new Insets(3));  
//        
//        HBox hb = new HBox(clear, sp);
//        hb.setAlignment(Pos.CENTER_LEFT);
//        hb.setPadding(new Insets(3)); 
//        bp.setTop(hb);
//        bp.setCenter(this.text);
//        
//        this.text.setText(new SimpleDateFormat("HH:mm:ss").format(new Date()) + "  " + info + "\r\n");
//        this.text.setFont(new Font(12));
////        this.text.setFill(Color.CRIMSON);
//        this.borderPane.setCenter(bp);
//        this.scene.setFill(null);    
////        this.stage.initStyle(StageStyle.TRANSPARENT);
//        this.stage.setScene(scene);
//        this.stage.setX(primaryScreenBounds.getWidth() - width);
//        this.stage.setY(primaryScreenBounds.getHeight() - height - 30); 
//        this.stage.initStyle(StageStyle.UTILITY); // 该模式下不能设置stage 的图标，即便设置也不会显示
//        this.stage.setTitle(title); 
//        this.stage.setAlwaysOnTop(true); 
    } 
    
    public void show() {
        if(!this.stage.isShowing()) {
            this.stage.show();
            new Thread(t).start();              
        }
    }
    
    public void hide() {
        if(this.stage.isShowing()) {
            this.stage.hide();
        }
    }
    
    public void exit() {
        if(this.stage != null) {
            this.stage.close();
        }
    }
    
    public TextArea getTextArea() {
        return text;
    }
}
