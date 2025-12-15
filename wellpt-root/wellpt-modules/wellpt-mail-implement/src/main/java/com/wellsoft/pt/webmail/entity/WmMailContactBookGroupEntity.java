package com.wellsoft.pt.webmail.entity;

import com.wellsoft.context.jdbc.entity.TenantEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 邮件个人通讯录分组
 *
 * @author chenq
 * @date 2018/6/6
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/6/6    chenq		2018/6/6		Create
 * </pre>
 */
@Entity
@Table(name = "UF_PT_MAIL_CONTACT_BOOK_GRP")
@DynamicInsert
@DynamicUpdate
public class WmMailContactBookGroupEntity extends TenantEntity {

    private static final long serialVersionUID = 740360252716776820L;

    /**
     * 分组名称
     */
    private String groupName;

    /**
     * 分组Id
     */
    private String groupId;

    /**
     * 获取 分组名称
     *
     * @return
     */
    public String getGroupName() {
        return groupName;
    }

    /**
     * 设置 分组名称
     *
     * @param groupName
     */
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    /**
     * 获取 分组Id
     *
     * @return
     */
    public String getGroupId() {
        return groupId;
    }

    /**
     * 设置 分组Id
     *
     * @param groupId
     */
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
