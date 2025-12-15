package com.wellsoft.pt.cg.core.generator.impl;

import com.wellsoft.pt.cg.core.Context;
import com.wellsoft.pt.cg.core.Type;
import com.wellsoft.pt.cg.core.generator.Model;
import com.wellsoft.pt.cg.core.generator.model.DevJsByFlowModel;
import com.wellsoft.pt.cg.core.generator.model.DirectionListenerByFlowModel;
import com.wellsoft.pt.cg.core.generator.model.FlowListenerByFlowModel;
import com.wellsoft.pt.cg.core.generator.model.TaskListenerByFlowModel;

import java.util.LinkedList;
import java.util.List;

/**
 * 根据流程定义生成
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
public class FlowGenerator extends AbstractGenerator {
    public static final int MODEL_DIRECTION_LISTENER = Type.OUTPUTTYPE_DIRECTION_LISTENER;// 流向监听器
    public static final int MODEL_TASK_LISTENER = Type.OUTPUTTYPE_TASK_LISTENER;// 环节监听器
    public static final int MODEL_FLOW_LISTENER = Type.OUTPUTTYPE_FLOW_LISTENER;// 流程监听器
    public static final int MODEL_DEV_JS = Type.OUTPUTTYPE_DEV_JS;// 流程二开

    private List<Model> models = new LinkedList<Model>();
    private Context context = null;

    @Override
    public void setModel(int model) {
        models = new LinkedList<Model>();
        if ((model & MODEL_DIRECTION_LISTENER) == MODEL_DIRECTION_LISTENER) {
            models.add(new DirectionListenerByFlowModel());
        }
        if ((model & MODEL_TASK_LISTENER) == MODEL_TASK_LISTENER) {
            models.add(new TaskListenerByFlowModel());
        }
        if ((model & MODEL_FLOW_LISTENER) == MODEL_FLOW_LISTENER) {
            models.add(new FlowListenerByFlowModel());
        }
        if ((model & MODEL_DEV_JS) == MODEL_DEV_JS) {
            models.add(new DevJsByFlowModel());
        }

    }

    @Override
    public void init(Context context) {
        this.context = context;
        setModel(context.getModel());
    }

    @Override
    public void start() {
        for (Model model : models) {
            model.work(this.context);
        }
    }

}
