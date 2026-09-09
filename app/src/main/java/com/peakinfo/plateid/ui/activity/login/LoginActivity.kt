package com.peakinfo.plateid.ui.activity.login

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.telephony.TelephonyManager
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.view.View.OnClickListener
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.fastjson.JSONObject
import com.baidu.location.LocationClientOption
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.PhoneUtils
import com.peakinfo.base.BaseApplication
import com.peakinfo.base.arouter.ARouterMap
import com.peakinfo.base.bean.Street
import com.peakinfo.base.bean.UpdateBean
import com.peakinfo.base.bean.ca.QuerySimBean
import com.peakinfo.base.ds.PreferencesDataStore
import com.peakinfo.base.ds.PreferencesKeys
import com.peakinfo.base.ext.i18N
import com.peakinfo.base.ext.startAct
import com.peakinfo.base.ext.startArouter
import com.peakinfo.base.util.Constant
import com.peakinfo.base.util.ToastUtil
import com.peakinfo.base.viewbase.VbBaseActivity
import com.peakinfo.common.event.BaiduLocationLoginEvent
import com.peakinfo.common.util.AppUtil
import com.peakinfo.common.util.BaiduLocationUtil
import com.peakinfo.plateid.R
import com.peakinfo.plateid.databinding.ActivityLoginBinding
import com.peakinfo.plateid.mvvm.viewmodel.LoginViewModel
import com.peakinfo.plateid.util.UpdateUtil
import com.tbruyelle.rxpermissions3.RxPermissions
import kotlinx.coroutines.runBlocking
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


@Route(path = ARouterMap.LOGIN)
class LoginActivity : VbBaseActivity<LoginViewModel, ActivityLoginBinding>(), OnClickListener {
    lateinit var baiduLocationUtil: BaiduLocationUtil
    var lat = 121.445345
    var lon = 31.238665
    var updateBean: UpdateBean? = null
    var locationEnable = 0
    var querySimBean: QuerySimBean? = null
    var streetList: MutableList<Street> = ArrayList()

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(baiduLocationLoginEvent: BaiduLocationLoginEvent) {
        startBaiduMapLocation()
    }

    @SuppressLint("CheckResult")
    override fun initView() {
        binding.tvVersion.text = "v" + AppUtils.getAppVersionName()
        if (AppUtils.isAppInstalled("com.rt.ipms_video")) {
            AppUtils.uninstallApp("com.rt.ipms_video")
        }
        if (AppUtils.isAppInstalled("com.rt.ipms_geo")) {
            val info = packageManager.getPackageInfo("com.rt.ipms_geo", 0)
            val versionName = info.versionName
            if (versionName == "2.4.9") {
                AppUtils.uninstallApp("com.rt.ipms_geo")
            }
        }
        var rxPermissions = RxPermissions(this@LoginActivity)
        rxPermissions.request(
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_PHONE_STATE
        ).subscribe {
            if (rxPermissions.isGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
                startBaiduMapLocation()
                baiduLocationUtil.startLocation()
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onResume() {
        super.onResume()
        var rxPermissions = RxPermissions(this@LoginActivity)
        if (rxPermissions.isGranted(Manifest.permission.READ_PHONE_STATE)) {
            val manufacturer = Build.MANUFACTURER
            val model = Build.MODEL
            val id = manufacturer + model + " " + Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                try {
                    Constant.imei = (getSystemService(TELEPHONY_SERVICE) as TelephonyManager).imei
                } catch (e: Exception) {
                    Constant.imei = id
                }
                try {
                    Constant.simId = (getSystemService(TELEPHONY_SERVICE) as TelephonyManager).simSerialNumber
                } catch (e: Exception) {
                    Constant.simId = ""
                }
            } else {
                try {
                    Constant.imei = PhoneUtils.getIMEI()
                } catch (e: Exception) {
                    Constant.imei = id
                }
                try {
                    Constant.simId = (getSystemService(TELEPHONY_SERVICE) as TelephonyManager).simSerialNumber
                } catch (e: Exception) {
                    Constant.simId = ""
                }
            }
            if (Constant.imei == "868946066209045"||Constant.imei.startsWith("samsungSM-G9810")) {
                Constant.simId = "huyong"
                Constant.imei = "868946066209045"
            }
            Constant.deviceId = AppUtil.getDeviceId()
            checkUpdateApi()
            if (querySimBean == null) {
                querySim()
            }
        }
    }

    fun startBaiduMapLocation() {
        baiduLocationUtil = BaiduLocationUtil()
        baiduLocationUtil.initBaiduLocation()
        val callback = object : BaiduLocationUtil.BaiduLocationCallBack {
            override fun locationChange(lon: Double, lat: Double, location: LocationClientOption?, isSuccess: Boolean, address: String?) {
                if (isSuccess) {
                    this@LoginActivity.lat = lat
                    this@LoginActivity.lon = lon
                    runBlocking {
                        PreferencesDataStore(BaseApplication.instance()).putDouble(PreferencesKeys.lat, lat)
                        PreferencesDataStore(BaseApplication.instance()).putDouble(PreferencesKeys.lon, lon)
                    }
                    locationEnable = 1
                } else {
                    locationEnable = -1
                }
            }

        }
        baiduLocationUtil.setBaiduLocationCallBack(callback)
    }


    override fun initListener() {
        binding.tvForgetPw.setOnClickListener(this)
        binding.etAccount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                if (binding.etPw.text.isNotEmpty() && p0!!.isNotEmpty()) {
                    binding.rtvLogin.alpha = 1f
                    binding.rtvLogin.setOnClickListener(this@LoginActivity)
                } else {
                    binding.rtvLogin.alpha = 0.2f
                    binding.rtvLogin.setOnClickListener(null)
                }
                binding.rtvLogin.delegate.init()
            }

        })
        binding.etAccount.setOnEditorActionListener { textView, i, keyEvent ->
            binding.etPw.requestFocus()
        }
        binding.etPw.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                if (binding.etAccount.text.isNotEmpty() && p0!!.isNotEmpty()) {
                    binding.rtvLogin.alpha = 1f
                    binding.rtvLogin.setOnClickListener(this@LoginActivity)
                } else {
                    binding.rtvLogin.alpha = 0.2f
                    binding.rtvLogin.setOnClickListener(null)
                }
                binding.rtvLogin.delegate.init()
            }

        })
    }

    override fun initData() {
    }

    @SuppressLint("CheckResult", "MissingPermission")
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.rtv_login -> {
                var rxPermissions = RxPermissions(this@LoginActivity)
                if (locationEnable == 1) {
                    showProgressDialog(20000)
                    queryPwStatus()
                } else {
                    if (rxPermissions.isGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
                        ToastUtil.showBottomToast(i18N(com.peakinfo.base.R.string.未获取到位置信息))
                    } else {
                        rxPermissions.request(Manifest.permission.ACCESS_FINE_LOCATION)
                            .subscribe {
                                if (it) {
                                    startBaiduMapLocation()
                                    baiduLocationUtil.startLocation()
                                } else if (!rxPermissions.isGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
                                    ToastUtil.showBottomToast(i18N(com.peakinfo.base.R.string.请打开位置信息))
                                }
                            }
                    }
                }
            }
        }
    }

    override fun startObserve() {
        super.startObserve()
        mViewModel.apply {
            checkUpdateLiveDate.observe(this@LoginActivity) {
                updateBean = it
                if (updateBean?.state == "0") {
                    UpdateUtil.instance?.checkNewVersion(updateBean!!, object : UpdateUtil.UpdateInterface {
                        override fun requestionPermission() {
                            requestPermissions()
                        }

                        override fun install(path: String) {

                        }
                    })
                }
            }
            querySimLiveData.observe(this@LoginActivity) {
                querySimBean = it
                if (querySimBean?.result != null && querySimBean?.result!!.isNotEmpty()) {
                    streetList = querySimBean?.result as MutableList<Street>
                } else {
                }
//                val targetAppid = it.appIdLast
//                streetList.firstOrNull { targetAppid.isNotEmpty() && it.appId == targetAppid }?.let { matchedStreet ->
//                    matchedStreet.ischeck = true
//                    Constant.APP_ID = matchedStreet.appId
//                    Constant.PASSWORD = matchedStreet.password
//                    streetChoosedList.apply {
//                        clear()
//                        add(matchedStreet)
//                    }
//                    binding.tvStreet.text = matchedStreet.streetName
//                }
                runBlocking {
                    val certSn = PreferencesDataStore(BaseApplication.instance()).getString(PreferencesKeys.certSn)
                    if (it.state == "1") {
                        ToastUtil.showBottomToast("设备未注册", 1)
                        return@runBlocking
                    } else if (it.state == "2") {
                        Constant.needCert = true
                    } else if (it.state == "3") {
                        Constant.refreshCert = true
                    }
                    Constant.certSn = it.certSn.toString()
                    runBlocking {
                        PreferencesDataStore(BaseApplication.instance()).putString(PreferencesKeys.certSn, it.certSn.toString())
                    }
                    Constant.code = it.code
                    Constant.unitName = it.unitName
                    Constant.unitId = it.unitId
                    Constant.pin = it.pin
                }
            }
            queryPwStatusLiveData.observe(this@LoginActivity) {
                if (it.editPw == 0) {
                    verifyAccount()
                } else {
                    dismissProgressDialog()
                    startArouter(ARouterMap.RESET_PW, data = Bundle().apply {
                        putString(ARouterMap.RESET_PW_ACCOUNT, binding.etAccount.text.toString())
                    })
                    binding.etPw.setText("")
                }
            }
            verifyAccountLiveDate.observe(this@LoginActivity) {
                dismissProgressDialog()
                it.caStreetList?.clear()
                it.caStreetList?.addAll(streetList)
                runBlocking {
                    PreferencesDataStore(BaseApplication.instance()).putString(PreferencesKeys.account, it.loginName.toString())
                    startAct<StreetChooseActivity>(data = Bundle().apply {
                        putParcelable(ARouterMap.LOGIN_INFO, it)
                    })
                }
            }
            errMsg.observe(this@LoginActivity) {
                ToastUtil.showBottomToast(it.msg)
                dismissProgressDialog()
            }
            mException.observe(this@LoginActivity) {
                dismissProgressDialog()
            }
        }
    }

    fun querySim() {
        val param = HashMap<String, Any>()
        val jsonObject = JSONObject()
        if (Constant.imei == "868946066209045") {
            Constant.simId = "huyong"
        }
        jsonObject["simId"] = Constant.simId
        jsonObject["deviceId"] = Constant.deviceId
        jsonObject["imeiId"] = Constant.imei
        jsonObject["cardType"] = AppUtil.getSimType()
        param["attr"] = jsonObject
        mViewModel.querySim(param)
    }

    fun queryPwStatus() {
        val param = HashMap<String, Any>()
        val jsonobject = JSONObject()
        jsonobject["loginName"] = binding.etAccount.text.toString()
        param["attr"] = jsonobject
        mViewModel.queryPwStatus(param)
    }

    @SuppressLint("MissingPermission")
    fun verifyAccount() {
        val param = HashMap<String, Any>()
        val jsonobject = JSONObject()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                jsonobject["imei"] = (getSystemService(TELEPHONY_SERVICE) as TelephonyManager).imei
            } catch (e: Exception) {
                val manufacturer = Build.MANUFACTURER
                val model = Build.MODEL
                val id = manufacturer + model + " " + Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
                jsonobject["imei"] = id
            }
        } else {
            jsonobject["imei"] = PhoneUtils.getIMEI()
        }
        jsonobject["loginName"] = binding.etAccount.text.toString()
        jsonobject["password"] = binding.etPw.text.toString()
        jsonobject["longitude"] = lon.toString()
        jsonobject["latitude"] = lat.toString()
        param["attr"] = jsonobject
        mViewModel.verifyAccount(param)
    }

    fun checkUpdateApi() {
        val param = HashMap<String, Any>()
        val jsonobject = JSONObject()
        jsonobject["version"] = AppUtils.getAppVersionCode()
        jsonobject["imei"] = Constant.imei
        jsonobject["softType"] = "11"
        param["attr"] = jsonobject
        mViewModel.checkUpdate(param)
    }

    @SuppressLint("CheckResult")
    fun requestPermissions() {
        var rxPermissions = RxPermissions(this@LoginActivity)
        rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe {
            if (it) {
                UpdateUtil.instance?.downloadFileAndInstall(object : UpdateUtil.UpdateInterface {
                    override fun requestionPermission() {

                    }

                    override fun install(path: String) {
                        AppUtils.installApp(path)
                    }
                })
            } else {

            }
        }
    }

    override fun getVbBindingView(): ViewBinding {
        return ActivityLoginBinding.inflate(layoutInflater)
    }

    override fun onReloadData() {
    }

    override fun providerVMClass(): Class<LoginViewModel>? {
        return LoginViewModel::class.java
    }

    override fun isRegEventBus(): Boolean {
        return true
    }

    override val isFullScreen: Boolean
        get() = false

}