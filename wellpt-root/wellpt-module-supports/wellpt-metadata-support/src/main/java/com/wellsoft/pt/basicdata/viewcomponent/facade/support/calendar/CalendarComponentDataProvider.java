package com.wellsoft.pt.basicdata.viewcomponent.facade.support.calendar;

import com.wellsoft.pt.basicdata.viewcomponent.facade.service.CalendarEventService;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;

import java.util.List;

public interface CalendarComponentDataProvider<E extends CalendarEventEntity> {

    /**
     * 获取事项列定义
     *
     * @return
     */
    CriteriaMetadata getColumns();

    /**
     * 名称
     *
     * @return
     */
    String getQueryName();

    /**
     * 删除一个事项
     *
     * @param uuid
     * @return
     */
    boolean deleteEvent(String uuid);

    /**
     * 查询
     *
     * @param queryContext
     * @return
     */
    List<E> loadEvents(CalendarEventParams ep);

    /**
     * 获取一个事项
     *
     * @param uuid
     * @return
     */
    E getEvent(String uuid);

    /**
     * 保存一个事项
     *
     * @param e
     * @return
     */
    String saveEvent(E e);

    /**
     * 获取状态字段
     *
     * @return
     */
    String getStatusFieldName();

    /**
     * 获取资源字段
     *
     * @return
     */
    String getResourceFieldName();

    Class<E> getEntityClass();

    CalendarEventService<E> getEventService();

}
