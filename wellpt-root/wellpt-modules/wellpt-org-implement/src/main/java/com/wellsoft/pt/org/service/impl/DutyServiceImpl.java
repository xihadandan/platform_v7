package com.wellsoft.pt.org.service.impl;

import com.wellsoft.context.component.select2.Select2DataBean;
import com.wellsoft.context.component.select2.Select2QueryApi;
import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.datadict.entity.DataDictionary;
import com.wellsoft.pt.basicdata.datadict.service.DataDictionaryService;
import com.wellsoft.pt.cache.config.CacheName;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.org.bean.DutyBean;
import com.wellsoft.pt.org.dao.DutyDao;
import com.wellsoft.pt.org.entity.*;
import com.wellsoft.pt.org.service.DutyRoleService;
import com.wellsoft.pt.org.service.DutyService;
import com.wellsoft.pt.org.service.JobService;
import com.wellsoft.pt.security.audit.entity.Role;
import com.wellsoft.pt.security.audit.facade.service.RoleFacadeService;
import com.wellsoft.pt.security.facade.service.SecurityApiFacade;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Description: 如何描述该类
 *
 * @author zhengky
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	       修改人		修改日期	      修改内容
 * 2014-8-22.1  zhengky	2014-8-22	  Create
 * </pre>
 * @date 2014-8-22
 */

@Service
@Transactional
public class DutyServiceImpl extends BaseServiceImpl implements DutyService, Select2QueryApi {

    private static final String DUTY_ID_PATTERN = "W0000000000";
    private static final String GET_ALL_DUTY = " from Duty duty where duty.tenantId=:tenantId order by duty.ilevel desc ,duty.code asc ,duty.name ";
    private static final String GET_ALL_DUTY_ORDER_BY_EXTENID = " from Duty duty where duty.tenantId=:tenantId order by duty.externalId asc ";
    @Autowired
    private DutyDao dutyDao;
    @Autowired
    private JobService jobService;
    @Autowired
    private DataDictionaryService dataDictionaryService;
    @Autowired
    private com.wellsoft.pt.common.generator.service.IdGeneratorService idGeneratorService;
    @Autowired
    private RoleFacadeService roleFacadeService;
    @Autowired
    private SecurityApiFacade securityApiFacade;
    @Autowired
    private DutyRoleService dutyRoleService;

    @Override
    public Duty getByUuid(String uuid) {
        return this.getByUuId(uuid);
    }

    @Override
    public List<QueryItem> query(QueryInfo queryInfo) {
        return null;
    }

    @Override
    public DutyBean getBean(String uuid) {

        Duty duty = this.dao.get(Duty.class, uuid);
        DutyBean dutyBean = new DutyBean();
        BeanUtils.copyProperties(duty, dutyBean);
        return dutyBean;

    }

    @Override
    @CacheEvict(value = CacheName.DEFAULT, allEntries = true)
    public void saveBean(DutyBean bean) {

        Duty duty = new Duty();
        if (StringUtils.isEmpty(bean.getName())) {
            throw new RuntimeException("职务名称不能为空!");
        }
        if (StringUtils.isEmpty(bean.getCode())) {
            throw new RuntimeException("职务编号不能为空!");
        }
        // 判断编码不能重复
        List<Duty> dutyls = this.dao.findBy(Duty.class, "code", bean.getCode());
        if (StringUtils.isBlank(bean.getUuid())) {
            bean.setUuid(null);
            if (dutyls != null && dutyls.size() > 0) {
                throw new RuntimeException("职务编号不能重复!");
            }
            String id = idGeneratorService.generate(Duty.class, DUTY_ID_PATTERN);
            String tenantId = SpringSecurityUtils.getCurrentTenantId();
            bean.setTenantId(tenantId);
            id = id.substring(0, 1) + tenantId.substring(1, tenantId.length()) + id.substring(4, 11);
            bean.setId(id);
        } else {
            duty = this.dao.get(Duty.class, bean.getUuid());
            if (dutyls != null && dutyls.size() > 0) {
                for (Duty duty1 : dutyls) {
                    if (duty1.getUuid().equals(duty.getUuid())) {
                        continue;
                    } else {
                        throw new RuntimeException("职务编号不能重复!");
                    }
                }
            }
        }
        BeanUtils.copyProperties(bean, duty);
        // level此处跟duty_level同步.
        String dutylevel = bean.getDutyLevel();
        duty.setIlevel(Integer.parseInt(bean.getDutyLevel().substring(1, dutylevel.length())));
        this.dao.save(duty);
        // 更新职位列表
        List<Job> jobs = this.jobService.getJobByDutyUuid(duty.getUuid());
        for (Job job : jobs) {
            job.setDuty(duty);
            job.setDutyName(duty.getName());
            this.jobService.save(job);
        }
        List<DutyRole> dutyRoles = dutyRoleService.getDutyRoleByDutyUuid(duty.getUuid());
        if (dutyRoles.size() > 0) {
            for (DutyRole dutyRole : dutyRoles)
                dutyRoleService.deleteDutyRoleByDutyUuidAndRoleUuid(duty.getUuid(), dutyRole.getDutyRoleId()
                        .getRoleUuid());
        }
        // 2、设置职务角色信息
        for (Role role : bean.getRoles()) {
            Role tmp = this.roleFacadeService.get(role.getUuid());
            if (tmp != null) {
                DutyRole dutyRole = new DutyRole();
                DutyRoleId dutyRoleId = new DutyRoleId(duty.getUuid(), tmp.getUuid(),
                        SpringSecurityUtils.getCurrentTenantId());
                dutyRole.setDutyRoleId(dutyRoleId);
                this.dutyRoleService.save(dutyRole);
            }
        }
    }

    @Override
    public void remove(String uuid) {
        Duty duty = this.getByUuId(uuid);
        if (duty.getJobs().size() > 0) {
            throw new RuntimeException("职务已被职位引用!不能删除");
        }
        this.dao.delete(duty);
    }

    @Override
    public void removeAll(Collection<String> uuids) {
        for (String uuid : uuids) {
            this.remove(uuid);
        }
    }

    @Override
    public HashMap<String, Object> saveDutyFromList(List list) {
        // "共获得100条部门记录，成功导入80条，已经存在20条!"
        int duplicatecount = 0;
        int successcount = 0;
        int totalcount = list.size();
        HashMap<String, Object> rsmap = new HashMap<String, Object>();
        if (list != null && !list.isEmpty()) {
            List<DataDictionary> datadictionarys = dataDictionaryService.getDataDictionariesByType("JOB_SERIES");
            for (Object o : list) {
                Duty duty = (Duty) o;

                for (DataDictionary dataDictionary : datadictionarys) {
                    if (duty.getSeriesName().equals(dataDictionary.getCode())) {
                        duty.setSeriesUuid(dataDictionary.getUuid());
                        duty.setSeriesName(dataDictionary.getName());
                        break;
                    }
                }
                // 如果编号=null 则设置其=外系统ID
                if (duty.getCode() == null) {
                    duty.setCode(duty.getExternalId());
                }

                if (duty.getSeriesUuid() == null) {
                    duty.setSeriesName(null);
                }
                duty.setIlevel(Integer.parseInt(duty.getDutyLevel().substring(1, duty.getDutyLevel().length())));

                Duty dutyUpdate = new Duty();
                boolean isUpdate = false;
                List<Duty> dutyls = this.dao.findBy(Duty.class, "externalId", duty.getExternalId());
                if (dutyls != null && dutyls.size() > 0) {
                    dutyUpdate = dutyls.get(0);
                    isUpdate = true;
                }

                if (isUpdate) {
                    dutyUpdate.setName(duty.getName());
                    dutyUpdate.setSeriesUuid(duty.getSeriesUuid());
                    dutyUpdate.setSeriesName(duty.getSeriesName());
                    dutyUpdate.setDutyLevel(duty.getDutyLevel());
                    dutyUpdate.setIlevel(duty.getIlevel());
                    this.dao.save(dutyUpdate);
                    duplicatecount = duplicatecount + 1;
                } else {
                    String id = idGeneratorService.generate(Duty.class, DUTY_ID_PATTERN);
                    String tenantId = SpringSecurityUtils.getCurrentTenantId();
                    id = id.substring(0, 1) + tenantId.substring(1, tenantId.length()) + id.substring(4, 11);
                    duty.setId(id);
                    duty.setTenantId(tenantId);
                    this.dao.save(duty);
                    successcount = successcount + 1;
                }

            }
        }

        String msg = "选择的职务数据共【" + totalcount + "】行，成功更新【" + duplicatecount + "】行，成功导入【" + successcount + "】行";
        System.out.println(msg);
        rsmap.put("msg", msg);
        return rsmap;
    }

    @Override
    /**
     *
     * 构建职务树形下拉选择框 职位选职务时使用
     *
     * (non-Javadoc)
     * @see com.wellsoft.pt.org.service.DutyService#createTreeSelect()
     */
    public List<TreeNode> createDutyTreeSelect(String conditon) {
        List<Duty> list = this.getAllDutysOrderByExtenId();
        List<TreeNode> children = new ArrayList<TreeNode>();
        for (Duty duty : list) {
            TreeNode treenode = new TreeNode();
            treenode.setId(duty.getUuid());
            treenode.setName("【" + duty.getCode() + "】" + duty.getName() + "【" + duty.getDutyLevel() + "】");
            treenode.setData(duty.getName());
            treenode.setIsParent(false);
            children.add(treenode);
        }
        return children;
    }

    @Override
    public List<User> getUsersByDutyId(String dutyId) {
        Duty duty = getById(dutyId);
        List<User> userls = new ArrayList<User>();
        if (duty == null) {
            return userls;
        }
        List<Job> jobs = jobService.getJobByDutyUuid(duty.getUuid());
        // 获取部门下的岗位
        for (Job job : jobs) {
            Set<UserJob> userJobs = job.getJobUsers();
            for (UserJob userJob : userJobs) {
                userls.add(userJob.getUser());
            }
        }
        return userls;
    }

    @Override
    public TreeNode getRoleTree(String uuid) {
        Set<Role> userRoles = null;// roleService.getRolesByDutyUuid(uuid);
        List<Role> roles = this.roleFacadeService.getAll();
        return OrgUtil.getCategoryRoleTree(dataDictionaryService, userRoles, roles);
    }

    @Override
    public TreeNode getDutyRoleNestedRoleTree(String uuid) {
        Duty duty = this.dao.get(Duty.class, uuid);
        TreeNode treeNode = new TreeNode();
        treeNode.setName(duty.getName());
        treeNode.setId(TreeNode.ROOT_ID);
        Set<Role> roles = null;// roleService.getRolesByDutyUuid(uuid);
        List<TreeNode> children = new ArrayList<TreeNode>();
        for (Role role : roles) {
            TreeNode child = new TreeNode();
            child.setId(role.getUuid());
            child.setName(role.getName());
            children.add(child);
            roleFacadeService.buildRoleNestedRoleTree(child, role);
        }
        treeNode.setChildren(children);
        return treeNode;
    }

    @Override
    public List<Duty> getAll() {
        // TODO Auto-generated method stub
        return this.dao.getAll(Duty.class);
    }

    @Override
    public void save(Duty duty) {
        // TODO Auto-generated method stub
        this.dao.save(duty);
    }

    @Override
    public Set<Duty> getDutyByRoleUuid(String roleUuid) {
        List<DutyRole> dutyRoles = dutyRoleService.getDutyRoleByRoleUuid(roleUuid);
        Set<Duty> dutys = new HashSet<Duty>();
        for (DutyRole dutyRole : dutyRoles) {
            dutys.add(this.getByUuid(dutyRole.getDutyRoleId().getDutyUuid()));
        }
        return dutys;
    }

    /**
     * @param id
     * @return
     */
    public Duty getByUuId(String uuid) {
        return this.dao.findUniqueBy(Duty.class, "uuid", uuid);
    }

    /**
     * @param id
     * @return
     */
    public Duty getById(String id) {
        return this.dao.findUniqueBy(Duty.class, "id", id);
    }

    public List<Duty> getAllDutys() {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("tenantId", SpringSecurityUtils.getCurrentTenantId());
        return this.dao.query(GET_ALL_DUTY, values);
    }

    public List<Duty> getAllDutysOrderByExtenId() {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("tenantId", SpringSecurityUtils.getCurrentTenantId());
        return this.dao.query(GET_ALL_DUTY_ORDER_BY_EXTENID, values);
    }

    @Override
    public List<Duty> namedQuery(String string, Map<String, Object> values, Class<Duty> class1, PagingInfo pagingInfo) {
        // TODO Auto-generated method stub
        return this.nativeDao.namedQuery(string, values, class1, pagingInfo);
    }

    @Override
    public Select2QueryData loadSelectData(Select2QueryInfo queryInfo) {
        List<Duty> list = this.getAllDutysOrderByExtenId();

        Select2QueryData result = new Select2QueryData();
        for (Duty duty : list) {
            Select2DataBean bean = new Select2DataBean(duty.getUuid(), duty.getName() + "【" + duty.getDutyLevel() + "】");
            result.addResultData(bean);
        }
        return result;
    }

    @Override
    public Select2QueryData loadSelectDataByIds(Select2QueryInfo queryInfo) {
        return null;
    }
}
