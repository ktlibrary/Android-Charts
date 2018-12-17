package cn.limc.androidcharts.listener;

import android.view.MotionEvent;

/**
 * 图标触摸点位置变化回调接口
 * Created by gustavo on 2015/3/6.
 */
public interface PositionChangedListener {

    /**
     * 显示触摸点的数据
     * @param index
     */
    void showData(int index);

}
