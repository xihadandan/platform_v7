package com.wellsoft.pt.org.store;

import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.datastore.support.AbstractDataStoreQueryInterface;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import com.wellsoft.pt.org.service.OrgGroupService;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/6/3
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/6/3    chenq		2019/6/3		Create
 * </pre>
 */
@Component
public class OrgGroupListDataStoreQuery extends AbstractDataStoreQueryInterface {

    @Resource
    OrgGroupService orgGroupService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.criteria.QueryInterface#getQueryName()
     */
    @Override
    public String getQueryName() {
        return "平台管理_组织服务_组织群组管理";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.criteria.QueryInterface#initCriteriaMetadata(QueryContext)
     */
    @Override
    public CriteriaMetadata initCriteriaMetadata(QueryContext queryContext) {
        CriteriaMetadata criteriaMetadata = CriteriaMetadata.createMetadata();
        criteriaMetadata.add("uuid", "uuid", "UUID", Long.class);
        criteriaMetadata.add("id", "id", "ID", String.class);
        criteriaMetadata.add("name", "name", "名称", String.class);
        criteriaMetadata.add("code", "code", "编码", String.class);
        criteriaMetadata.add("createTime", "createTime", "创建时间", String.class);
        criteriaMetadata.add("modifyTime", "modifyTime", "修改时间", String.class);
        return criteriaMetadata;
    }

    /**
     * (non-Javadoc)
     *
     * @see AbstractDataStoreQueryInterface#query(QueryContext)
     */
    @Override
    public List<QueryItem> query(QueryContext queryContext) {
        List<QueryItem> list = orgGroupService.listQueryItemByNameHQLQuery("orgGroupListKeywordQuery", getQueryParams(queryContext), queryContext.getPagingInfo());

        return list;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.criteria.QueryInterface#count(QueryContext)
     */
    @Override
    public long count(QueryContext queryContext) {
        return orgGroupService.countByNamedHQLQuery("orgGroupListKeywordQuery", getQueryParams(queryContext));
    }

    /**
     * @param queryContext
     * @return
     */
    private Map<String, Object> getQueryParams(QueryContext queryContext) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("tenant", SpringSecurityUtils.getCurrentTenantId());
        params.put("system", RequestSystemContextPathResolver.system());
        params.put("whereSql", queryContext.getWhereSqlString());
        params.put("orderBy", queryContext.getOrderString());
        params.putAll(queryContext.getQueryParams());
        return params;
    }

}
