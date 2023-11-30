package com.peakinfo.plateid.startup

import com.peakinfo.base.start.BaseStartUpManager
import com.peakinfo.base.start.StartUpKey
import com.peakinfo.plateid.AppApplication
import com.xj.anchortask.library.AnchorTask

class AnchorTaskZero : AnchorTask(StartUpKey.TASK_NAME_ONE) {
    override fun isRunOnMainThread(): Boolean {
        return false
    }

    override fun run() {
        BaseStartUpManager.instance().applicationInit(AppApplication.instance())
        AppStartUpManager.instance().applicationInit(AppApplication.instance())
    }
}