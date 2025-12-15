/*
 * @(#)2019年4月17日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.iexport.suport;

import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年4月17日.1	zhulh		2019年4月17日		Create
 * </pre>
 * @date 2019年4月17日
 */
public class EntityIdColumnValueProcessorFactory {

    public static final ColumnValueProcessor getColumnValueProcessor(Class<? extends IdEntity> entityClass) {
        EntityIDGenerator entityIDGenerator = new EntityIDGenerator(entityClass);
        return new EntityIDColumnValueProcessor(entityIDGenerator);
    }

    public static final ColumnValueProcessor getColumnValueProcessor(Class<? extends IdEntity> entityClass,
                                                                     String suffixPattern, boolean checkUnique) {
        EntityIDGenerator entityIDGenerator = new EntityIDGenerator(entityClass, suffixPattern, checkUnique);
        return new EntityIDColumnValueProcessor(entityIDGenerator);
    }

    private static class EntityIDColumnValueProcessor implements ColumnValueProcessor {

        private EntityIDGenerator entityIDGenerator;

        /**
         * @param entityIDGenerator
         */
        public EntityIDColumnValueProcessor(EntityIDGenerator entityIDGenerator) {
            super();
            this.entityIDGenerator = entityIDGenerator;
        }

        /**
         * (non-Javadoc)
         *
         * @see com.wellsoft.pt.basicdata.iexport.suport.ColumnValueProcessor#doProcess(java.lang.Object, com.wellsoft.pt.basicdata.iexport.suport.IexportDataRecord, com.wellsoft.pt.basicdata.iexport.acceptor.IexportData, com.wellsoft.pt.basicdata.iexport.suport.IexportBundle)
         */
        @Override
        public Object doProcess(Object sourceValue, IexportDataRecord dataRecord, IexportData iexportData,
                                IexportBundle iexportBundle) {
            return entityIDGenerator.generate(dataRecord).getPropertyColumnValue();
        }

    }

}
