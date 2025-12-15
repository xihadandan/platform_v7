/*
 * @(#)2022-03-22 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.handover.service;

import com.wellsoft.pt.handover.dao.WhWorkHandoverDao;
import com.wellsoft.pt.handover.dto.GetFlowDatasRecordsDto;
import com.wellsoft.pt.handover.dto.GetWorkHandoverByUuidDto;
import com.wellsoft.pt.handover.dto.SaveWhWorkHandoverDto;
import com.wellsoft.pt.handover.dto.WhWorkTypeToHandoverCountDto;
import com.wellsoft.pt.handover.entity.WhWorkHandoverEntity;
import com.wellsoft.pt.jpa.service.JpaService;

import java.util.List;

/**
 * Description: 数据库表WH_WORK_HANDOVER的service服务接口
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2022-03-22.1	zenghw		2022-03-22		Create
 * </pre>
 * @date 2022-03-22
 */
public interface WhWorkHandoverService extends JpaService<WhWorkHandoverEntity, WhWorkHandoverDao, String> {

    /**
     * 通过UUID 获取工作交接基本信息
     *
     * @param handoverUuid
     * @return com.wellsoft.pt.handover.dto.GetWorkHandoverByUuidDto
     **/
    public GetWorkHandoverByUuidDto getWorkHandoverByUuid(String handoverUuid);

    /**
     * 删除未执行的工作交接
     *
     * @param workHandoverUuid 工作交接uuid
     * @return void
     **/
    public void deleteWorkHandover(String workHandoverUuid);

    /**
     * 保存工作交接
     *
     * @param saveWhWorkHandoverDto
     * @return void
     **/
    public void saveWorkHandoverNow(SaveWhWorkHandoverDto saveWhWorkHandoverDto);

    /**
     * 保存工作交接-系统空闲时执行
     *
     * @param saveWhWorkHandoverDto
     * @return void
     **/
    public void saveWorkHandoverFree(SaveWhWorkHandoverDto saveWhWorkHandoverDto);

    /**
     * 工作交接未创建时查询交接结果接口
     *
     * @param getFlowDatasRecordsDto
     * @return
     **/
    public List<WhWorkTypeToHandoverCountDto> getFlowDatasRecords(GetFlowDatasRecordsDto getFlowDatasRecordsDto);

    public Integer checkWorkFlowTaskDelegation(String handoverPersonId);

    /**
     * 执行工作交接任务接口
     *
     * @param workHandoverUuid
     * @return void
     **/
    public void handoverTask(String workHandoverUuid);
}
