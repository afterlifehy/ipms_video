package com.rt.ipms_video.startup

import com.rt.base.start.BaseStartUpManager
import com.rt.base.start.StartUpKey
import com.rt.ipms_video.AppApplication
import com.rt.ipms_video.startup.AppStartUpManager
import com.xj.anchortask.library.AnchorTask

class AnchorTaskOne : AnchorTask(StartUpKey.MUST_BE_ONE) {
    override fun isRunOnMainThread(): Boolean {
        return false
    }

    override fun run() {
        BaseStartUpManager.instance().delayInit(AppApplication.instance())
        AppStartUpManager.instance().delayInit(AppApplication.instance())
    }
}