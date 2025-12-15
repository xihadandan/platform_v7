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
@Table(name = "APP_PROD_ANON_URL")
@DynamicUpdate
@DynamicInsert
public class AppProdAnonUrlEntity extends com.wellsoft.context.jdbc.entity.Entity {

    private Long prodVersionUuid;
    private String prodVersionId;
    private String url;
    private Type type;
    private String pageId;
    private DeviceType deviceType;


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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPageId() {
        return pageId;
    }

    public void setPageId(String pageId) {
        this.pageId = pageId;
    }

    public DeviceType getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(DeviceType deviceType) {
        this.deviceType = deviceType;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public static enum Type {
        PAGE, URL
    }

    public static enum DeviceType {
        PC, MOBILE
    }

}
