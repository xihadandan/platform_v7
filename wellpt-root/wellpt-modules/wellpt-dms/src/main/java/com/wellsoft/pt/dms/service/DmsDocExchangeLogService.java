package com.wellsoft.pt.dms.service;

import com.wellsoft.pt.dms.dao.impl.DmsDocExchangeLogDaoImpl;
import com.wellsoft.pt.dms.entity.DmsDocExchangeLogEntity;
import com.wellsoft.pt.jpa.service.JpaService;

import java.util.List;

/**
 * Description: 文档交换记录的操作日志服务
 *
 * @author chenq
 * @date 2018/5/18
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/5/18    chenq		2018/5/18		Create
 * </pre>
 */
public interface DmsDocExchangeLogService extends
        JpaService<DmsDocExchangeLogEntity, DmsDocExchangeLogDaoImpl, String> {


    /**
     * 查询文档交换记录的所有操作日志
     *
     * @param docExchangeRecordUuid
     * @return
     */
    List<DmsDocExchangeLogEntity> listLogByRecordUuid(String docExchangeRecordUuid);

    /**
     * 保存日志
     *
     * @param docExcRecordUuid 文档交换记录UUID
     * @param operationName    操作名称
     * @param operator         操作用户名称
     * @param target           目标对象
     * @param content          内容
     * @param fileUuids        附件UUID
     */
    void saveLog(String docExcRecordUuid, String operationName, String operator, String target,
                 String content,
                 String fileUuids, String fileNames);
}
