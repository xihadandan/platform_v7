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
@Table(name = "APP_PROD_VERSION_LOG")
@DynamicUpdate
@DynamicInsert
public class AppProductVersionLogEntity extends com.wellsoft.context.jdbc.entity.Entity {

    private Long prodVersionUuid;
    private String detail;

    public Long getProdVersionUuid() {
        return prodVersionUuid;
    }

    public void setProdVersionUuid(Long prodVersionUuid) {
        this.prodVersionUuid = prodVersionUuid;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
