package com.peakinfo.base.ext

import com.peakinfo.base.BaseApplication


fun Any.i18n(res: Int): String {
    return BaseApplication.instance().resources.getString(res)
}
