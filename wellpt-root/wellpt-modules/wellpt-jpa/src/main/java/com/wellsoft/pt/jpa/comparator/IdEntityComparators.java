/*
 * @(#)May 23, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.comparator;

import com.wellsoft.context.jdbc.entity.IdEntity;

import java.util.Comparator;
import java.util.Date;

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
public class IdEntityComparators {
    // 创建时间升序
    public static final Comparator<IdEntity> CREATE_TIME_ASC = new CreateTimeAscComparator();
    // 创建时间降序
    public static final Comparator<IdEntity> CREATE_TIME_DESC = new CreateTimeDescComparator();
    // 修改时间升序
    public static final Comparator<IdEntity> MODIFY_TIME_ASC = new ModifyTimeAscComparator();
    // 修改时间降序
    public static final Comparator<IdEntity> MODIFY_TIME_DESC = new ModifyTimeDescComparator();

    /**
     * Description: 创建时间升序
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
    private static class CreateTimeAscComparator implements Comparator<IdEntity> {

        /**
         * (non-Javadoc)
         *
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        @Override
        public int compare(IdEntity o1, IdEntity o2) {
            Date t1 = o1.getCreateTime();
            Date t2 = o2.getCreateTime();
            if (t1 == null || t2 == null) {
                return 1;
            }
            return t1.compareTo(t2);
        }

    }

    /**
     * Description: 创建时间降序
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
    private static class CreateTimeDescComparator implements Comparator<IdEntity> {

        /**
         * (non-Javadoc)
         *
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        @Override
        public int compare(IdEntity o1, IdEntity o2) {
            Date t1 = o1.getCreateTime();
            Date t2 = o2.getCreateTime();
            if (t1 == null || t2 == null) {
                return 1;
            }
            return -t1.compareTo(t2);
        }

    }

    /**
     * Description: 修改时间升序
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
    private static class ModifyTimeAscComparator implements Comparator<IdEntity> {

        /**
         * (non-Javadoc)
         *
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        @Override
        public int compare(IdEntity o1, IdEntity o2) {
            Date t1 = o1.getModifyTime();
            Date t2 = o2.getModifyTime();
            if (t1 == null || t2 == null) {
                return 1;
            }
            return t1.compareTo(t2);
        }
    }

    /**
     * Description: 修改时间降序
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
    private static class ModifyTimeDescComparator implements Comparator<IdEntity> {

        /**
         * (non-Javadoc)
         *
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        @Override
        public int compare(IdEntity o1, IdEntity o2) {
            Date t1 = o1.getModifyTime();
            Date t2 = o2.getModifyTime();
            if (t1 == null || t2 == null) {
                return 1;
            }
            return -t1.compareTo(t2);
        }

    }
}
