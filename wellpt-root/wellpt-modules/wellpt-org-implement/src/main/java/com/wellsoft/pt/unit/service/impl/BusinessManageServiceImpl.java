package com.wellsoft.pt.unit.service.impl;

import com.wellsoft.context.jdbc.support.PropertyFilter;
import com.wellsoft.context.jdbc.support.QueryInfo;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.org.dao.UserDao;
import com.wellsoft.pt.org.entity.User;
import com.wellsoft.pt.security.audit.constant.RoleConstants;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.facade.service.SecurityApiFacade;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.unit.bean.BusinessUnitTreeBean;
import com.wellsoft.pt.unit.bean.BusinessUnitTreeRoleBean;
import com.wellsoft.pt.unit.dao.BusinessTypeDao;
import com.wellsoft.pt.unit.dao.BusinessUnitTreeDao;
import com.wellsoft.pt.unit.entity.BusinessType;
import com.wellsoft.pt.unit.entity.BusinessUnitTree;
import com.wellsoft.pt.unit.entity.BusinessUnitTreeRole;
import com.wellsoft.pt.unit.entity.CommonUnit;
import com.wellsoft.pt.unit.service.BusinessManageService;
import com.wellsoft.pt.unit.service.BusinessUnitTreeRoleService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Description: BusinessManageServiceImpl.java
 *
 * @author liuzq
 * @date 2013-11-18
 */
@Service
@Transactional
public class BusinessManageServiceImpl extends BaseServiceImpl implements BusinessManageService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private BusinessTypeDao businessTypeDao;

    @Autowired
    private BusinessUnitTreeDao businessUnitTreeDao;

    @Autowired
    private BusinessUnitTreeRoleService businessUnitTreeRoleService;

    @Autowired
    private SecurityApiFacade securityApiFacade;

    @Override
    public List<BusinessUnitTreeBean> query(QueryInfo queryInfo) {
        Map<String, Object> values = PropertyFilter.convertToMap(queryInfo.getPropertyFilters());
        // 获取当前登录用户
        UserDetails userDetails = SpringSecurityUtils.getCurrentUser();

        if (userDetails == null)
            return null;

        // 过滤数据权限
        if (!securityApiFacade.hasRole(userDetails.getUserId(), RoleConstants.ROLE_ADMIN)) {
            values.put("userId", userDetails.getUserId());
        }
        values.put("orderBy", StringUtils.replace(queryInfo.getOrderBy(), "businessTypeName", "name"));

        List<BusinessUnitTreeBean> beanList = new ArrayList<BusinessUnitTreeBean>();

        List<BusinessUnitTree> unitResult = businessTypeDao.namedQuery("businessUnitTreeQuery", values,
                BusinessUnitTree.class, queryInfo.getPagingInfo());
        for (BusinessUnitTree unitTree : unitResult) {
            BusinessUnitTreeBean bean = new BusinessUnitTreeBean();
            bean.setUuid(unitTree.getUuid());
            // 业务类别
            BusinessType type = unitTree.getBusinessType();
            if (type != null) {
                bean.setBusinessTypeName(type.getName());
                bean.setBusinessTypeUuid(type.getUuid());

                CommonUnit unit = unitTree.getUnit();
                if (unit != null) {
                    bean.setUnitId(unit.getId());
                    bean.setUnitName(unit.getName());
                }
                // 单位内通讯录管理员
                bean.setUnitManagerUserName(type.getUnitManagerUserName());
                bean.setUnitManagerUserId(type.getUnitManagerUserId());
            }

            // 单位内业务负责人
            String businessManagerUserId = unitTree.getBusinessManagerUserId();
            if (StringUtils.isNotBlank(businessManagerUserId)) {
                String[] businessManagerUserIds = businessManagerUserId.split(";");
                StringBuilder businessManagerUserName = new StringBuilder();
                for (String userId : businessManagerUserIds) {
                    User user = userDao.getById(userId);
                    if (user != null) {
                        businessManagerUserName.append(user.getUserName()).append(";");
                    }
                }
                if (businessManagerUserName.indexOf(";") > 0) {
                    businessManagerUserName = businessManagerUserName.deleteCharAt(businessManagerUserName
                            .lastIndexOf(";"));
                }
                bean.setBusinessManagerUserName(businessManagerUserName.toString());
                bean.setBusinessManagerUserId(businessManagerUserId);
            }

            // 收发业务具体发送人员
            String businessSenderId = unitTree.getBusinessSenderId();
            if (StringUtils.isNotBlank(businessSenderId)) {
                String[] businessSenderIds = businessSenderId.split(";");
                StringBuilder businessUserName = new StringBuilder();
                for (String userId : businessSenderIds) {
                    User user = userDao.getById(userId);
                    if (user != null) {
                        businessUserName.append(user.getUserName()).append(";");
                    }
                }
                if (businessUserName.indexOf(";") > 0) {
                    businessUserName = businessUserName.deleteCharAt(businessUserName.lastIndexOf(";"));
                }
                bean.setBusinessSenderName(businessUserName.toString());
                bean.setBusinessSenderId(businessSenderId);
            }
            String businessReceiverId = unitTree.getBusinessReceiverId();
            if (StringUtils.isNotBlank(businessReceiverId)) {
                String[] businessReceiverIds = businessReceiverId.split(";");
                StringBuilder businessReceiverName = new StringBuilder();
                for (String userId : businessReceiverIds) {
                    User user = userDao.getById(userId);
                    if (user != null) {
                        businessReceiverName.append(user.getUserName()).append(";");
                    }
                }
                if (businessReceiverName.indexOf(";") > 0) {
                    businessReceiverName = businessReceiverName.deleteCharAt(businessReceiverName.lastIndexOf(";"));
                }
                bean.setBusinessReceiverName(businessReceiverName.toString());
                bean.setBusinessReceiverId(businessReceiverId);
            }

            beanList.add(bean);
        }
        return beanList;
    }

    @Override
    public BusinessUnitTreeBean getBusinessUnitTreeBean(String uuid) {
        BusinessUnitTreeBean bean = new BusinessUnitTreeBean();

        BusinessUnitTree unitTree = this.businessUnitTreeDao.get(uuid);

        BeanUtils.copyProperties(unitTree, bean);

        // 单位内业务负责人
        String businessManagerUserId = unitTree.getBusinessManagerUserId();
        if (StringUtils.isNotBlank(businessManagerUserId)) {
            String[] businessManagerUserIds = businessManagerUserId.split(";");
            StringBuilder businessManagerUserName = new StringBuilder();
            for (String userId : businessManagerUserIds) {
                User user = userDao.getById(userId);
                if (user != null) {
                    businessManagerUserName.append(user.getUserName()).append(";");
                }
            }
            if (businessManagerUserName.indexOf(";") > 0) {
                businessManagerUserName = businessManagerUserName
                        .deleteCharAt(businessManagerUserName.lastIndexOf(";"));
            }
            bean.setBusinessManagerUserName(businessManagerUserName.toString());
            bean.setBusinessManagerUserId(businessManagerUserId);
        }

        // 收发业务具体发送人员
        String businessSenderId = unitTree.getBusinessSenderId();
        if (StringUtils.isNotBlank(businessSenderId)) {
            String[] businessSenderIds = businessSenderId.split(";");
            StringBuilder businessUserName = new StringBuilder();
            for (String userId : businessSenderIds) {
                User user = userDao.getById(userId);
                if (user != null) {
                    businessUserName.append(user.getUserName()).append(";");
                }
            }
            if (businessUserName.indexOf(";") > 0) {
                businessUserName = businessUserName.deleteCharAt(businessUserName.lastIndexOf(";"));
            }
            bean.setBusinessSenderName(businessUserName.toString());
            bean.setBusinessSenderId(businessSenderId);
        }
        String businessReceiverId = unitTree.getBusinessReceiverId();
        if (StringUtils.isNotBlank(businessReceiverId)) {
            String[] businessReceiverIds = businessReceiverId.split(";");
            StringBuilder businessReceiverName = new StringBuilder();
            for (String userId : businessReceiverIds) {
                User user = userDao.getById(userId);
                if (user != null) {
                    businessReceiverName.append(user.getUserName()).append(";");
                }
            }
            if (businessReceiverName.indexOf(";") > 0) {
                businessReceiverName = businessReceiverName.deleteCharAt(businessReceiverName.lastIndexOf(";"));
            }
            bean.setBusinessReceiverName(businessReceiverName.toString());
            bean.setBusinessReceiverId(businessReceiverId);
        }

        bean.setCurrentUserIsAdmin(((UserDetails) SpringSecurityUtils.getCurrentUser()).isAdmin());

        // 设置业务角色列表
        List<BusinessUnitTreeRole> roles = businessUnitTreeRoleService.getByBusinessUnitTreeUuid(uuid);
        List<BusinessUnitTreeRoleBean> roleBeans = new ArrayList<BusinessUnitTreeRoleBean>();
        for (BusinessUnitTreeRole businessUnitTreeRole : roles) {
            BusinessUnitTreeRoleBean roleBean = new BusinessUnitTreeRoleBean();
            BeanUtils.copyProperties(businessUnitTreeRole, roleBean);
            roleBeans.add(roleBean);
        }
        bean.setBusinessUnitTreeRoles(roleBeans);

        return bean;
    }
}
