/*
 * @(#)2014-6-13 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.excelexporttemplate.dao.impl;

import com.wellsoft.pt.basicdata.excelexporttemplate.dao.ExcelExportDefinitionDao;
import com.wellsoft.pt.basicdata.excelexporttemplate.entity.ExcelExportDefinition;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import org.springframework.stereotype.Repository;

/**
 * Description: 如何描述该类
 *
 * @author wubin
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-6-13.1	wubin		2014-6-13		Create
 * </pre>
 * @date 2014-6-13
 */
@Repository
public class ExcelExportDefinitionDaoImpl extends AbstractJpaDaoImpl<ExcelExportDefinition, String> implements
        ExcelExportDefinitionDao {
	/*public boolean idIsExists(String id, String uuid) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", id);
		StringBuilder hql = new StringBuilder(" from ExcelExportDefinition o where o.id = :id ");
		if (StringUtils.isNotBlank(uuid)) {
			hql.append(" and o.uuid <> :uuid ");
			params.put("uuid", uuid);
		}
		return this.query(hql.toString(), params, ExcelExportDefinition.class).size() > 0;
	}*/
}
