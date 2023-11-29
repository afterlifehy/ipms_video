package com.rt.ipms_video.startup

import com.rt.base.start.BaseStartUpManager
import com.rt.base.start.StartUpKey
import com.xj.anchortask.library.AnchorTask

class AnchorTaskOne : AnchorTask(StartUpKey.TASK_NAME_TWO) {
    override fun isRunOnMainThread(): Boolean {
        return false
    }

    override fun run() {
        BaseStartUpManager.instance().delayInit(com.rt.ipms_video.AppApplication.instance())
        AppStartUpManager.instance().delayInit(com.rt.ipms_video.AppApplication.instance())
    }
}