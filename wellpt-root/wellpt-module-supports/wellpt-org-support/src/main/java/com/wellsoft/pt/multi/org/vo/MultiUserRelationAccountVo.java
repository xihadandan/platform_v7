package com.wellsoft.pt.multi.org.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Description:
 * 关联账户VO
 * <pre>
 * 修改记录:
 * 修改后版本    修改人     修改日期    修改内容
 * 1.0         baozh    2022/1/12   Create
 * </pre>
 */
@ApiModel("关联账户VO")
public class MultiUserRelationAccountVo {

    @ApiModelProperty("关联账户")
    private String relationAccount;

    @ApiModelProperty("关联账户密码")
    private String relationPassword;

    @ApiModelProperty("关联账户系统ID")
    private String systemUnitId;

    @ApiModelProperty("验证码")
    private String validCode;

    @ApiModelProperty("手机短信")
    private String smsValidCode;

    public String getRelationAccount() {
        return relationAccount;
    }

    public void setRelationAccount(String relationAccount) {
        this.relationAccount = relationAccount;
    }

    public String getRelationPassword() {
        return relationPassword;
    }

    public void setRelationPassword(String relationPassword) {
        this.relationPassword = relationPassword;
    }

    public String getSystemUnitId() {
        return systemUnitId;
    }

    public void setSystemUnitId(String systemUnitId) {
        this.systemUnitId = systemUnitId;
    }

    public String getValidCode() {
        return validCode;
    }

    public void setValidCode(String validCode) {
        this.validCode = validCode;
    }

    public String getSmsValidCode() {
        return smsValidCode;
    }

    public void setSmsValidCode(String smsValidCode) {
        this.smsValidCode = smsValidCode;
    }
}
