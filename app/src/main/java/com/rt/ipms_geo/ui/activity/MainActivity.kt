package com.rt.ipms_geo.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.telephony.TelephonyManager
import android.view.View
import android.view.View.OnClickListener
import android.widget.PopupWindow.OnDismissListener
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.alibaba.fastjson.JSONObject
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.PhoneUtils
import com.hyperai.hyperlpr3.HyperLPR3
import com.hyperai.hyperlpr3.bean.HyperLPRParameter
import com.rt.base.BaseApplication
import com.rt.base.arouter.ARouterMap
import com.rt.base.bean.BlueToothDeviceBean
import com.rt.base.bean.Street
import com.rt.base.dialog.DialogHelp
import com.rt.base.ds.PreferencesDataStore
import com.rt.base.ds.PreferencesKeys
import com.rt.base.ext.i18N
import com.rt.base.help.ActivityCacheManager
import com.rt.base.util.Constant
import com.rt.base.util.ToastUtil
import com.rt.base.viewbase.VbBaseActivity
import com.rt.common.event.CurrentStreetUpdateEvent
import com.rt.common.realm.RealmUtil
import com.rt.common.util.AppUtil
import com.rt.common.util.BluePrint
import com.rt.ipms_geo.R
import com.rt.ipms_geo.ca.ui.activity.CALogoutActivity
import com.rt.ipms_geo.ca.ui.activity.CAParkingLotActivity
import com.rt.ipms_geo.databinding.ActivityMainBinding
import com.rt.ipms_geo.mvvm.viewmodel.MainViewModel
import com.rt.ipms_geo.pop.StreetPop
import com.rt.ipms_geo.ui.activity.abnormal.BerthAbnormalActivity
import com.rt.ipms_geo.ui.activity.income.IncomeCountingActivity
import com.rt.ipms_geo.ui.activity.login.LoginActivity
import com.rt.ipms_geo.ui.activity.mine.LogoutActivity
import com.rt.ipms_geo.ui.activity.mine.MineActivity
import com.rt.ipms_geo.ui.activity.order.OrderMainActivity
import com.rt.ipms_geo.ui.activity.parking.ParkingLotActivity
import com.tbruyelle.rxpermissions3.RxPermissions
import kotlinx.coroutines.runBlocking
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@Route(path = ARouterMap.MAIN)
class MainActivity : VbBaseActivity<MainViewModel, ActivityMainBinding>(), OnClickListener {
    var streetPop: StreetPop? = null
    var streetList: MutableList<Street> = ArrayList()
    var currentStreet: Street? = null
    var tempStreet: Street? = null

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(currentStreetUpdateEvent: CurrentStreetUpdateEvent) {
        currentStreet = currentStreetUpdateEvent.street
        if (currentStreet!!.streetName.indexOf("(") < 0) {
            binding.tvTitle.text = currentStreet!!.streetNo + currentStreet!!.streetName
        } else {
            binding.tvTitle.text =
                currentStreet!!.streetNo + currentStreet!!.streetName.substring(0, currentStreet!!.streetName.indexOf("("))
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        // super.onSaveInstanceState(outState)
    }

    override fun initView() {
        initHyperLPR()
    }

    override fun initListener() {
        binding.ivHead.setOnClickListener(this)
        binding.llParkingLot.setOnClickListener(this)
        binding.flIncomeCounting.setOnClickListener(this)
        binding.flOrder.setOnClickListener(this)
        binding.flBerthAbnormal.setOnClickListener(this)
        binding.flLogout.setOnClickListener(this)
    }

    @SuppressLint("SetTextI18n")
    override fun initData() {
        streetList = RealmUtil.instance?.findCheckedStreetList() as MutableList<Street>
        currentStreet = RealmUtil.instance?.findCurrentStreet()
//        connectBluePrint()

        if (currentStreet!!.streetName.indexOf("(") < 0) {
            binding.tvTitle.text = currentStreet!!.streetNo + currentStreet!!.streetName
        } else {
            binding.tvTitle.text =
                currentStreet!!.streetNo + currentStreet!!.streetName.substring(0, currentStreet!!.streetName.indexOf("("))
        }
        if (streetList.size == 1) {
            binding.tvTitle.setCompoundDrawables(null, null, null, null)
            binding.tvTitle.setOnClickListener(null)
        } else {
            binding.tvTitle.setOnClickListener(this)
        }
    }

    @SuppressLint("CheckResult", "MissingPermission")
    fun connectBluePrint() {
        BluePrint.instance?.disConnect()
        if (RealmUtil.instance?.findCurrentDeviceList()!!.isNotEmpty()) {
            Thread {
                val device = RealmUtil.instance?.findCurrentDeviceList()!![0]
                if (device != null) {
                    val printResult = BluePrint.instance?.connet(device.address)
                    if (printResult != 0) {
                        runOnUiThread {
                            if (!isFinishing && !isDestroyed) {
                                DialogHelp.Builder().setTitle(i18N(com.rt.base.R.string.打印机连接失败需要手动连接))
                                    .setLeftMsg(i18N(com.rt.base.R.string.取消))
                                    .setRightMsg(i18N(com.rt.base.R.string.去连接)).setCancelable(true)
                                    .setOnButtonClickLinsener(object : DialogHelp.OnButtonClickLinsener {
                                        override fun onLeftClickLinsener(msg: String) {
                                        }

                                        override fun onRightClickLinsener(msg: String) {
                                            val intent = Intent(this@MainActivity, MineActivity::class.java)
                                            startActivity(intent)
                                        }

                                    }).build(this@MainActivity).showDailog()
                            }
                        }
                    }
                }
            }.start()
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                var rxPermissions = RxPermissions(this@MainActivity)
                rxPermissions.request(Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN).subscribe {
                    if (it) {
                        BluePrint.instance?.disConnect()
                        val printList = BluePrint.instance?.blueToothDevice!!
                        if (printList.size == 1) {
                            Thread {
                                val device = printList[0]
                                var connectResult = BluePrint.instance?.connet(device.address)
                                if (connectResult == 0) {
                                    RealmUtil.instance?.deleteAllDevice()
                                    RealmUtil.instance?.addRealm(BlueToothDeviceBean(device.address, device.name))
                                }
                            }.start()
                        } else if (printList.size > 1) {
                            multipleDevice()
                        } else {
                            DialogHelp.Builder().setTitle(i18N(com.rt.base.R.string.未检测到已配对的打印设备))
                                .setLeftMsg(i18N(com.rt.base.R.string.取消))
                                .setRightMsg(i18N(com.rt.base.R.string.去配对)).setCancelable(true)
                                .setOnButtonClickLinsener(object : DialogHelp.OnButtonClickLinsener {
                                    override fun onLeftClickLinsener(msg: String) {
                                    }

                                    override fun onRightClickLinsener(msg: String) {
                                        val intent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS)
                                        startActivity(intent)
                                    }

                                }).build(this@MainActivity).showDailog()
                        }
                    }
                }
            } else {
                val printList = BluePrint.instance?.blueToothDevice!!
                if (printList.size == 1) {
                    val device = printList[0]
                    Thread {
                        var connectResult = BluePrint.instance?.connet(device.address)
                        if (connectResult == 0) {
                            RealmUtil.instance?.deleteAllDevice()
                            RealmUtil.instance?.addRealm(BlueToothDeviceBean(device.address, device.name))
                        }
                    }.start()
                } else if (printList.size > 1) {
                    multipleDevice()
                } else {
                    DialogHelp.Builder().setTitle(i18N(com.rt.base.R.string.未检测到已配对的打印设备))
                        .setLeftMsg(i18N(com.rt.base.R.string.取消))
                        .setRightMsg(i18N(com.rt.base.R.string.去配对)).setCancelable(true)
                        .setOnButtonClickLinsener(object : DialogHelp.OnButtonClickLinsener {
                            override fun onLeftClickLinsener(msg: String) {
                            }

                            override fun onRightClickLinsener(msg: String) {
                                val intent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS)
                                startActivity(intent)
                            }

                        }).build(this@MainActivity).showDailog()
                }
            }
        }
    }

    fun multipleDevice() {
        DialogHelp.Builder().setTitle(i18N(com.rt.base.R.string.检测到存在多台打印设备需手动连接))
            .setLeftMsg(i18N(com.rt.base.R.string.取消))
            .setRightMsg(i18N(com.rt.base.R.string.去连接)).setCancelable(true)
            .setOnButtonClickLinsener(object : DialogHelp.OnButtonClickLinsener {
                override fun onLeftClickLinsener(msg: String) {
                }

                override fun onRightClickLinsener(msg: String) {
                    val intent = Intent(this@MainActivity, MineActivity::class.java)
                    startActivity(intent)
                }

            }).build(this@MainActivity).showDailog()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_head -> {
                val intent = Intent(this@MainActivity, MineActivity::class.java)
                startActivity(intent)
            }

            R.id.tv_title -> {
                streetPop = StreetPop(this@MainActivity, currentStreet, streetList, object : StreetPop.StreetSelectCallBack {
                    @SuppressLint("MissingPermission")
                    override fun selectStreet(street: Street) {
                        if (street.streetNo == currentStreet!!.streetNo) {
                            return
                        }
                        showProgressDialog(20000)
                        tempStreet = street
                        runBlocking {
                            val token = PreferencesDataStore(BaseApplication.baseApplication).getString(PreferencesKeys.token)
                            val longitude = PreferencesDataStore(BaseApplication.baseApplication).getDouble(PreferencesKeys.lon)
                            val latitude = PreferencesDataStore(BaseApplication.baseApplication).getDouble(PreferencesKeys.lat)
                            val param = HashMap<String, Any>()
                            val jsonobject = JSONObject()
                            jsonobject["token"] = token
                            jsonobject["longitude"] = longitude.toString()
                            jsonobject["latitude"] = latitude.toString()
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                try {
                                    jsonobject["imei"] = (getSystemService(TELEPHONY_SERVICE) as TelephonyManager).imei
                                    jsonobject["simId"] = (getSystemService(TELEPHONY_SERVICE) as TelephonyManager).simSerialNumber
                                } catch (e: Exception) {
                                    val manufacturer = Build.MANUFACTURER
                                    val model = Build.MODEL
                                    val id =
                                        manufacturer + model + " " + Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
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
                })
                streetPop?.showAsDropDown((v.parent) as RelativeLayout)
                val upDrawable = ContextCompat.getDrawable(BaseApplication.instance(), com.rt.common.R.mipmap.ic_arrow_up)
                upDrawable?.setBounds(0, 0, upDrawable.intrinsicWidth, upDrawable.intrinsicHeight)
                binding.tvTitle.setCompoundDrawables(
                    null,
                    null,
                    upDrawable,
                    null
                )
                streetPop?.setOnDismissListener(object : OnDismissListener {
                    override fun onDismiss() {
                        val downDrawable = ContextCompat.getDrawable(BaseApplication.instance(), com.rt.common.R.mipmap.ic_arrow_down)
                        downDrawable?.setBounds(0, 0, downDrawable.intrinsicWidth, downDrawable.intrinsicHeight)
                        binding.tvTitle.setCompoundDrawables(
                            null,
                            null,
                            downDrawable,
                            null
                        )
                    }
                })
            }

            R.id.ll_parkingLot -> {
                if (Constant.APP_ID.isEmpty()) {
                    val intent = Intent(this@MainActivity, ParkingLotActivity::class.java)
                    startActivity(intent)
                } else {
                    val intent = Intent(this@MainActivity, CAParkingLotActivity::class.java)
                    startActivity(intent)
                }
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
                if (Constant.APP_ID.isEmpty()) {
                    val intent = Intent(this@MainActivity, LogoutActivity::class.java)
                    startActivity(intent)
                }else{
                    val intent = Intent(this@MainActivity, CALogoutActivity::class.java)
                    startActivity(intent)
                }
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

    @SuppressLint("NewApi", "MissingPermission")
    override fun startObserve() {
        super.startObserve()
        mViewModel.apply {
            logoutLiveData.observe(this@MainActivity) {
                runBlocking {
                    PreferencesDataStore(BaseApplication.instance()).putString(PreferencesKeys.token, "")
                    val loginName = PreferencesDataStore(BaseApplication.instance()).getString(PreferencesKeys.account)
                    val longitude = PreferencesDataStore(BaseApplication.instance()).getDouble(PreferencesKeys.lon)
                    val latitude = PreferencesDataStore(BaseApplication.instance()).getDouble(PreferencesKeys.lat)
                    val param = HashMap<String, Any>()
                    val jsonobject = JSONObject()
                    jsonobject["loginName"] = loginName
                    jsonobject["streetNo"] = tempStreet?.streetNo
                    jsonobject["longitude"] = longitude
                    jsonobject["latitude"] = latitude
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
            }
            login2LiveData.observe(this@MainActivity) {
                dismissProgressDialog()
                currentStreet = tempStreet
                val old = RealmUtil.instance?.findCurrentStreet()
                RealmUtil.instance?.updateCurrentStreet(currentStreet!!, old)
                runBlocking {
                    PreferencesDataStore(BaseApplication.instance()).putString(PreferencesKeys.token, it.token)
                }
                ToastUtil.showBottomToast("${currentStreet?.streetName}签到成功")
                if (currentStreet!!.streetName.indexOf("(") < 0) {
                    binding.tvTitle.text = currentStreet!!.streetNo + currentStreet!!.streetName
                } else {
                    binding.tvTitle.text =
                        currentStreet!!.streetNo + currentStreet!!.streetName.substring(0, currentStreet!!.streetName.indexOf("("))
                }
            }
            errMsg.observe(this@MainActivity) {
                dismissProgressDialog()
                ToastUtil.showBottomToast(it.msg)
                if (it.api == "login2") {
                    ToastUtil.showBottomToast("${currentStreet?.streetName}签到失败")
                    runBlocking {
                        PreferencesDataStore(BaseApplication.instance()).putString(PreferencesKeys.token, "")
                        PreferencesDataStore(BaseApplication.instance()).putString(PreferencesKeys.phone, "")
                        PreferencesDataStore(BaseApplication.instance()).putString(PreferencesKeys.name, "")
                        PreferencesDataStore(BaseApplication.instance()).putString(PreferencesKeys.account, "")
                    }
                    RealmUtil.instance?.deleteAllStreet()
                    ARouter.getInstance().build(ARouterMap.LOGIN).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
                    for (i in ActivityCacheManager.instance().getAllActivity()) {
                        if (i !is LoginActivity) {
                            i.finish()
                        }
                    }
                }
            }
        }
    }

    override fun getVbBindingView(): ViewBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onReloadData() {

    }

    override fun isRegEventBus(): Boolean {
        return true
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
            ToastUtil.showBottomToast(i18N(com.rt.base.R.string.再按一次退出程序))
        }
    }
}