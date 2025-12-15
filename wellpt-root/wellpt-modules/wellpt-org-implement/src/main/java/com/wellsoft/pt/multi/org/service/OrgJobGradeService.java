package com.wellsoft.pt.multi.org.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.multi.org.dao.OrgJobGradeDao;
import com.wellsoft.pt.multi.org.entity.OrgJobGradeEntity;
import com.wellsoft.pt.multi.org.vo.OrgJobGradesVo;

import java.util.List;

public interface OrgJobGradeService extends JpaService<OrgJobGradeEntity, OrgJobGradeDao, String> {

    /**
     * 保存职等
     *
     * @param orgJobGradeVo
     * @return
     * @author baozh
     * @date 2021/10/20 17:24
     */
    void saveJobGrade(OrgJobGradesVo orgJobGradeVo);

    /**
     * 查询职等列表
     *
     * @param
     * @return
     * @author baozh
     * @date 2021/10/20 17:40
     */
    List<OrgJobGradeEntity> jobGradeList();

    /**
     * 通过职等查询职等名称
     *
     * @param jobGrade
     * @return
     * @author baozh
     * @date 2021/11/30 11:38
     */
    OrgJobGradeEntity getByJobGrade(Integer jobGrade);
}
