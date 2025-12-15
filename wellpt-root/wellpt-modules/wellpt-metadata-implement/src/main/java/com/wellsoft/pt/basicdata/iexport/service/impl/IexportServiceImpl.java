/*
 * @(#)2015-6-16 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.iexport.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.service.*;
import com.wellsoft.pt.basicdata.iexport.suport.*;
import com.wellsoft.pt.basicdata.iexport.table.ExportTable;
import com.wellsoft.pt.basicdata.iexport.visitor.ExportVisitor;
import com.wellsoft.pt.basicdata.iexport.visitor.ImportVisitor;
import com.wellsoft.pt.basicdata.iexport.web.IexportController;
import com.wellsoft.pt.cache.CacheManager;
import com.wellsoft.pt.jpa.criteria.Criteria;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;
import com.wellsoft.pt.repository.service.MongoFileService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
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
 * 2015-6-16.1	zhulh		2015-6-16		Create
 * </pre>
 * @date 2015-6-16
 */
@Service
@Transactional
public class IexportServiceImpl extends BaseServiceImpl implements IexportService {

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private IexportDataBuilder iexportDataBuilder;

    @Autowired
    private MongoFileService mongoFileService;

    @Autowired
    private IexportDataRecordSetService iexportDataRecordSetService;

    @Autowired
    private DataImportLogService dataImportLogService;
    @Autowired
    private ImportIexportService importIexportService;

    /**
     * (non-Javadoc)
     *
     * @see IexportService#getExportTree(String, String)
     */
    @Override
    @Transactional(readOnly = true)
    public List<TreeNode> getExportTree(String uuid, String type) {
        if (IexportController.isNewExport()) {
            return importIexportService.getExportTree(uuid, type);
        }
        String uuids[] = uuid.split(";");
        List<TreeNode> treeNodes = new ArrayList<TreeNode>();
        IexportDataHolder holder = new IexportDataHolder();
        for (String uuidTemp : uuids) {
            IexportData iexportData = iexportDataBuilder.build(uuidTemp, type, null);
            if (holder.contains(iexportData)) {
                continue;
            } else {
                holder.add(iexportData);
            }
            TreeNode nodeChild = buildTree(iexportData, false, holder);
            if (nodeChild != null) {
                treeNodes.add(nodeChild);
            }
        }
        return treeNodes;
    }

    /**
     * (non-Javadoc)
     *
     * @see IexportService#importData(InputStream)
     */
    @Override
    @Transactional(readOnly = false, timeout = 240000)
    public void importData(InputStream input, boolean newVer, String importIds, boolean isProtobuf) {
        List<String> listUuid = new ArrayList<String>();
        ImportVisitor visitor = null;
        //        // 适配租户升级接口租户升级无勾选记录
        //        if (StringUtils.isBlank(importIds)) {
        //            try {
        //                IexportDataRecordSetCacheUtils.clear();
        //                List<IexportData> iexportDatas = iexportDataBuilder.build(input);
        //                visitor = new ImportVisitor(listUuid, newVer);
        //                for (IexportData iexportData : iexportDatas) {
        //                    iexportData.accept(visitor);
        //                }
        //            } catch (Exception e) {
        //                if (e instanceof RuntimeException) {
        //                    throw (RuntimeException) e;
        //                }
        //                throw new RuntimeException(e);
        //            }
        //        } else {
        // 界面勾选记录升级
        String importIdList[] = importIds.split(";");
        Set<String> importSet = Sets.newHashSet(importIdList);
        try {
            IexportDataRecordSetCacheUtils.clear();
            List<IexportData> iexportDatas = iexportDataBuilder.build(input, isProtobuf);
            visitor = new ImportVisitor(listUuid, newVer);
            for (IexportData iexportData : iexportDatas) {
                if (importSet.contains(iexportData.getUuid())) {
                    iexportData.accept(visitor, importSet);
                }
            }
            cacheManager.clearAllCache();
        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new RuntimeException(e);
        }
        //        }
    }

    /**
     * (non-Javadoc)
     *
     * @see IexportService#importData(String, boolean, String)
     */
    @Override
    @Transactional(timeout = 240000)
    public void importData(String fileId, boolean newVer, String importIds) {
        MongoFileEntity mongoFileEntity = mongoFileService.getFile(fileId);
        InputStream input = mongoFileEntity.getInputstream();
        if (IexportController.isNewExport()) {
            try {
                importIexportService.importData(input, importIds);
//                cacheManager.clearAllCache();
            } catch (Exception e) {
                logger.error(ExceptionUtils.getStackTrace(e));
                throw new RuntimeException(e);
            }
        } else {
            importData(input, newVer, importIds, mongoFileEntity.getFileName().endsWith("defpf"));
            IOUtils.closeQuietly(input);
        }
        // 记录日志
        dataImportLogService.log(mongoFileEntity, importIds);
    }

    /**
     * 生成对应的treeNode
     *
     * @param iexportData
     * @return
     */
    private TreeNode buildTree(IexportData iexportData, Boolean isImport, IexportDataHolder holder) {
        Map<String, String> treeNodeData = new HashMap<String, String>();
        treeNodeData.put("type", iexportData.getType());
        TreeNode node = new TreeNode();
        node.setId(iexportData.getUuid());
        if (IexportType.ErrorData.equals(iexportData.getType())) {
            node.setData(iexportData.getType());
        }
        if (isImport && iexportDataRecordSetService.isExists(iexportData)) {
            /**优化**/
            treeNodeData.put("color", "green");
            if (iexportDataRecordSetService.hasDifference(iexportData)) {
                treeNodeData.put("color", "orange");//red->orange
            }
        }
        node.setName(iexportData.getName());
        node.setChecked(true);
        node.setData(treeNodeData);
        List<IexportData> iexportDatas = iexportData.getChildren();
        for (IexportData data : iexportDatas) {
            if (holder.contains(data)) {
                continue;
            } else {
                holder.add(data);
            }
            TreeNode nodeChild = buildTree(data, isImport, holder);
            if (nodeChild != null) {
                node.getChildren().add(nodeChild);
            }
        }
        return node;
    }

    @Override
    public ExportVisitor getExportData(String[] exportIds, String id, String type) {
        Map<String, Object> filter = new HashMap<String, Object>();
        for (String uuid : exportIds) {
            filter.put(uuid, uuid);
        }
        String[] ids = id.split(";");
        ExportVisitor visitor = new ExportVisitor();
        for (String idTemp : ids) {
            IexportData iexportData = iexportDataBuilder.build(idTemp, type, filter);
            List<IexportData> iexportDatas = iexportData.getChildren();
            List<IexportData> chilidDatas = new ArrayList<IexportData>();
            for (IexportData children : iexportDatas) {
                chilidDatas.add(children);
            }
            iexportData.setChildren(chilidDatas);
            iexportData.accept(visitor);
        }
        return visitor;
    }

    /**
     * (non-Javadoc)
     *
     * @see IexportService#getImportTree(String)
     */
    @Override
    @Transactional(readOnly = true)
    public List<TreeNode> getImportTree(String fileId) {
        MongoFileEntity mongoFileEntity = mongoFileService.getFile(fileId);
        TreeNode treeNode = null;
        List<TreeNode> treeNodes = new ArrayList<TreeNode>();
        if (IexportController.isNewExport()) {
            try {
                treeNodes = importIexportService.getImportTree(mongoFileEntity.getInputstream());
            } catch (Exception e) {
                logger.error(ExceptionUtils.getStackTrace(e));
                throw new RuntimeException(e);
            }
        } else {
            try {
                List<IexportData> iexportDatas = iexportDataBuilder.build(mongoFileEntity.getInputstream(), mongoFileEntity
                        .getFileName().endsWith("defpf"));
                IexportDataHolder holder = new IexportDataHolder();
                for (IexportData iexportData : iexportDatas) {
                    if (holder.contains(iexportData)) {
                        continue;
                    } else {
                        holder.add(iexportData);
                    }
                    treeNode = buildTree(iexportData, true, holder);
                    treeNodes.add(treeNode);
                }
            } catch (Exception e) {
                logger.error(ExceptionUtils.getStackTrace(e));
                throw new RuntimeException(e);
            }
        }

        return treeNodes;
    }

    /**
     * (non-Javadoc)
     *
     * @see IexportService#getDifference(String, String, String)
     */
    @Override
    @Transactional(readOnly = true)
    public IexportDataDifference getDifference(String fileId, String uuid, String type) {
        MongoFileEntity mongoFileEntity = mongoFileService.getFile(fileId);
        IexportDataDifference difference = new IexportDataDifference();
        try {
            if (IexportController.isNewExport()) {
                return importIexportService.getDifference(mongoFileEntity.getInputstream(), uuid);
            }
            IexportData iexportData = iexportDataBuilder.get(mongoFileEntity.getInputstream(), uuid, type);
            if (iexportData == null) {
                return difference;
            }
            // 构建不同的信息
            return getDifference(iexportData, IexportDataProviderFactory.getDataProvider(type).getData(uuid));
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<TreeNode> exportTableDataAsTree(ExportTable table) {
        List<TreeNode> treeNodes = Lists.newArrayList();
        // 1. 查询表结构
        StringBuilder sql = new StringBuilder("SELECT * FROM ").append(table.getTable());
        if (StringUtils.isNotBlank(table.getWhere())) {
            sql.append(" WHERE ").append(table.getWhere());
        }
        Criteria criteria = nativeDao.createSqlCriteria(sql.toString());
        if (MapUtils.isNotEmpty(table.getParams())) {
            Set<String> keys = table.getParams().keySet();
            for (String k : keys) {
                criteria.addQueryParams(k, table.getParams().get(k));
            }
        }
        List<HashMap> dataList = criteria.list(HashMap.class);
        if (CollectionUtils.isNotEmpty(dataList)) {
            for (HashMap map : dataList) {
                TreeNode node = new TreeNode();
                node.setType(table.getDescriptionMetadata().getTypeName());
                node.setName(map.get(table.getDescriptionMetadata().getTitleColumn()).toString());
                node.setId(map.get(table.getDescriptionMetadata().getIdColumn()).toString());
                node.setData(map);
                treeNodes.add(node);
                Set<Map.Entry> entrySet = map.entrySet();
                Map<String, Object> parentDataAsParam = Maps.newHashMap();
                for (Map.Entry en : entrySet) {
                    parentDataAsParam.put(table.getTable().toLowerCase() + "_" + en.getKey(), en.getValue());
                }
                this.cascadeExportSubTables(table, parentDataAsParam, node);
            }


        }

        return treeNodes;
    }


    private void cascadeExportSubTables(ExportTable table, Map<String, Object> parentDataAsParam, TreeNode parentNode) {
        if (CollectionUtils.isNotEmpty(table.getSubExportTables())) {
            for (ExportTable subTable : table.getSubExportTables()) {
                StringBuilder subSql = new StringBuilder("SELECT * FROM ").append(subTable.getTable());
                if (StringUtils.isNotBlank(subTable.getWhere())) {
                    subSql.append(" WHERE ").append(subTable.getWhere());
                }
                Criteria subCriteria = nativeDao.createSqlCriteria(subSql.toString());
                if (MapUtils.isNotEmpty(subTable.getParams())) {
                    Set<String> keys = subTable.getParams().keySet();
                    for (String k : keys) {
                        subCriteria.addQueryParams(k, subTable.getParams().get(k));
                    }
                }
                Set<Map.Entry<String, Object>> entrySet = parentDataAsParam.entrySet();
                for (Map.Entry<String, Object> en : entrySet) {
                    subCriteria.addQueryParams(en.getKey(), en.getValue());
                }

                List<HashMap> subDataList = subCriteria.list(HashMap.class);
                if (CollectionUtils.isNotEmpty(subDataList)) {
                    for (HashMap subDataMap : subDataList) {
                        TreeNode subTreeNode = new TreeNode();
                        subTreeNode.setType(subTable.getDescriptionMetadata().getTypeName());
                        subTreeNode.setName(subDataMap.get(subTable.getDescriptionMetadata().getTitleColumn()).toString());
                        subTreeNode.setId(subDataMap.get(subTable.getDescriptionMetadata().getIdColumn()).toString());
                        subTreeNode.setData(subDataMap);
                        parentNode.getChildren().add(subTreeNode);
                        Set<Map.Entry> subEntrySet = subDataMap.entrySet();
                        Map<String, Object> dataParam = Maps.newHashMap();
                        dataParam.putAll(parentDataAsParam);
                        for (Map.Entry en : subEntrySet) {
                            dataParam.put(subTable.getTable().toLowerCase() + "_" + en.getKey(), en.getValue());
                        }
                        this.cascadeExportSubTables(subTable, dataParam, parentNode);
                    }
                }
            }
        }
    }

    /**
     * @param controlIexportData
     * @param testIexportData
     * @return
     */
    private IexportDataDifference getDifference(IexportData controlIexportData, IexportData testIexportData) {
        Map<String, Object> controlData = controlIexportData.getRowData();
        Map<String, Object> testData = testIexportData.getRowData();
        Set<String> keys = new LinkedHashSet<String>();
        keys.addAll(controlData.keySet());
        keys.addAll(testData.keySet());

        IexportDataDifference dataDifference = new IexportDataDifference();
        List<IexportDataDifferenceDetail> dataDifferenceDetails = new ArrayList<IexportDataDifferenceDetail>();
        dataDifference.setUuid(controlIexportData.getUuid());
        dataDifference.setType(controlIexportData.getType());
        dataDifference.setDataDifferenceDetails(dataDifferenceDetails);
        for (String key : keys) {
            Object controlValue = StringUtils.EMPTY;
            Object testValue = StringUtils.EMPTY;
            if (controlData.containsKey(key)) {
                controlValue = controlData.get(key);
            }
            if (testData.containsKey(key)) {
                testValue = testData.get(key);
            }
            IexportDataDifferenceDetail detail = new IexportDataDifferenceDetail();
            detail.setFieldName(key);
            detail.setControlValue(ObjectUtils.toString(controlValue));
            detail.setTestValue(ObjectUtils.toString(testValue));
            dataDifferenceDetails.add(detail);
        }
        return dataDifference;
    }

    /**
     * @param iexportDatas
     * @param uuid
     * @param type
     * @return
     */
    private IexportData getData(List<IexportData> iexportDatas, String uuid, String type) {
        for (IexportData iexportData : iexportDatas) {
            String dataUuid = iexportData.getUuid();
            String dataType = iexportData.getType();
            if (StringUtils.equals(dataUuid, uuid) && StringUtils.equals(dataType, type)) {
                return iexportData;
            }
        }
        return null;
    }

}
