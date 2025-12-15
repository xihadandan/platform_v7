class SubFlow {
  constructor() {
    /* 从62 subflowProperties.js  NewFlowOKEvent方法中得到newFlow */
    const date = new Date()
    this.id = date.getTime()
    this.name = ""
    this.value = ""
    this.label = ""
    this.conditions = ""
    this.interfaceValue = ""
    this.interfaceName = ""
    this.createWay = "12"
    this.createWayName = ""
    this.createInstanceFormId = ""
    this.createInstanceFormName = ""
    this.createInstanceWay = ""
    this.createInstanceWayName = ""
    this.taskUsers = ""
    this.taskUsersName = ""
    this.createInstanceBatch = "0"
    this.isMajor = "0"
    this.isWait = "0"
    this.isShare = "0"
    this.toTaskName = ""
    this.toTaskId = ""
    this.title = "default"
    this.titleExpression = ""
    this.copyBotRuleName = ""
    this.copyBotRuleId = ""
    this.syncBotRuleName = ""
    this.syncBotRuleId = ""
    this.returnWithOver = "0"
    this.returnWithDirection = "0"
    this.returnDirectionName = ""
    this.returnDirectionId = ""
    this.returnBotRuleName = ""
    this.returnBotRuleId = ""
    this.returnOverrideFieldNames = ""
    this.returnOverrideFields = ""
    this.returnAdditionFieldNames = ""
    this.returnAdditionFields = ""
    this.notifyDoing = "0"
    this.undertakeSituationPlaceHolder = ""
    // this.undertakeSituationPlaceHolderValue = ''
    // this.undertakeSituationPlaceHolderName = ''
    this.infoDistributionPlaceHolder = ""
    // this.infoDistributionPlaceHolderValue = ''
    // this.infoDistributionPlaceHolderName = ''
    this.operationRecordPlaceHolder = ""
    // this.operationRecordPlaceHolderValue = ''
    // this.operationRecordPlaceHolderName = ''
    /* 从返回的接口中得到 */
    this.copyFields = ""
    this.isMainFormCreateWay = ""
    this.isMerge = ""
    this.subformId = ""
    this.granularity = '' // 分发粒度
    this.bizRoleId = '' // 业务角色id
  }
}


export default SubFlow