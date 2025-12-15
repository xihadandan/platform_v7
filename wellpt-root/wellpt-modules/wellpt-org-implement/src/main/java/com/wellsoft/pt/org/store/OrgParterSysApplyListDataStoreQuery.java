package com.wellsoft.pt.org.store;

import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.datastore.support.AbstractDataStoreQueryInterface;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import com.wellsoft.pt.org.service.OrgPartnerSysApplyService;
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
public class OrgParterSysApplyListDataStoreQuery extends AbstractDataStoreQueryInterface {

    @Resource
    OrgPartnerSysApplyService orgPartnerSysApplyService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.criteria.QueryInterface#getQueryName()
     */
    @Override
    public String getQueryName() {
        return "平台管理_组织服务_协作系统申请";
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
        criteriaMetadata.add("reqName", "reqName", "请求方名称", String.class);
        criteriaMetadata.add("resName", "resName", "协作方名称", String.class);
        criteriaMetadata.add("code", "code", "编码", String.class);
        criteriaMetadata.add("state", "state", "状态", Integer.class);
        criteriaMetadata.add("applyReason", "applyReason", "申请原因", String.class);
        criteriaMetadata.add("applyTime", "applyTime", "申请时间", String.class);
        criteriaMetadata.add("categoryName", "categoryName", "分类", String.class);
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
        List<QueryItem> list = orgPartnerSysApplyService.listQueryItemByNameSQLQuery("orgPartnerSysApplyListKeywordQuery", getQueryParams(queryContext), queryContext.getPagingInfo());
        for (QueryItem item : list) {
            item.put("uuid", item.getLong("uuid").toString());
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
        return orgPartnerSysApplyService.countByNamedSQLQuery("orgPartnerSysApplyListKeywordQuery", getQueryParams(queryContext));
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
        return params;
    }

}
