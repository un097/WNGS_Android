package com.xuhai.wngs.beans.main;

import java.io.Serializable;

/**
 * Created by renxiangpeng on 15/11/24.
 */
public class MainModdtlBean implements Serializable {
    String funcid;
    String fucname;
    String fucimg;
    String fucbs;
    String fucurl;
    private String info;
    private String express;
    private String bbs;

    public void setFucurl(String fucurl) {
        this.fucurl = fucurl;
    }

    public String getFucurl() {
        return fucurl;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getExpress() {
        return express;
    }

    public void setExpress(String express) {
        this.express = express;
    }

    public String getBbs() {
        return bbs;
    }

    public void setBbs(String bbs) {
        this.bbs = bbs;
    }

    public String getFuncid() {
        return funcid;
    }

    public void setFuncid(String funcid) {
        this.funcid = funcid;
    }

    public String getFucname() {
        return fucname;
    }

    public void setFucname(String fucname) {
        this.fucname = fucname;
    }

    public String getFucimg() {
        return fucimg;
    }

    public void setFucimg(String fucimg) {
        this.fucimg = fucimg;
    }

    public String getFucbs() {
        return fucbs;
    }

    public void setFucbs(String fucbs) {
        this.fucbs = fucbs;
    }
}
