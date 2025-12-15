package com.wellsoft.pt.bot.support;

import com.google.common.collect.Maps;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.implement.definition.entity.FormDefinition;

import java.util.List;
import java.util.Map;

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
public class DyFormBoterPrepareData extends BoterPrepareData {

    private Map<String, DyFormData> dyFormDataMap = Maps.newHashMap();//表单数据:uuid->DyFormData

    private Map<String, DyFormData> sourceDyFormDataMap = Maps.newHashMap();//来源表单数据:表单定义uuid->DyFormData

    private Map<String, DyFormData> targetByFormDataMap = Maps.newHashMap();//目标表单原数据：表单定义uuid->DyformData

    private Map<String, Map<String, Object>> formDataMap = Maps.newHashMap();//来源表单数据:表单定义Id->Map

    private Map<String, Map<String, Object>> targetFormDataMap = Maps.newHashMap();//目标表单数据:表单定义Id->Map

    private Map<String, List<Map<String, Object>>> saveTargetFormData = Maps.newHashMap();//保存的目标表单单据

    private Map<String, FormDefinition> formDefinitionMap = Maps.newHashMap();//表单定义

    public Map<String, DyFormData> getDyFormDataMap() {
        return dyFormDataMap;
    }


    public void setDyFormDataMap(
            Map<String, DyFormData> dyFormDataMap) {
        this.dyFormDataMap = dyFormDataMap;
    }

    public Map<String, DyFormData> getSourceDyFormDataMap() {
        return sourceDyFormDataMap;
    }

    public void setSourceDyFormDataMap(
            Map<String, DyFormData> sourceDyFormDataMap) {
        this.sourceDyFormDataMap = sourceDyFormDataMap;
    }

    public Map<String, Map<String, Object>> getFormDataMap() {
        return formDataMap;
    }

    public void setFormDataMap(
            Map<String, Map<String, Object>> formDataMap) {
        this.formDataMap = formDataMap;
    }

    public Map<String, List<Map<String, Object>>> getSaveTargetFormData() {
        return saveTargetFormData;
    }

    public void setSaveTargetFormData(
            Map<String, List<Map<String, Object>>> saveTargetFormData) {
        this.saveTargetFormData = saveTargetFormData;
    }


    public Map<String, FormDefinition> getFormDefinitionMap() {
        return formDefinitionMap;
    }

    public void setFormDefinitionMap(
            Map<String, FormDefinition> formDefinitionMap) {
        this.formDefinitionMap = formDefinitionMap;
    }

    public Map<String, DyFormData> getTargetByFormDataMap() {
        return targetByFormDataMap;
    }

    public void setTargetByFormDataMap(
            Map<String, DyFormData> targetByFormDataMap) {
        this.targetByFormDataMap = targetByFormDataMap;
    }

    public Map<String, Map<String, Object>> getTargetFormDataMap() {
        return targetFormDataMap;
    }

    public void setTargetFormDataMap(
            Map<String, Map<String, Object>> targetFormDataMap) {
        this.targetFormDataMap = targetFormDataMap;
    }
}
