package com.wellsoft.pt.multi.org.service.impl;

import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.multi.org.dao.MultiJobRankLevelDao;
import com.wellsoft.pt.multi.org.entity.MultiJobRankLevelEntity;
import com.wellsoft.pt.multi.org.entity.MultiOrgJobRank;
import com.wellsoft.pt.multi.org.service.MultiJobRankLevelService;
import com.wellsoft.pt.multi.org.service.MultiOrgJobRankService;
import com.wellsoft.pt.multi.org.vo.JobRankLevelVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Description:
 * 工作职位直接
 * <pre>
 * 修改记录:
 * 修改后版本    修改人     修改日期    修改内容
 * 1.0         baozh    2021/10/27   Create
 * </pre>
 */
@Service
public class MultiJobRankLevelServiceImpl extends AbstractJpaServiceImpl<MultiJobRankLevelEntity, MultiJobRankLevelDao, String> implements MultiJobRankLevelService {

    @Autowired
    private MultiOrgJobRankService multiOrgJobRankService;

    @Override
    public int deleteByUserId(String userId) {
        Map<String, Object> param = new HashMap<>();
        param.put("userId", userId);
        return dao.deleteByHQL("DELETE FROM MultiJobRankLevelEntity WHERE userId = :userId", param);
    }

    @Override
    public List<JobRankLevelVo> queryListByUserId(String userId) {
        List<MultiJobRankLevelEntity> entities = dao.listByFieldEqValue("userId", userId);

        List<String> jobRanks = entities.stream().map(entity -> entity.getJobRankId()).collect(Collectors.toList());
        Map<String, MultiOrgJobRank> jobGradeMap = new HashMap<>();
        if (!jobRanks.isEmpty()) {
            List<MultiOrgJobRank> jobRankEntityList = multiOrgJobRankService.getMultiOrgJobRankByJobRankId(jobRanks.toArray(new String[]{}));
            for (MultiOrgJobRank jobRankEntity : jobRankEntityList) {
                jobGradeMap.put(jobRankEntity.getId(), jobRankEntity);
            }
        }
        List<JobRankLevelVo> collect = entities.stream().map(entity -> {
            JobRankLevelVo vo = new JobRankLevelVo();
            BeanUtils.copyProperties(entity, vo);
            if (!jobGradeMap.isEmpty() && jobGradeMap.containsKey(entity.getJobRankId())) {
                MultiOrgJobRank jobRankEntity = jobGradeMap.get(entity.getJobRankId());
                vo.setJobGrade(jobRankEntity.getJobGrade());
                vo.setJobRank(jobRankEntity.getJobRank());
            }
            return vo;
        }).collect(Collectors.toList());

        return collect;
    }
}
