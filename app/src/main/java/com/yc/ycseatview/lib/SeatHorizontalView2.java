package com.yc.ycseatview.lib;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.yc.ycseatview.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * <pre>
 *     @author yangchong
 *     email  : yangchong211@163.com
 *     time   : 2020/07/15
 *     desc   : 自定义座位控件
 *     revise : Horizontal横向方向
 * </pre>
 */
public class SeatHorizontalView2 extends LinearLayout implements InterSeatView {

    private Context mContext;
    private RecyclerView mRecyclerView;
    private SeatTypeAdapter seatTypeAdapter;
    /**
     * 集合数据
     */
    private ArrayList<SeatBean> mList = new ArrayList<>();
    /**
     * 座位，二位数组。方便添加过道位
     * key是列
     * value是列的数据
     */
    private LinkedHashMap<Integer , ArrayList<SeatBean>> mSeatMap = new LinkedHashMap<>();
    /**
     * 过道
     */
    private ArrayList<Integer> mAisle = new ArrayList<>();
    /**
     * 列数
     */
    private int mColumn;
    /**
     * 行数
     */
    private int mLine;

    public SeatHorizontalView2(Context context) {
        super(context);
        initView(context);
    }

    public SeatHorizontalView2(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public SeatHorizontalView2(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        this.mContext = context;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.custom_seat_view, this, true);
        initFindViewById(view);
        initListener();
    }

    private void initFindViewById(View view) {
        mRecyclerView = view.findViewById(R.id.recycler_view);
    }

    private void initListener() {

    }

    private void initRecyclerView(int column, int line) {
        int total = column * line;
        mSeatMap.clear();
        LinkedHashMap<Integer , ArrayList<SeatBean>> map = SeatDataHelper.getSeatMap(total,line);
        mSeatMap.putAll(map);
        mList.clear();
        //将map集合转化为list数据
        ArrayList<SeatBean> listToMap = SeatDataHelper.getListToMap(mSeatMap);
        mList.addAll(listToMap);
        SeatLogUtils.i("SeatRecyclerView------initRecyclerView--2--初始化总学生座位数-" + mList.size());
        setRecyclerView(mLine);
    }

    private void setRecyclerView(final int line) {
        GridLayoutManager layoutManager = new GridLayoutManager(mContext, line, RecyclerView.HORIZONTAL, false);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int i) {
                int type = mList.get(i).getType();
                if (type == SeatConstant.SeatType.TYPE_3){
                    return line;
                } else {
                    return 1;
                }
            }
        });
        mRecyclerView.setLayoutManager(layoutManager);
        if (seatTypeAdapter == null) {
            SpaceViewItemLine itemDecoration = new SpaceViewItemLine(10);
            itemDecoration.setPaddingEdgeSide(true);
            itemDecoration.setPaddingStart(true);
            mRecyclerView.addItemDecoration(itemDecoration);
            initCallBack();
            //seatTypeAdapter = new SeatTypeAdapter1(mContext, mList);
            seatTypeAdapter = new SeatTypeAdapter(mContext);
            seatTypeAdapter.setData(mList);
            mRecyclerView.setAdapter(seatTypeAdapter);
        } else {
            //seatTypeAdapter.notifyDataSetChanged();
            seatTypeAdapter.setData(mList);
        }
    }

    /**
     * 设置行和列
     *
     * @param column 列
     * @param line   行
     */
    public void setColumnAndLine(int column, int line) {
        if (column <= 0) {
            column = 5;
        }
        if (line <= 0) {
            line = 5;
        }
        this.mColumn = column;
        this.mLine = line;
        SeatLogUtils.i("SeatRecyclerView------setColumnAndLine----mColumn-" + mColumn + "-----mLine-" + mLine);
        initRecyclerView(mColumn, mLine);
    }


    private void initCallBack() {
        ItemTouchCallback callback = new ItemTouchCallback(new ItemTouchCallback.OnItemTouchCallbackListener() {
            @Override
            public void onSwiped(int adapterPosition) {
                // 滑动删除的时候，从数据库、数据源移除，并刷新UI
                if (mList != null) {
                    //adapter.remove(adapterPosition);
                    //1、删除数据
                    mList.remove(adapterPosition);
                    //2、刷新
                    seatTypeAdapter.notifyItemRemoved(adapterPosition);
                }
            }

            @Override
            public boolean onMove(int srcPosition, int targetPosition) {
                if (mList != null) {
                    int type = mList.get(srcPosition).getType();
                    int mTargetPosition;
                    if (type == SeatConstant.SeatType.TYPE_3){
                        //是过道
                        int column = mList.get(targetPosition).getColumn();
                        if (srcPosition < targetPosition) {
                            //往后移
                            //是过道
                            //int line = mList.get(targetPosition).getLine();
                            mTargetPosition = column * mLine;
                            //不是过道
                        } else {
                            //往前移动
                            //是过道
                            mTargetPosition = (column - 1) * mLine;
                        }
                        // 更换数据源中的数据Item的位置
                        Collections.swap(mList, srcPosition, mTargetPosition);
                        // 更新UI中的Item的位置，主要是给用户看到交互效果
                        seatTypeAdapter.notifyItemMoved(srcPosition, mTargetPosition);
                    } else {
                        //不是过道
                        if (srcPosition < targetPosition) {
                            //往后移
                            mTargetPosition = targetPosition;
                            //不是过道
                        } else {
                            //往前移动
                            mTargetPosition = targetPosition;
                        }
                        // 更换数据源中的数据Item的位置
                        Collections.swap(mList, srcPosition, mTargetPosition);
                        // 更新UI中的Item的位置，主要是给用户看到交互效果
                        seatTypeAdapter.notifyItemMoved(srcPosition, mTargetPosition);
                    }
                    return true;
                }
                return true;
            }
        });
        callback.setDragEnable(true);
        callback.setSwipeEnable(true);
        //创建helper对象，callback监听recyclerView item 的各种状态
        ItemTouchHelper2 itemTouchHelper = new ItemTouchHelper2(callback);
        //关联recyclerView，一个helper对象只能对应一个recyclerView
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    /**
     * 恢复自动排座
     */
    @Override
    public void restoreSeat() {
        initRecyclerView(mColumn, mLine);
    }

    /**
     * 更改座位布局
     */
    @Override
    public void changeSeat() {

    }

    /**
     * 添加过道
     * 默认在左边
     */
    public void addCorridor() {
        SeatBean seatBean = new SeatBean();
        seatBean.setType(SeatConstant.SeatType.TYPE_3);
        mList.add(mLine, seatBean);
        seatTypeAdapter.setData(mList);
    }

    /**
     * 添加调课位
     *
     * @param type 类型
     */
    @Override
    public void addTypeClass(int type) {
        boolean fastDoubleClick = WidgetsUtils.isFastDoubleClick();
        if (fastDoubleClick) {
            return;
        }
        switch (type) {
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
     * 左侧增加一列。找到第一列的学生座位，然后添加座位即可
     */
    private void addLeftClass() {
        removePreClass();
        ArrayList<SeatBean> newList = new ArrayList<>();
        for (int i=0 ; i<mLine ; i++){
            SeatBean seatBean = new SeatBean();
            seatBean.setType(SeatConstant.SeatType.TYPE_2);
            newList.add(seatBean);
        }
        newList.addAll(mList);
        mList.clear();
        mList.addAll(newList);
        seatTypeAdapter.setData(mList);
    }

    /**
     * 右侧增加一列。找到最后一列的学生座位，然后添加座位即可
     */
    private void addRightClass() {
        removePreClass();
        for (int i=0 ; i<mLine ; i++){
            SeatBean seatBean = new SeatBean();
            seatBean.setType(SeatConstant.SeatType.TYPE_2);
            mList.add(seatBean);
        }
        seatTypeAdapter.setData(mList);
    }

    /**
     * 后方增加一列。找到最后一排的学生座位，然后添加座位即可
     */
    private void addLastClass() {
        removePreClass();
        ArrayList<SeatBean> newList = new ArrayList<>();
        //添加最后一列
        for (int j=0 ; j<mList.size() ; j++){
            int index = mList.get(j).getIndex();
            if ((index+1) % mLine == 0){
                SeatBean seatBean = new SeatBean();
                seatBean.setType(SeatConstant.SeatType.TYPE_2);
                int addCount = index / mLine;
                int pos = index + addCount + 1;
                seatBean.setIndex(pos);
                newList.add(seatBean);
                SeatLogUtils.i("SeatRecyclerView------后方增加一列---添加排课位数据1--"+addCount+"----"+seatBean.getIndex());
            }
        }
        //修改原始数据
        for (int i=0 ; i<mList.size() ; i++){
            int index = mList.get(i).getIndex();
            //数据索引+
            int addCount = index / mLine;
            int pos = index + addCount;
            mList.get(i).setIndex(pos);
            SeatLogUtils.i("SeatRecyclerView------后方增加一列---修改正常数据索引2--"+mList.get(i).getIndex());
        }
        newList.addAll(mList);
        mList.clear();
        mList.addAll(newList);
        SeatLogUtils.i("SeatRecyclerView------后方增加一列---结束时数据3--"+newList.size());
        //重排位置，从小到大
        Collections.sort(mList, new Comparator<SeatBean>() {
            @Override
            public int compare(SeatBean o1, SeatBean o2) {
                long start1 = o1.getIndex();
                long start2 = o2.getIndex();
                if(start1 > start2) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });
        setRecyclerView(mLine+1);
    }

    /**
     * 删除之前的调课位
     */
    private void removePreClass() {
        SeatLogUtils.i("SeatRecyclerView------添加座位----删除之前的调课位-");
        Iterator<SeatBean> iterator = mList.iterator();
        boolean isChange = false;
        while (iterator.hasNext()) {
            SeatBean bean = iterator.next();
            int type = bean.getType();
            if (type == SeatConstant.SeatType.TYPE_2) {
                iterator.remove();
                isChange = true;
            }
        }
        if (isChange) {
            setRecyclerView(mLine);
            //seatTypeAdapter.notifyDataSetChanged();
        }
    }
}
