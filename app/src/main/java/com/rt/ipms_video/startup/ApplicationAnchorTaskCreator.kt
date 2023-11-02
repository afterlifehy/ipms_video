package com.rt.ipms_video.startup

import com.rt.base.start.StartUpKey
import com.xj.anchortask.library.AnchorTask
import com.xj.anchortask.library.IAnchorTaskCreator

class ApplicationAnchorTaskCreator : IAnchorTaskCreator {
    override fun createTask(taskName: String): AnchorTask? {
        when (taskName) {
            StartUpKey.MUST_BE_INITIALIZED -> {
                return AnchorTaskZero()
            }
            StartUpKey.MUST_BE_ONE -> {
                return AnchorTaskOne()

            }
        }
        return null
    }
}