//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.rt.base.okhttp.builder;

import com.rt.base.okhttp.request.OtherRequest;
import com.rt.base.okhttp.request.RequestCall;
import okhttp3.RequestBody;

public class HeadBuilder extends GetBuilder {
    public HeadBuilder() {
    }

    public RequestCall build() {
        return (new OtherRequest((RequestBody)null, (String)null, "HEAD", this.url, this.tag, this.params, this.headers, this.id)).build();
    }
}
