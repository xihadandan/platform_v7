package com.wellsoft.pt.org.dto;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2022年12月22日   chenq	 Create
 * </pre>
 */
public class OrgJobDutyDto implements Serializable {
    private static final long serialVersionUID = 7916556236306846089L;

    private String dutyId; // 职务ID
    private String dutyName; // 职务名称
    private List<OrgJobRankDto> jobRanks;// 职级

    public String getDutyId() {
        return this.dutyId;
    }

    public void setDutyId(final String dutyId) {
        this.dutyId = dutyId;
    }

    public String getDutyName() {
        return this.dutyName;
    }

    public void setDutyName(final String dutyName) {
        this.dutyName = dutyName;
    }

    public List<OrgJobRankDto> getJobRanks() {
        return this.jobRanks;
    }

    public void setJobRanks(final List<OrgJobRankDto> jobRanks) {
        this.jobRanks = jobRanks;
    }

    // 职级
    public static class OrgJobRankDto implements Serializable {
        private String id;

        @ApiModelProperty("名称")
        private String name;


        @ApiModelProperty("职级")
        private String jobRank;

        @ApiModelProperty("职务序列uuid")
        private String dutySeqUuid;

        @ApiModelProperty("职等")
        private Integer jobGrade;

        private List<String> levels;// 职档

        public String getId() {
            return this.id;
        }

        public void setId(final String id) {
            this.id = id;
        }

        public String getName() {
            return this.name;
        }

        public void setName(final String name) {
            this.name = name;
        }

        public String getJobRank() {
            return this.jobRank;
        }

        public void setJobRank(final String jobRank) {
            this.jobRank = jobRank;
        }

        public String getDutySeqUuid() {
            return this.dutySeqUuid;
        }

        public void setDutySeqUuid(final String dutySeqUuid) {
            this.dutySeqUuid = dutySeqUuid;
        }

        public Integer getJobGrade() {
            return this.jobGrade;
        }

        public void setJobGrade(final Integer jobGrade) {
            this.jobGrade = jobGrade;
        }

        public List<String> getLevels() {
            return this.levels;
        }

        public void setLevels(final List<String> levels) {
            this.levels = levels;
        }
    }

}
