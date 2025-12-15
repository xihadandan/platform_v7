package com.wellsoft.pt.api.support;

import com.google.common.base.Stopwatch;

import java.util.HashMap;
import java.util.Map;

/**
 * Description: API执行上下文线程变量
 *
 * @author chenq
 * @date 2018/8/10
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/8/10    chenq		2018/8/10		Create
 * </pre>
 */
public class ApiContextHolder {

    public final static String COMMAND_KEY = "command";//指令的uuid变量

    public final static String TOKEN_CLAIMS = "claims";

    public final static String REQUEST_IDEMPOTENT_KEY = "IDEMPOTENT_KEY";


    private static ThreadLocal<Map<String, Object>> context = new ThreadLocal<>();


    public static ThreadLocal<Map<String, Object>> getContext() {
        if (context.get() == null) {
            context.set(new HashMap<String, Object>());
        }
        return context;
    }

    public static TokenClaims getTokenClaims() {
        return (TokenClaims) getContext().get().get(TOKEN_CLAIMS);
    }

    public static String getCommandUuid() {
        return (String) getContext().get().get(COMMAND_KEY);
    }

    public static String getRequestIdempotentKey() {
        return (String) getContext().get().get(REQUEST_IDEMPOTENT_KEY);
    }


    /**
     * 计时器
     *
     * @param timerCode 编号
     * @return
     */
    public static Stopwatch timer(String timerCode) {
        timerCode = timerCode == null ? "0" : timerCode;
        if (getContext().get().get("timer_" + timerCode) == null) {
            getContext().get().put("timer_" + timerCode, Stopwatch.createStarted());
        }
        return (Stopwatch) getContext().get().get("timer_" + timerCode);
    }


}
