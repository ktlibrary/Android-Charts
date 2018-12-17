package com.gustavo.chartlibrary;

import android.text.TextUtils;

import java.text.DecimalFormat;


/**
 * 
 * @author gustavo
 * 2014年4月28日 17:30:45 	
 */
public class MathUtils {
	private static String[] unit =  {"","万","亿","万亿"};
	private static String[] CHINESE_NUMBER =  {"零", "一","二","三","四", "五", "六", "七", "八", "九", "十"};
	private static String[] CHINES_UNIT =  {"", "十","百","千","万", "十", "百", "千", "亿", "十"};


	private static DecimalFormat df;

	/**
	 * 四舍五入,保留两位小数,带正负号
	 * @param number
	 * @return
	 */
	public static String toTwoWithFlag(String number){
		if(TextUtils.isEmpty(number)){
			return "";
		}
		double num = 0;
		try {
			num = Double.parseDouble(number);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		if(num >0){
			return "+"+toTwo(num);
		}else{
			return toTwo(num);
		}
	}
	public static String toTwoWithFlag(double number){

		if(number > 0){
			return "+"+toTwo(number);
		}else{
			return toTwo(number);
		}
	}

	/**
	 * 四舍五入 保留两位小数
	 *
	 * @param number
	 * @return	4.00 -> 4
	 */
	public static String toTwo(double number){
        if (df == null) {
            df = new DecimalFormat("#0.00");//四舍五入 保留两位小数
        }
		return df.format(number);
	}

	public static String toTwo(float number){
        if (df == null) {
            df = new DecimalFormat("#0.00");//四舍五入 保留两位小数
        }
		return df.format(number);
	}
	/**
	 * 四舍五入 保留两位小数
	 * @param number
	 * @return
	 */
	public static String toTwo(String number){
		if(TextUtils.isEmpty(number)){
			return "0";
		}
		double num = 0;
		try {
			num = Double.parseDouble(number);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return toTwo(num);
	}
	/**
	 * 
	 * @param number
	 * @return
	 */
	public static String formatSize(double number){
        if(number == 0) return "0";

        int index = 0;

        while(Math.abs(number) >= 10000 && index < unit.length - 1){
			number /= 10000;
            index++;
        }
		String format = toTwo(number);
		if(format.endsWith(".00")){
			format = format.substring(0, format.indexOf("."));
		}
        return format + unit[index];
	}
	/**
	 * 将数据转换为xx万/xx亿
	 * @param number
	 * @return	String
	 */
	public static String formatSize(String number){
		if(TextUtils.isEmpty(number)){
			return "";
		}
		double num = 0;
		try {
			num = Double.parseDouble(number);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return formatSize(num);
	}

	public static long formatToShou(String num){
		if(TextUtils.isEmpty(num)){
			return 0;
		}
		long i = 0;
		try {
			i = Long.parseLong(num) / 100;
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return i;

	}

    /**
     *
     * @param value	需要格式化的数字
     * @param num	保留的小数位数
     * @return
     */
    public static String saveDotNumber(double value, int num) {
        StringBuffer sb = new StringBuffer("#0");
        if(num >0){
            sb.append(".");
            for (int i = 0; i < num; i++) {
                sb.append("0");
            }
        }
        String pattern = sb.toString();
        DecimalFormat df1 = new DecimalFormat(pattern);
        return df1.format(value);
    }

    public static String saveDotNumber(String value, int num) {
    	return saveDotNumber(parseFloat(value), num);
    }

	/**
	 * 如果转换失败,返回0
	 * @param str
	 * @return
	 */
	public static float parseFloat(String str){

		float aFloat = 0;
		if(TextUtils.isEmpty(str)){
			return aFloat;
		}

		try {
			aFloat = Float.parseFloat(str);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			aFloat = 0;
		}
		return aFloat;
	}

	public static long parseLong(String str){
		long aFloat = 0;
		if(TextUtils.isEmpty(str)){
			return aFloat;
		}

		try {
			aFloat = Long.parseLong(str);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			aFloat = 0;
		}
		return aFloat;
	}


	public static int parseInt(String str){
		int aFloat = 0;
		if(TextUtils.isEmpty(str)){
			return aFloat;
		}

		try {
			aFloat = Integer.parseInt(str);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			aFloat = 0;
		}
		return aFloat;
	}

	/**
	 * 阿拉伯数字转换成汉子
	 * @param number
	 * @return
     */
	public static String toChinese(int number){
		if(number == 0)	return CHINESE_NUMBER[0];

		boolean negative = false;
		StringBuilder sb = new StringBuilder();
		if(number < 0){
			negative = true;
			number = Math.abs(number);
		}

		char[] charArray = String.valueOf(number).toCharArray();
		for (int i = 0; i < charArray.length; i++) {
			Integer integer = Integer.valueOf(charArray[i] + "");
			sb.append(CHINESE_NUMBER[integer]);

			if(integer != 0 || (charArray.length - 1 - i) % 4 == 0){
				sb.append(CHINES_UNIT[charArray.length - 1 - i]);
			}
		}
		String result = sb.toString()
				.replaceAll(CHINESE_NUMBER[0] + "++", CHINESE_NUMBER[0])
				.replace(CHINESE_NUMBER[0] + "万", "万")
				.replace(CHINESE_NUMBER[0] + "亿", "亿")
				.replace("亿万", "亿");
		if(result.endsWith(CHINESE_NUMBER[0])){
			result = result.substring(0, result.length() - 1);
		}
		if(result.startsWith("一十")){
			result = result.substring(1, result.length());
		}
		if(negative){
			result = "负" + result;
		}
		return result;
	}

	public static String toVideoTime(int millis){
		int time = millis / 1000;
		String result = String.format("%02d", time / 3600) + ":"
				+ String.format("%02d", time % 3600 / 60) + ":"
				+ String.format("%02d", time % 3600 % 60);
		if(result.startsWith("00:")){
			result = result.substring(3);
		}
		return result;
	}
}
