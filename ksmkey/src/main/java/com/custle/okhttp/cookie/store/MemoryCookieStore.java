//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.custle.okhttp.cookie.store;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import okhttp3.Cookie;
import okhttp3.HttpUrl;

public class MemoryCookieStore implements CookieStore {
    private final HashMap<String, List<Cookie>> allCookies = new HashMap();

    public MemoryCookieStore() {
    }

    public void add(HttpUrl url, List<Cookie> cookies) {
        List<Cookie> oldCookies = (List)this.allCookies.get(url.host());
        if (oldCookies != null) {
            Iterator<Cookie> itNew = cookies.iterator();
            Iterator<Cookie> itOld = oldCookies.iterator();

            while(itNew.hasNext()) {
                String va = ((Cookie)itNew.next()).name();

                while(va != null && itOld.hasNext()) {
                    String v = ((Cookie)itOld.next()).name();
                    if (v != null && va.equals(v)) {
                        itOld.remove();
                    }
                }
            }

            oldCookies.addAll(cookies);
        } else {
            this.allCookies.put(url.host(), cookies);
        }

    }

    public List<Cookie> get(HttpUrl uri) {
        List<Cookie> cookies = (List)this.allCookies.get(uri.host());
        if (cookies == null) {
            cookies = new ArrayList();
            this.allCookies.put(uri.host(), cookies);
        }

        return (List)cookies;
    }

    public boolean removeAll() {
        this.allCookies.clear();
        return true;
    }

    public List<Cookie> getCookies() {
        List<Cookie> cookies = new ArrayList();
        Set<String> httpUrls = this.allCookies.keySet();
        Iterator var3 = httpUrls.iterator();

        while(var3.hasNext()) {
            String url = (String)var3.next();
            cookies.addAll((Collection)this.allCookies.get(url));
        }

        return cookies;
    }

    public boolean remove(HttpUrl uri, Cookie cookie) {
        List<Cookie> cookies = (List)this.allCookies.get(uri.host());
        return cookie != null ? cookies.remove(cookie) : false;
    }
}
