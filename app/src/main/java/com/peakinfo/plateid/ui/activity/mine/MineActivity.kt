package com.peakinfo.plateid.ui.activity.mine

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.telephony.TelephonyManager
import android.view.View
import android.view.View.OnClickListener
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.alibaba.fastjson.JSONObject
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.PhoneUtils
import com.peakinfo.base.BaseApplication
import com.peakinfo.base.arouter.ARouterMap
import com.peakinfo.base.bean.UpdateBean
import com.peakinfo.base.dialog.DialogHelp
import com.peakinfo.base.ds.PreferencesDataStore
import com.peakinfo.base.ds.PreferencesKeys
import com.peakinfo.base.ext.i18N
import com.peakinfo.base.ext.startArouter
import com.peakinfo.base.help.ActivityCacheManager
import com.peakinfo.base.util.ToastUtil
import com.peakinfo.base.viewbase.VbBaseActivity
import com.peakinfo.common.event.BaiduLocationLoginEvent
import com.peakinfo.common.realm.RealmUtil
import com.peakinfo.common.util.GlideUtils
import com.peakinfo.plateid.BuildConfig
import com.peakinfo.plateid.R
import com.peakinfo.plateid.databinding.ActivityMineBinding
import com.peakinfo.plateid.mvvm.viewmodel.MineViewModel
import com.peakinfo.plateid.ui.activity.login.LoginActivity
import com.peakinfo.plateid.util.UpdateUtil
import com.tbruyelle.rxpermissions3.RxPermissions
import kotlinx.coroutines.runBlocking
import org.greenrobot.eventbus.EventBus

@Route(path = ARouterMap.MINE)
class MineActivity : VbBaseActivity<MineViewModel, ActivityMineBinding>(), OnClickListener {
    var updateBean: UpdateBean? = null

    @SuppressLint("CheckResult", "MissingPermission")
    override fun initView() {
        GlideUtils.instance?.loadImage(binding.layoutToolbar.ivBack, com.peakinfo.common.R.mipmap.ic_back_white)
        binding.layoutToolbar.tvTitle.text = i18N(com.peakinfo.base.R.string.我的)
        binding.layoutToolbar.tvTitle.setTextColor(ContextCompat.getColor(BaseApplication.instance(), com.peakinfo.base.R.color.white))

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
        binding.rtvLogout.setOnClickListener(this)
        binding.flModifyPw.setOnClickListener(this)
        binding.flLog.setOnClickListener(this)
    }

    override fun initData() {
    }

    @SuppressLint("CheckResult")
    override fun onResume() {
        super.onResume()
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
                var imei = ""
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    try {
                        imei = (getSystemService(TELEPHONY_SERVICE) as TelephonyManager).imei
                    } catch (e: Exception) {
                        val manufacturer = Build.MANUFACTURER
                        val model = Build.MODEL
                        val id = manufacturer + model + " " + Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
                        imei = id
                    }
                } else {
                    imei = PhoneUtils.getIMEI()
                }
                val param = HashMap<String, Any>()
                val jsonobject = JSONObject()
                jsonobject["version"] = AppUtils.getAppVersionCode()
                jsonobject["imei"] = imei
                jsonobject["softType"] = "11"
                param["attr"] = jsonobject
                mViewModel.checkUpdate(param)
            }

            R.id.fl_feeRate -> {
                ARouter.getInstance().build(ARouterMap.FEE_RATE).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
            }

            R.id.fl_modifyPw -> {
                runBlocking {
                    val loginName = PreferencesDataStore(BaseApplication.instance()).getString(PreferencesKeys.account)
                    startArouter(ARouterMap.RESET_PW, data = Bundle().apply {
                        putString(ARouterMap.RESET_PW_ACCOUNT, loginName)
                    })
                }
            }

            R.id.fl_log -> {
                ARouter.getInstance().build(ARouterMap.LOG_UPLOAD).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
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
                            EventBus.getDefault().post(BaiduLocationLoginEvent())
                            for (i in ActivityCacheManager.instance().getAllActivity()) {
                                if (i !is LoginActivity) {
                                    i.finish()
                                }
                            }
                            runBlocking {
                                PreferencesDataStore(BaseApplication.instance()).putString(PreferencesKeys.token, "")
                                PreferencesDataStore(BaseApplication.instance()).putString(PreferencesKeys.phone, "")
                                PreferencesDataStore(BaseApplication.instance()).putString(PreferencesKeys.name, "")
                                PreferencesDataStore(BaseApplication.instance()).putString(PreferencesKeys.account, "")
                            }
                            RealmUtil.instance?.deleteAllStreet()
                        }

                    }).build(this@MineActivity).showDailog()
            }
        }
    }

    @SuppressLint("CheckResult")
    fun requestPermissions() {
        var rxPermissions = RxPermissions(this@MineActivity)
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

                        override fun install(path: String) {

                        }
                    })
                } else {
                    ToastUtil.showBottomToast("当前已是最新版本")
                }
            }
            errMsg.observe(this@MineActivity) {
                ToastUtil.showBottomToast(it.msg)
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