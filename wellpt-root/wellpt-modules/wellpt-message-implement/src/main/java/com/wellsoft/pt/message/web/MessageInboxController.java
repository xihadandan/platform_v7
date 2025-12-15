package com.wellsoft.pt.message.web;


import com.google.common.collect.Lists;
import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.ResultMessage;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.message.dto.MessageInboxDto;
import com.wellsoft.pt.message.entity.MessageInbox;
import com.wellsoft.pt.message.service.MessageInboxService;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgUserAccountFacadeService;
import com.wellsoft.pt.multi.org.facade.service.impl.MultiOrgUserAccountFacadeServiceImpl;
import com.wellsoft.pt.org.facade.service.OrgFacadeService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

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
@RestController
@RequestMapping({"/message/inbox", "/api/message/inbox"})
public class MessageInboxController {

    @Autowired
    private OrgFacadeService orgFacadeService;

    @Autowired
    MultiOrgUserAccountFacadeService multiOrgUserAccountFacadeService;
    @Resource
    private MessageInboxService messageInboxService;

    @ResponseBody
    @GetMapping(value = "/queryRecentTenLists")
    public ResultMessage queryRecentTenLists() {

//        messageInboxService.saveMessageInboxTest();

        List<MessageInboxDto> messageInboxDtoList = messageInboxService.queryRecentTenLists();
        ResultMessage resultMessage = new ResultMessage();
        resultMessage.setData(messageInboxDtoList);
        return resultMessage;
    }


    @ResponseBody
    @PutMapping(value = "/updateToReadState")
    public ResultMessage updateToReadState() {
        messageInboxService.updateToReadState();
        return new ResultMessage();
    }

    @ResponseBody
    @PutMapping(value = "/updateToReadStateByclass")
    public ResultMessage updateToReadState(@RequestParam("classifyUuid") String classifyUuid) {
        messageInboxService.updateToReadStateByclass(classifyUuid);
        return new ResultMessage();
    }


    @ResponseBody
    @PutMapping(value = "/updateToUnReadStateByclass")
    public ResultMessage updateToUnReadStateByclass(@RequestParam("classifyUuid") String classifyUuid) {
        messageInboxService.updateToUnReadStateByclass(classifyUuid);
        return new ResultMessage();
    }

    @ApiOperation(value = "获取离线消息", notes = "切换账户")
    @ApiImplicitParam(name = "onlineUserId", value = "在线用户ID", paramType = "query", dataType = "String", required = true)
    @GetMapping("/countOffLine/{onlineUserId}")
    public ApiResult<Map<String, Long>> countOffLine(@PathVariable String onlineUserId) {
        Map<String, Long> result = new HashMap<>();
        ConcurrentMap<String, Set<String>> userRelationAccountMap = MultiOrgUserAccountFacadeServiceImpl.userRelationAccountMap;
        if (userRelationAccountMap.containsKey(onlineUserId)) {
            Set<String> offLineUserIds = userRelationAccountMap.get(onlineUserId);
            for (String offLineUserId : offLineUserIds) {
                if (!onlineUserId.equals(offLineUserId)) {
                    Long count = messageInboxService.offLine(offLineUserId);
                    result.put(offLineUserId, count);
                }
            }
        }
        return ApiResult.success(result);
    }

    /**
     * @param messageOutboxUuid
     * @return
     */
    @GetMapping("/listByMessageOutboxUuid")
    public ApiResult<List<MessageInboxDto>> listByMessageOutboxUuid(@RequestParam("messageOutboxUuid") String messageOutboxUuid) {
        List<MessageInbox> inboxes = messageInboxService.listByMessageOutboxUuid(messageOutboxUuid);
        List<MessageInboxDto> dtoList = BeanUtils.copyCollection(inboxes, MessageInboxDto.class);
        Set<String> recipientSet = dtoList.stream().map(item -> item.getRecipient()).collect(Collectors.toSet());
        if (CollectionUtils.isNotEmpty(recipientSet)) {
            Map<String, String> recipientNameMap = orgFacadeService.getNameByOrgEleIds(Lists.newArrayList(recipientSet));
            dtoList.forEach(item -> {
                if (recipientNameMap.containsKey(item.getRecipient())) {
                    item.setRecipientName(recipientNameMap.get(item.getRecipient()));
                }
            });
        }
        return ApiResult.success(dtoList);
    }
}
