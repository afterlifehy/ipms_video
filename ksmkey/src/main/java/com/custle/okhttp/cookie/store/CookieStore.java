//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.custle.okhttp.cookie.store;

import java.util.List;
import okhttp3.Cookie;
import okhttp3.HttpUrl;

public interface CookieStore {
    void add(HttpUrl var1, List<Cookie> var2);

    List<Cookie> get(HttpUrl var1);

    List<Cookie> getCookies();

    boolean remove(HttpUrl var1, Cookie var2);

    boolean removeAll();
}
