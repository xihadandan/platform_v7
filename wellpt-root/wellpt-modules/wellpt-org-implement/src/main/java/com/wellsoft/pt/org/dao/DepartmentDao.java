package com.wellsoft.pt.org.dao;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.wellsoft.pt.org.entity.Department;
import com.wellsoft.pt.org.entity.Job;
import com.wellsoft.pt.org.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class DepartmentDao extends OrgHibernateDao<Department, String> {
    private static final String QUERY_GROUP_BY_DEPID = "select g from Group g left join g.departments o where o.uuid=?";
    private static final String QUERY_USER_BY_DEPID = "select u from Department o ,DepartmentUser ouj,User u where ouj.user = u and ouj.department =o and o.uuid=? ";
    private static final String QUERY_CHILDREN_BY_ID = "select o from Department o where o.parent.id = ?";
    private static final String QUERY_CHILDREN = "select o from Department o where o.parent.uuid = :uuid";
    private static final String QUERY_TOPLEVEL_DEPARTMENT = "select o from Department o where o.parent.uuid is null or o.parent.uuid =''";
    private static final String QUERY_DEP_BY_USERID = "select o from Department o ,DepartmentUser ouj,User u where ouj.user = u and ouj.department =o and u.uuid=?";
    private static final String GET_DEPTMENT_ID_LIKE_NAME = "select id from Department department where department.name like '%' || :name || '%' or department.shortName like '%' || :name || '%'";
    private static final String QUERY_CHILDREN_IDS_BY_ID = "select id from Department department where department.parent.id = :departmentId";
    private static final String QUERY_PARENT_DEPARTMENT = "select parent from Department d where d.uuid= :deptUuid";
    private static final String GET_UUIDS_BY_UUID = "select d.uuid from Department d where d.parent.uuid=:uuid and (d.isActive=1 or d.isActive is null)";
    private Logger logger = LoggerFactory.getLogger(DepartmentDao.class);

    public List<Department> getChildrenById(String id) {
        return this.find(QUERY_CHILDREN_BY_ID, id);
    }

    public List<Department> getTopDepartment() {
        String hql = "select o from Department o where o.parent.uuid is null";
        return this.find(hql);
    }

    public Department getByPath(String path) {
        String[] paths = path.split("/");
        int length = paths.length;
        String orgpath = paths[length - 1];
        List<String> pathlist = Lists.newArrayList();
        // 倒序处理
        for (int i = length - 2; i >= 0; i--) {
            pathlist.add(paths[i]);
        }
        String hql = "select o from Department o where o.name = '" + orgpath + "'";
        String parent = "o";
        for (String o : pathlist) {
            parent = parent + ".parentDep";
            hql = hql + " and " + parent + ".name ='" + o + "'";
        }

        Department dep = null;
        try {
            dep = this.findUnique(hql);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        return dep;
    }

    /**
     * @param @param  uuid
     * @param @return 设定文件
     * @return List<Organization> 返回类型
     * @throws
     * @Title: getAllChildren
     * @Description: 级联返回部门所有下属部门
     */
    public List<Department> getAllChildren(String id) {
        List<Department> allchildren = Lists.newArrayList();
        for (Department node : getChildrenById(id)) {
            iteratorChild(node, allchildren);
        }
        return allchildren;
    }

    /**
     * @param @param  uuid
     * @param @return 设定文件
     * @return Set<User> 返回类型
     * @throws
     * @Title: getAllUser
     * @Description: 返回所有部门下的用户，包括子部门下的用户，这里使用set来处理,解决重复问题
     */
    public Set<User> getAllUserByUUID(String uuid) {
        // 添加本部门用户
        Department dept = get(uuid);
        Set<User> users = Sets.newHashSet();
        users.addAll(getUser(uuid));
        // 添加所有子部门用户
        List<Department> allOrg = this.getAllChildren(uuid);
        for (Department org : allOrg) {
            users.addAll(getUser(org.getUuid()));
        }
        return users;
    }

    /**
     * @param @param  uuid
     * @param @return 设定文件
     * @return String 返回类型
     * @throws
     * @Title: getPath
     * @Description: 获取路径
     */
    public String getFullPath(String uuid) {
        Department department = this.get(uuid);
        String path = department.getName();
        Department parent = department.getParent();
        while (parent != null) {
            path = parent.getName() + "/" + path;
            parent = parent.getParent();
        }
        return path;
    }

    /**
     * @param @param  orguuid
     * @param @return 设定文件
     * @return List<User> 返回类型
     * @throws
     * @Title: getUser
     * @Description: 获取本部门下用户
     */
    public List<User> getUser(String deptuuid) {
        List<User> users = this.createQuery(QUERY_USER_BY_DEPID, deptuuid).list();
        return users;
    }

    public List<Department> getDeptsByUser(String useruuid) {
        List<Department> depts = createQuery(QUERY_DEP_BY_USERID, useruuid).list();
        return depts;
    }

    public List<User> getSameDeptUsers(String useruuid) {
        List<Department> depts = getDeptsByUser(useruuid);
        List<User> users = Lists.newArrayList();
        for (Department dept : depts) {
            users.addAll(getUser(dept.getUuid()));
        }
        return users;
    }

    // 遍历部门
    private void iteratorChild(Department node, List<Department> allchildren) {
        if (node != null) {
            allchildren.add(node);
            for (Department child : getChildrenById(node.getUuid())) {
                if (getChildrenById(child.getUuid()).size() > 0) {
                    // 进入递归处理
                    iteratorChild(child, allchildren);
                } else {
                    // 处理没有子部门
                    if (!allchildren.contains(child)) {
                        allchildren.add(child);
                    }
                }
            }
        }
    }

    public void addChild(String uuid, Department child) {
        Department dep = get(uuid);
        child.setParent(dep);
        this.save(child);
    }

    public void addChild(Department dep, Department child) {
        child.setParent(dep);
        this.save(child);
    }

    public List<Department> getTopLevel() {
        return this.find(QUERY_TOPLEVEL_DEPARTMENT);
    }

    // public List<Group> getGroupByDep(String uuid) {
    // Department dep = get(uuid);
    // List<Group> groups = Lists.newArrayList();
    // if (dep != null) {
    // // 查询出拥有部门的群组
    // groups = createQuery(QUERY_GROUP_BY_DEPID, uuid).list();
    // }
    // return groups;
    // }

    /**
     * @return
     */
    public Department getById(String id) {
        return this.findUniqueBy("id", id);
    }

    public List<Department> getChildren(String uuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("uuid", uuid);
        return this.find(QUERY_CHILDREN, values);
    }

    /**
     * 如何描述该方法
     *
     * @param rawName
     * @return
     */
    public List<String> getDeptmentIdsLikeName(String rawName) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("name", rawName);
        return this.find(GET_DEPTMENT_ID_LIKE_NAME, values);
    }

    /**
     * @param id
     * @param childrenIds
     */
    public void traverseAndAddChildrenIds(String departmentId, Collection<String> childrenIds) {
        List<String> ids = getChildrenIdsById(departmentId);
        childrenIds.addAll(ids);
        for (String id : ids) {
            traverseAndAddChildrenIds(id, childrenIds);
        }
    }

    /**
     * @param departmentId
     * @return
     */
    private List<String> getChildrenIdsById(String departmentId) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("departmentId", departmentId);
        return this.find(QUERY_CHILDREN_IDS_BY_ID, values);
    }

    /**
     * 获得该部门下的所有用户(包括子部门)
     *
     * @param department
     */
    public List<User> getAllUserByDepartmentPath(String deptPath) {
        String UserjobsSql = "select u from User u,DepartmentUserJob dept_user_job where dept_user_job.department.path like '"
                + deptPath + "%' and u=dept_user_job.user order by dept_user_job.user.code asc";
        List<User> users = this.find(UserjobsSql);
        return users;
    }

    /**
     * 获得该部门下的所有用户(包括子部门)
     *
     * @param department
     */
    public List<Job> getAllJobByDepartmentPath(String deptPath) {
        String jobsSql = "select j from Job j,Department dept where dept.path like '" + deptPath
                + "%' and j.departmentUuid=dept.uuid order by j.code asc,j.name ";
        List<Job> jobs = this.find(jobsSql);
        return jobs;
    }

    public List<Department> getAllChildrenDeptByPath(String deptPath) {
        String qrySql = "select dept from Department dept where dept.path like '" + deptPath + "%'  ";
        List<Department> depts = this.find(qrySql);
        return depts;
    }

    /**
     * 由部门uuid获取上一级部门
     *
     * @param deptUuid
     * @return
     */
    public List<Department> getParentDeptByUuid(String deptUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("deptUuid", deptUuid);
        return this.find(QUERY_PARENT_DEPARTMENT, values);
    }

    public List<String> getChildDeptUuidsByUuid(String uuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("uuid", uuid);
        return find(GET_UUIDS_BY_UUID, values);
    }
}
