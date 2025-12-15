package com.wellsoft.pt.org.entity;

import com.wellsoft.context.jdbc.entity.SysEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2025年03月11日   chenq	 Create
 * </pre>
 */
@Entity
@Table(name = "ORG_ELEMENT_I18N")
@DynamicUpdate
@DynamicInsert
public class OrgElementI18nEntity extends SysEntity {

    private Long dataUuid;
    private String dataId;
    private String dataCode;
    private String locale;
    private String content;


    public OrgElementI18nEntity() {
    }

    public OrgElementI18nEntity(Long dataUuid, String dataId, String dataCode, String locale, String content) {
        this.dataUuid = dataUuid;
        this.dataId = dataId;
        this.dataCode = dataCode;
        this.locale = locale;
        this.content = content;
    }

    public Long getDataUuid() {
        return dataUuid;
    }

    public void setDataUuid(Long dataUuid) {
        this.dataUuid = dataUuid;
    }

    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    public String getDataCode() {
        return dataCode;
    }

    public void setDataCode(String dataCode) {
        this.dataCode = dataCode;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
