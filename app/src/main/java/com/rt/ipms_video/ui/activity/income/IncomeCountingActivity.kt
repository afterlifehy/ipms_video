package com.rt.ipms_video.ui.activity.income

import android.view.View
import android.view.View.OnClickListener
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.facade.annotation.Route
import com.rt.base.arouter.ARouterMap
import com.rt.base.dialog.DialogHelp
import com.rt.base.ext.i18N
import com.rt.base.viewbase.VbBaseActivity
import com.rt.common.util.AppUtil
import com.rt.common.util.BluePrint
import com.rt.ipms_video.R
import com.rt.ipms_video.databinding.ActivityIncomeCountingBinding
import com.rt.ipms_video.mvvm.viewmodel.IncomeCountingViewModel
import com.zrq.spanbuilder.TextStyle

@Route(path = ARouterMap.INCOME_COUNTING)
class IncomeCountingActivity : VbBaseActivity<IncomeCountingViewModel, ActivityIncomeCountingBinding>(), OnClickListener {
    val sizes = intArrayOf(19, 16)
    val colors = intArrayOf(com.rt.base.R.color.color_ff0371f4, com.rt.base.R.color.color_ff0371f4)
    val styles = arrayOf(TextStyle.BOLD, TextStyle.NORMAL)
    private val print = BluePrint(this)

    override fun initView() {
        binding.layoutToolbar.tvTitle.text = i18N(com.rt.base.R.string.营收盘点)
        val strings = arrayOf("8.00", "元")
        binding.tvTotalCharge.text = AppUtil.getSpan(strings, sizes, colors,styles)
    }

    override fun initListener() {
        binding.layoutToolbar.flBack.setOnClickListener(this)
        binding.tvTotalChargeTitle.setOnClickListener(this)
        binding.rtvPrint.setOnClickListener(this)
    }

    override fun initData() {

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fl_back -> {
                onBackPressedSupport()
            }

            R.id.tv_totalChargeTitle -> {
                DialogHelp.Builder().setTitle(i18N(com.rt.base.R.string.提示)).setContentMsg(i18N(com.rt.base.R.string.今日总收费介绍))
                    .setRightMsg(i18N(com.rt.base.R.string.确定)).setCancelable(true)
                    .setOnButtonClickLinsener(object : DialogHelp.OnButtonClickLinsener {
                        override fun onLeftClickLinsener(msg: String) {
                        }

                        override fun onRightClickLinsener(msg: String) {
                        }

                    }).isAloneButton(true).build(this@IncomeCountingActivity).showDailog()
            }

            R.id.rtv_print -> {
                print.zkblueprint("")
            }
        }
    }

    override fun getVbBindingView(): ViewBinding {
        return ActivityIncomeCountingBinding.inflate(layoutInflater)
    }

    override fun onReloadData() {
    }

    override val isFullScreen: Boolean
        get() = true

    override fun marginStatusBarView(): View {
        return binding.layoutToolbar.ablToolbar
    }

    override fun providerVMClass(): Class<IncomeCountingViewModel> {
        return IncomeCountingViewModel::class.java
    }
}