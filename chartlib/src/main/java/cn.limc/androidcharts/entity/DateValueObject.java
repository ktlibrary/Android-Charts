package cn.limc.androidcharts.entity;

/**
 * Created by pengpeng on 2016/11/24.
 */

public class DateValueObject {
    private String date;
    private String value;
    private int color;
    private boolean up;

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isUp() {
        return up;
    }

    public void setUp(boolean up) {
        this.up = up;
    }
}
