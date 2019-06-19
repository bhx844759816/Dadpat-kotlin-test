package com.benbaba.dadpat.host.ui

import android.text.TextUtils
import cn.smssdk.EventHandler
import cn.smssdk.SMSSDK
import com.benbaba.dadpat.host.R
import com.benbaba.dadpat.host.base.BaseDataActivity
import com.benbaba.dadpat.host.config.Constants
import com.benbaba.dadpat.host.http.applySchedulers
import com.benbaba.dadpat.host.utils.clickWithTrigger
import com.benbaba.dadpat.host.view.dialog.LoadingDialogFragment
import com.benbaba.dadpat.host.vm.RegisterViewModel
import com.bhx.common.utils.LogUtils
import com.bhx.common.utils.PhoneUtils
import com.bhx.common.utils.ToastUtils
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_register.*
import java.util.concurrent.TimeUnit

/**
 * 注册界面
 */
class RegisterActivity : BaseDataActivity<RegisterViewModel>() {
    private var mCount = 60
    /**
     * 短信Sdk的回调事件
     */
    private val mEventHandler = object : EventHandler() {
        override fun afterEvent(event: Int, result: Int, data: Any) {
            LogUtils.i("event:$event,result:$result,data:$data")
            runOnUiThread {
                LoadingDialogFragment.dismiss(this@RegisterActivity)
                if (result == SMSSDK.RESULT_COMPLETE) {
                    ToastUtils.toastShort("发送验证码成功")
                } else {
                    ToastUtils.toastShort("发送验证码失败")
                }
            }
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_register
    }

    override fun getEventKey(): Any {
        return Constants.EVENT_KEY_REGISTER
    }

    override fun initView() {
        super.initView()
        SMSSDK.registerEventHandler(mEventHandler)
        //返回键
        registerBack.clickWithTrigger {
            this@RegisterActivity.finish()
        }
        //发送验证码
        registerPhoneSend.clickWithTrigger {
            val phone = registerPhone.text.toString().trim()
            if (!PhoneUtils.isMobile(phone)) {
                ToastUtils.toastShort("请输入正确得手机号")
                return@clickWithTrigger
            }
            mCount = 60
            sendPhoneCode(phone)
        }
        //注册
        registerBtn.clickWithTrigger {
            val phone = registerPhone.text.toString().trim()
            val code = registerPhoneCode.text.toString().trim()
            val pwd = registerPwd.text.toString().trim()
            if (!PhoneUtils.isMobile(phone)) {
                ToastUtils.toastShort("请输入正确得手机号")
                return@clickWithTrigger
            }
            if (TextUtils.isEmpty(code)) {
                ToastUtils.toastShort("请输入验证码")
                return@clickWithTrigger
            }
            if (TextUtils.isEmpty(pwd)) {
                ToastUtils.toastShort("请输入密码")
                return@clickWithTrigger
            }
            //调用注册方法
            mViewModel.register(phone, code, pwd)
        }
    }

    /**
     * 发送手机验证码
     */
    private fun sendPhoneCode(phone: String) {
        LoadingDialogFragment.show(this@RegisterActivity)
        addDisposable(Observable.interval(0, 1, TimeUnit.SECONDS)//设置0延迟，每隔一秒发送一条数据
            .take((mCount + 1).toLong()) //
            .compose(applySchedulers())
            .doOnSubscribe {
                if (!it.isDisposed) {
                    SMSSDK.getVerificationCode("86", phone)
                    registerPhoneSend.setTextColor(resources.getColor(R.color.white_42))
                    registerPhoneSend.isEnabled = false//在发送数据的时候设置为不能点击
                }
            }
            .observeOn(AndroidSchedulers.mainThread())//操作UI主要在UI线程
            .subscribe({
                mCount--
                registerPhoneSend.text = mCount.toString()
            }, {
                ToastUtils.toastShort("发送验证码失败")
            }, {
                registerPhoneSend.isEnabled = true
                registerPhoneSend.setTextColor(resources.getColor(R.color.white))
                registerPhoneSend.text = resources.getString(R.string.send_phone_code_retry)//数据发送完后设置为原来的文字
            })
        )
    }

    /**
     * 注册事件监听
     */
    override fun dataObserver() {


    }

}
