package com.wellsoft.pt.cg.core.source;

import com.wellsoft.pt.bpm.engine.entity.FlowDefinition;

import java.util.LinkedList;
import java.util.List;

/**
 * 流程定义数据
 *
 * @author lmw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-7-7.1	lmw		2015-7-7		Create
 * </pre>
 * @date 2015-6-18
 */
public class FlowSource implements Source {
    private List<FlowDefinition> flows = new LinkedList<FlowDefinition>();

    public void add(FlowDefinition flowDefinition) {
        flows.add(flowDefinition);
    }

    public List<FlowDefinition> getFlows() {
        return flows;
    }
}
