package com.wellsoft.pt.message.dto;

import com.wellsoft.pt.message.entity.MessageClassify;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author yt
 * @title: MessageClassifyDto
 * @date 2020/5/19 11:29 上午
 */
@ApiModel("前端消息分类")
public class MessageClassifyDto extends MessageClassify {

    public static final String ALL_CLASSIFY = "all";
    public static final String ALL_CLASSIFY_NAME = "全部消息";

    public static final String USER_CLASSIFY = "user";
    public static final String USER_CLASSIFY_NAME = "用户消息";
    /**
     * 未读计数
     */
    @ApiModelProperty("未读计数")
    private long unReadCount;

    public MessageClassifyDto() {
    }

    public MessageClassifyDto(String uuid, String name) {

        this.setUuid(uuid);
        this.setName(name);
    }

    public long getUnReadCount() {
        return unReadCount;
    }

    public void setUnReadCount(long unReadCount) {
        this.unReadCount = unReadCount;
    }
}
