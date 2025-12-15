/*
 * @(#)2014-6-17 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.excelexporttemplate.service.impl;

import com.wellsoft.pt.basicdata.excelexporttemplate.service.ExcelEngineService;
import com.wellsoft.pt.basicdata.excelexporttemplate.support.Cel2JsonParse;
import com.wellsoft.pt.basicdata.excelexporttemplate.support.DateJsonValueProcessor;
import com.wellsoft.pt.basicdata.excelexporttemplate.support.Json2CelEngine;
import com.wellsoft.pt.basicdata.exceltemplate.dao.ExcelImportRuleDao;
import com.wellsoft.pt.basicdata.exceltemplate.entity.ExcelImportRule;
import com.wellsoft.pt.dyform.facade.dto.DyFormFormDefinition;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;
import com.wellsoft.pt.repository.service.MongoFileService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ExcelEngineServiceImpl extends BaseServiceImpl implements ExcelEngineService {

    @Autowired
    private ExcelImportRuleDao excelImportRuleDao;

    @Autowired
    private DyFormFacade dyFormApiFacade;

    @Autowired
    private MongoFileService mongoFileService;

    public static String getFormUuid(JSONObject jsonObject) {
        return jsonObject.getString(FORM_UUID);// 自己会抛异常
    }

    public static JSONObject getFormDatas(JSONObject jsonObject) {
        return jsonObject.getJSONObject(FORM_DATAS);
    }

    public static JSONArray getFormMainDatas(JSONObject jsonObject) {
        return jsonObject.getJSONObject(FORM_DATAS).getJSONArray(getFormUuid(jsonObject));
    }

    public static JSONObject getFormMainData(JSONObject jsonObject) {
        return jsonObject.getJSONObject(FORM_DATAS).getJSONArray(getFormUuid(jsonObject)).getJSONObject(0);
    }

    public static List<String> getFormKeys(JSONObject jsonObject) {
        List<String> subFormKeys = new ArrayList<String>();
        for (Object keySet : getFormDatas(jsonObject).keySet()) {
            String key = String.valueOf(keySet);
            subFormKeys.add(key);
        }
        return subFormKeys;
    }

    public static String objectString(Object obj) {
        return obj == null ? "" : obj.toString();
    }

    /**
     * 解析Excel中的数据为JSON
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.excelexporttemplate.service.ExcelEngineService#parseJSON(java.io.InputStream)
     */
    @Override
    public String parseJSON(MultipartFile part) {
        Assert.notNull(part, "multipartFile不能为空");
        Cel2JsonParse cel2JsonParse = null;
        try {
            if (part.getOriginalFilename().toUpperCase().endsWith(".XLS")) {
                cel2JsonParse = new Cel2JsonParse(new HSSFWorkbook(part.getInputStream()));
            } else {
                cel2JsonParse = new Cel2JsonParse(new XSSFWorkbook(part.getInputStream()));
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
        JsonConfig jsonConfig = new JsonConfig();
        jsonConfig.registerJsonValueProcessor(Date.class, new DateJsonValueProcessor("yyyy-MM-dd HH:mm:ss"));
        cel2JsonParse.setConfig(jsonConfig);
        JSONObject jsonObject = cel2JsonParse.parse();
        String jsonString = jsonObject.toString();
        for (String formId : getFormKeys(jsonObject)) {
            DyFormFormDefinition dyFormDefinition = dyFormApiFacade.getFormDefinitionById(formId);
            if (dyFormDefinition == null || StringUtils.isBlank(dyFormDefinition.getUuid())) {
                continue;
            }
            jsonString = jsonString.replaceAll("\"" + formId + "\"", "\"" + dyFormDefinition.getUuid() + "\"");//""分号内的精确匹配,formId不分割匹配
        }
        return jsonString;
    }

    public String parseJSONEx(InputStream inputStream) {
        Cel2JsonParse cel2JsonParse = new Cel2JsonParse(inputStream);
        JsonConfig jsonConfig = new JsonConfig();
        jsonConfig.registerJsonValueProcessor(Date.class, new DateJsonValueProcessor("yyyy-MM-dd HH:mm:ss"));
        cel2JsonParse.setConfig(jsonConfig);
        JSONObject jsonObject = cel2JsonParse.parse();
        String jsonString = jsonObject.toString();
        for (String formId : getFormKeys(jsonObject)) {
            DyFormFormDefinition dyFormDefinition = dyFormApiFacade.getFormDefinitionById(formId);
        }
        return jsonString;
    }

    /**
     * 解析Excel中的数据为JSON
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.excelexporttemplate.service.ExcelEngineService#parseJSON(java.io.InputStream)
     */
    @Override
    public String parseJSONS(MultipartFile part) {
        Assert.notNull(part, "multipartFile不能为空");
        Cel2JsonParse cel2JsonParse = null;
        try {
            if (part.getOriginalFilename().toUpperCase().endsWith(".XLS")) {
                cel2JsonParse = new Cel2JsonParse(new HSSFWorkbook(part.getInputStream()));
            } else {
                cel2JsonParse = new Cel2JsonParse(new XSSFWorkbook(part.getInputStream()));
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
        JsonConfig jsonConfig = new JsonConfig();
        jsonConfig.registerJsonValueProcessor(Date.class, new DateJsonValueProcessor("yyyy-MM-dd HH:mm:ss"));
        cel2JsonParse.setConfig(jsonConfig);
        JSONObject jsonObject = cel2JsonParse.parse();
        String jsonString = jsonObject.toString();
        return jsonString;
    }

    /**
     * 解析Excel中的数据为JSON
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.excelexporttemplate.service.ExcelEngineService#parseJSON(java.io.InputStream)
     */
    @Override
    public Map<String, List<Map<String, Object>>> parseFormDatas(MultipartFile part) {
        Assert.notNull(part, "multipartFile不能为空");
        Cel2JsonParse cel2JsonParse = null;
        try {
            if (part.getOriginalFilename().toUpperCase().endsWith(".XLS")) {
                cel2JsonParse = new Cel2JsonParse(new HSSFWorkbook(part.getInputStream()));
            } else {
                cel2JsonParse = new Cel2JsonParse(new XSSFWorkbook(part.getInputStream()));
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
        JsonConfig jsonConfig = new JsonConfig();
        jsonConfig.registerJsonValueProcessor(Date.class, new DateJsonValueProcessor("yyyy-MM-dd HH:mm:ss"));
        cel2JsonParse.setConfig(jsonConfig);
        JSONObject jsonObject = cel2JsonParse.parse();
        return cel2JsonParse.getFormDatas();
    }

    /**
     * 根据模版解析数据
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.excelexporttemplate.service.ExcelEngineService#parseExcel(java.io.InputStream, java.lang.String)
     */
    @Override
    public File parseExcel(MultipartFile inputStream, String jsonObject) {
        Assert.notNull(inputStream, "multipartFile不能为空");
        Json2CelEngine json2CelEngine = null;
        try {
            if (inputStream.getOriginalFilename().toUpperCase().endsWith(".XLS")) {
                json2CelEngine = new Json2CelEngine(new HSSFWorkbook(inputStream.getInputStream()));
            } else {
                json2CelEngine = new Json2CelEngine(new XSSFWorkbook(inputStream.getInputStream()));
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
        JSONObject localJSONObject = JSONObject.fromObject(jsonObject);
        String jsonString = jsonObject;
        for (String localFormUuid : getFormKeys(localJSONObject)) {
            String formId = dyFormApiFacade.getFormIdByFormUuid(localFormUuid);
            jsonString = jsonString.replaceAll(localFormUuid, formId);
        }
        File file = json2CelEngine.process(JSONObject.fromObject(jsonString));
        return file;
    }

    /**
     * 根据模版解析数据
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.excelexporttemplate.service.ExcelEngineService#parseExcel(java.io.InputStream, java.lang.String)
     */
    @Override
    public File parseExcel(String jsonObject) {
        JSONObject localJSONObject = JSONObject.fromObject(jsonObject);
        if (StringUtils.isEmpty(localJSONObject.getString(FORM_ID))) {
            throw new RuntimeException("导出的数据不符合规范,缺少[" + FORM_ID + "]");
            // return null;
        }
        MongoFileEntity entity = getExcelTemplateStream(localJSONObject.getString(FORM_ID));
        String fileName = null;
        InputStream inputstream = null;
        if (entity == null || (fileName = entity.getFileName()) == null
                || (inputstream = entity.getInputstream()) == null) {
            throw new RuntimeException("请进入系统后台[基础数据管理->数据导入规则],配置数据导入规则[" + localJSONObject.getString(FORM_ID) + "]");
            // return null;
        }
        Json2CelEngine json2CelEngine = null;// = new Json2CelEngine(entity);
        try {
            if (fileName.toUpperCase().endsWith(".XLS")) {
                json2CelEngine = new Json2CelEngine(new HSSFWorkbook(inputstream));
            } else {
                json2CelEngine = new Json2CelEngine(new XSSFWorkbook(inputstream));
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
        String jsonString = jsonObject;
        for (String localFormUuid : getFormKeys(localJSONObject)) {
            String formId = dyFormApiFacade.getFormIdByFormUuid(localFormUuid);
            jsonString = jsonString.replaceAll(localFormUuid, formId);
        }
        File file = json2CelEngine.process(JSONObject.fromObject(jsonString));
        return file;
    }

    @Override
    public MongoFileEntity getExcelTemplateStream(String id) {
        if (StringUtils.isEmpty(id)) {
            return null;
        }
        ExcelImportRule excelImportRule = excelImportRuleDao.findUniqueBy("id", id);
        if (excelImportRule == null) {
            return null;
        }
        if (excelImportRule.getFileUuid() == null || "".equals(excelImportRule.getFileUuid())) {
            return null;
        }
        List<MongoFileEntity> files = ExcelImportRuleDao.getMongoFileEntityByFileUuid(mongoFileService, excelImportRule.getFileUuid());//  mongoFileService.getFilesFromFolder(excelImportRule.getFileUuid(), "attach");
        if (files == null || files.size() == 0) {
            return null;
        }
        MongoFileEntity mongoFileEntity = files.get(0);
        return mongoFileEntity;//mongoFileEntity.getInputstream();
    }

    @Override
    public String getExcelTemplateName(String id) {
        if (StringUtils.isEmpty(id)) {
            return null;
        }
        ExcelImportRule excelImportRule = excelImportRuleDao.findUniqueBy("id", id);
        if (excelImportRule == null) {
            return null;
        }
        return excelImportRule.getName();
    }
}
