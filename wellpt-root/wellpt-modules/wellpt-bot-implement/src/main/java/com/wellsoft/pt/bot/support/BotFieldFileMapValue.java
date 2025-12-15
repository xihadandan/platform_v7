package com.wellsoft.pt.bot.support;

import com.google.common.collect.Maps;
import org.apache.commons.compress.utils.Lists;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
public class BotFieldFileMapValue extends AbstractBotFieldValue {


    public BotFieldFileMapValue(
            Map<String, ?> data,
            Object fromFieldValue, Object toFieldValue, String script) {
        super(data, fromFieldValue, toFieldValue, script);
    }

    @Override
    public Object value() {
        // 返回文件列表
        List<Map<String, String>> filstList = Lists.newArrayList();
        if (fromFieldValue instanceof Collection) {
            Collection<String> collection = (Collection<String>) fromFieldValue;
            Iterator<String> iterable = collection.iterator();
            while (iterable.hasNext()) {
                Map<String, String> map = Maps.newHashMap();
                map.put("fileID", iterable.next());
                filstList.add(map);
            }
        }
        return filstList;
    }
}
