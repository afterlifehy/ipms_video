//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.custle.ksmkey.bean;

public class MKResolveResponse extends MKBaseResponse {
    private ResolveData data;

    public MKResolveResponse() {
    }

    public ResolveData getData() {
        return this.data;
    }

    public void setData(ResolveData data) {
        this.data = data;
    }

    public static class ResolveData {
        private String equipmentCode;
        private String deviceId;
        private String deptName;

        public ResolveData() {
        }

        public String getEquipmentCode() {
            return this.equipmentCode;
        }

        public void setEquipmentCode(String equipmentCode) {
            this.equipmentCode = equipmentCode;
        }

        public String getDeviceId() {
            return this.deviceId;
        }

        public void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
        }

        public String getDeptName() {
            return this.deptName;
        }

        public void setDeptName(String deptName) {
            this.deptName = deptName;
        }
    }
}
