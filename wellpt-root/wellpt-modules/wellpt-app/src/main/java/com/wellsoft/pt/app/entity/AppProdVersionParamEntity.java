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
 * 2023年09月11日   chenq	 Create
 * </pre>
 */
@javax.persistence.Entity
@Table(name = "APP_PROD_VERSION_PARAM")
@DynamicUpdate
@DynamicInsert
public class AppProdVersionParamEntity extends Entity {

    private String prodId;
    private Long prodVersionUuid;
    private String propKey;
    private String propValue;
    private String name;
    private String remark;

    public String getProdId() {
        return prodId;
    }

    public void setProdId(String prodId) {
        this.prodId = prodId;
    }

    public Long getProdVersionUuid() {
        return prodVersionUuid;
    }

    public void setProdVersionUuid(Long prodVersionUuid) {
        this.prodVersionUuid = prodVersionUuid;
    }

    public String getPropKey() {
        return propKey;
    }

    public void setPropKey(String propKey) {
        this.propKey = propKey;
    }

    public String getPropValue() {
        return propValue;
    }

    public void setPropValue(String propValue) {
        this.propValue = propValue;
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
}
