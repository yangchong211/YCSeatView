package com.yc.ycseatview;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import io.flutter.view.FlutterView;


public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        FrameLayout content = findViewById(R.id.content);

//        setContentView(R.layout.activity_main);
//        FragmentTransaction ft= getSupportFragmentManager().beginTransaction();
//        ft.replace(R.id.content, Flutter.createFragment("Hello Flutter"));
//        ft.commit();

        // 通过FlutterView引入Flutter编写的页面
        FlutterView flutterView = new FlutterView(this);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        FrameLayout flContainer = findViewById(R.id.content);
        flContainer.addView(flutterView, lp);
        // 关键代码，将Flutter页面显示到FlutterView中
//        FlutterEngine flutterEngine = new FlutterEngine(this);
//        flutterView.attachToFlutterEngine(flutterEngine);
    }
}
