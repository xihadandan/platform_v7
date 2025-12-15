/*
 * @(#)2019年7月5日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.design.facade.service;

import com.wellsoft.context.annotation.Description;
import com.wellsoft.pt.app.design.dto.LayoutDto;
import com.wellsoft.pt.app.design.dto.WidgetDto;

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
 * 2019年7月5日.1	zhulh		2019年7月5日		Create
 * </pre>
 * @date 2019年7月5日
 */
public interface AppPageDesignerFacadeService {

    @Description("获取页面类型的布局信息列表")
    List<LayoutDto> getLayouts(String containerWtype);

    @Description("根据页面UUID，获取页面的组件信息列表")
    List<WidgetDto> getWidgetsByPageUuid(String pageUuid);

}
