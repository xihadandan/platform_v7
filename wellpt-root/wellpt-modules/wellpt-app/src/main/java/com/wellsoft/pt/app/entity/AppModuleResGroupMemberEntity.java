package com.wellsoft.pt.app.entity;

import com.wellsoft.context.jdbc.entity.Entity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Table;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年07月06日   chenq	 Create
 * </pre>
 */
@Table(name = "APP_MODULE_RES_GROUP_MEMBER")
@DynamicInsert
@DynamicUpdate
@javax.persistence.Entity
public class AppModuleResGroupMemberEntity extends Entity {

    private Long groupUuid;

    private Long memberUuid;

    private String type;

    public Long getGroupUuid() {
        return groupUuid;
    }

    public void setGroupUuid(Long groupUuid) {
        this.groupUuid = groupUuid;
    }

    public Long getMemberUuid() {
        return memberUuid;
    }

    public void setMemberUuid(Long memberUuid) {
        this.memberUuid = memberUuid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
