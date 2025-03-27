//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.peakinfo.base.ca.com.custle.ksmkey.bean;

public class MKTrustQueryBean extends MKBaseBean {
    private Data data;

    public MKTrustQueryBean() {
    }

    public Data getData() {
        return this.data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {
        private Integer trust;

        public Data() {
        }

        public Integer getTrust() {
            return this.trust;
        }

        public void setTrust(Integer trust) {
            this.trust = trust;
        }
    }
}
