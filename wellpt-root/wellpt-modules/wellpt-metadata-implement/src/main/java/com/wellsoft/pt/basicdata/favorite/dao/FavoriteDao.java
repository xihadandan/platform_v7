package com.wellsoft.pt.basicdata.favorite.dao;

import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.pt.basicdata.favorite.entity.FavoriteItem;
import com.wellsoft.pt.jpa.hibernate.HibernateDao;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014年7月18日.1	zhongzh		2014年7月18日		Create
 * </pre>
 * @date 2014年7月18日
 */
@Repository
public class FavoriteDao extends HibernateDao<FavoriteItem, String> {
    private static final String DELETE_BY_ENTITY_UUID = "delete from FavoriteItem favorite where favorite.entityUuid = :entityUuid";

    private static final String DELETE_FAVORITE = "delete from FavoriteItem favorite where favorite.entityUuid = :entityUuid and favorite.userId = :userId";

    private static final String COUNT_BY_ENTITY_UUID = "select count(entityUuid) from FavoriteItem favorite where favorite.entityUuid = :entityUuid and favorite.userId = :userId";

    private static final String FAVORITE_ENTITY_FILTER = "select count(1) from FavoriteItem oo where oo.entityUuid = o.uuid and oo.userId = :currentUserId";

    /**
     * 如何描述该方法
     *
     * @return
     */
    private static <ENTITY extends IdEntity> String buildOrderby(Map<String, String> orderby) {
        StringBuilder sb = new StringBuilder("");
        if (orderby != null && orderby.size() > 0) {
            sb.append(" order by ");
            for (String key : orderby.keySet()) {
                sb.append("o.").append(key).append(" ").append(orderby.get(key)).append(",");
            }
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    /**
     * @param entityUuid
     */
    public void deleteByEntityUuid(String entityUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("entityUuid", entityUuid);
        this.batchExecute(DELETE_BY_ENTITY_UUID, values);
    }

    public boolean isExist(String entityUuid, String userId) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("entityUuid", entityUuid);
        values.put("userId", userId);
        return (Long) this.findUnique(COUNT_BY_ENTITY_UUID, values) > 0;
    }

    /**
     * @param entityUuid
     * @param userId
     */
    public void delete(String entityUuid, String userId) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("entityUuid", entityUuid);
        values.put("userId", userId);
        batchExecute(DELETE_FAVORITE, values);
    }

    public <ITEM extends IdEntity> List<ITEM> query(Class<ITEM> entityClass, String whereHql,
                                                    Map<String, Object> values, Map<String, String> orderBys, PagingInfo pagingInfo) {
        String entityName = entityClass.getCanonicalName();
        whereHql = StringUtils.isBlank(whereHql) ? "1 = 1" : whereHql;
        String queryHql = "from " + entityName + " o where " + whereHql + " and exists (" + FAVORITE_ENTITY_FILTER
                + ") " + buildOrderby(orderBys);
        Query query = getSession().createQuery(queryHql);
        values.put("currentUserId", SpringSecurityUtils.getCurrentUserId());
        query.setProperties(values);
        // 分页信息
        if (pagingInfo != null && pagingInfo.getPageSize() != -1) {
            query.setFirstResult(pagingInfo.getFirst() - 1);
            query.setMaxResults(pagingInfo.getPageSize());
        }
        List<ITEM> queryItems = query.list();
        // 计算总数
        if (pagingInfo != null && pagingInfo.isAutoCount()) {
            query = getSession().createQuery(
                    "select count(*) from " + entityName + " o where " + whereHql + " and exists ("
                            + FAVORITE_ENTITY_FILTER + ")");
            query.setProperties(values);
            pagingInfo.setTotalCount((Long) query.uniqueResult());
        }
        return queryItems;
    }

}
