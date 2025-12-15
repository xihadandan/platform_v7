package com.wellsoft.pt.unit.service.impl;

import com.wellsoft.context.jdbc.support.PropertyFilter;
import com.wellsoft.context.jdbc.support.QueryInfo;
import com.wellsoft.pt.cache.config.CacheName;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.unit.bean.BusinessTypeBean;
import com.wellsoft.pt.unit.dao.BusinessTypeDao;
import com.wellsoft.pt.unit.dao.CommonUnitDao;
import com.wellsoft.pt.unit.entity.BusinessType;
import com.wellsoft.pt.unit.entity.CommonUnit;
import com.wellsoft.pt.unit.service.BusinessTypeService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Description: BusinessTypeServiceImpl.java
 *
 * @author liuzq
 * @date 2013-11-5
 */
@Service
@Transactional
public class BusinessTypeServiceImpl extends BaseServiceImpl implements BusinessTypeService {

    @Autowired
    private BusinessTypeDao businessTypeDao;

    @Autowired
    private CommonUnitDao commonUnitDao;

    @Override
    public List<BusinessTypeBean> query(QueryInfo queryInfo) {
        Map<String, Object> values = PropertyFilter.convertToMap(queryInfo.getPropertyFilters());
        values.put("orderBy", queryInfo.getOrderBy());
        List<BusinessType> result = businessTypeDao.namedQuery("businessTypeQuery", values, BusinessType.class,
                queryInfo.getPagingInfo());
        List<BusinessTypeBean> beanList = new ArrayList<BusinessTypeBean>();

        for (BusinessType type : result) {
            BusinessTypeBean bean = new BusinessTypeBean();
            BeanUtils.copyProperties(type, bean);
            CommonUnit unit = type.getUnit();
            if (unit != null) {
                bean.setUnitId(unit.getId());
                bean.setUnitName(unit.getName());
            }
            beanList.add(bean);
        }
        return beanList;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.unit.service.BusinessTypeService#getById(java.lang.String)
     */
    @Override
    public BusinessType getById(String id) {
        return businessTypeDao.getById(id);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.UnitService#saveBean(com.wellsoft.pt.org.bean.UnitBean)
     */
    @Override
    public void saveBean(BusinessTypeBean bean) {
        // 保存业务类别基本信息
        BusinessType businessType = new BusinessType();
        if (StringUtils.isNotBlank(bean.getUuid())) {
            businessType = this.businessTypeDao.get(bean.getUuid());
        }
        businessType.setId(bean.getId());
        businessType.setCode(bean.getCode());
        businessType.setName(bean.getName());

        // 1、保存业务管理单位
        businessType.setUnit(this.commonUnitDao.getByUnitId(bean.getUnitId()));

        this.businessTypeDao.save(businessType);
    }

    @Override
    public void saveUnitManagerToBusinessType(String businessTypeUuid, String unitManagerUserId,
                                              String unitManagerUserName) {
        BusinessType businessType = null;
        if (StringUtils.isNotBlank(businessTypeUuid)) {
            businessType = this.businessTypeDao.get(businessTypeUuid);
            // 单位内通讯录管理员
            if (businessType != null) {
                businessType.setUnitManagerUserId(unitManagerUserId);
                businessType.setUnitManagerUserName(unitManagerUserName);

            }

        }
        this.businessTypeDao.save(businessType);
    }

    @Override
    public BusinessTypeBean getBean(String uuid) {
        BusinessType type = this.businessTypeDao.get(uuid);

        BusinessTypeBean bean = new BusinessTypeBean();

        BeanUtils.copyProperties(type, bean);

        bean.setBusinessTypeUuid(type.getUuid());

        CommonUnit commonUnit = type.getUnit();

        if (commonUnit != null) {
            bean.setUnitName(commonUnit.getName());
            bean.setUnitId(commonUnit.getUnitId());
        }

        return bean;
    }

    @Override
    @CacheEvict(value = CacheName.DEFAULT, allEntries = true)
    public void remove(String uuid) {
        this.businessTypeDao.delete(uuid);
    }

    @Override
    @CacheEvict(value = CacheName.DEFAULT, allEntries = true)
    public void removes(Collection<String> uuids) {
        for (String uuid : uuids) {
            this.businessTypeDao.delete(uuid);
        }
    }

    @Override
    public boolean validateId(String id, String uuid) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("id", id);

        List<BusinessType> list = null;

        if (StringUtils.isNotBlank(uuid)) {
            paramMap.put("uuid", uuid);
            list = this.businessTypeDao.getListByUuidId(paramMap);
            if (list != null && !list.isEmpty())
                return false;
        } else {
            BusinessType type = this.businessTypeDao.getById(id);
            if (type != null)
                return false;
        }
        return true;
    }

    public BusinessType getBusinessTypeById(String businessTypeId) {
        if (StringUtils.isBlank(businessTypeId)) {
            return null;
        }
        return this.businessTypeDao.getById(businessTypeId);
    }

    public List<BusinessType> getAllBusinessTypes() {
        return this.businessTypeDao.getAll("uuid", true);
    }

}
