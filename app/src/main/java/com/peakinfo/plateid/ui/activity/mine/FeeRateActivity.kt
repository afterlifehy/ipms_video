package com.peakinfo.plateid.ui.activity.mine

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import androidx.viewpager2.widget.ViewPager2
import com.alibaba.android.arouter.facade.annotation.Route
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.SizeUtils
import com.peakinfo.base.BaseApplication
import com.peakinfo.base.arouter.ARouterMap
import com.peakinfo.base.bean.Street
import com.peakinfo.base.ext.gone
import com.peakinfo.base.ext.hide
import com.peakinfo.base.ext.i18N
import com.peakinfo.base.ext.show
import com.peakinfo.base.viewbase.VbBaseActivity
import com.peakinfo.common.realm.RealmUtil
import com.peakinfo.common.util.GlideUtils
import com.peakinfo.common.view.flycotablayout.listener.OnTabSelectListener
import com.peakinfo.plateid.R
import com.peakinfo.plateid.adapter.FeeRatePagerAdapter
import com.peakinfo.plateid.databinding.ActivityFeeRateBinding
import com.peakinfo.plateid.mvvm.viewmodel.FeeRateViewModel
import com.peakinfo.plateid.ui.fragment.FeeRateFragment

@Route(path = ARouterMap.FEE_RATE)
class FeeRateActivity : VbBaseActivity<FeeRateViewModel, ActivityFeeRateBinding>(), OnClickListener {
    var tabList: MutableList<Street> = ArrayList()
    var fragmentList: MutableList<Fragment> = ArrayList()

    override fun initView() {
        GlideUtils.instance?.loadImage(binding.layoutToolbar.ivBack, com.peakinfo.common.R.mipmap.ic_back_white)
        binding.layoutToolbar.tvTitle.text = i18N(com.peakinfo.base.R.string.费率列表)
        binding.layoutToolbar.tvTitle.setTextColor(ContextCompat.getColor(BaseApplication.instance(), com.peakinfo.base.R.color.white))

    }

    override fun initListener() {
        binding.layoutToolbar.flBack.setOnClickListener(this)
        binding.ivArrowLeft.setOnClickListener(this)
        binding.ivArrowRight.setOnClickListener(this)
        binding.stlStreet.setOnTouchListener { p0, p1 -> true }
    }

    override fun initData() {
        tabList = RealmUtil.instance?.findCheckedStreetList() as MutableList<Street>
        if (tabList.size == 1) {
            binding.ivArrowRight.hide()
        }
        for (i in tabList) {
            val bundle = Bundle()
            val feeRateFragment = FeeRateFragment()
            bundle.putString("streetNo", i.streetNo)
            feeRateFragment.arguments = bundle
            fragmentList.add(feeRateFragment)
        }
        binding.vpFeeRate.adapter = FeeRatePagerAdapter(this@FeeRateActivity, fragmentList, tabList)
        binding.stlStreet.setViewPager(binding.vpFeeRate)
        binding.stlStreet.currentTab = 0
        binding.vpFeeRate.offscreenPageLimit = 2
        for (i in tabList.indices) {
            val tab = (binding.stlStreet.getChildAt(0) as LinearLayout).getChildAt(i) as RelativeLayout
            val tabTxt = tab.getChildAt(0) as TextView
            tabTxt.isSingleLine = false
            tabTxt.maxLines = 2
            val lp = tab.layoutParams
            lp.width = ScreenUtils.getScreenWidth() - SizeUtils.dp2px(72f)
            (binding.stlStreet.getChildAt(0) as LinearLayout).getChildAt(i).requestLayout()
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fl_back -> {
                onBackPressedSupport()
            }

            R.id.iv_arrowLeft -> {
                if (binding.vpFeeRate.currentItem > 0) {
                    binding.ivArrowLeft.show()
                    binding.vpFeeRate.setCurrentItem(binding.vpFeeRate.currentItem - 1, true)
                } else {
                    binding.ivArrowLeft.hide()
                }
            }

            R.id.iv_arrowRight -> {
                if (binding.vpFeeRate.currentItem < tabList.size - 1) {
                    binding.ivArrowRight.show()
                    binding.vpFeeRate.setCurrentItem(binding.vpFeeRate.currentItem + 1, true)
                } else {
                    binding.ivArrowRight.hide()
                }
            }
        }
    }

    override fun getVbBindingView(): ViewBinding {
        return ActivityFeeRateBinding.inflate(layoutInflater)
    }

    override fun onReloadData() {
    }

    override val isFullScreen: Boolean
        get() = true

    override fun marginStatusBarView(): View {
        return binding.layoutToolbar.ablToolbar
    }
}