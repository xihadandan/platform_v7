package com.wellsoft.pt.multi.org.dao.impl;

import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import com.wellsoft.pt.multi.org.dao.OrgJobGradeDao;
import com.wellsoft.pt.multi.org.entity.OrgJobGradeEntity;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * Description:
 * 职等dao实现类
 * <pre>
 * 修改记录:
 * 修改后版本    修改人     修改日期    修改内容
 * 1.0         baozh    2021/10/21   Create
 * </pre>
 */
@Repository
public class OrgJobGradeDaoImpl extends AbstractJpaDaoImpl<OrgJobGradeEntity, String> implements OrgJobGradeDao {
    @Override
    public int deleteAllBySystemUnitId(String systemUnitId) {
        Map<String, Object> param = new HashMap<>();
        param.put("systemUnitId", systemUnitId);
        return updateByHQL(" UPDATE OrgJobGradeEntity SET isValid = 0,isException = 0 WHERE systemUnitId = :systemUnitId", param);

    }
}
