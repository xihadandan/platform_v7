/*
 * @(#)2019年4月10日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.iexport.suport;

import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.common.generator.service.IdGeneratorService;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年4月10日.1	zhulh		2019年4月10日		Create
 * </pre>
 * @date 2019年4月10日
 */
public class EntityIDGenerator implements IdentifierGenerator {

    private static final String ID_COLUMN_NAME = "id";

    private Class<? extends IdEntity> entityClass;

    private String pattern = "000";

    private boolean checkUnique = true;

    /**
     * @param entityClass
     */
    public EntityIDGenerator(Class<? extends IdEntity> entityClass) {
        super();
        this.entityClass = entityClass;
    }

    /**
     * @param entityClass
     * @param pattern
     * @param checkUnique
     */
    public EntityIDGenerator(Class<? extends IdEntity> entityClass, String pattern, boolean checkUnique) {
        super();
        this.entityClass = entityClass;
        this.pattern = pattern;
        this.checkUnique = checkUnique;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.suport.IdentifierGenerator#generate(com.wellsoft.pt.basicdata.iexport.suport.IexportDataRecord)
     */
    @Override
    public IexportDataIdentifier generate(IexportDataRecord iexportDataRecord) {
        IexportDataColumn idDataColumn = iexportDataRecord.getDataColumn(ID_COLUMN_NAME);
        String sufix = idDataColumn.getValue() + Separator.UNDERLINE.getValue();
        IdGeneratorService idGeneratorService = ApplicationContextHolder.getBean(IdGeneratorService.class);
        String id = idGeneratorService.generate(entityClass, pattern, sufix, checkUnique);
        IexportDataIdentifier identifier = new IexportDataIdentifier(id, id);
        return identifier;
    }

}
