package smartvip.mainStage.center.rightShow.maker;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class TabMakerShowDataSource_setParams_JL207A {
    public SimpleIntegerProperty no = new SimpleIntegerProperty();
    public SimpleStringProperty id = new SimpleStringProperty("");
    public SimpleStringProperty sim = new SimpleStringProperty("");
    public SimpleStringProperty pulse = new SimpleStringProperty("");
    public SimpleStringProperty ip = new SimpleStringProperty("");
    public SimpleStringProperty port = new SimpleStringProperty("");  
    public SimpleStringProperty code = new SimpleStringProperty("");
    public SimpleStringProperty carID = new SimpleStringProperty("");
    public SimpleStringProperty time = new SimpleStringProperty("");  
    public SimpleStringProperty operator = new SimpleStringProperty("");  
    public SimpleStringProperty memo = new SimpleStringProperty(""); 

    public TabMakerShowDataSource_setParams_JL207A() {
    }
    public TabMakerShowDataSource_setParams_JL207A(int no, String id, String sim, String pulse, String ip, String port, String code, String carID, String time, String operator, String memo) {
        this.setNo(no);
        this.setId(id);
        this.setSim(sim);
        this.setPulse(pulse);
        this.setIp(ip);
        this.setPort(port);
        this.setCode(code);
        this.setCarID(carID);
        this.setTime(time);
        this.setOperator(operator);
        this.setMemo(memo);
    }

    public Integer getNo() {
        return no.get();
    }

    public final void setNo(Integer no) {
        this.no.set(no);
    }

    public String getId() {
        return id.get();
    }

    public final void setId(String id) {
        this.id.set(id);
    }

    public String getSim() {
        return sim.get();
    }

    public final void setSim(String sim) {
        this.sim.set(sim);
    }
    
    public String getCarID() {
        return carID.get();
    }

    public final void setCarID(String carID) {
        this.carID.set(carID);
    }    

    public String getPulse() {
        return pulse.get();
    }

    public final void setPulse(String pulse) {
        this.pulse.set(pulse);
    }

    public String getIp() {
        return ip.get();
    }

    public final void setIp(String ip) {
        this.ip.set(ip);
    }

    public String getCode() {
        return code.get();
    }

    public final void setCode(String code) {
        this.code.set(code);
    }

    public String getTime() {
        return time.get();
    }

    public final void setTime(String time) {
        this.time.set(time);
    }

    public String getOperator() {
        return operator.get();
    }

    public final void setOperator(String operator) {
        this.operator.set(operator);
    }
    
    public String getPort() {
        return port.get();
    }

    public final void setPort(String port) {
        this.port.set(port);
    }    
    
    public String getMemo() {
        return memo.get();
    }

    public final void setMemo(String memo) {
        this.memo.set(memo);
    }     
    
}
