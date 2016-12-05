package com.xuhai.wngs.beans.shzl;

/**
 * Created by WR on 2014/12/3.
 */
public class ShzlBldDailyBean {
   String id;
   String title;
   String goodsimg;
   String sales;
   String price;

    public String getGoodsid() {
        return id;
    }

    public void setGoodsid(String goodsid) {
        this.id = goodsid;
    }

    public String getGoods() {
        return title;
    }

    public void setGoods(String goods) {
        this.title = goods;
    }

    public String getGoodsimg() {
        return goodsimg;
    }

    public void setGoodsimg(String goodsimg) {
        this.goodsimg = goodsimg;
    }

    public String getSales() {
        return sales;
    }

    public void setSales(String sales) {
        this.sales = sales;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
