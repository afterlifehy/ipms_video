package com.peakinfo.base.ext

import android.util.Log
import com.peakinfo.base.BaseApplication
import com.peakinfo.base.BuildConfig


fun Any.i18n(res: Int): String {
    return BaseApplication.instance().resources.getString(res)
}

fun Any.log(str: String) {
    if (BuildConfig.DEBUG) {
        Log.v("rt", str)
    }
}
