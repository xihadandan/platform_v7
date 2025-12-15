package com.wellsoft.pt.org.store;

import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.datastore.support.AbstractDataStoreQueryInterface;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import com.wellsoft.pt.org.service.BizOrganizationService;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
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
public class BizOrganizationListDataStoreQuery extends AbstractDataStoreQueryInterface {

    @Resource
    BizOrganizationService bizOrganizationService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.criteria.QueryInterface#getQueryName()
     */
    @Override
    public String getQueryName() {
        return "平台管理_组织服务_业务组织管理";
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
        criteriaMetadata.add("remark", "remark", "备注", String.class);
        criteriaMetadata.add("expired", "expired", "是否已失效", Boolean.class);
        criteriaMetadata.add("neverExpire", "neverExpire", "永不过期", Boolean.class);
        criteriaMetadata.add("expireTime", "expireTime", "过期时间", String.class);
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
        List<QueryItem> list = bizOrganizationService.listQueryItemByNameHQLQuery("bizOrgListKeywordQuery", getQueryParams(queryContext), queryContext.getPagingInfo());
        for (QueryItem qi : list) {
            if (qi.getDate("expireTime") != null && !qi.getBoolean("neverExpire")) {
                qi.put("expired", qi.getDate("expireTime").compareTo(new Date()) < 0);
            }
        }
        return list;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.criteria.QueryInterface#count(QueryContext)
     */
    @Override
    public long count(QueryContext queryContext) {
        return bizOrganizationService.countByNamedHQLQuery("bizOrgListKeywordQuery", getQueryParams(queryContext));
    }

    /**
     * @param queryContext
     * @return
     */
    private Map<String, Object> getQueryParams(QueryContext queryContext) {
        Map<String, Object> params = Maps.newHashMap();
        params.putAll(queryContext.getQueryParams());
        params.put("keyword", queryContext.getKeyword());
        params.put("whereSql", queryContext.getWhereSqlString());
        params.put("orderBy", queryContext.getOrderString());
        if (StringUtils.isNotBlank(RequestSystemContextPathResolver.system())) {
            params.put("system", RequestSystemContextPathResolver.system());
            params.put("tenant", SpringSecurityUtils.getCurrentTenantId());
        } else {
            params.put("systemIsNull", true);
        }
        return params;
    }

}
