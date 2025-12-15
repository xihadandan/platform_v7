package com.wellsoft.pt.org.entity;

import com.wellsoft.context.jdbc.entity.SysEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 组织群组
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年02月08日   chenq	 Create
 * </pre>
 */
@Entity
@Table(name = "ORG_GROUP_MEMBER")
@DynamicUpdate
@DynamicInsert
public class OrgGroupMemberEntity extends SysEntity {

    private static final long serialVersionUID = -1608716743652856247L;

    private String memberId;

    private String memberIdPath;

    private Long groupUuid;

    private Integer seq;

    public String getMemberId() {
        return this.memberId;
    }

    public void setMemberId(final String memberId) {
        this.memberId = memberId;
    }

    /**
     * @return the memberIdPath
     */
    public String getMemberIdPath() {
        return memberIdPath;
    }

    /**
     * @param memberIdPath 要设置的memberIdPath
     */
    public void setMemberIdPath(String memberIdPath) {
        this.memberIdPath = memberIdPath;
    }

    public Long getGroupUuid() {
        return this.groupUuid;
    }

    public void setGroupUuid(final Long groupUuid) {
        this.groupUuid = groupUuid;
    }

    public Integer getSeq() {
        return seq;
    }

    public void setSeq(Integer seq) {
        this.seq = seq;
    }
}
