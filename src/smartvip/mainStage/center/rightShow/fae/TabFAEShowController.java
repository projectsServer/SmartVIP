package smartvip.mainStage.center.rightShow.fae;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import tool.box.poi.excel.File4FAEShow;

public class TabFAEShowController implements Initializable  {
    private int contentStartIndex;
    private int contentRowCellsCount;
    private int contentRowLinesCount;
    
    @FXML private GridPane gridPane;
    @FXML private ScrollPane scrollPane;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) { // url=xx.fxml文件   rb=xx.properties文件
        String resParseFile = File4FAEShow.parseFile(new File(resources.getString("SmartVIP_file")), 1 , "FAEShow");
        if(resParseFile.contains("ok")) {  // 读配置文件，决定布局的内容
            this.addUis2Pane(File4FAEShow.listExcel);
        } else {
            System.out.println(resParseFile);
        }
    }

    private void addUis2Pane(List<List<Object>> listExcel) {
        gridPaneAddHeader(gridPane); // add gridPane header
        gridPaneAddContent(gridPane, listExcel);
    }   
    
    public void gridPaneAddHeader(GridPane gridPane) {
        String[] line_1 = new String[] {"序号", "功能描述", "参数", "适用产品", "", "", "", "举例", "命令字", "发送长度", "接收长度"};
        for(int i=0; i<line_1.length; i++) {
            Label tip = new Label(line_1[i]);
            tip.setStyle("-fx-font-size: 9pt; -fx-padding: 2 4 2 4; -fx-alignment: CENTER; -fx-font-family: \"Segoe UI Semibold\";");
            if(i == 3) {
                HBox box = new HBox(tip);
                box.setAlignment(Pos.CENTER);
                gridPane.add(box, i, 0, 4, 1); // obj col row  spanCol spanRow
            } else {
                gridPane.add(tip, i, 0, 1, 2); // obj col row  spanCol spanRow
            }
            switch (i) {
                case 0: tip.setMinSize(40, 20); break;
                case 1: tip.setMinSize(200, 20); break;
                case 2: tip.setMinSize(200, 20); break;
                case 7: tip.setMinSize(200, 20); break;
                case 8: tip.setMinSize(50, 20); break;
                case 9: tip.setMinSize(50, 20); break;                
                case 10: tip.setMinSize(50, 20); break;
                default: tip.setMinSize(40, 20); break;
            }
        }
        String[] line_2 = new String[] {"记录仪", "模块", "中控", "仪表"};
        for(int i=0; i<line_2.length; i++) {
            Label tip = new Label(line_2[i]);
            tip.setStyle("-fx-font-size: 9pt; -fx-padding: 2 4 2 4; -fx-alignment: CENTER; -fx-font-family: 'Segoe UI Semibold';");
            tip.setMinSize(40, 20);
            gridPane.add(tip, 3+i, 1, 1, 1); // obj col row  spanCol spanRow
        }  
        
        gridPane.add(new Separator(), 0, 2, line_1.length, 1);
        
        contentStartIndex = gridPane.getChildren().size();
    }  
    public void gridPaneAddContent(GridPane gridPane, List<List<Object>> listExcel) {
        int rowIndex = 3;
        
        contentRowCellsCount = listExcel.get(0).size();
        contentRowLinesCount = listExcel.size();
        
        for(int i=0; i<listExcel.size(); i++) { // add row
            for(int j=0; j<listExcel.get(i).size(); j++) { // add column
                if(j == 1) { // add 1 and 2 at same time
                    CheckBox cb = new CheckBox(listExcel.get(i).get(j).toString());
                    TextField tf = new TextField("");

                    cb.selectedProperty().addListener((ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) -> {
                                tf.setDisable(old_val);
                            });
                    cb.setStyle("-fx-font-size: 11pt; -fx-alignment: CENTER; ");
                    gridPane.add(cb, j++, i + rowIndex); // obj col row  spanCol spanRow, and j++

                    tf.setDisable(true);
                    tf.setAlignment(Pos.CENTER);
                    tf.setMinWidth(200);
                    tf.setStyle("-fx-font-size: 11pt; -fx-padding: 2 0 2 0;  -fx-alignment: CENTER; ");
                    tf.setPromptText("读取参数 或 设置参数");
                    gridPane.add(tf, j, i + rowIndex); // obj col row  spanCol spanRow
                    
                } else {
                    Label tip = new Label(listExcel.get(i).get(j).toString());
                    tip.setStyle("-fx-font-size: 11pt; -fx-padding: 5 4 5 4; -fx-alignment: CENTER; -fx-font-family: 'Segoe UI Semibold';");
                    gridPane.add(tip, j, i + rowIndex); // obj col row  spanCol spanRow    
                    
                    switch (j) {
                        case 0: tip.setMinSize(40, 20); break;
                        case 3: case 4: case 5: case 6: tip.setMinSize(40, 20); break;
                        case 8: tip.setMinSize(50, 20); break;
                        case 9: tip.setMinSize(50, 20); break;
                        case 10: tip.setMinSize(50, 20); break;
                    }                    
                }
            }
        }
        
    }
    
    public GridPane getGridPane() {
        return gridPane;
    }    
    
    public ScrollPane getScrollPane() {
        return scrollPane;
    }
    
    public int getContentStartIndex() {
        return this.contentStartIndex;
    }
    
    public int getContentRowCellsCount() {
        return this.contentRowCellsCount;
    }   
    
    public int getContentRowLinesCount() {
        return this.contentRowLinesCount;
    }     
    
}