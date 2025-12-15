package com.wellsoft.pt.ei.service.impl;

import com.wellsoft.pt.ei.service.PermissionTransferService;
import com.wellsoft.pt.message.websocket.service.MsgHeaders;
import com.wellsoft.pt.message.websocket.service.MsgPayLoad;
import com.wellsoft.pt.message.websocket.service.MsgTypeEnum;
import com.wellsoft.pt.message.websocket.service.WebSocketService;
import com.wellsoft.pt.security.acl.service.AclTaskService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Description: 如何描述该类
 *
 * @author liuxj
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本  修改人    修改日期      修改内容
 * V1.0   liuxj    2025/1/2    Create
 * </pre>
 * @date 2025/1/2
 */
@Service
public class PermissionTransferServiceImpl implements PermissionTransferService {


    @Autowired
    WebSocketService webSocketService;

    @Autowired
    AclTaskService aclTaskService;

    /**
     * 发送进度消息
     */
    private void sendProgressMessage(String message) {
        String userId = SpringSecurityUtils.getCurrentUserId();
        MsgPayLoad payLoad = new MsgPayLoad(MsgTypeEnum.permissionTransfer, message);
        MsgHeaders msgHeaders = new MsgHeaders(new MsgPayLoad(MsgTypeEnum.permissionTransfer, "permissionTransfer"));
        webSocketService.sendToUser(userId, payLoad, msgHeaders);
    }

    @Override
    public void transferData() {
        aclTaskService.convertAclEntry(this::sendProgressMessage);
    }
}
