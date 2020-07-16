package com.yc.ycseatview.lib;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;

/**
 * <pre>
 *     @author yangchong
 *     blog  : https://github.com/yangchong211
 *     time  : 2018/9/18
 *     desc  : 为分组列表提供的GridLayoutManager
 *     revise: 因为分组列表如果要使用GridLayoutManager实现网格布局。
 * </pre>
 */
public class SeatLayoutManager extends GridLayoutManager {

    public SeatLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
        setSpanSizeLookup();
    }

    private void setSpanSizeLookup() {
        super.setSpanSizeLookup(new SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int count = getSpanCount();
                return count;
            }
        });
    }

    @Override
    public void setSpanSizeLookup(SpanSizeLookup spanSizeLookup) {

    }
}