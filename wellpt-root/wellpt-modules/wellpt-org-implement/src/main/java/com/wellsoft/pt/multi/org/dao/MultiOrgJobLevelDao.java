package com.wellsoft.pt.multi.org.dao;

import com.wellsoft.pt.jpa.dao.JpaDao;
import com.wellsoft.pt.multi.org.entity.MultiOrgJobLevelEntity;

public interface MultiOrgJobLevelDao extends JpaDao<MultiOrgJobLevelEntity, String> {

    /**
     * 根据职级删除
     *
     * @param jobRankUuid,systemUnitId
     * @return
     * @author baozh
     * @date 2021/10/26 16:00
     */
    int deleteByJobRankUuid(String jobRankUuid, String systemUnitId);
}
