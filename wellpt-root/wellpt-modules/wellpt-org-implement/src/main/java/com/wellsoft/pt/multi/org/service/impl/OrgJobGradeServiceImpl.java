package com.wellsoft.pt.multi.org.service.impl;

import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.multi.org.constant.UnitParamConstant;
import com.wellsoft.pt.multi.org.dao.OrgJobGradeDao;
import com.wellsoft.pt.multi.org.entity.OrgJobGradeEntity;
import com.wellsoft.pt.multi.org.service.OrgJobGradeService;
import com.wellsoft.pt.multi.org.service.UnitParamService;
import com.wellsoft.pt.multi.org.vo.OrgJobGradeVo;
import com.wellsoft.pt.multi.org.vo.OrgJobGradesVo;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description:
 * 职等ServiceImpl
 * <pre>
 * 修改记录:
 * 修改后版本    修改人     修改日期    修改内容
 * 1.0         baozh    2021/10/20   Create
 * </pre>
 */
@Service
public class OrgJobGradeServiceImpl extends AbstractJpaServiceImpl<OrgJobGradeEntity, OrgJobGradeDao, String> implements OrgJobGradeService {

    @Autowired
    UnitParamService unitParamService;

    @Transactional
    @Override
    public void saveJobGrade(OrgJobGradesVo orgJobGradeVo) {
        String systemUnitId = SpringSecurityUtils.getCurrentUserUnitId();
        //先删除之前的职等
        dao.deleteAllBySystemUnitId(systemUnitId);
        unitParamService.setValue(UnitParamConstant.JOB_GRADE_ORDER, orgJobGradeVo.getOrder());
        List<OrgJobGradeEntity> entities = dao.listByFieldEqValue("systemUnitId", systemUnitId);
        Map<Integer, OrgJobGradeEntity> jobGradMap = new HashMap<>();
        for (OrgJobGradeEntity entity : entities) {
            jobGradMap.put(entity.getJobGrade(), entity);
        }
        //然后保存
        for (OrgJobGradeVo jobGrade : orgJobGradeVo.getOrgJobGrades()) {
            OrgJobGradeEntity entity = null;
            if (jobGradMap.containsKey(jobGrade.getJobGrade())) {
                entity = jobGradMap.get(jobGrade.getJobGrade());
            } else {
                entity = new OrgJobGradeEntity();
            }
            BeanUtils.copyProperties(jobGrade, entity);
            entity.setIsValid(1);
            save(entity);
        }
    }

    @Override
    public List<OrgJobGradeEntity> jobGradeList() {
        OrgJobGradeEntity entity = new OrgJobGradeEntity();
        entity.setSystemUnitId(SpringSecurityUtils.getCurrentUserUnitId());
        String order = unitParamService.getValue(UnitParamConstant.JOB_GRADE_ORDER);
        if (order == null) {
            order = "ASC";
        }
        List<OrgJobGradeEntity> gradeEntities = listAllByPage(entity, null, "jobGrade " + order);
        return gradeEntities;
    }

    @Override
    public OrgJobGradeEntity getByJobGrade(Integer jobGrade) {
        List<OrgJobGradeEntity> entities = dao.listByFieldEqValue("jobGrade", jobGrade);
        return CollectionUtils.isNotEmpty(entities) ? entities.get(0) : null;
    }
}
