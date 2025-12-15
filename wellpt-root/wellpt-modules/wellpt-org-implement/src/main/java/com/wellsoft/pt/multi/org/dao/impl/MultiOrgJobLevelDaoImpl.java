package com.wellsoft.pt.multi.org.dao.impl;

import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import com.wellsoft.pt.multi.org.dao.MultiOrgJobLevelDao;
import com.wellsoft.pt.multi.org.entity.MultiOrgJobLevelEntity;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * Description:
 * 职档Daoimpl
 * <pre>
 * 修改记录:
 * 修改后版本    修改人     修改日期    修改内容
 * 1.0         baozh    2021/10/26   Create
 * </pre>
 */
@Repository
public class MultiOrgJobLevelDaoImpl extends AbstractJpaDaoImpl<MultiOrgJobLevelEntity, String> implements MultiOrgJobLevelDao {

    @Override
    public int deleteByJobRankUuid(String jobRankUuid, String systemUnitId) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("jobRankUuid", jobRankUuid);
        paramMap.put("systemUnitId", systemUnitId);
        return deleteByHQL("DELETE FROM MultiOrgJobLevelEntity WHERE systemUnitId = :systemUnitId AND jobRankUuid = :jobRankUuid", paramMap);
    }
}
