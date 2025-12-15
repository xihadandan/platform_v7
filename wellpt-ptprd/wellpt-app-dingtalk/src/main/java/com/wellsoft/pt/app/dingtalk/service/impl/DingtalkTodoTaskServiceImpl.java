/*
 * @(#)4/25/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.dingtalk.service.impl;

import com.wellsoft.pt.app.dingtalk.dao.DingtalkTodoTaskDao;
import com.wellsoft.pt.app.dingtalk.entity.DingtalkTodoTaskEntity;
import com.wellsoft.pt.app.dingtalk.service.DingtalkTodoTaskService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 4/25/25.1	    zhulh		4/25/25		    Create
 * </pre>
 * @date 4/25/25
 */
@Service
public class DingtalkTodoTaskServiceImpl extends AbstractJpaServiceImpl<DingtalkTodoTaskEntity, DingtalkTodoTaskDao, Long> implements
        DingtalkTodoTaskService {

    @Override
    public List<DingtalkTodoTaskEntity> listByTaskInstUuidAndState(String taskInstUuid, DingtalkTodoTaskEntity.State state) {
        DingtalkTodoTaskEntity entity = new DingtalkTodoTaskEntity();
        entity.setTaskInstUuid(taskInstUuid);
        entity.setState(state);
        return this.dao.listByEntity(entity);
    }

}
