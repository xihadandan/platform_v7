
insert into task_job_details (UUID, CREATE_TIME, CREATOR, MODIFIER, MODIFY_TIME, REC_VER, CODE, END_TIME, EXPRESSION, ID, JOB_CLASS_NAME, NAME, REMARK, REPEAT_COUNT, REPEAT_DAY_OF_MONTH, REPEAT_DAY_OF_SEASON, REPEAT_DAY_OF_WEEK, REPEAT_DAY_OF_YEAR, REPEAT_INTERVAL, REPEAT_INTERVAL_TIME, START_TIME, TENANT_ID, TIME_POINT, TIMING_MODE, TYPE)
values ('002c53a9-10b6-4958-b5c8-c4937743fe52', '04-1月 -14 05.40.46.000000 下午', 'U0010000001', 'U0010000001', '19-10月-15 11.52.27.488000 上午', 32, '006', '', '0/60 * * ? * *', 'SHORTMESSAGE', 'com.wellsoft.pt.message.sms.MasJob', '短信定时收发', '短信定时收发', -1, '', '第 1', '', '01', null, '00:01:00', '19-10月-15 11.52.25.000000 上午', '@{multi.tenancy.tenant.id}', '14:35:30', 6, 'timing');

insert into task_job_details (UUID, CREATE_TIME, CREATOR, MODIFIER, MODIFY_TIME, REC_VER, CODE, END_TIME, EXPRESSION, ID, JOB_CLASS_NAME, NAME, REMARK, REPEAT_COUNT, REPEAT_DAY_OF_MONTH, REPEAT_DAY_OF_SEASON, REPEAT_DAY_OF_WEEK, REPEAT_DAY_OF_YEAR, REPEAT_INTERVAL, REPEAT_INTERVAL_TIME, START_TIME, TENANT_ID, TIME_POINT, TIMING_MODE, TYPE)
values ('8cc8b3c9-9d92-4bf3-96b8-cf66b94eeb82', '12-1月 -16 01.35.06.000000 下午', 'U0010000001', 'U0010000001', '12-1月 -16 01.38.27.654000 下午', 33, '001', '', '', 'SEND_MESSAGE_QUEUE', 'com.wellsoft.pt.message.job.MessageQueueJob', '消息队列发送', '消息队列发送', -1, '', '第 1', '', '01', null, '00:00:10', '12-1月 -16 01.34.59.000000 下午', '@{multi.tenancy.tenant.id}', '', 6, 'timing');

insert into task_job_details (UUID, CREATE_TIME, CREATOR, MODIFIER, MODIFY_TIME, REC_VER, CODE, END_TIME, EXPRESSION, ID, JOB_CLASS_NAME, NAME, REMARK, REPEAT_COUNT, REPEAT_DAY_OF_MONTH, REPEAT_DAY_OF_SEASON, REPEAT_DAY_OF_WEEK, REPEAT_DAY_OF_YEAR, REPEAT_INTERVAL, REPEAT_INTERVAL_TIME, START_TIME, TENANT_ID, TIME_POINT, TIMING_MODE, TYPE)
values ('0ace5575-489f-4001-8dce-f3042fad76a2', '18-9月 -14 04.59.00.179000 下午', 'U0010000001', 'U0010000001', '18-9月 -14 05.04.33.244000 下午', 2, '002', '18-9月 -18 05.04.27.000000 下午', '', 'GarbageCollectorStartUp_GC', 'com.wellsoft.pt.repository.jobs.GarbageCollectorStartUp', 'mongo垃圾文件收集器', '', 1, '', '第 1', '', '01', 0, '00:00:30', '18-9月 -14 05.04.25.000000 下午', '@{multi.tenancy.tenant.id}', '23:04:21', 1, 'timing');

insert into task_job_details (UUID, CREATE_TIME, CREATOR, MODIFIER, MODIFY_TIME, REC_VER, CODE, END_TIME, EXPRESSION, ID, JOB_CLASS_NAME, NAME, REMARK, REPEAT_COUNT, REPEAT_DAY_OF_MONTH, REPEAT_DAY_OF_SEASON, REPEAT_DAY_OF_WEEK, REPEAT_DAY_OF_YEAR, REPEAT_INTERVAL, REPEAT_INTERVAL_TIME, START_TIME, TENANT_ID, TIME_POINT, TIMING_MODE, TYPE)
values ('2e6466ca-708e-40be-9783-3e9dabf25e60', '29-10月-14 09.59.48.995000 下午', 'U0010000001', 'U0010000001', '29-10月-14 09.59.48.995000 下午', 0, '001', '', '', 'SCHEDULE_REMIND_JOB', 'com.wellsoft.schedule.support.ScheduleJob', '日程定时提醒', '', -1, '', '第 1', '', '01', 0, '00:00:30', '29-10月-14 09.59.44.000000 下午', '@{multi.tenancy.tenant.id}', '', 6, 'timing');

insert into task_job_details (UUID, CREATE_TIME, CREATOR, MODIFIER, MODIFY_TIME, REC_VER, CODE, END_TIME, EXPRESSION, ID, JOB_CLASS_NAME, NAME, REMARK, REPEAT_COUNT, REPEAT_DAY_OF_MONTH, REPEAT_DAY_OF_SEASON, REPEAT_DAY_OF_WEEK, REPEAT_DAY_OF_YEAR, REPEAT_INTERVAL, REPEAT_INTERVAL_TIME, START_TIME, TENANT_ID, TIME_POINT, TIMING_MODE, TYPE)
values ('ce3ff7f4-336d-45a8-ac67-f36929b5fbec', '06-11月-14 06.05.38.881000 下午', 'U0010000001', 'U0010000001', '06-11月-14 06.05.38.881000 下午', 0, '006', '', '', 'onlineMessageBackup', 'com.wellsoft.pt.message.job.Message2BackupJob', '在线消息备份', '', null, '', '第 1', '', '01', 0, '', '', '@{multi.tenancy.tenant.id}', '02:00:00', 1, 'timing');

insert into task_job_details (UUID, CREATE_TIME, CREATOR, MODIFIER, MODIFY_TIME, REC_VER, CODE, END_TIME, EXPRESSION, ID, JOB_CLASS_NAME, NAME, REMARK, REPEAT_COUNT, REPEAT_DAY_OF_MONTH, REPEAT_DAY_OF_SEASON, REPEAT_DAY_OF_WEEK, REPEAT_DAY_OF_YEAR, REPEAT_INTERVAL, REPEAT_INTERVAL_TIME, START_TIME, TENANT_ID, TIME_POINT, TIMING_MODE, TYPE)
values ('1c6bf959-972b-48ff-a7b7-f0496777427d', '29-1月 -16 11.40.24.000000 上午', 'U0010000015', 'U0010000015', '29-1月 -16 02.30.32.431000 下午', 5, '018', '', '', 'DutyAgentCleanUpJob', 'com.wellsoft.pt.org.support.job.DutyAgentCleanUpJob', '工作委托过期数据清理', '', -1, '', '第 1', '', '01', 0, '00:05:00', '29-1月 -16 12.42.02.000000 下午', '@{multi.tenancy.tenant.id}', '', 6, 'timing');


insert into sys_param_item (UUID, CREATE_TIME, CREATOR, MODIFIER, MODIFY_TIME, REC_VER, KEY, VALUE, NAME, SOURCETYPE, CODE, TYPE)
values ('c1777e1d-9457-4346-90a7-1ff14c51ad68', '04-2月 -16 02.07.20.000000 下午', 'U0010000001', 'U0010000001', '18-2月 -16 03.50.06.707000 下午', 10, 'org.user.domain.account.check', 'false', '用户->域账号检验', 0, '001001', '003');

insert into sys_param_item (UUID, CREATE_TIME, CREATOR, MODIFIER, MODIFY_TIME, REC_VER, KEY, VALUE, NAME, SOURCETYPE, CODE, TYPE)
values ('e02252f4-654a-4e70-9e45-0bf925ff27f7', '04-2月 -16 02.51.25.000000 下午', 'U0010000001', 'U0010000001', '18-2月 -16 03.56.06.385000 下午', 7, 'org.personal.group.async', 'true', '个人群组->是否同步', 0, '001003', '003');

insert into sys_param_item (UUID, CREATE_TIME, CREATOR, MODIFIER, MODIFY_TIME, REC_VER, KEY, VALUE, NAME, SOURCETYPE, CODE, TYPE)
values ('b86f18a7-1445-4a45-9a06-d2c90ad07f2e', '04-2月 -16 03.01.39.000000 下午', 'U0010000001', 'U0010000001', '18-2月 -16 03.49.58.858000 下午', 6, 'org.department.show.in.lcp', 'false', '部门->是否允许此部门在LCP中显示', 0, '001002', '003');

insert into sys_param_item (UUID, CREATE_TIME, CREATOR, MODIFIER, MODIFY_TIME, REC_VER, KEY, VALUE, NAME, SOURCETYPE, CODE, TYPE)
values ('44d250db-3d60-4d86-9e73-27da89e9f432', '17-2月 -16 03.38.10.000000 下午', 'U0010000001', 'U0010000001', '18-2月 -16 03.58.09.576000 下午', 9, 'mobile.app.ios.dowload.from.market', 'true', '手机APP->从IOS市场下载', 0, '002001', '002');

insert into sys_param_item (UUID, CREATE_TIME, CREATOR, MODIFIER, MODIFY_TIME, REC_VER, KEY, VALUE, NAME, SOURCETYPE, CODE, TYPE)
values ('7764cdd8-e9d3-4c9d-9bff-607051428b26', '17-2月 -16 03.38.44.000000 下午', 'U0010000001', 'U0010000001', '18-2月 -16 03.58.13.961000 下午', 12, 'mobile.app.ios.market.dowload.url', 'http://7xav6h.com2.z0.glb.qiniucdn.com/ios.html', '手机APP->IOS市场下载地址', 0, '002002', '002');




insert into WF_DEF_FORMAT (UUID, CREATE_TIME, CREATOR, MODIFIER, MODIFY_TIME, REC_VER, CODE, IS_CLEAR, NAME, VALUE)
values ('00881b46-a550-43a3-bfee-8856b497d3cf', '12-7月 -13 04.59.08.280000 下午', 'U0000000001', 'U0010000001', '24-10月-14 10.03.48.285000 下午', 5, '008', -1, '拟办意见', '${name} ${currentUser!} ${beginTime} ${opinionText}');

insert into WF_DEF_FORMAT (UUID, CREATE_TIME, CREATOR, MODIFIER, MODIFY_TIME, REC_VER, CODE, IS_CLEAR, NAME, VALUE)
values ('3484ce90-99f3-41dc-81ec-da492f8d8f00', '13-7月 -13 04.12.09.097000 下午', 'U0000000001', 'U0010000001', '03-10月-14 11.40.37.454000 上午', 19, '002', 0, '默认格式-环节', '<u>${name} ${currentUsername} ${currentTime}</u> <p style="line-height: 150%; padding-left: 2em;">${opinionText}</p>');

insert into WF_DEF_FORMAT (UUID, CREATE_TIME, CREATOR, MODIFIER, MODIFY_TIME, REC_VER, CODE, IS_CLEAR, NAME, VALUE)
values ('45ae8f7b-1de2-45f4-ac27-32468751fca5', '28-2月 -13 03.27.37.283000 下午', 'U0000000001', 'U0010000001', '24-10月-14 10.03.55.344000 下午', 2, '007', 0, '办理部门', '${currentDepartment}');

insert into WF_DEF_FORMAT (UUID, CREATE_TIME, CREATOR, MODIFIER, MODIFY_TIME, REC_VER, CODE, IS_CLEAR, NAME, VALUE)
values ('549390b9-32d6-4c61-8101-b76cad8f4792', '14-7月 -13 09.57.10.000000 下午', 'U0000000001', 'U0010000001', '17-12月-15 09.53.05.097000 上午', 4, '010', -1, '办理日期', '${currentDate}');

insert into WF_DEF_FORMAT (UUID, CREATE_TIME, CREATOR, MODIFIER, MODIFY_TIME, REC_VER, CODE, IS_CLEAR, NAME, VALUE)
values ('975dda34-9e44-484f-ae6f-252ce78a2f4f', '22-8月 -14 01.55.39.656000 下午', 'U0010000632', 'U0010000001', '12-11月-14 07.51.57.775000 下午', 29, '004', 0, '办理意见', '<if actionType="Submit"><u>${currentUsername}${currentTime}</u><p style="line-height: 100%;padding-left: 2em;margin:0">${opinionText}</p><br></if><if actionType="CounterSign"><u>${currentUsername} 会签给${counterSignUserNames} ${currentTime}</u><p style="line-height: 100%;padding-left: 2em;margin:0">${opinionText}</p><br></if><if actionType="Transfer"><u>${currentUsername} 转办给${transferUserNames} ${currentTime}</u><p style="line-height: 100%;padding-left: 2em;margin:0">${opinionText}</p><br></if><if actionType="Rollback"><u>${currentUsername} 退回 ${currentTime}</u><p style="line-height: 100%;padding-left: 2em;margin:0">${opinionText}</p><br></if><if actionType="DirectRollback"><u>${currentUsername} 直接退回 ${currentTime}</u><p style="line-height: 100%;padding-left: 2em;margin:0">${opinionText}</p><br></if>');

insert into WF_DEF_FORMAT (UUID, CREATE_TIME, CREATOR, MODIFIER, MODIFY_TIME, REC_VER, CODE, IS_CLEAR, NAME, VALUE)
values ('a4532d9d-c56e-4545-9f43-34b9c23e2263', '28-2月 -13 03.26.40.407000 下午', 'U0000000001', 'U0010000001', '17-9月 -14 04.55.29.415000 下午', 4, '003', 0, '办理人', '${currentUsername}');

insert into WF_DEF_FORMAT (UUID, CREATE_TIME, CREATOR, MODIFIER, MODIFY_TIME, REC_VER, CODE, IS_CLEAR, NAME, VALUE)
values ('bab6092a-c445-47e3-b181-067e2ffc897d', '18-10月-13 10.42.29.637000 上午', 'U0000000001', 'U0010000001', '30-10月-14 08.29.52.813000 下午', 4, '006', 0, '办理时间', '${currentTime}');

insert into WF_DEF_FORMAT (UUID, CREATE_TIME, CREATOR, MODIFIER, MODIFY_TIME, REC_VER, CODE, IS_CLEAR, NAME, VALUE)
values ('ec93010e-b28a-4317-a3e4-df02072dccd9', '05-6月 -13 02.29.20.177000 下午', 'U0000000001', 'U0010000001', '24-10月-14 10.03.42.926000 下午', 3, '009', 0, '批办意见', '<br>批办${opinionText}');

insert into WF_DEF_FORMAT (UUID, CREATE_TIME, CREATOR, MODIFIER, MODIFY_TIME, REC_VER, CODE, IS_CLEAR, NAME, VALUE)
values ('ab14470f-e1e5-40cd-8d0a-dd6162e33975', '24-10月-14 10.01.50.622000 下午', 'U0010000001', 'U0010000001', '12-11月-14 08.48.10.751000 下午', 21, '005', 0, '公共意见', '<if actionType="Submit">
<u class="opinion_task_name">${taskName}</u><span class="opinion_user_name">${currentUsername}<i> ${currentTime}</i></span><p class="opinion_user_text">${opinionText}</p><br>
</if>
<if actionType="CounterSign">
<u>${taskName} （${currentUsername} 会签给${counterSignUserNames} ${currentTime}）</u><p style="line-height: 100%;padding-left: 2em;margin:0">${opinionText}</p><br>
</if>
<if actionType="Transfer">
<u>${taskName}（${currentUsername} 转办给${transferUserNames} ${currentTime}）</u><p style="line-height: 100%;padding-left: 2em;margin:0">${opinionText}</p><br>
</if>
<if actionType="Rollback">
<u>${taskName}（${currentUsername} 退回 ${currentTime}）</u><p style="line-height: 100%;padding-left: 2em;margin:0">${opinionText}</p><br>
</if>
<if actionType="DirectRollback">
<u>${taskName}（${currentUsername} 直接退回 ${currentTime}）</u><p style="line-height: 100%;padding-left: 2em;margin:0">${opinionText}</p><br>
</if>');

