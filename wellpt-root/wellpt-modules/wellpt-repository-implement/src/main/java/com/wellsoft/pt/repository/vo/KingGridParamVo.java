package com.wellsoft.pt.repository.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Description:
 * <pre>
 * 修改记录:
 * 修改后版本    修改人     修改日期    修改内容
 * 1.0         baozh    2021/11/15   Create
 * </pre>
 */
@ApiModel("金格转换参数")
public class KingGridParamVo {


    @ApiModelProperty("office文档默认转换成什么文件，pdf/ofd，默认pdf")
    private String officeDocConvertType;
    @ApiModelProperty("文档添加的水印文字")
    private String marker;
    @ApiModelProperty("true/false 是否显示签章功能")
    private String showSignatureBtn;
    @ApiModelProperty("true/false 是否显示下载按钮")
    private String showDownloadBtn;
    @ApiModelProperty("true/false 是否显示打印按钮")
    private String showPrintBtn;


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

}
