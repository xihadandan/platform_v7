package com.wellsoft.pt.org.store;

import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.datastore.support.AbstractDataStoreQueryInterface;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import com.wellsoft.pt.org.service.OrgElementModelService;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang.StringUtils;
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
public class BizOrgRoleMemberTypeOptionDataStoreQuery extends AbstractDataStoreQueryInterface {

    @Resource
    OrgElementModelService orgElementModelService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.criteria.QueryInterface#getQueryName()
     */
    @Override
    public String getQueryName() {
        return "平台管理_组织服务_业务角色成员可选类型";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.criteria.QueryInterface#initCriteriaMetadata(QueryContext)
     */
    @Override
    public CriteriaMetadata initCriteriaMetadata(QueryContext queryContext) {
        CriteriaMetadata criteriaMetadata = CriteriaMetadata.createMetadata();
        criteriaMetadata.add("id", "id", "ID", String.class);
        criteriaMetadata.add("name", "name", "名称", String.class);

        return criteriaMetadata;
    }

    /**
     * (non-Javadoc)
     *
     * @see AbstractDataStoreQueryInterface#query(QueryContext)
     */
    @Override
    public List<QueryItem> query(QueryContext queryContext) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("system", RequestSystemContextPathResolver.system());
        params.put("tenant", SpringSecurityUtils.getCurrentTenantId());
        List<QueryItem> list = Lists.newArrayList();
        QueryItem item = new QueryItem();
        item.put("id", "user");
        item.put("name", "人员");
        list.add(item);
        list.addAll(orgElementModelService.listQueryItemByHQL("from OrgElementModelEntity where enable = true " +
                "" + (StringUtils.isNotBlank(RequestSystemContextPathResolver.system()) ? " and system=:system and tenant=:tenant " : " and system is null and tenant is null ") +
                " order by uuid asc", params, null));
        return list;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.criteria.QueryInterface#count(QueryContext)
     */
    @Override
    public long count(QueryContext queryContext) {
        return 0L;
    }


}
