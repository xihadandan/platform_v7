/*
 * @(#)2014-12-3 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.web;

import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.integration.service.impl.ZongXianExchangeServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author xujianjia
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017年5月16日.1	xujianjia		2017年5月16日		Create
 * </pre>
 * @date 2017年5月16日
 */
@Controller
@RequestMapping("/repeat/send")
public class RepeatSendControll extends BaseController {
    @Autowired
    private ZongXianExchangeServiceImpl zongXianExchangeServiceImpl;

    @RequestMapping("/sendData")
    @ResponseBody
    public Map<String, Object> sendData(@RequestParam(value = "dataId", required = true) String dataId) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        Boolean flag = zongXianExchangeServiceImpl.repeatSend(dataId);
        if (flag) {
            resultMap.put("state", "success");
            resultMap.put("msg", dataId + "：重复数据成功！");
        } else {
            resultMap.put("state", "error");
            resultMap.put("msg", dataId + "：重复数据失败！");
        }

        return resultMap;
    }
}
