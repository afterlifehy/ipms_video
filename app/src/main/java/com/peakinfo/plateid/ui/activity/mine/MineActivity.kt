package com.peakinfo.plateid.ui.activity.mine

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
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
import com.blankj.utilcode.util.PathUtils
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloadListener
import com.liulishuo.filedownloader.FileDownloader
import com.liulishuo.filedownloader.util.FileDownloadUtils
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
import com.peakinfo.plateid.R
import com.peakinfo.plateid.databinding.ActivityMineBinding
import com.peakinfo.plateid.dialog.BlueToothDeviceListDialog
import com.peakinfo.plateid.mvvm.viewmodel.MineViewModel
import com.tbruyelle.rxpermissions3.RxPermissions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@Route(path = ARouterMap.MINE)
class MineActivity : VbBaseActivity<MineViewModel, ActivityMineBinding>(), OnClickListener {
    var updateBean: UpdateBean? = null
    var blueToothDeviceListDialog: BlueToothDeviceListDialog? = null
    var currentDevice: BlueToothDeviceBean? = null

    @SuppressLint("CheckResult", "MissingPermission")
    override fun initView() {
        GlideUtils.instance?.loadImage(binding.layoutToolbar.ivBack, com.peakinfo.common.R.mipmap.ic_back_white)
        binding.layoutToolbar.tvTitle.text = i18N(com.peakinfo.base.R.string.我的)
        binding.layoutToolbar.tvTitle.setTextColor(ContextCompat.getColor(BaseApplication.instance(), com.peakinfo.base.R.color.white))

        binding.tvVersion.text = AppUtils.getAppVersionName()
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
        runBlocking {
            val lastTime = PreferencesDataStore(BaseApplication.instance()).getLong(PreferencesKeys.lastCheckUpdateTime)
            if (System.currentTimeMillis() - lastTime > 12 * 60 * 60 * 1000) {
                val param = HashMap<String, Any>()
                val jsonobject = JSONObject()
                jsonobject["version"] = AppUtils.getAppVersionCode()
                param["attr"] = jsonobject
                mViewModel.checkUpdate(param)
            }
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
                            ActivityCacheManager.instance().getCurrentActivity()?.finish()
                            runBlocking {
                                PreferencesDataStore(BaseApplication.instance()).putString(PreferencesKeys.token, "")
                                PreferencesDataStore(BaseApplication.instance()).putString(PreferencesKeys.phone, "")
                                PreferencesDataStore(BaseApplication.instance()).putString(PreferencesKeys.name, "")
                            }
                            RealmUtil.instance?.deleteAllStreet()
                        }

                    }).build(this@MineActivity).showDailog()
            }
        }
    }

    fun showBlueToothDeviceListDialog() {
        if (RealmUtil.instance?.findCurrentDeviceList()!!.isNotEmpty()) {
            currentDevice = RealmUtil.instance?.findCurrentDeviceList()!![0]
        }
        blueToothDeviceListDialog = BlueToothDeviceListDialog(
            BluePrint.instance?.blueToothDevice!!, currentDevice,
            object : BlueToothDeviceListDialog.BlueToothDeviceCallBack {
                @SuppressLint("MissingPermission")
                override fun chooseDevice(device: BluetoothDevice) {
                    BluePrint.instance?.disConnect()
                    var connectResult = BluePrint.instance?.connet(device.address)
                    if (connectResult == 0) {
                        RealmUtil.instance?.deleteAllDevice()
                        RealmUtil.instance?.addRealm(BlueToothDeviceBean(device.address, device.name))
                        binding.tvDeviceName.text = device.name
                    } else {
                        return
                    }
                }
            })
        blueToothDeviceListDialog?.show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("CheckResult")
    fun requestionPermission() {
        var rxPermissions = RxPermissions(this@MineActivity)
        rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe {
            if (it) {
                if (packageManager.canRequestPackageInstalls()) {
                    downloadFileAndInstall()
                } else {
                    val uri = Uri.parse("package:${AppUtils.getAppPackageName()}")
                    val intent =
                        Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, uri)
                    requestInstallPackageLauncher.launch(intent)
                }
            } else {

            }
        }
    }

    val requestInstallPackageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            downloadFileAndInstall()
        } else {

        }
    }

    fun downloadFileAndInstall() {
        ToastUtil.showMiddleToast(i18N(com.peakinfo.base.R.string.开始下载更新))
        GlobalScope.launch(Dispatchers.IO) {
            FileDownloader.setup(this@MineActivity)
            val path = "${PathUtils.getExternalDownloadsPath()}/${FileDownloadUtils.generateFileName(updateBean?.url)}.apk"
            FileDownloader.getImpl().create(updateBean?.url)
                .setPath(path)
                .setListener(object : FileDownloadListener() {
                    override fun pending(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                    }

                    override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                        Log.v("123", "${(soFarBytes * 100f / totalBytes.toFloat()).toInt()}")
                    }

                    override fun completed(task: BaseDownloadTask?) {
                        AppUtils.installApp(path)
                    }

                    override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                    }

                    override fun error(task: BaseDownloadTask?, e: Throwable?) {
                        Log.v("123", e.toString())
                    }

                    override fun warn(task: BaseDownloadTask?) {
                    }

                }).start()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun startObserve() {
        super.startObserve()
        mViewModel.apply {
            checkUpdateLiveDate.observe(this@MineActivity) {
                updateBean = it
                if (updateBean?.state == "0" && updateBean?.force == "1") {
                    DialogHelp.Builder().setTitle(i18N(com.peakinfo.base.R.string.发现新版本是否下载安装更新))
                        .setRightMsg(i18N(com.peakinfo.base.R.string.确定)).setCancelable(false)
                        .isAloneButton(true)
                        .setOnButtonClickLinsener(object : DialogHelp.OnButtonClickLinsener {
                            override fun onLeftClickLinsener(msg: String) {
                            }

                            override fun onRightClickLinsener(msg: String) {
                                requestionPermission()
                            }

                        }).build(this@MineActivity).showDailog()
                    runBlocking {
                        PreferencesDataStore(BaseApplication.instance()).putLong(
                            PreferencesKeys.lastCheckUpdateTime,
                            System.currentTimeMillis()
                        )
                    }
                } else if (updateBean?.state == "0" && updateBean?.force == "0") {
                    DialogHelp.Builder().setTitle(i18N(com.peakinfo.base.R.string.发现新版本是否下载安装更新))
                        .setRightMsg(i18N(com.peakinfo.base.R.string.确定))
                        .setLeftMsg(i18N(com.peakinfo.base.R.string.取消)).setCancelable(true)
                        .setOnButtonClickLinsener(object : DialogHelp.OnButtonClickLinsener {
                            override fun onLeftClickLinsener(msg: String) {
                            }

                            override fun onRightClickLinsener(msg: String) {
                                requestionPermission()
                            }

                        }).build(this@MineActivity).showDailog()
                    runBlocking {
                        PreferencesDataStore(BaseApplication.instance()).putLong(
                            PreferencesKeys.lastCheckUpdateTime,
                            System.currentTimeMillis()
                        )
                    }
                } else if (updateBean?.state == "1") {
                    ToastUtil.showMiddleToast("当前已是最新版本")
                }
            }
            errMsg.observe(this@MineActivity) {
                ToastUtil.showMiddleToast(it.msg)
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