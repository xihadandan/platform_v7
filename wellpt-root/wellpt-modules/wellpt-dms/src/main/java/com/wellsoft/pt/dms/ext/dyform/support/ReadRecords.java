/*
 * @(#)May 23, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.ext.dyform.support;

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
 * May 23, 2017.1	zhulh		May 23, 2017		Create
 * </pre>
 * @date May 23, 2017
 */
public class ReadRecords extends BaseObject {
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -2062890405043130730L;
    private List<ReadRecord> records;
    private String unreadUserName;

    /**
     * @return the records
     */
    public List<ReadRecord> getRecords() {
        return records;
    }

    /**
     * @param records 要设置的records
     */
    public void setRecords(List<ReadRecord> records) {
        this.records = records;
    }

    /**
     * @return the unreadUserName
     */
    public String getUnreadUserName() {
        return unreadUserName;
    }

    /**
     * @param unreadUserName 要设置的unreadUserName
     */
    public void setUnreadUserName(String unreadUserName) {
        this.unreadUserName = unreadUserName;
    }

    public static class ReadRecord extends BaseObject {
        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = 3570072341047546360L;
        private String userId;
        private String userName;
        private String readTime;

        /**
         * @return the userId
         */
        public String getUserId() {
            return userId;
        }

        /**
         * @param userId 要设置的userId
         */
        public void setUserId(String userId) {
            this.userId = userId;
        }

        /**
         * @return the userName
         */
        public String getUserName() {
            return userName;
        }

        /**
         * @param userName 要设置的userName
         */
        public void setUserName(String userName) {
            this.userName = userName;
        }

        /**
         * @return the readTime
         */
        public String getReadTime() {
            return readTime;
        }

        /**
         * @param readTime 要设置的readTime
         */
        public void setReadTime(String readTime) {
            this.readTime = readTime;
        }

    }
}
