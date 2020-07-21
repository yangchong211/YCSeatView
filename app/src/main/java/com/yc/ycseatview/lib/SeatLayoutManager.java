package com.yc.ycseatview.lib;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * <pre>
 *     @author yangchong
 *     blog  : https://github.com/yangchong211
 *     time  : 2018/9/18
 *     desc  : 要使用GridLayoutManager实现网格布局
 *     revise:
 * </pre>
 */
public class SeatLayoutManager extends GridLayoutManager {

    private AbsSeatAdapter mAdapter;
    private int mSpanCount;

    public SeatLayoutManager(Context context, int spanCount , AbsSeatAdapter adapter) {
        //super(context, spanCount);
        super(context,spanCount, RecyclerView.HORIZONTAL,false);
        this.mAdapter = adapter;
        this.mSpanCount = spanCount;
        setSpanSizeLookup();
    }

    private void setSpanSizeLookup() {
        super.setSpanSizeLookup(new SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int count = getSpanCount();
                if (mAdapter != null && mAdapter.getData()!=null) {
                    Object o = mAdapter.getData().get(position);
                    if (o instanceof SeatBean){
                        SeatBean bean = (SeatBean) o;
                        int type = bean.getType();
                        if (type == SeatConstant.SeatType.TYPE_3){
                            return mSpanCount;
                        } else {
                            return 1;
                        }
                    }
                }
                return count;
            }
        });
    }

}