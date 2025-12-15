package com.wellsoft.pt.bm.entity;

import com.wellsoft.context.annotation.UnCloneable;
import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Date;

/**
 * Description: 提交审核过程表
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
@Table(name = "bm_submit_verify")
@DynamicUpdate
@DynamicInsert
public class SubmitVerify extends IdEntity {

    @UnCloneable
    private SelfPublicityApply selfPublicityApply;
    //提交时间
    private Date submitTime;
    //审核时间
    private Date verifyTime;
    //审核部门Id
    private String verifyId;
    //是否已审核 0：待审核   1：审核通过   -1:审核不通过
    private Integer isVerify;
    //回复信息
    private String replyMsg;

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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "puuid")
    public SelfPublicityApply getSelfPublicityApply() {
        return selfPublicityApply;
    }

    public void setSelfPublicityApply(SelfPublicityApply selfPublicityApply) {
        this.selfPublicityApply = selfPublicityApply;
    }
}
