package com.xuhai.wngs.beans.sjfw;

import com.google.gson.annotations.SerializedName;

/**
 * Created by renxiangpeng on 15/11/20.
 */
public class SjfwClassBean {
    String classid;

    @SerializedName("class")
    String class1;

    public String getClassid() {
        return classid;
    }

    public void setClassid(String classid) {
        this.classid = classid;
    }

    public String getClass1() {
        return class1;
    }

    public void setClass1(String class1) {
        this.class1 = class1;
    }
}
