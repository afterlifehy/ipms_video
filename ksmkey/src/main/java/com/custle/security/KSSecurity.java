//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.custle.security;

import android.content.Context;

public class KSSecurity {
    public static final int SO_PIN = 0;
    public static final int USER_PIN = 1;

    public KSSecurity() {
    }

    public static native String getVersion();

    public static native long initialize(String path);

    public static native void release(long handle);

    public static native int setPin(Context ctx, long handle, String cont, String pin, int userType);

    public static native int modifyPin(Context ctx, long handle, String cont, String oldPin, String newPin, int userType);

    public static native int login(Context ctx, long handle, String pin, int userType);

    public static native boolean logout(long handle);

    public static native int makeP10(Context ctx, long handle, String key, String dn, byte[] p10, int[] p10Len, boolean isApply);

    public static native int MakeP10TmpKeyDelete(long handle, boolean isApply);

    public static native int saveCert(Context ctx, long handle, String cont, String key, String signCert, String encCert, String encKey, boolean isApply);

    public static native int getCert(long handle, String cont, int type, byte[] cert, int[] certLen);

    public static native int getCertItem(long handle, String cert, int type, byte[] info, int[] infoLen);

    public static native int getCertInfoByOid(long handle, String cert, String oid, byte[] info, int[] infoLen);

    public static native int hashForSign(long handle, String cont, byte[] inData, int inLen, byte[] digest);

    public static native int getKeyId(long handle, String cont, byte[] keyId);

    public static native int sm2PartSignInit(long handle, String cont, long[] signHandle);

    public static native int sm2PartSignS1(long handle, long signHandle, byte[] signS1);

    public static native int sm2PartSignS3(long handle, long signHandle, byte[] signS2, byte[] signValue, int[] signValueLen);

    public static native int sm2Verify(long handle, byte[] inData, int inLen, String cert, byte[] signValue, int signValueLen);

    public static native int sm2PartEncrypt(long handle, String cont, byte[] encData, int encLen, byte[] decData, int[] decLen);

    public static native int sm2PartDecryptInit(long handle, String cont, long[] decHandle);

    public static native int sm2PartDecryptT1(long handle, long decHandle, byte[] inData, int inLen, byte[] decT1, int[] decT1Len);

    public static native int sm2PartDecryptT2(long handle, byte[] priKey2, byte[] decT1, int decT1Len, byte[] decT2, int[] decT2Len);

    public static native int sm2PartDecryptT3(long handle, long decHandle, byte[] decT2, int decT2Len, byte[] decT3, int[] decT3Len);

    public static native int sm2Encrypt(long handle, String cert, byte[] inData, int inLen, byte[] encData, int[] encLen);

    public static native int sm2Decrypt(long handle, String cont, byte[] encData, int encLen, byte[] decData, int[] decLen);

    public static native int sm3Hash(long handle, byte[] inData, int inLen, byte[] outData);

    public static native int sm4Encrypt(long handle, byte[] inData, int inLen, byte[] key, int keyLen, byte[] outData, int[] outLen);

    public static native int sm4Decrypt(long handle, byte[] inData, int inLen, byte[] key, int keyLen, byte[] outData, int[] outLen);

    static {
        System.loadLibrary("KSSecurity");
    }
}
