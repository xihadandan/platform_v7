package com.wellsoft.pt.cg.core.generator.model;

import com.wellsoft.pt.bpm.engine.entity.FlowDefinition;
import com.wellsoft.pt.cg.core.Context;
import com.wellsoft.pt.cg.core.Type;
import com.wellsoft.pt.cg.core.source.FlowSource;
import freemarker.template.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * 根据流程生成流程监听器
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
public class FlowListenerByFlowModel extends AbstractModel {
    private static final int MODELCODE = Type.OUTPUTTYPE_FLOW_LISTENER;

    @Override
    public int getCode() {
        return MODELCODE;
    }

    @Override
    public void work(Context context) {
        FlowSource source = (FlowSource) context.getSource();
        Configuration cfg = initConfig(context);
        try {
            for (FlowDefinition flow : source.getFlows()) {
                Map<String, Object> other = new HashMap<String, Object>();
                String className = getClassName(flow.getId()) + "FlowListener";
                other.put("name", flow.getName());
                other.put("className", className);
                Map<String, Object> root = structureData(context, other);
                writeTemplate(context, root, cfg, "wf.flow.listener.ftl", className + ".java",
                        "/listener");

            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
