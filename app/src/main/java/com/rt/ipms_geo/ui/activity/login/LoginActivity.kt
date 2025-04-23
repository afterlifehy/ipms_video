package com.rt.ipms_geo.ui.activity.login

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
import com.blankj.utilcode.util.EncryptUtils
import com.blankj.utilcode.util.PhoneUtils
import com.custle.ksmkey.MKeyApi
import com.rt.base.BaseApplication
import com.rt.base.arouter.ARouterMap
import com.rt.base.bean.Street
import com.rt.base.bean.UpdateBean
import com.rt.base.bean.ca.QuerySimBean
import com.rt.base.ds.PreferencesDataStore
import com.rt.base.ds.PreferencesKeys
import com.rt.base.ext.gone
import com.rt.base.ext.i18N
import com.rt.base.ext.show
import com.rt.base.ext.startAct
import com.rt.base.ext.startArouter
import com.rt.base.util.Constant
import com.rt.base.util.ToastUtil
import com.rt.base.viewbase.VbBaseActivity
import com.rt.common.event.BaiduLocationLoginEvent
import com.rt.common.realm.RealmUtil
import com.rt.common.util.AppUtil
import com.rt.common.util.BaiduLocationUtil
import com.rt.ipms_geo.R
import com.rt.ipms_geo.databinding.ActivityLoginBinding
import com.rt.ipms_geo.dialog.StreetChooseListDialog
import com.rt.ipms_geo.mvvm.viewmodel.LoginViewModel
import com.rt.ipms_geo.ui.activity.MainActivity
import com.rt.ipms_geo.util.UpdateUtil
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
    var needLogin = false
    var querySimBean: QuerySimBean? = null
    var streetListDialog: StreetChooseListDialog? = null
    var streetList: MutableList<Street> = ArrayList()
    var streetChoosedList: MutableList<Street> = ArrayList()

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(baiduLocationLoginEvent: BaiduLocationLoginEvent) {
        startBaiduMapLocation()
    }

    @SuppressLint("CheckResult", "MissingPermission")
    override fun initView() {
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
            if (rxPermissions.isGranted(Manifest.permission.READ_PHONE_STATE)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    try {
                        Constant.imei = (getSystemService(TELEPHONY_SERVICE) as TelephonyManager).imei
                        Constant.simId = (getSystemService(TELEPHONY_SERVICE) as TelephonyManager).simSerialNumber
                    } catch (e: Exception) {
                        val manufacturer = Build.MANUFACTURER
                        val model = Build.MODEL
                        val id = manufacturer + model + " " + Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
                        Constant.imei = id
                        Constant.simId = id
                    }
                } else {
                    Constant.imei = PhoneUtils.getIMEI()
                    Constant.simId = (getSystemService(TELEPHONY_SERVICE) as TelephonyManager).simSerialNumber
                }
                Constant.deviceId = AppUtil.getDeviceId()
                checkUpdate()
                querySim()
            }
        }

        binding.tvVersion.text = "v" + AppUtils.getAppVersionName()
        if (AppUtils.isAppInstalled("com.peakinfo.plateid")) {
            AppUtils.uninstallApp("com.peakinfo.plateid")
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
        binding.cbStreet.setOnClickListener(this)
        binding.rflStreet.setOnClickListener(this)
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
                        ToastUtil.showBottomToast(i18N(com.rt.base.R.string.未获取到位置信息))
                    } else {
                        rxPermissions.request(Manifest.permission.ACCESS_FINE_LOCATION)
                            .subscribe {
                                if (it) {
                                    startBaiduMapLocation()
                                    baiduLocationUtil.startLocation()
                                } else if (!rxPermissions.isGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
                                    ToastUtil.showBottomToast(i18N(com.rt.base.R.string.请打开位置信息))
                                }
                            }
                    }
                }
            }

            R.id.cb_street,
            R.id.rfl_street -> {
                if (streetList.isEmpty()) {
                    return
                }
                binding.cbStreet.isChecked = true
                streetListDialog =
                    StreetChooseListDialog(streetList, streetChoosedList, object : StreetChooseListDialog.StreetChooseCallBack {
                        override fun chooseStreets() {
                            if (streetChoosedList.isNotEmpty()) {
                                val firstStreet = streetChoosedList[0]
                                binding.tvStreet.text = firstStreet.streetName
                                Constant.APP_ID = firstStreet.appId
                                Constant.PASSWORD = firstStreet.password
                                if (binding.etAccount.text.isNotEmpty() && binding.etPw.text.isNotEmpty()) {
                                    binding.rtvLogin.setOnClickListener(this@LoginActivity)
                                    binding.rtvLogin.alpha = 1f
                                } else {
                                    binding.rtvLogin.setOnClickListener(null)
                                    binding.rtvLogin.alpha = 0.2f
                                }
                            } else {
                                binding.tvStreet.text = ""
                                Constant.APP_ID = ""
                                Constant.PASSWORD = ""
                                if (binding.etAccount.text.isNotEmpty() && binding.etPw.text.isNotEmpty()) {
                                    binding.rtvLogin.setOnClickListener(this@LoginActivity)
                                    binding.rtvLogin.alpha = 1f
                                } else {
                                    binding.rtvLogin.setOnClickListener(null)
                                    binding.rtvLogin.alpha = 0.2f
                                }
                            }
                        }

                    })
                streetListDialog?.show()
                streetListDialog?.setOnDismissListener {
                    binding.cbStreet.isChecked = false
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
                    binding.rflStreet.show()
                    streetList = querySimBean?.result as MutableList<Street>
                } else {
                    binding.rflStreet.gone()
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
                    } else if (it.state == "2" && TextUtils.isEmpty(certSn)) {
                        Constant.needCert = true
                    } else if (it.state == "3") {
                        Constant.refreshCert = true
                    }
                    Constant.certSn = it.certSn.toString()
                    runBlocking {
                        PreferencesDataStore(BaseApplication.instance()).putString(PreferencesKeys.certSn, it.certSn.toString())
                    }
                    Constant.code = it.code
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
                if (streetChoosedList.isNotEmpty()) {
                    caLogin()
                } else {
                    dismissProgressDialog()
                    runBlocking {
                        PreferencesDataStore(BaseApplication.instance()).putString(PreferencesKeys.account, it.loginName.toString())
                        startAct<StreetChooseActivity>(data = Bundle().apply {
                            putParcelable(ARouterMap.LOGIN_INFO, it)
                        })
                    }
                }
            }
            caLoginLiveData.observe(this@LoginActivity) {
                dismissProgressDialog()
                val userId = binding.etAccount.text.toString()
                runBlocking {
                    PreferencesDataStore(BaseApplication.instance()).putString(PreferencesKeys.account, userId)
                    PreferencesDataStore(BaseApplication.instance()).putString(PreferencesKeys.token, it.token)
                }
                RealmUtil.instance?.deleteAllStreet()
                RealmUtil.instance?.addRealmAsyncList(streetChoosedList)
                RealmUtil.instance?.updateCurrentStreet(streetChoosedList[0], null)
                if (it != null && it.token != "") {
                    startAct<MainActivity>()
                    logInOutNotice("1")
                } else {
                    ToastUtil.showBottomToast("登录失败, 响应结果为空或token为空!", 1)
                }
            }
            tokenLiveData.observe(this@LoginActivity) {
                logout(it.token.toString())
            }
            logoutLiveData.observe(this@LoginActivity) {
                logInOutNotice("2")
                if (needLogin) {
                    queryPwStatus()
                    needLogin = false
                } else {
                    dismissProgressDialog()
                    ToastUtil.showBottomToast("签退成功", 0)
                }
            }
            errMsg.observe(this@LoginActivity) {
                ToastUtil.showBottomToast(it.msg)
                if (it.api == "caLogin") {
                    if (it.code == 1012) {
                        needLogin = true
                        token()
                    } else {
                        dismissProgressDialog()
                        val param = HashMap<String, Any>()
                        param["deviceId"] = Constant.deviceId
                        param["deviceCode"] = Constant.deviceId
                        mViewModel.refreshCert(param)
                    }
//                    else if (it.code == 3006) {
//                        CAInterceptor.caClient.getCertInfo("") {
//                            if (it.code == 0) {
//                                val jsonObject = org.json.JSONObject(it.data)
//                                val certSn = jsonObject.getString("certSn")
//                                Constant.certSn = certSn
//                                runBlocking {
//                                    PreferencesDataStore(BaseApplication.instance()).putString(PreferencesKeys.certSn, certSn)
//                                }
//                                notifyUpdateCert(certSn)
//                            }
//                        }
//                    }
                } else {
                    dismissProgressDialog()
                }
            }
            mException.observe(this@LoginActivity) {
                dismissProgressDialog()
            }
        }
    }

    private fun notifyUpdateCert(certSn: String) {
        if (!TextUtils.isEmpty(certSn)) {
            val param = HashMap<String, Any>()
            val jsonobject = JSONObject()
            jsonobject["deviceId"] = MKeyApi.getDeviceId(BaseApplication.instance())
            jsonobject["certSn"] = certSn
            param["attr"] = jsonobject
            mViewModel.notifyUpdateCert(param)
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

    fun caLogin() {
        val passwordMD5 = EncryptUtils.encryptMD5ToString(binding.etAccount.text.toString()).lowercase()
        val param = HashMap<String, Any>()
        param["userId"] = binding.etAccount.text.toString()
        param["deviceId"] = Constant.deviceId
        param["simId"] = Constant.simId
        param["password"] = passwordMD5
        param["longitude"] = lon
        param["latitude"] = lat
        param["dataTime"] = System.currentTimeMillis()
        mViewModel.caLogin(param)
    }

    fun checkUpdate() {
        val param = HashMap<String, Any>()
        val jsonobject = JSONObject()
        jsonobject["version"] = AppUtils.getAppVersionCode()
        jsonobject["imei"] = Constant.imei
        jsonobject["softType"] = "11"
        param["attr"] = jsonobject
        mViewModel.checkUpdate(param)
    }

    fun token() {
        val userId = binding.etAccount.text.toString()
        val password = binding.etPw.text.toString()
        if (userId.isEmpty()) {
            ToastUtil.showBottomToast("请输入账号")
            return
        }
        if (password.isEmpty()) {
            ToastUtil.showBottomToast("请输入密码")
            return
        }
        ToastUtil.showBottomToast("正在签退, 请稍后...", 0)
        val passwordMD5 = EncryptUtils.encryptMD5ToString(userId).lowercase()
        val param = HashMap<String, Any>()
        param["userId"] = userId
        param["simId"] = Constant.simId
        param["password"] = passwordMD5
        param["dataTime"] = System.currentTimeMillis()
        mViewModel.token(param)
    }

    fun logout(token: String) {
        val userId = binding.etAccount.text.toString()
        val passwordMD5 = EncryptUtils.encryptMD5ToString(userId).lowercase()
        val param = HashMap<String, Any>()
        param["token"] = token
        param["userId"] = userId
        param["deviceId"] = Constant.deviceId
        param["simId"] = Constant.simId
        param["password"] = passwordMD5
        param["longitude"] = lon.toString()
        param["latitude"] = lat.toString()
        param["dataTime"] = System.currentTimeMillis()
        mViewModel.logout(param)
    }

    fun logInOutNotice(state: String) {
        val param = HashMap<String, Any>()
        val jsonobject = JSONObject()
        jsonobject["imei"] = Constant.imei
        jsonobject["loginName"] = binding.etAccount.text.toString()
        jsonobject["simId"] = Constant.simId
        jsonobject["state"] = state
        jsonobject["version"] = AppUtils.getAppVersionName()
        param["attr"] = jsonobject
        mViewModel.logInOutNotice(param)
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