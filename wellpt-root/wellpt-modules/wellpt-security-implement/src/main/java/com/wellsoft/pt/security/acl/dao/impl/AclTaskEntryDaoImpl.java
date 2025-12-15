package com.wellsoft.pt.security.acl.dao.impl;

import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import com.wellsoft.pt.security.acl.dao.AclSidDao;
import com.wellsoft.pt.security.acl.dao.AclTaskEntryDao;
import com.wellsoft.pt.security.acl.entity.AclSid;
import com.wellsoft.pt.security.acl.entity.AclTaskEntry;
import org.springframework.stereotype.Repository;

/**
 * Description: 如何描述该类
 *
 * @author liuxj
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本  修改人    修改日期      修改内容
 * V1.0   liuxj    2024/12/20    Create
 * </pre>
 * @date 2024/12/20
 */
@Repository
public class AclTaskEntryDaoImpl  extends AbstractJpaDaoImpl<AclTaskEntry, String> implements AclTaskEntryDao {
}
