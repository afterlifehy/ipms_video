//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.peakinfo.base.ca.com.custle.ksmkey.bean;

import java.util.List;

public class MKSignValueBean extends MKBaseBean {
    private Data data;

    public MKSignValueBean() {
    }

    public Data getData() {
        return this.data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {
        private List<String> signValue;

        public Data() {
        }

        public List<String> getSignValue() {
            return this.signValue;
        }

        public void setSignValue(List<String> signValue) {
            this.signValue = signValue;
        }
    }
}
