export default {
  wtype: 'WidgetFormOrgSelect',
  name: '组织选择',
  iconClass: 'pticon iconfont icon-a-icjichuzujianzuzhixuanzekuang',
  scope: ['dyform', 'mobileDyform'],
  category: 'basicComponent',
  configuration: {
    code: '',
    length: 64,
    dbDataType: '1',
    inputMode: '43',
    applyToDatas: [],
    dbDataType: null,
    inputDisplayStyle: 'IconLabel',
    separator: ';',
    modalTitle: '请选择',
    multiSelect: true,
    isLabelValueWidget: true,
    defaultDisplayState: 'edit',
    displayValueField: undefined,
    uneditableDisplayState: 'label',
    syncLabel2FormItem: true,
    orgUuid: undefined,
    choosenTitleDisplay: 'title',
    isPathValue: false, // 返回值格式 true完整格式 false仅组织ID
    checkableTypes: [],
    orgSelectTypes: [
      { id: 'MyOrg', value: 'MyOrg', label: '我的组织', enable: true },
      { id: 'MyLeader', value: 'MyLeader', label: '我的领导', enable: true },
      { id: 'MyDept', value: 'MyDept', label: '我的部门', enable: true },
      { id: 'MyUnderling', value: 'MyUnderling', label: '我的下属', enable: true }
    ],
    filterNode: {
      showType: undefined,
      showData: '',
      showFunction: undefined,
      hideType: undefined,
      hideData: '',
      hideFunction: undefined,
      hideNoUserNode: false
    },
    uniConfiguration: {
      bordered: false
    } //移动端配置
  }
};
