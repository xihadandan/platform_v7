/*
 * @(#)2015-1-9 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.web;

import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.bpm.engine.service.TaskFormOpinionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-1-9.1	zhulh		2015-1-9		Create
 * </pre>
 * @date 2015-1-9
 */
@Controller
@RequestMapping("/workflow/task/form/opinion/fix")
public class FlowTaskFormOpinionController extends BaseController {
    @Autowired
    private TaskFormOpinionService taskFormOpinionService;

    //	/**
    //	 * 跳到流程定义界面
    //	 *
    //	 * @return
    //	 */
    //	@RequestMapping(value = "")
    //	@ResponseBody
    //	public String flowDefine() {
    //		taskFormOpinionService.fixErrorData();
    //
    //		return "success";
    //	}

    /**
     * 跳到流程定义界面
     *
     * @return
     */
    @RequestMapping(value = "", produces = "text/plain")
    @ResponseBody
    public String flowDefine(@RequestParam(value = "flowInstUuid") String flowInstUuid) {
        return taskFormOpinionService.fixErrorData(flowInstUuid);
    }
}
