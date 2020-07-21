package com.yc.ycseatview.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yc.ycseatview.R;
import com.yc.ycseatview.lib.SeatConstant;
import com.yc.ycseatview.lib.SeatHorizontalView;
import com.yc.ycseatview.lib.SeatHorizontalView2;
import com.yc.ycseatview.lib.SeatLogUtils;
import com.yc.ycseatview.lib.SeatPictureUtils;

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
    private TextView mTvAddClass;
    private TextView mTvAddCorridor;
    private TextView mTvRestore;
    private TextView mTvChange;
    private TextView mTvPicture;
    private TextView mTvCommit;
    private LinearLayout mLlSeatView;
    private NestedScrollView scrollView;
    private SeatHorizontalView2 mSeatView;
    private LinearLayout mLlContentView;
    private RelativeLayout mRlSetClass;
    private TextView mTvStartSeat;
    private LinearLayout mLlChangeClass;
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
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat_hor_info2);
        //横屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        findViewById();
        setListener();
        initData();
    }

    private void findViewById() {
        mIvBack = findViewById(R.id.iv_back);
        mTvTitle = findViewById(R.id.tv_title);
        mTvAddClass = findViewById(R.id.tv_add_class);
        mTvAddCorridor = findViewById(R.id.tv_add_corridor);
        mTvRestore = findViewById(R.id.tv_restore);
        mTvChange = findViewById(R.id.tv_change);
        mSeatView = findViewById(R.id.seat_view);
        mTvPicture = findViewById(R.id.tv_picture);
        mTvCommit = findViewById(R.id.tv_commit);
        mLlSeatView = findViewById(R.id.ll_seat_view);
        scrollView = findViewById(R.id.scrollView);
        mLlContentView = findViewById(R.id.ll_content_view);
        mRlSetClass = findViewById(R.id.rl_set_class);
        mTvStartSeat = findViewById(R.id.tv_start_seat);
        mLlChangeClass = findViewById(R.id.ll_change_class);

        mRlSetClass.setVisibility(View.VISIBLE);
        mLlChangeClass.setVisibility(View.GONE);
        mTvCommit.setVisibility(View.GONE);
        mTvPicture.setVisibility(View.GONE);
    }

    private void setListener() {
        mIvBack.setOnClickListener(this);
        mTvAddClass.setOnClickListener(this);
        mTvAddCorridor.setOnClickListener(this);
        mTvRestore.setOnClickListener(this);
        mTvChange.setOnClickListener(this);
        mTvPicture.setOnClickListener(this);
        mTvCommit.setOnClickListener(this);
        mTvStartSeat.setOnClickListener(this);
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
            finish();
        } else if (v == mTvStartSeat){
            //开始调课
            setSeatClass();
        } else if (v == mTvAddClass){
            //添加调课位
            addClass();
        } else if (v == mTvAddCorridor){
            //添加过道
            addCorridor();
        } else if (v == mTvRestore){
            //恢复自动排座
            restoreSeat();
        } else if (v == mTvChange){
            //更改座位布局
            changeSeat();
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
        mRlSetClass.setVisibility(View.GONE);
        mLlChangeClass.setVisibility(View.VISIBLE);
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
        SelectClassDialog classDialog = new SelectClassDialog(this);
        classDialog.setFinishListener(new SelectClassDialog.onFinishListener() {
            @Override
            public void listener(@SelectClassDialog.Type int type, String select) {
                switch (type){
                    case SelectClassDialog.Type.TYPE_1:
                        //左侧增加一列
                        addTypeClass(SeatConstant.Type.TYPE_1);
                        break;
                    case SelectClassDialog.Type.TYPE_2:
                        //右侧增加一列
                        addTypeClass(SeatConstant.Type.TYPE_2);
                        break;
                    case SelectClassDialog.Type.TYPE_3:
                        //后方增加一列
                        addTypeClass(SeatConstant.Type.TYPE_3);
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
        mSeatView.addTypeClass(type);
    }


    /**
     * 添加过道
     */
    private void addCorridor() {
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
