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
public class DingJobVo {
    private String deptId;
    private String deptName;
    private String jobName;
    private Integer isMain;

    public DingJobVo() {

    }

    public DingJobVo(String deptId, String deptName, String jobName, Integer isMain) {
        this.deptId = deptId;
        this.deptName = deptName;
        this.jobName = jobName;
        this.isMain = isMain;
    }

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public Integer getIsMain() {
        return isMain;
    }

    public void setIsMain(Integer isMain) {
        this.isMain = isMain;
    }

}
