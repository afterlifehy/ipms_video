//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.peakinfo.base.ca.com.custle.ksmkey.common;

import android.content.Context;
import com.peakinfo.base.ca.com.custle.ksmkey.certificate.MKUserInfo;

public class MKAppManager {
    private static volatile MKAppManager appManager = null;
    private Context context;
    private String url;
    private String contCode;
    private String appId;
    private String appCode;
    private MKUserInfo userInfo;
    private String userToken;

    public static MKAppManager getInstance() {
        if (appManager == null) {
            Class var0 = MKAppManager.class;
            synchronized(MKAppManager.class) {
                if (appManager == null) {
                    appManager = new MKAppManager();
                }
            }
        }

        return appManager;
    }

    private MKAppManager() {
    }

    public Context getContext() {
        return this.context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getUrl() {
        return this.url == null ? "https://device.mkeysec.cn/sdk/v1" : this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContCode() {
        return this.contCode;
    }

    public void setContCode(String contCode) {
        this.contCode = contCode;
    }

    public String getAppId() {
        return this.appId == null ? "" : this.appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppCode() {
        return this.appCode == null ? "" : this.appCode;
    }

    public void setAppCode(String appCode) {
        this.appCode = appCode;
    }

    public MKUserInfo getUserInfo() {
        return this.userInfo;
    }

    public void setUserInfo(MKUserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public String getUserToken() {
        return this.userToken == null ? "" : this.userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }
}
