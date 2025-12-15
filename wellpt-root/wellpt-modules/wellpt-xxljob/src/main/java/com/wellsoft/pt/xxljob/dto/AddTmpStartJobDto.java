package com.wellsoft.pt.xxljob.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

/**
 * Description: 临时任务-添加并启动临时任务对象
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	        修改人		修改日期			修改内容
 * 2021/10/20.1	    zenghw		2021/10/20		    Create
 * </pre>
 * @date 2021/10/20
 */
@ApiModel(value = "临时任务-添加并启动临时任务对象")
public class AddTmpStartJobDto implements Serializable {
    private static final long serialVersionUID = 1577386050110622510L;

    @ApiModelProperty(value = "用户userId", required = true)
    private String userId;

    @ApiModelProperty(value = "业务参数", required = false)
    private String businessKey;

    @ApiModelProperty(value = "业务值", required = false)
    private String businessValue;

    @ApiModelProperty(value = "任务描述", required = true)
    private String jobDesc;

    @ApiModelProperty(value = "临时任务名", required = true)
    private String tempJobName;

    @ApiModelProperty(value = "执行的时间列表 时间格式支持：yyyy-MM-dd HH:mm:ss", required = true)
    private List<String> workingTimeList;

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(final String userId) {
        this.userId = userId;
    }

    public String getBusinessKey() {
        return this.businessKey;
    }

    public void setBusinessKey(final String businessKey) {
        this.businessKey = businessKey;
    }

    public String getBusinessValue() {
        return this.businessValue;
    }

    public void setBusinessValue(final String businessValue) {
        this.businessValue = businessValue;
    }

    public String getJobDesc() {
        return this.jobDesc;
    }

    public void setJobDesc(final String jobDesc) {
        this.jobDesc = jobDesc;
    }

    public String getTempJobName() {
        return this.tempJobName;
    }

    public void setTempJobName(final String tempJobName) {
        this.tempJobName = tempJobName;
    }

    public List<String> getWorkingTimeList() {
        return this.workingTimeList;
    }

    public void setWorkingTimeList(final List<String> workingTimeList) {
        this.workingTimeList = workingTimeList;
    }
}
