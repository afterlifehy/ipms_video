package com.peakinfo.base.http.interceptor

import com.blankj.utilcode.util.TimeUtils
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.IOException

class LogInterceptor //可以从连几次
    (private val isDebug: Boolean) : Interceptor {
    protected val log: Logger by lazy { LoggerFactory.getLogger(this::class.java) }
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        if (isDebug) {
            log.debug("okhttp3:$request")
        }
        log.info(request.toString())
        val response: Response = chain.proceed(request)
//        if (isDebug) {
        val mediaType = response.body!!.contentType()
        val content = response.body!!.string()
//            if (isDebug) {
//                String[] url = response.request().url().url().toString().split("/");
//                String method = url[url.length - 1];
//                if (method.contains("?")) {
//                    method = method.split("?")[0];
//                }
//                Log.i("keey", "url:" + method);
//                Log.i("method:", "request:" + request.toString() + "==" + "response body:" + content);//输出返回信息
        log.info("request:{}==response body:{}", request, content)
//            }
        log.info(response.toString())
        log.info("{}     {}", request, content)
        return response.newBuilder()
            .body(okhttp3.ResponseBody.create(mediaType, content))
            .build();
//        }
//        return response
    }

    val currentTime: String
        get() = TimeUtils.millis2String(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss")
}
