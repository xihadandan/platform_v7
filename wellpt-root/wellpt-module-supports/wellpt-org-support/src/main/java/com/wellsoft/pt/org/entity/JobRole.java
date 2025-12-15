package com.wellsoft.pt.org.entity;


import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

//@Entity
//@Table(name = "ORG_JOB_ROLE")
@Deprecated
public class JobRole implements Serializable {


    /**
     *
     */
    private static final long serialVersionUID = 5188472018844428952L;

    private JobRoleId jobRoleId;

    @EmbeddedId
    public JobRoleId getJobRoleId() {
        return jobRoleId;
    }

    public void setJobRoleId(JobRoleId jobRoleId) {
        this.jobRoleId = jobRoleId;
    }


}
