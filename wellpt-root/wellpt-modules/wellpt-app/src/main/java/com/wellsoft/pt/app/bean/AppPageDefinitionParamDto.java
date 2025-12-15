package com.wellsoft.pt.app.bean;

import com.google.common.collect.Maps;
import com.wellsoft.pt.app.entity.AppDefElementI18nEntity;
import com.wellsoft.pt.app.ui.client.widget.configuration.AppWidgetDefinitionElement;
import com.wellsoft.pt.app.ui.client.widget.configuration.FunctionElement;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2020年12月16日   chenq	 Create
 * </pre>
 */
@ApiModel
public class AppPageDefinitionParamDto implements Serializable {

    @ApiModelProperty("ID")
    private String id;
    @ApiModelProperty("页面名称")
    private String name;
    @ApiModelProperty("页面定义json字符")
    private String definitionJson;
    @ApiModelProperty("产品集成UUID")
    private String piUuid;
    @ApiModelProperty("编号")
    private String code;
    @ApiModelProperty("标题")
    private String title;
    @ApiModelProperty("uuid")
    private String uuid;
    private String isPc;
    private String remark;
    @ApiModelProperty("页面容器类型")
    private String wtype;
    @ApiModelProperty("是否创建新版本")
    private Boolean newVersion = false;
    private Boolean isDefault = false;
    private Boolean designable = true;
    private Boolean layoutFixed = false;
    private String appId;
    private String theme;
    private Boolean enabled = true;
    private Boolean isAnonymous = false;
    private String tenant;
    @ApiModelProperty("组件生成的功能元素集合")
    private HashMap<String, ArrayList<FunctionElement>> functionElements = Maps.newHashMap();
    @ApiModelProperty("组件定义集合")
    private List<AppWidgetDefinitionElement> appWidgetDefinitionElements = null;

    private List<AppDefElementI18nEntity> i18ns;

    private BigDecimal version;

    public String getDefinitionJson() {
        return definitionJson;
    }

    public void setDefinitionJson(String definitionJson) {
        this.definitionJson = definitionJson;
    }

    public String getPiUuid() {
        return piUuid;
    }

    public void setPiUuid(String piUuid) {
        this.piUuid = piUuid;
    }

    public Boolean getNewVersion() {
        return newVersion;
    }

    public void setNewVersion(Boolean newVersion) {
        this.newVersion = newVersion;
    }

    public HashMap<String, ArrayList<FunctionElement>> getFunctionElements() {
        return functionElements;
    }

    public void setFunctionElements(HashMap<String, ArrayList<FunctionElement>> functionElements) {
        this.functionElements = functionElements;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<AppWidgetDefinitionElement> getAppWidgetDefinitionElements() {
        return appWidgetDefinitionElements;
    }

    public void setAppWidgetDefinitionElements(List<AppWidgetDefinitionElement> appWidgetDefinitionElements) {
        this.appWidgetDefinitionElements = appWidgetDefinitionElements;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getIsPc() {
        return isPc;
    }

    public void setIsPc(String isPc) {
        this.isPc = isPc;
    }

    public String getWtype() {
        return wtype;
    }

    public void setWtype(String wtype) {
        this.wtype = wtype;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    public Boolean getDesignable() {
        return designable;
    }

    public void setDesignable(Boolean designable) {
        this.designable = designable;
    }

    public Boolean getLayoutFixed() {
        return layoutFixed;
    }

    public void setLayoutFixed(Boolean layoutFixed) {
        this.layoutFixed = layoutFixed;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    public BigDecimal getVersion() {
        return version;
    }

    public void setVersion(BigDecimal version) {
        this.version = version;
    }

    public Boolean getIsAnonymous() {
        return isAnonymous;
    }

    public void setIsAnonymous(Boolean anonymous) {
        isAnonymous = anonymous;
    }

    public List<AppDefElementI18nEntity> getI18ns() {
        return i18ns;
    }

    public void setI18ns(List<AppDefElementI18nEntity> i18ns) {
        this.i18ns = i18ns;
    }
}
