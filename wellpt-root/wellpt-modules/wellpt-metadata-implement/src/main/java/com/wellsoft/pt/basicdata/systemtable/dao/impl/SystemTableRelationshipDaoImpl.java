package com.wellsoft.pt.basicdata.systemtable.dao.impl;

import com.wellsoft.pt.basicdata.systemtable.dao.SystemTableRelationshipDao;
import com.wellsoft.pt.basicdata.systemtable.entity.SystemTableRelationship;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import org.springframework.stereotype.Repository;

/**
 * Description: 系统表关系数据层访问类
 *
 * @author zhouyq
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-4-25.1	zhouyq		2013-4-25		Create
 * </pre>
 * @date 2013-4-25
 */
@Repository
public class SystemTableRelationshipDaoImpl extends AbstractJpaDaoImpl<SystemTableRelationship, String> implements
        SystemTableRelationshipDao {

}
