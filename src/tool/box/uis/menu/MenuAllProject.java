package tool.box.uis.menu;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

public class MenuAllProject {
    private final String key;
    
    public MenuAllProject(String key) {
        this.key = key;
    }        
    
    Callback<TableColumn, TableCell> riskCellFactory = new Callback<TableColumn, TableCell>() {
        @Override
        public TableCell call(TableColumn param) {
            final TableCell cell = new TableCell() {
                private Label riskLabel = new Label("");
                @Override
                public void updateItem(Object item, boolean empty) {
                    super.updateItem(item, empty);
                    
                    if (!isEmpty()) {
                        if(item.toString().contains("绿")) {
                            riskLabel.setStyle("-fx-font-family: \"Segoe UI Semibold\"; -fx-font-size: 13px; -fx-label-padding: 3 0 3 0; -fx-text-fill: green; -fx-background-color: green; "); 
                        } else if(item.toString().contains("红")) {
                            riskLabel.setStyle("-fx-font-family: \"Segoe UI Semibold\"; -fx-font-size: 13px; -fx-label-padding: 3 0 3 0; -fx-text-fill: red; -fx-background-color: red; "); 
                        } else if(item.toString().contains("黄")) {
                            riskLabel.setStyle("-fx-font-family: \"Segoe UI Semibold\"; -fx-font-size: 13px; -fx-label-padding: 3 0 3 0; -fx-text-fill: yellow; -fx-background-color: yellow; "); 
                        } else {
                            riskLabel.setStyle("-fx-font-family: \"Segoe UI Semibold\"; -fx-font-size: 13px; -fx-label-padding: 3 0 3 0; -fx-text-fill: white; -fx-background-color: white; "); 
                        }
                        
                        riskLabel.setMaxWidth(60);
                        riskLabel.setMinWidth(60);
                        setGraphic(riskLabel);  
                    }
                }
            };
            return cell;
        }
    };

    
    Callback<TableColumn, TableCell> cellFactory = new Callback<TableColumn, TableCell>() {
        @Override
        public TableCell call(TableColumn param) {
            final TableCell cell = new TableCell() {
                private Label riskLabel = new Label("");;
                @Override
                public void updateItem(Object item, boolean empty) {
                    super.updateItem(item, empty);
                    
                    if (!isEmpty()) {
                        riskLabel.setText(item.toString());
                        riskLabel.setStyle("-fx-font-family: \"Segoe UI Semibold\"; -fx-font-size: 13px; -fx-label-padding: 3 0 3 0;  "); 
                        setGraphic(riskLabel);  
                    }
                }
            };
            return cell;
        }
    };        
    
    // 取得项目的总览信息
    public int getAllProject4Table(ObservableList<Row> data) {
        // 多线程服务器——全部项目总览信息
        String allProject[][] = unitItemsTest();
        
        for (String[] item : allProject) {
            data.add(new Row(item[0], item[1], item[2], item[3], item[4], item[5], item[6], item[7], item[8], item[9]));
        }     
        return data.size();
    }  
    
    public Object[] getTableviewHeader() {
        String[][] header = new String[][] {{"产品类型", "120", "type"}, 
                                            {"产品型号", "100", "model"},
                                            {"客户名称", "120", "custom"},
                                            {"适配车型", "120", "vehicle"}, 
                                            {"原材料成本", "60", "price"},
                                            {"项目当前进度", "200", "progress"},
                                            {"项目状态", "60", "status"}, 
                                            {"风险评估", "55", "risk"}, 
                                            {"技术负责人", "80", "rd"}, 
                                            {"项目经理", "80", "pm"} };
    
        TableColumn[] column = new TableColumn[header.length]; 
        for(int i=0; i<header.length; i++) {
            column[i] = new TableColumn<>(header[i][0]);
            column[i].setMinWidth(Integer.parseInt(header[i][1]));
            column[i].setCellValueFactory(new PropertyValueFactory<>(header[i][2])); 
            if(Integer.parseInt(header[i][1]) == 55) {
                column[i].setCellFactory(riskCellFactory);
            } else {
                column[i].setCellFactory(cellFactory);
            }           
//            column[i].setCellFactory(cellFactory); // 单元状态
        }
        
        return column;
    }    
    
    public class Row {

        private final SimpleStringProperty type;
        private final SimpleStringProperty model;
        private final SimpleStringProperty custom;
        private final SimpleStringProperty vehicle;
        private final SimpleStringProperty price;
        private final SimpleStringProperty progress;
        private final SimpleStringProperty status;
        private final SimpleStringProperty risk;    
        private final SimpleStringProperty rd;
        private final SimpleStringProperty pm;

        private Row(String type, String model, String custom, String vehicle, String price, String status, String progress, String risk, String rd, String pm) {
            this.type = new SimpleStringProperty(type);
            this.model = new SimpleStringProperty(model);
            this.custom = new SimpleStringProperty(custom);
            this.vehicle = new SimpleStringProperty(vehicle);
            this.price = new SimpleStringProperty(price);
            this.progress = new SimpleStringProperty(progress);
            this.status = new SimpleStringProperty(status);
            this.risk = new SimpleStringProperty(risk);
            this.rd = new SimpleStringProperty(rd);
            this.pm = new SimpleStringProperty(pm);
        }

        public String getType() {
            return type.get();
        }
        public String getModel() {
            return model.get();
        }
        public String getCustom() {
            return custom.get();
        }
        public String getVehicle() {
            return vehicle.get();
        }

        public String getPrice() {
            return price.get();
        }
        public String getProgress() {
            return progress.get();
        }
        public String getStatus() {
            return status.get();
        }
        public String getRisk() {
            return risk.get();
        }
        public String getRd() {
            return rd.get();
        }
        public String getPm() {
            return pm.get();
        }

    }    
    
    private String[][] unitItemsTest() {
        String[][] tmp = new String[][] {
            {"组合仪表", "ZB2220A", "一汽解放", "J6P", "328", "正常", "工程样机阶段", "绿牌", "魏丽娜", "邓春云"},
            {"组合仪表", "ZB2220B", "一汽新能源", "AE200", "435", "正常", "工程样机阶段", "绿牌", "薛永刚", "邓雷"},
            {"组合仪表", "ZB2190", "一汽青岛", "JH6", "397", "正常", "原理样机阶段", "绿牌", "魏丽娜", "邓春云"},
            {"组合仪表", "ZB1183", "济南重汽", "JN43", "654", "提前", "DV试验", "绿牌", "臧军望", "王宇"},
            {"组合仪表", "ZB183D", "三一重工", "SY439", "587", "暂停", "DV试验", "黄牌", "冯钢", "王宇"},
            {"组合仪表", "ZB167E", "一汽轿车", "B30", "138", "提前", "等待小批试装", "绿牌", "王博玉", "王博玉"},
            {"组合仪表", "ZB2157G", "一汽客车", "AE3000", "210", "暂停", "等待小批试装", "绿牌", "王博玉", "王博玉"},
            {"车载终端", "CG901", "一汽解放", "J6P", "896", "滞后", "OTS审核", "红牌", "郑祥滨", "邓舸"},
            {"车载终端", "JL207A", "江铃福特", "JH476", "980", "正常", "原理样机", "绿牌", "郑祥滨", "邓舸"},
            {"车载终端", "CZ201A", "一汽解放", "J7", "1876", "提前", "技术输入确认", "绿牌", "郑祥滨", "鄂鸿飞"},
            {"车载终端", "JL901A3", "江淮汽车", "JMC476", "2987", "正常", "等待OTS审核", "绿牌", "邓舸", "邓舸"},
            {"车身网络模块", "CW202B", "西沃客车", "J6P", "589", "正常", "等待批产结项", "绿牌", "王大伟", "王大伟"},
            {"车身网络模块", "CW202A", "西沃客车", "JH6", "614", "正常", "工程样机", "绿牌", "王大伟", "王大伟"},
            {"车身网络模块", "CW203A", "西沃客车", "S500", "879", "正常", "小批试装", "绿牌", "王大伟", "王大伟"},
            {"车速传感器", "CC107B", "一汽解放", "J6P", "15", "正常", "小批试装", "黄牌", "魏景军", "王宇"},
            {"车速传感器", "CC2087A", "一汽解放", "JH6", "17", "正常", "批量验收", "绿牌", "魏景军", "王宇"},
            {"信息服务平台", "CX-11", "通用客户", "N/A", "N/A", "提前", "海创视频接入", "绿牌", "李德贤", "李德贤"}
        };
        return tmp;
    }    
}
