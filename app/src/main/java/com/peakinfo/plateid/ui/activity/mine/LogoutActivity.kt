package com.peakinfo.plateid.ui.activity.mine

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.view.View
import android.view.View.OnClickListener
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.alibaba.fastjson.JSONObject
import com.blankj.utilcode.util.TimeUtils
import com.peakinfo.base.BaseApplication
import com.peakinfo.base.arouter.ARouterMap
import com.peakinfo.base.dialog.DialogHelp
import com.peakinfo.base.ds.PreferencesDataStore
import com.peakinfo.base.ds.PreferencesKeys
import com.peakinfo.base.ext.i18N
import com.peakinfo.base.ext.i18n
import com.peakinfo.base.help.ActivityCacheManager
import com.peakinfo.base.util.ToastUtil
import com.peakinfo.base.viewbase.VbBaseActivity
import com.peakinfo.common.realm.RealmUtil
import com.peakinfo.plateid.R
import com.peakinfo.plateid.databinding.ActivityLogOutBinding
import com.peakinfo.plateid.mvvm.viewmodel.LogoutViewModel
import com.peakinfo.plateid.ui.activity.login.LoginActivity
import com.tbruyelle.rxpermissions3.RxPermissions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

@Route(path = ARouterMap.LOGOUT)
class LogoutActivity : VbBaseActivity<LogoutViewModel, ActivityLogOutBinding>(), OnClickListener {
    private var job: Job? = null
    var locationManager: LocationManager? = null
    var lat = 0.00
    var lon = 0.00
    var locationEnable = false

    @SuppressLint("MissingPermission", "CheckResult")
    override fun initView() {
        binding.layoutToolbar.tvTitle.text = i18n(com.peakinfo.base.R.string.签退)
        job = GlobalScope.launch(Dispatchers.IO) {
            while (true) {
                val time = TimeUtils.millis2String(System.currentTimeMillis(), "HH:mm:ss")
                withContext(Dispatchers.Main) {
                    binding.rtvHour1.text = time.split(":")[0][0].toString()
                    binding.rtvHour2.text = time.split(":")[0][1].toString()
                    binding.rtvMinute1.text = time.split(":")[1][0].toString()
                    binding.rtvMinute2.text = time.split(":")[1][1].toString()
                    binding.rtvSec1.text = time.split(":")[2][0].toString()
                    binding.rtvSec2.text = time.split(":")[2][1].toString()
                }
                delay(1000)
            }
        }

        var rxPermissions = RxPermissions(this@LogoutActivity)
        rxPermissions.request(Manifest.permission.ACCESS_FINE_LOCATION).subscribe {
            if (it) {
                locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
                val provider = LocationManager.NETWORK_PROVIDER
                locationManager?.requestLocationUpdates(provider, 1000, 1f, object : LocationListener {
                    override fun onLocationChanged(location: Location) {
                        lat = location.latitude
                        lon = location.longitude
                        locationEnable = true
                    }

                    override fun onProviderDisabled(provider: String) {
                        locationEnable = false
                        ToastUtil.showMiddleToast(i18N(com.peakinfo.base.R.string.请打开位置信息))
                    }

                    override fun onProviderEnabled(provider: String) {
                        locationEnable = true
                    }

                })
            }
        }
    }

    override fun initListener() {
        binding.layoutToolbar.flBack.setOnClickListener(this)
        binding.tvLogout.setOnClickListener(this)
    }

    override fun initData() {
        runBlocking {
            val loginName = PreferencesDataStore(BaseApplication.instance()).getString(PreferencesKeys.loginName)
            val workingHour = RealmUtil.instance?.findCurrentWorkingHour(loginName)
            if (workingHour != null) {
                binding.tvWorkingHours.text = TimeUtils.millis2String(workingHour.time, "yyyy-MM-dd HH:mm:ss")
            }
        }
    }

    @SuppressLint("MissingPermission", "CheckResult")
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fl_back -> {
                onBackPressedSupport()
            }

            R.id.tv_logout -> {
                var rxPermissions = RxPermissions(this@LogoutActivity)
                rxPermissions.request(Manifest.permission.ACCESS_FINE_LOCATION).subscribe {
                    if (it) {
                        DialogHelp.Builder().setTitle(i18N(com.peakinfo.base.R.string.确认签退))
                            .setLeftMsg(i18N(com.peakinfo.base.R.string.取消))
                            .setRightMsg(i18N(com.peakinfo.base.R.string.确定)).setCancelable(true)
                            .setOnButtonClickLinsener(object : DialogHelp.OnButtonClickLinsener {
                                override fun onLeftClickLinsener(msg: String) {
                                }

                                override fun onRightClickLinsener(msg: String) {
                                    if (locationManager == null) {
                                        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
                                        val provider = LocationManager.NETWORK_PROVIDER
                                        locationManager?.requestLocationUpdates(provider, 1000, 1f, object : LocationListener {
                                            override fun onLocationChanged(location: Location) {
                                                lat = location.latitude
                                                lon = location.longitude
                                                locationEnable = true
                                            }

                                            override fun onProviderDisabled(provider: String) {
                                                locationEnable = false
                                                ToastUtil.showMiddleToast(i18N(com.peakinfo.base.R.string.请打开位置信息))
                                            }

                                            override fun onProviderEnabled(provider: String) {
                                                locationEnable = true
                                            }
                                        })
                                    }
                                    if (locationEnable) {
                                        showProgressDialog(20000)
                                        runBlocking {
                                            val token =
                                                PreferencesDataStore(BaseApplication.baseApplication).getString(PreferencesKeys.token)
                                            val param = HashMap<String, Any>()
                                            val jsonobject = JSONObject()
                                            jsonobject["token"] = token
                                            jsonobject["longitude"] = lon
                                            jsonobject["latitude"] = lat
                                            param["attr"] = jsonobject
                                            mViewModel.logout(param)
                                        }
                                    } else {
                                        ToastUtil.showMiddleToast(i18N(com.peakinfo.base.R.string.请打开位置信息))
                                    }
                                }

                            }).build(this@LogoutActivity).showDailog()
                    }
                }
            }
        }
    }

    override fun startObserve() {
        super.startObserve()
        mViewModel.apply {
            logoutLiveData.observe(this@LogoutActivity) {
                dismissProgressDialog()
                ARouter.getInstance().build(ARouterMap.LOGIN).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
                for (i in ActivityCacheManager.instance().getAllActivity()) {
                    if (i is LoginActivity) {
                        i.finish()
                    }
                }
                ToastUtil.showMiddleToast(i18N(com.peakinfo.base.R.string.签退成功))
                runBlocking {
                    PreferencesDataStore(BaseApplication.instance()).putString(PreferencesKeys.token, "")
                    PreferencesDataStore(BaseApplication.instance()).putString(PreferencesKeys.phone, "")
                    PreferencesDataStore(BaseApplication.instance()).putString(PreferencesKeys.name, "")
                }
                RealmUtil.instance?.deleteAllStreet()
            }
            errMsg.observe(this@LogoutActivity) {
                dismissProgressDialog()
                ToastUtil.showMiddleToast(it.msg)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        GlobalScope.launch(Dispatchers.IO) {
            job?.cancelAndJoin()
        }
    }

    override fun getVbBindingView(): ViewBinding {
        return ActivityLogOutBinding.inflate(layoutInflater)
    }

    override fun onReloadData() {
    }

    override val isFullScreen: Boolean
        get() = true

    override fun marginStatusBarView(): View {
        return binding.layoutToolbar.ablToolbar
    }

    override fun providerVMClass(): Class<LogoutViewModel> {
        return LogoutViewModel::class.java
    }

}