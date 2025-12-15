package com.wellsoft.pt.app.dingtalk.entity;

import com.wellsoft.context.jdbc.entity.TenantEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 钉钉部门信息表
 *
 * @author Well
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年5月29日.1	Well		2020年5月29日		Create
 * </pre>
 * @date 2020年5月29日
 */
@Entity
@Table(name = "MULTI_ORG_DING_DEPT")
@DynamicUpdate
@DynamicInsert
@Deprecated
public class MultiOrgDingDept extends TenantEntity {

    public static final int STATE_0 = 0;
    public static final int STATE_1 = 1;

    /**
     *
     */
    private static final long serialVersionUID = -5840565210396928827L;

    private String eleId; // 平台部门节点id
    private String id; // 钉钉部门id
    private String name; // 部门名称
    private String parentId; // 父id
    private String ext; // 扩展字段
    private int createDeptGroup; // 是否创建部门群
    private int autoAddUser; // 是否自动加入用户
    private String orgVersionId;

    public String getEleId() {
        return eleId;
    }

    public void setEleId(String eleId) {
        this.eleId = eleId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public int getCreateDeptGroup() {
        return createDeptGroup;
    }

    public void setCreateDeptGroup(int createDeptGroup) {
        this.createDeptGroup = createDeptGroup;
    }

    public int getAutoAddUser() {
        return autoAddUser;
    }

    public void setAutoAddUser(int autoAddUser) {
        this.autoAddUser = autoAddUser;
    }

    public String getOrgVersionId() {
        return orgVersionId;
    }

    public void setOrgVersionId(String orgVersionId) {
        this.orgVersionId = orgVersionId;
    }
}
