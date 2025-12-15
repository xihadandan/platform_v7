package com.wellsoft.pt.fulltext;

/**
 * es6 升级  es7
 *
 * @author baozh
 * <p>
 * import com.google.gson.Gson;
 * import com.google.gson.GsonBuilder;
 * import com.wellsoft.context.jdbc.support.PagingInfo;
 * import com.wellsoft.context.jdbc.support.QueryData;
 * import com.wellsoft.pt.fulltext.enums.QryDateRangeEnum;
 * import com.wellsoft.pt.fulltext.index.WellFlowDocumentIndex;
 * import com.wellsoft.pt.fulltext.request.IndexRequestParams;
 * import com.wellsoft.pt.fulltext.support.HighlightResultMapper;
 * import org.apache.commons.lang3.time.DateFormatUtils;
 * import org.apache.commons.lang3.time.DateUtils;
 * import org.elasticsearch.client.transport.TransportClient;
 * import org.elasticsearch.common.transport.InetSocketTransportAddress;
 * import org.elasticsearch.index.query.BoolQueryBuilder;
 * import org.elasticsearch.search.highlight.HighlightBuilder;
 * import org.elasticsearch.search.sort.FieldSortBuilder;
 * import org.elasticsearch.search.sort.SortOrder;
 * import org.junit.Before;
 * import org.junit.Test;
 * import org.springframework.data.domain.Page;
 * import org.springframework.data.domain.PageRequest;
 * import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
 * import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
 * import org.springframework.data.elasticsearch.core.query.SearchQuery;
 * <p>
 * import java.lang.reflect.Field;
 * import java.net.InetAddress;
 * import java.util.ArrayList;
 * import java.util.Date;
 * <p>
 * import static org.elasticsearch.index.query.QueryBuilders.*;
 */

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
public class QueryTest {

    /**
     * es6 升级 es7
     * @author baozh

    private String host = "localhost";
    private int port = 9300;
    private ElasticsearchTemplate template;
    private Gson gson = null;


     @Test public void test() {
     Class clazz = WellFlowDocumentIndex.class;
     Field[] fields = clazz.getFields();
     System.out.println();
     }

     @Before public void init() {
     try {
     template = new ElasticsearchTemplate(TransportClient
     .builder()
     .build()
     .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(host), port)));
     gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
     } catch (Exception e) {

     throw new RuntimeException(e);
     }
     }

     @Test public void testQuery() {
     IndexRequestParams params = new IndexRequestParams();
     params.setDateRange(QryDateRangeEnum.ONE_WEEK);
     params.setKeyword("外卖骑手");
     params.setPagingInfo(new PagingInfo(1, 100));
     QueryData data = query(params);
     System.out.println(gson.toJson(data));
     }


     public QueryData query(IndexRequestParams params) {
     SearchQuery searchQuery = searchQuery(params);
     QueryData queryData = new QueryData();
     if (searchQuery != null) {
     Page<WellFlowDocumentIndex> page = template.queryForPage(searchQuery, WellFlowDocumentIndex.class, new HighlightResultMapper());
     queryData.setDataList(page.getContent());
     queryData.setPagingInfo(new PagingInfo(page.getNumber(), params.getPagingInfo().getPageSize()));
     PagingInfo info = params.getPagingInfo();
     if (info != null) {
     info.setTotalCount(page.getTotalElements());
     }
     queryData.getPagingInfo().setTotalCount(page.getTotalElements());
     } else {
     queryData.setDataList(new ArrayList<>());
     PagingInfo info = params.getPagingInfo();
     if (info != null) {
     info.setTotalCount(0);
     }
     }
     return queryData;


     }

     private SearchQuery searchQuery(IndexRequestParams params) {
     BoolQueryBuilder boolQueryBuilder = boolQuery();
     filterTime(boolQueryBuilder, params);
     boolQueryBuilder.must(multiMatchQuery(params.getKeyword(), "title", "content"));
     NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
     queryBuilder.withQuery(boolQueryBuilder);
     queryBuilder.withIndices("well_flow_document");
     queryBuilder.withTypes("workflow");
     HighlightBuilder.Field titleField = new HighlightBuilder.Field("title");
     HighlightBuilder.Field contentField = new HighlightBuilder.Field("content");
     queryBuilder.withHighlightFields(titleField.fragmentSize(300).numOfFragments(1), contentField.fragmentSize(300).numOfFragments(1));
     order(queryBuilder, params);
     // ela 查询页数从0开始
     queryBuilder
     .withPageable(new PageRequest(params.getPagingInfo().getCurrentPage() - 1, params.getPagingInfo().getPageSize()));
     SearchQuery query = queryBuilder.build();

     return query;
     }

     private void order(NativeSearchQueryBuilder queryBuilder, IndexRequestParams params) {
     queryBuilder.withSort(new FieldSortBuilder("modifyTime").order(SortOrder.DESC));
     }

     private void filterTime(BoolQueryBuilder boolQueryBuilder, IndexRequestParams params) {
     if (params.getEndTime() != null || params.getStartTime() != null) {// 有自定义时间范围的
     if (params.getStartTime() != null) {
     boolQueryBuilder.filter(rangeQuery("modifyTime").gte(DateFormatUtils.format(params.getStartTime(), "yyyy-MM-dd HH:mm:ss")));
     }
     if (params.getEndTime() != null) {
     boolQueryBuilder.filter(rangeQuery("modifyTime").lte(DateFormatUtils.format(params.getEndTime(), "yyyy-MM-dd HH:mm:ss")));
     }
     return;
     }

     if (params.getDateRange() != null) { // 定义时间范围类型的
     Date startTime = null;
     Date endTime = new Date();
     switch (params.getDateRange()) {
     case ONE_DAY:
     startTime = DateUtils.addDays(endTime, -1);
     break;
     case ONE_WEEK:
     startTime = DateUtils.addWeeks(endTime, -1);
     break;
     case ONE_MONTH:
     startTime = DateUtils.addMonths(endTime, -1);
     break;
     case ONE_YEAR:
     startTime = DateUtils.addYears(endTime, -1);
     break;
     }
     boolQueryBuilder.filter(rangeQuery("modifyTime").gte(DateFormatUtils.format(startTime, "yyyy-MM-dd HH:mm:ss")).lte(DateFormatUtils.format(endTime, "yyyy-MM-dd HH:mm:ss")));

     }

     }*/
}
