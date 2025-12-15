package com.wellsoft.pt.security.config.dto;

import com.wellsoft.pt.multi.org.entity.MultiOrgSystemUnit;
import com.wellsoft.pt.security.config.entity.AppLoginPageConfigEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * Description: App登录页面配置设置
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	        修改人		修改日期			修改内容
 * 2021/3/17.1	    zenghw		2021/3/17		    Create
 * </pre>
 * @date 2021/3/17
 */
@ApiModel(value = "App登录页面配置设置输出对象", description = "App登录页面配置设置输出对象")
public class AppLoginPageConfigSettingDto {
    @ApiModelProperty(value = "app登录页面配置")
    private AppLoginPageConfigEntity po;
    @ApiModelProperty(value = "平台ID")
    private String ptId;
    @ApiModelProperty(value = "默认ID")
    private String defaultId;
    @ApiModelProperty(value = "系统单位ID")
    private String systemUnitId;
    @ApiModelProperty(value = "系统单位组织节点列表")
    private List<MultiOrgSystemUnit> allSystemUnits;
    @ApiModelProperty(value = "背景图片")
    private String backgroundImage;
    @ApiModelProperty(value = "头部图片")
    private String headerImage;

    public AppLoginPageConfigEntity getPo() {
        return po;
    }

    public void setPo(AppLoginPageConfigEntity po) {
        this.po = po;
    }

    public String getPtId() {
        return ptId;
    }

    public void setPtId(String ptId) {
        this.ptId = ptId;
    }

    public String getDefaultId() {
        return defaultId;
    }

    public void setDefaultId(String defaultId) {
        this.defaultId = defaultId;
    }

    public String getSystemUnitId() {
        return systemUnitId;
    }

    public void setSystemUnitId(String systemUnitId) {
        this.systemUnitId = systemUnitId;
    }

    public List<MultiOrgSystemUnit> getAllSystemUnits() {
        return allSystemUnits;
    }

    public void setAllSystemUnits(List<MultiOrgSystemUnit> allSystemUnits) {
        this.allSystemUnits = allSystemUnits;
    }

    public String getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(String backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    public String getHeaderImage() {
        return headerImage;
    }

    public void setHeaderImage(String headerImage) {
        this.headerImage = headerImage;
    }
}
