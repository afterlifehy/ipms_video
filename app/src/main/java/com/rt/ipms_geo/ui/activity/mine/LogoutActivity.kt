package com.rt.ipms_geo.ui.activity.mine

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import android.view.View
import android.view.View.OnClickListener
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.alibaba.fastjson.JSONObject
import com.baidu.location.LocationClientOption
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.PhoneUtils
import com.blankj.utilcode.util.TimeUtils
import com.rt.base.BaseApplication
import com.rt.base.arouter.ARouterMap
import com.rt.base.dialog.DialogHelp
import com.rt.base.ds.PreferencesDataStore
import com.rt.base.ds.PreferencesKeys
import com.rt.base.ext.i18N
import com.rt.base.ext.i18n
import com.rt.base.help.ActivityCacheManager
import com.rt.base.util.ToastUtil
import com.rt.base.viewbase.VbBaseActivity
import com.rt.common.event.BaiduLocationLoginEvent
import com.rt.common.realm.RealmUtil
import com.rt.common.util.BaiduLocationUtil
import com.rt.ipms_geo.R
import com.rt.ipms_geo.databinding.ActivityLogOutBinding
import com.rt.ipms_geo.mvvm.viewmodel.LogoutViewModel
import com.rt.ipms_geo.ui.activity.login.LoginActivity
import com.tbruyelle.rxpermissions3.RxPermissions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus

@Route(path = ARouterMap.LOGOUT)
class LogoutActivity : VbBaseActivity<LogoutViewModel, ActivityLogOutBinding>(), OnClickListener {
    private var job: Job? = null
    lateinit var baiduLocationUtil: BaiduLocationUtil
    var lat = 0.00
    var lon = 0.00

    @SuppressLint("MissingPermission", "CheckResult")
    override fun initView() {
        binding.layoutToolbar.tvTitle.text = i18n(com.rt.base.R.string.签退)
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
                startBaiduMapLocation()
                baiduLocationUtil.startLocation()
            }
        }
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
                    this@LogoutActivity.lat = lat
                    this@LogoutActivity.lon = lon
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
        binding.tvLogout.setOnClickListener(this)
    }

    override fun initData() {
        runBlocking {
            val loginName = PreferencesDataStore(BaseApplication.instance()).getString(PreferencesKeys.account)
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
                if (rxPermissions.isGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    logout()
                } else {
                    rxPermissions.request(Manifest.permission.ACCESS_FINE_LOCATION)
                        .subscribe {
                            if (it) {
                                startBaiduMapLocation()
                                logout()
                            } else {
                                ToastUtil.showBottomToast(i18N(com.rt.base.R.string.请打开位置信息))
                            }
                        }
                }
            }
        }
    }

    fun logout() {
        DialogHelp.Builder().setTitle(i18N(com.rt.base.R.string.确认签退))
            .setLeftMsg(i18N(com.rt.base.R.string.取消))
            .setRightMsg(i18N(com.rt.base.R.string.确定)).setCancelable(true)
            .setOnButtonClickLinsener(object : DialogHelp.OnButtonClickLinsener {
                override fun onLeftClickLinsener(msg: String) {
                }

                @SuppressLint("MissingPermission")
                override fun onRightClickLinsener(msg: String) {
                    showProgressDialog(20000)
                    runBlocking {
                        val token = PreferencesDataStore(BaseApplication.baseApplication).getString(PreferencesKeys.token)
                        val longitude = PreferencesDataStore(BaseApplication.instance()).getDouble(PreferencesKeys.lon)
                        val latitude = PreferencesDataStore(BaseApplication.instance()).getDouble(PreferencesKeys.lat)
                        val param = HashMap<String, Any>()
                        val jsonobject = JSONObject()
                        jsonobject["token"] = token
                        jsonobject["longitude"] = lon.takeIf { it != 0.0 }?.toString() ?: longitude.toString()
                        jsonobject["latitude"] = lat.takeIf { it != 0.0 }?.toString() ?: latitude.toString()
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            try {
                                jsonobject["imei"] = (getSystemService(TELEPHONY_SERVICE) as TelephonyManager).imei
                                jsonobject["simId"] = (getSystemService(TELEPHONY_SERVICE) as TelephonyManager).simSerialNumber
                            } catch (e: Exception) {
                                val manufacturer = Build.MANUFACTURER
                                val model = Build.MODEL
                                val id = manufacturer + model + " " + Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
                                jsonobject["imei"] = id
                                jsonobject["simId"] = id
                            }
                        } else {
                            jsonobject["imei"] = PhoneUtils.getIMEI()
                            jsonobject["simId"] = (getSystemService(TELEPHONY_SERVICE) as TelephonyManager).simSerialNumber
                        }
                        jsonobject["version"] = AppUtils.getAppVersionName()
                        param["attr"] = jsonobject
                        mViewModel.logout(param)
                    }
                }
            }).build(this@LogoutActivity).showDailog()
    }

    override fun startObserve() {
        super.startObserve()
        mViewModel.apply {
            logoutLiveData.observe(this@LogoutActivity) {
                dismissProgressDialog()
                ARouter.getInstance().build(ARouterMap.LOGIN).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
                EventBus.getDefault().post(BaiduLocationLoginEvent())
                for (i in ActivityCacheManager.instance().getAllActivity()) {
                    if (i !is LoginActivity) {
                        i.finish()
                    }
                }
                ToastUtil.showBottomToast(i18N(com.rt.base.R.string.签退成功))
                runBlocking {
                    PreferencesDataStore(BaseApplication.instance()).putString(PreferencesKeys.token, "")
                    PreferencesDataStore(BaseApplication.instance()).putString(PreferencesKeys.phone, "")
                    PreferencesDataStore(BaseApplication.instance()).putString(PreferencesKeys.name, "")
                    PreferencesDataStore(BaseApplication.instance()).putString(PreferencesKeys.account, "")
                }
                RealmUtil.instance?.deleteAllStreet()
            }
            errMsg.observe(this@LogoutActivity) {
                dismissProgressDialog()
                ToastUtil.showBottomToast(it.msg)
            }
            mException.observe(this@LogoutActivity) {
                dismissProgressDialog()
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