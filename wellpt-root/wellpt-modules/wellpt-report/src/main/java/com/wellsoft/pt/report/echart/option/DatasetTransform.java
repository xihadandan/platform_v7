package com.wellsoft.pt.report.echart.option;

import com.google.common.collect.Maps;

import java.io.Serializable;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2024年06月22日   chenq	 Create
 * </pre>
 */
public class DatasetTransform implements Serializable {

    private Type type = Type.filter;

    private Map<String, Object> config = Maps.newHashMap();

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Map<String, Object> getConfig() {
        return config;
    }

    public void setConfig(Map<String, Object> config) {
        this.config = config;
    }

    public DatasetTransform put(String key, Object value) {
        this.config.put(key, value);
        return this;
    }

    public static enum Type {
        filter, sort;
    }
}
