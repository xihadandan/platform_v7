package com.wellsoft.pt.org.dto;

import com.wellsoft.pt.org.entity.OrgElementI18nEntity;
import com.wellsoft.pt.org.entity.OrgGroupEntity;
import org.apache.commons.compress.utils.Lists;

import java.io.Serializable;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年02月08日   chenq	 Create
 * </pre>
 */
public class OrgGroupDto extends OrgGroupEntity implements Serializable {

    private List<OrgElementI18nEntity> i18ns;

    private List<String> owner = Lists.newArrayList(); // 使用者

    private List<String> member = Lists.newArrayList(); // 成员

    private List<String> memberPath = Lists.newArrayList(); // 成员路径

    private List<String> roleUuids = Lists.newArrayList(); // 角色

    public List<String> getOwner() {
        return this.owner;
    }

    public void setOwner(final List<String> owner) {
        this.owner = owner;
    }

    public List<String> getMember() {
        return this.member;
    }

    public void setMember(final List<String> member) {
        this.member = member;
    }

    /**
     * @return the memberPath
     */
    public List<String> getMemberPath() {
        return memberPath;
    }

    /**
     * @param memberPath 要设置的memberPath
     */
    public void setMemberPath(List<String> memberPath) {
        this.memberPath = memberPath;
    }

    public List<String> getRoleUuids() {
        return this.roleUuids;
    }

    public void setRoleUuids(final List<String> roleUuids) {
        this.roleUuids = roleUuids;
    }

    public List<OrgElementI18nEntity> getI18ns() {
        return i18ns;
    }

    public void setI18ns(List<OrgElementI18nEntity> i18ns) {
        this.i18ns = i18ns;
    }
}
