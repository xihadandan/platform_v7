package com.wellsoft.pt.message.web;

import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.message.entity.ShortMessage;
import com.wellsoft.pt.message.service.ShortMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Description:
 *
 * @author chenq
 * @date 2018/7/16
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/7/16    chenq		2018/7/16		Create
 * </pre>
 */
@Controller
@RequestMapping({"/message/shortMessage", "/api/message/shortMessage"})
public class ShortMessageController extends BaseController {

    @Autowired
    private ShortMessageService shortMessageService;

    @RequestMapping("/list")
    public String list() {
        return forward("/message/short_message");
    }

    /**
     * @param messageOutboxUuid
     * @return
     */
    @ResponseBody
    @GetMapping("/listByMessageOutboxUuid")
    public ApiResult<List<ShortMessage>> listByMessageOutboxUuid(@RequestParam("messageOutboxUuid") String messageOutboxUuid) {
        List<ShortMessage> shortMessages = shortMessageService.listByMessageOutboxUuid(messageOutboxUuid);
        return ApiResult.success(shortMessages);
    }

}
