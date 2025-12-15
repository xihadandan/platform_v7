package com.wellsoft.pt.message.support;

import com.wellsoft.context.jdbc.entity.IdEntity;

/**
 * Description: 消息额外传参类
 *
 * @author tony
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2014-10-16.1	tony		2014-10-16		Create
 * </pre>
 * @date 2014-10-16
 */
public class MessageExtraParm extends IdEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    private String sender;//消息发送者
    private String systemid;//发送方系统id
    private String relatedUrl;//相关源url
    private String messageid;//暂定在線消息uuid 使用

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getSystemid() {
        return systemid;
    }

    public void setSystemid(String systemid) {
        this.systemid = systemid;
    }

    public String getRelatedUrl() {
        return relatedUrl;
    }

    public void setRelatedUrl(String relatedUrl) {
        this.relatedUrl = relatedUrl;
    }

    public String getMessageid() {
        return messageid;
    }

    public void setMessageid(String messageid) {
        this.messageid = messageid;
    }

}
