/*
 * @(#)2018年4月19日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.excelexporttemplate.service.impl;

import com.wellsoft.pt.basicdata.excelexporttemplate.dao.ExcelExportDefinitionDao;
import com.wellsoft.pt.basicdata.excelexporttemplate.entity.ExcelExportDefinition;
import com.wellsoft.pt.basicdata.excelexporttemplate.service.ExcelExportDefinitionService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年4月19日.1	chenqiong		2018年4月19日		Create
 * </pre>
 * @date 2018年4月19日
 */
@Service
public class ExcelExportDefinitionServiceImpl extends
        AbstractJpaServiceImpl<ExcelExportDefinition, ExcelExportDefinitionDao, String> implements
        ExcelExportDefinitionService {

    @Override
    public boolean idIsExists(String id, String uuid) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", id);
        StringBuilder hql = new StringBuilder("select count(uuid) from ExcelExportDefinition o where o.id = :id ");
        if (StringUtils.isNotBlank(uuid)) {
            hql.append(" and o.uuid <> :uuid ");
            params.put("uuid", uuid);
        }
        return (Long) this.dao.getNumberByHQL(hql.toString(), params) > 0;
    }

}
