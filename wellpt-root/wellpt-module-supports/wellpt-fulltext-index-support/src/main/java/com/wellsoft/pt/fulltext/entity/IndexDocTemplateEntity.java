package com.wellsoft.pt.fulltext.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
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
 * 2020年09月09日   chenq	 Create
 * </pre>
 */

@Entity
@Table(name = "INDEX_DOC_TEMPLATE")
@DynamicUpdate
@DynamicInsert
@Deprecated
public class IndexDocTemplateEntity extends IdEntity {

    private String titleExps;
    private String contentExps;
    private String creatorExps;
    private String createTimeField;
    private String modifyTimeField;
    private String url;
    private String name;
    private String type;

    public String getTitleExps() {
        return titleExps;
    }

    public void setTitleExps(String titleExps) {
        this.titleExps = titleExps;
    }

    public String getContentExps() {
        return contentExps;
    }

    public void setContentExps(String contentExps) {
        this.contentExps = contentExps;
    }

    public String getCreatorExps() {
        return creatorExps;
    }

    public void setCreatorExps(String creatorExps) {
        this.creatorExps = creatorExps;
    }

    public String getCreateTimeField() {
        return createTimeField;
    }

    public void setCreateTimeField(String createTimeField) {
        this.createTimeField = createTimeField;
    }

    public String getModifyTimeField() {
        return modifyTimeField;
    }

    public void setModifyTimeField(String modifyTimeField) {
        this.modifyTimeField = modifyTimeField;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
