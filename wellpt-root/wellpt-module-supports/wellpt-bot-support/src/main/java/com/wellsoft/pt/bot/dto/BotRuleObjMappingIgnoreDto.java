package com.wellsoft.pt.bot.dto;

import java.io.Serializable;

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
public class BotRuleObjMappingIgnoreDto implements Serializable {
    private static final long serialVersionUID = 1142235848113500462L;

    // 源单据字段
    private String sourceObjField;
    // 目标单据字段
    private String targetObjField;
    // 源单据ID
    private String sourceObjId;
    // 目标单据ID
    private String targetObjId;

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
}
