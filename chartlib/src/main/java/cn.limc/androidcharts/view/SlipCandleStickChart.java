/*
 * SlipCandleStickChart.java
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
import android.text.TextUtils;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.List;

import cn.limc.androidcharts.ChartConfig;
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
 * @version v1.0 2014/01/21 11:37:51
 * 
 */
public class SlipCandleStickChart extends SlipStickChart {

	public static final int K_STYLE_CANDLE = 1;
	public static final int K_STYLE_AMERICAN = 2;
	protected int kStyle = K_STYLE_CANDLE;
	/**
	 * red candle fill or line rectangle
	 */
	protected boolean drawRedStickFill = true;

	/**
	 * <p>
	 * Default price up stick's border color
	 * </p>
	 * <p>
	 * 値上がりローソクのボーダー色のデフォルト値
	 * </p>
	 * <p>
	 * 默认阳线的边框颜色
	 * </p>
	 */
	public static final int DEFAULT_POSITIVE_STICK_BORDER_COLOR = Color.RED;

	/**
	 * <p>
	 * Default price up stick's fill color
	 * </p>
	 * <p>
	 * 値上がりローソクの色のデフォルト値
	 * </p>
	 * <p>
	 * 默认阳线的填充颜色
	 * </p>
	 */
	public static final int DEFAULT_POSITIVE_STICK_FILL_COLOR = ChartConfig.COLOR_POSITIVE_STICK;

	/**
	 * <p>
	 * Default price down stick's border color
	 * </p>
	 * <p>
	 * 値下りローソクのボーダー色のデフォルト値
	 * </p>
	 * <p>
	 * 默认阴线的边框颜色
	 * </p>
	 */
	public static final int DEFAULT_NEGATIVE_STICK_BORDER_COLOR = Color.GREEN;

	/**
	 * <p>
	 * Default price down stick's fill color
	 * </p>
	 * <p>
	 * 値下りローソクの色のデフォルト値
	 * </p>
	 * <p>
	 * 默认阴线的填充颜色
	 * </p>
	 */
	public static final int DEFAULT_NEGATIVE_STICK_FILL_COLOR = ChartConfig.COLOR_NEGATIVE_STICK;

	/**
	 * <p>
	 * Default price no change stick's color (cross-star,T-like etc.)
	 * </p>
	 * <p>
	 * クローススターの色のデフォルト値
	 * </p>
	 * <p>
	 * 默认十字线显示颜色
	 * </p>
	 */
	public static final int DEFAULT_CROSS_STAR_COLOR = Color.LTGRAY;

	/**
	 * <p>
	 * Price up stick's border color
	 * </p>
	 * <p>
	 * 値上がりローソクのボーダー色
	 * </p>
	 * <p>
	 * 阳线的边框颜色
	 * </p>
	 */
	private int positiveStickBorderColor = DEFAULT_POSITIVE_STICK_BORDER_COLOR;

	/**
	 * <p>
	 * Price up stick's fill color
	 * </p>
	 * <p>
	 * 値上がりローソクの色
	 * </p>
	 * <p>
	 * 阳线的填充颜色
	 * </p>
	 */
	protected int positiveStickFillColor = DEFAULT_POSITIVE_STICK_FILL_COLOR;

	/**
	 * <p>
	 * Price down stick's border color
	 * </p>
	 * <p>
	 * 値下りローソクのボーダー色
	 * </p>
	 * <p>
	 * 阴线的边框颜色
	 * </p>
	 */

	private int negativeStickBorderColor = DEFAULT_NEGATIVE_STICK_BORDER_COLOR;

	/**
	 * <p>
	 * Price down stick's fill color
	 * </p>
	 * <p>
	 * 値下りローソクの色
	 * </p>
	 * <p>
	 * 阴线的填充颜色
	 * </p>
	 */
	protected int negativeStickFillColor = DEFAULT_NEGATIVE_STICK_FILL_COLOR;

	/**
	 * <p>
	 * Price no change stick's color (cross-star,T-like etc.)
	 * </p>
	 * <p>
	 * クローススターの色（価格変動無し）
	 * </p>
	 * <p>
	 * 十字线显示颜色（十字星,一字平线,T形线的情况）
	 * </p>
	 */
	private int crossStarColor = DEFAULT_CROSS_STAR_COLOR;

	protected static Paint mPaintPositive = new Paint();

	static {
		mPaintPositive.setStrokeWidth(ChartConfig.LINE_WIDTH);
	}

	/**
	 * <p>
	 * Constructor of SlipCandleStickChart
	 * </p>
	 * <p>
	 * SlipCandleStickChart类对象的构造函数
	 * </p>
	 * <p>
	 * SlipCandleStickChartのコンストラクター
	 * </p>
	 * 
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public SlipCandleStickChart(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	/**
	 * <p>
	 * Constructor of SlipCandleStickChart
	 * </p>
	 * <p>
	 * SlipCandleStickChart类对象的构造函数
	 * </p>
	 * <p>
	 * SlipCandleStickChartのコンストラクター
	 * </p>
	 * 
	 * @param context
	 * @param attrs
	 */
	public SlipCandleStickChart(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * <p>
	 * Constructor of SlipCandleStickChart
	 * </p>
	 * <p>
	 * SlipCandleStickChart类对象的构造函数
	 * </p>
	 * <p>
	 * SlipCandleStickChartのコンストラクター
	 * </p>
	 * 
	 * @param context
	 */
	public SlipCandleStickChart(Context context) {
		super(context);
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
	}

	/**
	 * <p>
	 * draw sticks
	 * </p>
	 * <p>
	 * スティックを書く
	 * </p>
	 * <p>
	 * 绘制柱条
	 * </p>
	 * 
	 * @param canvas
	 */
	@Override
	protected void drawSticks(Canvas canvas) {
		if (null == stickData) {
			return;
		}
		if (stickData.size() <= 0) {
			return;
		}

		float stickWidth = getDataQuadrantPaddingWidth() / displayNumber;
		if(stickWidth < stickSpacing){
			stickSpacing = 1;
		}
		stickWidth -= stickSpacing;
		float stickX = getDataQuadrantPaddingStartX();
        midX.clear();
        midY.clear();
		dateList.clear();

		float openY, highY, lowY, closeY;
		OHLCEntity ohlc;
		for (int i = displayFrom; i < displayFrom + displayNumber; i++) {
			if(enableRefresh && i < 0){
				if(Math.abs(i) <= REFRESH_SIZE){
					canvas.drawText(REFRESH_TEXT[i + REFRESH_SIZE], stickX, getDataQuadrantPaddingHeight()/2, paintFont);
					stickX = stickX + stickSpacing + stickWidth;
					continue;
				}
			}

			if(stickData.size() <= i || i < 0)	break;
			ohlc = (OHLCEntity) stickData.get(i);
			openY = (float) ((1f - (ohlc.getOpen() - minValue) / (maxValue - minValue))
					* (getDataQuadrantPaddingHeight()) + getDataQuadrantPaddingStartY());
			highY = (float) ((1f - (ohlc.getHigh() - minValue) / (maxValue - minValue))
					* (getDataQuadrantPaddingHeight()) + getDataQuadrantPaddingStartY());
			lowY = (float) ((1f - (ohlc.getLow() - minValue) / (maxValue - minValue))
					* (getDataQuadrantPaddingHeight()) + getDataQuadrantPaddingStartY());
			closeY = (float) ((1f - (ohlc.getClose() - minValue) / (maxValue - minValue))
					* (getDataQuadrantPaddingHeight()) + getDataQuadrantPaddingStartY());

			if (ohlc.getOpen() < ohlc.getClose()) {
				mPaintPositive.setColor(positiveStickFillColor);
				if(kStyle == K_STYLE_CANDLE){
					// stick or line
					if(drawRedStickFill){
						if (stickWidth > ChartConfig.LINE_WIDTH) {
							//stick
							canvas.drawRect(stickX, closeY, stickX + stickWidth, openY,
									mPaintPositive);
							mPaintPositive.setColor(positiveStickFillColor);
						}
						canvas.drawLine(stickX + stickWidth / 2f, highY, stickX + stickWidth / 2f, lowY, mPaintPositive);
					}else{
						if (stickWidth > ChartConfig.LINE_WIDTH) {
							//line
							canvas.drawLine(stickX, closeY, stickX + stickWidth, closeY, mPaintPositive);
							canvas.drawLine(stickX, closeY, stickX, openY, mPaintPositive);
							canvas.drawLine(stickX + stickWidth, closeY, stickX + stickWidth, openY, mPaintPositive);
							canvas.drawLine(stickX, openY, stickX + stickWidth, openY, mPaintPositive);
						}
						canvas.drawLine(stickX + stickWidth / 2f, highY, stickX + stickWidth / 2f, closeY, mPaintPositive);
						canvas.drawLine(stickX + stickWidth / 2f, openY, stickX + stickWidth / 2f, lowY, mPaintPositive);
					}
				}else{

				}

			} else if (ohlc.getOpen() > ohlc.getClose()) {
				mPaintPositive.setColor(negativeStickFillColor);

				if(kStyle == K_STYLE_CANDLE){
					// stick or line
					if (stickWidth > ChartConfig.LINE_WIDTH) {
						canvas.drawRect(stickX, openY, stickX + stickWidth, closeY,
								mPaintPositive);
					}
					canvas.drawLine(stickX + stickWidth / 2f, highY, stickX
							+ stickWidth / 2f, lowY, mPaintPositive);
				}
//				else{
//					//K_STYLE_AMERICAN
//					canvas.drawLine(stickX + stickWidth / 2f, highY, stickX + stickWidth / 2f, lowY, mPaintPositive);
//					canvas.drawLine(stickX, openY, stickX + stickWidth / 2f, openY, mPaintPositive);
//					canvas.drawLine(stickX + stickWidth / 2f, closeY, stickX + stickWidth, closeY, mPaintPositive);
//				}

			} else {
//				mPaintPositive.setColor(negativeStickFillColor);
				if(kStyle == K_STYLE_CANDLE){
					// line or point
					if (stickWidth > ChartConfig.LINE_WIDTH) {
						canvas.drawLine(stickX, closeY, stickX + stickWidth, openY,
								mPaintPositive);
					}
					canvas.drawLine(stickX + stickWidth / 2f, highY, stickX
							+ stickWidth / 2f, lowY, mPaintPositive);
				}
			}

			if(kStyle == K_STYLE_AMERICAN){
				//K_STYLE_AMERICAN
				canvas.drawLine(stickX + stickWidth / 2f, highY, stickX + stickWidth / 2f, lowY, mPaintPositive);
				canvas.drawLine(stickX, openY, stickX + stickWidth / 2f, openY, mPaintPositive);
				canvas.drawLine(stickX + stickWidth / 2f, closeY, stickX + stickWidth, closeY, mPaintPositive);
			}

            midX.add(stickX + stickWidth / 2f);
            midY.add(closeY);
			dateList.add(ohlc.getDate());
			// next x
            stickX = stickX + stickSpacing + stickWidth;
        }
    }
	protected List<Float> midX = new ArrayList<>();
    protected List<Float> midY = new ArrayList<>();
    protected List<String> dateList = new ArrayList<>();

    @Override
    protected float getDataMidX(float x) {

        /*int dataIndexOf = getDataIndexOf(x);
        if(dataIndexOf >= midX.size()){
            return midX.get(midX.size() - 1);
        }
        return midX.get(dataIndexOf);*/
		return -1f;
    }

    @Override
    protected float getDataMidY(float x) {
		if(midY.size() == 0)    return -1f;
        int dataIndexOf = getDataIndexOf(x);
        if(dataIndexOf >= midY.size()){
            return midY.get(midY.size() - 1);
        }
        return midY.get(dataIndexOf);
    }

    /**
	 * @return the positiveStickBorderColor
	 */
	public int getPositiveStickBorderColor() {
		return positiveStickBorderColor;
	}

	/**
	 * @param positiveStickBorderColor
	 *            the positiveStickBorderColor to set
	 */
	public void setPositiveStickBorderColor(int positiveStickBorderColor) {
		this.positiveStickBorderColor = positiveStickBorderColor;
	}

	/**
	 * @return the positiveStickFillColor
	 */
	public int getPositiveStickFillColor() {
		return positiveStickFillColor;
	}

	/**
	 * @param positiveStickFillColor
	 *            the positiveStickFillColor to set
	 */
	public void setPositiveStickFillColor(int positiveStickFillColor) {
		this.positiveStickFillColor = positiveStickFillColor;
	}

	/**
	 * @return the negativeStickBorderColor
	 */
	public int getNegativeStickBorderColor() {
		return negativeStickBorderColor;
	}

	/**
	 * @param negativeStickBorderColor
	 *            the negativeStickBorderColor to set
	 */
	public void setNegativeStickBorderColor(int negativeStickBorderColor) {
		this.negativeStickBorderColor = negativeStickBorderColor;
	}

	/**
	 * @return the negativeStickFillColor
	 */
	public int getNegativeStickFillColor() {
		return negativeStickFillColor;
	}

	/**
	 * @param negativeStickFillColor
	 *            the negativeStickFillColor to set
	 */
	public void setNegativeStickFillColor(int negativeStickFillColor) {
		this.negativeStickFillColor = negativeStickFillColor;
	}

	/**
	 * @return the crossStarColor
	 */
	public int getCrossStarColor() {
		return crossStarColor;
	}

	/**
	 * @param crossStarColor
	 *            the crossStarColor to set
	 */
	public void setCrossStarColor(int crossStarColor) {
		this.crossStarColor = crossStarColor;
	}

	public void setkStyle(int kStyle) {
		this.kStyle = kStyle;
	}

	/**
	 * 红色蜡烛图形状
	 * @param drawRedStickFill	true-实体长方形; false-空心长方形
     */
	public void setDrawRedStickFill(boolean drawRedStickFill) {
		this.drawRedStickFill = drawRedStickFill;
	}

	public int[] getXYByDate(String date){
		/*int[] out = new int[2];
		int i = dateList.indexOf(date);
		if (i != -1) {
			out[0] = (int) midX.get(i).floatValue();
			out[1] = (int) midY.get(i).floatValue();
		}
		return out;*/


		int[] out = new int[2];
		if (null == stickData) {
			return out;
		}
		if (stickData.size() <= 0) {
			return out;
		}

		if (this.autoCalcValueRange) {
			calcValueRange();
		}
		float stickWidth = getDataQuadrantPaddingWidth() / displayNumber;
		if(stickWidth < stickSpacing){
			stickSpacing = 1;
		}
		stickWidth -= stickSpacing;
		float stickX = getDataQuadrantPaddingStartX();

		float openY, highY, lowY, closeY;
		OHLCEntity ohlc;
		for (int i = displayFrom; i < displayFrom + displayNumber; i++) {
			if(enableRefresh && i < 0){
				if(Math.abs(i) <= REFRESH_SIZE){
					stickX = stickX + stickSpacing + stickWidth;
					continue;
				}
			}

			if(stickData.size() <= i || i < 0)	break;
			ohlc = (OHLCEntity) stickData.get(i);
			openY = (float) ((1f - (ohlc.getOpen() - minValue) / (maxValue - minValue))
					* (getDataQuadrantPaddingHeight()) + getDataQuadrantPaddingStartY());
			highY = (float) ((1f - (ohlc.getHigh() - minValue) / (maxValue - minValue))
					* (getDataQuadrantPaddingHeight()) + getDataQuadrantPaddingStartY());
			lowY = (float) ((1f - (ohlc.getLow() - minValue) / (maxValue - minValue))
					* (getDataQuadrantPaddingHeight()) + getDataQuadrantPaddingStartY());
			closeY = (float) ((1f - (ohlc.getClose() - minValue) / (maxValue - minValue))
					* (getDataQuadrantPaddingHeight()) + getDataQuadrantPaddingStartY());

			if(TextUtils.equals(ohlc.getDate(), date)){
				out[0] = (int) (stickX + stickWidth / 2f);
				out[1] = (int) closeY;
				break;
			}
			// next x
			stickX = stickX + stickSpacing + stickWidth;
		}
		return out;
	}

}
