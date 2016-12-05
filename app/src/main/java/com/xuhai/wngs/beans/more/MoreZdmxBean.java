package com.xuhai.wngs.beans.more;

/**
 * Created by renxiangpeng on 15/11/13.
 */
public class MoreZdmxBean {
    String desc;
    String amount;
    String trandate;
    String transeqno;
    String transtype;
    String processtype;

    public void setProcesstype(String processtype) {
        this.processtype = processtype;
    }

    public String getProcesstype() {
        return processtype;
    }

    public void setTranstype(String transtype) {
        this.transtype = transtype;
    }

    public String getTranstype() {
        return transtype;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTrandate() {
        return trandate;
    }

    public void setTrandate(String trandate) {
        this.trandate = trandate;
    }

    public String getTranseqno() {
        return transeqno;
    }

    public void setTranseqno(String transeqno) {
        this.transeqno = transeqno;
    }
}
