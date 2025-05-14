//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.rt.base.ksmkey.util;

import com.rt.base.ksmkey.MKeyApiCallback;
import com.rt.base.ksmkey.MKeyApiResult;

public class MKAppUtils {
    public MKAppUtils() {
    }

    public static void mkeyResultCallBack(MKeyApiCallback callback, int code, String msg) {
        mkeyResultCallBack(callback, code, msg, "");
    }

    public static void mkeyResultCallBack(MKeyApiCallback callback, int code, String msg, String data) {
        MKeyApiResult result = new MKeyApiResult();
        result.setCode(code);
        result.setMsg(msg);
        result.setData(data == null ? "" : data);
        if (callback != null) {
            callback.onMKeyApiCallBack(result);
        }

    }
}
