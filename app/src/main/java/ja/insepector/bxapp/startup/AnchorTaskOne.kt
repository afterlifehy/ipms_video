package ja.insepector.bxapp.startup

import ja.insepector.base.start.BaseStartUpManager
import ja.insepector.base.start.StartUpKey
import com.xj.anchortask.library.AnchorTask

class AnchorTaskOne : AnchorTask(StartUpKey.TASK_NAME_TWO) {
    override fun isRunOnMainThread(): Boolean {
        return false
    }

    override fun run() {
        BaseStartUpManager.instance().delayInit(ja.insepector.bxapp.AppApplication.instance())
        AppStartUpManager.instance().delayInit(ja.insepector.bxapp.AppApplication.instance())
    }
}