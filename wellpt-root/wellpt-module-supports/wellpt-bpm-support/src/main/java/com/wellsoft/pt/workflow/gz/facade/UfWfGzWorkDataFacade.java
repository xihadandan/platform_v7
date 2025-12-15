/*
 * @(#)2015-08-12 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.gz.facade;

import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.pt.workflow.gz.entity.UfWfGzWorkData;
import com.wellsoft.pt.workflow.gz.service.UfWfGzWorkDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
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
 * 2015-08-12.1	zhulh		2015-08-12		Create
 * </pre>
 * @date 2015-08-12
 */
@Component
public class UfWfGzWorkDataFacade extends AbstractApiFacade {

    @Autowired
    private UfWfGzWorkDataService ufWfGzWorkDataService;

    public List<UfWfGzWorkData> getAllByPk(Collection<String> uuids) {
        return ufWfGzWorkDataService.getAllByPk(uuids);
    }

}
