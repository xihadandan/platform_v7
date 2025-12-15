package com.wellsoft.pt.api.entity;

import com.wellsoft.context.jdbc.entity.TenantEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Description: 内部api对外授权情况
 *
 * @author chenq
 * @date 2018/10/25
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/10/25    chenq		2018/10/25		Create
 * </pre>
 */
@Entity
@Table(name = "API_AUTHORIZE_ITEM")
@DynamicUpdate
@DynamicInsert
public class ApiAuthorizeItemEntity extends TenantEntity {

    private static final long serialVersionUID = 2338046409468911104L;

    private String pattern;

    private ApiOutSystemConfigEntity systemConfig;

    private Boolean isAuthorized;//是否授权

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public Boolean getIsAuthorized() {
        return isAuthorized;
    }

    public void setIsAuthorized(Boolean authorized) {
        isAuthorized = authorized;
    }

    @ManyToOne
    @JoinColumn(name = "system_uuid")
    public ApiOutSystemConfigEntity getSystemConfig() {
        return systemConfig;
    }

    public void setSystemConfig(ApiOutSystemConfigEntity systemConfig) {
        this.systemConfig = systemConfig;
    }
}
