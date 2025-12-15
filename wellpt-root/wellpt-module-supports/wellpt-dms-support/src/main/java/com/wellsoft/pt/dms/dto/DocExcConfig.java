package com.wellsoft.pt.dms.dto;

import com.wellsoft.context.jdbc.support.BaseQueryItem;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

/**
 * @Auther: yt
 * @Date: 2021/7/13 17:04
 * @Description:
 */
@ApiModel("公文交换排序")
public class DocExcConfig implements BaseQueryItem {

    @NotBlank(message = "uuid不能为空")
    @ApiModelProperty(value = "uuid", required = true)
    protected String uuid;

    @NotNull(message = "排序不能为空")
    @ApiModelProperty(value = "排序", required = true)
    private Integer sequence;

    // 业务名称
    @ApiModelProperty(value = "业务名称")
    private String name;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
