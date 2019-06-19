package com.benbaba.dadpat.host.adapter

import android.content.Context
import android.view.View
import android.widget.ImageView
import com.benbaba.dadpat.host.R
import com.benbaba.dadpat.host.config.Constants
import com.benbaba.dadpat.host.model.PluginBean
import com.benbaba.dadpat.host.view.WaveDrawable
import com.bhx.common.adapter.rv.CommonAdapter
import com.bhx.common.adapter.rv.ViewHolder
import com.bhx.common.utils.LogUtils

/**
 * 首页适配器
 */
class MainAdapter : CommonAdapter<PluginBean> {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, list: MutableList<PluginBean>?) : super(context, list)

    override fun itemLayoutId(): Int = R.layout.adapter_main

    override fun convert(holder: ViewHolder?, bean: PluginBean?, position: Int) {
        holder?.let {
            val name: ImageView = it.getView(R.id.adapterItemName)
            val img: ImageView = it.getView(R.id.adapterItemImg)
            val downland: ImageView = holder.getView(R.id.adapterItemDownland)
            if (bean != null) {
                if (bean.isRelease == "2") {
                    name.setBackgroundResource(R.drawable.main_coming_soon)
                    downland.visibility = View.GONE
                    img.setImageDrawable(bean.waveDrawable)
                } else {
                    Constants.RES_TEXT_MAP[bean.pluginName]?.let { it1 -> name.setBackgroundResource(it1) }
//                    if (bean.waveDrawable == null) {
//                        bean.waveDrawable = getWaveDrawable(bean.imgRes)
//                    }
                    if (bean.isInstall) {
                        if (bean.isNeedUpdate) {
                            if (bean.isDownLanding) {
                                downland.visibility = View.GONE
                                img.setImageDrawable(bean.waveDrawable)
                            } else {
                                downland.visibility = View.VISIBLE
                                img.setImageDrawable(null)
                                img.setImageResource(bean.imgRes)
                            }
                        } else {
                            downland.visibility = View.GONE
                            img.setImageDrawable(null)
                            img.setImageResource(bean.imgRes)
                        }
                    } else {
                        LogUtils.i("插件名称${bean.pluginAlias}")
                        downland.visibility = View.GONE
                        if(bean.waveDrawable == null){
                            bean.waveDrawable = getWaveDrawable(bean.imgRes)
                        }
                        img.setImageDrawable(bean.waveDrawable)
                    }
                    if (bean.waveDrawable != null && (!bean.isInstall || bean.isNeedUpdate)) {
                        bean.waveDrawable.level = (bean.downProgress * 10000).toInt()
                    }
                }

            }
        }
    }

    /**
     * 实例化下载得Drawable
     *
     * @return
     */
    private fun getWaveDrawable(res: Int): WaveDrawable {
        val waveDrawable = WaveDrawable(this.mContext.applicationContext, res)
        waveDrawable.isIndeterminate = false
        waveDrawable.setWaveSpeed(4)
        waveDrawable.setWaveAmplitude(10)
        return waveDrawable
    }

}