package com.wellsoft.pt.bm.entity;

import com.wellsoft.context.annotation.UnCloneable;
import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Date;
import java.util.Set;

/**
 * Description: 荣誉/资质主信息表
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
@Table(name = "bm_self_publicity_apply")
@DynamicUpdate
@DynamicInsert
public class SelfPublicityApply extends IdEntity {

    private String uuid;
    //类型标识 1：荣誉  2：资质
    private String type;
    //注册号
    private String zch;
    //名称
    private String mc;
    //授予单位
    private String sydw;
    //取得日期
    private Date qdrq;
    //有效日期
    private Date yxrq;
    @UnCloneable
    private Set<SubmitVerify> SubmitVerifies;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "selfPublicityApply")
    @Cascade(value = {CascadeType.ALL})
    public Set<SubmitVerify> getSubmitVerifies() {
        return SubmitVerifies;
    }

    public void setSubmitVerifies(Set<SubmitVerify> submitVerifies) {
        SubmitVerifies = submitVerifies;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

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

    public String getSydw() {
        return sydw;
    }

    public void setSydw(String sydw) {
        this.sydw = sydw;
    }

    public Date getQdrq() {
        return qdrq;
    }

    public void setQdrq(Date qdrq) {
        this.qdrq = qdrq;
    }

    public Date getYxrq() {
        return yxrq;
    }

    public void setYxrq(Date yxrq) {
        this.yxrq = yxrq;
    }
}
