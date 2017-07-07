package smartvip.mainStage.center.rightShow.maker;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import smartvip.FXMLLoginController;
import smartvip.mainStage.center.leftTopControl.maker.excel.FileCheck4CG901A;
import smartvip.mainStage.center.leftTopControl.maker.excel.FileCheck4JL207A;
import smartvip.mainStage.center.leftTopControl.maker.excel.FileSetParams4CG901A;
import smartvip.mainStage.center.leftTopControl.maker.excel.FileSetParams4JL207A;
import tool.box.common.LocalStorage;

public class TabMakerShowController implements Initializable  {
    private TabMakerShowDataSource_setParams_CG901A tabMakerShowDataSource;
    
    @FXML private TableView tableView;

    @Override
    public void initialize(URL location, ResourceBundle resources) { // url=xx.fxml文件   rb=xx.properties文件
        System.out.println("TabMakerShowController  initialize");
        
        tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY); // 平铺整个界面
        
        String filePath = LocalStorage.getPropery("OptionFilePath", "");
        if(!filePath.equals("")) {
            File fileTmp = new File(filePath);
                if (fileTmp.exists()) {
                    if(!fileTmp.renameTo(fileTmp)){  
                        System.out.println("文件已经被打开，请先关闭......");
                    } else {    // Tab addInfo excel content by dynamic thread 
                        String[] res = new String[] {LocalStorage.getPropery("OptionFilePathType", ""), LocalStorage.getPropery("OptionFilePathOption", "")};
                        String[][] params = new String[][] {{"cmd", res[0]},{"subCmd", res[1]}, {"user", FXMLLoginController.login.getName()}};
                       
                        if(res[0].contains("CG901A") && res[1].contains("参数设置")) {
                            FileSetParams4CG901A.parseFile(fileTmp, res[0], res[1], "");
                            addTabContent(res[0], res[1], FileSetParams4CG901A.listExcel);                            
                        } else if(res[0].contains("CG901A") && res[1].contains("印制板检测")) {
                            FileCheck4CG901A.parseFile(fileTmp, res[0], res[1], "");
                            addTabContent(res[0], res[1], FileCheck4CG901A.listExcel);                        
                        } else if(res[0].contains("CG901A") && res[1].contains("总检测试")) {
                            FileCheck4CG901A.parseFile(fileTmp, res[0], res[1], "");
                            addTabContent(res[0], res[1], FileCheck4CG901A.listExcel); 
                        } else if(res[0].contains("JL207A") && res[1].contains("参数设置")) {
                            FileSetParams4JL207A.parseFile(fileTmp, res[0], res[1], "");
                            addTabContent(res[0], res[1], FileSetParams4JL207A.listExcel); 
                        } else if(res[0].contains("JL207A") && res[1].contains("印制板检测")) {
                            FileCheck4JL207A.parseFile(fileTmp, res[0], res[1], "");
                            addTabContent(res[0], res[1], FileCheck4JL207A.listExcel);                             
                        } else if(res[0].contains("JL207A") && res[1].contains("总检测试")) {
                            FileCheck4JL207A.parseFile(fileTmp, res[0], res[1], "");
                            addTabContent(res[0], res[1], FileCheck4JL207A.listExcel); 
                        }
                    }
                }            
        }        
    }    

    public TableView getTableView() {
        return tableView;
    }

    public void addTabContent(String model, String step, List<List<Object>> list) {
        
        try{
            if(model.contains("CG901A") && step.contains("参数设置")) {
                tableView.getColumns().clear();
                tableView.getColumns().addAll(getTableviewHeader_setParams_CG901A());

                ObservableList<TabMakerShowDataSource_setParams_CG901A> datas = FXCollections.observableArrayList();
                for(int i=0; i<list.size(); i++) {
                    datas.add(new TabMakerShowDataSource_setParams_CG901A(Integer.valueOf(list.get(i).get(0).toString()), 
                                                        String.valueOf(list.get(i).get(1)),
                                                        String.valueOf(list.get(i).get(2)),
                                                        String.valueOf(list.get(i).get(3)),
                                                        String.valueOf(list.get(i).get(4)),
                                                        String.valueOf(list.get(i).get(5)),
                                                        String.valueOf(list.get(i).get(6)),
                                                        String.valueOf(list.get(i).get(7)),                    
                                                        String.valueOf(list.get(i).get(8)),
                                                        String.valueOf(list.get(i).get(9)),
                                                        String.valueOf(list.get(i).get(10)))); 
                }
                tableView.setItems(datas);
            } else if(model.contains("CG901A") && step.contains("印制板检测")) {
                tableView.getColumns().clear();
                tableView.getColumns().addAll(getTableviewHeader_check());

                ObservableList<TabMakerShowDataSource_check> datas = FXCollections.observableArrayList();
                for(int i=0; i<list.size(); i++) {
                    datas.add(new TabMakerShowDataSource_check(
                                                        Integer.valueOf(list.get(i).get(0).toString()), 
                                                        String.valueOf(list.get(i).get(1)),
                                                        String.valueOf(list.get(i).get(2)),
                                                        String.valueOf(list.get(i).get(3)),
                                                        String.valueOf(list.get(i).get(4)),
                                                        String.valueOf(list.get(i).get(5)),
                                                        String.valueOf(list.get(i).get(6)),
                                                        String.valueOf(list.get(i).get(7)),
                                                        Integer.valueOf(list.get(i).get(8).toString()))); 
                }
                tableView.setItems(datas);            
            } else if(model.contains("CG901A") && step.contains("总检测试")) {
                tableView.getColumns().clear();
                tableView.getColumns().addAll(getTableviewHeader_check());

                ObservableList<TabMakerShowDataSource_check> datas = FXCollections.observableArrayList();
                for(int i=0; i<list.size(); i++) {
                    datas.add(new TabMakerShowDataSource_check(Integer.valueOf(list.get(i).get(0).toString()), 
                                                        String.valueOf(list.get(i).get(1)),
                                                        String.valueOf(list.get(i).get(2)),
                                                        String.valueOf(list.get(i).get(3)),
                                                        String.valueOf(list.get(i).get(4)),
                                                        String.valueOf(list.get(i).get(5)),
                                                        String.valueOf(list.get(i).get(6)),
                                                        String.valueOf(list.get(i).get(7)),
                                                        Integer.valueOf(list.get(i).get(8).toString())));                 
                }
                tableView.setItems(datas); 
            } else if(model.contains("JL207A") && step.contains("参数设置")) {
                tableView.getColumns().clear();
                tableView.getColumns().addAll(getTableviewHeader_setParams_JL207A());

                ObservableList<TabMakerShowDataSource_setParams_JL207A> datas = FXCollections.observableArrayList();
                for(int i=0; i<list.size(); i++) {
                    datas.add(new TabMakerShowDataSource_setParams_JL207A(Integer.valueOf(list.get(i).get(0).toString()), 
                                                        String.valueOf(list.get(i).get(1)),
                                                        String.valueOf(list.get(i).get(2)),
                                                        String.valueOf(list.get(i).get(3)),
                                                        String.valueOf(list.get(i).get(4)),
                                                        String.valueOf(list.get(i).get(5)),
                                                        String.valueOf(list.get(i).get(6)),
                                                        String.valueOf(list.get(i).get(7)),                    
                                                        String.valueOf(list.get(i).get(8)),
                                                        String.valueOf(list.get(i).get(9)),
                                                        String.valueOf(list.get(i).get(10)))); 
                }
                tableView.setItems(datas);
            } else if(model.contains("JL207A") && (step.contains("总检测试") || step.contains("印制板检测")) ) {
                tableView.getColumns().clear();
                tableView.getColumns().addAll(getTableviewHeader_check());

                ObservableList<TabMakerShowDataSource_check> datas = FXCollections.observableArrayList();
                for(int i=0; i<list.size(); i++) {
                    datas.add(new TabMakerShowDataSource_check(Integer.valueOf(list.get(i).get(0).toString()), 
                                                        String.valueOf(list.get(i).get(1)),
                                                        String.valueOf(list.get(i).get(2)),
                                                        String.valueOf(list.get(i).get(3)),
                                                        String.valueOf(list.get(i).get(4)),
                                                        String.valueOf(list.get(i).get(5)),
                                                        String.valueOf(list.get(i).get(6)),
                                                        String.valueOf(list.get(i).get(7)),
                                                        Integer.valueOf(list.get(i).get(8).toString())));               
                }
                tableView.setItems(datas); 
            }
        } catch (Exception exception) {
            tableView.setItems(null);
        }
    }
    
    public Object[] getTableviewHeader_setParams_CG901A() {
        String[][] header = new String[][] {{"序号", "20", "no"},
                                            {"10位ID", "90", "id"},
                                            {"SIM卡号", "120", "sim"},
                                            {"VIN/ICCID", "160", "vin"},
                                            {"16位KEY", "140", "key"},
                                            {"16位IV", "140", "iv"},
                                            {"客户代码", "30", "code"},
                                            {"IMSI", "120", "imsi"},
                                            {"失效时间", "180", "time"},
                                            {"操作者", "180", "operator"},
                                            {"备注", "100", "memo"}};
        
        TableColumn[] column = new TableColumn[header.length]; 
        for(int i=0; i<header.length; i++) {
            column[i] = new TableColumn<>(header[i][0]);
            column[i].setMinWidth(Integer.parseInt(header[i][1]));
            column[i].setCellValueFactory(new PropertyValueFactory<>(header[i][2]));   
//            column[i].setCellFactory(cellFactory); // 单元状态
        }
        
        return column;
    }   
    
    Callback<TableColumn, TableCell> cellFactory4CheckResult = new Callback<TableColumn, TableCell>() {
        @Override
        public TableCell call(TableColumn param) {
            final TableCell cell = new TableCell() {
                
                @Override
                public void updateItem(Object item, boolean empty) {
                    super.updateItem(item, empty);
                    
                    if(item == null) {
                        setText(null);
                        setGraphic(null);
                    }else {
                        final Label result = new Label("");
                        if (!isEmpty()) {
                            if(item.toString().equals("-")) {
                                result.setText(item.toString());
                                result.setStyle("-fx-label-padding: 5 0 5 0; -fx-background-color: transparent; ");
                            } else if(item.toString().equals("OK")) {
                                result.setText("正常");
                                result.setStyle("-fx-label-padding: 5 0 5 0; -fx-background-color: green; "); 
                            } else if(item.toString().equals("N/A")) {
                                result.setText("需判定");
                                result.setStyle("-fx-label-padding: 5 0 5 0; -fx-background-color: yellow; ");                             
                            } else {
                                result.setText("错误");
                                result.setStyle("-fx-label-padding: 5 0 5 0; -fx-background-color: red; "); 
                            }

                            result.setAlignment(Pos.CENTER); 
                            result.setMaxWidth(60);
                            result.setMinWidth(60);

                            setGraphic(result);  
                        }                        
                    }
                }
            };
            return cell;
        }
    }; 
    public Object[] getTableviewHeader_check() {
        String[][] header = new String[][] {{"序号", "40", "no"},
                                            {"检测项目", "150", "item"},
                                            {"命令字", "60", "subCmd"},
                                            {"检测返回值/状态", "200", "detail"},
                                            {"检测结果", "100", "result"},
                                            {"检测合格依据", "300", "memo"}, 
                                            {"发送长度", "60", "sendLen"},
                                            {"接收长度", "60", "receiveLen"},
                                            {"接收超时", "60", "timeout"}};
        
        TableColumn[] column = new TableColumn[header.length]; 
        for(int i=0; i<header.length; i++) {
            column[i] = new TableColumn<>(header[i][0]);
            column[i].setMinWidth(Integer.parseInt(header[i][1]));
            column[i].setCellValueFactory(new PropertyValueFactory<>(header[i][2])); 
            if(i == 4) {
                column[i].setCellFactory(cellFactory4CheckResult);
            }
        }
        
        return column;
    } 
    
    
    public void update_setParams_CG901A(int index, List<Object> list, boolean isScroll) { // update line
        tableView.getItems().set(index, new TabMakerShowDataSource_setParams_CG901A(
                                            Integer.valueOf(list.get(0).toString()), 
                                            String.valueOf(list.get(1)),
                                            String.valueOf(list.get(2)),
                                            String.valueOf(list.get(3)),
                                            String.valueOf(list.get(4)),
                                            String.valueOf(list.get(5)),
                                            String.valueOf(list.get(6)),
                                            String.valueOf(list.get(7)),                    
                                            String.valueOf(list.get(8)),
                                            String.valueOf(list.get(9)),
                                            String.valueOf(list.get(10))));
        tableView.getSelectionModel().select(index);
    }
    
    public void update_setParams_JL207A(int index, List<Object> list, boolean isScroll) { // update line
        tableView.getItems().set(index, new TabMakerShowDataSource_setParams_JL207A(
                                            Integer.valueOf(list.get(0).toString()), 
                                            String.valueOf(list.get(1)),
                                            String.valueOf(list.get(2)),
                                            String.valueOf(list.get(3)),
                                            String.valueOf(list.get(4)),
                                            String.valueOf(list.get(5)),
                                            String.valueOf(list.get(6)),
                                            String.valueOf(list.get(7)),                    
                                            String.valueOf(list.get(8)),
                                            String.valueOf(list.get(9)),
                                            String.valueOf(list.get(10))));
        tableView.getSelectionModel().select(index);
    }    
    
    public void update_check_CG901A(int index, List<Object> list, boolean isScroll) { // update column
        tableView.getItems().set(index, new TabMakerShowDataSource_check(
                                            Integer.valueOf(list.get(0).toString()), 
                                            String.valueOf(list.get(1)),
                                            String.valueOf(list.get(2)),
                                            String.valueOf(list.get(3)),
                                            String.valueOf(list.get(4)),
                                            String.valueOf(list.get(5)),
                                            String.valueOf(list.get(6)),
                                            String.valueOf(list.get(7)),
                                            Integer.valueOf(list.get(8).toString())));

        if(!String.valueOf(list.get(4)).toUpperCase().equals("OK") && 
           !String.valueOf(list.get(4)).toUpperCase().equals("N/A") && 
           !String.valueOf(list.get(4)).toUpperCase().equals("-")) {
            tableView.getSelectionModel().select(index);
        }
    } 
    
    public void update_check_JL207A(int index, List<Object> list, boolean isScroll) { // update column
        tableView.getItems().set(index, new TabMakerShowDataSource_check(
                                            Integer.valueOf(list.get(0).toString()), 
                                            String.valueOf(list.get(1)),
                                            String.valueOf(list.get(2)),
                                            String.valueOf(list.get(3)),
                                            String.valueOf(list.get(4)),
                                            String.valueOf(list.get(5)),
                                            String.valueOf(list.get(6)),
                                            String.valueOf(list.get(7)),
                                            Integer.valueOf(list.get(8).toString())));

        if(!String.valueOf(list.get(4)).toUpperCase().equals("OK") && 
           !String.valueOf(list.get(4)).toUpperCase().equals("N/A") && 
           !String.valueOf(list.get(4)).toUpperCase().equals("-")) {
            tableView.getSelectionModel().select(index);
        }
    }    
    
     public Object[] getTableviewHeader_setParams_JL207A() {
        String[][] header = new String[][] {{"序号", "25", "no"},
                                            {"10位ID", "100", "id"},
                                            {"SIM卡号", "120", "sim"},
                                            {"脉冲系数", "50", "pulse"},
                                            {"平台IP/域名", "150", "ip"},
                                            {"平台端口号", "50", "port"},
                                            {"客户代码", "40", "code"},
                                            {"车牌号码", "100", "carID"},
                                            {"失效时间", "180", "time"},
                                            {"操作者", "180", "operator"},
                                            {"备注", "100", "memo"}};
        
        TableColumn[] column = new TableColumn[header.length]; 
        for(int i=0; i<header.length; i++) {
            column[i] = new TableColumn<>(header[i][0]);
            column[i].setMinWidth(Integer.parseInt(header[i][1]));
            column[i].setCellValueFactory(new PropertyValueFactory<>(header[i][2]));   
//            column[i].setCellFactory(cellFactory); // 单元状态
        }
        
        return column;
    }       
     

}
