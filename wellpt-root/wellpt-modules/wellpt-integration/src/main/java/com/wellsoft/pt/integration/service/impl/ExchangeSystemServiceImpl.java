/*
 * @(#)2018年4月17日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.service.impl;

import com.wellsoft.pt.integration.dao.ExchangeSystemDao;
import com.wellsoft.pt.integration.entity.ExchangeSystem;
import com.wellsoft.pt.integration.service.ExchangeSystemService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
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
 * 2018年4月17日.1	chenqiong		2018年4月17日		Create
 * </pre>
 * @date 2018年4月17日
 */
@Service
public class ExchangeSystemServiceImpl extends AbstractJpaServiceImpl<ExchangeSystem, ExchangeSystemDao, String>
        implements ExchangeSystemService {

    @Override
    public ExchangeSystem getExchangeSystemByUnitAndType(String unitId, String typeId) {
        String hql = "from ExchangeSystem e where e.unitId = :unitId and e.typeId like '%' || :typeId || '%'";
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("unitId", unitId);
        values.put("typeId", typeId);
        List<ExchangeSystem> eList = listByHQL(hql, values);
        if (eList.size() > 0) {
            return eList.get(0);
        } else {
            return null;
        }

    }

    @Override
    public ExchangeSystem getExchangeSystemByUnitAndType1(String unitId, String typeId) {
        String hql = "from ExchangeSystem e where e.unitId = :unitId and e.typeId1 like '%' || :typeId || '%'";
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("unitId", unitId);
        values.put("typeId", typeId);
        List<ExchangeSystem> eList = listByHQL(hql, values);
        if (eList.size() > 0) {
            return eList.get(0);
        } else {
            return null;
        }

    }

    @Override
    public List<ExchangeSystem> getExchangeSystemsByUnit(String unitId) {
        String hql = "from ExchangeSystem e where e.unitId = :unitId";
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("unitId", unitId);
        List<ExchangeSystem> eList = listByHQL(hql, values);
        return eList;
    }

    @Override
    public List<ExchangeSystem> getExSystemListByUnids(String unids) {
        String[] arr = unids.split(";");
        String ids = "";
        for (String s : arr) {
            ids += ",'" + s + "'";
        }
        String hql = "from ExchangeSystem e where e.unitId in(" + ids.replaceFirst(",", "") + ") ";
        return listByHQL(hql, null);
    }

    @Override
    public List<ExchangeSystem> getBeanByIds(String toSystem) {
        if (toSystem != null && !toSystem.equals("")) {
            String[] arr = toSystem.split(";");
            String ids = "";
            for (String s : arr) {
                ids += ",'" + s + "'";
            }
            String hql = "from ExchangeSystem e where e.id in(" + ids.replaceFirst(",", "") + ") ";
            return listByHQL(hql, null);
        } else {
            return null;
        }

    }

    @Override
    public List<ExchangeSystem> findByExample(ExchangeSystem example) {
        return this.dao.listByEntity(example);
    }

    @Override
    public ExchangeSystem getById(String systemId) {
        return this.dao.getOneByHQL("from ExchangeSystem where id='" + systemId + "'", null);
    }
}
