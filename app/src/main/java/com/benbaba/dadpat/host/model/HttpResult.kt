package com.benbaba.dadpat.host.model

/**
 * 请求的结果包装类
 */
data class HttpResult<T>(val success: Boolean, val code: String, val msg: String , val data: T)


