package com.wellsoft.pt.dms.facade.service.impl;

import com.google.common.collect.Lists;
import com.wellsoft.context.component.select2.Select2DataBean;
import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.pt.dms.bean.DmsDataLabelDto;
import com.wellsoft.pt.dms.entity.DataLabelRelaEntity;
import com.wellsoft.pt.dms.entity.DmsDataLabelEntity;
import com.wellsoft.pt.dms.entity.DmsDataLabelRelaEntity;
import com.wellsoft.pt.dms.facade.service.DmsDataLabelFacadeService;
import com.wellsoft.pt.dms.service.DmsDataLabelRelaService;
import com.wellsoft.pt.dms.service.DmsDataLabelService;
import com.wellsoft.pt.jpa.util.ClassUtils;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Description:
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
public class DmsDataLabelFacadeServiceImpl extends AbstractApiFacade implements
        DmsDataLabelFacadeService {


    @Autowired
    DmsDataLabelService dmsDataLabelService;

    @Autowired
    DmsDataLabelRelaService dmsDataLabelRelaService;


    @Override
    public Select2QueryData loadDataLabelRelaEntityData(Select2QueryInfo queryInfo) {
        Map<String, Class<?>> entityClasses = ClassUtils.getEntityClasses();
        Select2QueryData result = new Select2QueryData();
        Iterator iterator = entityClasses.keySet().iterator();

        while (iterator.hasNext()) {
            String simpleName = (String) iterator.next();
            Class clazz = entityClasses.get(simpleName);
            try {
                if (DataLabelRelaEntity.class.isAssignableFrom(clazz)) {

                    result.addResultData(
                            new Select2DataBean(((Class) entityClasses.get(simpleName)).getName(),
                                    StringUtils.capitalize(simpleName)));
                }
            } catch (Exception e) {
                logger.error("获取标签关联实体异常：", e);

            }

        }

        return result;
    }

    public Select2QueryData loadUserDataLabelDtosByModuleId(String moduleId) {
        List<DmsDataLabelEntity> dmsDataLabelEntities = dmsDataLabelService.queryByUserIdAndModuleId(
                SpringSecurityUtils.getCurrentUserId(),
                moduleId);
        Select2QueryData result = new Select2QueryData();
        for (DmsDataLabelEntity entity : dmsDataLabelEntities) {
            result.addResultData(
                    new Select2DataBean(entity.getUuid(),
                            String.format("<span class='%s' style='%s'></span><span>%s</span>",
                                    "glyphicon glyphicon-stop", "color:" + entity.getLabelColor(),
                                    entity.getLabelName())));
        }
        return result;
    }


    @Override
    @Transactional
    public void addLabelAndRelaData(DmsDataLabelDto labelDto, List<String> dataUuid) {
        DmsDataLabelEntity labelEntity = new DmsDataLabelEntity();
        BeanUtils.copyProperties(labelDto, labelEntity);
        labelEntity.setUserId(SpringSecurityUtils.getCurrentUserId());
        dmsDataLabelService.save(labelEntity);
        for (String duid : dataUuid) {
            DmsDataLabelRelaEntity dmsDataLabelRelaEntity = new DmsDataLabelRelaEntity();
            dmsDataLabelRelaEntity.setLabelUuid(labelEntity.getUuid());
            dmsDataLabelRelaEntity.setDataUuid(duid);
            dmsDataLabelRelaService.save(dmsDataLabelRelaEntity);
        }
    }

    @Override
    @Transactional
    public void addLabelAndRelaDataEntity(DmsDataLabelDto labelDto, List<String> dataUuid,
                                          String entityClassName) throws Exception {
        DmsDataLabelEntity labelEntity = new DmsDataLabelEntity();
        BeanUtils.copyProperties(labelDto, labelEntity);
        labelEntity.setUserId(SpringSecurityUtils.getCurrentUserId());
        dmsDataLabelService.save(labelEntity);
        for (String duid : dataUuid) {
            DataLabelRelaEntity dataLabelRelaEntity = (DataLabelRelaEntity) ClassUtils.getEntityClasses().get(
                    StringUtils.uncapitalize(entityClassName)).newInstance();
            dataLabelRelaEntity.setLabelUuid(labelEntity.getUuid());
            dataLabelRelaEntity.setDataUuid(duid);
            dmsDataLabelRelaService.saveRelaEntity(dataLabelRelaEntity);
        }
    }


    @Override
    @Transactional
    public void addLabelRelaData(List<String> dataUuid, String labelUuid) {
        for (String duid : dataUuid) {
            DmsDataLabelRelaEntity dmsDataLabelRelaEntity = new DmsDataLabelRelaEntity();
            dmsDataLabelRelaEntity.setLabelUuid(labelUuid);
            dmsDataLabelRelaEntity.setDataUuid(duid);
            dmsDataLabelRelaService.save(dmsDataLabelRelaEntity);
        }
    }

    @Override
    @Transactional
    public void addLabelRelaEntity(List<String> dataUuid, String labelUuid,
                                   String entityClassName) throws Exception {
        for (String duid : dataUuid) {
            DataLabelRelaEntity dataLabelRelaEntity = (DataLabelRelaEntity) ClassUtils.getEntityClasses().get(
                    StringUtils.uncapitalize(entityClassName)).newInstance();
            dataLabelRelaEntity.setLabelUuid(labelUuid);
            dataLabelRelaEntity.setDataUuid(duid);
            dmsDataLabelRelaService.deleteByDataUuidAndLabelUuid(duid, labelUuid);
            dmsDataLabelRelaService.saveRelaEntity(dataLabelRelaEntity);
        }
    }

    @Override
    public void removeAllLableOfData(List<String> dataUuid) {
        dmsDataLabelRelaService.deleteByDataUuids(dataUuid);
    }

    @Override
    public void removeLableOfData(String dataUuid, String labelUuid) {
        dmsDataLabelRelaService.deleteByDataUuidAndLabelUuid(dataUuid, labelUuid);
    }


    @Override
    public void removeLableOfDataByRelaUuids(List<String> relaUuids) {
        dmsDataLabelRelaService.deleteByUuids(relaUuids);
    }

    @Override
    public void removeLableOfDataByRelaUuidsAndRelaEntityClass(List<String> relaUuid,
                                                               String entityClassName) {
        dmsDataLabelRelaService.deleteByUuidsAndEntityClass(relaUuid, entityClassName);
    }


    @Override
    public List<DmsDataLabelDto> listLabelsOfData(String dataUuid) {
        List<DmsDataLabelRelaEntity> dmsDataLabelRelaEntities = dmsDataLabelRelaService.listByDataUuid(
                dataUuid);
        if (CollectionUtils.isNotEmpty(dmsDataLabelRelaEntities)) {
            List<DmsDataLabelDto> dmsDataLabelDtos = Lists.newArrayList();
            for (DmsDataLabelRelaEntity relaEntity : dmsDataLabelRelaEntities) {
                dmsDataLabelDtos.add(convertLableRela2LabelDto(relaEntity));
            }
            return dmsDataLabelDtos;
        }
        return Lists.newArrayList();
    }

    private DmsDataLabelDto convertLableRela2LabelDto(
            DataLabelRelaEntity relaEntity) {
        DmsDataLabelDto dto = new DmsDataLabelDto();
        DmsDataLabelEntity labelEntity = dmsDataLabelService.getOne(
                relaEntity.getLabelUuid());
        BeanUtils.copyProperties(labelEntity, dto);
        dto.setLabelRelaUuid(relaEntity.getUuid());
        return dto;
    }

    @Override
    public List<DmsDataLabelDto> listLabelByDataUuidAndEntityClass(String dataUuid,
                                                                   String entityClassName) {
        List<DmsDataLabelRelaEntity> dmsDataLabelRelaEntities = dmsDataLabelRelaService.listDataRelaEntities(
                dataUuid, entityClassName);
        if (CollectionUtils.isNotEmpty(dmsDataLabelRelaEntities)) {
            List<DmsDataLabelDto> dmsDataLabelDtos = Lists.newArrayList();
            for (DataLabelRelaEntity relaEntity : dmsDataLabelRelaEntities) {
                dmsDataLabelDtos.add(convertLableRela2LabelDto(relaEntity));
            }
            return dmsDataLabelDtos;
        }
        return Lists.newArrayList();
    }

}
