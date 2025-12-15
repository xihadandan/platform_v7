import constant, { flowTaskRights, requiredOpinion } from './constant';

class FlowDesigner {
  constructor() {
    this.isNewFlow = true;
    this.selectedTool = '';
    this.selectedCellId = '';
    this.graphItem = undefined;
    this.diction = {};
    this.data = undefined;
    this.userDetails = undefined;
    this.modules = undefined;
    this.defaultOrgData = undefined;
    this.organizationList = undefined;
    this.formDefinition = undefined;
    this.definitionVjson = null;
    this.formLayouts = [];
    this.formFieldDefinition = {};
    this.subformFieldMap = {};
    this.tasks = [];
    this.subflows = [];
    this.directions = [];
    this.flowSettingActions = {}; // 流程设置-操作设置
    this.flowDefinition = {}; // 流程设置-全局设置-流程定义
    this.orgsEnable = []; // 启用组织
    this.allOrgs = []; // 所有组织
    this.subflowChangedBusinessType = []; // 子流程分发组织修改过组织类型的
    this.bizOrgIdRoleOptions = {};
    this.bizOrgIdRoleMap = {};
    this.i18nEl = {};
    this.dyformVarList = []
    this.vueInstance = undefined
    this.languageOptions = []
  }
  setTasks(cells) {
    this.tasks = cells;
  }
  setSubflows(cells) {
    this.subflows = cells;
  }
  setDirections(cells) {
    this.directions = cells;
  }
  setSelectedCellId(id) {
    this.selectedCellId = id;
  }
  setSelectedTool(type = '') {
    this.selectedTool = type;
  }
  getSelectedTool() {
    return this.selectedTool;
  }
  setDiction(diction) {
    const rightKeys = Object.keys(flowTaskRights);
    for (const key in diction) {
      if (!rightKeys.includes(key)) {
        this.diction[key] = diction[key];
      }
    }
  }
  setData(data) {
    this.data = data;
  }
  setUserDetails(data) {
    this.userDetails = data;
  }
  setModules(data) {
    this.modules = data;
  }
  setDefaultOrgData(data) {
    this.defaultOrgData = data;
  }
  setFlowDefinition(definition) {
    this.flowDefinition = definition;
  }
  setFlowSettingActions(actions) {
    this.flowSettingActions = actions;
    this.setRightOptions();
  }
  setRightOptions() {
    for (const key in flowTaskRights) {
      const settingKey = flowTaskRights[key]['settingKey'];
      this.diction[key] = this.getRightOptions(settingKey);
    }
  }
  getRightOptions(type) {
    let rightOptions = [];
    if (this.flowSettingActions) {
      const rights = this.flowSettingActions.rights;
      const buttons = this.flowSettingActions.buttons;
      for (let index = 0; index < rights.length; index++) {
        const item = rights[index];
        if (item[type] && item[type]['apply']) {
          const hasButton = buttons.find(b => b.code === item.code);
          let buttonName = '';
          if (hasButton) {
            buttonName = hasButton.name;
          }
          rightOptions.push({
            value: item.code,
            title: item.title,
            name: item.title,
            // i18n: (hasButton && hasButton.i18n) || {},
            defaultVisible: item[type]['defaultVisible'],
            requiredOpinion: item[type]['requiredOpinion'],
            displayState: 'label'
          });
        }
      }
    }
    return rightOptions;
  }
  initRightConfig(rightOptions) {
    const { task: flowDefinitionTask } = this.flowDefinition;
    const { addSignViewFormMode, counterSignViewFormMode, transferViewFormMode } = flowDefinitionTask;

    // 获取必填意见默认值
    let opinion = {};
    for (const key in requiredOpinion) {
      const opinionKey = requiredOpinion[key];
      const hasOption = rightOptions.find(r => r.value === key);
      let opinionValue = false;
      if (hasOption && hasOption.requiredOpinion) {
        opinionValue = true;
      }
      opinion[opinionKey] = opinionValue;
    }

    return {
      requiredSubmitOpinion: false,
      printAfterSubmit: false,
      requiredRollbackOpinion: false,
      submitModeOfAfterRollback: 'default',
      copyToSkipTask: false,

      requiredTransferOpinion: false,
      isSetTransferUser: '2',
      transferUsers: [],
      transferViewFormMode, // 转办表单权限
      transferOperateRight: 'default',

      requiredCounterSignOpinion: false,
      isSetCounterSignUser: '2',
      counterSignUsers: [],
      counterSignViewFormMode, // 会签表单权限
      counterSignOperateRight: 'default',

      requiredAddSignOpinion: false,
      isSetAddSignUser: '2',
      addSignUsers: [],
      addSignViewFormMode, // 加签表单权限
      addSignOperateRight: 'default',

      requiredCancelOpinion: false,
      requiredRemindOpinion: false,
      requiredHandOverOpinion: false,
      requiredGotoTaskOpinion: false,
      isSetCopyUser: '2',
      copyUsers: [],
      ...opinion
    };
  }
  getDefaultRightsOld(type) {
    let defaultRights = [];
    if (this.diction && this.diction[type]) {
      const rights = this.diction[type];
      for (let index = 0; index < rights.length; index++) {
        const item = rights[index];
        if (item.isDefault === '1') {
          if (item.value === 'B004026' && type == 'startRights') {
            // 发起权限 默认不显示必须签署意见
            continue;
          }
          if ((item.value === 'B004008' || item.value === 'B004010' || item.value === 'B004012') && type == 'adminRights') {
            // 监督权限 默认不显示抄送、关注、取消关注
            continue;
          }
          defaultRights.push({
            type: 32,
            value: item.value,
            argValue: null
          });
        }
      }
    }
    return defaultRights;
  }
  setFormDefinition(formDefinition) {
    this.formDefinition = formDefinition;
    this.definitionVjson = this.getDefinitionVjson();
    this.setFormField();
    this.setFormLayout();
  }
  getFormDefinition() {
    return this.formDefinition;
  }
  getDefinitionVjson() {
    let definitionVjson = undefined;
    const { formDefinition } = this;
    if (formDefinition) {
      definitionVjson = JSON.parse(formDefinition.definitionVjson);
    }
    return definitionVjson;
  }
  setGraphItem(graphItem) {
    this.graphItem = graphItem;
  }
  setFormLayout() {
    this.formLayouts = this.getFormLayout();
  }
  getFormLayout(formDefinition = this.definitionVjson) {
    let dataSource = [];
    if (!formDefinition) {
      return dataSource;
    }
    const { widgets } = formDefinition;
    if (widgets && widgets.length) {
      const formatDatas = datas => {
        if (datas && datas.length) {
          for (let index = 0; index < datas.length; index++) {
            const item = datas[index];
            let visible = item.configuration.defaultVisible;
            if (item.wtype === 'WidgetFormLayout') {
              // if (!visible) {
              //   // 和62一致表单隐藏的布局不显示在列表
              //   continue;
              // }
              // 布局
              dataSource.push({
                title: item.title,
                value: item.id,
                wtype: item.wtype,
                name: item.name,
                widget: item,
                visible
              });
            } else if (item.wtype === 'WidgetTab') {
              // 标签
              const tabs = item.configuration.tabs;
              for (let i = 0; i < tabs.length; i++) {
                const tab = tabs[i];
                let visible = tab.configuration.defaultVisible !== undefined ? tab.configuration.defaultVisible : true;
                dataSource.push({
                  title: tab.title,
                  value: tab.id,
                  wtype: tab.wtype, // WidgetTabItem
                  widget: item,
                  name: '页签',
                  visible
                });
                formatDatas(tab.configuration.widgets);
              }
            }
            // else if (item.wtype === 'WidgetCard') {
            //   // 卡片
            //   dataSource.push({
            //     title: item.title,
            //     value: item.id,
            //     wtype: item.wtype,
            //     visible
            //   });
            // }
            // WidgetAnchor:锚点 WidgetStatePanel:状态面板 表单上没有显示设置
            formatDatas(item.configuration.widgets);
          }
        }
      };
      formatDatas(widgets);
    }
    return dataSource;
  }
  setFormField(formDefinition = this.definitionVjson) {
    let subformFieldMap = {};
    let formFieldDefinition = {};

    const { fields } = formDefinition;

    const getFieldList = (datas, subformField = false, subformUuid = '', subWidgetId = '') => {
      let fieldList = [];
      for (let index = 0; index < datas.length; index++) {
        let id, code, name, visible, edit, required, widget, wtype;
        const item = datas[index];

        if (subformField) {
          if (!item.widget) {
            continue;
          }
          id = item.widget.id;
          code = item.widget.configuration.code;
          name = item.widget.configuration.name;
          visible = item.defaultDisplayState !== 'hidden';
          edit = item.defaultDisplayState === 'edit';
          required = item.required ? true : false;
          widget = item.widget;
        } else {
          const { configuration } = item;
          id = item.id;
          code = configuration.code;
          name = configuration.name;
          visible = configuration.defaultDisplayState !== 'hidden';
          edit = configuration.defaultDisplayState === 'edit';
          required = configuration.required ? true : false;
          widget = item;
        }
        wtype = widget.wtype;
        const field = {
          id,
          code,
          name,
          visible,
          edit,
          required,
          widget,
          wtype,
          subformField,
          subformUuid,
          subWidgetId
        };
        fieldList.push(field);
      }
      return fieldList;
    };
    if (fields && fields.length) {
      formFieldDefinition.uuid = this.formDefinition.uuid;
      formFieldDefinition.name = this.formDefinition.name;
      formFieldDefinition.fields = getFieldList(fields);
    }
    if (formDefinition.subforms && formDefinition.subforms.length) {
      const { subforms } = formDefinition;
      for (let index = 0; index < subforms.length; index++) {
        const item = subforms[index];
        const { configuration } = item;
        const subformUuid = configuration.formUuid;
        const subformName = configuration.formName;
        const subWidgetId = item.id;
        subformFieldMap[subformUuid] = {
          uuid: subformUuid,
          name: subformName,
          fields: getFieldList(configuration.columns, true, subformUuid, subWidgetId)
        };
      }
    }
    this.formFieldDefinition = formFieldDefinition;
    this.subformFieldMap = subformFieldMap;
  }
  setAllOrgs(orgs) {
    // isDefault: true 默认行政组织
    this.allOrgs = orgs;
  }
  setEnableOrgs(orgs) {
    this.orgsEnable = orgs;
  }
  setOrganizationList(data) {
    this.organizationList = data;
  }
  // 按id从所有组织获取"行政组织"信息
  getOrgInfoFromAllOrgById(id) {
    let info;
    for (let index = 0; index < this.allOrgs.length; index++) {
      const item = this.allOrgs[index];
      if (item.id === id) {
        info = item;
        break;
      }
    }
    return info;
  }
  // 按id从所有组织获取"业务组织"信息
  getBizOrgInfoFromAllOrgById(id) {
    let info;
    for (let index = 0; index < this.allOrgs.length; index++) {
      const item = this.allOrgs[index];
      if (!item.bizOrgs || !item.bizOrgs.length) {
        continue;
      }

      info = item.bizOrgs.find(b => b.id === id);
      if (info) {
        break;
      }
    }
    return info;
  }
  // 获取业务组织列表-通过行政组织id
  getBizOrgsListByOrgId(id) {
    let list = [];
    for (let index = 0; index < this.orgsEnable.length; index++) {
      const item = this.orgsEnable[index];
      if (item.id === id) {
        list = item.bizOrgs;
        break;
      }
    }
    return list;
  }
  // 获取行政组织信息-通过行政组织id
  getOrgInfoById(id) {
    let info;
    for (let index = 0; index < this.orgsEnable.length; index++) {
      const item = this.orgsEnable[index];
      if (item.id === id) {
        info = item;
        break;
      }
    }
    return info;
  }
  // 获取业务组织信息-通过业务组织id
  getBizOrgById(id) {
    let info;
    for (let index = 0; index < this.orgsEnable.length; index++) {
      const item = this.orgsEnable[index];
      if (!item.bizOrgs.length) {
        continue;
      }

      info = item.bizOrgs.find(b => b.id === id);
      if (info) {
        break;
      }
    }
    return info;
  }
  // 获取业务组织节点，bizOrgIds不传获取所有
  getBizOrgTreeNode(orgId, bizOrgIds, parentSelect = true) {
    let treeNode;

    if (bizOrgIds && typeof bizOrgIds === 'string') {
      bizOrgIds = bizOrgIds.split(';');
    }
    for (let index = 0; index < this.orgsEnable.length; index++) {
      const item = this.orgsEnable[index];
      if (item.id === orgId) {
        treeNode = JSON.parse(JSON.stringify(item));
        if (!parentSelect) {
          treeNode.selectable = false;
        }
        if (bizOrgIds && item.bizOrgs) {
          const bizOrgs = item.bizOrgs.filter(b => bizOrgIds.includes(b.id));
          treeNode.bizOrgs = bizOrgs;
        }
      }
    }
    return treeNode;
  }
  setBizOrgIdRoleMap(data, bizOrgList) {
    data.forEach((role, index) => {
      const bizOrgId = bizOrgList[index]['id'];
      this.addBizOrgRoles({
        bizOrgId,
        options: role
      });
    });
  }
  addBizOrgRoles({ bizOrgId, options }) {
    let rulesMap = {};
    let rules = [];
    if (options) {
      options.forEach(item => {
        rulesMap[item.id] = item;
        rules.push(item);
      });
    }
    this.bizOrgIdRoleOptions[bizOrgId] = rules;
    this.bizOrgIdRoleMap[bizOrgId] = rulesMap;
  }
  // 设置表单变量
  setDyformVarList(result) {
    let children = [];
    for (let key in result.fields) {
      let field = result.fields[key];
      children.push({
        title: field.displayName,
        label: field.displayName,
        key: field.name,
        value: '${' + field.name + '}',
      });
    }
    this.dyformVarList = children
  }
  setLanguageOptions(options, vueInstance) {
    this.languageOptions = options
    this.vueInstance = vueInstance
  }
  translateI18nAll(word, from = 'zh') {
    let promise = [], locale = [], localeMap = {}
    this.languageOptions.map(item => {
      if (item.value.indexOf(from) === -1) {
        const to = item.value.split('_')[0]
        locale.push(item.value)
        promise.push(this.vueInstance.$translate(word, from, to))
      }
    })

    return new Promise((resolve, reject) => {
      Promise.all(promise).then(result => {
        locale.map((item, index) => {
          localeMap[item] = result[index]
        })
        resolve(localeMap)
      }, err => {
        reject(err)
      })
    })
  }
  // 同步后台按钮操作名称
  syncButtons(workFlow) {
    if (!workFlow.graphData) {
      return
    }
    const buttons = this.flowSettingActions.buttons
    const graphData = JSON.parse(workFlow.graphData);
    let { cells } = graphData
    let edited = []// 已修改的按钮
    for (let index = 0; index < cells.length; index++) {
      const cell = cells[index];
      const cellData = cell.data;
      const shape = cell.shape;
      if (shape === constant.NodeTask || shape === constant.NodeCollab) {
        for (const rightKey in flowTaskRights) {
          if (cellData[rightKey]) {
            for (const rightItem of cellData[rightKey]) {
              if (rightItem.name === rightItem.title) {
                const findButton = buttons.find(btn => btn.code === rightItem.value)
                if (findButton) {
                  rightItem.title = findButton.title
                }
              } else {
                edited.push(rightItem)
              }
            }
          }
        }
      }
    }
    this.addOrDeleteButtons(cells)
    workFlow.graphData = JSON.stringify(graphData)
  }
  addOrDeleteButtons(cells) {
    const rights = this.flowSettingActions.rights;
    for (let index = 0; index < cells.length; index++) {
      const cell = cells[index];
      const cellData = cell.data;
      const shape = cell.shape;
      if (shape === constant.NodeTask || shape === constant.NodeCollab) {
        for (const key in flowTaskRights) {
          let rightOptions = cellData[key]
          if (rightOptions && rightOptions.length) {
            const type = flowTaskRights[key]['settingKey'];
            let added = [], deleted = []
            rightOptions.map(right => {
              const findItem = rights.find(f => f.code === right.value)
              if (!findItem) {
                deleted.push(right)
              }
            })
            rights.map(item => {
              if (item[type] && item[type]['apply']) {
                const findItem = rightOptions.find(f => f.value === item.code)
                if (!findItem) {
                  added.push(item)
                }
              }
            })
            added.map(item => {
              rightOptions.push({
                value: item.code,
                title: item.title,
                name: item.title,
                defaultVisible: item[type]['defaultVisible'],
                requiredOpinion: item[type]['requiredOpinion'],
                displayState: 'label'
              })
            })
            deleted.map(item => {
              let deleteIndex = rightOptions.findIndex(f => f.value == item.value);
              if (deleteIndex > -1) {
                rightOptions.splice(deleteIndex, 1);
              }
            })
          }
        }
      }
    }
  }
}

export default FlowDesigner;
