package com.wellsoft.pt.bpm.engine.dispatcher;

import com.wellsoft.pt.bpm.engine.context.ExecutionContext;
import com.wellsoft.pt.bpm.engine.entity.FlowInstance;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.bpm.engine.node.SubTaskNode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DemoCustomDispatcherFlowResolver extends DefaultDispatcherFlowResolver implements
        CustomDispatcherFlowResolver {
    public String getName() {
        return "自定义子流程分发示例";
    }

    public void create(ExecutionContext executionContext, FlowInstance flowInstance, TaskInstance parentTask,
                       SubTaskNode subTaskNode, int subFlowIndex) {
    }
}
