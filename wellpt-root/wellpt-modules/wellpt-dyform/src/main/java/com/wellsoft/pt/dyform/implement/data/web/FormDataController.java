/*

 * @(#)2012-10-30 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dyform.implement.data.web;

import com.wellsoft.context.enums.JsonDataErrorCode;
import com.wellsoft.context.jdbc.entity.TenantEntity;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.date.DateUtil;
import com.wellsoft.context.util.encode.JsonBinder;
import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseFormDataController;
import com.wellsoft.context.web.controller.IgnoreResultAdvice;
import com.wellsoft.context.web.controller.ResultMessage;
import com.wellsoft.pt.basicdata.datadict.service.DataDictionaryService;
import com.wellsoft.pt.basicdata.datastore.bean.DataStoreOrder;
import com.wellsoft.pt.basicdata.datastore.bean.DataStoreParams;
import com.wellsoft.pt.basicdata.datastore.facade.service.CdDataStoreService;
import com.wellsoft.pt.basicdata.datastore.support.Condition;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.dto.DyFormFormDefinition;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.dyform.facade.support.DyformTitleUtils;
import com.wellsoft.pt.dyform.implement.data.dto.SqlQueryDslObj;
import com.wellsoft.pt.dyform.implement.definition.entity.FormDefinition;
import com.wellsoft.pt.jpa.criterion.CriterionOperator;
import com.wellsoft.pt.jpa.template.TemplateEngineFactory;
import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;
import com.wellsoft.pt.repository.service.MongoFileService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.HtmlUtils;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Description: 动态表单数据Controller类
 *
 * @author jiangmb
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-10-31.1	jiangmb		2012-10-31		Create
 * 2014-5-8.1	hunt		2014-5-8		定义JSON化
 * </pre>
 * @date 2012-10-30
 */
@Api(tags = "表单数据接口")
@IgnoreResultAdvice
@Controller
@RequestMapping({"/pt/dyform/data", "/api/dyform/data"})
public class FormDataController extends BaseFormDataController {
    private static final int BUFFER_LENGTH = 1024 * 16;
    private static final Pattern RANGE_PATTERN = Pattern.compile("bytes=(?<start>\\d*)-(?<end>\\d*)");
    @Autowired
    DyFormFacade dyFormFacade;
    @Autowired
    MongoFileService mongoFileService;




    /*
     * @Autowired private TaskService taskService;
     *
     * @Autowired private FlowService flowService;
     */

    //	/**
//	 * 测试删除表单数据
//	 */
//	@RequestMapping(value = "/testDelFullFormData")
//	@ResponseBody
//	public void TestDelFullFormData(@RequestParam("formUuid") String formUuid,
//			@RequestParam("dataUuid") String dataUuid, HttpServletRequest request, HttpServletResponse response) {
//		dyFormFacade.delFullFormData(formUuid, dataUuid);
//	}
    @Autowired
    DataDictionaryService dataDictionaryService;
    private Logger logger = LoggerFactory.getLogger(FormDataController.class);

    /**
     * 检查数据是否存在
     *
     * @return
     * @throws JSONException
     */
    @RequestMapping(value = "/validate/exists", method = RequestMethod.POST)
    @ApiOperation(value = "检查数据是否存在", notes = "检查数据是否存在")
    public ResponseEntity<ResultMessage> checkExists(@RequestBody String jsonData) {
        JSONObject jsonObject;
        String checkType = null;
        String uuid = null;
        String fieldName = null;
        String fieldValue = null;
        boolean isUnitUnique = false;
        try {
            jsonObject = new JSONObject(jsonData);
            checkType = (jsonObject.has("tblName") ? (String) jsonObject.get("tblName") : "");
            fieldName = (jsonObject.has("fieldName") ? (String) jsonObject.get("fieldName") : "");
            fieldValue = (jsonObject.has("fieldValue") ? (String) jsonObject.get("fieldValue") : "");
            isUnitUnique = jsonObject.has("unitUnique") ? (boolean) jsonObject.get("unitUnique") : false;//单位唯一性标记
            uuid = (jsonObject.has("uuid") ? (String) jsonObject.get("uuid") : "");
        } catch (JSONException e1) {
            logger.error(e1.getMessage(), e1);
        }

        boolean isExist = false;
        if (StringUtils.isBlank(uuid)) {
            try {
                isExist = isUnitUnique ? dyFormFacade.queryFormDataExists(checkType, new String[]{fieldName,
                                TenantEntity.SYSTEM_UNIT_ID4DB},
                        new String[]{fieldValue, SpringSecurityUtils.getCurrentUserUnitId()}) : dyFormFacade
                        .queryFormDataExists(checkType, fieldName, fieldValue);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                return getFaultResultMsg(e.getMessage(), JsonDataErrorCode.ParameterException);
            }
        } else {
            try {
                isExist = isUnitUnique ? dyFormFacade.queryFormDataExists(checkType, new String[]{fieldName,
                                TenantEntity.SYSTEM_UNIT_ID4DB},
                        new String[]{fieldValue, SpringSecurityUtils.getCurrentUserUnitId()}, uuid) : dyFormFacade
                        .queryFormDataExists(checkType, fieldName, fieldValue, uuid);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                return getFaultResultMsg(e.getMessage(), JsonDataErrorCode.ParameterException);
            }
        }
        return getSucessfulResultMsg(new Boolean(isExist));
    }

    /**
     * 保存表单数据
     *
     * @param jsonData
     * @return
     * @throws JSONException
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     * @throws ParseException
     */
    @RequestMapping(value = "/saveFormData", method = RequestMethod.POST)
    @ApiOperation(value = "检查数据是否存在", notes = "检查数据是否存在")
    public ResponseEntity<ResultMessage> saveFormData(@RequestBody String jsonData, HttpServletRequest request,
                                                      HttpServletResponse response) throws JSONException, JsonParseException, JsonMappingException, IOException,
            ParseException {

        DyFormData dyFormData = this.dyFormFacade.parseDyformData(jsonData);// JsonBinder.buildNormalBinder().fromJson(jsonData,
        // DyFormData.class);
        if (dyFormData == null) {
            logger.error("数据解析失败: " + jsonData);
            return getFaultResultMsg("前台传入的数据解析失败", JsonDataErrorCode.FormDefinitionSaveException);
        }

        // dyFormData.setFieldValue("selectest", "DYBTN_WF_ADMIN");
        // dyFormData.setFieldValue("selectest", 1);
        // dyFormData.setFieldValue("test3", "{'2':'机关事业单位'}");
        // dyFormData.setFieldValue("test3", "2");

        // dyFormData.setFieldValue("ZC", null);
        try {
            return getSucessfulResultMsg(this.dyFormFacade.saveFormData(dyFormData));
        } catch (Exception ex) {
            return processException(ex);
        }
    }

    @RequestMapping(value = "/saveDataModelFormDataNewVersion/{dataUuid}", method = RequestMethod.POST)
    @ApiOperation(value = "数据模型的表单数据保存为新版本", notes = "数据模型的表单数据保存为新版本")
    public ResponseEntity<ResultMessage> saveDataModelFormDataNewVersion(@PathVariable String dataUuid, @RequestBody String jsonData) {

        DyFormData dyFormData = this.dyFormFacade.parseDyformData(jsonData);
        if (dyFormData == null) {
            logger.error("数据解析失败: " + jsonData);
            return getFaultResultMsg("前台传入的数据解析失败", JsonDataErrorCode.FormDefinitionSaveException);
        }
        try {
            return getSucessfulResultMsg(this.dyFormFacade.saveDataModelFormDataAsNewVersion(dyFormData, dataUuid));
        } catch (Exception ex) {
            return processException(ex);
        }
    }


    @RequestMapping(value = "/updateFieldValue", method = RequestMethod.POST)
    @ApiOperation(value = "更新字段值", notes = "更新字段值")
    public ResponseEntity<ResultMessage> updateFieldValue(@RequestBody String jsonData) throws JSONException {
        // 前端上传的参数
        //        jsonData = "{\"uuid\":\"1fc6a3c516854ade96d8338f97ce61b3\",\"formUuid\":\"f33102e1-5459-4c90-a5b6-983cb04a02d2\",\"fieldName\":\"t1\",\"fieldValue\":\"1\"}";
        JSONObject obj = new JSONObject(jsonData);
        String formUuid = obj.getString("formUuid");
        String uuid = obj.getString("uuid");
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("uuid", uuid);
        JSONObject fieldValueMap = obj.getJSONObject("fieldValueMap");
        Set<String> fields = fieldValueMap.keySet();
        for (String field : fields) {
            param.put(field, fieldValueMap.get(field));
        }
        Boolean result = dyFormFacade.updateFieldValue(formUuid, param);
        ResultMessage message = null;
        if (result) {
            message = new ResultMessage();
        } else {
            message = new ResultMessage(null, false);
        }
        return new ResponseEntity<ResultMessage>(message, HttpStatus.OK);
    }

//	@RequestMapping(value = "/editFormData", method = RequestMethod.GET)
//	@ApiOperation(value = "编辑表单数据", notes = "编辑表单数据")
//	public ResponseEntity<ResultMessage> editFormData(@RequestParam("formUuid") String formUuid,
//			@RequestParam("dataUuid") String dataUuid) {
//
//		DyFormData dyformdata = this.dyFormFacade.getDyFormData(formUuid, dataUuid);
//
//		DyFormData subformdata = dyformdata.getDyFormData("bfa0c271-0412-4c89-ba61-884d5c6f3fa6",
//				"24cdc0ae-8bd8-43f5-9e47-b920f7038566");
//
//		// subformdata.setFieldValue("radio", "1");
//
//		this.dyFormFacade.saveFormData(dyformdata);
//
//		return getSucessfulResultMsg(dyformdata);
//	}

    /**
     * 校验表单数据
     *
     * @param jsonData
     * @return
     * @throws JSONException
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     * @throws ParseException
     */
    @RequestMapping(value = "/validateFormData", method = RequestMethod.POST)
    @ApiOperation(value = "校验表单数据", notes = "校验表单数据")
    public ResponseEntity<ResultMessage> validateFormData(@RequestBody String jsonData) {
        DyFormData dyFormData = dyFormFacade.parseDyformData(jsonData);
        //		dyFormData.setFieldValue("YQSCBLJGWJLX", "{\"12345\":\"54321\"}");// 合规性校验
        //		dyFormData.addValidator("SXMC", new Validator() {
        //
        //			@Override
        //			public ValidatorResult isValid(Object value, ValidatorFieldContent fieldDefinition, Map<String, Object> params) {
        //				return new ValidatorResult(false, "总是错");
        //			}
        //		});// 自定义主表校验
        //		dyFormData.addValidator("2874a4e2-ec50-4507-9d16-0ff7beb4b72e", "GZR", new Validator() {
        //
        //			@Override
        //			public ValidatorResult isValid(Object value, ValidatorFieldContent fieldDefinition, Map<String, Object> params) {
        //				return new ValidatorResult(false, "工作日错误");
        //			}
        //		});// 自定义从表校验
        return getSucessfulResultMsg(dyFormData.validateFormData());
    }

    @RequestMapping(value = "/getFormData", method = RequestMethod.POST)
    @ApiOperation(value = "获取表单数据", notes = "获取表单数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "formUuid", value = "表单定义UUID", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "dataUuid", value = "表单数据UUID", paramType = "query", dataType = "String", required = true)})
    public ResponseEntity<ResultMessage> getFormData(@RequestParam("formUuid") String formUuid,
                                                     @RequestParam("dataUuid") String dataUuid) {
        Map<String/* 表单定义uuid */, List<Map<String /* 表单字段名 */, Object/* 表单字段值 */>>> formDatas = new HashMap<String, List<Map<String, Object>>>();

        formDatas = dyFormFacade.getFormData(formUuid, dataUuid);

        return getSucessfulResultMsg(formDatas);
    }

    @RequestMapping(value = "/getFormDefinitionData", method = RequestMethod.POST)
    @ApiOperation(value = "获取表单定义和数据", notes = "获取表单定义和数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "formUuid", value = "表单定义UUID", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "dataUuid", value = "表单数据UUID", paramType = "query", dataType = "String", required = true)})
    public ResponseEntity<ResultMessage> getFormDefinitionData(@RequestParam("formUuid") String formUuid,
                                                               @RequestParam("dataUuid") String dataUuid) {
        try {
            DyFormData dyformdata = dyFormFacade.getDyFormData(formUuid, dataUuid);
            return getSucessfulResultMsg(dyformdata);
        } catch (Exception ex) {
            return getFaultResultMsg(ex, JsonDataErrorCode.FormDefinitionFomatException);
        }
    }

    @RequestMapping(value = "/getFormDefinitionDataByFormId", method = RequestMethod.POST)
    @ApiOperation(value = "获取表单定义和数据", notes = "获取表单定义和数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "formId", value = "表单定义ID", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "dataUuid", value = "表单数据UUID", paramType = "query", dataType = "String", required = true)})
    public ResponseEntity<ResultMessage> getFormDefinitionDataByFormId(@RequestParam("formId") String formId,
                                                                       @RequestParam("dataUuid") String dataUuid) {
        try {
            String formUuid = this.dyFormFacade.getFormUuidById(formId);
            DyFormData dyformdata = dyFormFacade.getDyFormData(formUuid, dataUuid);
            return getSucessfulResultMsg(dyformdata);
        } catch (Exception ex) {
            return getFaultResultMsg(ex.getMessage(), JsonDataErrorCode.FormDefinitionFomatException);
        }
    }

    /*
     * @RequestMapping(value = "/getMainFormData", method = RequestMethod.POST)
     * public ResponseEntity<ResultMessage> getMainFormData(String formUuid,
     * String dataUuid) { Map<String 表单字段名, Object表单字段值> formDatas = new
     * HashMap<String, Object>(); formDatas =
     * dyFormFacade.getFormDataOfMainform(formUuid, dataUuid); return
     * getSucessfulResultMsg(formDatas); }
     */

    /**
     * 全量重写表单数据测试
     *
     * @param formUuid
     * @param dataUuid
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/rewriteFormdata")
    @ApiOperation(value = "全量重写表单数据", notes = "全量重写表单数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "formUuid", value = "表单定义ID", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "dataUuid", value = "表单数据UUID", paramType = "query", dataType = "String", required = true)})
    @ResponseBody
    public String rewriteFormdata(@RequestParam("formUuid") String formUuid, @RequestParam("dataUuid") String dataUuid,
                                  HttpServletRequest request, HttpServletResponse response) {
        DyFormData dyFormData = dyFormFacade.getDyFormData(formUuid, dataUuid);
        this.dyFormFacade.rewriteFormData(dyFormData);
        return "ok";
    }

    @RequestMapping(value = "/getFormDataOfChildNode4ParentNode", method = RequestMethod.POST)
    @ApiOperation(value = "获取从表子行数据", notes = "获取从表子行数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "formUuidOfSubform", value = "从表定义UUID", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "formUuidOfMainform", value = "主表定义UUID", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "dataUuidOfMainform", value = "主表数据UUID", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "dataUuidOfParentNode", value = "从表行数据UUID", paramType = "query", dataType = "String", required = true)})
    public ResponseEntity<ResultMessage> getFormDataOfChildNode4ParentNode(
            @RequestParam("formUuidOfSubform") String formUuidOfSubform,
            @RequestParam("formUuidOfMainform") String formUuidOfMainform,
            @RequestParam("dataUuidOfMainform") String dataUuidOfMainform,
            @RequestParam("dataUuidOfParentNode") String dataUuidOfParentNode) {
        List<Map<String /* 表单字段名 */, Object/* 表单字段值 */>> formDatas = null;

        formDatas = dyFormFacade.getFormDataOfChildNode4ParentNode(formUuidOfSubform, formUuidOfMainform,
                dataUuidOfMainform, dataUuidOfParentNode);

        return getSucessfulResultMsg(formDatas);
    }

    @RequestMapping(value = "/getDefaultFormData", method = RequestMethod.POST)
    @ApiOperation(value = "获取表单默认数据", notes = "获取表单默认数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "formUuid", value = "表单定义ID", paramType = "query", dataType = "String", required = true)})
    public ResponseEntity<ResultMessage> getDefaultFormData(@RequestParam("formUuid") String formUuid) {
        Map<String, Object> formData = new HashMap<String, Object>();
        try {
            formData = dyFormFacade.getDefaultFormData(formUuid);
            return getSucessfulResultMsg(formData);
        } catch (JSONException e) {
            logger.error(e.getMessage(), e);
            return getFaultResultMsg(e.getMessage(), JsonDataErrorCode.FormDefinitionFomatException);
        }
    }

    @RequestMapping(value = "/getFormDataOfParentNode", method = RequestMethod.POST)
    @ApiOperation(value = "通过从表数据获取表单主表数据", notes = "通过从表数据获取表单主表数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "formUuidOfSubform", value = "从表定义UUID", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "formUuidOfMainform", value = "主表定义UUID", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "dataUuidOfMainform", value = "主表数据UUID", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "pageSize", value = "每页大小", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "currentPageNo", value = "当前页码", paramType = "query", dataType = "String", required = true)})
    public ResponseEntity<ResultMessage> getFormDataOfParentNode(String formUuidOfSubform, String formUuidOfMainform,
                                                                 String dataUuidOfMainform, int pageSize, int currentPageNo) {
        PagingInfo pagingInfo = new PagingInfo();
        pagingInfo.setAutoCount(true);
        pagingInfo.setCurrentPage(currentPageNo);
        pagingInfo.setPageSize(pageSize);

        return getSucessfulResultMsg(this.dyFormFacade.getFormDataOfParentNodeByPage(formUuidOfSubform,
                formUuidOfMainform, dataUuidOfMainform, pagingInfo));

    }

    @RequestMapping(value = "/getDigestValue", method = RequestMethod.POST)
    @ApiOperation(value = "获取摘要", notes = "获取摘要")
    public ResponseEntity<ResultMessage> getDigestValue(@RequestBody String signedContent) {
        return getSucessfulResultMsg(this.dyFormFacade.getDigestValue(signedContent));
    }

    @ApiIgnore
    @RequestMapping("/demo")
    public String open(@RequestParam(value = "formDefinition") String formDefinition, Model model) {
        model.addAttribute("formDefinition", formDefinition);
        return forward("/dyform/dyform_demo");
    }

//	@RequestMapping(value = "/getFormDataByFlowInstance")
//	@ResponseBody
//	public String getFormDataByFlowInstance(@RequestParam("formUuid") String formUuid,
//			@RequestParam("flowInstanceBh") String flowInstanceBh, @RequestParam("lshName") String lshName) {
//		QueryItem queryItem = new QueryItem();
//		String selection = " 1=1 and " + lshName + "='" + flowInstanceBh + "'";
//		List<QueryItem> queryItemList = dyFormFacade.query(formUuid, null, selection, null, "", "", "", 0, 99999);
//		if (queryItemList.size() > 0) {
//			queryItem = queryItemList.get(0);
//			Object checkResult = queryItem.get("checkResultJson");
//			if (checkResult instanceof Clob) {
//				return ClobUtils.ClobToString((Clob) checkResult);
//			} else if (checkResult instanceof String) {
//				return checkResult.toString();
//			}
//		}
//		return null;
//	}

    /**
     * 描述：表单模板
     *
     * @param uuid
     * @param form_uuid
     * @param isEdit
     * @param map
     * @return
     */
    @ApiIgnore
    @RequestMapping(value = "/template")
    public String template(@RequestParam(value = "dataUuid", required = false) String dataUuid,
                           @RequestParam(value = "formUuid", required = false) String formUuid,
                           @RequestParam(value = "isEdit", required = true, defaultValue = "false") String isEdit, // 是否是编辑功能
                           @RequestParam(value = "isNew", required = true, defaultValue = "true") String isNew, // 是否是新建功能
                           Model map) {
        map.addAttribute("dataUuid", dataUuid);// uuid:表单的uuid
        map.addAttribute("formUuid", formUuid);
        // 是否编辑标识
        map.addAttribute("isEdit", isEdit);
        map.addAttribute("isNew", isNew);
        return "pt/dyform/dyform_template";
    }

    @RequestMapping(value = "/delete")
    @ApiOperation(value = "删除表单数据", notes = "删除表单数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dataUuid[]", value = "表单数据UUID数组", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "formUuid", value = "表单定义UUID", paramType = "query", dataType = "String", required = true)})
    @ResponseBody
    public String delete(@RequestParam(value = "dataUuid[]", required = true) String[] dataUuid,
                         @RequestParam(value = "formUuid", required = true) String formUuid) {
        String[] uuidArr = dataUuid;
        for (int i = 0; i < uuidArr.length; i++) {
            dyFormFacade.delFormData(formUuid, uuidArr[i]);
        }
        return "success";

    }

    @RequestMapping(value = "/dynamicQuery")
    @ApiOperation(value = "表单数据动态查询", notes = "表单数据动态查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "formUuid", value = "表单定义UUID", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "projection[]", value = "查询列名数组", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "selection", value = "查询语句", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "selectionArgs[]", value = "查询语句参数", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "groupBy", value = "分组信息", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "having", value = "分组条件", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "orderBy", value = "排序信息", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "firstResult", value = "第一条数据索引", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "maxResults", value = "最多查询条数", paramType = "query", dataType = "String", required = true)})
    @ResponseBody
    public List<QueryItem> dynamicQueryFormData(@RequestParam("formUuid") String formUuid,
                                                @RequestParam("projection[]") String[] projection, @RequestParam("selection") String selection,
                                                @RequestParam("selectionArgs[]") String[] selectionArgs, @RequestParam("groupBy") String groupBy,
                                                @RequestParam("having") String having, @RequestParam("orderBy") String orderBy,
                                                @RequestParam("firstResult") int firstResult, @RequestParam("maxResults") int maxResults) {
        selection = StringEscapeUtils.unescapeHtml4(selection);
        List<QueryItem> datas = dyFormFacade.query(formUuid, projection, selection, selectionArgs, groupBy, having,
                orderBy, firstResult, maxResults);
        return datas;
    }

    @RequestMapping(value = "/autoComplete", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "searchText", value = "查询文本", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "fields", value = "查询字段", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "dataSourceId", value = "数据仓库ID", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "outfields", value = "其他字段", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "contraint", value = "条件", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "pageSize", value = "条数", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "currentPage", value = "当前页码", paramType = "query", dataType = "String", required = true)})
    @ApiOperation(value = "表单下拉选项自动完成", notes = "表单下拉选项自动完成")
    public @ResponseBody
    String autoComplete(@RequestParam("searchText") String searchText,
                        @RequestParam("fields") String fields, @RequestParam("dataSourceId") String dataSourceId,
                        @RequestParam("outfields") String outfields, @RequestParam("contraint") String contraint,
                        @RequestParam("pageSize") String pageSize,
                        @RequestParam(value = "currentPage", required = false) Integer currentPage) throws JSONException,
            JsonParseException, JsonMappingException, IOException, ParseException {
        if (StringUtils.isBlank(pageSize) || StringUtils.equals(pageSize.trim(), "undefined")) {
            pageSize = "300";
        }
        if (currentPage == null || currentPage <= 0) {
            currentPage = 1;
        }
        Map<String, Object> params = new HashMap<String, Object>();
        CdDataStoreService cdDataStoreService = (CdDataStoreService) ApplicationContextHolder
                .getBean("cdDataStoreService");
        List<String> fieldList = JsonBinder.buildNormalBinder().fromJson(fields, List.class);
        DataStoreParams dataStoreParams = new DataStoreParams();
        dataStoreParams.setDataStoreId(dataSourceId);
        dataStoreParams.setParams(params);
        // 精确查询
        JSONObject contraintJ = new JSONObject(contraint);
        Iterator<String> it = contraintJ.keys();
        Object[] obj = new Object[]{};
        boolean sapDs = false;
        if (obj != null && obj.length > 0 && ((String) obj[0]).equalsIgnoreCase("sap")) {// sap接口
            sapDs = true;
        }
        while (it.hasNext()) {
            String fieldName = it.next();
            String value = null;
            if (contraintJ.has(fieldName) && contraintJ.get(fieldName) != null
                    && StringUtils.isNotBlank(value = contraintJ.get(fieldName).toString())) {
                dataStoreParams.getCriterions().add(
                        new Condition(sapDs ? fieldName + "___MUST" : fieldName, value, CriterionOperator.eq));
                params.put(sapDs ? fieldName + "___MUST" : fieldName, value);
            }
        }
        // 模糊查询
        String orderField = "";
        List<Condition> criterions = new ArrayList<Condition>(0);
        for (String fieldName : fieldList) {
            if (params.containsKey(fieldName)) {
                continue;
            }
            criterions.add(new Condition(fieldName, searchText, CriterionOperator.like));
            orderField = fieldName;
        }
        Condition condition = new Condition();
        condition.setType(CriterionOperator.or.getType());
        condition.setConditions(criterions);
        dataStoreParams.getCriterions().add(condition);
        // 设置分页
        PagingInfo pagingInfo = new PagingInfo();
        pagingInfo.setCurrentPage(currentPage);
        pagingInfo.setPageSize(Integer.parseInt(pageSize));
        if (StringUtils.isNotBlank(orderField)) {
            // 设置排序
            dataStoreParams.getOrders().add(new DataStoreOrder(orderField, true));
        }

        long time1 = System.currentTimeMillis();
        List<Map<String, Object>> items = cdDataStoreService.loadData(dataStoreParams).getData();
        long time2 = System.currentTimeMillis();
        logger.info("dyform data autocomplete dataSourceId=" + dataSourceId + " spent " + (time2 - time1) / 1000 + "s");
        int i = 0;
        JSONArray array = new JSONArray();
        List<String> outfieldList = JsonBinder.buildNormalBinder().fromJson(outfields, List.class);
        for (Map<String, Object> item : items) {
            if (i > Integer.parseInt(pageSize)) {// 超出分页的条数时直接舍弃
                break;
            }
            JSONObject json = new JSONObject();
            for (String of : outfieldList) {
                if (item.containsKey(of) && item.get(of) != null) {
                    json.put(of, item.get(of));
                }
            }
            array.put(json);
            i++;
        }

        return array.toString();
    }

    @ApiIgnore
    @RequestMapping(value = "/uploadImage")
    @ResponseBody
    public void uploadImage(@RequestParam(value = "upload") MultipartFile upload,
                            @RequestParam(value = "folderId", required = false) String folderId, HttpServletRequest request,
                            HttpServletResponse response) {
        String fileId = "";
        String alterMsg = "上传成功";
        String CKEditorFuncNum = request.getParameter("CKEditorFuncNum");
        folderId = StringUtils.isBlank(folderId) ? UUID.randomUUID().toString() : folderId;
        try {
            MongoFileEntity mongoFileEntity = mongoFileService.saveFile(upload.getOriginalFilename(),
                    upload.getInputStream());
            fileId = mongoFileEntity.getId();
            mongoFileService.pushFileToFolder(folderId, fileId, "ckeditor");
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        }
        String fullContentType = "application/json;charset=UTF-8";
        response.setContentType(fullContentType);
        // 填充编辑器
        PrintWriter out = null;
        try {
            String resultScript = null;
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("uploaded", 1);
            jsonObject.put("fileid", fileId);
            jsonObject.put("fileName", upload.getOriginalFilename());
            resultScript = jsonObject.toString();
            out = response.getWriter();
            out.print(resultScript);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(out);
        }
    }

    @ApiIgnore
    @RequestMapping(value = "/downloadImage")
    @ResponseBody
    public void downloadImage(@RequestParam("uuid") String uuid, HttpServletResponse response,
                              HttpServletRequest request) throws IOException {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = mongoFileService.getFile(uuid).getInputstream();
            // 从jcr中获得的图片流还得转为输出流形式输出到浏览器
            response.setContentType("image/jpeg"); // 必须设置ContentType为image/jpeg
            response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);
            response.reset();
            outputStream = response.getOutputStream();
            IOUtils.copy(inputStream, outputStream);
            outputStream.flush();
            outputStream.close();
        } finally {
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(outputStream);
        }
    }

    @ApiIgnore
    @RequestMapping(value = "/uploadFile")
    @ResponseBody
    public void uploadFile(@RequestParam(value = "upload") MultipartFile upload,
                           @RequestParam(value = "folderId", required = false) String folderId, HttpServletRequest request,
                           HttpServletResponse response) {
        uploadImage(upload, folderId, request, response);
    }

    @ApiIgnore
    @RequestMapping(value = "/downloadFile/{uuid}")
    public void downloadFile(@PathVariable("uuid") String uuid, HttpServletResponse response,
                             HttpServletRequest request) throws IOException {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        MongoFileEntity mongoFileEntity = mongoFileService.getFile(uuid);
        try {
            response.reset();
            int a = mongoFileEntity.getFileName().lastIndexOf(".");
            String file_ext = mongoFileEntity.getFileName().substring(a);
            String fileName = "视频_" + DateUtil.getFormatDate(new Date(), "yyyy-MM-dd") + file_ext;
            fileName = URLEncoder.encode(fileName, "utf-8");
            String range = request.getHeader("Range");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            response.setContentType(mongoFileEntity.getContentType());
            inputStream = mongoFileEntity.getInputstream();
            if (mongoFileEntity.getContentType().startsWith("image") || StringUtils.isBlank(range)) {
                outputStream = response.getOutputStream();
                IOUtils.copy(inputStream, outputStream);
                outputStream.flush();
                outputStream.close();
            } else {
                long length = mongoFileEntity.getLength();
                long start = 0;
                long end = length - 1;
                Matcher matcher = RANGE_PATTERN.matcher(range);
                if (matcher.matches()) {
                    String startGroup = matcher.group("start");
                    start = startGroup.isEmpty() ? start : Integer.valueOf(startGroup);
                    start = start < 0 ? 0 : start;
                    String endGroup = matcher.group("end");
                    end = endGroup.isEmpty() ? end : Integer.valueOf(endGroup);
                    end = end > length - 1 ? length - 1 : end;
                }
                long contentLength = end - start + 1;
                response.setBufferSize(BUFFER_LENGTH);
                response.setHeader("Accept-Ranges", "bytes");
                response.setDateHeader("Last-Modified", mongoFileEntity.getUploadDate().getTime());
                response.setHeader("Content-Range", String.format("bytes %s-%s/%s", start, end, length));
                response.setHeader("Content-Length", String.format("%s", contentLength));
                response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
                inputStream.skip(start);
                OutputStream out = response.getOutputStream();
                int len;
                byte[] buf = new byte[BUFFER_LENGTH];//根据实际情况修改大小
                while ((len = inputStream.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
        } catch (Exception e) {
            logger.error("mongoFileEntity:" + mongoFileEntity.getFileName(), e);
        } finally {
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(outputStream);
        }
    }

    @RequestMapping(value = "/getDyformTitle", method = RequestMethod.POST)
    @ApiOperation(value = "获取表单标题", notes = "获取表单标题")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "formUuid", value = "表单定义UUID", paramType = "query", dataType = "String", required = false),
            @ApiImplicitParam(name = "dataUuid", value = "表单数据UUID", paramType = "query", dataType = "String", required = false),
            @ApiImplicitParam(name = "titleExpression", value = "标题表达式", paramType = "query", dataType = "String", required = false)})
    public ResponseEntity<ResultMessage> getDyformTitle(@RequestBody String jsonData,
                                                        @RequestParam(name = "formUuid", required = false) String formUuid,
                                                        @RequestParam(name = "dataUuid", required = false) String dataUuid,
                                                        @RequestParam(name = "titleExpression", required = false) String titleExpression,
                                                        HttpServletRequest request,
                                                        HttpServletResponse response) throws JSONException, JsonParseException, JsonMappingException, IOException,
            ParseException {
        DyFormData dyFormData = this.dyFormFacade.parseDyformData(jsonData), dyFormData2;
        if (dyFormData == null) {
            logger.error("数据解析失败: " + jsonData);
            return getFaultResultMsg("前台传入的数据解析失败", JsonDataErrorCode.FormDefinitionSaveException);
        }
        // 请求从表标题
        if (StringUtils.isNotBlank(formUuid) && false == StringUtils.equals(formUuid, dyFormData.getFormUuid())
                && StringUtils.isNotBlank(dataUuid)) {
            dyFormData2 = dyFormData.getDyFormData(formUuid, dataUuid);
        } else {
            dyFormData2 = dyFormData;
        }
        try {
            DyFormFormDefinition dyformDefinition = StringUtils.isBlank(dyFormData2.getFormUuid()) || dyFormData2.getFormUuid().equals("-1") ? new FormDefinition() :
                    dyFormFacade.getFormDefinition(dyFormData2.getFormUuid());
            if (StringUtils.isNotBlank(titleExpression)) {
                titleExpression = HtmlUtils.htmlUnescape(titleExpression);
            } else {
                titleExpression = dyformDefinition.doGetTitleExpression();
            }
            String title = DyformTitleUtils.generateDyformTitle(SpringSecurityUtils.getCurrentUserId(), dyformDefinition,
                    dyFormData2, titleExpression);
            return getSucessfulResultMsg(title);
        } catch (Exception ex) {
            return processException(ex);
        }
    }


    @PostMapping("/getFormDataByWhere/{formId}")
    public @ResponseBody
    ApiResult<List> getFormDataByWhere(@PathVariable String formId, @RequestBody SqlQueryDslObj obj) {
        obj.getNamedParams().putAll(TemplateEngineFactory.getExplainRootModel());
        String where = obj.getWhere(null);
        return ApiResult.success(dyFormFacade.getFormDataByWhere(formId, StringUtils.isNotBlank(where) ? " and (" + where + ")" : null, obj.getNamedParams()));
    }

    @PostMapping("/getFormDataUuidByWhereByFormUuid/{formUuid}")
    public @ResponseBody
    ApiResult<List> getFormDataUuidByWhereByFormUuid(@PathVariable String formUuid, @RequestBody SqlQueryDslObj obj) {
        obj.getNamedParams().putAll(TemplateEngineFactory.getExplainRootModel());
        String where = obj.getWhere(null);
        return ApiResult.success(dyFormFacade.getFormDataUuidByWhereByFormUuid(formUuid, (StringUtils.isNotBlank(where) ? " and (" + where + ")" : "") +
                (StringUtils.defaultIfBlank(" " + obj.getOrder(), "")), obj.getNamedParams()));
    }
}
