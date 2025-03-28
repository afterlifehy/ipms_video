//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.peakinfo.base.ca.com.custle.okhttp.builder;

import com.peakinfo.base.ca.com.custle.okhttp.request.PostFileRequest;
import com.peakinfo.base.ca.com.custle.okhttp.request.RequestCall;

import java.io.File;
import okhttp3.MediaType;

public class PostFileBuilder extends OkHttpRequestBuilder<PostFileBuilder> {
    private File file;
    private MediaType mediaType;

    public PostFileBuilder() {
    }

    public OkHttpRequestBuilder file(File file) {
        this.file = file;
        return this;
    }

    public OkHttpRequestBuilder mediaType(MediaType mediaType) {
        this.mediaType = mediaType;
        return this;
    }

    public RequestCall build() {
        return (new PostFileRequest(this.url, this.tag, this.params, this.headers, this.file, this.mediaType, this.id)).build();
    }
}
