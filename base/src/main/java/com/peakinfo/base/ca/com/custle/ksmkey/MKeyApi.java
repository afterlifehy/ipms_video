//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.peakinfo.base.ca.com.custle.ksmkey;

import android.content.Context;

import com.peakinfo.base.ca.com.custle.ksmkey.certificate.MKCertManager;
import com.peakinfo.base.ca.com.custle.ksmkey.certificate.MKCertSignature;
import com.peakinfo.base.ca.com.custle.ksmkey.common.MKAppManager;
import com.peakinfo.base.ca.com.custle.ksmkey.common.MKAppNet;
import com.peakinfo.base.ca.com.custle.ksmkey.util.MKAppUtils;
import com.peakinfo.base.ca.com.custle.ksmkey.util.MKJsonUtil;
import com.peakinfo.base.ca.com.custle.ksmkey.certificate.MKUserInfo;
import com.peakinfo.base.ca.com.custle.okhttp.OkHttpUtils;
import com.peakinfo.base.ca.com.custle.okhttp.https.HttpsUtils;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import okhttp3.OkHttpClient;

public class MKeyApi {
    private static volatile MKeyApi mKeyApi = null;

    public static String getVersion() {
        return "2.1.1";
    }

    public static void initSDK(String url, String contCode) {
        if (url != null && url.length() != 0) {
            MKAppManager.getInstance().setUrl(url);
        } else {
            MKAppManager.getInstance().setUrl("https://device.mkeysec.cn/sdk/v1");
        }

        if (contCode != null && contCode.length() != 0) {
            MKAppManager.getInstance().setContCode(contCode);
        } else {
            MKAppManager.getInstance().setContCode("");
        }

    }

    public static MKeyApi getInstance(Context context, String appId, String appCode, String userInfo) {
        if (context != null) {
            MKAppManager.getInstance().setContext(context);
        }

        if (appId != null && appId.length() != 0) {
            MKAppManager.getInstance().setAppId(appId);
        }

        if (appCode != null && appCode.length() != 0) {
            MKAppManager.getInstance().setAppCode(appCode);
        }

        MKUserInfo ksUserInfo = (MKUserInfo) MKJsonUtil.toObject(userInfo, MKUserInfo.class);
        if (ksUserInfo != null) {
            MKAppManager.getInstance().setUserInfo(ksUserInfo);
        }

        if (mKeyApi == null) {
            Class var5 = MKeyApi.class;
            synchronized(MKeyApi.class) {
                if (mKeyApi == null) {
                    mKeyApi = new MKeyApi();
                }
            }
        }

        return mKeyApi;
    }

    public MKeyApi() {
        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory((InputStream[])null, (InputStream)null, (String)null);
        OkHttpClient okHttpClient = (new OkHttpClient.Builder()).sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager).hostnameVerifier(new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        }).connectTimeout(30L, TimeUnit.SECONDS).readTimeout(60L, TimeUnit.SECONDS).build();
        OkHttpUtils.initClient(okHttpClient);
    }

    public void applyCert(final String pin, final MKeyApiCallback callback) {
        final MKUserInfo userInfo = MKAppManager.getInstance().getUserInfo();
        if (MKAppManager.getInstance().getAppCode().length() != 0 && userInfo != null && userInfo.getName().length() != 0 && userInfo.getIdNo().length() != 0) {
            MKAppNet.userAuth(MKAppManager.getInstance().getContext(), userInfo.getName(), userInfo.getIdNo(), userInfo.getMobile(), MKAppManager.getInstance().getAppCode(), new MKAppNet.UserAuthCallBack() {
                public void onSuccess() {
                    MKCertManager.getInstance().certApply(userInfo, pin, callback);
                }

                public void onfailure(String errCode, String errMsg) {
                    MKAppUtils.mkeyResultCallBack(callback, errCode, errMsg);
                }
            });
        } else {
            MKAppUtils.mkeyResultCallBack(callback, "2", "输入参数错误");
        }
    }

    public void updateCert(final String pin, final MKeyApiCallback callback) {
        MKUserInfo userInfo = MKAppManager.getInstance().getUserInfo();
        if (MKAppManager.getInstance().getAppCode().length() != 0 && userInfo != null && userInfo.getName().length() != 0 && userInfo.getIdNo().length() != 0) {
            MKAppNet.userAuth(MKAppManager.getInstance().getContext(), userInfo.getName(), userInfo.getIdNo(), userInfo.getMobile(), MKAppManager.getInstance().getAppCode(), new MKAppNet.UserAuthCallBack() {
                public void onSuccess() {
                    MKCertManager.getInstance().certUpdate(pin, callback);
                }

                public void onfailure(String errCode, String errMsg) {
                    MKAppUtils.mkeyResultCallBack(callback, errCode, errMsg);
                }
            });
        } else {
            MKAppUtils.mkeyResultCallBack(callback, "2", "输入参数错误");
        }
    }

    public void revokeCert(final String pin, final MKeyApiCallback callback) {
        MKUserInfo userInfo = MKAppManager.getInstance().getUserInfo();
        if (MKAppManager.getInstance().getAppCode().length() != 0 && userInfo != null && userInfo.getName().length() != 0 && userInfo.getIdNo().length() != 0) {
            MKAppNet.userAuth(MKAppManager.getInstance().getContext(), userInfo.getName(), userInfo.getIdNo(), userInfo.getMobile(), MKAppManager.getInstance().getAppCode(), new MKAppNet.UserAuthCallBack() {
                public void onSuccess() {
                    MKCertManager.getInstance().certRevoke(pin, callback);
                }

                public void onfailure(String errCode, String errMsg) {
                    MKAppUtils.mkeyResultCallBack(callback, errCode, errMsg);
                }
            });
        } else {
            MKAppUtils.mkeyResultCallBack(callback, "2", "输入参数错误");
        }
    }

    public void deleteCert(MKeyApiCallback callback) {
        MKUserInfo userInfo = MKAppManager.getInstance().getUserInfo();
        if (userInfo != null && userInfo.getIdNo().length() != 0) {
            MKCertManager.getInstance().certDelete(callback);
        } else {
            MKAppUtils.mkeyResultCallBack(callback, "2", "输入参数错误");
        }
    }

    public void getCertContCodeList(MKeyApiCallback callback) {
        MKCertManager.getInstance().certGetList(callback);
    }

    public void getCert(MKeyApiCallback callback) {
        MKUserInfo userInfo = MKAppManager.getInstance().getUserInfo();
        if (userInfo != null && userInfo.getIdNo().length() != 0) {
            MKCertManager.getInstance().certGet(callback);
        } else {
            MKAppUtils.mkeyResultCallBack(callback, "2", "输入参数错误");
        }
    }

    public void getCertInfo(MKeyApiCallback callback) {
        MKUserInfo userInfo = MKAppManager.getInstance().getUserInfo();
        if (userInfo != null && userInfo.getIdNo().length() != 0) {
            MKCertManager.getInstance().certInfoGet(callback);
        } else {
            MKAppUtils.mkeyResultCallBack(callback, "2", "输入参数错误");
        }
    }

    public void getCertInfoByOid(String strOid, MKeyApiCallback callback) {
        if (MKAppManager.getInstance().getUserInfo() == null) {
            MKAppUtils.mkeyResultCallBack(callback, "2", "输入参数错误");
        }

        MKCertManager.getInstance().certOidInfoGet(strOid, callback);
    }

    public void signature(final String signSrc, final String pin, final MKeyApiCallback callback) {
        MKUserInfo userInfo = MKAppManager.getInstance().getUserInfo();
        if (MKAppManager.getInstance().getAppCode().length() != 0 && userInfo != null && userInfo.getName().length() != 0 && userInfo.getIdNo().length() != 0 && signSrc != null && signSrc.length() != 0 && pin != null && pin.length() != 0) {
            MKAppNet.userAuth(MKAppManager.getInstance().getContext(), userInfo.getName(), userInfo.getIdNo(), userInfo.getMobile(), MKAppManager.getInstance().getAppCode(), new MKAppNet.UserAuthCallBack() {
                public void onSuccess() {
                    MKCertSignature.getInstance().signature(signSrc, pin, callback);
                }

                public void onfailure(String errCode, String errMsg) {
                    MKAppUtils.mkeyResultCallBack(callback, errCode, errMsg);
                }
            });
        } else {
            MKAppUtils.mkeyResultCallBack(callback, "2", "输入参数错误");
        }
    }

    public void verifySignature(String signSrc, String signValue, MKeyApiCallback callback) {
        MKUserInfo userInfo = MKAppManager.getInstance().getUserInfo();
        if (userInfo != null && userInfo.getIdNo().length() != 0 && signSrc != null && signSrc.length() != 0 && signValue != null && signValue.length() != 0) {
            MKCertSignature.getInstance().verifySignature(signSrc, signValue, callback);
        } else {
            MKAppUtils.mkeyResultCallBack(callback, "2", "输入参数错误");
        }
    }

    public void verifyPin(final String pin, final MKeyApiCallback callback) {
        MKUserInfo userInfo = MKAppManager.getInstance().getUserInfo();
        if (userInfo != null && userInfo.getIdNo().length() != 0 && pin != null && pin.length() != 0) {
            MKAppNet.userAuth(MKAppManager.getInstance().getContext(), userInfo.getName(), userInfo.getIdNo(), userInfo.getMobile(), MKAppManager.getInstance().getAppCode(), new MKAppNet.UserAuthCallBack() {
                public void onSuccess() {
                    MKCertManager.getInstance().pinVerify(pin, callback);
                }

                public void onfailure(String errCode, String errMsg) {
                    MKAppUtils.mkeyResultCallBack(callback, errCode, errMsg);
                }
            });
        } else {
            MKAppUtils.mkeyResultCallBack(callback, "2", "输入参数错误");
        }
    }

    public void modifyPin(final String oldPin, final String newPin, final MKeyApiCallback callback) {
        MKUserInfo userInfo = MKAppManager.getInstance().getUserInfo();
        if (userInfo != null && userInfo.getIdNo().length() != 0) {
            MKAppNet.userAuth(MKAppManager.getInstance().getContext(), userInfo.getName(), userInfo.getIdNo(), userInfo.getMobile(), MKAppManager.getInstance().getAppCode(), new MKAppNet.UserAuthCallBack() {
                public void onSuccess() {
                    MKCertManager.getInstance().pinChange(oldPin, newPin, callback);
                }

                public void onfailure(String errCode, String errMsg) {
                    MKAppUtils.mkeyResultCallBack(callback, errCode, errMsg);
                }
            });
        } else {
            MKAppUtils.mkeyResultCallBack(callback, "2", "输入参数错误");
        }
    }

    public void unlockPin(final String adminPin, final String newPin, final MKeyApiCallback callback) {
        MKUserInfo userInfo = MKAppManager.getInstance().getUserInfo();
        if (MKAppManager.getInstance().getAppCode().length() != 0 && userInfo != null && userInfo.getName().length() != 0 && userInfo.getIdNo().length() != 0) {
            MKAppNet.userAuth(MKAppManager.getInstance().getContext(), userInfo.getName(), userInfo.getIdNo(), userInfo.getMobile(), MKAppManager.getInstance().getAppCode(), new MKAppNet.UserAuthCallBack() {
                public void onSuccess() {
                    MKCertManager.getInstance().pinUnlock(adminPin, newPin, callback);
                }

                public void onfailure(String errCode, String errMsg) {
                    MKAppUtils.mkeyResultCallBack(callback, errCode, errMsg);
                }
            });
        } else {
            MKAppUtils.mkeyResultCallBack(callback, "2", "输入参数错误");
        }
    }

    public void getFreeSignStatus(final MKeyApiCallback callback) {
        MKUserInfo userInfo = MKAppManager.getInstance().getUserInfo();
        if (MKAppManager.getInstance().getAppCode().length() != 0 && userInfo != null && userInfo.getName().length() != 0 && userInfo.getIdNo().length() != 0) {
            MKAppNet.userAuth(MKAppManager.getInstance().getContext(), userInfo.getName(), userInfo.getIdNo(), userInfo.getMobile(), MKAppManager.getInstance().getAppCode(), new MKAppNet.UserAuthCallBack() {
                public void onSuccess() {
                    MKCertManager.getInstance().freeSignGetStatus(callback);
                }

                public void onfailure(String errCode, String errMsg) {
                    MKAppUtils.mkeyResultCallBack(callback, errCode, errMsg);
                }
            });
        } else {
            MKAppUtils.mkeyResultCallBack(callback, "2", "输入参数错误");
        }
    }

    public void setFreeSign(final String pin, final MKeyApiCallback callback) {
        MKUserInfo userInfo = MKAppManager.getInstance().getUserInfo();
        if (MKAppManager.getInstance().getAppCode().length() != 0 && userInfo != null && userInfo.getName().length() != 0 && userInfo.getIdNo().length() != 0 && pin != null && pin.length() != 0) {
            MKAppNet.userAuth(MKAppManager.getInstance().getContext(), userInfo.getName(), userInfo.getIdNo(), userInfo.getMobile(), MKAppManager.getInstance().getAppCode(), new MKAppNet.UserAuthCallBack() {
                public void onSuccess() {
                    MKCertManager.getInstance().freeSignSet(pin, callback);
                }

                public void onfailure(String errCode, String errMsg) {
                    MKAppUtils.mkeyResultCallBack(callback, errCode, errMsg);
                }
            });
        } else {
            MKAppUtils.mkeyResultCallBack(callback, "2", "输入参数错误");
        }
    }

    public void cancelFreeSign(final String pin, final MKeyApiCallback callback) {
        MKUserInfo userInfo = MKAppManager.getInstance().getUserInfo();
        if (MKAppManager.getInstance().getAppCode().length() != 0 && userInfo != null && userInfo.getName().length() != 0 && userInfo.getIdNo().length() != 0 && pin != null && pin.length() != 0) {
            MKAppNet.userAuth(MKAppManager.getInstance().getContext(), userInfo.getName(), userInfo.getIdNo(), userInfo.getMobile(), MKAppManager.getInstance().getAppCode(), new MKAppNet.UserAuthCallBack() {
                public void onSuccess() {
                    MKCertManager.getInstance().freeSignCancel(pin, callback);
                }

                public void onfailure(String errCode, String errMsg) {
                    MKAppUtils.mkeyResultCallBack(callback, errCode, errMsg);
                }
            });
        } else {
            MKAppUtils.mkeyResultCallBack(callback, "2", "输入参数错误");
        }
    }
}
