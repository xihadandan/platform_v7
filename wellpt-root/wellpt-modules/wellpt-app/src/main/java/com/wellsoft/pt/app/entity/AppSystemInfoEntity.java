package com.wellsoft.pt.app.entity;

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
 * 2024年01月16日   chenq	 Create
 * </pre>
 */
@Entity
@Table(name = "APP_SYSTEM_INFO")
@DynamicUpdate
@DynamicInsert
public class AppSystemInfoEntity extends SysEntity {

    private Long prodVersionUuid;
    private String title;
    private String favicon;
    private String adminLogo;
    private String adminTitle;
    private String defaultLocale;
    private Boolean enableLocale;

    public Long getProdVersionUuid() {
        return prodVersionUuid;
    }

    public void setProdVersionUuid(Long prodVersionUuid) {
        this.prodVersionUuid = prodVersionUuid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFavicon() {
        return favicon;
    }

    public void setFavicon(String favicon) {
        this.favicon = favicon;
    }

    public String getAdminLogo() {
        return adminLogo;
    }

    public void setAdminLogo(String adminLogo) {
        this.adminLogo = adminLogo;
    }

    public String getAdminTitle() {
        return adminTitle;
    }

    public void setAdminTitle(String adminTitle) {
        this.adminTitle = adminTitle;
    }

    public String getDefaultLocale() {
        return defaultLocale;
    }

    public void setDefaultLocale(String defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    public Boolean getEnableLocale() {
        return enableLocale;
    }

    public void setEnableLocale(Boolean enableLocale) {
        this.enableLocale = enableLocale;
    }
}
