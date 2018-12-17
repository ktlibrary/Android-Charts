package cn.limc.androidcharts.entity;

/**
 * Created by pengpeng on 16/8/24.
 */
public class DKTDEntity {

    private float high;
    private float low;
    private float highP;
    private float lowP;
    private String date;
    private int stickColor;// 竖线颜色
    private int upColor;//上边短线颜色
    private int downColor;//
    private int arrowType;// 0-没有, 1-上方, 2-下方
    private boolean drawText;

    public boolean isDrawText() {
        return drawText;
    }

    public void setDrawText(boolean drawText) {
        this.drawText = drawText;
    }

    public float getHighP() {
        return highP;
    }

    public void setHighP(float highP) {
        this.highP = highP;
    }

    public float getLowP() {
        return lowP;
    }

    public void setLowP(float lowP) {
        this.lowP = lowP;
    }

    public float getHigh() {
        return high;
    }

    public void setHigh(float high) {
        this.high = high;
    }

    public float getLow() {
        return low;
    }

    public void setLow(float low) {
        this.low = low;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getStickColor() {
        return stickColor;
    }

    public void setStickColor(int stickColor) {
        this.stickColor = stickColor;
    }

    public int getUpColor() {
        return upColor;
    }

    public void setUpColor(int upColor) {
        this.upColor = upColor;
    }

    public int getDownColor() {
        return downColor;
    }

    public void setDownColor(int downColor) {
        this.downColor = downColor;
    }

    public int getArrowType() {
        return arrowType;
    }

    public void setArrowType(int arrowType) {
        this.arrowType = arrowType;
    }
}
