package com.wellsoft.pt.message.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * Description: 如何描述该类
 *
 * @author wbx
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-10-16.1	wbx		2013-10-16		Create
 * </pre>
 * @date 2013-10-16
 */
@Entity
@Table(name = "msg_short_message_not_send")
@DynamicUpdate
@DynamicInsert
public class ShortMessageNotSend extends IdEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 8152148500343807612L;

    /**
     * 发送人名称
     */
    private String senderName;
    /**
     * 接收人名称
     */
    private String recipientName;
    /**
     * 内容
     */
    private String body;
    /**
     * 发送时间
     */
    private Date sendTime;

    /**
     * 回执时间
     */
    private Date receivedTime;
    /**
     * 发送人手机号码
     */
    private String sendMobilePhone;
    /**
     * 接收人手机号码
     */
    private String recipientMobilePhone;
    /**
     * 是否已阅  未使用
     */
    private Boolean isread;
    /**
     * 短信ID(编号)，确保唯一后可以用来找到对应的回执
     */
    private Long smid;

    /**
     * 消息类型(0 modem; 1 mas机; 2 smas机)
     */
    private Integer type;
    /**
     * 发送人ID
     */
    private String send;
    /**
     * 接收人ID
     */
    private String received;

    //新添属性
    /**
     * 回执编码
     */
    private Integer recId;
    /**
     * 回执描述
     */
    private String recMsg;
    /**
     * 是否要重发:1需要重发,即发送失败   0不需要重发,即发送成功
     */
    private Boolean isReSend;
    /**
     * 发送状态 0已发送待回执 1发送成功 2发送失败 3重发成功
     */
    private Integer sendStatus;

    /**
     * 预留字段1 (流程实例)
     */
    private String reservedText1;
    /**
     * 预留字段2 (环节实例)
     */
    private String reservedText2;
    /**
     * 预留字段3 (actionType)
     */
    private String reservedText3;
    /**
     * 预留字段4 (项目短编号)
     */
    private String reservedText4;
    /**
     * 预留字段5 (项目名称)
     */
    private String reservedText5;
    /**
     * 预留字段6 (事项名称)
     */
    private String reservedText6;

    /**
     * @return the senderName
     */
    public String getSenderName() {
        return senderName;
    }

    /**
     * @param senderName 要设置的senderName
     */
    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    /**
     * @return the recipientName
     */
    public String getRecipientName() {
        return recipientName;
    }

    /**
     * @param recipientName 要设置的recipientName
     */
    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    /**
     * @return the body
     */
    public String getBody() {
        return body;
    }

    /**
     * @param body 要设置的body
     */
    public void setBody(String body) {
        this.body = body;
    }

    /**
     * @return the receivedTime
     */
    public Date getReceivedTime() {
        return receivedTime;
    }

    /**
     * @param receivedTime 要设置的receivedTime
     */
    public void setReceivedTime(Date receivedTime) {
        this.receivedTime = receivedTime;
    }

    /**
     * @return the sendMobilePhone
     */
    public String getSendMobilePhone() {
        return sendMobilePhone;
    }

    /**
     * @param sendMobilePhone 要设置的sendMobilePhone
     */
    public void setSendMobilePhone(String sendMobilePhone) {
        this.sendMobilePhone = sendMobilePhone;
    }

    /**
     * @return the recipientMobilePhone
     */
    public String getRecipientMobilePhone() {
        return recipientMobilePhone;
    }

    /**
     * @param recipientMobilePhone 要设置的recipientMobilePhone
     */
    public void setRecipientMobilePhone(String recipientMobilePhone) {
        this.recipientMobilePhone = recipientMobilePhone;
    }

    /**
     * @return the isread
     */
    public Boolean getIsread() {
        return isread;
    }

    /**
     * @param isread 要设置的isread
     */
    public void setIsread(Boolean isread) {
        this.isread = isread;
    }

    /**
     * @return the sendTime
     */
    public Date getSendTime() {
        return sendTime;
    }

    /**
     * @param sendTime 要设置的sendTime
     */
    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    /**
     * @return the smid
     */
    public Long getSmid() {
        return smid;
    }

    /**
     * @param smid 要设置的smid
     */
    public void setSmid(Long smid) {
        this.smid = smid;
    }

    /**
     * @return the type
     */
    public Integer getType() {
        return type;
    }

    /**
     * @param type 要设置的type
     */
    public void setType(Integer type) {
        this.type = type;
    }

    /**
     * @return the send
     */
    public String getSend() {
        return send;
    }

    /**
     * @param send 要设置的send
     */
    public void setSend(String send) {
        this.send = send;
    }

    /**
     * @return the received
     */
    public String getReceived() {
        return received;
    }

    /**
     * @param received 要设置的received
     */
    public void setReceived(String received) {
        this.received = received;
    }

    public Integer getRecId() {
        return recId;
    }

    public void setRecId(Integer recId) {
        this.recId = recId;
    }

    public String getRecMsg() {
        return recMsg;
    }

    public void setRecMsg(String recMsg) {
        this.recMsg = recMsg;
    }

    public Integer getSendStatus() {
        return sendStatus;
    }

    public void setSendStatus(Integer sendStatus) {
        this.sendStatus = sendStatus;
    }

    public Boolean getIsReSend() {
        return isReSend;
    }

    public void setIsReSend(Boolean isReSend) {
        this.isReSend = isReSend;
    }

    /**
     * @return the reservedText1
     */
    public String getReservedText1() {
        return reservedText1;
    }

    /**
     * @param reservedText1 要设置的reservedText1
     */
    public void setReservedText1(String reservedText1) {
        this.reservedText1 = reservedText1;
    }

    /**
     * @return the reservedText2
     */
    public String getReservedText2() {
        return reservedText2;
    }

    /**
     * @param reservedText2 要设置的reservedText2
     */
    public void setReservedText2(String reservedText2) {
        this.reservedText2 = reservedText2;
    }

    /**
     * @return the reservedText3
     */
    public String getReservedText3() {
        return reservedText3;
    }

    /**
     * @param reservedText3 要设置的reservedText3
     */
    public void setReservedText3(String reservedText3) {
        this.reservedText3 = reservedText3;
    }

    public String getReservedText4() {
        return reservedText4;
    }

    public void setReservedText4(String reservedText4) {
        this.reservedText4 = reservedText4;
    }

    public String getReservedText5() {
        return reservedText5;
    }

    public void setReservedText5(String reservedText5) {
        this.reservedText5 = reservedText5;
    }

    public String getReservedText6() {
        return reservedText6;
    }

    public void setReservedText6(String reservedText6) {
        this.reservedText6 = reservedText6;
    }

}
