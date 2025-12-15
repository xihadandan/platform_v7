package com.wellsoft.pt.org.entity;


import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

//@Entity
//@Table(name = "ORG_JOB_PRIVILEGE")
@Deprecated
public class JobPrivilege implements Serializable {


    /**
     *
     */
    private static final long serialVersionUID = 5825368790803162944L;

    private JobPrivilegeId jobPrivilegeId;

    @EmbeddedId
    public JobPrivilegeId getJobPrivilegeId() {
        return jobPrivilegeId;
    }

    public void setJobPrivilegeId(JobPrivilegeId jobPrivilegeId) {
        this.jobPrivilegeId = jobPrivilegeId;
    }


}
