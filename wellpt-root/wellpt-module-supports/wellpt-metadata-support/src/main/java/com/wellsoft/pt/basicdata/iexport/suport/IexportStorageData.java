/*
 * @(#)2015-6-16 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.iexport.suport;

import org.apache.commons.io.IOUtils;

import java.io.*;
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
public class IexportStorageData implements Serializable {
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 2874879391849916905L;

    private String uuid;
    private String name;
    private String type;
    private String parentUuid;
    private Integer recVer;
    private byte[] rowData;

    // 依赖优先
    private boolean priority = true;
    private byte[] data;

    // 数据记录
    private byte[] record;

    /**
     * @return the uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * @param uuid 要设置的uuid
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name 要设置的name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type 要设置的type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the parentUuid
     */
    public String getParentUuid() {
        return parentUuid;
    }

    /**
     * @param parentUuid 要设置的parentUuid
     */
    public void setParentUuid(String parentUuid) {
        this.parentUuid = parentUuid;
    }

    /**
     * @return the recVer
     */
    public Integer getRecVer() {
        return recVer;
    }

    /**
     * @param recVer 要设置的recVer
     */
    public void setRecVer(Integer recVer) {
        this.recVer = recVer;
    }

    /**
     * @return the rowData
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getRowData() {
        ObjectInputStream objectInputStream;
        try {
            ByteArrayOutputStream o = new ByteArrayOutputStream();
            o.write(this.rowData);
            ByteArrayInputStream bi = new ByteArrayInputStream(o.toByteArray());
            o.flush();
            o.close();

            objectInputStream = new ObjectInputStream(new BufferedInputStream(bi));
            return (Map<String, Object>) objectInputStream.readObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param rowData 要设置的rowData
     */
    public void setRowData(Map<String, Object> rowData) {
        try {
            ByteArrayInputStream i = null;
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(rowData);
            objectOutputStream.flush();
            objectOutputStream.close();

            i = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            this.rowData = IOUtils.toByteArray(i);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @return the priority
     */
    public boolean isPriority() {
        return priority;
    }

    /**
     * @param priority 要设置的priority
     */
    public void setPriority(boolean priority) {
        this.priority = priority;
    }

    /**
     * @return the data
     */
    public byte[] getData() {
        return data;
    }

    /**
     * @param data 要设置的data
     */
    public void setData(byte[] data) {
        this.data = data;
    }

    /**
     * @return the record
     */
    public byte[] getRecord() {
        return record;
    }

    /**
     * @param record 要设置的record
     */
    public void setRecord(byte[] record) {
        this.record = record;
    }

}
