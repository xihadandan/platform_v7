package com.wellsoft.pt.dyform.manager.store;

import com.google.common.collect.Lists;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.datastore.support.AbstractDataStoreQueryInterface;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import com.wellsoft.pt.multi.org.entity.MultiOrgSystemUnit;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

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
public class AppDyformManagerDataStoreQuery extends AbstractDataStoreQueryInterface {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.criteria.QueryInterface#getQueryName()
     */
    @Override
    public String getQueryName() {
        return "平台管理_产品集成_表单";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.criteria.QueryInterface#initCriteriaMetadata(com.wellsoft.pt.jpa.criteria.QueryContext)
     */
    @Override
    public CriteriaMetadata initCriteriaMetadata(QueryContext queryContext) {
        CriteriaMetadata criteriaMetadata = CriteriaMetadata.createMetadata();
        criteriaMetadata.add("uuid", "t1.uuid", "UUID", String.class);
        criteriaMetadata.add("createTime", "t1.create_time", "创建时间", String.class);
        criteriaMetadata.add("creator", "t1.creator", "创建人", String.class);
        criteriaMetadata.add("modifier", "t1.modifier", "修改人", String.class);
        criteriaMetadata.add("modifyTime", "t1.modify_time", "修改时间", Date.class);
        criteriaMetadata.add("recVer", "t1.rec_ver", "recVer", Integer.class);
        criteriaMetadata.add("name", "t1.name", "单据名称", String.class);
        criteriaMetadata.add("id", "t1.id", "表单ID", String.class);
        criteriaMetadata.add("code", "t1.code", "编号", String.class);
        criteriaMetadata.add("version", "t1.version", "版本", String.class);
        criteriaMetadata.add("tableName", "t1.table_name", "数据库表名", String.class);
        criteriaMetadata.add("moduleId", "t1.module_id", "模块ID", String.class);
        criteriaMetadata.add("categoryUuid", "t1.category_uuid", "表单分类UUID", Long.class);
        criteriaMetadata.add("categoryName", "t8.name", "表单分类名称", String.class);
        criteriaMetadata.add("isRef", "is_ref", "是否引用表单", Boolean.class);
        criteriaMetadata.add("customJsModule", "t1.custom_js_module", "加载自定义JavaScript模块", String.class);
        criteriaMetadata.add("formType", "t1.form_type", "单据类型", String.class);
        criteriaMetadata.add("systemUnitId", "t1.system_unit_id", "归属系统单位ID", String.class);
        criteriaMetadata.add("maxVersionFormUuid", "t7.max_version_form_uuid", "最新版本的表单UUID", String.class);
        return criteriaMetadata;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.support.AbstractDataStoreQueryInterface#query(com.wellsoft.pt.jpa.criteria.QueryContext)
     */
    @Override
    public List<QueryItem> query(QueryContext queryContext) {
        List<QueryItem> queryItems = queryContext.getNativeDao().namedQuery("appModuleDyformManagerQuery",
                getQueryParams(queryContext), QueryItem.class, queryContext.getPagingInfo());
        return queryItems;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.criteria.QueryInterface#count(com.wellsoft.pt.jpa.criteria.QueryContext)
     */
    @Override
    public long count(QueryContext queryContext) {
        return queryContext.getNativeDao().countByNamedQuery("appModuleDyformManagerQuery", getQueryParams(queryContext));
//        return queryContext.getPagingInfo().getTotalCount();
        /*return queryContext.getNativeDao()
                .countByNamedQuery("appDyformManagerQuery", getQueryParams(queryContext));*/
    }

    /**
     * @param queryContext
     * @return
     */
    private Map<String, Object> getQueryParams(QueryContext queryContext) {
        UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
        Map<String, Object> queryParams = queryContext.getQueryParams();
        queryParams.put("keyword", queryContext.getKeyword());
        queryParams.put("whereSql", queryContext.getWhereSqlString());
        queryParams.put("orderBy", queryContext.getOrderString());
        queryParams.put("moduleId", queryContext.getQueryParams().get("moduleId"));
        String currentUserUnitId = SpringSecurityUtils.getCurrentUserUnitId();
        if (StringUtils.isBlank((String) queryParams.get("moduleId")) && !MultiOrgSystemUnit.PT_ID.equalsIgnoreCase(currentUserUnitId)) {
            queryParams.put("systemUnitIds", Lists.<String>
                    newArrayList(MultiOrgSystemUnit.PT_ID, currentUserUnitId));
        }
//        UserSystemOrgDetails userSystemOrgDetails = userDetails.getUserSystemOrgDetails();
//        List<String> systemIds = userSystemOrgDetails.currentOrAllSystemId();
        if (StringUtils.isNotBlank(RequestSystemContextPathResolver.system())) {
            queryParams.put("systemIds", Lists.newArrayList(RequestSystemContextPathResolver.system()));
        }
        queryParams.put("tenantId", userDetails.getTenantId());
        return queryParams;
    }

}
