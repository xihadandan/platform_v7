package com.wellsoft.pt.message.websocket.service;

/**
 * @author yt
 * @title: MsgTypeEnum
 * @date 2020/5/27 11:50 上午
 */
public enum MsgTypeEnum {

    inboxOnLine("inboxOnLine"),//收件箱在线消息
    inboxOffLine("inboxOffLine"),//收件箱离线消息
    permissionTransfer("permissionTransfer"),//权限迁移
    countOffLine("countOffLine");//统计关联账户离线消息数量


    MsgTypeEnum(String str) {
    }
}
