package com.benbaba.dadpat.host.ui

import android.content.Intent
import android.text.TextUtils
import androidx.lifecycle.Observer
import com.benbaba.dadpat.host.App
import com.benbaba.dadpat.host.R
import com.benbaba.dadpat.host.base.BaseDataActivity
import com.benbaba.dadpat.host.config.Constants
import com.benbaba.dadpat.host.model.TokenBean
import com.benbaba.dadpat.host.utils.SPUtils
import com.benbaba.dadpat.host.utils.clickWithTrigger
import com.benbaba.dadpat.host.utils.startActivity
import com.benbaba.dadpat.host.vm.PhoneLoginViewModel
import com.bhx.common.utils.AppManager
import com.bhx.common.utils.ToastUtils
import kotlinx.android.synthetic.main.activity_phone_login.*

/**
 * 账号登陆
 */
class PhoneLoginActivity : BaseDataActivity<PhoneLoginViewModel>() {

    override fun getEventKey(): Any {
        return Constants.EVENT_KEY_PHONE_LOGIN
    }


    override fun getLayoutId(): Int {
        return R.layout.activity_phone_login
    }


    override fun initView() {
        super.initView()
        //返回键
        loginBack.clickWithTrigger {
            this@PhoneLoginActivity.finish()
        }
        //注册点击
        phoneRegister.clickWithTrigger {
            startActivity<RegisterActivity>()
        }
        //忘记密码点击
        forgetPsd.clickWithTrigger {
            startActivity<ForgetPasswordActivity>()
        }
        //点击登陆
        loginBtn.clickWithTrigger {
            val phone = loginPhone.text.trim().toString()
            val psd = loginPsd.text.trim().toString()
            if (TextUtils.isEmpty(phone)) {
                ToastUtils.toastShort("手机号不能为空")
                return@clickWithTrigger
            }
            if (TextUtils.isEmpty(psd)) {
                ToastUtils.toastShort("请输入密码")
                return@clickWithTrigger
            }
            //登陆
            mViewModel.login(phone, psd)
        }
    }

    override fun dataObserver() {
        registerObserver(Constants.TAG_PHONE_LOGIN_RESULT, TokenBean::class.java).observe(
            this, Observer {
                if (it != null) {
                    App.instance().setToken(it.token)
                    SPUtils.put(this@PhoneLoginActivity.applicationContext, Constants.SP_LOGIN_TYPE, "phone")
                    SPUtils.put(this@PhoneLoginActivity.applicationContext, Constants.SP_LOGIN, true)
                    val intent = Intent(this@PhoneLoginActivity, MainActivity::class.java)
                    intent.putExtra("User", it.user)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    this@PhoneLoginActivity.finish()
                }
            })
    }


}
