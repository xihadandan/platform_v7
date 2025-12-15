package com.wellsoft.pt.app.feishu.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * 飞书审批配置json对象
 */
@ApiModel("飞书审批配置json对象")
public class FeishuApprovalConfVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 待办消息推送开关 默认关闭
     */
    @ApiModelProperty("待办消息推送开关(默认关闭)")
    private boolean toDoMsgPush;

    public static FeishuApprovalConfVo getDefaultApprovalConfVo() {
        FeishuApprovalConfVo confVo = new FeishuApprovalConfVo();
        confVo.setToDoMsgPush(false);
        return confVo;
    }

    public boolean getToDoMsgPush() {
        return toDoMsgPush;
    }

    public void setToDoMsgPush(boolean toDoMsgPush) {
        this.toDoMsgPush = toDoMsgPush;
    }
}
