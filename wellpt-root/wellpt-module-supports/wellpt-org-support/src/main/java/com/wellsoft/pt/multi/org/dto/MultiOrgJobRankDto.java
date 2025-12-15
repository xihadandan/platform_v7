package com.wellsoft.pt.multi.org.dto;

import com.wellsoft.pt.multi.org.entity.MultiOrgJobRank;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * Description:
 * 职级返回数据
 * <pre>
 * 修改记录:
 * 修改后版本    修改人     修改日期    修改内容
 * 1.0         baozh    2021/10/26   Create
 * </pre>
 */
@ApiModel("职级返回数据")
public class MultiOrgJobRankDto extends MultiOrgJobRank {

    @ApiModelProperty("职档列表")
    private List<String> jobLevel;

    @ApiModelProperty("职务序列名称")
    private String dutySeqName;

    @ApiModelProperty("职等名称")
    private String jobGradeName;

    public List<String> getJobLevel() {
        return jobLevel;
    }

    public void setJobLevel(List<String> jobLevel) {
        this.jobLevel = jobLevel;
    }

    public String getDutySeqName() {
        return dutySeqName;
    }

    public void setDutySeqName(String dutySeqName) {
        this.dutySeqName = dutySeqName;
    }

    public String getJobGradeName() {
        return jobGradeName;
    }

    public void setJobGradeName(String jobGradeName) {
        this.jobGradeName = jobGradeName;
    }
}
