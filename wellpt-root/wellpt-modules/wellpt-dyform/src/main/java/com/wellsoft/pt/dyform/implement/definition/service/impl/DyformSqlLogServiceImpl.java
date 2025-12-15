package com.wellsoft.pt.dyform.implement.definition.service.impl;

import com.wellsoft.pt.dyform.implement.definition.dao.DyformSqlLogDao;
import com.wellsoft.pt.dyform.implement.definition.entity.DyformSqlLogEntity;
import com.wellsoft.pt.dyform.implement.definition.service.DyformSqlLogService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.springframework.stereotype.Service;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/3/8
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/3/8    chenq		2019/3/8		Create
 * </pre>
 */
@Service
public class DyformSqlLogServiceImpl extends
        AbstractJpaServiceImpl<DyformSqlLogEntity, DyformSqlLogDao, String> implements
        DyformSqlLogService {
    @Override
    public void saveSqlLog(String sqlScript, String tblName, String formUuid) {
        DyformSqlLogEntity logEntity = new DyformSqlLogEntity();
        logEntity.setTableName(tblName);
        logEntity.setSqlScript(sqlScript);
        logEntity.setFormUuid(formUuid);
        this.save(logEntity);
    }
}
