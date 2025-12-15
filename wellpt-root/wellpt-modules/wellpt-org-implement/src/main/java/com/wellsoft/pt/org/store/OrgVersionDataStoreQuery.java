package com.wellsoft.pt.org.store;

import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.datastore.support.AbstractDataStoreQueryInterface;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import com.wellsoft.pt.org.service.OrgVersionService;
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
public class OrgVersionDataStoreQuery extends AbstractDataStoreQueryInterface {

    @Resource
    OrgVersionService orgVersionService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.criteria.QueryInterface#getQueryName()
     */
    @Override
    public String getQueryName() {
        return "平台管理_组织管理_版本列表";
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
        criteriaMetadata.add("orgUuid", "orgUuid", "组织UUID", Long.class);
        criteriaMetadata.add("id", "id", "ID", String.class);
        criteriaMetadata.add("state", "state", "状态", String.class);
        criteriaMetadata.add("approve", "approve", "审批状态", String.class);
        criteriaMetadata.add("ver", "ver", "历史版本号", Float.class);
        criteriaMetadata.add("code", "code", "编码", String.class);
        criteriaMetadata.add("sourceUuid", "sourceUuid", "源版本UUID", Long.class);
        criteriaMetadata.add("publishTime", "publishTime", "发布时间", String.class);
        criteriaMetadata.add("invalidTime", "invalidTime", "失效时间", String.class);
        criteriaMetadata.add("effectTime", "effectTime", "生效时间", String.class);
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
        return orgVersionService.listQueryItemByNameHQLQuery("orgVersionListQuery", getQueryParams(queryContext), queryContext.getPagingInfo());
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.criteria.QueryInterface#count(QueryContext)
     */
    @Override
    public long count(QueryContext queryContext) {
        return orgVersionService.countByNamedHQLQuery("orgVersionListQuery", getQueryParams(queryContext));
    }

    /**
     * @param queryContext
     * @return
     */
    private Map<String, Object> getQueryParams(QueryContext queryContext) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("keyword", queryContext.getKeyword());
        params.put("whereSql", queryContext.getWhereSqlString());
        params.put("orderBy", queryContext.getOrderString());
        params.put("orgUuid", Long.parseLong(queryContext.getQueryParams().get("orgUuid").toString()));
        return params;
    }

}
