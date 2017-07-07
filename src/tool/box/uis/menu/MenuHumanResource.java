package tool.box.uis.menu;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Callback;

public class MenuHumanResource {
    private final String key;
    
    public MenuHumanResource(String key) {
        this.key = key;
    }        
    
    Callback<TableColumn, TableCell> leftCellFactory = new Callback<TableColumn, TableCell>() {
        @Override
        public TableCell call(TableColumn param) {
            final TableCell cell = new TableCell() {
                private Text text;
                @Override
                public void updateItem(Object item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!isEmpty()) {
                        if(text == null) {
                            text = new Text(item.toString());
                            text.setStyle("-fx-font-family: \"Segoe UI Semibold\";"); 
                            text.setTextAlignment(TextAlignment.LEFT); 
                            text.setWrappingWidth(230);
                            setGraphic(text);  
                        }
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
    
    // 取得人力资源
    public int getHumanResource4Table(ObservableList<Row> data) {
        // 多线程服务器——人力资源
        String allProject[][] = unitItemsTest();
        
        for (String[] item : allProject) {
            data.add(new Row(item[0], item[1], item[2], item[3], item[4], item[5], item[6], item[7], item[8]));
        }     
        return data.size();
    }  
    
    public Object[] getTableviewHeader() {
        String[][] header = new String[][] {{"姓名", "100", "name"}, 
                                            {"入职时间", "100", "workTime"},
                                            {"职业规划", "150", "career"},
                                            {"已完结项目", "250", "donePros"}, 
                                            {"在研项目", "250", "onPros"},
                                            {"项目承担角色", "150", "role"},
                                            {"联系方式", "150", "contact"}, 
                                            {"电子邮件", "150", "email"}, 
                                            {"入职导师", "100", "tutor"} };
    
        TableColumn[] column = new TableColumn[header.length]; 
        for(int i=0; i<header.length; i++) {
            column[i] = new TableColumn<>(header[i][0]);
            column[i].setMinWidth(Integer.parseInt(header[i][1]));
            column[i].setCellValueFactory(new PropertyValueFactory<>(header[i][2])); 
            if(Integer.parseInt(header[i][1]) == 250) {
                column[i].setCellFactory(leftCellFactory);
            } else {
                column[i].setCellFactory(cellFactory);
            }           
//            column[i].setCellFactory(cellFactory); // 单元状态
        }
        
        return column;
    }    
    
    public class Row {
        private final SimpleStringProperty name;
        private final SimpleStringProperty workTime;
        private final SimpleStringProperty career;
        private final SimpleStringProperty donePros;
        private final SimpleStringProperty onPros;
        private final SimpleStringProperty role;
        private final SimpleStringProperty contact;
        private final SimpleStringProperty email;    
        private final SimpleStringProperty tutor;

        private Row(String name, String workTime, String career, String donePros, String onPros, String role, String contact, String email, String tutor) {
            this.name = new SimpleStringProperty(name);
            this.workTime = new SimpleStringProperty(workTime);
            this.career = new SimpleStringProperty(career);
            this.donePros = new SimpleStringProperty(donePros);
            this.onPros = new SimpleStringProperty(onPros);
            this.role = new SimpleStringProperty(role);
            this.contact = new SimpleStringProperty(contact);
            this.email = new SimpleStringProperty(email);
            this.tutor = new SimpleStringProperty(tutor);
        }

        public String getName() {
            return name.get();
        }
        public String getWorkTime() {
            return workTime.get();
        }
        public String getCareer() {
            return career.get();
        }
        public String getDonePros() {
            return donePros.get();
        }
        public String getOnPros() {
            return onPros.get();
        }
        public String getRole() {
            return role.get();
        }
        public String getContact() {
            return contact.get();
        }
        public String getEmail() {
            return email.get();
        }
        public String getTutor() {
            return tutor.get();
        }

    }    
    
    private String[][] unitItemsTest() {
        String[][] tmp = new String[][] {
            {"魏丽娜", "2008.04", "技术负责人",          "ZB2171、ZB2191、ZB1179", "ZB2171、ZB2191、ZB1179", "技术负责人", "13812341234", "xxxxxxx@126.com", "XXXX"},
            {"臧军望", "2008.04", "技术负责人、项目经理", "ZB2171、ZB2191、ZB1179", "ZB2171、ZB2191、ZB1177", "软件工程师", "13812541235", "xxxxxxx@qq.com", "XXXX"},
            {"祝文甫", "2008.04", "技术负责人、项目经理", "ZB2171、ZB2191、ZB1179", "ZB2171、ZB2191、ZB1178", "软件工程师", "13812641236", "xxxxxxx@163.com", "XXXX"},
            {"邓春云", "2008.04", "技术负责人、项目经理", "ZB2171、ZB2191、ZB1179", "ZB2171、ZB2191、ZB1176", "项目经理", "13812371237", "xxxxxxx@foxmail.com", "XXXX"},
            {"王博玉", "2008.04", "技术负责人、项目经理", "ZB2171、ZB2191、ZB1179", "ZB2171、ZB2191、ZB1175", "技术负责人", "13812381238", "xxxxxxx@sina.com", "XXXX"},
            {"冯钢",   "2008.04", "技术负责人",          "ZB2171、ZB2191、ZB1179", "ZB2171、ZB2191、ZB1174", "结构工程师", "13812941239", "xxxxxxx@163.com", "XXXX"},
            {"郑祥滨", "2008.04", "技术负责人",          "JL203C、JL203D、JL903C", "CG901、JL207A、CZ201A", "技术负责人", "13812301230", "xxxxxxx@126.com", "XXXX"},
            {"王大伟", "2008.04", "技术负责人、项目经理", "CW202、CW203、CW204", "CW205、CW206、CW207", "技术负责人", "13812141231", "xxxxxxx@foxmail.com", "XXXX"},
            {"邓雷",   "2008.04", "技术负责人、项目经理", "ZB2171、ZB2191、ZB1179", "ZB2171、ZB2191、ZB1173", "硬件工程师", "13812241232", "xxxxxxx@163.com", "XXXX"},
            {"胡杨",   "2008.04", "技术负责人",          "ZB2171、ZB2191、ZB1179", "ZB2171、ZB2191、ZB1172", "硬件工程师", "13813341233", "xxxxxxx@foxmail.com", "XXXX"},
            {"王宇",   "2008.04", "项目经理",            "ZB2171、ZB2191、ZB1179", "ZB2171、ZB2191、ZB1171", "项目经理", "13812441234", "xxxxxxx@sina.com", "XXXX"},
            {"王晶",   "2008.04", "技术负责人、项目经理", "ZB2171、ZB2191、ZB1179", "ZB2171、ZB1179", "硬件工程师", "13812351235", "xxxxxxx@foxmail.com", "XXXX"},
            {"佟大龙", "2008.04", "技术负责人",          "ZB2171、ZB2191、ZB1179", "ZB2171、ZB1179", "硬件工程师", "13812641236", "xxxxxxx@163.com", "XXXX"},
            {"李翠",   "2008.04", "技术负责人",          "ZB2171", "ZB2171", "硬件工程师", "13812741734", "xxxxxxx@126.com", "XXXX"},
            {"赵英",   "2008.04", "技术负责人",           "ZB2171、ZB2191、ZB1179", "ZB2171、ZB1179", "硬件工程师", "13812381238", "xxxxxxx@foxmail.com", "XXXX"},
            {"李天放", "2008.04", "技术负责人",           "ZB2171、ZB2191", "ZB2171、ZB1179", "硬件工程师", "13819341239", "xxxxxxx@163.com", "XXXX"},
            {"侯颖",   "2008.04", "技术负责人、项目经理", "ZB2171、ZB2191、ZB1179", "ZB2171", "测试工程师", "13810341230", "xxxxxxx@sina.com", "XXXX"},  
            {"栾文迪", "2008.04", "技术负责人",           "ZB2171、ZB2191、ZB1179", "ZB2171", "硬件工程师", "13812141231", "xxxxxxx@foxmail.com", "XXXX"},   
            {"魏景军", "2008.04", "技术负责人",           "ZB2171、ZB2191", "ZB2171", "硬件工程师", "13812341232", "xxxxxxx@163.com", "XXXX"}, 
            {"张文静", "2008.04", "技术负责人",           "ZB2171、ZB2191", "ZB2171", "软件工程师", "13812331233", "xxxxxxx@foxmail.com", "XXXX"},               
            {"孙庆顺", "2008.04", "技术负责人",           "ZB2171、ZB2191", "ZB2171", "结构工程师", "13812441234", "xxxxxxx@sina.com", "XXXX"}
        };
        return tmp;
    }    
}
