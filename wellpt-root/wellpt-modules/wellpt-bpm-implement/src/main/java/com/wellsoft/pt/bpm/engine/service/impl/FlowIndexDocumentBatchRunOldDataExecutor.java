package com.wellsoft.pt.bpm.engine.service.impl;

import com.google.common.collect.Lists;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.pt.bpm.engine.entity.FlowInstance;
import com.wellsoft.pt.bpm.engine.service.FlowIndexDocumentService;
import com.wellsoft.pt.bpm.engine.service.FlowInstanceService;
import com.wellsoft.pt.bpm.engine.support.TaskData;
import com.wellsoft.pt.fulltext.entity.IndexDocTemplateEntity;
import com.wellsoft.pt.fulltext.index.WellFlowDocumentIndex;
import com.wellsoft.pt.fulltext.service.WellFlowDoucmentIndexService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
@Service
public class FlowIndexDocumentBatchRunOldDataExecutor {
    @Autowired
    FlowInstanceService flowInstanceService;
    @Autowired
    WellFlowDoucmentIndexService wellFlowDoucmentIndexService;
    @Autowired
    FlowIndexDocumentService flowIndexDocumentService;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public void indexAllWorkflowDocument() {
        PagingInfo pagingInfo = new PagingInfo(0, 100, true);
        List<FlowInstance> flowInstanceList = flowInstanceService.listAllByOrderPage(pagingInfo, "createTime asc");
        int totalPage = (int) pagingInfo.getTotalPages();
        for (int curPage = 0; curPage < totalPage; curPage++) {
            if (curPage != 0) {
                pagingInfo = new PagingInfo(curPage, 100, true);
                flowInstanceList = flowInstanceService.listAllByOrderPage(pagingInfo, "createTime asc");
            }
            List<WellFlowDocumentIndex> indices = Lists.newArrayList();
            IndexDocTemplateEntity templateEntity = new IndexDocTemplateEntity();
            templateEntity.setUrl("/workflow/work/v53/view/work?taskInstUuid=${taskInstanceUuid}&flowInstUuid=${flowInstanceUuid}");
            templateEntity.setTitleExps("${流程标题}${流程名称}${发起人姓名}");
            templateEntity.setContentExps("${流程标题}${流程名称}${流程ID}${流程编号}${发起人姓名}${发起人所在部门名称}${表单数据结构}");
            templateEntity.setCreateTimeField("createTime");
            templateEntity.setModifyTimeField("modifyTime");
            templateEntity.setCreatorExps("${发起人姓名}");
            for (FlowInstance flowInstance : flowInstanceList) {
                try {
                    TaskData taskData = new TaskData();
                    taskData.setFlowInstUuid(flowInstance.getUuid());
                    taskData.setUserId(flowInstance.getStartUserId());
                    taskData.put("flowIndexDocumentTemplate", templateEntity);
                    WellFlowDocumentIndex wellIndex = flowIndexDocumentService.buildIndex(taskData);
                    if (wellIndex != null) {
                        indices.add(wellIndex);
                    }
                } catch (Exception e) {
                    logger.error("", e);
                }

            }
            wellFlowDoucmentIndexService.asyncBulkIndex(indices);
        }


    }
}
