package com.wellsoft.pt.manager.commons.reference.entity;

import com.wellsoft.context.jdbc.entity.TenantEntity;

import javax.persistence.MappedSuperclass;

/**
 * Description: 业务模块配置被引用的实体类
 *
 * @author chenq
 * @date 2019/6/6
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/6/6    chenq		2019/6/6		Create
 * </pre>
 */
@MappedSuperclass
public abstract class ModuleFunctionConfigRefEntity extends TenantEntity {

    private String refUuid;

    private String moduleId;

    public String getRefUuid() {
        return refUuid;
    }

    public void setRefUuid(String refUuid) {
        this.refUuid = refUuid;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }
}
