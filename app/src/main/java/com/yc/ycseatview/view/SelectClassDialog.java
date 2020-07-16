package com.yc.ycseatview.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.yc.ycseatview.R;
import com.yc.ycseatview.pickerview.WheelView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *     @author  yangchong
 *     email  : yangchong211@163.com
 *     time   : 2020/07/14
 *     desc   : 选择调课位弹窗
 *     revise:
 * </pre>
 */
public class SelectClassDialog extends Dialog {

    private static final String TAG = "SelectClassDialog";
    private Context context;
    private Config mConfig;
    private TextView mTvCancel;
    private TextView mTvContent;
    private TextView mTvSure;
    private WheelView<String> mWheelView;
    private String select = "";
    private int type;

    public SelectClassDialog(@NonNull Context context) {
        super(context, R.style.customDialog);
        this.context = context;
        this.mConfig = new Config();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_seat_setting);
        initFindViewById();
        initListener();
        setCancelable(mConfig.canCancel);
        setCanceledOnTouchOutside(mConfig.canCancelOnTouchOutside);
        setData();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (context instanceof Activity){
            //setBackgroundAlpha((Activity) context,1.0f);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (context instanceof Activity){
            //setBackgroundAlpha((Activity) context,0.3f);
        }
        Window window = this.getWindow();
        if (window!=null){
            //计算屏幕宽高
            window.setGravity(Gravity.BOTTOM | Gravity.CENTER);
            //window.setWindowAnimations(R.style.MyDialog);
            window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    private void initFindViewById() {
        mTvCancel = findViewById(R.id.tv_cancel);
        mTvContent = findViewById(R.id.tv_content);
        mTvSure = findViewById(R.id.tv_sure);
        mWheelView = findViewById(R.id.wheel_view);
    }

    private void initListener() {
        mTvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (mListener!=null){
                    mListener.listener(Type.TYPE_4,null);
                }
            }
        });
        mTvSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (mListener!=null){
                    mListener.listener(type,select);
                }
            }
        });
    }

    private void setData() {
        List<String> list = new ArrayList<>();
        list.add("左侧增加一列");
        list.add("右侧增加一列");
        list.add("后方增加一列");
        mTvContent.setText("新增调课位");
        mWheelView.setOnItemSelectedListener(new WheelView.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(WheelView<String> wheelView, String data, int position) {
                Log.i(TAG, "onItemSelected: data=" + data + ",position=" + position);
                mTvContent.setText(data);
                select = data;
                switch (position){
                    case 0:
                        type = Type.TYPE_1;
                        break;
                    case 1:
                        type = Type.TYPE_2;
                        break;
                    case 2:
                    default:
                        type = Type.TYPE_3;
                        break;
                }
            }
        });
        mWheelView.setOnWheelChangedListener(new WheelView.OnWheelChangedListener() {
            @Override
            public void onWheelScroll(int scrollOffsetY) {
                Log.d(TAG, "onWheelScroll: scrollOffsetY=" + scrollOffsetY);
            }

            @Override
            public void onWheelItemChanged(int oldPosition, int newPosition) {
                Log.i(TAG, "onWheelItemChanged: oldPosition=" + oldPosition + ",newPosition=" + newPosition);
            }

            @Override
            public void onWheelSelected(int position) {
                Log.d(TAG, "onWheelSelected: position=" + position);
            }

            @Override
            public void onWheelScrollStateChanged(int state) {
                Log.i(TAG, "onWheelScrollStateChanged: state=" + state);
            }
        });
        mWheelView.setData(list);
    }

    /**
     * 点击外部是否可以取消
     */
    public SelectClassDialog shouldCancelOnTouchOutside(boolean flag) {
        mConfig.canCancelOnTouchOutside = flag;
        return this;
    }

    /**
     * 点击返回键是否可以取消
     */
    public SelectClassDialog shouldCancelOnBackKeyDown(boolean flag) {
        mConfig.canCancel = flag;
        return this;
    }

    /**
     * 配置类
     */
    private static class Config {
        //是否可以取消
        boolean canCancel = true;
        //点击外部是否可以取消
        boolean canCancelOnTouchOutside = true;
    }

    @Override
    public void show() {
        if (context instanceof Activity) {
            if (((Activity) context).isFinishing()) {
                return;
            }
        }
        if (isShowing()){
            return;
        }
        super.show();
    }

    /**
     * 设置页面的透明度
     * 主要作用于：弹窗时设置宿主Activity的背景色
     * @param bgAlpha 1表示不透明
     */
    public static void setBackgroundAlpha(Activity activity , float bgAlpha) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.alpha = bgAlpha;
        Window window = activity.getWindow();
        if(window!=null){
            if (bgAlpha == 1) {
                //不移除该Flag的话,在有视频的页面上的视频会出现黑屏的bug
                window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            } else {
                //此行代码主要是解决在华为手机上半透明效果无效的bug
                window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            }
            window.setAttributes(lp);
        }
    }

    private onFinishListener mListener;

    /**
     * 设置弹窗销毁监听
     * @param listener                      listener
     */
    public void setFinishListener(onFinishListener listener){
        mListener = listener;
    }
    public interface onFinishListener{
        /**
         * 监听
         */
        void listener(int type, String select);
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {
        //左侧增加一列
        int TYPE_1 = 1;
        //右侧增加一列
        int TYPE_2 = 2;
        //后方增加一列
        int TYPE_3 = 3;
        //取消
        int TYPE_4 = 4;
    }

}
