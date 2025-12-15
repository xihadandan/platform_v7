package com.wellsoft.pt.dyform.implement.definition.dao.impl;

import com.wellsoft.pt.dyform.implement.definition.dao.DyformSqlLogDao;
import com.wellsoft.pt.dyform.implement.definition.entity.DyformSqlLogEntity;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import org.springframework.stereotype.Repository;

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
@Repository
public class DyformSqlLogDaoImpl extends AbstractJpaDaoImpl<DyformSqlLogEntity, String> implements
        DyformSqlLogDao {
}
