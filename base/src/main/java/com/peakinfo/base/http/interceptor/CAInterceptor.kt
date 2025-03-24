package com.peakinfo.base.http.interceptor

import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Base64
import androidx.annotation.RequiresApi
import com.blankj.utilcode.util.EncryptUtils
import com.custle.ksmkey.MKeyApi
import com.peakinfo.base.BaseApplication
import com.peakinfo.base.base.mvvm.UrlManager
import com.peakinfo.base.base.mvvm.repository.LoginRepository
import com.peakinfo.base.ext.log
import com.peakinfo.base.util.Constant
import com.peakinfo.base.util.ToastUtil
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.Buffer
import org.json.JSONObject
import java.nio.charset.StandardCharsets

class CAInterceptor : Interceptor {
    private val unitName = "中科国智科技服务（上海）有限公司"
    private val pin = "1234567"

    companion object {
        private val appId = "58"
        val caClient by lazy {
            MKeyApi.initSDK(UrlManager.getCAUrl(), "pos")
            MKeyApi.getInstance(BaseApplication.instance(), appId, "", "100")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        if (!request.url.toString().startsWith(UrlManager.getServerUrl())) {
            return chain.proceed(request)
        }
        val body = request.body
        val buffer = Buffer()
        body?.writeTo(buffer)

        val parameterStr = buffer.readUtf8()
        val requestBody = parameterStr.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        val orgSign = Constant.password + "|" + parameterStr
        val sign = EncryptUtils.encryptMD5ToString(orgSign).lowercase()
        val base64EncodedParams = base64Encode2String(parameterStr)
        if (base64EncodedParams.length < 400) {
//            LogFileUtil.logToFile("${AppUtil.getCurrentTime()}    签名body转base64:  $base64EncodedParams")
        } else {
//            LogFileUtil.logToFile("${AppUtil.getCurrentTime()}    签名body转base64:  ${base64EncodedParams.substring(0, 400)}")
        }

        var time = 1000 * 20
        var flag = true
        var type = "2"//1:申请证书，2：重签 ，3：更新证书
        if (Constant.needCert) {
            caClient.applyCert(pin) {
                log("申请证书结果 ${it.code},${it.data},${it.msg}")
//                LogFileUtil.logToFile("${AppUtil.getCurrentTime()}   申请证书结果 ${it.code},${it.data},${it.msg}")
                if (it.code == "0") {
                    Constant.needCert = false
                    type = "1"
                    caClient.getCertInfo() {
                        log(" 获取证书certSn ${it.code}${it.msg}${it.data} ")
//                        LogFileUtil.logToFile("${AppUtil.getCurrentTime()}    获取证书certSn ${it.code}${it.msg}${it.data} ")
                        if (it.code == "0") {
                            val jsonObject = JSONObject(it.data)
                            val certSn = jsonObject.getString("certSn")
                            Constant.certSn = certSn
                            request = request.newBuilder()
                                .addHeader("Accept", "application/json")
                                .build()
                            val response = runBlocking {
                                val param = HashMap<String, Any>()
                                val jsonObject = com.alibaba.fastjson.JSONObject()
//                                jsonObject["deviceId"] = HttpCommonParam.deviceId
                                jsonObject["certSn"] = certSn
                                jsonObject["type"] = type
                                param["attr"] = jsonObject
                                mLoginRepository.reportUpdateCA(param)
                            }
                            if (response.status== 0) {

                            }
                        } else {
                            Handler(Looper.getMainLooper()).post {
                                ToastUtil.showBottomToast("获取证书（申请）失败", 1)
                            }
                        }
                    }

                    caClient.signature(base64EncodedParams, pin) {
                        val caSign = Constant.code + "|" + it.data
                        log(" 签名 head caSign $caSign")
//                        LogFileUtil.logToFile("${AppUtil.getCurrentTime()}    重签 head caSign $caSign")
                        if (request.method.equals("get", ignoreCase = true)) {
                            request = request.newBuilder().addHeader("ca-sign", caSign).addHeader("sign", sign)
                                .addHeader("Accept", "application/json").get().build()
                        } else {
                            request = request.newBuilder().addHeader("ca-sign", caSign).addHeader("sign", sign)
                                .addHeader("Accept", "application/json").post(requestBody).build()
                        }
                        flag = false
                    }
                } else {
                    Handler(Looper.getMainLooper()).post {
                        ToastUtil.showBottomToast("申请证书失败：${it.code}${it.msg}", 1)
                    }
                }
            }
        } else if (Constant.refreshCert) {
            caClient.updateCert(pin) {
                log("更新证书" + it.code + it.msg)
//                LogFileUtil.logToFile("${AppUtil.getCurrentTime()}    更新证书 " + it.code + it.msg)
                if (it.code == "0") {
                    Constant.refreshCert = false
                    type = "3"
                    caClient.getCertInfo() {
                        log(" 获取证书certSn ${it.code}${it.msg}${it.data} ")
//                        LogFileUtil.logToFile("${AppUtil.getCurrentTime()}    获取证书certSn ${it.code}${it.msg}${it.data} ")
                        if (it.code == "0") {
                            val jsonObject = JSONObject(it.data)
                            val certSn = jsonObject.getString("certSn")
                            Constant.certSn = certSn
                            request = request.newBuilder()
                                .addHeader("Accept", "application/json")
                                .build()
                            val response = runBlocking {
                                val param = HashMap<String, Any>()
                                val jsonObject = com.alibaba.fastjson.JSONObject()
//                                jsonObject["deviceId"] = HttpCommonParam.deviceId
                                jsonObject["certSn"] = certSn
                                jsonObject["type"] = type
                                param["attr"] = jsonObject
                                mLoginRepository.reportUpdateCA(param)
                            }
                            if (response.status == 0) {

                            }
                        } else {
                            Handler(Looper.getMainLooper()).post {
                                ToastUtil.showBottomToast("获取失败（更新）:${it.code}${it.msg}", 1)
                                ToastUtil.showBottomToast("${Constant.certSn}", 0)
                            }
                            log("获取失败（更新）" + it.code + it.msg)
//                            LogFileUtil.logToFile("${AppUtil.getCurrentTime()}    获取失败（更新） " + it.code + it.msg)
                        }
                    }

                    caClient.signature(base64EncodedParams, pin) {
                        val caSign = Constant.code + "|" + it.data
                        log(" 签名 head caSign $caSign")
//                        LogFileUtil.logToFile("${AppUtil.getCurrentTime()}    重签 head caSign $caSign")
                        if (request.method.equals("get", ignoreCase = true)) {
                            request = request.newBuilder().addHeader("ca-sign", caSign).addHeader("sign", sign)
                                .addHeader("Accept", "application/json").get().build()
                        } else {
                            request = request.newBuilder().addHeader("ca-sign", caSign).addHeader("sign", sign)
                                .addHeader("Accept", "application/json").post(requestBody).build()
                        }
                        flag = false
                    }
                } else {
                    Handler(Looper.getMainLooper()).post {
                        ToastUtil.showBottomToast("更新失败:${it.code}${it.msg}", 1)
                    }
                }
            }
        } else {
            caClient.signature(base64EncodedParams, pin) {
                log(" signature" + it.msg + it.code)
//                LogFileUtil.logToFile("${AppUtil.getCurrentTime()}    signature " + it.msg + it.code)
                if (it.code == "4103" || it.code == "1032") {
                    caClient.applyCert(pin) {
                        type = "2"
                        log(" 4103重签（正常） ${it.code}${it.msg}")
//                        LogFileUtil.logToFile("${AppUtil.getCurrentTime()}    4103重签（正常）${it.code}${it.msg}")
                        if (it.code == "0") {
                            caClient.getCertInfo() {
                                log(" 获取证书（正常） ${it.code}${it.msg}${it.data} ")
//                                LogFileUtil.logToFile("${AppUtil.getCurrentTime()}    获取证书（正常）${it.code}${it.msg}${it.data}")
                                if (it.code == "0") {
                                    val jsonObject = JSONObject(it.data)
                                    val certSn = jsonObject.getString("certSn")
                                    Constant.certSn = certSn
                                    request = request.newBuilder()
                                        .addHeader("Accept", "application/json")
                                        .build()
                                    val response = runBlocking {
                                        val param = HashMap<String, Any>()
                                        val jsonObject = com.alibaba.fastjson.JSONObject()
//                                        jsonObject["deviceId"] = HttpCommonParam.deviceId
                                        jsonObject["certSn"] = certSn
                                        jsonObject["type"] = type
                                        param["attr"] = jsonObject
                                        mLoginRepository.reportUpdateCA(param)
                                    }
                                    if (response.status == 0) {

                                    }
                                } else {
                                    Handler(Looper.getMainLooper()).post {
                                        ToastUtil.showBottomToast("获取失败（正常）:${it.code}${it.msg}", 1)
                                        ToastUtil.showBottomToast("${Constant.certSn}", 0)
                                    }
                                }
                            }

                            caClient.signature(base64EncodedParams, pin) {
                                val caSign = Constant.code + "|" + it.data
                                log(" 重签 head caSign $caSign")
//                                LogFileUtil.logToFile("${AppUtil.getCurrentTime()}    重签 head caSign $caSign")
                                if (request.method.equals("get", ignoreCase = true)) {
                                    request = request.newBuilder().addHeader("ca-sign", caSign).addHeader("sign", sign)
                                        .addHeader("Accept", "application/json").get().build()
                                } else {
                                    request = request.newBuilder().addHeader("ca-sign", caSign).addHeader("sign", sign)
                                        .addHeader("Accept", "application/json").post(requestBody).build()
                                }
                                flag = false
                            }
                        } else {
                            Handler(Looper.getMainLooper()).post {
                                ToastUtil.showBottomToast("4103重签（正常）:${it.code}${it.msg}", 0)
                                ToastUtil.showBottomToast("${Constant.certSn}", 0)
                            }
                        }
                    }
                } else if (it.code == "0") {
                    val caSign = Constant.code + "|" + it.data
                    log(" 签名head caSign $caSign")
//                    LogFileUtil.logToFile("${AppUtil.getCurrentTime()}   签名head caSign $caSign")
                    if (request.method.equals("get", ignoreCase = true)) {
                        request = request.newBuilder().addHeader("ca-sign", caSign).addHeader("sign", sign)
                            .addHeader("Accept", "application/json").get().build()
                    } else {
                        request = request.newBuilder().addHeader("ca-sign", caSign).addHeader("sign", sign)
                            .addHeader("Accept", "application/json").post(requestBody).build()
                    }
                    flag = false
                } else {
                    Handler(Looper.getMainLooper()).post {
                        ToastUtil.showBottomToast("签名（正常）:${it.code}${it.msg}", 0)
                        ToastUtil.showBottomToast("${Constant.certSn}", 0)
                    }
                }
            }
        }
        while (flag) {
            Thread.sleep(500)
            time -= 500
            if (time < 0) {
                break
            }
            continue
        }
        log("request $request")
        if (!request.url.toUri().path.endsWith("/photo")) {
//            LogFileUtil.logToFile("${AppUtil.getCurrentTime()}    request $request")
        }
        return chain.proceed(request)
    }

    val mLoginRepository by lazy {
        LoginRepository()
    }


    fun base64Encode2String(string: String): String {
        return Base64.encodeToString(string.toByteArray(StandardCharsets.UTF_8), Base64.NO_WRAP)
    }
}