import VuePageDevelopment from '@develop/VuePageDevelopment';
import { generateId } from '@framework/vue/utils/util';
const DEFAULT_SYS_MODEL_FIELD = [
  'UUID',
  'CREATOR',
  'CREATE_TIME',
  'MODIFY_TIME',
  'MODIFIER',
  'TENANT',
  'SYSTEM',
  'REC_VER',
  'SYSTEM_UNIT_ID',
  'STATUS',
  'FORM_UUID'
];
class DataModelManagerPageDevelopment extends VuePageDevelopment {


  constructor() {
    super(arguments);
    this.currentDataModelDetail = null;
  }
  mounted() {
    let _this = this;
    // 监听树形节点选中事件
    this.getPageContext().handleEvent(`YTdSPhOxMqmmSldtWFtEVbKUFZHeSqjd:treeNodeSelected`, ({ selected, node }) => {
      let _originalData = node.dataRef._originalData || {};
      let { type, uuid } = _originalData;

      // 切换状态
      _this.emitEvent('jwEQfXRmJTKuRGNnLHlaIRBXosVrfgWl:setState', type);

      if (selected && uuid) {
        this.$loading('数据加载中');
        // 获取数据
        $axios.get(`/proxy/api/dm/getDetails`, { params: { uuid } }).then(({ data }) => {
          _this.$loading(false);
          _this.$widget.$nextTick(() => {
            let model = data.data;
            _this.currentDataModelDetail = data.data;
            if (type === 'TABLE') {
              // 获取表单，并设置表单数据
              let wForm = _this.getVueWidgetById('rAvssJpqlIKsXtalMgZMeNGMTCANLwLZ'); // 信息表单

              if (model.columnJson != undefined) {
                model.columnJson = JSON.parse(model.columnJson);
                // 属性选项
                let columnOptions = _this.getPageState().columnOptions;
                columnOptions.length = 0;
                for (let i = 0, len = model.columnJson.length; i < len; i++) {
                  columnOptions.push({
                    label: model.columnJson[i].title,
                    value: model.columnJson[i].column
                  });
                }
              }

              if (model.ruleJson != undefined) {
                model.ruleJson = JSON.parse(model.ruleJson);
              }

              wForm.setFormData({ ...model }); // 设置表单数据

              // 刷新属性列表
              _this.getPageContext().emitEvent('wWKtEMYQWwHlLUYYPNlXbeCSlSYWXsTX:refetch');
              _this.getPageContext().emitEvent('MprRuBtqCipUXPGuMnqkaYqQITYkQBPc:refetch');

              let widget = _this.getVueWidgetById('MprRuBtqCipUXPGuMnqkaYqQITYkQBPc');
              if (widget) {
                widget.invokeDevelopmentMethod('resetColumn');
              }

              _this.getPageContext().emitEvent('dHhoTKBNkNCaFuLYPZgorwSjXgIUDzJT:refetch');
            } else if (type === 'VIEW') {
              if (model.modelJson != undefined) {
                model.modelJson = JSON.parse(model.modelJson);
              }
              _this.getPageContext().emitEvent('get-data-model-view-detail', model);
            }
            _this.model = model;
          });
        });
      } else {
        let wForm = _this.getVueWidgetById('rAvssJpqlIKsXtalMgZMeNGMTCANLwLZ'); // 信息表单
        wForm.resetForm();
      }
    });

    this.getPageContext().handleEvent('view-object-detail-mounted', () => {
      _this.getPageContext().emitEvent('get-data-model-view-detail', _this.model);
    });
  }

  saveTableColConfig({ $widget, form }) {
    let wForm = this.getVueWidgetById('rAvssJpqlIKsXtalMgZMeNGMTCANLwLZ'); // 信息表单
    if (wForm.form.columnJson == undefined) {
      wForm.form.columnJson = [];
    }
    let col = JSON.parse(JSON.stringify(form));
    if (form.uuid !== undefined) {
      // 更新数据
      for (let i = 0, len = wForm.form.columnJson.length; i < len; i++) {
        if (wForm.form.columnJson[i].uuid === form.uuid) {
          let keys = Array.from(new Set(Object.keys(wForm.form.columnJson[i]).concat(Object.keys(col))));
          for (let key of keys) {
            wForm.form.columnJson[i][key] = col[key];
          }
          break;
        }
      }
    } else {
      col.uuid = generateId();
      col.notNull = !!col.notNull;
      wForm.form.columnJson.push(col);
    }

    let columnOptions = this.getPageState().columnOptions;
    columnOptions.length = 0;
    for (let i = 0, len = wForm.form.columnJson.length; i < len; i++) {
      columnOptions.push({
        label: wForm.form.columnJson[i].title,
        value: wForm.form.columnJson[i].column
      });
    }

    this.getPageContext().emitEvent('IlXonjpBzXxDrXFzYGOkDfBVmANnFQND:closeDrawer');
    this.getPageContext().emitEvent('wWKtEMYQWwHlLUYYPNlXbeCSlSYWXsTX:refetch');
  }


  saveTableConfig() {
    this.$loading('保存中');
    let wForm = this.getVueWidgetById('rAvssJpqlIKsXtalMgZMeNGMTCANLwLZ'); // 信息表单
    let _this = this,
      model = JSON.parse(JSON.stringify(wForm.form));

    model.columnJson = JSON.stringify(model.columnJson);
    if (model.ruleJson) {
      model.ruleJson = JSON.stringify(model.ruleJson);
    }
    // model.indexJson = JSON.stringify(model.indexJson);
    $axios
      .post(`/proxy/api/dm/save`, model)
      .then(({ data }) => {
        _this.$loading(false);
        if (data.code != 0) {
          _this.$widget.$error({
            title: '保存失败',
            content: _this.$widget.$createElement('div', {
              domProps: {
                innerHTML: Array.from(new Set(data.msg.split('\n'))).join('<br>')
              }
            })
          });
          console.error(data);
        } else {
          _this.$widget.$message.success('保存成功');
        }
      })
      .catch(() => {
        _this.$widget.$message.error('保存失败');
        _this.$loading(false);
      });
  }

  clearCurrentTablCol() {
    let model = this.getPageState().currentTableCol;
    for (let k in model) {
      model[k] = undefined;
    }
  }

  clearValiRuleConfig() {
    let model = this.getPageState().currentValiRule;
    for (let k in model) {
      model[k] = undefined;
    }
  }

  saveTableValiRuleConfig({ $widget, form }) {
    let wForm = this.getVueWidgetById('rAvssJpqlIKsXtalMgZMeNGMTCANLwLZ'); // 信息表单
    if (wForm.form.ruleJson == undefined) {
      wForm.form.ruleJson = [];
    }
    let col = JSON.parse(JSON.stringify(form));

    if (form.uuid !== undefined) {
      // 更新数据
      for (let i = 0, len = wForm.form.ruleJson.length; i < len; i++) {
        if (wForm.form.ruleJson[i].uuid === form.uuid) {
          Object.assign(wForm.form.ruleJson[i], col);
          break;
        }
      }
    } else {
      col.uuid = generateId();
      wForm.form.ruleJson.push(col);
    }

    this.getPageContext().emitEvent('EqbXkxqRhpMNONFcsTvpSwvagGsUBCVc:closeDrawer');
    this.getPageContext().emitEvent('dHhoTKBNkNCaFuLYPZgorwSjXgIUDzJT:refetch');
  }

  setTableColTableRowData({ tableParams, callback, $widget }) {
    let wForm = this.getVueWidgetById('rAvssJpqlIKsXtalMgZMeNGMTCANLwLZ'); // 信息表单
    if (wForm) {
      let columnJson = wForm.form.columnJson;
      if (columnJson) {
        let rowData = JSON.parse(JSON.stringify(columnJson));
        // 隐藏默认的系统字段
        if (!$widget.showDefaultCol) {
          rowData = [];
          for (let i = 0, len = columnJson.length; i < len; i++) {
            if (!DEFAULT_SYS_MODEL_FIELD.includes(columnJson[i].column)) {
              rowData.push(columnJson[i]);
            }
          }
        }
        callback({ data: rowData });
      }
    }
  }

  showSysDefaultCol(e, callback) {
    let wForm = this.getVueWidgetById('rAvssJpqlIKsXtalMgZMeNGMTCANLwLZ'); // 信息表单
    let columnJson = wForm.form.columnJson;
    let rowData = [];
    if (e.$evt.checked) {
      rowData = JSON.parse(JSON.stringify(columnJson));
      e.$evtWidget.showDefaultCol = true;
    } else {
      // 隐藏默认的系统字段
      for (let i = 0, len = columnJson.length; i < len; i++) {
        if (!DEFAULT_SYS_MODEL_FIELD.includes(columnJson[i].column)) {
          rowData.push(columnJson[i]);
        }
      }
      e.$evtWidget.showDefaultCol = false;
    }
    e.$evtWidget.onDataChange({
      data: rowData
    });

    callback(true);
  }

  setValiRuleTableRowData({ tableParams, callback, $widget }) {
    let wForm = this.getVueWidgetById('rAvssJpqlIKsXtalMgZMeNGMTCANLwLZ'); // 信息表单
    let ruleJson = wForm.form.ruleJson;
    if (ruleJson) {
      callback({ data: ruleJson });
    }
  }

  editValiRuleTableRowDrawer({ meta }) {
    this.getPageContext().emitEvent('EqbXkxqRhpMNONFcsTvpSwvagGsUBCVc:showDrawer');
    this.$widget.$nextTick(() => {
      let wForm = this.getVueWidgetById('lMQSpbXtEicERWNGyydGVdeYmtDNzYEb'); // 信息表单
      wForm.setFormData(meta);
    });
  }
  deleteCol({ meta, $evtWidget }) {
    let deleteKeys = meta.selectedRowKeys || [meta.uuid];
    let wForm = this.getVueWidgetById('rAvssJpqlIKsXtalMgZMeNGMTCANLwLZ'); // 信息表单

    let columnJson = wForm.form.columnJson;
    for (let i = 0; i < columnJson.length; i++) {
      if (deleteKeys.includes(columnJson[i].uuid) && !columnJson[i].isSysDefault) {
        columnJson.splice(i--, 1);
      }
    }

    $evtWidget.refetch();
  }

  deleteRule({ meta, $evtWidget }) {
    let deleteKeys = meta.selectedRowKeys || [meta.uuid];
    $evtWidget.deleteRowsByKeys(deleteKeys);
  }

  editTableColDetailDrawer({ meta }) {
    this.getPageContext().emitEvent('IlXonjpBzXxDrXFzYGOkDfBVmANnFQND:showDrawer');
    this.$widget.$nextTick(() => {
      // 获取表单
      let widgetForm = this.getVueWidgetById('AjFTeeKNHNgKMsaDGjUPwetxIvOCXXVB');
      widgetForm.setFormData(meta);
    });
  }

  saveTablePersistData({ form, $widget }) {
    let wForm = this.getVueWidgetById('rAvssJpqlIKsXtalMgZMeNGMTCANLwLZ'); // 信息表单
    let submitData = { ...form, field: {} };
    $axios.post(`/proxy/api/dm/saveTable/${wForm.form.id}`, submitData).then(({ data }) => {
      console.log(data);
      _this.$widget.refetch();
    });
  }

  newDataModel({ key, domEvent }) {
    let title = domEvent.target.innerText.trim();
    // let model = this.getPageState().newDataModel;
    // model.type = key;
    // model.columnJson = undefined;
    this.getPageContext().emitEvent('nZTxSEyRvjTeJqnIugdYuBVixYAcTgJy:showModal', null, null, title);
    let _this = this;
    this.$widget.$nextTick(() => {
      let widgetForm = _this.getVueWidgetById('nFvYSPADpwNVkBsQjUgUFLGfgGbzSTgF'); // 获取表单组件，设置表单数据
      widgetForm.setField('type', key);
      if (key == 'TABLE') {
        widgetForm.setField(
          'columnJson',
          JSON.stringify([
            {
              uuid: 'UUID',
              column: 'UUID',
              dataType: 'number',
              length: 19,
              notNull: true,
              title: 'UUID',
              remark: '数据唯一标识字段',
              unique: 'GLOBAL',
              isSysDefault: true
            },
            {
              uuid: 'CREATOR',
              column: 'CREATOR',
              dataType: 'varchar',
              length: 64,
              notNull: true,
              title: '创建人',
              isSysDefault: true
            },
            {
              uuid: 'CREATE_TIME',
              column: 'CREATE_TIME',
              dataType: 'timestamp',
              notNull: true,
              title: '创建时间',
              isSysDefault: true
            },
            {
              uuid: 'MODIFIER',
              column: 'MODIFIER',
              dataType: 'varchar',
              length: 64,
              title: '修改人',
              isSysDefault: true
            },
            {
              uuid: 'MODIFY_TIME',
              column: 'MODIFY_TIME',
              dataType: 'timestamp',
              title: '修改时间',
              isSysDefault: true
            },
            {
              uuid: 'TENANT',
              column: 'TENANT',
              dataType: 'varchar',
              length: 64,
              title: '归属租户',
              isSysDefault: true
            },
            {
              uuid: 'SYSTEM',
              column: 'SYSTEM',
              dataType: 'varchar',
              length: 64,
              title: '归属系统',
              isSysDefault: true
            },
            {
              uuid: 'REC_VER',
              column: 'REC_VER',
              dataType: 'number',
              length: 9,
              title: '版本号',
              isSysDefault: true
            },
            {
              uuid: 'STATUS',
              column: 'STATUS',
              dataType: 'number',
              length: 1,
              title: '数据状态',
              isSysDefault: true
            },
            {
              uuid: 'SYSTEM_UNIT_ID',
              column: 'SYSTEM_UNIT_ID',
              dataType: 'varchar',
              length: 32,
              title: '单位ID',
              isSysDefault: true
            },
            {
              uuid: 'FORM_UUID',
              column: 'FORM_UUID',
              dataType: 'varchar',
              length: 64,
              title: '表单UUID',
              isSysDefault: true
            }
          ])
        );
        // 添加系统默认字段
      }
    });
  }

  redirect2formCreate() {
    window.open(`/dyform-designer/index?dataModelUuid=${this.currentDataModelDetail.uuid}`, '_blank');
  }

  get META() {
    return {
      name: '数据模型管理页二开',
      hook: {
        saveTableColConfig: '保存属性',
        saveTableConfig: '保存存储对象',
        clearCurrentTablCol: '清空属性',
        saveTableValiRuleConfig: '保存校验规则',
        clearValiRuleConfig: '清空校验规则',
        setTableColTableRowData: '设置属性表格行数据',
        deleteCol: '删除属性表格行数据',
        editTableColDetailDrawer: '编辑属性表格行数据',
        setValiRuleTableRowData: '设置规则表格行数据',
        editValiRuleTableRowDrawer: '编辑校验表格行数据',
        deleteRule: '删除校验表格行数据',
        saveTablePersistData: '保存存储数据',
        showSysDefaultCol: '显示内置属性',
        redirect2formCreate: '新开窗开创建数据模型表单'
      }
    };
  }
}


export default DataModelManagerPageDevelopment;
