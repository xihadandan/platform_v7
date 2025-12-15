/*
 * @(#)May 25, 2020 V1.0
 *
 * Copyright 2020 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.ext.listview.web.action;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.config.Config;
import com.wellsoft.context.exception.BusinessException;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.util.PrinttemplateDateUtil;
import com.wellsoft.pt.basicdata.facade.service.BasicDataApiFacade;
import com.wellsoft.pt.basicdata.printtemplate.entity.PrintTemplate;
import com.wellsoft.pt.bpm.engine.entity.FlowInstance;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.bpm.engine.service.FlowInstanceService;
import com.wellsoft.pt.bpm.engine.service.TaskInstanceService;
import com.wellsoft.pt.dms.core.annotation.Action;
import com.wellsoft.pt.dms.core.annotation.ListViewActionConfig;
import com.wellsoft.pt.dms.core.support.FileManagerDyFormActionData;
import com.wellsoft.pt.dms.core.support.ListViewSelection;
import com.wellsoft.pt.dms.core.web.ActionSupport;
import com.wellsoft.pt.dms.core.web.action.ActionResult;
import com.wellsoft.pt.dms.entity.DmsFileEntity;
import com.wellsoft.pt.dms.service.DmsFileService;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.dto.DyformFieldDefinition;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.repository.entity.LogicFileInfo;
import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;
import com.wellsoft.pt.repository.service.MongoFileService;
import net.sf.json.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
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
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.*;

/**
 * Description: 如何描述该类
 *
 * @author shenhb
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * May 25, 2020.5	shenhb	May 25, 2020		Create
 * </pre>
 * @date May 25, 2020
 */
@Action("视图列表")
public class ListViewPrintAction extends ActionSupport {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -2235364431236199649L;
    private final static String MAPPING_DATA_UUID = "dataUuid";
    private final static String MAPPING_FORM_UUID = "formUuid";
    private final static String MAPPING_FLOW_INSTANCE_ID = "flowInstanceId";
    private final static String MAPPING_TASK_INSTANCE_ID = "taskInstanceId";
    private final static String HTML_DATA_LIST_NAME = "dytableList";
    private final static String FORM_DATA_MAP = "formDataMap";
    private final static String BODY_FILES = "bodyFiles";
    //限制套打导出文件存在文件服务中的文档数量
    private final static int NUM = 0;
    @Autowired
    private DyFormFacade dyFormApiFacade;
    @Autowired
    private BasicDataApiFacade basicDataApiFacade;
    @Autowired
    private MongoFileService mongoFileService;
    @Autowired
    private DyFormFacade dyFormFacade;
    @Autowired
    private DmsFileService dmsFileService;
    @Autowired
    private FlowInstanceService flowInstanceService;
    @Autowired
    private TaskInstanceService taskInstanceService;

    @ListViewActionConfig(name = "表格套打", id = ListViewActions.ACTION_TABLE_PRINT, executeJsModule = "DmsListViewTablePrintAction", confirmMsg = "确认要套打吗?")
    @ResponseBody
    public ActionResult actionPerformed(@RequestBody ListViewSelection listViewSelection,
                                        HttpServletRequest request) {
        if (CollectionUtils.isEmpty(listViewSelection.getSelection())) {
            throw new BusinessException("请选择要套打的数据!");
        }

        MongoFileEntity mongoFileEntity = null;
        File file = null;
        try {
            String printTemplateId = getPrintTemplateId(request);
            String printResultFileName = getPrintResultFileName(listViewSelection);
            if (StringUtils.isBlank(printTemplateId)) {
                throw new BusinessException("打印模板不能为空!");
            }

            file = doPrint(printTemplateId, listViewSelection);
            FileInputStream fileInputStream = null;
            try {
                fileInputStream = FileUtils.openInputStream(file);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (fileInputStream == null) {
                throw new BusinessException("打印模板调用接口,返回文件流出错!");
            }

            // 打印结果保存到MongoDB中
            String printFileName = getPrintResultFileName(printResultFileName, printTemplateId);
            // 限制套打导出文件存在mongodb中的文档数量，各个打印模板的每个导出名最大存30份，结果文件在文件服务中
            mongoFileEntity = mongoFileService.saveFile(printTemplateId + ((NUM + 1) % 30), printFileName, fileInputStream);
        } catch (Exception e) {
            if (e instanceof BusinessException) {
                throw (BusinessException) e;
            } else {
                logger.error(e.getMessage(), e);
                throw new RuntimeException("打印出错！");
            }
        } finally {
            if (null != file) {
                file.delete();
            }
        }

        LogicFileInfo logicFileInfo = mongoFileEntity.getLogicFileInfo();
        ActionResult actionResult = createActionResult();
        actionResult.setData(logicFileInfo);
        return actionResult;
    }


    /**
     * 如何描述该方法
     *
     * @param listViewSelection listViewSelection
     * @return
     */
    private String getPrintTemplateId(ListViewSelection listViewSelection) {
        return listViewSelection.getExtraString("printTemplateId");
    }

    private String getPrintResultFileName(ListViewSelection listViewSelection) {
        return listViewSelection.getExtraString("printResultFileName");
    }

    private String getPrintTemplateId(HttpServletRequest request) {
        return request.getParameter("templateId");
    }


    /**
     * @param printTemplateId   printTemplateId
     * @param listViewSelection listViewSelection
     * @return
     */
    private File doPrint(String printTemplateId, ListViewSelection listViewSelection) {

        String fieldMapping = listViewSelection.getExtraString("fieldMapping");

        PrintTemplate printTemplate = null;
        // word打印清除表单大字段的HTML格式
        printTemplate = basicDataApiFacade
                .getPrintTemplateById(printTemplateId);
        if (printTemplate == null) {
            throw new BusinessException("套打模板获取失败！");
        }
        List<Map<String, Object>> dataMapList = new ArrayList<>();

        for (Object selectionMapObject : listViewSelection.getSelection()) {
            Map<String, Object> selectionMap = (Map<String, Object>) selectionMapObject;

            // <字段， 附件monofile列表>
            Map<String, List<MongoFileEntity>> bodyFiles = new HashMap<String, List<MongoFileEntity>>();
            DyFormData dyFormData = getDyFormDataBySelection(selectionMap, fieldMapping);

            String dataUuid = dyFormData.getDataUuid();
            String formUuid = dyFormData.getFormUuid();

            // 解决套打附件正文内容
            // 先取正文附件字段
            List<DyformFieldDefinition> fieldDefinitions = dyFormFacade.getFieldDefinitions(formUuid);
            Map<String, Object> recordFieldValueMap = new HashMap<String, Object>();

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
                            inputString = inputString.replaceAll("</p>", "</p> \n");
                            inputString = inputString.replaceAll("<br>", " \n");
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

            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put(FORM_DATA_MAP, formDataMap);
            dataMap.put(BODY_FILES, bodyFiles);
            dataMapList.add(dataMap);
        }


        String finalFileName = new Date().getTime() + "temp";
        int docNum = 0;
        //生成的所有套打文件
        List<String> printTempFilePathList = new ArrayList<>();
        try {

            if (printTemplate.doIsTemplateFileTypeAsHtml()) {
                List<Map<String, Object>> formDataMapList = new ArrayList<>();
                for (Map<String, Object> dataMap : dataMapList) {
                    Map<String, Object> formDataMap = (Map<String, Object>) dataMap.get(FORM_DATA_MAP);
                    formDataMapList.add(formDataMap);
                }
                Map<String, Object> dataMap = new HashMap<>();
                dataMap.put(HTML_DATA_LIST_NAME, formDataMapList);
                InputStream inputStream = basicDataApiFacade.getPrintResultAsInputStream(printTemplateId, Collections.EMPTY_LIST,
                        dataMap, new HashMap<String, List<MongoFileEntity>>());
                docNum++;
                String filePath = Config.HOME_DIR + File.separator + finalFileName + "-" + docNum + ".doc";
                try {
                    FileUtils.copyInputStreamToFile(inputStream, new File(filePath));
                    printTempFilePathList.add(filePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else {
                for (Map<String, Object> dataMap : dataMapList) {
                    Map<String, Object> formDataMap = (Map<String, Object>) dataMap.get(FORM_DATA_MAP);
                    Map<String, List<MongoFileEntity>> bodyFiles = (Map<String, List<MongoFileEntity>>) dataMap.get(BODY_FILES);
                    //            List<IdEntity> entities = getDmsFileEntities(fileManagerDyFormActionData);
                    InputStream inputStream = basicDataApiFacade.getPrintResultAsInputStream(printTemplateId, Collections.EMPTY_LIST,
                            formDataMap, bodyFiles);
                    docNum++;
                    String filePath = Config.HOME_DIR + File.separator + finalFileName + "-" + docNum + ".doc";
                    try {
                        FileUtils.copyInputStreamToFile(inputStream, new File(filePath));
                        printTempFilePathList.add(filePath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            //异常，删除已经生成的文件
            for (String printTempFilePath : printTempFilePathList) {
                new File(printTempFilePath).delete();
            }
            throw e;
        }

        if (printTemplate.doIsTemplateFileTypeAsHtml()) {
            if (CollectionUtils.isNotEmpty(printTempFilePathList)) {
                return new File(printTempFilePathList.get(0));
            } else {
                return null;
            }
        } else if (printTemplate.doIsTemplateFileTypeAsWordPoi()) {
            return basicDataApiFacade.uniteDocx(docNum, printTemplate, finalFileName, true);
        } else {
            return basicDataApiFacade.uniteDocByPage(docNum, printTemplate, finalFileName);
        }
    }

    private DyFormData getDyFormDataBySelection(Map<String, Object> selectionMap, String fieldMapping) {

        String type = null;
        JSONObject jsonObject = null;
        try {
            jsonObject = JSONObject.fromObject("{}");

            String[] fieldMappingArr = fieldMapping.split(";");
            for (String s : fieldMappingArr) {
                String[] split1 = s.split(":");
                jsonObject.put(split1[0], split1[1]);
            }

            if (jsonObject.containsKey(MAPPING_DATA_UUID) && jsonObject.containsKey(MAPPING_FORM_UUID)) {
                type = MAPPING_DATA_UUID;
            } else if (jsonObject.containsKey(MAPPING_FLOW_INSTANCE_ID)) {
                type = MAPPING_FLOW_INSTANCE_ID;
            } else if (jsonObject.containsKey(MAPPING_TASK_INSTANCE_ID)) {
                type = MAPPING_TASK_INSTANCE_ID;
            } else {
                throw new BusinessException("批量套打字段映射配置错误！");
            }
        } catch (Exception e) {
            throw new BusinessException("批量套打字段映射配置错误！");
        }

        //先使用列表方式，再使用流程归档库方式
        String dataUuid = null;
        String formUuid = null;
        DyFormData dyFormData = null;
        //普通列表
        try {
            if (type.equals(MAPPING_DATA_UUID)) {
                dataUuid = (String) selectionMap.get(
                        jsonObject.getString(MAPPING_DATA_UUID)
                );
                formUuid = (String) selectionMap.get(
                        jsonObject.getString(MAPPING_FORM_UUID)
                );
            } else if (type.equals(MAPPING_FLOW_INSTANCE_ID)) {
                String flowInstanceId = (String) selectionMap.get(
                        jsonObject.getString(MAPPING_FLOW_INSTANCE_ID)
                );
                FlowInstance flowInstance = flowInstanceService.getOne(flowInstanceId);
                dataUuid = flowInstance.getDataUuid();
                formUuid = flowInstance.getFormUuid();
            } else if (type.equals(MAPPING_TASK_INSTANCE_ID)) {
                String taskInstanceId = (String) selectionMap.get(
                        jsonObject.getString(MAPPING_TASK_INSTANCE_ID)
                );
                TaskInstance taskInstance = taskInstanceService.getOne(taskInstanceId);
                dataUuid = taskInstance.getDataUuid();
                formUuid = taskInstance.getFormUuid();
            }

            dyFormData = dyFormApiFacade.getDyFormData(formUuid, dataUuid);
        } catch (Exception e) {
            throw new BusinessException("获取表单数据失败");
        }

        if (dyFormData == null) {
            throw new BusinessException("获取表单数据失败");
        }
        return dyFormData;
    }

    /**
     * @return String
     */
    private String getPrintResultFileName(String printFileName, String printTemplateId) {
        PrintTemplate printTemplate = basicDataApiFacade.getPrintTemplateById(printTemplateId);
        if (StringUtils.isBlank(printFileName)) {
            printFileName = printTemplate.getName();
        }
        if (PrintTemplate.TEMPLATE_TYPE_HTML.equals(printTemplate.getTemplateType())) {
            printFileName += ".html";
        } else {
            printFileName += ".doc";
        }
        return printFileName;
    }

    /**
     * 如何描述该方法
     *
     * @param fileManagerDyFormActionData fileManagerDyFormActionData
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
