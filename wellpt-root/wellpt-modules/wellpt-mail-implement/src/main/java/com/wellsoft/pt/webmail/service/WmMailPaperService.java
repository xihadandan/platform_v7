/*
 * @(#)2018年3月7日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.webmail.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.webmail.bean.WmMailPaperDto;
import com.wellsoft.pt.webmail.dao.WmMailPaperDao;
import com.wellsoft.pt.webmail.entity.WmMailPaperEntity;

import java.util.List;

/**
 * Description: 邮件信纸服务接口
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
public interface WmMailPaperService extends JpaService<WmMailPaperEntity, WmMailPaperDao, String> {

    /**
     * 更新用户默认的信纸背景
     */
    void updateUserDefaultPaper(WmMailPaperDto dto);

    /**
     * 查询用户默认的信纸背景
     *
     * @param userId
     * @return
     */
    WmMailPaperEntity queryUserDefaultPaper(String userId);

    /**
     * 查询系统默认配置的信纸
     *
     * @return
     */
    List<WmMailPaperEntity> querySystemMailPapers();

}
