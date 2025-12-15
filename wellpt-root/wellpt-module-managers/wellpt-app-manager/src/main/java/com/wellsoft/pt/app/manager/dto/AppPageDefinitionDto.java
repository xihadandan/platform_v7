/*
 * @(#)2019年6月6日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.manager.dto;

import com.wellsoft.pt.app.bean.AppPageDefinitionBean;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

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
 * 2019年6月6日.1	zhulh		2019年6月6日		Create
 * </pre>
 * @date 2019年6月6日
 */
@ApiModel("应用页面实体")
public class AppPageDefinitionDto extends AppPageDefinitionBean {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 7691246261822880150L;

    @ApiModelProperty("页面资源")
    private List<AppPageResourceDto> pageResources;

    /**
     * @return the pageResources
     */
    public List<AppPageResourceDto> getPageResources() {
        return pageResources;
    }

    /**
     * @param pageResources 要设置的pageResources
     */
    public void setPageResources(List<AppPageResourceDto> pageResources) {
        this.pageResources = pageResources;
    }

}
