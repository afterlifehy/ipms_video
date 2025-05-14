//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.rt.base.ksmkey.common;

import android.content.Context;
import com.rt.base.ksmkey.bean.MKUserAuthBean;
import com.rt.base.ksmkey.util.MKJsonUtil;
import com.rt.base.ksmkey.util.MKUtils;
import com.rt.base.okhttp.OkHttpUtils;
import com.rt.base.okhttp.builder.PostFormBuilder;
import com.rt.base.okhttp.callback.StringCallback;
import java.net.URLDecoder;
import java.net.URLEncoder;
import okhttp3.Call;

public class MKAppNet {
    public MKAppNet() {
    }

    public static void userAuth(Context context, String deviceCode, String code, final UserAuthCallBack callBack) {
        try {
            MKUtils.logDebug("userAuth http: " + MKAppManager.getInstance().getUrl() + "/authorize/user");
            String appId = MKAppManager.getInstance().getAppId();
            String deviceId = MKUtils.getDeviceUuid();
            String packageName = MKUtils.getPackageName(context);
            MKUtils.logDebug("userAuth request: deviceId=" + deviceId + "&equipmentCode=" + deviceCode + "&code=" + code + "&appId=" + appId + "&packageName=" + packageName + "&clientType=1");
            ((PostFormBuilder)OkHttpUtils.post().url(MKAppManager.getInstance().getUrl() + "/authorize/user")).addParams("deviceId", MKUtils.getDeviceUuid()).addParams("equipmentCode", deviceCode).addParams("code", URLEncoder.encode(code, "UTF-8")).addParams("appId", MKAppManager.getInstance().getAppId()).addParams("packageName", MKUtils.getPackageName(context)).addParams("clientType", "1").build().execute(new StringCallback() {
                public void onError(Call call, Exception e, int id) {
                    callBack.onFailure(10, e.getLocalizedMessage());
                }

                public void onResponse(String response, int id) {
                    try {
                        response = URLDecoder.decode(response, "UTF-8");
                        MKUtils.logDebug("userAuth response: " + response);
                        MKUserAuthBean bean = (MKUserAuthBean)MKJsonUtil.toObject(response, MKUserAuthBean.class);
                        if (bean.getRet() == 0) {
                            MKAppManager.getInstance().setUserToken(bean.getData().getToken());
                            callBack.onSuccess();
                        } else if (bean.getRet() == 1035) {
                            callBack.onFailure(14, bean.getMsg());
                        } else {
                            callBack.onFailure(13, bean.getMsg());
                        }
                    } catch (Exception var4) {
                        MKUtils.logDebug("userAuth response err: " + var4.getLocalizedMessage());
                        callBack.onFailure(12, var4.getLocalizedMessage());
                    }

                }
            });
        } catch (Exception var7) {
            MKUtils.logDebug("userAuth err: " + var7.getLocalizedMessage());
            callBack.onFailure(12, var7.getLocalizedMessage());
        }

    }

    public interface UserAuthCallBack {
        void onSuccess();

        void onFailure(int errCode, String errMsg);
    }
}
