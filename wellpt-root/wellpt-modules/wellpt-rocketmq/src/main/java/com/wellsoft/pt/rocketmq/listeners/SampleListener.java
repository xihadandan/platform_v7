package com.wellsoft.pt.rocketmq.listeners;

import com.google.gson.Gson;
import com.wellsoft.pt.rocketmq.annotation.RocketMqListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2020年12月28日   chenq	 Create
 * </pre>
 */
@Component
public class SampleListener {


    @RocketMqListener(body = HashMap.class, tags = "Hello")
    public void hello(HashMap map) {
        System.err.println(new Gson().toJson(map));
    }

    @RocketMqListener(body = HashMap.class, tags = "Hello2")
    public void hello2(HashMap map) {
        System.err.println(new Gson().toJson(map));
    }
}
