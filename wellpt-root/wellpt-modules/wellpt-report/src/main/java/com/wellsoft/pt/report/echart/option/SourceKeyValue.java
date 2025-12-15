package com.wellsoft.pt.report.echart.option;

import java.util.LinkedHashMap;

/**
 * Description: 数据集原始数据key-value形式
 *
 * @author chenq
 * @date 2019/5/14
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/5/14    chenq		2019/5/14		Create
 * </pre>
 */
public class SourceKeyValue<String, V> extends LinkedHashMap implements SourceElement {


    public SourceKeyValue add(String key, V v) {
        this.put(key, v);
        return this;
    }

}
