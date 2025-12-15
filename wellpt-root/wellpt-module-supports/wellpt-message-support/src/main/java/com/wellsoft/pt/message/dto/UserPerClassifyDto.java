package com.wellsoft.pt.message.dto;

import com.wellsoft.pt.message.entity.MessageClassify;

import java.io.Serializable;
import java.util.List;

/**
 * @author yt
 * @title: UserPerClassifyDto  消息通知设置
 * @date 2020/5/19 5:35 下午
 */
@SuppressWarnings("serial")
public class UserPerClassifyDto implements Serializable {

    //消息分类
    private MessageClassify classify;
    //消息格式 个性设置
    private List<UserPersonaliseDto> userPersonalises;

    public MessageClassify getClassify() {
        return classify;
    }

    public void setClassify(MessageClassify classify) {
        this.classify = classify;
    }

    public List<UserPersonaliseDto> getUserPersonalises() {
        return userPersonalises;
    }

    public void setUserPersonalises(List<UserPersonaliseDto> userPersonalises) {
        this.userPersonalises = userPersonalises;
    }
}
