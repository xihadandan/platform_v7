package com.wellsoft.pt.fulltext.store;

import com.google.common.collect.Lists;
import com.wellsoft.context.jdbc.support.QueryData;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.datastore.support.AbstractDataStoreQueryInterface;
import com.wellsoft.pt.fulltext.enums.QryDateRangeEnum;
import com.wellsoft.pt.fulltext.index.WellFlowDocumentIndex;
import com.wellsoft.pt.fulltext.request.IndexRequestParams;
import com.wellsoft.pt.fulltext.service.WellFlowDoucmentIndexService;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2020年09月08日   chenq	 Create
 * </pre>
 */
@Component
public class DocumentIndexQueryDataStoreQuery extends AbstractDataStoreQueryInterface {
    @Autowired
    WellFlowDoucmentIndexService wellFlowDoucmentIndexService;

    @Override
    public List<QueryItem> query(QueryContext context) {
        QueryData queryData = wellFlowDoucmentIndexService.query(convert2IndexRequestParams(context));
        List<QueryItem> queryItems = Lists.newArrayList();
        context.getPagingInfo().setPageSize(queryData.getPagingInfo().getPageSize());
        context.getPagingInfo().setCurrentPage(queryData.getPagingInfo().getCurrentPage());
        context.getPagingInfo().setTotalCount(queryData.getPagingInfo().getTotalCount());
        if (CollectionUtils.isNotEmpty(queryData.getDataList())) {
            for (Object document : queryData.getDataList()) {
                WellFlowDocumentIndex flowDocument = (WellFlowDocumentIndex) document;
                QueryItem item = new QueryItem();
//                item.put("url", flowDocument.getUrl());
//                item.put("title", flowDocument.getTitle());
//                item.put("content", flowDocument.getContent());
//                item.put("uuid", flowDocument.getUuid());
//                item.put("creator", flowDocument.getCreator());
//                item.put("modifier", flowDocument.getModifier());
//                item.put("createTime", flowDocument.getCreateTime());
//                item.put("modifyTime", flowDocument.getModifyTime());
                queryItems.add(item);
            }
        }
        return queryItems;
    }

    private IndexRequestParams convert2IndexRequestParams(QueryContext context) {
        IndexRequestParams params = new IndexRequestParams();
        params.setKeyword(context.getKeyword());
        try {
            if (context.getQueryParams().get("startTime") != null) {
                params.setStartTime(DateUtils.parseDate(context.getQueryParams().get("startTime").toString(), "yyyy-MM-dd"));
            }
            if (context.getQueryParams().get("endTime") != null) {
                params.setEndTime(DateUtils.parseDate(context.getQueryParams().get("endTime").toString() + " 23:59:59", "yyyy-MM-dd HH:mm:ss"));
            }
        } catch (Exception e) {
            throw new RuntimeException("时间格式异常");
        }
        if (context.getQueryParams().get("dataRangeType") != null) {
            try {
                params.setDateRange(QryDateRangeEnum.valueOf(context.getQueryParams().get("dataRangeType").toString()));
            } catch (Exception e) {
                throw new RuntimeException("时间范围类型异常");
            }
        }
        params.setPagingInfo(context.getPagingInfo());
        if (context.getQueryParams().get("fragmentSize") != null) {
            params.setFragmentSize((int) context.getQueryParams().get("fragmentSize"));
        }
        if (context.getQueryParams().get("order") != null) {
            Map<String, Object> order = (Map<String, Object>) context.getQueryParams().get("order");
            if (order.containsKey("direction") && order.containsKey("property")) {
                params.setOrder(new IndexRequestParams.Order(Sort.Direction.valueOf(order.get("direction").toString()), order.get("property").toString()));
            }
        }
        return params;
    }

    @Override
    public String getQueryName() {
        return "全文检索流程文档类-数据仓库";
    }

    @Override
    public CriteriaMetadata initCriteriaMetadata(QueryContext context) {
        CriteriaMetadata criteriaMetadata = CriteriaMetadata.createMetadata();
        criteriaMetadata.add("url", "url", "链接", String.class);
        criteriaMetadata.add("createTime", "createTime", "创建时间", String.class);
        criteriaMetadata.add("creator", "creator", "创建人", String.class);
        criteriaMetadata.add("modifier", "modifier", "修改人", String.class);
        criteriaMetadata.add("modifyTime", "modifyTime", "修改时间", Date.class);
        criteriaMetadata.add("content", "content", "内容", String.class);
        criteriaMetadata.add("title", "title", "标题", String.class);
        criteriaMetadata.add("uuid", "uuid", "UUID", String.class);
        return criteriaMetadata;
    }

    @Override
    public long count(QueryContext context) {
        if (context.getPagingInfo() != null && context.getPagingInfo().getTotalCount() != -1) {
            return context.getPagingInfo().getTotalCount();
        }
        return wellFlowDoucmentIndexService.count(convert2IndexRequestParams(context));
    }
}
