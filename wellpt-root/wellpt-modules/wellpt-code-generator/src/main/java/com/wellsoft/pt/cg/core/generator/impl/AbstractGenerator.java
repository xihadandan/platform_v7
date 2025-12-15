package com.wellsoft.pt.cg.core.generator.impl;


import com.wellsoft.pt.cg.core.Context;
import com.wellsoft.pt.cg.core.generator.Generator;


/**
 * 生成器虚类
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
public abstract class AbstractGenerator implements Generator {

    /**
     * 根据上下文本生成文件
     *
     * @param context
     */
    public void generate(Context context) {
        this.init(context);
        this.setModel(context.getModel());
        this.start();
    }
}
