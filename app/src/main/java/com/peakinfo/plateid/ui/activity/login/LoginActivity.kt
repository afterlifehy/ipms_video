package com.peakinfo.plateid.ui.activity.login

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.View.OnClickListener
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.alibaba.fastjson.JSONObject
import com.baidu.location.LocationClientOption
import com.blankj.utilcode.util.AppUtils
import com.peakinfo.base.BaseApplication
import com.peakinfo.base.arouter.ARouterMap
import com.peakinfo.base.bean.UpdateBean
import com.peakinfo.base.ds.PreferencesDataStore
import com.peakinfo.base.ds.PreferencesKeys
import com.peakinfo.base.ext.i18N
import com.peakinfo.base.util.ToastUtil
import com.peakinfo.base.viewbase.VbBaseActivity
import com.peakinfo.common.event.BaiduLocationLoginEvent
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
        }

        binding.tvVersion.text = "v" + AppUtils.getAppVersionName()
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
                    binding.rtvLogin.delegate.setBackgroundColor(
                        ContextCompat.getColor(
                            BaseApplication.instance(),
                            com.peakinfo.base.R.color.color_ff0371f4
                        )
                    )
                    binding.rtvLogin.setOnClickListener(this@LoginActivity)
                } else {
                    binding.rtvLogin.delegate.setBackgroundColor(
                        ContextCompat.getColor(
                            BaseApplication.instance(),
                            com.peakinfo.base.R.color.color_990371f4
                        )
                    )
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
                    binding.rtvLogin.delegate.setBackgroundColor(
                        ContextCompat.getColor(
                            BaseApplication.instance(),
                            com.peakinfo.base.R.color.color_ff0371f4
                        )
                    )
                    binding.rtvLogin.setOnClickListener(this@LoginActivity)
                } else {
                    binding.rtvLogin.delegate.setBackgroundColor(
                        ContextCompat.getColor(
                            BaseApplication.instance(),
                            com.peakinfo.base.R.color.color_990371f4
                        )
                    )
                    binding.rtvLogin.setOnClickListener(null)
                }
                binding.rtvLogin.delegate.init()
            }

        })
    }

    override fun initData() {
        val param = HashMap<String, Any>()
        val jsonobject = JSONObject()
        jsonobject["version"] = AppUtils.getAppVersionCode()
        param["attr"] = jsonobject
        mViewModel.checkUpdate(param)
    }

    @SuppressLint("CheckResult", "MissingPermission")
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_forgetPw -> {

            }

            R.id.rtv_login -> {
                var rxPermissions = RxPermissions(this@LoginActivity)
                if (locationEnable == 1) {
                    showProgressDialog(20000)
                    val param = HashMap<String, Any>()
                    val jsonobject = JSONObject()
                    try {
                        jsonobject["imei"] = (getSystemService(TELEPHONY_SERVICE) as TelephonyManager).imei
                    } catch (e: Exception) {
                        val manufacturer = Build.MANUFACTURER
                        val model = Build.MODEL
                        val id = manufacturer + model + " " + Settings.Secure.getString(
                            BaseApplication.instance().getContentResolver(),
                            Settings.Secure.ANDROID_ID
                        )
                        jsonobject["imei"] = id
                    }
                    jsonobject["loginName"] = binding.etAccount.text.toString()
                    jsonobject["password"] = binding.etPw.text.toString()
                    jsonobject["longitude"] = lon.toString()
                    jsonobject["latitude"] = lat.toString()
                    param["attr"] = jsonobject
                    mViewModel.verifyAccount(param)
                } else {
                    if (rxPermissions.isGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
                        ToastUtil.showMiddleToast(i18N(com.peakinfo.base.R.string.未获取到位置信息))
                    } else {
                        rxPermissions.request(Manifest.permission.ACCESS_FINE_LOCATION)
                            .subscribe {
                                if (it) {
                                    startBaiduMapLocation()
                                    baiduLocationUtil.startLocation()
                                } else if (!rxPermissions.isGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
                                    ToastUtil.showMiddleToast(i18N(com.peakinfo.base.R.string.请打开位置信息))
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
            verifyAccountLiveDate.observe(this@LoginActivity) {
                dismissProgressDialog()
                ARouter.getInstance().build(ARouterMap.STREET_CHOOSE).withParcelable(ARouterMap.LOGIN_INFO, it)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
            }
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
            errMsg.observe(this@LoginActivity) {
                dismissProgressDialog()
                ToastUtil.showMiddleToast(it.msg)
            }
            mException.observe(this@LoginActivity) {
                dismissProgressDialog()
            }
        }
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