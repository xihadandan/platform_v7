package com.wellsoft.pt.unit.service.impl;

import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.unit.dao.CommonUserDao;
import com.wellsoft.pt.unit.entity.CommonUser;
import com.wellsoft.pt.unit.service.CommonUserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CommonUserServiceImpl extends BaseServiceImpl implements CommonUserService {
    @Autowired
    private CommonUserDao commonUserDao;

    public CommonUser getByCurrentUserId(String userId) {
        if (StringUtils.isBlank(userId)) {
            return null;
        }
        UserDetails userDetail = SpringSecurityUtils.getCurrentUser();
        return this.commonUserDao.getByCurrentUserId(userId, userDetail.getTenantId());
    }
}
