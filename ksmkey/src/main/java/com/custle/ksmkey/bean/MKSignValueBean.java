//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.custle.ksmkey.bean;

import java.util.List;

public class MKSignValueBean extends MKBaseResponse {
    private SignData data;

    public MKSignValueBean() {
    }

    public SignData getData() {
        return this.data;
    }

    public void setData(SignData data) {
        this.data = data;
    }

    public static class SignData {
        private List<String> signValue;

        public SignData() {
        }

        public List<String> getSignValue() {
            return this.signValue;
        }

        public void setSignValue(List<String> signValue) {
            this.signValue = signValue;
        }
    }
}
