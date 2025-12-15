package com.wellsoft.pt.webmail.dao.impl;

import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import com.wellsoft.pt.webmail.dao.WmMailboxInfoUserDao;
import com.wellsoft.pt.webmail.entity.WmMailboxInfoUser;
import org.springframework.stereotype.Repository;

/**
 * @Auther: yt
 * @Date: 2022/2/12 16:30
 * @Description:
 */
@Repository
public class WmMailboxInfoUserDaoImpl extends AbstractJpaDaoImpl<WmMailboxInfoUser, String> implements WmMailboxInfoUserDao {
}
