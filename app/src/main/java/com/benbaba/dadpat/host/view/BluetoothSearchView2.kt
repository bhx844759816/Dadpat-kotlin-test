package com.benbaba.dadpat.host.view

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.benbaba.dadpat.host.R
import com.bhx.common.utils.DensityUtil
import com.bhx.common.utils.LogUtils

/**
 * Created by Administrator on 2019/6/4.
 */
class BluetoothSearchView2 @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val mBgBitmap: Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.icon_bluetooth_search)
    private val mViewWidth: Int
    private val mViewHeight: Int
    private val mBgPaint: Paint
    private val mDevicePaint: Paint
    private val mTextPaint: Paint
    private val mRectWidth: Int
    private val mRectHeight: Int
    private var mDeviceList: MutableList<Device> = mutableListOf()
    private var textOffset: Float
    var callback: ((Device) -> Unit)? = null

    init {
        mViewWidth = mBgBitmap.width + DensityUtil.dip2px(context, 20f)
        mViewHeight = mBgBitmap.height + DensityUtil.dip2px(context, 20f)
        mRectWidth = DensityUtil.dip2px(context, 80f)
        mRectHeight = DensityUtil.dip2px(context, 30f)
        mBgPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mBgPaint.isDither = true
        mBgPaint.isAntiAlias = true
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
        mTextPaint.color = Color.BLACK
        val fontMetrics = Paint.FontMetrics()
        mTextPaint.getFontMetrics(fontMetrics)
        textOffset = (fontMetrics.descent + fontMetrics.ascent) / 2
//        randomDevice()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(mViewWidth, mViewHeight)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
            it.drawBitmap(
                mBgBitmap,
                (mViewWidth / 2 - mBgBitmap.width / 2).toFloat(),
                (mViewHeight / 2 - mBgBitmap.height / 2).toFloat(),
                mBgPaint
            )
            mDeviceList.forEach { device ->
                if (device.rect != null) {
                    it.drawRoundRect(device.rect!!, 15f, 15f, mDevicePaint)
                    it.drawText(
                        device.deviceName!!,
                        device.rect!!.centerX(),
                        device.rect!!.centerY() - textOffset,
                        mTextPaint
                    )
                }
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                for (device in mDeviceList) {
                    if (device.rect != null) {
                        if (device.rect!!.contains(event.x, event.y)) {
                            callback?.invoke(device)
                            break
                        }
                    }
                }
            }
        }
        return super.onTouchEvent(event)
    }

    /**
     * 随机生成设备的位置
     */
    fun randomDevice(bluetoothDevice: BluetoothDevice) {
        val device = Device()
        device.device = bluetoothDevice
        device.deviceName = bluetoothDevice.name
        val rectF = RectF()
        rectF.left = (Math.random() * (mViewWidth - mRectWidth)).toFloat()
        rectF.top = (Math.random() * (mViewHeight - mRectHeight)).toFloat()
        rectF.right = rectF.left + mRectWidth
        rectF.bottom = rectF.top + mRectHeight
        device.rect = rectF
        mDeviceList.add(device)
        postInvalidate()
    }

    private fun randomDevice() {
        val device = Device()
        val rectF = RectF()
        rectF.left = (Math.random() * (mViewWidth - mRectWidth)).toFloat()
        rectF.top = (Math.random() * (mViewHeight - mRectHeight)).toFloat()
        rectF.right = rectF.left + mRectWidth
        rectF.bottom = rectF.top + mRectHeight
        device.rect = rectF
        mDeviceList.add(device)
    }

    fun clearDevice() {
        mDeviceList.clear()
        postInvalidate()
    }

    inner class Device {
        var deviceName: String? = null
        var rect: RectF? = null
        var device: BluetoothDevice? = null
    }

}