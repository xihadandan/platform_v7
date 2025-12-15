package com.wellsoft.pt.security.core.userdetails;

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
 * 2025年04月15日   chenq	 Create
 * </pre>
 */
public class LoginNextAction implements Serializable {
    private String code;
    private Map<String, Object> actionParam = Maps.newHashMap();

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Map<String, Object> getActionParam() {
        return actionParam;
    }

    public void setActionParam(Map<String, Object> actionParam) {
        this.actionParam = actionParam;
    }


    public LoginNextAction() {
    }


    public LoginNextAction(String code) {
        this.code = code;
    }

    public LoginNextAction(String code, Map<String, Object> actionParam) {
        this.code = code;
        this.actionParam = actionParam;
    }
}
