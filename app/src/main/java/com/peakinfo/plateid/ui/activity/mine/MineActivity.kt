package com.peakinfo.plateid.ui.activity.mine

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.View
import android.view.View.OnClickListener
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.alibaba.fastjson.JSONObject
import com.blankj.utilcode.util.AppUtils
import com.peakinfo.base.BaseApplication
import com.peakinfo.base.arouter.ARouterMap
import com.peakinfo.base.bean.BlueToothDeviceBean
import com.peakinfo.base.bean.UpdateBean
import com.peakinfo.base.dialog.DialogHelp
import com.peakinfo.base.ds.PreferencesDataStore
import com.peakinfo.base.ds.PreferencesKeys
import com.peakinfo.base.ext.i18N
import com.peakinfo.base.help.ActivityCacheManager
import com.peakinfo.base.util.ToastUtil
import com.peakinfo.base.viewbase.VbBaseActivity
import com.peakinfo.common.realm.RealmUtil
import com.peakinfo.common.util.BluePrint
import com.peakinfo.common.util.GlideUtils
import com.peakinfo.plateid.BuildConfig
import com.peakinfo.plateid.R
import com.peakinfo.plateid.databinding.ActivityMineBinding
import com.peakinfo.plateid.dialog.BlueToothDeviceListDialog
import com.peakinfo.plateid.mvvm.viewmodel.MineViewModel
import com.peakinfo.plateid.ui.activity.login.LoginActivity
import com.peakinfo.plateid.util.UpdateUtil
import com.tbruyelle.rxpermissions3.RxPermissions
import kotlinx.coroutines.runBlocking

@Route(path = ARouterMap.MINE)
class MineActivity : VbBaseActivity<MineViewModel, ActivityMineBinding>(), OnClickListener {
    var updateBean: UpdateBean? = null
    var blueToothDeviceListDialog: BlueToothDeviceListDialog? = null
    var currentDevice: BlueToothDeviceBean? = null
    var bluePrintStatus = 0
    var mineBluePrint = 0

    @SuppressLint("CheckResult", "MissingPermission")
    override fun initView() {
        GlideUtils.instance?.loadImage(binding.layoutToolbar.ivBack, com.peakinfo.common.R.mipmap.ic_back_white)
        binding.layoutToolbar.tvTitle.text = i18N(com.peakinfo.base.R.string.我的)
        binding.layoutToolbar.tvTitle.setTextColor(ContextCompat.getColor(BaseApplication.instance(), com.peakinfo.base.R.color.white))

        mineBluePrint = intent.getIntExtra(ARouterMap.MINE_BLUE_PRINT, 0)
        if (BuildConfig.is_dev) {
            binding.tvVersion.text = AppUtils.getAppVersionName() + " Dev"
        } else {
            binding.tvVersion.text = AppUtils.getAppVersionName()
        }
    }

    override fun initListener() {
        binding.layoutToolbar.flBack.setOnClickListener(this)
        binding.flBaseInfo.setOnClickListener(this)
        binding.flVersion.setOnClickListener(this)
        binding.flFeeRate.setOnClickListener(this)
        binding.flBlueToothPrint.setOnClickListener(this)
        binding.rtvLogout.setOnClickListener(this)
    }

    override fun initData() {
        if (RealmUtil.instance?.findCurrentDeviceList()!!.isNotEmpty()) {
            currentDevice = RealmUtil.instance?.findCurrentDeviceList()!![0]
            if (currentDevice != null) {
                binding.tvDeviceName.text = currentDevice?.name
            }
        }
    }

    @SuppressLint("CheckResult")
    override fun onResume() {
        super.onResume()
        Thread {
            if (BluePrint.instance != null && BluePrint.instance!!.zpSDK != null) {
                try {
                    BluePrint.instance!!.zpSDK?.printerStatus()
                    when (BluePrint.instance!!.zpSDK?.GetStatus()) {
                        -1 -> {
                            bluePrintStatus = -1
                            runOnUiThread {
                                BluePrint.instance!!.zpSDK?.disconnect()
                                binding.tvDeviceName.text = ""
                            }
                        }

                        0 -> {
                            bluePrintStatus = 0
                        }

                        1 -> {
                            bluePrintStatus = 1
                            runOnUiThread {
                                ToastUtil.showMiddleToast(i18N(com.peakinfo.base.R.string.打印机缺纸))
                            }
                        }

                        2 -> {
                            bluePrintStatus = 2
                            runOnUiThread {
                                ToastUtil.showMiddleToast(i18N(com.peakinfo.base.R.string.打印机开盖))
                            }
                        }
                    }
                } catch (e: Exception) {
                    runOnUiThread {
//                        BluePrint.instance!!.zpSDK?.disconnect()
                        binding.tvDeviceName.text = ""
                    }
                }
            }
        }.start()
        if (mineBluePrint == 1) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                var rxPermissions = RxPermissions(this@MineActivity)
                rxPermissions.request(Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN).subscribe {
                    if (it) {
                        showBlueToothDeviceListDialog()
                    }
                }
            } else {
                showBlueToothDeviceListDialog()
            }
            mineBluePrint = 0
        }
    }

    @SuppressLint("CheckResult", "MissingPermission")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fl_back -> {
                onBackPressedSupport()
            }

            R.id.fl_baseInfo -> {
                ARouter.getInstance().build(ARouterMap.BASE_INFO).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
            }

            R.id.fl_version -> {
                val param = HashMap<String, Any>()
                val jsonobject = JSONObject()
                jsonobject["version"] = AppUtils.getAppVersionCode()
                param["attr"] = jsonobject
                mViewModel.checkUpdate(param)
            }

            R.id.fl_feeRate -> {
                ARouter.getInstance().build(ARouterMap.FEE_RATE).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
            }

            R.id.fl_blueToothPrint -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    var rxPermissions = RxPermissions(this@MineActivity)
                    rxPermissions.request(Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN).subscribe {
                        if (it) {
                            showBlueToothDeviceListDialog()
                        }
                    }
                } else {
                    showBlueToothDeviceListDialog()
                }
            }

            R.id.rtv_logout -> {
                DialogHelp.Builder().setTitle(i18N(com.peakinfo.base.R.string.是否确定进行不签退退出))
                    .setRightMsg(i18N(com.peakinfo.base.R.string.确定)).setCancelable(true)
                    .setLeftMsg(i18N(com.peakinfo.base.R.string.取消)).setCancelable(true)
                    .setOnButtonClickLinsener(object : DialogHelp.OnButtonClickLinsener {
                        override fun onLeftClickLinsener(msg: String) {
                        }

                        override fun onRightClickLinsener(msg: String) {
                            ARouter.getInstance().build(ARouterMap.LOGIN).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
                            for (i in ActivityCacheManager.instance().getAllActivity()) {
                                if (i !is LoginActivity) {
                                    i.finish()
                                }
                            }
                            runBlocking {
                                PreferencesDataStore(BaseApplication.instance()).putString(PreferencesKeys.token, "")
                                PreferencesDataStore(BaseApplication.instance()).putString(PreferencesKeys.phone, "")
                                PreferencesDataStore(BaseApplication.instance()).putString(PreferencesKeys.name, "")
                                PreferencesDataStore(BaseApplication.instance()).putString(PreferencesKeys.loginName, "")
                            }
                            RealmUtil.instance?.deleteAllStreet()
                        }

                    }).build(this@MineActivity).showDailog()
            }
        }
    }

    fun showBlueToothDeviceListDialog() {
        if (BluePrint.instance?.blueToothDevice!!.size > 0) {
            if (RealmUtil.instance?.findCurrentDeviceList()!!.isNotEmpty()) {
                currentDevice = RealmUtil.instance?.findCurrentDeviceList()!![0]
            }

            blueToothDeviceListDialog = BlueToothDeviceListDialog(
                BluePrint.instance?.blueToothDevice!!, if (bluePrintStatus == 0) currentDevice else null,
                object : BlueToothDeviceListDialog.BlueToothDeviceCallBack {
                    @SuppressLint("MissingPermission")
                    override fun chooseDevice(device: BluetoothDevice?) {
                        BluePrint.instance?.disConnect()
                        if (device != null) {
                            var connectResult = BluePrint.instance?.connet(device.address)
                            if (connectResult == 0) {
                                RealmUtil.instance?.deleteAllDevice()
                                RealmUtil.instance?.addRealm(BlueToothDeviceBean(device.address, device.name))
                                binding.tvDeviceName.text = device.name
                                bluePrintStatus = 0
                            } else {
                                bluePrintStatus = -1
                                return
                            }
                        } else {
                            bluePrintStatus = -1
                            binding.tvDeviceName.text = ""
                            ToastUtil.showMiddleToast(i18N(com.peakinfo.base.R.string.无打印机连接))
                        }
                    }
                })
            blueToothDeviceListDialog?.show()
        } else {
            DialogHelp.Builder().setTitle(i18N(com.peakinfo.base.R.string.未检测到已配对的打印设备))
                .setLeftMsg(i18N(com.peakinfo.base.R.string.取消))
                .setRightMsg(i18N(com.peakinfo.base.R.string.去配对)).setCancelable(true)
                .setOnButtonClickLinsener(object : DialogHelp.OnButtonClickLinsener {
                    override fun onLeftClickLinsener(msg: String) {
                    }

                    override fun onRightClickLinsener(msg: String) {
                        val intent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS)
                        startActivity(intent)
                    }

                }).build(this@MineActivity).showDailog()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("CheckResult")
    fun requestPermissions() {
        var rxPermissions = RxPermissions(this@MineActivity)
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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun startObserve() {
        super.startObserve()
        mViewModel.apply {
            checkUpdateLiveDate.observe(this@MineActivity) {
                updateBean = it
                if (updateBean?.state == "0") {
                    UpdateUtil.instance?.checkNewVersion(updateBean!!, object : UpdateUtil.UpdateInterface {
                        override fun requestionPermission() {
                            requestPermissions()
                        }
                    })
                } else {
                    ToastUtil.showMiddleToast("当前已是最新版本")
                }
            }
            errMsg.observe(this@MineActivity) {
                ToastUtil.showMiddleToast(it.msg)
            }
            mException.observe(this@MineActivity) {
                dismissProgressDialog()
            }
        }
    }

    override fun getVbBindingView(): ViewBinding {
        return ActivityMineBinding.inflate(layoutInflater)
    }

    override fun onReloadData() {
    }

    override val isFullScreen: Boolean
        get() = true

    override fun marginStatusBarView(): View {
        return binding.layoutToolbar.ablToolbar
    }

    override fun providerVMClass(): Class<MineViewModel> {
        return MineViewModel::class.java
    }
}