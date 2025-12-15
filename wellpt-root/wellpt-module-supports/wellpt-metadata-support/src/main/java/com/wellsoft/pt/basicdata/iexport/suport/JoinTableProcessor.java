package com.wellsoft.pt.basicdata.iexport.suport;

import com.wellsoft.context.jdbc.entity.JpaEntity;
import org.hibernate.Session;

import java.io.Serializable;

/**
 * @author yt
 * @title: JoinTableProcessor
 * @date 2020/9/14 19:35
 */
public interface JoinTableProcessor<T extends JpaEntity<UUID>, UUID extends Serializable> {

    <P extends JpaEntity<UUID>, C extends JpaEntity<UUID>> void joinTableSave(Session session, ProtoDataBeanTree<T, P, C> t);

}
