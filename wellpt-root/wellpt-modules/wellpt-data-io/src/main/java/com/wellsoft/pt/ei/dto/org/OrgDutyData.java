package com.wellsoft.pt.ei.dto.org;

import com.wellsoft.pt.ei.annotate.FieldType;

import java.io.Serializable;

/**
 * Description: 如何描述该类
 *
 * @author liuyz
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021/9/28.1	liuyz		2021/9/28		Create
 * </pre>
 * @date 2021/9/28
 */
public class OrgDutyData implements Serializable {
    @FieldType(desc = "主键uuid")
    private String uuid;
    @FieldType(desc = "职务名称", required = true)
    private String name;
    @FieldType(desc = "职务Id")
    private String id;
    @FieldType(desc = "编码", required = true)
    private String code;
    @FieldType(desc = "备注")
    private String remark;
    @FieldType(desc = "sap编码")
    private String sapCode;
    @FieldType(desc = "归属单位Id")
    private String systemUnitId;
    @FieldType(desc = "归属单位名称")
    private String systemUnitName;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getSapCode() {
        return sapCode;
    }

    public void setSapCode(String sapCode) {
        this.sapCode = sapCode;
    }

    public String getSystemUnitId() {
        return systemUnitId;
    }

    public void setSystemUnitId(String systemUnitId) {
        this.systemUnitId = systemUnitId;
    }

    public String getSystemUnitName() {
        return systemUnitName;
    }

    public void setSystemUnitName(String systemUnitName) {
        this.systemUnitName = systemUnitName;
    }
}
