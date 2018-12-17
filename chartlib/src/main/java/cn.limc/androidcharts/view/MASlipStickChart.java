/*
 * MASlipStickChart.java
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
import android.graphics.PointF;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.List;

import cn.limc.androidcharts.ChartConfig;
import cn.limc.androidcharts.entity.DateValueEntity;
import cn.limc.androidcharts.entity.LineEntity;
import cn.limc.androidcharts.entity.OHLCEntity;

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
 * @version v1.0 2014/01/21 10:55:40
 * 
 */
public class MASlipStickChart extends SlipStickChart {

	private int PositiveStickColor = ChartConfig.COLOR_POSITIVE_STICK;
	private int NegativeStickColor = ChartConfig.COLOR_NEGATIVE_STICK;

	/**
	 * red candle fill or line rectangle
	 */
	protected boolean drawRedStickFill = true;

	public int getPositiveStickColor() {
		return PositiveStickColor;
	}

	public void setPositiveStickColor(int positiveStickColor) {
		PositiveStickColor = positiveStickColor;
	}

	public int getNegativeStickColor() {
		return NegativeStickColor;
	}

	public void setNegativeStickColor(int negativeStickColor) {
		NegativeStickColor = negativeStickColor;
	}

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
	private List<LineEntity<DateValueEntity>> linesData;

	/**
	 * <p>
	 * Constructor of MASlipStickChart
	 * </p>
	 * <p>
	 * MASlipStickChart类对象的构造函数
	 * </p>
	 * <p>
	 * MASlipStickChartのコンストラクター
	 * </p>
	 * 
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public MASlipStickChart(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	/**
	 * <p>
	 * Constructor of MASlipStickChart
	 * </p>
	 * <p>
	 * MASlipStickChart类对象的构造函数
	 * </p>
	 * <p>
	 * MASlipStickChartのコンストラクター
	 * </p>
	 * 
	 * @param context
	 * @param attrs
	 */
	public MASlipStickChart(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * <p>
	 * Constructor of MASlipStickChart
	 * </p>
	 * <p>
	 * MASlipStickChart类对象的构造函数
	 * </p>
	 * <p>
	 * MASlipStickChartのコンストラクター
	 * </p>
	 * 
	 * @param context
	 */
	public MASlipStickChart(Context context) {
		super(context);
	}


	@Override
	protected void calcDataValueRange() {

	    // max min value calc fix
		double maxValueTemp = Double.MIN_VALUE;
		double minValueTemp = Double.MAX_VALUE;
		if(isMinZero){
			minValueTemp = 0;
		}

		if(displayFrom < stickData.size()){

			OHLCEntity first = (OHLCEntity) this.stickData.get(displayFrom < 0 ? 0 : displayFrom);//原版是取get(0)
			// 第一个stick为停盘的情况
			if (first.getHigh() == 0 && first.getLow() == 0) {

			} else {
				maxValueTemp = first.getVol();
			}
		}

		for (int i = this.displayFrom; i < this.displayFrom
				+ this.displayNumber; i++) {
			if(i < 0)   continue;

			if (this.stickData.size() <= i) break;
			OHLCEntity stick = (OHLCEntity) this.stickData.get(i);

			if(maxValueTemp < stick.getVol()){
				maxValueTemp = stick.getVol();
			}

		}

		// 逐条输出MA线
		if(linesData !=null && linesData.size()>0){
			
			for (int i = 0; i < this.linesData.size(); i++) {
				LineEntity<DateValueEntity> line = this.linesData.get(i);
				if (line != null && line.getLineData().size() > 0) {
					// 判断显示为方柱或显示为线条
					for (int j = displayFrom; j < displayFrom + displayNumber; j++) {
						DateValueEntity lineData = line.getLineData().get(j);
						if (lineData.getValue() < minValueTemp) {
							minValueTemp = lineData.getValue();
						}
						
						if (lineData.getValue() > maxValueTemp) {
							maxValueTemp = lineData.getValue();
						}
						
					}
				}
			}
		}
		this.maxValue = maxValueTemp;
		this.minValue = minValueTemp;
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
		super.onDraw(canvas);

		// draw lines
		if (null != this.linesData) {
			if (0 != this.linesData.size()) {
				drawLines(canvas);
			}
		}

	}
    @Override
    protected void drawSticks(Canvas canvas) {//修改版	根据开盘/收盘价  来显示颜色

        if (null == stickData) {
            return;
        }
        if (stickData.size() == 0) {
            return;
        }

//        Paint mPaintCross = new Paint();

		float stickWidth = getDataQuadrantPaddingWidth() / displayNumber;
		if(stickWidth < stickSpacing){
			stickSpacing = 1;
		}
		stickWidth -= stickSpacing;

		float stickX = getDataQuadrantPaddingStartX();
		midX.clear();

        for (int i = displayFrom; i < displayFrom + displayNumber; i++) {
			if(enableRefresh && i < 0){
				if(Math.abs(i) <= REFRESH_SIZE){
					stickX = stickX + stickSpacing + stickWidth;
					continue;
				}
			}


            if(stickData.size() <= i || i < 0)	break;
            OHLCEntity ohlc = (OHLCEntity) stickData.get(i);
            float highY = (float) ((1f - (ohlc.getVol() - minValue)
                    / (maxValue - minValue))
                    * (getDataQuadrantPaddingHeight()) + getDataQuadrantPaddingStartY());
            float lowY = (float) ((1f - (0 - minValue)
                    / (maxValue - minValue))
                    * (getDataQuadrantPaddingHeight()) + getDataQuadrantPaddingStartY());

            // stick or line?


            if (ohlc.getOpen() < ohlc.getClose()) {
				paintLine.setColor(PositiveStickColor);
				// stick or line
				if(drawRedStickFill){
					if (stickWidth > ChartConfig.LINE_WIDTH) {
						canvas.drawRect(stickX, highY, stickX + stickWidth, lowY, paintLine);
					} else {
						canvas.drawLine(stickX, highY, stickX, lowY, paintLine);
					}
				}else{
					if (stickWidth > ChartConfig.LINE_WIDTH) {
						//line
						canvas.drawLine(stickX, highY, stickX + stickWidth, highY, paintLine);
						canvas.drawLine(stickX, highY, stickX, lowY, paintLine);
						canvas.drawLine(stickX + stickWidth, highY, stickX + stickWidth, lowY, paintLine);
						canvas.drawLine(stickX, lowY, stickX + stickWidth, lowY, paintLine);
					}else{
						canvas.drawLine(stickX, highY, stickX, lowY, paintLine);
					}
				}

            } else if (ohlc.getOpen() > ohlc.getClose()) {
				paintLine.setColor(NegativeStickColor);
                // stick or line
                if (stickWidth > ChartConfig.LINE_WIDTH) {
                    canvas.drawRect(stickX, highY, stickX + stickWidth, lowY, paintLine);
                } else {
                    canvas.drawLine(stickX, highY, stickX, lowY, paintLine);
                }
            } else {
				paintLine.setColor(NegativeStickColor);
                // line or point
				if (stickWidth > ChartConfig.LINE_WIDTH) {
					canvas.drawRect(stickX, highY, stickX + stickWidth, lowY, paintLine);
				} else {
					canvas.drawLine(stickX, highY, stickX, lowY, paintLine);
				}
            }

			midX.add(stickX + stickWidth / 2f);

            // next x
            stickX = stickX + stickSpacing + stickWidth;
        }
    }
    private ArrayList<Float> midX = new ArrayList<>();

    @Override
    protected float getDataMidX(float x) {
        /*if(midX.size() <= 0){
            return -1f;
        }
        int dataIndexOf = getDataIndexOf(x);
        if(dataIndexOf >= midX.size()){
            return midX.get(midX.size() - 1);
        }
        return midX.get(dataIndexOf);*/
		return -1f;
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
		if (null == stickData) {
			return;
		}
		if (stickData.size() <= 0) {
			return;
		}
		// distance between two points
		float lineLength = getDataQuadrantPaddingWidth() / displayNumber - 1;
		// start point‘s X
		float startX;

		// draw MA lines
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
			for (int j = super.getDisplayFrom(); j < super.getDisplayFrom()
					+ super.getDisplayNumber(); j++) {
				float value = lineData.get(j).getValue();
				// calculate Y
				float valueY = (float) ((1f - (value - minValue)
						/ (maxValue - minValue)) * getDataQuadrantPaddingHeight())
						+ getDataQuadrantPaddingStartY();

				// if is not last point connect to previous point
				if (j > super.getDisplayFrom()) {
					canvas.drawLine(ptFirst.x, ptFirst.y, startX, valueY, paintLine);
				}
				// reset
				ptFirst = new PointF(startX, valueY);
				startX = startX + 1 + lineLength;
			}
		}
	}

	/**
	 * @return the linesData
	 */
	public List<LineEntity<DateValueEntity>> getLinesData() {
		return linesData;
	}

	/**
	 * @param linesData
	 *            the linesData to set
	 */
	public void setLineData(List<LineEntity<DateValueEntity>> linesData) {
		this.linesData = linesData;
	}
	
	@Override
	public void getPosition(int index) {
		if(positionChangedListener != null){
			positionChangedListener.showData(index);//拿到位置后的处理
		}
		super.getPosition(index);
	}

	/**
	 * 红色蜡烛图形状
	 * @param drawRedStickFill	true-实体长方形; false-空心长方形
	 */
	public void setDrawRedStickFill(boolean drawRedStickFill) {
		this.drawRedStickFill = drawRedStickFill;
	}
}
