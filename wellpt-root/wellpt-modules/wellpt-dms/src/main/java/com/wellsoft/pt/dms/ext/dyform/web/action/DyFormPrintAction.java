/*
 * @(#)2018年6月27日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.ext.dyform.web.action;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.exception.BusinessException;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.util.PrinttemplateDateUtil;
import com.wellsoft.pt.basicdata.facade.service.BasicDataApiFacade;
import com.wellsoft.pt.basicdata.printtemplate.entity.PrintTemplate;
import com.wellsoft.pt.dms.core.annotation.Action;
import com.wellsoft.pt.dms.core.annotation.ActionConfig;
import com.wellsoft.pt.dms.core.support.FileManagerDyFormActionData;
import com.wellsoft.pt.dms.core.web.ActionSupport;
import com.wellsoft.pt.dms.core.web.action.ActionResult;
import com.wellsoft.pt.dms.entity.DmsFileEntity;
import com.wellsoft.pt.dms.service.DmsFileService;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.dto.DyformFieldDefinition;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.jpa.template.TemplateEngineFactory;
import com.wellsoft.pt.repository.entity.LogicFileInfo;
import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;
import com.wellsoft.pt.repository.service.MongoFileService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.htmlparser.Parser;
import org.htmlparser.util.ParserException;
import org.htmlparser.visitors.TextExtractingVisitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年6月27日.1	zhulh		2018年6月27日		Create
 * </pre>
 * @date 2018年6月27日
 */
@Action("表单单据操作")
public class DyFormPrintAction extends ActionSupport {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 5583804733416292789L;

    @Autowired
    private BasicDataApiFacade basicDataApiFacade;

    @Autowired
    private MongoFileService mongoFileService;

    @Autowired
    private DyFormFacade dyFormFacade;

    @Autowired
    private DmsFileService dmsFileService;

    /**
     * @param dyFormActionData
     * @return
     */
    @ActionConfig(name = "套打", id = DyFormActions.ACTION_PRINT, executeJsModule = "DmsDyformPrintAction")
    @ResponseBody
    public ActionResult print(@RequestBody FileManagerDyFormActionData fileManagerDyFormActionData,
                              HttpServletRequest request) {
        MongoFileEntity mongoFileEntity = null;
        try {
            String printTemplateId = getPrintTemplateId(request);
            InputStream inputStream = doPrint(printTemplateId, fileManagerDyFormActionData);

            if (inputStream == null) {
                throw new BusinessException("打印模板调用接口,返回文件流出错!");
            }

            // 打印结果保存到MongoDB中
            String mongoFileUuid = fileManagerDyFormActionData.getDataUuid();
            String printFileName = getPrintResultFileName(request, printTemplateId, fileManagerDyFormActionData);
            mongoFileEntity = mongoFileService.saveFile(mongoFileUuid, printFileName, inputStream);
        } catch (Exception e) {
            if (e instanceof BusinessException) {
                throw (BusinessException) e;
            } else {
                logger.error(e.getMessage(), e);
                throw new RuntimeException("打印出错！");
            }
        }

        LogicFileInfo logicFileInfo = mongoFileEntity.getLogicFileInfo();
        ActionResult actionResult = createActionResult();
        actionResult.setData(logicFileInfo);
        return actionResult;
    }

    /**
     * @param printTemplateId
     * @param fileManagerDyFormActionData
     * @return
     */
    private InputStream doPrint(String printTemplateId, FileManagerDyFormActionData fileManagerDyFormActionData) {
        DyFormData dyFormData = fileManagerDyFormActionData.getDyFormData();
        String formUuid = dyFormData.getFormUuid();
        String dataUuid = dyFormData.getDataUuid();

        // 解决套打附件正文内容
        // <字段， 附件monofile列表>
        Map<String, List<MongoFileEntity>> bodyFiles = new HashMap<String, List<MongoFileEntity>>();
        // 先取正文附件字段
        List<DyformFieldDefinition> fieldDefinitions = dyFormFacade.getFieldDefinitions(formUuid);
        // Set<String> recordFields = getRecordFields(flowDelegate);
        Map<String, Object> recordFieldValueMap = new HashMap<String, Object>();
        // word打印清除表单大字段的HTML格式
        com.wellsoft.pt.basicdata.printtemplate.entity.PrintTemplate printTemplate = basicDataApiFacade
                .getPrintTemplateById(printTemplateId);
        for (DyformFieldDefinition fieldDefinition : fieldDefinitions) {
            String fieldName = fieldDefinition.getName();
            // 如果是附件字段
            if (dyFormData.isFileField(fieldName)) {
                List<MongoFileEntity> fileEntities = mongoFileService.getFilesFromFolder(dataUuid, fieldName);
                bodyFiles.put(fieldName, fileEntities);
            } else {
                // 信息格式清除HTML标签的字段值
                Object recordFieldValue = dyFormData.isValueAsMapField(fieldName) ? dyFormData
                        .getFieldDisplayValue(fieldName) : dyFormData.getFieldValue(fieldName);
                if (recordFieldValue != null
                        && (printTemplate.doIsTemplateFileTypeAsWord() || printTemplate.doIsTemplateFileTypeAsWordXml() || printTemplate.doIsTemplateFileTypeAsWordPoi())) {
                    String inputString = null;
                    try {
                        if (recordFieldValue instanceof Clob) {
                            inputString = IOUtils.toString(((Clob) recordFieldValue).getCharacterStream());
                        } else if (recordFieldValue instanceof String) {
                            inputString = (String) recordFieldValue;
                        } else {
                            continue;
                        }
                        inputString = "<div>" + inputString + "</div>";
                        recordFieldValueMap.put(fieldName, html2Text(inputString));
                    } catch (IOException ex) {
                        logger.warn(ex.getMessage(), ex);
                    } catch (SQLException ex) {
                        logger.warn(ex.getMessage(), ex);
                    }
                }
            }
        }

        // 打印
        // 表单对应的所有字段显示值（包含主表和从表）
        Map<String, List<Map<String, Object>>> formDataDisplayMap = dyFormData.getDisplayValuesKeyAsFormId();
        Map<String, Object> formDataMap = Maps.newHashMap();
        formDataMap.putAll(dyFormData.getFormDataOfMainform());
        if (printTemplate.doIsTemplateFileTypeAsWord() || printTemplate.doIsTemplateFileTypeAsWordXml()
                || printTemplate.doIsTemplateFileTypeAsWordXmlByComment() || printTemplate.doIsTemplateFileTypeAsWordPoi()) {
            formDataMap.putAll(PrinttemplateDateUtil.getPrinttemplateMap(dyFormData.getFormId(), formDataDisplayMap));
        } else {
            formDataMap.putAll(PrinttemplateDateUtil.getPrinttemplateMap(dyFormData.getFormId(), formDataDisplayMap,
                    false));
        }
        formDataMap.putAll(recordFieldValueMap);
        List<IdEntity> entities = getDmsFileEntities(fileManagerDyFormActionData);
        InputStream inputStream = basicDataApiFacade.getPrintResultAsInputStream(printTemplateId, entities,
                formDataMap, bodyFiles);

        return inputStream;
    }

    /**
     * 如何描述该方法
     *
     * @param request
     * @return
     */
    private String getPrintTemplateId(HttpServletRequest request) {
        return request.getParameter("templateId");
    }

    /**
     * @param request
     * @return
     */
    private String getPrintResultFileName(HttpServletRequest request, String printTemplateId,
                                          FileManagerDyFormActionData fileManagerDyFormActionData) {
        String printFileName = request.getParameter("printResultFileName");
        if (StringUtils.isBlank(printFileName)) {
            com.wellsoft.pt.basicdata.printtemplate.entity.PrintTemplate printTemplate = basicDataApiFacade
                    .getPrintTemplateById(printTemplateId);
            printFileName = printTemplate.getName();
            if (PrintTemplate.TEMPLATE_TYPE_HTML.equals(printTemplate.getTemplateType())) {
                printFileName += ".html";
            } else {
                printFileName += ".doc";
            }
        } else {
            List<IdEntity> idEntities = getDmsFileEntities(fileManagerDyFormActionData);
            DyFormData dyFormData = fileManagerDyFormActionData.getDyFormData();
            Object root = TemplateEngineFactory.getDefaultTemplateEngine().mergeDataAsMap(idEntities,
                    dyFormData.getFormDataOfMainform(), dyFormData.getFormDataOfMainform(), true, true);
            try {
                printFileName = TemplateEngineFactory.getDefaultTemplateEngine().process(printFileName, root);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        return printFileName;
    }

    /**
     * 如何描述该方法
     *
     * @param fileManagerDyFormActionData
     * @return
     */
    private List<IdEntity> getDmsFileEntities(FileManagerDyFormActionData fileManagerDyFormActionData) {
        List<IdEntity> idEntities = Lists.newArrayList();
        String fileUuid = fileManagerDyFormActionData.getFileUuid();
        if (StringUtils.isNotBlank(fileUuid)) {
            DmsFileEntity dmsFileEntity = dmsFileService.get(fileUuid);
            if (dmsFileEntity != null) {
                idEntities.add(dmsFileEntity);
            }
        }
        return idEntities;
    }

    /**
     * Java过滤HTML标签实例
     *
     * @param inputString
     * @return
     */
    public String html2Text(String inputString) {
        if (StringUtils.isBlank(inputString)) {
            return StringUtils.EMPTY;
        }
        Parser parser;
        String textStr = inputString;
        try {
            parser = new Parser(inputString);
            parser.setEncoding("UTF-8");
            // 创建StringFindingVisitor对象
            TextExtractingVisitor visitor = new TextExtractingVisitor();
            // 去除网页中的所有标签,提出纯文本内容
            parser.visitAllNodesWith(visitor);
            textStr = visitor.getExtractedText();
        } catch (ParserException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
        return textStr;
    }

}
