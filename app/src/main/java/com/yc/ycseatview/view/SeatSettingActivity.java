package com.yc.ycseatview.view;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
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
    private LinearLayout mLlSetTotalNum;
    private TextView mTvConditions;
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
        mLlSetTotalNum = findViewById(R.id.ll_set_total_num);
        mTvConditions = findViewById(R.id.tv_conditions);
    }

    private void setListener() {
        mIvBack.setOnClickListener(this);
        mLlColumn.setOnClickListener(this);
        mLlLine.setOnClickListener(this);
        mTvSure.setOnClickListener(this);
        mLlSetTotalNum.setOnClickListener(this);
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
        } else if (v == mLlSetTotalNum){
            //设置学生总数量
            showSelectDialog(3);
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
                } else if (type == 2){
                    if (select!=null){
                        mTvLineNum.setText(select);
                    }
                } else {
                    if (select!=null){
                        mTvTotalNum.setText(select);
                        mTvTotalNum.setTextColor(Color.RED);
                    }
                }
                changeSureButtonStates();
            }
        });
        selectSeatDialog.show();
    }

    private void changeSureButtonStates() {
        mTvConditions.setVisibility(View.GONE);
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
        int setTotal = column * line;

        CharSequence textTotal = mTvTotalNum.getText();
        if (textTotal!=null && textTotal.length()>0){
            int total = NumberUtils.parse(textTotal.toString());
            if (setTotal>total || total==0 || setTotal==0){
                mTvConditions.setVisibility(View.GONE);
            } else {
                mTvConditions.setVisibility(View.VISIBLE);
            }
        } else {
            mTvConditions.setVisibility(View.GONE);
        }
    }


    /**
     * 点击确定创建座位布局
     * 列数/行数皆已设置&座位数量>该班级学员数
     */
    private void sureCreateSeat() {
        CharSequence textColumn = mTvColumnNum.getText();
        CharSequence textLine = mTvLineNum.getText();
        CharSequence textTotal = mTvTotalNum.getText();
        if (textTotal==null || textTotal.length()==0 || textTotal.equals("0") || textTotal.equals("请选择")){
            Toast.makeText(this,"请设置学生总数",Toast.LENGTH_SHORT).show();
            return;
        }
        if (textColumn==null || textColumn.length()==0 || textColumn.equals("0")|| textColumn.equals("请选择")){
            Toast.makeText(this,"请选择列数",Toast.LENGTH_SHORT).show();
            return;
        }
        if (textLine==null || textLine.length()==0 || textLine.equals("0")|| textLine.equals("请选择")){
            Toast.makeText(this,"请选择行数",Toast.LENGTH_SHORT).show();
            return;
        }
        int total = NumberUtils.parse(textTotal.toString());
        int column = NumberUtils.parse(textColumn.toString());
        int line = NumberUtils.parse(textLine.toString());
        if (column<1 || line<1){
            Toast.makeText(this,"行数和列数都要大于1",Toast.LENGTH_SHORT).show();
            return;
        }
        if (total > (column*line)){
            Toast.makeText(this,"设置座位数必须大于学生总数",Toast.LENGTH_SHORT).show();
            return;
        }
        //Horizontal方向recyclerView
        Intent intent = new Intent(this, SeatInfoHorizontalActivity.class);
        intent.putExtra("column",column);
        intent.putExtra("line",line);
        intent.putExtra("total",total);
        this.startActivity(intent);
    }


}
