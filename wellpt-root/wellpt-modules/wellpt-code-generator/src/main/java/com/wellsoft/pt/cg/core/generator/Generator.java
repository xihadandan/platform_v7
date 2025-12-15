package com.wellsoft.pt.cg.core.generator;

import com.wellsoft.pt.cg.core.Context;


/**
 * 生成器接口
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
public interface Generator {
    /**
     * 根据上下文本生成文件
     *
     * @param context
     */
    void generate(Context context);

    /**
     * 设置生成器生成模式
     *
     * @param model
     */
    void setModel(int model);

    /**
     * 初始化生成器
     *
     * @param context
     */
    void init(Context context);

    /**
     * 开启生成器
     */
    void start();
}
