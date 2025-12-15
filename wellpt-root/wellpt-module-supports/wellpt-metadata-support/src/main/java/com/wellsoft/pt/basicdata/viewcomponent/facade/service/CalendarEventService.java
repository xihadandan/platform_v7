package com.wellsoft.pt.basicdata.viewcomponent.facade.service;

import com.wellsoft.pt.basicdata.viewcomponent.facade.support.calendar.CalendarEventEntity;
import com.wellsoft.pt.basicdata.viewcomponent.facade.support.calendar.CalendarEventParams;

import java.util.HashMap;
import java.util.List;

public interface CalendarEventService<E extends CalendarEventEntity> {

    List<E> loadEvents(CalendarEventParams ep, HashMap<String, Object> params);

    String saveEvent(E e);

    void deleteEvent(String uuid);

    E getEvent(String uuid);

    String getTableName();

}
