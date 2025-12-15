/*
 * @(#)2016年3月30日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.css;

import io.swagger.annotations.ApiModelProperty;
import org.springframework.core.Ordered;

import java.io.Serializable;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年3月30日.1	zhulh		2016年3月30日		Create
 * </pre>
 * @date 2016年3月30日
 */
public interface CssFile extends Ordered, Serializable {

    @ApiModelProperty("css文件Id")
    String getId();

    @ApiModelProperty("css文件名称")
    String getName();

    @ApiModelProperty("css文件路径")
    String getPath();

}
