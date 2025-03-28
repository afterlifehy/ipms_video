//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.peakinfo.base.ca.com.custle.okhttp.request;

import com.peakinfo.base.ca.com.custle.okhttp.callback.Callback;
import com.peakinfo.base.ca.com.custle.okhttp.utils.Exceptions;

import java.util.Iterator;
import java.util.Map;
import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.RequestBody;

public abstract class OkHttpRequest {
    protected String url;
    protected Object tag;
    protected Map<String, String> params;
    protected Map<String, String> headers;
    protected int id;
    protected Request.Builder builder = new Request.Builder();

    protected OkHttpRequest(String url, Object tag, Map<String, String> params, Map<String, String> headers, int id) {
        this.url = url;
        this.tag = tag;
        this.params = params;
        this.headers = headers;
        this.id = id;
        if (url == null) {
            Exceptions.illegalArgument("url can not be null.", new Object[0]);
        }

        this.initBuilder();
    }

    private void initBuilder() {
        this.builder.url(this.url).tag(this.tag);
        this.appendHeaders();
    }

    protected abstract RequestBody buildRequestBody();

    protected RequestBody wrapRequestBody(RequestBody requestBody, Callback callback) {
        return requestBody;
    }

    protected abstract Request buildRequest(RequestBody var1);

    public RequestCall build() {
        return new RequestCall(this);
    }

    public Request generateRequest(Callback callback) {
        RequestBody requestBody = this.buildRequestBody();
        RequestBody wrappedRequestBody = this.wrapRequestBody(requestBody, callback);
        Request request = this.buildRequest(wrappedRequestBody);
        return request;
    }

    protected void appendHeaders() {
        Headers.Builder headerBuilder = new Headers.Builder();
        if (this.headers != null && !this.headers.isEmpty()) {
            Iterator var2 = this.headers.keySet().iterator();

            while(var2.hasNext()) {
                String key = (String)var2.next();
                headerBuilder.add(key, (String)this.headers.get(key));
            }

            this.builder.headers(headerBuilder.build());
        }
    }

    public int getId() {
        return this.id;
    }
}
