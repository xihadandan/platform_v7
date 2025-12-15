package com.wellsoft.pt.app.entity;

import com.wellsoft.context.jdbc.entity.Entity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Table;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年08月29日   chenq	 Create
 * </pre>
 */
@javax.persistence.Entity
@Table(name = "APP_PROD_VERSION_LOGIN")
@DynamicUpdate
@DynamicInsert
public class AppProdVersionLoginEntity extends Entity {


    private Long prodVersionUuid;
    private String prodVersionId;
    private String prodId;
    private String name;
    private String title;
    private String remark;
    private String defJson;
    private Boolean isDefault;
    private Boolean isPc;

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

    public String getProdId() {
        return prodId;
    }

    public void setProdId(String prodId) {
        this.prodId = prodId;
    }

    public String getDefJson() {
        return defJson;
    }

    public void setDefJson(String defJson) {
        this.defJson = defJson;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean aDefault) {
        isDefault = aDefault;
    }

    public Boolean getIsPc() {
        return isPc;
    }

    public void setIsPc(Boolean pc) {
        isPc = pc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
