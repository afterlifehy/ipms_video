//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.custle.okhttp.callback;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import okhttp3.Response;

public abstract class GenericsCallback<T> extends Callback<T> {
    IGenericsSerializator mGenericsSerializator;

    public GenericsCallback(IGenericsSerializator serializator) {
        this.mGenericsSerializator = serializator;
    }

    public T parseNetworkResponse(Response response, int id) throws IOException {
        String string = response.body().string();
        Class<T> entityClass = (Class)((ParameterizedType)this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        if (entityClass == String.class) {
            return (T) string;
        } else {
            T bean = this.mGenericsSerializator.transform(string, entityClass);
            return bean;
        }
    }
}
