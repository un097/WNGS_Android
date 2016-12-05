package com.xuhai.wngs.beans.main;

import java.util.List;

/**
 * Created by renxiangpeng on 15/11/24.
 */
public class MainCityEngBean {
    String eng;
    List<MainCityBean> citylist;

    public String getEng() {
        return eng;
    }

    public void setEng(String eng) {
        this.eng = eng;
    }

    public List<MainCityBean> getCitylist() {
        return citylist;
    }

    public void setCitylist(List<MainCityBean> citylist) {
        this.citylist = citylist;
    }
}
