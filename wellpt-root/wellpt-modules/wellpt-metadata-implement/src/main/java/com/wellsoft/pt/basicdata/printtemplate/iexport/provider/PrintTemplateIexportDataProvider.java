/*
 * @(#)2015-6-16 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.printtemplate.iexport.provider;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.entity.JpaEntity;
import com.wellsoft.pt.basicdata.iexport.acceptor.ErrorDataIexportData;
import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.acceptor.PrintMongoFileSerializable;
import com.wellsoft.pt.basicdata.iexport.service.AbstractIexportDataProvider;
import com.wellsoft.pt.basicdata.iexport.service.IexportDataRecordSetService;
import com.wellsoft.pt.basicdata.iexport.suport.*;
import com.wellsoft.pt.basicdata.printtemplate.entity.PrintTemplate;
import com.wellsoft.pt.basicdata.printtemplate.iexport.acceptor.PrintTemplateIexportData;
import com.wellsoft.pt.basicdata.printtemplate.service.PrintTemplateService;
import com.wellsoft.pt.jpa.util.HqlUtils;
import com.wellsoft.pt.repository.service.MongoFileService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * Description: 打印模板
 *
 * @author linz
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016-1-19.1	linz		2016-1-19		Create
 * </pre>
 * @date 2016-1-19
 */
@Service
@Transactional(readOnly = true)
public class PrintTemplateIexportDataProvider extends AbstractIexportDataProvider<PrintTemplate, String> {

    static {
        // 3.1、 打印模板
        TableMetaData.register(IexportType.PrintTemplate, "打印模板", PrintTemplate.class);
    }

    @Autowired
    private IexportDataRecordSetService iexportDataMetaDataService;
    @Autowired
    private MongoFileService mongoFileService;
    @Autowired
    private PrintTemplateService printTemplateService;

    public static byte[] longToBytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.SIZE);
        buffer.putLong(0, x);
        return buffer.array();
    }

    public static long bytesToLong(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.SIZE);
        buffer.put(bytes, 0, bytes.length);
        buffer.flip();// need flip
        return buffer.getLong();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.service.IexportDataProvider#getType()
     */
    @Override
    public String getType() {
        return IexportType.PrintTemplate;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.service.IexportDataProvider#getData(java.lang.String)
     */
    @Override
    public IexportData getData(String uuid) {
        PrintTemplate printTemplate = this.dao.get(PrintTemplate.class, uuid);
        if (printTemplate == null) {
            return new ErrorDataIexportData(IexportType.PrintTemplate, "找不到对应的打印模板依赖关系,可能已经被删除", "打印模板", uuid);
        }
        return new PrintTemplateIexportData(printTemplate);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.service.AbstractIexportDataProvider#storeData(com.wellsoft.pt.basicdata.iexport.acceptor.IexportData, boolean)
     */
    @SuppressWarnings("unchecked")
    @Override
    public void storeData(IexportData iexportData, boolean newVer) throws Exception {
        Object object = IexportDataResultSetUtils.inputStream2Object(iexportData.getInputStream());
        Map<String, Object> dataMap = (Map<String, Object>) object;
        IexportDataRecordSet printIexportData = (IexportDataRecordSet) dataMap
                .get(IexportDataResultSetUtils.ENTITY_BEAN);
        iexportDataMetaDataService.save(printIexportData);
        List<PrintMongoFileSerializable> printMongoFileSerializables = (List<PrintMongoFileSerializable>) dataMap
                .get(IexportDataResultSetUtils.MONGO_FILES);
        if (printMongoFileSerializables == null || printMongoFileSerializables.isEmpty()) {
            return;
        }
        for (PrintMongoFileSerializable printMongoFileSerializable : printMongoFileSerializables) {
            byte fileArray[] = printMongoFileSerializable.getFileArray();
            PrintTemplate printTemplate = this.dao.get(PrintTemplate.class, iexportData.getUuid());
            mongoFileService.saveFile(printTemplate.getFileUuid(), printMongoFileSerializable.getFileName(),
                    new ByteArrayInputStream(fileArray));
            mongoFileService.pushFileToFolder(printTemplate.getUuid(), printTemplate.getFileUuid(), "attach");
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.service.AbstractIexportDataProvider#getMetaData()
     */
    @Override
    public IexportMetaData getMetaData() {
        IexportMetaData iexportMetaData = super.getMetaData();
        // 打印模板ID生成方式
        iexportMetaData.registerColumnValueProcessor(TableMetaData.getTableName(IexportType.PrintTemplate), "id",
                EntityIdColumnValueProcessorFactory.getColumnValueProcessor(PrintTemplate.class));
        return iexportMetaData;
    }

    @Override
    public String getTreeName(PrintTemplate printTemplate) {
        return new PrintTemplateIexportData(printTemplate).getName();
    }


    public TreeNode treeNode(String uuid) {
        PrintTemplate template = printTemplateService.getOne(uuid);
        if (template == null) {
            return null;
        }
        TreeNode node = new TreeNode();
        node.setId(uuid);
        node.setType(getType());
        node.setName(getTreeName(template));
        Set<String> fileIds = this.getFileIds(template);
        if (CollectionUtils.isNotEmpty(fileIds)) {
            for (String f : fileIds) {
                node.appendChild(this.exportTreeNodeByDataProvider(f, IexportType.LogicFileInfo));
            }
        }
        return node;
    }

    @Override
    public Set<String> getFileIds(PrintTemplate printTemplate) {
        if (StringUtils.isNotBlank(printTemplate.getFileUuid())) {
            Set<String> fileIds = new HashSet<>();
            fileIds.add(printTemplate.getFileUuid());
            return fileIds;
        }
        return null;
    }

    @Override
    public <P extends JpaEntity<String>, C extends JpaEntity<String>> BusinessProcessor<PrintTemplate> saveOrUpdate(
            Map<String, ProtoDataBeanTree<PrintTemplate, P, C>> map, Collection<Serializable> uuids) {
        List<PrintTemplate> oldList = this.getList(uuids);
        List<PrintTemplate> list = new ArrayList<>();
        for (PrintTemplate old : oldList) {
            ProtoDataBeanTree<PrintTemplate, P, C> t = map.get(old.getUuid());
            // 版本号不一致 修改
            if (!old.getRecVer().equals(t.getProtoDataBean().getData().getRecVer())) {
                String sql = super.entityToUpdateSql(t.getProtoDataBean().getData());
                super.executeUpdateSql(sql, t);
                this.pushFileToFolder(t.getProtoDataBean());
            }
            map.remove(old.getUuid());
        }
        // 剩余的添加
        for (ProtoDataBeanTree<PrintTemplate, P, C> t : map.values()) {
            String sql = this.entityToInsertSql(t.getProtoDataBean().getData());
            list.add(t.getProtoDataBean().getData());
            this.executeUpdateSql(sql, t);
            this.pushFileToFolder(t.getProtoDataBean());
        }

        BusinessProcessor<PrintTemplate> businessProcessor = new BusinessProcessor<PrintTemplate>(list) {
            @Override
            public void handle(Map<String, ProtoDataBean> beanMap) {
                for (PrintTemplate printTemplate : this.getAddList()) {
                    printTemplateService.saveAcl(printTemplate);
                }
            }
        };
        return businessProcessor;
    }

    private void pushFileToFolder(ProtoDataBean<PrintTemplate> protoDataBean) {
        if (protoDataBean.getFileIds() != null) {
            PrintTemplate printTemplate = protoDataBean.getData();
            for (String fileId : protoDataBean.getFileIds()) {
                mongoFileService.popAllFilesFromFolder(printTemplate.getUuid());
                mongoFileService.pushFileToFolder(printTemplate.getUuid(), fileId, "attach");
            }
        }
    }

    @Override
    public void putChildProtoDataHqlParams(PrintTemplate printTemplate, Map<String, PrintTemplate> parentMap,
                                           Map<String, ProtoDataHql> hqlMap) {
        String start = getType() + Separator.UNDERLINE.getValue();
        parentMap.put(start + printTemplate.getUuid(), printTemplate);
        if (!hqlMap.containsKey(this.getChildHqlKey(IexportType.PrintContents))) {
            ProtoDataHql protoDataHql = new ProtoDataHql(getType(), "PrintContents");
            protoDataHql.setGenerateHql(new GenerateHql() {
                @Override
                public void build(ProtoDataHql protoDataHql) {
                    protoDataHql.getSbHql().append("from ").append(protoDataHql.getEntityName()).append(" where ");
                    HqlUtils.appendSql("TEMPLATE_UUID", protoDataHql.getParams(), protoDataHql.getSbHql(),
                            (Set<Serializable>) protoDataHql.getParams().get("templateUuids"));
                    protoDataHql.getSbHql().append(" order by sortOrder asc ");
                }
            });
            hqlMap.put(this.getChildHqlKey(IexportType.PrintContents), protoDataHql);
        }
        this.addDataUuid(hqlMap.get(this.getChildHqlKey(IexportType.PrintContents)), printTemplate.getUuid(),
                "templateUuids");

        if (StringUtils.isNotBlank(printTemplate.getModuleId())) {
            this.putAppFunctionParentMap(printTemplate, parentMap, hqlMap);
        }
    }

    @Override
    public Map<String, List<PrintTemplate>> getParentMapList(ProtoDataHql protoDataHql) {
        Map<String, List<PrintTemplate>> map = new HashMap<>();
        // 页面或组件定义依赖的套打模板
        if (protoDataHql.getParentType().equals(IexportType.AppPageDefinition)
                || protoDataHql.getParentType().equals(IexportType.AppWidgetDefinition)) {
            List<PrintTemplate> list = this.dao.find(protoDataHql.getHql().toString(), protoDataHql.getParams(),
                    PrintTemplate.class);
            for (PrintTemplate printTemplate : list) {
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue()
                        + protoDataHql.getParams().get("dependencyUuid");
                this.putParentMap(map, printTemplate, key);
            }
        } else if (protoDataHql.getParentType().equals(IexportType.FlowDefinition)) {
            if (protoDataHql.getParams().get("uuids") != null) {
                StringBuilder hql = new StringBuilder("from PrintTemplate where ");
                HqlUtils.appendSql("uuid", protoDataHql.getParams(), hql,
                        (Set<Serializable>) protoDataHql.getParams().get("uuids"));
                List<PrintTemplate> list = this.dao.find(hql.toString(), protoDataHql.getParams(), PrintTemplate.class);
                for (PrintTemplate printTemplate : list) {
                    String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + "printTemplate"
                            + Separator.UNDERLINE.getValue() + printTemplate.getUuid();
                    this.putParentMap(map, printTemplate, key);
                }
            }
            if (protoDataHql.getParams().get("ids") != null) {
                StringBuilder hql = new StringBuilder("from PrintTemplate where ");
                HqlUtils.appendSql("id", protoDataHql.getParams(), hql,
                        (Set<Serializable>) protoDataHql.getParams().get("ids"));
                List<PrintTemplate> list = this.dao.find(hql.toString(), protoDataHql.getParams(), PrintTemplate.class);
                for (PrintTemplate printTemplate : list) {
                    String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + "printTemplateId"
                            + Separator.UNDERLINE.getValue() + printTemplate.getId();
                    this.putParentMap(map, printTemplate, key);
                }
            }
        } else if (protoDataHql.getParentType().equals(IexportType.AppFunction)) {
            List<PrintTemplate> list = this.dao.find(protoDataHql.getHql().toString(), protoDataHql.getParams(),
                    PrintTemplate.class);
            for (PrintTemplate printTemplate : list) {
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + printTemplate.getUuid();
                this.putParentMap(map, printTemplate, key);
            }
        } else {
            super.getParentMapList(protoDataHql);
        }
        return map;
    }
}
