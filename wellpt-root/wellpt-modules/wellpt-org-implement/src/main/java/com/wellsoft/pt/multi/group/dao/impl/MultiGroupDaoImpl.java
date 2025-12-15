package com.wellsoft.pt.multi.group.dao.impl;

import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import com.wellsoft.pt.multi.group.dao.MultiGroupDao;
import com.wellsoft.pt.multi.group.entity.MultiGroup;
import org.springframework.stereotype.Repository;

/**
 * @Auther: yt
 * @Date: 2022/2/10 10:13
 * @Description:
 */
@Repository
public class MultiGroupDaoImpl extends AbstractJpaDaoImpl<MultiGroup, String> implements MultiGroupDao {
}
