package com.yc.ycseatview.lib;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yc.ycseatview.R;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * <pre>
 *     @author yangchong
 *     email  : yangchong211@163.com
 *     time   : 2020/07/20
 *     desc   : 状态操作视图
 *     revise :
 * </pre>
 */
public class SeatStatesView extends FrameLayout implements View.OnClickListener {

    private Context mContext;
    private RelativeLayout mRlSetClass;
    private TextView mTvNormalSeat;
    private TextView mTvSeatHint;
    private TextView mTvStartSeat;
    private LinearLayout mLlChangeClass;
    private TextView mTvAddClass;
    private TextView mTvAddCorridor;
    private TextView mTvRestore;
    private TextView mTvChange;
    private RelativeLayout mRlStatesStudent;
    private TextView mTvStatesStudentName;
    private TextView mTvStatesStudent1;
    private TextView mTvStatesStudent2;
    private TextView mTvStatesClose;
    private int viewType = 0;

    public SeatStatesView(@NonNull Context context) {
        super(context);
        initView(context);
    }

    public SeatStatesView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public SeatStatesView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        this.mContext = context;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.custom_states_view, this, true);
        initFindViewById(view);
        initListener();
    }

    private void initListener() {
        //只显示设置座位可坐和不可坐视图
        mTvStartSeat.setOnClickListener(this);

        //只显示调课位，过道，恢复视图
        mTvAddClass.setOnClickListener(this);
        mTvAddCorridor.setOnClickListener(this);
        mTvRestore.setOnClickListener(this);
        mTvChange.setOnClickListener(this);

        //只显示状态视图：删除学员视图，添加调课位学员和标记不可坐视图，标记请假【和取消请假】视图，删除过道视图
        //需要细分。具体看setStatesTypeView
        mTvStatesStudent1.setOnClickListener(this);
        mTvStatesStudent2.setOnClickListener(this);
        mTvStatesClose.setOnClickListener(this);
    }

    private void initFindViewById(View view) {
        mRlSetClass = view.findViewById(R.id.rl_set_class);
        mTvNormalSeat = view.findViewById(R.id.tv_normal_seat);
        mTvSeatHint = view.findViewById(R.id.tv_seat_hint);
        mTvStartSeat = view.findViewById(R.id.tv_start_seat);
        mLlChangeClass = view.findViewById(R.id.ll_change_class);
        mTvAddClass = view.findViewById(R.id.tv_add_class);
        mTvAddCorridor = view.findViewById(R.id.tv_add_corridor);
        mTvRestore = view.findViewById(R.id.tv_restore);
        mTvChange = view.findViewById(R.id.tv_change);
        mRlStatesStudent = view.findViewById(R.id.rl_states_student);
        mTvStatesStudentName = view.findViewById(R.id.tv_states_student_name);
        mTvStatesStudent1 = view.findViewById(R.id.tv_states_student1);
        mTvStatesStudent2 = view.findViewById(R.id.tv_states_student2);
        mTvStatesClose = view.findViewById(R.id.tv_states_close);
    }

    /**
     * 设置状态视图
     * @param type                      类型
     */
    public void setStatesView(int type){
        this.viewType = 0;
        switch (type){
            //只显示设置座位可坐和不可坐视图
            case 0:
                mRlSetClass.setVisibility(VISIBLE);
                mLlChangeClass.setVisibility(GONE);
                mRlStatesStudent.setVisibility(GONE);
                break;
            //只显示调课位，过道，恢复视图
            case 1:
                mRlSetClass.setVisibility(GONE);
                mLlChangeClass.setVisibility(VISIBLE);
                mRlStatesStudent.setVisibility(GONE);
                break;
            //只显示状态视图：删除学员视图，添加调课位学员和标记不可坐视图，标记请假【和取消请假】视图，删除过道视图
            //需要细分。具体看setStatesTypeView
            case 2:
                mRlSetClass.setVisibility(GONE);
                mLlChangeClass.setVisibility(GONE);
                mRlStatesStudent.setVisibility(VISIBLE);
                break;
            default:
                break;
        }
    }

    /**
     * 设置状态视图的细分
     * @param viewType                  类型
     */
    @SuppressLint("SetTextI18n")
    public void setStatesTypeView(@SeatConstant.ViewType int viewType , SeatBean bean){
        switch (viewType) {
            //删除学员视图
            case SeatConstant.ViewType.TYPE_1:
                setStatesView(2);
                mTvStatesStudentName.setVisibility(VISIBLE);
                mTvStatesStudent1.setVisibility(VISIBLE);
                mTvStatesStudent2.setVisibility(GONE);
                mTvStatesStudentName.setText("删除:"+bean.getName() + "  " + bean.getColumn()+ "列" +"/"+bean.getLine()+"行");
                mTvStatesStudent1.setText("删除学员");
                break;
            //调课位，添加调课位学员和标记不可坐视图
            case SeatConstant.ViewType.TYPE_2:
                setStatesView(2);
                mTvStatesStudentName.setVisibility(VISIBLE);
                mTvStatesStudent1.setVisibility(VISIBLE);
                mTvStatesStudent2.setVisibility(VISIBLE);
                mTvStatesStudentName.setText("调课位");
                mTvStatesStudent1.setText("添加调期学员");
                mTvStatesStudent2.setText("标记不可坐");
                break;
            //标记请假【和取消请假】视图
            case SeatConstant.ViewType.TYPE_3:
                setStatesView(2);
                mTvStatesStudentName.setVisibility(VISIBLE);
                mTvStatesStudent1.setVisibility(VISIBLE);
                mTvStatesStudent2.setVisibility(GONE);
                mTvStatesStudentName.setText("请假:"+bean.getName() + "  " + bean.getColumn()+ "列" +"/"+bean.getLine()+"行");
                mTvStatesStudent1.setText("标记请假");
                break;
            //删除过道视图
            case SeatConstant.ViewType.TYPE_4:
                setStatesView(2);
                mTvStatesStudentName.setVisibility(VISIBLE);
                mTvStatesStudent1.setVisibility(VISIBLE);
                mTvStatesStudent2.setVisibility(GONE);
                mTvStatesStudentName.setText("过道");
                mTvStatesStudent1.setText("删除过道");
                break;
            //回到原视图
            case SeatConstant.ViewType.TYPE_5:
                setStatesView(1);
                break;
            //取消请假视图
            case SeatConstant.ViewType.TYPE_7:
                setStatesView(2);
                mTvStatesStudentName.setVisibility(VISIBLE);
                mTvStatesStudent1.setVisibility(VISIBLE);
                mTvStatesStudent2.setVisibility(GONE);
                mTvStatesStudentName.setText("取消:"+bean.getName() + "  " + bean.getColumn()+ "列" +"/"+bean.getLine()+"行");
                mTvStatesStudent1.setText("取消请假");
                break;
            default:
                break;
        }
        this.viewType = viewType;
    }

    public void setAddClassText(boolean isAdd){
        if (isAdd){
            mTvAddClass.setText("+调课位");
            mTvAddClass.setBackgroundResource(R.drawable.shape_seat_solid_00a5a8_r14);
        } else {
            mTvAddClass.setText("删除调课位");
            mTvAddClass.setBackgroundResource(R.drawable.shape_seat_solid_ff8c8c_r14);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == mTvStartSeat){
            //只显示设置座位可坐和不可坐视图
            //自动排座
            if (mListener!=null){
                mListener.listener(ClickType.CLICK_1);
            }
        } else if (v == mTvAddClass){
            //添加调课位
            if (mListener!=null){
                mListener.listener(ClickType.CLICK_2);
            }
        } else if (v == mTvAddCorridor){
            //添加过道
            if (mListener!=null){
                mListener.listener(ClickType.CLICK_3);
            }
        } else if (v == mTvRestore){
            //恢复自动排座
            if (mListener!=null){
                mListener.listener(ClickType.CLICK_4);
            }
        } else if (v == mTvChange){
            //更改座位布局
            if (mListener!=null){
                mListener.listener(ClickType.CLICK_5);
            }
        } else if (v == mTvStatesClose){
            //收起
            if (mListener!=null){
                mListener.listener(ClickType.CLICK_6);
            }
        } else if (v == mTvStatesStudent1){
            //区分状态
            switch (viewType) {
                //删除学员视图
                case SeatConstant.ViewType.TYPE_1:
                    if (mListener!=null){
                        mListener.listener(ClickType.CLICK_7);
                    }
                    break;
                //调课位，添加调课位学员和标记不可坐视图
                case SeatConstant.ViewType.TYPE_2:
                    if (mListener!=null){
                        //添加调课位学员
                        mListener.listener(ClickType.CLICK_8);
                    }
                    break;
                //标记请假【和取消请假】视图
                case SeatConstant.ViewType.TYPE_3:
                    if (mListener!=null){
                        //添加调课位学员
                        mListener.listener(ClickType.CLICK_10);
                    }
                    break;
                //删除过道视图
                case SeatConstant.ViewType.TYPE_4:
                    if (mListener!=null){
                        //添加调课位学员
                        mListener.listener(ClickType.CLICK_14);
                    }
                    break;
                //回到原视图
                case SeatConstant.ViewType.TYPE_5:
                    if (mListener!=null){
                        //添加调课位学员
                        mListener.listener(ClickType.CLICK_13);
                    }
                    break;
                //添加调期学员，类似插班生
                case SeatConstant.ViewType.TYPE_6:
                    if (mListener!=null){
                        //添加调课位学员
                        mListener.listener(ClickType.CLICK_12);
                    }
                    break;
                //取消请假视图
                case SeatConstant.ViewType.TYPE_7:
                    if (mListener!=null){
                        //添加调课位学员
                        mListener.listener(ClickType.CLICK_11);
                    }
                    break;
            }
        } else if (v == mTvStatesStudent2){
            //区分状态
            switch (viewType) {
                //调课位，添加调课位学员和标记不可坐视图
                case SeatConstant.ViewType.TYPE_2:
                    if (mListener!=null){
                        //标记不可坐视图
                        mListener.listener(ClickType.CLICK_9);
                    }
                    break;
            }
        }
    }

    private OnClickListener mListener;

    /**
     * 设置弹窗销毁监听
     * @param listener                      listener
     */
    public void setClickListener(OnClickListener listener){
        mListener = listener;
    }
    public interface OnClickListener{
        /**
         * 监听
         */
        void listener(@ClickType int type);
    }

    /**
     * 视图类型
     */
    @Retention(RetentionPolicy.SOURCE)
    public @interface ClickType {
        //只显示设置座位可坐和不可坐视图
        //自动排座
        int CLICK_1 = 1;

        //只显示调课位，过道，恢复视图
        //添加调课位
        int CLICK_2 = 2;
        //添加过道
        int CLICK_3 = 3;
        //恢复自动排座
        int CLICK_4 = 4;
        //更改座位布局
        int CLICK_5 = 5;

        //只显示状态视图：删除学员视图，添加调课位学员和标记不可坐视图，标记请假【和取消请假】视图，删除过道视图
        //收起
        int CLICK_6 = 6;
        //删除学员视图
        int CLICK_7 = 7;

        //添加调课位学员和标记不可坐视图
        //添加调课位学员
        int CLICK_8 = 8;
        //标记不可坐视图
        int CLICK_9 = 9;

        //标记请假
        int CLICK_10 = 10;
        //取消请假
        int CLICK_11 = 11;
        //添加调期学员
        int CLICK_12 = 12;

        //回到原视图
        int CLICK_13 = 13;
        //删除过道视图
        int CLICK_14 = 14;
    }


}
