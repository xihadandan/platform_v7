package com.wellsoft.pt.unit.facade.service.impl;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.multi.org.bean.OrgUserVo;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgUserService;
import com.wellsoft.pt.org.entity.User;
import com.wellsoft.pt.org.service.UserService;
import com.wellsoft.pt.unit.bean.BusinessManage;
import com.wellsoft.pt.unit.entity.BusinessType;
import com.wellsoft.pt.unit.entity.BusinessUnitTree;
import com.wellsoft.pt.unit.entity.CommonUnit;
import com.wellsoft.pt.unit.entity.CommonUnitTree;
import com.wellsoft.pt.unit.facade.service.UnitApiFacade;
import com.wellsoft.pt.unit.service.BusinessTypeService;
import com.wellsoft.pt.unit.service.BusinessUnitTreeService;
import com.wellsoft.pt.unit.service.CommonUnitService;
import com.wellsoft.pt.unit.service.CommonUnitTreeService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class UnitApiFacadeServiceImpl extends AbstractApiFacade implements UnitApiFacade {

    @Autowired
    private BusinessUnitTreeService businessUnitTreeService;
    @Autowired
    private CommonUnitService commonUnitService;
    @Autowired
    private UserService userService;
    @Autowired
    private MultiOrgUserService multiOrgUserService;

    @Autowired
    private BusinessTypeService businessTypeService;

    @Autowired
    private CommonUnitTreeService commonUnitTreeService;

    public List<BusinessManage> getAllBusinessManage(String businessTypeId, String userId) {
        return businessUnitTreeService.getBusinessManage(businessTypeId, userId);
    }

    /**
     * 2根据业务类别ID获取业务单位树
     *
     * @param businessTypeId 业务类别ID
     * @return
     */
    public List<TreeNode> getBusinessUnitTreeByBusinessTypeId(String businessTypeId) {
        return businessUnitTreeService.getAsTree(businessTypeId);
    }

    /**
     * 根据业务类别ID和业务单位ID获取执行人员列表
     *
     * @param businessTypeId 业务类别ID
     * @param commonUnitId   单位ID
     * @param type           1表示获取发送人，2表示获取接收人
     * @return
     */
    public List<String> getBusinessManageUserIds(String businessTypeId, String commonUnitId,
                                                 int type) {
        return this.businessUnitTreeService.getBusinessUnitUserIds(businessTypeId, commonUnitId,
                type);
    }

    /**
     * 根据业务类别ID和业务单位ID获取指定业务角色的人员ID
     *
     * @param businessTypeId
     * @param commonUnitId
     * @param bizRole
     * @return
     */
    public List<String> getBusinessUnitUserIds(String businessTypeId, String commonUnitId,
                                               String bizRole) {
        return this.businessUnitTreeService.getBusinessUnitUserIds(businessTypeId, commonUnitId,
                bizRole);
    }

    /**
     * 5根据当前登陆用户获取所属单位
     *
     * @param userId 用户ID
     * @return
     */
    public CommonUnit getCommonUnitByUserId(String userId) {
        return this.commonUnitService.getByCurrentUserId(userId);
    }

    /**
     * 根据用户ID获取当前用户所在租户的单位列表PS:一个租户下面有多个单位哦
     *
     * @param userId 用户ID
     * @return
     */
    public List<CommonUnit> getByUserId(String userId) {
        return this.commonUnitService.getByUserId(userId);
    }

    /**
     * 6根据业务类别ID和业务单位UUID获取业务分支树
     *
     * @param businessTypeId 业务类别ID
     * @param commonUnitId   业务单位ID
     * @return
     */
    public List<TreeNode> getBusinessUnitTree(String businessTypeId, String commonUnitId) {
        return this.businessUnitTreeService.getAsTreeBranch(businessTypeId, commonUnitId);
    }

    /**
     * 根据单位ID获取单位
     *
     * @param id 单位ID
     * @return
     */
    public CommonUnit getCommonUnitById(String id) {
        CommonUnit commonUnit = commonUnitService.getById(id);
        if (commonUnit == null) {
            commonUnit = commonUnitService.getByUnitId(id);
        }
        return commonUnit;
    }

    @Deprecated
    public List<CommonUnit> getCommonUnitListByIds(String ids) {
        return getCommonUnitsByIds(ids);
    }

    /**
     * 根据多个单位ID获取单位列表
     *
     * @param ids
     * @return
     */
    public List<CommonUnit> getCommonUnitsByIds(String ids) {
        List<CommonUnit> commonUnitList = new ArrayList<CommonUnit>();
        if (StringUtils.isNotBlank(ids)) {
            String[] idArray = ids.split(";");
            for (String id : idArray) {
                CommonUnit commonUnit = commonUnitService.getById(id);
                // 新增业务ID后保证以前的业务逻辑能用
                if (commonUnit == null) {
                    commonUnit = commonUnitService.getByUnitId(id);
                }
                if (commonUnit != null)
                    commonUnitList.add(commonUnit);
            }
        }
        return commonUnitList;
    }

    /**
     * 获取所有单位
     *
     * @return
     */
    public List<CommonUnit> getAllCommonUnits() {
        return BeanUtils.convertCollection(commonUnitService.getAllCommonUnits(), CommonUnit.class);
    }

    /**
     * 8根据业务类别ID获取业务单位管理员
     *
     * @param businessTypeId 业务类别ID
     * @param unitId
     * @return
     */
    public List<User> getUnitManagerById(String businessTypeId) {
        BusinessType businessType = this.businessTypeService.getBusinessTypeById(businessTypeId);
        List<User> userList = new ArrayList<User>();
        if (businessType != null) {
            String userIds = businessType.getUnitManagerUserId();
            if (StringUtils.isNotBlank(userIds)) {
                String[] userIdArray = userIds.split(";");
                List<String> ids = new ArrayList<String>();
                for (String userId : userIdArray) {
                    ids.add(userId);
                }
                userList = this.userService.getByIds(ids);
            }
        }
        return userList;
    }

    /**
     * 9根据业务类别ID和单位ID获取业务单位负责人
     *
     * @param businessTypeId
     * @param unitId
     * @return
     */
    public List<OrgUserVo> getBusinessUnitManagerById(String businessTypeId, String unitId) {
        List<BusinessUnitTree> list = this.businessUnitTreeService.getBusinessUnitManagerById(
                businessTypeId, unitId);
        List<OrgUserVo> userList = new ArrayList<OrgUserVo>();
        if (list != null) {
            for (BusinessUnitTree unitTree : list) {
                String userIds = unitTree.getBusinessManagerUserId();
                if (StringUtils.isNotBlank(userIds)) {
                    String[] userIdArray = userIds.split(";");
                    List<String> ids = new ArrayList<String>();
                    for (String userId : userIdArray) {
                        ids.add(userId);
                    }
                    userList = this.multiOrgUserService.queryUserListByIds(ids);
                }
            }
        }
        return userList;
    }

    @Deprecated
    public List<BusinessType> getBusinessTypeList() {
        return getBusinessTypes();
    }

    /**
     * 10获取业务类别列表
     *
     * @return
     */
    public List<BusinessType> getBusinessTypes() {
        return this.businessTypeService.getAllBusinessTypes();
    }

    /**
     * 11根据业务类别ID，业务单位ID和userId获取业务管理对象
     *
     * @param businessTypeId
     * @param unitId
     * @param userId
     * @return
     */
    public BusinessManage getBusinessManage(String businessTypeId, String unitId, String userId) {
        return this.businessUnitTreeService.getBusinessManage(businessTypeId, unitId, userId);
    }

    /**
     * 根据业务类别ID获取业务管理单位
     *
     * @param businessTypeId
     * @return
     */
    public CommonUnit getCommonUnitByBusinessTypeId(String businessTypeId) {
        return this.commonUnitService.getByBusinessTypeId(businessTypeId);
    }

    /**
     * 根据业务类型ID及用户ID获取用户所代码的单位列表
     *
     * @param businessTypeId
     * @param userId
     * @return
     */
    public List<CommonUnit> getCommonUnitsByBusinessTypeIdAndUserId(String businessTypeId,
                                                                    String userId) {
        return this.businessUnitTreeService.getCommonUnitsByBusinessTypeIdAndUserId(businessTypeId,
                userId);
    }

    /**
     * 根据单位ID及业务类型获取下一级子单位
     *
     * @param parentUnitId
     * @param businessTypeId
     * @return
     */
    public List<CommonUnit> getCommonUnitsByParentIdAndBusinessTypeId(String parentUnitId,
                                                                      String businessTypeId) {
        return this.commonUnitTreeService.getCommonUnitsByParentIdAndBusinessTypeId(parentUnitId,
                businessTypeId);
    }

    /**
     * 获取指定业务的所有单位
     *
     * @param userId 用户ID
     * @return
     */
    public List<CommonUnit> getCommonUnitsByBusinessTypeId(String businessTypeId) {
        return this.commonUnitTreeService.getCommonUnitsByBusinessTypeId(businessTypeId);
    }

    /**
     * 获取指定业务负责人userId
     *
     * @param businessTypeId
     * @return
     */
    public List<String> getBusinessManagerByBusinessTypeId(String businessTypeId) {
        List<BusinessUnitTree> list = this.businessUnitTreeService.getBusinessUnitManagerById(
                businessTypeId);
        List<String> userIdList = new ArrayList<String>();
        if (list != null) {
            for (BusinessUnitTree unitTree : list) {
                String userIds = unitTree.getBusinessManagerUserId();
                if (StringUtils.isNotBlank(userIds)) {
                    String[] userIdArray = userIds.split(";");
                    userIdList.add(userIdArray[0]);
                }
            }
        }
        return userIdList;
    }

    /**
     * 通过单位名称模糊查询所有单位
     *
     * @param unitNameKey
     * @return 所有单位
     */
    public List<CommonUnit> getCommonUnitsByBlurUnitName(String unitNameKey) {
        return this.commonUnitService.getCommonUnitsByBlurUnitName(unitNameKey);
    }

    /**
     * 通过单位ID获取单位所在的租户ID
     *
     * @param unitId
     * @return
     */
    public String getTenantIdByCommonUnitId(String unitId) {
        CommonUnit commonUnit = commonUnitService.getById(unitId);
        if (commonUnit == null) {
            commonUnit = commonUnitService.getByUnitId(unitId);
            if (commonUnit == null) {
                return null;
            }
        }
        return commonUnit.getTenantId();
    }

    /**
     * 通过单位UNIT_ID获取单位所在的租户ID
     *
     * @param unitId
     * @return
     */
    public String getTenantIdByCommonUnitUnitId(String unitId) {
        CommonUnit commonUnit = commonUnitService.getByUnitId(unitId);
        if (commonUnit == null) {
            return null;
        }
        return commonUnit.getTenantId();
    }

    public List<CommonUnit> getCommonUnitByTenantId(String tenantId) {
        return commonUnitService.getCommonUnitByTenantId(tenantId);
    }

    public CommonUnitTree getCommonUnitTreeByUuid(String uuid) {
        return commonUnitService.getCommonUnitTreeByUuid(uuid);
    }
}
