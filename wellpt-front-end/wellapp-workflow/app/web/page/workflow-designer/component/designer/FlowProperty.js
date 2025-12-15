import {
  defaultMessageTemplate,
  multiJobFlowTypeConfig,
  notFoundMainIdentityOptions,
  identitySelectOptions
} from "./constant";

class FlowProperty {
  constructor() {
    this.categorySN = ''
    this.formID = ''
    this.autoUpdateTitle = '0' // 自动更新标题
    this.isActive = '1' // 流程状态
    this.pcShowFlag = '1' // PC端显示
    this.isMobileShow = '1' // 移动端显示
    this.remark = '' // 备注
    this.creators = [] // 流程权限-发起人 [{"type =1,"value ="U_112507086906064896","argValue ="bat_test_3"}]
    this.users = [] // 流程权限-参与人
    this.monitors = [] // 流程权限-督办人 [{"type =8,"value ="Creator","argValue = null}]
    this.admins = [] // 流程权限-监控者
    this.viewers = [] // 流程权限-阅读者
    this.keepRuntimePermission = '' // 流程权限-办结时保留监控权限
    this.granularity = 'A' // 流程权限-最大权限粒度
    this.enableAccessPermissionProvider = '' // 流程权限-通过接口鉴权
    this.accessPermissionProvider = '' // 流程权限-通过接口鉴权-接口id
    this.onlyUseAccessPermissionProvider = '' // 流程权限-通过接口鉴权-仅通过接口鉴权（流程权限设置不再生效）
    this.equalFlow = {
      name: '',
      id: ''
    } // 高级设置-等价流程
    this.isFree = '' // 高级设置-设置为自由流程
    this.useDefaultOrg = '1' // 高级设置-使用组织-系统默认组织
    this.orgId = '' // 高级设置-使用组织 O_800094164644524032
    this.orgVersionId = ''
    this.enableMultiOrg = '' // 高级设置-多组织审批
    // this.multiOrgId = undefined // 高级设置-多组织id
    this.bakUsers = []
    this.autoUpgradeOrgVersion = '0' // 高级设置-使用组织或组织的版本变更时，流转中的流程


    this.isOnlyOneGranularity = null
    this.isSendFile = null
    this.isSendMsg = null
    this.fileTemplate = null
    this.printTemplate = ""
    this.printTemplateId = ""
    this.printTemplateUuid = ""
    this.listener = ""
    this.globalTaskListener = ""
    this.timerListener = ""
    this.customJsModule = ""
    this.flowStates = []
    this.messageTemplates = []
    this.fileRecipients = []
    this.msgRecipients = []
    this.dueTime = null
    this.timeUnit = null
    this.beginDirections = []
    this.endDirections = []

    this.indexType = "default" // 高级设置-索引设置
    this.indexTitleExpression = ""
    this.indexContentExpression = ""
    this.records = [] // 高级设置-信息记录
    this.opinionCheckSets = [] // 高级设置-签署意见校验设置
    this.eventScripts = [] // 高级设置-事件脚本

    this.availableBizOrg = 'all' // 可用业务组织
    this.bizOrgId = '' // 指定业务组织
    this.multiOrgs = []

    this.setDefaultMessageTemplate()

    this.enabledAutoSubmit = false // 审批去重功能开关
    this.autoSubmitRule = null

    this.multiJobFlowType = multiJobFlowTypeConfig[0]['id'] // 一人多职流转设置
    this.mainJobNotFoundFlowType = notFoundMainIdentityOptions[0]['value']
    this.selectJobMode = identitySelectOptions[1]['value']
    this.selectJobField = false
    this.jobField = ""
  }

  setDefaultMessageTemplate() {
    defaultMessageTemplate.map(msg => {
      let distributerElements = []
      msg.distributers.map(item => {
        distributerElements.push({
          distributerType: null,
          distributerTypeName: item.dtypeName,
          id: item.id,
          name: '',
          designee: []
        })
      })
      this.messageTemplates.push({
        type: msg.type,
        typeName: msg.typeName,
        id: '',
        name: '',
        isSendMsg: msg.isSendMsg,
        condition: null,
        extraMsgRecipients: null,
        extraMsgRecipientUserIds: null,
        extraMsgCustomRecipients: null,
        extraMsgCustomRecipientUserIds: null,
        copyMsgRecipients: [],
        conditionEnable: '0',
        condExpressionSignal: 'and',
        distributerElements,
        distributionElements: [],
        conditionElements: [],
      })
    })
  }
}


export default FlowProperty