package com.wellsoft.pt.multi.org.dao;

import com.wellsoft.pt.jpa.dao.JpaDao;
import com.wellsoft.pt.multi.org.entity.OrgJobGradeEntity;

public interface OrgJobGradeDao extends JpaDao<OrgJobGradeEntity, String> {

    /**
     * 根据系统单元ID删除所有
     *
     * @param systemUnitId
     * @return
     * @author baozh
     * @date 2021/10/26 11:04
     */
    int deleteAllBySystemUnitId(String systemUnitId);
}
