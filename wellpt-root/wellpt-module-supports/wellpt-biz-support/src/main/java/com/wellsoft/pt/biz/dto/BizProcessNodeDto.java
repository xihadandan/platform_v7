/*
 * @(#)10/12/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.dto;

import com.wellsoft.context.base.BaseObject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * Description: 业务过程结点传输对象
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 10/12/22.1	zhulh		10/12/22		Create
 * </pre>
 * @date 10/12/22
 */
@ApiModel("业务过程节点传输对象")
public class BizProcessNodeDto extends BaseObject {
    private static final long serialVersionUID = 612492286288278907L;

    @ApiModelProperty("过程节点名称")
    private String name;

    @ApiModelProperty("过程节点ID")
    private String id;

    @ApiModelProperty("子过程节点")
    private List<BizProcessNodeDto> nodes;

    @ApiModelProperty("业务事项")
    private List<BizProcessItemDto> items;

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name 要设置的name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id 要设置的id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the nodes
     */
    public List<BizProcessNodeDto> getNodes() {
        return nodes;
    }

    /**
     * @param nodes 要设置的nodes
     */
    public void setNodes(List<BizProcessNodeDto> nodes) {
        this.nodes = nodes;
    }

    /**
     * @return the items
     */
    public List<BizProcessItemDto> getItems() {
        return items;
    }

    /**
     * @param items 要设置的items
     */
    public void setItems(List<BizProcessItemDto> items) {
        this.items = items;
    }
}
