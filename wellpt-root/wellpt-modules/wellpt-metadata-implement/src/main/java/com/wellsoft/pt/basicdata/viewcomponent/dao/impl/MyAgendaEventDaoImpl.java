package com.wellsoft.pt.basicdata.viewcomponent.dao.impl;

import com.wellsoft.pt.basicdata.viewcomponent.dao.MyAgendaEventDao;
import com.wellsoft.pt.basicdata.viewcomponent.facade.support.calendar.MyAgendaEventEntity;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import org.springframework.stereotype.Repository;

@Repository
public class MyAgendaEventDaoImpl extends AbstractJpaDaoImpl<MyAgendaEventEntity, String> implements MyAgendaEventDao {

}
