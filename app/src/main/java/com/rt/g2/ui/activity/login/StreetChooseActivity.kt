package com.rt.g2.ui.activity.login

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import android.view.View
import android.view.View.OnClickListener
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
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
import com.rt.base.bean.LoginBean
import com.rt.base.bean.Street
import com.rt.base.bean.WorkingHoursBean
import com.rt.base.ds.PreferencesDataStore
import com.rt.base.ds.PreferencesKeys
import com.rt.base.ext.i18N
import com.rt.base.util.ToastUtil
import com.rt.base.viewbase.VbBaseActivity
import com.rt.common.realm.RealmUtil
import com.rt.common.util.AppUtil
import com.rt.common.util.BaiduLocationUtil
import com.rt.g2.R
import com.rt.g2.adapter.StreetChoosedAdapter
import com.rt.g2.databinding.ActivityStreetChooseBinding
import com.rt.g2.dialog.StreetChooseListDialog
import com.rt.g2.mvvm.viewmodel.StreetChooseViewModel
import com.tbruyelle.rxpermissions3.RxPermissions
import kotlinx.coroutines.runBlocking

@Route(path = ARouterMap.STREET_CHOOSE)
class StreetChooseActivity : VbBaseActivity<StreetChooseViewModel, ActivityStreetChooseBinding>(),
    OnClickListener {
    var streetList: MutableList<Street> = ArrayList()
    var streetChooseListDialog: StreetChooseListDialog? = null
    var streetChoosedAdapter: StreetChoosedAdapter? = null
    var streetChoosedList: MutableList<Street> = ArrayList()
    var loginInfo: LoginBean? = null

    lateinit var baiduLocationUtil: BaiduLocationUtil
    var lat = 0.00
    var lon = 0.00

    @SuppressLint("CheckResult", "MissingPermission")
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun initView() {
        binding.layoutToolbar.tvTitle.text = i18N(com.rt.base.R.string.路段选择)

        loginInfo = intent.getParcelableExtra(ARouterMap.LOGIN_INFO) as? LoginBean

        binding.rvStreet.setHasFixedSize(true)
        binding.rvStreet.layoutManager = LinearLayoutManager(this)
        streetChoosedAdapter = StreetChoosedAdapter(streetChoosedList, this)
        binding.rvStreet.adapter = streetChoosedAdapter

        var rxPermissions = RxPermissions(this@StreetChooseActivity)
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
        binding.rflAddStreet.setOnClickListener(this)
        binding.rtvEnterWorkBench.setOnClickListener(this)
    }

    override fun initData() {
        streetList.clear()
        streetList = loginInfo?.result as MutableList<Street>
    }

    @SuppressLint("CheckResult", "MissingPermission")
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fl_back -> {
                onBackPressedSupport()
            }

            R.id.rfl_addStreet -> {
                streetChooseListDialog =
                    StreetChooseListDialog(streetList, streetChoosedList, object : StreetChooseListDialog.StreetChooseCallBack {
                        override fun chooseStreets() {
                            streetChoosedAdapter?.setList(streetChoosedList.distinct())
                        }

                    })
                streetChooseListDialog?.show()
            }

            R.id.rtv_enterWorkBench -> {
                val rxPermissions = RxPermissions(this@StreetChooseActivity)
                if (rxPermissions.isGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    if (baiduLocationUtil == null) {
                        startBaiduMapLocation()
                    }
                    login2()
                } else {
                    if (rxPermissions.isGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
                        ToastUtil.showBottomToast(i18N(com.rt.base.R.string.未获取到位置信息))
                    } else {
                        rxPermissions.request(Manifest.permission.ACCESS_FINE_LOCATION).subscribe {
                            if (it) {
                                startBaiduMapLocation()
                                login2()
                            } else {
                                ToastUtil.showBottomToast(i18N(com.rt.base.R.string.请打开位置信息))
                            }
                        }
                    }
                }
            }

            R.id.rfl_delete -> {
                if (!AppUtil.isFastClick(500)) {
                    val item = v.tag as Street
                    val position = streetChoosedList.indexOf(item)
                    streetChoosedList.remove(item)
                    streetChoosedAdapter?.removeAt(position)
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun login2() {
        if (streetChoosedList.isNotEmpty()) {
            showProgressDialog(20000)
            runBlocking {
                val longitude = PreferencesDataStore(BaseApplication.baseApplication).getDouble(PreferencesKeys.lon)
                val latitude = PreferencesDataStore(BaseApplication.baseApplication).getDouble(PreferencesKeys.lat)
                val param = HashMap<String, Any>()
                val jsonobject = JSONObject()
                jsonobject["loginName"] = loginInfo?.loginName
                jsonobject["streetNo"] = streetChoosedList[0].streetNo
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
                mViewModel.login2(param)
            }
        } else {
            ToastUtil.showBottomToast(i18N(com.rt.base.R.string.请添加路段))
        }
    }

    override fun startObserve() {
        super.startObserve()
        mViewModel.apply {
            login2LiveData.observe(this@StreetChooseActivity) {
                dismissProgressDialog()
                for (i in streetChoosedList) {
                    RealmUtil.instance?.updateStreetChoosed(i)
                }
                val streetList = loginInfo?.result as ArrayList<Street>
                runBlocking {
                    PreferencesDataStore(BaseApplication.instance()).putString(PreferencesKeys.token, it.token)
                    PreferencesDataStore(BaseApplication.instance()).putString(PreferencesKeys.phone, loginInfo!!.phone.toString())
                    PreferencesDataStore(BaseApplication.instance()).putString(PreferencesKeys.name, loginInfo!!.name.toString())
                    PreferencesDataStore(BaseApplication.instance()).putString(
                        PreferencesKeys.account,
                        loginInfo!!.loginName.toString()
                    )
                }
                RealmUtil.instance?.deleteAllStreet()
                RealmUtil.instance?.addRealmAsyncList(streetChoosedList)
                RealmUtil.instance?.updateCurrentStreet(streetChoosedList[0], null)

                val workingHoursBean = RealmUtil.instance?.findCurrentWorkingHour(loginInfo!!.loginName.toString())
                if (workingHoursBean != null) {
                    val lastDay = TimeUtils.millis2String(workingHoursBean.time, "yyyy-MM-dd")
                    val currentDay = TimeUtils.millis2String(System.currentTimeMillis(), "yyyy-MM-dd")
                    if (lastDay != currentDay) {
                        RealmUtil.instance?.addRealm(WorkingHoursBean(loginInfo!!.loginName.toString(), System.currentTimeMillis()))
                    }
                } else {
                    RealmUtil.instance?.addRealm(WorkingHoursBean(loginInfo!!.loginName.toString(), System.currentTimeMillis()))
                }
                ARouter.getInstance().build(ARouterMap.MAIN).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
            }
            errMsg.observe(this@StreetChooseActivity) {
                dismissProgressDialog()
                ToastUtil.showBottomToast(it.msg)
            }
            mException.observe(this@StreetChooseActivity) {
                dismissProgressDialog()
            }
        }
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