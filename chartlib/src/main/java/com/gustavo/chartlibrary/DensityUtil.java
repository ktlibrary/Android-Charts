package com.gustavo.chartlibrary;

import android.content.Context;
import android.util.TypedValue;

public class DensityUtil {

	/**
	 * 根据手机的分辨率dip 的单位转成px
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}
    /**
	 * 根据手机的分辨率dip 的单位转成px
	 */
	public static int dp2px(Context context, float dpValue) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, context.getResources().getDisplayMetrics());
	}

	
	/**
	 * 根据手机的分辨率从px 的单位转成dip
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 将px值转换为sp值，保证文字大小不变
	 * 
	 * @param pxValue
	 * @param fontScale
	 *            （DisplayMetrics类中属性scaledDensity）
	 * @return
	 */
	public static int px2sp(float pxValue, float fontScale) {
		return (int) (pxValue / fontScale + 0.5f);
	}

	/**
	 * 将sp值转换为px值，保证文字大小不变
	 * 
	 * @param spValue
	 *            （DisplayMetrics类中属性scaledDensity）
	 * @return
	 */
	public static int sp2px(Context context, float spValue) {
		float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue * fontScale + 0.5f);
	}

	public static int getStatusHeight(Context context){
		int height;
		int identifier = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
		if(identifier != 0){
			height = context.getResources().getDimensionPixelSize(identifier);
		}else {
			height = dip2px(context, 25);
		}
		return height;
	}
}
