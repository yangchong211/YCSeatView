package com.yc.ycseatview.lib;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * <pre>
 *     @author yangchong
 *     email  : yangchong211@163.com
 *     time   : 2020/07/22
 *     desc   : 图片压缩工具类
 *     revise :
 * </pre>
 */
public final class SeatCompressUtils {

    /**
     * 获得屏幕宽度
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    /**
     * 获得屏幕高度
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }


    /**
     * 通过大小压缩，将修改图片宽高
     * @param image                                 图片
     * @param pixelW                                宽
     * @param pixelH                                高
     * @return
     */
    /**
     * 通过大小压缩，将修改图片宽高，适合获得缩略图，Used to get thumbnail
     * @param image                                 图片
     * @param pixelW                                宽
     * @param pixelH                                高
     * @return
     */
    public static Bitmap compressBitmapByBmp(Bitmap image, int pixelW, int pixelH) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, os);
        if( os.toByteArray().length / 1024>1024) {
            //判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
            os.reset();
            //重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, 50, os);
            //这里压缩50%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        newOpts.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bitmap = BitmapFactory.decodeStream(is, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        //be=1表示不缩放
        int be = calculateInSampleSize(newOpts, pixelW, pixelH);
        if (be <= 0) {
            be = 1;
        }
        //设置缩放比例
        newOpts.inSampleSize = be;
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        is = new ByteArrayInputStream(os.toByteArray());
        bitmap = BitmapFactory.decodeStream(is, null, newOpts);
        int desWidth = (int) (w / be);
        int desHeight = (int) (h / be);
        bitmap = Bitmap.createScaledBitmap(bitmap, desWidth, desHeight, true);
        //压缩好比例大小后再进行质量压缩
        //return compress(bitmap, maxSize); // 这里再进行质量压缩的意义不大，反而耗资源，删除
        return bitmap;
    }

    /**
     * 根据路径获得突破并压缩返回bitmap用于显示，该方法标记为废弃
     * @param image                                 bitmap
     * @param newWidth                              宽
     * @param newHeight                             高
     * @return
     */
    @Deprecated
    public static Bitmap getSmallBitmap(Bitmap image, int newWidth, int newHeight) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, os);
        if( os.toByteArray().length / 1024>1024) {
            //判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
            os.reset();
            //重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, 50, os);
            //这里压缩50%，把压缩后的数据存放到baos中
        }
        byte[] bytes = os.toByteArray();
        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap decodeFile = BitmapFactory.decodeStream(is,null, options);
        SeatLogUtils.d("SeatCompressUtils----getSmallBitmap---byteCount压缩前大小--"+decodeFile);
        // Calculate inSampleSize
        // 计算图片的缩放值
        options.inSampleSize = calculateInSampleSize(options, newWidth, newHeight);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        is = new ByteArrayInputStream(os.toByteArray());
        Bitmap bitmap = BitmapFactory.decodeStream(is, null, options);
        int bitmapByteCount = bitmap.getByteCount();
        SeatLogUtils.d("SeatCompressUtils----getSmallBitmap---byteCount压缩中大小--"+bitmapByteCount);
        // 质量压缩
        Bitmap newBitmap = compressImage(bitmap, 500);
        int byteCount = newBitmap.getByteCount();
        SeatLogUtils.d("SeatCompressUtils----getSmallBitmap---byteCount压缩后大小--"+byteCount);
        if (bitmap != null){
            // 手动释放资源
            bitmap.recycle();
        }
        return newBitmap;
    }

    /**
     * 计算图片的缩放值
     * @param options                           属性
     * @param reqWidth                          宽
     * @param reqHeight                         高
     * @return
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }


    /**
     * 质量压缩
     * @param image                             bitmap
     * @param maxSize                           最大值
     * @return
     */
    public static Bitmap compressImage(Bitmap image, int maxSize){
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        // scale
        int options = 80;
        // Store the bitmap into output stream(no compress)
        image.compress(Bitmap.CompressFormat.JPEG, options, os);
        // Compress by loop
        while ( os.toByteArray().length / 1024 > maxSize) {
            // Clean up os
            os.reset();
            // interval 10
            options -= 10;
            image.compress(Bitmap.CompressFormat.JPEG, options, os);
        }
        Bitmap bitmap = null;
        byte[] b = os.toByteArray();
        if (b.length != 0) {
            bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
        }
        return bitmap;
    }

}
