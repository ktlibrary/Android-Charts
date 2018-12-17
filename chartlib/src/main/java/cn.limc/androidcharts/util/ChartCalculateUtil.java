package cn.limc.androidcharts.util;

import java.util.ArrayList;
import java.util.List;

import cn.limc.androidcharts.entity.IStickEntity;
import cn.limc.androidcharts.entity.MACDEntity;
import cn.limc.androidcharts.entity.OHLCEntity;

/**
 * 指标计算
 * Created by gustavo on 2016/1/5.
 */
public class ChartCalculateUtil {


    /**
     * 计算公式为
     * n日RSV=（Cn－Ln）/（Hn－Ln）×100
     * 公式中，Cn为第n日收盘价；Ln为n日内的最低价；Hn为n日内的最高价。
     * <p/>
     * 其次，计算K值与D值：
     * 当日K值=2/3×前一日K值+1/3×当日RSV
     * 当日D值=2/3×前一日D值+1/3×当日K值
     * <p/>
     * 若无前一日K 值与D值，则可分别用50来代替。
     * J值=3*当日K值-2*当日D值
     *
     * @param result
     * @param dayN
     * @param m1
     * @param m2
     */
    public static List<double[]> getKDJList(List<IStickEntity> result, int dayN, int m1, int m2) {

        int size = result.size();
        double preK = 50.0;
        double preD = 50.0;
        double rsv = 0.0;
        List<double[]> list = new ArrayList<>();

        double hn, ln, cn, K, D, J;

        for (int i = 0; i < size; i++) {
            OHLCEntity ohlcEntity = (OHLCEntity) result.get(i);

            hn = ohlcEntity.getHigh();
            ln = ohlcEntity.getLow();
            cn = ohlcEntity.getClose();

            if (i < dayN) {

                for (int j = i; j >= 0; j--) {
                    OHLCEntity entity = (OHLCEntity) result.get(j);
                    hn = Math.max(hn, entity.getHigh());
                    ln = Math.min(ln, entity.getLow());
                }

            } else {

                for (int j = i; j > i - dayN; j--) {
                    OHLCEntity entity = (OHLCEntity) result.get(j);
                    hn = Math.max(hn, entity.getHigh());
                    ln = Math.min(ln, entity.getLow());
                }
            }

            if (hn != ln) {
                rsv = (cn - ln) / (hn - ln) * 100;
            }
            K = 2 * preK / m1 + 1 * rsv / m1;
            D = 2 * preD / m2 + 1 * K / m2;
            J = 3 * K - 2 * D;
            list.add(new double[]{K, D, J});

            preK = K;
            preD = D;
        }
        return list;
    }

    /**
     * 公式:
     * LC := REF(CLOSE,1); 昨收
     * <p/>
     * RSI1:SMA(MAX(CLOSE-LC,0),N1,1)/SMA(ABS(CLOSE-LC),N1,1)*100;
     * RSI2:SMA(MAX(CLOSE-LC,0),N2,1)/SMA(ABS(CLOSE-LC),N2,1)*100;
     * RSI3:SMA(MAX(CLOSE-LC,0),N3,1)/SMA(ABS(CLOSE-LC),N3,1)*100;
     * <p/>
     * 参数名 最小值 最大值 缺省值
     * 参数1 N1 	1 	100 	6
     * 参数2 N2 	1 	100 	12
     * 参数3 N3 	1 	100 	24
     *
     * @param list
     * @param n
     */
    public static List<Float> getRSIList(List<Float> list, int n) {
        int size = list.size();
        List<Float> rsiList = new ArrayList<>();
        List<Float> minusList = new ArrayList<>();
        List<Float> minusABSList = new ArrayList<>();
        minusList.add(0.0f);
        minusABSList.add(0.0f);
        for (int i = 1; i < size; i++) {
            Float minus = list.get(i) - list.get(i - 1);
            minusList.add(Math.max(minus, 0.0f));
            minusABSList.add(Math.abs(minus));
        }
        List<Float> smaList = getSMAList(minusList, n, 1f);
        List<Float> smaList1 = getSMAList(minusABSList, n, 1f);

        for (int i = 0; i < size; i++) {
            if (smaList1.get(i) == 0) {
                rsiList.add(0.0f);
            } else {
                rsiList.add(smaList.get(i) / smaList1.get(i) * 100);
            }
        }

        return rsiList;
    }

    /**
     * 算法: 若Y=SMA(X,N,M) 则 Y=(M*X+(N-M)*Y')/N，其中Y'表示上一周期Y值，N必须大于M
     *
     * @param list
     * @param n
     * @param m    default=1
     * @return
     */
    public static List<Float> getSMAList(List<Float> list, final int n, float m) {
        List<Float> smaList = new ArrayList<>();
        Float sma = list.get(0);
        smaList.add(sma);
        for (int i = 1; i < list.size(); i++) {

            sma = (list.get(i) * m + (n - m) * sma) / n;
            smaList.add(sma);
        }
        return smaList;
    }

    /**
     * calculate MACD values
     * <p/>
     * DIFF : EMA(CLOSE,S) - EMA(CLOSE,P);
     * DEA  : EMA(DIFF,M);
     * MACD : 2*(DIFF-DEA);
     *
     * @param list        :Price list to calculate，the first at head, the last at tail.
     * @param shortPeriod :the short period value.
     * @param longPeriod  :the long period value.
     * @param midPeriod   :the mid period value.
     * @return
     */
    public static List<IStickEntity> getMACDList(final List<IStickEntity> list, final int shortPeriod, final int longPeriod, int midPeriod) {

        int size = list.size();
        List<Float> doubleList = new ArrayList<>();
        List<String> dateList = new ArrayList<>();
        OHLCEntity entity;
        for (int i = 0; i < size; i++) {
            entity = (OHLCEntity) list.get(i);
            doubleList.add(entity.getClose());
            dateList.add(entity.getDate());
        }

        Float dif = 0.0f;
        Float dea = 0.0f;

        List<Float> shortList = getEMAList(doubleList, shortPeriod);
        List<Float> longList = getEMAList(doubleList, longPeriod);
        List<Float> difList = new ArrayList<>();
        List<IStickEntity> entityList = new ArrayList<>();

        for (int i = 0; i < size; i++) {

            dif = shortList.get(i) - longList.get(i);
            difList.add(dif);
            dea = getEXPMA(difList, midPeriod);
            entityList.add(new MACDEntity(dea, dif, (dif - dea) * 2, dateList.get(i)));

        }

        return entityList;
    }

    /**
     * Calculate EMA,
     *
     * @param list :Price list to calculate，the first at head, the last at tail.
     * @return
     */
    public static Float getEXPMA(final List<Float> list, final int number) {
        Float k = 2.0f / (number + 1.0f);
        Float ema = list.get(0);
        for (int i = 1; i < list.size(); i++) {
            ema = list.get(i) * k + ema * (1 - k);
        }
        return ema;
    }
    /**
     * Calculate EMA,
     *
     * @param list :Price list to calculate，the first at head, the last at tail.
     * @return
     */
    public static List<Float> getEMAList(List<Float> list, final int number) {
        List<Float> emaList = new ArrayList<>();
        float k = 2.0f / (number + 1.0f);
        Float ema = list.get(0);
        emaList.add(ema);
        for (int i = 1; i < list.size(); i++) {
            ema = list.get(i) * k + ema * (1 - k);
            emaList.add(ema);
        }
        return emaList;
    }
}
