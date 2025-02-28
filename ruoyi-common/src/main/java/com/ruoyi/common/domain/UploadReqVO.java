package com.ruoyi.common.domain;

import java.util.List;

public class UploadReqVO {

    //手机号
    private String mobile;
    //姓名
    private String realname;
    //身份证号
    private String idno;
    //字符数组
    private List<String> testBase64Str;
    private String sign;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getIdno() {
        return idno;
    }

    public void setIdno(String idno) {
        this.idno = idno;
    }

    public List<String> getTestBase64Str() {
        return testBase64Str;
    }

    public void setTestBase64Str(List<String> testBase64Str) {
        this.testBase64Str = testBase64Str;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
