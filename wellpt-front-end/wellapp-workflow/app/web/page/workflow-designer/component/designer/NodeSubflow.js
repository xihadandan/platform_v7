
/*
task 68个参数
子流程 98个参数，后30个参数是子流程自己的
比62流程少5 个参数，少的参数如下

{
    "x": "1032",
    "y": "1008",
    "conditionX": "",
    "conditionY": "",
    "conditionLine": ""
}
*/

import NodeTask from "./NodeTask";

class NodeSubflow extends NodeTask {
  constructor({
    id = '',
    name = '',
    type = '2',
    // expandList = '1'
  } = {}) {
    super({ id, name, type })

    this.isSetFlow = null
    this.isAutoSubmit = null
    this.isCopyAll = null
    this.isCopyBody = null
    this.isCopyAttach = null
    this.businessType = ""
    this.businessTypeName = ""
    this.businessRole = ""
    this.businessRoleName = ""
    this.newFlows = []
    this.relations = [] // 前置关系
    /* 办理进度 */
    this.undertakeSituationPlaceHolder = ""
    this.undertakeSituationTitleExpression = ""
    this.undertakeSituationButtons = []
    this.undertakeSituationColumns = []
    this.sortRule = "default"
    this.undertakeSituationOrders = []
    /* 信息发布 */
    this.infoDistributionPlaceHolder = ""
    this.infoDistributionTitleExpression = ""
    /* 操作记录 */
    this.operationRecordPlaceHolder = ""
    this.operationRecordTitleExpression = ""
    this.subTaskMonitors = []
    this.inStagesCondition = null
    this.stageDivisionFormId = null
    this.stageHandlingState = null
    this.stageState = null
    this.stages = []
    this.traceTask = ""
    this.childLookParent = "0"
    this.parentSetChild = "0"
    if (this.type === '2') {
      this.expandList = '1'
    }
    this.timerId = ''
  }
}

export default NodeSubflow