package com.benbaba.dadpat.host.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import androidx.lifecycle.Observer
import cn.smssdk.EventHandler
import cn.smssdk.SMSSDK
import com.benbaba.dadpat.host.R
import com.benbaba.dadpat.host.base.BaseDataActivity
import com.benbaba.dadpat.host.config.Constants
import com.benbaba.dadpat.host.http.applySchedulers
import com.benbaba.dadpat.host.utils.clickWithTrigger
import com.benbaba.dadpat.host.view.dialog.LoadingDialogFragment
import com.benbaba.dadpat.host.vm.ForgetPasswordViewModel
import com.bhx.common.http.ApiException
import com.bhx.common.utils.LogUtils
import com.bhx.common.utils.PhoneUtils
import com.bhx.common.utils.ToastUtils
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_forget_password.*
import kotlinx.android.synthetic.main.activity_register.*
import java.util.concurrent.TimeUnit

class ForgetPasswordActivity : BaseDataActivity<ForgetPasswordViewModel>() {
    private var mCount = 60
    /**
     * 短信Sdk的回调事件
     */
    private val mEventHandler = object : EventHandler() {
        override fun afterEvent(event: Int, result: Int, data: Any) {
            LogUtils.i("event:$event,result:$result,data:$data")
            runOnUiThread {
                LoadingDialogFragment.dismiss(this@ForgetPasswordActivity)
                if (result == SMSSDK.RESULT_COMPLETE) {
                    ToastUtils.toastShort("发送验证码成功")
                } else {
                    ToastUtils.toastShort("发送验证码失败")
                }
            }
        }
    }

    override fun getEventKey(): Any {
        return Constants.EVENT_KEY_FORGET_PASSWORD
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_forget_password
    }

    override fun initView() {
        super.initView()
        SMSSDK.registerEventHandler(mEventHandler)
        backIv.clickWithTrigger {
            this@ForgetPasswordActivity.finish()
        }

        sendCodeTv.clickWithTrigger {
            val phone = phoneEt.text.toString().trim()
            if (!PhoneUtils.isMobile(phone)) {
                ToastUtils.toastShort("请输入正确的手机号")
                return@clickWithTrigger
            }
            sendPhoneCode(phone)
        }

        confirmBtn.clickWithTrigger {
            val phone = phoneEt.text.toString().trim()
            val code = codeEt.text.toString().trim()
            val password = newPasswordEt.text.toString().trim()
            if (!PhoneUtils.isMobile(phone)) {
                ToastUtils.toastShort("请输入正确的手机号")
                return@clickWithTrigger
            }
            if (TextUtils.isEmpty(code)) {
                ToastUtils.toastShort("请输入验证码")
                return@clickWithTrigger
            }
            if (TextUtils.isEmpty(password)) {
                ToastUtils.toastShort("请输入新密码")
                return@clickWithTrigger
            }
            //
            mViewModel.modifyPassword(phone, code, password)
        }
    }

    /**
     * 发送手机验证码
     */
    private fun sendPhoneCode(phone: String) {
        LoadingDialogFragment.show(this@ForgetPasswordActivity)
        addDisposable(Observable.interval(0, 1, TimeUnit.SECONDS)//设置0延迟，每隔一秒发送一条数据
            .take((mCount + 1).toLong()) //
            .compose(applySchedulers())
            .doOnSubscribe {
                if (!it.isDisposed) {
                    SMSSDK.getVerificationCode("86", phone)
                    sendCodeTv.setTextColor(resources.getColor(R.color.white_42))
                    sendCodeTv.isEnabled = false//在发送数据的时候设置为不能点击
                }
            }
            .observeOn(AndroidSchedulers.mainThread())//操作UI主要在UI线程
            .subscribe({
                mCount--
                registerPhoneSend.text = mCount.toString()
            }, {
                ToastUtils.toastShort("发送验证码失败")
            }, {
                sendCodeTv.isEnabled = true
                sendCodeTv.setTextColor(resources.getColor(R.color.white))
                sendCodeTv.text = resources.getString(R.string.send_phone_code_retry)//数据发送完后设置为原来的文字
            })
        )

    }

    /**
     * 注册事件接收者
     */
    override fun dataObserver() {
        // 注册修改密码的结果监听
        registerObserver(
            Constants.EVENT_KEY_FORGET_PASSWORD,
            Constants.TAG_MODIFY_PASSWORD_RESULT,
            Boolean::class.java
        ).observe(
            this, Observer {
                if (it) {
                    this@ForgetPasswordActivity.finish()
                }
            }
        )
    }

}
