package com.wellsoft.pt.multi.org.dto;

import com.google.common.collect.Lists;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.NotBlank;

import java.util.List;

/**
 * Description:
 * 职务序列树形结构
 * <pre>
 * 修改记录:
 * 修改后版本    修改人     修改日期    修改内容
 * 1.0         baozh    2021/10/20   Create
 * </pre>
 */
@ApiModel("职务序列树形结构")
public class OrgDutySeqTreeDto {

    @ApiModelProperty("uuid")
    protected String uuid;

    @ApiModelProperty("id")
    protected String id;

    @ApiModelProperty("职务序列编号")
    private String dutySeqCode;

    @NotBlank
    @ApiModelProperty("职务序列名称")
    private String dutySeqName;

    @ApiModelProperty("图标")
    private String icon;

    @ApiModelProperty("图标背景色")
    private String backgroundColor;

    @ApiModelProperty("子级类型(职务序列：Q,职级：R,职务：D)")
    private String childrenType;

    @ApiModelProperty("描述")
    private String describe;

    @ApiModelProperty("父类uuid")
    private String parentUuid;

    @ApiModelProperty("子序列列表")
    private List<OrgDutySeqTreeDto> childrens = Lists.newArrayList();

    @ApiModelProperty("序列层级")
    private int level = 0;

    @ApiModelProperty("开始行")
    private int startRow = 0;

    @ApiModelProperty("开始列")
    private int startCell = 1;

    @ApiModelProperty("跨列")
    private int cellRange;

    @ApiModelProperty("跨行")
    private int rowRange;

    @ApiModelProperty("子节点深度")
    private int childDepth;

    public List<OrgDutySeqTreeDto> getChildrens() {
        return childrens;
    }

    public void setChildrens(List<OrgDutySeqTreeDto> childrens) {
        this.childrens = childrens;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getDutySeqCode() {
        return dutySeqCode;
    }

    public void setDutySeqCode(String dutySeqCode) {
        this.dutySeqCode = dutySeqCode;
    }

    public String getDutySeqName() {
        return dutySeqName;
    }

    public void setDutySeqName(String dutySeqName) {
        this.dutySeqName = dutySeqName;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getChildrenType() {
        return childrenType;
    }

    public void setChildrenType(String childrenType) {
        this.childrenType = childrenType;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getParentUuid() {
        return parentUuid;
    }

    public void setParentUuid(String parentUuid) {
        this.parentUuid = parentUuid;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getCellRange() {
        return cellRange;
    }

    public void setCellRange(int cellRange) {
        this.cellRange = cellRange;
    }

    public int getRowRange() {
        return rowRange;
    }

    public void setRowRange(int rowRange) {
        this.rowRange = rowRange;
    }

    public int getChildDepth() {
        return childDepth;
    }

    public void setChildDepth(int childDepth) {
        this.childDepth = childDepth;
    }

    public int getStartRow() {
        return startRow;
    }

    public void setStartRow(int startRow) {
        this.startRow = startRow;
    }

    public int getStartCell() {
        return startCell;
    }

    public void setStartCell(int startCell) {
        this.startCell = startCell;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
