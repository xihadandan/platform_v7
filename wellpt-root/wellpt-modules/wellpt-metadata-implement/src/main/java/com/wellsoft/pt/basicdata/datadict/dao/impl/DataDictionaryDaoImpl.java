/*
 * @(#)2012-11-15 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datadict.dao.impl;

import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.pt.basicdata.datadict.dao.DataDictionaryDao;
import com.wellsoft.pt.basicdata.datadict.entity.DataDictionary;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
@Repository
public class DataDictionaryDaoImpl extends AbstractJpaDaoImpl<DataDictionary, String> implements
        DataDictionaryDao {
    /**
     * 根据父节点查询所有子结点
     */
    private final static String QUERY_BY_PARENT = "from DataDictionary t where t.parent.uuid = :parentUuid order by seq asc";
    /**
     * 查询所有根结点
     */
    private final static String QUERY_BY_TOP_LEVEL = "from DataDictionary t where t.parent.uuid = null or t.parent.uuid = '' order by seq asc , code asc";

    // 查询字典类型下子结点的指定字典编码的数据字典列表
    private final static String QUERY_BY_TYPE_AND_CODE = "from DataDictionary t where t.parent.type = :type and t.code = :code";

    /**
     * @param dataDictionary
     * @return
     */
    @Override
    public List<DataDictionary> getByParent(DataDictionary dataDictionary) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("parentUuid", dataDictionary.getUuid());
        return this.listByHQL(QUERY_BY_PARENT, values);
    }

    @Override
    public int countMaxSeqUnderParentDataDdic(DataDictionary parent) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("puuid", parent != null ? parent.getUuid() : null);
        return ((BigDecimal) this.getNumberBySQL(
                "select nvl(max(seq),0) from cd_data_dict d where " + (parent != null ? "d.parent_uuid=:puuid" : "d.parent_uuid is null"),
                param)).intValue();
    }


    /**
     * @param dataDictionary
     * @return
     */
    @Override
    public DataDictionary getByCategory(String category) {
        List<DataDictionary> dataDictionaries = this.listByFieldEqValue("category", category);
        return CollectionUtils.isNotEmpty(dataDictionaries) ? dataDictionaries.get(0) : null;
    }

    /**
     * 如何描述该方法
     *
     * @param id
     * @return
     */
    @Override
    public DataDictionary getById(String id) {
        List<DataDictionary> dataDictionaries = this.listByFieldEqValue("id", id);
        return CollectionUtils.isNotEmpty(dataDictionaries) ? dataDictionaries.get(0) : null;
    }

    /**
     * 查询所有数据字典的根结点
     *
     * @return
     */
    @Override
    public List<DataDictionary> getTopLevel() {
        return listByHQL(QUERY_BY_TOP_LEVEL, null);
    }

    /**
     * @param type
     * @return
     */
    @Override
    public DataDictionary getByType(String type) {
        List<DataDictionary> dataDictionaries = this.listByFieldEqValue("type", type);
        return CollectionUtils.isNotEmpty(dataDictionaries) ? dataDictionaries.get(0) : null;
    }

    /**
     * @param type
     * @param code
     * @return
     */
    @Override
    public List<DataDictionary> getDataDictionaries(String type, String code) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("type", type);
        values.put("code", code);
        return this.listByHQL(QUERY_BY_TYPE_AND_CODE, values);
    }

    @Override
    public DataDictionary getByParentTypeAndCode(String parentType, String code) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("code", code);
        params.put("type", parentType);
        return this.getOneByHQL(
                "from DataDictionary t where t.parent.type = :type and t.code = :code", params);
    }

    @Override
    public List<DataDictionary> getDataDictionariesByParentTypeAndName(String parentType,
                                                                       String name,
                                                                       PagingInfo pagingInof) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("name", "%" + name + "%");
        params.put("type", parentType);
        String hql = "from DataDictionary t where t.parent.type = :type and t.name like :name order by t.code asc";
        return this.listByHQLAndPage(hql, params, pagingInof);
    }

}
