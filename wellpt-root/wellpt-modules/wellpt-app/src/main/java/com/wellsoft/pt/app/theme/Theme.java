/*
 * @(#)2016年5月4日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.theme;

import com.wellsoft.pt.app.css.CssFile;
import com.wellsoft.pt.app.js.JavaScriptModule;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.core.Ordered;

import java.io.Serializable;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年5月4日.1	zhulh		2016年5月4日		Create
 * </pre>
 * @date 2016年5月4日
 */
@ApiModel("主题")
public interface Theme extends Ordered, Serializable {

    @ApiModelProperty("ID")
    String getId();

    @ApiModelProperty("名称")
    String getName();

    @ApiModelProperty("加载的CSS主题文件")
    List<CssFile> getCssFiles();

    @ApiModelProperty("加载的JS主题文件")
    List<JavaScriptModule> getJavaScriptModules();

}
