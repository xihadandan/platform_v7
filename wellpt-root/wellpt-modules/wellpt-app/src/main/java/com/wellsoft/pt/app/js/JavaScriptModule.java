/*
 * @(#)2016年3月30日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.js;

import com.wellsoft.pt.app.css.CssFile;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.core.Ordered;

import java.io.Serializable;
import java.util.Set;

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
public interface JavaScriptModule extends Serializable, Ordered {

    @ApiModelProperty("js模块id")
    String getId();

    @ApiModelProperty("js模块名称")
    String getName();

    @ApiModelProperty("JS文件路径")
    String getPath();

    @ApiModelProperty("混淆")
    String getConfusePath();

    @ApiModelProperty("加载的CSS文件")
    Set<CssFile> getCssFiles();

    @ApiModelProperty("依赖的模块")
    Set<String> getDependencies();

    @ApiModelProperty("导出的名称")
    String getExports();

}
