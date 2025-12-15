package com.wellsoft.pt.cg.core.sniffer.impl;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.bpm.engine.entity.FlowDefinition;
import com.wellsoft.pt.bpm.engine.service.FlowService;
import com.wellsoft.pt.cg.core.Context;
import com.wellsoft.pt.cg.core.source.FlowSource;
import com.wellsoft.pt.cg.core.source.Source;
import org.apache.commons.lang.StringUtils;

/**
 * 流程定义嗅探器
 *
 * @author lmw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-7-8.1	lmw		2015-7-8		Create
 * </pre>
 * @date 2015-7-8
 */
public class FlowSniffer extends AbstractSniffer {
    public static final String PARAM_FLOWS = "flows";// 上下文的数据字段
    public static final String DECOLLATOR = ";";// 分隔符号

    private FlowService flowService;

    @Override
    public Source sniffer(Context context) {
        if (flowService == null) {
            flowService = ApplicationContextHolder.getBean(FlowService.class);
        }
        FlowSource flowSource = new FlowSource();
        String flows = (String) context.getParam(PARAM_FLOWS);
        if (!StringUtils.isNotBlank(flows)) {
            throw new RuntimeException("Do not specify flow ");
        }
        String[] fs = flows.split(DECOLLATOR);
        for (String flow : fs) {
            FlowDefinition definition = flowService.getFlowDefinition(flow);
            flowSource.add(definition);
        }
        return flowSource;
    }

    @Override
    public void impact(Context context, Source source) {
        context.setSource(source);
    }

}
