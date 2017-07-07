package smartvip.mainStage.center.rightShow.maker;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class TabMakerShowDataSource_check {
    public SimpleIntegerProperty no = new SimpleIntegerProperty();
    public SimpleStringProperty item = new SimpleStringProperty("");
    public SimpleStringProperty subCmd = new SimpleStringProperty("");
    public SimpleStringProperty detail = new SimpleStringProperty("");
    public SimpleStringProperty result = new SimpleStringProperty("");
    public SimpleStringProperty memo = new SimpleStringProperty(""); 
    public SimpleStringProperty sendLen = new SimpleStringProperty(""); 
    public SimpleStringProperty receiveLen = new SimpleStringProperty(""); 
    public SimpleIntegerProperty timeout = new SimpleIntegerProperty();      

    public TabMakerShowDataSource_check() {
    }
    public TabMakerShowDataSource_check(int no, String item, String subCmd, String detail, String result, String memo, String sendLen, String receiveLen, int timeout) {
        this.setNo(no);
        this.setItem(item);
        this.setSubCmd(subCmd);
        this.setDetail(detail);
        this.setResult(result);
        this.setMemo(memo);
        this.setSendLen(sendLen);
        this.setReceiveLen(receiveLen);        
        this.setTimeout(timeout);
    }

    public Integer getNo() {
        return no.get();
    }

    public final void setNo(Integer no) {
        this.no.set(no);
    }

    public String getItem() {
        return item.get();
    }

    public final void setItem(String item) {
        this.item.set(item);
    }

    public String getSubCmd() {
        return subCmd.get();
    }

    public final void setSubCmd(String subCmd) {
        this.subCmd.set(subCmd);
    }    
    
    public String getResult() {
        return result.get();
    }
    
    public String getSendLen() {
        return sendLen.get();
    }

    public String getReceiveLen() {
        return receiveLen.get();
    }  
    
    public final void setSendLen(String sendLen) {
        this.sendLen.set(sendLen);
    } 

    public final void setReceiveLen(String receiveLen) {
        this.receiveLen.set(receiveLen);
    }     

    public final void setResult(String result) {
        this.result.set(result);
    }
    
    public String getMemo() {
        return memo.get();
    }

    public final void setMemo(String memo) {
        this.memo.set(memo);
    }     
 
    public String getDetail() {
        return detail.get();
    }

    public final void setDetail(String detail) {
        this.detail.set(detail);
    }      
    
    public Integer getTimeout() {
        return timeout.get();
    }

    public final void setTimeout(Integer timeout) {
        this.timeout.set(timeout);
    }
}
