package cn.limc.androidcharts;

import android.content.Context;
import android.graphics.Color;

import com.gustavo.chartlibrary.DensityUtil;

/**
 * Created by gustavo on 2015/10/28.
 */
public class ChartConfig {

    //分时显示数据数
    public static int MINUTE_NUMBER = 240;
    //日线
    public static int DAY_NUMBER = 42;
//    public static int DAY_NUMBER_LAND = 60;

    //边框颜色
    public static int COLOR_BORDER = Color.parseColor("#c0bfbf");

    //xy轴颜色
    public static int COLOR_XY = Color.parseColor("#c0bfbf");

    //xy轴坐标值颜色
    public static int COLOR_LONGITUDE_FONT = Color.parseColor("#adadad");

    //xy轴字体大小
    public static int LONGITUDE_FONT_SIZE = 10;

    public static int LATITUDE_FONT_SIZE = LONGITUDE_FONT_SIZE;

    public static int LONGITUDE_NUM = 4;
    //经纬线颜色
    public static int COLOR_LONGITUDE_LINE = Color.parseColor("#e5e5e5");

    //十字线颜色
    public static int COLOR_CROSS_LINE = Color.parseColor("#686464");
    public static int COLOR_CROSS_FONT = COLOR_CROSS_LINE;


    //蜡烛图阳阴线颜色
    public static int COLOR_POSITIVE_STICK = Color.parseColor("#c82a1d");
    public static int COLOR_NEGATIVE_STICK = Color.parseColor("#3c874b");

    //分时 区域颜色
    public static int  COLOR_AREA = Color.parseColor("#94abd2f6");

    //画线宽度
    public static float LINE_WIDTH = 1.5f;
    //圆点半径
    public static float CIRCLE_RADIO = 1f;

    public static float DRAG_UNIT = 0.05f;
    public static int STICK_SPACING = 2;

    public static int COLOR_DAY5 = Color.parseColor("#e50492");
    public static int COLOR_DAY10 = Color.parseColor("#ff6c00");
    public static int COLOR_DAY20 = Color.parseColor("#288eea");

    //MACD
    public static int COLOR_MACD_DIFF_LINE = COLOR_DAY5;
    public static int COLOR_MACD_DEA_LINE = COLOR_DAY10;
    public static int COLOR_MACD_MACD_LINE = COLOR_DAY20;

    //分时九转
    public static float NINE_TEXT_SIZE = 10;

    // 日k
    public static int MAX_DISPLAY_NUMBER = 200;
    public static int MIN_DISPLAY_NUMBER = 20;

    public static void init(Context context){
        LONGITUDE_FONT_SIZE = DensityUtil.sp2px(context, 10);
        LATITUDE_FONT_SIZE = LONGITUDE_FONT_SIZE;
        NINE_TEXT_SIZE = LONGITUDE_FONT_SIZE;

        LINE_WIDTH = DensityUtil.dp2px(context, 1.5f);

        CIRCLE_RADIO = DensityUtil.dp2px(context, 1f);
        STICK_SPACING = DensityUtil.dp2px(context, 2);
        
        
    }

}
