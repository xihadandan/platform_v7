package com.wellsoft.pt.webmail.entity;

import com.wellsoft.context.jdbc.entity.TenantEntity;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 用户邮箱容量
 *
 * @author chenq
 * @date 2019/7/17
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/7/17    chenq		2019/7/17		Create
 * </pre>
 */
@Entity
@Table(name = "WM_MAIL_USE_CAPACITY")
public class WmMailUseCapacityEntity extends TenantEntity {
    private static final long serialVersionUID = -7610998594545907055L;

    private String userId;

    private String mailbox;

    private Long capacityUsed;//已用容量统计


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMailbox() {
        return mailbox;
    }

    public void setMailbox(String mailbox) {
        this.mailbox = mailbox;
    }

    public Long getCapacityUsed() {
        return capacityUsed;
    }

    public void setCapacityUsed(Long capacityUsed) {
        this.capacityUsed = capacityUsed;
    }
}
