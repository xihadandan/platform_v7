package com.wellsoft.pt.bm.bean;

import com.wellsoft.pt.bm.entity.PublicityAttach;
import com.wellsoft.pt.bm.entity.SelfPublicityApply;

import java.util.Date;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author wangbx
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-12-7.1	wangbx		2013-12-7		Create
 * </pre>
 * @date 2013-12-7
 */
public class SelfPublicityBean extends SelfPublicityApply {
    //提交时间
    private Date submitTime;
    //审核时间
    private Date verifyTime;
    //审核部门Id
    private String verifyId;
    //是否已审核 0：待审核   1：审核通过   -1:审核不通过
    private String isVerify;
    //回复信息
    private String replyMsg;
    //名称
    private String reMc;
    //法人姓名
    private String reFrxm;
    //手机
    private String reMp;
    //邮件
    private String reEmail;

    private List<PublicityAttach> publicityAttachs;

    public String getReMp() {
        return reMp;
    }

    public void setReMp(String reMp) {
        this.reMp = reMp;
    }

    public String getReEmail() {
        return reEmail;
    }

    public void setReEmail(String reEmail) {
        this.reEmail = reEmail;
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

    public String getVerifyId() {
        return verifyId;
    }

    public void setVerifyId(String verifyId) {
        this.verifyId = verifyId;
    }

    public String getIsVerify() {
        return isVerify;
    }

    public void setIsVerify(String isVerify) {
        this.isVerify = isVerify;
    }

    public String getReplyMsg() {
        return replyMsg;
    }

    public void setReplyMsg(String replyMsg) {
        this.replyMsg = replyMsg;
    }

    public String getReMc() {
        return reMc;
    }

    public void setReMc(String reMc) {
        this.reMc = reMc;
    }

    public String getReFrxm() {
        return reFrxm;
    }

    public void setReFrxm(String reFrxm) {
        this.reFrxm = reFrxm;
    }

    /**
     * @return the publicityAttachs
     */
    public List<PublicityAttach> getPublicityAttachs() {
        return publicityAttachs;
    }

    /**
     * @param publicityAttachs 要设置的publicityAttachs
     */
    public void setPublicityAttachs(List<PublicityAttach> publicityAttachs) {
        this.publicityAttachs = publicityAttachs;
    }

}
