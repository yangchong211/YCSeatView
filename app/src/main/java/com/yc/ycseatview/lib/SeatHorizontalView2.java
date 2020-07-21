package com.yc.ycseatview.lib;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

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
public class SeatHorizontalView2 extends FrameLayout implements InterSeatView {

    private Context mContext;
    private RecyclerView mRecyclerPicView;
    private RecyclerView mRecyclerView;
    private SeatTypeAdapter seatTypeAdapter;
    /**
     * 集合数据
     */
    private ArrayList<SeatBean> mList = new ArrayList<>();
    /**
     * 座位，二位数组。方便添加过道位，以及处理过道位和调课位重叠逻辑
     * key是列。比如第1，2，3列
     * value是列的数据
     */
    private LinkedHashMap<Integer , ArrayList<SeatBean>> mSeatMap = new LinkedHashMap<>();
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

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        SeatLogUtils.i("SeatHorizontalView2-----onAttachedToWindow--");
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        SeatLogUtils.i("SeatHorizontalView2-----onDetachedFromWindow--");
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
        mRecyclerPicView = findViewById(R.id.recycler_pic_view);
        mRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                int width = mRecyclerView.getWidth();
                int height = mRecyclerView.getHeight();
                SeatLogUtils.i("layoutView---------mRecyclerView--"+width+"----------"+height);
            }
        });
    }

    private void initListener() {

    }

    private void initRecyclerView(int column, int line) {
        int total = column * line;
        LinkedHashMap<Integer , ArrayList<SeatBean>> map = SeatDataHelper.getSeatMap(total,line);
        mapToListData(map);
        SeatLogUtils.i("SeatRecyclerView------initRecyclerView--2--初始化总学生座位数-" + mList.size());
        setRecyclerView(mLine);
    }

    private void setRecyclerView(final int line) {
        if (seatTypeAdapter == null) {
            initCallBack();
            seatTypeAdapter = new SeatTypeAdapter(mContext);
            seatTypeAdapter.setData(mList);
            mRecyclerView.setAdapter(seatTypeAdapter);
            mRecyclerPicView.setAdapter(seatTypeAdapter);
            SpaceViewItemLine itemDecoration = new SpaceViewItemLine(10);
            itemDecoration.setPaddingEdgeSide(true);
            itemDecoration.setPaddingStart(true);
            mRecyclerView.addItemDecoration(itemDecoration);
            mRecyclerPicView.addItemDecoration(itemDecoration);
            seatTypeAdapter.registerAdapterDataObserver(new ViewDataObserver(mRecyclerView));
            //setCache();
        } else {
            seatTypeAdapter.setData(mList);
        }
        final SeatLayoutManager layoutManager = new SeatLayoutManager(mContext, line,seatTypeAdapter);
        final SeatLayoutManager layoutManagerPic = new SeatLayoutManager(mContext, line,seatTypeAdapter);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerPicView.setLayoutManager(layoutManagerPic);
        setPicRecyclerViewParams();
    }

    private void setPicRecyclerViewParams() {
        mRecyclerPicView.post(new Runnable() {
            @Override
            public void run() {
                int recyclerViewItemHeight = SeatPictureUtils.getRecyclerViewItemHeight(mRecyclerPicView);
                int recyclerViewItemWidth = SeatPictureUtils.getRecyclerViewItemWidth(mRecyclerPicView);
                int totalHeight = mLine * recyclerViewItemHeight + mLine * SeatPictureUtils.dip2px(mContext,10);
                int totalWidth = mColumn * recyclerViewItemWidth;
                SeatLogUtils.i("layoutView---------mRecyclerView计算--"+totalWidth+"----------"+totalHeight);
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mRecyclerPicView.getLayoutParams();
                layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                layoutParams.height = totalHeight ;
                mRecyclerPicView.setLayoutParams(layoutParams);
                mRecyclerPicView.setVisibility(GONE);
            }
        });
    }

    /**
     * 设置正常RecyclerView隐藏，显示图片RecyclerView。主要是用于截图
     */
    public void setPicRecyclerViewVisible() {
        if (mRecyclerView.getVisibility() == VISIBLE){
            mRecyclerView.setVisibility(GONE);
            mRecyclerPicView.setVisibility(VISIBLE);
        }
    }

    /**
     * 设置正常RecyclerView显示，隐藏图片RecyclerView
     */
    public void setRecyclerViewVisible() {
        if (mRecyclerView.getVisibility() == GONE){
            mRecyclerView.setVisibility(VISIBLE);
            mRecyclerPicView.setVisibility(GONE);
        }
    }

    /**
     * 添加item点击事件
     */
    public void addItemListener() {
        if (seatTypeAdapter!=null){
            seatTypeAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    if (mList!=null && mList.size()>position && position>=0){
                        SeatBean bean = mList.get(position);
                        bean.setSelect(!bean.isSelect());
                        seatTypeAdapter.notifyItemChanged(position);
                    }
                }
            });
            seatTypeAdapter.setClassTag(true);
            seatTypeAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 移除item点击事件
     */
    public void removeItemListener() {
        if (seatTypeAdapter!=null){
            seatTypeAdapter.setOnItemClickListener(null);
            seatTypeAdapter.setClassTag(false);
            seatTypeAdapter.notifyDataSetChanged();
        }
        //todo 给座位安排学生
    }

    /**
     * 获取座位数据
     * @return                              二维数组
     */
    public LinkedHashMap<Integer , ArrayList<SeatBean>> getAllData() {
        return mSeatMap;
    }

    private void setCache() {
        //屏幕外缓存
        //当列表滑动出了屏幕时，ViewHolder会被缓存在 mCachedViews ，其大小由mViewCacheMax决定，
        //默认DEFAULT_CACHE_SIZE为2，可通过Recyclerview.setItemViewCacheSize()动态设置。
        mRecyclerView.setItemViewCacheSize(225);
        //缓存池
        RecyclerView.RecycledViewPool recycledViewPool = new RecyclerView.RecycledViewPool();
        recycledViewPool.clear();
        recycledViewPool.setMaxRecycledViews(seatTypeAdapter.getItemViewType(0),225);
        mRecyclerView.setRecycledViewPool(recycledViewPool);
        //自定义缓存
        mRecyclerView.setViewCacheExtension(new RecyclerView.ViewCacheExtension() {
            @Nullable
            @Override
            public View getViewForPositionAndType(@NonNull RecyclerView.Recycler recycler, int i, int i1) {
                return null;
            }
        });
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
                if (mList != null && mList.size()>1) {
                    //如果有一个是不可坐的座位，则不可以交换位置
                    if (mList.get(srcPosition).isSelect() || mList.get(targetPosition).isSelect()){
                        Toast.makeText(mContext,"不可坐不可挪动",Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    int type = mList.get(srcPosition).getType();
                    if (type == SeatConstant.SeatType.TYPE_3){
                        doCorridorData(srcPosition,targetPosition);
                    } else {
                        doNormalData(srcPosition,targetPosition);
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

    private void doNormalData(int srcPosition, int targetPosition) {
        int mTargetPosition;
        //不是过道
        if (srcPosition < targetPosition) {
            //往后移
            mTargetPosition = targetPosition;
            //不是过道
        } else {
            //往前移动
            mTargetPosition = targetPosition;
        }
        // 更换map集合中
        setChangePosition(srcPosition,mTargetPosition);
    }

    private void setChangePosition(int srcPosition, int mTargetPosition) {
        SeatBean srcBean = mList.get(srcPosition);
        SeatBean targetBean = mList.get(mTargetPosition);
        int srcColumn = srcBean.getColumn();
        int srcLine = srcBean.getLine();
        int targetColumn = targetBean.getColumn();
        int targetLine = targetBean.getLine();

        Set<Integer> integers = mSeatMap.keySet();
        Iterator<Integer> iterator = integers.iterator();
        boolean isTarget = false;
        boolean isSrc = false;
        while (iterator.hasNext()){
            Integer next = iterator.next();
            if (next == srcColumn){
                //开始。将目标位置的数据，设置到当前位置上
                ArrayList<SeatBean> list = mSeatMap.get(targetColumn);
                if (list==null || isTarget){
                    continue;
                }
                list.set(targetLine-1,srcBean);
                isTarget = true;
                SeatLogUtils.i("doNormalData------将目标位置的数据，设置到当前位置上----" + (srcLine-1) + "------" + targetColumn + "----"+targetBean.toString());
            } else if (next == targetColumn){
                //目标。将开始拖动的数据，设置到目标位置上
                ArrayList<SeatBean> list = mSeatMap.get(srcColumn);
                if (list==null || isSrc){
                    continue;
                }
                list.set(srcLine-1,targetBean);
                isSrc = true;
                SeatLogUtils.i("doNormalData------将开始拖动的数据，设置到目标位置上----" + (targetLine-1) + "------" + srcColumn + "----"+srcBean.toString());
            }
        }
        ArrayList<SeatBean> listToMap = SeatDataHelper.getListToMap(mSeatMap);
        mList.clear();
        mList.addAll(listToMap);
        SeatDataHelper.sortList(mList);
        seatTypeAdapter.setData(mList);
    }

    /**
     * 处理过道拖动
     * @param srcPosition                           开始位置
     * @param targetPosition                        目标位置
     */
    private void doCorridorData(int srcPosition, int targetPosition) {
        int srcColumn = mList.get(srcPosition).getColumn();
        int targetColumn = mList.get(targetPosition).getColumn();
        LinkedHashMap<Integer, ArrayList<SeatBean>> newMap = new LinkedHashMap<>();
        Set<Integer> integers = mSeatMap.keySet();
        ArrayList<SeatBean> srcList = mSeatMap.get(srcColumn);
        ArrayList<SeatBean> targetList = mSeatMap.get(targetColumn);
        Iterator<Integer> iterator = integers.iterator();
        while (iterator.hasNext()){
            Integer next = iterator.next();
            if (next==srcColumn){
                newMap.put(srcColumn,targetList);
            } else if (next == targetColumn){
                newMap.put(targetColumn,srcList);
            } else {
                newMap.put(next,mSeatMap.get(next));
            }
        }
        mSeatMap.clear();
        mSeatMap.putAll(newMap);
        ArrayList<SeatBean> listToMap = SeatDataHelper.getListToMap(mSeatMap);
        mList.clear();
        mList.addAll(listToMap);
        SeatDataHelper.sortList(mList);
        seatTypeAdapter.setData(mList);
        SeatLogUtils.i("SeatRecyclerView------doCorridorData-----"+srcColumn+ "----" + targetColumn);
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
        LinkedHashMap<Integer , ArrayList<SeatBean>> map = SeatDataHelper.addCorridor(mSeatMap);
        mapToListData(map);
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
        LinkedHashMap<Integer , ArrayList<SeatBean>> map = SeatDataHelper.addRightOrLeftDataClass(mSeatMap,mLine,true);
        mapToListData(map);
        seatTypeAdapter.setData(mList);
    }

    /**
     * 右侧增加一列。找到最后一列的学生座位，然后添加座位即可
     */
    private void addRightClass() {
        removePreClass();
        LinkedHashMap<Integer , ArrayList<SeatBean>> map = SeatDataHelper.addRightOrLeftDataClass(mSeatMap,mLine,false);
        mapToListData(map);
        seatTypeAdapter.setData(mList);
    }

    /**
     * 后方增加一列。找到最后一排的学生座位，然后添加座位即可
     */
    private void addLastClass() {
        removePreClass();
        LinkedHashMap<Integer , ArrayList<SeatBean>> map = SeatDataHelper.addLastDataClass(mSeatMap);
        mapToListData(map);
        setRecyclerView(mLine+1);
    }

    private void mapToListData(LinkedHashMap<Integer, ArrayList<SeatBean>> map) {
        mSeatMap.clear();
        mSeatMap.putAll(map);
        //将map集合转化为list数据
        ArrayList<SeatBean> listToMap = SeatDataHelper.getListToMap(mSeatMap);
        mList.clear();
        mList.addAll(listToMap);
        SeatDataHelper.sortList(mList);
    }

    /**
     * 删除之前的调课位
     */
    private void removePreClass() {
        SeatLogUtils.i("SeatRecyclerView------添加座位----删除之前的调课位-");
        boolean isChange = false;
        Set<Integer> integers = mSeatMap.keySet();
        Iterator<Integer> iterator = integers.iterator();
        while (iterator.hasNext()){
            Integer next = iterator.next();
            ArrayList<SeatBean> list = mSeatMap.get(next);
            //左右有调课位
            if (list!=null && list.size()>1 && list.get(list.size()-1).getType()== SeatConstant.SeatType.TYPE_2){
                if (list.get(0).getType()== SeatConstant.SeatType.TYPE_2){
                    //左右
                    iterator.remove();
                } else {
                    //最后一排
                    list.remove(list.size()-1);
                }
                isChange = true;
            }
        }
        //将map集合转化为list数据
        ArrayList<SeatBean> listToMap = SeatDataHelper.getListToMap(mSeatMap);
        mList.clear();
        mList.addAll(listToMap);
        SeatDataHelper.sortList(mList);
        if (isChange) {
            setRecyclerView(mLine);
        }
    }

}
