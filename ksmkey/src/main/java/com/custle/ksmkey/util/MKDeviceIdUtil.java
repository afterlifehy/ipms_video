//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.custle.ksmkey.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.provider.Settings.Secure;
import java.security.MessageDigest;
import java.util.Locale;

public class MKDeviceIdUtil {
    public MKDeviceIdUtil() {
    }

    public static String getDeviceId(Context context) {
        String androidId = getAndroidId(context);
        if (androidId != null && !androidId.isEmpty()) {
            return androidId;
        } else {
            String serial = getSERIAL();
            return serial != null && !serial.isEmpty() ? getSERIAL() : getDeviceUUID();
        }
    }

    @SuppressLint({"HardwareIds"})
    private static String getAndroidId(Context context) {
        try {
            return Secure.getString(context.getContentResolver(), "android_id");
        } catch (Exception var2) {
            return "";
        }
    }

    private static String getSERIAL() {
        try {
            return Build.SERIAL;
        } catch (Exception var1) {
            return "";
        }
    }

    private static String getDeviceUUID() {
        try {
            String dev = "23" + Build.BOARD.length() % 10 + Build.BRAND.length() % 10 + Build.DEVICE.length() % 10 + Build.HARDWARE.length() % 10 + Build.ID.length() % 10 + Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 + Build.SERIAL.length() % 10;
            return dev;
        } catch (Exception var1) {
            var1.printStackTrace();
            return "";
        }
    }

    private static byte[] getHashByString(String data) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
            messageDigest.reset();
            messageDigest.update(data.getBytes("UTF-8"));
            return messageDigest.digest();
        } catch (Exception var2) {
            return "".getBytes();
        }
    }

    private static String bytesToHex(byte[] data) {
        StringBuilder sb = new StringBuilder();

        for(int n = 0; n < data.length; ++n) {
            String stmp = Integer.toHexString(data[n] & 255);
            if (stmp.length() == 1) {
                sb.append("0");
            }

            sb.append(stmp);
        }

        return sb.toString().toUpperCase(Locale.CHINA);
    }
}
