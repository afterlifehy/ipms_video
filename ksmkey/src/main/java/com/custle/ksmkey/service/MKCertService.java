//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.custle.ksmkey.service;

import com.custle.ksmkey.bean.MKApplyCertResponse;
import com.custle.ksmkey.bean.MKBaseResponse;
import com.custle.ksmkey.bean.MKDecryptValueBean;
import com.custle.ksmkey.bean.MKQueryCertBean;
import com.custle.ksmkey.bean.MKResolveResponse;
import com.custle.ksmkey.bean.MKSignValueBean;
import com.custle.ksmkey.common.MKAppManager;
import com.custle.ksmkey.interfaces.MKBaseCallBack;
import com.custle.ksmkey.interfaces.MKBaseValueCallBack;
import com.custle.ksmkey.util.MKJsonUtil;
import com.custle.ksmkey.util.MKUtils;
import com.custle.okhttp.OkHttpUtils;
import com.custle.okhttp.builder.PostFormBuilder;
import com.custle.okhttp.callback.StringCallback;

import java.net.URLDecoder;
import java.net.URLEncoder;

import okhttp3.Call;

public class MKCertService {
    public MKCertService() {
    }

    public static void certResolve(String certSn, final MKBaseValueCallBack callback) {
        try {
            MKUtils.logDebug("certResolve url: " + MKAppManager.getInstance().getUrl() + "/cert/resolve");
            MKUtils.logDebug("certResolve request: certSn=" + certSn);
            ((PostFormBuilder)OkHttpUtils.post().url(MKAppManager.getInstance().getUrl() + "/cert/resolve")).addParams("certSn", certSn).build().execute(new StringCallback() {
                public void onError(Call call, Exception e, int id) {
                    MKUtils.logDebug("certResolve response: " + e.getLocalizedMessage());
                    if (callback != null) {
                        callback.onResult(10, e.getLocalizedMessage(), (Object)null);
                    }

                }

                public void onResponse(String s, int i) {
                    try {
                        s = URLDecoder.decode(s, "UTF-8");
                        MKUtils.logDebug("certResolve response: " + s);
                        MKResolveResponse response = (MKResolveResponse) MKJsonUtil.toObject(s, MKResolveResponse.class);
                        if (callback != null) {
                            callback.onResult(response.getRet(), response.getMsg(), response.getData());
                        }
                    } catch (Exception var4) {
                        MKUtils.logDebug("certResolve response ext: " + var4.getLocalizedMessage());
                        if (callback != null) {
                            callback.onResult(12, var4.getLocalizedMessage(), (Object)null);
                        }
                    }

                }
            });
        } catch (Exception var3) {
            MKUtils.logDebug("certResolve request ext: " + var3.getLocalizedMessage());
            if (callback != null) {
                callback.onResult(12, var3.getLocalizedMessage(), (Object)null);
            }
        }

    }

    public static void getCertFormKeyId(String keyId, final QueryCertCallBack callBack) {
        try {
            MKUtils.logDebug("getCertFormKeyId url: " + MKAppManager.getInstance().getUrl() + "/querycert/query");
            MKUtils.logDebug("getCertFormKeyId request: &keyId=" + keyId);
            ((PostFormBuilder)((PostFormBuilder)OkHttpUtils.post().url(MKAppManager.getInstance().getUrl() + "/querycert/query")).addHeader("token", MKAppManager.getInstance().getUserToken())).addParams("keyId", URLEncoder.encode(keyId, "UTF-8")).build().execute(new StringCallback() {
                public void onError(Call call, Exception e, int id) {
                    if (callBack != null) {
                        MKUtils.logDebug("getCertFormKeyId response: " + e.getLocalizedMessage());
                        callBack.onResult(100, e.getLocalizedMessage(), "");
                    }

                }

                public void onResponse(String response, int id) {
                    try {
                        response = URLDecoder.decode(response, "UTF-8");
                        MKUtils.logDebug("getCertFormKeyId response: " + response);
                        MKQueryCertBean bean = (MKQueryCertBean) MKJsonUtil.toObject(response, MKQueryCertBean.class);
                        if (callBack != null) {
                            if (bean.getRet() == 0) {
                                callBack.onResult(0, "成功", bean.getData().getCert());
                            } else {
                                callBack.onResult(bean.getRet(), bean.getMsg(), "");
                            }
                        }
                    } catch (Exception var4) {
                        if (callBack != null) {
                            callBack.onResult(101, var4.getLocalizedMessage(), "");
                        }
                    }

                }
            });
        } catch (Exception var3) {
            MKUtils.logDebug("getCertFormKeyId request ext: " + var3.getLocalizedMessage());
            if (callBack != null) {
                callBack.onResult(102, var3.getLocalizedMessage(), "");
            }
        }

    }

    public static void genKeyPairPostServerNet(String key, String keyId, final MKBaseCallBack callback) {
        try {
            MKUtils.logDebug("genKeyPairPostServerNet url: " + MKAppManager.getInstance().getUrl() + "/key/gen");
            MKUtils.logDebug("genKeyPairPostServerNet request: key=" + key + "&keyId=" + keyId);
            ((PostFormBuilder)((PostFormBuilder)OkHttpUtils.post().url(MKAppManager.getInstance().getUrl() + "/key/gen")).addHeader("token", MKAppManager.getInstance().getUserToken())).addParams("key", URLEncoder.encode(key, "UTF-8")).addParams("keyId", URLEncoder.encode(keyId, "UTF-8")).addParams("alg", "SM2").addParams("algVersion", "2").build().execute(new StringCallback() {
                public void onError(Call call, Exception e, int id) {
                    MKUtils.logDebug("genKeyPairPostServerNet response: " + e.getLocalizedMessage());
                    if (callback != null) {
                        callback.onResult(10, e.getLocalizedMessage());
                    }

                }

                public void onResponse(String s, int i) {
                    try {
                        s = URLDecoder.decode(s, "UTF-8");
                        MKUtils.logDebug("genKeyPairPostServerNet response: " + s);
                        MKBaseResponse response = (MKBaseResponse) MKJsonUtil.toObject(s, MKBaseResponse.class);
                        if (callback != null) {
                            callback.onResult(response.getRet(), response.getMsg());
                        }
                    } catch (Exception var4) {
                        MKUtils.logDebug("genKeyPairPostServerNet response ext: " + var4.getLocalizedMessage());
                        if (callback != null) {
                            callback.onResult(12, var4.getLocalizedMessage());
                        }
                    }

                }
            });
        } catch (Exception var4) {
            MKUtils.logDebug("genKeyPairPostServerNet request ext: " + var4.getLocalizedMessage());
            if (callback != null) {
                callback.onResult(12, var4.getLocalizedMessage());
            }
        }

    }

    public static void applyCertRequestCertNet(String envSn, String p10, String keyId, final MKBaseValueCallBack callback) {
        try {
            MKUtils.logDebug("applyCertRequestCertNet url: " + MKAppManager.getInstance().getUrl() + "/cert/apply");
            MKUtils.logDebug("applyCertRequestCertNet request: envSn=" + envSn + "&p10=" + p10 + "&keyId=" + keyId);
            ((PostFormBuilder)((PostFormBuilder)OkHttpUtils.post().url(MKAppManager.getInstance().getUrl() + "/cert/apply")).addHeader("token", MKAppManager.getInstance().getUserToken())).addParams("envSn", envSn == null ? "" : envSn).addParams("p10", URLEncoder.encode(p10, "UTF-8")).addParams("keyId", URLEncoder.encode(keyId, "UTF-8")).build().execute(new StringCallback() {
                public void onError(Call call, Exception e, int id) {
                    MKUtils.logDebug("applyCertRequestCertNet response: " + e.getLocalizedMessage());
                    if (callback != null) {
                        callback.onResult(10, e.getLocalizedMessage(), (Object)null);
                    }

                }

                public void onResponse(String s, int i) {
                    try {
                        s = URLDecoder.decode(s, "UTF-8");
                        MKUtils.logDebug("applyCertRequestCertNet response: " + s);
                        MKApplyCertResponse response = (MKApplyCertResponse) MKJsonUtil.toObject(s, MKApplyCertResponse.class);
                        if (callback != null) {
                            callback.onResult(response.getRet(), response.getMsg(), response.getData());
                        }
                    } catch (Exception var4) {
                        MKUtils.logDebug("applyCertRequestCertNet response ext: " + var4.getLocalizedMessage());
                        if (callback != null) {
                            callback.onResult(12, var4.getLocalizedMessage(), (Object)null);
                        }
                    }

                }
            });
        } catch (Exception var5) {
            MKUtils.logDebug("applyCertRequestCertNet request ext: " + var5.getLocalizedMessage());
            if (callback != null) {
                callback.onResult(12, var5.getLocalizedMessage(), (Object)null);
            }
        }

    }

    public static void reApplyCertRequestCertNet(String envSn, String certSn, String p10, String keyId, final MKBaseValueCallBack callback) {
        try {
            MKUtils.logDebug("reApplyCertRequestCertNet url: " + MKAppManager.getInstance().getUrl() + "/cert/resign");
            MKUtils.logDebug("reApplyCertRequestCertNet request: envSn=" + envSn + "certSn=" + certSn + "&p10=" + p10 + "&keyId=" + keyId);
            ((PostFormBuilder)((PostFormBuilder)OkHttpUtils.post().url(MKAppManager.getInstance().getUrl() + "/cert/resign")).addHeader("token", MKAppManager.getInstance().getUserToken())).addParams("envSn", envSn == null ? "" : envSn).addParams("certSn", certSn).addParams("p10", URLEncoder.encode(p10, "UTF-8")).addParams("keyId", URLEncoder.encode(keyId, "UTF-8")).build().execute(new StringCallback() {
                public void onError(Call call, Exception e, int id) {
                    MKUtils.logDebug("reApplyCertRequestCertNet response: " + e.getLocalizedMessage());
                    if (callback != null) {
                        callback.onResult(10, e.getLocalizedMessage(), (Object)null);
                    }

                }

                public void onResponse(String s, int i) {
                    try {
                        s = URLDecoder.decode(s, "UTF-8");
                        MKUtils.logDebug("reApplyCertRequestCertNet response: " + s);
                        MKApplyCertResponse response = (MKApplyCertResponse) MKJsonUtil.toObject(s, MKApplyCertResponse.class);
                        if (callback != null) {
                            callback.onResult(response.getRet(), response.getMsg(), response.getData());
                        }
                    } catch (Exception var4) {
                        MKUtils.logDebug("reApplyCertRequestCertNet response ext: " + var4.getLocalizedMessage());
                        if (callback != null) {
                            callback.onResult(12, var4.getLocalizedMessage(), (Object)null);
                        }
                    }

                }
            });
        } catch (Exception var6) {
            MKUtils.logDebug("reApplyCertRequestCertNet request ext: " + var6.getLocalizedMessage());
            if (callback != null) {
                callback.onResult(12, var6.getLocalizedMessage(), (Object)null);
            }
        }

    }

    public static void updateCertRequestCertNet(String envSn, String certSn, String p10, String keyId, final MKBaseValueCallBack callback) {
        try {
            MKUtils.logDebug("updateCertRequestCertNet url: " + MKAppManager.getInstance().getUrl() + "/cert/update");
            MKUtils.logDebug("updateCertRequestCertNet request: envSn=" + envSn + "&certSn=" + certSn + "&keyId=" + keyId + "&p10=" + p10);
            ((PostFormBuilder)((PostFormBuilder)OkHttpUtils.post().url(MKAppManager.getInstance().getUrl() + "/cert/update")).addHeader("token", MKAppManager.getInstance().getUserToken())).addParams("envSn", envSn == null ? "" : envSn).addParams("certSn", certSn).addParams("p10", URLEncoder.encode(p10, "UTF-8")).addParams("keyId", URLEncoder.encode(keyId, "UTF-8")).addParams("month", "12").addParams("client", "android").build().execute(new StringCallback() {
                public void onError(Call call, Exception e, int id) {
                    MKUtils.logDebug("updateCertRequestCertNet response: " + e.getLocalizedMessage());
                    if (callback != null) {
                        callback.onResult(10, e.getLocalizedMessage(), (Object)null);
                    }

                }

                public void onResponse(String s, int i) {
                    try {
                        s = URLDecoder.decode(s, "UTF-8");
                        MKUtils.logDebug("updateCertRequestCertNet response: " + s);
                        MKApplyCertResponse response = (MKApplyCertResponse) MKJsonUtil.toObject(s, MKApplyCertResponse.class);
                        if (callback != null) {
                            callback.onResult(response.getRet(), response.getMsg(), response.getData());
                        }
                    } catch (Exception var4) {
                        MKUtils.logDebug("updateCertRequestCertNet response ext: " + var4.getLocalizedMessage());
                        if (callback != null) {
                            callback.onResult(12, var4.getLocalizedMessage(), (Object)null);
                        }
                    }

                }
            });
        } catch (Exception var6) {
            MKUtils.logDebug("updateCertRequestCertNet request ext: " + var6.getLocalizedMessage());
            if (callback != null) {
                callback.onResult(12, var6.getLocalizedMessage(), (Object)null);
            }
        }

    }

    public static void revokeCertNet(String certSn, final MKBaseCallBack callback) {
        try {
            MKUtils.logDebug("revokeCertNet url: " + MKAppManager.getInstance().getUrl() + "/cert/revoke");
            MKUtils.logDebug("revokeCertNet request: certSn=" + certSn + "&status=3");
            ((PostFormBuilder)((PostFormBuilder)OkHttpUtils.post().url(MKAppManager.getInstance().getUrl() + "/cert/revoke")).addHeader("token", MKAppManager.getInstance().getUserToken())).addParams("certSn", certSn).addParams("status", "3").build().execute(new StringCallback() {
                public void onError(Call call, Exception e, int id) {
                    MKUtils.logDebug("revokeCertNet response: " + e.getLocalizedMessage());
                    if (callback != null) {
                        callback.onResult(10, e.getLocalizedMessage());
                    }

                }

                public void onResponse(String s, int i) {
                    try {
                        s = URLDecoder.decode(s, "UTF-8");
                        MKUtils.logDebug("revokeCertNet response: " + s);
                        MKBaseResponse response = (MKBaseResponse) MKJsonUtil.toObject(s, MKBaseResponse.class);
                        if (callback != null) {
                            callback.onResult(response.getRet(), response.getMsg());
                        }
                    } catch (Exception var4) {
                        MKUtils.logDebug("revokeCertNet response ext: " + var4.getLocalizedMessage());
                        if (callback != null) {
                            callback.onResult(12, var4.getLocalizedMessage());
                        }
                    }

                }
            });
        } catch (Exception var3) {
            MKUtils.logDebug("revokeCertNet request ext: " + var3.getLocalizedMessage());
            if (callback != null) {
                callback.onResult(12, var3.getLocalizedMessage());
            }
        }

    }

    public static void serverSignNet(String certSn, String signS1, String data, String keyId, final MKBaseValueCallBack callback) {
        try {
            MKUtils.logDebug("serverSignNet url: " + MKAppManager.getInstance().getUrl() + "/key/sign");
            MKUtils.logDebug("serverSignNet request: certSn=" + certSn);
            MKUtils.logDebug("serverSignNet request: strSign=" + signS1);
            MKUtils.logDebug("serverSignNet request: msg=" + data);
            MKUtils.logDebug("serverSignNet request: keyId=" + keyId);
            MKUtils.logDebug("serverSignNet request: token=" + MKAppManager.getInstance().getUserToken());
            ((PostFormBuilder)((PostFormBuilder)OkHttpUtils.post().url(MKAppManager.getInstance().getUrl() + "/key/sign")).addHeader("token", MKAppManager.getInstance().getUserToken())).addParams("certSn", URLEncoder.encode(certSn, "UTF-8")).addParams("strSign", URLEncoder.encode(signS1, "UTF-8")).addParams("msg", URLEncoder.encode(data, "UTF-8")).addParams("keyId", URLEncoder.encode(keyId, "UTF-8")).addParams("alg", "SM3").addParams("algVersion", "2").build().execute(new StringCallback() {
                public void onError(Call call, Exception e, int id) {
                    MKUtils.logDebug("serverSignNet response: " + e.getLocalizedMessage());
                    if (callback != null) {
                        callback.onResult(10, e.getLocalizedMessage(), "");
                    }

                }

                public void onResponse(String s, int i) {
                    try {
                        s = URLDecoder.decode(s, "UTF-8");
                        MKUtils.logDebug("serverSignNet response: " + s);
                        MKSignValueBean response = (MKSignValueBean) MKJsonUtil.toObject(s, MKSignValueBean.class);
                        if (callback != null) {
                            callback.onResult(response.getRet(), response.getMsg(), response.getData());
                        }
                    } catch (Exception var4) {
                        MKUtils.logDebug("serverSignNet response ext: " + var4.getLocalizedMessage());
                        if (callback != null) {
                            callback.onResult(12, var4.getLocalizedMessage(), "");
                        }
                    }

                }
            });
        } catch (Exception var6) {
            MKUtils.logDebug("serverSignNet request ext: " + var6.getLocalizedMessage());
            if (callback != null) {
                callback.onResult(12, var6.getLocalizedMessage(), "");
            }
        }

    }

    public static void serverDecryptNet(String decT1, String certSn, String keyId, final MKBaseValueCallBack callback) {
        try {
            MKUtils.logDebug("serverDecryptNet url: " + MKAppManager.getInstance().getUrl() + "/sm2/dec");
            MKUtils.logDebug("serverDecryptNet request: decTone=" + decT1);
            MKUtils.logDebug("serverDecryptNet request: certSn=" + certSn);
            MKUtils.logDebug("serverDecryptNet request: keyId=" + keyId);
            ((PostFormBuilder)((PostFormBuilder)OkHttpUtils.post().url(MKAppManager.getInstance().getUrl() + "/sm2/dec")).addHeader("token", MKAppManager.getInstance().getUserToken())).addParams("decTone", URLEncoder.encode(decT1, "UTF-8")).addParams("certSn", URLEncoder.encode(certSn, "UTF-8")).addParams("keyId", URLEncoder.encode(keyId, "UTF-8")).addParams("alg", "SM3").addParams("algVersion", "2").build().execute(new StringCallback() {
                public void onError(Call call, Exception e, int id) {
                    MKUtils.logDebug("serverDecryptNet response: " + e.getLocalizedMessage());
                    if (callback != null) {
                        callback.onResult(10, e.getLocalizedMessage(), "");
                    }

                }

                public void onResponse(String s, int i) {
                    try {
                        s = URLDecoder.decode(s, "UTF-8");
                        MKUtils.logDebug("serverDecryptNet response: " + s);
                        MKDecryptValueBean response = (MKDecryptValueBean) MKJsonUtil.toObject(s, MKDecryptValueBean.class);
                        if (callback != null) {
                            callback.onResult(response.getRet(), response.getMsg(), response.getData());
                        }
                    } catch (Exception var4) {
                        MKUtils.logDebug("serverDecryptNet response ext: " + var4.getLocalizedMessage());
                        if (callback != null) {
                            callback.onResult(12, var4.getLocalizedMessage(), "");
                        }
                    }

                }
            });
        } catch (Exception var5) {
            MKUtils.logDebug("serverDecryptNet request ext: " + var5.getLocalizedMessage());
            if (callback != null) {
                callback.onResult(12, var5.getLocalizedMessage(), "");
            }
        }

    }

    public interface QueryCertCallBack {
        void onResult(int ret, String msg, String cert);
    }
}
