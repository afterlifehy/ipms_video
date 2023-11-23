package com.rt.ipms_video.ui.activity.mine

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
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
import com.rt.base.BaseApplication
import com.rt.base.arouter.ARouterMap
import com.rt.base.bean.UpdateBean
import com.rt.base.dialog.DialogHelp
import com.rt.base.ds.PreferencesDataStore
import com.rt.base.ds.PreferencesKeys
import com.rt.base.ext.i18N
import com.rt.base.help.ActivityCacheManager
import com.rt.base.util.ToastUtil
import com.rt.base.viewbase.VbBaseActivity
import com.rt.common.realm.RealmUtil
import com.rt.common.util.GlideUtils
import com.rt.ipms_video.R
import com.rt.ipms_video.databinding.ActivityMineBinding
import com.rt.ipms_video.mvvm.viewmodel.MineViewModel
import com.tbruyelle.rxpermissions3.RxPermissions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@Route(path = ARouterMap.MINE)
class MineActivity : VbBaseActivity<MineViewModel, ActivityMineBinding>(), OnClickListener {
    var updateBean: UpdateBean? = null
    var locationManager: LocationManager? = null
    var lat = 121.123212
    var lon = 31.434312

    @SuppressLint("CheckResult", "MissingPermission")
    override fun initView() {
        GlideUtils.instance?.loadImage(binding.layoutToolbar.ivBack, com.rt.common.R.mipmap.ic_back_white)
        binding.layoutToolbar.tvTitle.text = i18N(com.rt.base.R.string.我的)
        binding.layoutToolbar.tvTitle.setTextColor(ContextCompat.getColor(BaseApplication.instance(), com.rt.base.R.color.white))

        binding.tvVersion.text = AppUtils.getAppVersionName()

        var rxPermissions = RxPermissions(this@MineActivity)
        rxPermissions.request(Manifest.permission.ACCESS_FINE_LOCATION).subscribe {
            if (it) {
                locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
                val provider = LocationManager.NETWORK_PROVIDER
                locationManager?.requestLocationUpdates(provider, 1000, 1f, @SuppressLint("MissingPermission")
                object : LocationListener {
                    override fun onLocationChanged(location: Location) {
                        lat = location.latitude
                        lon = location.longitude
                    }
                })
            }
        }
    }


    override fun initListener() {
        binding.layoutToolbar.flBack.setOnClickListener(this)
        binding.flBaseInfo.setOnClickListener(this)
        binding.flVersion.setOnClickListener(this)
        binding.rtvLogout.setOnClickListener(this)
    }

    override fun initData() {
        val param = HashMap<String, Any>()
        val jsonobject = JSONObject()
        jsonobject["version"] = AppUtils.getAppVersionCode()
        param["attr"] = jsonobject
        mViewModel.checkUpdate(param)
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
                if (updateBean?.state == "0" && updateBean?.force == "0") {
                    DialogHelp.Builder().setTitle(i18N(com.rt.base.R.string.发现新版本是否下载安装更新))
                        .setRightMsg(i18N(com.rt.base.R.string.确定)).setCancelable(true)
                        .setLeftMsg(i18N(com.rt.base.R.string.取消)).setCancelable(true)
                        .setOnButtonClickLinsener(object : DialogHelp.OnButtonClickLinsener {
                            override fun onLeftClickLinsener(msg: String) {
                            }

                            override fun onRightClickLinsener(msg: String) {
                                requestionPermission()
                            }

                        }).build(this@MineActivity).showDailog()
                }
            }

            R.id.rtv_logout -> {
                var rxPermissions = RxPermissions(this@MineActivity)
                rxPermissions.request(Manifest.permission.ACCESS_FINE_LOCATION).subscribe {
                    if (it) {
                        if (locationManager == null) {
                            locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
                            val provider = LocationManager.NETWORK_PROVIDER
                            locationManager?.requestLocationUpdates(provider, 1000, 1f, object : LocationListener {
                                override fun onLocationChanged(location: Location) {
                                    lat = location.latitude
                                    lon = location.longitude
                                }
                            })
                        }
                        showProgressDialog(20000)
                        runBlocking {
                            val token = PreferencesDataStore(BaseApplication.baseApplication).getString(PreferencesKeys.token)
                            val param = HashMap<String, Any>()
                            val jsonobject = JSONObject()
                            jsonobject["token"] = token
                            jsonobject["longitude"] = lon
                            jsonobject["latitude"] = lat
                            param["attr"] = jsonobject
                            mViewModel.logout(param)
                        }
                    }
                }
            }
        }
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
        ToastUtil.showToast(i18N(com.rt.base.R.string.开始下载更新))
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
                    ToastUtil.showToast("后台下载更新中...")
                    requestionPermission()
                }
            }
            logoutLiveData.observe(this@MineActivity) {
                dismissProgressDialog()
                ARouter.getInstance().build(ARouterMap.LOGIN).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
                ActivityCacheManager.instance().getCurrentActivity()?.finish()
                ToastUtil.showToast(i18N(com.rt.base.R.string.签退成功))
                runBlocking {
                    PreferencesDataStore(BaseApplication.instance()).putString(PreferencesKeys.token, "")
                    PreferencesDataStore(BaseApplication.instance()).putString(PreferencesKeys.phone, "")
                    PreferencesDataStore(BaseApplication.instance()).putString(PreferencesKeys.name, "")
                }
                RealmUtil.instance?.deleteAllStreet()
            }
            errMsg.observe(this@MineActivity) {
                ToastUtil.showToast(it.msg)
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