package com.wellsoft.pt.repository.entity;

public enum ConvertType {

    OFFICE2OFD("office2ofd", "office转为ofd", "ofd"),
    OFFICE2PDF("office2pdf", "office转为pdf", "pdf"),
    PDF2OFD("pdf2ofd", "pdf转为ofd", "ofd"),
    OFD2PDF("ofd2pdf", "ofd转为pdf", "pdf"),
    PREVIEW("preview", "预览", "");
    private String type;

    private String desc;

    private String convertSuffix;


    ConvertType(String type, String desc, String convertSuffix) {
        this.type = type;
        this.desc = desc;
        this.convertSuffix = convertSuffix;
    }

    public static ConvertType getConvertType(String type) {
        for (ConvertType convertType : ConvertType.values()) {
            if (convertType.getValue().equals(type)) {
                return convertType;
            }
        }
        return null;
    }

    public static ConvertType getConvertType2ofd(String prefix) {
        if ("pdf".equals(prefix)) {
            return PDF2OFD;
        }
        return OFFICE2OFD;
    }

    public String getValue() {
        return this.type;
    }

    public String getConvertSuffix() {
        return convertSuffix;
    }

    public void setConvertSuffix(String convertSuffix) {
        this.convertSuffix = convertSuffix;
    }
}
