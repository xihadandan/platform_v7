package com.wellsoft.pt.multi.org.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 用户职位信息
 *
 * @author yt
 * @title: UserJob
 * @date 2020/6/17 3:22 下午
 */
public class UserJob implements Serializable {

    /**
     * 主职
     */
    private List<OrgElementVo> mainJobs;

    /**
     * 副职
     */
    private List<OrgElementVo> otherJobs;


    public List<OrgElementVo> getMainJobs() {
        return mainJobs;
    }

    public void setMainJobs(List<OrgElementVo> mainJobs) {
        this.mainJobs = mainJobs;
    }

    public List<OrgElementVo> getOtherJobs() {
        return otherJobs;
    }

    public void setOtherJobs(List<OrgElementVo> otherJobs) {
        this.otherJobs = otherJobs;
    }
}
