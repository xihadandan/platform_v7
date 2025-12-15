package com.wellsoft.pt.app.entity;

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
 * 2023年08月21日   chenq	 Create
 * </pre>
 */
@Entity
@Table(name = "APP_PROD_RELA_PAGE")
@DynamicUpdate
@DynamicInsert
public class AppProdRelaPageEntity extends com.wellsoft.context.jdbc.entity.Entity {

    private String prodId;

    private Long prodVersionUuid;

    private String pageUuid;

    private String pageId;

    private String theme;

    public Long getProdVersionUuid() {
        return prodVersionUuid;
    }

    public void setProdVersionUuid(Long prodVersionUuid) {
        this.prodVersionUuid = prodVersionUuid;
    }

    public String getPageUuid() {
        return pageUuid;
    }

    public void setPageUuid(String pageUuid) {
        this.pageUuid = pageUuid;
    }

    public String getPageId() {
        return pageId;
    }

    public void setPageId(String pageId) {
        this.pageId = pageId;
    }

    public String getProdId() {
        return prodId;
    }

    public void setProdId(String prodId) {
        this.prodId = prodId;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }
}
