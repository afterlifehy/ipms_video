package ja.insepector.bxapp.startup

import android.app.Application
import ja.insepector.base.start.AppInitManager

class AppStartUpManager private constructor() : AppInitManager() {
    private val TAG: String = AppStartUpManager::class.java.simpleName

    companion object {
        var mAppStartUpManager: AppStartUpManager? = null
        fun instance(): AppStartUpManager {
            if (mAppStartUpManager == null) {
                mAppStartUpManager = AppStartUpManager()
            }
            return mAppStartUpManager!!
        }
    }

    override fun applicationInit(application: Application) {
//        CrashHandler.instance()?.initCrash(application)
    }

    override fun delayInit(application: Application) {

    }
}