package smartvip.mainStage.center.rightShow.maker;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class TabMakerShowDataSource_setParams_CG901A {
    public SimpleIntegerProperty no = new SimpleIntegerProperty();
    public SimpleStringProperty id = new SimpleStringProperty("");
    public SimpleStringProperty sim = new SimpleStringProperty("");
    public SimpleStringProperty vin = new SimpleStringProperty("");
    public SimpleStringProperty key = new SimpleStringProperty("");
    public SimpleStringProperty iv = new SimpleStringProperty("");  
    public SimpleStringProperty code = new SimpleStringProperty("");
    public SimpleStringProperty imsi = new SimpleStringProperty("");
    public SimpleStringProperty time = new SimpleStringProperty("");  
    public SimpleStringProperty operator = new SimpleStringProperty("");  
    public SimpleStringProperty memo = new SimpleStringProperty(""); 

    public TabMakerShowDataSource_setParams_CG901A() {
    }
    public TabMakerShowDataSource_setParams_CG901A(int no, String id, String sim, String vin, String key, String iv, String code, String imsi, String time, String operator, String memo) {
        this.setNo(no);
        this.setId(id);
        this.setSim(sim);
        this.setVin(vin);
        this.setKey(key);
        this.setIv(iv);
        this.setCode(code);
        this.setImsi(imsi);
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
    
    public String getImsi() {
        return imsi.get();
    }

    public final void setImsi(String imsi) {
        this.imsi.set(imsi);
    }    

    public String getKey() {
        return key.get();
    }

    public final void setKey(String key) {
        this.key.set(key);
    }

    public String getIv() {
        return iv.get();
    }

    public final void setIv(String iv) {
        this.iv.set(iv);
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
    
    public String getVin() {
        return vin.get();
    }

    public final void setVin(String vin) {
        this.vin.set(vin);
    }    
    
    public String getMemo() {
        return memo.get();
    }

    public final void setMemo(String memo) {
        this.memo.set(memo);
    }     
    
}
