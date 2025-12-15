package com.wellsoft.pt.bm.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * Description: 咨询投诉类
 *
 * @author wangbx
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-12-5.1	wangbx		2013-12-5		Create
 * </pre>
 * @date 2013-12-5
 */
@Entity
@Table(name = "bm_advisory_complaints")
@DynamicUpdate
@DynamicInsert
public class AdvisoryComplaints extends IdEntity {

    //唯一标识号
    private String uuid;
    //提交的内容
    private String body;
    //提交标题
    private String title;
    //类型标识(1:咨询 2：投诉)
    private String type;
    //提交时间
    private Date submitTime;
    //提交人姓名
    private String submitUserName;
    //联系电话
    private String tel;
    //通讯地址
    private String address;
    //邮箱
    private String email;
    //提交人单位
    private String unit;
    //是否已回复 (0：未回复 1：已回复)
    private Boolean isReply;
    //回复时间
    private Date replyTime;
    //提交的单位/部门的id
    private String replyId;
    //回复内容
    private String replyMsg;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(Date submitTime) {
        this.submitTime = submitTime;
    }

    public String getSubmitUserName() {
        return submitUserName;
    }

    public void setSubmitUserName(String submitUserName) {
        this.submitUserName = submitUserName;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Boolean getIsReply() {
        return isReply;
    }

    public void setIsReply(Boolean isReply) {
        this.isReply = isReply;
    }

    public Date getReplyTime() {
        return replyTime;
    }

    public void setReplyTime(Date replyTime) {
        this.replyTime = replyTime;
    }

    public String getReplyId() {
        return replyId;
    }

    public void setReplyId(String replyId) {
        this.replyId = replyId;
    }

    public String getReplyMsg() {
        return replyMsg;
    }

    public void setReplyMsg(String replyMsg) {
        this.replyMsg = replyMsg;
    }

}
