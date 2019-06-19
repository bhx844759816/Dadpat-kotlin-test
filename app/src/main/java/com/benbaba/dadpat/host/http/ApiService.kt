package com.benbaba.dadpat.host.http

import com.benbaba.dadpat.host.model.*
import io.reactivex.Observable
import okhttp3.MultipartBody
import retrofit2.http.*
import java.util.*

/**
 * Api请求的接口类
 */
interface ApiService {
    //注册
    @FormUrlEncoded
    @POST("user/auth/register.do")
    fun register(@FieldMap map: Map<String, String>): Observable<HttpResult<User>>

    //验证验证码
    @FormUrlEncoded
    @POST("user/sms/verify.do")
    fun verifySms(@FieldMap map: Map<String, String>): Observable<HttpResult<Any>>

    //jwt方式登陆
    @FormUrlEncoded
    @POST("user/jwt/access.do")
    fun doLogin(@FieldMap map: Map<String, String>): Observable<HttpResult<TokenBean>>

    // 修改密码
    @FormUrlEncoded
    @POST("user/auth/updateCredentialBySms.do")
    fun modifyPsd(@FieldMap params: Map<String, String>): Observable<HttpResult<String>>

    //获取插件列表
    @GET("apkType/getLastApkByOrder.do?order=2")
    fun getPluginList(): Observable<HttpResult<List<PluginBean>>>

    //反馈意见
    @FormUrlEncoded
    @POST("advise/save.do")
    @Headers("Content-Type:application/x-www-form-urlencoded; charset=utf-8")
    fun postFeedBack(@FieldMap map: Map<String, String>): Observable<HttpResult<String>>


    @FormUrlEncoded
    @POST("user/update.do")
    @Headers("Content-Type:application/x-www-form-urlencoded; charset=utf-8")
    fun updateUserInfo(@FieldMap names: Map<String, String?>): Observable<HttpResult<String>>

    //更新用户头像
    @Multipart
    @POST("user/saveHeadImg.do")
    fun updateUserPhoto(@Part file: MultipartBody.Part): Observable<HttpResult<String>>

    //获取用户信息
    @GET("user/get.do")
    fun getUser(): Observable<HttpResult<User>>

    //获取公告列表
    @GET("/notice/getList.do?type=0")
    fun getNoticeList(): Observable<HttpResult<List<NoticeBean>>>

    @FormUrlEncoded
    @POST("user/auth/{accessType}/access.do")
    @Headers("Content-Type:application/x-www-form-urlencoded; charset=utf-8")
    fun loginThird(
        @Path("accessType") accessType: String, @Field("userType") userType: String,
        @Field("data") data: String
    ): Observable<HttpResult<TokenBean>>

    //退出登陆
    @POST("user/auth/logout.do")
    fun doLogout(): Observable<HttpResult<Any>>

    /**
     * 获取主程序的信息
     */
    @GET("apkType/getLastApkByOrder.do?order=1")
    fun getHostApp(): Observable<HttpResult<List<PluginBean>>>
}