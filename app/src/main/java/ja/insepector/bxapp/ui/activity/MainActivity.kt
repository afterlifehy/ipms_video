package ja.insepector.bxapp.ui.activity

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
import ja.insepector.base.BaseApplication
import ja.insepector.base.arouter.ARouterMap
import ja.insepector.base.bean.Street
import ja.insepector.base.bean.UpdateBean
import ja.insepector.base.dialog.DialogHelp
import ja.insepector.base.ds.PreferencesDataStore
import ja.insepector.base.ds.PreferencesKeys
import ja.insepector.base.ext.i18N
import ja.insepector.base.help.ActivityCacheManager
import ja.insepector.base.util.ToastUtil
import ja.insepector.base.viewbase.VbBaseActivity
import ja.insepector.common.realm.RealmUtil
import ja.insepector.common.util.AppUtil
import ja.insepector.common.util.BluePrint
import ja.insepector.bxapp.R
import ja.insepector.bxapp.databinding.ActivityMainBinding
import ja.insepector.bxapp.mvvm.viewmodel.MainViewModel
import ja.insepector.bxapp.pop.StreetPop
import ja.insepector.bxapp.ui.activity.abnormal.BerthAbnormalActivity
import ja.insepector.bxapp.ui.activity.income.IncomeCountingActivity
import ja.insepector.bxapp.ui.activity.mine.LogoutActivity
import ja.insepector.bxapp.ui.activity.mine.MineActivity
import ja.insepector.bxapp.ui.activity.order.OrderMainActivity
import ja.insepector.bxapp.ui.activity.parking.ParkingLotActivity
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

    override fun onSaveInstanceState(outState: Bundle) {
        // super.onSaveInstanceState(outState)
    }

    override fun initView() {
        initHyperLPR()
//        setStatusBarColor(ja.insepector.base.R.color.black, false)
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

        Thread {
            if (RealmUtil.instance?.findCurrentDeviceList()!!.isNotEmpty()) {
                val device = RealmUtil.instance?.findCurrentDeviceList()!![0]
                if (device != null) {
                    BluePrint.instance?.connet(device.address)
                }
            }
        }.start()

        binding.tvTitle.text = currentStreet!!.streetNo + currentStreet!!.streetName.substring(0, currentStreet!!.streetName.indexOf("("))
        val param = HashMap<String, Any>()
        val jsonobject = JSONObject()
        jsonobject["version"] = AppUtils.getAppVersionName()
        param["attr"] = jsonobject
        mViewModel.checkUpdate(param)
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
                        binding.tvTitle.text =
                            currentStreet!!.streetNo + currentStreet!!.streetName.substring(0, currentStreet!!.streetName.indexOf("("))
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
                if (updateBean?.state == "0" && updateBean?.force == "1") {
                    DialogHelp.Builder().setTitle(i18N(ja.insepector.base.R.string.发现新版本是否下载安装更新))
                        .setRightMsg(i18N(ja.insepector.base.R.string.确定)).setCancelable(false)
                        .isAloneButton(true)
                        .setOnButtonClickLinsener(object : DialogHelp.OnButtonClickLinsener {
                            override fun onLeftClickLinsener(msg: String) {
                            }

                            override fun onRightClickLinsener(msg: String) {
                                requestionPermission()
                            }

                        }).build(this@MainActivity).showDailog()
                    runBlocking {
                        PreferencesDataStore(BaseApplication.instance()).putLong(
                            PreferencesKeys.lastCheckUpdateTime,
                            System.currentTimeMillis()
                        )
                    }
                } else if (updateBean?.state == "0" && updateBean?.force == "0") {
                    runBlocking {
                        val lastTime = PreferencesDataStore(BaseApplication.instance()).getLong(PreferencesKeys.lastCheckUpdateTime)
                        if (System.currentTimeMillis() - lastTime > 12 * 60 * 60 * 1000) {
                            DialogHelp.Builder().setTitle(i18N(ja.insepector.base.R.string.发现新版本是否下载安装更新))
                                .setRightMsg(i18N(ja.insepector.base.R.string.确定)).setCancelable(true)
                                .setLeftMsg(i18N(ja.insepector.base.R.string.取消)).setCancelable(true)
                                .setOnButtonClickLinsener(object : DialogHelp.OnButtonClickLinsener {
                                    override fun onLeftClickLinsener(msg: String) {
                                    }

                                    override fun onRightClickLinsener(msg: String) {
                                        requestionPermission()
                                    }

                                }).build(this@MainActivity).showDailog()
                            PreferencesDataStore(BaseApplication.instance()).putLong(
                                PreferencesKeys.lastCheckUpdateTime,
                                System.currentTimeMillis()
                            )
                        }
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("CheckResult")
    fun requestionPermission() {
        var rxPermissions = RxPermissions(this@MainActivity)
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
        ToastUtil.showToast(i18N(ja.insepector.base.R.string.开始下载更新))
        GlobalScope.launch(Dispatchers.IO) {
            FileDownloader.setup(this@MainActivity)
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
            ToastUtil.showToast(i18N(ja.insepector.base.R.string.再按一次退出程序))
        }
    }
}