package com.yc.ycseatview.view;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yc.ycseatview.R;
import com.yc.ycseatview.utils.NumberUtils;

/**
 * <pre>
 *     @author  yangchong
 *     email  : yangchong211@163.com
 *     time   : 2020/07/14
 *     desc   : 座位设置行和列页面
 *     revise:
 * </pre>
 */
public class SeatSettingActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView mIvBack;
    private TextView mTvTitle;
    private TextView mTvTotalNum;
    private LinearLayout mLlColumn;
    private TextView mTvColumnNum;
    private LinearLayout mLlLine;
    private TextView mTvLineNum;
    private TextView mTvSure;
    private int type;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat_setting);
        type = getIntent().getIntExtra("type",1);
        //横屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        findViewById();
        setListener();
        changeSureButtonStates();
        mTvTitle.setText("调课位demo");
    }


    private void findViewById() {
        mIvBack = findViewById(R.id.iv_back);
        mTvTitle = findViewById(R.id.tv_title);
        mTvTotalNum = findViewById(R.id.tv_total_num);
        mLlColumn = findViewById(R.id.ll_column);
        mTvColumnNum = findViewById(R.id.tv_column_num);
        mLlLine = findViewById(R.id.ll_line);
        mTvLineNum = findViewById(R.id.tv_line_num);
        mTvSure = findViewById(R.id.tv_sure);
    }

    private void setListener() {
        mIvBack.setOnClickListener(this);
        mLlColumn.setOnClickListener(this);
        mLlLine.setOnClickListener(this);
        mTvSure.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == mIvBack){
            finish();
        } else if (v == mLlColumn){
            //选择列数
            showSelectDialog(1);
        } else if (v == mLlLine){
            //选择行数
            showSelectDialog(2);
        } else if (v == mTvSure){
            //确定
            sureCreateSeat();
        }
    }

    private void showSelectDialog(int data) {
        SelectSeatDialog selectSeatDialog = new SelectSeatDialog(this);
        selectSeatDialog.setDataBean(data);
        selectSeatDialog.setFinishListener(new SelectSeatDialog.onFinishListener() {
            @Override
            public void listener(int type, String select) {
                if (type==1){
                    if (select!=null){
                        mTvColumnNum.setText(select);
                    }
                } else {
                    if (select!=null){
                        mTvLineNum.setText(select);
                    }
                }
                changeSureButtonStates();
            }
        });
        selectSeatDialog.show();
    }

    private void changeSureButtonStates() {
        CharSequence textColumn = mTvColumnNum.getText();
        CharSequence textLine = mTvLineNum.getText();
        if (textColumn==null || textColumn.length()==0 || textColumn.equals("0")){
            mTvSure.setBackgroundResource(R.drawable.shape_rect_stroke_cecece_r20);
            return;
        }
        if (textLine==null || textLine.length()==0 || textLine.equals("0")){
            mTvSure.setBackgroundResource(R.drawable.shape_rect_stroke_cecece_r20);
            return;
        }
        int column = NumberUtils.parse(textColumn.toString());
        int line = NumberUtils.parse(textLine.toString());
        if (column>0 && line>0){
            mTvSure.setBackgroundResource(R.drawable.shape_rect_stroke_00a5a8_r20);
        }
    }


    /**
     * 点击确定创建座位布局
     */
    private void sureCreateSeat() {
        CharSequence textColumn = mTvColumnNum.getText();
        CharSequence textLine = mTvLineNum.getText();
        if (textColumn==null || textColumn.length()==0 || textColumn.equals("0")){
            Toast.makeText(this,"请选择列数",Toast.LENGTH_SHORT).show();
            return;
        }
        if (textLine==null || textLine.length()==0 || textLine.equals("0")){
            Toast.makeText(this,"请选择行数",Toast.LENGTH_SHORT).show();
            return;
        }
        int column = NumberUtils.parse(textColumn.toString());
        int line = NumberUtils.parse(textLine.toString());
        if (type==1){
            //Vertical方向recyclerView
            Intent intent = new Intent(this, SeatInfoVerticalActivity.class);
            intent.putExtra("column",column);
            intent.putExtra("line",line);
            this.startActivity(intent);
        } else if (type == 2){
            //Horizontal方向recyclerView
            Intent intent = new Intent(this, SeatInfoHorizontalActivity.class);
            intent.putExtra("column",column);
            intent.putExtra("line",line);
            this.startActivity(intent);
        } else if (type == 3){
            //Horizontal方向recyclerView
            Intent intent = new Intent(this, SeatInfoHorizontalActivity2.class);
            intent.putExtra("column",column);
            intent.putExtra("line",line);
            this.startActivity(intent);
        }
    }


}
