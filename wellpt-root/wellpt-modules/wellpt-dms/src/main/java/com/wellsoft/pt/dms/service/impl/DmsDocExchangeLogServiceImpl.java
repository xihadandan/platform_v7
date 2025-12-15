package com.wellsoft.pt.dms.service.impl;

import com.wellsoft.pt.dms.dao.impl.DmsDocExchangeLogDaoImpl;
import com.wellsoft.pt.dms.entity.DmsDocExchangeLogEntity;
import com.wellsoft.pt.dms.service.DmsDocExchangeLogService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.rowset.serial.SerialClob;
import java.util.List;

/**
 * Description: 文档交换的操作日志服务实现类
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
@Service
public class DmsDocExchangeLogServiceImpl extends
        AbstractJpaServiceImpl<DmsDocExchangeLogEntity, DmsDocExchangeLogDaoImpl, String> implements
        DmsDocExchangeLogService {

    @Override
    public List<DmsDocExchangeLogEntity> listLogByRecordUuid(String docExchangeRecordUuid) {
        return this.dao.listLogByRecordUuid(docExchangeRecordUuid);
    }

    @Override
    @Transactional
    public void saveLog(String docExcRecordUuid, String operationName, String operator,
                        String target, String content, String fileUuids, String fileNames) {
        try {
            save(new DmsDocExchangeLogEntity(docExcRecordUuid,
                    operationName,
                    operator,
                    new SerialClob(StringUtils.stripToEmpty(target).toCharArray()),
                    content,
                    fileUuids, fileNames));
        } catch (Exception e) {
            logger.error("撤回时候保存操作日志异常：", e);
        }
    }
}
