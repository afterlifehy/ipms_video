//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.custle.okhttp;

import com.custle.okhttp.builder.GetBuilder;
import com.custle.okhttp.builder.HeadBuilder;
import com.custle.okhttp.builder.OtherRequestBuilder;
import com.custle.okhttp.builder.PostFileBuilder;
import com.custle.okhttp.builder.PostFormBuilder;
import com.custle.okhttp.builder.PostStringBuilder;
import com.custle.okhttp.callback.Callback;
import com.custle.okhttp.request.RequestCall;
import com.custle.okhttp.utils.Platform;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.Executor;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public class OkHttpUtils {
    public static final long DEFAULT_MILLISECONDS = 10000L;
    private static volatile OkHttpUtils mInstance;
    private OkHttpClient mOkHttpClient;
    private Platform mPlatform;

    public OkHttpUtils(OkHttpClient okHttpClient) {
        if (okHttpClient == null) {
            this.mOkHttpClient = new OkHttpClient();
        } else {
            this.mOkHttpClient = okHttpClient;
        }

        this.mPlatform = Platform.get();
    }

    public static OkHttpUtils initClient(OkHttpClient okHttpClient) {
        if (mInstance == null) {
            Class var1 = OkHttpUtils.class;
            synchronized(OkHttpUtils.class) {
                if (mInstance == null) {
                    mInstance = new OkHttpUtils(okHttpClient);
                }
            }
        }

        return mInstance;
    }

    public static OkHttpUtils getInstance() {
        return initClient((OkHttpClient)null);
    }

    public Executor getDelivery() {
        return this.mPlatform.defaultCallbackExecutor();
    }

    public OkHttpClient getOkHttpClient() {
        return this.mOkHttpClient;
    }

    public static GetBuilder get() {
        return new GetBuilder();
    }

    public static PostStringBuilder postString() {
        return new PostStringBuilder();
    }

    public static PostFileBuilder postFile() {
        return new PostFileBuilder();
    }

    public static PostFormBuilder post() {
        return new PostFormBuilder();
    }

    public static OtherRequestBuilder put() {
        return new OtherRequestBuilder("PUT");
    }

    public static HeadBuilder head() {
        return new HeadBuilder();
    }

    public static OtherRequestBuilder delete() {
        return new OtherRequestBuilder("DELETE");
    }

    public static OtherRequestBuilder patch() {
        return new OtherRequestBuilder("PATCH");
    }

    public void execute(RequestCall requestCall, final Callback callbackParam) {
        final Callback callback = callbackParam != null ? callbackParam : Callback.CALLBACK_DEFAULT;

        final int id = requestCall.getOkHttpRequest().getId();
        requestCall.getCall().enqueue(new okhttp3.Callback() {
            public void onFailure(Call call, IOException e) {
                OkHttpUtils.this.sendFailResultCallback(call, e, callback, id);
            }

            public void onResponse(Call call, Response response) {
                try {
                    if (call.isCanceled()) {
                        OkHttpUtils.this.sendFailResultCallback(call, new IOException("Canceled!"), callback, id);
                        return;
                    }

                    if (callback.validateReponse(response, id)) {
                        Object o = callback.parseNetworkResponse(response, id);
                        OkHttpUtils.this.sendSuccessResultCallback(o, callback, id);
                        return;
                    }

                    OkHttpUtils.this.sendFailResultCallback(call, new IOException("request failed , reponse's code is : " + response.code()), callback, id);
                } catch (Exception var7) {
                    OkHttpUtils.this.sendFailResultCallback(call, var7, callback, id);
                    return;
                } finally {
                    if (response.body() != null) {
                        response.body().close();
                    }

                }

            }
        });
    }

    public void sendFailResultCallback(final Call call, final Exception e, final Callback callback, final int id) {
        if (callback != null) {
            this.mPlatform.execute(new Runnable() {
                public void run() {
                    callback.onError(call, e, id);
                    callback.onAfter(id);
                }
            });
        }
    }

    public void sendSuccessResultCallback(final Object object, final Callback callback, final int id) {
        if (callback != null) {
            this.mPlatform.execute(new Runnable() {
                public void run() {
                    callback.onResponse(object, id);
                    callback.onAfter(id);
                }
            });
        }
    }

    public void cancelTag(Object tag) {
        Iterator var2 = this.mOkHttpClient.dispatcher().queuedCalls().iterator();

        Call call;
        while(var2.hasNext()) {
            call = (Call)var2.next();
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }

        var2 = this.mOkHttpClient.dispatcher().runningCalls().iterator();

        while(var2.hasNext()) {
            call = (Call)var2.next();
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }

    }

    public static class METHOD {
        public static final String HEAD = "HEAD";
        public static final String DELETE = "DELETE";
        public static final String PUT = "PUT";
        public static final String PATCH = "PATCH";

        public METHOD() {
        }
    }
}
