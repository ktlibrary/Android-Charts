/*
 * GridChart.java
 * Android-Charts
 *
 * Created by limc on 2011/05/29.
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
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;


import java.util.ArrayList;
import java.util.List;

import cn.limc.androidcharts.ChartConfig;
import cn.limc.androidcharts.event.ITouchEventNotify;
import cn.limc.androidcharts.event.ITouchEventResponse;
import cn.limc.androidcharts.listener.OnLongPressListener;
import cn.limc.androidcharts.listener.PositionChangedListener;
import cn.limc.androidcharts.listener.XYValueFormatter;

/**
 * 
 * <p>
 * GridChart is base type of all the charts that use a grid to display like
 * line-chart stick-chart etc. GridChart implemented a simple grid with basic
 * functions what can be used in it's inherited charts.
 * </p>
 * <p>
 * GridChartは全部グリドチャートのベスクラスです、一部処理は共通化け実現した。
 * </p>
 * <p>
 * GridChart是所有网格图表的基础类对象，它实现了基本的网格图表功能，这些功能将被它的继承类使用
 * </p>
 * 
 * @author limc
 * @version v1.0 2011/05/30 14:19:50
 * 
 */
public class GridChart extends BaseChart implements ITouchEventNotify,
        ITouchEventResponse {

	protected static final int DEFAULT_DISPLAY_FROM = 0;
	protected int displayFrom = DEFAULT_DISPLAY_FROM;
	public static final int DEFAULT_DISPLAY_NUMBER = 50;
	protected int displayNumber = DEFAULT_DISPLAY_NUMBER;


	public static final int ZOOM_BASE_LINE_CENTER = 0;
	public static final int ZOOM_BASE_LINE_LEFT = 1;
	public static final int ZOOM_BASE_LINE_RIGHT = 2;

	public static final int DEFAULT_MIN_DISPLAY_NUMBER = 20;
	public static final int DEFAULT_ZOOM_BASE_LINE = ZOOM_BASE_LINE_CENTER;

	protected int minDisplayNumber = ChartConfig.MIN_DISPLAY_NUMBER;
	protected int maxDisplayNumber = ChartConfig.MAX_DISPLAY_NUMBER;
	protected int zoomBaseLine = DEFAULT_ZOOM_BASE_LINE;

	protected boolean enableRefresh;
	protected static final String[] REFRESH_ING = {"","加", "载", "中", ""};
	protected static final String[] REFRESH_HINT = {"","加", "载", "数", "据"};
	protected static String[] REFRESH_TEXT = REFRESH_HINT;

	protected static int REFRESH_SIZE = REFRESH_TEXT.length;

	public static final int AXIS_X_POSITION_BOTTOM = 1 << 0;
	@Deprecated
	public static final int AXIS_X_POSITION_TOP = 1 << 1;
	public static final int AXIS_Y_POSITION_LEFT = 1 << 2;
	public static final int AXIS_Y_POSITION_RIGHT = 1 << 3;
	public static final int AXIS_Y_POSITION_LEFT_INSIDE = 1 << 4;

	public static final int TITLE_ALL = 0;
	public static final int TITLE_ONLY_FIRST_LAST = 1;

	protected int tilteMode = TITLE_ALL;

	protected static final int NONE = 0;
	protected static final int ZOOM = 1;
	protected static final int DOWN = 2;
	protected static final int DRAG = 3;
	protected int touchMode;
	protected PointF startPoint;
	protected PointF startPointA;
	protected PointF startPointB;
	protected float olddistance = 0f;
	protected float newdistance = 0f;
	/**
	 * 默认允许缩放
	 */
	protected boolean enableZoom = true;
	protected boolean enableDrag = true;


	/**
	 * <p>
	 * default background color
	 * </p>
	 * <p>
	 * 背景の色のデフォルト値
	 * </p>
	 * <p>
	 * 默认背景色
	 * </p>
	 */
	public  int DEFAULT_BACKGROUND_COLOR = Color.TRANSPARENT;

	@Override
	public void setBackgroundColor(int color) {
		super.setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
	}
	/**
	 * <p>
	 * default color of X axis
	 * </p>
	 * <p>
	 * X軸の色のデフォルト値
	 * </p>
	 * <p>
	 * 默认坐标轴X的显示颜色
	 * </p>
	 */
	public static final int DEFAULT_AXIS_X_COLOR = ChartConfig.COLOR_XY;

	/**
	 * <p>
	 * default color of Y axis
	 * </p>
	 * <p>
	 * Y軸の色のデフォルト値
	 * </p>
	 * <p>
	 * 默认坐标轴Y的显示颜色
	 * </p>
	 */
	public static final int DEFAULT_AXIS_Y_COLOR = ChartConfig.COLOR_XY;
	public static final float DEFAULT_AXIS_WIDTH = 1;

	public static final int DEFAULT_AXIS_X_POSITION = AXIS_X_POSITION_BOTTOM;

	public static final int DEFAULT_AXIS_Y_POSITION = AXIS_Y_POSITION_LEFT_INSIDE;

	/**
	 * <p>
	 * default color of grid‘s longitude line
	 * </p>
	 * <p>
	 * 経線の色のデフォルト値
	 * </p>
	 * <p>
	 * 默认网格经线的显示颜色
	 * </p>
	 */
	public static final int DEFAULT_LONGITUDE_COLOR = ChartConfig.COLOR_LONGITUDE_LINE;

	/**
	 * <p>
	 * default color of grid‘s latitude line
	 * </p>
	 * <p>
	 * 緯線の色のデフォルト値
	 * </p>
	 * <p>
	 * 默认网格纬线的显示颜色
	 * </p>
	 */
	public static final int DEFAULT_LAITUDE_COLOR = ChartConfig.COLOR_LONGITUDE_LINE;

	/**
	 * <p>
	 * default margin of the axis to the left border
	 * </p>
	 * <p>
	 * 轴線より左枠線の距離のデフォルト値
	 * </p>
	 * <p>
	 * 默认轴线左边距
	 * </p>
	 */
	@Deprecated
	public static final float DEFAULT_AXIS_MARGIN_LEFT = 42f;
	public static final float DEFAULT_AXIS_Y_TITLE_QUADRANT_WIDTH = 16f;

	/**
	 * <p>
	 * default margin of the axis to the bottom border
	 * </p>
	 * <p>
	 * 轴線より下枠線の距離のデフォルト値
	 * </p>
	 * <p>
	 * 默认轴线下边距
	 * </p>
	 */
	@Deprecated
	public static final float DEFAULT_AXIS_MARGIN_BOTTOM = 16f;
	public static final float DEFAULT_AXIS_X_TITLE_QUADRANT_HEIGHT = 16f;

	/**
	 * <p>
	 * default margin of the axis to the top border
	 * </p>
	 * <p>
	 * 轴線より上枠線の距離のデフォルト値
	 * </p>
	 * <p>
	 * 默认轴线上边距
	 * </p>
	 */
	@Deprecated
	public static final float DEFAULT_AXIS_MARGIN_TOP = 5f;
	public static final float DEFAULT_DATA_QUADRANT_PADDING_TOP = 5f;
	public static final float DEFAULT_DATA_QUADRANT_PADDING_BOTTOM = 5f;

	/**
	 * <p>
	 * default margin of the axis to the right border
	 * </p>
	 * <p>
	 * 轴線より右枠線の距離のデフォルト値
	 * </p>
	 * <p>
	 * 轴线右边距
	 * </p>
	 */
	@Deprecated
	public static final float DEFAULT_AXIS_MARGIN_RIGHT = 5f;
	public static final float DEFAULT_DATA_QUADRANT_PADDING_LEFT = 5f;
	public static final float DEFAULT_DATA_QUADRANT_PADDING_RIGHT = 5f;

	/**
	 * <p>
	 * default numbers of grid‘s latitude line
	 * </p>
	 * <p>
	 * 緯線の数量のデフォルト値
	 * </p>
	 * <p>
	 * 网格纬线的数量
	 * </p>
	 */
	public static final int DEFAULT_LATITUDE_NUM = 4;

	/**
	 * <p>
	 * default numbers of grid‘s longitude line
	 * </p>
	 * <p>
	 * 経線の数量のデフォルト値
	 * </p>
	 * <p>
	 * 网格经线的数量
	 * </p>
	 */
	public static final int DEFAULT_LONGITUDE_NUM = ChartConfig.LONGITUDE_NUM;

	/**
	 * <p>
	 * Should display longitude line?
	 * </p>
	 * <p>
	 * 経線を表示するか?
	 * </p>
	 * <p>
	 * 默认经线是否显示
	 * </p>
	 */
	public static final boolean DEFAULT_DISPLAY_LONGITUDE = Boolean.TRUE;

	/**
	 * <p>
	 * Should display longitude as dashed line?
	 * </p>
	 * <p>
	 * 経線を点線にするか?
	 * </p>
	 * <p>
	 * 默认经线是否显示为虚线
	 * </p>
	 */
	public static final boolean DEFAULT_DASH_LONGITUDE = Boolean.TRUE;

	/**
	 * <p>
	 * Should display longitude line?
	 * </p>
	 * <p>
	 * 緯線を表示するか?
	 * </p>
	 * <p>
	 * 纬线是否显示
	 * </p>
	 */
	public static final boolean DEFAULT_DISPLAY_LATITUDE = Boolean.TRUE;

	/**
	 * <p>
	 * Should display latitude as dashed line?
	 * </p>
	 * <p>
	 * 緯線を点線にするか?
	 * </p>
	 * <p>
	 * 纬线是否显示为虚线
	 * </p>
	 */
	public static final boolean DEFAULT_DASH_LATITUDE = Boolean.TRUE;

	/**
	 * <p>
	 * Should display the degrees in X axis?
	 * </p>
	 * <p>
	 * X軸のタイトルを表示するか?
	 * </p>
	 * <p>
	 * X轴上的标题是否显示
	 * </p>
	 */
	public static final boolean DEFAULT_DISPLAY_LONGITUDE_TITLE = Boolean.TRUE;

	/**
	 * <p>
	 * Should display the degrees in Y axis?
	 * </p>
	 * <p>
	 * Y軸のタイトルを表示するか?
	 * </p>
	 * <p>
	 * 默认Y轴上的标题是否显示
	 * </p>
	 */
	public static final boolean DEFAULT_DISPLAY_LATITUDE_TITLE = Boolean.TRUE;

	/**
	 * <p>
	 * Should display the border?
	 * </p>
	 * <p>
	 * 枠を表示するか?
	 * </p>
	 * <p>
	 * 默认控件是否显示边框
	 * </p>
	 */
	public static final boolean DEFAULT_DISPLAY_BORDER = Boolean.TRUE;

	/**
	 * <p>
	 * default color of text for the longitude　degrees display
	 * </p>
	 * <p>
	 * 経度のタイトルの色のデフォルト値
	 * </p>
	 * <p>
	 * 默认经线刻度字体颜色
	 * </p>
	 */
	public static final int DEFAULT_BORDER_COLOR = ChartConfig.COLOR_BORDER;

	public static final float DEFAULT_BORDER_WIDTH = 1f;

	/**
	 * <p>
	 * default color of text for the longitude　degrees display
	 * </p>
	 * <p>
	 * 経度のタイトルの色のデフォルト値
	 * </p>
	 * <p>
	 * 经线刻度字体颜色
	 * </p>
	 */
	public static final int DEFAULT_LONGITUDE_FONT_COLOR = ChartConfig.COLOR_LONGITUDE_FONT;

	/**
	 * <p>
	 * default font size of text for the longitude　degrees display
	 * </p>
	 * <p>
	 * 経度のタイトルのフォントサイズのデフォルト値
	 * </p>
	 * <p>
	 * 经线刻度字体大小
	 * </p>
	 */
	public static final int DEFAULT_LONGITUDE_FONT_SIZE = ChartConfig.LONGITUDE_FONT_SIZE;

	/**
	 * <p>
	 * default color of text for the latitude　degrees display
	 * </p>
	 * <p>
	 * 緯度のタイトルの色のデフォルト値
	 * </p>
	 * <p>
	 * 纬线刻度字体颜色
	 * </p>
	 */
	public static final int DEFAULT_LATITUDE_FONT_COLOR = ChartConfig.COLOR_LONGITUDE_FONT;

	/**
	 * <p>
	 * default font size of text for the latitude　degrees display
	 * </p>
	 * <p>
	 * 緯度のタイトルのフォントサイズのデフォルト値
	 * </p>
	 * <p>
	 * 默认纬线刻度字体大小
	 * </p>
	 */
	public static final int DEFAULT_LATITUDE_FONT_SIZE = ChartConfig.LATITUDE_FONT_SIZE;

	public static final int DEFAULT_CROSS_LINES_COLOR = ChartConfig.COLOR_CROSS_LINE;
	public static final int DEFAULT_CROSS_LINES_FONT_COLOR = ChartConfig.COLOR_CROSS_FONT;

	/**
	 * <p>
	 * default titles' max length for display of Y axis
	 * </p>
	 * <p>
	 * Y軸の表示用タイトルの最大文字長さのデフォルト値
	 * </p>
	 * <p>
	 * 默认Y轴标题最大文字长度
	 * </p>
	 */
	public static final int DEFAULT_LATITUDE_MAX_TITLE_LENGTH = 5;

	/**
	 * <p>
	 * default dashed line type
	 * </p>
	 * <p>
	 * 点線タイプのデフォルト値
	 * </p>
	 * <p>
	 * 默认虚线效果
	 * </p>
	 */
	public static final PathEffect DEFAULT_DASH_EFFECT = new DashPathEffect(
			new float[] { 3, 3, 3, 3 }, 1);

	/**
	 * <p>
	 * Should display the Y cross line if grid is touched?
	 * </p>
	 * <p>
	 * タッチしたポイントがある場合、十字線の垂直線を表示するか?
	 * </p>
	 * <p>
	 * 默认在控件被点击时，显示十字竖线线
	 * </p>
	 */
	public static final boolean DEFAULT_DISPLAY_CROSS_X_ON_TOUCH = true;

	/**
	 * <p>
	 * Should display the Y cross line if grid is touched?
	 * </p>
	 * <p>
	 * タッチしたポイントがある場合、十字線の水平線を表示するか?
	 * </p>
	 * <p>
	 * 默认在控件被点击时，显示十字横线线
	 * </p>
	 */
	public static final boolean DEFAULT_DISPLAY_CROSS_Y_ON_TOUCH = true;

	/**
	 * <p>
	 * Color of X axis
	 * </p>
	 * <p>
	 * X軸の色
	 * </p>
	 * <p>
	 * 坐标轴X的显示颜色
	 * </p>
	 */
	private int axisXColor = DEFAULT_AXIS_X_COLOR;

	/**
	 * <p>
	 * Color of Y axis
	 * </p>
	 * <p>
	 * Y軸の色
	 * </p>
	 * <p>
	 * 坐标轴Y的显示颜色
	 * </p>
	 */
	private int axisYColor = DEFAULT_AXIS_Y_COLOR;

	private float axisWidth = DEFAULT_AXIS_WIDTH;

	protected int axisXPosition = DEFAULT_AXIS_X_POSITION;

	protected int axisYPosition = DEFAULT_AXIS_Y_POSITION;

	/**
	 * <p>
	 * Color of grid‘s longitude line
	 * </p>
	 * <p>
	 * 経線の色
	 * </p>
	 * <p>
	 * 网格经线的显示颜色
	 * </p>
	 */
	private int longitudeColor = DEFAULT_LONGITUDE_COLOR;

	/**
	 * <p>
	 * Color of grid‘s latitude line
	 * </p>
	 * <p>
	 * 緯線の色
	 * </p>
	 * <p>
	 * 网格纬线的显示颜色
	 * </p>
	 */
	private int latitudeColor = DEFAULT_LAITUDE_COLOR;

	/**
	 * <p>
	 * Margin of the axis to the left border
	 * </p>
	 * <p>
	 * 轴線より左枠線の距離
	 * </p>
	 * <p>
	 * 轴线左边距
	 * </p>
	 */
	protected float axisYTitleQuadrantWidth = DEFAULT_AXIS_Y_TITLE_QUADRANT_WIDTH;

	/**
	 * <p>
	 * Margin of the axis to the bottom border
	 * </p>
	 * <p>
	 * 轴線より下枠線の距離
	 * </p>
	 * <p>
	 * 轴线下边距
	 * </p>
	 */
	protected float axisXTitleQuadrantHeight = DEFAULT_AXIS_X_TITLE_QUADRANT_HEIGHT;

	/**
	 * <p>
	 * Margin of the axis to the top border
	 * </p>
	 * <p>
	 * 轴線より上枠線の距離
	 * </p>
	 * <p>
	 * 轴线上边距
	 * </p>
	 */
	protected float dataQuadrantPaddingTop = DEFAULT_DATA_QUADRANT_PADDING_TOP;

	/**
	 * <p>
	 * Margin of the axis to the right border
	 * </p>
	 * <p>
	 * 轴線より右枠線の距離
	 * </p>
	 * <p>
	 * 轴线左边距
	 * </p>
	 */
	protected float dataQuadrantPaddingLeft = DEFAULT_DATA_QUADRANT_PADDING_LEFT;
	protected float dataQuadrantPaddingBottom = DEFAULT_DATA_QUADRANT_PADDING_BOTTOM;

	/**
	 * <p>
	 * Margin of the axis to the right border
	 * </p>
	 * <p>
	 * 轴線より右枠線の距離
	 * </p>
	 * <p>
	 * 轴线右边距
	 * </p>
	 */
	protected float dataQuadrantPaddingRight = DEFAULT_DATA_QUADRANT_PADDING_RIGHT;

	/**
	 * <p>
	 * Should display the degrees in X axis?
	 * </p>
	 * <p>
	 * X軸のタイトルを表示するか?
	 * </p>
	 * <p>
	 * X轴上的标题是否显示
	 * </p>
	 */
	private boolean displayLongitudeTitle = DEFAULT_DISPLAY_LONGITUDE_TITLE;

	/**
	 * <p>
	 * Should display the degrees in Y axis?
	 * </p>
	 * <p>
	 * Y軸のタイトルを表示するか?
	 * </p>
	 * <p>
	 * Y轴上的标题是否显示
	 * </p>
	 */
	private boolean displayLatitudeTitle = DEFAULT_DISPLAY_LATITUDE_TITLE;

	/**
	 * <p>
	 * Numbers of grid‘s latitude line
	 * </p>
	 * <p>
	 * 緯線の数量
	 * </p>
	 * <p>
	 * 网格纬线的数量
	 * </p>
	 */
	protected int latitudeNum = DEFAULT_LATITUDE_NUM;

	/**
	 * <p>
	 * Numbers of grid‘s longitude line
	 * </p>
	 * <p>
	 * 経線の数量
	 * </p>
	 * <p>
	 * 网格经线的数量
	 * </p>
	 */
	protected int longitudeNum = DEFAULT_LONGITUDE_NUM;

	/**
	 * <p>
	 * Should display longitude line?
	 * </p>
	 * <p>
	 * 経線を表示するか?
	 * </p>
	 * <p>
	 * 经线是否显示
	 * </p>
	 */
	private boolean displayLongitude = DEFAULT_DISPLAY_LONGITUDE;

	/**
	 * <p>
	 * Should display longitude as dashed line?
	 * </p>
	 * <p>
	 * 経線を点線にするか?
	 * </p>
	 * <p>
	 * 经线是否显示为虚线
	 * </p>
	 */
	private boolean dashLongitude = DEFAULT_DASH_LONGITUDE;

	/**
	 * <p>
	 * Should display longitude line?
	 * </p>
	 * <p>
	 * 緯線を表示するか?
	 * </p>
	 * <p>
	 * 纬线是否显示
	 * </p>
	 */
	private boolean displayLatitude = DEFAULT_DISPLAY_LATITUDE;

	private boolean displayHorizontalLine = true;
	private boolean displayVerticalLine = true;

	/**
	 * <p>
	 * Should display latitude as dashed line?
	 * </p>
	 * <p>
	 * 緯線を点線にするか?
	 * </p>
	 * <p>
	 * 纬线是否显示为虚线
	 * </p>
	 */
	private boolean dashLatitude = DEFAULT_DASH_LATITUDE;

	/**
	 * <p>
	 * dashed line type
	 * </p>
	 * <p>
	 * 点線タイプ?
	 * </p>
	 * <p>
	 * 虚线效果
	 * </p>
	 */
	private PathEffect dashEffect = DEFAULT_DASH_EFFECT;

	/**
	 * <p>
	 * Should display the border?
	 * </p>
	 * <p>
	 * 枠を表示するか?
	 * </p>
	 * <p>
	 * 控件是否显示边框
	 * </p>
	 */
	private boolean displayBorder = DEFAULT_DISPLAY_BORDER;

	/**
	 * <p>
	 * Color of grid‘s border line
	 * </p>
	 * <p>
	 * 枠線の色
	 * </p>
	 * <p>
	 * 图边框的颜色
	 * </p>
	 */
	private int borderColor = DEFAULT_BORDER_COLOR;

	protected float borderWidth = DEFAULT_BORDER_WIDTH;

	/**
	 * <p>
	 * Color of text for the longitude　degrees display
	 * </p>
	 * <p>
	 * 経度のタイトルの色
	 * </p>
	 * <p>
	 * 经线刻度字体颜色
	 * </p>
	 */
	private int longitudeFontColor = DEFAULT_LONGITUDE_FONT_COLOR;

	/**
	 * <p>
	 * Font size of text for the longitude　degrees display
	 * </p>
	 * <p>
	 * 経度のタイトルのフォントサイズ
	 * </p>
	 * <p>
	 * 经线刻度字体大小
	 * </p>
	 */
	private int longitudeFontSize = DEFAULT_LONGITUDE_FONT_SIZE;

	/**
	 * <p>
	 * Color of text for the latitude　degrees display
	 * </p>
	 * <p>
	 * 緯度のタイトルの色
	 * </p>
	 * <p>
	 * 纬线刻度字体颜色
	 * </p>
	 */
	private int latitudeFontColor = DEFAULT_LATITUDE_FONT_COLOR;

	/**
	 * <p>
	 * Font size of text for the latitude　degrees display
	 * </p>
	 * <p>
	 * 緯度のタイトルのフォントサイズ
	 * </p>
	 * <p>
	 * 纬线刻度字体大小
	 * </p>
	 */
	protected int latitudeFontSize = DEFAULT_LATITUDE_FONT_SIZE;

	/**
	 * <p>
	 * Color of cross line inside grid when touched
	 * </p>
	 * <p>
	 * タッチしたポイント表示用十字線の色
	 * </p>
	 * <p>
	 * 十字交叉线颜色
	 * </p>
	 */
	private int crossLinesColor = DEFAULT_CROSS_LINES_COLOR;

	/**
	 * <p>
	 * Color of cross line degree text when touched
	 * </p>
	 * <p>
	 * タッチしたポイント表示用十字線度数文字の色
	 * </p>
	 * <p>
	 * 十字交叉线坐标轴字体颜色
	 * </p>
	 */
	private int crossLinesFontColor = DEFAULT_CROSS_LINES_FONT_COLOR;

	/**
	 * <p>
	 * Titles Array for display of X axis
	 * </p>
	 * <p>
	 * X軸の表示用タイトル配列
	 * </p>
	 * <p>
	 * X轴标题数组
	 * </p>
	 */
	protected List<String> longitudeTitles;
	protected List<String> customLongitudeTitles;

	/**
	 * <p>
	 * Titles for display of Y axis
	 * </p>
	 * <p>
	 * Y軸の表示用タイトル配列
	 * </p>
	 * <p>
	 * Y轴标题数组
	 * </p>
	 */
	private List<String> latitudeTitles;
	/**
	 * 涨幅数组
	 */
	private List<String> increasePercent;

	/**
	 * <p>
	 * Titles' max length for display of Y axis
	 * </p>
	 * <p>
	 * Y軸の表示用タイトルの最大文字長さ
	 * </p>
	 * <p>
	 * Y轴标题最大文字长度
	 * </p>
	 */
	private int latitudeMaxTitleLength = DEFAULT_LATITUDE_MAX_TITLE_LENGTH;

	/**
	 * <p>
	 * Should display the Y cross line if grid is touched?
	 * </p>
	 * <p>
	 * タッチしたポイントがある場合、十字線の垂直線を表示するか?
	 * </p>
	 * <p>
	 * 在控件被点击时，显示十字竖线线
	 * </p>
	 */
	private boolean displayCrossXOnTouch = DEFAULT_DISPLAY_CROSS_X_ON_TOUCH;

	/**
	 * <p>
	 * Should display the Y cross line if grid is touched?
	 * </p>
	 * <p>
	 * タッチしたポイントがある場合、十字線の水平線を表示するか?
	 * </p>
	 * <p>
	 * 在控件被点击时，显示十字横线线
	 * </p>
	 */
	private boolean displayCrossYOnTouch = DEFAULT_DISPLAY_CROSS_Y_ON_TOUCH;

	/**
	 * <p>
	 * Touched point inside of grid
	 * </p>
	 * <p>
	 * タッチしたポイント
	 * </p>
	 * <p>
	 * 单点触控的选中点
	 * </p>
	 */
	private PointF touchPoint;

	/**
	 * <p>
	 * Touched point’s X value inside of grid
	 * </p>
	 * <p>
	 * タッチしたポイントのX
	 * </p>
	 * <p>
	 * 单点触控的选中点的X
	 * </p>
	 */
	private float clickPostX = -1;

	/**
	 * <p>
	 * Touched point’s Y value inside of grid
	 * </p>
	 * <p>
	 * タッチしたポイントのY
	 * </p>
	 * <p>
	 * 单点触控的选中点的Y
	 * </p>
	 */
	private float clickPostY = -1;

	/**
	 * <p>
	 * Event will notify objects' list
	 * </p>
	 * <p>
	 * イベント通知対象リスト
	 * </p>
	 * <p>
	 * 事件通知对象列表
	 * </p>
	 */
	private List<ITouchEventResponse> notifyList;

	protected Paint paintAxis = new Paint();
	protected Paint paintBorder = new Paint();
	protected Paint paintLLLine = new Paint();
	protected Paint paintFont = new Paint();
	protected Paint paintFontCenter = new Paint();
	protected Paint paintLine = new Paint();
	protected Paint mPaintAlpha = new Paint();

	private void init(Context context) {
		paintBorder.setAntiAlias(true);
		paintBorder.setStyle(Style.STROKE);

		paintLLLine.setStyle(Style.STROKE);

        paintFont.setTextSize(ChartConfig.NINE_TEXT_SIZE);
        paintFont.setAntiAlias(true);

		paintFontCenter.setTextSize(ChartConfig.NINE_TEXT_SIZE);
		paintFontCenter.setAntiAlias(true);
		paintFontCenter.setTextAlign(Paint.Align.CENTER);

		paintLine.setAntiAlias(true);
		paintLine.setStrokeWidth(ChartConfig.LINE_WIDTH);
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @param context
	 * 
	 * @see BaseChart#BaseChart(Context)
	 */
	public GridChart(Context context) {
		super(context);
		init(context);
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
	 * @see BaseChart#BaseChart(Context,
	 * AttributeSet, int)
	 */
	public GridChart(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @param context
	 * 
	 * @param attrs
	 * 
	 * @see BaseChart#BaseChart(Context,
	 * AttributeSet)
	 */
	public GridChart(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
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
	protected void  onDraw(Canvas canvas) {
		super.onDraw(canvas);

		drawXAxis(canvas);
		drawYAxis(canvas);

		if (this.displayBorder) {
			drawBorder(canvas);
		}
		//画经线
//		if (displayLongitude || displayLongitudeTitle) {//原版
		if(displayLongitude){
			drawLongitudeLine(canvas);
		}
		if (displayLongitudeTitle) {
			drawLongitudeTitle(canvas);
		}
		//画纬线
//		if (displayLatitude || displayLatitudeTitle) {
		if(displayLatitude){
			drawLatitudeLine(canvas);
		}
		if (displayLatitudeTitle) {
			drawLatitudeTitle(canvas);
		}

		if (displayCrossXOnTouch || displayCrossYOnTouch) {
//			drawWithFingerClick(canvas);
			if(displayVerticalLine){
				drawVerticalLine(canvas);//绘制十字线
			}
			if(displayHorizontalLine){
				drawHorizontalLine(canvas);
			}

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * <p>Called when chart is touched<p> <p>チャートをタッチしたら、メソッドを呼ぶ<p>
	 * <p>图表点击时调用<p>
	 * 
	 * @param event
	 * 
	 * @see android.view.View#onTouchEvent(MotionEvent)
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// do notify
		notifyEventAll(event);

		return touchEvent(event) || super.onTouchEvent(event);

	}

	protected boolean touchEvent(MotionEvent event) {

		if (getDataSize() <= 0) {
			return false;
		}

		if (event.getX() < getDataQuadrantPaddingStartX()
				|| event.getX() > getDataQuadrantPaddingEndX()) {
			return false;
		}
		//修改版	为了两图联动暂时去掉   暂不限定y轴响应触摸范围
		/*if (event.getY() < getDataQuadrantPaddingStartY()
				|| event.getY() > getDataQuadrantPaddingEndY()) {
			return false;
		}*/

		final float MIN_LENGTH = (super.getWidth() / 40) < 5 ? 5 : (super.getWidth() / 50);

		switch (event.getAction() & MotionEvent.ACTION_MASK) {
			// 设置拖拉模式
			case MotionEvent.ACTION_DOWN:
				touchMode = DOWN;
				if (event.getPointerCount() == 1) {
					startPoint = new PointF(event.getX(), event.getY());
				}
				postDelayed(checkForLongPress, LONG_PRESS_TIME);
				break;

			case MotionEvent.ACTION_UP:
				if(enableRefresh){
					if(displayFrom < 0){
						if(displayFrom > -REFRESH_SIZE){
							setRefreshState(false);

						}else if(displayFrom == -REFRESH_SIZE){
							setRefreshState(true);

							if (onRefreshListener != null) {
								onRefreshListener.start();
							}
						}
					}
				}

				if(!isLongPress && Math.abs(startPoint.x - event.getX()) < 15){
					performClick();
				}

				clickPostX = -1;
				clickPostY = -1;
				removeLongPressCheck();
				touchMode = NONE;

				startPointA = null;
				startPointB = null;

				postInvalidate();
				return super.onTouchEvent(event);

			case MotionEvent.ACTION_CANCEL:
				clickPostX = -1;
				clickPostY = -1;
				removeLongPressCheck();
				postInvalidate();
				break;

			case MotionEvent.ACTION_POINTER_UP:
				touchMode = NONE;
				startPointA = null;
				startPointB = null;
				return super.onTouchEvent(event);
			// 设置多点触摸模式
			case MotionEvent.ACTION_POINTER_DOWN:
				olddistance = calcDistance(event);
				if (olddistance > MIN_LENGTH) {
					touchMode = ZOOM;
					startPointA = new PointF(event.getX(0), event.getY(0));
					startPointB = new PointF(event.getX(1), event.getY(1));
				}
				break;

			case MotionEvent.ACTION_MOVE:

				if (!isLongPress) {
					if(startPoint != null){
						if (Math.abs(startPoint.x - event.getX()) > MIN_LENGTH || Math.abs(startPoint.y - event.getY()) > MIN_LENGTH) {
							removeLongPressCheck();
						}
					}

					if(touchMode == ZOOM){

						//放大缩小
						if(enableZoom){
							newdistance = calcDistance(event);
							if (Math.abs(newdistance - olddistance) > 10) {

								if (newdistance > olddistance) {
									zoomIn();
								} else {
									zoomOut();
								}
								// 重置距离
								olddistance = newdistance;
							}
							startPointA = new PointF(event.getX(0), event.getY(0));
							if(event.getPointerCount() > 1){
								startPointB = new PointF(event.getX(1), event.getY(1));
							}

							super.postInvalidate();
//                            super.notifyEventAll(this);
						}
					}else{

						// 单点drag
						if(enableDrag && startPoint != null && Math.abs(startPoint.x - event.getX()) > 10){

							int drag_unit = getDragUnit();
							if (startPoint.x >= event.getX()) {
								if (displayFrom + displayNumber + drag_unit < getDataSize()) {
									displayFrom = displayFrom + drag_unit;
								}else{
									displayFrom = getDataSize() - displayNumber;
									if(displayFrom < 0){
										displayFrom = 0;
									}
								}
							} else {
								if (displayFrom > drag_unit) {
									displayFrom = displayFrom - drag_unit;
								}else{
									if(enableRefresh){
										displayFrom = displayFrom - drag_unit;
										if(Math.abs(displayFrom) > REFRESH_SIZE){
											displayFrom = -REFRESH_SIZE;
										}
									}else{
										displayFrom = 0;
									}
								}
							}
						}

						startPoint = new PointF(event.getX(), event.getY());

						postInvalidate();
//                        super.notifyEventAll(this);
					}




				} else {

					getParent().requestDisallowInterceptTouchEvent(true);
					if (clickPostX == -1 || Math.abs(event.getX() - startPoint.x) > 1 || Math.abs(event.getY() - startPoint.y) > 1) {
						clickPostX = event.getX();
						clickPostY = event.getY();
						startPoint = new PointF(event.getX(), event.getY());
						postInvalidate();
					}
				}

				break;
		}
		touchPoint = new PointF(clickPostX, clickPostY);


		return true;
	}

	/**
	 * <p>
	 * draw some text with border
	 * </p>
	 * <p>
	 * 文字を書く、枠あり
	 * </p>
	 * <p>
	 * 绘制一段文本，并增加外框
	 * </p>
	 * 
	 * @param ptStart
	 *            <p>
	 *            start point
	 *            </p>
	 *            <p>
	 *            開始ポイント
	 *            </p>
	 *            <p>
	 *            开始点
	 *            </p>
	 * 
	 * @param ptEnd
	 *            <p>
	 *            end point
	 *            </p>
	 *            <p>
	 *            結束ポイント
	 *            </p>
	 *            <p>
	 *            结束点
	 *            </p>
	 * 
	 * @param content
	 *            <p>
	 *            text content
	 *            </p>
	 *            <p>
	 *            文字内容
	 *            </p>
	 *            <p>
	 *            文字内容
	 *            </p>
	 * 
	 * @param fontSize
	 *            <p>
	 *            font size
	 *            </p>
	 *            <p>
	 *            文字フォントサイズ
	 *            </p>
	 *            <p>
	 *            字体大小
	 *            </p>
	 * 
	 * @param canvas
	 */
	private void drawAlphaTextBox(PointF ptStart, PointF ptEnd, String content,
			int fontSize, Canvas canvas) {

		paintFont.setTextSize(fontSize);

        // draw a rectangle
		paintFont.setColor(Color.rgb(241, 249, 254));
		canvas.drawRect(ptStart.x, ptStart.y, ptEnd.x, ptEnd.y, paintFont);

		paintFont.setColor(crossLinesColor);
		// draw a rectangle' border
		canvas.drawLine(ptStart.x, ptStart.y, ptStart.x, ptEnd.y, paintFont);
		canvas.drawLine(ptStart.x, ptEnd.y, ptEnd.x, ptEnd.y, paintFont);
		canvas.drawLine(ptEnd.x, ptEnd.y, ptEnd.x, ptStart.y, paintFont);
		canvas.drawLine(ptEnd.x, ptStart.y, ptStart.x, ptStart.y, paintFont);

		paintFont.setColor(crossLinesFontColor);
		// draw text
		canvas.drawText(content, ptStart.x + 2, ptStart.y + fontSize, paintFont);
	}

	protected float getDataQuadrantWidth() {
		if(axisYPosition == AXIS_Y_POSITION_LEFT_INSIDE){
			return super.getWidth() - 2 * borderWidth
					- axisWidth;
		}else{
			return super.getWidth() - axisYTitleQuadrantWidth - 2 * borderWidth
					- axisWidth;
		}
	}

	protected float getDataQuadrantHeight() {
		return super.getHeight() - axisXTitleQuadrantHeight - 2 * borderWidth
				- axisWidth;
	}

	protected float getDataQuadrantStartX() {
		if (axisYPosition == AXIS_Y_POSITION_LEFT) {
			return borderWidth + axisYTitleQuadrantWidth + axisWidth;
		} else {
			return borderWidth;
		}
	}

	protected float getDataQuadrantPaddingStartX() {
		return getDataQuadrantStartX() + dataQuadrantPaddingLeft;
	}

	protected float getDataQuadrantEndX() {
		if (axisYPosition == AXIS_Y_POSITION_RIGHT) {
			return super.getWidth() - borderWidth - axisYTitleQuadrantWidth
					- axisWidth;
		} else {
			return super.getWidth() - borderWidth;
		}
	}

	protected float getDataQuadrantPaddingEndX() {
		return getDataQuadrantEndX() - dataQuadrantPaddingRight;
	}

	protected float getDataQuadrantStartY() {
		return borderWidth;
	}

	protected float getDataQuadrantPaddingStartY() {
		return getDataQuadrantStartY() + dataQuadrantPaddingTop;
	}

	protected float getDataQuadrantEndY() {
		return super.getHeight() - borderWidth - axisXTitleQuadrantHeight
				- axisWidth;
	}

	protected float getDataQuadrantPaddingEndY() {
		return getDataQuadrantEndY() - dataQuadrantPaddingBottom;
	}

	protected float getDataQuadrantPaddingWidth() {
		return getDataQuadrantWidth() - dataQuadrantPaddingLeft
				- dataQuadrantPaddingRight;
	}

	protected float getDataQuadrantPaddingHeight() {
		return getDataQuadrantHeight() - dataQuadrantPaddingTop
				- dataQuadrantPaddingBottom;
	}

	/**
	 * <p>
	 * calculate degree title on X axis
	 * </p>
	 * <p>
	 * X軸の目盛を計算する
	 * </p>
	 * <p>
	 * 计算X轴上显示的坐标值 百分比     //不调用则无法获取点击点数据的位置
	 * </p>
	 * 
	 * @param value
	 *            <p>
	 *            value for calculate
	 *            </p>
	 *            <p>
	 *            計算有用データ
	 *            </p>
	 *            <p>
	 *            计算用数据
	 *            </p>
	 * 
	 * @return String
	 *         <p>
	 *         degree
	 *         </p>
	 *         <p>
	 *         目盛
	 *         </p>
	 *         <p>
	 *         坐标值
	 *         </p>
	 */
	public String getAxisXGraduate(Object value) {
		float valueLength = ((Float) value).floatValue()
				- getDataQuadrantPaddingStartX();
		return String.valueOf(valueLength / this.getDataQuadrantPaddingWidth());
	}

	/**
	 * <p>
	 * calculate degree title on Y axis
	 * </p>
	 * <p>
	 * Y軸の目盛を計算する
	 * </p>
	 * <p>
	 * 计算Y轴上显示的坐标值    
	 * </p>
	 * 
	 * @param value
	 *            <p>
	 *            value for calculate
	 *            </p>
	 *            <p>
	 *            計算有用データ
	 *            </p>
	 *            <p>
	 *            计算用数据
	 *            </p>
	 * 
	 * @return String
	 *         <p>
	 *         degree
	 *         </p>
	 *         <p>
	 *         目盛
	 *         </p>
	 *         <p>
	 *         坐标值
	 *         </p>
	 */
	public String getAxisYGraduate(Object value) {
		float valueLength = ((Float) value).floatValue()
				- getDataQuadrantPaddingStartY();
		return String
				.valueOf(1 - valueLength / this.getDataQuadrantPaddingHeight());//修改版
//		return String
//				.valueOf(valueLength / this.getDataQuadrantPaddingHeight());
	}

	/**
	 * <p>
	 * draw cross line ,called when graph is touched
	 * </p>
	 * <p>
	 * 十字線を書く、グラプをタッチたら、メソードを呼び
	 * </p>
	 * <p>
	 * 在图表被点击后,绘制十字线的垂直线及x轴上的数字
	 * </p>
	 * 
	 * @param canvas
	 */
	protected void drawVerticalLine(Canvas canvas) {

		if (!displayLongitude) {
			return;
		}
		if (!displayCrossXOnTouch) {
			return;
		}
		if (clickPostX <= 0) {
			return;
		}

		paintLine.setColor(crossLinesColor);
		paintLine.setTextSize(longitudeFontSize);

		float lineVLength = getDataQuadrantHeight() + axisWidth;

		//修正画线的位置
		float dataMidX = getDataMidX(clickPostX);
		if(dataMidX == -1){
			dataMidX = clickPostX;
		}

		// calculate points to draw
		// 修正所画矩形的大小
        String content = getFormatValue(getAxisXGraduate(dataMidX), xFormatter);
        float textLenth = paintLine.measureText(content);
        float newX1 = dataMidX - textLenth / 2 - 2;
        float newX2 = dataMidX + textLenth / 2 + 2;
        if(newX2 > getWidth()){
            newX1 = getWidth() - textLenth - 4;
            newX2 = getWidth() - 2;
        }
        if(newX1 < 0){
            newX1 = 0f;
            newX2 = textLenth + 4;
        }

		PointF boxVS = new PointF(newX1, borderWidth + lineVLength);
		PointF boxVE = new PointF(newX2, borderWidth + lineVLength + axisXTitleQuadrantHeight);
//        PointF boxVS = new PointF(clickPostX - longitudeFontSize * 5f / 2f,
//				borderWidth + lineVLength);
//		PointF boxVE = new PointF(clickPostX + longitudeFontSize * 5f / 2f,
//				borderWidth + lineVLength + axisXTitleQuadrantHeight);

        if(axisXTitleQuadrantHeight != 0){
            // draw text 和边框
            drawAlphaTextBox(boxVS, boxVE, content,longitudeFontSize, canvas);
        }

		canvas.drawLine(dataMidX, borderWidth, dataMidX, lineVLength, paintLine);
	}

    /**
     * 得到点击点在数据中的位置
     * @param x 触点的x坐标
     * @return
     */
    protected int getDataIndexOf(float x){
        return -1;
    }

    /**
     * 得到该数据的中线x值
     * @param x 触点的x坐标
     * @return
     */
    protected float getDataMidX(float x){

        return -1f;
    }
	/**
	 * 在图表被点击后绘制十字线的水平线及y轴上的数字
	 * @param canvas
	 */
	protected void drawHorizontalLine(Canvas canvas) {

		if (!displayLatitude) {//修改版            原版displayLatitudeTitle
			return;
		}
		if (!displayCrossYOnTouch) {
			return;
		}
		if (clickPostY <= 0) {
			return;
		}

		paintLine.setColor(crossLinesColor);

		float lineHLength = getDataQuadrantWidth() + axisWidth;

        float dataMidY = getDataMidY(clickPostX);
        float drayY;
        if(dataMidY != -1){
            drayY = dataMidY;
        }else{
            drayY = clickPostY;
        }

		String axisYGraduate = getFormatValue(getAxisYGraduate(drayY), yFormatter);
        paintFont.setTextSize(latitudeFontSize);
		float textWidth = paintFont.measureText(axisYGraduate) + 2;

		PointF boxHS;
		PointF boxHE;
		switch (axisYPosition){
			case AXIS_Y_POSITION_LEFT:
				boxHS = new PointF(borderWidth, drayY
						- latitudeFontSize / 2f - 2);
				boxHE = new PointF(borderWidth + textWidth,
						drayY + latitudeFontSize / 2f + 2);

				// draw text
				drawAlphaTextBox(boxHS, boxHE, axisYGraduate,
						latitudeFontSize, canvas);

				canvas.drawLine(borderWidth + textWidth, drayY,
						borderWidth + textWidth + lineHLength,
						drayY, paintLine);
				break;

			case AXIS_Y_POSITION_LEFT_INSIDE:
				boxHS = new PointF(borderWidth, drayY - latitudeFontSize / 2f - 2);
				boxHE = new PointF( borderWidth + textWidth, drayY + latitudeFontSize / 2f + 2);

				// draw text
				drawAlphaTextBox(boxHS, boxHE, axisYGraduate, latitudeFontSize, canvas);


				canvas.drawLine(borderWidth + textWidth, drayY, borderWidth + lineHLength,
						drayY, paintLine);
				break;

			default:
				boxHS = new PointF(super.getWidth() - borderWidth
						- textWidth, drayY - latitudeFontSize
						/ 2f - 2);
				boxHE = new PointF(super.getWidth() - borderWidth,
						drayY + latitudeFontSize / 2f + 2);

				// draw text
				drawAlphaTextBox(boxHS, boxHE, axisYGraduate,
						latitudeFontSize, canvas);


				canvas.drawLine(borderWidth, drayY, borderWidth + lineHLength,
						drayY, paintLine);
				break;
		}

	}
    protected float getDataMidY(float x){

        return -1f;
    }

	/**
	 * <p>
	 * draw border
	 * </p>
	 * <p>
	 * グラプのボーダーを書く
	 * </p>
	 * <p>
	 * 绘制边框
	 * </p>
	 * 
	 * @param canvas
	 */
	protected void drawBorder(Canvas canvas) {

		paintBorder.setColor(borderColor);
		paintBorder.setStrokeWidth(borderWidth);
		// draw a rectangle
		canvas.drawRect(borderWidth / 2, borderWidth / 2, super.getWidth()
				- borderWidth / 2, super.getHeight() - borderWidth / 2, paintBorder);
	}

	/**
	 * <p>
	 * draw X Axis
	 * </p>
	 * <p>
	 * X軸を書く
	 * </p>
	 * <p>
	 * 绘制X轴
	 * </p>
	 * 
	 * @param canvas
	 */
	protected void drawXAxis(Canvas canvas) {

		float length = super.getWidth();
		float postY;
		if (axisXPosition == AXIS_X_POSITION_BOTTOM) {
			postY = super.getHeight() - axisXTitleQuadrantHeight - borderWidth
					- axisWidth / 2;
		} else {
			postY = super.getHeight() - borderWidth - axisWidth / 2;
		}

		paintAxis.setColor(axisXColor);
		paintAxis.setStrokeWidth(axisWidth);

		canvas.drawLine(borderWidth, postY, length, postY, paintAxis);

	}

	/**
	 * <p>
	 * draw Y Axis
	 * </p>
	 * <p>
	 * Y軸を書く
	 * </p>
	 * <p>
	 * 绘制Y轴
	 * </p>
	 * 
	 * @param canvas
	 */
	protected void drawYAxis(Canvas canvas) {

		float length = super.getHeight() - axisXTitleQuadrantHeight
				- borderWidth;
		float postX;
		if (axisYPosition == AXIS_Y_POSITION_LEFT) {
			postX = borderWidth + axisYTitleQuadrantWidth + axisWidth / 2;
		} else if(axisYPosition == AXIS_Y_POSITION_RIGHT){
			postX = super.getWidth() - borderWidth - axisYTitleQuadrantWidth
					- axisWidth / 2;
		}else {
			postX = borderWidth + axisWidth / 2;
		}

		paintAxis.setColor(axisXColor);
		paintAxis.setStrokeWidth(axisWidth);

		canvas.drawLine(postX, borderWidth, postX, length, paintAxis);
	}

	/**
	 * <p>
	 * draw longitude lines
	 * </p>
	 * <p>
	 * 経線を書く
	 * </p>
	 * <p>
	 * 绘制经线
	 * </p>
	 * 
	 * @param canvas
	 */
	protected void drawLongitudeLine(Canvas canvas) {
		if (null == longitudeTitles) {
			return;
		}
		if (!displayLongitude) {
			return;
		}
//		int counts = longitudeTitles.size();
		int counts = longitudeNum;
		float length = getDataQuadrantHeight();


		paintLLLine.setColor(longitudeColor);
		if (dashLongitude) {
			paintLLLine.setPathEffect(dashEffect);
		}
		if (counts > 1) {
			float postOffset = this.getDataQuadrantPaddingWidth() / (counts);

			float offset;
			if (axisYPosition == AXIS_Y_POSITION_LEFT) {
				offset = borderWidth + axisYTitleQuadrantWidth + axisWidth
						+ dataQuadrantPaddingLeft;
			} else if(axisYPosition == AXIS_Y_POSITION_RIGHT){
				offset = borderWidth + dataQuadrantPaddingLeft;
			}else{
				offset = borderWidth + dataQuadrantPaddingLeft + axisWidth;
			}

            Path path = new Path();
            for (int i = 0; i < counts; i++) {
//                canvas.drawLine(offset + i * postOffset, borderWidth, offset
//						+ i * postOffset, length, mPaintLine);
                path.moveTo(offset + i * postOffset, borderWidth);
                path.lineTo(offset+ i * postOffset, length);
            }
			canvas.drawPath(path, paintLLLine);
		}
	}

	/**
	 * <p>
	 * draw longitude lines
	 * </p>
	 * <p>
	 * 経線を書く
	 * </p>
	 * <p>
	 * 绘制经线title	即X轴上显示的title
	 * </p>
	 * 
	 * @param canvas
	 */
	protected void drawLongitudeTitle(Canvas canvas) {

		if (null == longitudeTitles) {
			return;
		}
		/*if (!displayLongitude) {
			return;
		}*/
		if (!displayLongitudeTitle) {
			return;
		}

		if (longitudeTitles.size() <= 1) {
			return;
		}


		paintFont.setColor(longitudeFontColor);
		paintFont.setTextSize(longitudeFontSize);

		float postOffset = this.getDataQuadrantPaddingWidth() / (longitudeNum);

		String str;
		float measureText;
		float offset;
		if (axisYPosition == AXIS_Y_POSITION_LEFT) {
			offset = borderWidth + axisYTitleQuadrantWidth + axisWidth
					+ dataQuadrantPaddingLeft;
		}else if(axisYPosition == AXIS_Y_POSITION_RIGHT){
			offset = borderWidth + dataQuadrantPaddingLeft;
		}else{
			offset = borderWidth + dataQuadrantPaddingLeft + axisWidth;
		}
		for (int i = 0; i < longitudeTitles.size(); i++) {
			if(customLongitudeTitles != null){
				str = longitudeTitles.get(i);
			}else{
				str = getFormatValue(longitudeTitles.get(i), xFormatter);
			}
			measureText = paintFont.measureText(str);
			if (i == 0) {
				canvas.drawText(str, offset + 2f,
						super.getHeight() - axisXTitleQuadrantHeight
								+ longitudeFontSize, paintFont);
			}
			else if(i == longitudeTitles.size() - 1){
				canvas.drawText(str, offset + i* postOffset - measureText,
						super.getHeight() - axisXTitleQuadrantHeight
								+ longitudeFontSize, paintFont);
			}else {
				if(tilteMode == TITLE_ALL){
					canvas.drawText(str, offset + i
									* postOffset - measureText/2f, super.getHeight()
									- axisXTitleQuadrantHeight + longitudeFontSize,
							paintFont);
				}
			}
		}
	}

	/**
	 * <p>
	 * draw latitude lines
	 * </p>
	 * <p>
	 * 緯線を書く
	 * </p>
	 * <p>
	 * 绘制纬线
	 * </p>
	 * 
	 * @param canvas
	 */
	protected void drawLatitudeLine(Canvas canvas) {

		if (null == latitudeTitles) {
			return;
		}
//		if (!displayLatitude) {
//			return;
//		}
		if (!displayLatitudeTitle) {
			return;
		}
		if (latitudeTitles.size() <= 1) {
			return;
		}

		float length = getDataQuadrantWidth();

		paintLLLine.setColor(latitudeColor);
		if (dashLatitude) {
			paintLLLine.setPathEffect(dashEffect);
		}

		float postOffset = this.getDataQuadrantPaddingHeight()
				/ (latitudeTitles.size() - 1);

		float offset = super.getHeight() - borderWidth
				- axisXTitleQuadrantHeight - axisWidth
				- dataQuadrantPaddingBottom;

        Path path = new Path();
		float startFrom;
        if (axisYPosition == AXIS_Y_POSITION_LEFT) {
			startFrom = borderWidth + axisYTitleQuadrantWidth + axisWidth;
		} else if(axisYPosition == AXIS_Y_POSITION_RIGHT) {
			startFrom = borderWidth;
		}else{
			startFrom = borderWidth + axisWidth;
		}
		int size = latitudeTitles.size();
		for (int i = 0; i < size; i++) {
			if(i != size / 2){
				path.moveTo(startFrom, offset - i * postOffset);
				path.lineTo(startFrom + length, offset - i * postOffset);
			}
		}
		canvas.drawPath(path, paintLLLine);

        path.reset();
		paintLLLine.setColor(borderColor);
		int i = size / 2;
        path.moveTo(startFrom, offset - i * postOffset);
        path.lineTo(startFrom + length, offset - i * postOffset);
        canvas.drawPath(path, paintLLLine);


    }

	/**
	 * <p>
	 * draw latitude lines
	 * </p>
	 * <p>
	 * 緯線を書く
	 * </p>
	 * <p>
	 * 绘制纬线title	即Y轴上显示的title
	 * </p>
	 * 
	 * @param canvas
	 */
	protected void drawLatitudeTitle(Canvas canvas) {
		
		if (null == latitudeTitles) {
			return;
		}
		if (!displayLatitudeTitle) {
			return;
		}
		if (latitudeTitles.size() <= 1) {
			return;
		}

		paintFont.setColor(latitudeFontColor);
		paintFont.setTextSize(latitudeFontSize);

		float postOffset = this.getDataQuadrantPaddingHeight()
				/ (latitudeTitles.size() - 1);

		float offset = super.getHeight() - borderWidth- axisXTitleQuadrantHeight - axisWidth- dataQuadrantPaddingBottom;

		String text;
		if (axisYPosition == AXIS_Y_POSITION_LEFT) {
			float startFrom = borderWidth + 2;
			int size = latitudeTitles.size();
			String s = "";
			float measureText = 0;
			for (int i = 0; i < size; i++) {
				if(increasePercent !=null && increasePercent.size()>0){
					s = getFormatValue(increasePercent.get(i), yFormatter);
					measureText = paintFont.measureText(s + "%");
				}
				text = getFormatValue(latitudeTitles.get(i), yFormatter);
				if (text.length() < getLatitudeMaxTitleLength()) {
					while (text.length() < getLatitudeMaxTitleLength()) {
						text = " " + text;
					}
            	}
				if (0 == i) {
					canvas.drawText(text, startFrom,
							super.getHeight() - this.axisXTitleQuadrantHeight
									- borderWidth - axisWidth - 3, paintFont);
					//画涨幅
					if(measureText !=0){
						canvas.drawText(s+"%", super.getWidth()-measureText -dataQuadrantPaddingLeft-borderWidth,
								super.getHeight() - this.axisXTitleQuadrantHeight
										- borderWidth - axisWidth - 3, paintFont);
					}
				}else if(i == size - 1){
					canvas.drawText(text, startFrom, offset
									- i * postOffset + latitudeFontSize / 2f + 3,
							paintFont);
					if(measureText != 0){
						canvas.drawText(s+"%", super.getWidth()-measureText-dataQuadrantPaddingLeft-borderWidth, offset
										- i * postOffset + latitudeFontSize / 2f + 3,
								paintFont);
					}
				}else {
					if(tilteMode == TITLE_ALL){
						canvas.drawText(text, startFrom, offset
										- i * postOffset + latitudeFontSize / 2f,
								paintFont);
						if(measureText != 0){
							canvas.drawText(s+"%", super.getWidth()-measureText-dataQuadrantPaddingLeft-borderWidth, offset
											- i * postOffset + latitudeFontSize / 2f,
									paintFont);
						}
					}
				}

			}
		} else if(axisYPosition == AXIS_Y_POSITION_RIGHT){
			float startFrom = super.getWidth() - borderWidth
					- axisYTitleQuadrantWidth;
			for (int i = 0; i < latitudeTitles.size(); i++) {
				text = getFormatValue(latitudeTitles.get(i), yFormatter);
				if (text.length() < getLatitudeMaxTitleLength()) {
					while (text.length() < getLatitudeMaxTitleLength()) {
						text = " " + text;
					}
				}
				if (0 == i) {
					canvas.drawText(text, startFrom,
							super.getHeight() - this.axisXTitleQuadrantHeight
									- borderWidth - axisWidth - 2f, paintFont);
				} else {
					if(tilteMode == TITLE_ALL){
						canvas.drawText(text, startFrom, offset
										- i * postOffset + latitudeFontSize / 2f,
								paintFont);
					}
				}
			}
		}else{
			float startFrom = borderWidth + 2;
			int size = latitudeTitles.size();
			String s = "";
			float measureText = 0;
			for (int i = 0; i < size; i++) {
				if(increasePercent !=null && increasePercent.size() > 0){
					s = getFormatValue(increasePercent.get(i), yFormatter);
					measureText = paintFont.measureText(s + "%");
				}
				text = getFormatValue(latitudeTitles.get(i), yFormatter);
				if (text.length() < getLatitudeMaxTitleLength()) {
					while (text.length() < getLatitudeMaxTitleLength()) {
						text = " " + text;
					}
				}
				if (0 == i) {
					canvas.drawText(text, startFrom,
							super.getHeight() - this.axisXTitleQuadrantHeight
									- borderWidth - axisWidth - 3, paintFont);
					//画涨幅
					if(measureText !=0){
						canvas.drawText(s+"%", super.getWidth()-measureText -dataQuadrantPaddingLeft-borderWidth,
								super.getHeight() - this.axisXTitleQuadrantHeight
										- borderWidth - axisWidth - 3, paintFont);
					}
				}else if(i == size - 1){
					canvas.drawText(text, startFrom, offset
									- i * postOffset + latitudeFontSize / 2f + 3,
							paintFont);
					if(measureText != 0){
						canvas.drawText(s+"%", super.getWidth()-measureText-dataQuadrantPaddingLeft-borderWidth, offset
										- i * postOffset + latitudeFontSize / 2f + 3,
								paintFont);
					}
				}else {

					if(tilteMode == TITLE_ALL || size / 2 == i){
						canvas.drawText(text, startFrom, offset
										- i * postOffset + latitudeFontSize / 2f,
								paintFont);
						if(measureText != 0){
							canvas.drawText(s+"%", super.getWidth()-measureText-dataQuadrantPaddingLeft-borderWidth, offset
											- i * postOffset + latitudeFontSize / 2f,
									paintFont);
						}
					}
				}

			}
		}

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
	protected void zoomIn() {
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
			int size = getDataSize();
			if (displayFrom + displayNumber >= size) {
				displayFrom = size - displayNumber;
			}
		}

	}

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
	protected void zoomOut() {
		if(displayNumber < maxDisplayNumber){

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
			if (displayNumber >= maxDisplayNumber) {
				displayNumber = maxDisplayNumber;
			}
			int size = getDataSize();
			if(displayNumber > size){
				displayNumber = size;
				displayFrom = 0;
			}

			// 处理displayFrom越界
			if (displayFrom + displayNumber >= size) {
				displayFrom = size - displayNumber;
			}

			if (displayFrom < 0) {
				displayFrom = 0;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @param event
	 * 
	 * @see
	 * ITouchEventResponse#notifyEvent(GridChart)
	 */
	@Override
	public void notifyEvent(MotionEvent motionEvent) {
		/*PointF point = chart.getTouchPoint();
		if (null != point) {
			clickPostX = point.x;
			clickPostY = point.y;
		}
		touchPoint = new PointF(clickPostX, clickPostY);
		super.invalidate();*/

		touchEvent(motionEvent);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @param event
	 * 
	 * @see
	 * ITouchEventNotify#addNotify(ITouchEventResponse
	 * )
	 */
	public void addNotify(ITouchEventResponse notify) {
		if (null == notifyList) {
			notifyList = new ArrayList<ITouchEventResponse>();
		}
		notifyList.add(notify);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @param event
	 * 
	 * @see ITouchEventNotify#removeNotify(int)
	 */
	public void removeNotify(int index) {
		if (null != notifyList && notifyList.size() > index) {
			notifyList.remove(index);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @param event
	 * 
	 * @see ITouchEventNotify#removeNotify()
	 */
	public void removeAllNotify() {
		if (null != notifyList) {
			notifyList.clear();
		}
	}

	public void notifyEventAll(MotionEvent event) {
		if (null != notifyList) {
			for (int i = 0; i < notifyList.size(); i++) {
				ITouchEventResponse ichart = notifyList.get(i);
				ichart.notifyEvent(event);
			}
		}
	}

	/**
	 * @return the axisXColor
	 */
	public int getAxisXColor() {
		return axisXColor;
	}

	/**
	 * @param axisXColor
	 *            the axisXColor to set
	 */
	public void setAxisXColor(int axisXColor) {
		this.axisXColor = axisXColor;
	}

	/**
	 * @return the axisYColor
	 */
	public int getAxisYColor() {
		return axisYColor;
	}

	/**
	 * @param axisYColor
	 *            the axisYColor to set
	 */
	public void setAxisYColor(int axisYColor) {
		this.axisYColor = axisYColor;
	}

	/**
	 * @return the axisWidth
	 */
	public float getAxisWidth() {
		return axisWidth;
	}

	/**
	 * @param axisWidth
	 *            the axisWidth to set
	 */
	public void setAxisWidth(float axisWidth) {
		this.axisWidth = axisWidth;
	}

	/**
	 * @return the longitudeColor
	 */
	public int getLongitudeColor() {
		return longitudeColor;
	}

	/**
	 * @param longitudeColor
	 *            the longitudeColor to set
	 */
	public void setLongitudeColor(int longitudeColor) {
		this.longitudeColor = longitudeColor;
	}

	/**
	 * @return the latitudeColor
	 */
	public int getLatitudeColor() {
		return latitudeColor;
	}

	/**
	 * @param latitudeColor
	 *            the latitudeColor to set
	 */
	public void setLatitudeColor(int latitudeColor) {
		this.latitudeColor = latitudeColor;
	}

	/**
	 * @return the axisMarginLeft
	 */
	@Deprecated
	public float getAxisMarginLeft() {
		return axisYTitleQuadrantWidth;
	}

	/**
	 * @param axisMarginLeft
	 *            the axisMarginLeft to set
	 */
	@Deprecated
	public void setAxisMarginLeft(float axisMarginLeft) {
		this.axisYTitleQuadrantWidth = axisMarginLeft;
	}

	/**
	 * @return the axisMarginLeft
	 */
	public float getAxisYTitleQuadrantWidth() {
		return axisYTitleQuadrantWidth;
	}

	/**
	 * @param axisYTitleQuadrantWidth
	 *            the axisYTitleQuadrantWidth to set
	 */
	public void setAxisYTitleQuadrantWidth(float axisYTitleQuadrantWidth) {
		this.axisYTitleQuadrantWidth = axisYTitleQuadrantWidth;
	}

	/**
	 * @return the axisXTitleQuadrantHeight
	 */
	@Deprecated
	public float getAxisMarginBottom() {
		return axisXTitleQuadrantHeight;
	}

	/**
	 * @param axisXTitleQuadrantHeight
	 *            the axisXTitleQuadrantHeight to set
	 */
	@Deprecated
	public void setAxisMarginBottom(float axisXTitleQuadrantHeight) {
		this.axisXTitleQuadrantHeight = axisXTitleQuadrantHeight;
	}

	/**
	 * @return the axisXTitleQuadrantHeight
	 */
	public float getAxisXTitleQuadrantHeight() {
		return axisXTitleQuadrantHeight;
	}

	/**
	 * @param axisXTitleQuadrantHeight
	 *            the axisXTitleQuadrantHeight to set
	 */
	public void setAxisXTitleQuadrantHeight(float axisXTitleQuadrantHeight) {
		this.axisXTitleQuadrantHeight = axisXTitleQuadrantHeight;
	}

	/**
	 * @return the dataQuadrantPaddingTop
	 */
	@Deprecated
	public float getAxisMarginTop() {
		return dataQuadrantPaddingTop;
	}

	/**
	 * @param axisMarginTop
	 *            the axisMarginTop to set
	 */
	@Deprecated
	public void setAxisMarginTop(float axisMarginTop) {
		this.dataQuadrantPaddingTop = axisMarginTop;
		this.dataQuadrantPaddingBottom = axisMarginTop;
	}

	/**
	 * @return the dataQuadrantPaddingRight
	 */
	@Deprecated
	public float getAxisMarginRight() {
		return dataQuadrantPaddingRight;
	}

	/**
	 * @param axisMarginRight
	 *            the axisMarginRight to set
	 */
	@Deprecated
	public void setAxisMarginRight(float axisMarginRight) {
		this.dataQuadrantPaddingRight = axisMarginRight;
		this.dataQuadrantPaddingLeft = axisMarginRight;
	}

	/**
	 * @return the dataQuadrantPaddingTop
	 */
	public float getDataQuadrantPaddingTop() {
		return dataQuadrantPaddingTop;
	}

	/**
	 * @param dataQuadrantPaddingTop
	 *            the dataQuadrantPaddingTop to set
	 */
	public void setDataQuadrantPaddingTop(float dataQuadrantPaddingTop) {
		this.dataQuadrantPaddingTop = dataQuadrantPaddingTop;
	}

	/**
	 * @return the dataQuadrantPaddingLeft
	 */
	public float getDataQuadrantPaddingLeft() {
		return dataQuadrantPaddingLeft;
	}

	/**
	 * @param dataQuadrantPaddingLeft
	 *            the dataQuadrantPaddingLeft to set
	 */
	public void setDataQuadrantPaddingLeft(float dataQuadrantPaddingLeft) {
		this.dataQuadrantPaddingLeft = dataQuadrantPaddingLeft;
	}

	/**
	 * @return the dataQuadrantPaddingBottom
	 */
	public float getDataQuadrantPaddingBottom() {
		return dataQuadrantPaddingBottom;
	}

	/**
	 * @param dataQuadrantPaddingBottom
	 *            the dataQuadrantPaddingBottom to set
	 */
	public void setDataQuadrantPaddingBottom(float dataQuadrantPaddingBottom) {
		this.dataQuadrantPaddingBottom = dataQuadrantPaddingBottom;
	}

	/**
	 * @return the dataQuadrantPaddingRight
	 */
	public float getDataQuadrantPaddingRight() {
		return dataQuadrantPaddingRight;
	}

	/**
	 * @param dataQuadrantPaddingRight
	 *            the dataQuadrantPaddingRight to set
	 */
	public void setDataQuadrantPaddingRight(float dataQuadrantPaddingRight) {
		this.dataQuadrantPaddingRight = dataQuadrantPaddingRight;
	}

	/**
	 * @param padding
	 *            the dataQuadrantPaddingTop dataQuadrantPaddingBottom
	 *            dataQuadrantPaddingLeft dataQuadrantPaddingRight to set
	 * 
	 */
	public void setDataQuadrantPadding(float padding) {
		this.dataQuadrantPaddingTop = padding;
		this.dataQuadrantPaddingLeft = padding;
		this.dataQuadrantPaddingBottom = padding;
		this.dataQuadrantPaddingRight = padding;
	}

	/**
	 * @param topnbottom
	 *            the dataQuadrantPaddingTop dataQuadrantPaddingBottom to set
	 * @param leftnright
	 *            the dataQuadrantPaddingLeft dataQuadrantPaddingRight to set
	 * 
	 */
	public void setDataQuadrantPadding(float topnbottom, float leftnright) {
		this.dataQuadrantPaddingTop = topnbottom;
		this.dataQuadrantPaddingLeft = leftnright;
		this.dataQuadrantPaddingBottom = topnbottom;
		this.dataQuadrantPaddingRight = leftnright;
	}

	/**
	 * @param top
	 *            the dataQuadrantPaddingTop to set
	 * @param right
	 *            the dataQuadrantPaddingLeft to set
	 * @param bottom
	 *            the dataQuadrantPaddingBottom to set
	 * @param left
	 *            the dataQuadrantPaddingRight to set
	 * 
	 */
	public void setDataQuadrantPadding(float top, float right, float bottom,
			float left) {
		this.dataQuadrantPaddingTop = top;
		this.dataQuadrantPaddingLeft = right;
		this.dataQuadrantPaddingBottom = bottom;
		this.dataQuadrantPaddingRight = left;
	}

	/**
	 * @return the displayLongitudeTitle
	 */
	public boolean isDisplayLongitudeTitle() {
		return displayLongitudeTitle;
	}

	/**
	 * @param displayLongitudeTitle
	 *            the displayLongitudeTitle to set
	 */
	public void setDisplayLongitudeTitle(boolean displayLongitudeTitle) {
		this.displayLongitudeTitle = displayLongitudeTitle;
	}

	/**
	 * @return the displayAxisYTitle
	 */
	public boolean isDisplayLatitudeTitle() {
		return displayLatitudeTitle;
	}

	/**
	 * @param displayLatitudeTitle
	 *            the displayLatitudeTitle to set
	 */
	public void setDisplayLatitudeTitle(boolean displayLatitudeTitle) {
		this.displayLatitudeTitle = displayLatitudeTitle;
	}

	/**
	 * @return the latitudeNum
	 */
	public int getLatitudeNum() {
		return latitudeNum;
	}

	/**
	 * @param latitudeNum
	 *            the latitudeNum to set
	 */
	public void setLatitudeNum(int latitudeNum) {
		this.latitudeNum = latitudeNum;
	}

	/**
	 * @return the longitudeNum
	 */
	public int getLongitudeNum() {
		return longitudeNum;
	}

	/**
	 * @param longitudeNum
	 *            the longitudeNum to set
	 */
	public void setLongitudeNum(int longitudeNum) {
		this.longitudeNum = longitudeNum;
	}

	/**
	 * @return the displayLongitude
	 */
	public boolean isDisplayLongitude() {
		return displayLongitude;
	}

	/**
	 * @param displayLongitude
	 *            the displayLongitude to set
	 */
	public void setDisplayLongitude(boolean displayLongitude) {
		this.displayLongitude = displayLongitude;
	}

	/**
	 * @return the dashLongitude
	 */
	public boolean isDashLongitude() {
		return dashLongitude;
	}

	/**
	 * @param dashLongitude
	 *            the dashLongitude to set
	 */
	public void setDashLongitude(boolean dashLongitude) {
		this.dashLongitude = dashLongitude;
	}

	/**
	 * @return the displayLatitude
	 */
	public boolean isDisplayLatitude() {
		return displayLatitude;
	}

	/**
	 * @param displayLatitude
	 *            the displayLatitude to set
	 */
	public void setDisplayLatitude(boolean displayLatitude) {
		this.displayLatitude = displayLatitude;
	}

	/**
	 * @return the dashLatitude
	 */
	public boolean isDashLatitude() {
		return dashLatitude;
	}

	/**
	 * @param dashLatitude
	 *            the dashLatitude to set
	 */
	public void setDashLatitude(boolean dashLatitude) {
		this.dashLatitude = dashLatitude;
	}

	/**
	 * @return the dashEffect
	 */
	public PathEffect getDashEffect() {
		return dashEffect;
	}

	/**
	 * @param dashEffect
	 *            the dashEffect to set
	 */
	public void setDashEffect(PathEffect dashEffect) {
		this.dashEffect = dashEffect;
	}

	/**
	 * @return the displayBorder
	 */
	public boolean isDisplayBorder() {
		return displayBorder;
	}

	/**
	 * @param displayBorder
	 *            the displayBorder to set
	 */
	public void setDisplayBorder(boolean displayBorder) {
		this.displayBorder = displayBorder;
	}

	/**
	 * @return the borderColor
	 */
	public int getBorderColor() {
		return borderColor;
	}

	/**
	 * @param borderColor
	 *            the borderColor to set
	 */
	public void setBorderColor(int borderColor) {
		this.borderColor = borderColor;
	}

	/**
	 * @return the borderWidth
	 */
	public float getBorderWidth() {
		return borderWidth;
	}

	/**
	 * @param borderWidth
	 *            the borderWidth to set
	 */
	public void setBorderWidth(float borderWidth) {
		this.borderWidth = borderWidth;
	}

	/**
	 * @return the longitudeFontColor
	 */
	public int getLongitudeFontColor() {
		return longitudeFontColor;
	}

	/**
	 * @param longitudeFontColor
	 *            the longitudeFontColor to set
	 */
	public void setLongitudeFontColor(int longitudeFontColor) {
		this.longitudeFontColor = longitudeFontColor;
	}

	/**
	 * @return the longitudeFontSize
	 */
	public int getLongitudeFontSize() {
		return longitudeFontSize;
	}

	/**修改版
	 * @param longitudeFontSize		单位sp
	 *            the longitudeFontSize to set
	 */
	public void setLongitudeFontSize(int longitudeFontSize) {
		this.longitudeFontSize = longitudeFontSize;
	}

	/**
	 * @return the latitudeFontColor
	 */
	public int getLatitudeFontColor() {
		return latitudeFontColor;
	}

	/**
	 * @param latitudeFontColor
	 *            the latitudeFontColor to set
	 */
	public void setLatitudeFontColor(int latitudeFontColor) {
		this.latitudeFontColor = latitudeFontColor;
	}

	/**
	 * @return the latitudeFontSize
	 */
	public int getLatitudeFontSize() {
		return latitudeFontSize;
	}

	/**修改版
	 * @param latitudeFontSize	y轴 单位sp 
	 *            the latitudeFontSize to set
	 */
	public void setLatitudeFontSize(int latitudeFontSize) {
		this.latitudeFontSize = latitudeFontSize;
	}

	/**
	 * @return the crossLinesColor
	 */
	public int getCrossLinesColor() {
		return crossLinesColor;
	}

	/**
	 * @param crossLinesColor
	 *            the crossLinesColor to set
	 */
	public void setCrossLinesColor(int crossLinesColor) {
		this.crossLinesColor = crossLinesColor;
	}

	/**
	 * @return the crossLinesFontColor
	 */
	public int getCrossLinesFontColor() {
		return crossLinesFontColor;
	}

	/**
	 * @param crossLinesFontColor
	 *            the crossLinesFontColor to set
	 */
	public void setCrossLinesFontColor(int crossLinesFontColor) {
		this.crossLinesFontColor = crossLinesFontColor;
	}

	/**
	 * @return the longitudeTitles
	 */
	public List<String> getLongitudeTitles() {
		return longitudeTitles;
	}

	/**
	 * @param longitudeTitles
	 *            the longitudeTitles to set
	 */
	public void setLongitudeTitles(List<String> longitudeTitles) {
		this.longitudeTitles = longitudeTitles;
	}

	/**
	 *
	 * @param longitudeTitles
	 */
	public void setCustomLongitudeTitles(List<String> longitudeTitles){
		customLongitudeTitles = longitudeTitles;
		this.longitudeTitles = longitudeTitles;

	}


	/**
	 * @return the latitudeTitles
	 */
	public List<String> getLatitudeTitles() {
		return latitudeTitles;
	}

	/**
	 * @param latitudeTitles
	 *            the latitudeTitles to set
	 */
	public void setLatitudeTitles(List<String> latitudeTitles) {
		this.latitudeTitles = latitudeTitles;
	}

	public List<String> getIncreasePercent() {
		return increasePercent;
	}

	public void setIncreasePercent(List<String> increasePercent) {
		this.increasePercent = increasePercent;
	}

	/**
	 * @return the latitudeMaxTitleLength
	 */
	public int getLatitudeMaxTitleLength() {
		return latitudeMaxTitleLength;
	}

	/**
	 * @param latitudeMaxTitleLength
	 *            the latitudeMaxTitleLength to set
	 */
	public void setLatitudeMaxTitleLength(int latitudeMaxTitleLength) {
		this.latitudeMaxTitleLength = latitudeMaxTitleLength;
	}

	/**
	 * @return the displayCrossXOnTouch
	 */
	public boolean isDisplayCrossXOnTouch() {
		return displayCrossXOnTouch;
	}

	/**
	 * @param displayCrossXOnTouch
	 *            the displayCrossXOnTouch to set
	 */
	public void setDisplayCrossXOnTouch(boolean displayCrossXOnTouch) {
		this.displayCrossXOnTouch = displayCrossXOnTouch;
	}

	/**
	 * @return the displayCrossYOnTouch
	 */
	public boolean isDisplayCrossYOnTouch() {
		return displayCrossYOnTouch;
	}

	/**
	 * @param displayCrossYOnTouch
	 *            the displayCrossYOnTouch to set
	 */
	public void setDisplayCrossYOnTouch(boolean displayCrossYOnTouch) {
		this.displayCrossYOnTouch = displayCrossYOnTouch;
	}

	/**
	 * @return the clickPostX
	 */
	public float getClickPostX() {
		return clickPostX;
	}

	/**
	 * @param clickPostX
	 *            the clickPostX to set
	 */
	public void setClickPostX(float clickPostX) {
		this.clickPostX = clickPostX;
	}

	/**
	 * @return the clickPostY
	 */
	public float getClickPostY() {
		return clickPostY;
	}

	/**
	 * @param clickPostY
	 *            the clickPostY to set
	 */
	public void setClickPostY(float clickPostY) {
		this.clickPostY = clickPostY;
	}

	/**
	 * @return the notifyList
	 */
	public List<ITouchEventResponse> getNotifyList() {
		return notifyList;
	}

	/**
	 * @param notifyList
	 *            the notifyList to set
	 */
	public void setNotifyList(List<ITouchEventResponse> notifyList) {
		this.notifyList = notifyList;
	}

	/**
	 * @return the touchPoint
	 */
	public PointF getTouchPoint() {
		return touchPoint;
	}

	/**
	 * @param touchPoint
	 *            the touchPoint to set
	 */
	public void setTouchPoint(PointF touchPoint) {
		this.touchPoint = touchPoint;
	}

	/**
	 * @return the axisXPosition
	 */
	public int getAxisXPosition() {
		return axisXPosition;
	}

	/**
	 * @param axisXPosition
	 *            the axisXPosition to set
	 */
	public void setAxisXPosition(int axisXPosition) {
		this.axisXPosition = axisXPosition;
	}

	/**
	 * @return the axisYPosition
	 */
	public int getAxisYPosition() {
		return axisYPosition;
	}

	/**
	 * @param axisYPosition
	 *            the axisYPosition to set
	 */
	public void setAxisYPosition(int axisYPosition) {
		this.axisYPosition = axisYPosition;
	}

	protected PositionChangedListener positionChangedListener;
	protected OnLongPressListener onLongPressListener;

	/**
	 * 监听点击的点位置变化
	 *
	 * @param pcl
	 */
	public void setOnPositionChangedListener(PositionChangedListener pcl) {
		this.positionChangedListener = pcl;
	}

	public void setOnLongPressListener(OnLongPressListener onLongPressListener) {
		this.onLongPressListener = onLongPressListener;
	}

	protected int getDragUnit(){
		return (int) (ChartConfig.DRAG_UNIT * displayNumber);
	}

	protected boolean isLongPress;
	protected final int LONG_PRESS_TIME = 500;
	protected Runnable checkForLongPress = new Runnable() {
		@Override
		public void run() {
			Log.w("Slip", "isLongPress");
			isLongPress = true;
		}
	};

	protected void removeLongPressCheck() {
		if(isLongPress){
			if(onLongPressListener != null){
				onLongPressListener.onEnd();
			}
		}
		isLongPress = false;
		removeCallbacks(checkForLongPress);
	}

	protected XYValueFormatter xFormatter, yFormatter;

	public void setXFormatter(XYValueFormatter xFormatter) {
		this.xFormatter = xFormatter;
	}

	public void setYFormatter(XYValueFormatter yFormatter) {
		this.yFormatter = yFormatter;
	}

	protected String getFormatValue(String value, XYValueFormatter formatter){
		if (formatter == null) return value;
		return formatter.format(value);
	}

	public void setEnableRefresh(boolean enableRefresh) {
		this.enableRefresh = enableRefresh;
	}
	/**
	 * @return the displayFrom
	 */
	public int getDisplayFrom() {
		return displayFrom;
	}

	public int getDisplayNumber(){
		return displayNumber;
	}

	/**
	 * @param displayFrom the displayFrom to set
	 */
	public void setDisplayFrom(int displayFrom) {
		this.displayFrom = displayFrom;
	}

	public interface OnRefreshListener{

		void start();
	}
	protected OnRefreshListener onRefreshListener;

	public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
		this.onRefreshListener = onRefreshListener;
	}

	public void setRefreshState(boolean isRefreshing){
		if(isRefreshing){

			REFRESH_TEXT = REFRESH_ING;
		}else{
			displayFrom = 0;
			REFRESH_TEXT = REFRESH_HINT;

		}
		super.postInvalidate();
//        super.notifyEventAll(this);

	}

	/**
	 * only first and last see {@code TITLE_ONLY_FIRST_LAST}
	 * @param tilteMode
	 */
	public void setTilteMode(int tilteMode) {
		this.tilteMode = tilteMode;
	}

	public void setDisplayHorizontalLine(boolean displayHorizontalLine) {
		this.displayHorizontalLine = displayHorizontalLine;
	}

	public void setDisplayVerticalLine(boolean displayVerticalLine) {
		this.displayVerticalLine = displayVerticalLine;
	}

	/**
	 * data size
	 * @return
	 */
	protected int getDataSize(){
		return -1;
	}

	/**
	 * <p>
	 * calculate the distance between two touch points
	 * </p>
	 * <p>
	 * 複数タッチしたポイントの距離
	 * </p>
	 * <p>
	 * 计算两点触控时两点之间的距离
	 * </p>
	 *
	 * @param event
	 * @return float
	 * <p>
	 * distance
	 * </p>
	 * <p>
	 * 距離
	 * </p>
	 * <p>
	 * 距离
	 * </p>
	 */
	protected float calcDistance(MotionEvent event) {
		if(event.getPointerCount() > 1){
			float x = event.getX(0) - event.getX(1);
			float y = event.getY(0) - event.getY(1);
			return (float) Math.sqrt(x * x + y * y);
		}else{
			return 0f;
		}
	}


	/**
	 * 设置是否允许缩放
	 *
	 * @param enableZoom
	 */
	public void setEnableZoom(boolean enableZoom) {
		this.enableZoom = enableZoom;
	}

	public void setEnableDrag(boolean enableDrag) {
		this.enableDrag = enableDrag;
	}
}
