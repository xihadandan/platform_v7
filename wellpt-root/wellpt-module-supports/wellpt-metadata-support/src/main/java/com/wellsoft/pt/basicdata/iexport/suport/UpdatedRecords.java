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
public class UpdatedRecords extends BaseObject {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 2021178215714482434L;

    private List<UpdatedRecord> updatedRecords = Lists.newArrayList();

    /**
     * @return the updatedRecords
     */
    public List<UpdatedRecord> getUpdatedRecords() {
        return updatedRecords;
    }

    /**
     * @param updatedRecords 要设置的updatedRecords
     */
    public void setUpdatedRecords(List<UpdatedRecord> updatedRecords) {
        this.updatedRecords = updatedRecords;
    }

    public static class UpdatedRecord extends BaseObject {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = -988731549126611142L;

        // 表名
        private String tableName;

        // 主键名
        private String primaryKeyName;

        // 键名值
        private String primaryKeyValue;

        // 新数据JSON
        private String newContent;

        // 旧数据JSON
        private String oldContent;

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
         * @return the newContent
         */
        public String getNewContent() {
            return newContent;
        }

        /**
         * @param newContent 要设置的newContent
         */
        public void setNewContent(String newContent) {
            this.newContent = newContent;
        }

        /**
         * @return the oldContent
         */
        public String getOldContent() {
            return oldContent;
        }

        /**
         * @param oldContent 要设置的oldContent
         */
        public void setOldContent(String oldContent) {
            this.oldContent = oldContent;
        }
    }

}
