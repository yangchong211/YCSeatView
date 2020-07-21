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
 *     desc   : 返回保存弹窗
 *     revise:
 * </pre>
 */
public class BackFinishDialog extends Dialog {

    private Context context;
    private Config mConfig;
    private TextView mTvNoSave;
    private TextView mTvSave;


    public BackFinishDialog(@NonNull Context context) {
        super(context, R.style.customDialog);
        this.context = context;
        this.mConfig = new Config();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_back_finish);
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
            window.setGravity(Gravity.CENTER);
            //window.setWindowAnimations(R.style.MyDialog);
            window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    private void initFindViewById() {
        mTvNoSave = findViewById(R.id.tv_no_save);
        mTvSave = findViewById(R.id.tv_save);
    }

    private void initListener() {
        mTvNoSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener!=null){
                    mListener.listener(false);
                }
            }
        });
        mTvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener!=null){
                    mListener.listener(true);
                }
            }
        });
    }

    private void setData() {

    }

    /**
     * 点击外部是否可以取消
     */
    public BackFinishDialog shouldCancelOnTouchOutside(boolean flag) {
        mConfig.canCancelOnTouchOutside = flag;
        return this;
    }

    /**
     * 点击返回键是否可以取消
     */
    public BackFinishDialog shouldCancelOnBackKeyDown(boolean flag) {
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
        void listener(boolean type);
    }


}
