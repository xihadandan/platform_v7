package com.wellsoft.pt.security.acl.service.impl;

import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.security.acl.dao.AclTaskEntryDao;
import com.wellsoft.pt.security.acl.entity.AclTaskEntry;
import com.wellsoft.pt.security.acl.service.AclTaskEntryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Description: 环节实例权限实现类
 *
 * @author liuxj
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本  修改人    修改日期      修改内容
 * V1.0   liuxj    2024/12/19    Create
 * </pre>
 * @date 2024/12/19
 */
@Service
public class AclTaskEntryServiceImpl extends AbstractJpaServiceImpl<AclTaskEntry, AclTaskEntryDao, String>
        implements AclTaskEntryService {


    @Override
    public int deleteByHQL(String hql, Map<String, Object> params) {
        return this.dao.deleteByHQL(hql, params);
    }

    @Override
    public List<String> listCharSequenceByHQL(String hql, Map<String, Object> params) {
        return this.dao.listCharSequenceByHQL(hql, params);
    }

}
