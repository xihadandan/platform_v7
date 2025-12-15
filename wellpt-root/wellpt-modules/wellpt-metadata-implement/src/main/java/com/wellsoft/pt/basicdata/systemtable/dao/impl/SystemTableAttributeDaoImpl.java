package com.wellsoft.pt.basicdata.systemtable.dao.impl;

import com.wellsoft.pt.basicdata.systemtable.dao.SystemTableAttributeDao;
import com.wellsoft.pt.basicdata.systemtable.entity.SystemTableAttribute;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import org.springframework.stereotype.Repository;

/**
 * Description: 系统表结构数据层访问类
 *
 * @author zhouyq
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-3-22.1	zhouyq		2013-3-22		Create
 * </pre>
 * @date 2013-3-22
 */
@Repository
public class SystemTableAttributeDaoImpl extends AbstractJpaDaoImpl<SystemTableAttribute, String> implements
        SystemTableAttributeDao {
}
