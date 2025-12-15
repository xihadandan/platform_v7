package com.wellsoft.pt.security.audit.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Description: 通过当前用户单位ID获取权限列表输出对象
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	        修改人		修改日期			修改内容
 * 2021/3/17.1	    zenghw		2021/3/17		    Create
 * </pre>
 * @date 2021/3/17
 */
@ApiModel(value = "通过当前用户单位ID获取权限列表输出对象", description = "通过当前用户单位ID获取权限列表输出对象")
public class QueryRoleByCurrentUserUnitIdDto {

    @ApiModelProperty(value = "权限uuid")
    private String uuid;

    @ApiModelProperty(value = "权限ID")
    private String id;

    @ApiModelProperty(value = "权限code")
    private String code;

    @ApiModelProperty(value = "权限名称")
    private String name;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
