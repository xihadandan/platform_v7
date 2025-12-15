package com.wellsoft.pt.message.dao.impl;

import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import com.wellsoft.pt.message.dao.UserPersonaliseDao;
import com.wellsoft.pt.message.entity.UserPersonalise;
import org.springframework.stereotype.Repository;

/**
 * @author yt
 * @title: UserPersonaliseDaoImpl
 * @date 2020/5/18 8:59 下午
 */
@Repository
public class UserPersonaliseDaoImpl extends AbstractJpaDaoImpl<UserPersonalise, String> implements UserPersonaliseDao {
}
