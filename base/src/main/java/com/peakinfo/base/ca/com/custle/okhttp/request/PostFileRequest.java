//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.peakinfo.base.ca.com.custle.okhttp.request;

import com.peakinfo.base.ca.com.custle.okhttp.OkHttpUtils;
import com.peakinfo.base.ca.com.custle.okhttp.callback.Callback;
import com.peakinfo.base.ca.com.custle.okhttp.utils.Exceptions;

import java.io.File;
import java.util.Map;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

public class PostFileRequest extends OkHttpRequest {
    private static MediaType MEDIA_TYPE_STREAM = MediaType.parse("application/octet-stream");
    private File file;
    private MediaType mediaType;

    public PostFileRequest(String url, Object tag, Map<String, String> params, Map<String, String> headers, File file, MediaType mediaType, int id) {
        super(url, tag, params, headers, id);
        this.file = file;
        this.mediaType = mediaType;
        if (this.file == null) {
            Exceptions.illegalArgument("the file can not be null !", new Object[0]);
        }

        if (this.mediaType == null) {
            this.mediaType = MEDIA_TYPE_STREAM;
        }

    }

    protected RequestBody buildRequestBody() {
        return RequestBody.create(this.mediaType, this.file);
    }

    protected RequestBody wrapRequestBody(RequestBody requestBody, final Callback callback) {
        if (callback == null) {
            return requestBody;
        } else {
            CountingRequestBody countingRequestBody = new CountingRequestBody(requestBody, new CountingRequestBody.Listener() {
                public void onRequestProgress(final long bytesWritten, final long contentLength) {
                    OkHttpUtils.getInstance().getDelivery().execute(new Runnable() {
                        public void run() {
                            callback.inProgress((float)bytesWritten * 1.0F / (float)contentLength, contentLength, PostFileRequest.this.id);
                        }
                    });
                }
            });
            return countingRequestBody;
        }
    }

    protected Request buildRequest(RequestBody requestBody) {
        return this.builder.post(requestBody).build();
    }
}
