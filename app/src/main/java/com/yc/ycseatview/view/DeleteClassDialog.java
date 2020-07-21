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
import android.widget.RelativeLayout;

import com.yc.ycseatview.R;

/**
 * <pre>
 *     @author  yangchong
 *     email  : yangchong211@163.com
 *     time   : 2020/07/14
 *     desc   : 删除调课位弹窗
 *     revise:
 * </pre>
 */
public class DeleteClassDialog extends Dialog {

    private Context context;
    private Config mConfig;
    private RelativeLayout mRlDialog;


    public DeleteClassDialog(@NonNull Context context) {
        super(context, R.style.customDialog);
        this.context = context;
        this.mConfig = new Config();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_delete_class);
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
        mRlDialog = findViewById(R.id.rl_dialog);
    }

    private void initListener() {
        mRlDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (mListener!=null){
                    mListener.listener();
                }
            }
        });
    }

    private void setData() {

    }

    /**
     * 点击外部是否可以取消
     */
    public DeleteClassDialog shouldCancelOnTouchOutside(boolean flag) {
        mConfig.canCancelOnTouchOutside = flag;
        return this;
    }

    /**
     * 点击返回键是否可以取消
     */
    public DeleteClassDialog shouldCancelOnBackKeyDown(boolean flag) {
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
        void listener();
    }


}
