package com.wellsoft.pt.common.i18n.entity;


import com.wellsoft.context.jdbc.entity.SysEntity;

import javax.persistence.MappedSuperclass;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2025年03月25日   chenq	 Create
 * </pre>
 */
@MappedSuperclass
public abstract class I18nEntity extends SysEntity {


    private Long dataUuid;
    private String dataId;
    private String dataCode;
    private String locale;
    private String content;

    public I18nEntity(Long dataUuid, String dataId, String dataCode, String locale, String content) {
        this.dataUuid = dataUuid;
        this.dataId = dataId;
        this.dataCode = dataCode;
        this.locale = locale;
        this.content = content;
    }

    public I18nEntity() {
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
