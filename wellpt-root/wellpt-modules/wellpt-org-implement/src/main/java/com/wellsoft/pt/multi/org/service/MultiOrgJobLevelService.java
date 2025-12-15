package com.wellsoft.pt.multi.org.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.multi.org.dao.MultiOrgJobLevelDao;
import com.wellsoft.pt.multi.org.entity.MultiOrgJobLevelEntity;

import java.util.List;

public interface MultiOrgJobLevelService extends JpaService<MultiOrgJobLevelEntity, MultiOrgJobLevelDao, String> {

    /**
     * 根据职级Uuid删除职档
     *
     * @param jobRankUuid,systemUnitId
     * @return
     * @author baozh
     * @date 2021/10/26 15:56
     */
    int deleteByJobRankUuid(String jobRankUuid, String systemUnitId);

    /**
     * 保存职档
     *
     * @param levels,jobRankUuid
     * @return
     * @author baozh
     * @date 2021/10/26 16:11
     */
    void saveJobLevel(List<String> levels, String jobRankUuid);

    /**
     * 根据职级获取职档
     *
     * @param jobRankUuid
     * @return
     * @author baozh
     * @date 2021/10/26 17:03
     */
    List<MultiOrgJobLevelEntity> listEntityByJobRankUuid(String jobRankUuid);

    /**
     * 根据职级获取职档
     *
     * @param jobRankUuid
     * @return
     * @author baozh
     * @date 2021/10/26 17:03
     */
    List<String> listByJobRankUuid(String jobRankUuid);
}
