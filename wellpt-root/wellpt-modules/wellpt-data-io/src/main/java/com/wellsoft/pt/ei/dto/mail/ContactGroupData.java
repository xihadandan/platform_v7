package com.wellsoft.pt.ei.dto.mail;

import com.wellsoft.pt.ei.annotate.FieldType;

import java.io.Serializable;

/**
 * @Auther: yt
 * @Date: 2021/9/28 19:07
 * @Description:
 */
public class ContactGroupData implements Serializable {
    private static final long serialVersionUID = 6250562415645817310L;

    @FieldType(desc = "主键uuid", required = true)
    private String uuid;
    @FieldType(desc = "用户ID", required = true)
    private String userId;
    @FieldType(desc = "分组名称", required = true)
    private String groupName;
    @FieldType(desc = "分组Id")
    private String groupId;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
