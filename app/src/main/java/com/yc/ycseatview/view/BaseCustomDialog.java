package com.yc.ycseatview.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.yc.ycseatview.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * <pre>
 *     @author  yangchong
 *     email  : yangchong211@163.com
 *     time   : 2020/07/14
 *     desc   : 返回保存弹窗
 *     revise:
 * </pre>
 */
public class BaseCustomDialog extends Dialog {

    private Context context;
    private Config mConfig;
    private TextView mTvNoSave;
    private TextView mTvSave;
    private TextView mTvTitle;
    private int type;
    private TextView mTvContent;

    public BaseCustomDialog(@NonNull Context context) {
        super(context, R.style.customDialog);
        this.context = context;
        this.mConfig = new Config();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_base_custom);
        initFindViewById();
        initListener();
        setCancelable(mConfig.canCancel);
        setCanceledOnTouchOutside(mConfig.canCancelOnTouchOutside);
        setData(mConfig.type);
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
        mTvTitle = findViewById(R.id.tv_title);
        mTvContent = findViewById(R.id.tv_content);
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

    private void setData(int type) {
        switch (type){
            case BaseCustomDialog.DialogType.DIALOG_1:
                //返回弹窗
                mTvTitle.setText("保存当前更改吗？");
                mTvNoSave.setText("不保存");
                mTvSave.setText("保存");
                mTvContent.setVisibility(View.GONE);
                break;
            case BaseCustomDialog.DialogType.DIALOG_2:
                //恢复自动排位
                mTvTitle.setText("确定恢复到自动排座？");
                mTvNoSave.setText("取消");
                mTvSave.setText("确定");
                mTvContent.setVisibility(View.GONE);
                break;
            case BaseCustomDialog.DialogType.DIALOG_3:
                //更改座位布局
                mTvTitle.setText("确定要更改吗？");
                mTvNoSave.setText("取消");
                mTvSave.setText("确定");
                mTvContent.setVisibility(View.GONE);
                break;
            case BaseCustomDialog.DialogType.DIALOG_4:
                //更改座位布局
                mTvTitle.setText("确定要删除吗？");
                mTvNoSave.setText("取消");
                mTvSave.setText("确定");
                mTvContent.setVisibility(View.VISIBLE);
                break;
        }
    }

    /**
     * 点击外部是否可以取消
     */
    public BaseCustomDialog shouldCancelOnTouchOutside(boolean flag) {
        mConfig.canCancelOnTouchOutside = flag;
        return this;
    }

    /**
     * 点击返回键是否可以取消
     */
    public BaseCustomDialog shouldCancelOnBackKeyDown(boolean flag) {
        mConfig.canCancel = flag;
        return this;
    }


    /**
     * 设置数据
     */
    public BaseCustomDialog setDataBean(@BaseCustomDialog.DialogType int type) {
        mConfig.type = type;
        return this;
    }

    /**
     * 配置类
     */
    private static class Config {
        int type;
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

    /**
     * 视图类型
     */
    @Retention(RetentionPolicy.SOURCE)
    public @interface DialogType {
        //确定保存
        int DIALOG_1 = 1;
        //恢复自动排座
        int DIALOG_2 = 2;
        //更改座位布局
        int DIALOG_3 = 3;
        //删除学员视图
        int DIALOG_4 = 4;
    }


}
