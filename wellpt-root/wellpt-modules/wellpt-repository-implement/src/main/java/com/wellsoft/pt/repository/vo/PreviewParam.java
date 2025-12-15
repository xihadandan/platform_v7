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
public class PreviewParam {

    private InputStream fileInputStream;

    private String fileName;

    private String url;

    private String type;

    private String officeDocConvertType;

    private String marker;

    private String showSignatureBtn;

    private String showDownloadBtn;

    private String showPrintBtn;

    private String userId;

    private String userCode;

    private String sealOrigin;

    private String certOrigin;

    public PreviewParam(InputStream fileInputStream, String fileName, String url) {
        this.fileInputStream = fileInputStream;
        this.fileName = fileName;
        this.url = url;
        this.type = "wps";
        this.officeDocConvertType = "pdf";
    }

    public InputStream getFileInputStream() {
        return fileInputStream;
    }

    public void setFileInputStream(InputStream fileInputStream) {
        this.fileInputStream = fileInputStream;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOfficeDocConvertType() {
        return officeDocConvertType;
    }

    public void setOfficeDocConvertType(String officeDocConvertType) {
        this.officeDocConvertType = officeDocConvertType;
    }

    public String getMarker() {
        return marker;
    }

    public void setMarker(String marker) {
        this.marker = marker;
    }

    public String getShowSignatureBtn() {
        return showSignatureBtn;
    }

    public void setShowSignatureBtn(String showSignatureBtn) {
        this.showSignatureBtn = showSignatureBtn;
    }

    public String getShowDownloadBtn() {
        return showDownloadBtn;
    }

    public void setShowDownloadBtn(String showDownloadBtn) {
        this.showDownloadBtn = showDownloadBtn;
    }

    public String getShowPrintBtn() {
        return showPrintBtn;
    }

    public void setShowPrintBtn(String showPrintBtn) {
        this.showPrintBtn = showPrintBtn;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getSealOrigin() {
        return sealOrigin;
    }

    public void setSealOrigin(String sealOrigin) {
        this.sealOrigin = sealOrigin;
    }

    public String getCertOrigin() {
        return certOrigin;
    }

    public void setCertOrigin(String certOrigin) {
        this.certOrigin = certOrigin;
    }
}
