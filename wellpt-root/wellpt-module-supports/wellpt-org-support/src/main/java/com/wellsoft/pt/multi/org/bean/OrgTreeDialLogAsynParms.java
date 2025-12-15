package com.wellsoft.pt.multi.org.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author yt
 * @title: OrgTreeDialLogAsynParms
 * @date 2020/6/9 1:59 下午
 */
@ApiModel(value = "组织树弹出框民步参数对象")
public class OrgTreeDialLogAsynParms extends OrgTreeDialogParams {

    /**
     * 根节点Id 全部单位使用，用于区分不同的集团单位（存在多个集团单位都使用了同一个系统单位（正常情况不存在这样的场景，但是产品说需要能正常显示，详见禅道bug 61775））
     */
    @ApiModelProperty("根节点Id 区分不同集团单位")
    private String rootId;

    /**
     * 节点Id 查询改节点下子节点
     */
    @ApiModelProperty("节点Id 查询改节点下子节点")
    private String treeNodeId;

    /**
     * 需要排除的选项
     */
    @ApiModelProperty("需要排除的选项")
    private List<String> excludeValues;

    /**
     * 搜索关键字
     */
    @ApiModelProperty("搜索关键字")
    private String keyword;

    /**
     * 需要检查 是否需要勾选的 节点Id(包含用户Id)
     */
    @ApiModelProperty(" 需要检查 是否需要勾选的 节点Id(包含用户Id)")
    private List<String> checkedIds;

    /**
     * 组织节点显示名称：0节点名称、1智能全路径、2节点简称
     */
    @ApiModelProperty("组织节点显示名称：0节点名称、1智能全路径、2节点简称")
    private int nameDisplayMethod;

    /**
     * 排序字段
     */
    @ApiModelProperty("排序字段")
    private String sort;

    public int getNameDisplayMethod() {
        return nameDisplayMethod;
    }

    public void setNameDisplayMethod(int nameDisplayMethod) {
        this.nameDisplayMethod = nameDisplayMethod;
    }

    public String getRootId() {
        return rootId;
    }

    public void setRootId(String rootId) {
        this.rootId = rootId;
    }

    public String getTreeNodeId() {
        return treeNodeId;
    }

    public void setTreeNodeId(String treeNodeId) {
        this.treeNodeId = treeNodeId;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public List<String> getCheckedIds() {
        return checkedIds;
    }

    public void setCheckedIds(List<String> checkedIds) {
        this.checkedIds = checkedIds;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public List<String> getExcludeValues() {
        return excludeValues;
    }

    public void setExcludeValues(List<String> excludeValues) {
        this.excludeValues = excludeValues;
    }
}
