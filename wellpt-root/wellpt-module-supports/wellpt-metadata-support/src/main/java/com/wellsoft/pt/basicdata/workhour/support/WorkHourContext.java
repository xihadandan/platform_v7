/*
 * @(#)2014-2-7 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.workhour.support;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.basicdata.workhour.entity.WorkHour;
import com.wellsoft.pt.basicdata.workhour.facade.service.WorkHourFacadeService;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.NamedThreadLocal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-2-7.1	zhulh		2014-2-7		Create
 * </pre>
 * @date 2014-2-7
 */
public class WorkHourContext {

    private static final ThreadLocal<WorkHourData> workHourDatas = new NamedThreadLocal<WorkHourData>(
            "WorkHour context");

    private static final ThreadLocal<Boolean> beginContext = new ThreadLocal<>();

    public static List<WorkHour> getAll() {
        List<WorkHour> workDays = getWorkHourData().getWorkHours();
        return workDays == null ? new ArrayList<WorkHour>(0) : workDays;
    }

    public static List<WorkHour> getMakeups() {
        List<WorkHour> makeups = getWorkHourData().getWorkHourMap().get(
                SpringSecurityUtils.getCurrentUserUnitId() + "_" + WorkHour.TYPE_MAKE_UP);
        return makeups == null ? new ArrayList<WorkHour>(0) : makeups;
    }

    public static List<WorkHour> getFixedHolidays() {
        List<WorkHour> fixedHolidays = getWorkHourData().getWorkHourMap().get(
                SpringSecurityUtils.getCurrentUserUnitId() + "_" +
                        WorkHour.TYPE_FIXED_HOLIDAYS);
        return fixedHolidays == null ? new ArrayList<WorkHour>(0) : fixedHolidays;
    }

    public static List<WorkHour> getSpecialHolidays() {
        List<WorkHour> specialHolidays = getWorkHourData().getWorkHourMap().get(
                SpringSecurityUtils.getCurrentUserUnitId() + "_" +
                        WorkHour.TYPE_SPECIAL_HOLIDAYS);
        return specialHolidays == null ? new ArrayList<WorkHour>(0) : specialHolidays;
    }

    public static List<WorkHour> getWorkDays() {
        List<WorkHour> workDays = getWorkHourData().getWorkHourMap().get(
                SpringSecurityUtils.getCurrentUserUnitId() + "_" + WorkHour.TYPE_WORK_DAY);
        return workDays == null ? new ArrayList<WorkHour>(0) : workDays;
    }

    public static WorkHour getWorkDayByCode(String code) {
        if (StringUtils.isBlank(code)) {
            return null;
        }
        List<WorkHour> workHours = getWorkHourData().getWorkHourMap().get(
                SpringSecurityUtils.getCurrentUserUnitId() + "_" + WorkHour.TYPE_WORK_DAY);
        for (WorkHour workHour : workHours) {
            if (code.equals(workHour.getCode())) {
                return workHour;
            }
        }
        return null;
    }

    public static boolean isWorkDayByCode(String code) {
        if (StringUtils.isBlank(code)) {
            return false;
        }
        List<WorkHour> workHours = getWorkHourData().getWorkHourMap().get(
                SpringSecurityUtils.getCurrentUserUnitId() + "_" + WorkHour.TYPE_WORK_DAY);
        if (CollectionUtils.isNotEmpty(workHours)) {
            for (WorkHour workHour : workHours) {
                if (Boolean.TRUE.equals(workHour.getIsWorkday()) && code.equals(
                        workHour.getCode())) {
                    return true;
                }
            }
        }
        return false;
    }

    private static WorkHourData getWorkHourData() {
        WorkHourData workHourData = workHourDatas.get();
        if (workHourData == null) {
            WorkHourFacadeService workHourService = ApplicationContextHolder.getBean(
                    WorkHourFacadeService.class);
            List<WorkHour> workHours = workHourService.listCurrentUnitWorkHours();
            WorkHourData value = new WorkHourData();
            value.setWorkHours(BeanUtils.convertCollection(workHours, WorkHour.class));
            Map<String, List<WorkHour>> workHourMap = new ConcurrentHashMap<String, List<WorkHour>>();
            String unitId = SpringSecurityUtils.getCurrentUserUnitId();
            for (WorkHour workHour : value.getWorkHours()) {
                String type = unitId + "_" + workHour.getType();
                if (!workHourMap.containsKey(type)) {
                    workHourMap.put(type, new ArrayList<WorkHour>());
                }
                workHourMap.get(type).add(workHour);
            }
            value.setWorkHourMap(workHourMap);
            workHourDatas.set(value);
            workHourData = workHourDatas.get();
        }

        return workHourData;
    }

    /**
     * 判断工作日上下文发起点，为了清除本地线程变量，只有发起点代码块才允许clear线程变量
     *
     * @return
     */
    public static boolean isInitiator() {
        if (beginContext.get() == null) {
            beginContext.set(true);
            return true;
        }
        return false;//非发起点
    }

    public static void clear() {
        workHourDatas.remove();
        beginContext.remove();
    }
}
