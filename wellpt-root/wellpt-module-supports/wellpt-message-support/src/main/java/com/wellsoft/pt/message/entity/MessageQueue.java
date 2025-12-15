package com.wellsoft.pt.message.entity;

import com.wellsoft.context.exception.WorkFlowException;
import com.wellsoft.context.jdbc.entity.TenantEntity;
import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.sql.Blob;
import java.util.Date;

/**
 * Description: 待发消息表
 *
 * @author linz
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-12-31	linz		2015-12-31		Create
 * </pre>
 * @date 2015-12-31 10:01:17
 */

@Entity
@Table(name = "MSG_MESSAGE_QUEUE")
@DynamicUpdate
@DynamicInsert
public class MessageQueue extends TenantEntity {

    /**
     *
     */
    private static final long serialVersionUID = -7627403675974785407L;

    /**
     * 消息模板名称
     */
    private String name;

    /**
     * 消息模板ID
     */
    private String templateId;

    /**
     * 编号
     */
    private String code;

    /**
     * 发送时间，立即发送、工作时间发送要根据该字段过滤数据
     */
    private Date sentTime;

    /**
     * 消息回复时的相关联消息UUID
     */
    private String correlationUuid;

    /**
     * Message的对象字节流
     */
    private Blob message;

    private String system;

    private String tenant;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Date getSentTime() {
        return sentTime;
    }

    public void setSentTime(Date sentTime) {
        this.sentTime = sentTime;
    }

    public String getCorrelationUuid() {
        return correlationUuid;
    }

    public void setCorrelationUuid(String correlationUuid) {
        this.correlationUuid = correlationUuid;
    }

    @JsonIgnore
    public Blob getMessage() {
        return message;
    }

    public void setMessage(Blob message) {
        this.message = message;
    }

    /**
     * 获取消息内容
     *
     * @return
     */
    @Transient
    public String getMessageAsString() {
        try {
            return IOUtils.toString(getMessage().getBinaryStream());
        } catch (Exception e) {
            throw new WorkFlowException(e);
        }
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }
}
