package com.wellsoft.pt.org.entity;

import com.wellsoft.context.jdbc.entity.SysEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 组织单元实例管理信息
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2022年11月09日   chenq	 Create
 * </pre>
 */
@Entity
@Table(name = "ORG_ELEMENT_MANAGEMENT")
@DynamicUpdate
@DynamicInsert
public class OrgElementManagementEntity extends SysEntity {


    private static final long serialVersionUID = 6205621234562671906L;

    private Long orgVersionUuid;
    private String orgElementId;
    private Long orgElementUuid;
    private String director;// 负责人
    private String leader;// 分管领导
    private String orgManager; //组织管理员
    private String orgAuthority;// 组织架构管理权限
    private String userAuthority;// 用户管理权限
    private Boolean enableAuthority;

    public Long getOrgVersionUuid() {
        return this.orgVersionUuid;
    }

    public void setOrgVersionUuid(final Long orgVersionUuid) {
        this.orgVersionUuid = orgVersionUuid;
    }

    public String getOrgElementId() {
        return this.orgElementId;
    }

    public void setOrgElementId(final String orgElementId) {
        this.orgElementId = orgElementId;
    }

    public Long getOrgElementUuid() {
        return this.orgElementUuid;
    }

    public void setOrgElementUuid(final Long orgElementUuid) {
        this.orgElementUuid = orgElementUuid;
    }

    public String getDirector() {
        return this.director;
    }

    public void setDirector(final String director) {
        this.director = director;
    }

    public String getLeader() {
        return this.leader;
    }

    public void setLeader(final String leader) {
        this.leader = leader;
    }

    public String getOrgManager() {
        return this.orgManager;
    }

    public void setOrgManager(final String orgManager) {
        this.orgManager = orgManager;
    }

    public String getOrgAuthority() {
        return this.orgAuthority;
    }

    public void setOrgAuthority(final String orgAuthority) {
        this.orgAuthority = orgAuthority;
    }

    public String getUserAuthority() {
        return this.userAuthority;
    }

    public void setUserAuthority(final String userAuthority) {
        this.userAuthority = userAuthority;
    }

    public Boolean getEnableAuthority() {
        return enableAuthority;
    }

    public void setEnableAuthority(Boolean enableAuthority) {
        this.enableAuthority = enableAuthority;
    }

    public enum LeaderType {
        DIRECTOR, // 负责人
        LEADER, // 分管领导
        MANAGER; // 管理员
    }
}
