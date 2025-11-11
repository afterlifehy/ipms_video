//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.custle.ksmkey.util;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.custle.ksmkey.common.MKAppManager;
import java.net.URLDecoder;
import java.net.URLEncoder;
import org.json.JSONArray;
import org.json.JSONObject;

public class MKNetUtils {
    public static final int ERR_RES_NULL = 100000;
    public static final int ERR_NET = 100010;
    public static final int ERR_EXT = 100020;
    public static final String MSG_RES_NULL = "响应数据为空";

    public MKNetUtils() {
    }

    public static void BaseCallBackResult(final BaseCallBack callBack, final int ret, final String msg) {
        (new Handler(Looper.getMainLooper())).post(new Runnable() {
            public void run() {
                if (callBack != null) {
                    callBack.onResult(ret, msg);
                }

            }
        });
    }

    public static void BaseValueCallBackResult(final BaseValueCallBack callBack, final int ret, final String msg, final Object object) {
        (new Handler(Looper.getMainLooper())).post(new Runnable() {
            public void run() {
                if (callBack != null) {
                    callBack.onResult(ret, msg, object);
                }

            }
        });
    }

    public static void CertResolveCallBackResult(final CertResolveCallBack callBack, final int ret, final String msg, final String equipmentCode, final String deviceId, final String deptName) {
        (new Handler(Looper.getMainLooper())).post(new Runnable() {
            public void run() {
                if (callBack != null) {
                    callBack.onResult(ret, msg, equipmentCode, deviceId, deptName);
                }

            }
        });
    }

    public static void MK_UserAuth(final Context context, final String deviceCode, final String code, final BaseCallBack callBack) {
        (new Thread(new Runnable() {
            public void run() {
                try {
                    String postParam = "deviceId=" + MKUtils.getDeviceUuid();
                    postParam = postParam + "&equipmentCode=" + deviceCode;
                    postParam = postParam + "&code=" + URLEncoder.encode(code, "UTF-8");
                    postParam = postParam + "&appId=" + MKAppManager.getInstance().getAppId();
                    postParam = postParam + "&packageName=" + MKUtils.getPackageName(context);
                    postParam = postParam + "&clientType=1";
                    String strUrl = MKAppManager.getInstance().getUrl() + "/authorize/user";
                    String strResponse = MKHttpUtil.httpPost(strUrl, postParam);
                    JSONObject resObject = new JSONObject(strResponse);
                    int httpStatus = resObject.getInt("status");
                    String strRes = resObject.getString("response");
                    if (httpStatus == 200) {
                        strRes = URLDecoder.decode(strRes, "UTF-8");
                        if (!strRes.isEmpty()) {
                            JSONObject object = new JSONObject(strRes);
                            int ret = object.getInt("ret");
                            String msg = object.getString("msg");
                            if (ret == 0 && object.has("data")) {
                                JSONObject dataObject = object.getJSONObject("data");
                                String strToken = dataObject.getString("token");
                                MKAppManager.getInstance().setUserToken(strToken);
                            }

                            MKNetUtils.BaseCallBackResult(callBack, ret, msg);
                        } else {
                            MKNetUtils.BaseCallBackResult(callBack, 100000, "响应数据为空");
                        }
                    } else {
                        MKNetUtils.BaseCallBackResult(callBack, 100010, strRes);
                    }
                } catch (Exception var12) {
                    MKNetUtils.BaseCallBackResult(callBack, 100020, var12.getLocalizedMessage());
                }

            }
        })).start();
    }

    public static void MK_CertResolve(final String certSn, final CertResolveCallBack callBack) {
        (new Thread(new Runnable() {
            public void run() {
                try {
                    String postParam = "certSn=" + certSn;
                    String strUrl = MKAppManager.getInstance().getUrl() + "/cert/resolve";
                    String strResponse = MKHttpUtil.httpPost(strUrl, postParam);
                    JSONObject resObject = new JSONObject(strResponse);
                    int httpStatus = resObject.getInt("status");
                    String strRes = resObject.getString("response");
                    if (httpStatus == 200) {
                        strRes = URLDecoder.decode(strRes, "UTF-8");
                        if (!strRes.isEmpty()) {
                            JSONObject object = new JSONObject(strRes);
                            int ret = object.getInt("ret");
                            String msg = object.getString("msg");
                            if (ret == 0) {
                                JSONObject dataObject = object.getJSONObject("data");
                                String equipmentCode = dataObject.getString("equipmentCode");
                                String deviceId = dataObject.getString("deviceId");
                                String deptName = dataObject.getString("deptName");
                                MKNetUtils.CertResolveCallBackResult(callBack, ret, msg, equipmentCode, deviceId, deptName);
                            } else {
                                MKNetUtils.CertResolveCallBackResult(callBack, ret, msg, "", "", "");
                            }
                        } else {
                            MKNetUtils.CertResolveCallBackResult(callBack, 100000, "响应数据为空", "", "", "");
                        }
                    } else {
                        MKNetUtils.CertResolveCallBackResult(callBack, 100010, strRes, "", "", "");
                    }
                } catch (Exception var14) {
                    MKNetUtils.CertResolveCallBackResult(callBack, 100020, var14.getLocalizedMessage(), "", "", "");
                }

            }
        })).start();
    }

    public static void MK_GetCertFormKeyId(final String keyId, final BaseValueCallBack callBack) {
        (new Thread(new Runnable() {
            public void run() {
                try {
                    String postParam = "keyId=" + URLEncoder.encode(keyId, "UTF-8");
                    String strUrl = MKAppManager.getInstance().getUrl() + "/querycert/query";
                    String strResponse = MKHttpUtil.httpPost(strUrl, postParam, MKAppManager.getInstance().getUserToken());
                    JSONObject resObject = new JSONObject(strResponse);
                    int httpStatus = resObject.getInt("status");
                    String strRes = resObject.getString("response");
                    if (httpStatus == 200) {
                        strRes = URLDecoder.decode(strRes, "UTF-8");
                        if (!strRes.isEmpty()) {
                            JSONObject object = new JSONObject(strRes);
                            int ret = object.getInt("ret");
                            String msg = object.getString("msg");
                            if (ret == 0) {
                                JSONObject dataObject = object.getJSONObject("data");
                                String strCert = dataObject.getString("cert");
                                MKNetUtils.BaseValueCallBackResult(callBack, ret, msg, strCert);
                            } else {
                                MKNetUtils.BaseValueCallBackResult(callBack, ret, msg, "");
                            }
                        } else {
                            MKNetUtils.BaseValueCallBackResult(callBack, 100000, "响应数据为空", "");
                        }
                    } else {
                        MKNetUtils.BaseValueCallBackResult(callBack, 100010, strRes, "");
                    }
                } catch (Exception var12) {
                    MKNetUtils.BaseValueCallBackResult(callBack, 100020, var12.getLocalizedMessage(), "");
                }

            }
        })).start();
    }

    public static void MK_PostGenKey(final String key, final String keyId, final BaseCallBack callBack) {
        (new Thread(new Runnable() {
            public void run() {
                try {
                    String postParam = "key=" + URLEncoder.encode(key, "UTF-8");
                    postParam = postParam + "&keyId=" + URLEncoder.encode(keyId, "UTF-8");
                    postParam = postParam + "&alg=SM2";
                    postParam = postParam + "&algVersion=2";
                    String strUrl = MKAppManager.getInstance().getUrl() + "/key/gen";
                    String strResponse = MKHttpUtil.httpPost(strUrl, postParam, MKAppManager.getInstance().getUserToken());
                    JSONObject resObject = new JSONObject(strResponse);
                    int httpStatus = resObject.getInt("status");
                    String strRes = resObject.getString("response");
                    if (httpStatus == 200) {
                        strRes = URLDecoder.decode(strRes, "UTF-8");
                        if (!strRes.isEmpty()) {
                            JSONObject object = new JSONObject(strRes);
                            int ret = object.getInt("ret");
                            String msg = object.getString("msg");
                            MKNetUtils.BaseCallBackResult(callBack, ret, msg);
                        } else {
                            MKNetUtils.BaseCallBackResult(callBack, 100000, "响应数据为空");
                        }
                    } else {
                        MKNetUtils.BaseCallBackResult(callBack, 100010, strRes);
                    }
                } catch (Exception var10) {
                    MKNetUtils.BaseCallBackResult(callBack, 100020, var10.getLocalizedMessage());
                }

            }
        })).start();
    }

    public static void MK_ApplyCert(final String envSn, final String keyId, final String p10, final BaseValueCallBack callBack) {
        (new Thread(new Runnable() {
            public void run() {
                try {
                    String postParam = "keyId=" + URLEncoder.encode(keyId, "UTF-8");
                    postParam = postParam + "&p10=" + URLEncoder.encode(p10, "UTF-8");
                    if (envSn != null && envSn.isEmpty()) {
                        postParam = postParam + "&envSn=" + envSn;
                    } else {
                        postParam = postParam + "&envSn=";
                    }

                    String strUrl = MKAppManager.getInstance().getUrl() + "/cert/apply";
                    String strResponse = MKHttpUtil.httpPost(strUrl, postParam, MKAppManager.getInstance().getUserToken(), 120);
                    JSONObject resObject = new JSONObject(strResponse);
                    int httpStatus = resObject.getInt("status");
                    String strRes = resObject.getString("response");
                    if (httpStatus == 200) {
                        strRes = URLDecoder.decode(strRes, "UTF-8");
                        if (!strRes.isEmpty()) {
                            JSONObject object = new JSONObject(strRes);
                            int ret = object.getInt("ret");
                            String msg = object.getString("msg");
                            if (ret == 0) {
                                JSONObject dataObject = object.getJSONObject("data");
                                String strCert = dataObject.getString("cert");
                                MKNetUtils.BaseValueCallBackResult(callBack, ret, msg, strCert);
                            } else {
                                MKNetUtils.BaseValueCallBackResult(callBack, ret, msg, "");
                            }
                        } else {
                            MKNetUtils.BaseValueCallBackResult(callBack, 100000, "响应数据为空", "");
                        }
                    } else {
                        MKNetUtils.BaseValueCallBackResult(callBack, 100010, strRes, "");
                    }
                } catch (Exception var12) {
                    MKNetUtils.BaseValueCallBackResult(callBack, 100020, var12.getLocalizedMessage(), "");
                }

            }
        })).start();
    }

    public static void MK_ReApplyCert(final String envSn, final String certSn, final String keyId, final String p10, final BaseValueCallBack callBack) {
        (new Thread(new Runnable() {
            public void run() {
                try {
                    String postParam = "keyId=" + URLEncoder.encode(keyId, "UTF-8");
                    postParam = postParam + "&p10=" + URLEncoder.encode(p10, "UTF-8");
                    postParam = postParam + "&certSn=" + certSn;
                    if (envSn != null && envSn.isEmpty()) {
                        postParam = postParam + "&envSn=" + envSn;
                    } else {
                        postParam = postParam + "&envSn=";
                    }

                    String strUrl = MKAppManager.getInstance().getUrl() + "/cert/resign";
                    String strResponse = MKHttpUtil.httpPost(strUrl, postParam, MKAppManager.getInstance().getUserToken(), 120);
                    JSONObject resObject = new JSONObject(strResponse);
                    int httpStatus = resObject.getInt("status");
                    String strRes = resObject.getString("response");
                    if (httpStatus == 200) {
                        strRes = URLDecoder.decode(strRes, "UTF-8");
                        if (!strRes.isEmpty()) {
                            JSONObject object = new JSONObject(strRes);
                            int ret = object.getInt("ret");
                            String msg = object.getString("msg");
                            if (ret == 0) {
                                JSONObject dataObject = object.getJSONObject("data");
                                String strCert = dataObject.getString("cert");
                                MKNetUtils.BaseValueCallBackResult(callBack, ret, msg, strCert);
                            } else {
                                MKNetUtils.BaseValueCallBackResult(callBack, ret, msg, "");
                            }
                        } else {
                            MKNetUtils.BaseValueCallBackResult(callBack, 100000, "响应数据为空", "");
                        }
                    } else {
                        MKNetUtils.BaseValueCallBackResult(callBack, 100010, strRes, "");
                    }
                } catch (Exception var12) {
                    MKNetUtils.BaseValueCallBackResult(callBack, 100020, var12.getLocalizedMessage(), "");
                }

            }
        })).start();
    }

    public static void MK_UpdateCert(final String envSn, final String certSn, final String keyId, final String p10, final BaseValueCallBack callBack) {
        (new Thread(new Runnable() {
            public void run() {
                try {
                    String postParam = "certSn=" + certSn;
                    postParam = postParam + "&keyId=" + URLEncoder.encode(keyId, "UTF-8");
                    postParam = postParam + "&p10=" + URLEncoder.encode(p10, "UTF-8");
                    if (envSn != null && envSn.isEmpty()) {
                        postParam = postParam + "&envSn=" + envSn;
                    } else {
                        postParam = postParam + "&envSn=";
                    }

                    postParam = postParam + "&month=12";
                    postParam = postParam + "&client=android";
                    String strUrl = MKAppManager.getInstance().getUrl() + "/cert/update";
                    String strResponse = MKHttpUtil.httpPost(strUrl, postParam, MKAppManager.getInstance().getUserToken(), 120);
                    JSONObject resObject = new JSONObject(strResponse);
                    int httpStatus = resObject.getInt("status");
                    String strRes = resObject.getString("response");
                    if (httpStatus == 200) {
                        strRes = URLDecoder.decode(strRes, "UTF-8");
                        if (!strRes.isEmpty()) {
                            JSONObject object = new JSONObject(strRes);
                            int ret = object.getInt("ret");
                            String msg = object.getString("msg");
                            if (ret == 0) {
                                JSONObject dataObject = object.getJSONObject("data");
                                String strCert = dataObject.getString("cert");
                                MKNetUtils.BaseValueCallBackResult(callBack, ret, msg, strCert);
                            } else {
                                MKNetUtils.BaseValueCallBackResult(callBack, ret, msg, "");
                            }
                        } else {
                            MKNetUtils.BaseValueCallBackResult(callBack, 100000, "响应数据为空", "");
                        }
                    } else {
                        MKNetUtils.BaseValueCallBackResult(callBack, 100010, strRes, "");
                    }
                } catch (Exception var12) {
                    MKNetUtils.BaseValueCallBackResult(callBack, 100020, var12.getLocalizedMessage(), "");
                }

            }
        })).start();
    }

    public static void MK_RevokeCert(final String certSn, final String status, final BaseCallBack callBack) {
        (new Thread(new Runnable() {
            public void run() {
                try {
                    String postParam = "certSn=" + certSn;
                    postParam = postParam + "&status=" + status;
                    String strUrl = MKAppManager.getInstance().getUrl() + "/cert/revoke";
                    String strResponse = MKHttpUtil.httpPost(strUrl, postParam, MKAppManager.getInstance().getUserToken());
                    JSONObject resObject = new JSONObject(strResponse);
                    int httpStatus = resObject.getInt("status");
                    String strRes = resObject.getString("response");
                    if (httpStatus == 200) {
                        strRes = URLDecoder.decode(strRes, "UTF-8");
                        if (!strRes.isEmpty()) {
                            JSONObject object = new JSONObject(strRes);
                            int ret = object.getInt("ret");
                            String msg = object.getString("msg");
                            MKNetUtils.BaseCallBackResult(callBack, ret, msg);
                        } else {
                            MKNetUtils.BaseCallBackResult(callBack, 100000, "响应数据为空");
                        }
                    } else {
                        MKNetUtils.BaseCallBackResult(callBack, 100010, strRes);
                    }
                } catch (Exception var10) {
                    MKNetUtils.BaseCallBackResult(callBack, 100020, var10.getLocalizedMessage());
                }

            }
        })).start();
    }

    public static void MK_KeySign(final String certSn, final String keyId, final String digest, final String signS1, final BaseValueCallBack callBack) {
        (new Thread(new Runnable() {
            public void run() {
                try {
                    String postParam = "strSign=" + URLEncoder.encode(signS1, "UTF-8");
                    postParam = postParam + "&certSn=" + URLEncoder.encode(certSn, "UTF-8");
                    postParam = postParam + "&msg=" + URLEncoder.encode(digest, "UTF-8");
                    postParam = postParam + "&keyId=" + URLEncoder.encode(keyId, "UTF-8");
                    postParam = postParam + "&alg=SM3";
                    postParam = postParam + "&algVersion=2";
                    String strUrl = MKAppManager.getInstance().getUrl() + "/key/sign";
                    String strResponse = MKHttpUtil.httpPost(strUrl, postParam, MKAppManager.getInstance().getUserToken());
                    JSONObject resObject = new JSONObject(strResponse);
                    int httpStatus = resObject.getInt("status");
                    String strRes = resObject.getString("response");
                    if (httpStatus == 200) {
                        strRes = URLDecoder.decode(strRes, "UTF-8");
                        if (!strRes.isEmpty()) {
                            JSONObject object = new JSONObject(strRes);
                            int ret = object.getInt("ret");
                            String msg = object.getString("msg");
                            if (ret == 0) {
                                JSONObject dataObj = object.getJSONObject("data");
                                JSONArray signObj = dataObj.getJSONArray("signValue");
                                String signS2 = signObj.getString(0);
                                MKNetUtils.BaseValueCallBackResult(callBack, ret, msg, signS2);
                            } else {
                                MKNetUtils.BaseValueCallBackResult(callBack, ret, msg, "");
                            }
                        } else {
                            MKNetUtils.BaseValueCallBackResult(callBack, 100000, "响应数据为空", "");
                        }
                    } else {
                        MKNetUtils.BaseValueCallBackResult(callBack, 100010, strRes, "");
                    }
                } catch (Exception var13) {
                    MKNetUtils.BaseValueCallBackResult(callBack, 100020, var13.getLocalizedMessage(), "");
                }

            }
        })).start();
    }

    public static void MK_KeyDecrypt(final String certSn, final String keyId, final String decT1, final BaseValueCallBack callBack) {
        (new Thread(new Runnable() {
            public void run() {
                try {
                    String postParam = "decTone=" + URLEncoder.encode(decT1, "UTF-8");
                    postParam = postParam + "&certSn=" + URLEncoder.encode(certSn, "UTF-8");
                    postParam = postParam + "&keyId=" + URLEncoder.encode(keyId, "UTF-8");
                    postParam = postParam + "&alg=SM3";
                    postParam = postParam + "&algVersion=2";
                    String strUrl = MKAppManager.getInstance().getUrl() + "/sm2/dec";
                    String strResponse = MKHttpUtil.httpPost(strUrl, postParam, MKAppManager.getInstance().getUserToken());
                    JSONObject resObject = new JSONObject(strResponse);
                    int httpStatus = resObject.getInt("status");
                    String strRes = resObject.getString("response");
                    if (httpStatus == 200) {
                        strRes = URLDecoder.decode(strRes, "UTF-8");
                        if (!strRes.isEmpty()) {
                            JSONObject object = new JSONObject(strRes);
                            int ret = object.getInt("ret");
                            String msg = object.getString("msg");
                            if (ret == 0) {
                                JSONObject dataObj = object.getJSONObject("data");
                                String decT2 = dataObj.getString("decStr");
                                MKNetUtils.BaseValueCallBackResult(callBack, ret, msg, decT2);
                            } else {
                                MKNetUtils.BaseValueCallBackResult(callBack, ret, msg, "");
                            }
                        } else {
                            MKNetUtils.BaseValueCallBackResult(callBack, 100000, "响应数据为空", "");
                        }
                    } else {
                        MKNetUtils.BaseValueCallBackResult(callBack, 100010, strRes, "");
                    }
                } catch (Exception var12) {
                    MKNetUtils.BaseValueCallBackResult(callBack, 100020, var12.getLocalizedMessage(), "");
                }

            }
        })).start();
    }

    public interface CertResolveCallBack {
        void onResult(int ret, String msg, String equipmentCode, String deviceId, String deptName);
    }

    public interface BaseValueCallBack {
        void onResult(int ret, String msg, Object object);
    }

    public interface BaseCallBack {
        void onResult(int ret, String msg);
    }
}
