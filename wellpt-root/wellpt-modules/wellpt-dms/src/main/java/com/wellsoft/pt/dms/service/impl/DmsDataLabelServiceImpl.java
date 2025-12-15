package com.wellsoft.pt.dms.service.impl;

import com.wellsoft.pt.dms.bean.DmsDataLabelDto;
import com.wellsoft.pt.dms.dao.impl.DmsDataLabelDaoImpl;
import com.wellsoft.pt.dms.entity.DmsDataLabelEntity;
import com.wellsoft.pt.dms.service.DmsDataLabelRelaService;
import com.wellsoft.pt.dms.service.DmsDataLabelService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: 数据标签服务实现类
 *
 * @author chenq
 * @date 2018/6/11
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/6/11    chenq		2018/6/11		Create
 * </pre>
 */
@Service
public class DmsDataLabelServiceImpl extends
        AbstractJpaServiceImpl<DmsDataLabelEntity, DmsDataLabelDaoImpl, String> implements
        DmsDataLabelService {

    @Autowired
    DmsDataLabelRelaService dmsDataLabelRelaService;

    @Override
    public List<DmsDataLabelEntity> queryByUserIdAndModuleId(String currentUserId,
                                                             String moduleId) {

        return dao.queryByUserIdAndModuleId(currentUserId, moduleId);
    }

    @Override
    @Transactional
    public DmsDataLabelEntity saveDmsDataLabel(DmsDataLabelDto dto) {
        DmsDataLabelEntity dmsDataLabelEntity = new DmsDataLabelEntity();
        if (StringUtils.isNotBlank(dto.getUuid())) {
            dmsDataLabelEntity = this.getOne(dto.getUuid());
        } else {
            dmsDataLabelEntity.setUserId(SpringSecurityUtils.getCurrentUserId());
            dmsDataLabelEntity.setModuleId(dto.getModuleId());
        }
        dmsDataLabelEntity.setLabelColor(dto.getLabelColor());
        dmsDataLabelEntity.setLabelName(StringUtils.isBlank(
                dto.getLabelName()) ? dmsDataLabelEntity.getLabelName() : dto.getLabelName());
        this.save(dmsDataLabelEntity);
        return dmsDataLabelEntity;
    }

    @Override
    @Transactional
    public void deleteLabelsAndRelaData(ArrayList<String> uuids) {
        this.deleteByUuids(uuids);
        dmsDataLabelRelaService.deleteByLabelUuids(uuids);

    }
}
