/*
 * @(#)2017-12-19 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.config.Config;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.exception.BusinessException;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.PrinttemplateDateUtil;
import com.wellsoft.pt.basicdata.facade.service.BasicDataApiFacade;
import com.wellsoft.pt.basicdata.printtemplate.dto.PrintDto;
import com.wellsoft.pt.basicdata.printtemplate.dto.PrintsDto;
import com.wellsoft.pt.basicdata.printtemplate.entity.PrintTemplate;
import com.wellsoft.pt.basicdata.printtemplate.service.PrintTemplateService;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.bpm.engine.service.TaskService;
import com.wellsoft.pt.dms.bean.DmsFileVo;
import com.wellsoft.pt.dms.dao.DmsFileDao;
import com.wellsoft.pt.dms.entity.DmsFileEntity;
import com.wellsoft.pt.dms.enums.FileTypeEnum;
import com.wellsoft.pt.dms.event.DmsFileSavedEvent;
import com.wellsoft.pt.dms.file.action.FileActions;
import com.wellsoft.pt.dms.file.service.DmsFileDoucmentIndexService;
import com.wellsoft.pt.dms.model.DmsFile;
import com.wellsoft.pt.dms.model.DmsFolderDyformDefinition;
import com.wellsoft.pt.dms.service.*;
import com.wellsoft.pt.dms.support.FileHelper;
import com.wellsoft.pt.dms.support.FileQueryTemplateUtils;
import com.wellsoft.pt.dms.support.FileStatus;
import com.wellsoft.pt.dms.support.WorkflowMediaType;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.dto.DyFormFormDefinition;
import com.wellsoft.pt.dyform.facade.dto.DyformFieldDefinition;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.jpa.util.HqlUtils;
import com.wellsoft.pt.org.facade.service.OrgFacadeService;
import com.wellsoft.pt.repository.entity.LogicFileInfo;
import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;
import com.wellsoft.pt.repository.service.MongoFileService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.htmlparser.Parser;
import org.htmlparser.util.ParserException;
import org.htmlparser.visitors.TextExtractingVisitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

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
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017-12-19.1	zhulh		2017-12-19		Create
 * </pre>
 * @date 2017-12-19
 */
@Service
public class DmsFileServiceImpl extends AbstractJpaServiceImpl<DmsFileEntity, DmsFileDao, String> implements DmsFileService {

    private final static String FORM_DATA_MAP = "formDataMap";
    private final static String BODY_FILES = "bodyFiles";
    private final static String HTML_DATA_LIST_NAME = "dytableList";
    // 限制套打导出文件存在文件服务中的文档数量
    private final static int NUM = 0;
    // 通过夹UUID、表单数据获取文档
    private static final String GET_BY_FOLDER_UUID_AND_DYFORM = "from DmsFileEntity t where t.folderUuid = :folderUuid and t.dataDefUuid = :dataDefUuid and t.dataUuid = :dataUuid";
    // 通过夹UUID、表单数据UUID获取文档
    private static final String GET_BY_FOLDER_UUID_AND_DATA_UUID = "from DmsFileEntity t where t.folderUuid = :folderUuid and t.dataUuid = :dataUuid";
    @Autowired
    private DmsFolderConfigurationService dmsFolderConfigurationService;
    @Autowired
    private DyFormFacade dyFormApiFacade;
    @Autowired
    private OrgFacadeService orgApiFacade;
    @Autowired
    private PrintTemplateService printTemplateService;
    @Autowired
    private DyFormFacade dyFormFacade;
    @Autowired
    private MongoFileService mongoFileService;
    @Autowired
    private BasicDataApiFacade basicDataApiFacade;
    @Autowired
    private TaskService taskService;
    @Autowired
    private DmsObjectIdentityService dmsObjectIdentityService;
    @Autowired
    private DmsObjectAssignActionService dmsObjectAssignActionService;
    @Autowired
    private DmsFolderService dmsFolderService;
    @Autowired
    private DmsFileDoucmentIndexService dmsFileDoucmentIndexService;

    public List<DmsFileEntity> getByDataUuid(String dataUuid) {
        List<DmsFileEntity> result = this.dao.listByFieldEqValue("dataUuid", dataUuid);
        if (CollectionUtils.isNotEmpty(result)) {
            return result;
        }
        return null;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsFileService#get(java.lang.String)
     */
    @Override
    public DmsFileEntity get(String uuid) {
        return this.dao.getOne(uuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsFileService#findByExample(DmsFile)
     */
    @Override
    public List<DmsFileEntity> findByExample(DmsFileEntity example) {
        return this.dao.listByEntity(example);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsFileService#countByDataUuid(java.lang.String)
     */
    @Override
    public Long countByDataUuid(String dataUuid) {
        DmsFileEntity example = new DmsFileEntity();
        example.setDataUuid(dataUuid);
        return this.dao.countByEntity(example);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsFileService#save(com.wellsoft.pt.dms.entity.DmsFile)
     */
    @Override
    @Transactional
    public void save(DmsFileEntity entity) {
        String formUuid = entity.getDataDefUuid();
        String contentType = entity.getContentType();
        if (StringUtils.isNotBlank(formUuid)
                && (StringUtils.equals(WorkflowMediaType.APPLICATION_WORKFLOW_DYFORM_VALUE, contentType)
                || StringUtils.equals(FileTypeEnum.DYFORM.getType(), contentType))) {
            DyFormFormDefinition dyFormFormDefinition = dyFormApiFacade.getFormDefinition(formUuid);
            String pFormUuid = dyFormFormDefinition.doGetPFormUuid();
            entity.setDataDefUuid(pFormUuid);
        }
        this.dao.save(entity);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsFileService#saveAll(java.util.Collection)
     */
    @Override
    @Transactional
    public void saveAll(Collection<DmsFileEntity> entities) {
        // this.dao.saveAll(entities);
        for (DmsFileEntity entity : entities) {
            this.save(entity);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsFileService#remove(java.lang.String)
     */
    @Override
    @Transactional
    public void remove(String uuid) {
        DmsFileEntity dmsFileEntity = this.dao.getOne(uuid);
        if (dmsFileEntity == null) {
            return;
        }
        // 删除表单数据
        if (FileHelper.isDyform(dmsFileEntity)) {
            dyFormApiFacade.delFormData(dmsFileEntity.getDataDefUuid(), dmsFileEntity.getDataUuid());
        }
        // 删除文件数据
        this.dao.delete(dmsFileEntity);
        // 删除权限
        dmsObjectAssignActionService.removeByObjectIdIdentity(dmsFileEntity.getUuid());
        dmsObjectIdentityService.removeByObjectIdIdentity(dmsFileEntity.getUuid());
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.DmsFileService#removeAll(java.util.Collection)
     */
    @Override
    @Transactional
    public void removeAll(Collection<DmsFileEntity> entities) {
        for (DmsFileEntity dmsFileEntity : entities) {
            this.dao.delete(dmsFileEntity);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsFileService#remove(DmsFile)
     */
    @Override
    @Transactional
    public void remove(DmsFileEntity entity) {
        this.dao.delete(entity);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsFileService#removeAllByPk(java.util.Collection)
     */
    @Override
    @Transactional
    public void removeAllByPk(Collection<String> uuids) {
        for (String uuid : uuids) {
            this.remove(uuid);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsFileService#getByDataUuid(java.lang.String)
     */
    @Override
    public DmsFileEntity getByFolderUuidAndDataUuid(String folderUuid, String dataUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("folderUuid", folderUuid);
        values.put("dataUuid", dataUuid);
        return this.dao.getOneByHQL(GET_BY_FOLDER_UUID_AND_DATA_UUID, values);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsFileService#isFileEditor(java.lang.String, java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public boolean isFileEditor(String userId, String fileUuid) {
        DmsFileEntity fileEntity = get(fileUuid);
        if (StringUtils.equals(fileEntity.getCreator(), userId)) {
            return true;
        }
        DmsFolderDyformDefinition folderDyformDefinition = dmsFolderConfigurationService
                .getFolderDyformDefinitionByFileUuid(fileUuid);
        if (folderDyformDefinition == null) {
            return false;
        }
        // 文档编辑人员字段
        String editFileField = folderDyformDefinition.getEditFileField();
        if (StringUtils.isBlank(editFileField)) {
            return false;
        }
        String formUuid = folderDyformDefinition.getFormUuid();
        DyFormFormDefinition dyFormDefinition = dyFormApiFacade.getFormDefinition(formUuid);
        String currUserId = SpringSecurityUtils.getCurrentUserId();
        FileQueryTemplateUtils.FileQueryTemplate fileQueryTemplate = FileQueryTemplateUtils.getTemplateOfUserOrgIds(orgApiFacade, currUserId);
        String userOrgIdsTemplate = fileQueryTemplate.getTemplate();
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("tableName", dyFormDefinition.getTableName());
        values.put("fileUuid", fileUuid);
        values.put("editFileField", folderDyformDefinition.getEditFileField());
        values.put("unit_in_expression_org_id", currUserId);
        values.put("userOrgIdsTemplate", userOrgIdsTemplate);
        values.put("orgIds", fileQueryTemplate.getOrgIds());
        values.put("roleIds", fileQueryTemplate.getRoleIds());
        values.put("sids", fileQueryTemplate.getSids());
        Long count = this.dao.countByNamedSQLQuery("isFileEditorQuery", values);
        return count.intValue() > 0;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsFileService#isFileEditor(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public boolean isFileEditor(String userId, String folderUuid, String dataUuid) {
        DmsFileEntity fileEntity = getByFolderUuidAndDataUuid(folderUuid, dataUuid);
        if (fileEntity == null) {
            return false;
        }
        if (StringUtils.equals(fileEntity.getCreator(), userId)) {
            return true;
        }
        DmsFolderDyformDefinition folderDyformDefinition = dmsFolderConfigurationService
                .getFolderDyformDefinitionByFolderUuid(folderUuid);
        if (folderDyformDefinition == null) {
            return false;
        }
        String editFileField = folderDyformDefinition.getEditFileField();
        if (StringUtils.isBlank(editFileField)) {
            return false;
        }
        String formUuid = folderDyformDefinition.getFormUuid();
        DyFormFormDefinition dyFormDefinition = dyFormApiFacade.getFormDefinition(formUuid);
        String currUserId = SpringSecurityUtils.getCurrentUserId();
        FileQueryTemplateUtils.FileQueryTemplate fileQueryTemplate = FileQueryTemplateUtils.getTemplateOfUserOrgIds(orgApiFacade, currUserId);
        String userOrgIdsTemplate = fileQueryTemplate.getTemplate();
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("tableName", dyFormDefinition.getTableName());
        values.put("dataUuid", dataUuid);
        values.put("editFileField", editFileField);
        values.put("unit_in_expression_org_id", currUserId);
        values.put("userOrgIdsTemplate", userOrgIdsTemplate);
        values.put("orgIds", fileQueryTemplate.getOrgIds());
        values.put("roleIds", fileQueryTemplate.getRoleIds());
        values.put("sids", fileQueryTemplate.getSids());
        Long count = this.dao.countByNamedSQLQuery("isFileEditorByDataUuidQuery", values);
        return count.intValue() > 0;
    }

    /**
     * 保存文档为草稿
     *
     * @param dyFormData
     * @param fileUuid
     * @param folderUuid
     * @return
     */
    @Override
    @Transactional
    public DmsFileEntity saveDocumentAsDraft(DyFormData dyFormData, String fileUuid, String folderUuid) {
        return saveDocument(dyFormData, FileStatus.DRAFT, fileUuid, folderUuid);
    }

    /**
     * 保存文档
     *
     * @param dyFormData
     * @param fileUuid
     * @param folderUuid
     * @return
     */
    @Override
    @Transactional
    public DmsFileEntity saveDocument(DyFormData dyFormData, String fileUuid, String folderUuid) {
        return saveDocument(dyFormData, FileStatus.NORMAL, fileUuid, folderUuid);
    }

    /**
     * 保存文档
     *
     * @param dyFormData
     * @param fileUuid
     * @param folderUuid
     * @return
     */
    public DmsFileEntity saveDocument(DyFormData dyFormData, String fileStatus, String fileUuid, String folderUuid) {
        Assert.hasLength(folderUuid, "文件所属夹不能为空！");

        // 保存或更新文件信息
        DmsFileEntity dmsFileEntity = null;
        if (StringUtils.isNotBlank(fileUuid)) {
            dmsFileEntity = this.dao.getOne(fileUuid);
        } else {
            String formUuid = dyFormData.getFormUuid();
            String dataUuid = dyFormData.getDataUuid();
            if (StringUtils.isNotBlank(dataUuid)) {
                Map<String, Object> values = new HashMap<String, Object>();
                values.put("folderUuid", folderUuid);
                values.put("dataDefUuid", formUuid);
                values.put("dataUuid", dataUuid);
                dmsFileEntity = this.dao.getOneByHQL(GET_BY_FOLDER_UUID_AND_DYFORM, values);
            }
            if (dmsFileEntity == null) {
                dmsFileEntity = new DmsFileEntity();
            }
        }

        DmsFolderDyformDefinition folderDyformDefinition = dmsFolderConfigurationService
                .getFolderDyformDefinitionByFolderUuid(folderUuid);
        // 文档名称
        String fileName = getFileName(folderUuid, dyFormData, folderDyformDefinition);
        String fileStatusField = folderDyformDefinition.getFileStatusField();
        if (dyFormData.isFieldExist(fileStatusField)) {
            dyFormData.setFieldValue(fileStatusField, fileStatus);
        }

        String dataUuid = dyFormApiFacade.saveFormData(dyFormData);
        dmsFileEntity.setFileName(fileName);
        dmsFileEntity.setContentType(FileTypeEnum.DYFORM.getType());
        dmsFileEntity.setDataDefUuid(dyFormData.getFormUuid());
        dmsFileEntity.setDataUuid(dataUuid);
        if (StringUtils.isBlank(dmsFileEntity.getFolderUuid())) {
            dmsFileEntity.setFolderUuid(folderUuid);
            dmsFileEntity.setLibraryUuid(dmsFolderService.getOne(folderUuid).getLibraryUuid());
        }
        dmsFileEntity.setStatus(fileStatus);
        save(dmsFileEntity);

        // 发布状态保存文档编辑人员、阅读人员，建立全文索引
        if (FileStatus.NORMAL.equals(fileStatus)) {
            // 保存文件阅读者、编辑者
            saveFileReaderAndEditor(dmsFileEntity, dyFormData, folderDyformDefinition);

            // 建立全文索引
            // dmsFileDoucmentIndexService.index(dmsFileEntity, dyFormData);
            ApplicationContextHolder.publishEvent(new DmsFileSavedEvent(dyFormData, dmsFileEntity));
        }
        return dmsFileEntity;
    }

    /**
     * 保存文件阅读者及编辑者
     *
     * @param dmsFileEntity
     * @param dyFormData
     * @param folderDyformDefinition
     */
    private void saveFileReaderAndEditor(DmsFileEntity dmsFileEntity, DyFormData dyFormData, DmsFolderDyformDefinition folderDyformDefinition) {
        Set<String> readerIds = null;
        Set<String> editorIds = null;
        String readFileField = folderDyformDefinition.getReadFileField();
        String editFileField = folderDyformDefinition.getEditFileField();
        if (dyFormData.isFieldExist(readFileField)) {
            readerIds = Sets.newLinkedHashSet(Lists.newArrayList(StringUtils.split(Objects.toString(dyFormData.getFieldValue(readFileField), StringUtils.EMPTY),
                    Separator.SEMICOLON.getValue())));
        }
        if (dyFormData.isFieldExist(editFileField)) {
            editorIds = Sets.newLinkedHashSet(Lists.newArrayList(StringUtils.split(Objects.toString(dyFormData.getFieldValue(editFileField), StringUtils.EMPTY),
                    Separator.SEMICOLON.getValue())));
        }

        // 保存文件阅读者、编辑者
        dmsObjectAssignActionService.saveFileReaderAndEditors(dmsFileEntity.getUuid(), readerIds, editorIds);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsFileService#save(com.wellsoft.pt.dyform.facade.dto.DyFormData, java.lang.String)
     */
    @Override
    @Transactional
    public String archive(DyFormData dyFormData, String folderUuid, List<String> readerIds) {
        DmsFolderDyformDefinition folderDyformDefinition = dmsFolderConfigurationService
                .getFolderDyformDefinitionByFolderUuid(folderUuid);
        // 文档名称
        String fileName = getFileName(folderUuid, dyFormData, folderDyformDefinition);
        return archive(fileName, dyFormData, folderUuid, readerIds);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsFileService#save(java.lang.String, com.wellsoft.pt.dyform.facade.dto.DyFormData, java.lang.String)
     */
    @Override
    @Transactional
    public String archive(String fileName, DyFormData dyFormData, String folderUuid, List<String> readerIds) {
        return archive(fileName, FileTypeEnum.DYFORM.getType(), dyFormData, folderUuid, readerIds);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsFileService#save(java.lang.String, java.lang.String, com.wellsoft.pt.dyform.facade.dto.DyFormData, java.lang.String)
     */
    @Override
    @Transactional
    public String archive(String fileName, String contentType, DyFormData dyFormData, String folderUuid, List<String> readerIds) {
        Assert.hasLength(folderUuid, "文件所属夹不能为空！");
        // 保存或更新文件信息
        DmsFileEntity dmsFileEntity = null;
        String formUuid = dyFormData.getFormUuid();
        String dataUuid = dyFormData.getDataUuid();
        if (StringUtils.isNotBlank(dataUuid)) {
            Map<String, Object> values = new HashMap<String, Object>();
            values.put("folderUuid", folderUuid);
            values.put("dataDefUuid", formUuid);
            values.put("dataUuid", dataUuid);
            dmsFileEntity = this.dao.getOneByHQL(GET_BY_FOLDER_UUID_AND_DYFORM, values);
        }
        if (dmsFileEntity == null) {
            dmsFileEntity = new DmsFileEntity();
        }
        dmsFileEntity.setFileName(fileName);
        dmsFileEntity.setContentType(contentType);
        dmsFileEntity.setDataDefUuid(dyFormData.getFormUuid());
        dmsFileEntity.setDataUuid(dataUuid);
        dmsFileEntity.setFolderUuid(folderUuid);
        dmsFileEntity.setLibraryUuid(dmsFolderService.getOne(folderUuid).getLibraryUuid());
        dmsFileEntity.setStatus(FileStatus.NORMAL);
        save(dmsFileEntity);

        // 保存文件阅读者、编辑者
        dmsObjectAssignActionService.saveFileReaderAndEditors(dmsFileEntity.getUuid(), Sets.newLinkedHashSet(readerIds), null);

        // 建立全文索引
        // dmsFileDoucmentIndexService.index(dmsFileEntity, dyFormData);
        ApplicationContextHolder.publishEvent(new DmsFileSavedEvent(dyFormData, dmsFileEntity));

        return dmsFileEntity.getUuid();
    }

    /**
     * @param folderUuid
     * @param dyFormData
     * @param folderDyformDefinition
     * @return
     */
    private String getFileName(String folderUuid, DyFormData dyFormData, DmsFolderDyformDefinition folderDyformDefinition) {
        String fileName = dyFormData.getDataUuid();
        if (folderDyformDefinition == null) {
            return fileName;
        }
        String fileNameField = folderDyformDefinition.getFileNameField();
        if (StringUtils.isNotBlank(fileNameField) && dyFormData.isFieldExist(fileNameField)) {
            Object fieldValue = dyFormData.getFieldValue(fileNameField);
            if (fieldValue != null) {
                fileName = fieldValue.toString();
            }
        }
        return fileName;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.service.DmsFileService#archive(com.wellsoft.pt.dms.model.DmsFile)
     */
    @Override
    @Transactional
    public String archive(DmsFile dmsFile, List<String> readerIds) {
        String folderUuid = dmsFile.getFolderUuid();
        String dataUuid = dmsFile.getDataUuid();
        Assert.hasLength(folderUuid, "归档夹不能为空！");

        DmsFileEntity dmsFileEntity = getByFolderUuidAndDataUuid(folderUuid, dataUuid);
        if (dmsFileEntity == null) {
            dmsFileEntity = new DmsFileEntity();
        }
        BeanUtils.copyProperties(dmsFile, dmsFileEntity, IdEntity.BASE_FIELDS);
        dmsFileEntity.setFileName(dmsFile.getName());
        dmsFileEntity.setStatus(FileStatus.NORMAL);
        this.dao.save(dmsFileEntity);

        // 保存文件阅读者、编辑者
        dmsObjectAssignActionService.saveFileReaderAndEditors(dmsFileEntity.getUuid(), Sets.newLinkedHashSet(readerIds), null);

        // 建立全文索引
        // dmsFileDoucmentIndexService.index(dmsFileEntity);
        ApplicationContextHolder.publishEvent(new DmsFileSavedEvent(dmsFileEntity));
        return dmsFileEntity.getUuid();
    }

    @Override
    @Transactional
    public LogicFileInfo print(PrintDto printDto) {
        MongoFileEntity mongoFileEntity = null;
        PrintTemplate printTemplate = null;
        File file = null;
        LogicFileInfo logicFileInfo = null;
        try {
            if (StringUtils.isBlank(printDto.getPrintTemplateUuId())) {
                throw new BusinessException("打印模板不能为空!");
            }
            if (StringUtils.isBlank(printDto.getDataUuid())) {
                throw new BusinessException("dataUuid不能为空!");
            }
            if (StringUtils.isBlank(printDto.getFormUuid())) {
                throw new BusinessException("formUuid不能为空!");
            }
            printTemplate = printTemplateService.getByUuid(printDto.getPrintTemplateUuId());
            if (printTemplate == null) {
                throw new BusinessException("无效的打印模板Uuid" + printDto.getPrintTemplateUuId() + "!");
            }
            List<String> dataUuids = Lists.newArrayList();
            List<String> formUuids = Lists.newArrayList();
            dataUuids.add(printDto.getDataUuid());
            formUuids.add(printDto.getFormUuid());
            file = doPrint(dataUuids, formUuids, printTemplate.getId());
            logicFileInfo = getLogicFileInfo(file, printTemplate);
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
        return logicFileInfo;

    }

    @Override
    @Transactional
    public LogicFileInfo prints(PrintsDto printsDto) {
        PrintTemplate printTemplate = null;
        File file = null;
        LogicFileInfo logicFileInfo = null;
        try {
            if (StringUtils.isBlank(printsDto.getPrintTemplateUuId())) {
                throw new BusinessException("打印模板不能为空!");
            }
            if (CollectionUtils.isEmpty(printsDto.getDataUuids())) {
                throw new BusinessException("dataUuid不能为空!");
            }
            if (CollectionUtils.isEmpty(printsDto.getFormUuids())) {
                throw new BusinessException("formUuid不能为空!");
            }
            printTemplate = printTemplateService.getByUuid(printsDto.getPrintTemplateUuId());
            if (printTemplate == null) {
                throw new BusinessException("无效的打印模板Uuid" + printsDto.getPrintTemplateUuId() + "!");
            }
            file = doPrints(printsDto, printTemplate.getId());
            logicFileInfo = getLogicFileInfo(file, printTemplate);
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

        return logicFileInfo;
    }

    private LogicFileInfo getLogicFileInfo(File file, PrintTemplate printTemplate) {
        MongoFileEntity mongoFileEntity = null;
        try {
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
            String printFileName = getPrintResultFileName("", printTemplate.getId());
            // 限制套打导出文件存在mongodb中的文档数量，各个打印模板的每个导出名最大存30份，结果文件在文件服务中
            mongoFileEntity = mongoFileService.saveFile(printTemplate.getId() + ((NUM + 1) % 30), printFileName,
                    fileInputStream);
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
        return mongoFileEntity.getLogicFileInfo();
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
     * 批量套打
     *
     * @param printsDto
     * @param printTemplateId
     * @return java.io.File
     **/
    private File doPrints(PrintsDto printsDto, String printTemplateId) {
        PrintTemplate printTemplate = null;
        // word打印清除表单大字段的HTML格式
        printTemplate = basicDataApiFacade.getPrintTemplateById(printTemplateId);
        if (printTemplate == null) {
            throw new BusinessException("套打模板获取失败！");
        }
        if (printsDto.getDataUuids().size() != printsDto.getFormUuids().size()
                && printsDto.getFormUuids().size() != printsDto.getContentTypes().size()) {
            throw new BusinessException("参数异常，dataUuids集合和formUuids集合长度对应不上！");
        }
        List<String> flowInstanceUuids = Lists.newArrayList();
        List<String> formDataUuids = Lists.newArrayList();
        List<String> formformUuids = Lists.newArrayList();
        // 过滤是归档流程还是归档表单
        for (int i = 0; i < printsDto.getContentTypes().size(); i++) {
            String contentType = printsDto.getContentTypes().get(i);
            if (WorkflowMediaType.APPLICATION_WORKFLOW_VALUE.equals(contentType)) {
                // 需求调整 那个套打的，学敏那边确认可以先仅处理文件库本身的，通过归档流程数据产生的归档文件，可暂不支持在文件库中套打
                // 流程数据
                String flowInstanceUuid = printsDto.getDataUuids().get(i);
                flowInstanceUuids.add(flowInstanceUuid);
            } else {
                // 流程表单数据 新建文档
                formDataUuids.add(printsDto.getDataUuids().get(i));
                if (StringUtils.isBlank(printsDto.getFormUuids().get(i))) {
                    throw new BusinessException("contentType为dyform的归档表单数据存在脏数据，找不到formUuid");
                }
                formformUuids.add(printsDto.getFormUuids().get(i));
            }
        }

        // 归档表单处理方式
        List<Map<String, Object>> dataMapList = new ArrayList<>();
        for (int i = 0; i < formDataUuids.size(); i++) {
            String dataUuid = formDataUuids.get(i);
            String formUuid = formformUuids.get(i);
            // <字段， 附件monofile列表>
            Map<String, List<MongoFileEntity>> bodyFiles = new HashMap<String, List<MongoFileEntity>>();

            DyFormData dyFormData = dyFormApiFacade.getDyFormData(formUuid, dataUuid);
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
                    Object recordFieldValue = dyFormData.isValueAsMapField(fieldName)
                            ? dyFormData.getFieldDisplayValue(fieldName)
                            : dyFormData.getFieldValue(fieldName);
                    if (recordFieldValue != null && (printTemplate.doIsTemplateFileTypeAsWord()
                            || printTemplate.doIsTemplateFileTypeAsWordXml()
                            || printTemplate.doIsTemplateFileTypeAsWordPoi())) {
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
                    || printTemplate.doIsTemplateFileTypeAsWordXmlByComment()
                    || printTemplate.doIsTemplateFileTypeAsWordPoi()) {
                formDataMap
                        .putAll(PrinttemplateDateUtil.getPrinttemplateMap(dyFormData.getFormId(), formDataDisplayMap));
            } else {
                formDataMap.putAll(
                        PrinttemplateDateUtil.getPrinttemplateMap(dyFormData.getFormId(), formDataDisplayMap, false));
            }
            formDataMap.putAll(recordFieldValueMap);
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put(FORM_DATA_MAP, formDataMap);
            dataMap.put(BODY_FILES, bodyFiles);
            dataMapList.add(dataMap);
        }

        String finalFileName = System.currentTimeMillis() + "temp";
        int docNum = 0;
        // 生成的所有套打文件
        List<String> printTempFilePathList = new ArrayList<>();
        try {
            // 归档表单方式
            if (printTemplate.doIsTemplateFileTypeAsHtml()) {
                // 归档表单方式
                List<Map<String, Object>> formDataMapList = new ArrayList<>();
                for (Map<String, Object> dataMap : dataMapList) {
                    Map<String, Object> formDataMap = (Map<String, Object>) dataMap.get(FORM_DATA_MAP);
                    formDataMapList.add(formDataMap);
                }
                if (formDataMapList.size() > 0) {
                    Map<String, Object> dataMap = new HashMap<>();
                    dataMap.put(HTML_DATA_LIST_NAME, formDataMapList);
                    InputStream inputStream = basicDataApiFacade.getPrintResultAsInputStream(printTemplateId,
                            Collections.EMPTY_LIST, dataMap, new HashMap<String, List<MongoFileEntity>>());
                    docNum++;
                    String filePath = Config.HOME_DIR + File.separator + finalFileName + "-" + docNum + ".doc";
                    try {
                        FileUtils.copyInputStreamToFile(inputStream, new File(filePath));
                        printTempFilePathList.add(filePath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                for (Map<String, Object> dataMap : dataMapList) {
                    Map<String, Object> formDataMap = (Map<String, Object>) dataMap.get(FORM_DATA_MAP);
                    Map<String, List<MongoFileEntity>> bodyFiles1 = (Map<String, List<MongoFileEntity>>) dataMap
                            .get(BODY_FILES);
                    // List<IdEntity> entities = getDmsFileEntities(fileManagerDyFormActionData);
                    InputStream inputStream = basicDataApiFacade.getPrintResultAsInputStream(printTemplateId,
                            Collections.EMPTY_LIST, formDataMap, bodyFiles1);
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
            // 归档流程方式
            for (String flowInstanceUuid : flowInstanceUuids) {
                TaskInstance taskInstance = taskService.getLastTaskInstanceByFlowInstUuid(flowInstanceUuid);
                if (taskInstance == null) {
                    throw new BusinessException("对应的文档不存在！");
                }
                InputStream inputStream = taskService.getPrintResultAsInputStream(taskInstance, printTemplateId, null,
                        null);
                docNum++;
                String filePath = Config.HOME_DIR + File.separator + finalFileName + "-" + docNum + ".doc";
                try {
                    FileUtils.copyInputStreamToFile(inputStream, new File(filePath));
                    printTempFilePathList.add(filePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            // 异常，删除已经生成的文件
            for (String printTempFilePath : printTempFilePathList) {
                new File(printTempFilePath).delete();
            }
            throw e;
        }

        // 文件名
        String filePath = Config.HOME_DIR + File.separator + finalFileName + "-" + docNum + ".doc";
        // 添加到列表中
        printTempFilePathList.add(filePath);

        // 最后是一样的
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

    /**
     * 打印模板
     *
     * @param dataUuids       表单数据uuid集合
     * @param formUuids       表单uuid集合
     * @param printTemplateId 打印模板
     * @return java.io.File
     **/
    private File doPrint(List<String> dataUuids, List<String> formUuids, String printTemplateId) {
        PrintTemplate printTemplate = null;
        // word打印清除表单大字段的HTML格式
        printTemplate = basicDataApiFacade.getPrintTemplateById(printTemplateId);
        if (printTemplate == null) {
            throw new BusinessException("套打模板获取失败！");
        }
        if (dataUuids.size() != formUuids.size()) {
            throw new BusinessException("参数异常，dataUuids集合和formUuids集合长度对应不上！");
        }
        List<Map<String, Object>> dataMapList = new ArrayList<>();
        for (int i = 0; i < dataUuids.size(); i++) {
            String dataUuid = dataUuids.get(i);
            String formUuid = formUuids.get(i);
            // <字段， 附件monofile列表>
            Map<String, List<MongoFileEntity>> bodyFiles = new HashMap<String, List<MongoFileEntity>>();

            DyFormData dyFormData = dyFormApiFacade.getDyFormData(formUuid, dataUuid);
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
                    Object recordFieldValue = dyFormData.isValueAsMapField(fieldName)
                            ? dyFormData.getFieldDisplayValue(fieldName)
                            : dyFormData.getFieldValue(fieldName);
                    if (recordFieldValue != null && (printTemplate.doIsTemplateFileTypeAsWord()
                            || printTemplate.doIsTemplateFileTypeAsWordXml()
                            || printTemplate.doIsTemplateFileTypeAsWordPoi())) {
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
                    || printTemplate.doIsTemplateFileTypeAsWordXmlByComment()
                    || printTemplate.doIsTemplateFileTypeAsWordPoi()) {
                formDataMap
                        .putAll(PrinttemplateDateUtil.getPrinttemplateMap(dyFormData.getFormId(), formDataDisplayMap));
            } else {
                formDataMap.putAll(
                        PrinttemplateDateUtil.getPrinttemplateMap(dyFormData.getFormId(), formDataDisplayMap, false));
            }
            formDataMap.putAll(recordFieldValueMap);
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put(FORM_DATA_MAP, formDataMap);
            dataMap.put(BODY_FILES, bodyFiles);
            dataMapList.add(dataMap);
        }
        String finalFileName = System.currentTimeMillis() + "temp";
        int docNum = 0;
        // 生成的所有套打文件
        List<String> printTempFilePathList = new ArrayList<>();
        try {

            if (printTemplate.doIsTemplateFileTypeAsHtml()) {
                List<Map<String, Object>> formDataMapList = new ArrayList<>();
                for (Map<String, Object> dataMap : dataMapList) {
                    Map<String, Object> formDataMap = (Map<String, Object>) dataMap.get(FORM_DATA_MAP);
                    formDataMapList.add(formDataMap);
                }
                Map<String, Object> dataMap = new HashMap<>();
                // dataMap.put(HTML_DATA_LIST_NAME, formDataMapList);
                InputStream inputStream = basicDataApiFacade.getPrintResultAsInputStream(printTemplateId,
                        Collections.EMPTY_LIST, formDataMapList.get(0), new HashMap<String, List<MongoFileEntity>>());
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
                    Map<String, List<MongoFileEntity>> bodyFiles1 = (Map<String, List<MongoFileEntity>>) dataMap
                            .get(BODY_FILES);
                    // List<IdEntity> entities = getDmsFileEntities(fileManagerDyFormActionData);
                    InputStream inputStream = basicDataApiFacade.getPrintResultAsInputStream(printTemplateId,
                            Collections.EMPTY_LIST, formDataMap, bodyFiles1);
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
            // 异常，删除已经生成的文件
            for (String printTempFilePath : printTempFilePathList) {
                new File(printTempFilePath).delete();
            }
            throw e;
        }

        if (docNum == 1 && CollectionUtils.size(printTempFilePathList) == 1) {
            return new File(printTempFilePathList.get(0));
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

    @Override
    public List<DmsFileEntity> list(DmsFileEntity fileEntity) {
        List<DmsFileEntity> dmsFileList = this.dao.listByEntity(fileEntity);
        return dmsFileList;
    }

    @Override
    @Transactional
    public int updateDmsFileCreator(DmsFileVo dmsFileVo) {
        if (dmsFileVo == null) {
            return 0;
        }
        if (CollectionUtils.isEmpty(dmsFileVo.getUuids())) {
            return 0;
        }
        if (StringUtils.isBlank(dmsFileVo.getUserId())) {
            return 0;
        }
        StringBuilder sbHql = new StringBuilder("update DmsFileEntity set creator = :userId  where ");
        Map<String, Object> params = new HashMap<>();
        params.put("userId", dmsFileVo.getUserId());
        HqlUtils.appendSql("uuid", params, sbHql, new HashSet<>(dmsFileVo.getUuids()));
        int result = this.dao.updateByHQL(sbHql.toString(), params);
        return result;
    }

    /**
     * 根据文件UUID，添加文件阅读者
     *
     * @param fileUuid
     * @param readerIds
     * @return
     */
    @Override
    @Transactional
    public boolean addFileReader(String fileUuid, List<String> readerIds) {
        dmsObjectAssignActionService.addFileReader(fileUuid, readerIds);
        return true;
    }

    /**
     * 根据文件数据UUID，添加文件阅读者
     *
     * @param dataUuid
     * @param readerIds
     * @return
     */
    @Override
    @Transactional
    public boolean addFileReaderByDataUuid(String dataUuid, List<String> readerIds) {
        List<DmsFileEntity> files = getByDataUuid(dataUuid);
        if (CollectionUtils.isEmpty(files)) {
            return false;
        }

        files.forEach(file -> {
            this.addFileReader(file.getUuid(), readerIds);
        });
        return true;
    }

    @Override
    public List<String> getFileReaders(String fileUuid) {
        return dmsObjectAssignActionService.listFileActionUser(fileUuid, FileActions.READ_FILE, FileActions.EDIT_FILE);
    }

    /**
     * 根据文件UUID，删除文件阅读者
     *
     * @param fileUuid
     * @param readerIds
     * @return
     */
    @Override
    @Transactional
    public boolean deleteFileReader(String fileUuid, List<String> readerIds) {
        dmsObjectAssignActionService.deleteFileReader(fileUuid, readerIds);
        return true;
    }

    /**
     * 根据文件数据UUID，删除文件阅读者
     *
     * @param dataUuid
     * @param readerIds
     * @return
     */
    @Override
    @Transactional
    public boolean deleteFileReaderByDataUuid(String dataUuid, List<String> readerIds) {
        List<DmsFileEntity> files = getByDataUuid(dataUuid);
        if (CollectionUtils.isEmpty(files)) {
            return false;
        }

        files.forEach(file -> {
            this.addFileReaderByDataUuid(file.getUuid(), readerIds);
        });
        return true;
    }

    @Override
    public Long countByFolderUuid(String folderUuid) {
        DmsFileEntity fileEntity = new DmsFileEntity();
        fileEntity.setFolderUuid(folderUuid);
        return this.dao.countByEntity(fileEntity);
    }

    /**
     * 检查同一目录下文件名是否重复
     *
     * @param fileName
     * @param fileUuid
     * @return
     */
    @Override
    public boolean existsTheSameFileNameWidthFileUuid(String fileName, String fileUuid) {
        Assert.hasLength(fileName, "文件名不能为空！");
        Assert.hasLength(fileUuid, "文件UUID不能为空！");

        String hql = "from DmsFileEntity t1 where t1.fileName = :fileName and t1.uuid <> :fileUuid and t1.status <> :deleteStatus and t1.folderUuid = (select t2.folderUuid from DmsFileEntity t2 where t2.uuid = :fileUuid)";
        Map<String, Object> params = Maps.newHashMap();
        params.put("fileName", fileName);
        params.put("fileUuid", fileUuid);
        params.put("deleteStatus", FileStatus.DELETE);
        return this.dao.countByHQL(hql, params) > 0;
    }

    /**
     * 检查同一目录下文件名是否重复
     *
     * @param fileName
     * @param folderUuid
     * @return
     */
    @Override
    public boolean existsFileNameByFolderUuid(String fileName, String folderUuid) {
        Assert.hasLength(fileName, "文件名不能为空！");
        Assert.hasLength(folderUuid, "文件養UUID不能为空！");

        String hql = "from DmsFileEntity t1 where t1.fileName = :fileName and t1.status <> :deleteStatus and t1.folderUuid = :folderUuid";
        Map<String, Object> params = Maps.newHashMap();
        params.put("fileName", fileName);
        params.put("folderUuid", folderUuid);
        params.put("deleteStatus", FileStatus.DELETE);
        return this.dao.countByHQL(hql, params) > 0;
    }

    @Override
    public List<DmsFileEntity> listByFolderUuid(String folderUuid) {
        String hql = "from DmsFileEntity t1 where t1.folderUuid = :folderUuid and t1.status = :status";
        Map<String, Object> params = Maps.newHashMap();
        params.put("folderUuid", folderUuid);
        params.put("status", FileStatus.NORMAL);
        return this.dao.listByHQL(hql, params);
    }

    @Override
    public List<DmsFileEntity> listByFolderUuidAndGTEModifyTime(String folderUuid, Date fromTime) {
        String hql = "from DmsFileEntity t1 where t1.folderUuid = :folderUuid and t1.status = :status and t1.modifyTime >= :fromTime";
        Map<String, Object> params = Maps.newHashMap();
        params.put("folderUuid", folderUuid);
        params.put("status", FileStatus.NORMAL);
        params.put("fromTime", fromTime);
        return this.dao.listByHQL(hql, params);
    }

}
