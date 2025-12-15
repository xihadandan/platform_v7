package com.wellsoft.pt.bot.support;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Description:
 *
 * @author chenq
 * @date 2018/9/19
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/9/19    chenq		2018/9/19		Create
 * </pre>
 */
public class BoterPrepareData {

    protected List<BotFieldMapping> fieldMappings = Lists.newArrayList();//字段转换映射数据

    protected List<BotFieldMapping> relationFieldMappings = Lists.newArrayList();//关联关系的字段映射

    protected String beforeBotScript;//转换前代码

    protected String afterBotScript;//转换后代码

    protected boolean isPersist;//是否保存单据

    protected String targetObjId;//目标单据ID


    public List<BotFieldMapping> getFieldMappings() {
        return fieldMappings;
    }

    public void setFieldMappings(List<BotFieldMapping> fieldMappings) {
        this.fieldMappings = fieldMappings;
    }

    public List<BotFieldMapping> getRelationFieldMappings() {
        return relationFieldMappings;
    }

    public void setRelationFieldMappings(
            List<BotFieldMapping> relationFieldMappings) {
        this.relationFieldMappings = relationFieldMappings;
    }

    public String getBeforeBotScript() {
        return beforeBotScript;
    }

    public void setBeforeBotScript(String beforeBotScript) {
        this.beforeBotScript = beforeBotScript;
    }

    public String getAfterBotScript() {
        return afterBotScript;
    }

    public void setAfterBotScript(String afterBotScript) {
        this.afterBotScript = afterBotScript;
    }

    public boolean isPersist() {
        return isPersist;
    }

    public void setPersist(boolean persist) {
        isPersist = persist;
    }


    public String getTargetObjId() {
        return targetObjId;
    }

    public void setTargetObjId(String targetObjId) {
        this.targetObjId = targetObjId;
    }
}
