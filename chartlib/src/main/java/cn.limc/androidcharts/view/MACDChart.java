/*
 * MACDChart.java
 * Android-Charts
 *
 * Created by limc on 2014.
 *
 * Copyright 2011 limc.cn All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.limc.androidcharts.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;


import java.util.ArrayList;
import java.util.List;

import cn.limc.androidcharts.ChartConfig;
import cn.limc.androidcharts.entity.DateValueObject;
import cn.limc.androidcharts.entity.HldEnity;
import cn.limc.androidcharts.entity.MACDEntity;
import cn.limc.androidcharts.entity.YylmEntity;
import cn.limc.androidcharts.entity.zb.CptxEnity;

/**
 * <p>
 * en
 * </p>
 * <p>
 * jp
 * </p>
 * <p>
 * cn
 * </p>
 *
 * @author limc
 * @version v1.0 2014/03/17 17:19:45
 */
public class MACDChart extends SlipStickChart {

    public static final int MACD_DISPLAY_TYPE_STICK = 1 << 0;
    public static final int MACD_DISPLAY_TYPE_LINE = 1 << 1;
    public static final int MACD_DISPLAY_TYPE_LINE_STICK = 1 << 2;

    public static final int DEFAULT_POSITIVE_STICK_COLOR = ChartConfig.COLOR_POSITIVE_STICK;
    public static final int DEFAULT_NEGATIVE_STICK_COLOR = ChartConfig.COLOR_NEGATIVE_STICK;
    public static final int DEFAULT_MACD_LINE_COLOR = ChartConfig.COLOR_MACD_MACD_LINE;
    public static final int DEFAULT_DIFF_LINE_COLOR = ChartConfig.COLOR_MACD_DIFF_LINE;
    public static final int DEFAULT_DEA_LINE_COLOR = ChartConfig.COLOR_MACD_DEA_LINE;
    public static final int DEFAULT_MACD_DISPLAY_TYPE = MACD_DISPLAY_TYPE_LINE_STICK;

    private int positiveStickColor = DEFAULT_POSITIVE_STICK_COLOR;
    private int negativeStickColor = DEFAULT_NEGATIVE_STICK_COLOR;
    private int macdLineColor = DEFAULT_MACD_LINE_COLOR;
    private int diffLineColor = DEFAULT_DIFF_LINE_COLOR;
    private int deaLineColor = DEFAULT_DEA_LINE_COLOR;
    private int macdDisplayType = DEFAULT_MACD_DISPLAY_TYPE;
    private List<CptxEnity> cptxList;

    /**
     * <p>
     * Constructor of MACDChart
     * </p>
     * <p>
     * MACDChart类对象的构造函数
     * </p>
     * <p>
     * MACDChartのコンストラクター
     * </p>
     *
     * @param context
     */
    public MACDChart(Context context) {
        super(context);

    }

    /**
     * <p>
     * Constructor of MACDChart
     * </p>
     * <p>
     * MACDChart类对象的构造函数
     * </p>
     * <p>
     * MACDChartのコンストラクター
     * </p>
     *
     * @param context
     * @param attrs
     * @param defStyle
     */
    public MACDChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

    /**
     * <p>
     * Constructor of MACDChart
     * </p>
     * <p>
     * MACDChart类对象的构造函数
     * </p>
     * <p>
     * MACDChartのコンストラクター
     * </p>
     *
     * @param context
     * @param attrs
     */
    public MACDChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    protected void calcValueRange() {
        /*if (stickData == null) {
            return;
        }
        if (stickData.size() <= 0) {
            return;
        }*/
        double maxValue = Double.MIN_VALUE;
        double minValue = Double.MAX_VALUE;

        if(displayFrom >= 0 && displayFrom < stickData.size()){
            MACDEntity first = (MACDEntity) stickData.get(displayFrom < 0 ? 0 : displayFrom);
            if (first != null) {
                maxValue = Math.max(first.getDea(), first.getDiff());
//            maxValue = Math.max(maxValue, first.getMacd());

                minValue = Math.min(first.getDea(), first.getDiff());
//            minValue = Math.min(minValue, first.getMacd());
            }
        }
        // 判断显示为方柱或显示为线条
        for (int i = displayFrom; i < displayFrom + displayNumber; i++) {
            if (i < 0) continue;

            if (i < stickData.size() && i >= 0) {
                MACDEntity macd = (MACDEntity) stickData.get(i);
                if (macd != null) {
                    maxValue = Math.max(macd.getMacd(), maxValue);
                    maxValue = Math.max(macd.getDea(), maxValue);
                    maxValue = Math.max(macd.getDiff(), maxValue);

                    minValue = Math.min(macd.getMacd(), minValue);
                    minValue = Math.min(macd.getDea(), minValue);
                    minValue = Math.min(macd.getDiff(), minValue);
                }
            }

            if(cptxList != null && cptxList.size() > 0){
                HldEnity hldEnity = cptxList.get(i);
                if (hldEnity != null) {
                    maxValue = Math.max(maxValue, hldEnity.high);
                    minValue = Math.min(minValue, hldEnity.low);
                }
            }
        }


        // 最大最小值校正
        double v = (maxValue - minValue) * 0.05f;
        this.maxValue = maxValue + v;
        this.minValue = minValue - v;

//        this.maxValue = maxValue;
//        this.minValue = minValue;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 在K线图上增加均线
        drawLinesData(canvas);

        drawDLJG(canvas);

        drawYylm(canvas);

        drawCpty(canvas);
    }

    private void drawCpty(Canvas canvas) {
        if(cptxList == null || cptxList.size() <= 0)	return;

        float stickWidth = getDataQuadrantPaddingWidth() / displayNumber;
        if(stickWidth < stickSpacing){
            stickSpacing = 1;
        }
        stickWidth -= stickSpacing;
        float widthReal = stickWidth;
        float stickX = getDataQuadrantPaddingStartX();

        float openY, highY, lowY, closeY;
        CptxEnity enity;
        float vWidth = stickWidth;
        float middleY = getDataQuadrantPaddingHeight() / 2;
        mPaintStick.setColor(Color.MAGENTA);
        canvas.drawLine(getDataQuadrantPaddingStartX(), middleY, getDataQuadrantWidth(), middleY, mPaintStick);
        if(stickWidth < 8){
            vWidth = 8;
        }
        Path path = new Path();
        for (int i = displayFrom; i < displayFrom + displayNumber; i++) {
            if (enableRefresh && i < 0) {
                if (Math.abs(i) <= REFRESH_SIZE) {
                    canvas.drawText(REFRESH_TEXT[i + REFRESH_SIZE], stickX, getDataQuadrantPaddingHeight() / 2, paintFont);
                    stickX = stickX + stickSpacing + stickWidth;
                    continue;
                }
            }

            if(cptxList.size() <= i || i < 0)	return;

            enity = cptxList.get(i);
            highY = getValueY(enity.high);
            lowY = getValueY(enity.low);

            mPaintStick.setColor(enity.color);
            if (widthReal >= ChartConfig.LINE_WIDTH) {
                canvas.drawRect(stickX + (stickWidth - widthReal) / 2, highY, stickX + (stickWidth - widthReal) / 2 + widthReal, lowY, mPaintStick);
            }else{
                canvas.drawLine(stickX, highY, stickX, lowY, mPaintStick);
            }


            if(enity.B == 1){
                drawYylmItem(vWidth, stickX, middleY, ChartConfig.COLOR_DAY5, path, canvas);
            }
            if(enity.C == 1){
                drawYylmItem(vWidth, stickX, middleY, ChartConfig.COLOR_DAY10, path, canvas);
            }
            if(enity.F == 1){
                drawYylmItem(vWidth, stickX, middleY, ChartConfig.COLOR_DAY20, path, canvas);
            }


            // next x
            stickX = stickX + stickSpacing + stickWidth;
        }


    }

    @Override
    protected void initAxisX() {
        titleX.clear();
        if (null != stickData && stickData.size() > 0) {
            float average = displayNumber / this.getLongitudeNum();
            int index;
            for (int i = 0; i < this.getLongitudeNum(); i++) {
                index = (int) Math.floor(i * average);
                if (index > displayNumber - 1) {
                    index = displayNumber - 1;
                }

                index = index + displayFrom;
//				if(stickData.size() <= index)	break;
                if (index >= 0 && index < stickData.size()) {
                    titleX.add(stickData.get(index).getDate());
                }
            }
            if (displayFrom + displayNumber - 1 < stickData.size() && displayFrom + displayNumber - 1 >= 0 ) {
                titleX.add(stickData.get(displayFrom + displayNumber - 1).getDate());
            } else {
                titleX.add("");
            }
            super.setLongitudeTitles(titleX);
        } else {
            if (null != yylmList && yylmList.size() > 0) {
                float average = displayNumber / this.getLongitudeNum();
                int index;
                for (int i = 0; i < this.getLongitudeNum(); i++) {
                    index = (int) Math.floor(i * average);
                    if (index > displayNumber - 1) {
                        index = displayNumber - 1;
                    }

                    index = index + displayFrom;
                    if (index >= 0 && index < yylmList.size()) {
                        titleX.add(yylmList.get(index).time);
                    }
                }
                if (displayFrom + displayNumber - 1 < yylmList.size() && displayFrom + displayNumber - 1 >= 0) {
                    titleX.add(yylmList.get(displayFrom + displayNumber - 1).time);
                } else {
                    titleX.add("");
                }
                super.setLongitudeTitles(titleX);
            }


        }

    }


    private List<String> dateList = new ArrayList<>();
    private List<Float> midX = new ArrayList<>();
    private List<Float> midY = new ArrayList<>();
    private List<DateValueObject> dljgList;
    private List<YylmEntity> yylmList;


    private void drawDLJG(Canvas canvas) {
        if (dljgList == null || dljgList.size() <= 0) return;
        if (dateList == null || dateList.size() <= 0) return;
        if (stickData == null || stickData.size() <= 0) return;
        if (midX == null || midX.size() <= 0) return;

        float top = getDataQuadrantStartY();
        float bottom = getDataQuadrantEndY();
        int indexOf;
        paintLine.setTextSize(ChartConfig.LONGITUDE_FONT_SIZE);
        Rect rect = new Rect();
        paintLine.getTextBounds("钝", 0, 1, rect);
        DateValueObject dateValueObject;
        float drawY = bottom;
        Path path = new Path();
        for (int i = 0; i < dljgList.size(); i++) {
            dateValueObject = dljgList.get(i);
            if (dateValueObject != null) {
                indexOf = dateList.indexOf(dateValueObject.getDate());
                if (indexOf != -1 && indexOf < midX.size()) {
                    if (indexOf + displayFrom < stickData.size() && indexOf + displayFrom >= 0) {
                        if (dateValueObject.isUp()) {
                            drawY = top + rect.height() + (i % 2 == 0 ? 0 : rect.height());
                        } else {
                            drawY = bottom - rect.height() - (i % 2 == 0 ? 0 : rect.height());
                        }
                    }
                    paintLine.setColor(dateValueObject.getColor());
                    canvas.drawText(dateValueObject.getValue(), midX.get(indexOf), drawY, paintLine);

//                    path.reset();
//                    float x = midX.get(indexOf);
//                    path.moveTo(x - 15, drawY + 15);
//                    path.lineTo(x + 15, drawY + 15);
//                    path.lineTo(x, drawY - 15);
//                    path.lineTo(x - 15, drawY + 15);
//                    canvas.drawPath(path, paintLine);

                }
            }

        }

    }

    @Override
    protected void drawSticks(Canvas canvas) {

        if (stickData == null || stickData.size() <= 0) return;

        if (macdDisplayType == MACD_DISPLAY_TYPE_LINE) {
            this.drawMacdLine(canvas);
            return;
        }


        mPaintStick.setAntiAlias(true);
        mPaintStick.setStrokeWidth(ChartConfig.LINE_WIDTH);
        midX.clear();
        midY.clear();
        dateList.clear();
        float stickWidth = getDataQuadrantPaddingWidth() / displayNumber;
        if(stickWidth < stickSpacing){
            stickSpacing = 1;
        }
        stickWidth -= stickSpacing;
        float stickX = getDataQuadrantPaddingStartX();

        // 判断显示为方柱或显示为线条
        for (int i = displayFrom; i < displayFrom + displayNumber; i++) {
            if (enableRefresh && i < 0) {
                if (Math.abs(i) <= REFRESH_SIZE) {
                    stickX = stickX + stickSpacing + stickWidth;
                    continue;
                }
            }

            if (i >= stickData.size() || i < 0) break;
            MACDEntity stick = (MACDEntity) stickData.get(i);
            if(stick == null)   continue;

            float highY;
            float lowY;
            if (stick.getMacd() == 0) {
                // 没有值的情况下不绘制
//				continue;
            } else {
                // 柱状线颜色设定
                if (stick.getMacd() > 0) {
                    mPaintStick.setColor(positiveStickColor);
                    highY = getValueY(stick.getMacd());
                    lowY = getValueY(0);

                    midY.add(lowY);
                } else {
                    mPaintStick.setColor(negativeStickColor);
                    highY = getValueY(0);
                    lowY = getValueY(stick.getMacd());
                    midY.add(highY);
                }

                if (macdDisplayType == MACD_DISPLAY_TYPE_STICK) {
                    // 绘制数据，根据宽度判断绘制直线或方柱
                    if (stickWidth > ChartConfig.LINE_WIDTH) {
                        canvas.drawRect(stickX, highY, stickX + stickWidth, lowY, mPaintStick);
                    } else {
                        canvas.drawLine(stickX, highY, stickX, lowY, mPaintStick);
                    }
                } else if (macdDisplayType == MACD_DISPLAY_TYPE_LINE_STICK) {

                    canvas.drawLine(stickX + stickWidth / 2, highY, stickX + stickWidth / 2, lowY, mPaintStick);
                }
            }

            midX.add(stickX + stickWidth / 2);
            dateList.add(stick.getDate());
            // X位移
            stickX = stickX + stickSpacing + stickWidth;
        }
    }

    protected void drawDiffLine(Canvas canvas) {
        if(stickData == null || stickData.size() == 0)  return;

        if (null == this.stickData) {
            return;
        }
        paintLine.setColor(diffLineColor);

        // distance between two points
        float lineLength = getDataQuadrantPaddingWidth() / displayNumber - 1;
        // start point‘s X
        float startX = getDataQuadrantPaddingStartX() + lineLength / 2;
        // start point
        PointF ptFirst = null;
        boolean draw = false;
        for (int i = displayFrom; i < displayFrom + displayNumber; i++) {
            if (enableRefresh && i < 0) {
                if (Math.abs(i) <= REFRESH_SIZE) {
                    startX = startX + stickSpacing + startX;
                    continue;
                }
            }

            if (i >= stickData.size() || i < 0) break;
            MACDEntity entity = (MACDEntity) stickData.get(i);
            float valueY = 0;
            if(entity != null){
                if(!draw && entity.getDea() != 0){
                    draw = true;
                }

                // calculate Y
                valueY = (float) ((1f - (entity.getDiff() - minValue)
                        / (maxValue - minValue)) * getDataQuadrantPaddingHeight())
                        + getDataQuadrantPaddingStartY();

                // if is not last point connect to previous point
                if (draw && i > 0 && i > displayFrom) {
                    canvas.drawLine(ptFirst.x, ptFirst.y, startX, valueY, paintLine);
                }
                // reset
            }
            ptFirst = new PointF(startX, valueY);
            startX = startX + 1 + lineLength;
        }
    }

    protected void drawDeaLine(Canvas canvas) {

        if(stickData == null || stickData.size() == 0)  return;

        paintLine.setColor(deaLineColor);
        // distance between two points
        float lineLength = getDataQuadrantPaddingWidth() / displayNumber - 1;
        // set start point’s X
        float startX = getDataQuadrantPaddingStartX() + lineLength / 2;
        // start point
        PointF ptFirst = null;
        boolean draw = false;
        for (int i = displayFrom; i < displayFrom + displayNumber; i++) {
            if (enableRefresh && i < 0) {
                if (Math.abs(i) <= REFRESH_SIZE) {
                    startX = startX + stickSpacing + startX;
                    continue;
                }
            }

            if (i >= stickData.size() || i < 0) break;
            MACDEntity entity = (MACDEntity) stickData.get(i);
            if (entity != null) {
                if(!draw && entity.getDea() != 0){
                    draw = true;
                }

                // calculate Y
                float valueY = (float) ((1f - (entity.getDea() - minValue)
                        / (maxValue - minValue)) * getDataQuadrantPaddingHeight())
                        + getDataQuadrantPaddingStartY();

                // if is not last point connect to previous point
                if (draw && i > 0 && i > displayFrom) {
                    canvas.drawLine(ptFirst.x, ptFirst.y, startX, valueY, paintLine);
                }
                // reset
                ptFirst = new PointF(startX, valueY);
                startX = startX + 1 + lineLength;
            }
        }
    }

    protected void drawMacdLine(Canvas canvas) {
//        Paint mPaintStick = new Paint();
        mPaintStick.setAntiAlias(true);
        mPaintStick.setColor(macdLineColor);
        mPaintStick.setStrokeWidth(ChartConfig.LINE_WIDTH);

        // distance between two points
        float lineLength = getDataQuadrantPaddingWidth() / displayNumber - 1;
        // set start point’s X
        float startX = getDataQuadrantPaddingStartX() + lineLength / 2;
        // start point
        PointF ptFirst = null;
        for (int i = displayFrom; i < displayFrom + displayNumber; i++) {
            if (i >= stickData.size() || i < 0) break;
            MACDEntity entity = (MACDEntity) stickData.get(i);
            // calculate Y
            float valueY = (float) ((1f - (entity.getMacd() - minValue)
                    / (maxValue - minValue)) * getDataQuadrantPaddingHeight())
                    + getDataQuadrantPaddingStartY();

            // if is not last point connect to previous point
            if (i > displayFrom) {
                canvas.drawLine(ptFirst.x, ptFirst.y, startX, valueY,
                        mPaintStick);
            }
            // reset
            ptFirst = new PointF(startX, valueY);
            startX = startX + 1 + lineLength;
        }
    }

    protected void drawLinesData(Canvas canvas) {

        if (stickData == null) {
            return;
        }
        if (stickData.size() <= 0) {
            return;
        }

        drawDeaLine(canvas);
        drawDiffLine(canvas);
    }

    protected void drawYylm(Canvas canvas) {
        if (yylmList == null || yylmList.isEmpty()) {
            return;
        }


        float stickWidth = getDataQuadrantPaddingWidth() / displayNumber;
        if(stickWidth < stickSpacing){
            stickSpacing = 1;
        }
        stickWidth -= stickSpacing;

        if(stickWidth < 8){
            stickWidth = 8;
        }
        float stickX = getDataQuadrantPaddingStartX();
        float highY = 0f;
        float lowY = getDataQuadrantPaddingHeight() + getDataQuadrantPaddingStartY();

        Path path = new Path();
        for (int i = displayFrom; i < displayFrom + displayNumber; i++) {
            if (enableRefresh && i < 0) {
                if (Math.abs(i) <= REFRESH_SIZE) {
                    stickX = stickX + stickSpacing + stickWidth;
                    continue;
                }
            }

            if (yylmList.size() <= i || i < 0) break;
            YylmEntity stick = yylmList.get(i);

            paintLine.setStyle(Paint.Style.STROKE);
            drawYylmItem(stickWidth, stickX, lowY, stick.color1, path, canvas);
            drawYylmItem(stickWidth, stickX, lowY, stick.color2, path, canvas);
            drawYylmItem(stickWidth, stickX, lowY, stick.color3, path, canvas);
            paintLine.setStyle(Paint.Style.FILL);

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

    private void drawYylmItem(float stickWidth, float stickX, float lowY, int color, Path path, Canvas canvas) {
        if (color != 0) {
            paintBorder.setColor(color);
            path.reset();
            path.moveTo(stickX, lowY);
            path.lineTo(stickX + stickWidth / 2, 20);
            path.lineTo(stickX + stickWidth, lowY);
            canvas.drawPath(path, paintBorder);
        }
    }

    public void setYylmList(List<YylmEntity> yylmList) {
        this.yylmList = yylmList;
    }

    public List<YylmEntity> getYylmList() {
        return yylmList;
    }

    /**
     * @return the positiveStickColor
     */
    public int getPositiveStickColor() {
        return positiveStickColor;
    }

    /**
     * @param positiveStickColor the positiveStickColor to set
     */
    public void setPositiveStickColor(int positiveStickColor) {
        this.positiveStickColor = positiveStickColor;
    }

    /**
     * @return the negativeStickColor
     */
    public int getNegativeStickColor() {
        return negativeStickColor;
    }

    /**
     * @param negativeStickColor the negativeStickColor to set
     */
    public void setNegativeStickColor(int negativeStickColor) {
        this.negativeStickColor = negativeStickColor;
    }

    /**
     * @return the macdLineColor
     */
    public int getMacdLineColor() {
        return macdLineColor;
    }

    /**
     * @param macdLineColor the macdLineColor to set
     */
    public void setMacdLineColor(int macdLineColor) {
        this.macdLineColor = macdLineColor;
    }

    /**
     * @return the diffLineColor
     */
    public int getDiffLineColor() {
        return diffLineColor;
    }

    /**
     * @param diffLineColor the diffLineColor to set
     */
    public void setDiffLineColor(int diffLineColor) {
        this.diffLineColor = diffLineColor;
    }

    /**
     * @return the deaLineColor
     */
    public int getDeaLineColor() {
        return deaLineColor;
    }

    /**
     * @param deaLineColor the deaLineColor to set
     */
    public void setDeaLineColor(int deaLineColor) {
        this.deaLineColor = deaLineColor;
    }

    /**
     * @return the macdDisplayType
     */
    public int getMacdDisplayType() {
        return macdDisplayType;
    }

    /**
     * @param macdDisplayType the macdDisplayType to set
     */
    public void setMacdDisplayType(int macdDisplayType) {
        this.macdDisplayType = macdDisplayType;
    }


    @Override
    public void getPosition(int index) {
        if (positionChangedListener != null) {
            positionChangedListener.showData(index);//拿到位置后的处理
        }
        super.getPosition(index);
    }

    public void setDljgList(List<DateValueObject> dljgList) {
        this.dljgList = dljgList;
    }

    public List<DateValueObject> getDljgList() {
        return dljgList;
    }

    @Override
    protected int getDataSize() {
        if(yylmList != null && yylmList.size() > 0){
            return yylmList.size();
        }
        if(cptxList != null && cptxList.size() > 0){
            return cptxList.size();
        }
        return super.getDataSize();
    }


    @Override
    protected String getDateString(int index) {
        if(yylmList != null && yylmList.size() > 0){
            return yylmList.get(index).time;
        }
        if(cptxList != null && cptxList.size() > 0){
            return cptxList.get(index).date;
        }
        return super.getDateString(index);
    }

    public void setCptxList(List<CptxEnity> cptxList) {
        this.cptxList = cptxList;
    }

    public List<CptxEnity> getCptxList(){
        return cptxList;
    }
}
