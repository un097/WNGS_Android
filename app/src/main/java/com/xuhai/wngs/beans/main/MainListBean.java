package com.xuhai.wngs.beans.main;

import java.util.List;

/**
 * Created by renxiangpeng on 15/11/24.
 */
public class MainListBean {
    String modid;
    List<MainModdtlBean> moddtl;

    public String getModid() {
        return modid;
    }

    public void setModid(String modid) {
        this.modid = modid;
    }

    public List<MainModdtlBean> getModdtl() {
        return moddtl;
    }

    public void setModdtl(List<MainModdtlBean> moddtl) {
        this.moddtl = moddtl;
    }
}
