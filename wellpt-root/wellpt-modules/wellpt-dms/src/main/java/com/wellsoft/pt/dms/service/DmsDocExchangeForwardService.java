package com.wellsoft.pt.dms.service;

import com.wellsoft.pt.dms.bean.DmsDocExchangeForwardDto;
import com.wellsoft.pt.dms.core.support.DocExchangeActionData;
import com.wellsoft.pt.dms.dao.impl.DmsDocExchangeForwardDaoImpl;
import com.wellsoft.pt.dms.entity.DmsDocExchangeForwardEntity;
import com.wellsoft.pt.dms.enums.DocExchangeRecordStatusEnum;
import com.wellsoft.pt.jpa.service.JpaService;

import java.util.List;

/**
 * Description:
 *
 * @author chenq
 * @date 2018/5/21
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/5/21    chenq		2018/5/21		Create
 * </pre>
 */
public interface DmsDocExchangeForwardService extends
        JpaService<DmsDocExchangeForwardEntity, DmsDocExchangeForwardDaoImpl, String> {

    /**
     * 保存转发详情
     *
     * @param entity      文档交换记录
     * @param forwardData 转发数据
     * @param status      转发状态
     * @param fromUserId
     */
    String saveForwardDetail(String docExchangeRecordUuid,
                             DocExchangeActionData.DocExcOperateData forwardData,
                             DocExchangeRecordStatusEnum status, String fromUserId, String fromUnitId);

    DmsDocExchangeForwardDto getSignedForwardByRecordUuid(String uuid);

    List<DmsDocExchangeForwardDto> listSendedForwardByRecordUuid(String uuid);
}
