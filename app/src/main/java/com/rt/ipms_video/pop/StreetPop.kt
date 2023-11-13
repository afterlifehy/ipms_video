package com.rt.ipms_video.pop

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.rt.base.BaseApplication
import com.rt.ipms_video.R
import com.rt.ipms_video.databinding.PopStreetBinding
import com.rt.ipms_video.adapter.ParkingChooseStreetAdapter

/**
 * Created by huy  on 2022/12/7.
 */
class StreetPop(val context: Context?, val streetList: MutableList<Int>, var callback: StreetSelectCallBack) :
    PopupWindow(context), View.OnClickListener {

    private lateinit var binding: PopStreetBinding
    var parkingChooseStreetAdapter: ParkingChooseStreetAdapter? = null
    var currentStreet = 1

    init {
        initView()
    }

    private fun initView() {
        binding = PopStreetBinding.inflate(LayoutInflater.from(context))
        binding.rvStreet.setHasFixedSize(true)
        binding.rvStreet.layoutManager = LinearLayoutManager(BaseApplication.instance())
        parkingChooseStreetAdapter = ParkingChooseStreetAdapter(streetList, currentStreet)
        binding.rvStreet.adapter = parkingChooseStreetAdapter

        binding.rtvOk.setOnClickListener(this)
        binding.viewMask.setOnClickListener(this)

        contentView = binding.root
        contentView!!.measure(View.MeasureSpec.EXACTLY, View.MeasureSpec.EXACTLY)
        this.width = ViewGroup.LayoutParams.MATCH_PARENT
        this.height = ViewGroup.LayoutParams.WRAP_CONTENT
        this.isFocusable = true
        this.isOutsideTouchable = true
        val dw = ColorDrawable(-0)
        //设置弹出窗体的背景
        this.setBackgroundDrawable(dw)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.view_mask -> {
                dismiss()
            }

            R.id.rtv_ok -> {
                callback.selectStreet(currentStreet)
                dismiss()
            }
        }
    }

    interface StreetSelectCallBack {
        fun selectStreet(street: Int)
    }


}