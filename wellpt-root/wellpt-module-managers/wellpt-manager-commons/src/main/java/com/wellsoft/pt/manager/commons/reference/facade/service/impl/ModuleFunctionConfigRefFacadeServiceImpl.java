package com.wellsoft.pt.manager.commons.reference.facade.service.impl;

import com.google.common.collect.Lists;
import com.wellsoft.pt.app.facade.api.AppFunctionFacade;
import com.wellsoft.pt.app.service.AppProductIntegrationService;
import com.wellsoft.pt.jpa.util.ClassUtils;
import com.wellsoft.pt.manager.commons.reference.ModuleFunctionConfigRefService;
import com.wellsoft.pt.manager.commons.reference.dto.ModuleFunctionConfigRefDto;
import com.wellsoft.pt.manager.commons.reference.entity.ModuleFunctionConfigRefEntity;
import com.wellsoft.pt.manager.commons.reference.facade.service.ModuleFunctionConfigRefFacadeService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
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
public class ModuleFunctionConfigRefFacadeServiceImpl implements
        ModuleFunctionConfigRefFacadeService {

    @Resource
    ModuleFunctionConfigRefService moduleFunctionConfigRefService;

    @Resource
    AppFunctionFacade appFunctionFacade;

    @Resource
    AppProductIntegrationService appProductIntegrationService;


    @Override
    @Transactional
    public void saveModuleFunctionConfigRef(
            List<ModuleFunctionConfigRefDto> dtoList) throws Exception {
        if (CollectionUtils.isNotEmpty(dtoList)) {
            List<ModuleFunctionConfigRefEntity> entities = moduleFunctionConfigRefEntityConvertion(
                    dtoList);
            moduleFunctionConfigRefService.saveAll(entities);
            if (StringUtils.isNotBlank(dtoList.get(0).getFunctionType())) {
                //引用配置添加到集成树下
                for (ModuleFunctionConfigRefEntity entity : entities) {
                    appFunctionFacade.synchronizeFunction2ModuleProductIntegrate(entity.getRefUuid(),
                            dtoList.get(0).getFunctionType(),
                            entity.getModuleId(), false);
                }
            }
        }

    }

    @Override
    @Transactional
    public void deleteModuleFunctionConfigRef(
            List<ModuleFunctionConfigRefDto> dtoList) throws Exception {
        List<ModuleFunctionConfigRefEntity> refEntities = moduleFunctionConfigRefEntityConvertion(dtoList);
        moduleFunctionConfigRefService.deleteByEntities(refEntities);
        for (ModuleFunctionConfigRefEntity ref : refEntities) {
            //删除产品集成树下的功能
            appProductIntegrationService.removeFunctionUnderParentDataId(ref.getModuleId(), ref.getRefUuid());
        }

    }

    private List<ModuleFunctionConfigRefEntity> moduleFunctionConfigRefEntityConvertion(
            List<ModuleFunctionConfigRefDto> dtoList) throws Exception {
        List<ModuleFunctionConfigRefEntity> refEntities = Lists.newArrayList();
        for (ModuleFunctionConfigRefDto dto : dtoList) {
            ModuleFunctionConfigRefEntity entity = (ModuleFunctionConfigRefEntity) ClassUtils.getEntityClasses().get(
                    StringUtils.uncapitalize(
                            dto.getEntityClass())).newInstance();
            BeanUtils.copyProperties(dto, entity);
            ModuleFunctionConfigRefEntity exist = moduleFunctionConfigRefService.getByEntity(
                    entity);
            if (exist != null) {
                refEntities.add(exist);
                continue;
            }
            refEntities.add(entity);
        }
        return refEntities;

    }
}
