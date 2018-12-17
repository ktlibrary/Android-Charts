/*
 * SlipLineChart.java
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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;

import com.gustavo.chartlibrary.R;

import java.util.ArrayList;
import java.util.List;

import cn.limc.androidcharts.ChartConfig;
import cn.limc.androidcharts.entity.DateValueEntity;
import cn.limc.androidcharts.entity.DateValueObject;
import cn.limc.androidcharts.entity.LineEntity;
import cn.limc.androidcharts.entity.PointEntity;

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
 * @version v1.0 2014/01/21 14:20:35
 */
public class MinuteLineChart extends GridChart {

    private int color_area = ChartConfig.COLOR_AREA;
    private boolean isDrawArea = false;
    private float currentEndX;
    /**
     * <p>
     * data to draw lines
     * </p>
     * <p>
     * ラインを書く用データ
     * </p>
     * <p>
     * 绘制线条用的数据
     * </p>
     */
    protected List<LineEntity<DateValueEntity>> linesData;

    private List<PointEntity> pointsData;
    /**
     * <p>
     * min value of Y axis
     * </p>
     * <p>
     * Y軸の最小値
     * </p>
     * <p>
     * Y的最小表示值
     * </p>
     */
    protected double minValue;

    /**
     * <p>
     * max value of Y axis
     * </p>
     * <p>
     * Y軸の最大値
     * </p>
     * <p>
     * Y的最大表示值
     * </p>
     */
    protected double maxValue;


    static int pointDis = 10;
    static float textDis = 15;


    /*
     * (non-Javadoc)
     *
     * @param context
     *
     * @see GridChart#GridChart(Context)
     */
    public MinuteLineChart(Context context) {
        super(context);
    }

    /*
     * (non-Javadoc)
     *
     * @param context
     *
     * @param attrs
     *
     * @param defStyle
     *
     * @see GridChart#GridChart(Context,
     * AttributeSet, int)
     */
    public MinuteLineChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /*
     * (non-Javadoc)
     *
     * @param context
     *
     * @param attrs
     *
     *
     *
     * @see GridChart#GridChart(Context,
     * AttributeSet)
     */
    public MinuteLineChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected void calcDataValueRange() {
        double maxValue = Double.MIN_VALUE;
        double minValue = Double.MAX_VALUE;
        // 逐条输出MA线
        for (int i = 0; i < this.linesData.size(); i++) {
            LineEntity<DateValueEntity> line = this.linesData.get(i);
            if (line != null && line.getLineData().size() > 0) {
                for (int j = displayFrom; j < displayFrom + displayNumber; j++) {
                    if(j < 0)   continue;

                    if (line.getLineData().size() <= j || j < 0)
                        break;
                    DateValueEntity lineData = line.getLineData().get(j);
                    if(lineData.isDrawEnable()){
                        if (lineData.getValue() < minValue) {
                            minValue = lineData.getValue();
                        }

                        if (lineData.getValue() > maxValue) {
                            maxValue = lineData.getValue();
                        }
                    }

                }
            }
        }
        this.maxValue = maxValue;
        this.minValue = minValue;
    }

    protected void calcValueRangePaddingZero() {
        double midTemp = midValue;
        double maxTemp = maxValue;
        double minTemp = minValue;

        if (noData) {
            maxTemp = midTemp * (1 + 0.1);
            minTemp = midTemp * (1 - 0.1);

        } else {
            if (maxTemp - midTemp > midTemp - minTemp) {
                minTemp = midTemp - (maxTemp - midTemp);
            } else if (maxTemp - midTemp < midTemp - minTemp) {
                maxTemp = midTemp + (midTemp - minTemp);
            } else {

            }
        }

//        double v = (maxTemp - minTemp) * 0.05;
//        maxTemp += v;
//        minTemp -= v;

        this.maxValue = maxTemp;
        this.minValue = minTemp;

    }

    protected void calcValueRangeFormatForAxis() {
        // int rate = 1;
        double rate = (this.maxValue - this.minValue) / (this.latitudeNum);
        // 等分轴修正
        if (this.latitudeNum > 0
                && (long) (this.maxValue - this.minValue)
                % (this.latitudeNum * rate) != 0) {
            // 最大值加上轴差
            this.maxValue = this.maxValue
                    + (this.latitudeNum * rate)
                    - ((long) (this.maxValue - this.minValue) % (this.latitudeNum * rate));
        }
    }

    protected void calcValueRange() {
        if (null == this.linesData) {
            this.maxValue = 0;
            this.minValue = 0;
            return;
        }
        if (this.linesData.size() > 0) {
            this.calcDataValueRange();
        } else {
            this.maxValue = 0;
            this.minValue = 0;
        }
        this.calcValueRangePaddingZero();
//         this.calcValueRangeFormatForAxis();
    }

    /*
     * (non-Javadoc)
     *
     * <p>Called when is going to draw this chart<p> <p>チャートを書く前、メソッドを呼ぶ<p>
     * <p>绘制图表时调用<p>
     *
     * @param canvas
     *
     * @see android.view.View#onDraw(android.graphics.Canvas)
     */
    @Override
    protected void onDraw(Canvas canvas) {
        initAxisY();
        if (customLongitudeTitles == null || customLongitudeTitles.size() <= 0) {
            initAxisX();
        }

        if(isDrawArea){
            drawAreas(canvas);
        }
        drawMacdSticks(canvas);

        // draw lines
        drawLines(canvas);

        drawPoints(canvas);

        drawDLJG(canvas);

        drawLimitLine(canvas);

        drawFace(canvas);

        super.onDraw(canvas);

    }


    public static class FaceEntity{
        public String date;
        /**
         * 1 - 笑脸； 2 - 哭脸
         */
        public int type;
    }

    private List<FaceEntity> faceEntityList;

    public void setFaceEntityList(List<FaceEntity> faceEntityList) {
        this.faceEntityList = faceEntityList;
    }

    private void drawFace(Canvas canvas) {
        if(faceEntityList == null || faceEntityList.size() == 0)  return;

        Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(), R.mipmap.line_face_smile);
        Bitmap bitmap2 = BitmapFactory.decodeResource(getResources(), R.mipmap.line_face_sad);
        for (FaceEntity faceEntity : faceEntityList) {
            int indexOf = dateList.indexOf(faceEntity.date);
            if (indexOf != -1) {
                float x = midX.get(indexOf);
                float y = midY.get(indexOf);

                Bitmap bitmap;
                if(faceEntity.type == 1){
                    bitmap = bitmap1;
                }else{
                    bitmap = bitmap2;
                }
                y = y + bitmap1.getHeight() * 3;
                if(y + bitmap1.getHeight() > getDataQuadrantPaddingHeight()){
                    y = getDataQuadrantPaddingHeight() - bitmap1.getHeight();
                }
                canvas.drawBitmap(bitmap, x, y, paintFontCenter);
            }
        }
    }

    public static class LimitLine{
        public float value;
        public int color;
    }

    private List<LimitLine> limitLineList;

    public void setLimitLineList(List<LimitLine> limitLineList) {
        this.limitLineList = limitLineList;
    }

    private void drawLimitLine(Canvas canvas) {
        if(limitLineList == null || limitLineList.size() == 0)  return;

        for (LimitLine limitLine : limitLineList) {
            float valueY = (float) ((1f - (limitLine.value - minValue)
                    / (maxValue - minValue)) * getDataQuadrantPaddingHeight())
                    + getDataQuadrantPaddingStartY();

            paintLine.setColor(limitLine.color);
            canvas.drawLine(getDataQuadrantPaddingStartX(), valueY, currentEndX, valueY, paintLine);
        }

    }

    /*
     * (non-Javadoc)
     *
     * @param value
     *
     * @see GridChart#getAxisXGraduate(Object)
     */
    @Override
    public String getAxisXGraduate(Object value) {
        float graduate = Float.valueOf(super.getAxisXGraduate(value));
        int index = (int) Math.floor(graduate * displayNumber);

        if (index >= displayNumber) {
            index = displayNumber - 1;
        } else if (index < 0) {
            index = 0;
        }
        index = index + displayFrom;

        if (null == this.linesData || linesData.size() <= 0) {
            return "";
        }
        LineEntity<DateValueEntity> line = linesData.get(0);
        if (line == null) {
            return "";
        }
        if (!line.isDisplay()) {
            return "";
        }
        List<DateValueEntity> lineData = line.getLineData();
        if (lineData == null || lineData.size() <= index) {
            return "";
        }
        getPosition(index);
        return String.valueOf(lineData.get(index).getDate());
    }

    /**
     * 获取点击的点在数据集合中的位置 修改版
     *
     * @param index
     */
    public void getPosition(int index) {
        if (index < 0) return;
        if (positionChangedListener != null) {
            positionChangedListener.showData(index);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @param value
     *
     * @see GridChart#getAxisYGraduate(Object)
     */
    @Override
    public String getAxisYGraduate(Object value) {
        float graduate = Float.valueOf(super.getAxisYGraduate(value));
        return String.valueOf(graduate * (maxValue - minValue) + minValue);

    }

    List<String> titleY = new ArrayList<String>();
    List<String> inreasePercent = new ArrayList<String>();

    /**
     * <p>
     * initialize degrees on Y axis
     * </p>
     * <p>
     * Y軸の目盛を初期化
     * </p>
     * <p>
     * 初始化Y轴的坐标值
     * </p>
     */
    protected void initAxisY() {
        this.calcValueRange();

        float average = (float) ((maxValue - minValue) / this.getLatitudeNum());
        double increasePart, average_percent;
        if (maxValue - midValue > midValue - minValue) {
            increasePart = maxValue - midValue;
        } else {
            increasePart = midValue - minValue;
        }

        if(midValue == 0){
            average_percent = 0;
        }else{
            average_percent = (increasePart / midValue) / (this.getLatitudeNum() / 2);
        }

        titleY.clear();
        inreasePercent.clear();
        // calculate degrees on Y axis
        String value;
        for (int i = 0; i < this.getLatitudeNum(); i++) {
            value = String.valueOf(minValue + i * average);
            titleY.add(value);
            //计算涨幅
            if(midValue + i * average_percent == 0){
                inreasePercent.add("0");
            }else{
                inreasePercent.add(String.valueOf((-increasePart / midValue + i * average_percent) * 100));

            }
        }
        if(midValue == 0){
            inreasePercent.add("0");
        }else{
            inreasePercent.add(String.valueOf(100 * increasePart / midValue));
        }
        // calculate last degrees by use max value

        value = String.valueOf(maxValue);

        titleY.add(value);

        super.setLatitudeTitles(titleY);
        if (showMid) {
            super.setIncreasePercent(inreasePercent);
        }
    }

    List<String> titleX = new ArrayList<>();

    /**
     * <p>
     * initialize degrees on X axis
     * </p>
     * <p>
     * X軸の目盛を初期化
     * </p>
     * <p>
     * 初始化X轴的坐标值
     * </p>
     */
    protected void initAxisX() {
        titleX.clear();
        if (null != linesData && linesData.size() > 0) {
            List<DateValueEntity> subData = linesData.get(0).getLineData();
            float average = displayNumber / this.getLongitudeNum();
            int index;
            for (int i = 0; i < this.getLongitudeNum(); i++) {
                index = (int) Math.floor(i * average);
                if (index > displayNumber - 1) {
                    index = displayNumber - 1;
                }
                index = index + displayFrom;
                if (index >= 0 && index < subData.size()) {
                    titleX.add(subData.get(index).getDate());
                }
            }
            if (displayFrom + displayNumber - 1 < subData.size() && displayFrom + displayNumber - 1 >= 0) {
                titleX.add(subData.get(displayFrom + displayNumber - 1).getDate());
            } else {
                titleX.add("");
            }
            super.setLongitudeTitles(titleX);
        }
    }

    /**
     * <p>
     * draw lines
     * </p>
     * <p>
     * ラインを書く
     * </p>
     * <p>
     * 绘制线条
     * </p>
     *
     * @param canvas
     */
    protected void drawLines(Canvas canvas) {
        if (null == this.linesData) {
            return;
        }
        // distance between two points
        float lineLength = getDataQuadrantPaddingWidth() / displayNumber - 1;
        // start point‘s X
        float startX = 0;
        midX.clear();
        midY.clear();
        dateList.clear();

        // draw lines
        for (int i = 0; i < linesData.size(); i++) {
            LineEntity<DateValueEntity> line = linesData.get(i);
            if (line == null) {
                continue;
            }
            if (!line.isDisplay()) {
                continue;
            }
            List<DateValueEntity> lineData = line.getLineData();
            if (lineData == null) {
                continue;
            }

            paintLine.setColor(line.getLineColor());
            // set start point’s X
            startX = getDataQuadrantPaddingStartX() + lineLength / 2;
            // start point
            PointF ptFirst = null;
            for (int j = displayFrom; j < displayFrom + displayNumber; j++) {
                if (lineData.size() <= j || j < 0)
                    break;

                DateValueEntity entity = lineData.get(j);
                float value = entity.getValue();
                // calculate Y
                float valueY = (float) ((1f - (value - minValue)
                        / (maxValue - minValue)) * getDataQuadrantPaddingHeight())
                        + getDataQuadrantPaddingStartY();


                // if is not last point connect to previous point
                boolean preDraw = true;
                if(j > 0){
                    DateValueEntity preEntity = lineData.get(j - 1);
                    if (preEntity != null) {
                        preDraw = preEntity.isDrawEnable();
                    }
                }
                if (j > displayFrom && entity.isDrawEnable() && preDraw) {
                    if (ptFirst != null) {
                        canvas.drawLine(ptFirst.x, ptFirst.y, startX, valueY,
                                paintLine);
                    }
                }

                if(i == 0){
                    midX.add(startX);
                    midY.add(valueY);
                    dateList.add(entity.getDate());
                }

                // reset
                ptFirst = new PointF(startX, valueY);
                startX = startX + 1 + lineLength;
            }
        }
        currentEndX = startX;
    }

    private List<Float> midX = new ArrayList<>();
    private List<Float> midY = new ArrayList<>();

    @Override
    protected int getDataIndexOf(float x) {
        float graduate = Float.valueOf(super.getAxisXGraduate(x));
        return (int) Math.floor(graduate * displayNumber);
    }


    @Override
    protected float getDataMidX(float x) {
        /*int dataIndexOf = getDataIndexOf(x);
		if(dataIndexOf == -1){
			return -1;
		}
		if(dataIndexOf >= midY.size()){
			return midX.get(midX.size() - 1);
		}
		return midX.get(dataIndexOf);*/
        return -1f;
    }

    @Override
    protected float getDataMidY(float x) {
        if(midY.size() == 0)    return -1f;
        int dataIndexOf = getDataIndexOf(x);
        if (dataIndexOf >= midY.size()) {
            return midY.get(midY.size() - 1);
        }
        return midY.get(dataIndexOf);
    }


    /**
     * <p>
     * Zoom in the graph
     * </p>
     * <p>
     * 拡大表示する。
     * </p>
     * <p>
     * 放大表示
     * </p>
     */
    /*protected void zoomIn() {
        if (displayNumber > minDisplayNumber) {
            // 区分缩放方向
            if (zoomBaseLine == ZOOM_BASE_LINE_CENTER) {
                displayNumber = displayNumber - 2;
                displayFrom = displayFrom + 1;
            } else if (zoomBaseLine == ZOOM_BASE_LINE_LEFT) {
                displayNumber = displayNumber - 2;
            } else if (zoomBaseLine == ZOOM_BASE_LINE_RIGHT) {
                displayNumber = displayNumber - 2;
                displayFrom = displayFrom + 2;
            }

            // 处理displayNumber越界
            if (displayNumber < minDisplayNumber) {
                displayNumber = minDisplayNumber;
            }

            // 处理displayFrom越界
            if (displayFrom >= linesData.get(0).getLineData()
                    .size()) {
                displayFrom = linesData.get(0).getLineData().size() - 1;
                if (linesData.get(0).getLineData().size() >= displayNumber) {
                    displayFrom = linesData.get(0).getLineData().size()
                            - displayNumber;
                }
            }
        }
    }*/

    /**
     * <p>
     * Zoom out the grid
     * </p>
     * <p>
     * 縮小表示する。
     * </p>
     * <p>
     * 缩小
     * </p>
     */
    /*protected void zoomOut() {
        // 区分缩放方向
        if (zoomBaseLine == ZOOM_BASE_LINE_CENTER) {//中间的情况已修改.另外两种情况,暂时没有修改,如果使用请先测试
            displayNumber = displayNumber + 2;
            displayFrom = displayFrom - 1;
        } else if (zoomBaseLine == ZOOM_BASE_LINE_LEFT) {
            displayNumber = displayNumber + 2;
        } else if (zoomBaseLine == ZOOM_BASE_LINE_RIGHT) {
            displayNumber = displayNumber + 2;
            if (displayFrom > 2) {
                displayFrom = displayFrom - 2;
            } else {
                displayFrom = 0;
            }
        }

        // 处理displayNumber越界
        if (displayNumber > linesData.get(0).getLineData().size()) {
            displayNumber = linesData.get(0).getLineData().size();
        }
        // 处理displayFrom越界
        if (displayFrom < 0) {
            displayFrom = 0;
        }
    }*/


    /**
     * @return the minValue
     */
    public double getMinValue() {
        return minValue;
    }

    /**
     * @param minValue the minValue to set
     */
    public void setMinValue(double minValue) {
        this.minValue = minValue;
    }

    /**
     * @return the maxValue
     */
    public double getMaxValue() {
        return maxValue;
    }

    /**
     * @param maxValue the maxValue to set
     */
    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    /**
     * @return the displayFrom
     */
    public int getDisplayFrom() {
        return displayFrom;
    }

    /**
     * @param displayFrom the displayFrom to set
     */
    public void setDisplayFrom(int displayFrom) {
        this.displayFrom = displayFrom;
    }

    /**
     * @return the displayNumber
     */
    public int getDisplayNumber() {
        return displayNumber;
    }

    /**
     * @param displayNumber the displayNumber to set
     */
    public void setDisplayNumber(int displayNumber) {
        this.displayNumber = displayNumber;
    }

    /**
     * @return the minDisplayNumber
     */
    public int getMinDisplayNumber() {
        return minDisplayNumber;
    }

    /**
     * @param minDisplayNumber the minDisplayNumber to set
     */
    public void setMinDisplayNumber(int minDisplayNumber) {
        this.minDisplayNumber = minDisplayNumber;
    }

    /**
     * @return the zoomBaseLine
     */
    public int getZoomBaseLine() {
        return zoomBaseLine;
    }

    /**
     * @param zoomBaseLine the zoomBaseLine to set
     */
    public void setZoomBaseLine(int zoomBaseLine) {
        this.zoomBaseLine = zoomBaseLine;
    }

    /**
     * @return the linesData
     */
    public List<LineEntity<DateValueEntity>> getLinesData() {
        return linesData;
    }

    /**
     * @param linesData the linesData to set
     */
    public void setLinesData(List<LineEntity<DateValueEntity>> linesData) {
        this.linesData = linesData;
    }

    public List<PointEntity> getPointsData() {
        return pointsData;
    }

    public void setPointsData(List<PointEntity> pointsData) {
        this.pointsData = pointsData;
    }

    /**
     * 中间值
     */
    private double midValue = 0;
    boolean noData;//没有数据
    boolean showMid = true;//是否显示涨幅

    /**
     * 设置中间值
     *
     * @param mid
     */
    public void setMidValue(double mid) {
        midValue = mid;
    }

    public double getMidValue(){
        return midValue;
    }

    public void setNoData(boolean b) {
        noData = b;
    }

    public void setShowMid(boolean showMid) {
        this.showMid = showMid;
    }


    /**
     * 绘制色块区域
     *
     * @param canvas
     */
    protected void drawAreas(Canvas canvas) {
        if (null == linesData || linesData.size() <= 0) {
            return;
        }
        // distance between two points
        float lineLength = getDataQuadrantPaddingWidth() / displayNumber - 1;
        // start point‘s X
        float startX;
//        midX.clear();
//        midY.clear();
//        dateList.clear();

        // draw lines
        LineEntity<DateValueEntity> line = linesData.get(0);
        if (line == null) {
            return;
        }
        if (!line.isDisplay()) {
            return;
        }
        List<DateValueEntity> lineData = line.getLineData();
        if (lineData == null) {
            return;
        }

        if(color_area == -1){
            mPaintAlpha.setColor(line.getLineColor());
            mPaintAlpha.setAlpha(60);
        }
        else{
            mPaintAlpha.setColor(color_area);
        }

        // set start point’s X
        startX = getDataQuadrantPaddingStartX() + lineLength / 2f;
        Path linePath = new Path();
        for (int j = displayFrom; j < displayFrom + displayNumber; j++) {
            if (lineData.size() <= j || j < 0) break;
            float value = lineData.get(j).getValue();

            // calculate Y
            float valueY = (float) ((1f - (value - minValue) / (maxValue - minValue)) * getDataQuadrantPaddingHeight())
                    + getDataQuadrantPaddingStartY();

            // if is not last point connect to previous point
            if (j == displayFrom) {
                linePath.moveTo(startX, getDataQuadrantPaddingEndY());
                linePath.lineTo(startX, valueY);
            } else {
                linePath.lineTo(startX, valueY);
            }

//            midX.add(startX);
//            midY.add(valueY);
//            dateList.add(lineData.get(j).getDate());

            startX = startX + 1 + lineLength;
        }
        linePath.lineTo(startX, getDataQuadrantPaddingEndY());
        linePath.close();

        canvas.drawPath(linePath, mPaintAlpha);
    }

    private void drawPoints(Canvas canvas){

        if(pointsData == null || pointsData.size() <= 0)	return;
        if(linesData == null || linesData.size() <= 0)	return;
        if(dateList == null || dateList.size() <= 0)	return;
        if(midX == null || midX.size() <= 0)	return;
        if(midY == null || midY.size() <= 0)	return;

        Rect rect = new Rect();
        paintFontCenter.getTextBounds("9", 0, 1, rect);

        textDis = pointDis + ChartConfig.CIRCLE_RADIO + rect.height() + rect.height() * 0.5f;
        int size = pointsData.size();
        for (int i = 0; i < size; i++) {

            PointEntity pointEntity = pointsData.get(i);

            int indexOf = dateList.indexOf(pointEntity.getDate());
            if (indexOf != -1) {

                paintFontCenter.setColor(pointEntity.getColor());
                if(indexOf < midX.size()){
                    float y = midY.get(indexOf);
                    float x = midX.get(indexOf);

                    if(pointEntity.isUp()){
//                        canvas.drawPoint(x, y < pointDis ? 5 : y - pointDis, paintFontCenter);
                        canvas.drawCircle(x, y < pointDis ? 5 : y - pointDis, ChartConfig.CIRCLE_RADIO, paintFontCenter);
                        if(pointEntity.isDrawText()){
                            canvas.drawText(pointEntity.getContent(), x - rect.width() * 0.5f, y < textDis ? rect.height() : y - pointDis - ChartConfig.CIRCLE_RADIO - rect.height() * 0.5f, paintFontCenter);
                        }

                    }else{
                        float yAxis = getDataQuadrantPaddingStartY() + getDataQuadrantPaddingHeight();
//                        canvas.drawPoint(x, y + pointDis > yAxis ? yAxis : y + pointDis, paintFontCenter);
                        canvas.drawCircle(x, y + pointDis > yAxis ? yAxis : y + pointDis, ChartConfig.CIRCLE_RADIO, paintFontCenter);
                        if(pointEntity.isDrawText()){
                            canvas.drawText(pointEntity.getContent(), x - rect.width() * 0.5f, y + textDis > yAxis ? yAxis : y + textDis, paintFontCenter);
                        }
                    }
                }
            }
        }

/*
		for (PointEntity pointEntity : pointsData) {

			int indexOf = dateList.indexOf(pointEntity.getDate());
			if (indexOf != -1) {

				paint.setColor(pointEntity.getColor());
				if(indexOf < midX.size()){
					float y = midY.get(indexOf);
					float x = midX.get(indexOf);

					if(up){
						canvas.drawPoint(x, y - 10, paint);
						if(pointEntity.isDrawText()){
							canvas.drawText(pointEntity.getContent(), x, y - 15, paint);
						}

					}else{
						canvas.drawPoint(x, y + 10, paint);
						if(pointEntity.isDrawText()){
							canvas.drawText(pointEntity.getContent(), x, y + 15, paint);
						}
					}
				}
			}

		}
*/
    }
    protected int stickSpacing = ChartConfig.STICK_SPACING;

    private List<DateValueEntity> macdLine;
    protected void drawMacdSticks(Canvas canvas) {
        if (macdLine == null || macdLine.size() <= 0)  return;


        float stickWidth = getDataQuadrantPaddingWidth() / displayNumber;
        if(stickWidth < stickSpacing){
            stickSpacing = 1;
        }
        stickWidth -= stickSpacing;

        float stickX = getDataQuadrantPaddingStartX();

        float midY = 0.5f * getDataQuadrantPaddingHeight() + getDataQuadrantPaddingStartY();
        DateValueEntity stick;
        for (int i = displayFrom; i < displayFrom + displayNumber; i++) {
            if(i >= macdLine.size() || i < 0)	break;
            stick = macdLine.get(i);

            float highY;
            float lowY;
            if (stick.getValue() == 0) {
                // 没有值的情况下不绘制
//                continue;
            }else{
                // 柱状线颜色设定
                if (stick.getValue() > 0) {
                    paintLine.setColor(ChartConfig.COLOR_POSITIVE_STICK);
                    highY = (1f - stick.getValue()) * 0.5f * (getDataQuadrantPaddingHeight())
                            + getDataQuadrantPaddingStartY();
                    lowY = midY;

                } else {
                    paintLine.setColor(ChartConfig.COLOR_NEGATIVE_STICK);
                    highY = midY;
                    lowY = (1f - stick.getValue()) * 0.5f * (getDataQuadrantPaddingHeight())
                            + getDataQuadrantPaddingStartY();

                }

                if (stickWidth > ChartConfig.LINE_WIDTH) {
                    canvas.drawRect(stickX, highY, stickX + stickWidth, lowY, paintLine);
                } else {
                    canvas.drawLine(stickX, highY, stickX, lowY, paintLine);
                }
            }

            // X位移
            stickX = stickX + stickSpacing + stickWidth;
        }
    }

    private List<DateValueObject> dljgList;

    private void drawDLJG(Canvas canvas) {
        if(dljgList == null || dljgList.size() <= 0)	return;
        if(dateList == null || dateList.size() <= 0)	return;
        if (linesData == null || linesData.size() <= 0) return;
        if(midX == null || midX.size() <= 0)	return;

        float top = getDataQuadrantStartY();
        float bottom = getDataQuadrantEndY();
        int indexOf;

        Rect rect = new Rect();
        paintFontCenter.getTextBounds("钝", 0, 1, rect);
        DateValueObject dateValueObject;
        float drawY;
        for (int i = 0; i < dljgList.size(); i++) {
            dateValueObject = dljgList.get(i);
            if (dateValueObject != null) {
                indexOf = dateList.indexOf(dateValueObject.getDate());
                if (indexOf != -1 && indexOf < midX.size()) {
                    if(dateValueObject.isUp()){
                        drawY = top + rect.height() + (i%2 == 0 ? 0 :rect.height());
                    }else{
                        drawY = bottom - rect.height() - (i%2 == 0 ? 0 :rect.height());
                    }
                    paintFontCenter.setColor(dateValueObject.getColor());
                    canvas.drawText(dateValueObject.getValue(), midX.get(indexOf), drawY, paintFontCenter);
                }
            }

        }

    }

    public void setDljgList(List<DateValueObject> dljgList) {
        this.dljgList = dljgList;
    }


    private ArrayList<String> dateList = new ArrayList<>();

    public void setColorArea(int colorArea) {
        this.color_area = colorArea;
    }

    public void setDrawArea(boolean drawArea) {
        this.isDrawArea = drawArea;
    }

    public void setMacdLine(List<DateValueEntity> macdLine) {
        this.macdLine = macdLine;
    }

    @Override
    protected int getDataSize() {
        if(linesData != null && linesData.size() > 0){
            return linesData.get(0).getLineData().size();
        }
        return super.getDataSize();
    }
}
