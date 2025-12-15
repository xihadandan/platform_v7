/*
 * @(#)2015-6-16 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.iexport.service.impl;

import com.google.common.collect.Lists;
import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.acceptor.IexportStorageDataProxy;
import com.wellsoft.pt.basicdata.iexport.protos.IexportStorageDataProto;
import com.wellsoft.pt.basicdata.iexport.service.IexportDataBuilder;
import com.wellsoft.pt.basicdata.iexport.suport.IexportDataHolder;
import com.wellsoft.pt.basicdata.iexport.suport.IexportDataProviderFactory;
import com.wellsoft.pt.basicdata.iexport.suport.IexportMetaData;
import com.wellsoft.pt.basicdata.iexport.suport.IexportStorageData;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.xerial.snappy.Snappy;

import java.io.*;
import java.util.ArrayList;
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
 * 2015-6-16.1	zhulh		2015-6-16		Create
 * </pre>
 * @date 2015-6-16
 */
@Service
@Transactional(readOnly = true)
public class IexportDataBuilderImpl implements IexportDataBuilder {

    /**
     * (non-Javadoc)
     *
     * @see IexportDataBuilder#build(String, String)
     */
    @Override
    public IexportData build(String uuid, String type, Map<String, Object> filter) {
        IexportData iexportData = IexportDataProviderFactory.getDataProvider(type).getData(uuid);
        IexportDataHolder holder = new IexportDataHolder();
        if (filter != null && filter.get(iexportData.getUuid()) == null) {
            return iexportData;
        }
        traverse(iexportData, null, holder, filter);
        return iexportData;
    }

    /**
     * 如何描述该方法
     *
     * @param iexportData
     * @param holder
     */
    private void traverse(IexportData iexportData, IexportData parentIexportData,
                          IexportDataHolder holder,
                          Map<String, Object> filter) {
        if (filter != null && filter.get(iexportData.getUuid()) == null) {
            return;
        }
        holder.add(iexportData, parentIexportData);
        List<IexportData> dependencies = iexportData.getDependencies();
        for (IexportData dependency : dependencies) {
            if (filter != null && filter.get(dependency.getUuid()) == null) {
                continue;
            }
            if (!holder.contains(dependency, iexportData)) {
                iexportData.getChildren().add(dependency);
                traverse(dependency, iexportData, holder, filter);
            }
        }
    }

    /**
     * (non-Javadoc)
     *
     * @throws IOException
     * @see IexportDataBuilder#build(InputStream)
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<IexportData> build(InputStream input, boolean isProtobuf) throws Exception {
        if (isProtobuf) {
            byte[] bytes = Snappy.uncompress(IOUtils.toByteArray(input));
            IexportStorageDataProto.IexportStorageDataCollection collection = IexportStorageDataProto.IexportStorageDataCollection.parseFrom(
                    bytes);
            return buildFromStorageDatas(collection);
        } else {
            ObjectInputStream o = new ObjectInputStream(input);
            List<IexportStorageData> storageDatas = (List<IexportStorageData>) o.readObject();
            return buildFromStorageDatas(storageDatas);
        }

    }

    private List<IexportData> buildFromStorageDatas(
            IexportStorageDataProto.IexportStorageDataCollection collection) {
        List<IexportData> exportDatas = Lists.newArrayList();
        List<IexportData> iexportDatas = Lists.newArrayList();
        Map<String, List<IexportData>> iexportDataMap = new HashMap<String, List<IexportData>>();
        List<IexportStorageDataProto.IexportStorageDataCollection.IexportStorageData> dataList = collection.getIexportDatasList();
        for (IexportStorageDataProto.IexportStorageDataCollection.IexportStorageData data : dataList) {
            IexportData iexportData = null;
            IexportStorageData storageData = new IexportStorageData();
            storageData.setUuid(data.getUuid());
            storageData.setType(data.getType());
            storageData.setData(data.getData().toByteArray());
            try {
                ByteArrayOutputStream o = new ByteArrayOutputStream();
                data.getRowData().writeTo(o);
                ByteArrayInputStream inputStream = new ByteArrayInputStream(
                        data.getRowData().toByteArray());
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                storageData.setRowData((Map<String, Object>) objectInputStream.readObject());
            } catch (Exception e) {

            }
            storageData.setName(data.getName());
            storageData.setParentUuid(data.getParentUuid());
            storageData.setPriority(data.getPriority());
            storageData.setRecord(data.getRecord().toByteArray());
            storageData.setRecVer(data.getRecVer());
            iexportData = new IexportStorageDataProxy(storageData);
            iexportDatas.add(iexportData);

            String parentUuid = storageData.getParentUuid();
            if (StringUtils.isBlank(parentUuid)) {
                exportDatas.add(iexportData);
                continue;
            }

            if (!iexportDataMap.containsKey(parentUuid)) {
                iexportDataMap.put(parentUuid, new ArrayList<IexportData>());
            }
            iexportDataMap.get(parentUuid).add(iexportData);
        }

        for (IexportData iexportData : iexportDatas) {
            String uuid = iexportData.getUuid() + iexportData.getType();
            if (iexportDataMap.containsKey(uuid)) {
                iexportData.setChildren(iexportDataMap.get(uuid));
            }
        }

        return exportDatas;
    }


    /**
     * (non-Javadoc)
     *
     * @see IexportDataBuilder#buildFromStorageDatas(List)
     */
    @Override
    public List<IexportData> buildFromStorageDatas(List<IexportStorageData> storageDatas) {
        return buildFromStorageDatas(storageDatas, null);
    }

    /**
     * (non-Javadoc)
     *
     * @see IexportDataBuilder#buildFromStorageDatas(List, Map)
     */
    @Override
    public List<IexportData> buildFromStorageDatas(List<IexportStorageData> storageDatas,
                                                   Map<String, IexportMetaData> metaDataMap) {
        List<IexportData> exportDatas = Lists.newArrayList();
        List<IexportData> iexportDatas = Lists.newArrayList();
        Map<String, List<IexportData>> iexportDataMap = new HashMap<String, List<IexportData>>();
        for (IexportStorageData storageData : storageDatas) {
            IexportData iexportData = null;
            if (metaDataMap != null) {
                iexportData = new IexportStorageDataProxy(storageData,
                        metaDataMap.get(storageData.getType()));
            } else {
                iexportData = new IexportStorageDataProxy(storageData);
            }
            iexportDatas.add(iexportData);

            String parentUuid = storageData.getParentUuid();
            if (StringUtils.isBlank(parentUuid)) {
                exportDatas.add(iexportData);
                continue;
            }

            if (!iexportDataMap.containsKey(parentUuid)) {
                iexportDataMap.put(parentUuid, new ArrayList<IexportData>());
            }
            iexportDataMap.get(parentUuid).add(iexportData);
        }

        for (IexportData iexportData : iexportDatas) {
            String uuid = iexportData.getUuid() + iexportData.getType();
            if (iexportDataMap.containsKey(uuid)) {
                iexportData.setChildren(iexportDataMap.get(uuid));
            }
        }

        return exportDatas;
    }

    /**
     * (non-Javadoc)
     *
     * @see IexportDataBuilder#get(InputStream, String, String)
     */
    @Override
    @SuppressWarnings("unchecked")
    public IexportData get(InputStream input, String uuid, String type) throws Exception {
        ObjectInputStream o = new ObjectInputStream(input);
        List<IexportStorageData> datas = (List<IexportStorageData>) o.readObject();
        for (IexportStorageData iexportStorageData : datas) {
            String dataUuid = iexportStorageData.getUuid();
            String dataType = iexportStorageData.getType();
            if (StringUtils.equals(dataUuid, uuid) && StringUtils.equals(dataType, type)) {
                return new IexportStorageDataProxy(iexportStorageData);
            }
        }
        return null;
    }

}
