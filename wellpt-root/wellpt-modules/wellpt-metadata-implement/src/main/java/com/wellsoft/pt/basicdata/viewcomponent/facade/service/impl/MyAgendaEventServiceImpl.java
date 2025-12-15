package com.wellsoft.pt.basicdata.viewcomponent.facade.service.impl;

import com.wellsoft.pt.basicdata.viewcomponent.dao.MyAgendaEventDao;
import com.wellsoft.pt.basicdata.viewcomponent.facade.service.MyAgendaEventService;
import com.wellsoft.pt.basicdata.viewcomponent.facade.support.calendar.CalendarEventParams;
import com.wellsoft.pt.basicdata.viewcomponent.facade.support.calendar.MyAgendaEventEntity;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;

@Service
public class MyAgendaEventServiceImpl extends AbstractJpaServiceImpl<MyAgendaEventEntity, MyAgendaEventDao, String> implements MyAgendaEventService {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    @Transactional
    public String saveEvent(MyAgendaEventEntity e) {
        this.save(e);
        return e.getUuid();
    }

    @Override
    @Transactional
    public void deleteEvent(String uuid) {
        this.delete(uuid);
    }

    @Override
    public MyAgendaEventEntity getEvent(String uuid) {
        return this.getOne(uuid);
    }

    @Override
    public List<MyAgendaEventEntity> loadEvents(CalendarEventParams ep, HashMap<String, Object> params) {
        return this.listByNameHQLQuery("queryMyAgendaEventList", params);
    }

    @Override
    public String getTableName() {
        return "my_agenda_event";
    }
}
