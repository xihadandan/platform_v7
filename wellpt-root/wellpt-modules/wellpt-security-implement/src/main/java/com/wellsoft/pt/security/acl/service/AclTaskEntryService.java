package com.wellsoft.pt.security.acl.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.security.acl.dao.AclTaskEntryDao;
import com.wellsoft.pt.security.acl.entity.AclTaskEntry;

import java.util.List;
import java.util.Map;

/**
 * Description: 环节实例权限服务接口
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
public interface AclTaskEntryService extends JpaService<AclTaskEntry, AclTaskEntryDao, String> {
    /**
     * 根据hql删除
     *
     * @param hql    hql
     * @param params 参数
     * @return
     */
    int deleteByHQL(String hql, Map<String, Object> params);

    /**
     * @param hql
     * @param params
     * @return
     */
    List<String> listCharSequenceByHQL(String hql, Map<String, Object> params);
}
