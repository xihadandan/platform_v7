package com.wellsoft.pt.dms.service.impl;

import com.wellsoft.pt.dms.bean.DmsDocExcContactBookUnitDto;
import com.wellsoft.pt.dms.dao.impl.DmsDocExcContactBookUnitDaoImpl;
import com.wellsoft.pt.dms.entity.DmsDocExcContactBookUnitEntity;
import com.wellsoft.pt.dms.enums.DocExcContactBookIdPrefixEnum;
import com.wellsoft.pt.dms.service.DmsDocExcContactBookUnitService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Description: 文档交换-通讯录单位服务实现类
 *
 * @author chenq
 * @date 2018/5/31
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/5/31    chenq		2018/5/31		Create
 * </pre>
 */
@Service
public class DmsDocExcContactBookUnitServiceImpl extends
        AbstractJpaServiceImpl<DmsDocExcContactBookUnitEntity, DmsDocExcContactBookUnitDaoImpl, String> implements
        DmsDocExcContactBookUnitService {
    @Override
    @Transactional
    public void saveContactBookUnit(DmsDocExcContactBookUnitDto contactBookUnitDto) {
        DmsDocExcContactBookUnitEntity contactBookUnitEntity = new DmsDocExcContactBookUnitEntity();
        if (StringUtils.isNotBlank(contactBookUnitDto.getUuid())) {
            contactBookUnitEntity = getOne(contactBookUnitDto.getUuid());
        } else {
            contactBookUnitEntity.setUnitId(
                    DocExcContactBookIdPrefixEnum.CONTACT_UNIT_ID.getId() + DateFormatUtils.format(
                            new Date(),
                            "yyMMddHHmmssSSS"));
            contactBookUnitEntity.setModuleId(contactBookUnitDto.getModuleId());
        }
        contactBookUnitEntity.setUnitCode(contactBookUnitDto.getUnitCode());
        contactBookUnitEntity.setUnitName(contactBookUnitDto.getUnitName());
        contactBookUnitEntity.setFullUnitName(contactBookUnitDto.getFullUnitName());
        save(contactBookUnitEntity);


    }

    @Override
    public List<DmsDocExcContactBookUnitEntity> listByUserId(String currentUserId) {
        return this.dao.listByUserId(currentUserId);
    }

    @Override
    public List<DmsDocExcContactBookUnitEntity> listByUserIdAndModule(String currentUserId,
                                                                      String moduleId) {
        return this.dao.listByUserIdAndModule(currentUserId, moduleId);
    }

    @Override
    public DmsDocExcContactBookUnitEntity getByUnitId(String unitId) {
        List<DmsDocExcContactBookUnitEntity> unitEntities = this.dao.listByFieldEqValue("unitId",
                unitId);
        return CollectionUtils.isNotEmpty(unitEntities) ? unitEntities.get(0) : null;
    }

    @Override
    public List<DmsDocExcContactBookUnitEntity> listBySysUnitIdAndModule(String unitId,
                                                                         String moduleId) {
        return this.dao.listBySysUnitIdAndModule(unitId, moduleId);
    }
}
