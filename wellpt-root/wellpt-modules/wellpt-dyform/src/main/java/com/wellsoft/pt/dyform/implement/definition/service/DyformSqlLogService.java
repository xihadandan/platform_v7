package com.wellsoft.pt.dyform.implement.definition.service;

import com.wellsoft.pt.dyform.implement.definition.dao.DyformSqlLogDao;
import com.wellsoft.pt.dyform.implement.definition.entity.DyformSqlLogEntity;
import com.wellsoft.pt.jpa.service.JpaService;

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
public interface DyformSqlLogService extends
        JpaService<DyformSqlLogEntity, DyformSqlLogDao, String> {

    void saveSqlLog(String sqlScript, String tblName, String formUuid);
}
