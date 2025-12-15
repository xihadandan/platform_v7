/*
 * @(#)2021-07-13 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.service;


import com.wellsoft.pt.dms.dao.DmsDocExchangeConfigDao;
import com.wellsoft.pt.dms.dto.DmsDocExchangeConfigDto;
import com.wellsoft.pt.dms.dto.DocExcConfig;
import com.wellsoft.pt.dms.entity.DmsDocExchangeConfigEntity;
import com.wellsoft.pt.jpa.service.JpaService;

import java.util.List;

/**
 * Description: 数据库表DMS_DOC_EXCHANGE_CONFIG的service服务接口
 *
 * @author yt
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021-07-13.1	yt		2021-07-13		Create
 * </pre>
 * @date 2021-07-13
 */
public interface DmsDocExchangeConfigService extends JpaService<DmsDocExchangeConfigEntity, DmsDocExchangeConfigDao, String> {

    /**
     * 查询列表
     *
     * @return
     */
    List<DocExcConfig> queryList();


    /**
     * 删除
     *
     * @param uuid
     */
    void del(String uuid);

    /**
     * 保存或更新
     *
     * @param configDto
     * @return
     */
    String saveOrUpdate(DmsDocExchangeConfigDto configDto);

    /**
     * 排序
     *
     * @param configList
     */
    void sequence(List<DocExcConfig> configList);
}
