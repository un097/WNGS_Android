package com.xuhai.wngs.beans.main;

import java.io.Serializable;

/**
 * Created by changliang on 14-10-10.
 */
public class MainBean implements Serializable {
    private int id;
    private String name;
    private int src;
    private String info;
    private String express;
    private String bbs;



    public void setExpress(String express) {
        this.express = express;
    }

    public String getExpress() {
        return express;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getInfo() {
        return info;
    }
    public String getBbs() {
        return bbs;
    }

    public void setBbs(String bbs) {
        this.bbs = bbs;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSrc() {
        return src;
    }

    public void setSrc(int src) {
        this.src = src;
    }
}
