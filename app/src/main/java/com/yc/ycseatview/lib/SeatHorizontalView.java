package com.yc.ycseatview.lib;

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
import java.util.Comparator;
import java.util.Iterator;

/**
 * <pre>
 *     @author  yangchong
 *     email  : yangchong211@163.com
 *     time   : 2020/07/15
 *     desc   : 自定义座位控件
 *     revise : Horizontal横向方向
 * </pre>
 */
public class SeatHorizontalView extends LinearLayout implements InterSeatView{

    private Context mContext;
    private RecyclerView mRecyclerView;
    private SeatTypeAdapter1 seatTypeAdapter;
    /**
     * 集合数据
     */
    private ArrayList<SeatBean> mList = new ArrayList<>();
    /**
     * 列数
     */
    private int mColumn;
    /**
     * 行数
     */
    private int mLine;

    public SeatHorizontalView(Context context) {
        super(context);
        initView(context);
    }

    public SeatHorizontalView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public SeatHorizontalView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
        mList.clear();
        for (int i=0 ; i<total ; i++){
            SeatBean seatBean = new SeatBean();
            seatBean.setCorridor(false);
            seatBean.setIndex(i);
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
            seatBean.setType(SeatConstant.SeatType.TYPE_1);
            seatBean.setName("学生"+i);
            mList.add(seatBean);
        }
        SeatLogUtils.i("SeatRecyclerView------initRecyclerView----初始化总学生座位数-"+mList.size());
        setRecyclerView(mLine);
    }

    private void setRecyclerView(final int line) {
        GridLayoutManager layoutManager = new GridLayoutManager(mContext,line,RecyclerView.HORIZONTAL,false);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int i) {
                boolean corridor = mList.get(i).isCorridor();
                if (corridor){
                    return line;
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
        seatTypeAdapter = new SeatTypeAdapter1(mContext,mList);
        //seatTypeAdapter.setData(mList);
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
                        boolean corridor = mList.get(srcPosition).isCorridor();
                        if (corridor){
                            //是过道
                        } else {
                            //不是过道
                            // 更换数据源中的数据Item的位置
                            Collections.swap(mList, srcPosition, targetPosition);
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
    @Override
    public void restoreSeat() {
        initRecyclerView(mColumn,mLine);
    }

    /**
     * 更改座位布局
     */
    @Override
    public void changeSeat() {

    }

    /**
     * 添加过道
     */
    public void addCorridor() {
        SeatBean seatBean = new SeatBean();
        seatBean.setCorridor(true);
        seatBean.setType(SeatConstant.SeatType.TYPE_3);
        mList.add(mLine,seatBean);
        seatTypeAdapter.notifyDataSetChanged();
    }

    /**
     * 添加调课位
     * @param type                  类型
     */
    @Override
    public void addTypeClass(int type) {
        boolean fastDoubleClick = WidgetsUtils.isFastDoubleClick();
        if (fastDoubleClick){
            return;
        }
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
     * 左侧增加一列。找到第一列的学生座位，然后添加座位即可
     */
    private void addLeftClass() {
        removePreClass();
        int size = mList.size();
        SeatLogUtils.i("SeatRecyclerView------添加座位----左侧增加一列-"+size);
        for (int i=0 ; i<size ; i++){
            int column = mList.get(i).getColumn();
            SeatLogUtils.i("SeatRecyclerView------左侧增加一列----------------"+column);
            if (column==1){
                SeatBean seatBean = new SeatBean();
                seatBean.setType(SeatConstant.SeatType.TYPE_2);
                seatBean.setIndex(i);
                mList.add(seatBean);
                SeatLogUtils.i("SeatRecyclerView------左侧增加一列---调课位1--"+i);
            } else {
                mList.get(i).setIndex(i);
                SeatLogUtils.i("SeatRecyclerView------左侧增加一列---调课位2--"+(i+0));
            }
        }
        //重排位置
        Collections.sort(mList, new Comparator<SeatBean>() {
            @Override
            public int compare(SeatBean o1, SeatBean o2) {
                long start1 = o1.getIndex();
                long start2 = o2.getIndex();
                if(start1 < start2) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });
        SeatLogUtils.i("SeatRecyclerView------左侧增加一列-----"+mList.toString());
        seatTypeAdapter.notifyDataSetChanged();
        //setRecyclerView(mColumn+1);
    }

    /**
     * 右侧增加一列。找到最后一列的学生座位，然后添加座位即可
     */
    private void addRightClass() {
        removePreClass();
        int size = mList.size();
        SeatLogUtils.i("SeatRecyclerView------添加座位----右侧增加一列-"+size);
        for (int i=0 ; i<size ; i++){
            int column = mList.get(i).getColumn();
            if (column == mColumn){
                SeatLogUtils.i("SeatRecyclerView------右侧增加一列-----"+i);
                SeatBean seatBean = new SeatBean();
                seatBean.setIndex(i+1);
                seatBean.setType(SeatConstant.SeatType.TYPE_2);
                mList.add(seatBean);
                SeatLogUtils.i("SeatRecyclerView------右侧增加一列---调课位1--"+i);
            }  else {
                mList.get(i).setIndex(i);
                SeatLogUtils.i("SeatRecyclerView------右侧增加一列---调课位2--"+(i+0));
            }
        }
        //重排位置
        Collections.sort(mList, new Comparator<SeatBean>() {
            @Override
            public int compare(SeatBean o1, SeatBean o2) {
                long start1 = o1.getIndex();
                long start2 = o2.getIndex();
                if(start1 < start2) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });
        SeatLogUtils.i("SeatRecyclerView------右侧增加一列-----"+mList.toString());
        //setRecyclerView(mColumn+1);
        seatTypeAdapter.notifyDataSetChanged();
    }

    /**
     * 后方增加一列。找到最后一排的学生座位，然后添加座位即可
     */
    private void addLastClass() {
        removePreClass();
        int size = mList.size();
        SeatLogUtils.i("SeatRecyclerView------添加座位----后方增加一列-"+size);
        for (int i=0 ; i<size ; i++){
            int line = mList.get(i).getLine();
            if (line == mLine){
                SeatLogUtils.i("SeatRecyclerView------后方增加一列-----"+i);
                SeatBean seatBean = new SeatBean();
                seatBean.setType(SeatConstant.SeatType.TYPE_2);
                mList.add(seatBean);
            }
        }
        seatTypeAdapter.notifyDataSetChanged();
    }

    /**
     * 删除之前的调课位
     */
    private void removePreClass(){
        SeatLogUtils.i("SeatRecyclerView------添加座位----删除之前的调课位-");
        Iterator<SeatBean> iterator = mList.iterator();
        boolean isChange = false;
        int index = 0;
        while (iterator.hasNext()){
            SeatBean bean = iterator.next();
            int type = bean.getType();
            if (type == SeatConstant.SeatType.TYPE_2){
                iterator.remove();
                isChange = true;
            } else {
                bean.setIndex(index++);
            }
        }
        if (isChange){
            seatTypeAdapter.notifyDataSetChanged();
        }
    }
}
