package com.wellsoft.pt.multi.org.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.NotBlank;

import java.util.List;

/**
 * Description:
 * 多组织职级
 * <pre>
 * 修改记录:
 * 修改后版本    修改人     修改日期    修改内容
 * 1.0         baozh    2021/10/26   Create
 * </pre>
 */
@ApiModel("职级")
public class MultiOrgJobRankVo {

    @ApiModelProperty("uuid")
    protected String uuid;
    @ApiModelProperty("职档")
    List<String> jobLevel;
    @ApiModelProperty("ID")
    private String id;
    // CODE
    @ApiModelProperty("编码")
    private String code;
    // NAME
    @NotBlank
    @ApiModelProperty("名称")
    private String name;
    // 归属的系统单位ID
    @ApiModelProperty("归属的系统单位ID")
    private String systemUnitId;
    @ApiModelProperty("职级")
    private String jobRank;
    @ApiModelProperty("描述")
    private String describe;
    @ApiModelProperty("职务序列uuid")
    private String dutySeqUuid;
    @ApiModelProperty("职等")
    private Integer jobGrade;

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

    public String getSystemUnitId() {
        return systemUnitId;
    }

    public void setSystemUnitId(String systemUnitId) {
        this.systemUnitId = systemUnitId;
    }

    public String getJobRank() {
        return jobRank;
    }

    public void setJobRank(String jobRank) {
        this.jobRank = jobRank;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getDutySeqUuid() {
        return dutySeqUuid;
    }

    public void setDutySeqUuid(String dutySeqUuid) {
        this.dutySeqUuid = dutySeqUuid;
    }

    public Integer getJobGrade() {
        return jobGrade;
    }

    public void setJobGrade(Integer jobGrade) {
        this.jobGrade = jobGrade;
    }

    public List<String> getJobLevel() {
        return jobLevel;
    }

    public void setJobLevel(List<String> jobLevel) {
        this.jobLevel = jobLevel;
    }
}
