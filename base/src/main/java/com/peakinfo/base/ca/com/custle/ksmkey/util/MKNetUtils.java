//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.peakinfo.base.ca.com.custle.ksmkey.util;

import android.content.Context;
import android.os.Build;
import android.os.Build.VERSION;

import com.custle.certificate.KSCertificate;
import com.peakinfo.base.ca.com.custle.ksmkey.common.MKAppManager;

import java.net.URLEncoder;
import okhttp3.Call;

public class MKNetUtils {
    public MKNetUtils() {
    }

    public static void postSDKLog(Context context, String strType, String errInfo) {
        try {
            String strDate = MKUtils.getCurrentTime("yyyy-MM-dd HH:mm:ss");
            String phontModel = Build.BRAND + ";" + Build.MODEL + ";" + VERSION.RELEASE;
            String strMsg = "{\"date\":\"" + strDate + "\",\"phone\":\"" + MKAppManager.getInstance().getUserInfo().getMobile() + "\",\"type\":\"" + strType + "\",\"android\":\"" + phontModel + "\",\"data\":" + errInfo + "}";
            ((PostFormBuilder)OkHttpUtils.post().url(MKAppManager.getInstance().getUrl() + "/base/sdklog")).addParams("logmsg", URLEncoder.encode(strMsg, "UTF-8")).build().execute(new StringCallback() {
                public void onError(Call call, Exception e, int id) {
                }

                public void onResponse(String response, int id) {
                }
            });
        } catch (Exception var6) {
        }

    }

    public static void postVerifyPinLog(Context context, String userId, String inputPin, String errMsg) {
        String strInputPin = MKUtils.shaMD5(inputPin);
        String strLocalPin = KSCertificate.getInstance(context).getLocalPin(userId);
        String errInfo = "{\"input_pin\":\"" + strInputPin + "\",\"local_pin\":\"" + strLocalPin + "\",\"result_msg\":\"" + errMsg + "\"}";
        postSDKLog(context, "LOG_VERIFY_PIN", errInfo);
    }
}
