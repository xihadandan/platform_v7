/*
 * @(#)2018年4月16日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bm.service;

import com.wellsoft.pt.bm.dao.SubmitVerifyDao;
import com.wellsoft.pt.bm.entity.SubmitVerify;
import com.wellsoft.pt.jpa.service.JpaService;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年4月16日.1	chenqiong		2018年4月16日		Create
 * </pre>
 * @date 2018年4月16日
 */
public interface SubmitVerifyService extends JpaService<SubmitVerify, SubmitVerifyDao, String> {

    /**
     * 如何描述该方法
     *
     * @param uuid
     * @param id
     * @return
     */
    List<SubmitVerify> getObjByPuuidAndVerifyId(String uuid, String id);

}
