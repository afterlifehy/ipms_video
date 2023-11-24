package com.rt.ipms_video.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.facade.annotation.Route
import com.hyperai.hyperlpr3.HyperLPR3
import com.hyperai.hyperlpr3.bean.HyperLPRParameter
import com.rt.base.BaseApplication
import com.rt.base.arouter.ARouterMap
import com.rt.base.ext.i18N
import com.rt.base.help.ActivityCacheManager
import com.rt.base.util.ToastUtil
import com.rt.base.viewbase.VbBaseActivity
import com.rt.common.util.AppUtil
import com.rt.ipms_video.R
import com.rt.ipms_video.databinding.ActivityMainBinding
import com.rt.ipms_video.mvvm.viewmodel.MainViewModel
import com.rt.ipms_video.ui.activity.abnormal.BerthAbnormalActivity
import com.rt.ipms_video.ui.activity.income.IncomeCountingActivity
import com.rt.ipms_video.ui.activity.mine.LogoutActivity
import com.rt.ipms_video.ui.activity.mine.MineActivity
import com.rt.ipms_video.ui.activity.order.OrderMainActivity
import com.rt.ipms_video.ui.activity.parking.ParkingLotActivity

@Route(path = ARouterMap.MAIN)
class MainActivity : VbBaseActivity<MainViewModel, ActivityMainBinding>(), OnClickListener {

    override fun onSaveInstanceState(outState: Bundle) {
        // super.onSaveInstanceState(outState)
    }

    override fun initView() {
        initHyperLPR()
//        setStatusBarColor(com.rt.base.R.color.black, false)
    }

    override fun initListener() {
        binding.ivHead.setOnClickListener(this)
        binding.llParkingLot.setOnClickListener(this)
        binding.flIncomeCounting.setOnClickListener(this)
        binding.flOrder.setOnClickListener(this)
        binding.flBerthAbnormal.setOnClickListener(this)
        binding.flLogout.setOnClickListener(this)
    }

    override fun initData() {
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_head -> {
                val intent = Intent(this@MainActivity, MineActivity::class.java)
                startActivity(intent)
            }

            R.id.ll_parkingLot -> {
                val intent = Intent(this@MainActivity, ParkingLotActivity::class.java)
                startActivity(intent)
            }

            R.id.fl_incomeCounting -> {
                val intent = Intent(this@MainActivity, IncomeCountingActivity::class.java)
                startActivity(intent)
            }

            R.id.fl_order -> {
                val intent = Intent(this@MainActivity, OrderMainActivity::class.java)
                startActivity(intent)
            }

            R.id.fl_berthAbnormal -> {
                val intent = Intent(this@MainActivity, BerthAbnormalActivity::class.java)
                startActivity(intent)
            }

            R.id.fl_logout -> {
                val intent = Intent(this@MainActivity, LogoutActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun initHyperLPR() {
        // 车牌识别算法配置参数
        val parameter = HyperLPRParameter()
            .setDetLevel(HyperLPR3.DETECT_LEVEL_LOW)
            .setMaxNum(1)
            .setRecConfidenceThreshold(0.85f)
        // 初始化(仅执行一次生效)
        HyperLPR3.getInstance().init(BaseApplication.instance(), parameter)
    }

    override fun getVbBindingView(): ViewBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onReloadData() {

    }

    override fun providerVMClass(): Class<MainViewModel> {
        return MainViewModel::class.java
    }

    override val isFullScreen: Boolean
        get() = false

    override fun onBackPressedSupport() {
        if (AppUtil.isFastClick(1000)) {
            ActivityCacheManager.instance().getAllActivity().forEach {
                if (!it.isFinishing) {
                    it.finish()
                }
            }
        } else {
            ToastUtil.showToast(i18N(com.rt.base.R.string.再按一次退出程序))
        }
    }
}