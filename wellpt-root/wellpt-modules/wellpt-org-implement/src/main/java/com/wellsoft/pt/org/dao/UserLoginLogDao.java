package com.wellsoft.pt.org.dao;

import com.wellsoft.pt.org.entity.UserLoginLog;
import org.hibernate.Query;
import org.hibernate.Session;

import java.util.List;
import java.util.Map;
import java.util.Set;

//@Repository
public class UserLoginLogDao extends OrgHibernateDao<UserLoginLog, String> {
    public List findByHql(final String hql, final Map<String, ?> paramMap, final int first, final int pageSize) {
        Session s = this.getSession();
        final Query q = s.createQuery(hql);
        if (paramMap != null && paramMap.size() > 0) {
            Set<String> paramNames = paramMap.keySet();
            for (String paramName : paramNames) {
                q.setParameter(paramName, paramMap.get(paramName));
            }
        }
        q.setFirstResult(first);
        q.setMaxResults(pageSize);
        return q.list();
    }

    public long countHql(final String hql, final Map<String, ?> paramMap) {
        if (paramMap != null && paramMap.size() > 0) {
            return this.countHqlResult(hql, paramMap);
        }
        return this.countHqlResult(hql);
    }
}
