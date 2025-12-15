/*
 * @(#)2017年11月24日 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.bean;

import com.google.common.collect.Maps;
import com.wellsoft.context.component.tree.TreeNode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.springframework.util.CollectionUtils;

import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zyguo
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017年11月24日.1	zyguo		2017年11月24日		Create
 * </pre>
 * @date 2017年11月24日
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel("组织树节点")
public class OrgTreeNode extends TreeNode {
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 4165042013337965983L;
    @ApiModelProperty("版本ID")
    private String orgVersionId;
    @ApiModelProperty("系统单位ID")
    private String systemUnitId;
    @ApiModelProperty("名称全路径")
    private String namePath;

    /**
     * @return the orgVersionId
     */
    public String getOrgVersionId() {
        return orgVersionId;
    }

    /**
     * @param orgVersionId 要设置的orgVersionId
     */
    public void setOrgVersionId(String orgVersionId) {
        this.orgVersionId = orgVersionId;
    }

    /**
     * @return the systemUnitId
     */
    public String getSystemUnitId() {
        return systemUnitId;
    }

    /**
     * @param systemUnitId 要设置的systemUnitId
     */
    public void setSystemUnitId(String systemUnitId) {
        this.systemUnitId = systemUnitId;
    }

    /**
     * @return the namePath
     */
    public String getNamePath() {
        return namePath;
    }

    /**
     * @param namePath 要设置的namePath
     */
    public void setNamePath(String namePath) {
        this.namePath = namePath;
    }

    public Map<String, OrgTreeNode> toMapByNamePath() {
        Map<String, OrgTreeNode> map = Maps.newHashMap();
        if (StringUtils.isNotBlank(this.namePath)) {
            map.put(this.namePath, this);
        }
        if (!CollectionUtils.isEmpty(this.children)) {
            for (TreeNode subNode : children) {
                Map<String, OrgTreeNode> subMap = ((OrgTreeNode) subNode).toMapByNamePath();
                if (subMap != null && !subMap.isEmpty()) {
                    map.putAll(subMap);
                }
            }
        }
        return map;
    }

}
