package com.yc.ycseatview.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yc.ycseatview.R;
import com.yc.ycseatview.lib.SeatBean;
import com.yc.ycseatview.lib.SeatConstant;
import com.yc.ycseatview.lib.SeatLogUtils;
import com.yc.ycseatview.lib.SeatPictureUtils;
import com.yc.ycseatview.lib.SeatTypeAdapter;
import com.yc.ycseatview.lib.SpaceViewItemLine;

import java.util.ArrayList;

public class ScrollRecyclerViewActivity extends AppCompatActivity {

    private HorizontalScrollView mScrollView;
    private TextView mTv;
    private RecyclerView mRecyclerView;
    private LinearLayout mLlMain;
    private SeatTypeAdapter seatTypeAdapter;
    /**
     * 集合数据
     */
    private ArrayList<SeatBean> mList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_srcoll_recycler_view);
        mScrollView = findViewById(R.id.scrollView);
        mTv = findViewById(R.id.tv);
        mRecyclerView = findViewById(R.id.recycler_view);
        mLlMain = findViewById(R.id.ll_main);
        mTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //生成图片
                Bitmap bitmap = SeatPictureUtils.measureSize(ScrollRecyclerViewActivity.this, mScrollView);
                //Bitmap bitmap = SeatPictureUtils.shotRecyclerView(5, mRecyclerView);
                ModelStorage.getInstance().setBitmap(bitmap);
                Intent intent = new Intent(ScrollRecyclerViewActivity.this, SeatImageActivity.class);
                intent.putExtra("type",2);
                startActivity(intent);
            }
        });
        initRecyclerView();
    }

    private void initRecyclerView() {
        mList.clear();
        for (int i = 0; i < 150; i++) {
            SeatBean seatBean = new SeatBean();
            seatBean.setType(SeatConstant.SeatType.TYPE_1);
            seatBean.setName("学生" + i);
            mList.add(seatBean);
        }
        GridLayoutManager layoutManager = new GridLayoutManager(this,
                5, RecyclerView.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        if (seatTypeAdapter == null) {
            SpaceViewItemLine itemDecoration = new SpaceViewItemLine(10);
            itemDecoration.setPaddingEdgeSide(true);
            itemDecoration.setPaddingStart(true);
            mRecyclerView.addItemDecoration(itemDecoration);
            seatTypeAdapter = new SeatTypeAdapter(this);
            seatTypeAdapter.setData(mList);
            mRecyclerView.setAdapter(seatTypeAdapter);
        } else {
            //seatTypeAdapter.notifyDataSetChanged();
            seatTypeAdapter.setData(mList);
        }

        //计算
//        mRecyclerView.post(new Runnable() {
//            @Override
//            public void run() {
//                int recyclerViewItemHeight = SeatPictureUtils.getRecyclerViewItemHeight(mRecyclerView);
//                int recyclerViewItemWidth = SeatPictureUtils.getRecyclerViewItemWidth(mRecyclerView);
//                int totalHeight = 30 * recyclerViewItemHeight;
//                int totalWidth = 5 * recyclerViewItemWidth;
//                SeatLogUtils.i("layoutView---------mRecyclerView计算--"+totalWidth+"----------"+totalHeight);
//                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mRecyclerView.getLayoutParams();
////                layoutParams.width = totalWidth+300;
//                layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
//                layoutParams.height = totalHeight + 300;
//                mRecyclerView.setLayoutParams(layoutParams);
//            }
//        });
    }
}
