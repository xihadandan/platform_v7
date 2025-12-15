package com.wellsoft.pt.basicdata.business.facade.service;

import com.wellsoft.pt.basicdata.business.dto.BusinessApplicationConfigDto;
import com.wellsoft.pt.basicdata.business.dto.BusinessCategoryDto;
import com.wellsoft.pt.basicdata.business.dto.BusinessCategoryOrgDto;
import com.wellsoft.pt.basicdata.business.dto.BusinessRoleDto;
import com.wellsoft.pt.basicdata.business.entity.BusinessCategoryOrgEntity;

import java.util.List;
import java.util.Set;

public interface BusinessFacadeService {

    /**
     * 获取业务类型列表
     *
     * @return
     */
    public List<BusinessCategoryDto> getAllBusinessCategory();

    /**
     * 根据当前用户系统单位获取业务类型列表
     *
     * @return
     */
    public List<BusinessCategoryDto> getBusinessCategoryListBySystemUuid();

    /**
     * 根据业务类别UUID获取业务角色列表
     *
     * @param categoryUuid
     * @return
     */
    public List<BusinessRoleDto> getBusinessRoleByCategoryUuid(String categoryUuid);

    /**
     * 根据业务类别ID获取业务角色列表
     *
     * @param categoryId
     * @return
     */
    public List<BusinessRoleDto> getBusinessRoleByCategoryId(String categoryId);

    /**
     * 通过业务类别、业务单位(业务通讯录选择的结点ID)、业务角色获取对应的人员(成员)
     *
     * @param categoryUuid
     * @param orgId
     * @param roleUuid
     * @return
     */
    public Set<String> getUserByOrgUuidAndRoleUuid(String categoryUuid, String orgId, String roleUuid);

    /**
     * 根据应用UUID获取对应的应用配置信息列表
     *
     * @param uuid
     * @return dictType：应用于(字典表_父节点_type), dictCode：应用于(字典表_code), formUuid：表单uuid,ruleUuid：规则uuid
     */
    public List<BusinessApplicationConfigDto> getBusinessApplicationConfig(String uuid);

    /**
     * 根据业务类别UUID获取对应的应用配置信息列表
     *
     * @param uuid
     * @return dictType：应用于(字典表_父节点_type), dictCode：应用于(字典表_code), formUuid：表单uuid,ruleUuid：规则uuid
     */
    public List<BusinessApplicationConfigDto> getBusinessApplicationConfigByCategoryUuid(String categoryUuid);

    /**
     * 通过业务类别、业务单位(业务通讯录选择的结点ID)，获取业务结点
     *
     * @param categoryUuid
     * @param orgId
     * @return
     */
    public BusinessCategoryOrgDto getBusinessCategoryOrgByCategoryUuidAndId(String categoryUuid, String orgId);

    /**
     * 根据业务结点UUID，获取业务结点
     *
     * @param uuid
     * @return
     */
    public BusinessCategoryOrgDto getBusinessCategoryOrgByUuid(String uuid);

    /**
     * 根据业务结点UUID，获取业务结点
     *
     * @param uuid
     * @return
     */
    public List<BusinessCategoryOrgDto> getBusinessCategoryOrgsByParentUuid(String uuid);

    /**
     * 获取业务通讯录实体
     *
     * @param id
     * @return
     */
    public abstract BusinessCategoryOrgDto getBusinessById(String id);

    boolean isMemberOf(String userId, Set<String> externalIds);

    List<BusinessCategoryOrgEntity> getBusinessByIds(List<String> ids);
}
