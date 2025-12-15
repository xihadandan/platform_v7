/*
 * @(#)2013-5-15 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.dao.impl;

import com.wellsoft.pt.bpm.engine.dao.TaskSubFlowDao;
import com.wellsoft.pt.bpm.engine.entity.TaskSubFlow;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import com.wellsoft.pt.jpa.hibernate4.NamedQueryScriptLoader;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

/**
 * Description: 任务子流程数据层访问类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-5-15.1	zhulh		2013-5-15		Create
 * </pre>
 * @date 2013-5-15
 */
@Repository
public class TaskSubFlowDaoImpl extends AbstractJpaDaoImpl<TaskSubFlow, String> implements TaskSubFlowDao {

    @Override
    public long countFlowShareDataBySQL(String queryName, Map<String, Object> values) {
        String sql = NamedQueryScriptLoader.generateDynamicNamedQueryString(getSession().getSessionFactory(), queryName, values);
        String countHql = prepareCountHql(sql);
        try {
            Object result = createSQLQuery(countHql, values).uniqueResult();
            if (result instanceof BigInteger) {
                return ((BigInteger) result).longValue();
            }
            return ((BigDecimal) result).longValue();
        } catch (Exception e) {
            throw new RuntimeException("sql can't be auto count, sql is:" + countHql, e);
        }
    }


    private String prepareCountHql(String orgHql) {
        String fromHql = orgHql;
        // select子句与order by子句会影响count查询,进行简单的排除.
        fromHql = "from " + StringUtils.substringAfter(fromHql, "from");
        fromHql = StringUtils.substringBeforeLast(fromHql, "order by");
        String countHql = "select count(1) c " + fromHql + "  ";
        return countHql;
    }
}
