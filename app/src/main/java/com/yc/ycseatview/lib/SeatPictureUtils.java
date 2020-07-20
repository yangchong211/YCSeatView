package com.yc.ycseatview.lib;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.LruCache;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * <pre>
 *     @author yangchong
 *     email  : yangchong211@163.com
 *     time   : 2020/07/15
 *     desc   : 图片截图生成工具
 * </pre>
 */
public final class SeatPictureUtils {


    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 截屏，截取activity的可见区域的视图
     * @param activity              上下文
     * @return                      bitmap图片
     */
    public static Bitmap activityShot(Activity activity) {
        /*获取windows中最顶层的view*/
        View view = activity.getWindow().getDecorView();
        //允许当前窗口保存缓存信息
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        //获取状态栏高度
        Rect rect = new Rect();
        view.getWindowVisibleDisplayFrame(rect);
        int statusBarHeight = rect.top;
        WindowManager windowManager = activity.getWindowManager();
        //获取屏幕宽和高
        DisplayMetrics outMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(outMetrics);
        int width = outMetrics.widthPixels;
        int height = outMetrics.heightPixels;
        //获取view的缓存位图
        Bitmap drawingCache = view.getDrawingCache();
        //去掉状态栏
        Bitmap bitmap = Bitmap.createBitmap(drawingCache, 0, statusBarHeight,
                width, height - statusBarHeight);
        //销毁缓存信息
        view.destroyDrawingCache();
        view.setDrawingCacheEnabled(false);
        return bitmap;
    }

    /**
     * 计算view的大小，测量宽高大小。万能工具方法，可以截屏长图
     * @param activity              上下文
     * @param view                  view
     * @return                      返回bitmap
     */
    public static Bitmap measureSize(Activity activity, View view) {
        //将布局转化成view对象
        WindowManager manager = activity.getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        int width = outMetrics.widthPixels;
        int height = outMetrics.heightPixels;
        //然后View和其内部的子View都具有了实际大小，也就是完成了布局，相当与添加到了界面上。
        //接着就可以创建位图并在上面绘制
        return layoutView(view, width, height);
    }

    /**
     * 填充布局内容
     * @param viewBitmap            view
     * @param width                 宽
     * @param height                高
     * @return
     */
    public static  Bitmap layoutView(final View viewBitmap, int width, int height) {
        // 整个View的大小 参数是左上角 和右下角的坐标
        viewBitmap.layout(0, 0, width, height);
        SeatLogUtils.i("layoutView------填充布局内容---1--"+width+"----------"+height);
        //宽，父容器已经检测出view所需的精确大小，这时候view的最终大小SpecSize所指定的值，相当于match_parent或指定具体数值。
        int measuredWidth = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.UNSPECIFIED);
        //高，表示父容器不对View有任何限制，一般用于系统内部，表示一种测量状态；
        int measuredHeight = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.UNSPECIFIED);
        SeatLogUtils.i("layoutView------填充布局内容---2--"+measuredWidth+"----------"+measuredHeight);
        viewBitmap.measure(measuredWidth, measuredHeight);
        int measuredWidth1 = viewBitmap.getMeasuredWidth();
        int measuredHeight1 = viewBitmap.getMeasuredHeight();
        SeatLogUtils.i("layoutView------填充布局内容---3--"+measuredWidth1+"----------"+measuredHeight1);
        viewBitmap.layout(0, 0, viewBitmap.getMeasuredWidth(), viewBitmap.getMeasuredHeight());
        viewBitmap.setDrawingCacheEnabled(true);
        viewBitmap.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        viewBitmap.setDrawingCacheBackgroundColor(Color.WHITE);
        // 把一个View转换成图片
        Bitmap cachebmp = viewConversionBitmap(viewBitmap);
        viewBitmap.destroyDrawingCache();
        return cachebmp;
    }

    /**
     * view转bitmap，如果是截图控件中有scrollView，那么可以取它的子控件LinearLayout或者RelativeLayout
     * @param v                     view
     * @return                      bitmap对象
     */
    private static Bitmap viewConversionBitmap(View v) {
        int w = v.getWidth();
        int h = 0;
        if (v instanceof LinearLayout){
            LinearLayout linearLayout = (LinearLayout) v;
            for (int i = 0; i < linearLayout.getChildCount(); i++) {
                h += linearLayout.getChildAt(i).getHeight();
            }
        } else if (v instanceof RelativeLayout){
            RelativeLayout relativeLayout = (RelativeLayout) v;
            for (int i = 0; i < relativeLayout.getChildCount(); i++) {
                h += relativeLayout.getChildAt(i).getHeight();
            }
        } else if (v instanceof CoordinatorLayout){
            CoordinatorLayout coordinatorLayout = (CoordinatorLayout) v;
            for (int i = 0; i < coordinatorLayout.getChildCount(); i++) {
                h += coordinatorLayout.getChildAt(i).getHeight();
            }
        } else {
            h = v.getHeight();
        }
        Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);
        //如果不设置canvas画布为白色，则生成透明
        c.drawColor(Color.WHITE);
        v.layout(0, 0, w, h);
        v.draw(c);
        return bmp;
    }


    public static int getRecyclerViewItemHeight(RecyclerView view) {
        RecyclerView.Adapter adapter = view.getAdapter();
        if (adapter != null) {
            int size = adapter.getItemCount();
            int height = 0;
            for (int i = 0; i < size; i++) {
                RecyclerView.ViewHolder holder = adapter.createViewHolder(view, adapter.getItemViewType(i));
                adapter.onBindViewHolder(holder, i);
                //测量
                holder.itemView.measure(
                        View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.AT_MOST),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                height = holder.itemView.getMeasuredHeight();
                return height;
            }
        }
        return 0;
    }

    public static int getRecyclerViewItemWidth(RecyclerView view) {
        RecyclerView.Adapter adapter = view.getAdapter();
        if (adapter != null) {
            int size = adapter.getItemCount();
            int width = 0;
            for (int i = 0; i < size; i++) {
                RecyclerView.ViewHolder holder = adapter.createViewHolder(view, adapter.getItemViewType(i));
                adapter.onBindViewHolder(holder, i);
                //测量
                holder.itemView.measure(
                        View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.UNSPECIFIED),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.AT_MOST));
                width = holder.itemView.getMeasuredWidth();
                return width;
            }
        }
        return 0;
    }

    public static Bitmap shotRecyclerView(int column , RecyclerView view) {
        RecyclerView.Adapter adapter = view.getAdapter();
        Bitmap bigBitmap = null;
        if (adapter != null) {
            int size = adapter.getItemCount();
            int height = 0;
            Paint paint = new Paint();
            int iHeight = 0;
            int iLeft = 0;
            final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
            // Use 1/8th of the available memory for this memory cache.
            final int cacheSize = maxMemory / 8;
            //这个地方用LruCache显得高大上，实际上用个List也是可以的
            LruCache<String, Bitmap> bitmapCache = new LruCache<>(cacheSize);
            for (int i = 0; i < size; i++) {
                RecyclerView.ViewHolder holder = adapter.createViewHolder(view, adapter.getItemViewType(i));
                adapter.onBindViewHolder(holder, i);
                //测量
                holder.itemView.measure(
                        View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.AT_MOST),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                //布局
                holder.itemView.layout(0, 0, holder.itemView.getMeasuredWidth(),
                        holder.itemView.getMeasuredHeight());

                holder.itemView.setDrawingCacheEnabled(true);
                holder.itemView.buildDrawingCache();
                Bitmap drawingCache = holder.itemView.getDrawingCache();
                if (drawingCache != null) {
                    bitmapCache.put(String.valueOf(i), drawingCache);
                }
                if ((i+1)%column==0){
                    //获取itemView的实际高度并累加
                    height += holder.itemView.getMeasuredHeight();
                }
            }
            // 这个地方容易出现OOM，关键是要看截取RecyclerView的展开的宽高
            bigBitmap = Bitmap.createBitmap(view.getMeasuredWidth(), height, Bitmap.Config.ARGB_8888);
            Canvas bigCanvas = new Canvas(bigBitmap);
            Drawable lBackground = view.getBackground();
            //先画RecyclerView的背景色
            if (lBackground instanceof ColorDrawable) {
                ColorDrawable lColorDrawable = (ColorDrawable) lBackground;
                int lColor = lColorDrawable.getColor();
                bigCanvas.drawColor(lColor);
            }
            for (int i = 0; i < size; i++) {
                Bitmap bitmap = bitmapCache.get(String.valueOf(i));
                bigCanvas.drawBitmap(bitmap, iLeft, iHeight, paint);
                if ((i+1)%column==0){
                    iLeft = 0;
                    iHeight += bitmap.getHeight();
                    //iHeight = 0;
                } else {
                    iLeft += bitmap.getWidth();
                    //iHeight += bitmap.getHeight();
                }
                bitmap.recycle();
            }
        }
        return bigBitmap;
    }

}
