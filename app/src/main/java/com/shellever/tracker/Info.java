package com.shellever.tracker;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by linuxfor on 8/12/2016.
 */
public class Info implements Serializable{
    private double latitude;    //纬度
    private double longitude;   //经度
    private int imgId;
    private String name;
    private String distance;
    private int zan;            //点赞

    public static List<Info> infoList = new ArrayList<Info>();

    static {
        infoList.add(new Info(34.242652, 108.971171, R.mipmap.a01, "英伦贵族小旅馆",
                "距离209米", 1456));
        infoList.add(new Info(34.242952, 108.972171, R.mipmap.a02, "沙井国际洗浴会所",
                "距离897米", 456));
        infoList.add(new Info(34.242852, 108.973171, R.mipmap.a03, "五环服装城",
                "距离249米", 1456));
        infoList.add(new Info(34.242152, 108.971971, R.mipmap.a04, "老米家泡馍小炒",
                "距离679米", 1456));
    }

    public Info(double latitude, double longitude, int imgId, String name, String distance, int zan) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.imgId = imgId;
        this.name = name;
        this.distance = distance;
        this.zan = zan;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getImgId() {
        return imgId;
    }

    public void setImgId(int imgId) {
        this.imgId = imgId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public int getZan() {
        return zan;
    }

    public void setZan(int zan) {
        this.zan = zan;
    }
}
