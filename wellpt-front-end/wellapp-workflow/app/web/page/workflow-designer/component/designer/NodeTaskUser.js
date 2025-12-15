
class NodeTaskUser {
  constructor({
    type = 1,
    orgId = '',
    groupId = ''
  } = {}) {
    this.groupId = groupId // '办理人分组ID，可用于区分不同批次添加的办理人配置',
    this.type = type // '1行政组织、2表单字段、4办理环节、8人员选项/人员过滤、16自定义、32业务组织',
    this.value = null // '办理人值，多个以分号隔开',
    this.argValue = null // '办理人名称，多个以分号隔开',
    this.orgId = orgId // '组织ID，一个组织选择的人员只有一个组织',
    this.bizOrgId = null // '业务组织ID，一个组织选择的人员只有一个业务组织',
    this.userOptions = [] // 用户选项列表
    this.enabledJobGrade = '0' // '是否启用职等，1启用，0不启用',
    this.jobGrade = null // '职等序号，多个以分号隔开',
    this.enabledJobRank = '0' // '是否启用职级，1启用，0不启用',
    this.jobRankId = null // '职级ID，多个以分号隔开'
    this.valuePath = null // 值路径，多个值以分号隔开'
  }
}

export default NodeTaskUser