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

import android.graphics.Canvas;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.yc.ycseatview.R;


/**
 * <pre>
 *     @author 杨充
 *     blog  : https://github.com/yangchong211
 *     time  : 2017/5/2
 *     desc  : 自定义ItemTouchHelper2
 *     revise: 参考严正杰大神博客：https://blog.csdn.net/yanzhenjie1003/article/details/51935982
 *             开源项目：https://github.com/yangchong211/YCRefreshView
 * </pre>
 */
public class ItemTouchCallback extends ItemTouchHelper2.Callback {

    /**
     * Item操作的回调，去更新UI和数据源
     */
    private OnItemTouchCallbackListener onItemTouchCallbackListener;
    /**
     * 是否可以拖拽
     */
    private boolean isCanDrag = false;
    /**
     * 是否可以被滑动
     */
    private boolean isCanSwipe = false;
    /**
     * 按住拖动item的颜色
     */
    private int color = 0;

    public ItemTouchCallback(OnItemTouchCallbackListener onItemTouchCallbackListener) {
        this.onItemTouchCallbackListener = onItemTouchCallbackListener;
    }

    /**
     * 设置是否可以被拖拽
     *
     * @param canDrag 是true，否false
     */
    public void setDragEnable(boolean canDrag) {
        isCanDrag = canDrag;
    }

    /**
     * 设置是否可以被滑动
     *
     * @param canSwipe 是true，否false
     */
    public void setSwipeEnable(boolean canSwipe) {
        isCanSwipe = canSwipe;
    }

    /**
     * 设置按住拖动item的颜色
     * @param color     颜色
     */
    public void setColor(@ColorInt int color){
        this.color = color;
    }

    /**
     * 当Item被长按的时候是否可以被拖拽
     *
     * @return                      true
     */
    @Override
    public boolean isLongPressDragEnabled() {
        return isCanDrag;
    }

    /**
     * Item是否可以被滑动(H：左右滑动，V：上下滑动)
     * isItemViewSwipeEnabled()返回值是否可以拖拽排序，true可以，false不可以
     * @return                      true
     */
    @Override
    public boolean isItemViewSwipeEnabled() {
        return isCanSwipe;
    }

    /**
     * 当用户拖拽或者滑动Item的时候需要我们告诉系统滑动或者拖拽的方向
     * 动作标识分：dragFlags和swipeFlags
     * dragFlags：列表滚动方向的动作标识（如竖直列表就是上和下，水平列表就是左和右）
     * wipeFlags：与列表滚动方向垂直的动作标识（如竖直列表就是左和右，水平列表就是上和下）
     *
     * 思路：如果你不想上下拖动，可以将 dragFlags = 0
     *      如果你不想左右滑动，可以将 swipeFlags = 0
     *      最终的动作标识（flags）必须要用makeMovementFlags()方法生成
     */
    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView,
                                @NonNull RecyclerView.ViewHolder viewHolder) {
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            // flag如果值是0，相当于这个功能被关闭
            int dragFlag = ItemTouchHelper2.LEFT | ItemTouchHelper2.RIGHT
                    | ItemTouchHelper2.UP | ItemTouchHelper2.DOWN;
            int swipeFlag = 0;
            return makeMovementFlags(dragFlag, swipeFlag);
        } else if (layoutManager instanceof LinearLayoutManager) {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
            int orientation = linearLayoutManager.getOrientation();

            int dragFlag = 0;
            int swipeFlag = 0;

            // 为了方便理解，相当于分为横着的ListView和竖着的ListView
            // 如果是横向的布局
            if (orientation == LinearLayoutManager.HORIZONTAL) {
                swipeFlag = ItemTouchHelper2.UP | ItemTouchHelper2.DOWN;
                dragFlag = ItemTouchHelper2.LEFT | ItemTouchHelper2.RIGHT;
            } else if (orientation == LinearLayoutManager.VERTICAL) {
                // 如果是竖向的布局，相当于ListView
                dragFlag = ItemTouchHelper2.UP | ItemTouchHelper2.DOWN;
                swipeFlag = ItemTouchHelper2.LEFT | ItemTouchHelper2.RIGHT;
            }
            //第一个参数是拖拽flag，第二个是滑动的flag
            return makeMovementFlags(dragFlag, swipeFlag);
        }
        return 0;
    }


    /**
     * 当Item被拖拽的时候被回调
     *
     * @param recyclerView          recyclerView
     * @param srcViewHolder         当前被拖拽的item的viewHolder
     * @param targetViewHolder      当前被拖拽的item下方的另一个item的viewHolder
     * @return                      是否被移动
     */
    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView,
                          @NonNull RecyclerView.ViewHolder srcViewHolder,
                          @NonNull RecyclerView.ViewHolder targetViewHolder) {
        if (onItemTouchCallbackListener != null) {
            int srcPosition = srcViewHolder.getAdapterPosition();
            int targetPosition = targetViewHolder.getAdapterPosition();
            return onItemTouchCallbackListener.onMove(srcPosition, targetPosition);
        }
        return false;
    }


    /**
     * 当item侧滑出去时触发（竖直列表是侧滑，水平列表是竖滑）
     *
     * @param viewHolder            viewHolder
     * @param direction             滑动的方向
     */
    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        if (onItemTouchCallbackListener != null) {
            onItemTouchCallbackListener.onSwiped(viewHolder.getAdapterPosition());
        }
    }

    /**
     * 当item被拖拽或侧滑时触发
     *
     * @param viewHolder            viewHolder
     * @param actionState           当前item的状态
     */
    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);
        //不管是拖拽或是侧滑，背景色都要变化
        if (actionState != ItemTouchHelper2.ACTION_STATE_IDLE) {
            if (color==0){
                viewHolder.itemView.setBackgroundColor(viewHolder.itemView.getContext()
                        .getResources().getColor(android.R.color.darker_gray));
            }else {
                viewHolder.itemView.setBackgroundResource(color);
            }
        }
    }

    /**
     * 当item的交互动画结束时触发
     *
     * @param recyclerView          recyclerView
     * @param viewHolder            viewHolder
     */
    @Override
    public void clearView(@NonNull RecyclerView recyclerView,
                          @NonNull RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        viewHolder.itemView.setBackgroundResource(R.drawable.shape_seat_view_null_r5);
        viewHolder.itemView.setAlpha(1);
        viewHolder.itemView.setScaleY(1);
    }


    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                            @NonNull RecyclerView.ViewHolder viewHolder,
                            float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        if (actionState == ItemTouchHelper2.ACTION_STATE_SWIPE) {
            float value = 1 - Math.abs(dX) / viewHolder.itemView.getWidth();
            viewHolder.itemView.setAlpha(value);
            viewHolder.itemView.setScaleY(value);
        }
    }

    public interface OnItemTouchCallbackListener {
        /**
         * 当某个Item被滑动删除的时候
         *
         * @param adapterPosition   item的position
         */
        void onSwiped(int adapterPosition);

        /**
         * 当两个Item位置互换的时候被回调
         *
         * @param srcPosition       拖拽的item的position
         * @param targetPosition    目的地的Item的position
         * @return                  开发者处理了操作应该返回true，开发者没有处理就返回false
         */
        boolean onMove(int srcPosition, int targetPosition);
    }
}
