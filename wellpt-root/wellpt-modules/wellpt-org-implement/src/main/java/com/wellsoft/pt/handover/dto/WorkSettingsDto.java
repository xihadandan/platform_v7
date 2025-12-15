package com.wellsoft.pt.handover.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Description:
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	        修改人		修改日期			修改内容
 * 2022/3/28.1	    zenghw		2022/3/28		    Create
 * </pre>
 * @date 2022/3/28
 */
@ApiModel(value = "任务执行设置")
public class WorkSettingsDto {

    // 默认执行时间 格式 ：01:00
    @ApiModelProperty("默认执行时间")
    private String workTime;

    public String getWorkTime() {
        return this.workTime;
    }

    public void setWorkTime(final String workTime) {
        this.workTime = workTime;
    }
}
