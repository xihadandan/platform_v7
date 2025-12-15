package com.wellsoft.pt.workflow.entity;

import com.wellsoft.context.jdbc.entity.TenantEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 流程定义定时清理配置
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021年7月14日.1	zhulh		2021年7月14日		Create
 * </pre>
 * @date 2021年7月14日
 */
@Entity
@Table(name = "WF_FLOW_DEF_CLEANUP_CONFIG")
@DynamicUpdate
@DynamicInsert
public class WfFlowDefinitionCleanupConfigEntity extends TenantEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -2791302808999956562L;

    // 是否启用
    private Boolean enabled;

    // 保留天数
    private Integer retentionDays;

    /**
     * @return the enabled
     */
    public Boolean getEnabled() {
        return enabled;
    }

    /**
     * @param enabled 要设置的enabled
     */
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * @return the retentionDays
     */
    public Integer getRetentionDays() {
        return retentionDays;
    }

    /**
     * @param retentionDays 要设置的retentionDays
     */
    public void setRetentionDays(Integer retentionDays) {
        this.retentionDays = retentionDays;
    }

}
