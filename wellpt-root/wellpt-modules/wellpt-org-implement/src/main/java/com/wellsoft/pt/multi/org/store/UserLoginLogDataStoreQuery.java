package com.wellsoft.pt.multi.org.store;

import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.datastore.support.AbstractDataStoreQueryInterface;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/12/3
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/12/3    chenq		2019/12/3		Create
 * </pre>
 */
@Component
public class UserLoginLogDataStoreQuery extends AbstractDataStoreQueryInterface {

    @Override
    public List<QueryItem> query(QueryContext queryContext) {
        List<QueryItem> queryItems = queryContext.getNativeDao().namedQuery("userLoginLogQuery",
                getQueryParams(queryContext), QueryItem.class, queryContext.getPagingInfo());
        return queryItems;
    }

    @Override
    public String getQueryName() {
        return "用户登录日志";
    }

    @Override
    public CriteriaMetadata initCriteriaMetadata(QueryContext context) {
        CriteriaMetadata metadata = CriteriaMetadata.createMetadata();
        metadata.add("uuid", "uuid", "UUID", String.class);
        metadata.add("loginName", "login_name", "登录名", String.class);
        metadata.add("userId", "user_id", "用户ID", String.class);
        metadata.add("userName", "user_name", "姓名", String.class);
        // metadata.add("department", "department", "部门", String.class);
        metadata.add("loginTime", "login_time", "登录时间", String.class);
        metadata.add("loginIp", "login_ip", "登录IP", String.class);
        metadata.add("loginSource", "login_source", "登录来源", String.class);
        metadata.add("userOs", "user_os", "客户端操作系统", String.class);
        metadata.add("browser", "browser", "客户端浏览器", String.class);
        metadata.add("browserVersion", "browser_version", "客户端浏览器版本", String.class);
        return metadata;
    }

    @Override
    public long count(QueryContext context) {
        return context.getPagingInfo().getTotalCount();
    }

    /**
     * @param queryContext
     * @return
     */
    private Map<String, Object> getQueryParams(QueryContext queryContext) {
        Map<String, Object> queryParams = queryContext.getQueryParams();
        queryParams.put("keyword", queryContext.getKeyword());
        queryParams.put("whereSql", queryContext.getWhereSqlString());
        queryParams.put("orderBy", queryContext.getOrderString());
        return queryParams;
    }
}
