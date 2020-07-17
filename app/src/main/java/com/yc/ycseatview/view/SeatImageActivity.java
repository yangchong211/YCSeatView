package com.yc.ycseatview.view;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.github.chrisbanes.photoview.PhotoView;
import com.yc.ycseatview.R;

/**
 * <pre>
 *     @author  yangchong
 *     email  : yangchong211@163.com
 *     time   : 2020/07/14
 *     desc   : 图片预览页面
 *     revise :
 * </pre>
 */
public class SeatImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        if (getIntent()!=null && getIntent().getIntExtra("type",1)==1){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        PhotoView imageView = findViewById(R.id.iv_image);
        Bitmap bitmap = ModelStorage.getInstance().getBitmap();
        BitmapDrawable drawable = new BitmapDrawable(this.getResources(), bitmap);
        imageView.setImageDrawable(drawable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ModelStorage.getInstance().setBitmap(null);
    }
}
