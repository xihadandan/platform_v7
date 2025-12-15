package com.wellsoft.pt.workflow.manager.store;

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
public class AppWorkflowManagerDataStoreQuery extends AbstractDataStoreQueryInterface {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.criteria.QueryInterface#getQueryName()
     */
    @Override
    public String getQueryName() {
        return "平台管理_产品集成_流程定义";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.criteria.QueryInterface#initCriteriaMetadata(QueryContext)
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
        criteriaMetadata.add("name", "t1.name", "流程定义名称", String.class);
        criteriaMetadata.add("id", "t1.id", "流程定义ID", String.class);
        criteriaMetadata.add("code", "t1.code", "流程定义编号", String.class);
        criteriaMetadata.add("version", "t1.version", "版本", String.class);
        criteriaMetadata.add("category", "t1.category", "流程分类编码", String.class);
        criteriaMetadata.add("categoryName", "category_name", "流程分类名称", String.class);
        criteriaMetadata.add("formName", "t1.form_name", "表单名称", String.class);
        criteriaMetadata.add("formUuid", "t1.form_uuid", "表单UUID", String.class);
        criteriaMetadata.add("moduleId", "t1.module_id", "模块ID", String.class);
        criteriaMetadata.add("freeed", "t1.freeed", "是否自由流程", Boolean.class);
        criteriaMetadata.add("enabled", "t1.enabled", "启用", Boolean.class);
        criteriaMetadata.add("flowSchemaUuid", "t1.flow_schema_uuid", "定义内容UUID", String.class);
        criteriaMetadata.add("isRef", "is_ref", "是否引用流程", Boolean.class);
        criteriaMetadata.add("systemUnitId", "t1.system_unit_id", "归属系统单位ID", String.class);
        criteriaMetadata.add("remark", "t1.remark", "备注", String.class);
        criteriaMetadata.add("delete_time", "t1.delete_time", "删除时间", Date.class);
        criteriaMetadata.add("delete_status", "t1.delete_status", "删除状态", Integer.class);
        criteriaMetadata.add("maxVersionDefUuid", "t7.max_version_def_uuid", "最新版本的流程定义UUID", String.class);
        return criteriaMetadata;
    }

    /**
     * (non-Javadoc)
     *
     * @see AbstractDataStoreQueryInterface#query(QueryContext)
     */
    @Override
    public List<QueryItem> query(QueryContext queryContext) {
        List<QueryItem> queryItems = queryContext.getNativeDao().namedQuery("appModuleWorkflowDefManagerQuery",
                getQueryParams(queryContext), QueryItem.class, queryContext.getPagingInfo());
        return queryItems;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.criteria.QueryInterface#count(QueryContext)
     */
    @Override
    public long count(QueryContext queryContext) {
        Long total = queryContext.getPagingInfo().getTotalCount();
        return total != -1 ? total : queryContext.getNativeDao().countByNamedQuery("appModuleWorkflowDefManagerQuery",
                getQueryParams(queryContext));
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
        queryParams.put("categoryUuid", queryContext.getQueryParams().get("categoryUuid"));
        String currentUserUnitId = SpringSecurityUtils.getCurrentUserUnitId();
        if (StringUtils.isBlank((String) queryParams.get("moduleId"))
                && !MultiOrgSystemUnit.PT_ID.equalsIgnoreCase(currentUserUnitId)) {
            queryParams.put("systemUnitIds", Lists.<String>newArrayList(MultiOrgSystemUnit.PT_ID, currentUserUnitId));
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
