//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.rt.base.ksmkey.bean;

public class MKApplyCertResponse extends MKBaseResponse {
    private CertInfo data;

    public MKApplyCertResponse() {
    }

    public CertInfo getData() {
        return this.data;
    }

    public void setData(CertInfo data) {
        this.data = data;
    }

    public static class CertInfo {
        private String cert;
        private String encCert;
        private String encKey;

        public CertInfo() {
        }

        public String getCert() {
            return this.cert == null ? "" : this.cert;
        }

        public void setCert(String cert) {
            this.cert = cert;
        }

        public String getEncCert() {
            return this.encCert == null ? "" : this.encCert;
        }

        public void setEncCert(String encCert) {
            this.encCert = encCert;
        }

        public String getEncKey() {
            return this.encKey == null ? "" : this.encKey;
        }

        public void setEncKey(String encKey) {
            this.encKey = encKey;
        }
    }
}
