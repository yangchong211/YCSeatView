package com.yc.ycseatview.lib;

import android.app.admin.SecurityLog;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.yc.ycseatview.R;

import java.util.ArrayList;
import java.util.Collections;

/**
 * <pre>
 *     @author  yangchong
 *     email  : yangchong211@163.com
 *     time   : 2020/07/15
 *     desc   : 自定义座位控件
 *     revise:
 * </pre>
 */
public class SeatRecyclerView extends LinearLayout {

    private Context mContext;
    private RecyclerView mRecyclerView;
    private SeatTypeAdapter seatTypeAdapter;
    /**
     * 集合数据
     */
    private ArrayList<SeatBean> list;
    /**
     * 列数
     */
    private int mColumn;
    /**
     * 行数
     */
    private int mLine;

    public SeatRecyclerView(Context context) {
        super(context);
        initView(context);
    }

    public SeatRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public SeatRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        this.mContext = context;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.custom_seat_view, this, true);
        initFindViewById(view);
        initListener();
        //initRecyclerView(0,0);
    }

    private void initFindViewById(View view) {
        mRecyclerView = view.findViewById(R.id.recycler_view);
    }

    private void initListener() {

    }

    private void initRecyclerView(int column, int line) {
        int total = column * line;
        list = new ArrayList<>();
        for (int i=0 ; i<total ; i++){
            SeatBean seatBean = new SeatBean();
            seatBean.setCorridor(false);
            //设置第几行第几列中的 列
            int beanColumn = (i+1)%mColumn;
            if (beanColumn==0){
                seatBean.setColumn(column);
            } else {
                seatBean.setColumn(beanColumn);
            }
            //设置第几行第几列中的 行
            if (beanColumn==0){
                int beanLine = (i+1)/mColumn;
                seatBean.setLine(beanLine);
            } else {
                int beanLine = (i+1)/mColumn + 1;
                seatBean.setLine(beanLine);
            }
            seatBean.setName("学生"+i);
            list.add(seatBean);
        }
        GridLayoutManager layoutManager = new GridLayoutManager(mContext,column,RecyclerView.VERTICAL,false);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int i) {
                boolean corridor = list.get(i).isCorridor();
                if (corridor){
                    return mColumn;
                } else {
                    return 1;
                }
            }
        });
        mRecyclerView.setLayoutManager(layoutManager);
        SpaceViewItemLine itemDecoration = new SpaceViewItemLine(10);
        itemDecoration.setPaddingEdgeSide(true);
        itemDecoration.setPaddingStart(true);
        mRecyclerView.addItemDecoration(itemDecoration);
        if (seatTypeAdapter==null){
            initCallBack();
        }
        seatTypeAdapter = new SeatTypeAdapter(mContext,list);
        mRecyclerView.setAdapter(seatTypeAdapter);
    }

    /**
     * 设置行和列
     * @param column                    列
     * @param line                      行
     */
    public void setColumnAndLine(int column, int line) {
        if (column<=0){
            column = 5;
        }
        if (line<=0){
            line = 5;
        }
        this.mColumn = column;
        this.mLine = line;
        SeatLogUtils.i("SeatRecyclerView------setColumnAndLine----mColumn-"+mColumn+"-----mLine-"+mLine);
        initRecyclerView(column,line);
    }


    private void initCallBack() {
        ItemTouchCallback callback = new ItemTouchCallback(new ItemTouchCallback.OnItemTouchCallbackListener() {
                @Override
                public void onSwiped(int adapterPosition) {
                    // 滑动删除的时候，从数据库、数据源移除，并刷新UI
                    if (list != null) {
                        //adapter.remove(adapterPosition);
                        //1、删除数据
                        list.remove(adapterPosition);
                        //2、刷新
                        seatTypeAdapter.notifyItemRemoved(adapterPosition);
                    }
                }

                @Override
                public boolean onMove(int srcPosition, int targetPosition) {
                    if (list != null) {
                        boolean corridor = list.get(srcPosition).isCorridor();
                        if (corridor){
                            //是过道
                        } else {
                            //不是过道
                            // 更换数据源中的数据Item的位置
                            Collections.swap(list, srcPosition, targetPosition);
                            // 更新UI中的Item的位置，主要是给用户看到交互效果
                            seatTypeAdapter.notifyItemMoved(srcPosition, targetPosition);
                        }
                        return true;
                    }
                    return true;
                }
            });
        callback.setDragEnable(true);
        callback.setSwipeEnable(true);
        //创建helper对象，callback监听recyclerView item 的各种状态
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        //关联recyclerView，一个helper对象只能对应一个recyclerView
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    /**
     * 恢复自动排座
     */
    public void restoreSeat() {
        initRecyclerView(mColumn,mLine);
    }

    /**
     * 更改座位布局
     */
    public void changeSeat() {

    }

    /**
     * 添加调课位
     * @param type                  类型
     */
    public void addClass(@SeatConstant.Type int type) {
        switch (type){
            case SeatConstant.Type.TYPE_1:
                //左侧增加一列
                addLeftClass();
                break;
            case SeatConstant.Type.TYPE_2:
                //右侧增加一列
                addRightClass();
                break;
            case SeatConstant.Type.TYPE_3:
                //后方增加一列
                addLastClass();
                break;
        }
    }

    /**
     * 左侧增加一列
     */
    private void addLeftClass() {
        for (int i=0 ; i<mLine ; i++){
            int index = i*mColumn;
            SeatBean seatBean = new SeatBean();
            list.add(index,seatBean);
        }
        seatTypeAdapter.notifyDataSetChanged();
    }

    /**
     * 右侧增加一列
     */
    private void addRightClass() {
        for (int i=0 ; i<mLine ; i++){
            int index = i*mColumn + mLine;
            SeatBean seatBean = new SeatBean();
            list.add(index,seatBean);
        }
        seatTypeAdapter.notifyDataSetChanged();
    }

    /**
     * 后方增加一列
     */
    private void addLastClass() {
        int start = list.size();
        for (int i=0 ; i<mColumn ; i++){
            SeatBean seatBean = new SeatBean();
            list.add(seatBean);
        }
        int end = list.size();
        //seatTypeAdapter.notifyItemRangeChanged(start,(end-start));
        seatTypeAdapter.notifyDataSetChanged();
    }

    /**
     * 添加过道
     */
    public void addCorridor() {
        SeatBean seatBean = new SeatBean();
        seatBean.setCorridor(true);
        list.add(mColumn*1,seatBean);
        seatTypeAdapter.notifyDataSetChanged();
    }
}
