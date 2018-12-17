/*
 * MACDEntity.java
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

package cn.limc.androidcharts.entity;

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
 * @version v1.0 2014/03/17 17:16:56
 * 
 */
public class MACDEntity implements IStickEntity {

	private float dea;
	private float diff;
	private float macd;
	private String date;

	/**
	 * <p>
	 * Constructor of MACDEntity
	 * </p>
	 * <p>
	 * MACDEntity类对象的构造函数
	 * </p>
	 * <p>
	 * MACDEntityのコンストラクター
	 * </p>
	 * 
	 */
	public MACDEntity() {
		super();
	}

	/**
	 * <p>
	 * Constructor of MACDEntity
	 * </p>
	 * <p>
	 * MACDEntity类对象的构造函数
	 * </p>
	 * <p>
	 * MACDEntityのコンストラクター
	 * </p>
	 * 
	 * @param dea
	 * @param diff
	 * @param macd
	 * @param date
	 */
	public MACDEntity(float dea, float diff, float macd, String date) {
		super();
		this.dea = dea;
		this.diff = diff;
		this.macd = macd;
		this.date = date;
	}

	/**
	 * @return the dea
	 */
	public float getDea() {
		return dea;
	}

	/**
	 * @param dea
	 *            the dea to set
	 */
	public void setDea(float dea) {
		this.dea = dea;
	}

	/**
	 * @return the diff
	 */
	public float getDiff() {
		return diff;
	}

	/**
	 * @param diff
	 *            the diff to set
	 */
	public void setDiff(float diff) {
		this.diff = diff;
	}

	/**
	 * @return the macd
	 */
	public float getMacd() {
		return macd;
	}

	/**
	 * @param macd
	 *            the macd to set
	 */
	public void setMacd(float macd) {
		this.macd = macd;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @return
	 * 
	 * @see IMeasurable#getHigh()
	 */
	public float getHigh() {
		return Math.max(Math.max(getDea(), getDiff()), getMacd());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @return
	 * 
	 * @see IMeasurable#getLow()
	 */
	public float getLow() {
		return Math.min(Math.min(getDea(), getDiff()), getMacd());
	}

	@Override
	public double getVol() {
		return 0;
	}

	@Override
	public void setVol(double vol) {

	}

	@Override
	public String toString() {
		return "MACDEntity{" +
				"dea=" + dea +
				", diff=" + diff +
				", macd=" + macd +
				", date='" + date + '\'' +
				'}';
	}
}
