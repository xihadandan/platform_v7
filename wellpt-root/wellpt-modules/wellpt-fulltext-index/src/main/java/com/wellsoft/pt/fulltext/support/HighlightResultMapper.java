package com.wellsoft.pt.fulltext.support;

/**
 * 方法描述
 *
 * @author baozh
 * <p>
 * <p>
 * import org.apache.commons.lang.StringUtils;
 * import org.apache.commons.lang3.reflect.MethodUtils;
 * import org.elasticsearch.action.search.SearchResponse;
 * import org.elasticsearch.search.SearchHit;
 * import org.springframework.data.domain.Pageable;
 * import org.springframework.data.elasticsearch.core.DefaultResultMapper;
 * import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
 * import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
 * <p>
 * import java.util.ArrayList;
 * import java.util.List;
 * import java.util.Set;
 */

/**
 * Description: 高亮显示数据
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2020年09月08日   chenq	 Create
 * </pre>
 */
public class HighlightResultMapper {//es6 升级 es7 extends DefaultResultMapper {

    /**
     * es6 升级 es7
     * @author baozh
     @Override public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> clazz, Pageable pageable) {
     long totalHits = response.getHits().totalHits();
     List<T> results = new ArrayList<T>();
     for (SearchHit hit : response.getHits()) {
     if (hit != null) {
     T result = null;
     if (StringUtils.isNotBlank(hit.sourceAsString())) {
     result = mapEntity(hit.sourceAsString(), clazz);
     Set<String> fields = hit.getHighlightFields().keySet();
     for (String f : fields) {
     try {
     MethodUtils.invokeMethod(result, "set" + StringUtils.capitalize(f), hit.getHighlightFields().get(f).getFragments()[0].string());
     } catch (Exception e) {
     }

     }
     results.add(result);
     }
     }
     }

     return new AggregatedPageImpl<T>(results, pageable, totalHits, response.getAggregations());
     }**/
}
