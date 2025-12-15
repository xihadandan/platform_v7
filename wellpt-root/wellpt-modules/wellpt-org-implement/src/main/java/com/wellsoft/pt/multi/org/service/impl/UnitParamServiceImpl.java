package com.wellsoft.pt.multi.org.service.impl;

import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.multi.org.dao.UnitParamDao;
import com.wellsoft.pt.multi.org.entity.UnitParamEntity;
import com.wellsoft.pt.multi.org.service.UnitParamService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Description:
 * 单位参数serviceImpl
 * <pre>
 * 修改记录:
 * 修改后版本    修改人     修改日期    修改内容
 * 1.0         baozh    2021/10/22   Create
 * </pre>
 */
@Service
public class UnitParamServiceImpl extends AbstractJpaServiceImpl<UnitParamEntity, UnitParamDao, String> implements UnitParamService {

    @Override
    public UnitParamEntity getUnitParam(String key) {
        UnitParamEntity paramEntity = new UnitParamEntity();
        paramEntity.setSystemUnitId(SpringSecurityUtils.getCurrentUserUnitId());
        paramEntity.setParamKey(key);
        List<UnitParamEntity> entities = listByEntity(paramEntity);
        if (entities == null || entities.isEmpty()) {
            paramEntity.setRemark("");
            return paramEntity;
        }
        return entities.get(0);
    }

    @Override
    public String getValue(String key) {
        UnitParamEntity unitParam = getUnitParam(key);
        return unitParam == null ? null : unitParam.getParamValue();
    }

    @Transactional
    @Override
    public void setValue(String key, String value) {
        UnitParamEntity unitParam = getUnitParam(key);
        unitParam.setParamValue(value);
        save(unitParam);
    }
}
