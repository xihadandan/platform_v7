0、更新wellpt-app-notice.jar模块


1、在system-message.properties文件中配置
## notice param
app.notice.sms.send.once=true
app.notice.sms.template.id=NOTICE_MESSAGE


2、在后台“消息格式”配置ID为NOTICE_MESSAGE的消息模板
勾选短信,表单变量配置${dytable.notice_title}


3、在后台“任务管理”开启“消息队列发送”任务


4、在后台“移动信息机设置”配置短信机信息