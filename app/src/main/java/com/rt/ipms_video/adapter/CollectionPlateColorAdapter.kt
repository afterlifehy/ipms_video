package com.rt.ipms_video.adapter

import android.view.LayoutInflater
import android.view.View.OnClickListener
import android.view.ViewGroup
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.SizeUtils
import com.rt.base.adapter.BaseBindingAdapter
import com.rt.base.adapter.VBViewHolder
import com.rt.base.ext.gone
import com.rt.base.ext.hide
import com.rt.base.ext.show
import com.rt.common.util.GlideUtils
import com.rt.ipms_video.databinding.ItemCollectionPlateColorBinding

class CollectionPlateColorAdapter(data: MutableList<Int>? = null, val onClickListener: OnClickListener) :
    BaseBindingAdapter<Int, ItemCollectionPlateColorBinding>(data) {
    var lastColorPosition = 0
    var checkedColorPosition = 0
    var checkedColor = com.rt.common.R.mipmap.ic_plate_blue
    override fun convert(holder: VBViewHolder<ItemCollectionPlateColorBinding>, item: Int) {
        GlideUtils.instance?.loadImage(holder.vb.ivColor, item)
        if (checkedColor == item) {
            holder.vb.rflStroke.show()
            holder.vb.ivHook.show()
        } else {
            holder.vb.rflStroke.hide()
            holder.vb.ivHook.hide()
        }

        holder.vb.flColor.tag = item
        holder.vb.flColor.setOnClickListener(onClickListener)
    }

    override fun createViewBinding(inflater: LayoutInflater, parent: ViewGroup): ItemCollectionPlateColorBinding {
        val binding = ItemCollectionPlateColorBinding.inflate(inflater)
        var lp = binding.root.layoutParams
        val width = (ScreenUtils.getScreenWidth()-SizeUtils.dp2px(64f))/7
        if (lp == null) {
            lp = ViewGroup.LayoutParams(width, width)
        } else {
            lp.width = width
            lp.height = width
        }
        binding.root.layoutParams = lp
        return binding
    }

    fun updateColor(color: Int, position: Int) {
        lastColorPosition = checkedColorPosition
        checkedColorPosition = position
        checkedColor = color
        notifyItemChanged(lastColorPosition)
        notifyItemChanged(checkedColorPosition)
    }
}