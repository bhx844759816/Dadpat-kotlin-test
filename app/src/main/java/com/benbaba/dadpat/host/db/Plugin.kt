//package com.benbaba.dadpat.host.db
//
//import androidx.room.Entity
//
///**
// * Created by Administrator on 2019/6/14.
// */
//
//@Entity
//data class Plugin(val version:Int,
//                  val versionName:String,
//                  val pluginName:String,
//                  val pluginAlias:String,
//                  val mainClass:String,
//                  val url:String,
//                  val apkMd5:String,
//                  val imageGray:String,
//                  val image:String
//                  )
////
////@SerializedName("apkVersion")
////private var version: Int = 0
////@SerializedName("apkIsVersionName")
////private var versionName: String? = null
////@SerializedName("apkEnglish")
////private var pluginName: String? = null
////@SerializedName("apkAlias")
////private var pluginAlias: String? = null
////@SerializedName("apkClassName")
////private var mainClass: String? = null
////@SerializedName("jdUrl")
////private var url: String? = null
////@SerializedName("apkMd5")
////private var apkMd5: String? = null
////@SerializedName("apkCoverBefore")
////private var imageGray: String? = null
////@SerializedName("apkCoverEnd")
////private var image: String? = null
////@SerializedName("apkSize")
////private var apkSize: String? = null
////@SerializedName("apkIsRelease")
////private var isRelease: String? = null// 是否发布
////@Transient
////private var isInstall: Boolean = false // 是否已经安装
////@Transient
////private var isNeedUpdate: Boolean = false// 是否需要更新
////@Transient
////private var isDownLanding: Boolean = false//正在下载
////@Transient
////private var savePath: String? = null
////@Transient
////private var waveDrawable: WaveDrawable? = null
////@Transient
////private var imgRes: Int = 0 // 背景图
////@Transient
////private var downProgress = 0f//下载进度