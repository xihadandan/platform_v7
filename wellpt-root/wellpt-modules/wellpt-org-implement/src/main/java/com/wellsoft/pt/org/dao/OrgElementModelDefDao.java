package com.wellsoft.pt.org.dao;

import com.wellsoft.pt.jpa.dao.JpaDao;
import com.wellsoft.pt.org.entity.OrgElementModelDefEntity;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2022年11月10日   chenq	 Create
 * </pre>
 */
public interface OrgElementModelDefDao extends JpaDao<OrgElementModelDefEntity, Long> {
    OrgElementModelDefEntity getByIdAndSystem(String id, String system);
    OrgElementModelDefEntity getByIdAndNullSystem(String id );
    void deleteByIdAndSystem(String id, String system);
}
