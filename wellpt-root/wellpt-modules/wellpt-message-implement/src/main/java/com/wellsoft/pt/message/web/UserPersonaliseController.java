package com.wellsoft.pt.message.web;


import com.wellsoft.context.web.controller.ResultMessage;
import com.wellsoft.pt.message.dto.SaveUserPersonaliseDto;
import com.wellsoft.pt.message.dto.UserPerDto;
import com.wellsoft.pt.message.service.UserPersonaliseService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 消息分类contoller
 *
 * @author shenhb
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 20-10-22.1	shenhb		20-10-22		Create
 * </pre>
 * @date 20-10-22
 */
@Controller
@RequestMapping("/userPersonalise")
public class UserPersonaliseController {

    @Resource
    private UserPersonaliseService userPersonaliseService;

    @GetMapping(value = "/queryList")
    @ResponseBody
    public ResultMessage queryList() {
        UserPerDto userPerDto = userPersonaliseService.queryList();
        ResultMessage resultMessage = new ResultMessage();
        resultMessage.setData(userPerDto);
        return resultMessage;
    }


    @PutMapping(value = "/reset")
    @ResponseBody
    public ResultMessage reset() {
        userPersonaliseService.reset();
        return new ResultMessage();
    }

    @PostMapping(value = "/saveUserPersonalise")
    @ResponseBody
    public ResultMessage saveUserPersonalise(@RequestBody SaveUserPersonaliseDto saveUserPersonaliseDto) {
        userPersonaliseService.saveUserPersonalise(saveUserPersonaliseDto.getMainSwitch(), saveUserPersonaliseDto.getTemplateIds(), saveUserPersonaliseDto.getIsPopups());
        return new ResultMessage();
    }

}
