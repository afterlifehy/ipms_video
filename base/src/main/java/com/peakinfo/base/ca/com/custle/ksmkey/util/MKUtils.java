//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.peakinfo.base.ca.com.custle.ksmkey.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Base64;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MKUtils {
    public MKUtils() {
    }

    public static String getP10Item(String p10, int type) {
        String result = null;
        byte[] bP10 = Base64.decode(p10, 2);
        if (bP10.length <= 64) {
            return result;
        } else {
            if (type == 1) {
                result = Base64.encodeToString(bP10, 0, 32, 2);
            }

            if (type == 2) {
                result = Base64.encodeToString(bP10, 32, 32, 2);
            }

            if (type == 3) {
                result = Base64.encodeToString(bP10, 64, bP10.length - 64, 2);
            }

            return result;
        }
    }

    public static String getCurrentTime(String format) {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        String currentTime = sdf.format(date);
        return currentTime;
    }

    public static String shaMD5(String strSrc) {
        String strHash = "";

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(strSrc.getBytes());
            byte[] hashData = md.digest();
            strHash = Base64.encodeToString(hashData, 2);
        } catch (NoSuchAlgorithmException var4) {
        }

        return strHash;
    }

    public static String numberToHexStr(int number) {
        return number > 4097 ? "0x" + String.format("%X", number) : String.format("%d", number);
    }

    public static String getPackageName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.packageName;
        } catch (Exception var3) {
            return "";
        }
    }
}
