package com.benbaba.dadpat.host.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.net.wifi.ScanResult
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.animation.LinearInterpolator
import com.benbaba.dadpat.host.R
import com.bhx.common.utils.DensityUtil
import com.bhx.common.utils.LogUtils
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

/**
 * Created by Administrator on 2019/6/6.
 */
class SearchBluetoothView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private lateinit var mWifiBitmap: Bitmap
    private var mBitmapPaint: Paint
    private var mCirclePaint: Paint
    private val mTextPaint: Paint
    private var mViewWidth: Int = 0
    private var mViewHeight: Int = 0
    private var mRingNums: Int = 0//圆环得个数
    private var mRingDistance: Int = 0//圆环得间距
    private var mRingWidth: Int = 0//圆环得宽度
    private var mRingAngle: Int = 0//圆环得角度
    private var mRingColor: Int = 0//圆环得角度
    private var mRingList: MutableList<Ring> = mutableListOf()
    private var mDeviceList = mutableListOf<Device<*>>()
    private var mIndex: Int = 0
    private var textOffset: Float
    private val mDevicePaint: Paint
    private val mRectWidth: Int = DensityUtil.dip2px(context, 80f)
    private val mRectHeight: Int = DensityUtil.dip2px(context, 30f)
    private var mDisposable: Disposable? = null
    var callback: ((Any) -> Unit)? = null

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.SearchWifiView, defStyleAttr, 0)
        mRingColor = typedArray.getColor(R.styleable.SearchWifiView_ring_color, DEFAULT_RING_COLOR)
        mRingNums = typedArray.getInteger(R.styleable.SearchWifiView_ring_num, DEFAULT_RING_NUMS)
        val centerDrawableID = typedArray.getResourceId(R.styleable.SearchWifiView_center_drawable, -1)
        if (centerDrawableID != -1) {
            mWifiBitmap = BitmapFactory.decodeResource(resources, centerDrawableID)
        }
        mRingDistance = typedArray.getDimensionPixelSize(
            R.styleable.SearchWifiView_ring_distance,
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, DEFAULT_RING_DISTANCE.toFloat(),
                resources.displayMetrics
            ).toInt()
        )
        mRingWidth = typedArray.getDimensionPixelSize(
            R.styleable.SearchWifiView_ring_width,
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, DEFAULT_RING_WIDTH.toFloat(),
                resources.displayMetrics
            ).toInt()
        )
        mRingAngle = typedArray.getInteger(R.styleable.SearchWifiView_ring_angle, DEFAULT_RING_ANGLE)
        typedArray.recycle()
        mBitmapPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mBitmapPaint.isAntiAlias = true
        mBitmapPaint.isDither = true
        mCirclePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mCirclePaint.isAntiAlias = true
        mCirclePaint.isDither = true
        mCirclePaint.style = Paint.Style.STROKE
        mCirclePaint.strokeCap = Paint.Cap.ROUND
        mCirclePaint.color = mRingColor
        mCirclePaint.strokeWidth = mRingWidth.toFloat()
        mDevicePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mDevicePaint.isDither = true
        mDevicePaint.isAntiAlias = true
        mDevicePaint.style = Paint.Style.FILL
        mDevicePaint.color = Color.WHITE
        mTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mTextPaint.isDither = true
        mTextPaint.isAntiAlias = true
        mTextPaint.textAlign = Paint.Align.CENTER
        mTextPaint.textSize = DensityUtil.dip2px(context, 13f).toFloat()
        mTextPaint.color = Color.parseColor("#63c7fc")
        val fontMetrics = Paint.FontMetrics()
        mTextPaint.getFontMetrics(fontMetrics)
        textOffset = (fontMetrics.descent + fontMetrics.ascent) / 2
        for (i in 0 until mRingNums) {
            val ring = Ring()
            ring.rectF = RectF()
            ring.index = i
            mRingList.add(ring)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        measureView(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(mViewWidth, mViewHeight)
        for (ring in mRingList) {
            val rectLen = mWifiBitmap.width / 2 + mRingDistance * (ring.index + 1)
            ring.rectF!!.left = (mViewWidth / 2 - rectLen).toFloat()
            ring.rectF!!.top = (mViewHeight / 2 - rectLen).toFloat()
            ring.rectF!!.right = (mViewWidth / 2 + rectLen).toFloat()
            ring.rectF!!.bottom = (mViewHeight / 2 + rectLen).toFloat()
            ring.rectLen = rectLen
        }
    }

    /**
     * 测量view得宽高
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    private fun measureView(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        mViewWidth = if (widthMode == MeasureSpec.EXACTLY) {
            widthSize
        } else {
            mWifiBitmap.width + 2 * mRingNums * (mRingWidth + mRingDistance)
        }
        mViewHeight = if (heightMode == MeasureSpec.EXACTLY) {
            heightSize
        } else {
            mViewWidth
        }
    }

    override fun onDraw(canvas: Canvas) {
        canvas.translate(0f, 0f)
        // 画中心得圆
        canvas.drawBitmap(
            mWifiBitmap, (mViewWidth / 2 - mWifiBitmap.width / 2).toFloat(),
            (mViewHeight / 2 - mWifiBitmap.height / 2).toFloat(), mBitmapPaint
        )
        for (i in 0 until mIndex) {
            val ring = mRingList[i]
            drawRightRing(canvas, ring)
            drawLeftRing(canvas, ring)
        }
        canvas.save()
        canvas.translate((mViewWidth / 2).toFloat(), (mViewHeight / 2).toFloat())
        for (device in mDeviceList) {
            val text = if (device.isConnect) {
                "爸爸拍拍√"
            } else {
                "爸爸拍拍"
            }
//            if (device.isConnect) {
//                mDevicePaint.color = Color.RED
//            } else {
//                mDevicePaint.color = Color.WHITE
//            }
            canvas.drawRoundRect(device.rect!!, 15f, 15f, mDevicePaint)
            canvas.drawText(
                text,
                device.rect!!.centerX(),
                device.rect!!.centerY() - textOffset,
                mTextPaint
            )
//DA:D1:23:45:67:89
        }
        super.onDraw(canvas)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_UP -> {
                val x = (event.x.toInt() - mViewWidth / 2).toFloat()
                val y = (event.y.toInt() - mViewHeight / 2).toFloat()
                for (device in mDeviceList) {
                    if (device.rect!!.contains(x, y)) {
                        callback?.invoke(device.t as Any)
                    }
                }
            }
        }

        return true
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        startAnim()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mDisposable?.dispose()
    }

    /**
     * 开启动画
     */
    private fun startAnim() {
        mDisposable = Observable.interval(1, TimeUnit.SECONDS)
            .observeOn(Schedulers.io())
            .subscribe {
                if (mIndex == mRingNums) {
                    mIndex = 0
                } else {
                    mIndex++
                }
                postInvalidate()
            }
    }

    /**
     * 画右边得圆环
     *
     * @param canvas 画布
     * @param ring   圆环对象
     */
    private fun drawRightRing(canvas: Canvas, ring: Ring) {
        canvas.drawArc(ring.rectF, (-mRingAngle / 2).toFloat(), mRingAngle.toFloat(), false, mCirclePaint)
    }

    /**
     * 画左边得圆环
     *
     * @param canvas 画布
     * @param ring   圆环对象
     */
    private fun drawLeftRing(canvas: Canvas, ring: Ring) {
        canvas.drawArc(ring.rectF, (180 - mRingAngle / 2).toFloat(), mRingAngle.toFloat(), false, mCirclePaint)
    }
//
//    /**
//     * 添加显示的设备
//     */
//    fun <T> addDevice(t: T, isConnected: Boolean) {
//        LogUtils.i("isConnect:$isConnected")
//        repeatCounts = 0
//        val device = Device<T>()
//        device.t = t
//        device.isConnect = isConnected
//        device.rect = getDeviceX()
//        mDeviceList.add(device)
//    }

    fun <T> addDevice(t:T, isConnected: Boolean, address: String) {
        synchronized(this){
            mDeviceList.forEach {
                if (it.address == address) {
                    return
                }
            }
            repeatCounts = 0
            val device = Device<T>()
            device.t = t
            device.isConnect = isConnected
            device.address = address
            device.rect = getDeviceX()
            mDeviceList.add(device)
        }
    }

    fun <T> addDeviceList(list: List<T>) {
        mDeviceList.clear()
        list.forEach {
            addDevice(it, false, "")
        }
        postInvalidate()
    }

    var repeatCounts = 0
    private fun getDeviceX(): RectF? {
        var rect: RectF? = null
        var isInsert = false
        while (repeatCounts < 100) {
            repeatCounts++
            LogUtils.i("repeatCounts$repeatCounts")
            rect = getDeviceRect()
            for (device in mDeviceList) {
                if (RectF.intersects(device.rect!!, rect)) {
                    isInsert = true
                    LogUtils.i("isInsert$isInsert")
                    break
                }
            }
            if (!isInsert) {
                break
            }
        }
        return rect
    }

    /**
     * getDeviceRect()
     * 和列表里面进行判断 如果相交重新生成
     * 1.重复上面步骤5次
     * 2.
     *
     *
     *
     *
     */

    /**
     * 获取设备得Device得显示得位置
     *
     * @param wifiBean
     * @return
     */
    private fun getDeviceRect(): RectF {
        val distance = mWifiBitmap.width + Math.random() * (mRingWidth * 3) / 2
        val rect = RectF()
        val radius = (Math.random() * 360).toInt()
        rect.left = (Math.cos(radius * Math.PI / 180) * distance - mRectWidth / 2).toInt().toFloat()
        rect.right = rect.left + mRectWidth
        rect.top = (Math.sin(radius * Math.PI / 180) * distance - mRectWidth / 2).toInt().toFloat()
        rect.bottom = rect.top + mRectHeight
        return rect
    }

    fun clearDevice() {
        mDeviceList.clear()
        postInvalidate()
    }

    fun clearDevice(address: MutableList<String>) {
        synchronized(this){
            val iterator = mDeviceList.iterator()
            while (iterator.hasNext()){
                if(!address.contains(iterator.next().address)){
                    iterator.remove()
                }
            }
            postInvalidate()
        }
    }

    companion object {
        val DEFAULT_RING_COLOR = Color.parseColor("#60FFFFFF")
        const val DEFAULT_RING_NUMS = 3
        const val DEFAULT_RING_DISTANCE = 30
        const val DEFAULT_RING_WIDTH = 5
        const val DEFAULT_RING_ANGLE = 90
    }

    internal inner class Ring {
        // 圆环得矩形框
        var rectF: RectF? = null
        var index: Int = 0
        var rectLen: Int = 0
    }

    internal inner class Device<T> {
        var t: T? = null
        var rect: RectF? = null
        var address: String? = null
        var isConnect: Boolean = false
    }
}