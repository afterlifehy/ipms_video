package com.rt.ipms_video.startup

import com.rt.base.start.BaseStartUpManager
import com.rt.base.start.StartUpKey
import com.xj.anchortask.library.AnchorTask

class AnchorTaskZero : AnchorTask(StartUpKey.TASK_NAME_ONE) {
    override fun isRunOnMainThread(): Boolean {
        return false
    }

    override fun run() {
        BaseStartUpManager.instance().applicationInit(com.rt.ipms_video.AppApplication.instance())
        AppStartUpManager.instance().applicationInit(com.rt.ipms_video.AppApplication.instance())
    }
}