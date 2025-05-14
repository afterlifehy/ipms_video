//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.custle.ksmkey.certificate;

import android.content.Context;
import android.util.Base64;

import com.custle.ksmkey.MKeyApiCallback;
import com.custle.ksmkey.bean.MKDecryptValueBean;
import com.custle.ksmkey.bean.MKSignValueBean;
import com.custle.ksmkey.common.MKAppManager;
import com.custle.ksmkey.interfaces.MKBaseValueCallBack;
import com.custle.ksmkey.service.MKCertService;
import com.custle.ksmkey.util.MKAppUtils;

import java.util.List;

public class MKCertSignature {
    private static volatile MKCertSignature certSignature = null;

    public static MKCertSignature getInstance() {
        if (certSignature == null) {
            Class var0 = MKCertSignature.class;
            synchronized(MKCertSignature.class) {
                if (certSignature == null) {
                    certSignature = new MKCertSignature();
                }
            }
        }

        return certSignature;
    }

    public MKCertSignature() {
    }

    public void signature(String signSrc, String pin, MKeyApiCallback callback) {
        String certId = this.getCertId();
        byte[] pbCert = new byte[2048];
        int[] iCertLen = new int[2];
        int iRet = KSCertificate.getInstance(MKAppManager.getInstance().getContext()).getCert(certId, 1, pbCert, iCertLen);
        if (iRet != 0) {
            MKAppUtils.mkeyResultCallBack(callback, iRet, "未找到证书");
        } else {
            String strCert = Base64.encodeToString(pbCert, 0, iCertLen[0], 2);
            KSCertInfo certInfo = KSCertificate.getInstance(MKAppManager.getInstance().getContext()).getCertInfo(strCert);
            if (certInfo != null && certInfo.getCertSn() != null && certInfo.getCertSn().length() != 0) {
                iRet = KSCertificate.getInstance(MKAppManager.getInstance().getContext()).verifyPin(certId, pin);
                if (iRet == 0) {
                    this.signature(certId, certInfo.getCertSn(), signSrc, pin, callback);
                } else if (iRet == 8720) {
                    MKAppUtils.mkeyResultCallBack(callback, 210, "证书密码锁死");
                } else if (iRet > 8704 && iRet < 8720) {
                    String strMsg = "证书密码验证错误，剩余输入" + (iRet - 8704) + "次";
                    int errCode = 200 + (iRet - 8704);
                    MKAppUtils.mkeyResultCallBack(callback, errCode, strMsg);
                } else {
                    MKAppUtils.mkeyResultCallBack(callback, iRet, "证书密码验证异常");
                }

            } else {
                MKAppUtils.mkeyResultCallBack(callback, 104, "获取证书信息失败");
            }
        }
    }

    private void signature(final String userID, String certSn, String signSrc, final String pin, final MKeyApiCallback callback) {
        try {
            final Context context = MKAppManager.getInstance().getContext();
            byte[] pbDigest = new byte[32];
            int iRet = KSCertificate.getInstance(context).hashForSign(userID, pin, signSrc.getBytes(), signSrc.length(), pbDigest);
            if (iRet != 0) {
                MKAppUtils.mkeyResultCallBack(callback, iRet, "签名失败");
                return;
            }

            String strDigest = Base64.encodeToString(pbDigest, 0, 32, 2);
            byte[] pbKeyId = new byte[32];
            iRet = KSCertificate.getInstance(context).getKeyId(userID, pin, pbKeyId);
            if (iRet != 0) {
                MKAppUtils.mkeyResultCallBack(callback, iRet, "签名失败");
                return;
            }

            String strKeyId = Base64.encodeToString(pbKeyId, 0, 32, 2);
            final long[] signHandle = new long[2];
            iRet = KSCertificate.getInstance(context).sm2PartSignInit(userID, pin, signHandle);
            if (iRet != 0) {
                MKAppUtils.mkeyResultCallBack(callback, iRet, "签名失败");
                return;
            }

            byte[] pbSignS1 = new byte[64];
            iRet = KSCertificate.getInstance(context).sm2PartSignS1(userID, signHandle[0], pin, pbSignS1);
            if (iRet != 0) {
                MKAppUtils.mkeyResultCallBack(callback, iRet, "签名失败");
                return;
            }

            String strSignS1 = Base64.encodeToString(pbSignS1, 0, 64, 2);
            MKCertService.serverSignNet(certSn, strSignS1, strDigest, strKeyId, new MKBaseValueCallBack() {
                public void onResult(Integer ret, String msg, Object object) {
                    if (ret != 0) {
                        MKAppUtils.mkeyResultCallBack(callback, ret, msg);
                    } else {
                        MKSignValueBean.SignData signData = (MKSignValueBean.SignData)object;
                        if (signData == null) {
                            MKAppUtils.mkeyResultCallBack(callback, 15, "服务返回数据为空");
                        } else {
                            List<String> signValueList = signData.getSignValue();
                            byte[] pbSignS2 = Base64.decode((String)signValueList.get(0), 2);
                            byte[] pbSignValue = new byte[128];
                            int[] iSignValueLen = new int[2];
                            int res = KSCertificate.getInstance(context).sm2PartSignS3(userID, signHandle[0], pbSignS2, pin, pbSignValue, iSignValueLen);
                            if (res == 0) {
                                String strSign = Base64.encodeToString(pbSignValue, 0, iSignValueLen[0], 2);
                                MKAppUtils.mkeyResultCallBack(callback, 0, "签名成功", strSign);
                            } else {
                                MKAppUtils.mkeyResultCallBack(callback, res, "签名失败");
                            }

                        }
                    }
                }
            });
        } catch (Exception var15) {
            MKAppUtils.mkeyResultCallBack(callback, 12, var15.getLocalizedMessage());
        }

    }

    public void verifySignature(String signSrc, String strCert, String signValue, MKeyApiCallback callback) {
        String certId = this.getCertId();
        byte[] pbSignValue = Base64.decode(signValue, 2);
        int iRet = KSCertificate.getInstance(MKAppManager.getInstance().getContext()).verifySignature(certId, signSrc.getBytes(), signSrc.length(), pbSignValue, pbSignValue.length, strCert);
        if (iRet == 0) {
            MKAppUtils.mkeyResultCallBack(callback, 0, "签名验证成功");
        } else {
            MKAppUtils.mkeyResultCallBack(callback, iRet, "签名验证失败");
        }

    }

    public void sm2Encrypt(String srcData, String strCert, MKeyApiCallback callback) {
        String certId = this.getCertId();
        byte[] pbEncData = new byte[srcData.length() + 128];
        int[] iEncLen = new int[2];
        int iRet = KSCertificate.getInstance(MKAppManager.getInstance().getContext()).sm2Encrypt(certId, srcData.getBytes(), srcData.length(), strCert, pbEncData, iEncLen);
        if (iRet == 0) {
            String strEncData = Base64.encodeToString(pbEncData, 0, iEncLen[0], 2);
            MKAppUtils.mkeyResultCallBack(callback, 0, "加密成功", strEncData);
        } else {
            MKAppUtils.mkeyResultCallBack(callback, 300, "加密失败");
        }

    }

    public void sm2Decrypt(String encData, String pin, MKeyApiCallback callback) {
        String certId = this.getCertId();
        byte[] pbCert = new byte[2048];
        int[] iCertLen = new int[2];
        int iRet = KSCertificate.getInstance(MKAppManager.getInstance().getContext()).getCert(certId, 1, pbCert, iCertLen);
        if (iRet != 0) {
            MKAppUtils.mkeyResultCallBack(callback, iRet, "未找到证书");
        } else {
            iRet = KSCertificate.getInstance(MKAppManager.getInstance().getContext()).verifyPin(certId, pin);
            if (iRet == 0) {
                byte[] pbEncData = Base64.decode(encData, 2);
                byte[] pbDecData = new byte[encData.length()];
                int[] iDecLen = new int[2];
                iRet = KSCertificate.getInstance(MKAppManager.getInstance().getContext()).sm2Decrypt(certId, pbEncData, pbEncData.length, pin, pbDecData, iDecLen);
                if (iRet == 0) {
                    String strDecData = Base64.encodeToString(pbDecData, 0, iDecLen[0], 2);
                    MKAppUtils.mkeyResultCallBack(callback, 0, "解密成功", strDecData);
                } else {
                    MKAppUtils.mkeyResultCallBack(callback, 300, "解密失败");
                }
            } else if (iRet == 8720) {
                MKAppUtils.mkeyResultCallBack(callback, 210, "证书密码锁死");
            } else if (iRet > 8704 && iRet < 8720) {
                String strMsg = "证书密码验证错误，剩余输入" + (iRet - 8704) + "次";
                int errCode = 200 + (iRet - 8704);
                MKAppUtils.mkeyResultCallBack(callback, errCode, strMsg);
            } else {
                MKAppUtils.mkeyResultCallBack(callback, iRet, "证书密码验证异常");
            }

        }
    }

    public void sm2PartEncrypt(String srcData, String strCert, MKeyApiCallback callback) {
        String certId = this.getCertId();
        byte[] pbEncData = new byte[srcData.length() + 128];
        int[] iEncLen = new int[2];
        int iRet = KSCertificate.getInstance(MKAppManager.getInstance().getContext()).sm2PartEncrypt(certId, srcData.getBytes(), srcData.length(), strCert, pbEncData, iEncLen);
        if (iRet == 0) {
            String strEncData = Base64.encodeToString(pbEncData, 0, iEncLen[0], 2);
            MKAppUtils.mkeyResultCallBack(callback, 0, "加密成功", strEncData);
        } else {
            MKAppUtils.mkeyResultCallBack(callback, 300, "加密失败");
        }

    }

    public void sm2PairDecrypt(String encData, String pin, MKeyApiCallback callback) {
        String certId = this.getCertId();
        byte[] pbCert = new byte[2048];
        int[] iCertLen = new int[2];
        int iRet = KSCertificate.getInstance(MKAppManager.getInstance().getContext()).getCert(certId, 1, pbCert, iCertLen);
        if (iRet != 0) {
            MKAppUtils.mkeyResultCallBack(callback, iRet, "未找到证书");
        } else {
            String strCert = Base64.encodeToString(pbCert, 0, iCertLen[0], 2);
            KSCertInfo certInfo = KSCertificate.getInstance(MKAppManager.getInstance().getContext()).getCertInfo(strCert);
            if (certInfo != null && certInfo.getCertSn() != null && certInfo.getCertSn().length() != 0) {
                iRet = KSCertificate.getInstance(MKAppManager.getInstance().getContext()).verifyPin(certId, pin);
                if (iRet == 0) {
                    this.sm2PairDecrypt(certId, encData, certInfo.getCertSn(), pin, callback);
                } else if (iRet == 8720) {
                    MKAppUtils.mkeyResultCallBack(callback, 210, "证书密码锁死");
                } else if (iRet > 8704 && iRet < 8720) {
                    String strMsg = "证书密码验证错误，剩余输入" + (iRet - 8704) + "次";
                    int errCode = 200 + (iRet - 8704);
                    MKAppUtils.mkeyResultCallBack(callback, errCode, strMsg);
                } else {
                    MKAppUtils.mkeyResultCallBack(callback, iRet, "证书密码验证异常");
                }

            } else {
                MKAppUtils.mkeyResultCallBack(callback, 104, "获取证书信息失败");
            }
        }
    }

    private void sm2PairDecrypt(final String userID, final String encData, String certSn, final String pin, final MKeyApiCallback callback) {
        try {
            final Context context = MKAppManager.getInstance().getContext();
            byte[] pbKeyId = new byte[32];
            int iRet = KSCertificate.getInstance(context).getKeyId(userID, pin, pbKeyId);
            if (iRet != 0) {
                MKAppUtils.mkeyResultCallBack(callback, iRet, "获取KeyID失败");
                return;
            }

            String strKeyId = Base64.encodeToString(pbKeyId, 0, 32, 2);
            final long[] decHandle = new long[2];
            iRet = KSCertificate.getInstance(context).sm2PartDecryptInit(userID, pin, decHandle);
            if (iRet != 0) {
                MKAppUtils.mkeyResultCallBack(callback, iRet, "解密初始化失败");
                return;
            }

            byte[] pbIn = Base64.decode(encData, 2);
            byte[] pbDecT1 = new byte[128];
            int[] iT1Len = new int[2];
            iRet = KSCertificate.getInstance(context).sm2PartDecryptT1(userID, decHandle[0], pbIn, pbIn.length, pbDecT1, iT1Len);
            if (iRet != 0) {
                MKAppUtils.mkeyResultCallBack(callback, iRet, "解密分量1失败");
                return;
            }

            String strT1 = Base64.encodeToString(pbDecT1, 0, iT1Len[0], 2);
            MKCertService.serverDecryptNet(strT1, certSn, strKeyId, new MKBaseValueCallBack() {
                public void onResult(Integer ret, String msg, Object object) {
                    if (ret != 0) {
                        MKAppUtils.mkeyResultCallBack(callback, ret, msg);
                    } else {
                        MKDecryptValueBean.DecryptData decryptData = (MKDecryptValueBean.DecryptData)object;
                        if (decryptData != null && decryptData.getDecStr() != null) {
                            byte[] pbDecT2 = Base64.decode(decryptData.getDecStr(), 2);
                            byte[] pbDecT3 = new byte[encData.length() - 90];
                            int[] iT3Len = new int[2];
                            int res = KSCertificate.getInstance(context).sm2PartDecryptT3(userID, decHandle[0], pbDecT2, pbDecT2.length, pbDecT3, iT3Len);
                            if (res == 0) {
                                String strDec = new String(pbDecT3, 0, iT3Len[0]);
                                MKAppUtils.mkeyResultCallBack(callback, 0, "解密成功", strDec);
                            } else {
                                MKAppUtils.mkeyResultCallBack(callback, res, "解密失败");
                            }

                        } else {
                            MKAppUtils.mkeyResultCallBack(callback, 15, "服务返回数据为空");
                        }
                    }
                }
            });
        } catch (Exception var15) {
            MKAppUtils.mkeyResultCallBack(callback, 12, var15.getLocalizedMessage());
        }

    }

    private String getCertId() {
        return MKAppManager.getInstance().getCertId() + "_" + MKAppManager.getInstance().getContCode();
    }
}
