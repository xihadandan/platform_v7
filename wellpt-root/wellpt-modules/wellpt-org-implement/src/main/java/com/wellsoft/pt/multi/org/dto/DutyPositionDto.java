package com.wellsoft.pt.multi.org.dto;

import java.util.HashSet;
import java.util.Set;

/**
 * Description:
 * 职务位置
 * <pre>
 * 修改记录:
 * 修改后版本    修改人     修改日期    修改内容
 * 1.0         baozh    2021/10/29   Create
 * </pre>
 */
public class DutyPositionDto {

    private Integer grade;

    private String dutySeqUuid;

    private Set<String> jobRank = new HashSet<>();

    private Set<String> dutyList = new HashSet<>();

    public DutyPositionDto(Integer grade, String dutySeqUuid) {
        this.grade = grade;
        this.dutySeqUuid = dutySeqUuid;
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    public String getDutySeqUuid() {
        return dutySeqUuid;
    }

    public void setDutySeqUuid(String dutySeqUuid) {
        this.dutySeqUuid = dutySeqUuid;
    }

    public Set<String> getJobRank() {
        return jobRank;
    }

    public void setJobRank(Set<String> jobRank) {
        this.jobRank = jobRank;
    }

    public Set<String> getDutyList() {
        return dutyList;
    }

    public void setDutyList(Set<String> dutyList) {
        this.dutyList = dutyList;
    }
}
