//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.peakinfo.base.ca.com.custle.ksmkey.certificate;

import android.content.Context;

import com.custle.certificate.KSCertInfo;
import com.custle.certificate.KSCertificate;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.peakinfo.base.ca.com.custle.ksmkey.MKeyApiCallback;
import com.peakinfo.base.ca.com.custle.ksmkey.bean.MKApplyCertBean;
import com.peakinfo.base.ca.com.custle.ksmkey.bean.MKBaseBean;
import com.peakinfo.base.ca.com.custle.ksmkey.bean.MKTrustQueryBean;
import com.peakinfo.base.ca.com.custle.ksmkey.common.MKAppManager;
import com.peakinfo.base.ca.com.custle.ksmkey.util.MKAppUtils;
import com.peakinfo.base.ca.com.custle.ksmkey.util.MKJsonUtil;
import com.peakinfo.base.ca.com.custle.ksmkey.util.MKNetUtils;
import com.peakinfo.base.ca.com.custle.ksmkey.util.MKUtils;
import com.peakinfo.base.ca.com.custle.okhttp.OkHttpUtils;
import com.peakinfo.base.ca.com.custle.okhttp.builder.PostFormBuilder;
import com.peakinfo.base.ca.com.custle.okhttp.callback.StringCallback;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import okhttp3.Call;

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

    public void certApply(MKUserInfo userInfo, String pin, MKeyApiCallback callback) {
        String userID = MKAppManager.getInstance().getUserInfo().getIdNo() + MKAppManager.getInstance().getContCode();
        String p10 = KSCertificate.getInstance(MKAppManager.getInstance().getContext()).makeP10(userID, userInfo.getDn(), pin);
        if (p10 != null && !"".equals(p10)) {
            this.certApplyGenKey(p10, pin, callback);
        } else {
            MKAppUtils.mkeyResultCallBack(callback, "101", "生成密钥对错误");
        }

    }

    private void certApplyGenKey(String p10, final String pin, final MKeyApiCallback callback) {
        try {
            String key = MKUtils.getP10Item(p10, 1);
            final String keyId = MKUtils.getP10Item(p10, 2);
            final String csr = MKUtils.getP10Item(p10, 3);
            ((PostFormBuilder)((PostFormBuilder) OkHttpUtils.post().url(MKAppManager.getInstance().getUrl() + "/key/gen")).addHeader("token", MKAppManager.getInstance().getUserToken())).addParams("key", URLEncoder.encode(key, "UTF-8")).addParams("keyId", URLEncoder.encode(keyId, "UTF-8")).addParams("alg", "SM2").addParams("algVersion", "2").build().execute(new StringCallback() {
                public void onError(Call call, Exception e, int id) {
                    MKAppUtils.mkeyResultCallBack(callback, "10", e.getLocalizedMessage());
                }

                public void onResponse(String response, int id) {
                    try {
                        response = URLDecoder.decode(response, "UTF-8");
                        MKBaseBean bean = (MKBaseBean) MKJsonUtil.toObject(response, MKBaseBean.class);
                        if (bean != null && bean.getRet() == 0) {
                            MKCertManager.this.CertApplyRequestCert(csr, keyId, pin, callback);
                        } else {
                            MKAppUtils.mkeyResultCallBack(callback, "102", bean.getMsg());
                        }
                    } catch (Exception var4) {
                        MKAppUtils.mkeyResultCallBack(callback, "12", var4.getLocalizedMessage());
                    }

                }
            });
        } catch (Exception var7) {
            MKAppUtils.mkeyResultCallBack(callback, "12", var7.getLocalizedMessage());
        }

    }

    private void CertApplyRequestCert(String csr, String keyId, final String pin, final MKeyApiCallback callback) {
        try {
            ((PostFormBuilder)((PostFormBuilder)OkHttpUtils.post().url(MKAppManager.getInstance().getUrl() + "/cert/apply")).addHeader("token", MKAppManager.getInstance().getUserToken())).addParams("p10", URLEncoder.encode(csr, "UTF-8")).addParams("keyId", URLEncoder.encode(keyId, "UTF-8")).build().execute(new StringCallback() {
                public void onError(Call call, Exception e, int id) {
                    MKAppUtils.mkeyResultCallBack(callback, "10", e.getLocalizedMessage());
                }

                public void onResponse(String response, int id) {
                    try {
                        response = URLDecoder.decode(response, "UTF-8");
                        MKApplyCertBean bean = (MKApplyCertBean)MKJsonUtil.toObject(response, MKApplyCertBean.class);
                        if (bean != null && bean.getRet() == 0) {
                            String userID = MKAppManager.getInstance().getUserInfo().getIdNo() + MKAppManager.getInstance().getContCode();
                            boolean bRet = KSCertificate.getInstance(MKAppManager.getInstance().getContext()).saveCert(userID, bean.getData().getCert(), pin);
                            if (bRet) {
                                MKAppUtils.mkeyResultCallBack(callback, "0", "证书申请成功");
                            } else {
                                MKAppUtils.mkeyResultCallBack(callback, "103", "证书申请失败");
                            }
                        } else {
                            MKAppUtils.mkeyResultCallBack(callback, "100", bean.getMsg());
                        }
                    } catch (Exception var6) {
                        MKAppUtils.mkeyResultCallBack(callback, "12", var6.getLocalizedMessage());
                    }

                }
            });
        } catch (Exception var6) {
            MKAppUtils.mkeyResultCallBack(callback, "12", var6.getLocalizedMessage());
        }

    }

    public void certUpdate(String pin, MKeyApiCallback callback) {
        String userID = MKAppManager.getInstance().getUserInfo().getIdNo() + MKAppManager.getInstance().getContCode();
        KSCertInfo certInfo = KSCertificate.getInstance(MKAppManager.getInstance().getContext()).getCertInfo(userID);
        if (certInfo != null && certInfo.getCertSn() != null && certInfo.getCertSn().length() != 0) {
            int iRet = KSCertificate.getInstance(MKAppManager.getInstance().getContext()).verifyPin(userID, pin);
            String strMsg;
            String errCode;
            if (iRet == 0) {
                strMsg = userID + "_UD";
                errCode = KSCertificate.getInstance(MKAppManager.getInstance().getContext()).makeP10(strMsg, MKAppManager.getInstance().getUserInfo().getDn(), pin);
                if (errCode == null || errCode.length() == 0) {
                    MKAppUtils.mkeyResultCallBack(callback, "101", "生成密钥对错误");
                    return;
                }

                this.certUpdateGenKey(errCode, pin, certInfo.getCertSn(), callback);
            } else if (iRet == 8720) {
                MKAppUtils.mkeyResultCallBack(callback, "210", "证书密码锁死");
                MKNetUtils.postVerifyPinLog(MKAppManager.getInstance().getContext(), userID, pin, "证书密码锁死 ret=" + MKUtils.numberToHexStr(iRet));
            } else if (iRet > 8704 && iRet < 8720) {
                strMsg = "证书密码验证错误，剩余输入" + (iRet - 8704) + "次";
                errCode = "" + (Integer.valueOf("200") + (iRet - 8704));
                MKAppUtils.mkeyResultCallBack(callback, errCode, strMsg);
                MKNetUtils.postVerifyPinLog(MKAppManager.getInstance().getContext(), userID, pin, "证书密码验证错误 ret=" + MKUtils.numberToHexStr(iRet));
            } else {
                MKAppUtils.mkeyResultCallBack(callback, "213", "证书密码验证异常");
                MKNetUtils.postVerifyPinLog(MKAppManager.getInstance().getContext(), userID, pin, "证书密码验证异常 ret=" + MKUtils.numberToHexStr(iRet));
            }

        } else {
            MKAppUtils.mkeyResultCallBack(callback, "104", "获取证书信息失败");
        }
    }

    private void certUpdateGenKey(String p10, final String pin, final String certSn, final MKeyApiCallback callback) {
        try {
            String key = MKUtils.getP10Item(p10, 1);
            final String keyId = MKUtils.getP10Item(p10, 2);
            final String csr = MKUtils.getP10Item(p10, 3);
            ((PostFormBuilder)((PostFormBuilder)OkHttpUtils.post().url(MKAppManager.getInstance().getUrl() + "/key/gen")).addHeader("token", MKAppManager.getInstance().getUserToken())).addParams("key", URLEncoder.encode(key, "UTF-8")).addParams("keyId", URLEncoder.encode(keyId, "UTF-8")).addParams("alg", "SM2").addParams("algVersion", "2").build().execute(new StringCallback() {
                public void onError(Call call, Exception e, int id) {
                    MKAppUtils.mkeyResultCallBack(callback, "10", e.getLocalizedMessage());
                }

                public void onResponse(String response, int id) {
                    try {
                        response = URLDecoder.decode(response, "UTF-8");
                        MKBaseBean bean = (MKBaseBean)MKJsonUtil.toObject(response, MKBaseBean.class);
                        if (bean != null && bean.getRet() == 0) {
                            MKCertManager.this.CertUpdateRequestCert(certSn, csr, keyId, "12", pin, callback);
                        } else {
                            MKAppUtils.mkeyResultCallBack(callback, "102", bean.getMsg());
                        }
                    } catch (Exception var4) {
                        MKAppUtils.mkeyResultCallBack(callback, "12", var4.getLocalizedMessage());
                    }

                }
            });
        } catch (Exception var8) {
            MKAppUtils.mkeyResultCallBack(callback, "12", var8.getLocalizedMessage());
        }

    }

    private void CertUpdateRequestCert(String certSn, String p10, String keyId, String month, final String pin, final MKeyApiCallback callback) {
        try {
            ((PostFormBuilder)((PostFormBuilder)OkHttpUtils.post().url(MKAppManager.getInstance().getUrl() + "/cert/update")).addHeader("token", MKAppManager.getInstance().getUserToken())).addParams("certSn", certSn).addParams("p10", URLEncoder.encode(p10, "UTF-8")).addParams("keyId", URLEncoder.encode(keyId, "UTF-8")).addParams("month", month).addParams("client", "android").build().execute(new StringCallback() {
                public void onError(Call call, Exception e, int id) {
                    MKAppUtils.mkeyResultCallBack(callback, "10", e.getLocalizedMessage());
                }

                public void onResponse(String response, int id) {
                    try {
                        response = URLDecoder.decode(response, "UTF-8");
                        MKApplyCertBean bean = (MKApplyCertBean)MKJsonUtil.toObject(response, MKApplyCertBean.class);
                        if (bean != null && bean.getRet() == 0) {
                            String userID = MKAppManager.getInstance().getUserInfo().getIdNo() + MKAppManager.getInstance().getContCode();
                            String tempId = userID + "_UD";
                            boolean bRet = KSCertificate.getInstance(MKAppManager.getInstance().getContext()).saveUpdateCert(userID, tempId, bean.getData().getCert(), pin);
                            if (bRet) {
                                MKAppUtils.mkeyResultCallBack(callback, "0", "证书更新成功");
                            } else {
                                MKAppUtils.mkeyResultCallBack(callback, "103", "证书更新失败");
                            }
                        } else {
                            MKAppUtils.mkeyResultCallBack(callback, "100", bean.getMsg());
                        }
                    } catch (Exception var7) {
                        MKAppUtils.mkeyResultCallBack(callback, "12", var7.getLocalizedMessage());
                    }

                }
            });
        } catch (Exception var8) {
            MKAppUtils.mkeyResultCallBack(callback, "12", var8.getLocalizedMessage());
        }

    }

    public void certGetList(MKeyApiCallback callback) {
        String strCertList = KSCertificate.getInstance(MKAppManager.getInstance().getContext()).getCertList();
        if (strCertList != null && strCertList.length() != 0) {
            String[] contArr = strCertList.split(";");
            Map<String, String> map = new HashMap();
            String[] var5 = contArr;
            int var6 = contArr.length;

            String strKey;
            String strValue;
            for(int var7 = 0; var7 < var6; ++var7) {
                strKey = var5[var7];
                strValue = strKey.substring(0, 18);
                String strCode = strKey.substring(18);
                if (map.containsKey(strValue)) {
                    strValue = (String)map.get(strValue) + ";" + strCode;
                    map.put(strValue, strValue);
                } else {
                    map.put(strValue, strCode);
                }
            }

            JsonObject jsonObject = new JsonObject();
            Iterator var17 = map.entrySet().iterator();

            while(var17.hasNext()) {
                Map.Entry<String, String> entry = (Map.Entry)var17.next();
                strKey = (String)entry.getKey();
                strValue = (String)entry.getValue();
                JsonArray array = new JsonArray();
                String[] codeList = strValue.split(";");
                String[] var12 = codeList;
                int var13 = codeList.length;

                for(int var14 = 0; var14 < var13; ++var14) {
                    String code = var12[var14];
                    array.add(code);
                }

                jsonObject.add(strKey, array);
            }

            String strList = jsonObject.toString();
            MKAppUtils.mkeyResultCallBack(callback, "0", "证书列表获取成功", strList);
        } else {
            MKAppUtils.mkeyResultCallBack(callback, "104", "证书获取失败");
        }
    }

    public void certGet(MKeyApiCallback callback) {
        String userID = MKAppManager.getInstance().getUserInfo().getIdNo() + MKAppManager.getInstance().getContCode();
        String strCert = KSCertificate.getInstance(MKAppManager.getInstance().getContext()).getCert(userID);
        if (strCert != null && strCert.length() != 0) {
            MKAppUtils.mkeyResultCallBack(callback, "0", "证书获取成功", strCert);
        } else {
            MKAppUtils.mkeyResultCallBack(callback, "104", "证书获取失败");
        }

    }

    public void certInfoGet(MKeyApiCallback callback) {
        String userID = MKAppManager.getInstance().getUserInfo().getIdNo() + MKAppManager.getInstance().getContCode();
        KSCertInfo certInfo = KSCertificate.getInstance(MKAppManager.getInstance().getContext()).getCertInfo(userID);
        if (certInfo != null) {
            String strCertInfo = MKJsonUtil.toJson(certInfo);
            MKAppUtils.mkeyResultCallBack(callback, "0", "证书信息获取成功", strCertInfo);
        } else {
            MKAppUtils.mkeyResultCallBack(callback, "104", "证书信息获取失败");
        }

    }

    public void certOidInfoGet(String strOid, MKeyApiCallback callback) {
        String userID = MKAppManager.getInstance().getUserInfo().getIdNo() + MKAppManager.getInstance().getContCode();
        String strOidValue = KSCertificate.getInstance(MKAppManager.getInstance().getContext()).getCertInfoByOid(userID, strOid);
        if (strOidValue != null && strOidValue.length() != 0) {
            MKAppUtils.mkeyResultCallBack(callback, "0", "证书OID信息获取成功", strOidValue);
        } else {
            MKAppUtils.mkeyResultCallBack(callback, "106", "证书OID信息获取失败");
        }

    }

    public void certRevoke(String pin, MKeyApiCallback callback) {
        String userID = MKAppManager.getInstance().getUserInfo().getIdNo() + MKAppManager.getInstance().getContCode();
        KSCertInfo certInfo = KSCertificate.getInstance(MKAppManager.getInstance().getContext()).getCertInfo(userID);
        if (certInfo != null && certInfo.getCertSn() != null && certInfo.getCertSn().length() != 0) {
            int iRet = KSCertificate.getInstance(MKAppManager.getInstance().getContext()).verifyPin(userID, pin);
            if (iRet == 0) {
                this.certRevoke(certInfo.getCertSn(), "3", callback);
            } else if (iRet == 8720) {
                MKAppUtils.mkeyResultCallBack(callback, "210", "证书密码锁死");
                MKNetUtils.postVerifyPinLog(MKAppManager.getInstance().getContext(), userID, pin, "证书密码锁死 ret=" + MKUtils.numberToHexStr(iRet));
            } else if (iRet > 8704 && iRet < 8720) {
                String strMsg = "证书密码验证错误，剩余输入" + (iRet - 8704) + "次";
                String errCode = "" + (Integer.valueOf("200") + (iRet - 8704));
                MKAppUtils.mkeyResultCallBack(callback, errCode, strMsg);
                MKNetUtils.postVerifyPinLog(MKAppManager.getInstance().getContext(), userID, pin, "证书密码验证错误 ret=" + MKUtils.numberToHexStr(iRet));
            } else {
                MKAppUtils.mkeyResultCallBack(callback, "213", "证书密码验证异常");
                MKNetUtils.postVerifyPinLog(MKAppManager.getInstance().getContext(), userID, pin, "证书密码验证异常 ret=" + MKUtils.numberToHexStr(iRet));
            }

        } else {
            MKAppUtils.mkeyResultCallBack(callback, "104", "获取证书信息失败");
        }
    }

    private void certRevoke(String certSn, String status, final MKeyApiCallback callback) {
        try {
            ((PostFormBuilder)((PostFormBuilder)OkHttpUtils.post().url(MKAppManager.getInstance().getUrl() + "/cert/revoke")).addHeader("token", MKAppManager.getInstance().getUserToken())).addParams("certSn", certSn).addParams("status", status).build().execute(new StringCallback() {
                public void onError(Call call, Exception e, int id) {
                    MKAppUtils.mkeyResultCallBack(callback, "10", e.getLocalizedMessage());
                }

                public void onResponse(String response, int id) {
                    try {
                        response = URLDecoder.decode(response, "UTF-8");
                        MKApplyCertBean bean = (MKApplyCertBean)MKJsonUtil.toObject(response, MKApplyCertBean.class);
                        if (bean != null && bean.getRet() == 0) {
                            String userID = MKAppManager.getInstance().getUserInfo().getIdNo() + MKAppManager.getInstance().getContCode();
                            KSCertificate.getInstance(MKAppManager.getInstance().getContext()).deleteCert(userID);
                            MKAppUtils.mkeyResultCallBack(callback, "0", "证书注销成功");
                        } else {
                            MKAppUtils.mkeyResultCallBack(callback, "100", bean.getMsg());
                        }
                    } catch (Exception var5) {
                        MKAppUtils.mkeyResultCallBack(callback, "12", var5.getLocalizedMessage());
                    }

                }
            });
        } catch (Exception var5) {
            MKAppUtils.mkeyResultCallBack(callback, "12", var5.getLocalizedMessage());
        }

    }

    public void certDelete(MKeyApiCallback callback) {
        String userID = MKAppManager.getInstance().getUserInfo().getIdNo() + MKAppManager.getInstance().getContCode();
        Boolean bRet = KSCertificate.getInstance(MKAppManager.getInstance().getContext()).deleteCert(userID);
        if (bRet) {
            MKAppUtils.mkeyResultCallBack(callback, "0", "证书删除成功");
        } else {
            MKAppUtils.mkeyResultCallBack(callback, "104", "证书获取失败");
        }

    }

    public void pinVerify(String pin, MKeyApiCallback callback) {
        String userID = MKAppManager.getInstance().getUserInfo().getIdNo() + MKAppManager.getInstance().getContCode();
        String strCert = KSCertificate.getInstance(MKAppManager.getInstance().getContext()).getCert(userID);
        if (strCert != null && strCert.length() != 0) {
            int iRet = KSCertificate.getInstance(MKAppManager.getInstance().getContext()).verifyPin(userID, pin);
            if (iRet == 0) {
                MKAppUtils.mkeyResultCallBack(callback, "0", "证书密码正确");
            } else if (iRet == 8720) {
                MKAppUtils.mkeyResultCallBack(callback, "210", "证书密码锁死");
                MKNetUtils.postVerifyPinLog(MKAppManager.getInstance().getContext(), userID, pin, "证书密码锁死 ret=" + MKUtils.numberToHexStr(iRet));
            } else if (iRet > 8704 && iRet < 8720) {
                String strMsg = "证书密码验证错误，剩余输入" + (iRet - 8704) + "次";
                String errCode = "" + (Integer.valueOf("200") + (iRet - 8704));
                MKAppUtils.mkeyResultCallBack(callback, errCode, strMsg);
                MKNetUtils.postVerifyPinLog(MKAppManager.getInstance().getContext(), userID, pin, "证书密码验证错误 ret=" + MKUtils.numberToHexStr(iRet));
            } else {
                MKAppUtils.mkeyResultCallBack(callback, "213", "证书密码验证异常");
                MKNetUtils.postVerifyPinLog(MKAppManager.getInstance().getContext(), userID, pin, "证书密码验证异常 ret=" + MKUtils.numberToHexStr(iRet));
            }

        } else {
            MKAppUtils.mkeyResultCallBack(callback, "104", "请先申请证书");
        }
    }

    public void pinChange(String oldPin, String newPin, MKeyApiCallback callback) {
        String userID = MKAppManager.getInstance().getUserInfo().getIdNo() + MKAppManager.getInstance().getContCode();
        String strCert = KSCertificate.getInstance(MKAppManager.getInstance().getContext()).getCert(userID);
        if (strCert != null && strCert.length() != 0) {
            int iRet = KSCertificate.getInstance(MKAppManager.getInstance().getContext()).modifyPin(userID, oldPin, newPin);
            if (iRet == 0) {
                MKAppUtils.mkeyResultCallBack(callback, "0", "修改证书密码成功");
            } else if (iRet == 8720) {
                MKAppUtils.mkeyResultCallBack(callback, "210", "证书密码锁死");
            } else if (iRet > 8704 && iRet < 8720) {
                String strMsg = "证书密码错误，剩余输入" + (iRet - 8704) + "次";
                String errCode = "" + (Integer.valueOf("200") + (iRet - 8704));
                MKAppUtils.mkeyResultCallBack(callback, errCode, strMsg);
            } else {
                MKAppUtils.mkeyResultCallBack(callback, "213", "证书密码错误");
            }

        } else {
            MKAppUtils.mkeyResultCallBack(callback, "104", "请先申请证书");
        }
    }

    public void pinUnlock(String adminPin, String newPin, MKeyApiCallback callback) {
        String userID = MKAppManager.getInstance().getUserInfo().getIdNo() + MKAppManager.getInstance().getContCode();
        int iRet = KSCertificate.getInstance(MKAppManager.getInstance().getContext()).unlockPin(userID, adminPin, newPin);
        if (iRet == 0) {
            MKAppUtils.mkeyResultCallBack(callback, "0", "设置密码成功");
        } else {
            MKAppUtils.mkeyResultCallBack(callback, "211", "设置密码失败");
        }

    }

    public void freeSignGetStatus(final MKeyApiCallback callback) {
        String userID = MKAppManager.getInstance().getUserInfo().getIdNo() + MKAppManager.getInstance().getContCode();
        KSCertInfo certInfo = KSCertificate.getInstance(MKAppManager.getInstance().getContext()).getCertInfo(userID);
        if (certInfo == null) {
            MKAppUtils.mkeyResultCallBack(callback, "104", "请先申请证书");
        } else {
            ((PostFormBuilder)((PostFormBuilder)OkHttpUtils.post().url(MKAppManager.getInstance().getUrl() + "/trust/query")).addHeader("token", MKAppManager.getInstance().getUserToken())).addParams("certSn", certInfo.getCertSn()).build().execute(new StringCallback() {
                public void onError(Call call, Exception e, int id) {
                    MKAppUtils.mkeyResultCallBack(callback, "10", e.getLocalizedMessage());
                }

                public void onResponse(String response, int id) {
                    try {
                        response = URLDecoder.decode(response, "UTF-8");
                        MKTrustQueryBean bean = (MKTrustQueryBean)MKJsonUtil.toObject(response, MKTrustQueryBean.class);
                        if (bean != null && bean.getRet() == 0) {
                            if (bean.getData() != null && bean.getData().getTrust() != null && bean.getData().getTrust() == 3) {
                                MKAppUtils.mkeyResultCallBack(callback, "0", "免密状态获取成功", "1");
                            } else {
                                MKAppUtils.mkeyResultCallBack(callback, "0", "免密状态获取成功", "0");
                            }
                        } else {
                            MKAppUtils.mkeyResultCallBack(callback, "401", bean.getMsg());
                        }
                    } catch (Exception var4) {
                        MKAppUtils.mkeyResultCallBack(callback, "12", var4.getLocalizedMessage());
                    }

                }
            });
        }
    }

    public void freeSignSet(String pin, MKeyApiCallback callback) {
        String userID = MKAppManager.getInstance().getUserInfo().getIdNo() + MKAppManager.getInstance().getContCode();
        int iRet = KSCertificate.getInstance(MKAppManager.getInstance().getContext()).verifyPin(userID, pin);
        if (iRet == 0) {
            this.freeSignSetRequest(pin, callback);
        } else if (iRet == 8720) {
            MKAppUtils.mkeyResultCallBack(callback, "210", "证书密码锁死");
            MKNetUtils.postVerifyPinLog(MKAppManager.getInstance().getContext(), userID, pin, "证书密码锁死 ret=" + MKUtils.numberToHexStr(iRet));
        } else if (iRet > 8704 && iRet < 8720) {
            String strMsg = "证书密码验证错误，剩余输入" + (iRet - 8704) + "次";
            String errCode = "" + (Integer.valueOf("200") + (iRet - 8704));
            MKAppUtils.mkeyResultCallBack(callback, errCode, strMsg);
            MKNetUtils.postVerifyPinLog(MKAppManager.getInstance().getContext(), userID, pin, "证书密码验证错误 ret=" + MKUtils.numberToHexStr(iRet));
        } else {
            MKAppUtils.mkeyResultCallBack(callback, "213", "证书密码验证异常");
            MKNetUtils.postVerifyPinLog(MKAppManager.getInstance().getContext(), userID, pin, "证书密码验证异常 ret=" + MKUtils.numberToHexStr(iRet));
        }

    }

    public void freeSignSetRequest(String pin, final MKeyApiCallback callback) {
        try {
            Context context = MKAppManager.getInstance().getContext();
            String userID = MKAppManager.getInstance().getUserInfo().getIdNo() + MKAppManager.getInstance().getContCode();
            KSCertInfo certInfo = KSCertificate.getInstance(context).getCertInfo(userID);
            if (certInfo == null) {
                MKAppUtils.mkeyResultCallBack(callback, "104", "请先申请证书");
                return;
            }

            String keyId = KSCertificate.getInstance(context).getKeyId(userID, pin);
            String strKey = KSCertificate.getInstance(context).getKey(userID, pin);
            ((PostFormBuilder)((PostFormBuilder)OkHttpUtils.post().url(MKAppManager.getInstance().getUrl() + "/trust/config")).addHeader("token", MKAppManager.getInstance().getUserToken())).addParams("certSn", certInfo.getCertSn()).addParams("keyId", URLEncoder.encode(keyId, "UTF-8")).addParams("key", URLEncoder.encode(strKey, "UTF-8")).addParams("alg", "SM2").build().execute(new StringCallback() {
                public void onError(Call call, Exception e, int id) {
                    MKAppUtils.mkeyResultCallBack(callback, "10", e.getLocalizedMessage());
                }

                public void onResponse(String response, int id) {
                    try {
                        response = URLDecoder.decode(response, "UTF-8");
                        MKBaseBean bean = (MKBaseBean)MKJsonUtil.toObject(response, MKBaseBean.class);
                        if (bean != null && bean.getRet() == 0) {
                            MKAppUtils.mkeyResultCallBack(callback, "0", "免密设置成功");
                        } else {
                            MKAppUtils.mkeyResultCallBack(callback, "400", bean.getMsg());
                        }
                    } catch (Exception var4) {
                        MKAppUtils.mkeyResultCallBack(callback, "12", var4.getLocalizedMessage());
                    }

                }
            });
        } catch (Exception var8) {
            MKAppUtils.mkeyResultCallBack(callback, "12", var8.getLocalizedMessage());
        }

    }

    public void freeSignCancel(String pin, MKeyApiCallback callback) {
        String userID = MKAppManager.getInstance().getUserInfo().getIdNo() + MKAppManager.getInstance().getContCode();
        int iRet = KSCertificate.getInstance(MKAppManager.getInstance().getContext()).verifyPin(userID, pin);
        if (iRet == 0) {
            this.freeSignCancelRequest(pin, callback);
        } else if (iRet == 8720) {
            MKAppUtils.mkeyResultCallBack(callback, "210", "证书密码锁死");
            MKNetUtils.postVerifyPinLog(MKAppManager.getInstance().getContext(), userID, pin, "证书密码锁死 ret=" + MKUtils.numberToHexStr(iRet));
        } else if (iRet > 8704 && iRet < 8720) {
            String strMsg = "证书密码验证错误，剩余输入" + (iRet - 8704) + "次";
            String errCode = "" + (Integer.valueOf("200") + (iRet - 8704));
            MKAppUtils.mkeyResultCallBack(callback, errCode, strMsg);
            MKNetUtils.postVerifyPinLog(MKAppManager.getInstance().getContext(), userID, pin, "证书密码验证错误 ret=" + MKUtils.numberToHexStr(iRet));
        } else {
            MKAppUtils.mkeyResultCallBack(callback, "213", "证书密码验证异常");
            MKNetUtils.postVerifyPinLog(MKAppManager.getInstance().getContext(), userID, pin, "证书密码验证异常 ret=" + MKUtils.numberToHexStr(iRet));
        }

    }

    public void freeSignCancelRequest(String pin, final MKeyApiCallback callback) {
        try {
            Context context = MKAppManager.getInstance().getContext();
            String userID = MKAppManager.getInstance().getUserInfo().getIdNo() + MKAppManager.getInstance().getContCode();
            KSCertInfo certInfo = KSCertificate.getInstance(context).getCertInfo(userID);
            if (certInfo == null) {
                MKAppUtils.mkeyResultCallBack(callback, "104", "请先申请证书");
                return;
            }

            String keyId = KSCertificate.getInstance(context).getKeyId(userID, pin);
            ((PostFormBuilder)((PostFormBuilder)OkHttpUtils.post().url(MKAppManager.getInstance().getUrl() + "/trust/clean")).addHeader("token", MKAppManager.getInstance().getUserToken())).addParams("certSn", certInfo.getCertSn()).addParams("keyId", URLEncoder.encode(keyId, "UTF-8")).build().execute(new StringCallback() {
                public void onError(Call call, Exception e, int id) {
                    MKAppUtils.mkeyResultCallBack(callback, "10", e.getLocalizedMessage());
                }

                public void onResponse(String response, int id) {
                    try {
                        response = URLDecoder.decode(response, "UTF-8");
                        MKBaseBean bean = (MKBaseBean)MKJsonUtil.toObject(response, MKBaseBean.class);
                        if (bean != null && bean.getRet() == 0) {
                            MKAppUtils.mkeyResultCallBack(callback, "0", "免密取消成功");
                        } else {
                            MKAppUtils.mkeyResultCallBack(callback, "400", bean.getMsg());
                        }
                    } catch (Exception var4) {
                        MKAppUtils.mkeyResultCallBack(callback, "12", var4.getLocalizedMessage());
                    }

                }
            });
        } catch (Exception var7) {
            MKAppUtils.mkeyResultCallBack(callback, "12", var7.getLocalizedMessage());
        }

    }

    public int getLastErrorCode() {
        return KSCertificate.getInstance(MKAppManager.getInstance().getContext()).getLastErrorCode();
    }
}
