package com.wellsoft.pt.app.ui.client.widget.configuration;

import com.wellsoft.context.base.BaseObject;
import com.wellsoft.pt.app.entity.AppDefElementI18nEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2022年04月08日   chenq	 Create
 * </pre>
 */
@ApiModel
public class AppWidgetDefinitionElement extends BaseObject {

    private static final long serialVersionUID = 5434134451608501139L;
    // 标题
    @ApiModelProperty("title")
    private String title;
    // 名称
    @ApiModelProperty("name")
    private String name;
    // ID
    @ApiModelProperty("id")
    private String id;
    // 组件类型
    @ApiModelProperty("wtype")
    private String wtype;
    // 定义JSON信息
    @ApiModelProperty("definitionJson")
    private String definitionJson;
    // 定义HTML信息
    @ApiModelProperty("html")
    private String html;
    @ApiModelProperty("refWidgetDefUuid")
    // 引用的组件定义UUID, 为空表示没引用
    private String refWidgetDefUuid;

    private String appId;// 产品ID 或者 模块ID , 用于快速查询模块或者产品下的组件

    private Boolean main; // 标记为主要

    private List<AppDefElementI18nEntity> i18ns;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getWtype() {
        return wtype;
    }

    public void setWtype(String wtype) {
        this.wtype = wtype;
    }

    public String getDefinitionJson() {
        return definitionJson;
    }

    public void setDefinitionJson(String definitionJson) {
        this.definitionJson = definitionJson;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public String getRefWidgetDefUuid() {
        return refWidgetDefUuid;
    }

    public void setRefWidgetDefUuid(String refWidgetDefUuid) {
        this.refWidgetDefUuid = refWidgetDefUuid;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public Boolean getMain() {
        return main;
    }

    public void setMain(Boolean main) {
        this.main = main;
    }

    public List<AppDefElementI18nEntity> getI18ns() {
        return i18ns;
    }

    public void setI18ns(List<AppDefElementI18nEntity> i18ns) {
        this.i18ns = i18ns;
    }
}
