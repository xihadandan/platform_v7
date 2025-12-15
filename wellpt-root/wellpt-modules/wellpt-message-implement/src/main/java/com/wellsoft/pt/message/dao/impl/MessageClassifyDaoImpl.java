package com.wellsoft.pt.message.dao.impl;

import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import com.wellsoft.pt.message.dao.MessageClassifyDao;
import com.wellsoft.pt.message.entity.MessageClassify;
import org.springframework.stereotype.Repository;

/**
 * @author yt
 * @title: MessageClassifyDaoImpl
 * @date 2020/5/18 8:58 下午
 */
@Repository
public class MessageClassifyDaoImpl extends AbstractJpaDaoImpl<MessageClassify, String> implements MessageClassifyDao {
}
