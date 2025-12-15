package com.wellsoft.pt.dyform.implement.definition.web;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.base.BaseObject;
import com.wellsoft.context.component.jqgrid.JqGridQueryController;
import com.wellsoft.context.component.jqgrid.JqGridQueryData;
import com.wellsoft.context.component.jqgrid.JqGridQueryInfo;
import com.wellsoft.context.enums.JsonDataErrorCode;
import com.wellsoft.context.jdbc.support.QueryData;
import com.wellsoft.context.jdbc.support.QueryInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.i18n.MsgUtils;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.FaultMessage;
import com.wellsoft.context.web.controller.IgnoreResultAdvice;
import com.wellsoft.context.web.controller.ResultMessage;
import com.wellsoft.pt.app.service.AppDefElementI18nService;
import com.wellsoft.pt.basicdata.datadict.service.DataDictionaryService;
import com.wellsoft.pt.basicdata.datastore.facade.service.CdDataStoreService;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.dyform.implement.definition.constant.DyFormConfig;
import com.wellsoft.pt.dyform.implement.definition.constant.DyFormConfig.EnumFieldPropertyName;
import com.wellsoft.pt.dyform.implement.definition.entity.FormDefinition;
import com.wellsoft.pt.dyform.implement.definition.enums.DyformTypeEnum;
import com.wellsoft.pt.dyform.implement.definition.enums.EnumDbWords;
import com.wellsoft.pt.dyform.implement.definition.enums.EnumRelationTblSystemField;
import com.wellsoft.pt.dyform.implement.definition.enums.EnumSystemField;
import com.wellsoft.pt.dyform.implement.definition.exceptions.HibernateDdlException;
import com.wellsoft.pt.dyform.implement.definition.service.FormDefinitionService;
import com.wellsoft.pt.dyform.implement.definition.util.dyform.FormDefinitionHandler;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.audit.dto.AuditDataLogDto;
import com.wellsoft.pt.security.audit.facade.service.AuditDataFacadeService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.HibernateException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Description: 表单定义控制类
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
@Api(tags = "表单定义接口")
@RestController
@RequestMapping({"/pt/dyform/definition", "/api/dyform/definition"})
public class FormDefinitionController extends JqGridQueryController {

    public static final String ATTR_FORM_UUID = "formUuid";
    public static final String ATTR_FORM_TYPE = "formType";
    public static final String ATTR_FORM_COPY = "formCopy";

    @Autowired
    private DyFormFacade dyFormFacade;

    @Autowired
    private CdDataStoreService cdDataStoreService;

    @Autowired
    private DataDictionaryService dataDictionaryService;

    @Autowired
    private FormDefinitionService formDefinitionService;

    @Autowired
    private AppDefElementI18nService appDefElementI18nService;

    @ApiIgnore
    @GetMapping(value = "/index")
    public String index(Model model, HttpServletRequest request) {
        return forward("/dyform/definition/dyform_definition_list");
    }

    @ApiIgnore
    @GetMapping(value = "/form-designer")
    public String formDesigner(Model model, @RequestParam(value = "uuid") String uuid, HttpServletRequest request,
                               @RequestParam(value = "moduleId", required = false) String moduleId) {
        FormDefinition definition = formDefinitionService.findDyFormFormDefinitionByFormUuid(uuid);
        if (definition != null) {
            DyformTypeEnum typeEnum = DyformTypeEnum.value2EnumObj(definition.getFormType());
            if (typeEnum != null) {
                if (typeEnum.equals(DyformTypeEnum.P)) {
                    return pformDesigner(model, uuid, request, moduleId);
                }
                if (typeEnum.equals(DyformTypeEnum.V)) {
                    return vformDesigner(model, uuid, request, moduleId);
                }
                if (typeEnum.equals(DyformTypeEnum.M)) {
                    return mformDesigner(model, uuid, request, moduleId);
                }
                if (typeEnum.equals(DyformTypeEnum.MST)) {
                    return mstformDesigner(model, uuid, request, moduleId);
                }
            }
        }
        return pformDesigner(model, uuid, request, moduleId);
    }

    @ApiIgnore
    @GetMapping(value = "/pform-designer")
    public String pformDesigner(Model model, @RequestParam(value = "uuid", defaultValue = "") String uuid,
                                HttpServletRequest request, @RequestParam(value = "moduleId", required = false) String moduleId) {
        model.addAttribute(ATTR_FORM_UUID, uuid);
        model.addAttribute(ATTR_FORM_TYPE, DyformTypeEnum.P.getValue());
        model.addAttribute("moduleId", moduleId);
        setModuleId(uuid, model);
        return forward("/dyform/definition/dyform_form_designer");
    }

    private void setModuleId(String uuid, Model model) {
        if (StringUtils.isBlank(uuid)) {
            return;
        }
        FormDefinition definition = formDefinitionService.findDyFormFormDefinitionByFormUuid(uuid);
        if (definition != null && StringUtils.isNoneBlank(definition.getModuleId())) {
            model.addAttribute("moduleId", definition.getModuleId());
        }
    }

    @ApiIgnore
    @GetMapping(value = "/vform-designer")
    public String vformDesigner(Model model, @RequestParam(value = "uuid", defaultValue = "") String uuid,
                                HttpServletRequest request, @RequestParam(value = "moduleId", required = false) String moduleId) {
        model.addAttribute(ATTR_FORM_UUID, uuid);
        model.addAttribute(ATTR_FORM_TYPE, DyformTypeEnum.V.getValue());
        model.addAttribute("moduleId", moduleId);
        setModuleId(uuid, model);
        return forward("/dyform/definition/dyform_form_designer");
    }

    @ApiIgnore
    @GetMapping(value = "/cform-designer")
    public String cformDesigner(Model model, @RequestParam(value = "uuid", defaultValue = "") String uuid,
                                HttpServletRequest request, @RequestParam(value = "moduleId", required = false) String moduleId) {
        String pFromUuid = request.getParameter("pFromUuid");
        model.addAttribute(ATTR_FORM_UUID, uuid);
        model.addAttribute(ATTR_FORM_TYPE, DyformTypeEnum.C.getValue());
        model.addAttribute("moduleId", moduleId);
        model.addAttribute("pFromUuid", pFromUuid);
        setModuleId(uuid, model);
        return forward("/dyform/definition/dyform_form_designer");
    }

    @ApiIgnore
    @GetMapping(value = "/mform-designer")
    public String mformDesigner(Model model, @RequestParam(value = "uuid", defaultValue = "") String uuid,
                                HttpServletRequest request, @RequestParam(value = "moduleId", required = false) String moduleId) {
        model.addAttribute(ATTR_FORM_UUID, uuid);
        model.addAttribute(ATTR_FORM_TYPE, DyformTypeEnum.M.getValue());
        model.addAttribute("moduleId", moduleId);
        setModuleId(uuid, model);
        return forward("/dyform/definition/dyform_form_designer");
    }

    @ApiIgnore
    @GetMapping(value = "/mstform-designer")
    public String mstformDesigner(Model model, @RequestParam(value = "uuid", defaultValue = "") String uuid,
                                  HttpServletRequest request, @RequestParam(value = "moduleId", required = false) String moduleId) {
        model.addAttribute(ATTR_FORM_UUID, uuid);
        model.addAttribute(ATTR_FORM_TYPE, DyformTypeEnum.MST.getValue());
        model.addAttribute("moduleId", moduleId);
        setModuleId(uuid, model);
        return forward("/dyform/definition/dyform_form_designer");
    }

    @ApiIgnore
    @GetMapping(value = "/form-copy")
    public String formCopy(Model model, @RequestParam(value = "uuid", defaultValue = "") String uuid,
                           HttpServletRequest request, @RequestParam(value = "moduleId", required = false) String moduleId) {
        model.addAttribute(ATTR_FORM_UUID, uuid);
        model.addAttribute(ATTR_FORM_COPY, uuid);
        FormDefinition formDefinition = formDefinitionService.findDyFormFormDefinitionByFormUuid(uuid);
        model.addAttribute(ATTR_FORM_TYPE, formDefinition.getFormType());
        model.addAttribute("moduleId", formDefinition.getModuleId());
        return forward("/dyform/definition/dyform_form_designer");
    }

    @IgnoreResultAdvice
    @ApiIgnore
    @PostMapping(value = "/list")
    public @ResponseBody
    JqGridQueryData listAsTreeJson(HttpServletRequest request, JqGridQueryInfo jqGridQueryInfo) {
        QueryInfo queryInfo = buildQueryInfo(jqGridQueryInfo, request);
        QueryData queryData = formDefinitionService.getForPageAsTree(jqGridQueryInfo, queryInfo);
        JqGridQueryData jqGridQueryData = convertToJqGridQueryData(queryData);
        // System.out.println(JsonBinder.buildNormalBinder().toJson(jqGridQueryData));
        // System.out.println(Jso);
        return jqGridQueryData;
    }

    @ApiIgnore
    @GetMapping("/mobile/preview")
    public String mobileFormPreview(@RequestParam(value = "uuid", required = false) String uuid,
                                    @RequestParam(value = "flag", required = false) String flag, Model model) {
        model.addAttribute("flag", flag);
        model.addAttribute("uuid", uuid);
        // FormDefinition formDefinition = formDefinitionService.findDyFormFormDefinitionByFormUuid(uuid);
        // model.addAttribute("moduleId", formDefinition.getModuleId());
        return forward("/dyform/dyform_designer_preview");
    }

    @ApiIgnore
    @GetMapping("/open")
    public String open(@RequestParam(value = "formUuid", required = false) String formUuid,
                       @RequestParam(value = "dataUuid", required = false) String dataUuid,
                       @RequestParam(value = "formDefinition", required = false) String formDefinition, Model model)
            throws JSONException {
        if (StringUtils.isNotBlank(formDefinition)) {
            // 加载表单默认信息
            JSONObject formDefinitionJSONObject = new JSONObject(formDefinition);
            String name = formDefinitionJSONObject.getString("name");
            String formType = formDefinitionJSONObject.getString("formType");
            String pFormUuid = formDefinitionJSONObject.getString("pFormUuid");
            formDefinition = dyFormFacade.loadDefinitionJsonDefaultInfo(formDefinition, formType, name, pFormUuid);
            model.addAttribute("formDefinition", formDefinition);
        } else {
            model.addAttribute("formUuid", formUuid);
            model.addAttribute("dataUuid", dataUuid);
        }
        return forward("/pt/dyform/explain/dyform_demo");
    }

    @ApiIgnore
    @GetMapping("/dyformDemo")
    public String dyformDemo(@RequestParam(value = "formUuid", required = false) String formUuid,
                             @RequestParam(value = "dataUuid", required = false) String dataUuid,
                             @RequestParam(value = "formDefinition", required = false) String formDefinition, Model model)
            throws JSONException {
        return open(formUuid, dataUuid, formDefinition, model);
    }

    @SuppressWarnings("static-method")
    protected ResponseEntity<ResultMessage> getSucessfulResultMsg(Object data) {
        ResultMessage resultMessage = new ResultMessage();
        resultMessage.setSuccess(true);
        resultMessage.setData(data);
        return new ResponseEntity<ResultMessage>(resultMessage, HttpStatus.OK);
    }

    @SuppressWarnings("static-method")
    protected ResponseEntity<ResultMessage> getFaultResultMsg(Object data, JsonDataErrorCode jsonDataErrorCode) {
        FaultMessage resultMessage = new FaultMessage();
        resultMessage.setSuccess(false);
        resultMessage.setData(data);
        resultMessage.setErrorCode(jsonDataErrorCode.toString());
        return new ResponseEntity<ResultMessage>(resultMessage, HttpStatus.EXPECTATION_FAILED);
    }

    /**
     * 更新表单定义信息
     *
     * @param rootTableInfo
     * @param request
     * @return
     * @throws IOException
     */
    @PostMapping(value = "/update")
    @ApiOperation(value = "更新表单定义", notes = "更新表单定义")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "formDefinition", value = "表单定义JSON", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "deletedFieldNames", value = "要删除的表单字段", paramType = "query", dataType = "String", required = true)})
    public @ResponseBody
    ResponseEntity<ResultMessage> update(
            @RequestParam(value = "formDefinition") String formDefinition,
            @RequestParam(value = "deletedFieldNames") String deletedFieldNames) {
        // 更新表单数据表定义信息及表单数据表
        FormDefinition dy = null;
        try {
            dy = dyFormFacade.updateFormDefinitionAndFormTable(formDefinition, deletedFieldNames);
        } catch (HibernateDdlException e) {
            logger.error(e.getMessage(), e);
            return getFaultResultMsg(e.getMessage(), JsonDataErrorCode.FormDefinitionUpdateException);
        } catch (HibernateException e) {
            logger.error(e.getMessage(), e);
            return getFaultResultMsg(
                    MsgUtils.getMessage("common.exception.hibernate", new Object[]{e.getMessage()}),
                    JsonDataErrorCode.FormDefinitionUpdateException);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return getFaultResultMsg(MsgUtils.getMessage("common.exception.unknown", new Object[]{e.getMessage()}),
                    JsonDataErrorCode.FormDefinitionUpdateException);
        }

        return getSucessfulResultMsg(dy.getUuid());
    }

    /**
     * 保存表单信息
     *
     * @param rootTableInfo
     * @param request
     * @return
     * @throws Exception
     */
    @ResponseBody
    @ApiOperation(value = "保存表单定义", notes = "保存表单定义")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "formDefinition", value = "表单定义JSON", paramType = "query", dataType = "String", required = true)})
    @PostMapping(value = "/save")
    public ResponseEntity<ResultMessage> save(@RequestParam(value = "formDefinition") String formDefinitionJson)
            throws Exception {
        FormDefinition formDefinition = JsonUtils.json2Object(formDefinitionJson, FormDefinition.class);

        formDefinition.setUuid(null);
        // String defintionJson = formDefinition.getDefinitionJson();

        if (this.formDefinitionService.isFormExistById(formDefinition.getId())) {
            return getFaultResultMsg("表单ID[" + formDefinition.getId() + "]已存在！",
                    JsonDataErrorCode.FormDefinitionSaveException);

        }

        if (DyformTypeEnum.isPform(formDefinition.getFormType())
                && this.formDefinitionService.isFormExistByFormTblName(formDefinition.getName())) {// 如果为存储单据，则定义所指的表名不得已存在
            return getFaultResultMsg("tableName[" + formDefinition.getName() + "] has exist  ",
                    JsonDataErrorCode.FormDefinitionSaveException);
        }

        try {
            if (StringUtils.isBlank(formDefinition.getUuid()) && StringUtils.isNotBlank(formDefinition.getDefinitionVjson())) {
                net.sf.json.JSONObject object = net.sf.json.JSONObject.fromObject(formDefinition.getDefinitionVjson());
                if (object.containsKey("fields") && object.getJSONArray("fields").size() == 0) {
                    // 无字段保存，直接保存定义，暂不生成数据库表
                    formDefinition.setSystem(RequestSystemContextPathResolver.system());
                    formDefinition.setTenant(SpringSecurityUtils.getCurrentTenantId());
                    formDefinition.doBindVersionAsMinVersion();
                    formDefinitionService.save(formDefinition);
                    ApplicationContextHolder.getBean(AuditDataFacadeService.class)
                            .saveAuditDataLog(new AuditDataLogDto().diffEntity(formDefinition, null).name(formDefinition.getName()).remark("创建表单"));
                    return getSucessfulResultMsg(formDefinition.getUuid());
                }
            }


            // 保存表单数据表定义信息并生成表单数据表
            this.formDefinitionService.createFormDefinitionAndFormTable(formDefinition);
        } catch (HibernateDdlException e) {
            logger.error(e.getMessage(), e);
            return getFaultResultMsg(e.getMessage(), JsonDataErrorCode.FormDefinitionSaveException);
        } catch (HibernateException e) {
            logger.error(e.getMessage(), e);
            return getFaultResultMsg(
                    MsgUtils.getMessage("common.exception.hibernate", new Object[]{e.getMessage()}),
                    JsonDataErrorCode.FormDefinitionSaveException);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return getFaultResultMsg(MsgUtils.getMessage("common.exception.unknown", new Object[]{e.getMessage()}),
                    JsonDataErrorCode.FormDefinitionSaveException);
        }

        return getSucessfulResultMsg(formDefinition.getUuid());
    }

    /**
     * 获取表单定义
     *
     * @param fieldDefinitionService
     * @param tableId
     * @return
     */
    @IgnoreResultAdvice
    @PostMapping(value = "/getFormDefinition", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取表单定义", notes = "获取表单定义")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "formUuid", value = "表单定义UUID", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "justDataAndDef", value = "是否取数据及定义", paramType = "query", dataType = "String", required = true)})
    public @ResponseBody
    String findFormDefinition(@RequestParam("formUuid") String formUuid,
                              @RequestParam(value = "justDataAndDef", required = false, defaultValue = "true") Boolean justDataAndDef,
                              HttpServletRequest request, HttpServletResponse response) {
        DyFormData dyFormData = dyFormFacade.getDyFormData(formUuid, null, BooleanUtils.isTrue(justDataAndDef));
        return dyFormData.getFormDefinition();
    }

    @GetMapping("/queryModuleFormDefinition")
    public @ResponseBody
    ApiResult<List<FormDefinition>> getModuleFormDefinition(@RequestParam(required = false) String moduleId, @RequestParam(required = false) Boolean vJson) {
        return ApiResult.success(formDefinitionService.getModuleFormDefinitionWithoutJson(moduleId, vJson));
    }


    @PostMapping("/queryFormDefinitionByModuleIds")
    public @ResponseBody
    ApiResult<List<FormDefinition>> queryFormDefinitionByModuleIds(@RequestBody List<String> moduleIds) {
        return ApiResult.success(formDefinitionService.queryFormDefinitionByModuleIds(moduleIds));
    }

    @PostMapping("/queryFormDefinitionNoJsonByModuleIds")
    public @ResponseBody
    ApiResult<List<FormDefinition>> queryFormDefinitionNoJsonByModuleIds(@RequestBody List<String> moduleIds) {
        return ApiResult.success(formDefinitionService.queryFormDefinitionNoJsonByModuleIds(moduleIds));
    }

    @GetMapping("/queryFormDefinitionIgnoreJsonByKeyword")
    public @ResponseBody
    ApiResult<List<QueryItem>> queryFormDefinitionIgnoreJsonByKeyword(@RequestParam(required = false) String keyword,
                                                                      @RequestParam(required = false) Integer pageSize,
                                                                      @RequestParam(required = false) Integer pageIndex
    ) {
        return ApiResult.success(formDefinitionService.queryFormDefinitionIgnoreJsonByKeyword(keyword, pageSize, pageIndex));
    }


    @PostMapping("/queryFormDefinitionIgnoreJsonByModuleIds")
    public @ResponseBody
    ApiResult<List<FormDefinition>> queryFormDefinitionIgnoreJsonByModuleIds(@RequestBody List<String> moduleIds) {
        return ApiResult.success(formDefinitionService.queryFormDefinitionIgnoreJsonByModuleIds(moduleIds));
    }

    @GetMapping("/queryFormDefinitionIgnoreJsonByDataModelId")
    public @ResponseBody
    ApiResult<List<FormDefinition>> queryFormDefinitionIgnoreJsonByModuleIds(@RequestParam("dataModelId") String dataModelId) {
        return ApiResult.success(formDefinitionService.queryFormDefinitionIgnoreJsonByTableName("UF_" + StringUtils.upperCase(dataModelId)));
    }

    /**
     * 获取表单定义
     *
     * @param formUuid
     * @return
     */
    @IgnoreResultAdvice
    @PostMapping(value = "/getFormDefinitionById", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取表单定义", notes = "获取表单定义")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "表单定义ID", paramType = "query", dataType = "String", required = true)})
    public @ResponseBody
    FormDefinition getFormDefinitionById(@RequestParam("id") String id, @RequestParam(value = "i18n", required = false) Boolean i18n) {
        FormDefinition definition = formDefinitionService.getDyFormFormDefinitionOfMaxVersionById(id);
        if (BooleanUtils.isTrue(i18n)) {
            definition.setI18ns(appDefElementI18nService.getI18ns(definition.getId(), null, new BigDecimal(definition.getVersion()), IexportType.DyFormDefinition, LocaleContextHolder.getLocale().toString()));
        }
        return definition;
    }


    /**
     * 获取表单定义
     *
     * @param formUuid
     * @return
     */
    @IgnoreResultAdvice
    @PostMapping(value = "/getFormDefinitionByUuid", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取表单定义", notes = "获取表单定义")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "formUuid", value = "表单定义UUID", paramType = "query", dataType = "String", required = true)})
    public @ResponseBody
    FormDefinition getFormDefinitionByUuid(@RequestParam("formUuid") String formUuid, @RequestParam(value = "i18n", required = false) Boolean i18n) {
        FormDefinition definition = formDefinitionService.getOne(formUuid);
        if (BooleanUtils.isTrue(i18n)) {
            definition.setI18ns(appDefElementI18nService.getI18ns(definition.getId(), null, new BigDecimal(definition.getVersion()), IexportType.DyFormDefinition, LocaleContextHolder.getLocale().toString()));
        }
        return definition;
    }

    /**
     * 获取表单定义
     *
     * @param ids
     * @return
     */
    @GetMapping(value = "/listByIds", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取表单定义", notes = "获取表单定义")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", value = "表单定义ID列表", paramType = "query", dataType = "List<String>", required = true)})
    public @ResponseBody
    List<FormDefinition> listByIds(@RequestParam("ids") List<String> ids) {
        return formDefinitionService.listByIds(ids);
    }

    @IgnoreResultAdvice
    @GetMapping("/getPreservedField")
    @ApiOperation(value = "获取表单预留字段", notes = "获取表单预留字段")
    public @ResponseBody
    HashSet<String> getPreservedField() {
        HashSet<String> names = Sets.newHashSet();
        for (EnumSystemField field : EnumSystemField.values()) {
            names.add(field.getName());
        }
        for (EnumRelationTblSystemField field : EnumRelationTblSystemField.values()) {
            names.add(field.getName());
        }
        for (EnumDbWords field : EnumDbWords.values()) {
            names.addAll(field.items());
        }
        return names;
    }

    @IgnoreResultAdvice
    @PostMapping(value = "/getFieldDictionaryOptionSet", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取表单字段选项", notes = "获取表单字段选项", response = ResultMessage.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "formUuid", value = "表单定义UUID", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "fieldName", value = "字段编码", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "paramsObj", value = "参数", paramType = "query", dataType = "String", required = true, example = "数据字典：JSON.stringify({dictCode:'字典类型'})，其中dictCode传数据字典类型<br>数据仓库：JSON.stringify({key1:'value1',...})，对象为数据仓库的参数（可选），其中dataSourceId传数据仓库ID（可选）")})
    public @ResponseBody
    String getFieldDictionaryOptionSet(@RequestBody FieldDictionaryOptionRequest request) {
        // ResultMessage result = new ResultMessage();
        String formUuid = request.getFormUuid();
        String fieldName = request.getFieldName();
        String paramsObj = request.getParamsObj();
        try {
            Map<String, Object> params = null;
            if (StringUtils.isNotBlank(paramsObj)) {
                params = JsonUtils.json2Object(paramsObj, Map.class);
            }
            // JsonUtils.json2Object报错返回空
            if (null == params) {
                params = Maps.newHashMap();
            }
            FormDefinition formDefinition = (FormDefinition) dyFormFacade.getFormDefinition(formUuid);
            FormDefinitionHandler handler = formDefinition.doGetFormDefinitionHandler();
            // 重写数据字典类型或者数据源ID
            String optionDataSource = handler.getFieldPropertyOfStringType(fieldName,
                    EnumFieldPropertyName.optionDataSource);
            if (DyFormConfig.DyDataSourceType.dataDictionary.equals(optionDataSource)) {// 从数据字段取值,这里取得数据字段树
                String dictType = EnumFieldPropertyName.dictCode.name();
                if (params.containsKey(dictType)) {
                    handler.addFieldProperty(fieldName, EnumFieldPropertyName.dictCode, params.get(dictType));
                    params.remove(dictType);
                }
            } else if (DyFormConfig.DyDataSourceType.dataSource.equals(optionDataSource)) {
                String dataSourceId = EnumFieldPropertyName.dataSourceId.name();
                if (params.containsKey(dataSourceId)) {
                    handler.addFieldProperty(fieldName, EnumFieldPropertyName.dataSourceId, params.get(dataSourceId));
                    params.remove(dataSourceId);
                }
            }
            handler.loadFieldDictionaryOptionSet(dataDictionaryService, cdDataStoreService, fieldName, params, false);
            Object optSet = handler.getFieldDictionaryOptionSet(fieldName);
            if (optSet instanceof JSONObject || optSet instanceof JSONArray) {
                return optSet.toString();// result.setData(optSet.toString());
            } else {
                return JsonUtils.object2Json(optSet);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            // result.setSuccess(false);
            // result.setMsg(new StringBuilder(ex.getMessage()));
        }
        return "[]";
    }

    private static class FieldDictionaryOptionRequest extends BaseObject {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = -6220050477856116837L;

        private String formUuid;
        private String fieldName;
        private String paramsObj;

        /**
         * @return the formUuid
         */
        public String getFormUuid() {
            return formUuid;
        }

        /**
         * @param formUuid 要设置的formUuid
         */
        public void setFormUuid(String formUuid) {
            this.formUuid = formUuid;
        }

        /**
         * @return the fieldName
         */
        public String getFieldName() {
            return fieldName;
        }

        /**
         * @param fieldName 要设置的fieldName
         */
        public void setFieldName(String fieldName) {
            this.fieldName = fieldName;
        }

        /**
         * @return the paramsObj
         */
        public String getParamsObj() {
            return paramsObj;
        }

        /**
         * @param paramsObj 要设置的paramsObj
         */
        public void setParamsObj(String paramsObj) {
            this.paramsObj = paramsObj;
        }


    }
}
