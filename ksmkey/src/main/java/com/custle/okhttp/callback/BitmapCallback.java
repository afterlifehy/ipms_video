//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.custle.okhttp.callback;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import okhttp3.Response;

public abstract class BitmapCallback extends Callback<Bitmap> {
    public BitmapCallback() {
    }

    public Bitmap parseNetworkResponse(Response response, int id) throws Exception {
        return BitmapFactory.decodeStream(response.body().byteStream());
    }
}
