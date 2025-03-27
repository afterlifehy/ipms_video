//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.peakinfo.base.ca.com.custle.ksmkey.certificate;

public class MKUserInfo {
    private String name;
    private String idNo;
    private String province;
    private String city;
    private String organization;
    private String organizationUnit;
    private String email;
    private String mobile;

    public MKUserInfo() {
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdNo() {
        return this.idNo;
    }

    public void setIdNo(String idNo) {
        this.idNo = idNo;
    }

    public String getProvince() {
        return this.province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getOrganization() {
        return this.organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getOrganizationUnit() {
        return this.organizationUnit;
    }

    public void setOrganizationUnit(String organizationUnit) {
        this.organizationUnit = organizationUnit;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return this.mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getDn() {
        if (this.name != null && !"".equals(this.name)) {
            StringBuilder builder = new StringBuilder();
            builder.append("C=CN,").append("CN=").append(this.name);
            if (this.province != null && "".equals(this.province)) {
                builder.append(",S=").append(this.province);
            }

            if (this.city != null && "".equals(this.city)) {
                builder.append(",L=").append(this.city);
            }

            if (this.organization != null && "".equals(this.organization)) {
                builder.append(",O=").append(this.organization);
            }

            if (this.organizationUnit != null && "".equals(this.organizationUnit)) {
                builder.append(",OU=").append(this.organizationUnit);
            }

            if (this.email != null && "".equals(this.email)) {
                builder.append(",E=").append(this.email);
            }

            return builder.toString();
        } else {
            return null;
        }
    }
}
