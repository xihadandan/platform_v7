class EdgeDirection {
  constructor({
    id = '',
    name = '',
    type = '1', // '2'是条件分支
    fromID = '',
    toID = '',
    terminalType = '',
    terminalName = '',

    useAsButton = '0',
    buttonName = '',
    buttonClassName = '',
    btnStyle = '',
    buttonOrder = null,
    sortOrder = null,
    remark = null,
    showRemark = '1',
    line = '',
    isShowName = '1',
    conditions = [],
    fileRecipients = [],
    msgRecipients = [],
    isDefault = null,
    isEveryCheck = null,
    isSendFile = null,
    isSendMsg = null,
    listener = null,
    branchMode = null,
    branchInstanceType = null,
    branchCreateWay = null,
    branchCreateInstanceFormId = null,
    isMainFormBranchCreateWay = null,
    branchTaskUsers = null,
    branchCreateInstanceWay = null,
    branchCreateInstanceBatch = null,
    branchTimer = null,
    branchInterface = null,
    branchInterfaceName = null,
    shareBranch = null,
    isIndependentBranch = null,
    eventScript = null,
    archives = []
  } = {}) {
    this.id = id;
    this.name = name;
    this.type = type;
    this.fromID = fromID;
    this.toID = toID;
    this.terminalType = terminalType;
    this.terminalName = terminalName;

    if (type === '2' || type === '3') {
      useAsButton = '';
      sortOrder = '';
      remark = '';
      isDefault = '0';
      isEveryCheck = '';
      listener = '';
      branchMode = '1';
      shareBranch = '';
      isIndependentBranch = '';
    }
    this.useAsButton = useAsButton; // 流向作为提交按钮使用
    this.buttonName = buttonName;
    this.buttonClassName = buttonClassName;
    this.btnStyle = btnStyle;
    this.buttonOrder = buttonOrder;
    this.sortOrder = sortOrder;
    this.remark = remark;
    this.showRemark = showRemark;
    this.line = line; // CURVE;right;left 曲线流向;
    this.isShowName = isShowName;
    this.conditions = conditions; // 分支条件设置
    this.fileRecipients = fileRecipients;
    this.msgRecipients = msgRecipients;
    this.isDefault = isDefault; // 条件分支 '1' 缺省分支
    this.isEveryCheck = isEveryCheck; // 每次提交时检查条件
    this.isSendFile = isSendFile;
    this.isSendMsg = isSendMsg;
    this.listener = listener;
    this.branchMode = branchMode; // 分支模式
    this.branchInstanceType = branchInstanceType;
    this.branchCreateWay = branchCreateWay;
    this.branchCreateInstanceFormId = branchCreateInstanceFormId;
    this.isMainFormBranchCreateWay = isMainFormBranchCreateWay;
    this.branchTaskUsers = branchTaskUsers;
    this.branchCreateInstanceWay = branchCreateInstanceWay;
    this.branchCreateInstanceBatch = branchCreateInstanceBatch;
    this.branchTimer = branchTimer;
    this.branchInterface = branchInterface;
    this.branchInterfaceName = branchInterfaceName;
    this.shareBranch = shareBranch;
    this.isIndependentBranch = isIndependentBranch;
    this.eventScript = eventScript;
    this.archives = archives; // 归档
    this.autoUpdateName = true;

    this.i18n = {
      zh_CN: {}
    };
    this.setDefaultI18n();
  }
  setDefaultI18n() {
    const nameCode = `${this.id}.directionName`;
    this.i18n.zh_CN[nameCode] = this.name;
  }
}

export default EdgeDirection;
