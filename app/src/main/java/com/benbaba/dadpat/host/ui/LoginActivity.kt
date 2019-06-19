package com.benbaba.dadpat.host.ui

import android.content.Intent
import androidx.lifecycle.Observer
import com.benbaba.dadpat.host.App
import com.benbaba.dadpat.host.R
import com.benbaba.dadpat.host.base.BaseDataActivity
import com.benbaba.dadpat.host.config.Constants
import com.benbaba.dadpat.host.model.TokenBean
import com.benbaba.dadpat.host.utils.SPUtils
import com.benbaba.dadpat.host.utils.clickWithTrigger
import com.benbaba.dadpat.host.utils.startActivity
import com.benbaba.dadpat.host.view.dialog.LoadingDialogFragment
import com.benbaba.dadpat.host.vm.LoginViewModel
import com.bhx.common.base.BaseActivity
import com.bhx.common.utils.AppManager
import com.bhx.common.utils.LogUtils
import kotlinx.android.synthetic.main.activity_login.*

/**
 * 登陆界面
 */
class LoginActivity : BaseDataActivity<LoginViewModel>() {
    private lateinit var loginType: String
    override fun getEventKey(): Any = Constants.EVENT_KEY_LOGIN
    override fun getLayoutId(): Int = R.layout.activity_login
    override fun initView() {
        super.initView()
        //微博登陆
        loginWb.clickWithTrigger {
            loginType = LOGIN_WB
            mViewModel.loginThird(1)
        }
        //微信登陆
        loginWx.clickWithTrigger {
            loginType = LOGIN_WX
            mViewModel.loginThird(0)
        }
        loginPhone.clickWithTrigger {
            startActivity<PhoneLoginActivity>()
        }
    }

    override fun dataObserver() {
        //接收登陆成功的事件
        registerObserver(Constants.TAG_LOGIN_RESULT, TokenBean::class.java).observe(this, Observer {
            LogUtils.i("登录成功")
            App.instance().setToken(it.token)
            SPUtils.put(this@LoginActivity.applicationContext, Constants.SP_LOGIN_TYPE, loginType)
            SPUtils.put(this@LoginActivity.applicationContext, Constants.SP_LOGIN, true)
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("User", it.user)
            startActivity(intent)
            this@LoginActivity.finish()

        })
    }

    override fun onResume() {
        super.onResume()
        if(mViewModel.isCancelLogin){
            LoadingDialogFragment.dismiss(this@LoginActivity)
        }
    }

    companion object {
        const val LOGIN_WX = "weixin"
        const val LOGIN_WB = "weibo"
    }
}
