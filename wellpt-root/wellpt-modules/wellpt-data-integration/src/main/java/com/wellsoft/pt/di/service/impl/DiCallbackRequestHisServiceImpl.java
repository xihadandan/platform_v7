/*
 * @(#)2019-07-25 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.di.service.impl;

import com.wellsoft.pt.di.dao.DiCallbackRequestHisDao;
import com.wellsoft.pt.di.entity.DiCallbackRequestHisEntity;
import com.wellsoft.pt.di.service.DiCallbackRequestHisService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.springframework.stereotype.Service;

/**
 * Description: 数据库表DI_CALLBACK_REQUEST_HIS的service服务接口实现类
 *
 * @author chenq
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019-07-25.1	chenq		2019-07-25		Create
 * </pre>
 * @date 2019-07-25
 */
@Service
public class DiCallbackRequestHisServiceImpl extends AbstractJpaServiceImpl<DiCallbackRequestHisEntity, DiCallbackRequestHisDao, String> implements DiCallbackRequestHisService {


}
