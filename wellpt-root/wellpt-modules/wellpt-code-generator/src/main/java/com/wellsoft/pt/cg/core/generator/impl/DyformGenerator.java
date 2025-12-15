/*
 * @(#)2015年8月20日 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.cg.core.generator.impl;

import com.wellsoft.pt.cg.core.Context;
import com.wellsoft.pt.cg.core.Type;
import com.wellsoft.pt.cg.core.generator.Model;
import com.wellsoft.pt.cg.core.generator.model.DyformMaintianByDyformModel;

import java.util.LinkedList;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015年8月20日.1	zhulh		2015年8月20日		Create
 * </pre>
 * @date 2015年8月20日
 */
public class DyformGenerator extends AbstractGenerator {
    public static final int MODEL_DYFORM_MAINTAIN = Type.OUTPUTTYPE_DYFORM_MAINTAIN;// 表单维护

    private List<Model> models = new LinkedList<Model>();
    private Context context = null;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.cg.core.generator.Generator#setModel(int)
     */
    @Override
    public void setModel(int model) {
        models = new LinkedList<Model>();
        if ((model & MODEL_DYFORM_MAINTAIN) == MODEL_DYFORM_MAINTAIN) {
            models.add(new DyformMaintianByDyformModel());
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.cg.core.generator.Generator#init(com.wellsoft.pt.cg.core.Context)
     */
    @Override
    public void init(Context context) {
        this.context = context;
        setModel(context.getModel());
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.cg.core.generator.Generator#start()
     */
    @Override
    public void start() {
        for (Model model : models) {
            model.work(this.context);
        }
    }

}
