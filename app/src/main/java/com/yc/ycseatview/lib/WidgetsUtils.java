package com.yc.ycseatview.lib;

import android.text.TextUtils;

import java.util.HashMap;

/**
 * <pre>
 *     @author  yangchong
 *     email  : yangchong211@163.com
 *     time   : 2020/07/15
 *     desc   : 防爆力点击工具
 *     revise:  参考诸葛书代码
 * </pre>
 */
public final class WidgetsUtils {

    private static final int MAX_INTERVAL = 500;
    private static long mLastClickTime;
    private static HashMap<String, Long> tagMaps = new HashMap<>();

    /**
     * 判断一个控件是否短时间内重复点击
     *
     * @return
     */
    public static boolean isFastDoubleClick() {
        return isFastDoubleClick(MAX_INTERVAL);
    }

    public static boolean isFastDoubleClick(int maxInterval) {
        long current = System.currentTimeMillis();
        long interval = current - mLastClickTime;
        if ((interval > 0) && (interval < maxInterval)) {
            return true;
        }
        mLastClickTime = current;
        return false;
    }

    public static boolean isFastDoubleClickWithTag(int maxInterval, String tag) {
        if (TextUtils.isEmpty(tag)) {
            return true;
        }
        long current = System.currentTimeMillis();
        long interval = 0;
        if (tagMaps.containsKey(tag)) {
            // 获取上次保存时间离现在的时间间距.
            interval = current - tagMaps.get(tag);
        }
        if ((interval > 0) && (interval < maxInterval)) {
            return true;
        }
        // 放入当前tag对应的时间
        tagMaps.put(tag, current);
        return false;
    }
}
