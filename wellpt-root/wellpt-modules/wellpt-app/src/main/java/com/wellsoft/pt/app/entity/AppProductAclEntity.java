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
 * 2023年08月02日   chenq	 Create
 * </pre>
 */
@Entity
@Table(name = "APP_PROD_ACL")
@DynamicUpdate
@DynamicInsert
public class AppProductAclEntity extends com.wellsoft.context.jdbc.entity.Entity {
    private String prodId;
    private String sid;
    private String type;

    public String getProdId() {
        return prodId;
    }

    public void setProdId(String prodId) {
        this.prodId = prodId;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
