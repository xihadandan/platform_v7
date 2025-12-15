package com.wellsoft.pt.app.feishu.vo;

import com.wellsoft.pt.app.feishu.enums.FeishuEventEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 飞书事件配置json对象
 */
@ApiModel("飞书事件配置json对象")
public class FeishuEventConfVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("事件")
    private FeishuEventEnum event;
    @ApiModelProperty("事件是否开启")
    private boolean enable;


    public static List<FeishuEventConfVo> getDefaultEventConfVos() {
        List<FeishuEventConfVo> eventConfVos = new ArrayList<>();
        for (FeishuEventEnum value : FeishuEventEnum.values()) {
            FeishuEventConfVo eventConfVo = new FeishuEventConfVo();
            eventConfVo.setEvent(value);
            eventConfVo.setEnable(true);
            eventConfVos.add(eventConfVo);
        }
        return eventConfVos;
    }

    public FeishuEventEnum getEvent() {
        return event;
    }

    public void setEvent(FeishuEventEnum event) {
        this.event = event;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}
