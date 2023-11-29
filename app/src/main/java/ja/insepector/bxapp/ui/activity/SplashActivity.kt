package ja.insepector.bxapp.ui.activity

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import com.blankj.utilcode.util.BarUtils
import ja.insepector.base.BaseApplication
import ja.insepector.base.ds.PreferencesDataStore
import ja.insepector.base.ds.PreferencesKeys
import ja.insepector.base.start.StartUpKey
import ja.insepector.base.viewbase.VbBaseActivity
import ja.insepector.bxapp.databinding.ActivitySplashBinding
import ja.insepector.bxapp.mvvm.viewmodel.SplashViewModel
import ja.insepector.bxapp.startup.ApplicationAnchorTaskCreator
import ja.insepector.bxapp.ui.activity.login.LoginActivity
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
            ContextCompat.getColor(this, ja.insepector.base.R.color.transparent)
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
                finish()
            }
        }, 100)
    }

    override fun onProjectStart() {
    }

    override fun onTaskFinish(taskName: String) {
    }
}