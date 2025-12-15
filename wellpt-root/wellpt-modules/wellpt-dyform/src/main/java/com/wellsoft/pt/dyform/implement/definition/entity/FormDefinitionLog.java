package com.wellsoft.pt.dyform.implement.definition.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.pt.dyform.implement.definition.constant.DyFormConfig.EnumFormPropertyName;
import com.wellsoft.pt.dyform.implement.definition.util.dyform.FormDefinitionHandler;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年5月9日.1	zhongzh		2020年5月9日		Create
 * </pre>
 * @date 2020年5月9日
 */
@Entity
@Table(name = "DYFORM_FORM_DEFINITION_LOG")
@DynamicUpdate
@DynamicInsert
public class FormDefinitionLog extends IdEntity {

    private static final long serialVersionUID = -7964822772029135133L;

    private static Logger LOG = LoggerFactory.getLogger(FormDefinitionLog.class);

    private String formUuid;

    private String operateIp;

    // 以json的形式保存整个数据表单的定义
    private String definitionJson;

    @Transient
    private transient FormDefinitionHandler formDefinitionHandler = null;

    public String getFormUuid() {
        return formUuid;
    }

    public void setFormUuid(String formUuid) {
        this.formUuid = formUuid;
    }

    public String getOperateIp() {
        return operateIp;
    }

    public void setOperateIp(String operateIp) {
        this.operateIp = operateIp;
    }

    public String getDefinitionJson() {
        return formDefinitionHandler == null ? definitionJson : formDefinitionHandler.toString();
    }

    public void setDefinitionJson(String definitionJson) {
        if (definitionJson == null) {
            return;
        }
        // 重置formDefinitionHandler
        formDefinitionHandler = null;
        this.definitionJson = definitionJson;
    }

    @Transient
    public FormDefinitionHandler doGetFormDefinitionHandler() {
        if (formDefinitionHandler == null) {
            try {
                JSONObject formDefinitionJSONObject = new JSONObject(definitionJson);
                String name = formDefinitionJSONObject.optString(EnumFormPropertyName.name.name());
                String formType = formDefinitionJSONObject.optString(EnumFormPropertyName.formType.name());
                String pFromUuid = formDefinitionJSONObject.optString(EnumFormPropertyName.pFormUuid.name());
                formDefinitionHandler = new FormDefinitionHandler(definitionJson, formType, name, pFromUuid);
            } catch (JSONException e) {
                LOG.error(e.getMessage(), e);
            }
        }
        return formDefinitionHandler;
    }

}
