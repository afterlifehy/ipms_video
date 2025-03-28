//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.peakinfo.base.ca.com.custle.ksmkey.common;

import android.content.Context;

import com.peakinfo.base.ca.com.custle.ksmkey.bean.MKUserAuthBean;
import com.peakinfo.base.ca.com.custle.ksmkey.util.MKJsonUtil;
import com.peakinfo.base.ca.com.custle.ksmkey.util.MKUtils;
import com.peakinfo.base.ca.com.custle.okhttp.OkHttpUtils;
import com.peakinfo.base.ca.com.custle.okhttp.builder.PostFormBuilder;
import com.peakinfo.base.ca.com.custle.okhttp.callback.StringCallback;

import java.net.URLDecoder;
import java.net.URLEncoder;
import okhttp3.Call;

public class MKAppNet {
    public MKAppNet() {
    }

    public static void userAuth(Context context, String userName, String idNo, String phone, String code, final UserAuthCallBack callBack) {
        try {
            ((PostFormBuilder) OkHttpUtils.post().url(MKAppManager.getInstance().getUrl() + "/authorize/user")).addParams("appId", MKAppManager.getInstance().getAppId()).addParams("userName", URLEncoder.encode(userName, "UTF-8")).addParams("idNo", idNo).addParams("phone", phone).addParams("code", URLEncoder.encode(code, "UTF-8")).addParams("packageName", MKUtils.getPackageName(context)).addParams("clientType", "1").build().execute(new StringCallback() {
                public void onError(Call call, Exception e, int id) {
                    callBack.onfailure("10", e.getLocalizedMessage());
                }

                public void onResponse(String response, int id) {
                    try {
                        response = URLDecoder.decode(response, "UTF-8");
                        MKUserAuthBean bean = (MKUserAuthBean) MKJsonUtil.toObject(response, MKUserAuthBean.class);
                        if (bean.getRet() == 0) {
                            MKAppManager.getInstance().setUserToken(bean.getData().getToken());
                            callBack.onSuccess();
                        } else if (bean.getRet() == 1035) {
                            callBack.onfailure("14", bean.getMsg());
                        } else {
                            callBack.onfailure("13", bean.getMsg());
                        }
                    } catch (Exception var4) {
                        callBack.onfailure("12", var4.getLocalizedMessage());
                    }

                }
            });
        } catch (Exception var7) {
            callBack.onfailure("12", var7.getLocalizedMessage());
        }

    }

    public interface UserAuthCallBack {
        void onSuccess();

        void onfailure(String var1, String var2);
    }
}
