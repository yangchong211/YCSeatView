/*
Copyright 2017 yangchong211（github.com/yangchong211）

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package com.yc.ycseatview.lib;

import android.support.v7.widget.RecyclerView;

import java.util.List;


/**
 * <pre>
 *     @author 杨充
 *     blog  : https://github.com/yangchong211
 *     time  : 2017/4/28
 *     desc  : 自定义AdapterDataObserver
 *     revise:
 * </pre>
 */
public class ViewDataObserver extends RecyclerView.AdapterDataObserver {

    private RecyclerView recyclerView;
    private AbsSeatAdapter<SeatBean> adapter;

    /**
     * 构造方法
     * @param recyclerView                  recyclerView
     */
    public ViewDataObserver(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        if (recyclerView.getAdapter() instanceof AbsSeatAdapter) {
            adapter = (AbsSeatAdapter<SeatBean>) recyclerView.getAdapter();
        }
    }

    @Override
    public void onItemRangeChanged(int positionStart, int itemCount) {
        super.onItemRangeChanged(positionStart, itemCount);
        update();
    }

    @Override
    public void onItemRangeInserted(int positionStart, int itemCount) {
        super.onItemRangeInserted(positionStart, itemCount);
        update();
    }

    @Override
    public void onItemRangeRemoved(int positionStart, int itemCount) {
        super.onItemRangeRemoved(positionStart, itemCount);
        update();
    }

    @Override
    public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
        super.onItemRangeMoved(fromPosition, toPosition, itemCount);
        update();
    }

    @Override
    public void onChanged() {
        super.onChanged();
        update();
    }


    /**
     * 自动更改座位中的索引，以及对应第几行，第几列数据
     */
    private void update() {
        SeatLogUtils.i("AdapterDataObserver----被调用---");
        if (recyclerView.getAdapter()!=null && recyclerView.getAdapter() instanceof AbsSeatAdapter){
//            //更新座位表
//            List<SeatBean> data = adapter.getData();
//            int line = adapter.getLine();
//            //重新改变座位是第几行，第几列
//            for (int i=0 ; i<data.size() ; i++){
//                SeatBean seatBean = data.get(i);
//                seatBean.setIndex(i);
//                int type = seatBean.getType();
//                if (type == SeatConstant.SeatType.TYPE_3){
//                    //过道
//                } else {
//                    //非过道
//                    //设置第几行第几列中的 列
//                    int beanColumn = i / line +1;
//                    seatBean.setColumn(beanColumn);
//                    //设置第几行第几列中的 行
//                    int beanLine = i % line + 1;
//                    seatBean.setLine(beanLine);
//                }
//            }
        }
    }

}