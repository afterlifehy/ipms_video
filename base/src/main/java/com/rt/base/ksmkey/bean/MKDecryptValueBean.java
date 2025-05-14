//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.rt.base.ksmkey.bean;

public class MKDecryptValueBean extends MKBaseResponse {
    private DecryptData data;

    public MKDecryptValueBean() {
    }

    public DecryptData getData() {
        return this.data;
    }

    public void setData(DecryptData data) {
        this.data = data;
    }

    public static class DecryptData {
        private String decStr;

        public DecryptData() {
        }

        public String getDecStr() {
            return this.decStr;
        }

        public void setDecStr(String decStr) {
            this.decStr = decStr;
        }
    }
}
