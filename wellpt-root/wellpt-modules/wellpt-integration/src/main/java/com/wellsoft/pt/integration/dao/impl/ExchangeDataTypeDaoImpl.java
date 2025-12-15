package com.wellsoft.pt.integration.dao.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.integration.dao.ExchangeDataTypeDao;
import com.wellsoft.pt.integration.entity.ExchangeDataType;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Description: ExchangeDataTypeDao
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
public class ExchangeDataTypeDaoImpl extends AbstractJpaDaoImpl<ExchangeDataType, String> implements
        ExchangeDataTypeDao {

    public List<ExchangeDataType> getListByIds(String arr) {
        if (arr != null && !arr.equals("")) {
            String hql = " from ExchangeDataType e where e.id in :ids ";
            Map<String, Object> params = Maps.newHashMap();
            params.put("ids", Arrays.asList(arr.split(";")));
            return listByHQL(hql, params);
        } else {
            return null;
        }

    }

}
