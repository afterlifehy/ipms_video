//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.custle.okhttp.builder;

import com.custle.okhttp.request.OtherRequest;
import com.custle.okhttp.request.RequestCall;
import okhttp3.RequestBody;

public class OtherRequestBuilder extends OkHttpRequestBuilder<OtherRequestBuilder> {
    private RequestBody requestBody;
    private String method;
    private String content;

    public OtherRequestBuilder(String method) {
        this.method = method;
    }

    public RequestCall build() {
        return (new OtherRequest(this.requestBody, this.content, this.method, this.url, this.tag, this.params, this.headers, this.id)).build();
    }

    public OtherRequestBuilder requestBody(RequestBody requestBody) {
        this.requestBody = requestBody;
        return this;
    }

    public OtherRequestBuilder requestBody(String content) {
        this.content = content;
        return this;
    }
}
