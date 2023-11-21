package com.rt.ipms_video.ui.activity

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.BarUtils
import com.rt.base.BaseApplication
import com.rt.base.arouter.ARouterMap
import com.rt.base.ds.PreferencesDataStore
import com.rt.base.ds.PreferencesKeys
import com.rt.base.start.StartUpKey
import com.rt.base.viewbase.VbBaseActivity
import com.rt.ipms_video.databinding.ActivitySplashBinding
import com.rt.ipms_video.mvvm.viewmodel.SplashViewModel
import com.rt.ipms_video.startup.ApplicationAnchorTaskCreator
import com.rt.ipms_video.ui.activity.login.LoginActivity
import com.xj.anchortask.library.AnchorProject
import com.xj.anchortask.library.OnProjectExecuteListener
import com.xj.anchortask.library.log.LogUtils
import kotlinx.coroutines.runBlocking

class SplashActivity : VbBaseActivity<SplashViewModel, ActivitySplashBinding>(),
    OnProjectExecuteListener {
    var project: AnchorProject? = null

    override fun initView() {
        BarUtils.setStatusBarColor(
            this,
            ContextCompat.getColor(this, com.rt.base.R.color.transparent)
        )

        setCustomDensity()
    }

    fun setCustomDensity() {
        val appDisplayMetrics: DisplayMetrics = application.resources.displayMetrics

        val targetDensity = appDisplayMetrics.heightPixels / 640f
        val targetDensityDpi = (160 * targetDensity).toInt()
        appDisplayMetrics.density = targetDensity
        appDisplayMetrics.densityDpi = targetDensityDpi
        val activityDisplayMetrics = resources.displayMetrics
        activityDisplayMetrics.density = targetDensity
        activityDisplayMetrics.densityDpi = targetDensityDpi
    }

    override fun initListener() {
    }

    override fun initData() {
        project = AnchorProject.Builder().setContext(this).setLogLevel(LogUtils.LogLevel.DEBUG)
            .setAnchorTaskCreator(ApplicationAnchorTaskCreator())
            .addTask(StartUpKey.TASK_NAME_ONE)
            .addTask(StartUpKey.TASK_NAME_TWO).afterTask(StartUpKey.TASK_NAME_ONE)
            .build()
        project?.addListener(this)
    }

    override fun onResume() {
        super.onResume()
        project?.start()
    }

    override fun getVbBindingView(): ViewBinding {
        return ActivitySplashBinding.inflate(layoutInflater)
    }

    override fun onReloadData() {
    }

    override val isFullScreen: Boolean
        get() = true

    override fun providerVMClass(): Class<SplashViewModel> {
        return SplashViewModel::class.java
    }

    override fun onProjectFinish() {
        Handler(Looper.getMainLooper()).postDelayed({
            runBlocking {
                if (PreferencesDataStore(BaseApplication.instance()).getString(PreferencesKeys.token).isEmpty()) {
                    val intent = Intent(this@SplashActivity, LoginActivity::class.java)
                    startActivity(intent)
                } else {
                    val intent = Intent(this@SplashActivity, MainActivity::class.java)
                    startActivity(intent)
                }
                initArouter()
                finish()
            }
        }, 1000)
    }

    fun initArouter() {
        Thread {
            if (com.rt.base.BuildConfig.is_debug) {
                ARouter.openLog()
                ARouter.openDebug()
            }
            ARouter.init(BaseApplication.instance())
        }.start()
    }

    override fun onProjectStart() {
    }

    override fun onTaskFinish(taskName: String) {
    }
}