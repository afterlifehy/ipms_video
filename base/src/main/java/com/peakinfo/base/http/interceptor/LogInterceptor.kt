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

        val requestContent = request.toString()
        val printContent = requestContent.replace(Regex("photo=([^&]*)")) { matchResult ->
            val value = matchResult.groupValues[1] // 获取 photo 参数值
            if (value.isEmpty()) {
                "photo=空值"
            } else {
                "photo=${value.take(50)} 总字节数:${value.toByteArray().size}"
            }
        }

        log.info("okhttp3:$printContent")
        val response: Response = chain.proceed(request)
        val content = response.body!!.string()
        val mediaType = response.body!!.contentType()
//        if (isDebug) {
        log.info(response.toString())
        log.info("=============request:{}\n=============response body:{}\n", printContent, content)
//        }
        return response.newBuilder()
            .body(okhttp3.ResponseBody.create(mediaType, content))
            .build();
//        }
//        return response
    }

    val currentTime: String
        get() = TimeUtils.millis2String(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss")
}
