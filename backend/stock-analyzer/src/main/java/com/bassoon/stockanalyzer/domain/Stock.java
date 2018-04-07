package com.bassoon.stockanalyzer.domain;

import java.io.Serializable;

public class Stock implements Serializable {
    private Long id;//主键key
    private String code;//股票编号
    private String name;//股票名称
    private String market;//行业
    private String region;//地区
    private String belong_to;//中证50/上证500//沪深300
    private double weight;//所占当前板块的权重
    private double current_price;//最新价格
    private double eps;//每股收益(元)
    private double bvps;//每股净资产(元)
    private double roe;//净资产收益率(%)
    private double total_stock;//总股本(亿股)
    private double liqui;//流通股本(亿股)
    private double ltsz;//流通市值(亿元)
    private String information_url;//档案链接,可以继续抓相关的信息

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMarket() {
        return market;
    }

    public void setMarket(String market) {
        this.market = market;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getBelong_to() {
        return belong_to;
    }

    public void setBelong_to(String belong_to) {
        this.belong_to = belong_to;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getCurrent_price() {
        return current_price;
    }

    public void setCurrent_price(double current_price) {
        this.current_price = current_price;
    }

    public double getEps() {
        return eps;
    }

    public void setEps(double eps) {
        this.eps = eps;
    }

    public double getBvps() {
        return bvps;
    }

    public void setBvps(double bvps) {
        this.bvps = bvps;
    }

    public double getRoe() {
        return roe;
    }

    public void setRoe(double roe) {
        this.roe = roe;
    }

    public double getTotal_stock() {
        return total_stock;
    }

    public void setTotal_stock(double total_stock) {
        this.total_stock = total_stock;
    }

    public double getLiqui() {
        return liqui;
    }

    public void setLiqui(double liqui) {
        this.liqui = liqui;
    }

    public double getLtsz() {
        return ltsz;
    }

    public void setLtsz(double ltsz) {
        this.ltsz = ltsz;
    }

    public String getInformation_url() {
        return information_url;
    }

    public void setInformation_url(String information_url) {
        this.information_url = information_url;
    }
}
