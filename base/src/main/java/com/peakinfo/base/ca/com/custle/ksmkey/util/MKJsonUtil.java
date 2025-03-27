//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.peakinfo.base.ca.com.custle.ksmkey.util;

import com.google.gson.Gson;
import java.lang.reflect.Type;

public class MKJsonUtil {
    public MKJsonUtil() {
    }

    public static String toJson(Object o) {
        Gson gson = new Gson();
        String str = "";
        if (o != null) {
            str = gson.toJson(o);
        }

        return str;
    }

    public static Object toObject(String jsonStr, Type type) {
        try {
            Gson gson = new Gson();
            Object o = null;
            if (jsonStr != null && !"".equals(jsonStr)) {
                o = gson.fromJson(jsonStr, type);
            }

            return o;
        } catch (Exception var4) {
            return null;
        }
    }
}
