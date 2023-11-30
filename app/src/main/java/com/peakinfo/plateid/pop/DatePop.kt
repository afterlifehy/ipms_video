package com.peakinfo.plateid.pop

import android.app.DatePickerDialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.annotation.RequiresApi
import com.blankj.utilcode.constant.TimeConstants
import com.blankj.utilcode.util.TimeUtils
import com.peakinfo.base.help.ActivityCacheManager
import com.peakinfo.common.util.AppUtil
import com.peakinfo.plateid.R
import com.peakinfo.plateid.databinding.PopDateBinding

/**
 * Created by huy  on 2022/12/7.
 */
class DatePop(val context: Context?, val startDate: String, val endDate: String, var callback: DateCallBack) :
    PopupWindow(context), View.OnClickListener {

    private var startTime: String = ""
    private var endTime: String = ""
    private lateinit var binding: PopDateBinding
    var datePickerDialog: DatePickerDialog? = null

    init {
        initView()
    }

    private fun initView() {
        binding = PopDateBinding.inflate(LayoutInflater.from(context))
        binding.tvStartTime.text = startDate
        binding.tvEndTime.text = endDate

        binding.ivClose.setOnClickListener(this)
        binding.cbSevenDay.setOnClickListener(this)
        binding.cbOneMonth.setOnClickListener(this)
        binding.cbThreeMonth.setOnClickListener(this)
        binding.tvStartTime.setOnClickListener(this)
        binding.tvEndTime.setOnClickListener(this)
        binding.rtvReset.setOnClickListener(this)
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

            R.id.iv_close -> {
                dismiss()
            }

            R.id.cb_sevenDay -> {
                binding.cbSevenDay.isChecked = true
                binding.cbOneMonth.isChecked = false
                binding.cbThreeMonth.isChecked = false
                startTime = TimeUtils.getStringByNow(-7, TimeConstants.DAY).substring(0, 10)
                endTime = TimeUtils.millis2String(System.currentTimeMillis(), "yyyy-MM-dd")
                binding.tvStartTime.text = startTime
                binding.tvEndTime.text = endTime
            }

            R.id.cb_oneMonth -> {
                binding.cbSevenDay.isChecked = false
                binding.cbOneMonth.isChecked = true
                binding.cbThreeMonth.isChecked = false
                startTime = TimeUtils.getStringByNow(-30, TimeConstants.DAY).substring(0, 10)
                endTime = TimeUtils.millis2String(System.currentTimeMillis(), "yyyy-MM-dd")
                binding.tvStartTime.text = startTime
                binding.tvEndTime.text = endTime
            }

            R.id.cb_threeMonth -> {
                binding.cbSevenDay.isChecked = false
                binding.cbOneMonth.isChecked = false
                binding.cbThreeMonth.isChecked = true
                startTime = TimeUtils.getStringByNow(-90, TimeConstants.DAY).substring(0, 10)
                endTime = TimeUtils.millis2String(System.currentTimeMillis(), "yyyy-MM-dd")
                binding.tvStartTime.text = startTime
                binding.tvEndTime.text = endTime
            }

            R.id.tv_startTime -> {
                datePickerDialog = ActivityCacheManager.instance().getCurrentActivity()?.let { DatePickerDialog(it) }
                datePickerDialog?.show()
                datePickerDialog?.setOnDateSetListener { datePicker, i, i2, i3 ->
                    startTime = "${i}-${AppUtil.fillZero((i2 + 1).toString())}-${AppUtil.fillZero(i3.toString())}"
                    binding.tvStartTime.text = startTime
                }
            }

            R.id.tv_endTime -> {
                datePickerDialog = ActivityCacheManager.instance().getCurrentActivity()?.let { DatePickerDialog(it) }
                datePickerDialog?.show()
                datePickerDialog?.setOnDateSetListener { datePicker, i, i2, i3 ->
                    endTime = "${i}-${AppUtil.fillZero((i2 + 1).toString())}-${AppUtil.fillZero(i3.toString())}"
                    binding.tvEndTime.text = endTime
                }
            }

            R.id.rtv_reset -> {
                binding.cbSevenDay.isChecked = false
                binding.cbOneMonth.isChecked = false
                binding.cbThreeMonth.isChecked = false
                startTime = TimeUtils.millis2String(System.currentTimeMillis(), "yyyy-MM-dd")
                endTime = TimeUtils.millis2String(System.currentTimeMillis(), "yyyy-MM-dd")
                binding.tvStartTime.text = startTime
                binding.tvEndTime.text = endTime
            }

            R.id.rtv_ok -> {
                callback.selectDate(startTime, endTime)
                dismiss()
            }
        }
    }

    interface DateCallBack {
        fun selectDate(startTime: String, endTime: String)
    }


}