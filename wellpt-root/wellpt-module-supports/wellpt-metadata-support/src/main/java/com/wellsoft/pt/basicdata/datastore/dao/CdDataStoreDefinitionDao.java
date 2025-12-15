/*
 * @(#)2016年10月19日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datastore.dao;

import com.wellsoft.pt.basicdata.datastore.entity.CdDataStoreDefinition;
import com.wellsoft.pt.jpa.dao.JpaDao;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author xiem
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年10月19日.1	xiem		2016年10月19日		Create
 * </pre>
 * @date 2016年10月19日
 */
public interface CdDataStoreDefinitionDao extends JpaDao<CdDataStoreDefinition, String> {

    public CdDataStoreDefinition findById(String id);

    public void deleteByIds(String[] ids);

    public boolean idIsExists(String id, String uuid);

    public List<CdDataStoreDefinition> listSimpleEntityByIds(String[] ids);

    public List<CdDataStoreDefinition> listSimpleEntityBySystemUnitId(String systemUnitId);

}
