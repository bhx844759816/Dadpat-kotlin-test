package com.bhx.common.glide;

import android.content.Context;
import android.media.Image;
import android.widget.ImageView;
import androidx.annotation.DrawableRes;
import com.bumptech.glide.Glide;

import java.io.File;

/**
 * 图片加载的封装
 */
public class ImageLoader {


    /**
     * ImageView设置图片
     *
     * @param context   上下文
     * @param url       图片url
     * @param imageView 显示图片的ImageView
     */
    public static void displayImage(Context context, String url, ImageView imageView) {
        Glide.with(context)
                .load(url)
                .into(imageView);
    }

    public static void displayImage(Context context, File file, ImageView imageView) {
        Glide.with(context)
                .load(file)
                .into(imageView);

    }

    /**
     * ImageView设置图片
     *
     * @param context    上下文
     * @param url        图片url
     * @param imageView  显示图片的ImageView
     * @param loadingRes 加载占位图
     * @param errorRes   加载错误图
     */
    public static void displayImage(Context context, String url, ImageView imageView, @DrawableRes int loadingRes, @DrawableRes int errorRes) {
        Glide.with(context)
                .load(url)
                .into(imageView);
    }
}
