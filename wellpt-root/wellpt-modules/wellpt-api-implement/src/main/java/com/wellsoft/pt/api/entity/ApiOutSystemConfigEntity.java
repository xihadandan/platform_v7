package com.wellsoft.pt.api.entity;

import com.wellsoft.context.jdbc.entity.TenantEntity;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

/**
 * Description: api对接系统配置信息
 *
 * @author chenq
 * @date 2018/8/13
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/8/13    chenq		2018/8/13		Create
 * </pre>
 */
@Table(name = "API_OUT_SYSTEM_CONFIG")
@Entity
@DynamicInsert
@DynamicUpdate
public class ApiOutSystemConfigEntity extends TenantEntity {

    private static final long serialVersionUID = -6687616682242997720L;

    private String systemCode;

    private String systemName;

    private String token;

    private List<ApiOutSysServiceConfigEntity> services;

    private List<ApiAuthorizeItemEntity> authorizeItems;


    public String getSystemCode() {
        return systemCode;
    }

    public void setSystemCode(String systemCode) {
        this.systemCode = systemCode;
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "systemConfig")
    @Cascade({CascadeType.ALL})
    @JsonIgnore
    public List<ApiOutSysServiceConfigEntity> getServices() {
        return services;
    }

    public void setServices(List<ApiOutSysServiceConfigEntity> services) {
        this.services = services;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "systemConfig")
    @Cascade({CascadeType.ALL})
    @JsonIgnore
    public List<ApiAuthorizeItemEntity> getAuthorizeItems() {
        return authorizeItems;
    }

    public void setAuthorizeItems(
            List<ApiAuthorizeItemEntity> authorizeItems) {
        this.authorizeItems = authorizeItems;
    }
}
