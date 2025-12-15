package com.wellsoft.pt.app.calendar.service.impl;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.wellsoft.context.util.date.DateUtil;
import com.wellsoft.pt.app.calendar.bean.RemindMsg;
import com.wellsoft.pt.app.calendar.dao.MyCalendarEventDao;
import com.wellsoft.pt.app.calendar.entity.MyCalendarEntity;
import com.wellsoft.pt.app.calendar.entity.MyCalendarEventEntity;
import com.wellsoft.pt.app.calendar.facade.service.AttentionFacade;
import com.wellsoft.pt.app.calendar.service.MyCalendarEventService;
import com.wellsoft.pt.app.calendar.service.MyCalendarService;
import com.wellsoft.pt.basicdata.viewcomponent.facade.support.calendar.CalendarEventParams;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.message.facade.service.MessageClientApiFacade;
import com.wellsoft.pt.multi.org.bean.OrgUserJobDto;
import com.wellsoft.pt.multi.org.bean.OrgUserVo;
import com.wellsoft.pt.multi.org.facade.service.OrgApiFacade;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class MyCalendarEventServiceImpl extends
        AbstractJpaServiceImpl<MyCalendarEventEntity, MyCalendarEventDao, String> implements MyCalendarEventService {

    // 消息提醒的模板ID
    public static String MSG_TEMPLATE = "CALENDAR_REMIND";
    public static String MSG_TEMPLATE_TIMELY = "CALENDAR_REMIND_TIMELY";
    protected Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private OrgApiFacade orgApiFacade;
    @Autowired
    private AttentionFacade attentionFacade;
    @Autowired
    private MyCalendarService myCalendarService;
    @Autowired
    private MessageClientApiFacade messageClientApiFacade;

    public static void main(String[] args) {
        Date start = DateUtil.getFirstDayOfMonth();
        Date end = DateUtil.getLastDayOfMonth();
        List<Date> list = DateUtil.getDayOfMonthInPeriod(start, end, 1);
        for (Date date : list) {
            System.out.println(DateUtil.getFormatDate(date, "yyyy-MM-dd"));
        }

    }

    @Override
    @Transactional
    public String saveEvent(MyCalendarEventEntity e) {
        String newRepeatMarkId = createRepeatMarkId();

        if (e.getRepeatConfVo() != null) {
            e.setRepeatConf(e.getRepeatConfVo().toString());
        }
        if (e.getRemindConfVo() != null) {
            e.setRemindConf(e.getRemindConfVo().toString());
        }

        // 添加操作
        if (StringUtils.isBlank(e.getRepeatMarkId())) {
            this.addEvent(e, newRepeatMarkId, "");
        } else { // 编辑操作
            // 根据repeatMarkId删除所有旧数据，然后重新添加
            this.deleteEventByRepeatMarkId(e.getRepeatMarkId());
            // 清空uuid和repeatMarkId
            e.setUuid(null);
            e.setRepeatMarkId(null);
            this.addEvent(e, newRepeatMarkId, e.getCreator());
        }
        return newRepeatMarkId;
    }

    private void addEvent(MyCalendarEventEntity e, String newRepeatMarkId, String creator) {
        // 获取日历本的创建人信息
        MyCalendarEntity calendar = myCalendarService.getOne(e.getBelongObjId());
        e.setCalendarCreator(calendar.getCreator());
        e.setCalendarCreatorName(orgApiFacade.getUserNameById(calendar.getCreator()));
        if (1 == e.getIsRepeat()) { // 重复事项
            List<Date> eventDateList = Lists.newArrayList();
            JsonObject conf = new Gson().fromJson(e.getRepeatConf(), JsonObject.class);
            String repeatType = conf.get("repeatType").getAsString().toLowerCase();
            String repeatValue = conf.get("repeatValue").getAsString().toLowerCase();
            Date periodStart = e.getRepeatPeriodStartTime();
            Date periodEnd = e.getRepeatPeriodEndTime();
            Date start = e.getStartTime();
            Date end = e.getEndTime();
            String startTime = DateUtil.getFormatDate(start, "HH:mm:ss");
            String endTime = DateUtil.getFormatDate(end, "HH:mm:ss");
            if (e.getIsAll() != null && e.getIsAll() == 1) {
                startTime = "00:00:00";
                endTime = "23:59:59";
            }
            if ("day".equalsIgnoreCase(repeatType)) { // 按天重复
                String[] opts = repeatValue.split(";");
                // 根据配置获取需要重复的周几的定义
                Integer[] days = computeRepeatDayOfWeekByDayConf(opts);
                eventDateList = DateUtil.getDayOfWeekInPeriod(periodStart, periodEnd, days);
            } else if ("week".equalsIgnoreCase(repeatType)) { // 周重复
                String[] opts = repeatValue.split(";");
                Integer[] days = computeRepeatDayOfWeekByWeekConf(opts);
                eventDateList = DateUtil.getDayOfWeekInPeriod(periodStart, periodEnd, days);
            } else if ("month".equalsIgnoreCase(repeatType)) { // 按月重复
                eventDateList = DateUtil.getDayOfMonthInPeriod(periodStart, periodEnd, Integer.valueOf(repeatValue));
            } else if ("year".equalsIgnoreCase(repeatType)) { // 按年重复
                eventDateList = DateUtil.getDayOfYearInPeriod(periodStart, periodEnd, repeatValue);
            }
            // 批量创建事项
            if (CollectionUtils.isNotEmpty(eventDateList)) {

                for (Date date : eventDateList) {
                    MyCalendarEventEntity eventOfDay = new MyCalendarEventEntity();
                    BeanUtils.copyPropertiesExcludeBaseField(e, eventOfDay,
                            new String[]{"remindTime", "remindStatus"});
                    String dateStr = DateUtil.getFormatDate(date, "yyyy-MM-dd");
                    eventOfDay.setStartTime(DateUtil.getFormatDateByStr(dateStr + " " + startTime,
                            "yyyy-MM-dd HH:mm:ss"));
                    eventOfDay.setEndTime(DateUtil.getFormatDateByStr(dateStr + " " + endTime, "yyyy-MM-dd HH:mm:ss"));
                    eventOfDay.setRepeatMarkId(newRepeatMarkId);
                    eventOfDay.setRemindTime(computeRemindTime(eventOfDay));
                    this.save(eventOfDay);
                    if (StringUtils.isNotBlank(creator)) {
                        eventOfDay.setCreator(creator);
                        this.save(eventOfDay);
                    }
                    this.sendRemindNotice(eventOfDay);
                }
            } else {
                throw new RuntimeException("设置的时间段内，无法满足重复的配置");
            }
        } else {
            e.setStartTime(e.getStartTime());
            e.setEndTime(e.getEndTime());
            if (e.getIsAll() != null && e.getIsAll() == 1) {
                Date periodStart = e.getStartTime();
                Date periodEnd = e.getEndTime();
                String startDate = DateUtil.getFormatDate(periodStart, "yyyy-MM-dd");
                String endDate = DateUtil.getFormatDate(periodEnd, "yyyy-MM-dd");
                e.setStartTime(DateUtil.getFormatDateByStr(startDate + " 00:00:00", "yyyy-MM-dd HH:mm:ss"));
                e.setEndTime(DateUtil.getFormatDateByStr(endDate + " 23:59:59", "yyyy-MM-dd HH:mm:ss"));
            }
            e.setRepeatMarkId(newRepeatMarkId);
            e.setRemindTime(computeRemindTime(e));
            this.save(e);
            if (StringUtils.isNotBlank(creator)) {
                e.setCreator(creator);
                this.save(e);
            }
            this.sendRemindNotice(e);
        }
    }

    //前端只展示年月日 时分 发送消息时应该也是只展示 年月日 时分
    public RemindMsg getRemindMsg(MyCalendarEventEntity e) {
        RemindMsg msg = new RemindMsg();
        msg.setTitle(e.getTitle());
        msg.setContent(e.getEventContent());
        msg.setStartTime(DateUtil.getFormatDate(e.getStartTime(), "yyyy-MM-dd HH:mm"));
        if (e.getRemindTime() != null) {
            msg.setRemindTime(DateUtil.getFormatDate(e.getRemindTime(), "yyyy-MM-dd HH:mm:ss"));
        }
        msg.setEndTime(DateUtil.getFormatDate(e.getEndTime(), "yyyy-MM-dd HH:mm"));
        msg.setCreator(e.getCreator());
        msg.setModifier(e.getCreator());

        //20201120新增地址
        msg.setAddress(e.getAddress());

        return msg;
    }

    private void sendRemindNotice(MyCalendarEventEntity event) {
        // 有设置提醒，才发送提醒
        Set<String> users = this.computeNoticeUser(event.getNoticeObjs(), event);
        if (event.getIsRemind() != null && event.getIsRemind() == 1) {
            if (users != null && !users.isEmpty()) {
                RemindMsg msg = getRemindMsg(event);
                messageClientApiFacade.send(MSG_TEMPLATE, event.getNoticeTypes(), msg, users, event.getUuid(), null);
            }
        }
        if (users != null && !users.isEmpty()) {
            RemindMsg msg = getRemindMsg(event);
            messageClientApiFacade.send(MSG_TEMPLATE_TIMELY, event.getNoticeTypes(), msg, users, event.getUuid(), null);
        }
    }

    // 计算通知的对象
    private Set<String> computeNoticeUser(String noticeObjs, MyCalendarEventEntity event) {
        Set<String> users = Sets.newHashSet();
        if (StringUtils.isNotBlank(noticeObjs)) {
            String[] objs = noticeObjs.split(";");
            for (String obj : objs) {
                if ("1".equals(obj)) { // 日程创建者
                    users.add(event.getCreator());
                } else if ("2".equals(obj)) { // 日历本所属
                    users.add(event.getCalendarCreator());
                } else if ("3".equals(obj)) { // 参与者
                    if (StringUtils.isNotBlank(event.getJoinUsers())) {
                        String[] orgids = event.getJoinUsers().split(";");
                        for (String orgId : orgids) {
                            users.add(orgId);
                        }
                    }
                }
            }
        }
        return users;
    }

    private Date computeRemindTime(MyCalendarEventEntity e) {
        // 设置提醒
        if (e.getIsRemind() == 1 && StringUtils.isNotBlank(e.getRemindConf())) {
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(e.getStartTime().getTime());
            JsonObject conf = new Gson().fromJson(e.getRemindConf(), JsonObject.class);
            int interval = conf.get("interval").getAsInt();
            String unit = conf.get("intervalUnit").getAsString();
            if ("min".equals(unit)) {
                c.add(Calendar.MINUTE, interval * -1);// N天前的日期
            } else if ("hour".equals(unit)) {
                c.add(Calendar.MINUTE, interval * -60);// N天前的日期
            } else if ("day".equals(unit)) {
                c.add(Calendar.DATE, interval * -1);// N天前的日期
            } else if ("week".equals(unit)) {
                c.add(Calendar.DATE, interval * -7);// N天前的日期
            } else if ("month".equals(unit)) {
                c.add(Calendar.MONTH, interval * -1);// N天前的日期
            }

            Date finalDate = new Date(c.getTimeInMillis());
            return finalDate;
        }
        return null;
    }

    private String createRepeatMarkId() {
        return System.currentTimeMillis() + "";
    }

    // 按天配置计算出一周中需要重复的周几的定义
    private Integer[] computeRepeatDayOfWeekByDayConf(String[] opts) {
        List<Integer> days = Lists.newArrayList();
        for (String opt : opts) {
            if ("work".equals(opt)) { // 按工作日重复，计算周一到周五
                days.add(2);
                days.add(3);
                days.add(4);
                days.add(5);
                days.add(6);
            }
            if ("sat".equals(opt)) { // 周六
                days.add(7);
            }
            if ("sun".equals(opt)) { // 周日
                days.add(1);
            }
        }
        return days.toArray(new Integer[]{});
    }

    // 按周配置计算出一周中需要重复的周几的定义
    private Integer[] computeRepeatDayOfWeekByWeekConf(String[] opts) {
        List<Integer> days = Lists.newArrayList();
        for (String opt : opts) {
            if ("all".equals(opt)) { // 按工作日重复，计算周一到周五
                for (int i = 1; i <= 7; i++) {
                    days.add(i);
                }
                // 配置了全部则直接跳出
                break;
            }
            if ("w1".equals(opt)) { // 周一
                days.add(2);
            } else if ("w2".equals(opt)) {// 周二
                days.add(3);
            } else if ("w3".equals(opt)) {// 周三
                days.add(4);
            } else if ("w4".equals(opt)) {// 周四
                days.add(5);
            } else if ("w5".equals(opt)) {// 周五
                days.add(6);
            } else if ("w6".equals(opt)) {// 周六
                days.add(7);
            } else if ("w7".equals(opt)) {// 周日
                days.add(1);
            }
        }
        return days.toArray(new Integer[]{});
    }

    @Override
    @Transactional
    public void deleteEvent(String uuid) {
        this.delete(uuid);
    }

    @Override
    @Transactional
    public void deleteEventByRepeatMarkId(String repeatMarkId) {
        MyCalendarEventEntity q = new MyCalendarEventEntity();
        q.setRepeatMarkId(repeatMarkId);
        List<MyCalendarEventEntity> objs = this.dao.listByEntity(q);
        if (objs != null) {
            this.deleteByEntities(objs);
            // 删除事件需要移除对应的提醒通知
            for (MyCalendarEventEntity event : objs) {
                this.messageClientApiFacade.cancelScheduleMessage(event.getUuid());
            }
        }
    }

    @Override
    public MyCalendarEventEntity getEvent(String uuid) {
        return this.getOne(uuid);
    }

    @Override
    public List<MyCalendarEventEntity> loadEvents(CalendarEventParams ep, HashMap<String, Object> params) {
        String userId = SpringSecurityUtils.getCurrentUserId();
        String systemUnitId = SpringSecurityUtils.getCurrentUserUnitId();
        params.put("systemUnitId", systemUnitId);

        String from = ep.getParam("from", "").toString();
        //如果是我的关注   并且没有关注的人 即判断条件为空  没有默认选择第一条  即追加 条件
        String whereSql = (String) params.get("whereSql");//"calendarCreator=WTF"
        List<OrgUserVo> list = attentionFacade.queryMyAttentionUserList();
        if ("attention".equals(from) && "1=1".equals(whereSql)) {
            //找出所有我的关注
            List<String> attendUserIds = new ArrayList<String>();
            attendUserIds.add("WTF");
            if (list != null && list.size() > 0) {
                attendUserIds.addAll(Lists.transform(list, new Function<OrgUserVo, String>() {
                    @Override
                    public String apply(OrgUserVo userVo) {
                        return userVo != null ? userVo.getId() : "";
                    }
                }));
            }

            whereSql = "( calendarCreator in(:attendUserIds) )";
            params.put("whereSql", whereSql);
            params.put("attendUserIds", attendUserIds);
        }
        List<MyCalendarEventEntity> objs = this.listByNameHQLQuery("queryAppMyCalendarEventList", params);
        //String from = ep.getParam("from", "").toString();

        // 是从我的关注来的请求，需要判断是否有查看的权限

        //23656 日程设置参与人可见，但是登陆任意一用户打开日程管理都可以查看
        //if ("attention".equals(from)) {
        // 记录该用户对该日历本是否有查看的权限
        Map<String, Boolean> rightsMap = Maps.newHashMap();
        // 因为上层调用接口套上了写事务，所以这里必须clone出来，不然event.title的变更会被写到数据库中去
        List<MyCalendarEventEntity> cloneList = Lists.newArrayList();
        for (MyCalendarEventEntity obj : objs) {
            MyCalendarEventEntity event = new MyCalendarEventEntity();
            BeanUtils.copyProperties(obj, event);
            boolean isCanView = this.isHasViewEventPrivilege(userId, event, rightsMap);
            if (isCanView) {
                event.setIsCanView(isCanView);
                cloneList.add(event);
            }
        }
        return cloneList;
		/*} else {
			return objs;
		}*/
    }

    // 判断用户是否有查看该事项的权限
    private boolean isHasViewEventPrivilege(String userId, MyCalendarEventEntity event, Map<String, Boolean> rightsMap) {
        // 检查用户是否是事项的参与人，如果是参与人，则有查看权限
        HashMap<String, String> joinUserMap = orgApiFacade.getUsersByOrgIds(event.getJoinUsers());
        if (joinUserMap.containsKey(userId)) {
            return true;
        } else {
            // 检查事项的公共范围的配置，看用户是否在其中, 默认参与人可见
            // 新增创建者可见
            if (event.getCalendarCreator().equals(userId)) {
                return true;
            }

            String range = event.getPublicRange();
            if (StringUtils.isBlank(range)) {
                // 事项没有设置公开范围，检查日历本的公开范围的配置
                boolean isCanView = false;
                String calendarUuid = event.getBelongObjId();
                if (rightsMap.containsKey(calendarUuid)) {
                    isCanView = rightsMap.get(calendarUuid);
                } else {
                    isCanView = myCalendarService.isHasViewEventPrivilege(userId, calendarUuid);
                    rightsMap.put(calendarUuid, isCanView);
                }
                return isCanView;
            } else if (MyCalendarEventEntity.PUBLIC_RANGE_ALL.equals(range)) {
                // 事项公开范围为所有人可见
                return true;
            } else if (MyCalendarEventEntity.PUBLIC_RANGE_LEADER.equals(range)) {
                // 事项公开范围为上级领导可见
                OrgUserVo calendarCreator = orgApiFacade.getUserVoById(event.getCalendarCreator());
                // 获取日历本创建者的所有上级领导
                Set<String> leaders = Sets.newHashSet();
                if (calendarCreator.getJobList() != null) {
                    for (OrgUserJobDto job : calendarCreator.getJobList()) {
                        Set<String> objs = orgApiFacade.queryUserSuperiorLeaderListByJobIdPath(job.getOrgTreeNodeDto()
                                .getEleIdPath(), false);
                        leaders.addAll(objs);
                    }
                }
                if (leaders.contains(userId)) {
                    return true;
                }
            } else if (MyCalendarEventEntity.PUBLIC_RANGE_PARTUSER.equals(range)) {
                // 事项公开范围为部分用户可见
                String partUsers = event.getPartUsers();
                HashMap<String, String> usersMap = orgApiFacade.getUsersByOrgIds(partUsers);
                if (usersMap != null && usersMap.get(userId) != null) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public String getTableName() {
        return "app_my_calendar_event";
    }

    @Override
    @Transactional
    public void updateStatus(String uuid, Integer status) {
        MyCalendarEventEntity obj = this.getEvent(uuid);
        if (obj != null) {
            obj.setIsFinish(status);
            this.update(obj);
        }

    }

    // 删除指定日历本下的所有事项
    @Override
    @Transactional
    public void deleteEventByCalendarUuid(String calendarUuid) {
        MyCalendarEventEntity q = new MyCalendarEventEntity();
        q.setBelongObjId(calendarUuid);
        List<MyCalendarEventEntity> objs = this.dao.listByEntity(q);
        if (CollectionUtils.isNotEmpty(objs)) {
            this.deleteByEntities(objs);
        }

    }

    @Override
    public List<MyCalendarEventEntity> queryAppMyCalendarEventListBySection(String startDate, String endDate) {

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("startDate", startDate);
        params.put("endDate", endDate);
        params.put("currentUserId", SpringSecurityUtils.getCurrentUserId());
        return this.dao.listByNameHQLQuery("queryAppMyCalendarEventListBySection", params);
    }
}
