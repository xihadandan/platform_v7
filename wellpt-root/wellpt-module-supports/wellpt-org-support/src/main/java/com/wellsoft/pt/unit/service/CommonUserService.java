package com.wellsoft.pt.unit.service;

import com.wellsoft.pt.unit.entity.CommonUser;

public interface CommonUserService {
    CommonUser getByCurrentUserId(String userId);
}
