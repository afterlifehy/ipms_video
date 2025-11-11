//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.custle.ksmkey.certificate;

import android.util.Base64;
import com.custle.ksmkey.MKeyApiCallback;
import com.custle.ksmkey.common.MKAppManager;
import com.custle.ksmkey.util.MKAppUtils;
import com.custle.ksmkey.util.MKNetUtils;
import com.custle.ksmkey.util.MKUtils;
import org.json.JSONObject;

public class MKCertManager {
    private static volatile MKCertManager certManager = null;

    public static MKCertManager getInstance() {
        if (certManager == null) {
            Class var0 = MKCertManager.class;
            synchronized(MKCertManager.class) {
                if (certManager == null) {
                    certManager = new MKCertManager();
                }
            }
        }

        return certManager;
    }

    public MKCertManager() {
    }

    public void certApply(String envSn, String certSn, String certDn, String pin, MKeyApiCallback callback) {
        byte[] pbP10 = new byte[1024];
        int[] p10Len = new int[2];
        int iRet = KSCertificate.getInstance(MKAppManager.getInstance().getContext()).makeP10(this.getCertId(), certDn, pin, true, pbP10, p10Len);
        if (iRet != 0) {
            MKAppUtils.mkeyResultCallBack(callback, 101, "生成密钥对错误");
        } else {
            String strP10 = Base64.encodeToString(pbP10, 0, p10Len[0], 2);
            this.certApplyByKeyId(envSn, certSn, strP10, pin, callback);
        }
    }

    private void certApplyByKeyId(final String envSn, final String certSn, final String p10, final String pin, final MKeyApiCallback callback) {
        String keyId = MKUtils.getP10Item(p10, 2);
        MKNetUtils.MK_GetCertFormKeyId(keyId, new MKNetUtils.BaseValueCallBack() {
            public void onResult(int ret, String msg, Object object) {
                if (ret == 0) {
                    int iRet = KSCertificate.getInstance(MKAppManager.getInstance().getContext()).saveCert(MKCertManager.this.getCertId(), (String)object, "", "", pin, true);
                    if (iRet == 0) {
                        MKAppUtils.mkeyResultCallBack(callback, 0, "证书申请成功");
                    } else {
                        MKCertManager.this.certApplyGenKey(envSn, certSn, p10, pin, callback);
                    }
                } else {
                    MKCertManager.this.certApplyGenKey(envSn, certSn, p10, pin, callback);
                }

            }
        });
    }

    private void certApplyGenKey(final String envSn, final String certSn, String p10, final String pin, final MKeyApiCallback callback) {
        String key = MKUtils.getP10Item(p10, 1);
        final String keyId = MKUtils.getP10Item(p10, 2);
        final String csr = MKUtils.getP10Item(p10, 3);
        MKNetUtils.MK_PostGenKey(key, keyId, new MKNetUtils.BaseCallBack() {
            public void onResult(int ret, String msg) {
                if (ret == 0) {
                    MKCertManager.this.CertApplyRequestCert(envSn, certSn, csr, keyId, pin, callback);
                } else {
                    if (ret == 1050) {
                        KSCertificate.getInstance(MKAppManager.getInstance().getContext()).makeP10TmpKeyDelete(MKCertManager.this.getCertId(), true);
                    }

                    MKAppUtils.mkeyResultCallBack(callback, ret, msg);
                }

            }
        });
    }

    private void CertApplyRequestCert(String envSn, String certSn, String csr, String keyId, final String pin, final MKeyApiCallback callback) {
        if (certSn != null && !certSn.isEmpty()) {
            MKNetUtils.MK_ReApplyCert(envSn, certSn, keyId, csr, new MKNetUtils.BaseValueCallBack() {
                public void onResult(int ret, String msg, Object object) {
                    if (ret != 0) {
                        if (ret == 1050) {
                            KSCertificate.getInstance(MKAppManager.getInstance().getContext()).makeP10TmpKeyDelete(MKCertManager.this.getCertId(), true);
                        }

                        MKAppUtils.mkeyResultCallBack(callback, ret, msg);
                    } else {
                        int iRet = KSCertificate.getInstance(MKAppManager.getInstance().getContext()).saveCert(MKCertManager.this.getCertId(), (String)object, "", "", pin, true);
                        if (iRet == 0) {
                            MKAppUtils.mkeyResultCallBack(callback, 0, "证书重签成功");
                        } else {
                            MKAppUtils.mkeyResultCallBack(callback, iRet, "证书保存失败");
                        }

                    }
                }
            });
        } else {
            MKNetUtils.MK_ApplyCert(envSn, keyId, csr, new MKNetUtils.BaseValueCallBack() {
                public void onResult(int ret, String msg, Object object) {
                    if (ret != 0) {
                        if (ret == 1050) {
                            KSCertificate.getInstance(MKAppManager.getInstance().getContext()).makeP10TmpKeyDelete(MKCertManager.this.getCertId(), true);
                        }

                        MKAppUtils.mkeyResultCallBack(callback, ret, msg);
                    } else {
                        int iRet = KSCertificate.getInstance(MKAppManager.getInstance().getContext()).saveCert(MKCertManager.this.getCertId(), (String)object, "", "", pin, true);
                        if (iRet == 0) {
                            MKAppUtils.mkeyResultCallBack(callback, 0, "证书申请成功");
                        } else {
                            MKAppUtils.mkeyResultCallBack(callback, iRet, "证书保存失败");
                        }

                    }
                }
            });
        }

    }

    public void certUpdate(String envSn, String certDn, String pin, MKeyApiCallback callback) {
        String certId = this.getCertId();
        byte[] pbCert = new byte[2048];
        int[] iCertLen = new int[2];
        int iRet = KSCertificate.getInstance(MKAppManager.getInstance().getContext()).getCert(certId, 1, pbCert, iCertLen);
        if (iRet != 0) {
            MKAppUtils.mkeyResultCallBack(callback, iRet, "未找到证书");
        } else {
            String strCert = Base64.encodeToString(pbCert, 0, iCertLen[0], 2);
            KSCertInfo certInfo = KSCertificate.getInstance(MKAppManager.getInstance().getContext()).getCertInfo(strCert);
            if (certInfo != null && certInfo.getCertSn() != null && !certInfo.getCertSn().isEmpty()) {
                iRet = KSCertificate.getInstance(MKAppManager.getInstance().getContext()).verifyPin(certId, pin);
                if (iRet == 0) {
                    byte[] pbP10 = new byte[1024];
                    int[] p10Len = new int[2];
                    iRet = KSCertificate.getInstance(MKAppManager.getInstance().getContext()).makeP10(certId, certDn, pin, false, pbP10, p10Len);
                    if (iRet != 0) {
                        MKAppUtils.mkeyResultCallBack(callback, 101, "生成密钥对错误");
                        return;
                    }

                    String strP10 = Base64.encodeToString(pbP10, 0, p10Len[0], 2);
                    this.certUpdateByKeyId(envSn, strP10, pin, certInfo.getCertSn(), callback);
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

    private void certUpdateByKeyId(final String envSn, final String p10, final String pin, final String certSn, final MKeyApiCallback callback) {
        String keyId = MKUtils.getP10Item(p10, 2);
        MKNetUtils.MK_GetCertFormKeyId(keyId, new MKNetUtils.BaseValueCallBack() {
            public void onResult(int ret, String msg, Object object) {
                if (ret == 0) {
                    int iRet = KSCertificate.getInstance(MKAppManager.getInstance().getContext()).saveCert(MKCertManager.this.getCertId(), (String)object, "", "", pin, false);
                    if (iRet == 0) {
                        MKAppUtils.mkeyResultCallBack(callback, 0, "证书更新成功");
                    } else {
                        MKAppUtils.mkeyResultCallBack(callback, iRet, "证书更新失败");
                    }
                } else {
                    MKCertManager.this.certUpdateGenKey(envSn, p10, pin, certSn, callback);
                }

            }
        });
    }

    private void certUpdateGenKey(final String envSn, String p10, final String pin, final String certSn, final MKeyApiCallback callback) {
        String key = MKUtils.getP10Item(p10, 1);
        final String keyId = MKUtils.getP10Item(p10, 2);
        final String csr = MKUtils.getP10Item(p10, 3);
        MKNetUtils.MK_PostGenKey(key, keyId, new MKNetUtils.BaseCallBack() {
            public void onResult(int ret, String msg) {
                if (ret == 0) {
                    MKCertManager.this.CertUpdateRequestCert(envSn, certSn, csr, keyId, pin, callback);
                } else {
                    if (ret == 1050) {
                        KSCertificate.getInstance(MKAppManager.getInstance().getContext()).makeP10TmpKeyDelete(MKCertManager.this.getCertId(), false);
                    }

                    MKAppUtils.mkeyResultCallBack(callback, ret, msg);
                }

            }
        });
    }

    private void CertUpdateRequestCert(String envSn, String certSn, String p10, String keyId, final String pin, final MKeyApiCallback callback) {
        MKNetUtils.MK_UpdateCert(envSn, certSn, keyId, p10, new MKNetUtils.BaseValueCallBack() {
            public void onResult(int ret, String msg, Object object) {
                if (ret != 0) {
                    if (ret == 1050) {
                        KSCertificate.getInstance(MKAppManager.getInstance().getContext()).makeP10TmpKeyDelete(MKCertManager.this.getCertId(), false);
                    }

                    MKAppUtils.mkeyResultCallBack(callback, ret, msg);
                } else {
                    int iRet = KSCertificate.getInstance(MKAppManager.getInstance().getContext()).saveCert(MKCertManager.this.getCertId(), (String)object, "", "", pin, false);
                    if (iRet == 0) {
                        MKAppUtils.mkeyResultCallBack(callback, 0, "证书更新成功");
                    } else {
                        MKAppUtils.mkeyResultCallBack(callback, iRet, "证书更新失败");
                    }

                }
            }
        });
    }

    public void certGet(MKeyApiCallback callback) {
        byte[] pbCert = new byte[2048];
        int[] iCertLen = new int[2];
        int iRet = KSCertificate.getInstance(MKAppManager.getInstance().getContext()).getCert(this.getCertId(), 1, pbCert, iCertLen);
        if (iRet != 0) {
            MKAppUtils.mkeyResultCallBack(callback, iRet, "证书获取失败");
        } else {
            String strCert = Base64.encodeToString(pbCert, 0, iCertLen[0], 2);
            MKAppUtils.mkeyResultCallBack(callback, 0, "证书获取成功", strCert);
        }
    }

    public void certInfoGet(String strCert, MKeyApiCallback callback) {
        try {
            String strTCert = "";
            if (strCert != null && !strCert.isEmpty()) {
                strTCert = strCert;
            } else {
                byte[] pbCert = new byte[2048];
                int[] iCertLen = new int[2];
                int iRet = KSCertificate.getInstance(MKAppManager.getInstance().getContext()).getCert(this.getCertId(), 1, pbCert, iCertLen);
                if (iRet != 0) {
                    MKAppUtils.mkeyResultCallBack(callback, iRet, "证书获取失败");
                    return;
                }

                strTCert = Base64.encodeToString(pbCert, 0, iCertLen[0], 2);
            }

            KSCertInfo certInfo = KSCertificate.getInstance(MKAppManager.getInstance().getContext()).getCertInfo(strTCert);
            if (certInfo == null || certInfo.getCertSn() == null || certInfo.getCertSn().isEmpty()) {
                MKAppUtils.mkeyResultCallBack(callback, 104, "获取证书信息失败");
                return;
            }

            JSONObject jsonObj = new JSONObject();
            jsonObj.put("certSn", certInfo.getCertSn());
            jsonObj.put("certIssuer", certInfo.getIssuerCN());
            jsonObj.put("certSubject", certInfo.getSubjectCN());
            jsonObj.put("startDate", certInfo.getStartDate());
            jsonObj.put("endDate", certInfo.getEndDate());
            String strCertInfo = jsonObj.toString();
            MKAppUtils.mkeyResultCallBack(callback, 0, "证书信息获取成功", strCertInfo);
        } catch (Exception var7) {
            MKAppUtils.mkeyResultCallBack(callback, 12, var7.getLocalizedMessage());
        }

    }

    public void certOidInfoGet(String strCert, String strOid, MKeyApiCallback callback) {
        String strTCert = "";
        if (strCert != null && !strCert.isEmpty()) {
            strTCert = strCert;
        } else {
            byte[] pbCert = new byte[2048];
            int[] iCertLen = new int[2];
            int iRet = KSCertificate.getInstance(MKAppManager.getInstance().getContext()).getCert(this.getCertId(), 1, pbCert, iCertLen);
            if (iRet != 0) {
                MKAppUtils.mkeyResultCallBack(callback, iRet, "证书获取失败");
                return;
            }

            strTCert = Base64.encodeToString(pbCert, 0, iCertLen[0], 2);
        }

        String strOidValue = KSCertificate.getInstance(MKAppManager.getInstance().getContext()).getCertInfoByOid(strTCert, strOid);
        if (strOidValue != null && !strOidValue.isEmpty()) {
            MKAppUtils.mkeyResultCallBack(callback, 0, "证书OID信息获取成功", strOidValue);
        } else {
            MKAppUtils.mkeyResultCallBack(callback, 106, "证书OID信息获取失败");
        }

    }

    public void certDelete(MKeyApiCallback callback) {
        boolean bRet = KSCertificate.getInstance(MKAppManager.getInstance().getContext()).deleteCert(this.getCertId());
        if (bRet) {
            MKAppUtils.mkeyResultCallBack(callback, 0, "证书删除成功");
        } else {
            MKAppUtils.mkeyResultCallBack(callback, 104, "证书获取失败");
        }

    }

    public void pinVerify(String pin, MKeyApiCallback callback) {
        String certId = this.getCertId();
        byte[] pbCert = new byte[2048];
        int[] iCertLen = new int[2];
        int iRet = KSCertificate.getInstance(MKAppManager.getInstance().getContext()).getCert(certId, 1, pbCert, iCertLen);
        if (iRet != 0) {
            MKAppUtils.mkeyResultCallBack(callback, iRet, "未找到证书");
        } else {
            iRet = KSCertificate.getInstance(MKAppManager.getInstance().getContext()).verifyPin(certId, pin);
            if (iRet == 0) {
                MKAppUtils.mkeyResultCallBack(callback, 0, "证书密码正确");
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

    public void pinChange(String oldPin, String newPin, MKeyApiCallback callback) {
        String certId = this.getCertId();
        byte[] pbCert = new byte[2048];
        int[] iCertLen = new int[2];
        int iRet = KSCertificate.getInstance(MKAppManager.getInstance().getContext()).getCert(certId, 1, pbCert, iCertLen);
        if (iRet != 0) {
            MKAppUtils.mkeyResultCallBack(callback, iRet, "未找到证书");
        } else {
            iRet = KSCertificate.getInstance(MKAppManager.getInstance().getContext()).modifyPin(certId, oldPin, newPin);
            if (iRet == 0) {
                MKAppUtils.mkeyResultCallBack(callback, 0, "修改证书密码成功");
            } else if (iRet == 8720) {
                MKAppUtils.mkeyResultCallBack(callback, 210, "证书密码锁死");
            } else if (iRet > 8704 && iRet < 8720) {
                String strMsg = "证书密码错误，剩余输入" + (iRet - 8704) + "次";
                int errCode = 200 + (iRet - 8704);
                MKAppUtils.mkeyResultCallBack(callback, errCode, strMsg);
            } else {
                MKAppUtils.mkeyResultCallBack(callback, iRet, "证书密码错误");
            }

        }
    }

    public void pinUnlock(String adminPin, String newPin, MKeyApiCallback callback) {
        int iRet = KSCertificate.getInstance(MKAppManager.getInstance().getContext()).unlockPin(this.getCertId(), adminPin, newPin);
        if (iRet == 0) {
            MKAppUtils.mkeyResultCallBack(callback, 0, "设置密码成功");
        } else {
            MKAppUtils.mkeyResultCallBack(callback, 211, "设置密码失败");
        }

    }

    private String getCertId() {
        return MKAppManager.getInstance().getCertId() + "_" + MKAppManager.getInstance().getContCode();
    }
}
