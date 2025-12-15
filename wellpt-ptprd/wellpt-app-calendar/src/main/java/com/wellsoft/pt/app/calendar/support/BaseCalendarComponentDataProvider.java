package com.wellsoft.pt.app.calendar.support;

import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.pt.app.calendar.entity.AppBaseCalendarEventEntity;
import com.wellsoft.pt.app.calendar.service.BaseCalendarEventService;
import com.wellsoft.pt.basicdata.viewcomponent.facade.service.CalendarEventService;
import com.wellsoft.pt.basicdata.viewcomponent.support.AbsCalendarComponentDataProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BaseCalendarComponentDataProvider extends AbsCalendarComponentDataProvider<AppBaseCalendarEventEntity> {

    @Autowired
    private BaseCalendarEventService eventService;

    @Override
    public String getQueryName() {
        return "APP_基础日历";
    }

    @Override
    public CalendarEventService<AppBaseCalendarEventEntity> getEventService() {
        return eventService;
    }

    @Override
    public String getStatusFieldName() {
        return AppBaseCalendarEventEntity.FIELD_NAME_STATUS;
    }

    @Override
    public String getResourceFieldName() {
        return IdEntity.CREATOR;
    }
}
