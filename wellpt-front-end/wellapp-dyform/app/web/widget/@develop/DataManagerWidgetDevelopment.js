import VueWidgetDevelopment from '@develop/VueWidgetDevelopment';

class DataManagerWidgetDevelopment extends VueWidgetDevelopment {

  dataUrl(params) {
    let url = '/dms/data/manager',
      urlParams = [];
    for (let p in params) {
      if (params[p]) {
        urlParams.push(`${p}=${params[p]}`);
      }
    }
    return url + '?' + urlParams.join('&');
  }

  open(event, dataUuid) {
    let _this = this;
    // 跳转创建表单数据
    if (event.targetPosition === 'newWindow' || event.targetPosition === 'currentWindow') {
      event.getWidget(event.dmsId, function (widget) {
        event.render({
          targetPosition: event.targetPosition,
          url: _this.dataUrl({
            dmsId: event.dmsId,
            wTableId: event.meta.wTableId,
            formUuid: widget.configuration.formUuid,
            dataUuid: dataUuid,
            displayState: event.option.displayState
          })
        });
      });
    } else if (event.targetPosition === 'widgetLayout' || event.targetPosition === 'widgetModal' || event.targetPosition === 'widgetTab') {
      this.refreshDms(event, {
        dmsId: event.dmsId,
        containerWid: event.containerWid,
        targetPosition: event.targetPosition,
        displayState: event.option.displayState,
        wTableId: event.meta.wTableId
      });
    }
  }

  refreshDms(event, options) {
    let { dmsId, containerWid, targetPosition, dataUuid, displayState, wTableId } = options;
    event.getWidget(dmsId, function (widget) {
      widget.props = {
        containerWid: containerWid, // 容器ID
        targetPosition: targetPosition,
        // 传递属性参数
        dataUuid: dataUuid,
        formUuid: widget.configuration.formUuid,
        wTableId: wTableId,
        displayState: displayState || 'edit'
      };

      let ruleKey = 'formElementRules',
        formElementRules = {};
      if (dataUuid) {
        ruleKey = displayState === 'edit' ? 'editStateFormElementRules' : 'labelStateFormElementRules';
      }
      if (widget.configuration[ruleKey]) {
        for (let i = 0, len = widget.configuration[ruleKey].length; i < len; i++) {
          formElementRules[widget.configuration[ruleKey][i].id] = {
            readonly: widget.configuration[ruleKey][i].readonly,
            editable: widget.configuration[ruleKey][i].editable,
            disable: widget.configuration[ruleKey][i].disable,
            hidden: widget.configuration[ruleKey][i].hidden,
            displayAsLabel: widget.configuration[ruleKey][i].displayAsLabel,
            required: widget.configuration[ruleKey][i].required
          };

          let children = widget.configuration[ruleKey][i].children;
          if (children) {
            let childrenRules = {};
            formElementRules[widget.configuration[ruleKey][i].id].children = childrenRules;
            for (let j = 0, jlen = children.length; j < jlen; j++) {
              childrenRules[children[j].id] = {
                readonly: children[j].readonly,
                editable: children[j].editable,
                disable: children[j].disable,
                hidden: children[j].hidden,
                displayAsLabel: children[j].displayAsLabel,
                required: children[j].required
              };
            }
          }
        }
        widget.props.formElementRules = formElementRules;
      }

      widget.level = 1;
      let renderOption = { widgets: [widget], containerWid: containerWid, targetPosition: targetPosition };
      if (targetPosition === 'widgetTab') {
        renderOption.title = widget.title;
        renderOption.key = dmsId;
      }
      if (targetPosition === 'widgetModal') {
        renderOption.title = ''; // title 等待被更新
        widget.configuration.buttonPosition = 'disable'; // 禁止按钮在表单设置页面里展示
        widget.configuration.titleVisible = false; // 禁止内部显示标题
      }
      event.render(renderOption);
    });
  };

  create(event) {
    this.open(event, null);
  }

  modify(event) {
    this.open(event, event.meta.dataUuid);
  };

  submit(formData, callback) {
    $axios.post('/proxy/api/dyform/data/saveFormData', formData.dyFormData).then(({ data }) => {
      callback(data.data);
    });
  }

  // 校验并保存
  validateAndSave(event) {
    let $dyform = event.$evtWidget; // 表单组件实例
    let _this = this;
    $dyform.collectFormData(true, function (valid, msg, formData) {
      if (valid) {
        _this.submit(formData, function (dataUuid) {
          if (dataUuid) {
            event.meta.dataUuid = dataUuid;
            _this.afterSaveSuccess(event);
          }
        });
      }
      //TODO: 弹窗提示
    });
  }

  afterSaveSuccess(event) {
    let $dyform = event.$evtWidget; // 表单组件实例
    let _this = this;
    $dyform.$message.success('保存成功', 1).then(function () {
      if (event.meta.wTableId) {
        // 刷新表格
        $dyform.pageContext.emitCrossTabEvent(`${event.meta.wTableId}:refetch`, true);
        $dyform.pageContext.emitEvent(`${event.meta.wTableId}:refetch`, true);
      }
      // 跳转到编辑状态：根据数据管理的配置
      if (event.meta.dmsId) {
        // 判断当前是在浏览器窗口、还是弹窗、或者tab、或者布局内
        if (event.meta.containerWid) {
          _this.refreshDms(event, {
            dataUuid: event.meta.dataUuid,
            dmsId: event.meta.dmsId,
            containerWid: event.meta.containerWid,
            targetPosition: event.meta.targetPosition,
            wTableId: event.meta.wTableId
          });
        } else {
          // 刷新
          if (location.search.indexOf(`dataUuid=${event.meta.dataUuid}`) === -1) {
            location.href += location.search ? '' : '?' + `&dataUuid=${event.meta.dataUuid}&displayState=edit`;
          } else {
            window.location.reload();
          }
        }
      }
    });
  }

  save(event) {
    this.validateAndSave(event);
    // let $dyform = event.$evtWidget; // 表单组件实例
    // let formData = $dyform.collectFormData();
    // this.submit(formData, function (dataUuid) {
    //   if (dataUuid) {
    //     _this.afterSaveSuccess(event);
    //   }
    // });
  }

  delete(event) {
    // 删除数据 dyFormActionService
    let dataUuid = event.meta.dataUuid;
    if (dataUuid) {
      let uuids = [];
      if (Array.isArray(dataUuid)) {
        for (let i = 0, len = dataUuid.length; i < len; i++) {
          uuids.push({ uuid: dataUuid[i] });
        }
      } else {
        uuids.push({ uuid: dataUuid });
      }

      event.getWidget(event.dmsId, function (widget) {
        $axios
          .post('/json/data/services', {
            serviceName: 'dyFormActionService',
            methodName: 'delete',
            args: JSON.stringify([uuids, widget.configuration.formUuid, false])
          })
          .then(({ data }) => {
            console.log(data);
          });
      });
    }
  }

  get META() {
    return {
      name: '数据管理',
      hook: {
        open: '打开数据',
        create: '新建',
        modify: '修改',
        save: '保存',
        delete: '删除'
      }
    };
  }
}


export default DataManagerWidgetDevelopment;
