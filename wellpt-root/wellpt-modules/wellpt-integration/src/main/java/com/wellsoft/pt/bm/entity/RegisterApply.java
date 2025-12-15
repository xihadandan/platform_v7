package com.wellsoft.pt.bm.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * Description: 如何描述该类
 *
 * @author wangbx
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-12-6.1	wangbx		2013-12-6		Create
 * </pre>
 * @date 2013-12-6
 */
@Entity
@Table(name = "bm_register_apply")
@DynamicUpdate
@DynamicInsert
public class RegisterApply extends IdEntity {

    private String uuid;
    //注册号
    private String zch;
    //名称
    private String mc;
    //法人姓名
    private String frxm;
    //对应身份证号码
    private String frid;
    //手机
    private String mp;
    //邮件
    private String email;
    //提交时间
    private Date submitTime;
    //审核时间
    private Date verifyTime;
    //是否已审核 0：待审核   1：审核通过   -1:审核不通过
    private Integer isVerify;
    //审核部门Id
    private String verifyId;
    //回复信息
    private String replyMsg;

    public String getZch() {
        return zch;
    }

    public void setZch(String zch) {
        this.zch = zch;
    }

    public String getMc() {
        return mc;
    }

    public void setMc(String mc) {
        this.mc = mc;
    }

    public String getFrxm() {
        return frxm;
    }

    public void setFrxm(String frxm) {
        this.frxm = frxm;
    }

    public String getFrid() {
        return frid;
    }

    public void setFrid(String frid) {
        this.frid = frid;
    }

    public String getMp() {
        return mp;
    }

    public void setMp(String mp) {
        this.mp = mp;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(Date submitTime) {
        this.submitTime = submitTime;
    }

    public Date getVerifyTime() {
        return verifyTime;
    }

    public void setVerifyTime(Date verifyTime) {
        this.verifyTime = verifyTime;
    }

    public Integer getIsVerify() {
        return isVerify;
    }

    public void setIsVerify(Integer isVerify) {
        this.isVerify = isVerify;
    }

    public String getReplyMsg() {
        return replyMsg;
    }

    public void setReplyMsg(String replyMsg) {
        this.replyMsg = replyMsg;
    }

    public String getVerifyId() {
        return verifyId;
    }

    public void setVerifyId(String verifyId) {
        this.verifyId = verifyId;
    }

}
