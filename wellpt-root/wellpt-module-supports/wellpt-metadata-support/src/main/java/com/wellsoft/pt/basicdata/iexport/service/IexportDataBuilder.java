/*
 * @(#)2015-6-16 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.iexport.service;

import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.suport.IexportMetaData;
import com.wellsoft.pt.basicdata.iexport.suport.IexportStorageData;

import java.io.InputStream;
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
public interface IexportDataBuilder {
    IexportData build(String uuid, String type, Map<String, Object> filter);

    /**
     * 如何描述该方法
     *
     * @param input
     * @return
     */
    List<IexportData> build(InputStream input, boolean isProtobuf) throws Exception;

    /**
     * @param storageDatas
     * @return
     */
    List<IexportData> buildFromStorageDatas(List<IexportStorageData> storageDatas);

    /**
     * 如何描述该方法
     *
     * @param inputstream
     * @param uuid
     * @param type
     * @return
     */
    IexportData get(InputStream input, String uuid, String type) throws Exception;

    /**
     * @param storageDatas
     * @param metaDataMap
     * @return
     */
    List<IexportData> buildFromStorageDatas(List<IexportStorageData> storageDatas,
                                            Map<String, IexportMetaData> metaDataMap);

}
