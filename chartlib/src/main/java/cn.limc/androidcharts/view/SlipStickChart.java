/*
 * SlipStickChart.java
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
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.List;

import cn.limc.androidcharts.ChartConfig;
import cn.limc.androidcharts.entity.IChartData;
import cn.limc.androidcharts.entity.IMeasurable;
import cn.limc.androidcharts.entity.IStickEntity;
import cn.limc.androidcharts.entity.OHLCEntity;
import cn.limc.androidcharts.entity.StickEntity;

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
 * @version v1.0 2014-1-20 下午2:03:08
 */
public class SlipStickChart extends GridChart {

    public static final int ZOOM_BASE_LINE_CENTER = 0;
    public static final int ZOOM_BASE_LINE_LEFT = 1;
    public static final int ZOOM_BASE_LINE_RIGHT = 2;

    public static final int DEFAULT_ZOOM_BASE_LINE = ZOOM_BASE_LINE_CENTER;
    public static final boolean DEFAULT_AUTO_CALC_VALUE_RANGE = true;

    public static final int DEFAULT_STICK_SPACING = ChartConfig.STICK_SPACING;

//    protected int minDisplayNumber = ChartConfig.MIN_DISPLAY_NUMBER;
//    protected int maxDisplayNumber = ChartConfig.MAX_DISPLAY_NUMBER;
    protected int zoomBaseLine = DEFAULT_ZOOM_BASE_LINE;
    protected boolean autoCalcValueRange = DEFAULT_AUTO_CALC_VALUE_RANGE;
    protected int stickSpacing = DEFAULT_STICK_SPACING;



    /**
     * <p>
     * default color for display stick border
     * </p>
     * <p>
     * 表示スティックのボーダーの色のデフォルト値
     * </p>
     * <p>
     * 默认表示柱条的边框颜色
     * </p>
     */
    public static final int DEFAULT_STICK_BORDER_COLOR = Color.RED;

    /**
     * <p>
     * default color for display stick
     * </p>
     * <p>
     * 表示スティックの色のデフォルト値
     * </p>
     * <p>
     * 默认表示柱条的填充颜色
     * </p>
     */
    public static final int DEFAULT_STICK_FILL_COLOR = Color.RED;

    /**
     * <p>
     * Color for display stick border
     * </p>
     * <p>
     * 表示スティックのボーダーの色
     * </p>
     * <p>
     * 表示柱条的边框颜色
     * </p>
     */
    private int stickBorderColor = DEFAULT_STICK_BORDER_COLOR;

    /**
     * <p>
     * Color for display stick
     * </p>
     * <p>
     * 表示スティックの色
     * </p>
     * <p>
     * 表示柱条的填充颜色
     * </p>
     */
    private int stickFillColor = DEFAULT_STICK_FILL_COLOR;

    /**
     * <p>
     * data to draw sticks
     * </p>
     * <p>
     * スティックを書く用データ
     * </p>
     * <p>
     * 绘制柱条用的数据
     * </p>
     */
    protected IChartData<IStickEntity> stickData;

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

    // index in chart, not data
    protected int maxVIndex;
    protected int minVIndex;
    protected double maxDataValue;
    protected double minDataValue;

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

    protected Paint mPaintStick = new Paint();


    /**
     * <p>
     * Constructor of SlipStickChart
     * </p>
     * <p>
     * SlipStickChart类对象的构造函数
     * </p>
     * <p>
     * SlipStickChartのコンストラクター
     * </p>
     *
     * @param context
     */
    public SlipStickChart(Context context) {
        super(context);
    }

    /**
     * <p>
     * Constructor of SlipStickChart
     * </p>
     * <p>
     * SlipStickChart类对象的构造函数
     * </p>
     * <p>
     * SlipStickChartのコンストラクター
     * </p>
     *
     * @param context
     * @param attrs
     * @param defStyle
     */
    public SlipStickChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * <p>
     * Constructor of SlipStickChart
     * </p>
     * <p>
     * SlipStickChart类对象的构造函数
     * </p>
     * <p>
     * SlipStickChartのコンストラクター
     * </p>
     *
     * @param context
     * @param attrs
     */
    public SlipStickChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected void calcDataValueRange() {

        // 暂时 修改
        double maxTemp = Double.MIN_VALUE;
        double minTemp = Double.MAX_VALUE;

        // double maxValue = Double.MIN_VALUE;
        // double minValue = Double.MAX_VALUE;

        if(displayFrom < stickData.size()){

            OHLCEntity first = (OHLCEntity) this.stickData.get(displayFrom < 0 ? 0 : displayFrom);//原版是取get(0)
            // 第一个stick为停盘的情况
            if (first.getHigh() == 0 && first.getLow() == 0) {

            } else {
                maxTemp = first.getHigh();
                minTemp = first.getLow();
                maxVIndex = 0;
                minVIndex = 0;
            }
        }

        for (int i = this.displayFrom; i < this.displayFrom
                + this.displayNumber; i++) {
            if(i < 0)   continue;

            if (this.stickData.size() <= i) break;
            OHLCEntity stick = (OHLCEntity) this.stickData.get(i);
            if (stick.getLow() < minTemp) {
                minTemp = stick.getLow();
                minVIndex = i - displayFrom;
            }
            if (stick.getHigh() > maxTemp) {
                maxTemp = stick.getHigh();
                maxVIndex = i - displayFrom;
            }

        }
        minDataValue = minTemp;
        maxDataValue = maxTemp;

        this.maxValue = maxTemp;
        this.minValue = minTemp;
    }

    protected void calcValueRangePaddingZero() {
        double maxValueTemp = this.maxValue;
        double minValueTemp = this.minValue;

        if (maxValueTemp > minValueTemp) {
            double v = (maxValueTemp - minValueTemp) * 0.05f;
            this.maxValue = maxValueTemp + v;
            this.minValue = minValueTemp - v;

            //最小值为0
            if (isMinZero) {
                this.minValue = 0;
            }

        } else if (maxValueTemp == minValueTemp) {
            if (maxValueTemp <= 10 && maxValueTemp > 1) {
                this.maxValue = maxValueTemp + 0.2;
                this.minValue = minValueTemp - 0.2;
            } else if (maxValueTemp <= 100 && maxValueTemp > 10) {
                this.maxValue = maxValueTemp + 10;
                this.minValue = minValueTemp - 10;
            } else if (maxValueTemp <= 1000 && maxValueTemp > 100) {
                this.maxValue = maxValueTemp + 100;
                this.minValue = minValueTemp - 100;
            } else if (maxValueTemp <= 10000 && maxValueTemp > 1000) {
                this.maxValue = maxValueTemp + 1000;
                this.minValue = minValueTemp - 1000;
            } else if (maxValueTemp <= 100000 && maxValueTemp > 10000) {
                this.maxValue = maxValueTemp + 10000;
                this.minValue = minValueTemp - 10000;
            } else if (maxValueTemp <= 1000000 && maxValueTemp > 100000) {
                this.maxValue = maxValueTemp + 100000;
                this.minValue = minValueTemp - 100000;
            } else if (maxValueTemp <= 10000000 && maxValueTemp > 1000000) {
                this.maxValue = maxValueTemp + 1000000;
                this.minValue = minValueTemp - 1000000;
            } else if (maxValueTemp <= 100000000 && maxValueTemp > 10000000) {
                this.maxValue = maxValueTemp + 10000000;
                this.minValue = minValueTemp - 10000000;
            }
        } else {
            this.maxValue = 0;
            this.minValue = 0;
        }

    }

    // 原版
    // protected void calcValueRangeFormatForAxis() {
    // // 修正最大值和最小值
    // long rate = (long)(this.maxValue - this.minValue) / (this.latitudeNum);
    // String strRate = String.valueOf( rate);
    // float first = Integer.parseInt(String.valueOf(strRate.charAt(0))) + 1.0f;
    // if (first > 0 && strRate.length() > 1) {
    // float second = Integer.parseInt(String.valueOf(strRate.charAt(1)));
    // if (second < 5) {
    // first = first - 0.5f;
    // }
    // rate = (long) (first * Math.pow(10, strRate.length() - 1));
    // } else {
    // rate = 1;
    // }
    // // 等分轴修正
    // if (this.latitudeNum > 0
    // && (long) (this.maxValue - this.minValue)
    // % (this.latitudeNum * rate) != 0) {
    // // 最大值加上轴差
    // this.maxValue = (long) this.maxValue
    // + (this.latitudeNum * rate)
    // - ((long) (this.maxValue - this.minValue) % (this.latitudeNum * rate));
    // }
    // }
    protected void calcValueRangeFormatForAxis() {
        // 修正最大值和最小值
        double rate = (this.maxValue - this.minValue) / (this.latitudeNum);
        double max = 0;
        // String strRate = String.valueOf( rate);
        // float first = Integer.parseInt(String.valueOf(strRate.charAt(0))) +
        // 1.0f;
        // if (first > 0 && strRate.length() > 1) {
        // float second = Integer.parseInt(String.valueOf(strRate.charAt(1)));
        // if (second < 5) {
        // first = first - 0.5f;
        // }
        // rate = (long) (first * Math.pow(10, strRate.length() - 1));
        // } else {
        // // rate = rate;
        // }
        // 等分轴修正
        if (this.latitudeNum > 0
                && (long) (this.maxValue - this.minValue)
                % (this.latitudeNum * rate) != 0) {
            // 最大值加上轴差
            max = (long) this.maxValue
                    + (this.latitudeNum * rate)
                    - ((long) (this.maxValue - this.minValue) % (this.latitudeNum * rate));
            if (this.maxValue < max)
                this.maxValue = max;
        }
    }

    protected void calcValueRange() {
        if (null == this.stickData) {
            this.maxValue = 0;
            this.minValue = 0;
            return;
        }

        if (this.stickData.size() > 0) {
            this.calcDataValueRange();
            this.calcValueRangePaddingZero();// 修改版 y轴上显示为整数
        } else {
            this.maxValue = 0;
            this.minValue = 0;
        }
        this.calcValueRangeFormatForAxis();
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
        if (this.autoCalcValueRange) {
            calcValueRange();
        }
        initAxisY();
        //如果没有手动设置x轴坐标,根据数据计算出
        if (customLongitudeTitles == null || customLongitudeTitles.size() <= 0) {
            initAxisX();// 初始化x轴坐标
        }
        drawSticks(canvas);// 绘制蜡烛 绘制的先后顺序,十字线位于上方 修改版
        super.onDraw(canvas);// 含有十字线
    }

	/*
     * (non-Javadoc)
	 * 
	 * @param value
	 * 
	 * @return
	 * 
	 * @see
	 * GridChart#getAxisXGraduate(java.lang.Object)
	 */

    @Override
    public String getAxisXGraduate(Object value) {
        float graduate = Float.valueOf(super.getAxisXGraduate(value));
        index = (int) Math.floor(graduate * displayNumber);

        if (index >= displayNumber) {
            index = displayNumber - 1;
        } else if (index < 0) {
            index = 0;
        }
        index = index + displayFrom;
        if (getDataSize() <= index) return ("");

        if(index < 0){
            return "";
        }else{
            getPosition(index);
            return getDateString(index);
        }
    }

    /**
     * 获取点击的点在数据集合中的位置 修改版
     *
     * @param index
     */
    public void getPosition(int index) {

    }

    @Override
    protected int getDataIndexOf(float x) {
        float graduate = Float.valueOf(super.getAxisXGraduate(x));
        return (int) Math.floor(graduate * displayNumber);
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
        return String.valueOf(graduate * (maxValue - minValue) + minValue);// 修改版
    }

    /*
     * (non-Javadoc)
     *
     * @param value
     *
     * @see
     * ITouchEventResponse#notifyEvent(GridChart)
     */
   /* @Override
    public void notifyEvent(GridChart chart) {
        // CandleStickChart candlechart = (CandleStickChart) chart;
        // this. = candlechart.getMaxSticksNum();

        super.setDisplayCrossYOnTouch(false);
        // notifyEvent
        super.notifyEvent(chart);
        // notifyEventAll
//        super.notifyEventAll(this);
    }*/

    List<String> titleX = new ArrayList<>();

    /**
     * <p>
     * initialize degrees on Y axis
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
            if (displayFrom + displayNumber - 1 < stickData.size()) {
                titleX.add(stickData.get(displayFrom + displayNumber - 1).getDate());
            } else {
                titleX.add("");
            }
            super.setLongitudeTitles(titleX);
        }
    }

    public int getSelectedIndex() {
        if (null == super.getTouchPoint()) {
            return 0;
        }
        float graduate = Float.valueOf(super.getAxisXGraduate(super
                .getTouchPoint().x));
        int index = (int) Math.floor(graduate * displayNumber);

        if (index >= displayNumber) {
            index = displayNumber - 1;
        } else if (index < 0) {
            index = 0;
        }
        index = index + displayFrom;

        return index;
    }

    List<String> titleY = new ArrayList<>();

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
        titleY.clear();
        // 原版
        // float average = (int) ((maxValue - minValue) /this.getLatitudeNum())
        // / 100 * 100;
        float average = (float) ((maxValue - minValue) / this.getLatitudeNum());// 修改版
        // calculate degrees on Y axis
        String value;
        for (int i = 0; i < this.getLatitudeNum(); i++) {
            value = String.valueOf(minValue + i * average);
//            if (value.length() < super.getLatitudeMaxTitleLength()) {
//                while (value.length() < super.getLatitudeMaxTitleLength()) {
//                    value = " " + value;
//                }
//            }
            titleY.add(value);
        }
        // calculate last degrees by use max value
        value = String.valueOf(maxValue);

//        if (value.length() < super.getLatitudeMaxTitleLength()) {
//            while (value.length() < super.getLatitudeMaxTitleLength()) {
//                value = " " + value;
//            }
//        }
        titleY.add(value);
        super.setLatitudeTitles(titleY);
    }

    protected void drawSticks(Canvas canvas) {
        if (null == stickData) {
            return;
        }
        if (stickData.size() == 0) {
            return;
        }

        mPaintStick.setColor(stickFillColor);

        float stickWidth = getDataQuadrantPaddingWidth() / displayNumber;
        if(stickWidth < stickSpacing){
            stickSpacing = 1;
        }
        stickWidth -= stickSpacing;
        float stickX = getDataQuadrantPaddingStartX();

        for (int i = displayFrom; i < displayFrom + displayNumber; i++) {
            if(enableRefresh && i < 0){
                if(Math.abs(i) <= REFRESH_SIZE){
                    stickX = stickX + stickSpacing + stickWidth;
                    continue;
                }
            }


            if (stickData.size() <= i || i < 0) break;
            IMeasurable stick = stickData.get(i);
            float highY = (float) ((1f - (stick.getHigh() - minValue)
                    / (maxValue - minValue))
                    * (getDataQuadrantPaddingHeight()) + getDataQuadrantPaddingStartY());
            float lowY = (float) ((1f - (stick.getLow() - minValue)
                    / (maxValue - minValue))
                    * (getDataQuadrantPaddingHeight()) + getDataQuadrantPaddingStartY());

            // stick or line?
            if (stickWidth > ChartConfig.LINE_WIDTH) {
                canvas.drawRect(stickX, highY, stickX + stickWidth, lowY,
                        mPaintStick);
            } else {
                canvas.drawLine(stickX, highY, stickX, lowY, mPaintStick);
            }

            // next x
            stickX = stickX + stickSpacing + stickWidth;
        }
    }


    /**
     * 点击的点对于的数据在集合中的位置
     */
    private int index;





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
            if (displayFrom >= stickData.size()) {
                displayFrom = stickData.size() - 1;
                if (stickData.size() >= displayNumber) {
                    displayFrom = stickData.size() - displayNumber;
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

        if(displayNumber > maxDisplayNumber){
            displayNumber = maxDisplayNumber;
        }

        // 处理displayNumber越界
        if (displayNumber > stickData.size()) {
            displayNumber = stickData.size();
        }
        // 处理displayFrom越界
        if (displayFrom < 0) {
            displayFrom = 0;
        }
    }*/


    /**
     * <p>
     * add a new stick data to sticks and refresh this chart
     * </p>
     * <p>
     * 新しいスティックデータを追加する，フラフをレフレシューする
     * </p>
     * <p>
     * 追加一条新数据并刷新当前图表
     * </p>
     *
     * @param entity <p>
     *               data
     *               </p>
     *               <p>
     *               データ
     *               </p>
     *               <p>
     *               新数据
     *               </p>
     */
    public void pushData(StickEntity entity) {
        if (null != entity) {
            addData(entity);
            super.postInvalidate();
        }
    }

    /**
     * <p>
     * add a new stick data to sticks
     * </p>
     * <p>
     * 新しいスティックデータを追加する
     * </p>
     * <p>
     * 追加一条新数据
     * </p>
     *
     * @param entity <p>
     *               data
     *               </p>
     *               <p>
     *               データ
     *               </p>
     *               <p>
     *               新数据
     *               </p>
     */
    public void addData(IStickEntity entity) {
        if (null != entity) {
            // add
            stickData.add(entity);
            if (this.maxValue < entity.getHigh()) {
                this.maxValue = 100 + ((int) entity.getHigh()) / 100 * 100;
            }

        }
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
     * @return the stickBorderColor
     */
    public int getStickBorderColor() {
        return stickBorderColor;
    }

    /**
     * @param stickBorderColor the stickBorderColor to set
     */
    public void setStickBorderColor(int stickBorderColor) {
        this.stickBorderColor = stickBorderColor;
    }

    /**
     * @return the stickFillColor
     */
    public int getStickFillColor() {
        return stickFillColor;
    }

    /**
     * @param stickFillColor the stickFillColor to set
     */
    public void setStickFillColor(int stickFillColor) {
        this.stickFillColor = stickFillColor;
    }

    /**
     * @return the stickData
     */
    public IChartData<IStickEntity> getStickData() {
        return stickData;
    }

    /**
     * @param stickData the stickData to set
     */
    public void setStickData(IChartData<IStickEntity> stickData) {
        this.stickData = stickData;
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
    public void setMaxValue(double maxValue) {
        this.maxValue = maxValue;
    }

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
     * @return the stickSpacing
     */
    public int getStickSpacing() {
        return stickSpacing;
    }

    /**
     * @param stickSpacing the stickSpacing to set
     */
    public void setStickSpacing(int stickSpacing) {
        this.stickSpacing = stickSpacing;
    }

    //最小值是否为0
    protected boolean isMinZero;

    public void setMinZero(boolean isMinZero) {
        this.isMinZero = isMinZero;
    }


    /**
     * 获取value值在y轴上的坐标
     * @param value
     * @return
     */
    protected float getValueY(float value){
        return  (float) ((1f - (value - minValue) / (maxValue - minValue)) * getDataQuadrantPaddingHeight())
                + getDataQuadrantPaddingStartY();
    }

    /**
     * data size
     * @return
     */
    @Override
    protected int getDataSize(){
        if (stickData != null) {
            return stickData.size();
        }
        return super.getDataSize();
    }

    protected String getDateString(int index){
        return  stickData.get(index).getDate();
    }

}
