/*
 * @(#)2012-11-12 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.workhour.service.impl;

import com.wellsoft.context.component.jqgrid.JqGridQueryData;
import com.wellsoft.context.component.jqgrid.JqGridQueryInfo;
import com.wellsoft.pt.basicdata.workhour.bean.WorkHourBean;
import com.wellsoft.pt.basicdata.workhour.dao.WorkHourDao;
import com.wellsoft.pt.basicdata.workhour.entity.WorkHour;
import com.wellsoft.pt.basicdata.workhour.enums.WorkUnit;
import com.wellsoft.pt.basicdata.workhour.service.WorkHourService;
import com.wellsoft.pt.basicdata.workhour.support.WorkHourContext;
import com.wellsoft.pt.basicdata.workhour.support.WorkHourUtils;
import com.wellsoft.pt.basicdata.workhour.support.WorkPeriod;
import com.wellsoft.pt.basicdata.workhour.support.WorkingHour;
import com.wellsoft.pt.bpm.engine.timer.support.TimerUnit;
import com.wellsoft.pt.cache.config.CacheName;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-11-12.1	zhulh		2012-11-12		Create
 * </pre>
 * @date 2012-11-12
 */
@Service
public class WorkHourServiceImpl extends AbstractJpaServiceImpl<WorkHour, WorkHourDao, String> implements
        WorkHourService {

    /**
     * 固定节假日列表查询
     *
     * @param queryInfo
     * @return
     */
    @Override
    public JqGridQueryData queryFixedHolidaysList(JqGridQueryInfo queryInfo) {
        String unitId = SpringSecurityUtils.getCurrentUserUnitId();
        String hql = "from WorkHour where unitId = '" + unitId + "' and type = 'Fixed'";
        Map<String, Object> paramMap = new HashMap<String, Object>();
        List<WorkHour> workHours = dao.listByHQL(hql, paramMap);
        List<WorkHour> jqUsers = new ArrayList<WorkHour>();
        for (WorkHour workHour : workHours) {
            WorkHour jqWorkHour = new WorkHour();
            BeanUtils.copyProperties(workHour, jqWorkHour);
            jqUsers.add(jqWorkHour);
        }
        JqGridQueryData queryData = new JqGridQueryData();
        queryData.setCurrentPage(queryInfo.getPage());
        queryData.setDataList(jqUsers);
        queryData.setRepeatitems(false);
        return queryData;
    }

    /**
     * 特殊节假日列表查询
     *
     * @param queryInfo
     * @return
     */
    @Override
    public JqGridQueryData querySpecialHolidaysList(JqGridQueryInfo queryInfo) {
        String unitId = SpringSecurityUtils.getCurrentUserUnitId();
        String hql = "from WorkHour where unitId = '" + unitId + "' and type = 'Special'";
        Map<String, Object> paramMap = new HashMap<String, Object>();
        List<WorkHour> workHours = dao.listByHQL(hql, paramMap);
        List<WorkHour> jqUsers = new ArrayList<WorkHour>();
        for (WorkHour workHour : workHours) {
            WorkHour jqWorkHour = new WorkHour();
            BeanUtils.copyProperties(workHour, jqWorkHour);
            jqUsers.add(jqWorkHour);
        }
        JqGridQueryData queryData = new JqGridQueryData();
        queryData.setCurrentPage(queryInfo.getPage());
        queryData.setDataList(jqUsers);
        queryData.setRepeatitems(false);
        return queryData;
    }

    /**
     * 补班日期列表查询
     *
     * @param queryInfo
     * @return
     */
    @Override
    public JqGridQueryData queryMakeUpList(JqGridQueryInfo queryInfo) {
        String unitId = SpringSecurityUtils.getCurrentUserUnitId();
        String hql = "from WorkHour where unitId = '" + unitId + "' and type = 'Makeup'";
        Map<String, Object> paramMap = new HashMap<String, Object>();
        List<WorkHour> workHours = dao.listByHQL(hql, paramMap);
        List<WorkHour> jqUsers = new ArrayList<WorkHour>();
        for (WorkHour workHour : workHours) {
            WorkHour jqWorkHour = new WorkHour();
            BeanUtils.copyProperties(workHour, jqWorkHour);
            jqUsers.add(jqWorkHour);
        }
        JqGridQueryData queryData = new JqGridQueryData();
        queryData.setCurrentPage(queryInfo.getPage());
        queryData.setDataList(jqUsers);
        queryData.setRepeatitems(false);
        return queryData;
    }

    /**
     * 保存工作时间
     *
     * @return
     */
    @RequestMapping(value = "/save")
    @CacheEvict(value = CacheName.DEFAULT, allEntries = true)
    @Transactional
    public WorkHour saveBean(WorkHourBean bean) {
        String unitId = SpringSecurityUtils.getCurrentUserUnitId();
        // 删除固定节假日
        Set<WorkHourBean> deletedFixedHolidays = bean.getDeletedFixedHolidays();
        for (WorkHourBean deletedFixedHoliday : deletedFixedHolidays) {
            WorkHour workHour1 = this.dao.getOne(deletedFixedHoliday.getUuid());
            dao.delete(workHour1);
        }
        // 删除特殊节假日
        Set<WorkHourBean> deletedSpecialHolidays = bean.getDeletedSpecialHolidays();
        for (WorkHourBean deletedSpecialHoliday : deletedSpecialHolidays) {
            WorkHour workHour2 = this.dao.getOne(deletedSpecialHoliday.getUuid());
            dao.delete(workHour2);
        }
        // 删除补班日期
        Set<WorkHourBean> deletedMakeups = bean.getDeletedMakeups();
        for (WorkHourBean deletedMakeup : deletedMakeups) {
            WorkHour workHour3 = this.dao.getOne(deletedMakeup.getUuid());
            dao.delete(workHour3);
        }

        // 保存固定节假日
        Set<WorkHourBean> fixedHolidays = bean.getChangedFixedHolidays();
        for (WorkHourBean fixedHoliday : fixedHolidays) {
            WorkHour workHour1 = new WorkHour();
            if (StringUtils.isBlank(fixedHoliday.getUuid())) {
                fixedHoliday.setUuid(null);
            } else {
                workHour1 = this.dao.getOne(fixedHoliday.getUuid());
            }
            BeanUtils.copyProperties(fixedHoliday, workHour1);
            workHour1.setType(WorkHour.TYPE_FIXED_HOLIDAYS);
            workHour1.setCode(WorkHour.TYPE_FIXED_HOLIDAYS);
            workHour1.setIsWorkday(Boolean.FALSE);
            workHour1.setUnitId(unitId);
            this.dao.save(workHour1);
        }
        // 保存特殊节假日
        Set<WorkHourBean> specialHolidays = bean.getChangedSpecialHolidays();
        for (WorkHourBean specialHoliday : specialHolidays) {
            WorkHour workHour2 = new WorkHour();
            if (StringUtils.isBlank(specialHoliday.getUuid())) {
                specialHoliday.setUuid(null);
            } else {
                workHour2 = this.dao.getOne(specialHoliday.getUuid());
            }
            BeanUtils.copyProperties(specialHoliday, workHour2);
            workHour2.setType(WorkHour.TYPE_SPECIAL_HOLIDAYS);
            workHour2.setCode(WorkHour.TYPE_SPECIAL_HOLIDAYS);
            workHour2.setUnitId(unitId);
            workHour2.setIsWorkday(Boolean.FALSE);
            workHour2.setUnitId(unitId);
            this.dao.save(workHour2);
        }
        // 保存补班日期
        Set<WorkHourBean> makeups = bean.getChangedMakeups();
        for (WorkHourBean makeup : makeups) {
            WorkHour workHour3 = new WorkHour();
            if (StringUtils.isBlank(makeup.getUuid())) {
                makeup.setUuid(null);
            } else {
                workHour3 = this.dao.getOne(makeup.getUuid());
            }
            BeanUtils.copyProperties(makeup, workHour3);
            workHour3.setType(WorkHour.TYPE_MAKE_UP);
            workHour3.setCode(WorkHour.TYPE_MAKE_UP);
            workHour3.setIsWorkday(Boolean.FALSE);
            workHour3.setUnitId(unitId);
            this.dao.save(workHour3);
        }
        return null;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.workhour.service.WorkHourService#removeByPk(java.lang.String)
     */
    @Override
    @CacheEvict(value = CacheName.DEFAULT, allEntries = true)
    @Transactional
    public void removeByPk(String uid) {
        dao.delete(uid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.workhour.service.WorkHourService#getWorkingHour(java.util.Date)
     */
    @Override
    public WorkingHour getWorkingHour(Date date) {
        return WorkHourUtils.getWorkingHour(date);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.workhour.service.WorkHourService#getEffectiveWorkingHour(java.util.Date)
     */
    @Override
    public WorkingHour getEffectiveWorkingHour(Date date) {
        return WorkHourUtils.getEffectiveWorkingHour(date);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.workhour.service.WorkHourService#isWorkHour(java.util.Date)
     */
    @Override
    public boolean isWorkHour(Date date) {
        return WorkHourUtils.isWorkHour(date);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.workhour.service.WorkHourService#isWorkDay(java.util.Date)
     */
    @Override
    public boolean isWorkDay(Date date) {
        return WorkHourUtils.isWorkDay(date);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.workhour.service.WorkHourService#getWorkDate(java.util.Date, java.lang.Double, java.lang.Integer)
     */
    @Override
    public Date getWorkDate(Date date, Double amount, WorkUnit workingUnit) {
        return WorkHourUtils.getWorkDate(date, amount, workingUnit);
    }

    @Override
    public Date getWorkDate(Date date, Double amount, Integer workingUnit) {
        Date workDate = null;
        switch (workingUnit) {
            case TimerUnit.WORKING_DAY:
                workDate = this.getWorkDate(date, amount, WorkUnit.WorkingDay);
                break;
            case TimerUnit.WORKING_HOUR:
                workDate = this.getWorkDate(date, amount, WorkUnit.WorkingHour);
                break;
            case TimerUnit.WORKING_MINUTE:
                workDate = this.getWorkDate(date, amount, WorkUnit.WorkingMinute);
                break;
            default:
                break;
        }
        return workDate;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.workhour.service.WorkHourService#getWorkPeriod(java.util.Date, java.util.Date)
     */
    @Override
    public WorkPeriod getWorkPeriod(Date fromTime, Date toTime) {
        return WorkHourUtils.getWorkPeriod(fromTime, toTime);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.workhour.service.WorkHourService#getNextWorkDate(java.util.Date)
     */
    @Override
    public Date getNextWorkDate(Date date) {
        return WorkHourUtils.getNextWorkDate(date);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.workhour.service.WorkHourService#getPrevWorkDate(java.util.Date)
     */
    @Override
    public Date getPrevWorkDate(Date date) {
        boolean isInitiator = WorkHourContext.isInitiator();
        try {
            Date prevDate = WorkHourUtils.getPrevWorkDate(date);
            boolean flag = WorkHourUtils.isWorkDay(prevDate);
            if (!flag) {
                prevDate = getPrevWorkDate(prevDate);
            }
            return prevDate;
        } catch (Exception e) {
        } finally {
            if (isInitiator) {
                WorkHourContext.clear();
            }
        }
        return null;

    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.workhour.service.WorkHourService#getWorkDatesByMonth(java.lang.String)
     */
    @Override
    public List<Date> getWorkDatesByMonth(String month) {
        List<Date> workDates = new ArrayList<Date>();
        List<Date> lDates = WorkHourUtils.getDatesByMonth(month);
        for (Date date : lDates) {
            boolean flag = WorkHourUtils.isWorkDay(date);
            if (flag) {
                workDates.add(date);
            }
        }
        return workDates;
    }

    @Override
    public WorkHour getByCode(String code) {
        return this.dao.getOneByHQL("from WorkHour where code='" + code + "'", null);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.workhour.service.WorkHourService#getByCodeAndUnit(java.lang.String, java.lang.String)
     */
    @Override
    public WorkHour getByCodeAndUnitId(String code, String unitId) {
        return this.dao.getOneByHQL("from WorkHour where code='" + code + "' and unitId='" + unitId + "'", null);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.workhour.service.WorkHourService#getWorkDayList(java.lang.String)
     */
    @Override
    public List<WorkHour> getWorkDayList(String unitId) {
        String defaultHql = "from WorkHour where unitId = 'default' and type='Workday'";
        String hql = "from WorkHour where unitId = '" + unitId + "' and type ='Workday'";
        List<WorkHour> list = new ArrayList<WorkHour>();
        Map<String, Object> paramMap = new HashMap<String, Object>();
        list = listByHQL(hql, paramMap);
        if (list == null || list.size() == 0) {
            list = listByHQL(defaultHql, paramMap);
        }
        return list;
    }
}
