package com.wellsoft.pt.basicdata.viewcomponent.facade.service.impl;

import com.wellsoft.context.component.select2.Select2DataBean;
import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.basicdata.viewcomponent.facade.service.CalendarComponentService;
import com.wellsoft.pt.basicdata.viewcomponent.facade.support.calendar.CalendarComponentDataProvider;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@SuppressWarnings("rawtypes")
@Transactional(readOnly = false)
public class CalendarComponentServiceImpl extends BaseServiceImpl implements CalendarComponentService {

    @Autowired(required = false)
    private List<CalendarComponentDataProvider> calendarComponentDataProviders;

    @Override
    public Select2QueryData loadCalendarComponent(Select2QueryInfo query) {
        Select2QueryData result = new Select2QueryData();
        if (CollectionUtils.isNotEmpty(calendarComponentDataProviders)) {
            for (CalendarComponentDataProvider dataProvider : calendarComponentDataProviders) {
                String className = dataProvider.getClass().getName();
                int pos = className.indexOf("$");
                className = className.substring(0, pos);
                result.addResultData(new Select2DataBean(className, dataProvider.getQueryName()));
            }
        }
        return result;
    }

    @Override
    public Select2QueryData loadColumnsSelectData(String dataProviderId) {
        Select2QueryData result = new Select2QueryData();
        if (StringUtils.isBlank(dataProviderId)) {
            return result;
        }
        CalendarComponentDataProvider dataProvider = this.getComponentDataProvider(dataProviderId);
        CriteriaMetadata criteriaMetadata = dataProvider.getColumns();
        if (null != criteriaMetadata) {
            int resultLength = criteriaMetadata.length();
            for (int j = 0; j < resultLength; j++) {
                String columnIndex = criteriaMetadata.getColumnIndex(j);
                String comment = criteriaMetadata.getComment(j);
                result.addResultData(new Select2DataBean(columnIndex, comment));
            }
        }
        return result;
    }

    private CalendarComponentDataProvider getComponentDataProvider(String dataProviderId) {
        CalendarComponentDataProvider dataProvider;
        try {
            dataProvider = (CalendarComponentDataProvider) ApplicationContextHolder.getBean(Class.forName(dataProviderId));
            return dataProvider;
        } catch (ClassNotFoundException e) {
            logger.error(e.getMessage(), e);
        }
        return null;

    }
}
