package smartvip.mainStage.center.rightShow.fae;

import javafx.beans.property.SimpleStringProperty;

public class TabFaeShowDataSource {
    public SimpleStringProperty no = new SimpleStringProperty("");
    public SimpleStringProperty function = new SimpleStringProperty("");
    public SimpleStringProperty params = new SimpleStringProperty("");
    public SimpleStringProperty jl = new SimpleStringProperty("");
    public SimpleStringProperty cg = new SimpleStringProperty("");
    public SimpleStringProperty cz = new SimpleStringProperty("");
    public SimpleStringProperty zb = new SimpleStringProperty("");
    public SimpleStringProperty cmd = new SimpleStringProperty("");
    public SimpleStringProperty eg = new SimpleStringProperty(""); 
    
    

    public TabFaeShowDataSource(String no, String function, String params, String jl, String cg, String cz, String zb, String cmd, String eg) {
        this.setNo(no);
        this.setFunction(function);
        this.setParams(params);
        this.setJl(jl);
        this.setCg(cg);
        this.setCz(cz);
        this.setZb(zb);
        this.setCmd(cmd);
        this.setEg(eg);
    }

    
    public final void setNo(String no) {
        this.no.set(no);
    }  
    public final void setFunction(String function) {
        this.function.set(function);
    }  
    public final void setParams(String params) {
        this.params.set(params);
    }
    public final void setJl(String jl) {
        this.jl.set(jl);
    }  
    public final void setCg(String cg) {
        this.cg.set(cg);
    }  
    public final void setCz(String cz) {
        this.cz.set(cz);
    }  
    public final void setZb(String zb) {
        this.zb.set(zb);
    }  
    public final void setCmd(String cmd) {
        this.cmd.set(cmd);
    }  
    public final void setEg(String eg) {
        this.eg.set(eg);
    }  
    
    public String getNo() {
        return no.get();
    }
    public String getFunction() {
        return function.get();
    }  
    public String getParams() {
        return params.get();
    }
    public String getJl() {
        return jl.get();
    }  
    public String getCg() {
        return cg.get();
    }  
    public String getCz() {
        return cz.get();
    }  
    public String getZb() {
        return zb.get();
    }  
    public String getCmd() {
        return cmd.get();
    }  
    public String getEg() {
        return eg.get();
    }
}
