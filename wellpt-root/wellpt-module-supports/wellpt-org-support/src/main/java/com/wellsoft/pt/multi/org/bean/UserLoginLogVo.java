package com.wellsoft.pt.multi.org.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * @Auther: yt
 * @Date: 2021/11/8 10:26
 * @Description: 用户登录日志
 */
@ApiModel("用户登录日志")
public class UserLoginLogVo implements Serializable {

    /**
     * 登录IP
     */
    @ApiModelProperty("登录IP")
    private String loginIp;

    @ApiModelProperty("登录来源")
    private Integer loginSource;
    @ApiModelProperty("客户端操作系统")
    private String userOs;
    @ApiModelProperty("客户端浏览器")
    private String browser;
    @ApiModelProperty("客户端浏览器版本")
    private String browserVersion;

    public String getBrowserVersion() {
        return this.browserVersion;
    }

    public void setBrowserVersion(final String browserVersion) {
        this.browserVersion = browserVersion;
    }

    public String getUserOs() {
        return this.userOs;
    }

    public void setUserOs(final String userOs) {
        this.userOs = userOs;
    }

    public String getBrowser() {
        return this.browser;
    }

    public void setBrowser(final String browser) {
        this.browser = browser;
    }

    public String getLoginIp() {
        return loginIp;
    }

    public void setLoginIp(String loginIp) {
        this.loginIp = loginIp;
    }

    public Integer getLoginSource() {
        return loginSource;
    }

    public void setLoginSource(Integer loginSource) {
        this.loginSource = loginSource;
    }
}
