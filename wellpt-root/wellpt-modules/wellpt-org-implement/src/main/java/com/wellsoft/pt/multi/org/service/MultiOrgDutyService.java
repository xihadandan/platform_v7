/*
 * @(#)2018年4月4日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.service;

import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.multi.org.dao.MultiOrgDutyDao;
import com.wellsoft.pt.multi.org.entity.MultiOrgDuty;

import java.util.List;

/**
 * Description:  职务服务接口类
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年4月4日.1	chenqiong		2018年4月4日		Create
 * </pre>
 * @date 2018年4月4日
 */
public interface MultiOrgDutyService extends JpaService<MultiOrgDuty, MultiOrgDutyDao, String> {

    /**
     * 根据id获取数据
     *
     * @param dutyId
     * @return
     */
    MultiOrgDuty getById(String dutyId);

    MultiOrgDuty getByJobId(String jobId);

    Select2QueryData queryDutyListForSelect2(Select2QueryInfo select2QueryInfo);

    /**
     * 按名称获取职务
     *
     * @param name
     * @param systemUnitId
     * @return
     */
    MultiOrgDuty getByName(String name, String systemUnitId);

    /**
     * 如何描述该方法
     *
     * @param systemUnitId
     * @return
     */
    List<MultiOrgDuty> queryAllDutyBySystemUnitId(String systemUnitId);

    List<MultiOrgDuty> getDutysByIds(List<String> ids);

    /**
     * 方法描述
     *
     * @param dutySeqUuid
     * @return
     * @author baozh
     * @date 2021/10/28 16:58
     */
    long countByDutySeqUuid(String dutySeqUuid);

    public long countBySystemUnitIds(List<String> systemUnitIds);

    List<MultiOrgDuty> listBySystemOrSystemUnitId(String system, String systemUnitId);

    List<MultiOrgDuty> listByJobIds(List<String> jobIds);
}
