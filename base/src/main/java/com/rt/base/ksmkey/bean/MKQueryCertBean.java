//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.rt.base.ksmkey.bean;

public class MKQueryCertBean extends MKBaseResponse {
    private Data data;

    public MKQueryCertBean() {
    }

    public Data getData() {
        return this.data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {
        private String cert;

        public Data() {
        }

        public String getCert() {
            return this.cert;
        }

        public void setCert(String cert) {
            this.cert = cert;
        }
    }
}
