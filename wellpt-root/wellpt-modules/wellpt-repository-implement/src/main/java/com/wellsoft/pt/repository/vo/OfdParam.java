package com.wellsoft.pt.repository.vo;

import java.io.InputStream;

/**
 * Description:
 *
 * @author linzc
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期        修改内容
 * 2021/9/18.1    linzc       2021/9/18     Create
 * </pre>
 */
public class OfdParam {
    private String appKey;

    private String appSecret;

    private String businessId;

    private String businessName;

    private String url;

    private InputStream fileInputStream;

    private String fileName;

    private String stampConvertMethod;

    private String fontMethod;

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String encode, String businessName) {
        this.businessName = businessName;
    }

    public InputStream getFileInputStream() {
        return fileInputStream;
    }

    public void setFileInputStream(InputStream fileInputStream) {
        this.fileInputStream = fileInputStream;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getStampConvertMethod() {
        return stampConvertMethod;
    }

    public void setStampConvertMethod(String stampConvertMethod) {
        this.stampConvertMethod = stampConvertMethod;
    }

    public String getFontMethod() {
        return fontMethod;
    }

    public void setFontMethod(String fontMethod) {
        this.fontMethod = fontMethod;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
