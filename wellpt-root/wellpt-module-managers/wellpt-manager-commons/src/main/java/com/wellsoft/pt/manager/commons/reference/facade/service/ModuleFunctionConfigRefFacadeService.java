package com.wellsoft.pt.manager.commons.reference.facade.service;

import com.wellsoft.context.service.Facade;
import com.wellsoft.pt.manager.commons.reference.dto.ModuleFunctionConfigRefDto;

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
public interface ModuleFunctionConfigRefFacadeService extends Facade {


    void saveModuleFunctionConfigRef(List<ModuleFunctionConfigRefDto> dtoList) throws Exception;

    void deleteModuleFunctionConfigRef(List<ModuleFunctionConfigRefDto> dtoList) throws Exception;

}
