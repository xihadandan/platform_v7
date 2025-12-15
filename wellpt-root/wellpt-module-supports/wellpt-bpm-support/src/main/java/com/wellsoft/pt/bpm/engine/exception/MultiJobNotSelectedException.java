package com.wellsoft.pt.bpm.engine.exception;

import com.wellsoft.context.enums.JsonDataErrorCode;

import java.util.Map;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2020年07月23日   chenq	 Create
 * </pre>
 */
public class MultiJobNotSelectedException extends WorkFlowException {

    private static final long serialVersionUID = 8489998261918676229L;

    private Map<String, Object> variables;


    public MultiJobNotSelectedException(Map<String, Object> variables) {
        this.variables = variables;
    }


    @Override
    public Object getData() {
        return variables;
    }


    @Override
    public JsonDataErrorCode getErrorCode() {
        return JsonDataErrorCode.MultiJobNotSelected;
    }
}
