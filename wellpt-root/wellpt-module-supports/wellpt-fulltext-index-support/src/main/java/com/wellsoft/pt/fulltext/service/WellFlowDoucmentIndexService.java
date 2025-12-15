package com.wellsoft.pt.fulltext.service;

import com.wellsoft.context.jdbc.support.QueryData;
import com.wellsoft.pt.fulltext.index.WellFlowDocumentIndex;
import com.wellsoft.pt.fulltext.request.IndexRequestParams;

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
public interface WellFlowDoucmentIndexService extends DocumentIndexService<WellFlowDocumentIndex> {

    String index(WellFlowDocumentIndex index);

    QueryData query(IndexRequestParams params);
}
