package com.wellsoft.pt.workflow.store;

import com.google.common.base.CaseFormat;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.datastore.support.AbstractDataStoreQueryInterface;
import com.wellsoft.pt.jpa.criteria.Criteria;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import com.wellsoft.pt.jpa.dao.NativeDao;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.workflow.dto.WfOpinionRuleItemDto;
import com.wellsoft.pt.workflow.facade.service.WfOpinionRuleItemFacadeService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Description: 流程管理_意见校验规则_数据仓库
 *
 * @author zenghw
 * @version 1.0
 * <p>
 * <pre>
 *          修改记录:
 *          修改后版本	        修改人		修改日期			修改内容
 *          2020/3/24.1	    zenghw		2021/5/13		    Create
 *          </pre>
 * @date 2020/3/24
 */
@Component
public class WorkFlowOpinionRuleListDataStore extends AbstractDataStoreQueryInterface {
    @Autowired
    private NativeDao nativeDao;
    private String nameQuery = "queryOpinionRuleList";
    @Autowired
    private WfOpinionRuleItemFacadeService wfOpinionRuleItemFacadeService;

    @Override
    public CriteriaMetadata initCriteriaMetadata(QueryContext queryContext) {
        CriteriaMetadata criteriaMetadata = CriteriaMetadata.createMetadata();

        Criteria criteria = this.nativeDao.createTableCriteria("WF_OPINION_RULE");
        CriteriaMetadata metadata = criteria.getCriteriaMetadata();
        for (int index = 0; index < metadata.length(); index++) {
            String columnIndex = metadata.getColumnIndex(index);
            // 使用驼峰风格列索引
            String camelColumnIndex = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, columnIndex);
            criteriaMetadata.add(camelColumnIndex, metadata.getMapColumnIndex(columnIndex), metadata.getComment(index),
                    metadata.getDataType(index), metadata.getColumnType(index));
        }
        // criteriaMetadata.add("opinionRuleItem", "opinionRuleItem", "检验项",
        // String.class);
        return criteriaMetadata;
    }

    @Override
    public List<QueryItem> query(QueryContext queryContext) {
        Map<String, Object> params = convertQueryParams(queryContext);
        List<QueryItem> items = queryContext.getNativeDao().namedQuery(nameQuery, params, QueryItem.class,
                queryContext.getPagingInfo());
        return items;
    }

    @Override
    public String getQueryName() {
        return "流程管理_意见校验规则_数据仓库";
    }

    @Override
    public long count(QueryContext queryContext) {
        Map<String, Object> params = convertQueryParams(queryContext);
        return this.nativeDao.countByNamedQuery(this.nameQuery, params);
    }

    /**
     * 将queryContext转化查询查询参数
     *
     * @param queryContext
     * @return java.util.HashMap<java.lang.String, java.lang.Object>
     **/
    private Map<String, Object> convertQueryParams(QueryContext queryContext) {
        Map<String, Object> params = queryContext.getQueryParams();

        String wheresql = queryContext.getWhereSqlString();
        String orderStr = queryContext.getOrderString();
        params.put("orderStr", orderStr);
        params.put("whereSql", wheresql);
        if (StringUtils.isNotBlank(RequestSystemContextPathResolver.system())) {
            params.put("systemIds", Lists.newArrayList(RequestSystemContextPathResolver.system()));
        }
        params.put("tenantId", SpringSecurityUtils.getCurrentTenantId());
        return params;
    }

    /**
     * key:OpinionRuleuuid
     *
     * @param wfOpinionRuleItemDtos
     * @return java.util.Map<java.lang.String, java.util.List < com.wellsoft.pt.workflow.dto.WfOpinionRuleItemDto>>
     **/
    private Map<String, List<WfOpinionRuleItemDto>> getWfOpinionRuleItemDtos(
            List<WfOpinionRuleItemDto> wfOpinionRuleItemDtos) {
        Map<String, List<WfOpinionRuleItemDto>> map = Maps.newHashMap();
        for (WfOpinionRuleItemDto dto : wfOpinionRuleItemDtos) {
            List<WfOpinionRuleItemDto> dtos = map.get(dto.getOpinionRuleUuid());
            if (dtos == null) {
                dtos = new ArrayList<>();
            }
            dtos.add(dto);
            map.put(dto.getOpinionRuleUuid(), dtos);
        }
        return map;
    }

}
