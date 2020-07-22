package com.yc.ycseatview.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yc.ycseatview.R;
import com.yc.ycseatview.lib.OnRestoreListener;
import com.yc.ycseatview.lib.SeatBean;
import com.yc.ycseatview.lib.SeatConstant;
import com.yc.ycseatview.lib.SeatHorizontalView;
import com.yc.ycseatview.lib.SeatHorizontalView2;
import com.yc.ycseatview.lib.SeatLogUtils;
import com.yc.ycseatview.lib.SeatPictureUtils;
import com.yc.ycseatview.lib.SeatStatesView;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * <pre>
 *     @author  yangchong
 *     email  : yangchong211@163.com
 *     time   : 2020/07/14
 *     desc   : 座位设置调动页面
 *     revise : Horizontal方向recyclerView
 * </pre>
 */
public class SeatInfoHorizontalActivity2 extends AppCompatActivity implements View.OnClickListener {

    private ImageView mIvBack;
    private TextView mTvTitle;
    private TextView mTvPicture;
    private TextView mTvCommit;
    private LinearLayout mLlSeatView;
    private NestedScrollView scrollView;
    private SeatHorizontalView2 mSeatView;
    private LinearLayout mLlContentView;
    private SeatStatesView mFlStatesView;
    /**
     * 行数
     */
    private int column;
    /**
     * 列数
     */
    private int line;

    @Override
    protected void onResume() {
        super.onResume();
        mSeatView.setRecyclerViewVisible();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    /**
     * 返回键退回到登陆页面
     **/
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            showSaveDialog();
            return true;
        }
        return false;
    }

    private void showSaveDialog() {
        final BackFinishDialog dialog = new BackFinishDialog(this);
        dialog.shouldCancelOnBackKeyDown(false);
        dialog.shouldCancelOnTouchOutside(false);
        dialog.setClickListener(new BackFinishDialog.OnClickListener() {
            @Override
            public void listener(boolean type) {
                if (type){
                    //确定保存
                    LinkedHashMap<Integer, ArrayList<SeatBean>> allData = mSeatView.getAllData();
                    SeatLogUtils.i("保存后关闭页面"+allData);
                    Toast.makeText(SeatInfoHorizontalActivity2.this,"保存后关闭页面",Toast.LENGTH_SHORT).show();
                } else {
                    //取消
                    dialog.dismiss();
                }
                finish();
            }
        });
        dialog.show();
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat_hor_info2);
        //横屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        findViewById();
        setListener();
        setInitStateView();
        initData();
    }

    private void findViewById() {
        mIvBack = findViewById(R.id.iv_back);
        mTvTitle = findViewById(R.id.tv_title);
        mSeatView = findViewById(R.id.seat_view);
        mTvPicture = findViewById(R.id.tv_picture);
        mTvCommit = findViewById(R.id.tv_commit);
        mLlSeatView = findViewById(R.id.ll_seat_view);
        scrollView = findViewById(R.id.scrollView);
        mLlContentView = findViewById(R.id.ll_content_view);
        mFlStatesView = findViewById(R.id.fl_states_view);
    }

    private void setListener() {
        mIvBack.setOnClickListener(this);
        mTvPicture.setOnClickListener(this);
        mTvCommit.setOnClickListener(this);
        mSeatView.setRestoreListener(new OnRestoreListener() {
            @Override
            public void OnRestore(int type) {
                switch (type){
                    //恢复自动排座
                    case OnRestoreListener.RESTORE:
                        mFlStatesView.setStatesView(1);
                        mFlStatesView.setAddClassText(true);
                        break;
                }
            }
        });
        mSeatView.setClickListener(new SeatHorizontalView2.OnClickListener() {
            @Override
            public void listener(@SeatConstant.ViewType int type,SeatBean bean) {
                switch (type){
                    //删除学员视图
                    case SeatConstant.ViewType.TYPE_1:
                        mFlStatesView.setStatesTypeView(SeatConstant.ViewType.TYPE_1,bean);
                        break;
                    //调课位，添加调课位学员和标记不可坐视图
                    case SeatConstant.ViewType.TYPE_2:
                        mFlStatesView.setStatesTypeView(SeatConstant.ViewType.TYPE_2,bean);
                        break;
                    //标记请假【和取消请假】视图
                    case SeatConstant.ViewType.TYPE_3:
                        mFlStatesView.setStatesTypeView(SeatConstant.ViewType.TYPE_3,bean);
                        break;
                    //删除过道视图
                    case SeatConstant.ViewType.TYPE_4:
                        mFlStatesView.setStatesTypeView(SeatConstant.ViewType.TYPE_4,bean);
                        break;
                    //回到原视图
                    case SeatConstant.ViewType.TYPE_5:
                        mFlStatesView.setStatesTypeView(SeatConstant.ViewType.TYPE_5,bean);
                        break;
                }
            }
        });
        mFlStatesView.setClickListener(new SeatStatesView.OnClickListener() {
            @Override
            public void listener(int type) {
                switch (type){
                    case SeatStatesView.ClickType.CLICK_1:
                        //开始调课
                        setSeatClass();
                        break;
                    case SeatStatesView.ClickType.CLICK_2:
                        //添加调课位
                        addClass();
                        break;
                    case SeatStatesView.ClickType.CLICK_3:
                        //添加过道
                        addCorridor();
                        break;
                    case SeatStatesView.ClickType.CLICK_4:
                        //恢复自动排座
                        restoreSeat();
                        break;
                    case SeatStatesView.ClickType.CLICK_5:
                        //更改座位布局
                        changeSeat();
                        break;
                    case SeatStatesView.ClickType.CLICK_6:
                        //收起
                        mSeatView.closeAllSelect();
                        mFlStatesView.setStatesView(1);
                        break;
                }
            }
        });
    }


    private void setInitStateView() {
        mFlStatesView.setStatesView(0);
        mTvCommit.setVisibility(View.GONE);
        mTvPicture.setVisibility(View.GONE);
    }


    private int height ;
    private int width;
    private void initData() {
        initIntentData();
        initRecyclerView();
        mLlContentView.post(new Runnable() {
            @Override
            public void run() {
                width = mLlContentView.getWidth();
                height = mLlContentView.getHeight();
                SeatLogUtils.i("layoutView---------mLlContentView--"+width+"----------"+height);
            }
        });
        mSeatView.post(new Runnable() {
            @Override
            public void run() {
                int width = mLlContentView.getWidth();
                int height = mLlContentView.getHeight();
                SeatLogUtils.i("layoutView---------mSeatView--"+width+"----------"+height);
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == mIvBack){
            //finish();
            //onBackPressed();
            showSaveDialog();
        } else if (v == mTvCommit){
            //提交数据
        } else if (v == mTvPicture){
            //生成图片
            //Bitmap bitmap = SeatPictureUtils.layoutView(mLlContentView,width,height);
            mSeatView.setPicRecyclerViewVisible();
            mLlSeatView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Bitmap bitmap = SeatPictureUtils.measureSize(SeatInfoHorizontalActivity2.this,mLlContentView);
                    ModelStorage.getInstance().setBitmap(bitmap);
                    Intent intent = new Intent(SeatInfoHorizontalActivity2.this, SeatImageActivity2.class);
                    intent.putExtra("type",1);
                    startActivity(intent);
                }
            },500);
        }
    }

    private void setSeatClass() {
        mSeatView.removeItemListener();
        mFlStatesView.setStatesView(1);
        mTvCommit.setVisibility(View.VISIBLE);
        mTvPicture.setVisibility(View.VISIBLE);
    }

    @SuppressLint("SetTextI18n")
    private void initIntentData() {
        if (getIntent()==null){
            finish();
            return;
        }
        column = getIntent().getIntExtra("column",0);
        line = getIntent().getIntExtra("line",0);
        mTvTitle.setText("Horizontal方向recyclerView----" +column+"列"+line+"行");
    }

    private void initRecyclerView() {
        mSeatView.setColumnAndLine(column,line);
        mSeatView.post(new Runnable() {
            @Override
            public void run() {
                mSeatView.addItemListener();
            }
        });
    }

    /**
     * 添加调课位
     */
    private void addClass() {
        boolean isHaveClass = mSeatView.getIsHaveClass();
        if (isHaveClass){
            //有调课位
            boolean isClassHaveStudent = mSeatView.getIsClassHaveStudent();
            if (isClassHaveStudent){
                //有学生
                DeleteClassDialog deleteClassDialog = new DeleteClassDialog(this);
                deleteClassDialog.setFinishListener(new DeleteClassDialog.onFinishListener() {
                    @Override
                    public void listener() {
                        //关闭弹窗
                    }
                });
                deleteClassDialog.show();
            } else {
                //没有学生
                mSeatView.removeTypeClass();
                mFlStatesView.setAddClassText(true);
            }
        } else {
            //没有调课位
            showAddClassDialog();
        }
    }

    private void showAddClassDialog() {
        SelectClassDialog classDialog = new SelectClassDialog(this);
        classDialog.setFinishListener(new SelectClassDialog.onFinishListener() {
            @Override
            public void listener(@SelectClassDialog.Type int type, String select) {
                switch (type){
                    case SelectClassDialog.Type.TYPE_1:
                        //左侧增加一列
                        addTypeClass(SeatConstant.Type.TYPE_LEFT);
                        mFlStatesView.setAddClassText(false);
                        break;
                    case SelectClassDialog.Type.TYPE_2:
                        //右侧增加一列
                        addTypeClass(SeatConstant.Type.TYPE_RIGHT);
                        mFlStatesView.setAddClassText(false);
                        break;
                    case SelectClassDialog.Type.TYPE_3:
                        //后方增加一列
                        addTypeClass(SeatConstant.Type.TYPE_LAST);
                        mFlStatesView.setAddClassText(false);
                        break;
                    case SelectClassDialog.Type.TYPE_4:
                        Toast.makeText(SeatInfoHorizontalActivity2.this,"取消",Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
        classDialog.show();
    }

    /**
     * 添加调课位
     * @param type                          类型
     */
    private void addTypeClass(@SeatConstant.SeatType int type) {
        //没有调课位
        mSeatView.addTypeClass(type);
    }

    /**
     * 添加过道
     */
    private void addCorridor() {
        //添加过道
        mSeatView.addCorridor();
    }

    /**
     * 恢复自动排座
     */
    private void restoreSeat() {
        mSeatView.restoreSeat();
    }

    /**
     * 更改座位布局
     */
    private void changeSeat() {
        mSeatView.changeSeat();
    }

}
