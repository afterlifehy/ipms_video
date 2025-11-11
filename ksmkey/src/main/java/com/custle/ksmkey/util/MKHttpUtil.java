//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.custle.ksmkey.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.json.JSONObject;

public class MKHttpUtil {
    static X509TrustManager xtm = new X509TrustManager() {
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
        }

        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    };
    static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
        public boolean verify(String s, SSLSession sslSession) {
            return true;
        }
    };

    public MKHttpUtil() {
    }

    public static String httpPost(String httpUrl, String param) throws Exception {
        return httpPost(httpUrl, param, "", 60);
    }

    public static String httpPost(String httpUrl, String param, String token) throws Exception {
        return httpPost(httpUrl, param, token, 60);
    }

    public static String httpPost(String httpUrl, String param, String token, int timeout) throws Exception {
        try {
            logPrint("http url: " + httpUrl);
            logPrint("http request: " + param);
            logPrint("http token: " + token);
            String result = "";
            JSONObject resObject = new JSONObject();
            HttpURLConnection http = null;
            URL url = new URL(httpUrl);
            if (httpUrl.toLowerCase().startsWith("https")) {
                trustAllCertificates();
                http = (HttpsURLConnection)url.openConnection();
                ((HttpsURLConnection)http).setHostnameVerifier(DO_NOT_VERIFY);
            } else {
                http = (HttpURLConnection)url.openConnection();
            }

            ((HttpURLConnection)http).setConnectTimeout(timeout * 1000);
            ((HttpURLConnection)http).setReadTimeout(timeout * 1000);
            ((HttpURLConnection)http).setRequestMethod("POST");
            ((HttpURLConnection)http).setDoInput(true);
            ((HttpURLConnection)http).setDoOutput(true);
            ((HttpURLConnection)http).setUseCaches(false);
            ((HttpURLConnection)http).setRequestProperty("Connection", "keep-alive");
            if (token != null && !token.isEmpty()) {
                ((HttpURLConnection)http).setRequestProperty("token", token);
            }

            ((HttpURLConnection)http).connect();
            DataOutputStream out = new DataOutputStream(((HttpURLConnection)http).getOutputStream());
            out.writeBytes(param);
            out.flush();
            out.close();
            int retCode = ((HttpURLConnection)http).getResponseCode();
            resObject.put("status", retCode);
            BufferedReader in;
            if (retCode == 200) {
                in = new BufferedReader(new InputStreamReader(((HttpURLConnection)http).getInputStream()));
                resObject.put("response", in.readLine());
            } else {
                in = new BufferedReader(new InputStreamReader(((HttpURLConnection)http).getErrorStream()));
                resObject.put("response", in.readLine());
            }

            result = resObject.toString();
            logPrint("http response: : " + result);
            in.close();
            ((HttpURLConnection)http).disconnect();
            return result;
        } catch (Exception var11) {
            logPrint("http Exception: : " + var11.getMessage());
            throw var11;
        }
    }

    public static String HttpGet(String httpUrl, String param, String token, int timeout) throws Exception {
        try {
            logPrint("http url: " + httpUrl);
            logPrint("http request: " + param);
            logPrint("http token: " + token);
            String result = "";
            HttpURLConnection http = null;
            URL url = new URL(httpUrl + "?" + param);
            if (httpUrl.toLowerCase().startsWith("https")) {
                trustAllCertificates();
                http = (HttpsURLConnection)url.openConnection();
                ((HttpsURLConnection)http).setHostnameVerifier(DO_NOT_VERIFY);
            } else {
                http = (HttpURLConnection)url.openConnection();
            }

            ((HttpURLConnection)http).setConnectTimeout(timeout);
            ((HttpURLConnection)http).setReadTimeout(timeout);
            ((HttpURLConnection)http).setRequestMethod("GET");
            ((HttpURLConnection)http).setUseCaches(false);
            ((HttpURLConnection)http).setRequestProperty("Connection", "keep-alive");
            ((HttpURLConnection)http).setRequestProperty("Content-Type", "application/json");
            if (token != null && token.length() != 0) {
                ((HttpURLConnection)http).setRequestProperty("token", token);
            }

            ((HttpURLConnection)http).connect();
            int retCode = ((HttpURLConnection)http).getResponseCode();
            BufferedReader in = null;
            if (retCode == 200) {
                in = new BufferedReader(new InputStreamReader(((HttpURLConnection)http).getInputStream()));
            } else {
                in = new BufferedReader(new InputStreamReader(((HttpURLConnection)http).getErrorStream()));
            }

            result = in.readLine();
            logPrint("http status: : " + retCode);
            logPrint("http response: : " + result);
            in.close();
            ((HttpURLConnection)http).disconnect();
            return result;
        } catch (Exception var9) {
            logPrint("http Exception: : " + var9.getMessage());
            throw var9;
        }
    }

    private static void trustAllCertificates() {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init((KeyManager[])null, new TrustManager[]{xtm}, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(DO_NOT_VERIFY);
        } catch (Exception var1) {
            var1.printStackTrace();
        }

    }

    private static void logPrint(String strLog) {
    }
}
