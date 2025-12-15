package com.wellsoft.pt.bpm.engine.element;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Description: 校验规则设置对象
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	        修改人		修改日期			修改内容
 * 2021/5/17.1	    zenghw		2021/5/17		    Create
 * </pre>
 * @date 2021/5/17
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpinionCheckSetElement implements Serializable {

    // 场景ID
    private String id;
    // 对应的校验规则UUID
    private String opinionRuleUuid;
    // 应用环节
    private String taskIds;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOpinionRuleUuid() {
        return opinionRuleUuid;
    }

    public void setOpinionRuleUuid(String opinionRuleUuid) {
        this.opinionRuleUuid = opinionRuleUuid;
    }

    public String getTaskIds() {
        return taskIds;
    }

    public void setTaskIds(String taskIds) {
        this.taskIds = taskIds;
    }
}
