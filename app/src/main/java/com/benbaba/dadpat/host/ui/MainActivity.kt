@file:Suppress("UNCHECKED_CAST")

package com.benbaba.dadpat.host.ui

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import android.view.View
import android.view.animation.Animation
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.benbaba.dadpat.common.bluetooth.SmartBle
import com.benbaba.dadpat.common.bluetooth.exception.BaseBluetoothException
import com.benbaba.dadpat.common.bluetooth.exception.BluetoothExceptionCompat
import com.benbaba.dadpat.host.App
import com.benbaba.dadpat.host.R
import com.benbaba.dadpat.host.adapter.MainAdapter
import com.benbaba.dadpat.host.base.BaseDataActivity
import com.benbaba.dadpat.host.config.Constants
import com.benbaba.dadpat.host.drum.BlueState
import com.benbaba.dadpat.host.drum.SocketManager
import com.benbaba.dadpat.host.model.PluginBean
import com.benbaba.dadpat.host.model.User
import com.benbaba.dadpat.host.update.AppUpdateManager
import com.benbaba.dadpat.host.utils.*
import com.benbaba.dadpat.host.view.NoScrollLayoutManager
import com.benbaba.dadpat.host.view.dialog.*
import com.benbaba.dadpat.host.vm.MainViewModel
import com.bhx.common.glide.ImageLoader
import com.bhx.common.mvvm.BaseMVVMActivity
import com.bhx.common.utils.*
import com.qihoo360.replugin.RePlugin
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_main.*

/**
 * 主页面
 */
class MainActivity : BaseDataActivity<MainViewModel>() {
    private var mUser: User? = null //用户信息
    private var mPluginList: MutableList<PluginBean> = ArrayList() //插件集合对象
    private var isAllowNetWorkDownLanding = false//是否允许移动流量下载插件
    private var mDragPluginBean: PluginBean? = null //拖拽的插件对象
    private var mNeedUpdateBean: PluginBean? = null //拖拽的插件对象
    private lateinit var mDownloadPluginBean: PluginBean//点击下载的插件对象
    private var mDragView: View? = null //当前拖拽的View
    private lateinit var mMainAdapter: MainAdapter    //首页列表适配器
    private lateinit var mDragHelper: ItemTouchHelper // 拖拽的帮助类
    private var isDialogShow = false // 是否显示了删除插件的对话框
    private var exitTime: Long = 0
    //DragItemCallBack的对象
    private val dragItemCallBack: DragItemCallBack by lazy {
        DragItemCallBack(mMainAdapter, mPluginList, mainTrash)
    }
    //拖拽排序的CallBack
    private val mItemDragListener = object : DragItemCallBack.OnItemDragListener {
        override fun startDrag() {
            isDialogShow = false
            setTrashVisible()
        }

        override fun deleteItem(view: View?, position: Int) {
            isDialogShow = true
            mDragView = view
            ConfirmDeleteDialogFragment.show(this@MainActivity, mPluginList[position].pluginAlias)
        }

        override fun clearView() {
            if (!isDialogShow) {
                setTrashInVisible()
            }
        }
    }
    // adapter数据改变的回调
    private val mAdapterDataObserver = object : RecyclerView.AdapterDataObserver() {
        override fun onChanged() {
            mainShaderView.setShaderView(mPluginList.takeLast(4))
        }

        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
            LogUtils.i("onItemRangeMoved")
            mainShaderView.setShaderView(mPluginList.takeLast(4))
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
            mainShaderView.setShaderView(mPluginList.takeLast(4))
        }
    }

    /**
     * 获取布局ID
     */
    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    /**
     * 设置事件的Key
     */
    override fun getEventKey(): Any {
        return Constants.EVENT_KEY_MAIN
    }

    /**
     * 初始化View
     */
    override fun initView() {
        super.initView()
        if (intent.getSerializableExtra("User") != null) {
            mUser = intent.getSerializableExtra("User") as User
        }
        mMainAdapter = MainAdapter(this, mPluginList)
        mainRecyclerView.addItemDecoration(GridSpaceItemDecoration(this))
        mainRecyclerView.layoutManager = NoScrollLayoutManager(this, 4, GridLayoutManager.VERTICAL, false)
        mainRecyclerView.setHasFixedSize(true)
        mDragHelper = ItemTouchHelper(dragItemCallBack)
        mDragHelper.attachToRecyclerView(mainRecyclerView)
        mainRecyclerView.adapter = mMainAdapter
        initListener()
        initUser()
    }

    /**
     * 注册监听事件
     */
    private fun initListener() {
        //注册触摸监听
        mainRecyclerView.addOnItemTouchListener(object : OnRecyclerItemClickListener(mainRecyclerView) {
            override fun onItemClick(vh: RecyclerView.ViewHolder?) {
                vh?.let {
                    val pos = it.adapterPosition
                    if (pos < 0 || pos >= mPluginList.size) {
                        return@let
                    }
                    val bean = mPluginList[pos]
                    if (bean.isInstall && !bean.isNeedUpdate) {
                        LogUtils.i("插件$bean")
                        mViewModel.startPluginActivity(bean)
                    } else {
                        //插件是否是开发完成
                        if (bean.isRelease == "2") {
                            return
                        }
                        //下载插件
                        if (NetworkUtils.isWifiConnected(this@MainActivity) || isAllowNetWorkDownLanding) {
                            mViewModel.downLandPlugin(bean)
                            mMainAdapter.notifyDataSetChanged()
                        } else {
                            mDownloadPluginBean = bean
                            mViewModel.getNeedDownSize(bean)
                        }
                    }
                }
            }

            override fun onItemLongClick(vh: RecyclerView.ViewHolder?) {
                vh?.let {
                    mDragPluginBean = mPluginList[vh.adapterPosition]
                    mDragPluginBean?.let {
                        if (it.isInstall) {
                            mDragHelper.startDrag(vh)
                        }
                    }
                }

            }
        })
        //注册数据改变的监听
        mMainAdapter.registerAdapterDataObserver(mAdapterDataObserver)
        //设置拖拽的监听
        dragItemCallBack.setOnItemDragListener(mItemDragListener)
        //点击头像的时候
        mainHeadImg.clickWithTrigger {
            PersonInfoDialogFragment.show(this@MainActivity, mUser)
        }
        //调节玩具鼓的音量
        adjustSound.clickWithTrigger {
            AdjustDeviceVolumeDialogFragment.show(this@MainActivity)
        }
        //点击公告
        mainNotice.clickWithTrigger {
            NoticeDialogFragment.show(this@MainActivity)
//            RePlugin.startActivity(
//                this@MainActivity, RePlugin.createIntent(
//                    "Plugin_Rhythm",
//                    "org.cocos2dx.javascript.AppActivity"
//                )
//            )
        }
        //配置玩具鼓的wifi
        mainSetting.clickWithTrigger {
            DeviceConnectDialogFragment2.show(this@MainActivity)
        }
    }

    /**
     * 初始化用户信息
     */
    private fun initUser() {
        mUser?.let {
            val url: String = if (!TextUtils.isEmpty(it.headImg) && it.headImg.startsWith("http")) {
                it.headImg
            } else {
                Constants.BASE_URL + it.headImg
            }
            //设置姓名
            mainName.text = it.userName
            //设置头像
            //Context context, String url, ImageView imageView, @DrawableRes int loadingRes, @DrawableRes int errorRes
            ImageLoader.displayImage(this@MainActivity, url, mainHeadImg)
        }
    }

    /**
     * 获取数据
     */
    override fun initData() {
        // 获取插件列表
        mViewModel.getPluginList()
    }

    /**
     * 事件订阅
     */
    override fun dataObserver() {
        LogUtils.i("dataObserver")
        //注册获取插件列表事件监听
        registerObserver(Constants.TAG_GET_PLUGIN_LIST, List::class.java).observe(this, Observer {
            if (it != null) {
                val list = it as List<PluginBean>
                mViewModel.handlePluginList(list)
                mViewModel.sortedPluginList(list)
                mPluginList.clear()
                mPluginList.addAll(list)
                mMainAdapter.notifyDataSetChanged()
            }
        })
        //注册安装事件监听
        registerObserver(Constants.TAG_PLUGIN_INSTALL_SUCCESS, PluginBean::class.java).observe(this, Observer {
            LogUtils.i("接收到安装完成的事件")
//            mViewModel.handlePluginList(mPluginList)
            mViewModel.sortedPluginList(mPluginList)
            mMainAdapter.notifyDataSetChanged()
        })
        //注册删除插件事件监听
        registerObserver(Constants.TAG_CONFIRM_DELETE_PLUGIN, Boolean::class.java).observe(this, Observer {
            if (it) {
                //删除插件
                mViewModel.deletePlugin(mDragPluginBean)
                mDragView?.visibility = View.VISIBLE
//                mViewModel.handlePluginList(mPluginList)
                LogUtils.i("列表$mPluginList")
                mViewModel.sortedPluginList(mPluginList)
                mMainAdapter.notifyDataSetChanged()
                //开启垃圾桶抖动的动画
                val anim = ShakeViewAnim.tada(mainTrash)
                anim.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                        setTrashInVisible()
                    }
                })
                anim.start()
            } else {
                //取消删除插件
                setTrashInVisible()
                mDragView?.visibility = View.VISIBLE
            }
        })
        //跳转插件加载对话框事件
        registerObserver(Constants.TAG_START_PLUGIN_ACTIVITY_DIALOG, String::class.java).observe(this, Observer {
            LogUtils.i("显示打开插件的对话框")
            LoadingDialogFragment.show(this@MainActivity, it)
        })
        //预加载插件成功
        registerObserver(Constants.TAG_PRELOAD_PLUGIN_SUCCESS, PluginBean::class.java).observe(this, Observer {
            LogUtils.i("预加载插件成功")
            val intent = RePlugin.createIntent(it.pluginName, it.mainClass.trim())
            val isStartResult = RePlugin.startActivity(this@MainActivity, intent)
            if (!isStartResult) {
                LoadingDialogFragment.dismiss(this@MainActivity)
                ReStartAPPDialogFragment.show(this@MainActivity)
            }
        })
        //保存用户信息
        registerObserver(Constants.TAG_SAVE_USER_INFO, User::class.java).observe(this, Observer {
            LogUtils.i("上传用户的个人信息")
            if (NetworkUtils.isConnected(this@MainActivity)) {
                mViewModel.uploadUserInfo(it)
            } else {
                ToastUtils.toastShort("请检查网络")
            }

        })
        //保存用户信息成功
        registerObserver(Constants.TAG_UPDATE_USER_INFO_SUCCESS, User::class.java).observe(this, Observer {
            LogUtils.i("接收更新用户信息成功")
            mUser = it
            initUser()
        })
        //获取需要下载的进度
        registerObserver(Constants.TAG_NEED_DOWNLAND_SIZE, String::class.java).observe(this, Observer {
            //弹出是否在4G条件下下载
            DownLandPromptDialogFragment.show(this@MainActivity, it)
        })
        //是否允许移动网络下下载
        registerObserver(Constants.TAG_ALLOW_NETWORK_DOWNLAND, Boolean::class.java).observe(this, Observer {
            if (it) {
                isAllowNetWorkDownLanding = true
                mViewModel.downLandPlugin(mDownloadPluginBean)
            } else {
                mViewModel.stopAllDownload()
            }
        })
        //注销登录
        registerObserver(Constants.TAG_LOGIN_OUT, Boolean::class.java).observe(this, Observer {
            LogUtils.i("注销登陆")
            mViewModel.loginOut()
            SPUtils.put(this@MainActivity, Constants.SP_LOGIN, false)
            SPUtils.put(this@MainActivity, Constants.SP_TOKEN, "")
            val intent = Intent(this@MainActivity,LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            this@MainActivity.finish()
//            startActivity<LoginActivity>()
//            AppManager.getAppManager().finishAllActivity()
        })
        //接收到需要检查版本更新的需求
        registerObserver(Constants.TAG_CHECK_APP_VERSION, Int::class.java).observe(this, Observer {
            checkAppUpdate()
        })
        //接收到蓝牙或者wifi设备连接
        registerObserver(Constants.TAG_SELECT_DEVICE_CONNECT, Int::class.java).observe(this, Observer {
            when (it) {
                0 -> startActivity<BluetoothSearchActivity>()
                1 -> startActivity<DrumSettingActivity>()
            }
        })
        //蓝牙操作结果
        registerObserver(Constants.TAG_BLUETOOTH_CONNECT_RESULT, BlueState::class.java).observe(this, Observer {
            when (it.code) {
                BlueState.CONNECT_SUCCESS -> {
                    ToastUtils.toastShort("连接爸爸拍拍蓝牙成功")
                }
                BlueState.CONNECT_ERROR,
                BlueState.SEARCH_ERROR -> {
                    if (it.exception != null) {
                        ToastUtils.toastShort(it.exception!!.msg)
                        operateException(it.exception)
                    }
                }
                BlueState.SEARCH_NO_DEVICE -> ToastUtils.toastShort("未搜索到爸爸拍拍")
            }
        })
        //开始下载更新Item显示
        registerObserver(Constants.TAG_DOWNLAND_START, PluginBean::class.java).observe(this, Observer {
            mMainAdapter.notifyItemChanged(mPluginList.indexOf(it))
        })

    }

    /**
     * 处理蓝牙操作的异常
     */
    private fun operateException(exception: BaseBluetoothException?) {
        when (exception?.code) {
            BluetoothExceptionCompat.CODE_BLUETOOTH_NOT_OPEN -> {
                SmartBle.getInstance().openBlueSync(this@MainActivity, 0x02)
            }

        }
    }


    /**
     * 检查版本更新   适配8.0手机得外部安装Apk得权限
     */
    @SuppressLint("CheckResult")
    fun checkAppUpdate() {
        if (Build.VERSION.SDK_INT >= 26) {
            val b = packageManager.canRequestPackageInstalls()
            if (!b) {
                RxPermissions(this).request(Manifest.permission.REQUEST_INSTALL_PACKAGES)
                    .subscribe { aBoolean ->
                        if (!aBoolean) {
//                            val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
//                            startActivityForResult(intent, 0x01)
                            Utils.startInstallPermissionSettingActivity(this@MainActivity,0x01)
                        } else {
                            AppUpdateManager.INSTANCE.checkIsNeedUpdate(this@MainActivity)
                        }
                    }
            } else {
                AppUpdateManager.INSTANCE.checkIsNeedUpdate(this@MainActivity)
            }
        } else {
            AppUpdateManager.INSTANCE.checkIsNeedUpdate(this@MainActivity)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0x01) {
            if (Build.VERSION.SDK_INT >= 26) {
                val b = packageManager.canRequestPackageInstalls()
                if (b) {
                    AppUpdateManager.INSTANCE.checkIsNeedUpdate(this@MainActivity)
                }
            }
        }
    }

    /**
     * 显示垃圾桶
     */
    private fun setTrashVisible() {
        mainTrash.visibility = View.VISIBLE
        mainTrash.animate()
            .translationX(0f)
            .setDuration(1000)
            .start()
    }

    /**
     * 页面显示的时候
     */
    override fun onResume() {
        super.onResume()
        LoadingDialogFragment.dismiss(this@MainActivity)
    }

    /**
     * 隐藏垃圾桶
     */
    private fun setTrashInVisible() {
        mainTrash.animate()
            .translationX(200f)
            .setDuration(1000)
            .start()
    }

    override fun onDestroy() {
        super.onDestroy()
        AppUpdateManager.INSTANCE.unDisposable()
    }


    override fun onBackPressed() {
        if (System.currentTimeMillis() - exitTime > 2000) {
            ToastUtils.toastShort("再按一次退出程序")
            exitTime = System.currentTimeMillis()
        } else {
            AppManager.getAppManager().AppExit(this)
        }
    }
}
