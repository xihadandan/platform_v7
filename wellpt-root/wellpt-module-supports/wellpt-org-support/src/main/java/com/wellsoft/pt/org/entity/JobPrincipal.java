package com.wellsoft.pt.org.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 部门(负责人、分管领导、管理员)
 *
 * @author zhengky
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	       修改人		修改日期	      修改内容
 * 2014-9-26.1  zhengky	2014-9-26	  Create
 * </pre>
 * @date 2014-9-26
 */
//@Entity
//@Table(name = "org_job_principal")
//@DynamicUpdate
//@DynamicInsert
@Deprecated
public class JobPrincipal extends IdEntity {

    public static final String TYPE_PRINCIPAL = "0";
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -7692515083458168667L;
    private String jobUuid;
    private String type; //(0 负责人,预留字段)
    private String orgId;
    private String tenantId;

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    @Column(nullable = false)
    public String getJobUuid() {
        return jobUuid;
    }

    public void setJobUuid(String jobUuid) {
        this.jobUuid = jobUuid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Column(nullable = false)
    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

}
