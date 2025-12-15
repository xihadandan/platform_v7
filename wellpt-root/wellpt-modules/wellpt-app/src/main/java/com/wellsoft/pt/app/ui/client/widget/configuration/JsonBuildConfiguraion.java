package com.wellsoft.pt.app.ui.client.widget.configuration;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.google.common.collect.Lists;
import com.wellsoft.pt.app.ui.WidgetConfiguration;
import org.apache.commons.collections.CollectionUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2020年04月29日   chenq	 Create
 * </pre>
 */
public class JsonBuildConfiguraion extends HashMap implements WidgetConfiguration {
    private static final long serialVersionUID = -1009920785406721012L;
    private static final String FUNCTIONELE_KEY = "_$functionEle";


    @Override
    public void afterPropertiesSet() throws Exception {

    }

    @Override
    public List<FunctionElement> getFunctionElements() {
        JSONObject configuration = (JSONObject) JSON.toJSON(this);
        //读取json配置中的功能元素节点
        List<JSONObject> functionElements = (List<JSONObject>) JSONPath.eval(configuration, "$.." + FUNCTIONELE_KEY);
        if (CollectionUtils.isEmpty(functionElements)) {
            return Collections.EMPTY_LIST;
        }
        List<FunctionElement> functionElementList = Lists.newArrayList();
        for (JSONObject jsonObject : functionElements) {
            FunctionElement functionElement = new FunctionElement();
            functionElement.setUuid(jsonObject.getString("uuid"));
            functionElement.setId(jsonObject.getString("id"));
            functionElement.setName(jsonObject.getString("name"));
            functionElement.setCode(functionElement.getId());
            functionElement.setRef(jsonObject.getBooleanValue("ref"));
            functionElementList.add(functionElement);
        }
        return functionElementList;
    }

    @Override
    public String getType() {
        return this.get("type") != null ? this.get("type").toString() : null;
    }
}
