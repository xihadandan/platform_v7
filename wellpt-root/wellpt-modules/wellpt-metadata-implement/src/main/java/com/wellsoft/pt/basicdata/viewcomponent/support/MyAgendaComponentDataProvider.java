package com.wellsoft.pt.basicdata.viewcomponent.support;

import com.wellsoft.pt.basicdata.viewcomponent.facade.service.CalendarEventService;
import com.wellsoft.pt.basicdata.viewcomponent.facade.service.MyAgendaEventService;
import com.wellsoft.pt.basicdata.viewcomponent.facade.support.calendar.MyAgendaEventEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MyAgendaComponentDataProvider extends AbsCalendarComponentDataProvider<MyAgendaEventEntity> {

    @Autowired
    private MyAgendaEventService eventService;

    @Override
    public String getQueryName() {
        return "我的日程";
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.viewcomponent.facade.support.calendar.CalendarComponentDataProvider#getEventService()
     */
    @Override
    public CalendarEventService<MyAgendaEventEntity> getEventService() {
        return eventService;
    }

}
