package com.yc.ycseatview;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yc.ycseatview.lib.SeatConstant;
import com.yc.ycseatview.lib.SeatRecyclerView;


/**
 * <pre>
 *     @author  yangchong
 *     email  : yangchong211@163.com
 *     time   : 2020/07/14
 *     desc   : 座位设置调动页面
 *     revise:
 * </pre>
 */
public class SeatInfoActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView mIvBack;
    private TextView mTvTitle;
    private LinearLayout mLlClass;
    private TextView mTvAddClass;
    private TextView mTvAddCorridor;
    private TextView mTvRestore;
    private TextView mTvChange;
    private SeatRecyclerView mSeatView;
    /**
     * 行数
     */
    private int column;
    /**
     * 列数
     */
    private int line;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat_info);
        //判断是否是横屏，如果不是则调整为横屏
        /*if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            //横屏
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }*/
        //横屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        findViewById();
        setListener();
        initData();
    }

    private void findViewById() {
        mIvBack = findViewById(R.id.iv_back);
        mTvTitle = findViewById(R.id.tv_title);
        mLlClass = findViewById(R.id.ll_class);
        mTvAddClass = findViewById(R.id.tv_add_class);
        mTvAddCorridor = findViewById(R.id.tv_add_corridor);
        mTvRestore = findViewById(R.id.tv_restore);
        mTvChange = findViewById(R.id.tv_change);
        mSeatView = findViewById(R.id.seat_view);
    }

    private void setListener() {
        mIvBack.setOnClickListener(this);
        mTvAddClass.setOnClickListener(this);
        mTvAddCorridor.setOnClickListener(this);
        mTvRestore.setOnClickListener(this);
        mTvChange.setOnClickListener(this);
    }

    private void initData() {
        initIntentData();
        initRecyclerView();
    }

    @Override
    public void onClick(View v) {
        if (v == mIvBack){
            finish();
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
        }
    }
    @SuppressLint("SetTextI18n")
    private void initIntentData() {
        if (getIntent()==null){
            finish();
            return;
        }
        column = getIntent().getIntExtra("column",0);
        line = getIntent().getIntExtra("line",0);
        mTvTitle.setText(column+"列"+line+"行");
    }

    private void initRecyclerView() {
        mSeatView.setColumnAndLine(column,line);
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
                        mSeatView.addClass(SeatConstant.Type.TYPE_1);
                        break;
                    case SelectClassDialog.Type.TYPE_2:
                        //右侧增加一列
                        mSeatView.addClass(SeatConstant.Type.TYPE_2);
                        break;
                    case SelectClassDialog.Type.TYPE_3:
                        //后方增加一列
                        mSeatView.addClass(SeatConstant.Type.TYPE_3);
                        break;
                    case SelectClassDialog.Type.TYPE_4:
                        Toast.makeText(SeatInfoActivity.this,"取消",Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
        classDialog.show();
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
