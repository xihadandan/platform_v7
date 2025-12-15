package com.wellsoft.pt.message.entity;

import com.wellsoft.context.jdbc.entity.TenantEntity;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Blob;
import java.util.Date;

/**
 * Description: 定时消息队列
 *
 * @author chenq
 * @date 2018/7/13
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/7/13    chenq		2018/7/13		Create
 * </pre>
 */
@Entity
@Table(name = "MSG_SCHEDULE_MESSAGE_QUEUE_HIS")
@DynamicUpdate
@DynamicInsert
public class ScheduleMessageQueueHisEntity extends TenantEntity {
    private static final long serialVersionUID = -740984334289211641L;

    @JsonIgnore
    private Blob message;

    private String templateId;

    private Date sendTime;

    private String name;

    public Blob getMessage() {
        return message;
    }

    public void setMessage(Blob message) {
        this.message = message;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
