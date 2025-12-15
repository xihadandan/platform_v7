package com.wellsoft.pt.security.acl.util;

import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.pt.security.acl.support.QueryInfo;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;

import java.util.Collection;
import java.util.Map;

/**
 * Description: 权限操作工具类
 *
 * @author liuxj
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本  修改人    修改日期      修改内容
 * V1.0   liuxj    2024/12/20    Create
 * </pre>
 * @date 2024/12/20
 */
public class AclUtil {
    /**
     * 构建where条件
     *
     * @return
     */
    public static <ENTITY extends IdEntity> String buildWhereHql(QueryInfo<ENTITY> queryInfo) {
        String whereHql = queryInfo.getWhereHql();
        return (StringUtils.isEmpty(whereHql) || StringUtils.equals(whereHql, "()")) ? "" : " and (" + whereHql + ")";
    }

    /**
     * 构建排序
     *
     * @return
     */
    public static <ENTITY extends IdEntity> String buildOrderby(QueryInfo<ENTITY> queryInfo) {
        Map<String, String> orderby = queryInfo.getOrderby();
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
     * 如何描述该方法
     *
     * @param query
     * @param params 查询参数
     */
    @SuppressWarnings("rawtypes")
    public static void setQueryParams(Query query, Map<String, Object> params) {
        for (String key : params.keySet()) {
            if (params.get(key).getClass().isArray()) {
                query.setParameterList(key, (Object[]) params.get(key));
            } else if (Collection.class.isAssignableFrom(params.get(key).getClass())) {
                query.setParameterList(key, (Collection) params.get(key));
            } else {
                query.setParameter(key, params.get(key));
            }
        }
    }
}
