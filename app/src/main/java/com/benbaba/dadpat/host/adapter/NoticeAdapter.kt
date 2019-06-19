package com.benbaba.dadpat.host.adapter

import android.content.Context
import com.benbaba.dadpat.host.R
import com.benbaba.dadpat.host.model.NoticeBean
import com.bhx.common.adapter.rv.CommonAdapter
import com.bhx.common.adapter.rv.ViewHolder

class NoticeAdapter : CommonAdapter<NoticeBean> {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, list: MutableList<NoticeBean>?) : super(context, list)

    override fun itemLayoutId(): Int = R.layout.adapter_notice

    override fun convert(holder: ViewHolder?, bean: NoticeBean?, position: Int) {
        bean?.let {
            holder?.setText(R.id.content,it.noticeContent)
            holder?.setText(R.id.time,it.noticeTime)
            holder?.setText(R.id.title,it.notcieTitle)
        }
    }
}