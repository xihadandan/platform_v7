package com.wellsoft.pt.org.service.impl;

import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.org.service.HuoQuLeaderService;
import com.wellsoft.pt.org.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class HuoQuLeaderServiceImpl extends BaseServiceImpl implements HuoQuLeaderService {

    @Autowired
    private UserService userService;

    // 1 获取上级领导的姓名
    @Override
    public String getSuperLeadName(String userId) {

        List<String> leaderIds = new ArrayList<String>();
        leaderIds = userService.getUserLeaderIds(userId);

        Iterator<String> it = leaderIds.iterator();
        StringBuilder LeaderName = new StringBuilder();
        while (it.hasNext()) {
            LeaderName.append(userService.getById(it.next()).getUserName());
            if (it.hasNext()) {
                LeaderName.append(Separator.SEMICOLON.getValue());
            }
        }
        System.out.println(LeaderName.toString());
        return LeaderName.toString();
    }

    // 2 获取职位汇报对象
    @Override
    public String getReportLeaderName(String userId) {
        Set<String> reportLeaderIds = new HashSet<String>();
        reportLeaderIds = userService.getReportLeaderIds(userId);

        Iterator<String> it = reportLeaderIds.iterator();
        StringBuilder reportLeaderName = new StringBuilder();
        while (it.hasNext()) {
            reportLeaderName.append(userService.getById(it.next()).getUserName());
            if (it.hasNext()) {
                reportLeaderName.append(Separator.SEMICOLON.getValue());
            }
        }
        return reportLeaderName.toString();
    }

    // 3 获取所有领导的姓名
    @Override
    public String getAllLeadName(String userId) {

        Set<String> allLeaderIds = new HashSet<String>();
        allLeaderIds = userService.getAllUserLeaderIds_new(userId);

        Iterator<String> it = allLeaderIds.iterator();
        StringBuilder allleaderName = new StringBuilder();
        while (it.hasNext()) {
            allleaderName.append(userService.getById(it.next()).getUserName());
            if (it.hasNext()) {
                allleaderName.append(Separator.SEMICOLON.getValue());
            }
        }
        return allleaderName.toString();
    }
}
