package com.wellsoft.pt.integration.dao.impl;

import com.wellsoft.pt.integration.dao.ExchangeDataTransformDao;
import com.wellsoft.pt.integration.entity.ExchangeDataTransform;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: ExchangeDataTransformDao
 *
 * @author Administrator
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-11-17.1	wbx		2013-11-17		Create
 * </pre>
 * @date 2013-11-17
 */
@Repository
public class ExchangeDataTransformDaoImpl extends AbstractJpaDaoImpl<ExchangeDataTransform, String> implements
        ExchangeDataTransformDao {

    @Override
    public List<ExchangeDataTransform> getListBySourceTypeId(String typeid) {
        String hql = " from ExchangeDataTransform e where e.sourceId = :sourceId";
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("sourceId", typeid);
        return this.listByHQL(hql, values);
    }

    @Override
    public List<ExchangeDataTransform> getBeanByids(String transformIds) {
        if (transformIds != null && !"".equals(transformIds)) {
            String[] ids = transformIds.split(";");
            String temp = "";
            for (String id : ids) {
                temp += ",'" + id + "'";
            }
            String hql = "from ExchangeDataTransform e where e.id in(" + temp.replaceFirst(",", "") + ") ";
            return this.listByHQL(hql, null);
        } else {
            return null;
        }
    }
}