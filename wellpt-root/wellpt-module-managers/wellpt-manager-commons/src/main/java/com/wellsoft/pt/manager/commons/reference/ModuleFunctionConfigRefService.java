package com.wellsoft.pt.manager.commons.reference;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.manager.commons.reference.dao.ModuleFunctionConfigRefDaoImpl;
import com.wellsoft.pt.manager.commons.reference.entity.ModuleFunctionConfigRefEntity;

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
public interface ModuleFunctionConfigRefService extends
        JpaService<ModuleFunctionConfigRefEntity, ModuleFunctionConfigRefDaoImpl, String> {
    ModuleFunctionConfigRefEntity getByEntity(ModuleFunctionConfigRefEntity entity);
}
