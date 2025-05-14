//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.rt.base.okhttp.request;

import java.util.Map;
import okhttp3.Request;
import okhttp3.RequestBody;

public class GetRequest extends OkHttpRequest {
    public GetRequest(String url, Object tag, Map<String, String> params, Map<String, String> headers, int id) {
        super(url, tag, params, headers, id);
    }

    protected RequestBody buildRequestBody() {
        return null;
    }

    protected Request buildRequest(RequestBody requestBody) {
        return this.builder.get().build();
    }
}
