package com.benbaba.dadpat.host.view;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.annotation.Nullable;
import com.benbaba.dadpat.host.R;
import com.benbaba.dadpat.host.model.PluginBean;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * 底部View得线性布局
 */
public class BottomLinearShaderView extends LinearLayout {
    ImageView mShader01;
    ImageView mShader02;
    ImageView mShader03;
    ImageView mShader04;
    private Bitmap[] mBitmaps = new Bitmap[4];
    private Map<Integer, WeakReference<Bitmap>> mBitmapMaps;// bitmap得集合

    public BottomLinearShaderView(Context context) {
        super(context);
        init();
    }

    public BottomLinearShaderView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BottomLinearShaderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        View view = inflate(getContext(), R.layout.view_bottom_linear_shader, this);
        mShader01 = view.findViewById(R.id.id_bottom_shader_01);
        mShader02 = view.findViewById(R.id.id_bottom_shader_02);
        mShader03 = view.findViewById(R.id.id_bottom_shader_03);
        mShader04 = view.findViewById(R.id.id_bottom_shader_04);
        mBitmapMaps = new WeakHashMap<>();
    }


    /**
     * 添加倒影，原理，先翻转图片，由上到下放大透明度
     */
    public Bitmap createShaderBitmap(Bitmap originalImage) {
        final int reflectionGap = 4;
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        Matrix matrix = new Matrix();
        matrix.preScale(1, -1);
        Bitmap reflectionImage_ = Bitmap.createBitmap(originalImage, 0,
                0, width, height, matrix, false);
        Bitmap reflectionImage = Bitmap.createBitmap(reflectionImage_, 0,
                height / 4, width, (height * 3) / 4, null, false);
        Canvas canvas = new Canvas(reflectionImage);
        canvas.drawBitmap(reflectionImage, 0, 0, null);
        Paint paint = new Paint();
        LinearGradient shader = new LinearGradient(0,
                0, 0, reflectionImage.getHeight()
                + reflectionGap, 0x64ffffff, 0x00ffffff, Shader.TileMode.CLAMP);
        paint.setShader(shader);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        canvas.drawRect(0, 0, width, reflectionImage.getHeight()
                + reflectionGap, paint);
        return reflectionImage;
    }

    /**
     * 设置显示得4个底部shader
     *
     * @param list
     */
    public void setShaderView(List<PluginBean> list) {
        for (int i = 0; i < list.size(); i++) {
            PluginBean pluginBean = list.get(i);
            int res = pluginBean.getImgRes();
            if (res == 0)
                continue;
            WeakReference<Bitmap> weakReference = mBitmapMaps.get(res);
            Bitmap bitmap;
            if (weakReference != null) {
                bitmap = weakReference.get();
                if (bitmap == null) {
                    bitmap = createShaderBitmap(BitmapFactory.decodeResource(getResources(), res));
                    mBitmapMaps.put(res, new WeakReference<>(bitmap));
                }
            } else {
                bitmap = createShaderBitmap(BitmapFactory.decodeResource(getResources(), res));
                mBitmapMaps.put(res, new WeakReference<>(bitmap));
            }
            mBitmaps[i] = bitmap;
        }

        for (int i = 0; i < mBitmaps.length; i++) {
            Bitmap bitmap = mBitmaps[i];
            switch (i) {
                case 0:
                    mShader01.setImageBitmap(bitmap);
                    break;
                case 1:
                    mShader02.setImageBitmap(bitmap);
                    break;
                case 2:
                    mShader03.setImageBitmap(bitmap);
                    break;
                case 3:
                    mShader04.setImageBitmap(bitmap);
                    break;
            }
        }
    }

}
