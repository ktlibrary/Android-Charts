/*
 * MASlipCandleStickChart.java
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
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;


import com.gustavo.chartlibrary.MathUtils;
import com.gustavo.chartlibrary.R;

import java.util.List;

import cn.limc.androidcharts.ChartConfig;
import cn.limc.androidcharts.entity.DKTDEntity;
import cn.limc.androidcharts.entity.DateValueEntity;
import cn.limc.androidcharts.entity.DateValueObject;
import cn.limc.androidcharts.entity.HldEnity;
import cn.limc.androidcharts.entity.LineEntity;
import cn.limc.androidcharts.entity.OHLCEntity;
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
 * @version v1.0 2014/01/21 12:03:25
 * 
 */
public class MASlipCandleStickChart extends SlipCandleStickChart {

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

	static int pointDis = 10;
	static float textDis = 15;

	private boolean drawMaxMin = true;
	private List<HldEnity> hldList;

	/**
	 * <p>
	 * Constructor of MASlipCandleStickChart
	 * </p>
	 * <p>
	 * MASlipCandleStickChart类对象的构造函数
	 * </p>
	 * <p>
	 * MASlipCandleStickChartのコンストラクター
	 * </p>
	 * 
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public MASlipCandleStickChart(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	/**
	 * <p>
	 * Constructor of MASlipCandleStickChart
	 * </p>
	 * <p>
	 * MASlipCandleStickChart类对象的构造函数
	 * </p>
	 * <p>
	 * MASlipCandleStickChartのコンストラクター
	 * </p>
	 * 
	 * @param context
	 * @param attrs
	 */
	public MASlipCandleStickChart(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * <p>
	 * Constructor of MASlipCandleStickChart
	 * </p>
	 * <p>
	 * MASlipCandleStickChart类对象的构造函数
	 * </p>
	 * <p>
	 * MASlipCandleStickChartのコンストラクター
	 * </p>
	 * 
	 * @param context
	 */
	public MASlipCandleStickChart(Context context) {
		super(context);

	}

	@Override
	protected void calcDataValueRange() {
		super.calcDataValueRange();
		double maxValue = this.maxValue;
		double minValue = this.minValue;

		if(linesData != null){
			// 逐条输出MA线
			for (int i = 0; i < this.linesData.size(); i++) {
				LineEntity<DateValueEntity> line = this.linesData.get(i);
				if (line != null && line.getLineData().size() > 0) {
					// 判断显示为方柱或显示为线条
					boolean validData = false;
					for (int j = displayFrom; j < displayFrom + displayNumber; j++) {
						if(j < 0)   continue;

						if(line.getLineData().size() <= j || j < 0)	break;
						DateValueEntity lineData = line.getLineData().get(j);
						if(!validData && lineData.getValue() != 0){
							validData = true;
						}

						if(validData){
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
	}

	/**
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
		drawLines(canvas);

		drawDKTD(canvas);

		drawPoints(canvas);

		drawExHint(canvas);

		drawDLJG(canvas);

		drawHLD(canvas);

		if(drawMaxMin){
			drawMaxMinValue(canvas);
		}
	}

	private void drawHLD(Canvas canvas) {
		if(hldList == null || hldList.size() <= 0)	return;

		float stickWidth = getDataQuadrantPaddingWidth() / displayNumber;
		if(stickWidth < stickSpacing){
			stickSpacing = 1;
		}
		stickWidth -= stickSpacing;
        float widthReal = stickWidth * 0.6f;
		float stickX = getDataQuadrantPaddingStartX();

		float openY, highY, lowY, closeY;
		HldEnity enity;
		for (int i = displayFrom; i < displayFrom + displayNumber; i++) {
			if (enableRefresh && i < 0) {
				if (Math.abs(i) <= REFRESH_SIZE) {
					canvas.drawText(REFRESH_TEXT[i + REFRESH_SIZE], stickX, getDataQuadrantPaddingHeight() / 2, paintFont);
					stickX = stickX + stickSpacing + stickWidth;
					continue;
				}
			}

			if(hldList.size() <= i || i < 0)	return;

			enity = hldList.get(i);
			highY = getValueY(enity.high);
			lowY = getValueY(enity.low);

			mPaintPositive.setColor(enity.color);
           
            if (widthReal >= ChartConfig.LINE_WIDTH) {
				canvas.drawRect(stickX + (stickWidth - widthReal) / 2, highY, stickX + (stickWidth - widthReal) / 2 + widthReal, lowY, mPaintPositive);
			}else{
				canvas.drawLine(stickX, highY, stickX, lowY, mPaintPositive);
			}

			// next x
			stickX = stickX + stickSpacing + stickWidth;
		}

	}

	private void drawMaxMinValue(Canvas canvas) {
		if (null == stickData) {
			return;
		}
		if (stickData.size() <= 0) {
			return;
		}

		paintFont.setColor(Color.BLACK);
		Rect rect = new Rect();
		paintFont.getTextBounds("9", 0, 1, rect);

		if(maxVIndex >= 0 && maxVIndex < midX.size()){
			Float maxVX = midX.get(maxVIndex);
			String s = "← " + MathUtils.toTwo(maxDataValue);
			if(maxVX + paintFont.measureText(s) > getDataQuadrantWidth()){
				maxVX = maxVX - paintFont.measureText(s);
				s = MathUtils.toTwo(maxDataValue) + " →";
			}
			canvas.drawText(s, maxVX, getDataQuadrantPaddingTop() + rect.height(), paintFont);
		}

		if(minVIndex >= 0 && minVIndex < midX.size()){
			Float minVX = midX.get(minVIndex);
			String s = "← " + MathUtils.toTwo(minDataValue);
			if(minVX + paintFont.measureText(s) > getDataQuadrantWidth()){
				minVX = minVX - paintFont.measureText(s);
				s = MathUtils.toTwo(minDataValue) + " →";
			}
			canvas.drawText(s, minVX, getDataQuadrantEndY(), paintFont);
		}
	}

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
							canvas.drawRect(stickX, closeY, stickX + stickWidth, openY, mPaintPositive);
						}else{
							canvas.drawLine(stickX, closeY, stickX, openY, mPaintPositive);
						}
						canvas.drawLine(stickX + stickWidth / 2f, highY, stickX + stickWidth / 2f, lowY, mPaintPositive);
					}else{
						if (stickWidth > ChartConfig.LINE_WIDTH) {
							//line
							canvas.drawLine(stickX, closeY, stickX + stickWidth, closeY, mPaintPositive);
							canvas.drawLine(stickX, closeY, stickX, openY, mPaintPositive);
							canvas.drawLine(stickX + stickWidth, closeY, stickX + stickWidth, openY, mPaintPositive);
							canvas.drawLine(stickX, openY, stickX + stickWidth, openY, mPaintPositive);
						}else{
							canvas.drawLine(stickX, closeY, stickX, openY, mPaintPositive);
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
					}else{
						canvas.drawLine(stickX, closeY, stickX, openY, mPaintPositive);
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
				if(i > 0){
					OHLCEntity preEntity = (OHLCEntity) stickData.get(i - 1);
					if(ohlc.getOpen() > preEntity.getClose()){
						mPaintPositive.setColor(positiveStickFillColor);
					}else{
						mPaintPositive.setColor(negativeStickFillColor);
					}
				}
				if(kStyle == K_STYLE_CANDLE){
					// line or point
					if (stickWidth > ChartConfig.LINE_WIDTH) {
						canvas.drawLine(stickX, closeY, stickX + stickWidth, openY, mPaintPositive);
					}else{
						canvas.drawLine(stickX, closeY, stickX, openY, mPaintPositive);
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

	private List<String> exDateList;


	/**
	 * 绘制除权提示点
	 * @param canvas
     */
	private void drawExHint(Canvas canvas) {
		if(exDateList == null || exDateList.size() <= 0)	return;
		if(dateList == null || dateList.size() <= 0)	return;
		if(midX == null || midX.size() <= 0)	return;

		float bottom = getDataQuadrantEndY();
		Bitmap bitmap;
		int indexOf;
		paintFont.setColor(Color.RED);
		int dateSize = dateList.size();
//		long day = 60 * 60 * 24;
		for (int i = 0; i < exDateList.size(); i++) {
			//	20170711000000
			for (int j = 0; j < dateSize; j++) {
				long exDate = Long.valueOf(exDateList.get(i));
				long date = Long.valueOf(dateList.get(j));
//				if(date >= exDate && date < exDate + day){
				if(date == exDate){
					indexOf = j;
					if (indexOf != -1 && indexOf < midX.size()) {
						float x = midX.get(indexOf);
						bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ex_hint);
						canvas.drawBitmap(bitmap, x - bitmap.getWidth() / 2, bottom - bitmap.getHeight(), paintFont);
					}
					break;
				}
			}

		}

	}

	private List<DKTDEntity> dktdList;

	public void setDktdList(List<DKTDEntity> dktdList) {
		this.dktdList = dktdList;
	}

	public List<DKTDEntity> getDktdList() {
		return dktdList;
	}

	private void drawDKTD(Canvas canvas) {

		if (dktdList == null || dktdList.size() <= 0) return;
		if (stickData == null || stickData.size() <= 0) return;
		if(dateList == null || dateList.size() <= 0)	return;
		if(midX == null || midX.size() <= 0)	return;

		DKTDEntity dktdEntity;
		PointF highPoint = new PointF();
		PointF lowPoint = new PointF();
		float x, high, low;

		int size = getDisplayFrom() + getDisplayNumber();
		Path onePath = new Path();
		Path anotherPath = new Path();
		int oneColor = 0, anotherColor = 0;
		boolean drawArea = false;
		Rect textRect = null;
		for (int i = getDisplayFrom(); i < size; i++) {
			if(enableRefresh && i < 0){
				if(Math.abs(i) <= REFRESH_SIZE){
					continue;
				}
			}

			if(dktdList.size() <= i)	break;
			if(midX.size() <= i - getDisplayFrom())	break;

			dktdEntity = dktdList.get(i);
			if(dktdEntity == null)	continue;

			x = midX.get(i - getDisplayFrom());
			high = getValueY(dktdEntity.getHighP());
			low = getValueY(dktdEntity.getLowP());

			if(high > getDataQuadrantHeight()){
				high = getDataQuadrantHeight();
			}
			if(low > getDataQuadrantHeight()){
				low = getDataQuadrantHeight();
			}

//			if(dktdEntity.getHigh() != 0 || dktdEntity.getLow() != 0){
//				mPaint.setColor(dktdEntity.getStickColor());
//				canvas.drawLine(x, high, x, low, mPaint);
//			}

			if(i == displayFrom){
				if(dktdEntity.getHigh() != 0 || dktdEntity.getLow() != 0){
					paintLine.setColor(dktdEntity.getStickColor());
					canvas.drawLine(x, high, x, low, paintLine);
				}
			}

			if(highPoint.x != 0 || highPoint.y != 0){
				paintLine.setColor(dktdEntity.getUpColor());
				canvas.drawLine(highPoint.x, highPoint.y, x, high, paintLine);
				paintLine.setColor(dktdEntity.getDownColor());
				canvas.drawLine(lowPoint.x, lowPoint.y, x, low, paintLine);

				//画色块区域
				if(dktdEntity.getHigh() != 0 || dktdEntity.getLow() != 0){
					if(oneColor == 0){
						oneColor = dktdEntity.getStickColor();
					}
					if(drawArea){
						if(oneColor == dktdEntity.getStickColor()){
							onePath.moveTo(lowPoint.x, lowPoint.y);
							onePath.lineTo(highPoint.x, highPoint.y);
							onePath.lineTo(x, high);
							onePath.lineTo(x, low);

						}else{
							if(anotherColor == 0){
								anotherColor = dktdEntity.getStickColor();
							}
							anotherPath.moveTo(lowPoint.x, lowPoint.y);
							anotherPath.lineTo(highPoint.x, highPoint.y);
							anotherPath.lineTo(x, high);
							anotherPath.lineTo(x, low);
						}
					}
					drawArea = true;

				}else{
					drawArea = false;
				}
			}

			if(dktdEntity.getArrowType() != 0){
				if(dktdEntity.isDrawText()){
					if(textRect == null){
						textRect = new Rect();
						paintFont.getTextBounds("底", 0, 1, textRect);
					}
					if(dktdEntity.getArrowType() == 1){

						float y = getValueY(stickData.get(i).getHigh());
						if(y - textRect.height() * 2 < textRect.height()){
							y = textRect.height() + getDataQuadrantPaddingStartY();
						}else{
							y -= textRect.height() * 2;
						}
						canvas.drawText("顶", x - textRect.width() / 2, y, paintFont);
					}else{
						float y = getValueY(stickData.get(i).getLow());
						if(y + textRect.height() * 2 > getDataQuadrantHeight()){
							y = getDataQuadrantHeight() - 6;
						}else{
							y += + textRect.height() * 2;
						}
						canvas.drawText("底", x - textRect.width() / 2, y, paintFont);
					}

				}else{
					Bitmap bitmap;
					float yBitmap;
					if(dktdEntity.getArrowType() == 1){
						bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.arrow_down_green);
						yBitmap = getValueY(stickData.get(i).getHigh()) - bitmap.getHeight() * 2;
						if(high - bitmap.getHeight() * 2 < yBitmap){
							yBitmap = high - bitmap.getHeight() * 2;
						}
						if(yBitmap < bitmap.getHeight()){
							yBitmap = bitmap.getHeight();
						}
					}else{
						bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.arrow_up_red);
						yBitmap = getValueY(stickData.get(i).getLow()) + bitmap.getHeight();
						if(low + bitmap.getHeight() > yBitmap){
							yBitmap = low + bitmap.getHeight();
						}
						if(yBitmap > getDataQuadrantPaddingStartY() + getDataQuadrantHeight()){
							yBitmap = getDataQuadrantPaddingStartY() + getDataQuadrantHeight() - bitmap.getHeight() - 5;
						}
					}
					canvas.drawBitmap(bitmap, x - bitmap.getWidth() / 2, yBitmap, paintFont);
				}
			}

			if(dktdEntity.getHighP() != 0 || dktdEntity.getLowP() != 0){
				highPoint.set(x, high);
				lowPoint.set(x, low);
			}
		}

		if(oneColor != 0){
			paintLine.setColor(oneColor);
			canvas.drawPath(onePath, paintLine);
			onePath.close();
		}

		if(anotherColor != 0){
			paintLine.setColor(anotherColor);
			canvas.drawPath(anotherPath, paintLine);
			anotherPath.close();
		}
	}

	private List<PointEntity> pointsData;

	public List<PointEntity> getPointsData() {
		return pointsData;
	}

	public void setPointsData(List<PointEntity> pointsData) {
		this.pointsData = pointsData;
	}

	private void drawPoints(Canvas canvas){

		if(pointsData == null || pointsData.size() <= 0)	return;
		if(dateList == null || dateList.size() <= 0)	return;
		if(midX == null || midX.size() <= 0)	return;

		Rect rect = new Rect();
		paintFont.getTextBounds("9", 0, 1, rect);
		textDis = pointDis + ChartConfig.CIRCLE_RADIO + rect.height() + rect.height() * 0.5f;
		int size = pointsData.size();
		float x, y;
		PointEntity pointEntity;
		for (int i = 0; i < size; i++) {
			pointEntity = pointsData.get(i);
			if (pointEntity != null) {
				int indexOf = dateList.indexOf(pointEntity.getDate());
				if (indexOf != -1) {

					paintFont.setColor(pointEntity.getColor());
					if(indexOf < midX.size()){
						x = midX.get(indexOf);

						if(indexOf + displayFrom < stickData.size() && indexOf + displayFrom >= 0) {
							if (pointEntity.isUp()) {
								y = getValueY(stickData.get(indexOf + displayFrom).getHigh());
								canvas.drawCircle(x, y < pointDis ? 5 : y - pointDis, ChartConfig.CIRCLE_RADIO, paintFont);
								if (pointEntity.isDrawText()) {
									canvas.drawText(pointEntity.getContent(), x - rect.width() / 2, y < textDis ? rect.height() : y - pointDis - ChartConfig.CIRCLE_RADIO - rect.height() * 0.5f, paintFont);
								}

							} else {
								y = getValueY(stickData.get(indexOf + displayFrom).getLow());
								float yAxis = getDataQuadrantPaddingStartY() + getDataQuadrantPaddingHeight();
								canvas.drawCircle(x, y + pointDis > yAxis ? yAxis : y + pointDis, ChartConfig.CIRCLE_RADIO, paintFont);
								if (pointEntity.isDrawText()) {
									canvas.drawText(pointEntity.getContent(), x - rect.width() / 2, y + textDis > yAxis ? yAxis : y + textDis, paintFont);
								}
							}
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
		if (stickData == null || stickData.size() <= 0) return;
		if (linesData == null || linesData.size() <= 0) return;

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
			boolean draw = false;
			for (int j = super.getDisplayFrom(); j < super.getDisplayFrom()
					+ super.getDisplayNumber(); j++) {
				if(enableRefresh && j < 0){
					if(Math.abs(j) <= REFRESH_SIZE){
						startX = startX + 1 + lineLength;
						continue;
					}
				}

				if(lineData.size() <= j)	break;
				float value = lineData.get(j).getValue();
				if(!draw && value != 0){
					draw = true;
				}
				// calculate Y
				float valueY = (float) ((1f - (value - minValue)
						/ (maxValue - minValue)) * getDataQuadrantPaddingHeight())
						+ getDataQuadrantPaddingStartY();

				// if is not last point connect to previous point
				if (draw && j > 0 && j > super.getDisplayFrom()) {
					if(ptFirst == null){
						ptFirst = new PointF(startX, valueY);
					}else{
						canvas.drawLine(ptFirst.x, ptFirst.y, startX, valueY, paintLine);
						ptFirst = new PointF(startX, valueY);
					}
				}
				// reset
//				ptFirst = new PointF(startX, valueY);
				startX = startX + 1 + lineLength;
			}
		}
	}

	private List<DateValueObject> dljgList;

	private void drawDLJG(Canvas canvas) {
		if(dljgList == null || dljgList.size() <= 0)	return;
		if(dateList == null || dateList.size() <= 0)	return;
		if (stickData == null || stickData.size() <= 0) return;
		if(midX == null || midX.size() <= 0)	return;

		float top = getDataQuadrantStartY();
		float bottom = getDataQuadrantEndY();
		int indexOf;

		Rect rect = new Rect();
		paintFont.getTextBounds("钝", 0, 1, rect);
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
					paintFont.setColor(dateValueObject.getColor());
					canvas.drawText(dateValueObject.getValue(), midX.get(indexOf), drawY, paintFont);
				}
			}

		}

	}

	public void setDljgList(List<DateValueObject> dljgList) {
		this.dljgList = dljgList;
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
	public void setLinesData(List<LineEntity<DateValueEntity>> linesData) {
		this.linesData = linesData;
	}
	
	@Override
	public void getPosition(int index) {
		if (positionChangedListener != null) {
			positionChangedListener.showData(index);//回调		拿到位置后的处理
		}
		super.getPosition(index);
	}

	public void setExDateList(List<String> exDateList) {


		this.exDateList = exDateList;
	}

	public void setDrawMaxMin(boolean drawMaxMin) {
		this.drawMaxMin = drawMaxMin;
	}

    public void setHldList(List<HldEnity> hldList) {
        this.hldList = hldList;
    }
}
