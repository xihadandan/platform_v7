/*
 * @(#)2015-6-16 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.iexport.visitor;

import com.google.protobuf.ByteString;
import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.protos.IexportStorageDataProto;
import com.wellsoft.pt.basicdata.iexport.suport.IexportStorageData;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.xerial.snappy.Snappy;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Description: 生成导出的数据
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
public class ExportVisitor implements Visitor {

    @Autowired
    private ArrayList<IexportStorageData> datas = new ArrayList<IexportStorageData>();

    private IexportStorageDataProto.IexportStorageDataCollection.Builder builder
            = IexportStorageDataProto.IexportStorageDataCollection.newBuilder();

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.visitor.Visitor#visit(com.wellsoft.pt.basicdata.iexport.acceptor.IexportData)
     */
    @Override
    public void visit(IexportData iexportData) {
        try {
            boolean proto = true;
            if (proto) {//使用protobuf数据序列化
                IexportStorageDataProto.IexportStorageDataCollection.IexportStorageData.Builder dataBuilder
                        = IexportStorageDataProto.IexportStorageDataCollection.IexportStorageData.newBuilder();
                dataBuilder.setUuid(iexportData.getUuid());
                dataBuilder.setName(iexportData.getName());
                dataBuilder.setType(iexportData.getType());
                dataBuilder.setPriority(iexportData.isPriority());
                if (iexportData.getParent() != null) {
                    dataBuilder.setParentUuid(
                            iexportData.getParent().getUuid() + iexportData.getParent().getType());
                }
                dataBuilder.setData(
                        ByteString.copyFrom(IOUtils.toByteArray(iexportData.getInputStream())));
                dataBuilder.setRecVer(iexportData.getRecVer());
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(
                        byteArrayOutputStream);
                objectOutputStream.writeObject(iexportData.getRowData());
                objectOutputStream.flush();
                dataBuilder.setRowData(ByteString.copyFrom(byteArrayOutputStream.toByteArray()));
                IOUtils.closeQuietly(objectOutputStream);
                builder.addIexportDatas(dataBuilder);
            } else {

                IexportStorageData data = new IexportStorageData();
                data.setUuid(iexportData.getUuid());
                data.setName(iexportData.getName());
                data.setType(iexportData.getType());
                data.setPriority(iexportData.isPriority());
                if (iexportData.getParent() != null) {
                    data.setParentUuid(
                            iexportData.getParent().getUuid() + iexportData.getParent().getType());
                }
                data.setData(IOUtils.toByteArray(iexportData.getInputStream()));
                data.setRecVer(iexportData.getRecVer());
                data.setRowData(iexportData.getRowData());
                // data.setRecord(SerializationUtils.serializationObject(iexportData.getRecord()));
                datas.add(data);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void write(OutputStream output) {
        ObjectOutputStream out = null;
        try {
            if (datas.isEmpty()) {
                output.write(Snappy.compress(builder.build().toByteArray()));
            } else {
                out = new ObjectOutputStream(output);
                out.writeObject(datas);
                out.flush();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(out);
        }
    }
}
