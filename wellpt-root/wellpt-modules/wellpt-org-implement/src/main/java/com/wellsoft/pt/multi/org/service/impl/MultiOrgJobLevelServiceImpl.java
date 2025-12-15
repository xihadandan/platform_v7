package com.wellsoft.pt.multi.org.service.impl;

import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.multi.org.dao.MultiOrgJobLevelDao;
import com.wellsoft.pt.multi.org.entity.MultiOrgJobLevelEntity;
import com.wellsoft.pt.multi.org.service.MultiOrgJobLevelService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Description:
 * 职档serviceImpl
 * <pre>
 * 修改记录:
 * 修改后版本    修改人     修改日期    修改内容
 * 1.0         baozh    2021/10/26   Create
 * </pre>
 */
@Service
public class MultiOrgJobLevelServiceImpl extends AbstractJpaServiceImpl<MultiOrgJobLevelEntity, MultiOrgJobLevelDao, String> implements MultiOrgJobLevelService {


    @Override
    public int deleteByJobRankUuid(String jobRankUuid, String systemUnitId) {
        return dao.deleteByJobRankUuid(jobRankUuid, systemUnitId);
    }

    @Transactional
    @Override
    public void saveJobLevel(List<String> levels, String jobRankUuid) {
        // deleteByJobRankUuid(jobRankUuid);
        List<MultiOrgJobLevelEntity> entities = listEntityByJobRankUuid(jobRankUuid);
        Map<String, MultiOrgJobLevelEntity> oldMap = new HashMap<>();
        for (MultiOrgJobLevelEntity entity : entities) {
            if (!levels.contains(entity.getJobLevel())) {
                delete(entity.getUuid());
            } else {
                oldMap.put(entity.getJobLevel(), entity);
            }
        }
        int i = 0;
        for (String level : levels) {
            if (oldMap.containsKey(level)) {
                MultiOrgJobLevelEntity jobLevelEntity = oldMap.get(level);
                jobLevelEntity.setJobLevelSeq(i);
                //TODO
                jobLevelEntity.setModifyTime(new Date());
                jobLevelEntity.setModifier(SpringSecurityUtils.getCurrentUserId());
                update(jobLevelEntity);
            } else {
                MultiOrgJobLevelEntity jobLevelEntity = new MultiOrgJobLevelEntity();
                jobLevelEntity.setJobLevel(level);
                jobLevelEntity.setJobLevelSeq(i++);
                jobLevelEntity.setJobRankUuid(jobRankUuid);
                save(jobLevelEntity);
            }

        }
    }

    @Override
    public List<MultiOrgJobLevelEntity> listEntityByJobRankUuid(String jobRankUuid) {
        MultiOrgJobLevelEntity paramEntity = new MultiOrgJobLevelEntity();
        paramEntity.setJobRankUuid(jobRankUuid);
        return listByEntity(paramEntity);
    }

    @Override
    public List<String> listByJobRankUuid(String jobRankUuid) {
        List<MultiOrgJobLevelEntity> entities = listEntityByJobRankUuid(jobRankUuid);
        List<String> collect = entities.stream()
                .sorted(Comparator.comparing(MultiOrgJobLevelEntity::getJobLevelSeq))
                .map(entity -> entity.getJobLevel())
                .collect(Collectors.toList());
        return collect;
    }
}
