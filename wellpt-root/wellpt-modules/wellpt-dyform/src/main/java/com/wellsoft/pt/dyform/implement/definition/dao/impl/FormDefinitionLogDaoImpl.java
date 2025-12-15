package com.wellsoft.pt.dyform.implement.definition.dao.impl;

import com.wellsoft.pt.dyform.implement.definition.dao.FormDefinitionLogDao;
import com.wellsoft.pt.dyform.implement.definition.entity.FormDefinitionLog;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import org.springframework.stereotype.Repository;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年5月9日.1	zhongzh		2020年5月9日		Create
 * </pre>
 * @date 2020年5月9日
 */
@Repository
public class FormDefinitionLogDaoImpl extends AbstractJpaDaoImpl<FormDefinitionLog, String> implements
        FormDefinitionLogDao {
}
