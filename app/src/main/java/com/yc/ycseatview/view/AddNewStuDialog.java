package com.yc.ycseatview.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yc.ycseatview.R;
import com.yc.ycseatview.lib.AbsSeatAdapter;
import com.yc.ycseatview.lib.OnItemClickListener;
import com.yc.ycseatview.lib.RecycleViewItemLine;
import com.yc.ycseatview.lib.SeatBean;
import com.yc.ycseatview.lib.SeatTypeAdapter;
import com.yc.ycseatview.lib.SeatViewHolder;

import java.util.ArrayList;

/**
 * <pre>
 *     @author  yangchong
 *     email  : yangchong211@163.com
 *     time   : 2020/07/14
 *     desc   : 添加调课位弹窗
 *     revise:
 * </pre>
 */
public class AddNewStuDialog extends Dialog {

    private Context context;
    private Config mConfig;
    private RelativeLayout mRlDialog;
    private TextView mTvClose;
    private LinearLayout mLlSearch;
    private RecyclerView mRecyclerView;


    public AddNewStuDialog(@NonNull Context context) {
        super(context, R.style.customDialog);
        this.context = context;
        this.mConfig = new Config();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_add_new_student);
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
        mTvClose = findViewById(R.id.tv_close);
        mLlSearch = findViewById(R.id.ll_search);
        mRecyclerView = findViewById(R.id.recycler_view);
    }

    private void initListener() {
        mTvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (mListener!=null){
                    mListener.listener(null);
                }
            }
        });
    }

    private void setData() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        final RecycleViewItemLine line = new RecycleViewItemLine(context,
                LinearLayout.HORIZONTAL, 10, context.getResources().getColor(R.color.white));
        mRecyclerView.addItemDecoration(line);
        NewsStudentAdapter adapter = new NewsStudentAdapter(context);
        mRecyclerView.setAdapter(adapter);
        final ArrayList<String> mList = new ArrayList<>();
        for (int i=0 ; i<6 ; i++){
            mList.add("逗比"+i);
        }
        adapter.setData(mList);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (mList.size()>position && position>=0){
                    String s = mList.get(position);
                    if (mListener!=null){
                        mListener.listener(s);
                    }
                    dismiss();
                }
            }
        });
    }

    /**
     * 点击外部是否可以取消
     */
    public AddNewStuDialog shouldCancelOnTouchOutside(boolean flag) {
        mConfig.canCancelOnTouchOutside = flag;
        return this;
    }

    /**
     * 点击返回键是否可以取消
     */
    public AddNewStuDialog shouldCancelOnBackKeyDown(boolean flag) {
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
        void listener(String name);
    }


    public class NewsStudentAdapter extends AbsSeatAdapter<String>{

        /**
         * 构造方法
         * @param context  上下文
         */
        public NewsStudentAdapter(Context context) {
            super(context, R.layout.item_new_student_view);
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void bindData(SeatViewHolder holder, String s) {
            TextView tv_name = holder.getView(R.id.tv_name);
            tv_name.setText(""+s);
        }
    }

}
