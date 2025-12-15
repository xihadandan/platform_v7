/*
 * @(#)2015年9月17日 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.selective.web;

import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.basicdata.selective.facade.SelectiveDatas;
import com.wellsoft.pt.basicdata.selective.support.SelectiveData;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
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
 * 2015年9月17日.1	zhulh		2015年9月17日		Create
 * </pre>
 * @date 2015年9月17日
 */
@Controller
@RequestMapping("/basicdata/selective/data")
public class SelectiveDataController extends BaseController {

    /**
     * @param configKeys
     * @return
     */
    @RequestMapping(value = "/init")
    public @ResponseBody
    List<SelectiveData> init(@RequestBody List<String> configKeys) {
        List<SelectiveData> selectiveDatas = new ArrayList<SelectiveData>();
        for (String configKey : configKeys) {
            SelectiveData selectiveData = SelectiveDatas.get(configKey);
            if (selectiveData != null) {
                selectiveDatas.add(selectiveData);
            }
        }
        return selectiveDatas;
    }

    /**
     * @param configKey
     * @return
     */
    @RequestMapping(value = "/get")
    public @ResponseBody
    SelectiveData get(@RequestParam(value = "configKey") String configKey) {
        return SelectiveDatas.get(configKey);
    }

}
