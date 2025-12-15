package com.wellsoft.pt.org.entity;

import com.wellsoft.context.jdbc.entity.SysEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 业务组织节点实例下的成员
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2024年11月25日   chenq	 Create
 * </pre>
 */
@Entity
@Table(name = "BIZ_ORG_ELEMENT_MEMBER")
@DynamicUpdate
@DynamicInsert
public class BizOrgElementMemberEntity extends SysEntity {


    private static final long serialVersionUID = -5597563431400156709L;

    private Long bizOrgUuid;
    private String memberId;
    private String bizOrgElementId;
    private String bizOrgRoleId;


    public Long getBizOrgUuid() {
        return bizOrgUuid;
    }

    public void setBizOrgUuid(Long bizOrgUuid) {
        this.bizOrgUuid = bizOrgUuid;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getBizOrgElementId() {
        return bizOrgElementId;
    }

    public void setBizOrgElementId(String bizOrgElementId) {
        this.bizOrgElementId = bizOrgElementId;
    }


    public String getBizOrgRoleId() {
        return bizOrgRoleId;
    }

    public void setBizOrgRoleId(String bizOrgRoleId) {
        this.bizOrgRoleId = bizOrgRoleId;
    }
}
