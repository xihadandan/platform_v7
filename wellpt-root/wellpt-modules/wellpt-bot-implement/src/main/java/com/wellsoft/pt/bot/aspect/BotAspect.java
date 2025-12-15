package com.wellsoft.pt.bot.aspect;

import com.google.common.collect.Maps;
import com.wellsoft.context.util.groovy.GroovyUtils;
import com.wellsoft.pt.bot.support.BotResult;
import com.wellsoft.pt.bot.support.Boter;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Description:
 *
 * @author chenq
 * @date 2018/9/19
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/9/19    chenq		2018/9/19		Create
 * </pre>
 */
@Aspect
@Component
public class BotAspect {


    @Before(" execution(* com.wellsoft.pt.bot.support.BotFactory.startBot(..)) && args(boter)")
    public void beforeBoter(JoinPoint jp, Boter boter) {
        if (org.apache.commons.lang.StringUtils.isNotBlank(
                boter.getPrepareData().getBeforeBotScript())) {
            GroovyUtils.run(boter.getPrepareData().getBeforeBotScript(), null, null);
        }
    }


    @AfterReturning(pointcut = " execution(* com.wellsoft.pt.bot.support.BotFactory.startBot(..)) && args(boter)", returning = "result")
    public void afterBoter(JoinPoint jp, Boter boter, BotResult result) {
        if (org.apache.commons.lang.StringUtils.isNotBlank(
                boter.getPrepareData().getAfterBotScript())) {
            Map<String, Object> properties = Maps.newHashMap();
            properties.put("boter", boter);
            properties.put("result", result);
            GroovyUtils.run(boter.getPrepareData().getAfterBotScript(), properties, null);
        }
    }
}
