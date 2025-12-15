/*
 * @(#)2018年4月17日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.service.impl;

import com.wellsoft.pt.integration.dao.SynchronousSourceTableDao;
import com.wellsoft.pt.integration.entity.SynchronousSourceTable;
import com.wellsoft.pt.integration.service.SynchronousSourceTableService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年4月17日.1	chenqiong		2018年4月17日		Create
 * </pre>
 * @date 2018年4月17日
 */
@Service
public class SynchronousSourceTableServiceImpl extends
        AbstractJpaServiceImpl<SynchronousSourceTable, SynchronousSourceTableDao, String> implements
        SynchronousSourceTableService {

    @Override
    public SynchronousSourceTable getById(String id) {
        return this.dao.getOneByHQL("from SynchronousSourceTable where id='" + id + "'", null);
    }

    @Override
    public List<SynchronousSourceTable> getSynchronousSourceTables(String direction, String taskId) {
        if (direction.equals("in")) {
            String hql = "from SynchronousSourceTable s where s.isEnable = :isEnable and s.notIn = :notIn and s.jobIdIn = :taskId order by s.code asc";
            Map<String, Object> values = new HashMap<String, Object>();
            values.put("isEnable", true);
            values.put("notIn", false);
            values.put("taskId", taskId);
            List<SynchronousSourceTable> eList = listByHQL(hql, values);
            return eList;
        } else if (direction.equals("out")) {
            String hql = "from SynchronousSourceTable s where s.isEnable = :isEnable and s.notOut = :notOut and s.jobId = :taskId order by s.code asc";
            Map<String, Object> values = new HashMap<String, Object>();
            values.put("isEnable", true);
            values.put("notOut", false);
            values.put("taskId", taskId);
            List<SynchronousSourceTable> eList = listByHQL(hql, values);
            return eList;
        }
        return new ArrayList<SynchronousSourceTable>();
    }

    @Override
    public SynchronousSourceTable getByTableEnName(String tableEnName) {
        return this.dao.getOneByHQL("from SynchronousSourceTable where tableEnName='" + tableEnName + "'", null);
    }

}
