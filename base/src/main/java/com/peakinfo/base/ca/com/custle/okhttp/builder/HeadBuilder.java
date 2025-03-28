//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.peakinfo.base.ca.com.custle.okhttp.builder;

import com.peakinfo.base.ca.com.custle.okhttp.request.OtherRequest;
import com.peakinfo.base.ca.com.custle.okhttp.request.RequestCall;

import okhttp3.RequestBody;

public class HeadBuilder extends GetBuilder {
    public HeadBuilder() {
    }

    public RequestCall build() {
        return (new OtherRequest((RequestBody)null, (String)null, "HEAD", this.url, this.tag, this.params, this.headers, this.id)).build();
    }
}
