package com.wellsoft.pt.dyform.implement.definition.entity;

import com.google.common.collect.Lists;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.app.entity.AppDefElementI18nEntity;
import com.wellsoft.pt.dyform.facade.dto.DyFormFormDefinition;
import com.wellsoft.pt.dyform.facade.dto.DyformFieldDefinition;
import com.wellsoft.pt.dyform.facade.dto.DyformSubformFormDefinition;
import com.wellsoft.pt.dyform.implement.definition.constant.DyFormConfig.EnumFormPropertyName;
import com.wellsoft.pt.dyform.implement.definition.enums.DyformTypeEnum;
import com.wellsoft.pt.dyform.implement.definition.enums.EnumSystemField;
import com.wellsoft.pt.dyform.implement.definition.util.dyform.FormDefinitionHandler;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.NotBlank;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Description: 表单定义实现类
 *
 * @author hongjz
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年3月28日.1	hongjz		2018年3月28日		Create
 * </pre>
 * @date 2018年3月28日
 */
@Entity
@Table(name = "DYFORM_FORM_DEFINITION")
@DynamicUpdate
@DynamicInsert
public class FormDefinition extends IdEntity implements DyFormFormDefinition {

    private static final long serialVersionUID = -7964822772029135133L;
    private static Logger LOG = LoggerFactory.getLogger(FormDefinition.class);
    /* ///////////以下为临时变量/////////////////** */
    private final String minVersion = "1.0";// 最低的版本
    // 表名
    private String tableName;
    // 显示名称
    @NotBlank
    private String name;
    // 表单属性id
    @NotBlank
    private String id;
    // 表单编号
    private String code;
    //表单事件
    private String events;
    // 描述
    private String remark;
    private String relationTbl;
    // 版本 ,形式：1.0
    private String version;
    // 是否启用表单签名
    private String enableSignature;
    // 以json的形式保存整个数据表单的定义
    private String definitionJson;
    // 定义端通过vue设计器生成的表单定义json
    private String definitionVjson;
    // 公共的定制JS模块ID
    private String customJsModule;
    // 系统单位ID
    private String systemUnitId;// 归属系统单位，一旦设置不能修改
    // 参照枚举类DyformTypeEnum
    // 表单类型
    private String formType; // "P:存储单据, V:展现单据, M: 手机单据,T:模板单据
    // 展现单据依赖的存储单据UUID
    private String pFormUuid; // 如果单据类型为P(存储单据)时，这个字段的值为null
    // 设计器参考模板
    private String refDesignerFormUuid; // 表示设计器的内容是参照哪个单据(存储单据/展现单据/模块单据)
    // 版本格式
    @JsonIgnore
    private DecimalFormat versionFormat = new DecimalFormat("0.0");

    private String moduleId;//归属模块ID

    private Long moduleUuid;

    // 分类UUID
    private Long categoryUuid;

    @ApiModelProperty("归属系统")
    protected String system;
    @ApiModelProperty("归属租户")
    protected String tenant;

    // 是否升级1.是 0.否
    @Transient
    private String isUp = "0";// 非持久化属性

    @Transient
    private transient FormDefinitionHandler formDefinitionHandler = null;

    @Transient
    private List<AppDefElementI18nEntity> i18ns = Lists.newArrayList();


    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;

    }

    @Override
    public String getEnableSignature() {
        return enableSignature;
    }

    public void setEnableSignature(String enableSignature) {
        this.enableSignature = enableSignature;
    }

    @Override
    public String getDefinitionJson() {
        return this.formDefinitionHandler == null ? this.definitionJson : this.formDefinitionHandler.toString();
    }

    public void setDefinitionJson(String definitionJson) {
        if (definitionJson == null) {
            return;
        }
        // 重置formDefinitionHandler
        this.formDefinitionHandler = null;
        this.definitionJson = definitionJson;
    }

    @Override
    public String getRelationTbl() {
        return relationTbl;
    }

    public void setRelationTbl(String relationTbl) {
        this.relationTbl = relationTbl;
    }

    /**
     * @return the events
     */
    public String getEvents() {
        return events;
    }

    /**
     * @param events 要设置的events
     */
    public void setEvents(String events) {
        this.events = events;
    }

    @Transient
    @Override
    @JsonIgnore
    public DecimalFormat getVersionFormat() {
        return versionFormat;
    }

    public void setVersionFormat(DecimalFormat versionFormat) {
        this.versionFormat = versionFormat;
    }

    @Transient
    public String getMinVersion() {
        return minVersion;
    }

    @Override
    @Transient
    public String getIsUp() {
        return isUp;
    }

    public void setIsUp(String isUp) {
        this.isUp = isUp;
    }

    /**
     * @return the customJsModule
     */
    @Override
    public String getCustomJsModule() {
        return customJsModule;
    }

    /**
     * @param customJsModule 要设置的customJsModule
     */
    public void setCustomJsModule(String customJsModule) {
        this.customJsModule = customJsModule;
    }

    public void doBindCreateTimeAsNow() {
        this.setCreateTime(new Date());
    }

    public void doBindModifyTimeAsNow() {
        this.setModifyTime(new Date());
    }

    public void doBindModifierAsCurrentUser() {
        this.setModifier(SpringSecurityUtils.getCurrentUserId());
    }

    public void doBindCreatorAsCurrentUser() {
        this.setCreator(SpringSecurityUtils.getCurrentUserId());
    }

    @Override
    public void doBindVersionAsMinVersion() {
        this.setVersion(minVersion);
        this.doBindVersion2DefinitionJson(minVersion);
    }

    public void doBindVersion2DefinitionJson(String version) {

        this.doGetFormDefinitionHandler().addFormProperty(EnumSystemField.version.getName(), version);

    }

    public void doBindVersionPlus() {
        this.setVersion(versionFormat.format((Float.parseFloat(this.getVersion()) + 0.1)));
        this.doBindVersion2DefinitionJson(this.getVersion());
    }

    @Override
    @Transient
    @JsonIgnore
    public boolean isMinVersion() {
        if (minVersion.equals(this.getVersion())) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void doBindUuid2Json() {
        try {
            JSONObject json = new JSONObject(getDefinitionJson());
            String thisUuid = getUuid();
            if (!json.has("uuid") || false == StringUtils.equals(json.getString("uuid"), thisUuid)) {
                json.put("uuid", thisUuid);
                setDefinitionJson(json.toString());
            }

        } catch (JSONException e) {
            LOG.error(e.getMessage(), e);
        }

    }

    /**
     * 更新JSON中的版本，add by wujx 20160829
     */
    public void updateVersion2Json() {
        try {
            JSONObject json = new JSONObject(this.getDefinitionJson());
            String thisVersion = this.getVersion();
            json.put("version", thisVersion);
            this.setDefinitionJson(json.toString());
        } catch (JSONException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    @Transient
    public FormDefinitionHandler doGetFormDefinitionHandler() {
        if (this.formDefinitionHandler == null) {
            try {
                this.formDefinitionHandler = new FormDefinitionHandler(definitionJson, this.getFormType(),
                        this.getName(), this.getpFormUuid());
                // 填充vjson的字段配置
                if (StringUtils.isNotBlank(definitionVjson)) {
                    fillFieldConfigurationOfVjson();
                }
            } catch (JSONException e) {
                LOG.error(e.getMessage(), e);
            }
        }
        return formDefinitionHandler;
    }

    private void fillFieldConfigurationOfVjson() {
        JSONObject vjsonObject = new JSONObject(definitionVjson);
        if (!vjsonObject.has("fields")) {
            return;
        }
        JSONArray jsonArray = vjsonObject.getJSONArray("fields");
        for (int index = 0; index < jsonArray.length(); index++) {
            JSONObject vfieldObject = jsonArray.getJSONObject(index);
            if (!vfieldObject.has("configuration")) {
                continue;
            }

            JSONObject vfieldConfigObject = vfieldObject.getJSONObject("configuration");
            if (!vfieldConfigObject.has("code")) {
                continue;
            }

            String fieldName = vfieldConfigObject.getString("code");
            JSONObject jsonObject = this.formDefinitionHandler.getFieldDefinitionJson(fieldName);
            if (jsonObject != null) {
                jsonObject.put("configuration", vfieldConfigObject);
            }
        }
    }

    @Override
    public List<DyformFieldDefinition> doGetFieldDefintions() {
        if (this.formDefinitionHandler == null) {
            return null;
        }

        List<DyformFieldDefinition> fieldDefintions = new ArrayList<DyformFieldDefinition>();
        List<String> fieldNames = this.formDefinitionHandler.getFieldNamesOfMainform();

        if (fieldNames != null) {
            for (String fieldName : fieldNames) {
                DyformFieldDefinition df = this.formDefinitionHandler.getFieldDefinition(fieldName);
                fieldDefintions.add(df);
            }
        }

        return fieldDefintions;
    }

    @Override
    public List<DyformFieldDefinition> doGetTableFieldDefintions() {
        if (this.formDefinitionHandler == null) {
            return null;
        }
        List<DyformFieldDefinition> fieldDefintions = new ArrayList<DyformFieldDefinition>();
        List<String> fieldNames = this.formDefinitionHandler.getFieldNamesOfMaintable();

        if (fieldNames != null) {
            for (String fieldName : fieldNames) {
                DyformFieldDefinition df = this.formDefinitionHandler.getFieldDefinition(fieldName);
                fieldDefintions.add(df);
            }
        }

        return fieldDefintions;
    }

    @Override
    public List<DyformFieldDefinition> doGetTemplateDefintions() {
        if (this.formDefinitionHandler == null) {
            return null;
        }
        List<DyformFieldDefinition> fieldDefintions = new ArrayList<DyformFieldDefinition>();
        return fieldDefintions;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.facade.dto.DyFormFormDefinition#doGetTemplateUuids()
     */
    @Override
    public List<String> doGetTemplateUuids() {
        return this.formDefinitionHandler.getFormUuidsOfTemplate();
    }

    @Override
    public List<DyformSubformFormDefinition> doGetSubformDefinitions() {
        if (this.formDefinitionHandler == null) {
            return null;
        }

        List<DyformSubformFormDefinition> subformDefinitions = new ArrayList<DyformSubformFormDefinition>();
        List<String> formUuidOfSubforms = this.formDefinitionHandler.getFormUuidsOfSubform();
        if (formUuidOfSubforms != null) {
            for (String formUuidOfSubform : formUuidOfSubforms) {
                DyformSubformFormDefinition df = this.formDefinitionHandler.getSubformDefinition(formUuidOfSubform);
                subformDefinitions.add(df);
            }
        }
        return subformDefinitions;
    }

    /**
     * @return the systemUnitId
     */
    @Override
    public String getSystemUnitId() {
        return systemUnitId;
    }

    /**
     * @param systemUnitId 要设置的systemUnitId
     */
    public void setSystemUnitId(String systemUnitId) {
        this.systemUnitId = systemUnitId;
    }

    @Override
    public String getFormType() {
        return formType;
    }

    public void setFormType(String formType) {
        this.formType = formType;
    }

    @Override
    public String getpFormUuid() {
        return pFormUuid;
    }

    public void setpFormUuid(String pFormUuid) {
        if (DyformTypeEnum.isExtendsPform(getFormType())) {
            this.pFormUuid = pFormUuid;
        } else if (StringUtils.isNotBlank(pFormUuid)) {
            throw new RuntimeException("pFormUuid在继承自存储单据有才设置");
        }
    }

    @Override
    public String getRefDesignerFormUuid() {
        return refDesignerFormUuid;
    }

    public void setRefDesignerFormUuid(String refDesignerFormUuid) {
        this.refDesignerFormUuid = refDesignerFormUuid;
    }

    public void setFormDefinitionHandler(FormDefinitionHandler formDefinitionHandler) {
        this.formDefinitionHandler = formDefinitionHandler;
    }

    @Override
    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public String doGetTblNameOfpForm() {
        return this.doGetFormDefinitionHandler().doGetTblNameOfpForm();
    }

    @Override
    public String doGetRelationTblNameOfpForm() {
        return this.doGetFormDefinitionHandler().doGetRelationTblNameOfPform();
    }

    @Override
    public boolean doHasLayout() {
        return !JsonUtils.isEmptyObject(this.doGetFormDefinitionHandler().getFormPropertyOfJSONType(
                EnumFormPropertyName.layouts));

    }

    /**
     * @return 表单默认展示单据
     */
    @Override
    public String doGetDefaultVFormUuid() {
        return doGetFormDefinitionHandler().getDefaultVFormUuid();
    }

    /**
     * @return 表单默认手机单据
     */
    @Override
    public String doGetDefaultMFormUuid() {
        return doGetFormDefinitionHandler().getDefaultMFormUuid();
    }

    @Override
    public String doGetTitleExpression() {
        return doGetFormDefinitionHandler().getTitleExpression();
    }

    /**
     * @return 表单存储单据
     */
    @Override
    public String doGetPFormUuid() {
        if (DyformTypeEnum.isMform(getFormType()) || DyformTypeEnum.isVform(getFormType())) {
            return getpFormUuid();
        }
        return getUuid();
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.facade.dto.DyFormFormDefinition#doGetPlaceholderCtrIds()
     */
    @Override
    public List<String> doGetPlaceholderCtrIds() {
        return doGetFormDefinitionHandler().getPlaceholderCtrIds();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.facade.dto.DyFormFormDefinition#doGetFileLibraryIds()
     */
    @Override
    public List<String> doGetFileLibraryIds() {
        return doGetFormDefinitionHandler().getFileLibraryIds();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.facade.dto.DyFormFormDefinition#doGetTableViewIds()
     */
    @Override
    public List<String> doGetTableViewIds() {
        return doGetFormDefinitionHandler().getTableViewIds();
    }

    public String getDefinitionVjson() {
        return definitionVjson;
    }

    public void setDefinitionVjson(String definitionVjson) {
        this.definitionVjson = definitionVjson;
    }

    public Long getModuleUuid() {
        return moduleUuid;
    }

    public void setModuleUuid(Long moduleUuid) {
        this.moduleUuid = moduleUuid;
    }

    /**
     * @return the categoryUuid
     */
    public Long getCategoryUuid() {
        return categoryUuid;
    }

    /**
     * @param categoryUuid 要设置的categoryUuid
     */
    public void setCategoryUuid(Long categoryUuid) {
        this.categoryUuid = categoryUuid;
    }

    /**
     * @return the system
     */
    public String getSystem() {
        return system;
    }

    /**
     * @param system 要设置的system
     */
    public void setSystem(String system) {
        this.system = system;
    }

    /**
     * @return the tenant
     */
    public String getTenant() {
        return tenant;
    }

    /**
     * @param tenant 要设置的tenant
     */
    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    @Transient
    public List<AppDefElementI18nEntity> getI18ns() {
        return i18ns;
    }

    public void setI18ns(List<AppDefElementI18nEntity> i18ns) {
        this.i18ns = i18ns;
    }
}
