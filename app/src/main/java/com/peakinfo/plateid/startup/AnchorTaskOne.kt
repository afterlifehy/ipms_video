package com.peakinfo.plateid.startup

import com.peakinfo.base.start.BaseStartUpManager
import com.peakinfo.base.start.StartUpKey
import com.peakinfo.plateid.AppApplication
import com.xj.anchortask.library.AnchorTask

class AnchorTaskOne : AnchorTask(StartUpKey.TASK_NAME_TWO) {
    override fun isRunOnMainThread(): Boolean {
        return false
    }

    override fun run() {
        BaseStartUpManager.instance().delayInit(AppApplication.instance())
        AppStartUpManager.instance().delayInit(AppApplication.instance())
    }
}