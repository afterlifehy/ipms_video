package com.peakinfo.plateid.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.RelativeLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.fastjson.JSONObject
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.PathUtils
import com.hyperai.hyperlpr3.HyperLPR3
import com.hyperai.hyperlpr3.bean.HyperLPRParameter
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloadListener
import com.liulishuo.filedownloader.FileDownloader
import com.liulishuo.filedownloader.util.FileDownloadUtils
import com.peakinfo.base.BaseApplication
import com.peakinfo.base.arouter.ARouterMap
import com.peakinfo.base.bean.BlueToothDeviceBean
import com.peakinfo.base.bean.Street
import com.peakinfo.base.bean.UpdateBean
import com.peakinfo.base.ds.PreferencesDataStore
import com.peakinfo.base.ds.PreferencesKeys
import com.peakinfo.base.ext.i18N
import com.peakinfo.base.help.ActivityCacheManager
import com.peakinfo.base.util.ToastUtil
import com.peakinfo.base.viewbase.VbBaseActivity
import com.peakinfo.common.realm.RealmUtil
import com.peakinfo.common.util.AppUtil
import com.peakinfo.common.util.BluePrint
import com.peakinfo.plateid.R
import com.peakinfo.plateid.databinding.ActivityMainBinding
import com.peakinfo.plateid.dialog.UpdateDialog
import com.peakinfo.plateid.mvvm.viewmodel.MainViewModel
import com.peakinfo.plateid.pop.StreetPop
import com.peakinfo.plateid.ui.activity.abnormal.BerthAbnormalActivity
import com.peakinfo.plateid.ui.activity.income.IncomeCountingActivity
import com.peakinfo.plateid.ui.activity.mine.LogoutActivity
import com.peakinfo.plateid.ui.activity.mine.MineActivity
import com.peakinfo.plateid.ui.activity.order.OrderMainActivity
import com.peakinfo.plateid.ui.activity.parking.ParkingLotActivity
import com.peakinfo.plateid.util.UpdateUtil
import com.tbruyelle.rxpermissions3.RxPermissions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@Route(path = ARouterMap.MAIN)
class MainActivity : VbBaseActivity<MainViewModel, ActivityMainBinding>(), OnClickListener {
    var updateBean: UpdateBean? = null
    var streetPop: StreetPop? = null
    var streetList: MutableList<Street> = ArrayList()
    var currentStreet: Street? = null
    var updateDialog: UpdateDialog? = null

    override fun onSaveInstanceState(outState: Bundle) {
        // super.onSaveInstanceState(outState)
    }

    override fun initView() {
        initHyperLPR()
//        setStatusBarColor(com.peakinfo.base.R.color.black, false)
    }

    override fun initListener() {
        binding.ivHead.setOnClickListener(this)
        binding.tvTitle.setOnClickListener(this)
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
        connectBluePrint()

        if (currentStreet!!.streetName.indexOf("(") < 0) {
            binding.tvTitle.text = currentStreet!!.streetNo + currentStreet!!.streetName
        } else {
            binding.tvTitle.text =
                currentStreet!!.streetNo + currentStreet!!.streetName.substring(0, currentStreet!!.streetName.indexOf("("))
        }
        runBlocking {
            val lastTime = PreferencesDataStore(BaseApplication.instance()).getLong(PreferencesKeys.lastCheckUpdateTime)
            if (System.currentTimeMillis() - lastTime > 12 * 60 * 60 * 1000) {
                val param = HashMap<String, Any>()
                val jsonobject = JSONObject()
                jsonobject["version"] = AppUtils.getAppVersionName()
                param["attr"] = jsonobject
                mViewModel.checkUpdate(param)
            }
        }
    }

    @SuppressLint("CheckResult", "MissingPermission")
    fun connectBluePrint() {
        BluePrint.instance?.disConnect()
        if (RealmUtil.instance?.findCurrentDeviceList()!!.isNotEmpty()) {
            Thread {
                val device = RealmUtil.instance?.findCurrentDeviceList()!![0]
                if (device != null) {
                    BluePrint.instance?.connet(device.address)
                }
            }.start()
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                var rxPermissions = RxPermissions(this@MainActivity)
                rxPermissions.request(Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN).subscribe {
                    if (it) {
                        Thread {
                            BluePrint.instance?.disConnect()
                            val printList = BluePrint.instance?.blueToothDevice!!
                            if (printList.size == 1) {
                                val device = printList[0]
                                var connectResult = BluePrint.instance?.connet(device.address)
                                if (connectResult == 0) {
                                    RealmUtil.instance?.deleteAllDevice()
                                    RealmUtil.instance?.addRealm(BlueToothDeviceBean(device.address, device.name))
                                }
                            }
                        }.start()
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
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_head -> {
                val intent = Intent(this@MainActivity, MineActivity::class.java)
                startActivity(intent)
            }

            R.id.tv_title -> {
                streetPop = StreetPop(this@MainActivity, currentStreet, streetList, object : StreetPop.StreetSelectCallBack {
                    override fun selectStreet(street: Street) {
                        currentStreet = street
                        val old = RealmUtil.instance?.findCurrentStreet()
                        RealmUtil.instance?.updateCurrentStreet(currentStreet!!, old)
                        if (currentStreet!!.streetName.indexOf("(") < 0) {
                            binding.tvTitle.text = currentStreet!!.streetNo + currentStreet!!.streetName
                        } else {
                            binding.tvTitle.text =
                                currentStreet!!.streetNo + currentStreet!!.streetName.substring(0, currentStreet!!.streetName.indexOf("("))
                        }
                    }
                })
                streetPop?.showAsDropDown((v.parent) as RelativeLayout)
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

    @SuppressLint("NewApi")
    override fun startObserve() {
        super.startObserve()
        mViewModel.apply {
            checkUpdateLiveDate.observe(this@MainActivity) {
                updateBean = it
                UpdateUtil.instance?.checkNewVersion(updateBean!!, object : UpdateUtil.UpdateInterface {
                    override fun requestionPermission() {
                        requestPermissions()
                    }
                })
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("CheckResult")
    fun requestPermissions() {
        var rxPermissions = RxPermissions(this@MainActivity)
        rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe {
            if (it) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (packageManager.canRequestPackageInstalls()) {
                        UpdateUtil.instance?.downloadFileAndInstall()
                    } else {
                        val uri = Uri.parse("package:${AppUtils.getAppPackageName()}")
                        val intent =
                            Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, uri)
                        requestInstallPackageLauncher.launch(intent)
                    }
                } else {
                    UpdateUtil.instance?.downloadFileAndInstall()
                }
            } else {

            }
        }
    }

    val requestInstallPackageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            UpdateUtil.instance?.downloadFileAndInstall()
        } else {

        }
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
            ToastUtil.showMiddleToast(i18N(com.peakinfo.base.R.string.再按一次退出程序))
        }
    }
}