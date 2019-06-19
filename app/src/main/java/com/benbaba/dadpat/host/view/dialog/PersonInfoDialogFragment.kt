package com.benbaba.dadpat.host.view.dialog

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.fragment.app.FragmentActivity
import com.benbaba.dadpat.host.R
import com.benbaba.dadpat.host.config.Constants
import com.benbaba.dadpat.host.http.applySchedulers
import com.benbaba.dadpat.host.http.handleResult
import com.benbaba.dadpat.host.model.User
import com.benbaba.dadpat.host.utils.MatisseUtils
import com.benbaba.dadpat.host.utils.SPUtils
import com.benbaba.dadpat.host.utils.clickWithTrigger
import com.bhx.common.base.BaseDialogFragment
import com.bhx.common.event.LiveBus
import com.bhx.common.glide.ImageLoader
import com.bhx.common.utils.AppCacheUtils
import com.bhx.common.utils.LogUtils
import com.bhx.common.utils.ToastUtils
import com.qihoo360.loader2.PMF.getApplicationContext
import com.tbruyelle.rxpermissions2.RxPermissions
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.compress.CompressHelper
import com.zhihu.matisse.compress.FileUtil
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_person_message.*
import java.io.File
import java.util.*

/**
 * 个人信息
 */
class PersonInfoDialogFragment : BaseDialogFragment() {
    private var mDataPickerDialog: DatePickerDialog? = null
    private var mUser: User? = null
    private val takePhotoCode = 0x02
    private var mPhotoFile: File? = null

    private val mDateListener = (DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
        val time = year.toString() + "-" + (month + 1) + "-" + dayOfMonth
        birthday.setText(time)
    })

    override fun getLayoutId(): Int = R.layout.dialog_person_message


    override fun initView(view: View?) {
        super.initView(view)
        if (arguments?.getSerializable("User") != null) {
            mUser = arguments?.getSerializable("User") as User
        }

        LogUtils.i("user:$mUser")
        //点击保存
        save.clickWithTrigger {
            mUser?.let {
                //获取用户名
                val userName = personName.text.toString().trim()
                if (TextUtils.isEmpty(userName)) {
                    ToastUtils.toastShort("用户名不能为空")
                    return@clickWithTrigger
                }
                //获取生日
                val userBirthday = birthday.text.toString().trim()
                if (TextUtils.isEmpty(userBirthday)) {
                    ToastUtils.toastShort("生日不能为空")
                    return@clickWithTrigger
                }
                //获取性别
                val sex = when {
                    girl.isChecked -> "2"
                    boy.isChecked -> "1"
                    else -> "0"
                }
                //组合参数
                val userCopy: User = it.copy(
                    userName = userName, userBirthday = userBirthday,
                    photoFile = mPhotoFile, userSex = sex
                )//只想改变名字
                LiveBus.getDefault().postEvent(Constants.EVENT_KEY_MAIN, Constants.TAG_SAVE_USER_INFO, userCopy)
                dismissAllowingStateLoss()
            }
        }
        //点击出生日期
        birthday.clickWithTrigger {
            showDataPicker()
        }
        //点击头像
        personHead.clickWithTrigger {
            takePhoto()
            //            LiveBus.getDefault().postEvent(Constants.EVENT_KEY_MAIN, Constants.TAG_CHANGE_USER_HEAD_PHOTO, true)
        }
        //用户反馈
        personFeedback.clickWithTrigger {
            activity?.let {
                FeedBackDialogFragment.show(it, mUser)
            }
        }
        //检查版本
        checkAppVersion.clickWithTrigger {
            LogUtils.i("检查版本发送端")
            LiveBus.getDefault().postEvent(Constants.EVENT_KEY_MAIN, Constants.TAG_CHECK_APP_VERSION, 1)

        }
        //用户协议
        userProtocol.clickWithTrigger {
            activity?.let {
                UserProtocolDialogFragment.show(it)
            }
        }
        //清理缓存
        clearCache.clickWithTrigger {
            AppCacheUtils.clearAllCache(context)
            getTotalCache()

        }
        //注销登陆
        loginOff.clickWithTrigger {
            LogUtils.i("注销登录发送端")
            LiveBus.getDefault().postEvent(Constants.EVENT_KEY_MAIN, Constants.TAG_LOGIN_OUT, true)
        }
    }

    fun getTotalCache() {
        Observable.create<String> {
            val size = AppCacheUtils.getTotalCacheSize(context)
            it.onNext(size)
            it.onComplete()
        }.compose(applySchedulers())
            .subscribe(Consumer {
                totalCache.text = it
            }, Consumer {

            })
    }


    override fun initData() {
        mUser?.let {
            //设置人名
            personName.setText(it.userName)
            //设置头像
            val url: String = if (!TextUtils.isEmpty(it.headImg) && it.headImg.startsWith("http")) {
                it.headImg
            } else {
                Constants.BASE_URL + it.headImg
            }
            LogUtils.i("url:$url")
            ImageLoader.displayImage(context, url, personHead)
            //设置性别
            when (it.userSex) {
                "", "0" -> {
                    boy.isChecked = false
                    girl.isChecked = false
                }
                "1" -> {
                    boy.isChecked = true
                    girl.isChecked = false
                }
                "2" -> {
                    boy.isChecked = false
                    girl.isChecked = true
                }
            }
            //设置出生日期
            birthday.setText(it.userBirthday)
            //设置缓存大小
            getTotalCache()
            //设置登陆类型(手机/第三方登陆)
            val loginType = SPUtils.get(context, Constants.SP_LOGIN_TYPE, "phone") as String
            val typeRes = when (loginType) {
                "phone" -> R.drawable.per_phone
                "weixin" -> R.drawable.per_wx
                "weibo" -> R.drawable.per_sina
                else -> R.drawable.per_phone
            }
            accountType.setBackgroundResource(typeRes)


        }
    }

    /**
     * 跳转到选择头像的界面
     */
    private fun takePhoto() {
        activity?.let {
            RxPermissions(it).requestEach(Manifest.permission.CAMERA)
                .subscribe { permission ->
                    if (permission.granted) {
                        MatisseUtils.openPhoto(this@PersonInfoDialogFragment, takePhotoCode, 1)
                    } else if (!permission.shouldShowRequestPermissionRationale) {
                        //展示权限被拒绝的情况
                    }
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == takePhotoCode) {
            //接收到选择的照片
            val file = FileUtil.getFileByPath(Matisse.obtainPathResult(data)[0])
            // 压缩后的文件（多个文件压缩可以循环压缩）
            mPhotoFile = CompressHelper.getDefault(getApplicationContext()).compressToFile(file)
            //显示到头像上
            ImageLoader.displayImage(context, mPhotoFile, personHead)
        }
    }

    private fun showDataPicker() {
        if (mDataPickerDialog == null) {
            val ca = Calendar.getInstance()
            val year = ca.get(Calendar.YEAR)
            val month = ca.get(Calendar.MONTH)
            val day = ca.get(Calendar.DAY_OF_MONTH)
            mDataPickerDialog = DatePickerDialog(context, mDateListener, year, month, day)
        }
        mDataPickerDialog?.show()

    }

    companion object {
        private val TAG = PersonInfoDialogFragment::class.simpleName


        fun show(activity: FragmentActivity, user: User?) {
            var fragment = activity.supportFragmentManager.findFragmentByTag(TAG)
            if (fragment == null) {
                fragment = PersonInfoDialogFragment()
            }
            val bundle = Bundle()
            bundle.putSerializable("User", user)
            fragment.arguments = bundle
            if (!fragment.isAdded) {
                val manager = activity.supportFragmentManager
                val transaction = manager.beginTransaction()
                transaction.add(fragment, TAG)
                transaction.commitAllowingStateLoss()
            }
        }

        fun dismiss(activity: FragmentActivity) {
            val fragment = activity.supportFragmentManager?.findFragmentByTag(TAG)
            if (fragment != null && fragment is PersonInfoDialogFragment && fragment.isAdded) {
                fragment.dismissAllowingStateLoss()
            }
        }
    }
}