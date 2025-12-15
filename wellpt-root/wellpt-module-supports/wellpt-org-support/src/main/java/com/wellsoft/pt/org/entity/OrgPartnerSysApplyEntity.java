package com.wellsoft.pt.org.entity;

import com.wellsoft.context.jdbc.entity.SysEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * Description: 组织协作系统申请（申请数据归属公共服务，各个租户都能访问）
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2022年11月09日   chenq	 Create
 * </pre>
 */
@Entity
@Table(name = "ORG_PARTNER_SYS_APPLY")
@DynamicUpdate
@DynamicInsert
public class OrgPartnerSysApplyEntity extends SysEntity {


    private String reqName;

    private String resName;

    private String reqSystem; // 请求方系统

    private String reqTenant; // 请求方租户

    private String resSystem; // 响应方系统

    private String resTenant; // 响应方租户

    private String resSystemCode;// 协作响应系统标识

    private State state;

    private String applyReason;

    private Date applyTime;

    private Long categoryUuid;


    public String getReqSystem() {
        return this.reqSystem;
    }

    public void setReqSystem(final String reqSystem) {
        this.reqSystem = reqSystem;
    }

    public String getResSystem() {
        return this.resSystem;
    }

    public void setResSystem(final String resSystem) {
        this.resSystem = resSystem;
    }

    public String getResTenant() {
        return this.resTenant;
    }

    public void setResTenant(final String resTenant) {
        this.resTenant = resTenant;
    }

    public String getResSystemCode() {
        return this.resSystemCode;
    }

    public void setResSystemCode(final String resSystemCode) {
        this.resSystemCode = resSystemCode;
    }

    public State getState() {
        return this.state;
    }

    public void setState(final State state) {
        this.state = state;
    }

    public String getApplyReason() {
        return this.applyReason;
    }

    public void setApplyReason(final String applyReason) {
        this.applyReason = applyReason;
    }

    public Date getApplyTime() {
        return this.applyTime;
    }

    public void setApplyTime(final Date applyTime) {
        this.applyTime = applyTime;
    }

    public String getReqTenant() {
        return this.reqTenant;
    }

    public void setReqTenant(final String reqTenant) {
        this.reqTenant = reqTenant;
    }


    public String getReqName() {
        return this.reqName;
    }

    public void setReqName(final String reqName) {
        this.reqName = reqName;
    }

    public String getResName() {
        return this.resName;
    }

    public void setResName(final String resName) {
        this.resName = resName;
    }

    public Long getCategoryUuid() {
        return this.categoryUuid;
    }

    public void setCategoryUuid(final Long categoryUuid) {
        this.categoryUuid = categoryUuid;
    }

    public static enum State {
        WAIT_CONFIRM /* 待确认 */, PASSED /* 确认通过 */, REJECTED /* 拒绝 */, TEMINATED /* 终止 */
    }

}
