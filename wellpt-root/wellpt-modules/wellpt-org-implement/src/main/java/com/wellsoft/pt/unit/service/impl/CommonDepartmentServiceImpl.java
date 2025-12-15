package com.wellsoft.pt.unit.service.impl;

import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.org.dao.DepartmentDao;
import com.wellsoft.pt.org.entity.Department;
import com.wellsoft.pt.org.entity.DepartmentUserJob;
import com.wellsoft.pt.org.entity.User;
import com.wellsoft.pt.unit.dao.CommonDepartmentDao;
import com.wellsoft.pt.unit.dao.CommonUnitDao;
import com.wellsoft.pt.unit.dao.CommonUserDao;
import com.wellsoft.pt.unit.entity.CommonDepartment;
import com.wellsoft.pt.unit.entity.CommonUnit;
import com.wellsoft.pt.unit.entity.CommonUser;
import com.wellsoft.pt.unit.service.CommonDepartmentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Description: CommonDepartmentServiceImpl.java
 *
 * @author liuzq
 * @date 2013-11-5
 */
@Service
@Transactional
public class CommonDepartmentServiceImpl extends BaseServiceImpl implements
        CommonDepartmentService {

    @Autowired
    private CommonDepartmentDao commonDepartmentDao;

    @Autowired
    private CommonUserDao commonUserDao;

    @Autowired
    private CommonUnitDao commonUnitDao;

    @Autowired
    private DepartmentDao deparmentDao;

    public void updateCommonDepartmentVisibleByCommonUnit(String commonUnitId, String departmentId,
                                                          Boolean isVisible) {
        CommonUnit commonUnit = this.commonUnitDao.getById(commonUnitId);
        if (commonUnit != null) {
            Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("unitUuid", commonUnit.getUuid());
            List<CommonDepartment> commonDepartmentList = this.commonDepartmentDao.getListByUnit(
                    paramMap);
            for (CommonDepartment bean : commonDepartmentList) {
                //如果找到了单位下直接部门就停止循环
                if (departmentId.equals(bean.getId())) {
                    bean.setIsVisible(isVisible);
                    this.commonDepartmentDao.save(bean);
                    break;
                } else {
                    //否则查找子部门
                    updateCommonDepartmentVisibleByFarent(bean, departmentId, isVisible);
                }
            }
        }
    }

    private void updateCommonDepartmentVisibleByFarent(CommonDepartment bean, String departmentId,
                                                       Boolean isVisible) {
        for (CommonDepartment child : bean.getChildren()) {
            if (departmentId.equals(child.getId())) {
                child.setIsVisible(isVisible);
                this.commonDepartmentDao.save(bean);
                break;
            } else {
                //否则查找子部门
                updateCommonDepartmentVisibleByFarent(child, departmentId, isVisible);
            }
        }
    }

    public void updateCommonDepartmentUserByCommonUnit(String srcCommonUnitId, String departmentId,
                                                       String destCommonUnitId, Boolean isVisible,
                                                       String tenantId) {

        if ((StringUtils.isNotBlank(srcCommonUnitId) && StringUtils.isNotBlank(destCommonUnitId))) {
            //挂接的目标单位
            CommonUnit commonUnit = this.commonUnitDao.getById(destCommonUnitId);

            Map<String, Object> paramMap = new HashMap<String, Object>();

            paramMap.put("commonUnitId", srcCommonUnitId);
            paramMap.put("departmentId", departmentId);
            List<CommonDepartment> commonDepartmentList = this.commonDepartmentDao.getListByParamMap(
                    paramMap);

            //如果部门已经存在，则更换挂接单位
            if (!commonDepartmentList.isEmpty()) {
                CommonDepartment commonDepartment = commonDepartmentList.get(0);
                //是否显示
                commonDepartment.setIsVisible(isVisible);

                //存在目标单位
                if (commonUnit != null) {
                    commonDepartment.setUnit(commonUnit);
                    this.commonDepartmentDao.save(commonDepartment);
                } else {
                    //删除公共库部门和用户
                    this.commonDepartmentDao.delete(commonDepartment);
                }

            } else {
                if (commonUnit != null) {
                    Department department = this.deparmentDao.getById(departmentId);

                    paramMap = new HashMap<String, Object>();
                    paramMap.put("tenantId", commonUnit.getTenantId());
                    paramMap.put("departmentId", department.getId());
                    commonDepartmentList = this.commonDepartmentDao.getListByParamMap(paramMap);
                    //如果本租户库已经存在这个部门，则抛出异常
                    if (!commonDepartmentList.isEmpty()) {
                        throw new RuntimeException(
                                "[" + commonDepartmentList.get(0).getName() + "]已被挂接!");
                    }

                    CommonDepartment commonDepartment = new CommonDepartment();
                    commonDepartment.setId(department.getId());
                    commonDepartment.setName(department.getName());
                    commonDepartment.setCode(department.getCode());
                    //是否显示
                    commonDepartment.setIsVisible(isVisible);
                    commonDepartment.setUnit(commonUnit);
                    //保存公共库部门
                    this.commonDepartmentDao.save(commonDepartment);
                }
            }
        } else if ((StringUtils.isBlank(srcCommonUnitId) && StringUtils.isNotBlank(
                destCommonUnitId))) {
            CommonUnit commonUnit = this.commonUnitDao.getById(destCommonUnitId);
            if (commonUnit != null) {
                Department department = this.deparmentDao.getById(departmentId);

                Map<String, Object> paramMap = new HashMap<String, Object>();
                paramMap.put("tenantId", commonUnit.getTenantId());
                paramMap.put("departmentId", department.getId());
                List<CommonDepartment> commonDepartmentList = this.commonDepartmentDao.getListByParamMap(
                        paramMap);
                if (!commonDepartmentList.isEmpty()) {
                    throw new RuntimeException(
                            "[" + commonDepartmentList.get(0).getName() + "]已被挂接!");
                }
                department.setCommonUnitId(commonUnit.getId());
                deparmentDao.save(department);
                CommonDepartment commonDepartment = new CommonDepartment();
                commonDepartment.setId(department.getId());
                commonDepartment.setName(department.getName());
                commonDepartment.setCode(department.getCode());
                //是否显示
                commonDepartment.setIsVisible(isVisible);
                commonDepartment.setUnit(commonUnit);
                //保存公共库部门
                this.commonDepartmentDao.save(commonDepartment);
            }
        } else if (StringUtils.isNotBlank(srcCommonUnitId) && StringUtils.isBlank(
                destCommonUnitId)) {
            //删除单位下的部门和用户
            Map<String, Object> paramMap = new HashMap<String, Object>();

            paramMap.put("commonUnitId", srcCommonUnitId);
            paramMap.put("departmentId", departmentId);
            List<CommonDepartment> commonDepartmentList = this.commonDepartmentDao.getListByParamMap(
                    paramMap);

            //如果部门已经存在，则更换挂接单位
            if (!commonDepartmentList.isEmpty()) {
                for (CommonDepartment commonDepartment : commonDepartmentList) {
                    this.commonDepartmentDao.delete(commonDepartment);
                }
            }

        } else {
            Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("tenantId", tenantId);
            paramMap.put("departmentId", departmentId);
            List<CommonDepartment> commonDepartmentList = this.commonDepartmentDao.getListByParamMap(
                    paramMap);

            //如果部门已经存在
            if (!commonDepartmentList.isEmpty()) {
                CommonDepartment commonDepartment = commonDepartmentList.get(0);
                commonDepartment.setIsVisible(isVisible);
                //保存公共库部门
                this.commonDepartmentDao.save(commonDepartment);
            }
        }

    }

    private void saveChildrenByParent(Department parent, CommonDepartment commonParent,
                                      CommonUnit commonUnit) {
        for (Department department : parent.getChildren()) {
            Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("tenantId", commonUnit.getTenantId());
            paramMap.put("departmentId", department.getId());
            List<CommonDepartment> commonDepartmentList = this.commonDepartmentDao.getListByParamMap(
                    paramMap);
            //如果本租户库已经存在这个部门，则跳过
            if (!commonDepartmentList.isEmpty())
                continue;

            CommonDepartment commonDepartment = new CommonDepartment();

            commonDepartment.setId(department.getId());
            commonDepartment.setName(department.getName());
            commonDepartment.setCode(department.getCode());
            commonDepartment.setIsVisible(department.getIsVisible());
            commonDepartment.setParent(commonParent);
            commonDepartment.setUnit(commonUnit);
            //保存公共库部门
            this.commonDepartmentDao.save(commonDepartment);

            Set<DepartmentUserJob> departmentUserSet = department.getDepartmentUsers();
            for (DepartmentUserJob departmentUser : departmentUserSet) {
                CommonUser commonUser = new CommonUser();

                User user = departmentUser.getUser();

                commonUser.setId(user.getId());
                commonUser.setName(user.getUserName());
                commonUser.setCode(user.getCode());
                commonUser.setSex(user.getSex());
                commonUser.setUnit(commonUnit);

                //保存部门用户关联关系
                Set<CommonDepartment> departments = new HashSet<CommonDepartment>(0);
                departments.add(commonDepartment);
                commonUser.setDepartments(departments);

                this.commonUserDao.save(commonUser);
            }
            this.saveChildrenByParent(department, commonDepartment, commonUnit);
        }
    }

    private void deleteChildrenByParent(CommonDepartment parent) {
        for (CommonDepartment department : parent.getChildren()) {
            //			for (CommonUser commonUser : department.getUsers()) {
            //				this.commonUserDao.delete(commonUser);
            //			}
            this.deleteChildrenByParent(department);
            this.commonDepartmentDao.delete(department);
        }
    }

    public void deleteCommonDepartment(String departmentId, String tenantId) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("tenantId", tenantId);
        paramMap.put("departmentId", departmentId);
        List<CommonDepartment> commonDepartmentList = this.commonDepartmentDao.getListByParamMap(
                paramMap);

        //如果部门已经存在
        if (!commonDepartmentList.isEmpty()) {
            for (CommonDepartment commonDept : commonDepartmentList) {
                this.commonDepartmentDao.save(commonDept);
            }
        }
    }

    //同步用户
    @Override
    public void updateCommonUser(String commonUnitId, String departmentId, User user,
                                 String updateType) {
        if (StringUtils.isNotBlank(commonUnitId)) {
            CommonUnit commonUnit = this.commonUnitDao.getById(commonUnitId);
            if (commonUnit != null) {
                Map<String, Object> paramMap = new HashMap<String, Object>();
                paramMap.put("unitUuid", commonUnit.getUuid());
                List<CommonDepartment> commonDepartmentList = this.commonDepartmentDao.getListByUnit(
                        paramMap);
                for (CommonDepartment commonDepartment : commonDepartmentList) {
                    this.foreachCommonDepartment(commonDepartment, departmentId, user, updateType);
                }
            }
        }
    }

    private void foreachCommonDepartment(CommonDepartment commonDepartment, String departmentId,
                                         User user,
                                         String updateType) {
        String commonDepartmentId = commonDepartment.getId();
        if (commonDepartmentId != null && commonDepartmentId.equals(departmentId)) {
            //新增用户
            if ("1".equals(updateType)) {
                CommonUser commonUser = new CommonUser();
                commonUser = commonUserDao.findUniqueBy("id", user.getId());
                commonUser.setId(user.getId());
                commonUser.setCode(user.getCode());
                commonUser.setName(user.getUserName());
                commonUser.setSex(user.getSex());
                Set<CommonDepartment> commmonDepartmentSet = new HashSet<CommonDepartment>();
                commmonDepartmentSet.add(commonDepartment);
                commonUser.setDepartments(commmonDepartmentSet);
                commonUser.setUnit(commonDepartment.getUnit());
                this.commonUserDao.save(commonUser);
            }
            //删除用户
            if ("2".equals(updateType)) {
                Map<String, Object> paramMap = new HashMap<String, Object>();
                paramMap.put("deptUuid", commonDepartment.getUuid());
                paramMap.put("userId", user.getId());
                CommonUser commonUser = this.commonUserDao.findByDepartmentAndUserId(paramMap);
                if (commonUser != null) {
                    this.commonUserDao.delete(commonUser);
                }
            }
        }
        for (CommonDepartment childDepartment : commonDepartment.getChildren()) {
            this.foreachCommonDepartment(childDepartment, departmentId, user, updateType);
        }
    }

    @Override
    public CommonDepartment getCommonDepartmentById(String commonDepartmentId) {
        // TODO Auto-generated method stub
        return this.commonDepartmentDao.findUniqueBy("id", commonDepartmentId);
    }

    @Override
    public List<CommonDepartment> getByUnitUuid(String uuid) {
        // TODO Auto-generated method stub
        return this.commonDepartmentDao.getListByUnitUuid(uuid);
    }
}
