//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.rt.base.ksmkey.certificate;

import android.content.Context;
import android.util.Base64;
import com.rt.base.ksmkey.MKeyApiCallback;
import com.rt.base.ksmkey.util.MKAppUtils;

public class MKSecurity {
    private static volatile MKSecurity security = null;

    public static MKSecurity getInstance() {
        if (security == null) {
            Class var0 = MKSecurity.class;
            synchronized(MKSecurity.class) {
                if (security == null) {
                    security = new MKSecurity();
                }
            }
        }

        return security;
    }

    public MKSecurity() {
    }

    public void sm3Hash(byte[] inData, MKeyApiCallback callback) {
        byte[] hashData = new byte[32];
        int iRet = KSCertificate.getInstance((Context)null).sm3Hash(inData, inData.length, hashData);
        if (iRet != 0) {
            MKAppUtils.mkeyResultCallBack(callback, iRet, "SM3杂凑失败");
        } else {
            String strHash = Base64.encodeToString(hashData, 0, 32, 2);
            MKAppUtils.mkeyResultCallBack(callback, 0, "SM3杂凑成功", strHash);
        }
    }

    public void sm4Encrypt(byte[] inData, byte[] key, MKeyApiCallback callback) {
        byte[] outData = new byte[inData.length + 16];
        int[] outLen = new int[2];
        int iRet = KSCertificate.getInstance((Context)null).sm4Encrypt(inData, inData.length, key, key.length, outData, outLen);
        if (iRet != 0) {
            MKAppUtils.mkeyResultCallBack(callback, iRet, "SM4加密失败");
        } else {
            String strHash = Base64.encodeToString(outData, 0, outLen[0], 2);
            MKAppUtils.mkeyResultCallBack(callback, 0, "SM4加密成功", strHash);
        }
    }

    public void sm4Decrypt(byte[] inData, byte[] key, MKeyApiCallback callback) {
        byte[] outData = new byte[inData.length + 16];
        int[] outLen = new int[2];
        int iRet = KSCertificate.getInstance((Context)null).sm4Decrypt(inData, inData.length, key, key.length, outData, outLen);
        if (iRet != 0) {
            MKAppUtils.mkeyResultCallBack(callback, iRet, "SM4解密失败");
        } else {
            String strHash = Base64.encodeToString(outData, 0, outLen[0], 2);
            MKAppUtils.mkeyResultCallBack(callback, 0, "SM4解密成功", strHash);
        }
    }
}
