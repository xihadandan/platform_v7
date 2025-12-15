package com.wellsoft.pt.ei.bo;

import com.wellsoft.context.jdbc.support.BaseQueryItem;

import java.util.Date;
import java.util.List;

/**
 * Description:
 * es全文检索实现
 * <pre>
 * 修改记录:
 * 修改后版本    修改人     修改日期    修改内容
 * 1.0         baozh    2021/9/26   Create
 * </pre>
 */
public class DataRecordDetailBo implements BaseQueryItem {

    private String taskUuid;

    private Integer taskStatus;

    private String name;

    private String parentName;

    private Long count;

    private Date finishTime;

    private String usedTime;

    private String errorMessage;

    private List<DataRecordDetailBo> childDetails;

    public String getTaskUuid() {
        return taskUuid;
    }

    public void setTaskUuid(String taskUuid) {
        this.taskUuid = taskUuid;
    }

    public Integer getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(Integer taskStatus) {
        this.taskStatus = taskStatus;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public Date getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
    }

    public String getUsedTime() {
        return usedTime;
    }

    public void setUsedTime(String usedTime) {
        this.usedTime = usedTime;
    }

    public List<DataRecordDetailBo> getChildDetails() {
        return childDetails;
    }

    public void setChildDetails(List<DataRecordDetailBo> childDetails) {
        this.childDetails = childDetails;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
