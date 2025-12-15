/*
 * @(#)2019年6月12日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.common.web.json;

import org.apache.commons.collections.CollectionUtils;
import org.json.JSONArray;
import org.springframework.core.MethodParameter;
import org.springframework.web.method.HandlerMethod;

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
 * 2019年6月12日.1	zhulh		2019年6月12日		Create
 * </pre>
 * @date 2019年6月12日
 */
public class JsonDataMethod {

    private JsonData jsonData;

    private JsonDataParameter[] parameters;

    /**
     * @param jsonData
     */
    public JsonDataMethod(JsonData jsonData, JSONArray jsonArray) {
        super();
        this.jsonData = jsonData;
        parseParameters(jsonArray);
    }

    /**
     * @param jsonArray
     */
    private void parseParameters(JSONArray jsonArray) {
        try {
            parameters = new JsonDataParameter[jsonArray.length()];
            for (int index = 0; index < jsonArray.length(); index++) {
                parameters[index] = new JsonDataParameter(index, jsonArray.get(index), getArgType(index));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param index
     * @return
     */
    private String getArgType(int index) {
        List<String> argTypes = jsonData.getArgTypes();
        if (CollectionUtils.isNotEmpty(argTypes)) {
            return argTypes.get(index);
        }
        return null;
    }

    boolean matches(HandlerMethod handlerMethod) {
        return checkIsMatches(handlerMethod);
    }

    /**
     * @param method
     * @return
     */
    private boolean checkIsMatches(HandlerMethod handlerMethod) {
        MethodParameter[] methodParameters = handlerMethod.getMethodParameters();
        for (int index = 0; index < methodParameters.length; index++) {
            if (!parameters[index].isMatches(methodParameters[index])) {
                return false;
            }
        }
        return true;
    }

}
