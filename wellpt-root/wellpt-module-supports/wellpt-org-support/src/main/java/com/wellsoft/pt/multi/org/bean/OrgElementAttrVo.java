package com.wellsoft.pt.multi.org.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/10/26
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/10/26    chenq		2019/10/26		Create
 * </pre>
 */
@ApiModel("组织元素属性")
public class OrgElementAttrVo implements Serializable {
    private static final long serialVersionUID = 8549763058321519865L;
    @ApiModelProperty("uuid")
    private String uuid;
    @ApiModelProperty("属性类型")
    private String attrType;
    @ApiModelProperty("属性值")
    private String attrValue;
    @ApiModelProperty("属性显示值")
    private String attrDisplay;
    @ApiModelProperty("编号")
    private String code;
    @ApiModelProperty("名称")
    private String name;
    @ApiModelProperty("元素Uuid")
    private String elementUuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getAttrType() {
        return attrType;
    }

    public void setAttrType(String attrType) {
        this.attrType = attrType;
    }

    public String getAttrValue() {
        return attrValue;
    }

    public void setAttrValue(String attrValue) {
        this.attrValue = attrValue;
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

    public String getElementUuid() {
        return elementUuid;
    }

    public void setElementUuid(String elementUuid) {
        this.elementUuid = elementUuid;
    }

    public String getAttrDisplay() {
        return attrDisplay;
    }

    public void setAttrDisplay(String attrDisplay) {
        this.attrDisplay = attrDisplay;
    }
}
