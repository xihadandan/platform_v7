/*
 * @(#)2019-02-14 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.business.service.impl;

import com.wellsoft.context.component.jqgrid.JqGridQueryData;
import com.wellsoft.context.component.jqgrid.JqGridQueryInfo;
import com.wellsoft.context.component.select2.Select2DataBean;
import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.business.dao.BusinessCategoryDao;
import com.wellsoft.pt.basicdata.business.dao.BusinessCategoryOrgDao;
import com.wellsoft.pt.basicdata.business.dao.BusinessRoleDao;
import com.wellsoft.pt.basicdata.business.dto.BusinessCategoryDto;
import com.wellsoft.pt.basicdata.business.dto.BusinessRoleDto;
import com.wellsoft.pt.basicdata.business.entity.BusinessCategoryEntity;
import com.wellsoft.pt.basicdata.business.entity.BusinessCategoryOrgEntity;
import com.wellsoft.pt.basicdata.business.entity.BusinessRoleEntity;
import com.wellsoft.pt.basicdata.business.service.BusinessCategoryService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.multi.org.entity.MultiOrgSystemUnit;
import com.wellsoft.pt.multi.org.entity.MultiOrgUserAccount;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgApiFacade;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgService;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgUserAccountFacadeService;
import com.wellsoft.pt.multi.org.facade.service.OrgApiFacade;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 数据库表BUSINESS_CATEGORY的service服务接口实现类
 *
 * @author leo
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019-02-14.1	leo		2019-02-14		Create
 * </pre>
 * @date 2019-02-14
 */
@Service
public class BusinessCategoryServiceImpl extends
        AbstractJpaServiceImpl<BusinessCategoryEntity, BusinessCategoryDao, String> implements
        BusinessCategoryService {

    @Autowired
    private BusinessRoleDao businessRoleDao;

    @Autowired
    private BusinessCategoryOrgDao businessCategoryOrgDao;


    @Autowired
    private OrgApiFacade orgApiFacade;

    @Autowired
    private MultiOrgApiFacade multiOrgApiFacade;

    @Autowired
    private MultiOrgUserAccountFacadeService multiOrgUserAccountFacadeService;

    @Autowired
    private MultiOrgService multiOrgService;

    @Override
    public JqGridQueryData query(JqGridQueryInfo queryInfo) {

        PagingInfo page = new PagingInfo(queryInfo.getPage(), queryInfo.getRows(), true);
        List<BusinessCategoryEntity> dbList = this.dao.listAllByOrderPage(page, null);
        List<BusinessCategoryDto> list = new ArrayList<BusinessCategoryDto>();
        for (BusinessCategoryEntity dbPo : dbList) {
            BusinessCategoryDto dto = new BusinessCategoryDto();
            BeanUtils.copyProperties(dbPo, dto);
            list.add(dto);
        }
        JqGridQueryData queryData = new JqGridQueryData();
        queryData.setCurrentPage(queryInfo.getPage());
        queryData.setDataList(list);
        queryData.setRepeatitems(false);
        queryData.setTotalPages(page.getTotalPages());
        queryData.setTotalRows(page.getTotalCount());
        return queryData;
    }


    @Override
    public JqGridQueryData queryByApplication(JqGridQueryInfo queryInfo, String value) {

        PagingInfo page = new PagingInfo(queryInfo.getPage(), queryInfo.getRows(), true);

        HashMap<String, Object> params = new HashMap<String, Object>();

        params.put("unitId", SpringSecurityUtils.getCurrentUserUnitId());
        params.put("value", value);
        List<QueryItem> datas = this.dao.listQueryItemByNameSQLQuery(
                "queryBusinessCategoryByApplication", params, page);

        List<BusinessCategoryDto> list = new ArrayList<BusinessCategoryDto>();

        for (QueryItem data : datas) {

            BusinessCategoryDto dto = new BusinessCategoryDto();
            dto.setApplicationUuid(data.getString("applicationId"));
            dto.setUuid(data.getString("uuid"));
            dto.setName(data.getString("name"));
            dto.setManageDeptValue(data.getString("manageDeptValue"));
            dto.setManageUserValue(data.getString("manageUserValue"));
            list.add(dto);
        }
        JqGridQueryData queryData = new JqGridQueryData();
        queryData.setCurrentPage(queryInfo.getPage());
        queryData.setDataList(list);
        queryData.setRepeatitems(false);
        queryData.setTotalPages(page.getTotalPages());
        queryData.setTotalRows(page.getTotalCount());
        return queryData;
    }

    @Override
    public BusinessCategoryDto getBeanByUuid(String uuid) {

        BusinessCategoryEntity po = this.dao.getOne(uuid);
        BusinessCategoryDto vo = new BusinessCategoryDto();
        BeanUtils.copyProperties(po, vo);

        List<BusinessRoleEntity> dbRoles = businessRoleDao.listByFieldEqValue(
                "businessCategoryUuid", uuid);
        for (BusinessRoleEntity role : dbRoles) {
            BusinessRoleDto dto = new BusinessRoleDto();

            BeanUtils.copyProperties(role, dto);

            vo.getAddRoles().add(dto);

        }

        return vo;
    }

    @Override
    @Transactional
    public void deleteByIds(String[] ids) {
        for (int i = 0; i < ids.length; i++) {
            List<BusinessRoleEntity> roles = businessRoleDao.listByFieldEqValue(
                    "businessCategoryUuid", ids[i]);
            for (BusinessRoleEntity role : roles) {
                businessRoleDao.delete(role);
            }

            List<BusinessCategoryOrgEntity> orgs = businessCategoryOrgDao.listByFieldEqValue(
                    "businessCategoryUuid", ids[i]);
            for (BusinessCategoryOrgEntity org : orgs) {
                businessCategoryOrgDao.delete(org);
            }

            dao.delete(ids[i]);
        }
    }

    @Override
    @Transactional
    public String save(BusinessCategoryDto vo) {

        if (StringUtils.isBlank(vo.getId()) && this.dao.listByFieldEqValue("id",
                vo.getId()).size() > 0) {
            return "添加失败，id已经存在";
        }
        BusinessCategoryEntity po = null;
        if (StringUtils.isBlank(vo.getUuid())) {
            po = new BusinessCategoryEntity();
            po.setUuid(null);
            po.setSystemUnitId(SpringSecurityUtils.getCurrentUserUnitId());
        } else {
            po = this.dao.getOne(vo.getUuid());
        }
        List<MultiOrgUserAccount> accounts = multiOrgUserAccountFacadeService.queryAllAdminIdsBySystemUnitId(vo.getManageDept());
        if (accounts.size() < 1) {
            return "该单位尚无管理员，请先添加单位管理员";
        }
        po.setManageUser(accounts.get(0).getId());
        po.setManageUserValue(accounts.get(0).getUserName());
        po.setName(vo.getName());
        po.setCode(vo.getCode());
        po.setId(vo.getId());
        po.setManageDept(vo.getManageDept());
        po.setManageDeptValue(vo.getManageDeptValue());

        this.dao.save(po);

        for (BusinessRoleDto dto : vo.getAddRoles()) {
            BusinessRoleEntity role = null;
            if (StringUtils.isBlank(dto.getUuid())) {
                role = new BusinessRoleEntity();
                dto.setUuid(null);
            } else {
                role = businessRoleDao.getOne(dto.getUuid());
            }
            BeanUtils.copyProperties(dto, role);
            role.setBusinessCategoryUuid(po.getUuid());
            businessRoleDao.save(role);
        }

        for (String uuid : vo.getDelRoleIds()) {
            businessRoleDao.delete(uuid);
        }

        return "success";
    }

    @Override
    public String getBasicClassOaUnitElePath(String unitId) {
        String currentVersion = orgApiFacade.getCurrentVersionByEleId(unitId);
        String eleIdPath = orgApiFacade.getEleIdPathByEleId(unitId, currentVersion);
        return eleIdPath;
    }

    @Override
    @Transactional
    public void updateManageUser(List<String> list, String userId, String userName) {
        for (String uuid : list) {
            BusinessCategoryEntity po = this.dao.getOne(uuid);
            po.setManageUser(userId);
            po.setManageUserValue(userName);
            dao.update(po);
        }

    }

    public Select2QueryData querySelectDataFromMultiOrgSystemUnit(
            Select2QueryInfo select2QueryInfo) {
        if (MultiOrgSystemUnit.PT_ID.equals(SpringSecurityUtils.getCurrentUserUnitId())) {
            return multiOrgApiFacade.querySelectDataFromMultiOrgSystemUnit(select2QueryInfo);
        }
        MultiOrgSystemUnit unit = multiOrgService.getSystemUnitById(SpringSecurityUtils.getCurrentUserUnitId());
        List<Select2DataBean> beans = new ArrayList<Select2DataBean>();
        beans.add(new Select2DataBean(unit.getId(), unit.getName()));
        return new Select2QueryData(beans);

    }

    public Select2QueryData querySelectDataFromMultiOrgSystemUnitAll(
            Select2QueryInfo select2QueryInfo) {
        return multiOrgApiFacade.querySelectDataFromMultiOrgSystemUnit(select2QueryInfo);
    }

    public Select2QueryData loadSelectDataFromMultiOrgSystemUnit(
            Select2QueryInfo select2QueryInfo) {
        return multiOrgApiFacade.loadSelectDataFromMultiOrgSystemUnit(select2QueryInfo);
    }

    public Select2QueryData querySelectDataFromBusinessCategory(Select2QueryInfo select2QueryInfo) {
        String queryValue = select2QueryInfo.getSearchValue();
        List<BusinessCategoryEntity> categorys = null;
        if (StringUtils.isBlank(queryValue)) {
            categorys = this.listAll();
        } else {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("name", "%" + queryValue + "%");
            categorys = this.listByHQL("from BusinessCategoryEntity where name like :name", map);
        }

        List<Select2DataBean> beans = new ArrayList<Select2DataBean>();
        for (BusinessCategoryEntity category : categorys) {
            beans.add(new Select2DataBean(category.getUuid(), category.getName()));
        }
        return new Select2QueryData(beans);
    }

    public Select2QueryData loadSelectDataFromBusinessCategory(Select2QueryInfo select2QueryInfo) {

        String[] uuids = select2QueryInfo.getIds();
        if (uuids.length == 0) {
            return new Select2QueryData();
        }

        List<Select2DataBean> beans = new ArrayList<Select2DataBean>();
        BusinessCategoryEntity category = this.getOne(uuids[0]);
        beans.add(new Select2DataBean(category.getUuid(), category.getName()));
        return new Select2QueryData(beans);
    }

}
