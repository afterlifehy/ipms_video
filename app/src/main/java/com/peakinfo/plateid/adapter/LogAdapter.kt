package com.peakinfo.plateid.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.blankj.utilcode.util.ConvertUtils
import com.peakinfo.base.adapter.BaseBindingAdapter
import com.peakinfo.plateid.databinding.ItemLogBinding
import java.io.File
import com.peakinfo.base.adapter.VBViewHolder

class LogAdapter(
    data: MutableList<File>? = null,
    var logFileCheckedList: MutableList<File>,
    val listener: (file: File, isChecked: Boolean) -> Unit
) :
    BaseBindingAdapter<File, ItemLogBinding>(data) {
    fun setCheckedFileList(logFileCheckedList: MutableList<File>) {
        this.logFileCheckedList = logFileCheckedList
        notifyDataSetChanged()
    }

    override fun convert(holder: VBViewHolder<ItemLogBinding>, item: File) {
        holder.vb.cbLog.text = item.name
        holder.vb.cbLog.setOnCheckedChangeListener(null)
        holder.vb.cbLog.isChecked = logFileCheckedList.contains(item) // 设置当前项的选中状态
        holder.vb.cbLog.setOnCheckedChangeListener { buttonView, isChecked ->
            listener(item, isChecked)
        }
        // 设置marginBottom
        val layoutParams = ViewGroup.MarginLayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        holder.itemView.layoutParams = layoutParams
        val params = holder.itemView.layoutParams as ViewGroup.MarginLayoutParams
        params.bottomMargin = ConvertUtils.dp2px(10f)
        holder.itemView.layoutParams = params
    }

    override fun createViewBinding(inflater: LayoutInflater, parent: ViewGroup): ItemLogBinding {
        return ItemLogBinding.inflate(inflater)
    }
}