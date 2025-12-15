package com.wellsoft.pt.multi.org.vo;

import io.swagger.annotations.ApiModelProperty;

/**
 * Description:
 * 工作职位职级关系
 * <pre>
 * 修改记录:
 * 修改后版本    修改人     修改日期    修改内容
 * 1.0         baozh    2021/10/27   Create
 * </pre>
 */
public class JobRankLevelAndDutySeqCodeVo extends JobRankLevelVo {

    @ApiModelProperty("职务序列编号")
    private String dutySeqCode;

    public String getDutySeqCode() {
        return this.dutySeqCode;
    }

    public void setDutySeqCode(String dutySeqCode) {
        this.dutySeqCode = dutySeqCode;
    }

}
