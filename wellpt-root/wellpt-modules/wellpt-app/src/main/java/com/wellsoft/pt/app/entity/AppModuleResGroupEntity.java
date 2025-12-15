package com.wellsoft.pt.app.entity;

import com.wellsoft.context.jdbc.entity.Entity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Table;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年07月06日   chenq	 Create
 * </pre>
 */
@Table(name = "APP_MODULE_RES_GROUP")
@DynamicInsert
@DynamicUpdate
@javax.persistence.Entity
public class AppModuleResGroupEntity extends Entity {

    private String name;

    private String moduleId;

    private ApplyTo applyTo;

    private Integer seq;

    public ApplyTo getApplyTo() {
        return applyTo;
    }

    public void setApplyTo(ApplyTo applyTo) {
        this.applyTo = applyTo;
    }

    public static enum ApplyTo {
        PAGE, SUB_PAGE
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public Integer getSeq() {
        return seq;
    }

    public void setSeq(Integer seq) {
        this.seq = seq;
    }
}
