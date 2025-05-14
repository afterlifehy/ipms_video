//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.custle.ksmkey.util;

import com.custle.ksmkey.MKeyApiCallback;
import com.custle.ksmkey.MKeyApiResult;

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
