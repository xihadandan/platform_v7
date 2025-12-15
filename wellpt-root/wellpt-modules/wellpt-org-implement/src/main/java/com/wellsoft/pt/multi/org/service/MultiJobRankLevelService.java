package com.wellsoft.pt.multi.org.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.multi.org.dao.MultiJobRankLevelDao;
import com.wellsoft.pt.multi.org.entity.MultiJobRankLevelEntity;
import com.wellsoft.pt.multi.org.vo.JobRankLevelVo;

import java.util.List;

public interface MultiJobRankLevelService extends JpaService<MultiJobRankLevelEntity, MultiJobRankLevelDao, String> {

    /**
     * 根据用户uuid
     *
     * @param userId,systemUnitId
     * @return
     * @author baozh
     * @date 2021/10/27 13:47
     */
    int deleteByUserId(String userId);

    /**
     * 根据userId获取职位职档信息
     *
     * @param userId
     * @return
     * @author baozh
     * @date 2021/10/27 14:18
     */
    List<JobRankLevelVo> queryListByUserId(String userId);
}
