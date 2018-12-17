package cn.limc.androidcharts.view.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.util.AttributeSet;

import java.util.List;

import cn.limc.androidcharts.entity.YylmEntity;
import cn.limc.androidcharts.view.SlipStickChart;

/**
 * Created by pengpeng on 2017/11/20.
 */

public class YylmChart extends SlipStickChart {
    private List<YylmEntity> yylmList;


    public YylmChart(Context context) {
        super(context);
    }

    public YylmChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public YylmChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    protected void drawSticks(Canvas canvas) {
        super.drawSticks(canvas);

        if (yylmList == null || yylmList.isEmpty()) {
            return;
        }


        float stickWidth = getDataQuadrantPaddingWidth() / displayNumber;
        if(stickWidth < stickSpacing){
            stickSpacing = 1;
        }
        stickWidth -= stickSpacing;
        float stickX = getDataQuadrantPaddingStartX();
        float highY = 0f;
        float lowY = getDataQuadrantPaddingHeight() + getDataQuadrantPaddingStartY();

        for (int i = displayFrom; i < displayFrom + displayNumber; i++) {
            if(enableRefresh && i < 0){
                if(Math.abs(i) <= REFRESH_SIZE){
                    stickX = stickX + stickSpacing + stickWidth;
                    continue;
                }
            }


            if (stickData.size() <= i || i < 0) break;
            YylmEntity stick = yylmList.get(i);

            Path path = new Path();
            drawYylm(stickWidth, stickX, lowY, stick.color1, path);
            drawYylm(stickWidth, stickX, lowY, stick.color2, path);
            drawYylm(stickWidth, stickX, lowY, stick.color3, path);
            canvas.drawPath(path, mPaintStick);

            // stick or line?
            /*if (stickWidth > 2f) {
                Path path = new Path();
                drawYylm(stickWidth, stickX, lowY, stick.color1, path);
                drawYylm(stickWidth, stickX, lowY, stick.color2, path);
                drawYylm(stickWidth, stickX, lowY, stick.color3, path);
                canvas.drawPath(path, mPaintStick);


//                canvas.drawRect(stickX, highY, stickX + stickWidth, lowY,
//                        mPaintStick);
            } else {
                canvas.drawLine(stickX, highY, stickX, lowY, mPaintStick);
            }*/

            // next x
            stickX = stickX + stickSpacing + stickWidth;
        }

    }

    private void drawYylm(float stickWidth, float stickX, float lowY, int color, Path path) {
        if(color != 0){
            mPaintStick.setColor(color);
            path.moveTo(stickX, lowY);
            path.lineTo(stickX + stickWidth / 2, 0);
            path.lineTo(stickX + stickWidth, lowY);
        }
    }

    public void setYylmList(List<YylmEntity> yylmList) {
        this.yylmList = yylmList;
    }
}
