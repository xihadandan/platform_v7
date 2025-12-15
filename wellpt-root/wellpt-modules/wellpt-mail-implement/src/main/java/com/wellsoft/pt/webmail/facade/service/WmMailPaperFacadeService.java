/*
 * @(#)2018年3月7日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.webmail.facade.service;

import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.webmail.bean.WmMailPaperDto;

import java.util.List;

/**
 * Description: 信纸facade服务
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年3月7日.1	chenqiong		2018年3月7日		Create
 * </pre>
 * @date 2018年3月7日
 */
public interface WmMailPaperFacadeService extends BaseService {

    /**
     * 查询当前用户默认的信纸背景
     *
     * @param userId
     * @return
     */
    WmMailPaperDto queryCurrentUserDefaultPaper();

    /**
     * 更新用户默认的信纸背景
     */
    void updateCurrentUserDefaultPaper(WmMailPaperDto dto);

    /**
     * 查询系统默认配置的信纸
     *
     * @return
     */
    List<WmMailPaperDto> querySystemMailPapers();
}
