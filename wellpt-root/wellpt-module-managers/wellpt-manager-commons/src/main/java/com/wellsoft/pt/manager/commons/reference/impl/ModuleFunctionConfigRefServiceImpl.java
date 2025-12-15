package com.wellsoft.pt.manager.commons.reference.impl;

import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.manager.commons.reference.ModuleFunctionConfigRefService;
import com.wellsoft.pt.manager.commons.reference.dao.ModuleFunctionConfigRefDaoImpl;
import com.wellsoft.pt.manager.commons.reference.entity.ModuleFunctionConfigRefEntity;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/6/6
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/6/6    chenq		2019/6/6		Create
 * </pre>
 */
@Service
public class ModuleFunctionConfigRefServiceImpl extends
        AbstractJpaServiceImpl<ModuleFunctionConfigRefEntity, ModuleFunctionConfigRefDaoImpl, String> implements
        ModuleFunctionConfigRefService {
    @Override
    public ModuleFunctionConfigRefEntity getByEntity(ModuleFunctionConfigRefEntity entity) {
        List<ModuleFunctionConfigRefEntity> list = this.dao.listByEntity(entity);
        return list.isEmpty() ? null : list.get(0);
    }
}
