/*
 * @(#)6/3/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.file.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.support.QueryData;
import com.wellsoft.context.util.collection.ListUtils;
import com.wellsoft.pt.dms.entity.DmsFileEntity;
import com.wellsoft.pt.dms.entity.DmsFolderEntity;
import com.wellsoft.pt.dms.facade.api.DmsFileServiceFacade;
import com.wellsoft.pt.dms.file.action.FileActions;
import com.wellsoft.pt.dms.file.facade.service.DmsFileManagerService;
import com.wellsoft.pt.dms.file.index.DmsFileDocumentIndex;
import com.wellsoft.pt.dms.file.service.DmsFileDoucmentIndexService;
import com.wellsoft.pt.dms.file.view.FileViewer;
import com.wellsoft.pt.dms.model.DmsFile;
import com.wellsoft.pt.dms.model.DmsFolderConfiguration;
import com.wellsoft.pt.dms.service.DmsFolderService;
import com.wellsoft.pt.dms.support.FileHelper;
import com.wellsoft.pt.dms.support.FileStatus;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.fulltext.facade.service.FulltextSettingFacadeService;
import com.wellsoft.pt.fulltext.request.IndexRequestParams;
import com.wellsoft.pt.fulltext.service.impl.AbstractDocumentIndexServiceImpl;
import com.wellsoft.pt.fulltext.support.DataFiller;
import com.wellsoft.pt.fulltext.support.FulltextSetting;
import com.wellsoft.pt.fulltext.utils.AttachmentUtils;
import com.wellsoft.pt.fulltext.utils.ElasticsearchClientUtil;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.org.facade.service.OrgFacadeService;
import com.wellsoft.pt.repository.entity.LogicFileInfo;
import com.wellsoft.pt.repository.service.MongoFileService;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.IgnoreLoginUtils;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 6/3/24.1	zhulh		6/3/24		Create
 * </pre>
 * @date 6/3/24
 */
@Service
public class DmsFileDoucmentIndexServiceImpl extends AbstractDocumentIndexServiceImpl<DmsFileDocumentIndex>
        implements DmsFileDoucmentIndexService {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private static List<String> WORD_EXTS = Lists.newArrayList("doc", "docx");
    private static List<String> TEXT_BASE_EXTS = Lists.newArrayList("txt", "text", "log", "wps", "wpt", "rtf", "dot", "dotm", "dotx", "dot",
            "doc", "docm", "docx", "ppt", "pptm", "pptx", "csv", "xls", "xlsb", "xlsm", "xlsx", "pdf", "ssp");

    @Autowired(required = false)
    private List<FileViewer> fileViewers;

    @Autowired
    private MongoFileService mongoFileService;

    @Autowired
    private ElasticsearchClientUtil clientUtil;

    @Autowired
    private OrgFacadeService orgFacadeService;

    @Autowired
    private DmsFileManagerService dmsFileManagerService;

    @Autowired
    private DmsFileServiceFacade dmsFileServiceFacade;

    @Autowired
    private DyFormFacade dyFormFacade;

    @Autowired
    private DmsFolderService dmsFolderService;

    @Autowired
    private FulltextSettingFacadeService fulltextSettingFacadeService;

    /**
     * 建立文件索引
     *
     * @param fileEntity
     */
    @Override
    public void index(DmsFileEntity fileEntity) {
        this.index(fileEntity, true);
    }

    /**
     * 建立文件索引
     *
     * @param fileEntity
     * @param async
     */
    @Override
    public void index(DmsFileEntity fileEntity, boolean async) {
        if (!isEnableFulltextIndex(fileEntity.getLibraryUuid())) {
            return;
        }

        UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
        String system = RequestSystemContextPathResolver.system();
        FileViewer fileViewer = getFileViewer(fileEntity);
        DyFormData dyFormData = null;
        // 实现FileViewer类型的数据
        if (fileViewer != null) {
            DmsFile dmsFile = new DmsFile();
            BeanUtils.copyProperties(fileEntity, dmsFile);
            dyFormData = fileViewer.getDyformData(dmsFile);
            if (dyFormData != null) {
                this.index(fileEntity, dyFormData, async);
            } else {
                DmsFileDocumentIndex documentIndex = new DmsFileDocumentIndex();
                this.saveIndex(documentIndex, document -> {
                    try {
                        RequestSystemContextPathResolver.setSystem(system);
                        IgnoreLoginUtils.login(userDetails);
                        DmsFileDocumentIndex dmsFileDocumentIndex = (DmsFileDocumentIndex) document;
                        fillDmsFileDocumentIndex(dmsFileDocumentIndex, fileEntity, fileEntity.getUuid(), userDetails, system);
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    } finally {
                        IgnoreLoginUtils.logout();
                        RequestSystemContextPathResolver.clear();
                    }
                }, async);
            }
        } else if (FileHelper.isDyform(fileEntity)) {
            // 表单数据
            this.index(fileEntity, dyFormFacade.getDyFormData(fileEntity.getDataDefUuid(), fileEntity.getDataUuid()), async);
        } else if (StringUtils.equals(fileEntity.getUuid(), fileEntity.getDataDefUuid())) {
            // 附件文件
            this.index(fileEntity, fileEntity.getDataUuid(), async);
        } else {
            // 其他文件
            DmsFileDocumentIndex documentIndex = new DmsFileDocumentIndex();
            this.saveIndex(documentIndex, document -> {
                try {
                    RequestSystemContextPathResolver.setSystem(system);
                    IgnoreLoginUtils.login(userDetails);
                    DmsFileDocumentIndex dmsFileDocumentIndex = (DmsFileDocumentIndex) document;
                    fillDmsFileDocumentIndex(dmsFileDocumentIndex, fileEntity, fileEntity.getUuid(), userDetails, system);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                } finally {
                    IgnoreLoginUtils.logout();
                    RequestSystemContextPathResolver.clear();
                }
            }, async);
        }
    }

    public void saveIndex(DmsFileDocumentIndex index, DataFiller dataFiller, boolean async) {
        if (async) {
            this.asyncIndex(index, dataFiller);
        } else {
            dataFiller.filling(index);
            this.index(index);
        }
    }

    private FileViewer getFileViewer(DmsFileEntity fileEntity) {
        if (CollectionUtils.isEmpty(fileViewers)) {
            return null;
        }
        String contentType = fileEntity.getContentType();
        for (FileViewer fileViewer : fileViewers) {
            if (fileViewer.matches(contentType)) {
                return fileViewer;
            }
        }
        return null;
    }

    /**
     * 建立文件索引
     *
     * @param fileEntity
     * @param fileId
     */
    @Override
    public void index(DmsFileEntity fileEntity, String fileId) {
        this.index(fileEntity, fileId, true);
    }

    @Override
    public void index(DmsFileEntity fileEntity, String fileId, boolean async) {
        if (!isEnableFulltextIndex(fileEntity.getLibraryUuid())) {
            return;
        }

        UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
        String system = RequestSystemContextPathResolver.system();
        FulltextSetting fulltextSetting = fulltextSettingFacadeService.getSettingBySystem(system);
        FulltextSetting.DmsFileConfiguration dmsFileConfiguration = fulltextSetting.getBuiltIn("dms_file", FulltextSetting.DmsFileConfiguration.class);
        DmsFileDocumentIndex documentIndex = new DmsFileDocumentIndex();
        saveIndex(documentIndex, document -> {
            try {
                RequestSystemContextPathResolver.setSystem(system);
                IgnoreLoginUtils.login(userDetails);
                DmsFileDocumentIndex dmsFileDocumentIndex = (DmsFileDocumentIndex) document;
                LogicFileInfo logicFileInfo = mongoFileService.getLogicFileInfo(fileId);
                // MongoFileEntity mongoFileEntity = mongoFileService.getFile(fileId);
                String indexUuid = fileEntity.getUuid() + "_" + fileId;
                fillDmsFileDocumentIndex(dmsFileDocumentIndex, fileEntity, indexUuid, userDetails, system);
//                if (BooleanUtils.isTrue(dmsFileConfiguration.getIndexAttachment())) {
//                    dmsFileDocumentIndex.setContent(AttachmentUtils.getFileContent(mongoFileEntity, fileEntity.getContentType()));
//                } else {
                dmsFileDocumentIndex.setContent(logicFileInfo.getFileName());
                dmsFileDocumentIndex.setFileNames(logicFileInfo.getFileName());
                boolean indexAttachment = BooleanUtils.isTrue(dmsFileConfiguration.getIndexAttachment());
                dmsFileDocumentIndex.setAttachments(AttachmentUtils.logicFileInfos2Attachments(Lists.newArrayList(logicFileInfo), indexAttachment));
                // dmsFileDocumentIndex.setContent(AttachmentUtils.getFileContent(mongoFileEntity.getFileName()));
//                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            } finally {
                IgnoreLoginUtils.logout();
                RequestSystemContextPathResolver.clear();
            }
        }, async);
    }

    /**
     * @param fileEntity
     * @param indexUuid
     * @return
     */
    private DmsFileDocumentIndex fillDmsFileDocumentIndex(DmsFileDocumentIndex documentIndex, DmsFileEntity fileEntity,
                                                          String indexUuid, UserDetails userDetails, String system) {
        documentIndex.setUuid(indexUuid);
        documentIndex.setTitle(fileEntity.getFileName());
        documentIndex.setFileUuid(fileEntity.getUuid());
        documentIndex.setLibraryUuid(fileEntity.getLibraryUuid());
        documentIndex.setDataDefUuid(fileEntity.getDataDefUuid());
        documentIndex.setDataUuid(fileEntity.getDataUuid());
        // documentIndex.setFileNames(mongoFileEntity.getFileName());
        documentIndex.setContentType(fileEntity.getContentType());
        documentIndex.setUrl(String.format("/dms/file/viewer/%s", fileEntity.getUuid()));
        // documentIndex.setContent(Base64.getEncoder().encodeToString(IOUtils.toByteArray(mongoFileEntity.getInputstream())));
        documentIndex.setCreateTime(fileEntity.getCreateTime());
        if (StringUtils.equals(fileEntity.getCreator(), userDetails.getUserId())) {
            documentIndex.setCreator(userDetails.getUserName());
            documentIndex.setCreatorId(userDetails.getUserId());
        } else {
            Map<String, String> userNameMap = orgFacadeService.getUserNamesByUserIds(Lists.newArrayList(fileEntity.getCreator()));
            documentIndex.setCreator(userNameMap.get(fileEntity.getCreator()));
            documentIndex.setCreatorId(fileEntity.getCreator());
        }
        documentIndex.setModifyTime(fileEntity.getModifyTime());
        documentIndex.setModifier(userDetails.getUserName());
        documentIndex.setIsDelete(FileStatus.DELETE.equals(fileEntity.getStatus()) ? 1 : 0);
        documentIndex.setSystem(getSystemId(fileEntity.getUuid(), userDetails, system));
        documentIndex.setReaders(Sets.newHashSet(dmsFileServiceFacade.getFileReaders(fileEntity.getUuid())));
        documentIndex.setIndexOrder(20);
        return documentIndex;
    }

    private String getSystemId(String fileUuid, UserDetails userDetails, String system) {
        if (StringUtils.isNotBlank(system)) {
            return system;
        }

        DmsFolderEntity folderEntity = dmsFolderService.getByFileUuid(fileUuid);
        if (folderEntity != null && StringUtils.isNotBlank(folderEntity.getSystem())) {
            system = folderEntity.getSystem();
        }
        if (StringUtils.isNotBlank(system)) {
            return system;
        }

        system = (String) userDetails.getExtraData("system");
        if (StringUtils.isNotBlank(system)) {
            return system;
        }
        List<String> systemIds = SpringSecurityUtils.getAccessableSystem();
        return CollectionUtils.isNotEmpty(systemIds) ? systemIds.get(0) : null;
    }

    /**
     * 建立文件索引
     *
     * @param fileEntity
     * @param dyFormData
     */
    @Override
    public void index(DmsFileEntity fileEntity, DyFormData dyFormData) {
        this.index(fileEntity, dyFormData, true);
    }

    @Override
    public void index(DmsFileEntity fileEntity, DyFormData dyFormData, boolean async) {
        if (!isEnableFulltextIndex(fileEntity.getLibraryUuid())) {
            return;
        }

        try {
//            boolean hasFileField = false;
//            Set<String> fileIdSet = Sets.newHashSet();
//            // 表单附件字段
//            List<String> fileNames = dyFormData.doGetFieldNames();
//            for (String fileName : fileNames) {
//                if (!dyFormData.isFileField(fileName)) {
//                    continue;
//                }
//                hasFileField = true;
//                List<String> fileIds = dyFormData.getValueOfFileIds(fileName, dyFormData.getDataUuid());
//                if (CollectionUtils.isEmpty(fileIds)) {
//                    continue;
//                }
//                fileIdSet.addAll(fileIds);
//            }
//
//            // 存在附件字段，删除原有索引
//            if (hasFileField) {
//                try {
//                    this.deleteIndexByFieldEq("fileUuid", fileEntity.getUuid());
//                } catch (Exception e) {
//                    logger.error(e.getMessage(), e);
//                }
//            }

            UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
            String system = RequestSystemContextPathResolver.system();
            FulltextSetting fulltextSetting = fulltextSettingFacadeService.getSettingBySystem(system);
            FulltextSetting.DmsFileConfiguration dmsFileConfiguration = fulltextSetting.getBuiltIn("dms_file", FulltextSetting.DmsFileConfiguration.class);
            DmsFileDocumentIndex documentIndex = new DmsFileDocumentIndex();
            this.saveIndex(documentIndex, document -> {
                try {
                    RequestSystemContextPathResolver.setSystem(system);
                    IgnoreLoginUtils.login(userDetails);
                    DmsFileDocumentIndex dmsFileDocumentIndex = (DmsFileDocumentIndex) document;
                    String indexUuid = fileEntity.getUuid() + "_" + dyFormData.getDataUuid();
                    fillDmsFileDocumentIndex(dmsFileDocumentIndex, fileEntity, indexUuid, userDetails, system);
                    // 表单数据
                    // dmsFileDocumentIndex.setContent(Base64.getEncoder().encodeToString(extractDyformData(dyFormData)));
                    dmsFileDocumentIndex.setContent(getContent(dyFormData));
                    List<LogicFileInfo> fileInfos = getFileInfos(dyFormData);
                    dmsFileDocumentIndex.setFileNames(fileInfos.stream().map(LogicFileInfo::getFileName).collect(Collectors.joining(Separator.SEMICOLON.getValue())));
                    dmsFileDocumentIndex.setAttachments(AttachmentUtils.logicFileInfos2Attachments(fileInfos, dmsFileConfiguration.getIndexAttachment()));
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                } finally {
                    IgnoreLoginUtils.logout();
                    RequestSystemContextPathResolver.clear();
                }
            }, async);

//            // 表单附件
//            for (String fileId : fileIdSet) {
//                this.index(fileEntity, fileId, async);
//            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private List<LogicFileInfo> getFileInfos(DyFormData dyFormData) {
        List<LogicFileInfo> fileInfos = Lists.newArrayList();
        Map<String, List<Map<String, Object>>> formDatas = dyFormData.getFormDatas();
        if (MapUtils.isEmpty(formDatas)) {
            return fileInfos;
        }

        // 主表数据
        fileInfos.addAll(extractFileInfos(dyFormData));

        // 从表数据
        formDatas.forEach((formUuid, dataList) -> {
            if (!StringUtils.equals(formUuid, dyFormData.getFormUuid())) {
                dataList.forEach(data -> {
                    DyFormData subformData = dyFormData.getDyFormData(formUuid, Objects.toString(data.get("uuid"), StringUtils.EMPTY));
                    fileInfos.addAll(extractFileInfos(subformData));
                });
            }
        });
        return fileInfos;
    }

    private List<LogicFileInfo> extractFileInfos(DyFormData dyFormData) {
        List<LogicFileInfo> fileInfos = Lists.newArrayList();
        List<String> fieldNames = dyFormData.doGetFieldNames();
        List<String> fileIds = Lists.newArrayList();
        for (String fieldName : fieldNames) {
            if (dyFormData.isFileField(fieldName)) {
                Object fieldValue = dyFormData.getFieldValue(fieldName);
                if (fieldValue instanceof Collection) {
                    ((Collection<?>) fieldValue).forEach(file -> {
                        if (file instanceof Map) {
                            String fileId = Objects.toString(((Map<String, Object>) file).get("fileID"), StringUtils.EMPTY);
                            if (StringUtils.isNotBlank(fileId)) {
                                fileIds.add(fileId);
                            }
                        } else {
                            fileInfos.add((LogicFileInfo) file);
                        }
                    });
                }
            }
        }
        if (CollectionUtils.isNotEmpty(fileIds)) {
            fileInfos.addAll(mongoFileService.getLogicFileInfo(fileIds));
        }
        return fileInfos;
    }

    /**
     * 逻辑删除文件索引
     *
     * @param fileEntity
     */
    @Override
    public void logicDelete(DmsFileEntity fileEntity) {
        if (!isEnableFulltextIndex(fileEntity.getLibraryUuid())) {
            return;
        }

        this.logicDeleteIndexByFieldEq("fileUuid", fileEntity.getUuid());
    }

    private String getContent(DyFormData dyFormData) {
        List<String> fieldValues = Lists.newArrayList();
        Map<String, List<Map<String, Object>>> formDatas = dyFormData.getFormDatas();
        if (MapUtils.isEmpty(formDatas)) {
            return StringUtils.EMPTY;
        }

        // 主表数据
        fieldValues.addAll(extractContent(dyFormData));

        // 从表数据
        formDatas.forEach((formUuid, dataList) -> {
            if (!StringUtils.equals(formUuid, dyFormData.getFormUuid())) {
                dataList.forEach(data -> {
                    DyFormData subformData = dyFormData.getDyFormData(formUuid, Objects.toString(data.get("uuid"), StringUtils.EMPTY));
                    fieldValues.addAll(extractContent(subformData));
                });
            }
        });

        return StringUtils.join(fieldValues, Separator.SEMICOLON.getValue());
    }

    private Collection<String> extractContent(DyFormData dyFormData) {
        Iterator<String> it = dyFormData.doGetFieldNames().iterator();
        List<String> fieldValues = Lists.newArrayList();
        while (it.hasNext()) {
            String fieldName = it.next();
            Object fieldValue = dyFormData.getFieldDisplayValue(fieldName);
//            if (fieldValue instanceof Collection) {
//                if (((Collection) fieldValue).isEmpty()) {
//                    fieldValue = null;
//                } else if (dyFormData.isFileField(fieldName)) {
//                    fieldValue = ((Collection) fieldValue).stream().map(file -> {
//                        if (file instanceof Map) {
//                            String fileName = Objects.toString(((Map) file).get("fileName"), StringUtils.EMPTY);
//                            if (StringUtils.isBlank(fileName)) {
//                                fileName = Objects.toString(((Map) file).get("filename"), StringUtils.EMPTY);
//                            }
//                            return fileName;
//                        } else if (file instanceof LogicFileInfo) {
//                            return ((LogicFileInfo) file).getFileName();
//                        }
//                        return file.toString();
//                    }).collect(Collectors.joining(Separator.SEMICOLON.getValue()));
//                }
//            }
            String valueString = StringUtils.trim(Objects.toString(fieldValue, StringUtils.EMPTY));
            if (StringUtils.isNotBlank(valueString)) {
//                // 同时显示真实值和显示值
//                if (!dyFormData.isFileField(fieldName)) {
//                    String realValueString = Objects.toString(dyFormData.getFieldValue(fieldName), StringUtils.EMPTY);
//                    if (!StringUtils.equals(valueString, realValueString) && StringUtils.isNotBlank(realValueString)) {
//                        valueString += "(" + realValueString + ")";
//                    }
//                }
                fieldValues.add(valueString);
            }
        }
        return fieldValues;
    }

    /**
     * @param dyFormData
     * @return
     */
    private byte[] extractDyformData(DyFormData dyFormData) {
        Map<String, List<Map<String, Object>>> formDatas = dyFormData.getFormDatas();
        return Objects.toString(formDatas, StringUtils.EMPTY).getBytes(StandardCharsets.UTF_8);
    }

    /**
     * @param libraryUuid
     * @return
     */
    @Override
    public boolean isEnableFulltextIndex(String libraryUuid) {
        if (StringUtils.isBlank(libraryUuid)) {
            return false;
        }
        DmsFolderConfiguration dmsFolderConfiguration = dmsFileManagerService.getFolderConfiguration(libraryUuid);
        return BooleanUtils.isTrue(dmsFolderConfiguration.getEnabledFulltextIndex());
    }

    /**
     * 全文检索
     *
     * @param keyword
     * @param libraryUuid
     * @return
     */
    @Override
    public QueryData query(String keyword, String libraryUuid) {
        IndexRequestParams requestParams = new IndexRequestParams();
        requestParams.setKeyword(keyword);
        return query(requestParams, libraryUuid);
    }

    /**
     * 全文检索
     *
     * @param requestParams
     * @param libraryUuid
     * @return
     */
    @Override
    public QueryData query(IndexRequestParams requestParams, String libraryUuid) {
        Map<String, Object> filterMap = Maps.newHashMap();
        // 查询结果字段映射
        Map<String, String> resultFieldMapping = Maps.newHashMap();
        resultFieldMapping.put("attachment.content", "content");

        filterMap.put("libraryUuid", libraryUuid);
        requestParams.setFilterMap(filterMap);
        requestParams.setResultFieldMapping(resultFieldMapping);
        // requestParams.setOrder(new IndexRequestParams.Order(Sort.Direction.DESC, "modifyTime"));
        QueryData queryData = clientUtil.searchDocument(requestParams, DmsFileDocumentIndex.class, "dms_file_document");
//        List<Object> dataList = (List<Object>) queryData.getDataList();
//        if (CollectionUtils.isNotEmpty(dataList)) {
//            shieldFileContentWithoutAccessFilePermission(dataList);
//        }
        return queryData;
    }

    @Override
    public void deleteBySystem(String system) {
        this.deleteIndexByFieldEq("system", system);
    }

    /**
     * 屏蔽无权限访问文件的内容
     *
     * @param dataList
     */
    private void shieldFileContentWithoutAccessFilePermission(List<Object> dataList) {
        Map<String, List<Object>> listMap = ListUtils.list2group(dataList, "fileUuid");
        Set<String> fileUuids = dataList.stream().map(file -> ((DmsFileDocumentIndex) file).getFileUuid()).collect(Collectors.toSet());
        List<String> allowAccessFileUuids = dmsFileServiceFacade.filterFileWithPermission(fileUuids, FileActions.READ_FILE, FileActions.EDIT_FILE);
        for (String fileUuid : fileUuids) {
            if (!allowAccessFileUuids.contains(fileUuid)) {
                List<Object> files = listMap.get(fileUuid);
                files.forEach(file -> {
                    ((DmsFileDocumentIndex) file).setContent("<font color='#e75213'>无权限查看文件内容！</font>");
                });
            }
        }
    }
}
