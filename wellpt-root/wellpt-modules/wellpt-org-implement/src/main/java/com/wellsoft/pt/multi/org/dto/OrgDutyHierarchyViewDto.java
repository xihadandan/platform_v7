package com.wellsoft.pt.multi.org.dto;


import com.wellsoft.pt.multi.org.entity.OrgJobGradeEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * Description:
 * 职务体系视图
 * <pre>
 * 修改记录:
 * 修改后版本    修改人     修改日期    修改内容
 * 1.0         baozh    2021/10/20   Create
 * </pre>
 */
@ApiModel("职务体系视图")
public class OrgDutyHierarchyViewDto {

    @ApiModelProperty("视图头部信息")
    private List<OrgDutySeqTreeDto> orgDutyHeaderDto;

    @ApiModelProperty("职务职级信息")
    private List<OrgDutyBodyDto> orgDutyBodyDto;

    @ApiModelProperty("职等列表")
    private List<OrgJobGradeEntity> jobGrade;

    @ApiModelProperty("字节点深度")
    private int childDepth;

    public List<OrgDutySeqTreeDto> getOrgDutyHeaderDto() {
        return orgDutyHeaderDto;
    }

    public void setOrgDutyHeaderDto(List<OrgDutySeqTreeDto> orgDutyHeaderDto) {
        this.orgDutyHeaderDto = orgDutyHeaderDto;
    }

    public List<OrgDutyBodyDto> getOrgDutyBodyDto() {
        return orgDutyBodyDto;
    }

    public void setOrgDutyBodyDto(List<OrgDutyBodyDto> orgDutyBodyDto) {
        this.orgDutyBodyDto = orgDutyBodyDto;
    }

    public List<OrgJobGradeEntity> getJobGrade() {
        return jobGrade;
    }

    public void setJobGrade(List<OrgJobGradeEntity> jobGrade) {
        this.jobGrade = jobGrade;
    }

    public int getChildDepth() {
        return childDepth;
    }

    public void setChildDepth(int childDepth) {
        this.childDepth = childDepth;
    }
}
