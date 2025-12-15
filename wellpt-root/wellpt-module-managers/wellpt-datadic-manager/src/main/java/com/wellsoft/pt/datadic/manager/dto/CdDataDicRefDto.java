package com.wellsoft.pt.datadic.manager.dto;

import java.io.Serializable;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/6/11
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/6/11    chenq		2019/6/11		Create
 * </pre>
 */
public class CdDataDicRefDto implements Serializable {

    private String uuid;

    private String parentUuid;

    private String refUuid;

    private String moduleId;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getParentUuid() {
        return parentUuid;
    }

    public void setParentUuid(String parentUuid) {
        this.parentUuid = parentUuid;
    }

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
