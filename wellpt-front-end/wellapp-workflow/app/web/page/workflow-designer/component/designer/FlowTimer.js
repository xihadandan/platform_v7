class FlowTimer {
  constructor() {
    const date = new Date()
    this.timerId = date.getTime()
    this.name = ''
    this.timingMode = '' // 计时方式 根据limitUnit 得出，数据不存
    this.includeStartTimePoint = '0' // '1' 立即开始计时
    this.limitUnit = '' // 启动后从下一 limitUnit 开始计时
    this.limitTimeType = '' // 时限类型
    this.limitTime1 = '' // 固定时限-时限输入框
    this.timeLimitUnit = '' // 固定时限-时限下拉框
    this.limitTime = '' // 时限字段

    this.timerConfigUuid = ''
    this.introductionType = '' // 是否有引用计时服务

    this.autoDelay = '0' // 自动推迟到下一工作时间的起始点前
    this.workTimePlanUuid = ''
    this.workTimePlanId = '' // 工作时间
    this.workTimePlanName = ''
    this.tasks = [] // 计时环节（普通）
    this.subTasks = []

    this.enableAlarm = '' // 预警提醒开关
    this.alarmElements = [] // 预警消息
    this.enableDueDoing = '' // 逾期提醒开关
    this.dueObjects = [{
      type: 32,
      value: 'Doing',
      argValue: null
    }] // 逾期消息需要提醒的人员类型-默认“在办人员”
    this.dueUsers = [] // 逾期消息需要提醒的人员
    this.dueTime = '1' // 逾期提醒-消息催办
    this.dueUnit = '2' // 逾期提醒-下拉框-工作小时
    this.dueFrequency = '1' // 逾期提醒-共1次

    this.autoUpdateLimitTime = '' // 自动更新时限
    this.ignoreEmptyLimitTime = '' // 时限为空时不计时
    this.timeEndType = '0' // 计时结束设置
    this.overDirections = '' // 计时结束设置-流经指定流向结束计时
    this.dueAction = '0' // 逾期时-默认不自动处理
    this.dueToUsers = [] // 逾期时-自动移交给其他人员办理 dueAction === '4'
    this.dueToTask = '' // 逾期时-自动进入下一个办理环节 dueAction === '16'

    this.alarmTime = null
    this.alarmUnit = null
    this.alarmFrequency = null
    this.alarmObjects = []
    this.alarmUsers = []
    this.alarmFlow = null
    this.alarmFlowDoings = []
    this.alarmFlowDoingUsers = []
    this.dutys = []
    this.affectMainFlow = null
    this.dueFlow = null
    this.dueFlowDoings = []
    this.dueFlowDoingUsers = []
    this.timerListener = ''

    this.limitUnitField = '' // 办理时限单位字段
    this.dueTimeType = '1' // 处理时间类型，1常量、2字段值
    this.dueUnitType = '1' // // 处理单位类型，1常量、2字段值
    this.dueFrequencyType = '1' // 逾期处理次数类型，1常量、2字段值
    this.timingModeType = '' // 计时方式类型
    this.timingModeUnit = '' // 计时方式单位
  }
}


export default FlowTimer