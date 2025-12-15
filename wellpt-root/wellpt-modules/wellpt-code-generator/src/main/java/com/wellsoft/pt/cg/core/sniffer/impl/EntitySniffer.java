package com.wellsoft.pt.cg.core.sniffer.impl;

import com.wellsoft.pt.cg.core.Context;
import com.wellsoft.pt.cg.core.source.EntitySource;
import com.wellsoft.pt.cg.core.source.Source;
import org.apache.commons.lang.StringUtils;

/**
 * 实体嗅探器
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
public class EntitySniffer extends AbstractSniffer {
    public static final String PARAM_CLAZZ = "clazzs";// 上下文的数据字段
    public static final String DECOLLATOR = ";";// 分隔符号

    @Override
    public Source sniffer(Context context) {
        EntitySource source = new EntitySource();
        String tb = (String) context.getParam(PARAM_CLAZZ);
        String[] tbs = tb.split(DECOLLATOR);
        if (!StringUtils.isNotBlank(tb)) {
            throw new RuntimeException("Do not specify entity ");
        }
        for (String t : tbs) {
            source.addClass(t);
        }
        return source;
    }

    @Override
    public void impact(Context context, Source source) {
        context.setSource(source);
    }

}
