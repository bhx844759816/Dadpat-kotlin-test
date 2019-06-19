package com.benbaba.dadpat.host.view

import android.animation.*
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import com.benbaba.dadpat.host.R
import com.bhx.common.utils.DensityUtil
import com.bhx.common.utils.LogUtils
import java.util.jar.Attributes

/**
 * Created by Administrator on 2019/6/3.
 */
class BluetoothSearchView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val mContext = context
    private var mViewSize = 0 // 自定义View的大小
    private var mBleBitmap: Bitmap // 蓝牙的图片
    private var mOutRingMaxWidth = 0 //外环最大半径
    private var mInnerRingMaxWidth = 0 //内环最大半径
    private var mCircleRadius = 0
    private var mBgPaint: Paint
    private var mOutRingPaint: Paint
    private var mInnerRingPaint: Paint
    private var mOutRingRect: RectF
    private var mInnerRingRect: RectF
    private var mCurrentOutRingWidth: Int = 0
    private var mCurrentInnerRingWidth: Int = 0
    private var mAnimator: AnimatorSet? = null


    init {
        //初始化蓝牙背景图
        mBleBitmap = BitmapFactory.decodeResource(mContext.resources, R.drawable.icon_bluetooth_connect)
        mCircleRadius = DensityUtil.dip2px(mContext, 80f)
        mOutRingMaxWidth = DensityUtil.dip2px(mContext, 35f)
        mInnerRingMaxWidth = DensityUtil.dip2px(mContext, 25f)
        mViewSize = (mCircleRadius / 2 + mOutRingMaxWidth + mInnerRingMaxWidth) * 2
        mBgPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mBgPaint.isAntiAlias = true
        mBgPaint.isDither = true
        mBgPaint.color = Color.WHITE
        mBgPaint.style = Paint.Style.FILL
        mOutRingPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mOutRingPaint.isAntiAlias = true
        mOutRingPaint.isDither = true
        mOutRingPaint.color = Color.parseColor("#80ffffff")
        mInnerRingPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mInnerRingPaint.isAntiAlias = true
        mInnerRingPaint.isDither = true
        mInnerRingPaint.color = Color.parseColor("#30ffffff")
        mOutRingRect = RectF()
        mInnerRingRect = RectF()
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(mViewSize, mViewSize)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        //绘制背景
        canvas?.let {
            it.save()
            it.translate(mViewSize / 2f, mViewSize / 2f)
            //绘制外圈的圆环
            mOutRingPaint.strokeWidth = mCurrentOutRingWidth.toFloat()
            mOutRingRect.left = mInnerRingRect.left - mCurrentOutRingWidth
            mOutRingRect.top = mInnerRingRect.top - mCurrentOutRingWidth
            mOutRingRect.bottom = mInnerRingRect.bottom + mCurrentOutRingWidth
            mOutRingRect.right = mInnerRingRect.right + mCurrentOutRingWidth
            it.drawArc(mOutRingRect, 0f, 360f, false, mOutRingPaint)
            //绘制内圈的圆环
            mInnerRingPaint.strokeWidth = mCurrentInnerRingWidth.toFloat()
            mInnerRingRect.left = -(mCircleRadius / 2 + mCurrentInnerRingWidth).toFloat()
            mInnerRingRect.top = -(mCircleRadius / 2 + mCurrentInnerRingWidth).toFloat()
            mInnerRingRect.bottom = (mCircleRadius / 2 + mCurrentInnerRingWidth).toFloat()
            mInnerRingRect.right = (mCircleRadius / 2 + mCurrentInnerRingWidth).toFloat()
            it.drawArc(mInnerRingRect, 0f, 360f, false, mInnerRingPaint)
            //绘制背景和图标
            it.drawCircle(0f, 0f, mCircleRadius / 2f, mBgPaint)
            it.drawBitmap(mBleBitmap, -mBleBitmap.width / 2f, -mBleBitmap.height / 2f, mBgPaint)
            it.restore()
            //

        }
    }

    private fun startAnim() {
        val innerAnim = ObjectAnimator.ofInt(0, mOutRingMaxWidth)
        innerAnim.repeatMode = ValueAnimator.RESTART
        innerAnim.repeatCount = ValueAnimator.INFINITE
        innerAnim.addUpdateListener { animation ->
            this@BluetoothSearchView.mCurrentInnerRingWidth = animation?.animatedValue as Int
            postInvalidate()
        }
        val outAnim = ObjectAnimator.ofInt(0, mInnerRingMaxWidth)
        outAnim.repeatMode = ValueAnimator.RESTART
        outAnim.repeatCount = ValueAnimator.INFINITE
        outAnim.addUpdateListener { animation ->
            this@BluetoothSearchView.mCurrentOutRingWidth = animation?.animatedValue as Int
            postInvalidate()
        }
        mAnimator = AnimatorSet()
        mAnimator!!.duration = 2000
        mAnimator!!.interpolator = LinearInterpolator()
        mAnimator!!.playTogether(innerAnim, outAnim)
        mAnimator!!.start()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        startAnim()
        //开启动画
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mAnimator?.cancel()
    }


}