package com.benbaba.dadpat.host.config

import android.os.Environment
import com.benbaba.dadpat.host.R
import java.io.File
import java.util.HashMap

class Constants {
    companion object {
        const val BASE_URL = "http://www.dadpat.com/"
        const val BASE_URL2 = "https://www.goofypapa.com"
        //插件保存的目录
        val PLUGIN_SAVE_DIR = (Environment.getExternalStorageDirectory().toString() + File.separator + "benbaba"
                + File.separator + "plugins" + File.separator)
        val APP_SAVE_DIR = (Environment.getExternalStorageDirectory().getPath() + File.separator + "benbaba"
                + File.separator + "apk" + File.separator)


        const val DEVICE_WIFI_SSID = "dadpat"
        //    public static final String DEVICE_WIFI_SSID = "benbb";
        const val DEVICE_WIFI_PASSWORD = "dadpat@123"
        const val APP_DOWNLAND_NAME = "dadpat.apk"


        /**
         *     ************************LiveBus事件对象***************************************
         */
        //
        const val EVENT_KEY_LOADING = "EVENT_KEY_LOADING" //loading页面
        const val TAG_LOADING_RESULT = "tag_loading_result" //
        //对话框的
        const val EVENT_KEY_LOADING_DIALOG = "event_key_loading_dialog"
        const val TAG_LOADING_DIALOG = "tag_loading_dialog"
        //http请求报错
        const val EVENT_KEY_HTTP_REQUEST_ERROR = "event_key_http_request_error"
        const val TAG_HTTP_REQUEST_ERROR = "tag_http_request_error"
        //手机号登陆
        const val EVENT_KEY_PHONE_LOGIN = "event_key_phone_login"
        const val TAG_PHONE_LOGIN_RESULT = "tag_login_result"
        //注册
        const val EVENT_KEY_REGISTER = "event_key_register"
        const val TAG_REGISTER_RESULT = "tag_register_result"
        //忘记密码
        const val EVENT_KEY_FORGET_PASSWORD = "event_key_forget_password"
        const val TAG_MODIFY_PASSWORD_RESULT = "tag_modify_password_result" //修改密码的结果
        //主页面
        const val EVENT_KEY_MAIN = "event_key_main"// MainActivity的事件
        const val TAG_GET_PLUGIN_LIST = "tag_get_plugin_list"//获取插件的列表事件
        const val TAG_PLUGIN_INSTALL_SUCCESS = "tag_plugin_install_success"//插件安装成功的事件
        const val TAG_CONFIRM_DELETE_PLUGIN = "tag_confirm_delete_plugin"//确认删除插件的事件
        const val TAG_START_PLUGIN_ACTIVITY_DIALOG = "tag_start_plugin_activity_dialog"//弹出加载插件的对话框事件
        const val TAG_PRELOAD_PLUGIN_SUCCESS = "tag_preload_plugin_success"//预加载插件成功的事件
        const val TAG_SAVE_USER_INFO = "tag_save_user_info" //保存用户信息的事件
        const val TAG_CHECK_APP_VERSION = "tag_check_app_version" //检查App的版本
        const val TAG_LOGIN_OUT = "tag_login_out" //退出登陆
        const val TAG_UPDATE_USER_INFO_SUCCESS = "tag_update_user_info_success" //更新用户信息成功
        const val TAG_ALLOW_NETWORK_DOWNLAND = "tag_allow_network_downland" // 是否允许下载
        const val TAG_NEED_DOWNLAND_SIZE = "tag_need_downland_size" //需要下载的大小
        const val TAG_SELECT_DEVICE_CONNECT = "tag_select_device_connect" //需要下载的大小
        const val TAG_DOWNLAND_ERROR = "tag_downland_error" //下载失败
        const val TAG_DOWNLAND_START = "tag_downland_start" //开始下载
        const val TAG_BLUETOOTH_CONNECT_RESULT = "tag_bluetooth_connect_result"


        //公告页面
        const val EVENT_KEY_NOTICE = "event_key_notice"// MainActivity的事件
        const val TAG_GET_NOTICES = "tag_get_notices"
        //登陆页面
        const val EVENT_KEY_LOGIN = "event_key_login"
        const val TAG_LOGIN_RESULT = "tag_login_result"
        //搜索设备页面
        const val EVENT_KEY_SEARCH_DEVICE = "event_key_search_device"
        const val TAG_SEARCH_DEVICE_RESULT = "tag_search_device_result"
        const val TAG_CONNECT_DEVICE_RESULT = "tag_connect_device_result"
        //wifi列表页面
        const val EVENT_KEY_WIFI_LIST = "event_key_search_device"
        const val TAG_WIFI_LIST_RESULT = "tag_wifi_list_result"
        const val TAG_WIFI_LIST_SELECT = "tag_wifi_list_select"

        //发送wifi信息页面
        const val EVENT_KEY_SEND_WIFI_INFO = "event_key_send_wifi_info"
        const val TAG_WIFI_SEND_DEVICE_RESULT = "tag_wifi_send_device_result"
        //蓝牙搜索页面
        const val EVENT_KEY_BLUETOOTH_SEARCH = "event_key_bluetooth_search"
        const val TAG_BLUETOOTH_OPERATION_RESULT  ="tag_bluetooth_operation_result"
        const val TAG_BLUETOOTH_SEARCH_RESULT = "tag_bluetooth_search_result"
        const val TAG_BLUETOOTH_SEARCH_CLEAR = "tag_bluetooth_search_clear"
        const val TAG_BLUETOOTH_SEARCH_START= "tag_bluetooth_search_start"
        const val TAG_BLUETOOTH_SEARCH_FINISH= "tag_bluetooth_search_finish"

        /**
         * **********************sharedPreferences对象的keu**********************
         */
        const val SP_LOGIN = "login" //是否登陆
        const val SP_TOKEN = "token" //token票据
        const val SP_LOGIN_TYPE = "login_type"//登陆类型
        const val VOLUME_DRUM = "volume_drum"//玩具股音量
        const val BLUETOOTH_ADDRESS = "bluetooth_address"//玩具股音量


        /**
         * **********************插件列表显示的图片*********************
         */
        val RES_IMG_MAP: HashMap<String, Int> = object : HashMap<String, Int>() {
            init {
                put("Plugin_Web_Calendar", R.drawable.main_item_calendar)
                put("Plugin_Web_Astronomy", R.drawable.main_item_astronomy)
                put("Plugin_Web_ChinaHistory", R.drawable.main_item_chinese_history)
                put("Plugin_Web_Earth", R.drawable.main_item_earth)
                put("Plugin_Web_Animal", R.drawable.main_item_animal)
                put("Plugin_Web_English", R.drawable.main_item_abc)
                put("Plugin_Web_Picture", R.drawable.main_item_picture)
                put("Plugin_Web_WorldHistory", R.drawable.main_item_world_history)
                put("Plugin_Dadpat", R.drawable.main_item_dadpat)
                put("Plugin_Rhythm", R.drawable.main_item_rhythm)
                put("Plugin_DadpatGuess", R.drawable.main_item_guess)
                put("Plugin_Piano", R.drawable.main_item_piano)
            }
        }
        val RES_TEXT_MAP: HashMap<String, Int> = object : HashMap<String, Int>() {
            init {
                put("Plugin_Web_Calendar", R.drawable.main_item_calendar_text)
                put("Plugin_Web_Astronomy", R.drawable.main_item_astronomy_text)
                put("Plugin_Web_ChinaHistory", R.drawable.main_item_chinese_history_text)
                put("Plugin_Web_Earth", R.drawable.main_item_earth_text)
                put("Plugin_Web_Animal", R.drawable.main_item_animal_text)
                put("Plugin_Web_English", R.drawable.main_item_abc_text)
                put("Plugin_Web_Picture", R.drawable.main_item_picture_text)
                put("Plugin_Web_WorldHistory", R.drawable.main_item_world_history_text)
                put("Plugin_Dadpat", R.drawable.main_item_dadpat_text)
                put("Plugin_Rhythm", R.drawable.main_item_rhythm_text)
                put("Plugin_DadpatGuess", R.drawable.main_item_guess_text)
                put("Plugin_Piano", R.drawable.main_item_piano_text)
            }
            //

        }


    }
}