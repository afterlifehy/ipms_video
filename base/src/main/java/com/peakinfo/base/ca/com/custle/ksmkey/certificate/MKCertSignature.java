//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.peakinfo.base.ca.com.custle.ksmkey.certificate;

import android.content.Context;

import com.custle.certificate.KSCertificate;
import com.peakinfo.base.ca.com.custle.ksmkey.MKeyApiCallback;
import com.peakinfo.base.ca.com.custle.ksmkey.bean.MKSignValueBean;
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
import java.util.List;
import okhttp3.Call;

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
        String userID = MKAppManager.getInstance().getUserInfo().getIdNo() + MKAppManager.getInstance().getContCode();
        String strCert = KSCertificate.getInstance(MKAppManager.getInstance().getContext()).getCert(userID);
        if (strCert != null && strCert.length() != 0) {
            int iRet = KSCertificate.getInstance(MKAppManager.getInstance().getContext()).verifyPin(userID, pin);
            if (iRet == 0) {
                this.signature(userID, signSrc, pin, callback);
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

    private void signature(final String userID, String signSrc, final String pin, final MKeyApiCallback callback) {
        try {
            final Context context = MKAppManager.getInstance().getContext();
            String hash = KSCertificate.getInstance(context).hashForSign(userID, signSrc, pin);
            String keyId = KSCertificate.getInstance(context).getKeyId(userID, pin);
            final long handle = KSCertificate.getInstance(context).sm2PartSignInit(userID, pin);
            String signS1 = KSCertificate.getInstance(context).sm2PartSignS1(userID, handle, pin);
            ((PostFormBuilder)((PostFormBuilder) OkHttpUtils.post().url(MKAppManager.getInstance().getUrl() + "/key/sign")).addHeader("token", MKAppManager.getInstance().getUserToken())).addParams("strSign", URLEncoder.encode(signS1, "UTF-8")).addParams("msg", URLEncoder.encode(hash, "UTF-8")).addParams("alg", "SM3").addParams("keyId", URLEncoder.encode(keyId, "UTF-8")).addParams("algVersion", "2").build().execute(new StringCallback() {
                public void onError(Call call, Exception e, int id) {
                    MKAppUtils.mkeyResultCallBack(callback, "10", e.getLocalizedMessage());
                }

                public void onResponse(String response, int id) {
                    try {
                        response = URLDecoder.decode(response, "UTF-8");
                        MKSignValueBean bean = (MKSignValueBean) MKJsonUtil.toObject(response, MKSignValueBean.class);
                        if (bean != null && bean.getRet() == 0) {
                            List<String> signValueList = bean.getData().getSignValue();
                            String signature = KSCertificate.getInstance(context).sm2PartSignS3(userID, handle, (String)signValueList.get(0), pin);
                            if (signature != null && signature.length() != 0) {
                                MKAppUtils.mkeyResultCallBack(callback, "0", "签名成功", signature);
                            } else {
                                MKAppUtils.mkeyResultCallBack(callback, "300", "签名失败");
                            }
                        } else {
                            MKAppUtils.mkeyResultCallBack(callback, "300", bean.getMsg());
                        }
                    } catch (Exception var6) {
                        MKAppUtils.mkeyResultCallBack(callback, "12", var6.getLocalizedMessage());
                    }

                }
            });
        } catch (Exception var11) {
            MKAppUtils.mkeyResultCallBack(callback, "12", var11.getLocalizedMessage());
        }

    }

    public void verifySignature(String signSrc, String signValue, MKeyApiCallback callback) {
        String userID = MKAppManager.getInstance().getUserInfo().getIdNo() + MKAppManager.getInstance().getContCode();
        String strCert = KSCertificate.getInstance(MKAppManager.getInstance().getContext()).getCert(userID);
        if (strCert != null && strCert.length() != 0) {
            Boolean bRet = KSCertificate.getInstance(MKAppManager.getInstance().getContext()).verifySignature(userID, signSrc, signValue, strCert);
            if (bRet) {
                MKAppUtils.mkeyResultCallBack(callback, "0", "签名验证成功");
            } else {
                MKAppUtils.mkeyResultCallBack(callback, "302", "签名验证失败");
            }

        } else {
            MKAppUtils.mkeyResultCallBack(callback, "104", "请先申请证书");
        }
    }
}
