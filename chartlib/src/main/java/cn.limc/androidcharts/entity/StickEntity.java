/*
 * StickEntity.java
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

package cn.limc.androidcharts.entity;

/**
 * <p>
 * Entity data which is use for display a stick in CCSStickChart
 * </p>
 * <p>
 * StickChartのスティック表示用データです、高安値を格納用オブジェクトです。
 * </p>
 * <p>
 * CCSStickChart保存柱条表示用的高低值的实体对象
 * </p>
 * 
 * @author limc
 * @version v1.0 2011/05/29 12:24:49
 */
public class StickEntity implements IStickEntity {

	/**
	 * <p>
	 * High Value
	 * </p>
	 * <p>
	 * 高値
	 * </p>
	 * <p>
	 * 最高值
	 * </p>
	 * 
	 */
	private float high;

	/**
	 * <p>
	 * Low Value
	 * </p>
	 * <p>
	 * 低値
	 * </p>
	 * <p>
	 * 最低值
	 * </p>
	 * 
	 */
	private float low;

	/**
	 * <p>
	 * Date
	 * </p>
	 * <p>
	 * 日付
	 * </p>
	 * <p>
	 * 日期
	 * </p>
	 * 
	 */
	private String date;

	/**
	 * 
	 * <p>
	 * Constructor of StickEntity
	 * </p>
	 * <p>
	 * StickEntity类对象的构造函数
	 * </p>
	 * <p>
	 * StickEntityのコンストラクター
	 * </p>
	 * 
	 * @param high
	 *            <p>
	 *            High Value
	 *            </p>
	 *            <p>
	 *            高値
	 *            </p>
	 *            <p>
	 *            最高价
	 *            </p>
	 * @param low
	 *            <p>
	 *            Low Value
	 *            </p>
	 *            <p>
	 *            低値
	 *            </p>
	 *            <p>
	 *            最低值
	 *            </p>
	 * @param date
	 *            <p>
	 *            Date
	 *            </p>
	 *            <p>
	 *            日付
	 *            </p>
	 *            <p>
	 *            日期
	 *            </p>
	 */
	public StickEntity(float high, float low, String date) {
		super();
		
		this.high = high;
		this.low = low;
		this.date = date;
	}

	/**
	 * 
	 * <p>
	 * Constructor of StickEntity
	 * </p>
	 * <p>
	 * StickEntity类对象的构造函数
	 * </p>
	 * <p>
	 * StickEntityのコンストラクター
	 * </p>
	 * 
	 */
	public StickEntity() {
		super();
	}

	/**
	 * @return the high
	 */
	public float getHigh() {
		return high;
	}

	/**
	 * @param high
	 *            the high to set
	 */
	public void setHigh(float high) {
		this.high = high;
	}

	/**
	 * @return the low
	 */
	public float getLow() {
		return low;
	}

	/**
	 * @param low
	 *            the low to set
	 */
	public void setLow(float low) {
		this.low = low;
	}

	/**
	 * @return the date
	 */
	public String getDate() {
		return date;
	}

	/**
	 * @param date
	 *            the date to set
	 */
	public void setDate(String date) {
		this.date = date;
	}


	@Override
	public double getVol() {
		return 0;
	}

	@Override
	public void setVol(double vol) {

	}
}
