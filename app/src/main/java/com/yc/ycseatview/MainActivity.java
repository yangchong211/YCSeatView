package com.yc.ycseatview;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.yc.ycseatview.view.RecyclerViewActivity;
import com.yc.ycseatview.view.ScrollRecyclerViewActivity;
import com.yc.ycseatview.view.SeatSettingActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.tv_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Vertical方向recyclerView
                Intent intent = new Intent(MainActivity.this, SeatSettingActivity.class);
                intent.putExtra("type",1);
                startActivity(intent);
            }
        });
        findViewById(R.id.tv_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Horizontal方向recyclerView
                Intent intent = new Intent(MainActivity.this, SeatSettingActivity.class);
                intent.putExtra("type",2);
                startActivity(intent);
            }
        });
        findViewById(R.id.tv_3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RecyclerViewActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.tv_4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ScrollRecyclerViewActivity.class);
                startActivity(intent);
            }
        });
    }
}