package com.wellsoft.pt.dms.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @Auther: yt
 * @Date: 2022/6/7 09:50
 * @Description:
 */
@ApiModel(value = "DmsFileVo", description = "归档数据更改创建人")
public class DmsFileVo {

    @ApiModelProperty(value = "uuid集合", required = true)
    private List<String> uuids;

    @ApiModelProperty(value = "用户ID", required = true)
    private String userId;

    public List<String> getUuids() {
        return uuids;
    }

    public void setUuids(List<String> uuids) {
        this.uuids = uuids;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
