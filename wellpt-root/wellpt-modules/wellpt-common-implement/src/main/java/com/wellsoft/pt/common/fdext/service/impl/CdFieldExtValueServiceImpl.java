/*
 * @(#)2016-03-11 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.common.fdext.service.impl;

import com.wellsoft.pt.common.fdext.bean.CdFieldExtensionValue;
import com.wellsoft.pt.common.fdext.entity.CdFieldExtValue;
import com.wellsoft.pt.common.fdext.service.CdFieldExtValueService;
import com.wellsoft.pt.common.fdext.support.BeanDataValue;
import com.wellsoft.pt.common.fdext.support.MapDataValue;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.SQLQuery;
import org.hibernate.transform.AliasedTupleSubsetResultTransformer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016-03-11.1	zhongzh		2016-03-11		Create
 * </pre>
 * @date 2016-03-11
 */
@Service
@Transactional
public class CdFieldExtValueServiceImpl extends BaseServiceImpl implements CdFieldExtValueService {

    public final static String VALUE_SQL_QUERY = "select t.* from CD_FIELD_EXT_VALUE t where t.group_code = :groupCode and t.data_uuid = :dataUuid order by t.create_time asc";
    public final static String VALUE_HQL_QUERY = "from CdFieldExtValue t where t.groupCode = :groupCode and t.dataUuid = :dataUuid order by t.createTime asc";
    private static Map<String, String> keyMap = new ConcurrentHashMap<String, String>();

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.fdext.service.CdFieldExtValueService#get(java.lang.String)
     */
    @Override
    public CdFieldExtValue get(String uuid) {
        return this.dao.get(CdFieldExtValue.class, uuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.fdext.service.CdFieldExtValueService#get(java.lang.String)
     */
    @Override
    public CdFieldExtensionValue getBean(String uuid) {
        CdFieldExtValue entity = this.get(uuid);
        CdFieldExtensionValue bean = new CdFieldExtensionValue();
        BeanUtils.copyProperties(entity, bean);
        return bean;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.fdext.service.CdFieldExtValueService#getBean(java.lang.String, java.lang.String)
     */
    @Override
    public CdFieldExtensionValue getBean(String dataUuid, String groupCode) {
        CdFieldExtValue example = new CdFieldExtValue();
        example.setDataUuid(dataUuid);
        example.setGroupCode(groupCode);
        CdFieldExtensionValue bean = new CdFieldExtensionValue();
        List<CdFieldExtValue> lists = this.dao.findByExample(example);
        if (lists != null && lists.size() > 0) {
            BeanUtils.copyProperties(lists.get(0), bean);
        }
        return bean;
    }

    @Override
    public BeanDataValue getBeanValue(String dataUuid, String groupCode) {
        CdFieldExtValue values = new CdFieldExtValue();
        values.setDataUuid(dataUuid);
        values.setGroupCode(groupCode);
        List<CdFieldExtValue> lists = this.dao.findByExample(values);
        if (lists != null && lists.size() > 0) {
            BeanUtils.copyProperties(lists.get(0), values);
        }
        return new BeanDataValue(values);
    }

    @Override
    public MapDataValue getMapValue(String dataUuid, String groupCode) {
        SQLQuery query = dao.getSession().createSQLQuery(VALUE_SQL_QUERY);
        AliasedTupleSubsetResultTransformer f = new AliasedTupleSubsetResultTransformer() {
            /** 如何描述serialVersionUID */
            private static final long serialVersionUID = 1L;

            private String dbNameToJavaName(String dbName, boolean firstCharUppered) {
                String name = dbName;
                if (name == null || name.trim().length() == 0) {
                    return "";
                }
                String[] parts = null;
                if (name.indexOf("_") != -1) {
                    parts = name.toLowerCase().split("_");
                } else {
                    parts = name.split("_");
                }
                StringBuilder sb = new StringBuilder();
                for (String part : parts) {
                    if (part.length() == 0) {
                        continue;
                    }
                    sb.append(part.substring(0, 1).toUpperCase());
                    sb.append(part.substring(1));
                }
                if (firstCharUppered) {
                    return sb.toString();
                } else {
                    return sb.substring(0, 1).toLowerCase() + sb.substring(1);
                }
            }

            @Override
            public boolean isTransformedValueATupleElement(String[] arg0, int arg1) {
                return false;
            }

            @Override
            public Object transformTuple(Object[] tuple/*值数组*/, String[] aliases/*字段数组*/) {
                Map<String, Object> map = new HashMap<String, Object>();
                for (int i = 0; i < aliases.length; i++) {
                    if (!keyMap.containsKey(aliases[i])) {
                        // 数据库的命名方式转化为Java属性名的命名方式
                        keyMap.put(aliases[i], dbNameToJavaName(aliases[i], false));
                    }
                    map.put(keyMap.get(aliases[i]), tuple[i]);
                }
                return map;
            }
        };
        Map<String, Object> values = new HashMap<String, Object>();
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> lists = query.setResultTransformer(f).list();
        query.setString("dataUuid", dataUuid);
        query.setString("groupCode", groupCode);
        if (lists != null && lists.size() > 0) {
            values = lists.get(0);
        }
        return new MapDataValue(values);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.fdext.service.CdFieldExtValueService#getAll()
     */
    @Override
    public List<CdFieldExtValue> getAll() {
        return this.dao.getAll(CdFieldExtValue.class);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.fdext.service.CdFieldExtValueService#findByExample(CdFieldExtValue)
     */
    @Override
    public List<CdFieldExtValue> findByExample(CdFieldExtValue example) {
        return this.dao.findByExample(example);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.fdext.service.CdFieldExtValueService#save(com.wellsoft.pt.common.fdext.entity.CdFieldExtValue)
     */
    @Override
    public void save(CdFieldExtValue entity) {
        this.dao.save(entity);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.fdext.service.CdFieldExtValueService#saveBean(com.wellsoft.pt.common.fdext.entity.CdFieldExtValue)
     */
    @Override
    public void saveBean(CdFieldExtensionValue bean) {
        String uuid = bean.getUuid();
        CdFieldExtValue entity = new CdFieldExtValue();
        if (StringUtils.isNotBlank(uuid)) {
            entity = this.get(uuid);
        }
        BeanUtils.copyProperties(bean, entity);
        this.save(entity);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.fdext.service.CdFieldExtValueService#saveAll(java.util.Collection)
     */
    @Override
    public void saveAll(Collection<CdFieldExtValue> entities) {
        this.dao.saveAll(entities);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.fdext.service.CdFieldExtValueService#remove(java.lang.String)
     */
    @Override
    public void remove(String uuid) {
        this.dao.deleteByPk(CdFieldExtValue.class, uuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.fdext.CdFieldExtValueService#removeAll(java.util.Collection)
     */
    @Override
    public void removeAll(Collection<CdFieldExtValue> entities) {
        this.dao.deleteAll(entities);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.fdext.service.CdFieldExtValueService#remove(CdFieldExtValue)
     */
    @Override
    public void remove(CdFieldExtValue entity) {
        this.dao.delete(entity);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.fdext.service.CdFieldExtValueService#removeAllByPk(java.util.Collection)
     */
    @Override
    public void removeAllByPk(Collection<String> uuids) {
        List<Serializable> list = new LinkedList<Serializable>();
        list.addAll(uuids);
        this.dao.deleteAllByPk(CdFieldExtValue.class, list);
    }

}
