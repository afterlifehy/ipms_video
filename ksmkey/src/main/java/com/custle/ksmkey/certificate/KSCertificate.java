//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.custle.ksmkey.certificate;

import android.content.Context;
import com.custle.ksmkey.util.MKDateUtils;
import com.custle.ksmkey.util.MKFileUtils;
import com.custle.security.KSSecurity;
import java.text.ParseException;
import java.util.Date;

public class KSCertificate {
    public static final int X509_CERT_VERSION = 1;
    public static final int X509_CERT_SN = 2;
    public static final int X509_CERT_SIGNALG = 3;
    public static final int X509_CERT_ISSUER_C = 4;
    public static final int X509_CERT_ISSUER_O = 5;
    public static final int X509_CERT_ISSUER_OU = 6;
    public static final int X509_CERT_ISSUER_S = 7;
    public static final int X509_CERT_ISSUER_CN = 8;
    public static final int X509_CERT_ISSUER_L = 9;
    public static final int X509_CERT_ISSUER_E = 10;
    public static final int X509_CERT_NOTBEFORE = 11;
    public static final int X509_CERT_NOTAFTER = 12;
    public static final int X509_CERT_SUBJECT_C = 13;
    public static final int X509_CERT_SUBJECT_O = 14;
    public static final int X509_CERT_SUBJECT_OU = 15;
    public static final int X509_CERT_SUBJECT_S = 16;
    public static final int X509_CERT_SUBJECT_CN = 17;
    public static final int X509_CERT_SUBJECT_L = 18;
    public static final int X509_CERT_SUBJECT_E = 19;
    public static final int X509_CERT_ISSUER_DN = 20;
    public static final int X509_CERT_SUBJECT_DN = 21;
    public static final int X509_CERT_DER_PUBKEY = 22;
    public static final int SO_PIN = 0;
    public static final int USER_PIN = 1;
    private static final String DEFAULT_PATH = "/custlecert/";
    private static final String DEFAULT_CONT = "cont1";
    private String mRootPath;
    private Context mContext;
    private static volatile KSCertificate certificate = null;

    public static KSCertificate getInstance(Context context) {
        if (certificate == null) {
            Class var1 = KSCertificate.class;
            synchronized(KSCertificate.class) {
                if (certificate == null) {
                    certificate = new KSCertificate(context);
                }
            }
        }

        return certificate;
    }

    public KSCertificate(Context context) {
        if (context != null) {
            this.mRootPath = context.getFilesDir().getAbsolutePath() + "/custlecert/";
            this.mContext = context;
        }

    }

    public int getCert(String userId, int type, byte[] cert, int[] certLen) {
        String path = this.mRootPath + userId;
        long handle = KSSecurity.initialize(path);
        int iRet = KSSecurity.getCert(handle, "cont1", type, cert, certLen);
        KSSecurity.release(handle);
        return iRet;
    }

    public KSCertInfo getCertInfo(String cert) {
        KSCertInfo certInfo = null;
        long handle = KSSecurity.initialize("");
        if (cert != null && !cert.isEmpty()) {
            certInfo = new KSCertInfo();
            byte[] info = new byte[128];
            int[] infoLen = new int[2];
            int iRet = KSSecurity.getCertItem(handle, cert, 2, info, infoLen);
            byte[] tmp;
            if (iRet == 0) {
                tmp = new byte[infoLen[0]];
                System.arraycopy(info, 0, tmp, 0, infoLen[0]);
                certInfo.setCertSn(new String(tmp));
            }

            iRet = KSSecurity.getCertItem(handle, cert, 8, info, infoLen);
            if (iRet == 0) {
                tmp = new byte[infoLen[0]];
                System.arraycopy(info, 0, tmp, 0, infoLen[0]);
                certInfo.setIssuerCN(new String(tmp));
            }

            iRet = KSSecurity.getCertItem(handle, cert, 17, info, infoLen);
            if (iRet == 0) {
                tmp = new byte[infoLen[0]];
                System.arraycopy(info, 0, tmp, 0, infoLen[0]);
                certInfo.setSubjectCN(new String(tmp));
            }

            iRet = KSSecurity.getCertItem(handle, cert, 14, info, infoLen);
            if (iRet == 0) {
                tmp = new byte[infoLen[0]];
                System.arraycopy(info, 0, tmp, 0, infoLen[0]);
                certInfo.setSubjectO(new String(tmp));
            }

            iRet = KSSecurity.getCertItem(handle, cert, 15, info, infoLen);
            if (iRet == 0) {
                tmp = new byte[infoLen[0]];
                System.arraycopy(info, 0, tmp, 0, infoLen[0]);
                certInfo.setSubjectOU(new String(tmp));
            }

            iRet = KSSecurity.getCertItem(handle, cert, 11, info, infoLen);
            if (iRet == 0) {
                tmp = new byte[infoLen[0]];
                System.arraycopy(info, 0, tmp, 0, infoLen[0]);
                certInfo.setStartDate(this.convertTime(new String(tmp)));
            }

            iRet = KSSecurity.getCertItem(handle, cert, 12, info, infoLen);
            if (iRet == 0) {
                tmp = new byte[infoLen[0]];
                System.arraycopy(info, 0, tmp, 0, infoLen[0]);
                certInfo.setEndDate(this.convertTime(new String(tmp)));
            }
        }

        KSSecurity.release(handle);
        return certInfo;
    }

    public String getCertInfoByOid(String cert, String oid) {
        String strOidValue = null;
        long handle = KSSecurity.initialize("");
        if (cert != null && !cert.isEmpty()) {
            byte[] info = new byte[128];
            int[] infoLen = new int[2];
            int iRet = KSSecurity.getCertInfoByOid(handle, cert, oid, info, infoLen);
            if (iRet == 0) {
                byte[] tmp = new byte[infoLen[0]];
                System.arraycopy(info, 0, tmp, 0, infoLen[0]);
                strOidValue = new String(tmp);
            }
        }

        KSSecurity.release(handle);
        return strOidValue;
    }

    public int verifyPin(String userId, String pin) {
        String path = this.mRootPath + userId;
        long handle = KSSecurity.initialize(path);
        int iRet = KSSecurity.login(this.mContext, handle, pin, 1);
        KSSecurity.logout(handle);
        KSSecurity.release(handle);
        return iRet;
    }

    public int modifyPin(String userId, String oldPin, String newPin) {
        String path = this.mRootPath + userId;
        long handle = KSSecurity.initialize(path);
        int iRet = KSSecurity.modifyPin(this.mContext, handle, "cont1", oldPin, newPin, 1);
        KSSecurity.logout(handle);
        KSSecurity.release(handle);
        return iRet;
    }

    public int unlockPin(String userId, String adminPin, String newPin) {
        String path = this.mRootPath + userId;
        long handle = KSSecurity.initialize(path);
        int iRet = KSSecurity.login(this.mContext, handle, adminPin, 0);
        if (iRet != 0) {
            KSSecurity.release(handle);
            return iRet;
        } else {
            iRet = KSSecurity.setPin(this.mContext, handle, "cont1", newPin, 1);
            KSSecurity.logout(handle);
            KSSecurity.release(handle);
            return iRet;
        }
    }

    public int makeP10(String userId, String dn, String pin, boolean isApply, byte[] p10, int[] p10Len) {
        String path = this.mRootPath + userId;
        long handle = KSSecurity.initialize(path);
        int iRet = KSSecurity.setPin(this.mContext, handle, "cont1", pin, 1);
        if (iRet != 0) {
            KSSecurity.release(handle);
            return iRet;
        } else {
            iRet = KSSecurity.login(this.mContext, handle, pin, 1);
            if (iRet != 0) {
                KSSecurity.release(handle);
                return iRet;
            } else {
                for(int i = 0; i < 50; ++i) {
                    iRet = KSSecurity.makeP10(this.mContext, handle, pin, dn, p10, p10Len, isApply);
                    if (iRet == 0) {
                        break;
                    }
                }

                KSSecurity.logout(handle);
                KSSecurity.release(handle);
                return iRet;
            }
        }
    }

    public int makeP10TmpKeyDelete(String userId, boolean isApply) {
        String path = this.mRootPath + userId;
        long handle = KSSecurity.initialize(path);
        int iRet = KSSecurity.MakeP10TmpKeyDelete(handle, isApply);
        KSSecurity.release(handle);
        return iRet;
    }

    public int saveCert(String userId, String signCert, String encCert, String encKey, String pin, boolean isApply) {
        String path = this.mRootPath + userId;
        long handle = KSSecurity.initialize(path);
        int iRet = KSSecurity.login(this.mContext, handle, pin, 1);
        if (iRet != 0) {
            KSSecurity.release(handle);
            return iRet;
        } else {
            iRet = KSSecurity.saveCert(this.mContext, handle, "cont1", pin, signCert, encCert, encKey, isApply);
            KSSecurity.logout(handle);
            KSSecurity.release(handle);
            return iRet;
        }
    }

    public boolean deleteCert(String userId) {
        String path = this.mRootPath + userId;
        return MKFileUtils.deleteDirectory(path);
    }

    public int hashForSign(String userId, String pin, byte[] inData, int inLen, byte[] digest) {
        String path = this.mRootPath + userId;
        long handle = KSSecurity.initialize(path);
        int iRet = KSSecurity.login(this.mContext, handle, pin, 1);
        if (iRet != 0) {
            KSSecurity.release(handle);
            return iRet;
        } else {
            iRet = KSSecurity.hashForSign(handle, "cont1", inData, inLen, digest);
            KSSecurity.logout(handle);
            KSSecurity.release(handle);
            return iRet;
        }
    }

    public int getKeyId(String userId, String pin, byte[] keyId) {
        String path = this.mRootPath + userId;
        long handle = KSSecurity.initialize(path);
        int iRet = KSSecurity.login(this.mContext, handle, pin, 1);
        if (iRet != 0) {
            KSSecurity.release(handle);
            return iRet;
        } else {
            iRet = KSSecurity.getKeyId(handle, "cont1", keyId);
            KSSecurity.logout(handle);
            KSSecurity.release(handle);
            return iRet;
        }
    }

    public int sm2PartSignInit(String userId, String pin, long[] signHandle) {
        String path = this.mRootPath + userId;
        long handle = KSSecurity.initialize(path);
        int iRet = KSSecurity.login(this.mContext, handle, pin, 1);
        if (iRet != 0) {
            return iRet;
        } else {
            iRet = KSSecurity.sm2PartSignInit(handle, "cont1", signHandle);
            KSSecurity.logout(handle);
            KSSecurity.release(handle);
            return iRet;
        }
    }

    public int sm2PartSignS1(String userId, long signHandle, String pin, byte[] pbSignS1) {
        String path = this.mRootPath + userId;
        long handle = KSSecurity.initialize(path);
        int iRet = KSSecurity.login(this.mContext, handle, pin, 1);
        if (iRet != 0) {
            return iRet;
        } else {
            iRet = KSSecurity.sm2PartSignS1(handle, signHandle, pbSignS1);
            KSSecurity.logout(handle);
            KSSecurity.release(handle);
            return iRet;
        }
    }

    public int sm2PartSignS3(String userId, long signHandle, byte[] signS2, String pin, byte[] signValue, int[] signValueLen) {
        String path = this.mRootPath + userId;
        long handle = KSSecurity.initialize(path);
        int iRet = KSSecurity.login(this.mContext, handle, pin, 1);
        if (iRet != 0) {
            return iRet;
        } else {
            iRet = KSSecurity.sm2PartSignS3(handle, signHandle, signS2, signValue, signValueLen);
            KSSecurity.logout(handle);
            KSSecurity.release(handle);
            return iRet;
        }
    }

    public int verifySignature(String userId, byte[] inData, int inLen, byte[] signValue, int signValueLen, String cert) {
        String path = this.mRootPath + userId;
        long handle = KSSecurity.initialize(path);
        int iRet = KSSecurity.sm2Verify(handle, inData, inLen, cert, signValue, signValueLen);
        KSSecurity.release(handle);
        return iRet;
    }

    public int sm2Encrypt(String userId, byte[] inData, int inLen, String cert, byte[] encData, int[] encLen) {
        String path = this.mRootPath + userId;
        long handle = KSSecurity.initialize(path);
        int iRet = KSSecurity.sm2Encrypt(handle, cert, inData, inLen, encData, encLen);
        KSSecurity.release(handle);
        return iRet;
    }

    public int sm2Decrypt(String userId, byte[] encData, int encLen, String pin, byte[] decData, int[] decLen) {
        String path = this.mRootPath + userId;
        long handle = KSSecurity.initialize(path);
        int iRet = KSSecurity.login(this.mContext, handle, pin, 1);
        if (iRet != 0) {
            return iRet;
        } else {
            iRet = KSSecurity.sm2Decrypt(handle, "cont1", encData, encLen, decData, decLen);
            KSSecurity.logout(handle);
            KSSecurity.release(handle);
            return iRet;
        }
    }

    public int sm2PartEncrypt(String userId, byte[] inData, int inLen, String cert, byte[] encData, int[] encLen) {
        String path = this.mRootPath + userId;
        long handle = KSSecurity.initialize(path);
        int iRet = KSSecurity.sm2PartEncrypt(handle, cert, inData, inLen, encData, encLen);
        KSSecurity.release(handle);
        return iRet;
    }

    public int sm2PartDecryptInit(String userId, String pin, long[] decHandle) {
        String path = this.mRootPath + userId;
        long handle = KSSecurity.initialize(path);
        int iRet = KSSecurity.login(this.mContext, handle, pin, 1);
        if (iRet != 0) {
            return iRet;
        } else {
            iRet = KSSecurity.sm2PartDecryptInit(handle, "cont1", decHandle);
            KSSecurity.logout(handle);
            KSSecurity.release(handle);
            return iRet;
        }
    }

    public int sm2PartDecryptT1(String userId, long decHandle, byte[] inData, int inLen, byte[] decT1, int[] decT1Len) {
        String path = this.mRootPath + userId;
        long handle = KSSecurity.initialize(path);
        int iRet = KSSecurity.sm2PartDecryptT1(handle, decHandle, inData, inLen, decT1, decT1Len);
        KSSecurity.logout(handle);
        KSSecurity.release(handle);
        return iRet;
    }

    public int sm2PartDecryptT2(String userId, byte[] priKey2, byte[] decT1, int decT1Len, byte[] decT2, int[] decT2Len) {
        String path = this.mRootPath + userId;
        long handle = KSSecurity.initialize(path);
        int iRet = KSSecurity.sm2PartDecryptT2(handle, priKey2, decT1, decT1Len, decT2, decT2Len);
        KSSecurity.logout(handle);
        KSSecurity.release(handle);
        return iRet;
    }

    public int sm2PartDecryptT3(String userId, long decHandle, byte[] decT2, int decT2Len, byte[] decT3, int[] decT3Len) {
        String path = this.mRootPath + userId;
        long handle = KSSecurity.initialize(path);
        int iRet = KSSecurity.sm2PartDecryptT3(handle, decHandle, decT2, decT2Len, decT3, decT3Len);
        KSSecurity.logout(handle);
        KSSecurity.release(handle);
        return iRet;
    }

    public int sm3Hash(byte[] inData, int inLen, byte[] outData) {
        long handle = KSSecurity.initialize("");
        int iRet = KSSecurity.sm3Hash(handle, inData, inLen, outData);
        KSSecurity.release(handle);
        return iRet;
    }

    public int sm4Encrypt(byte[] inData, int inLen, byte[] key, int keyLen, byte[] encData, int[] encLen) {
        long handle = KSSecurity.initialize("");
        int iRet = KSSecurity.sm4Encrypt(handle, inData, inLen, key, keyLen, encData, encLen);
        KSSecurity.release(handle);
        return iRet;
    }

    public int sm4Decrypt(byte[] inData, int inLen, byte[] key, int keyLen, byte[] encData, int[] encLen) {
        long handle = KSSecurity.initialize("");
        int iRet = KSSecurity.sm4Decrypt(handle, inData, inLen, key, keyLen, encData, encLen);
        KSSecurity.release(handle);
        return iRet;
    }

    private String convertTime(String time) {
        try {
            String tt = time.substring(0, time.length() - 1);
            Date date = MKDateUtils.stringToDate(tt, "yyyyMMddHHmmss");
            date = MKDateUtils.addMinute(480, date);
            return MKDateUtils.dateToString(date, "yyyy-MM-dd HH:mm:ss");
        } catch (ParseException var4) {
            return time;
        }
    }
}
