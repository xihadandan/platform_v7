package com.wellsoft.pt.unit.service.impl;

import com.wellsoft.context.jdbc.support.PropertyFilter;
import com.wellsoft.context.jdbc.support.QueryInfo;
import com.wellsoft.pt.cache.config.CacheName;
import com.wellsoft.pt.common.generator.service.IdGeneratorService;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.mt.entity.Tenant;
import com.wellsoft.pt.mt.facade.service.TenantFacadeService;
import com.wellsoft.pt.org.entity.Department;
import com.wellsoft.pt.org.entity.User;
import com.wellsoft.pt.org.service.DepartmentService;
import com.wellsoft.pt.org.service.UserService;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.unit.bean.CommonUnitBean;
import com.wellsoft.pt.unit.dao.CommonUnitDao;
import com.wellsoft.pt.unit.dao.CommonUnitTreeDao;
import com.wellsoft.pt.unit.entity.CommonUnit;
import com.wellsoft.pt.unit.entity.CommonUnitTree;
import com.wellsoft.pt.unit.service.CommonUnitService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Description: CommonUnitServiceImpl.java
 *
 * @author liuzq
 * @date 2013-11-5
 */
@Service
@Transactional
public class CommonUnitServiceImpl extends BaseServiceImpl implements CommonUnitService {
    private static final String UNIT_ID_PATTERN = "O0000000000";

    @Autowired
    private CommonUnitDao commonUnitDao;

    @Autowired
    private CommonUnitTreeDao commonUnitTreeDao;

    @Autowired
    private IdGeneratorService tenantIdGeneratorService;

    @Autowired
    private TenantFacadeService tenantDao;

    @Autowired
    private UserService userService;

    @Autowired
    private DepartmentService departmentService;

    @Override
    public List<CommonUnitBean> query(QueryInfo queryInfo) {
        Map<String, Object> values = PropertyFilter.convertToMap(queryInfo.getPropertyFilters());
        values.put("orderBy", queryInfo.getOrderBy());
        List<CommonUnit> result = commonUnitDao.namedQuery("commonUnitQuery", values, CommonUnit.class,
                queryInfo.getPagingInfo());
        List<CommonUnitBean> beans = new ArrayList<CommonUnitBean>();
        for (CommonUnit unit : result) {
            CommonUnitBean bean = new CommonUnitBean();
            BeanUtils.copyProperties(unit, bean);

            Tenant tenant = tenantDao.getById(unit.getTenantId());
            if (tenant != null) {
                bean.setTenantId(tenant.getId());
                bean.setTenantName(tenant.getName());
            }
            beans.add(bean);
        }
        return beans;
    }

    @Override
    @CacheEvict(value = CacheName.DEFAULT, allEntries = true)
    public void saveBean(CommonUnitBean bean) {
        // 保存单位基本信息
        CommonUnit commonUnit = new CommonUnit();
        if (StringUtils.isNotBlank(bean.getUuid())) {
            commonUnit = this.commonUnitDao.get(bean.getUuid());
        } else {
            // bean.setId(IdPrefix.UNIT.getValue() + UUID.randomUUID());
            String id = tenantIdGeneratorService.generate(CommonUnit.class, UNIT_ID_PATTERN);
            String tenantId = bean.getTenantId();
            bean.setUnitId(id.substring(0, 1) + tenantId.substring(1, tenantId.length()) + id.substring(4, 11));
        }
        BeanUtils.copyProperties(bean, commonUnit);

        // //设置租户
        // if (StringUtils.isNotBlank(bean.getTenantUuid())) {
        // commonUnit.setTenant(this.tenantDao.get(bean.getTenantUuid()));
        // }
        this.commonUnitDao.save(commonUnit);
    }

    @Override
    public CommonUnitBean getBean(String unitUuid) {
        CommonUnitBean bean = new CommonUnitBean();

        CommonUnit commonUnit = this.commonUnitDao.get(unitUuid);

        BeanUtils.copyProperties(commonUnit, bean);

        Tenant tenant = tenantDao.getById(commonUnit.getTenantId());
        if (tenant != null) {
            bean.setTenantName(tenant.getName());
            bean.setTenantId(tenant.getId());
        }

        return bean;
    }

    @Override
    @CacheEvict(value = CacheName.DEFAULT, allEntries = true)
    public void removeByUuid(String uuid) {
        this.commonUnitDao.delete(uuid);
    }

    @Override
    @CacheEvict(value = CacheName.DEFAULT, allEntries = true)
    public void removeByUuids(Collection<String> uuids) {
        for (String uuid : uuids) {
            this.commonUnitDao.delete(uuid);
        }
    }

    @Override
    public List<CommonUnit> getListByTenantId(String tenantId) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("tenantId", tenantId);
        return this.commonUnitDao.getListByParamMap(paramMap);
    }

    public List<CommonUnit> getByUserId(String userId) {
        if (StringUtils.isBlank(userId)) {
            return null;
        }
        User user = userService.getById(userId);
        if (user == null)
            return null;
        return this.commonUnitDao.findBy("tenantId", user.getTenantId());
    }

    public CommonUnit getByCurrentUserId(String userId) {
        if (StringUtils.isBlank(userId)) {
            return null;
        }
        UserDetails userDetail = SpringSecurityUtils.getCurrentUser();
        Department department = userService.getDepartmentByUserId(userDetail.getUserId());
        return this.commonUnitDao.getById(department.getCommonUnitId());
    }

    public CommonUnit getById(String id) {
        if (StringUtils.isBlank(id)) {
            return null;
        }
        CommonUnit commonUnit = this.commonUnitDao.getById(id);
        if (commonUnit == null) {
            commonUnit = getByUnitId(id);
        }
        return commonUnit;
    }

    public CommonUnit getByUnitId(String unitId) {
        if (StringUtils.isBlank(unitId)) {
            return null;
        }
        return this.commonUnitDao.getByUnitId(unitId);
    }

    @Override
    public CommonUnit getByBusinessTypeId(String businessTypeId) {
        CommonUnit commonUnit = this.commonUnitDao.getCommonUnitByBusinessTypeId(businessTypeId);
        return commonUnit;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.unit.service.CommonUnitService#getAllCommonUnits()
     */
    @Override
    public List<CommonUnit> getAllCommonUnits() {
        return commonUnitDao.getAll("code", true);
    }

    @Override
    public List<CommonUnit> getCommonUnitsByBlurUnitName(String unitNameKey) {
        return commonUnitDao.getCommonUnitsByBlurUnitName(unitNameKey);
    }

    @Override
    public List<CommonUnit> getCommonUnitByTenantId(String tenantId) {
        // TODO Auto-generated method stub
        String hql = "from CommonUnit c where c.tenantId = :tenantId";
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("tenantId", tenantId);
        return commonUnitDao.find(hql, values);
    }

    @Override
    public CommonUnitTree getCommonUnitTreeByUuid(String uuid) {
        // TODO Auto-generated method stub
        return commonUnitTreeDao.get(uuid);
    }

    @Override
    public CommonUnitBean getCommonUnitBean(String commonUnitId) {
        // TODO Auto-generated method stub
        CommonUnit commonUnit = this.getById(commonUnitId);
        CommonUnitBean bean = new CommonUnitBean();
        BeanUtils.copyProperties(commonUnit, bean);
        Tenant tenant = tenantDao.getById(commonUnit.getTenantId());
        bean.setTenantId(tenant.getId());
        bean.setTenantName(tenant.getName());
        return bean;
    }

    @Override
    public void updateUnitIdForInit() {
        // TODO Auto-generated method stub
        List<CommonUnit> listCommonUnit = this.getAllCommonUnits();
        for (CommonUnit commonUnit : listCommonUnit) {
            String tenantId = commonUnit.getTenantId();
            String unitId = commonUnit.getUnitId();
            if (tenantId == null || "".equals(tenantId) || (unitId != null && !"".equals(unitId)))
                continue;
            String id = tenantIdGeneratorService.generate(CommonUnit.class, UNIT_ID_PATTERN);
            commonUnit.setUnitId(id.substring(0, 1) + tenantId.substring(1, tenantId.length()) + id.substring(4, 11));
            commonUnitDao.save(commonUnit);
        }
    }

    @Override
    public List<User> getAllUserByUnitId(String unitId) {
        // TODO Auto-generated method stub
        CommonUnit commonUnit = this.getById(unitId);
        if (commonUnit == null) {
            return null;
        }
        List<User> users = departmentService.getAllUserByUnitId(commonUnit.getId());
        return users;
    }
}
