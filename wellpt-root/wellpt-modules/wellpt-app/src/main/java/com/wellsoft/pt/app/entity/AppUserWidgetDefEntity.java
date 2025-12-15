package com.wellsoft.pt.app.entity;

import com.google.common.collect.Lists;
import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/8/27
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/8/27    chenq		2019/8/27		Create
 * </pre>
 */
@Entity
@Table(name = "APP_USER_WIDGET_DEFINITION")
@DynamicUpdate
@DynamicInsert
public class AppUserWidgetDefEntity extends IdEntity {


    private static final long serialVersionUID = 8558492431379200968L;

    private String userId;

    private String definitionJson;

    private String widgetId;

    private Type type;

    private String appId;

    private String title;

    private String remark;

    private Boolean enabled;

    private List<AppDefElementI18nEntity> i18ns = Lists.newArrayList();


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDefinitionJson() {
        return definitionJson;
    }

    public void setDefinitionJson(String definitionJson) {
        this.definitionJson = definitionJson;
    }

    public String getWidgetId() {
        return widgetId;
    }

    public void setWidgetId(String widgetId) {
        this.widgetId = widgetId;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    @Transient
    public List<AppDefElementI18nEntity> getI18ns() {
        return i18ns;
    }

    public void setI18ns(List<AppDefElementI18nEntity> i18ns) {
        this.i18ns = i18ns;
    }

    public enum Type {
        WIDGET, WIDGET_AS_TEMPLATE, FUNCTION_WIDGET
    }
}
