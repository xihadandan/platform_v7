/*
 * @(#)2019年4月11日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.iexport.suport;

import org.springframework.core.NamedThreadLocal;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年4月11日.1	zhulh		2019年4月11日		Create
 * </pre>
 * @date 2019年4月11日
 */
public class IexportContextHolder {

    private static final ThreadLocal<IexportBundle> iexportBundleHolder = new NamedThreadLocal<IexportBundle>(
            "Iexport Bundle");

    private static final ThreadLocal<InsertedRecords> insertedRecordsHolder = new NamedThreadLocal<InsertedRecords>(
            "Inserted Records");

    private static final ThreadLocal<UpdatedRecords> updatedRecordsHolder = new NamedThreadLocal<UpdatedRecords>(
            "Updated Records");

    /**
     * @return
     */
    public static IexportBundle getIexportBundle() {
        IexportBundle iexportBundle = iexportBundleHolder.get();
        return iexportBundle;
    }

    /**
     * @param iexportBundle
     */
    public static void setIexportBundle(IexportBundle iexportBundle) {
        iexportBundleHolder.set(iexportBundle);
    }

    /**
     *
     */
    public static void removeIexportBundle() {
        iexportBundleHolder.remove();
    }

    /**
     * @return
     */
    public static InsertedRecords getInsertedRecords() {
        InsertedRecords insertedRecords = insertedRecordsHolder.get();
        return insertedRecords;
    }

    /**
     * @param insertedRecords
     */
    public static void setInsertedRecords(InsertedRecords insertedRecords) {
        insertedRecordsHolder.set(insertedRecords);
    }

    /**
     *
     */
    public static void removeInsertedRecords() {
        insertedRecordsHolder.remove();
    }

    /**
     * @return
     */
    public static UpdatedRecords getUpdatedRecords() {
        UpdatedRecords updatedRecords = updatedRecordsHolder.get();
        return updatedRecords;
    }

    /**
     * @param updatedRecords
     */
    public static void setUpdatedRecords(UpdatedRecords updatedRecords) {
        updatedRecordsHolder.set(updatedRecords);
    }

    /**
     *
     */
    public static void removeUpdatedRecords() {
        updatedRecordsHolder.remove();
    }

}
