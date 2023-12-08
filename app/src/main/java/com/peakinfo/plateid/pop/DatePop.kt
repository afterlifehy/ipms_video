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
import java.text.SimpleDateFormat
import java.util.Calendar

/**
 * Created by huy  on 2022/12/7.
 */
class DatePop(val context: Context?, var startDate: String, var endDate: String, var callback: DateCallBack) :
    PopupWindow(context), View.OnClickListener {

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
                startDate = TimeUtils.getStringByNow(-7, TimeConstants.DAY).substring(0, 10)
                endDate = TimeUtils.millis2String(System.currentTimeMillis(), "yyyy-MM-dd")
                binding.tvStartTime.text = startDate
                binding.tvEndTime.text = endDate
            }

            R.id.cb_oneMonth -> {
                binding.cbSevenDay.isChecked = false
                binding.cbOneMonth.isChecked = true
                binding.cbThreeMonth.isChecked = false
                startDate = TimeUtils.getStringByNow(-30, TimeConstants.DAY).substring(0, 10)
                endDate = TimeUtils.millis2String(System.currentTimeMillis(), "yyyy-MM-dd")
                binding.tvStartTime.text = startDate
                binding.tvEndTime.text = endDate
            }

            R.id.cb_threeMonth -> {
                binding.cbSevenDay.isChecked = false
                binding.cbOneMonth.isChecked = false
                binding.cbThreeMonth.isChecked = true
                startDate = TimeUtils.getStringByNow(-90, TimeConstants.DAY).substring(0, 10)
                endDate = TimeUtils.millis2String(System.currentTimeMillis(), "yyyy-MM-dd")
                binding.tvStartTime.text = startDate
                binding.tvEndTime.text = endDate
            }

            R.id.tv_startTime -> {
                datePickerDialog = ActivityCacheManager.instance().getCurrentActivity()?.let { DatePickerDialog(it) }
                val year = TimeUtils.getValueByCalendarField(startDate, SimpleDateFormat("yyyy-MM-dd"), Calendar.YEAR)
                val month = TimeUtils.getValueByCalendarField(startDate, SimpleDateFormat("yyyy-MM-dd"), Calendar.MONTH)
                val day = TimeUtils.getValueByCalendarField(startDate, SimpleDateFormat("yyyy-MM-dd"), Calendar.DAY_OF_MONTH)
                datePickerDialog?.updateDate(year, month, day)
                datePickerDialog?.show()
                datePickerDialog?.setOnDateSetListener { datePicker, i, i2, i3 ->
                    startDate = "${i}-${AppUtil.fillZero((i2 + 1).toString())}-${AppUtil.fillZero(i3.toString())}"
                    binding.tvStartTime.text = startDate
                }
            }

            R.id.tv_endTime -> {
                datePickerDialog = ActivityCacheManager.instance().getCurrentActivity()?.let { DatePickerDialog(it) }
                val year = TimeUtils.getValueByCalendarField(endDate, SimpleDateFormat("yyyy-MM-dd"), Calendar.YEAR)
                val month = TimeUtils.getValueByCalendarField(endDate, SimpleDateFormat("yyyy-MM-dd"), Calendar.MONTH)
                val day = TimeUtils.getValueByCalendarField(endDate, SimpleDateFormat("yyyy-MM-dd"), Calendar.DAY_OF_MONTH)
                datePickerDialog?.updateDate(year, month, day)
                datePickerDialog?.show()
                datePickerDialog?.setOnDateSetListener { datePicker, i, i2, i3 ->
                    endDate = "${i}-${AppUtil.fillZero((i2 + 1).toString())}-${AppUtil.fillZero(i3.toString())}"
                    binding.tvEndTime.text = endDate
                }
            }

            R.id.rtv_reset -> {
                binding.cbSevenDay.isChecked = false
                binding.cbOneMonth.isChecked = false
                binding.cbThreeMonth.isChecked = false
                endDate = TimeUtils.millis2String(System.currentTimeMillis(), "yyyy-MM-dd")
                startDate = endDate.substring(0, 8) + "01"
                binding.tvStartTime.text = startDate
                binding.tvEndTime.text = endDate
            }

            R.id.rtv_ok -> {
                callback.selectDate(startDate, endDate)
                dismiss()
            }
        }
    }

    interface DateCallBack {
        fun selectDate(startTime: String, endTime: String)
    }


}