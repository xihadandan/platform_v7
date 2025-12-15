/*
 * @(#)2012-11-15 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datadict.dao;

import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.pt.basicdata.datadict.entity.DataDictionary;
import com.wellsoft.pt.jpa.dao.JpaDao;

import java.util.List;

/**
 * Description: 数据字典数据层访问类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-11-15.1	zhulh		2012-11-15		Create
 * </pre>
 * @date 2012-11-15
 */
public interface DataDictionaryDao extends JpaDao<DataDictionary, String> {

    List<DataDictionary> getDataDictionariesByParentTypeAndName(String parentType, String name,
                                                                PagingInfo pagingInof);

    DataDictionary getByParentTypeAndCode(String parentType, String code);

    List<DataDictionary> getDataDictionaries(String type, String code);

    DataDictionary getByType(String type);

    List<DataDictionary> getTopLevel();

    DataDictionary getById(String id);

    DataDictionary getByCategory(String category);

    List<DataDictionary> getByParent(DataDictionary dataDictionary);

    int countMaxSeqUnderParentDataDdic(DataDictionary parent);
}
