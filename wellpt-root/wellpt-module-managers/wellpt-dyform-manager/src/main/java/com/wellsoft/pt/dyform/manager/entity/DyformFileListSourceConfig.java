package com.wellsoft.pt.dyform.manager.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.pt.app.entity.AppDefElementI18nEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;

/**
 * 列表式附件配置_附件来源
 */
@Entity
@Table(name = "DYFORM_FILE_LIST_SOURCE_CONFIG")
@DynamicUpdate
@DynamicInsert
public class DyformFileListSourceConfig extends IdEntity {

    private static final long serialVersionUID = 7460312831684567845L;


    // 名称
    private String sourceName;

    // 编码
    private String code;

    // 图标
    private String icon;

    // JS模块
    private String jsModule;

    // 默认选中
    private Integer defaultFlag;

    // 排序字段
    private Integer orderIndex;

    private List<AppDefElementI18nEntity> i18ns;

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getJsModule() {
        return jsModule;
    }

    public void setJsModule(String jsModule) {
        this.jsModule = jsModule;
    }

    public Integer getDefaultFlag() {
        return defaultFlag;
    }

    public void setDefaultFlag(Integer defaultFlag) {
        this.defaultFlag = defaultFlag;
    }

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }

    @Transient
    public List<AppDefElementI18nEntity> getI18ns() {
        return i18ns;
    }

    public void setI18ns(List<AppDefElementI18nEntity> i18ns) {
        this.i18ns = i18ns;
    }
}
