package com.wellsoft.pt.app.dingtalk.vo;

/**
 * Description: 如何描述该类
 *
 * @author liuyz
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021/11/5.1	liuyz		2021/11/5		Create
 * </pre>
 * @date 2021/11/5
 */
@Deprecated
public class OaJobVo {
    private String jobId;
    private String jobIdPath;
    private String jobName;
    private String jobNamePath;
    private Integer isMain;

    public OaJobVo() {

    }

    public OaJobVo(String jobId, String jobIdPath, String jobName, String jobNamePath, Integer isMain) {
        this.jobId = jobId;
        this.jobIdPath = jobIdPath;
        this.jobName = jobName;
        this.jobNamePath = jobNamePath;
        this.isMain = isMain;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getJobIdPath() {
        return jobIdPath;
    }

    public void setJobIdPath(String jobIdPath) {
        this.jobIdPath = jobIdPath;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getJobNamePath() {
        return jobNamePath;
    }

    public void setJobNamePath(String jobNamePath) {
        this.jobNamePath = jobNamePath;
    }

    public Integer getIsMain() {
        return isMain;
    }

    public void setIsMain(Integer isMain) {
        this.isMain = isMain;
    }
}
