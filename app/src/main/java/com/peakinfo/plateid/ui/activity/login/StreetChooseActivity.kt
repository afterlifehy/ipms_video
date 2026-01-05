package com.peakinfo.plateid.ui.activity.login

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.facade.annotation.Route
import com.baidu.location.LocationClientOption
import com.blankj.utilcode.util.ColorUtils
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.peakinfo.base.BaseApplication
import com.peakinfo.base.arouter.ARouterMap
import com.peakinfo.base.bean.LoginBean
import com.peakinfo.base.bean.Street
import com.peakinfo.base.ds.PreferencesDataStore
import com.peakinfo.base.ds.PreferencesKeys
import com.peakinfo.base.ext.bindFragment
import com.peakinfo.base.ext.i18N
import com.peakinfo.base.viewbase.VbBaseActivity
import com.peakinfo.common.util.BaiduLocationUtil
import com.peakinfo.plateid.R
import com.peakinfo.plateid.adapter.StreetChoosedAdapter
import com.peakinfo.plateid.databinding.ActivityStreetChooseBinding
import com.peakinfo.plateid.dialog.StreetChooseListDialog
import com.peakinfo.plateid.mvvm.viewmodel.StreetChooseViewModel
import com.peakinfo.plateid.service.HeartbeatService
import com.peakinfo.plateid.ui.fragment.ChooseStreetCAFragment
import com.peakinfo.plateid.ui.fragment.ChooseStreetNonCAFragment
import com.tbruyelle.rxpermissions3.RxPermissions
import kotlinx.coroutines.runBlocking

@Route(path = ARouterMap.STREET_CHOOSE)
class StreetChooseActivity : VbBaseActivity<StreetChooseViewModel, ActivityStreetChooseBinding>(),
    OnClickListener {
    var loginInfo: LoginBean? = null

    lateinit var baiduLocationUtil: BaiduLocationUtil
    var lat = 0.00
    var lon = 0.00
    var currentTab = 0
    var nonCaStreetList: MutableList<Street> = ArrayList()
    var caStreetList: MutableList<Street> = ArrayList()

    @SuppressLint("CheckResult", "MissingPermission")
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun initView() {
        binding.layoutToolbar.tvTitle.text = i18N(com.peakinfo.base.R.string.路段选择)

        loginInfo = intent.getParcelableExtra(ARouterMap.LOGIN_INFO) as? LoginBean
        nonCaStreetList.clear()
        loginInfo?.result?.let { nonCaStreetList.addAll(it) }
        caStreetList.clear()
        loginInfo?.caStreetList?.let { caStreetList.addAll(it) }

        var rxPermissions = RxPermissions(this@StreetChooseActivity)
        rxPermissions.request(Manifest.permission.ACCESS_FINE_LOCATION).subscribe {
            if (it) {
                startBaiduMapLocation()
                baiduLocationUtil.startLocation()
            }
        }
        binding.vpStreet.offscreenPageLimit = 1
        if (binding.vpStreet.adapter == null) {
            binding.vpStreet.offscreenPageLimit = 1
            binding.vpStreet.bindFragment(this) {
                listOf(
                    ChooseStreetNonCAFragment.newInstance(loginInfo!!),
                    ChooseStreetCAFragment.newInstance(loginInfo!!) // 使用静态工厂方法
                )
            }
        }

        binding.tlStreet.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                currentTab = tab!!.position
                tab.customView?.findViewById<TextView>(R.id.tab_text)?.setTextColor(ColorUtils.getColor(R.color.black))
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab?.customView?.findViewById<TextView>(R.id.tab_text)?.setTextColor(ColorUtils.getColor(R.color.white))
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

        })

        TabLayoutMediator(binding.tlStreet, binding.vpStreet) { tab: TabLayout.Tab, i: Int ->
            when (i) {
                0 -> {
                    tab.customView = LayoutInflater.from(this).inflate(R.layout.item_tab_left, null)
                }

                else -> {
                    tab.customView = LayoutInflater.from(this).inflate(R.layout.item_tab_right, null)
                }
            }
        }.attach()
    }

    fun startBaiduMapLocation() {
        baiduLocationUtil = BaiduLocationUtil()
        baiduLocationUtil.initBaiduLocation()
        val callback = object : BaiduLocationUtil.BaiduLocationCallBack {
            override fun locationChange(
                lon: Double,
                lat: Double,
                location: LocationClientOption?,
                isSuccess: Boolean,
                address: String?
            ) {
                if (isSuccess) {
                    this@StreetChooseActivity.lat = lat
                    this@StreetChooseActivity.lon = lon
                    runBlocking {
                        PreferencesDataStore(BaseApplication.instance()).putDouble(PreferencesKeys.lat, lat)
                        PreferencesDataStore(BaseApplication.instance()).putDouble(PreferencesKeys.lon, lon)
                    }
                }
            }
        }
        baiduLocationUtil.setBaiduLocationCallBack(callback)
    }

    override fun initListener() {
        binding.layoutToolbar.flBack.setOnClickListener(this)
    }

    override fun initData() {
    }

    @SuppressLint("CheckResult", "MissingPermission")
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fl_back -> {
                onBackPressedSupport()
            }
        }
    }

    override fun startObserve() {
        super.startObserve()
    }

    override fun getVbBindingView(): ViewBinding {
        return ActivityStreetChooseBinding.inflate(layoutInflater)
    }

    override fun onReloadData() {
    }

    override val isFullScreen: Boolean
        get() = true

    override fun providerVMClass(): Class<StreetChooseViewModel> {
        return StreetChooseViewModel::class.java
    }

    override fun marginStatusBarView(): View {
        return binding.layoutToolbar.ablToolbar
    }
}