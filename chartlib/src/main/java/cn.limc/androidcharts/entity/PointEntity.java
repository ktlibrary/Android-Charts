package cn.limc.androidcharts.entity;

/**
 * Created by gustavo on 2015/6/8.
 */
public class PointEntity {

    private String date;
    private int color;
    private String content;
    private boolean drawText;
    private boolean up;

    public boolean isUp() {
        return up;
    }

    public void setUp(boolean up) {
        this.up = up;
    }

    public boolean isDrawText() {
        return drawText;
    }

    public void setDrawText(boolean drawText) {
        this.drawText = drawText;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
