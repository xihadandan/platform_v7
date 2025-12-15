package com.wellsoft.pt.repository.dao;

import com.wellsoft.context.util.io.ClobUtils;
import com.wellsoft.pt.jpa.hibernate.HibernateDao;
import org.hibernate.SQLQuery;
import org.hibernate.transform.AliasedTupleSubsetResultTransformer;
import org.springframework.stereotype.Repository;

import java.sql.Clob;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 系统表结构数据层访问类
 *
 * @author zhouyq
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-3-21.1	zhouyq		2013-3-21		Create
 * </pre>
 * @date 2013-3-21
 */
@Repository
public class DbTableDao extends HibernateDao {

    /**
     * 系统表数据查询
     * (non-Javadoc)
     *
     * @throws ClassNotFoundException
     * @see com.wellsoft.pt.basicdata.systemtable.service.SystemTableService#query(java.lang.String, java.lang.String[], java.lang.String, java.lang.String[], java.lang.String, java.lang.String, java.lang.String, int, int)
     */
    public List<Map<String, Object>> query(String sql) throws Exception {
        return query(sql, null);
    }

    /**
     * 系统表数据查询
     * (non-Javadoc)
     *
     * @throws ClassNotFoundException
     * @see com.wellsoft.pt.basicdata.systemtable.service.SystemTableService#query(java.lang.String, java.lang.String[], java.lang.String, java.lang.String[], java.lang.String, java.lang.String, java.lang.String, int, int)
     */
    public List<Map<String, Object>> queryByParams(String sql, Map<String, Object> values) throws Exception {
        return query(sql, values, -1);
    }

    /**
     * 系统表数据查询
     * (non-Javadoc)
     *
     * @throws ClassNotFoundException
     * @see com.wellsoft.pt.basicdata.systemtable.service.SystemTableService#query(java.lang.String, java.lang.String[], java.lang.String, java.lang.String[], java.lang.String, java.lang.String, java.lang.String, int, int)
     */
    public List<Map<String, Object>> query(String sql, Integer maxResults) throws Exception {
        return query(sql, null, maxResults);
    }

    /**
     * 系统表数据查询
     * (non-Javadoc)
     *
     * @throws ClassNotFoundException
     * @see com.wellsoft.pt.basicdata.systemtable.service.SystemTableService#query(java.lang.String, java.lang.String[], java.lang.String, java.lang.String[], java.lang.String, java.lang.String, java.lang.String, int, int)
     */
    public List<Map<String, Object>> query(String sql, Map<String, Object> values, Integer maxResults) throws Exception {
        SQLQuery query = getSession().createSQLQuery(sql);
        if (values != null) {
            query.setProperties(values);
        }
        if (maxResults != null && maxResults > 0) {
            query.setMaxResults(maxResults);
        }
        // query.setResultSetMapping(arg0)

        AliasedTupleSubsetResultTransformer f = new AliasedTupleSubsetResultTransformer() {

            @Override
            public boolean isTransformedValueATupleElement(String[] arg0, int arg1) {

                return false;
            }

            @Override
            // 重写这个方法是关键
            public Object transformTuple(Object[] tuple/* 值数组 */, String[] aliases/* 字段数组 */) {
                Map<String, Object> map = new HashMap<String, Object>();
                for (int i = 0; i < aliases.length; i++) {
                    if (tuple[i] instanceof Clob) {
                        tuple[i] = ClobUtils.ClobToString((Clob) tuple[i]);
                    }
                    map.put(aliases[i].toLowerCase(), tuple[i]);

                }
                return map;
            }
        };
        List<Map<String, Object>> resultList = query.setResultTransformer(f).list();
        return resultList;
    }

}
