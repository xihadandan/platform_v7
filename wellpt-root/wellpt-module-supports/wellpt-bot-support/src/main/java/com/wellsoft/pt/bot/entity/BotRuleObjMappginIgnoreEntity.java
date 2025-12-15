package com.wellsoft.pt.bot.entity;

import com.wellsoft.context.jdbc.entity.TenantEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2021年10月13日   chenq	 Create
 * </pre>
 */
@Entity
@Table(name = "BOT_RULE_OBJ_MAPPING_IGNORE")
@DynamicUpdate
@DynamicInsert
public class BotRuleObjMappginIgnoreEntity extends TenantEntity {
    private static final long serialVersionUID = 3307558842454559431L;

    // 源单据字段
    private String sourceObjField;
    // 目标单据字段
    private String targetObjField;
    // 源单据ID
    private String sourceObjId;
    // 目标单据ID
    private String targetObjId;

    private String ruleConfUuid;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getSourceObjField() {
        return sourceObjField;
    }

    public void setSourceObjField(String sourceObjField) {
        this.sourceObjField = sourceObjField;
    }

    public String getTargetObjField() {
        return targetObjField;
    }

    public void setTargetObjField(String targetObjField) {
        this.targetObjField = targetObjField;
    }

    public String getSourceObjId() {
        return sourceObjId;
    }

    public void setSourceObjId(String sourceObjId) {
        this.sourceObjId = sourceObjId;
    }

    public String getTargetObjId() {
        return targetObjId;
    }

    public void setTargetObjId(String targetObjId) {
        this.targetObjId = targetObjId;
    }

    public String getRuleConfUuid() {
        return ruleConfUuid;
    }

    public void setRuleConfUuid(String ruleConfUuid) {
        this.ruleConfUuid = ruleConfUuid;
    }
}
