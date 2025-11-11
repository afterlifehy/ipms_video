//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.custle.ksmkey;

import android.content.Context;

import com.custle.ksmkey.certificate.MKCertManager;
import com.custle.ksmkey.certificate.MKCertSignature;
import com.custle.ksmkey.certificate.MKSecurity;
import com.custle.ksmkey.common.MKAppManager;
import com.custle.ksmkey.util.MKAppUtils;
import com.custle.ksmkey.util.MKDeviceIdUtil;
import com.custle.ksmkey.util.MKNetUtils;
import com.custle.ksmkey.util.MKUtils;

public class MKeyApi {
    private static volatile MKeyApi mKeyApi = null;

    public static String getVersion() {
        return "1.0.4";
    }

    public static String getDeviceId(Context context) {
        return MKDeviceIdUtil.getDeviceId(context);
    }

    public static void initSDK(String url, String contCode) {
        if (url != null && !url.isEmpty()) {
            MKAppManager.getInstance().setUrl(url);
        } else {
            MKAppManager.getInstance().setUrl("https://device.mkeysec.cn/sdk/v1");
        }

        if (contCode != null && !contCode.isEmpty()) {
            MKAppManager.getInstance().setContCode(contCode);
        } else {
            MKAppManager.getInstance().setContCode("");
        }

    }

    public static MKeyApi getInstance(Context context, String appId, String appCode, String certId) {
        if (context != null) {
            MKAppManager.getInstance().setContext(context);
        }

        if (appId != null && !appId.isEmpty()) {
            MKAppManager.getInstance().setAppId(appId);
        }

        if (appCode != null && !appCode.isEmpty()) {
            MKAppManager.getInstance().setAppCode(appCode);
        }

        if (certId != null && !certId.isEmpty()) {
            MKAppManager.getInstance().setCertId(certId);
        }

        if (mKeyApi == null) {
            Class var4 = MKeyApi.class;
            synchronized (MKeyApi.class) {
                if (mKeyApi == null) {
                    mKeyApi = new MKeyApi();
                }
            }
        }

        return mKeyApi;
    }

    public MKeyApi() {
    }

    public void applyCert(final String deviceCode, final String unitName, final String envSn, final String certSn, final String pin, final MKeyApiCallback callback) {
        if (!MKAppManager.getInstance().getAppCode().isEmpty() && !MKAppManager.getInstance().getCertId().isEmpty() && deviceCode != null && !deviceCode.isEmpty() && unitName != null && !unitName.isEmpty() && pin != null && !pin.isEmpty()) {
            MKNetUtils.MK_UserAuth(MKAppManager.getInstance().getContext(), deviceCode, MKAppManager.getInstance().getAppCode(), new MKNetUtils.BaseCallBack() {
                public void onResult(int ret, String msg) {
                    if (ret == 0) {
                        if (certSn != null && !certSn.isEmpty()) {
                            MKNetUtils.MK_CertResolve(certSn, new MKNetUtils.CertResolveCallBack() {
                                public void onResult(int ret, String msg, String equipmentCode, String deviceId, String deptName) {
                                    if (ret != 0) {
                                        MKAppUtils.mkeyResultCallBack(callback, ret, msg);
                                    } else {
                                        String certDn = "C=CN,CN=" + equipmentCode + ",O=" + deptName + ",OU=" + deviceId;
                                        MKCertManager.getInstance().certApply(envSn, certSn, certDn, pin, callback);
                                    }
                                }
                            });
                        } else {
                            String certDn = "C=CN,CN=" + deviceCode + ",O=" + unitName + ",OU=" + MKUtils.getDeviceUuid();
                            MKCertManager.getInstance().certApply(envSn, certSn, certDn, pin, callback);
                        }
                    } else {
                        MKAppUtils.mkeyResultCallBack(callback, ret, msg);
                    }

                }
            });
        } else {
            MKAppUtils.mkeyResultCallBack(callback, 2, "输入参数错误");
        }
    }

    public void updateCert(final String deviceCode, final String unitName, final String envSn, final String pin, final MKeyApiCallback callback) {
        if (!MKAppManager.getInstance().getAppCode().isEmpty() && !MKAppManager.getInstance().getCertId().isEmpty() && deviceCode != null && !deviceCode.isEmpty() && pin != null && !pin.isEmpty()) {
            MKNetUtils.MK_UserAuth(MKAppManager.getInstance().getContext(), deviceCode, MKAppManager.getInstance().getAppCode(), new MKNetUtils.BaseCallBack() {
                public void onResult(int ret, String msg) {
                    if (ret == 0) {
                        String certDn = "C=CN,CN=" + deviceCode + ",O=" + unitName + ",OU=" + MKDeviceIdUtil.getDeviceId(MKAppManager.getInstance().getContext());
                        MKCertManager.getInstance().certUpdate(envSn, certDn, pin, callback);
                    } else {
                        MKAppUtils.mkeyResultCallBack(callback, ret, msg);
                    }

                }
            });
        } else {
            MKAppUtils.mkeyResultCallBack(callback, 2, "输入参数错误");
        }
    }

    public void deleteCert(final MKeyApiCallback callback) {
        if (MKAppManager.getInstance().getCertId().isEmpty()) {
            MKAppUtils.mkeyResultCallBack(callback, 2, "输入参数错误");
        } else {
            MKCertManager.getInstance().certDelete(callback);
        }
    }

    public void getCert(final MKeyApiCallback callback) {
        if (MKAppManager.getInstance().getCertId().isEmpty()) {
            MKAppUtils.mkeyResultCallBack(callback, 2, "输入参数错误");
        } else {
            MKCertManager.getInstance().certGet(callback);
        }
    }

    public void getCertInfo(String strCert, final MKeyApiCallback callback) {
        if (MKAppManager.getInstance().getCertId().isEmpty()) {
            MKAppUtils.mkeyResultCallBack(callback, 2, "输入参数错误");
        } else {
            MKCertManager.getInstance().certInfoGet(strCert, callback);
        }
    }

    public void getCertInfoByOid(String strCert, final String strOid, final MKeyApiCallback callback) {
        if (MKAppManager.getInstance().getCertId().isEmpty()) {
            MKAppUtils.mkeyResultCallBack(callback, 2, "输入参数错误");
        }

        MKCertManager.getInstance().certOidInfoGet(strCert, strOid, callback);
    }

    public void signature(String deviceCode, final String signSrc, final String pin, final MKeyApiCallback callback) {
        if (!MKAppManager.getInstance().getAppCode().isEmpty() && !MKAppManager.getInstance().getCertId().isEmpty() && deviceCode != null && !deviceCode.isEmpty() && signSrc != null && !signSrc.isEmpty() && pin != null && !pin.isEmpty()) {
            MKNetUtils.MK_UserAuth(MKAppManager.getInstance().getContext(), deviceCode, MKAppManager.getInstance().getAppCode(), new MKNetUtils.BaseCallBack() {
                public void onResult(int ret, String msg) {
                    if (ret == 0) {
                        MKCertSignature.getInstance().signature(signSrc, pin, callback);
                    } else {
                        MKAppUtils.mkeyResultCallBack(callback, ret, msg);
                    }

                }
            });
        } else {
            MKAppUtils.mkeyResultCallBack(callback, 2, "输入参数错误");
        }
    }

    public void verifySignature(final String signSrc, String strCert, final String signValue, final MKeyApiCallback callback) {
        if (!MKAppManager.getInstance().getCertId().isEmpty() && signSrc != null && !signSrc.isEmpty() && strCert != null && !strCert.isEmpty() && signValue != null && !signValue.isEmpty()) {
            MKCertSignature.getInstance().verifySignature(signSrc, strCert, signValue, callback);
        } else {
            MKAppUtils.mkeyResultCallBack(callback, 2, "输入参数错误");
        }
    }

    public void sm2Encrypt(String srcData, String strCert, MKeyApiCallback callback) {
        if (!MKAppManager.getInstance().getCertId().isEmpty() && srcData != null && !srcData.isEmpty() && strCert != null && !strCert.isEmpty()) {
            MKCertSignature.getInstance().sm2PartEncrypt(srcData, strCert, callback);
        } else {
            MKAppUtils.mkeyResultCallBack(callback, 2, "输入参数错误");
        }
    }

    public void sm2Decrypt(String deviceCode, final String encData, final String pin, final MKeyApiCallback callback) {
        if (!MKAppManager.getInstance().getAppCode().isEmpty() && !MKAppManager.getInstance().getCertId().isEmpty() && deviceCode != null && !deviceCode.isEmpty() && encData != null && !encData.isEmpty() && pin != null && !pin.isEmpty()) {
            MKNetUtils.MK_UserAuth(MKAppManager.getInstance().getContext(), deviceCode, MKAppManager.getInstance().getAppCode(), new MKNetUtils.BaseCallBack() {
                public void onResult(int ret, String msg) {
                    if (ret == 0) {
                        MKCertSignature.getInstance().sm2PairDecrypt(encData, pin, callback);
                    } else {
                        MKAppUtils.mkeyResultCallBack(callback, ret, msg);
                    }

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
        if (!MKAppManager.getInstance().getCertId().isEmpty() && pin != null && !pin.isEmpty()) {
            MKCertManager.getInstance().pinVerify(pin, callback);
        } else {
            MKAppUtils.mkeyResultCallBack(callback, 2, "输入参数错误");
        }
    }

    public void modifyPin(final String oldPin, final String newPin, final MKeyApiCallback callback) {
        if (MKAppManager.getInstance().getCertId().isEmpty()) {
            MKAppUtils.mkeyResultCallBack(callback, 2, "输入参数错误");
        } else {
            MKCertManager.getInstance().pinChange(oldPin, newPin, callback);
        }
    }

    public void unlockPin(final String adminPin, final String newPin, final MKeyApiCallback callback) {
        if (MKAppManager.getInstance().getCertId().isEmpty()) {
            MKAppUtils.mkeyResultCallBack(callback, 2, "输入参数错误");
        } else {
            MKCertManager.getInstance().pinUnlock(adminPin, newPin, callback);
        }
    }
}
