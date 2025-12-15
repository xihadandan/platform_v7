/*
 * @(#)2015-6-16 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.printtemplate.iexport.provider;

import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.entity.JpaEntity;
import com.wellsoft.pt.basicdata.iexport.acceptor.ErrorDataIexportData;
import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.acceptor.PrintMongoFileSerializable;
import com.wellsoft.pt.basicdata.iexport.service.AbstractIexportDataProvider;
import com.wellsoft.pt.basicdata.iexport.service.IexportDataRecordSetService;
import com.wellsoft.pt.basicdata.iexport.suport.*;
import com.wellsoft.pt.basicdata.printtemplate.entity.PrintContents;
import com.wellsoft.pt.basicdata.printtemplate.iexport.acceptor.PrintContentsIexportData;
import com.wellsoft.pt.repository.service.MongoFileService;
import org.apache.commons.lang3.StringUtils;
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
public class PrintContentsIexportDataProvider extends AbstractIexportDataProvider<PrintContents, String> {

    static {
        // 3.1、 打印模板
        TableMetaData.register(IexportType.PrintContents, "打印模板内容", PrintContents.class);
    }

    @Autowired
    private IexportDataRecordSetService iexportDataMetaDataService;
    @Autowired
    private MongoFileService mongoFileService;

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
        return IexportType.PrintContents;
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
        PrintContents printContents = this.dao.get(PrintContents.class, uuid);
        if (printContents == null) {
            return new ErrorDataIexportData(IexportType.PrintContents, "找不到对应的打印模板依赖关系,可能已经被删除", "打印模板内容", uuid);
        }
        return new PrintContentsIexportData(printContents);
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
            PrintContents printContents = this.dao.get(PrintContents.class, iexportData.getUuid());
            mongoFileService.saveFile(printContents.getFileUuid(), printMongoFileSerializable.getFileName(),
                    new ByteArrayInputStream(fileArray));
            mongoFileService.popAllFilesFromFolder(printContents.getUuid());
            mongoFileService.pushFileToFolder(printContents.getUuid(), printContents.getFileUuid(), "attach");
        }
    }

    @Override
    public String getTreeName(PrintContents printContents) {
        return new PrintContentsIexportData(printContents).getName();
    }

    @Override
    public Set<String> getFileIds(PrintContents printContents) {
        if (StringUtils.isNotBlank(printContents.getFileUuid())) {
            Set<String> fileIds = new HashSet<>();
            fileIds.add(printContents.getFileUuid());
            return fileIds;
        }
        return null;
    }

    @Override
    public <P extends JpaEntity<String>, C extends JpaEntity<String>> BusinessProcessor<PrintContents> saveOrUpdate(Map<String, ProtoDataBeanTree<PrintContents, P, C>> map, Collection<Serializable> uuids) {
        List<PrintContents> oldList = this.getList(uuids);
        List<PrintContents> list = new ArrayList<>();
        for (PrintContents old : oldList) {
            ProtoDataBeanTree<PrintContents, P, C> t = map.get(old.getUuid());
            //版本号不一致 修改
            if (!old.getRecVer().equals(t.getProtoDataBean().getData().getRecVer())) {
                String sql = super.entityToUpdateSql(t.getProtoDataBean().getData());
                super.executeUpdateSql(sql, t);
                this.pushFileToFolder(t.getProtoDataBean());
            }
            map.remove(old.getUuid());
        }
        //剩余的添加
        for (ProtoDataBeanTree<PrintContents, P, C> t : map.values()) {
            String sql = this.entityToInsertSql(t.getProtoDataBean().getData());
            list.add(t.getProtoDataBean().getData());
            this.executeUpdateSql(sql, t);
            this.pushFileToFolder(t.getProtoDataBean());
        }
        return null;
    }

    private void pushFileToFolder(ProtoDataBean<PrintContents> protoDataBean) {
        if (protoDataBean.getFileIds() != null) {
            PrintContents printContents = protoDataBean.getData();
            for (String fileId : protoDataBean.getFileIds()) {
                mongoFileService.popAllFilesFromFolder(printContents.getUuid());
                mongoFileService.pushFileToFolder(printContents.getUuid(), fileId, "attach");
            }
        }
    }

    @Override
    public Map<String, List<PrintContents>> getParentMapList(ProtoDataHql protoDataHql) {
        List<PrintContents> list = this.dao.find(protoDataHql.getHql().toString(), protoDataHql.getParams(), PrintContents.class);
        Map<String, List<PrintContents>> map = new HashMap<>();
        if (protoDataHql.getParentType().equals(IexportType.PrintTemplate)) {
            for (PrintContents printContents : list) {
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + printContents.getPrintTemplate().getUuid();
                this.putParentMap(map, printContents, key);
            }
        } else {
            super.getParentMapList(protoDataHql);
        }
        return map;
    }
}
