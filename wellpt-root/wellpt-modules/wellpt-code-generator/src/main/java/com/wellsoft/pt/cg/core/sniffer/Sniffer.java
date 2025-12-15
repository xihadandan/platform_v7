package com.wellsoft.pt.cg.core.sniffer;

import com.wellsoft.pt.cg.core.Context;
import com.wellsoft.pt.cg.core.source.Source;

/**
 * 嗅探器，获取源数据，作为生成器的输入
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
public interface Sniffer {
    /**
     * 嗅探并填充到上下文
     *
     * @param context
     */
    public void snifferAndImpact(Context context);

    /**
     * 嗅探
     *
     * @param context
     * @return
     */
    public Source sniffer(Context context);

    /**
     * 填充
     *
     * @param context
     * @param source
     */
    public void impact(Context context, Source source);
}
