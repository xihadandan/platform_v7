package com.wellsoft.pt.dms.bean;

import java.io.Serializable;

/**
 * Description:文档交换-通讯录单位
 *
 * @author chenq
 * @date 2018/5/31
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/5/31    chenq		2018/5/31		Create
 * </pre>
 */

public class DmsDocExcContactBookUnitDto implements Serializable {

    private String uuid;

    private String unitName;//简称

    private String fullUnitName;//全称

    private String unitCode;

    private String moduleId;

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getUnitCode() {
        return unitCode;
    }

    public void setUnitCode(String unitCode) {
        this.unitCode = unitCode;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getFullUnitName() {
        return fullUnitName;
    }

    public void setFullUnitName(String fullUnitName) {
        this.fullUnitName = fullUnitName;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }
}
