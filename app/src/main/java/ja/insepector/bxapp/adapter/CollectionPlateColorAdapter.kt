package ja.insepector.bxapp.adapter

import android.view.LayoutInflater
import android.view.View.OnClickListener
import android.view.ViewGroup
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.SizeUtils
import ja.insepector.base.adapter.BaseBindingAdapter
import ja.insepector.base.adapter.VBViewHolder
import ja.insepector.base.ext.hide
import ja.insepector.base.ext.show
import ja.insepector.common.util.GlideUtils
import ja.insepector.bxapp.databinding.ItemCollectionPlateColorBinding

class CollectionPlateColorAdapter(val widthType: Int, data: MutableList<Int>? = null, val onClickListener: OnClickListener) :
    BaseBindingAdapter<Int, ItemCollectionPlateColorBinding>(data) {
    var lastColorPosition = 0
    var checkedColorPosition = 0
    var checkedColor = 0
    var collectioPlateColorList: MutableList<Int> = ArrayList()

    init {
        collectioPlateColorList.add(ja.insepector.common.R.mipmap.ic_plate_blue)
        collectioPlateColorList.add(ja.insepector.common.R.mipmap.ic_plate_green)
        collectioPlateColorList.add(ja.insepector.common.R.mipmap.ic_plate_yellow)
        collectioPlateColorList.add(ja.insepector.common.R.mipmap.ic_plate_yellow_green)
        collectioPlateColorList.add(ja.insepector.common.R.mipmap.ic_plate_white)
        collectioPlateColorList.add(ja.insepector.common.R.mipmap.ic_plate_black)
        collectioPlateColorList.add(ja.insepector.common.R.mipmap.ic_plate_other)
    }

    override fun convert(holder: VBViewHolder<ItemCollectionPlateColorBinding>, item: Int) {
        GlideUtils.instance?.loadImage(holder.vb.ivColor, collectioPlateColorList[item])
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
        var width = 0
        if (widthType == 1) {
            width = (ScreenUtils.getScreenWidth() - SizeUtils.dp2px(30f)) / 7
        } else if (widthType == 2) {
            width = (ScreenUtils.getScreenWidth() - SizeUtils.dp2px(52f)) / 7
        } else if (widthType == 3) {
            width = (ScreenUtils.getScreenWidth() - SizeUtils.dp2px(38f)) / 7
        }

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