/*
 * @(#)2019年4月16日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.iexport.suport;

import com.google.common.collect.Lists;
import com.wellsoft.context.base.BaseObject;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年4月16日.1	zhulh		2019年4月16日		Create
 * </pre>
 * @date 2019年4月16日
 */
public class InsertedRecords extends BaseObject {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 7794855830756063425L;

    // 插入的数据
    private List<InsertedRecord> insertedRecords = Lists.newArrayList();

    /**
     * @return the insertedRecords
     */
    public List<InsertedRecord> getInsertedRecords() {
        return insertedRecords;
    }

    /**
     * @param insertedRecords 要设置的insertedRecords
     */
    public void setInsertedRecords(List<InsertedRecord> insertedRecords) {
        this.insertedRecords = insertedRecords;
    }

    public static class InsertedRecord extends BaseObject {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = 447723106316157426L;

        // 表名
        private String tableName;

        // 主键名
        private String primaryKeyName;

        // 键名值
        private String primaryKeyValue;

        // 插入内容
        private String content;

        // 复制的值，不存在为空
        private String duplicatedPrimaryKeyValue;

        // 复制的内容，不存在为空
        private String duplicatedContent;

        /**
         * @return the tableName
         */
        public String getTableName() {
            return tableName;
        }

        /**
         * @param tableName 要设置的tableName
         */
        public void setTableName(String tableName) {
            this.tableName = tableName;
        }

        /**
         * @return the primaryKeyName
         */
        public String getPrimaryKeyName() {
            return primaryKeyName;
        }

        /**
         * @param primaryKeyName 要设置的primaryKeyName
         */
        public void setPrimaryKeyName(String primaryKeyName) {
            this.primaryKeyName = primaryKeyName;
        }

        /**
         * @return the primaryKeyValue
         */
        public String getPrimaryKeyValue() {
            return primaryKeyValue;
        }

        /**
         * @param primaryKeyValue 要设置的primaryKeyValue
         */
        public void setPrimaryKeyValue(String primaryKeyValue) {
            this.primaryKeyValue = primaryKeyValue;
        }

        /**
         * @return the content
         */
        public String getContent() {
            return content;
        }

        /**
         * @param content 要设置的content
         */
        public void setContent(String content) {
            this.content = content;
        }

        /**
         * @return the duplicatedPrimaryKeyValue
         */
        public String getDuplicatedPrimaryKeyValue() {
            return duplicatedPrimaryKeyValue;
        }

        /**
         * @param duplicatedPrimaryKeyValue 要设置的duplicatedPrimaryKeyValue
         */
        public void setDuplicatedPrimaryKeyValue(String duplicatedPrimaryKeyValue) {
            this.duplicatedPrimaryKeyValue = duplicatedPrimaryKeyValue;
        }

        /**
         * @return the duplicatedContent
         */
        public String getDuplicatedContent() {
            return duplicatedContent;
        }

        /**
         * @param duplicatedContent 要设置的duplicatedContent
         */
        public void setDuplicatedContent(String duplicatedContent) {
            this.duplicatedContent = duplicatedContent;
        }

    }

}
