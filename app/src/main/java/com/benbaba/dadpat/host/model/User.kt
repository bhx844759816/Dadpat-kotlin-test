package com.benbaba.dadpat.host.model

import java.io.File
import java.io.Serializable

data class User(
    val userId: String, val loginName: String, val userName: String, val userMobile: String?,
    val userSex: String?, val userBirthday: String?, val headImg: String, @Transient val photoFile: File?
) : Serializable