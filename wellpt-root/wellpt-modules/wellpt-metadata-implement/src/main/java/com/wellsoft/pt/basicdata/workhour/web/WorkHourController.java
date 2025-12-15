package com.wellsoft.pt.basicdata.workhour.web;

import com.wellsoft.context.component.jqgrid.JqGridQueryData;
import com.wellsoft.context.component.jqgrid.JqGridQueryInfo;
import com.wellsoft.context.util.date.DateUtils;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.basicdata.workhour.entity.WorkHour;
import com.wellsoft.pt.basicdata.workhour.enums.WorkDayEnum;
import com.wellsoft.pt.basicdata.workhour.service.WorkHourService;
import com.wellsoft.pt.basicdata.workhour.support.WorkHourUtils;
import com.wellsoft.pt.basicdata.workhour.support.WorkPeriod;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.ParseException;
import java.util.*;

/**
 * Description: 工作时间控制层
 *
 * @author zhouyq
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-4-17.1	zhouyq		2013-4-17		Create
 * </pre>
 * @date 2013-4-17
 */
@Controller
@RequestMapping("/basicdata/workhour")
public class WorkHourController extends BaseController {

    // 对星期进行排序
    Comparator<WorkHour> comparator = new Comparator<WorkHour>() {
        @Override
        public int compare(WorkHour workhour1, WorkHour workhour2) {
            return workhour1.getSortOrder().compareTo(workhour2.getSortOrder());
        }
    };
    @Autowired
    private WorkHourService workHourService;

    /**
     * 跳转到 工作时间界面
     *
     * @return
     */
    @RequestMapping(value = "")
    public String workHour(Model model) {
        String unitId = SpringSecurityUtils.getCurrentUserUnitId();
        //		List<WorkHour> workHourList = workHourService.listAll();
        List<WorkHour> workDayList = new ArrayList<WorkHour>();
        workDayList = workHourService.getWorkDayList(unitId);
        //		List<WorkHour> fixedHolidaysList = new ArrayList<WorkHour>();
        //		List<WorkHour> specialHolidaysList = new ArrayList<WorkHour>();
        //		List<WorkHour> makeUpList = new ArrayList<WorkHour>();
        //		for (WorkHour workHour : workHourList) {
        //			if ("Special".equals(workHour.getType())) {
        //				System.out.println("特殊节假日：" + workHour.getName());
        //				specialHolidaysList.add(workHour);
        //			} else if ("Fixed".equals(workHour.getType())) {
        //				System.out.println("固定节假日：" + workHour.getName());
        //				fixedHolidaysList.add(workHour);
        //			} else if ("Makeup".equals(workHour.getType())) {
        //				System.out.println("补班日期：" + workHour.getName());
        //				makeUpList.add(workHour);
        //			}
        //		}
        Collections.sort(workDayList, comparator);
        model.addAttribute("workDayList", workDayList);
        //		model.addAttribute("fixedHolidaysList", fixedHolidaysList);
        //		model.addAttribute("specialHolidaysList", specialHolidaysList);
        //		model.addAttribute("makeUpList", makeUpList);
        return forward("/basicdata/workhour/workhour");
    }

    /**
     * 固定节假日列表
     *
     * @param queryInfo
     * @return
     */
    @RequestMapping(value = "/fixedHolidaysList")
    public @ResponseBody
    JqGridQueryData fixedHolidaysListAsJson(JqGridQueryInfo queryInfo) {
        JqGridQueryData queryData = workHourService.queryFixedHolidaysList(queryInfo);
        return queryData;
    }

    /**
     * 特殊节假日列表
     *
     * @param queryInfo
     * @return
     */
    @RequestMapping(value = "/specialHolidaysList")
    public @ResponseBody
    JqGridQueryData specialHolidaysList(JqGridQueryInfo queryInfo) {
        JqGridQueryData queryData = workHourService.querySpecialHolidaysList(queryInfo);
        return queryData;
    }

    /**
     * 补班日期列表
     *
     * @param queryInfo
     * @return
     */
    @RequestMapping(value = "/makeUpList")
    public @ResponseBody
    JqGridQueryData makeUpList(JqGridQueryInfo queryInfo) {
        JqGridQueryData queryData = workHourService.queryMakeUpList(queryInfo);
        return queryData;
    }

    /**
     * 工作日
     *
     * @param fromTime1
     * @param toTime1
     * @param fromTime2
     * @param toTime2
     * @param notCheckedValArray
     * @param checkedValArray
     * @return
     */
    @RequestMapping(value = "/save")
    public String save(@RequestParam("fromTime1") String fromTime1, @RequestParam("toTime1") String toTime1,
                       @RequestParam("fromTime2") String fromTime2, @RequestParam("toTime2") String toTime2,
                       @RequestParam("notCheckedValArray") String[] notCheckedValArray,
                       @RequestParam("checkedValArray") String[] checkedValArray) {
        String userUnitId = SpringSecurityUtils.getCurrentUserUnitId();
        for (String chedkedVal : checkedValArray) {
            // 过滤从jqgrid穿过来的checkbox的value值
            if (!("on".equals(chedkedVal))) {
                WorkHour checkedWorkHour = workHourService.getByCodeAndUnitId(chedkedVal, userUnitId);
                if (checkedWorkHour != null) {
                    checkedWorkHour.setFromTime1(fromTime1);
                    checkedWorkHour.setToTime1(toTime1);
                    checkedWorkHour.setFromTime2(fromTime2);
                    checkedWorkHour.setToTime2(toTime2);
                    checkedWorkHour.setIsWorkday(true);
                    workHourService.save(checkedWorkHour);
                } else {
                    WorkHour checkWorkHour = new WorkHour();
                    checkWorkHour.setFromTime1(fromTime1);
                    checkWorkHour.setToTime1(toTime1);
                    checkWorkHour.setFromTime2(fromTime2);
                    checkWorkHour.setToTime2(toTime2);
                    checkWorkHour.setIsWorkday(true);
                    checkWorkHour.setCode(chedkedVal);
                    checkWorkHour.setUnitId(userUnitId);
                    checkWorkHour.setName(WorkDayEnum.value2remark(chedkedVal));
                    checkWorkHour.setSortOrder(WorkDayEnum.value2Order(chedkedVal));
                    checkWorkHour.setType(WorkHour.TYPE_WORK_DAY);
                    workHourService.save(checkWorkHour);
                }

            }

        }
        for (String notChedkedVal : notCheckedValArray) {
            if (!("on".equals(notChedkedVal))) {
                WorkHour notCheckedWorkHour = workHourService.getByCodeAndUnitId(notChedkedVal, userUnitId);
                if (notCheckedWorkHour != null) {
                    notCheckedWorkHour.setFromTime1(fromTime1);
                    notCheckedWorkHour.setToTime1(toTime1);
                    notCheckedWorkHour.setFromTime2(fromTime2);
                    notCheckedWorkHour.setToTime2(toTime2);
                    notCheckedWorkHour.setIsWorkday(false);
                    workHourService.save(notCheckedWorkHour);
                } else {
                    WorkHour notCheckWorkHour = new WorkHour();
                    notCheckWorkHour.setFromTime1(fromTime1);
                    notCheckWorkHour.setToTime1(toTime1);
                    notCheckWorkHour.setFromTime2(fromTime2);
                    notCheckWorkHour.setToTime2(toTime2);
                    notCheckWorkHour.setIsWorkday(false);
                    notCheckWorkHour.setUnitId(userUnitId);
                    notCheckWorkHour.setCode(notChedkedVal);
                    notCheckWorkHour.setName(WorkDayEnum.value2remark(notChedkedVal));
                    notCheckWorkHour.setSortOrder(WorkDayEnum.value2Order(notChedkedVal));
                    notCheckWorkHour.setType(WorkHour.TYPE_WORK_DAY);
                    workHourService.save(notCheckWorkHour);
                }
            }

        }
        return forward("/basicdata/workhour/workhour");
    }

    @RequestMapping(value = "/test")
    @ResponseBody
    public WorkPeriod test() throws ParseException {
        WorkPeriod workPeriod = WorkHourUtils.getPeriod(DateUtils.parse("2017-11-20 15:49:00"),
                DateUtils.parse("2017-11-20 23:23:00"));
        Calendar cal = Calendar.getInstance();
        cal.setTime(DateUtils.parse("2021-06-18"));
        int week = cal.get(Calendar.DAY_OF_WEEK);
        System.out.println(week);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        Date endTime = cal.getTime();
        Integer afterMniute = 1 * 24 * 60;
        Date startTime = WorkHourUtils.getWorkDateAfterMinuteBy24Rule(endTime, afterMniute);
        System.out.println(startTime);
        afterMniute = 2 * 24 * 60;
        startTime = WorkHourUtils.getWorkDateAfterMinuteBy24Rule(endTime, afterMniute);
        System.out.println(startTime);
        afterMniute = 3 * 24 * 60;
        startTime = WorkHourUtils.getWorkDateAfterMinuteBy24Rule(endTime, afterMniute);
        System.out.println(startTime);
        afterMniute = 4 * 24 * 60;
        startTime = WorkHourUtils.getWorkDateAfterMinuteBy24Rule(endTime, afterMniute);
        System.out.println(startTime);
        afterMniute = 5 * 24 * 60;
        startTime = WorkHourUtils.getWorkDateAfterMinuteBy24Rule(endTime, afterMniute);
        System.out.println(startTime);
        afterMniute = 6 * 24 * 60;
        startTime = WorkHourUtils.getWorkDateAfterMinuteBy24Rule(endTime, afterMniute);
        System.out.println(startTime);
        afterMniute = 7 * 24 * 60;
        startTime = WorkHourUtils.getWorkDateAfterMinuteBy24Rule(endTime, afterMniute);
        System.out.println(startTime);
        afterMniute = 8 * 24 * 60;
        startTime = WorkHourUtils.getWorkDateAfterMinuteBy24Rule(endTime, afterMniute);
        System.out.println(startTime);
        afterMniute = 9 * 24 * 60;
        startTime = WorkHourUtils.getWorkDateAfterMinuteBy24Rule(endTime, afterMniute);
        System.out.println(startTime);
        afterMniute = 10 * 24 * 60;
        startTime = WorkHourUtils.getWorkDateAfterMinuteBy24Rule(endTime, afterMniute);
        System.out.println(startTime);
        System.out.println("----------------------------------------");
        afterMniute = -1 * 24 * 60;
        startTime = WorkHourUtils.getWorkDateAfterMinuteBy24Rule(endTime, afterMniute);
        System.out.println(startTime);
        afterMniute = -2 * 24 * 60;
        startTime = WorkHourUtils.getWorkDateAfterMinuteBy24Rule(endTime, afterMniute);
        System.out.println(startTime);
        afterMniute = -3 * 24 * 60;
        startTime = WorkHourUtils.getWorkDateAfterMinuteBy24Rule(endTime, afterMniute);
        System.out.println(startTime);
        afterMniute = -4 * 24 * 60;
        startTime = WorkHourUtils.getWorkDateAfterMinuteBy24Rule(endTime, afterMniute);
        System.out.println(startTime);
        afterMniute = -5 * 24 * 60;
        startTime = WorkHourUtils.getWorkDateAfterMinuteBy24Rule(endTime, afterMniute);
        System.out.println(startTime);
        afterMniute = -6 * 24 * 60;
        startTime = WorkHourUtils.getWorkDateAfterMinuteBy24Rule(endTime, afterMniute);
        System.out.println(startTime);
        afterMniute = -7 * 24 * 60;
        startTime = WorkHourUtils.getWorkDateAfterMinuteBy24Rule(endTime, afterMniute);
        System.out.println(startTime);
        afterMniute = -8 * 24 * 60;
        startTime = WorkHourUtils.getWorkDateAfterMinuteBy24Rule(endTime, afterMniute);
        System.out.println(startTime);
        afterMniute = -9 * 24 * 60;
        startTime = WorkHourUtils.getWorkDateAfterMinuteBy24Rule(endTime, afterMniute);
        System.out.println(startTime);
        afterMniute = -10 * 24 * 60;
        startTime = WorkHourUtils.getWorkDateAfterMinuteBy24Rule(endTime, afterMniute);
        System.out.println(startTime);
        return workPeriod;
    }


    @RequestMapping(value = "/getWorkDateAfterMinuteBy24RuleTest")
    @ResponseBody
    public void getWorkDateAfterMinuteBy24RuleTest() throws ParseException {
        Calendar cal = Calendar.getInstance();
        cal.setTime(DateUtils.parse("2021-06-18"));
        int week = cal.get(Calendar.DAY_OF_WEEK);
        System.out.println(week);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        Date endTime = cal.getTime();
        Integer afterMniute = 1 * 24 * 60;
        Date startTime = WorkHourUtils.getWorkDateAfterMinuteBy24Rule(endTime, afterMniute);
        System.out.println(startTime);
        afterMniute = 2 * 24 * 60;
        startTime = WorkHourUtils.getWorkDateAfterMinuteBy24Rule(endTime, afterMniute);
        System.out.println(startTime);
        afterMniute = 3 * 24 * 60;
        startTime = WorkHourUtils.getWorkDateAfterMinuteBy24Rule(endTime, afterMniute);
        System.out.println(startTime);
        afterMniute = 4 * 24 * 60;
        startTime = WorkHourUtils.getWorkDateAfterMinuteBy24Rule(endTime, afterMniute);
        System.out.println(startTime);
        afterMniute = 5 * 24 * 60;
        startTime = WorkHourUtils.getWorkDateAfterMinuteBy24Rule(endTime, afterMniute);
        System.out.println(startTime);
        afterMniute = 6 * 24 * 60;
        startTime = WorkHourUtils.getWorkDateAfterMinuteBy24Rule(endTime, afterMniute);
        System.out.println(startTime);
        afterMniute = 7 * 24 * 60;
        startTime = WorkHourUtils.getWorkDateAfterMinuteBy24Rule(endTime, afterMniute);
        System.out.println(startTime);
        afterMniute = 8 * 24 * 60;
        startTime = WorkHourUtils.getWorkDateAfterMinuteBy24Rule(endTime, afterMniute);
        System.out.println(startTime);
        afterMniute = 9 * 24 * 60;
        startTime = WorkHourUtils.getWorkDateAfterMinuteBy24Rule(endTime, afterMniute);
        System.out.println(startTime);
        afterMniute = 10 * 24 * 60;
        startTime = WorkHourUtils.getWorkDateAfterMinuteBy24Rule(endTime, afterMniute);
        System.out.println(startTime);
        System.out.println("----------------------------------------");
        afterMniute = -1 * 24 * 60;
        startTime = WorkHourUtils.getWorkDateAfterMinuteBy24Rule(endTime, afterMniute);
        System.out.println(startTime);
        afterMniute = -2 * 24 * 60;
        startTime = WorkHourUtils.getWorkDateAfterMinuteBy24Rule(endTime, afterMniute);
        System.out.println(startTime);
        afterMniute = -3 * 24 * 60;
        startTime = WorkHourUtils.getWorkDateAfterMinuteBy24Rule(endTime, afterMniute);
        System.out.println(startTime);
        afterMniute = -4 * 24 * 60;
        startTime = WorkHourUtils.getWorkDateAfterMinuteBy24Rule(endTime, afterMniute);
        System.out.println(startTime);
        afterMniute = -5 * 24 * 60;
        startTime = WorkHourUtils.getWorkDateAfterMinuteBy24Rule(endTime, afterMniute);
        System.out.println(startTime);
        afterMniute = -6 * 24 * 60;
        startTime = WorkHourUtils.getWorkDateAfterMinuteBy24Rule(endTime, afterMniute);
        System.out.println(startTime);
        afterMniute = -7 * 24 * 60;
        startTime = WorkHourUtils.getWorkDateAfterMinuteBy24Rule(endTime, afterMniute);
        System.out.println(startTime);
        afterMniute = -8 * 24 * 60;
        startTime = WorkHourUtils.getWorkDateAfterMinuteBy24Rule(endTime, afterMniute);
        System.out.println(startTime);
        afterMniute = -9 * 24 * 60;
        startTime = WorkHourUtils.getWorkDateAfterMinuteBy24Rule(endTime, afterMniute);
        System.out.println(startTime);
        afterMniute = -10 * 24 * 60;
        startTime = WorkHourUtils.getWorkDateAfterMinuteBy24Rule(endTime, afterMniute);
        System.out.println(startTime);
    }


}
