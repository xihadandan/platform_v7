/*
 * @(#)2016年1月13日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.iexport.suport;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.acceptor.PrintMongoFileSerializable;
import com.wellsoft.pt.basicdata.iexport.service.IexportDataRecordSetService;
import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;
import com.wellsoft.pt.repository.service.MongoFileService;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.ByteBuffer;
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
 * 2016年1月13日.1	zhulh		2016年1月13日		Create
 * </pre>
 * @date 2016年1月13日
 */
public class IexportDataResultSetUtils {
    public static final String ENTITY_BEAN = "entity";
    public static final String MONGO_FILES = "mongoDb";
    protected static final Logger logger = LoggerFactory.getLogger(IexportDataResultSetUtils.class);
    private static ByteBuffer buffer = ByteBuffer.allocate(Long.SIZE);

    public static InputStream object2InputStream(Object object) {
        ByteArrayInputStream i = null;
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(object);
            objectOutputStream.flush();
            objectOutputStream.close();

            i = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
        return i;
    }

    public static Object inputStream2Object(InputStream inputStream) {
        ObjectInputStream objectInputStream;
        try {
            objectInputStream = new ObjectInputStream(new BufferedInputStream(inputStream));
            return objectInputStream.readObject();
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
        return null;
    }

    public static InputStream mongoFileResultInputStream(IexportData iexportData, Object rawData, List<String> fileIds) {
        MongoFileService mongoFileService = ApplicationContextHolder.getBean(MongoFileService.class);
        IexportDataRecordSetService iexportDataMetaDataService = ApplicationContextHolder
                .getBean(IexportDataRecordSetService.class);
        Map<String, Object> object = new HashMap<String, Object>();
        try {

            List<PrintMongoFileSerializable> printMongoFileSerializables = new ArrayList<PrintMongoFileSerializable>();
            IexportDataRecordSet iexportDataResultSet = iexportDataMetaDataService.getData(iexportData);
            iexportDataResultSet.setRawData(rawData);
            object.put(ENTITY_BEAN, iexportDataResultSet);
            for (String fileId : fileIds) {
                MongoFileEntity mongoFileEntity = mongoFileService.getFile(fileId);
                if (mongoFileEntity == null) {
                    continue;
                }
                byte file[] = IexportDataResultSetUtils.toByteArray(mongoFileEntity.getInputstream());
                PrintMongoFileSerializable mongoFileSerializable = new PrintMongoFileSerializable();
                mongoFileSerializable.setFileId(mongoFileEntity.getFileID());
                mongoFileSerializable.setFileName(mongoFileEntity.getFileName());
                mongoFileSerializable.setFileArray(file);
                printMongoFileSerializables.add(mongoFileSerializable);

            }
            object.put(MONGO_FILES, printMongoFileSerializables);
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
        return IexportDataResultSetUtils.object2InputStream(object);
    }

    public static InputStream iexportDataResultSet2InputStream(IexportData iexportData, Object rawData) {
        IexportDataRecordSetService iexportDataMetaDataService = ApplicationContextHolder
                .getBean(IexportDataRecordSetService.class);
        ByteArrayInputStream i = null;
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            IexportDataRecordSet iexportDataResultSet = iexportDataMetaDataService.getData(iexportData);
            iexportDataResultSet.setRawData(rawData);
            objectOutputStream.writeObject(iexportDataResultSet);
            objectOutputStream.flush();
            objectOutputStream.close();

            i = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
        return i;
    }

    public static IexportDataRecordSet inputStream2IexportDataResultSet(InputStream inputStream) {
        ObjectInputStream objectInputStream;
        try {
            objectInputStream = new ObjectInputStream(new BufferedInputStream(inputStream));
            return (IexportDataRecordSet) objectInputStream.readObject();
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
        return null;
    }

    /**
     * @param uuid
     * @param type
     * @return
     */
    public static Map<String, Object> getIexportRowData(String uuid, String type) {
        IexportDataRecordSetService iexportDataMetaDataService = ApplicationContextHolder
                .getBean(IexportDataRecordSetService.class);
        return iexportDataMetaDataService.getIexportRowData(uuid, type);
    }

    public static byte[] longToBytes(long x) {
        buffer.putLong(0, x);
        return buffer.array();
    }

    public static long bytesToLong(byte[] bytes) {
        buffer.put(bytes, 0, bytes.length);
        buffer.flip();// need flip
        return buffer.getLong();
    }

    public static byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
        }
        return output.toByteArray();
    }

}
