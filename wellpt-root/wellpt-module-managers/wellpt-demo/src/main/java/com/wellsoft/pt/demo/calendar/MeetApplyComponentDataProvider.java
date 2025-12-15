package com.wellsoft.pt.demo.calendar;

import com.wellsoft.pt.basicdata.datastore.support.DataStoreColumn;
import com.wellsoft.pt.basicdata.viewcomponent.facade.service.CalendarEventService;
import com.wellsoft.pt.basicdata.viewcomponent.facade.support.calendar.CalendarEventEntity;
import com.wellsoft.pt.basicdata.viewcomponent.facade.support.calendar.CalendarEventParams;
import com.wellsoft.pt.basicdata.viewcomponent.support.AbsCalendarComponentDataProvider;
import com.wellsoft.pt.jpa.criteria.Criteria;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.dao.NativeDao;
import com.wellsoft.pt.jpa.dao.UniversalDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MeetApplyComponentDataProvider extends AbsCalendarComponentDataProvider<MeetApplyEntity> {

    @Autowired
    private NativeDao nativeDao;

    @Autowired
    private UniversalDao universalDao;

    @Override
    public String getQueryName() {
        return "APP_会议室申请";
    }

    @Override
    public CalendarEventService<MeetApplyEntity> getEventService() {
        return null;
    }

    @Override
    @Transactional
    public List<MeetApplyEntity> loadEvents(CalendarEventParams ep) {
        String tableName = "UF_MEET_APPLY";
        CriteriaMetadata criteriaMetadata = this.getColumns();
        Map<String, DataStoreColumn> columnMap = criteriaMetadataToColumnMap(criteriaMetadata);
        Criteria c = this.nativeDao.createTableCriteria(tableName);
        String whereSql = condition2whereSql(c, ep.getCriterions(), columnMap);

        HashMap<String, Object> params = createLoadEventParams(ep, whereSql);
        String hql = "from MeetApplyEntity t where (( t.startTime between :startTime and :endTime ) or ( t.endTime between :startTime and :endTime ) or ( t.startTime <:startTime and t.endTime >:endTime ))";
        return universalDao.find(hql, params, entityClass);
    }

    @Override
    public String getStatusFieldName() {
        return null;
    }

    @Override
    public String getResourceFieldName() {
        return CalendarEventEntity.BELONG_OBJ_ID;
    }
}
