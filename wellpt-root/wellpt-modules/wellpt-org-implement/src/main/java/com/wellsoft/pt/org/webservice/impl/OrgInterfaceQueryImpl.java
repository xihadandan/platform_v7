package com.wellsoft.pt.org.webservice.impl;

import com.wellsoft.pt.multi.org.entity.MultiOrgGroup;
import com.wellsoft.pt.multi.org.entity.MultiOrgGroupMember;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgGroupFacade;
import com.wellsoft.pt.org.dao.UserDao;
import com.wellsoft.pt.org.entity.*;
import com.wellsoft.pt.org.service.UserService;
import com.wellsoft.pt.org.webservice.service.OrgInterfaceQueryService;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Description: 组织外部接口查询服务实现类
 *
 * @author zhengky
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	       修改人		修改日期	      修改内容
 * 2014-9-19.1  zhengky	2014-9-19	  Create
 * </pre>
 * @date 2014-9-19
 */
@Service
@Transactional
public class OrgInterfaceQueryImpl implements OrgInterfaceQueryService {

    private Logger logger = LoggerFactory.getLogger(OrgInterfaceQueryImpl.class);

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserService userService;

    @Autowired
    private MultiOrgGroupFacade multiOrgGroupFacade;

    @Override
    // TODO 速度优化
    public String getOrgInfoByUserCode(String userCode) {
        long time1 = System.currentTimeMillis();
        JSONObject orgInfo = new JSONObject();

        User user = userService.getByLoginNameIgnoreCase(userCode, null);
        if (user == null) {
            user = userService.getById(userCode);
        }
        if (user == null) {
            List<User> userls = userService.findBy("employeeNumber", userCode);
            if (!userls.isEmpty()) {
                user = userls.get(0);
            }
        }
        if (user == null) {
            return new JSONObject().toString();
        }

        JSONObject orgDept = new JSONObject();
        JSONObject orgDuty = new JSONObject();
        JSONObject orgJob = new JSONObject();
        JSONObject orgGroup = new JSONObject();

        // 返回用户所在的部门
        Set<DepartmentUserJob> departmentUsers = user.getDepartmentUsers();

        Iterator<DepartmentUserJob> deptIt = departmentUsers.iterator();
        while (deptIt.hasNext()) {
            DepartmentUserJob departmentUserJob = deptIt.next();
            JSONObject orgDeptInfo = new JSONObject();
            orgDeptInfo.put("name", departmentUserJob.getDepartment().getPath());
            orgDeptInfo.put("isMajor", departmentUserJob.getIsMajor().toString());
            orgDept.put(departmentUserJob.getDepartment().getId(), orgDeptInfo);
        }

        // 返回用户所在的职位
        // 获得主职位
        Set<UserJob> jobs = user.getUserJobs();

        Iterator<UserJob> jobIt = jobs.iterator();
        while (jobIt.hasNext()) {
            UserJob userjob = jobIt.next();
            JSONObject orgJobInfo = new JSONObject();
            orgJobInfo.put("name", userjob.getJob().getDepartmentName() + "/" + userjob.getJob().getName());
            orgJobInfo.put("isMajor", userjob.getIsMajor().toString());
            orgJob.put(userjob.getJob().getId(), orgJobInfo);

            // 返回用户所在的职务
            Duty duty = userjob.getJob().getDuty();
            JSONObject orgDutyInfo = new JSONObject();
            orgDutyInfo.put("name", duty.getName());
            // 职务会重复，判断一下去掉重复数据.
            if (orgDuty.get(duty.getId()) == null) {
                orgDuty.put(duty.getId(), orgDutyInfo);
            }

        }

        // 增加用户所属群组
        List<MultiOrgGroupMember> groups = this.multiOrgGroupFacade.queryGroupListByMemberId(user.getId());
        for (MultiOrgGroupMember group : groups) {
            MultiOrgGroup g = this.multiOrgGroupFacade.getGroupById(group.getGroupId());
            JSONObject orgGroupInfo = new JSONObject();
            orgGroupInfo.put("name", g.getName());
            orgGroup.put(g.getId(), orgGroupInfo);
        }

        orgInfo.put("Dept", orgDept);
        orgInfo.put("Job", orgJob);
        orgInfo.put("Duty", orgDuty);
        orgInfo.put("Group", orgGroup);
        orgInfo.put("UserName", user.getUserName());
        orgInfo.put("UserId", user.getId());
        orgInfo.put("MainEmail", user.getMainEmail());
        orgInfo.put("OtherEmail", user.getOtherEmail());

        JSONObject myAllleader = getUserLeaderInfo(userService.getAllUserLeaderIds_new(user.getId()));
        JSONObject myleader = getUserLeaderInfo(userService.getUserLeaderIds_new(user.getId()));
        JSONObject mySubordinate = getUserSubordinate(userService.getSubordinate_new(user.getUuid()));

        orgInfo.put("MyLeader", myleader);// 直接领导
        orgInfo.put("MyAllLeader", myAllleader);// 所有领导
        orgInfo.put("MySubordinate", mySubordinate);// 我的下属
        orgInfo.put("MyDirectDepts", getDirectDepartments(userService.getDirectDepartments(user.getId())));// 我的直系部门
        // 返回用户所在职位的职务

        long time2 = System.currentTimeMillis();
        logger.info("getOrgInfoByUserCode UserCode:[" + userCode + "]and Data spent " + (time2 - time1) / 1000.0 + "s");
        return orgInfo.toString();
    }

    private JSONObject getUserSubordinate(List<String> userids) {
        JSONObject rsobj = new JSONObject();
        for (String userid : userids) {
            User allleader = userService.getById(userid);
            rsobj.put(allleader.getId(), allleader.getLoginName());
        }
        return rsobj;
    }

    private JSONObject getUserLeaderInfo(Set<String> userids) {
        JSONObject rsobj = new JSONObject();
        for (String userid : userids) {
            User allleader = userService.getById(userid);
            rsobj.put(allleader.getId(), allleader.getLoginName());
        }
        return rsobj;
    }

    private JSONObject getDirectDepartments(Set<Department> departments) {
        JSONObject rsobj = new JSONObject();
        for (Department department : departments) {
            rsobj.put(department.getId(), department.getPath());
        }
        return rsobj;
    }
}
