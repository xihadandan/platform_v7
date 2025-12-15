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
 * 2023年08月03日   chenq	 Create
 * </pre>
 */
@Entity
@Table(name = "APP_PROD_VERSION_SETTING")
@DynamicUpdate
@DynamicInsert
public class AppProdVersionSettingEntity extends com.wellsoft.context.jdbc.entity.Entity {

    private Long prodVersionUuid;
    private String prodVersionId;
    private String title;
    private String icon;
    private String theme;
    private Boolean mapEnabled;
    private String layoutConf;
    private String pcIndexUrl;
    private String mobileIndexUrl;
    private String deviceAnon; // 设备匿名: 00 01 10 11

    public Long getProdVersionUuid() {
        return prodVersionUuid;
    }

    public void setProdVersionUuid(Long prodVersionUuid) {
        this.prodVersionUuid = prodVersionUuid;
    }

    public String getProdVersionId() {
        return prodVersionId;
    }

    public void setProdVersionId(String prodVersionId) {
        this.prodVersionId = prodVersionId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public Boolean getMapEnabled() {
        return mapEnabled;
    }

    public void setMapEnabled(Boolean mapEnabled) {
        this.mapEnabled = mapEnabled;
    }

    public String getLayoutConf() {
        return layoutConf;
    }

    public void setLayoutConf(String layoutConf) {
        this.layoutConf = layoutConf;
    }

    public String getDeviceAnon() {
        return deviceAnon;
    }

    public void setDeviceAnon(String deviceAnon) {
        this.deviceAnon = deviceAnon;
    }

    public String getPcIndexUrl() {
        return pcIndexUrl;
    }

    public void setPcIndexUrl(String pcIndexUrl) {
        this.pcIndexUrl = pcIndexUrl;
    }

    public String getMobileIndexUrl() {
        return mobileIndexUrl;
    }

    public void setMobileIndexUrl(String mobileIndexUrl) {
        this.mobileIndexUrl = mobileIndexUrl;
    }


}
