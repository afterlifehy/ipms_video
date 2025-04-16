package com.rt.base.ext

import android.util.Log
import com.rt.base.BaseApplication
import com.rt.base.BuildConfig


fun Any.i18n(res: Int): String {
    return BaseApplication.instance().resources.getString(res)
}

fun Any.log(str: String) {
    if (BuildConfig.DEBUG) {
        Log.v("rt", str)
    }
}
