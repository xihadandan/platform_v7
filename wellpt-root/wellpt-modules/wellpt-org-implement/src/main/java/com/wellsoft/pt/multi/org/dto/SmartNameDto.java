package com.wellsoft.pt.multi.org.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * Description:
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	        修改人		修改日期			修改内容
 * 2021/12/8.1	    zenghw		2021/12/8		    Create
 * </pre>
 * @date 2021/12/8
 */
@ApiModel("智能名称对象")
public class SmartNameDto {
    // @RequestParam("nameDisplayMethod") int nameDisplayMethod,
    // @RequestParam("nodeIds") List<String> nodeIds,
    // @RequestParam(name = "nodeNames",required = false) List<String> nodeNames
    @ApiModelProperty("nameDisplayMethod")
    private Integer nameDisplayMethod;

    @ApiModelProperty("nodeIds")
    private List<String> nodeIds;

    @ApiModelProperty("nodeNames")
    private List<String> nodeNames;

    public int getNameDisplayMethod() {
        return nameDisplayMethod;
    }

    public void setNameDisplayMethod(Integer nameDisplayMethod) {
        this.nameDisplayMethod = nameDisplayMethod;
    }

    public List<String> getNodeIds() {
        return nodeIds;
    }

    public void setNodeIds(List<String> nodeIds) {
        this.nodeIds = nodeIds;
    }

    public List<String> getNodeNames() {
        return nodeNames;
    }

    public void setNodeNames(List<String> nodeNames) {
        this.nodeNames = nodeNames;
    }
}
