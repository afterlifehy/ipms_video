//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.custle.ksmkey;

import android.content.Context;

import com.custle.ksmkey.bean.MKResolveResponse;
import com.custle.ksmkey.certificate.MKCertManager;
import com.custle.ksmkey.certificate.MKCertSignature;
import com.custle.ksmkey.certificate.MKSecurity;
import com.custle.ksmkey.common.MKAppManager;
import com.custle.ksmkey.common.MKAppNet;
import com.custle.ksmkey.interfaces.MKBaseValueCallBack;
import com.custle.ksmkey.service.MKCertService;
import com.custle.ksmkey.util.MKAppUtils;
import com.custle.ksmkey.util.MKDeviceIdUtil;
import com.custle.ksmkey.util.MKUtils;
import com.custle.okhttp.OkHttpUtils;
import com.custle.okhttp.https.HttpsUtils;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.OkHttpClient;

public class MKeyApi {
    private static volatile MKeyApi mKeyApi = null;

    public static String getVersion() {
        return "1.0.3";
    }

    public static String getDeviceId(Context context) {
        return MKDeviceIdUtil.getDeviceId(context);
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

    public static MKeyApi getInstance(Context context, String appId, String appCode, String certId) {
        if (context != null) {
            MKAppManager.getInstance().setContext(context);
        }

        if (appId != null && appId.length() != 0) {
            MKAppManager.getInstance().setAppId(appId);
        }

        if (appCode != null && appCode.length() != 0) {
            MKAppManager.getInstance().setAppCode(appCode);
        }

        if (certId != null && certId.length() != 0) {
            MKAppManager.getInstance().setCertId(certId);
        }

        if (mKeyApi == null) {
            Class var4 = MKeyApi.class;
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

    public void applyCert(final String deviceCode, final String unitName, final String envSn, final String certSn, final String pin, final MKeyApiCallback callback) {
        if (MKAppManager.getInstance().getAppCode().length() != 0 && MKAppManager.getInstance().getCertId().length() != 0 && deviceCode != null && deviceCode.length() != 0 && unitName != null && unitName.length() != 0 && pin != null && pin.length() != 0) {
            MKAppNet.userAuth(MKAppManager.getInstance().getContext(), deviceCode, MKAppManager.getInstance().getAppCode(), new MKAppNet.UserAuthCallBack() {
                public void onSuccess() {
                    if (certSn != null && certSn.length() != 0) {
                        MKCertService.certResolve(certSn, new MKBaseValueCallBack() {
                            public void onResult(Integer ret, String msg, Object object) {
                                if (ret != 0) {
                                    MKAppUtils.mkeyResultCallBack(callback, ret, msg);
                                } else {
                                    MKResolveResponse.ResolveData resolveData = (MKResolveResponse.ResolveData)object;
                                    if (resolveData == null) {
                                        MKAppUtils.mkeyResultCallBack(callback, 15, "服务返回数据为空");
                                    } else {
                                        String certDn = "C=CN,CN=" + resolveData.getEquipmentCode() + ",O=" + resolveData.getDeptName() + ",OU=" + resolveData.getDeviceId();
                                        MKCertManager.getInstance().certApply(envSn, certSn, certDn, pin, callback);
                                    }
                                }
                            }
                        });
                    } else {
                        String certDn = "C=CN,CN=" + deviceCode + ",O=" + unitName + ",OU=" + MKUtils.getDeviceUuid();
                        MKCertManager.getInstance().certApply(envSn, certSn, certDn, pin, callback);
                    }

                }

                public void onFailure(int errCode, String errMsg) {
                    MKAppUtils.mkeyResultCallBack(callback, errCode, errMsg);
                }
            });
        } else {
            MKAppUtils.mkeyResultCallBack(callback, 2, "输入参数错误");
        }
    }

    public void updateCert(final String deviceCode, final String unitName, final String envSn, final String pin, final MKeyApiCallback callback) {
        if (MKAppManager.getInstance().getAppCode().length() != 0 && MKAppManager.getInstance().getCertId().length() != 0 && deviceCode != null && deviceCode.length() != 0 && pin != null && pin.length() != 0) {
            MKAppNet.userAuth(MKAppManager.getInstance().getContext(), deviceCode, MKAppManager.getInstance().getAppCode(), new MKAppNet.UserAuthCallBack() {
                public void onSuccess() {
                    String certDn = "C=CN,CN=" + deviceCode + ",O=" + unitName + ",OU=" + MKDeviceIdUtil.getDeviceId(MKAppManager.getInstance().getContext());
                    MKCertManager.getInstance().certUpdate(envSn, certDn, pin, callback);
                }

                public void onFailure(int errCode, String errMsg) {
                    MKAppUtils.mkeyResultCallBack(callback, errCode, errMsg);
                }
            });
        } else {
            MKAppUtils.mkeyResultCallBack(callback, 2, "输入参数错误");
        }
    }

    public void deleteCert(final MKeyApiCallback callback) {
        if (MKAppManager.getInstance().getCertId().length() == 0) {
            MKAppUtils.mkeyResultCallBack(callback, 2, "输入参数错误");
        } else {
            MKCertManager.getInstance().certDelete(callback);
        }
    }

    public void getCert(final MKeyApiCallback callback) {
        if (MKAppManager.getInstance().getCertId().length() == 0) {
            MKAppUtils.mkeyResultCallBack(callback, 2, "输入参数错误");
        } else {
            MKCertManager.getInstance().certGet(callback);
        }
    }

    public void getCertInfo(String strCert, final MKeyApiCallback callback) {
        if (MKAppManager.getInstance().getCertId().length() == 0) {
            MKAppUtils.mkeyResultCallBack(callback, 2, "输入参数错误");
        } else {
            MKCertManager.getInstance().certInfoGet(strCert, callback);
        }
    }

    public void getCertInfoByOid(String strCert, final String strOid, final MKeyApiCallback callback) {
        if (MKAppManager.getInstance().getCertId().length() == 0) {
            MKAppUtils.mkeyResultCallBack(callback, 2, "输入参数错误");
        }

        MKCertManager.getInstance().certOidInfoGet(strCert, strOid, callback);
    }

    public void signature(String deviceCode, final String signSrc, final String pin, final MKeyApiCallback callback) {
        if (MKAppManager.getInstance().getAppCode().length() != 0 && MKAppManager.getInstance().getCertId().length() != 0 && deviceCode != null && deviceCode.length() != 0 && signSrc != null && signSrc.length() != 0 && pin != null && pin.length() != 0) {
            MKAppNet.userAuth(MKAppManager.getInstance().getContext(), deviceCode, MKAppManager.getInstance().getAppCode(), new MKAppNet.UserAuthCallBack() {
                public void onSuccess() {
                    MKCertSignature.getInstance().signature(signSrc, pin, callback);
                }

                public void onFailure(int errCode, String errMsg) {
                    MKAppUtils.mkeyResultCallBack(callback, errCode, errMsg);
                }
            });
        } else {
            MKAppUtils.mkeyResultCallBack(callback, 2, "输入参数错误");
        }
    }

    public void verifySignature(final String signSrc, String strCert, final String signValue, final MKeyApiCallback callback) {
        if (MKAppManager.getInstance().getCertId().length() != 0 && signSrc != null && signSrc.length() != 0 && strCert != null && strCert.length() != 0 && signValue != null && signValue.length() != 0) {
            MKCertSignature.getInstance().verifySignature(signSrc, strCert, signValue, callback);
        } else {
            MKAppUtils.mkeyResultCallBack(callback, 2, "输入参数错误");
        }
    }

    public void sm2Encrypt(String srcData, String strCert, MKeyApiCallback callback) {
        if (MKAppManager.getInstance().getCertId().length() != 0 && srcData != null && srcData.length() != 0 && strCert != null && strCert.length() != 0) {
            MKCertSignature.getInstance().sm2PartEncrypt(srcData, strCert, callback);
        } else {
            MKAppUtils.mkeyResultCallBack(callback, 2, "输入参数错误");
        }
    }

    public void sm2Decrypt(String deviceCode, final String encData, final String pin, final MKeyApiCallback callback) {
        if (MKAppManager.getInstance().getAppCode().length() != 0 && MKAppManager.getInstance().getCertId().length() != 0 && deviceCode != null && deviceCode.length() != 0 && encData != null && encData.length() != 0 && pin != null && pin.length() != 0) {
            MKAppNet.userAuth(MKAppManager.getInstance().getContext(), deviceCode, MKAppManager.getInstance().getAppCode(), new MKAppNet.UserAuthCallBack() {
                public void onSuccess() {
                    MKCertSignature.getInstance().sm2PairDecrypt(encData, pin, callback);
                }

                public void onFailure(int errCode, String errMsg) {
                    MKAppUtils.mkeyResultCallBack(callback, errCode, errMsg);
                }
            });
        } else {
            MKAppUtils.mkeyResultCallBack(callback, 2, "输入参数错误");
        }
    }

    public void sm3Hash(byte[] inData, MKeyApiCallback callback) {
        if (inData != null && inData.length != 0) {
            MKSecurity.getInstance().sm3Hash(inData, callback);
        } else {
            MKAppUtils.mkeyResultCallBack(callback, 2, "输入参数错误");
        }
    }

    public void sm4Encrypt(byte[] inData, byte[] key, MKeyApiCallback callback) {
        if (inData != null && inData.length != 0 && key != null && key.length != 0) {
            MKSecurity.getInstance().sm4Encrypt(inData, key, callback);
        } else {
            MKAppUtils.mkeyResultCallBack(callback, 2, "输入参数错误");
        }
    }

    public void sm4Decrypt(byte[] inData, byte[] key, MKeyApiCallback callback) {
        if (inData != null && inData.length != 0 && key != null && key.length != 0) {
            MKSecurity.getInstance().sm4Decrypt(inData, key, callback);
        } else {
            MKAppUtils.mkeyResultCallBack(callback, 2, "输入参数错误");
        }
    }

    public void verifyPin(final String pin, final MKeyApiCallback callback) {
        if (MKAppManager.getInstance().getCertId().length() != 0 && pin != null && pin.length() != 0) {
            MKCertManager.getInstance().pinVerify(pin, callback);
        } else {
            MKAppUtils.mkeyResultCallBack(callback, 2, "输入参数错误");
        }
    }

    public void modifyPin(final String oldPin, final String newPin, final MKeyApiCallback callback) {
        if (MKAppManager.getInstance().getCertId().length() == 0) {
            MKAppUtils.mkeyResultCallBack(callback, 2, "输入参数错误");
        } else {
            MKCertManager.getInstance().pinChange(oldPin, newPin, callback);
        }
    }

    public void unlockPin(final String adminPin, final String newPin, final MKeyApiCallback callback) {
        if (MKAppManager.getInstance().getCertId().length() == 0) {
            MKAppUtils.mkeyResultCallBack(callback, 2, "输入参数错误");
        } else {
            MKCertManager.getInstance().pinUnlock(adminPin, newPin, callback);
        }
    }
}
