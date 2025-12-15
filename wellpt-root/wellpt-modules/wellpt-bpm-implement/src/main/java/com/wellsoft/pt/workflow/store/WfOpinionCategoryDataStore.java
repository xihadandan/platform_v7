package com.wellsoft.pt.workflow.store;

import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.datastore.support.AbstractDataStoreQueryInterface;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import com.wellsoft.pt.workflow.service.FlowOpinionCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author chenq
 * @date 2020/4/15
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2020/4/15    chenq		2020/4/15		Create
 * </pre>
 */
@Component
public class WfOpinionCategoryDataStore extends AbstractDataStoreQueryInterface {

    @Autowired
    FlowOpinionCategoryService flowOpinionCategoryService;

    @Override
    public List<QueryItem> query(QueryContext context) {
        Map<String, Object> params = getQueryParams(context);
        return flowOpinionCategoryService.listQueryItemByNameHQLQuery(
                "queryFlowOpinionCateByBusinessFlag", params,
                context.getPagingInfo());
    }

    @Override
    public String getQueryName() {
        return "工作流_常用行业意见分类";
    }

    @Override
    public CriteriaMetadata initCriteriaMetadata(QueryContext context) {
        CriteriaMetadata metadata = CriteriaMetadata.createMetadata();
        metadata.add("uuid", "uuid", "UUID", String.class);
        metadata.add("name", "name", "名称", String.class);
        metadata.add("code", "code", "编码", String.class);
        metadata.add("business_flag", "business_flag", "行业应用标记", String.class);
        metadata.add("create_time", "createTime", "创建时间", Date.class);
        metadata.add("modify_time", "modifyTime", "修改时间", Date.class);
        return metadata;
    }

    private Map<String, Object> getQueryParams(QueryContext context) {
        Object flags = context.getQueryParams().get("businessFlag");
        Map<String, Object> params = Maps.newHashMap();
        params.put("businessFlags", flags);
        params.put("keyword", context.getQueryParams().get("keyword"));
        params.put("orderBy", context.getOrderString());
        return params;
    }

    @Override
    public long count(QueryContext context) {
        return context.getPagingInfo().getTotalCount();
    }
}
