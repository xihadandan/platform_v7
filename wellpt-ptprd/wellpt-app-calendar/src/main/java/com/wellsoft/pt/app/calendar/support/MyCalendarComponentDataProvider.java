package com.wellsoft.pt.app.calendar.support;

import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.pt.app.calendar.entity.MyCalendarEventEntity;
import com.wellsoft.pt.app.calendar.service.MyCalendarEventService;
import com.wellsoft.pt.basicdata.viewcomponent.facade.service.CalendarEventService;
import com.wellsoft.pt.basicdata.viewcomponent.support.AbsCalendarComponentDataProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MyCalendarComponentDataProvider extends AbsCalendarComponentDataProvider<MyCalendarEventEntity> {

    @Autowired
    private MyCalendarEventService eventService;

    @Override
    public String getQueryName() {
        return "APP_我的日程管理";
    }

    @Override
    public CalendarEventService<MyCalendarEventEntity> getEventService() {
        return eventService;
    }

    @Override
    public String getStatusFieldName() {
        return MyCalendarEventEntity.FIELD_NAME_STATUS;
    }

    @Override
    public String getResourceFieldName() {
        return IdEntity.CREATOR;
    }
}
