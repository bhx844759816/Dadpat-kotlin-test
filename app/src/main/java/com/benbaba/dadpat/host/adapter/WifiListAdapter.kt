package com.benbaba.dadpat.host.adapter

import android.content.Context
import android.net.wifi.ScanResult
import android.view.View
import com.benbaba.dadpat.host.R
import com.benbaba.dadpat.host.config.Constants
import com.benbaba.dadpat.host.model.NoticeBean
import com.bhx.common.adapter.rv.CommonAdapter
import com.bhx.common.adapter.rv.ViewHolder
import com.bhx.common.event.LiveBus

class WifiListAdapter : CommonAdapter<ScanResult> {
    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, list: MutableList<ScanResult>?) : super(context, list)

    override fun itemLayoutId(): Int = R.layout.adapter_wifi_list_item

    override fun convert(holder: ViewHolder?, result: ScanResult?, position: Int) {
        holder?.setText(R.id.wifiName, result?.SSID?.replace("\"", ""))
        holder?.setOnClickListener(R.id.wifiSend) {
            LiveBus.getDefault().postEvent(Constants.EVENT_KEY_WIFI_LIST, Constants.TAG_WIFI_LIST_SELECT, position)
        }
    }


}