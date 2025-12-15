package com.wellsoft.pt.basicdata.business.facade.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.wellsoft.pt.basicdata.business.dto.BusinessApplicationConfigDto;
import com.wellsoft.pt.basicdata.business.dto.BusinessCategoryDto;
import com.wellsoft.pt.basicdata.business.dto.BusinessCategoryOrgDto;
import com.wellsoft.pt.basicdata.business.dto.BusinessRoleDto;
import com.wellsoft.pt.basicdata.business.entity.BusinessCategoryEntity;
import com.wellsoft.pt.basicdata.business.entity.BusinessCategoryOrgEntity;
import com.wellsoft.pt.basicdata.business.entity.BusinessRoleOrgUserEntity;
import com.wellsoft.pt.basicdata.business.facade.service.BusinessFacadeService;
import com.wellsoft.pt.basicdata.business.service.*;
import com.wellsoft.pt.jpa.util.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class BusinessFacadeServiceImpl implements BusinessFacadeService {

    @Autowired
    private BusinessCategoryService businessCategoryService;

    @Autowired
    private BusinessRoleService businessRoleService;

    @Autowired
    private BusinessRoleOrgUserService businessRoleOrgUserService;

    @Autowired
    private BusinessApplicationService businessApplicationService;

    @Autowired
    private BusinessCategoryOrgService businessCategoryOrgService;

    public List<BusinessCategoryDto> getAllBusinessCategory() {
        List<BusinessCategoryDto> dtos = new ArrayList<BusinessCategoryDto>();
        List<BusinessCategoryEntity> entitys = businessCategoryService.listAll();
        for (BusinessCategoryEntity entity : entitys) {
            BusinessCategoryDto dto = new BusinessCategoryDto();
            BeanUtils.copyProperties(entity, dto);
            dtos.add(dto);
        }
        return dtos;
    }

    public List<BusinessCategoryDto> getBusinessCategoryListBySystemUuid() {
        List<BusinessCategoryDto> dtos = new ArrayList<BusinessCategoryDto>();
        BusinessCategoryEntity queryEntity = new BusinessCategoryEntity();
        //queryEntity.setSystemUnitId(SpringSecurityUtils.getCurrentUserUnitId());
        List<BusinessCategoryEntity> entitys = businessCategoryService.listByEntity(queryEntity);
        for (BusinessCategoryEntity entity : entitys) {
            BusinessCategoryDto dto = new BusinessCategoryDto();
            BeanUtils.copyProperties(entity, dto);
            dtos.add(dto);
        }
        return dtos;
    }

    public List<BusinessRoleDto> getBusinessRoleByCategoryUuid(String categoryUuid) {
        return businessRoleService.findByCategoryUuid(categoryUuid);
    }

    public List<BusinessRoleDto> getBusinessRoleByCategoryId(String categoryId) {
        return businessRoleService.findByCategoryId(categoryId);
    }

    public Set<String> getUserByOrgUuidAndRoleUuid(String categoryUuid, String orgId, String roleUuid) {
        // BusinessCategoryOrgEntity orgEntity = businessCategoryOrgService.get(orgId);
        List<BusinessRoleOrgUserEntity> entitys = businessRoleOrgUserService.findByOrgUuidAndRoleUuid(categoryUuid,
                orgId, roleUuid);
        Set<String> userIds = new HashSet<String>();
        for (BusinessRoleOrgUserEntity entity : entitys) {
            if (StringUtils.isNotBlank(entity.getUsers())) {
                userIds.addAll(Lists.newArrayList(entity.getUsers().split(";")));
            }
        }
        return userIds;
    }

    private Set<String> explainBusinessCategoryOrgId(String orgId) {
        if (StringUtils.isBlank(orgId)) {
            throw new RuntimeException("[业务单位]参数必填");
        }
        BusinessCategoryOrgEntity orgEntity = businessCategoryOrgService.getOne(orgId);
        if (orgEntity == null) {
            throw new RuntimeException("[业务单位=" + orgId + "]无法找到");
        }
        Set<String> ids = Sets.newHashSet();
        if (BusinessCategoryOrgEntity.TYPE_1.equals(orgEntity.getType())) {//业务单位类型的通讯录节点
            // ids.add(orgEntity.getDept());
            ids.addAll(cascadeAddChildBusinessCateOrg(orgEntity.getUuid()));
        } else if (BusinessCategoryOrgEntity.TYPE_2.equals(orgEntity.getType())) {
            //业务单位是分类：解析该分类下的所有单位节点
            ids.addAll(cascadeAddChildBusinessCateOrg(orgId));
        }
        return ids;

    }

    private Set<String> cascadeAddChildBusinessCateOrg(String uuid) {
        Set<String> ids = Sets.newHashSet();
        List<BusinessCategoryOrgEntity> orgEntities = businessCategoryOrgService.listByParentUuid(uuid);
        if (CollectionUtils.isNotEmpty(orgEntities)) {
            for (BusinessCategoryOrgEntity org : orgEntities) {
                ids.addAll(explainBusinessCategoryOrgId(org.getUuid()));
            }
        }
        return ids;
    }

    public List<BusinessApplicationConfigDto> getBusinessApplicationConfig(String uuid) {
        return businessApplicationService.findBusinessApplicationConfig(uuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.business.facade.service.BusinessFacadeService#getBusinessApplicationConfigByCategoryUuid(java.lang.String)
     */
    @Override
    public List<BusinessApplicationConfigDto> getBusinessApplicationConfigByCategoryUuid(String categoryUuid) {
        return businessApplicationService.findBusinessApplicationConfigByCategoryUuid(categoryUuid);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.business.facade.service.BusinessFacadeService#getBusinessCategoryOrgByCategoryUuidAndId(java.lang.String, java.lang.String)
     */
    @Override
    public BusinessCategoryOrgDto getBusinessCategoryOrgByCategoryUuidAndId(String categoryUuid, String orgId) {
        BusinessCategoryOrgDto businessCategoryOrgDto = null;
        BusinessCategoryOrgEntity businessCategoryOrgEntity = businessCategoryOrgService.getOne(orgId);
        if (businessCategoryOrgEntity == null) {
            businessCategoryOrgEntity = businessCategoryOrgService.getByCategoryUuidAndDeptId(categoryUuid, orgId);
        }
        if (businessCategoryOrgEntity != null) {
            businessCategoryOrgDto = new BusinessCategoryOrgDto();
            BeanUtils.copyProperties(businessCategoryOrgEntity, businessCategoryOrgDto);
        }
        return businessCategoryOrgDto;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.business.facade.service.BusinessFacadeService#getBusinessCategoryOrgByUuid(java.lang.String)
     */
    @Override
    public BusinessCategoryOrgDto getBusinessCategoryOrgByUuid(String uuid) {
        BusinessCategoryOrgDto businessCategoryOrgDto = null;
        BusinessCategoryOrgEntity businessCategoryOrgEntity = businessCategoryOrgService.getOne(uuid);
        if (businessCategoryOrgEntity != null) {
            businessCategoryOrgDto = new BusinessCategoryOrgDto();
            BeanUtils.copyProperties(businessCategoryOrgEntity, businessCategoryOrgDto);
        }
        return businessCategoryOrgDto;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.business.facade.service.BusinessFacadeService#getBusinessCategoryOrgsByParentUuid(java.lang.String)
     */
    @Override
    public List<BusinessCategoryOrgDto> getBusinessCategoryOrgsByParentUuid(String uuid) {
        List<BusinessCategoryOrgEntity> categoryOrgEntities = Lists.newArrayList();
        recursListByParentUuid(uuid, categoryOrgEntities);
        return BeanUtils.copyCollection(categoryOrgEntities, BusinessCategoryOrgDto.class);
    }

    /**
     * @param parentUuid
     * @param categoryOrgEntities
     */
    private void recursListByParentUuid(String parentUuid, List<BusinessCategoryOrgEntity> categoryOrgEntities) {
        List<BusinessCategoryOrgEntity> orgEntities = businessCategoryOrgService.listByParentUuid(parentUuid);
        for (BusinessCategoryOrgEntity businessCategoryOrgEntity : orgEntities) {
            if (BusinessCategoryOrgEntity.TYPE_1.equals(businessCategoryOrgEntity.getType())) {
                categoryOrgEntities.add(businessCategoryOrgEntity);
            } else {
                recursListByParentUuid(businessCategoryOrgEntity.getUuid(), categoryOrgEntities);
            }
        }
    }

    /**
     * 获取业务通讯录实体
     *
     * @param id
     * @return
     */
    @Override
    public BusinessCategoryOrgDto getBusinessById(String id) {
        BusinessCategoryOrgDto dto = new BusinessCategoryOrgDto();
        BusinessCategoryOrgEntity entity = businessCategoryOrgService.getBusinessById(id);
        if (entity != null) {
            BeanUtils.copyProperties(entity, dto);
        } else {
            return null;
        }
        return dto;
    }

    @Override
    public boolean isMemberOf(String userId, Set<String> externalIds) {
        return businessRoleOrgUserService.isMemberOf(userId, externalIds);
    }

    @Override
    public List<BusinessCategoryOrgEntity> getBusinessByIds(List<String> ids) {
        return businessCategoryOrgService.getBusinessByIds(ids);
    }

}
