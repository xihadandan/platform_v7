/* 
{
  mode:'审批去重模式，before前置审批、after后置审批',
  effectiveTask:'规则生效环节，all全流程、include指定环节、exclude除指定环节外的其他环节',
  taskIds:['指定的环节ID列表'],
  effectiveUser:'规则生效人员，all所有人、include指定人员、exclude除指定人员外的其他人员',
  users: [{// 指定的人员
    type: '1行政组织',
    value: '办理人值',
        argValue: '办理人名称',
    }],
  matchTypes:['重复审批人员判定，
    start流程发起人,
    task审批节点办理人,
    collaboration协作节点办理人(不含决策人),
    branch包含分支、条件分支之后环节的办理人'
  ],
  handleMode:'处理方式，submit自动审批，skip自动跳过',
  submitOpinionMode:'自动提交意见，latest使用最后一次人工填写意见、default使用缺省意见',
  defaultSubmitOpinionText:'缺省意见内容',
  keepRecord:'是否留痕，true留痕、false不留痕',
  exitConditions:['退出条件，
    'dataChanged后续环节数据版本发生变更',
    'canEditForm后续环节可编辑表单时/前序环节可编辑表单时', 'singleUserOnCanEditForm仅一人办理或办理人全部去重时判断',
    'editAndRequiredField后续环节可编辑表单且存在必填字段时/前序环节可编辑表单且存在必填字段时', 'singleUserOnEditAndRequiredField仅一人办理或办理人全部去重时判断',
    'chooseDirection后续环节需要选择流向时/前序环节需要选择流向时', 'singleUserOnChooseDirection仅一人办理或办理人全部去重时判断',
    'chooseUser后续环节需要选择下一环节办理人/抄送人时/前序环节需要选择下一环节办理人/抄送人时', 'singleUserOnChooseUser仅一人办理或办理人全部去重时判断'
  ],
  exitScope:'退出范围，single单次退出前置审批、all全流程退出前置审批',
  supplementRule:'补审补办规则，1存在未审批过的人员时补审补办、2存在未审批人员，且有环节被跳过时补审补办',
  supplementMode:'补审补办方式，task按最后跳过环节补审补办、user按人员补审补办',
  supplementTaskName:'补审补办环节名称',
  supplementOperateRight:'补审补办操作权限，submit只有提交权限、default同补审环节权限',
  supplementViewFormMode:'补审补办表单权限, 只有阅读权限readonly、default同补审环节权限'
}
*/
class AutoSubmitRule {
  constructor({
    mode = 'before',
  } = {}) {
    this.mode = mode
    this.effectiveTask = 'all'
    this.taskIds = []
    this.effectiveUser = 'all'
    this.users = []
    this.matchTypes = ['task', 'collaboration']
    if (mode === 'before') {
      this.handleMode = 'submit'
      this.submitOpinionMode = 'latest'
      this.defaultSubmitOpinionText = '（重复审批自动提交）'

    } else {
      this.handleMode = 'skip'
      this.submitOpinionMode = null
      this.defaultSubmitOpinionText = null

    }

    this.keepRecord = false

    if (mode === 'before') {
      this.exitConditions = [
        'dataChanged',
        'editAndRequiredField', 'singleUserOnEditAndRequiredField',
        'chooseDirection', 'singleUserOnChooseDirection',
        'chooseUser', 'singleUserOnChooseUser'
      ]
    } else {
      this.exitConditions = [
        'editAndRequiredField', 'singleUserOnEditAndRequiredField',
        'chooseDirection', 'singleUserOnChooseDirection',
        'chooseUser', 'singleUserOnChooseUser'
      ]
    }
    this.exitScope = 'single'
    if (mode === 'before') {
      this.supplementRule = null
      this.supplementMode = null
      this.supplementTaskName = null
      this.supplementOperateRight = null
      this.supplementViewFormMode = null
    } else {
      this.supplementRule = '1'
      this.supplementMode = 'task'
      this.supplementTaskName = '补审补办'
      this.supplementOperateRight = 'submit'
      this.supplementViewFormMode = 'readonly'
    }
  }
}

export default AutoSubmitRule