/*
 * OHLCEntity.java
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
 * Entity data which is use for display a candleStick in CandleStickChart use
 * OHLC(Open,High,Low,Close) four values for a candle
 * </p>
 * <p>
 * CandleStickChartのロウソク線表示用データです、四本値を格納用オブジェクトです。
 * </p>
 * <p>
 * 保存蜡烛线表示用的OHLC四个值的实体对象
 * </p>
 * 
 * @author limc
 * @version v1.0 2011/05/29 12:19:06
 */
public class OHLCEntity implements IStickEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private double vol;
	@Override
	public double getVol() {
		return vol;
	}
	@Override
	public void setVol(double vol) {
		this.vol = vol;
	}

	/**
	 * <p>
	 * Open Value
	 * </p>
	 * <p>
	 * 始値
	 * </p>
	 * <p>
	 * 开盘价
	 * </p>
	 */
	private float open;

	/**
	 * <p>
	 * High Value
	 * </p>
	 * <p>
	 * 高値
	 * </p>
	 * <p>
	 * 最高价
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
	 * 最低价
	 * </p>
	 * 
	 */
	private float low;

	/**
	 * <p>
	 * Close Value
	 * </p>
	 * <p>
	 * 終値
	 * </p>
	 * <p>
	 * 收盘价
	 * </p>
	 * 
	 */
	private float close;

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
	 * Constructor of OHLCEntity
	 * </p>
	 * <p>
	 * OHLCEntity类对象的构造函数
	 * </p>
	 * <p>
	 * OHLCEntityのコンストラクター
	 * </p>
	 * 
	 * @param open
	 *            <p>
	 *            Open Value
	 *            </p>
	 *            <p>
	 *            始値
	 *            </p>
	 *            <p>
	 *            开盘价
	 *            </p>
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
	 *            最低价
	 *            </p>
	 * @param close
	 *            <p>
	 *            Close Value
	 *            </p>
	 *            <p>
	 *            終値
	 *            </p>
	 *            <p>
	 *            收盘价
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
	public OHLCEntity(float open, float high, float low, float close,
			String date,double vol) {
		super();
		this.open = open;
		this.high = high;
		this.low = low;
		this.close = close;
		this.date = date;
		this.vol = vol;
	}

	/**
	 * <p>
	 * Constructor of OHLCEntity
	 * </p>
	 * <p>
	 * OHLCEntity类对象的构造函数
	 * </p>
	 * <p>
	 * OHLCEntityのコンストラクター
	 * </p>
	 * 
	 */
	public OHLCEntity() {
		super();
	}

	/**
	 * @return the open
	 */
	public float getOpen() {
		return open;
	}

	/**
	 * @param open
	 *            the open to set
	 */
	public void setOpen(float open) {
		this.open = open;
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
	 * @return the close
	 */
	public float getClose() {
		return close;
	}

	/**
	 * @param close
	 *            the close to set
	 */
	public void setClose(float close) {
		this.close = close;
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

}
