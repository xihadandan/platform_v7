package com.wellsoft.pt.bot.support;

import java.util.HashMap;

/**
 * Description:
 *
 * @author chenq
 * @date 2018/9/18
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/9/18    chenq		2018/9/18		Create
 * </pre>
 */
public class BasicBotFieldValue extends AbstractBotFieldValue {


    public BasicBotFieldValue(Object fromFieldValue) {
        super(new HashMap<String, Object>(), fromFieldValue, null, null);
    }

    @Override
    public Object value() {
        return this.fromFieldValue;
    }
}
