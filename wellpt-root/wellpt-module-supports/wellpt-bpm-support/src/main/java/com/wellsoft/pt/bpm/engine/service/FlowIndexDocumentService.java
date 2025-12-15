package com.wellsoft.pt.bpm.engine.service;

import com.wellsoft.pt.bpm.engine.support.TaskData;
import com.wellsoft.pt.fulltext.index.WellFlowDocumentIndex;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2020年09月10日   chenq	 Create
 * </pre>
 */
public interface FlowIndexDocumentService {

    /**
     * 索引流程文档到全文检索库
     *
     * @param taskData
     */
    void indexWorkflowDocument(TaskData taskData);

    WellFlowDocumentIndex buildIndex(TaskData taskData);

    void buildAndSaveIndex(TaskData taskData);

    void deleteIndex(TaskData taskData);

    void deleteBySystem(String system);

    long countBySystem(String system);
}
