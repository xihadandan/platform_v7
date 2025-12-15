/*
 * @(#)5/10/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.parser;

import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.bpm.engine.element.SubTaskElement;
import com.wellsoft.pt.bpm.engine.element.TaskElement;
import com.wellsoft.pt.bpm.engine.enums.TaskNodeType;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.annotate.JsonDeserialize;

import java.io.IOException;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 5/10/24.1	zhulh		5/10/24		Create
 * </pre>
 * @date 5/10/24
 */
public class TaskElementDeserializer extends JsonDeserializer<TaskElement> {

    /**
     * @param jp
     * @param ctxt
     * @return
     * @throws IOException
     * @throws JsonProcessingException
     */
    @Override
    public TaskElement deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonNode jsonNode = jp.readValueAsTree();
        String taskElementJson = jsonNode.toString();
        // Map<String, Object> taskJsonMap = jp.getCodec().readValue(jp, Map.class);
        if (TaskNodeType.SubTask.getValue().equals(jsonNode.get("type").asText())) {
            return JsonUtils.json2Object(taskElementJson, SubTaskElement.class);
        }
        return JsonUtils.json2Object(taskElementJson, TaskElementProxy.class);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonDeserialize()
    static final class TaskElementProxy extends TaskElement {
        private static final long serialVersionUID = -1795715246266795874L;

    }

}
