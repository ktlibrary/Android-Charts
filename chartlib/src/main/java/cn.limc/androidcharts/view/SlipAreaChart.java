/*
 * SlipAreaChart.java
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
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;

import java.util.List;

import cn.limc.androidcharts.entity.DateValueEntity;
import cn.limc.androidcharts.entity.LineEntity;

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
 * @version v1.0 2014/01/22 16:19:37
 * 
 */
public class SlipAreaChart extends SlipLineChart {

	/**
	 * <p>
	 * Constructor of SlipAreaChart
	 * </p>
	 * <p>
	 * SlipAreaChart类对象的构造函数
	 * </p>
	 * <p>
	 * SlipAreaChartのコンストラクター
	 * </p>
	 * 
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public SlipAreaChart(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	/**
	 * <p>
	 * Constructor of SlipAreaChart
	 * </p>
	 * <p>
	 * SlipAreaChart类对象的构造函数
	 * </p>
	 * <p>
	 * SlipAreaChartのコンストラクター
	 * </p>
	 * 
	 * @param context
	 * @param attrs
	 */
	public SlipAreaChart(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * <p>
	 * Constructor of SlipAreaChart
	 * </p>
	 * <p>
	 * SlipAreaChart类对象的构造函数
	 * </p>
	 * <p>
	 * SlipAreaChartのコンストラクター
	 * </p>
	 * 
	 * @param context
	 */
	public SlipAreaChart(Context context) {
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
        // draw lines
        super.onDraw(canvas);
        drawAreas(canvas);
    }

	/**
	 * <p>
	 * draw lines
	 * </p>
	 * <p>
	 * ラインを書く
	 * </p>
	 * <p>
	 * 绘制
	 * </p>
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

		int i = 0;
			// draw lines
//		for (int i = 0; i < linesData.size(); i++) {
			LineEntity<DateValueEntity> line = linesData
					.get(i );
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

			Paint mPaint = new Paint();
			mPaint.setColor(line.getLineColor());
			mPaint.setAlpha(60);
			mPaint.setAntiAlias(true);

			// set start point’s X
			startX = getDataQuadrantPaddingStartX() + lineLength / 2f;
			Path linePath = new Path();
			for (int j = displayFrom; j < displayFrom + displayNumber; j++) {
				if(lineData.size() <= j || j < 0)	break;
				float value = lineData.get(j).getValue();
				
				// calculate Y
				float valueY = (float) ((1f - (value - minValue)
						/ (maxValue - minValue)) * getDataQuadrantPaddingHeight())
						+ getDataQuadrantPaddingStartY();

				// if is not last point connect to previous point
				if (j == displayFrom) {
					linePath.moveTo(startX, getDataQuadrantPaddingEndY());
					linePath.lineTo(startX, valueY);
				}
				/*else if (j == displayFrom + displayNumber - 1) {
					linePath.lineTo(startX, valueY);
//					linePath.lineTo(startX, getDataQuadrantPaddingEndY());
				}*/
                else {
					linePath.lineTo(startX, valueY);
//					linePath.lineTo(startX, getDataQuadrantPaddingEndY());
				}
				startX = startX + 1 + lineLength;
			}
			linePath.lineTo(startX, getDataQuadrantPaddingEndY());
			linePath.close();

			canvas.drawPath(linePath, mPaint);
//		}
	}

	
}
