package com.wellsoft.pt.datadic.manager.service.impl;

import com.wellsoft.pt.datadic.manager.dao.CdDataDicRefDao;
import com.wellsoft.pt.datadic.manager.dto.CdDataDicRefDto;
import com.wellsoft.pt.datadic.manager.entity.CdDataDicRefEntity;
import com.wellsoft.pt.datadic.manager.service.CdDataDicRefService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/6/11
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/6/11    chenq		2019/6/11		Create
 * </pre>
 */
@Service
public class CdDataDicRefServiceImpl extends
        AbstractJpaServiceImpl<CdDataDicRefEntity, CdDataDicRefDao, String> implements
        CdDataDicRefService {
    @Override
    @Transactional
    public void saveDataDicRef(CdDataDicRefDto dto) {
        CdDataDicRefEntity entity = new CdDataDicRefEntity();
        BeanUtils.copyProperties(dto, entity);
        List<CdDataDicRefEntity> exist = this.dao.listByEntity(entity);
        if (!exist.isEmpty()) {
            return;
        }
        this.dao.save(entity);
    }

    @Override
    @Transactional
    public void deleteDataDicRef(CdDataDicRefDto dto) {
        CdDataDicRefEntity entity = new CdDataDicRefEntity();
        BeanUtils.copyProperties(dto, entity);
        List<CdDataDicRefEntity> exist = this.dao.listByEntity(entity);
        if (exist.isEmpty()) {
            return;
        }
        this.dao.deleteByEntities(exist);
    }
}
