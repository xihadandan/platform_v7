import WorkFlowErrorHandler from '@workflow/app/web/page/workflow-work/component/WorkFlowErrorHandler.js';
import WorkFlowInteraction from '@workflow/app/web/page/workflow-work/component/WorkFlowInteraction.js';
import WorkView from '@workflow/app/web/page/workflow-work/component/work-view.vue';
import { getTaskUserLabel, getTaskDecisionMakerLabel, addSystemPrefix } from "./utils.js";
import { isEmpty } from 'lodash'
const formElementRules = WorkView.computed.formElementRules;
class WorkflowSimulation {
  constructor({ $widget, flowDefinition, showLoading, showLoadingFlowName, onSuccess, onPause }) {
    const _this = this;
    _this.$widget = $widget;
    _this._flowDefinition = flowDefinition;
    _this.showLoading = showLoading;
    _this.showLoadingFlowName = showLoadingFlowName;
    _this.onSuccess = onSuccess;
    _this.onPause = onPause;
    _this.params = {
      runNum: 1,
      formDataSource: 'create',
      interactionMode: 'requiredField',
      autoViewWorkAfterSimulation: false,
      pauseTasks: [],
      endTasks: [],
      interactionTasks: [],
      tasks: []
    };
    _this.defaultStartUserId = $widget._$USER && $widget._$USER.userId;
    _this.defaultStartUserName = $widget._$USER && $widget._$USER.userName;
    _this.multiStartUser = false;
    _this.inited = false;
    _this.state = {
      code: 'inited', // running、pause
      tip: ''
    };
    _this.simulationData = {};
    _this.userInteractedTasks = [];
    _this.errorHandler = new WorkFlowErrorHandler(_this);
    _this.workFlow = new WorkFlowInteraction();
    _this.flowSettingPromise = _this.loadGlobalSetting().then(() => {
      return _this.loadFlowSetting();
    });
    _this.records = [];
    _this.recordRetainDays = 3;
    _this.activeKey = 'flow';
  }

  get flowDefinition() {
    return this._flowDefinition || this.$widget.flowDefinition;
  }

  loadGlobalSetting() {
    if (EASY_ENV_IS_NODE) {
      return;
    }

    return $axios.get('/proxy/api/workflow/setting/getByKey?key=FLOW_SIMULATION').then(({ data: result }) => {
      if (result.data && result.data.attrVal) {
        let setting = JSON.parse(result.data.attrVal);
        if (setting.params.startUserId) {
          this.defaultStartUserId = setting.params.startUserId;
          this.defaultStartUserName = setting.params.startUserName;
        }
        this.multiStartUser = setting.params.multiStartUser;
        if (setting.params.recordRetainDays > 0) {
          this.recordRetainDays = setting.params.recordRetainDays;
        }
      }
    });
  }

  loadFlowSetting() {
    const _this = this;
    return $axios.get(`/proxy/api/workflow/simulation/setting/getByFlowDefUuid?flowDefUuid=${_this.flowDefinition.uuid}`).then(({ data: result }) => {
      if (result.data && result.data.definitionJson) {
        let params = JSON.parse(result.data.definitionJson);
        for (let key in params) {
          _this.$widget.$set(_this.params, key, params[key]);
        }
      }
    }).then(() => {
      if (!_this.params.startUserId) {
        _this.params.startUserId = _this.defaultStartUserId;
        _this.params.startUserName = _this.defaultStartUserName;
      }
      _this.inited = true;
    });
  }

  getTaskUserLabel(taskId) {
    let taskConfig = this.flowDefinition.tasks.find(task => task.id == taskId);
    if (!taskConfig) {
      return '';
    }
    return getTaskUserLabel(taskConfig);
  }

  getTaskDecisionMakerLabel(taskId) {
    let taskConfig = this.flowDefinition.tasks.find(task => task.id == taskId);
    if (!taskConfig) {
      return '';
    }
    return getTaskDecisionMakerLabel(taskConfig);
  }

  /**
   *
   * @param {*} saveParams
   */
  readyAndstart(saveParams = true) {
    this.flowSettingPromise.then(() => {
      this.start(saveParams);
    })
  }

  /**
   * 开始流程仿真
   */
  start(saveParams = true) {
    const _this = this;
    _this.workUrls = [];
    _this.startUserIndex = 0;
    _this.currentRunNum = 1;
    _this.userInteractedTasks = [];
    _this.pauseHideLoading = false;
    _this.created = false;
    _this.state.code = 'start';
    if (saveParams) {
      _this.saveParams().then(() => {
        _this.simulationData = {};
        _this.doSimulation();
      });
    } else {
      _this.simulationData = {};
      _this.doSimulation();
    }
  }

  /**
   * 保存仿真参数
   *
   * @returns
   */
  saveParams() {
    const _this = this;
    let promise = null;
    if (_this.recordParams) {
      promise = $axios.post('/proxy/api/workflow/simulation/paramsChanged', [_this.recordParams, _this.params]).then(({ data: result }) => {
        if (result.data) {
          return new Promise((resolve, reject) => {
            _this.$widget.$confirm({
              title: '确认',
              content: `仿真记录的仿真参数与最新配置的仿真参数不一致，是否替换掉最新的仿真参数`,
              okText: '是',
              cancelText: '否',
              onOk() {
                for (let key in _this.recordParams) {
                  _this.$widget.$set(_this.params, key, _this.recordParams[key]);
                }
                _this.recordParams = null;
                $axios.post('/proxy/api/workflow/simulation/setting/save', { flowDefUuid: _this.flowDefinition.uuid, definitionJson: JSON.stringify(_this.params) }).then(() => {
                  resolve();
                });
              },
              onCancel() {
                _this.recordParams = null;
                $axios.post('/proxy/api/workflow/simulation/setting/save', { flowDefUuid: _this.flowDefinition.uuid, definitionJson: JSON.stringify(_this.params) }).then(() => {
                  resolve();
                });
              }
            })
          })
        } else {
          return $axios.post('/proxy/api/workflow/simulation/setting/save', { flowDefUuid: _this.flowDefinition.uuid, definitionJson: JSON.stringify(_this.params) }).then(() => {
          });
        }
      });
    } else {
      promise = $axios.post('/proxy/api/workflow/simulation/setting/save', { flowDefUuid: _this.flowDefinition.uuid, definitionJson: JSON.stringify(_this.params) }).then(() => {
      });
    }
    return promise;
  }

  /**
   * 暂停流程仿真
   */
  pause() {
    this.state.code = 'pause';
  }

  /**
   *
   * @param {*} recordUuid
   * @param {*} state
   */
  updateRecordStateByUuid(recordUuid, state) {
    if (recordUuid) {
      return $axios.post(`/proxy/api/workflow/simulation/record/updateState?uuid=${recordUuid}&state=${state}`);
    }
    return Promise.reject();
  }

  /**
  * 恢复流程仿真
  */
  resume() {
    const _this = this;
    _this.$widget.activeKey = 'flow';
    _this.saveParams().then(() => {
      if (_this.simulationData.flowInstUuid) {
        _this.getSimulationData(_this.simulationData.flowInstUuid).then((simulationData) => {
          _this.simulationData = simulationData;
          if (_this.isDispatching()) {
            _this.state.code = 'running';
            _this.state.tip = _this.getRunningStateTip(_this.simulationData);
            setTimeout(() => {
              _this.startNextStepSimulationIfRequire({ data: {} });
            }, 2000);
          } else if (_this.simulationData.isOver !== true) {
            _this.doSimulation();
          } else {
            _this.onSimulationSuccess();
          }
        })
      } else {
        _this.doSimulation();
      }
    });
  }

  /**
   * 根据记录恢复流程仿真
   *
   * @param {*} record
   */
  resumeByRecord(record) {
    const _this = this;
    _this.$widget.activeKey = 'flow';
    // 当前记录继续仿真
    if (_this.simulationData.recordUuid == record.uuid) {
      _this.resume();
    } else {
      let contentJson = JSON.parse(record.contentJson);
      let params = contentJson.params;
      _this.recordParams = params;
      // for (let key in params) {
      //   _this.$widget.$set(_this.params, key, params[key]);
      // }

      _this.updateRecordStateByUuid(record.uuid, 'running').then(() => {
        _this.initWorkflowStartState(params.startTaskId);
        _this.getSimulationData(record.flowInstUuid).then((simulationData) => {
          _this.simulationData = simulationData;
          if (_this.isDispatching()) {
            _this.state.code = 'running';
            _this.state.tip = _this.getRunningStateTip(_this.simulationData);
            setTimeout(() => {
              _this.startNextStepSimulationIfRequire({ data: {} });
            }, 2000);
          } else if (_this.simulationData.isOver !== true) {
            _this.doSimulation();
          } else {
            _this.onSimulationSuccess();
          }
        });
      });
    }
  }

  /**
   *
   * 根据记录重新流程仿真
   *
   * @param {*} record
   */
  restartByRecord(record) {
    const _this = this;
    _this.$widget.activeKey = 'flow';
    let contentJson = JSON.parse(record.contentJson);
    let params = contentJson.params;
    _this.recordParams = params;
    // for (let key in params) {
    //   _this.$widget.$set(_this.params, key, params[key]);
    // }
    _this.initWorkflowStartState(params.startTaskId);

    _this.start();
  }

  /**
   *
   */
  reset() {
    const _this = this;
    _this.state.code = 'inited';
    _this.simulationData = {};
    _this.userInteractedTasks = [];
    _this.pauseHideLoading = false;
    _this.initWorkflowStartState(_this.params.startTaskId);
  }

  /**
   * 转办
   */
  transfer() {
    const _this = this;
    let simulationData = _this.collectData();
    $axios
      .post('/proxy/api/workflow/simulation/simulationTransfer', simulationData)
      .then(({ data: result }) => {
        if (result.success) {
          _this.$widget.$message.success('转办成功！');
          _this.workFlow.clearTempData();
        } else {
          _this.handlerError(result, _this.transfer);
        }
      })
      .catch(({ response }) => {
        _this.handlerError(response.data, _this.transfer);
      });
  }

  /**
   * 会签
   */
  counterSign() {
    const _this = this;
    let simulationData = _this.collectData();
    $axios
      .post('/proxy/api/workflow/simulation/simulationCounterSign', simulationData)
      .then(({ data: result }) => {
        if (result.success) {
          _this.$widget.$message.success('会签成功！');
          _this.workFlow.clearTempData();
        } else {
          _this.handlerError(result, _this.counterSign);
        }
      })
      .catch(({ response }) => {
        _this.handlerError(response.data, _this.counterSign);
      });
  }

  /**
   * 跳转
   */
  gotoTask() {
    const _this = this;
    let simulationData = _this.collectData();
    $axios
      .post('/proxy/api/workflow/simulation/simulationGotoTask', simulationData)
      .then(({ data: result }) => {
        if (result.success) {
          _this.$widget.$message.success('跳转成功！')
          _this.workFlow.clearTempData();
          _this.getSimulationData(simulationData.flowInstUuid).then(data => {
            _this.simulationData = data;
            if (data.state) {
              _this.state.code = data.state;
            }
            _this.emitEvent('simulationUpdate');
          });
        } else {
          _this.handlerError(result, _this.gotoTask);
        }
      })
      .catch(({ response }) => {
        _this.handlerError(response.data, _this.gotoTask);
      });
  }

  /**
   * 查看表单
   */
  viewForm() {
    this.$widget.activeKey = 'form';
    this.activeKey = 'form';
  }

  /**
   *
   * @param {*} record
   */
  viewByRecord(record) {
    const _this = this;
    _this.$widget.$loading();
    setTimeout(() => {
      _this.$widget.$loading(false);
    }, 600);
    _this.state.code = record.state;

    let contentJson = JSON.parse(record.contentJson);
    let params = contentJson.params;
    _this.recordParams = params;
    // for (let key in params) {
    //   _this.$widget.$set(_this.params, key, params[key]);
    // }
    _this.initWorkflowStartState(params.startTaskId);
    _this.getSimulationData(record.flowInstUuid).then((simulationData) => {
      _this.simulationData = simulationData;
    });
  }

  /**
   *
   */
  initWorkflowReadyState() {
    const _this = this;
    let workflowViewer = _this.workflowViewer || _this.$widget.$refs.workflowViewer;
    if (workflowViewer) {
      workflowViewer.initReadyState();
      _this.workflowViewer = workflowViewer;
    }
  }

  /**
   *
   * @param {*} startTaskId
   */
  initWorkflowStartState(startTaskId) {
    const _this = this;
    let workflowViewer = _this.workflowViewer || _this.$widget.$refs.workflowViewer;
    if (workflowViewer) {
      workflowViewer.initStartState(startTaskId || undefined);
      _this.workflowViewer = workflowViewer;
    }
  }

  /**
   * 仿真处理
   */
  doSimulation() {
    const _this = this;
    let simulationData = _this.collectData();
    _this.state.code = 'running';
    _this.state.tip = _this.getRunningStateTip(simulationData);
    if (_this.showLoading) {
      _this.$widget.loading = {
        spinning: true,
        tip: _this.showLoadingFlowName ? (_this.flowDefinition.name + ': ' + _this.state.tip) : _this.state.tip
      };
    }
    $axios
      .post('/proxy/api/workflow/simulation/simulationSubmit', simulationData)
      .then(({ data: result }) => {
        if (result.success) {
          _this.startNextStepSimulationIfRequire(result);
        } else {
          _this.handlerError(result);
        }
      })
      .catch(({ response }) => {
        _this.handlerError(response.data);
      });
  }

  getRunningStateTip(simulationData) {
    const _this = this;
    let tip = '正在仿真...';
    // 存在未完成子流程时取一个子流程信息
    let taskInfo = simulationData.subTaskInfo || simulationData;
    let taskName = taskInfo.taskName;
    let todoUserName = taskInfo.todoUserName;
    let superviseUserName = taskInfo.superviseUserName;
    let flowDefName = taskInfo.flowDefName || '';
    if (!isEmpty(taskName) && !isEmpty(todoUserName)) {
      tip = `正在仿真 ${flowDefName ? (flowDefName + ': ') : ''}${todoUserName} 提交环节 ${taskName}...`;
    } else if (!isEmpty(taskName) && !isEmpty(superviseUserName)) {
      tip = `正在仿真 ${flowDefName ? (flowDefName + ': ') : ''}${superviseUserName} 完成环节 ${taskName}...`;
    } if (_this.isDispatching()) {
      tip = `正在仿真 子流程分发中(${taskInfo.dispatchingCount}/${taskInfo.totalCount})...`;
    } else if (isEmpty(taskInfo.flowInstUuid)) {
      let startUserName = _this.params.startUserName;
      if (startUserName) {
        let startUserIndex = _this.startUserIndex;
        let startUserNames = startUserName.split(";");
        if (startUserNames[startUserIndex]) {
          tip = `正在仿真 ${startUserNames[startUserIndex]} 发起流程...`;
        }
      }
    }
    return tip;
  }

  /**
   *
   * @returns
   */
  isDispatching() {
    let subTaskInfo = this.simulationData && this.simulationData.subTaskInfo;
    if (!subTaskInfo || !subTaskInfo.totalCount) {
      return false;
    }
    return subTaskInfo.dispatchingCount != subTaskInfo.totalCount;
  }

  /**
   * 收集仿真数据
   */
  collectData() {
    const _this = this;
    let simulationData = _this.simulationData;
    _this.workFlow._tempData2WorkData();
    let interactionTaskData = _this.workFlow.getWorkData();
    simulationData = Object.assign({ flowDefUuid: _this.flowDefinition.uuid, flowDefId: _this.flowDefinition.id }, simulationData, interactionTaskData);
    simulationData.simulationParams = _this.params;
    simulationData.startUserIndex = _this.startUserIndex;
    simulationData.currentRunNum = _this.currentRunNum;
    simulationData.userInteractedTasks = _this.userInteractedTasks;
    _this.simulationData = simulationData;
    return simulationData;
  }

  /**
   * 启动下一步仿真
   */
  startNextStepSimulationIfRequire(result) {
    let _this = this;
    if (!_this.created) {
      _this.emitEvent('simulationStart');
      _this.created = true;
    }
    let data = result.data;
    _this.workFlow.clearTempData();
    _this.getSimulationData(_this.simulationData.flowInstUuid || data.flowInstUuid).then((simulationData) => {
      // 仿真数据
      _this.simulationData = simulationData;
      // 进入下一步仿真
      if (_this.simulationData.isOver !== true) {
        let params = _this.params;
        let pauseTasks = params.pauseTasks || [];
        // 暂停
        if (_this.simulationData.state == 'pause' || _this.state.code == 'pause' || pauseTasks.includes(_this.simulationData.taskId)) {
          _this.state.code = 'pause';
          _this.state.tip = `已暂停(${_this.simulationData.taskName})`;
          _this.updateRecordStateByUuid(_this.simulationData.recordUuid, 'pause').then(() => {
            _this.emitEvent('simulationPause');
          });
        } else if (_this.isDispatching()) {
          _this.state.code = 'running';
          _this.state.tip = _this.getRunningStateTip(_this.simulationData);
          setTimeout(() => {
            _this.startNextStepSimulationIfRequire(result);
          }, 2000);
        } else {
          _this.doSimulation();
        }
        if (_this.showLoading) {
          _this.$widget.loading = {
            spinning: true,
            tip: _this.showLoadingFlowName ? (_this.flowDefinition.name + ': ' + _this.state.tip) : _this.state.tip
          };
          if (_this.state.code == 'pause' && !_this.pauseHideLoading) {
            _this.pauseHideLoading = true;
            setTimeout(() => {
              _this.$widget.loading = false;
              _this.onPause && _this.onPause.call(_this, _this.workUrls);
            }, 2000);
          }
        }
      } else {
        _this.onSimulationSuccess();
      }
    });
  }

  getSimulationData(flowInstUuid) {
    return $axios.get(`/api/workflow/simulation/getSimulationData?flowInstUuid=${flowInstUuid}`).then(({ data: result }) => {
      // 仿真数据
      return result.data || {};
    });
  }

  handlerError(result, callback) {
    const _this = this;
    _this.$widget.loading = false;

    let options = {};
    options.callback = callback || _this.doSimulation;
    options.callbackContext = _this;
    options.cancel = () => {
      if (_this.simulationData.recordUuid) {
        _this.updateRecordStateByUuid(_this.simulationData.recordUuid, 'pause').then(() => {
          _this.state.code = 'pause';
          _this.workFlow.clearTempData();
          _this.emitEvent('simulationPause');
        });
      } else {
        _this.state.code = 'inited';
        _this.initWorkflowReadyState();
      }
    }
    options.workFlow = _this.workFlow;
    if (!_this.formDataInteractionIfRequired(result)) {
      _this.errorHandler.handle(result, null, null, options);

      // 异常
      let faultData = result.data || result;
      if (!faultData.errorCode || faultData.errorCode == 'WorkFlowException') {
        _this.state.code = 'error';
        _this.emitEvent('simulationFailure');
        if (_this.simulationData.recordUuid) {
          _this.updateRecordStateByUuid(_this.simulationData.recordUuid, 'error').then(() => {
          });
        }
      }
    }
  }


  /**
   * 表单数据交互
   *
   * @returns
   */
  formDataInteractionIfRequired(result) {
    const _this = this;
    let faultData = {};
    // api接口返回的错误信息
    if (!result.success && result.data) {
      faultData = result.data;
    }
    if (faultData.errorCode != 'WorkFlowException' || !faultData.data || faultData.data.simulation != true) {
      return false;
    }

    import('@dyform/app/web/framework/vue/install').then(m => {
      _this.showEditSimulationFormDataModal(faultData);
    });
    return true;
  }

  showEditSimulationFormDataModal(interactionData) {
    const _this = this;
    let simulationData = _this.simulationData;
    let Modal = Vue.extend({
      template: `<a-config-provider :locale="locale">
        <a-modal dialogClass="pt-modal" :title="title" :visible="visible" width="900px" :maskClosable="false" okText="确定" cancelText="取消" @ok="handleOk" @cancel="handleCancel">
          <Scroll style="height: 500px">
            <WidgetDyform ref="dyform" :displayState="displayState" :formUuid="formUuid" :dataUuid="dataUuid"
              :isNewFormData="isNewFormData" :formDatas="formDatas" :formElementRules="formElementRules" @mounted="onDyformMounted" />
          </Scroll>
        </a-modal>
      </a-config-provider>`,
      data: function () {
        return {
          title: `编辑仿真数据(${interactionData.data.taskName})`,
          visible: true,
          displayState: 'edit',
          formUuid: interactionData.data.formUuid,
          dataUuid: interactionData.data.dataUuid,
          isNewFormData: isEmpty(interactionData.data.dataUuid),
          formDatas: interactionData.data.formDatas,
          workData: {
            taskForm: interactionData.data.taskForm,
          },
          locale: _this.$widget.locale,
          validate: interactionData.data.taskForm != null
        };
      },
      provide() {
        return {
          pageContext: _this.$widget.pageContext,
          namespace: _this.$widget.namespace,
          vPageState: _this.$widget.vPageState,
          $pageJsInstance: _this.$widget.$pageJsInstance,
          designMode: false,
          locale: _this.$widget.locale,
        };
      },
      computed: {
        formElementRules() {
          if (!this.workData.taskForm) {
            return undefined;
          }
          let formDefinition = JSON.parse(interactionData.data.formDefinition);
          this.tabs = formDefinition.tabs;
          return formElementRules.call(this);
        }
      },
      created() {
        if (Vue.prototype.$store == null) {
          Vue.prototype.$store = _this.$widget.$store;
        }
      },
      methods: {
        onDyformMounted() {
          this.$refs.dyform.dyform.vueInstance = _this.$widget.pageContext.vueInstance;
        },
        handleCancel() {
          if (isEmpty(simulationData.flowInstUuid)) {
            _this.state.code = 'inited';
            _this.initWorkflowReadyState();
          } else if (_this.simulationData.recordUuid) {
            _this.updateRecordStateByUuid(_this.simulationData.recordUuid, 'pause').then(() => {
              _this.state.code = 'pause';
              _this.emitEvent('simulationPause');
            });
          }

          this.visible = false;
          this.$destroy();
        },
        handleOk() {
          let doSaveFormData = () => {
            let formData = this.$refs.dyform.collectFormData();
            $axios.post('/proxy/api/workflow/simulation/saveFormData', formData.dyFormData).then(({ data: result }) => {
              // 记录已交互的环节
              if (result.success) {
                _this.userInteractedTasks.push(interactionData.data.dataInteractionTaskId);
                simulationData.formUuid = formData.formUuid;
                simulationData.dataUuid = result.data;
                _this.doSimulation();
              } else {
                _this.$widget.$message.error(`仿真失败——${result.msg}`);
              }
              this.visible = false;
              this.$destroy();
            }).catch(({ response }) => {
              let faultData = response && response.data && response.data.data;
              if (faultData && faultData.errorCode == 'SaveData') {
                let _thisModal = this;
                _thisModal.$refs.dyform.handleException(faultData.data, {
                  callback: _thisModal.handleOk,
                });
              } else {
                _this.$widget.$message.error((response && response.data && response.data.msg) || '服务异常！');
              }
            });
          };

          if (this.validate) {
            this.$refs.dyform.validateFormData((validate) => {
              if (!validate) {
                return;
              }
              doSaveFormData();
            });
          } else {
            doSaveFormData();
          }
        }
      }
    });
    let modal = new Modal({
      i18n: _this.$widget.$i18n
    });
    modal.$mount();
  }

  /**
   * 仿真成功处理
   */
  onSimulationSuccess() {
    const _this = this;
    _this.updateRecordStateByUuid(_this.simulationData.recordUuid, 'success').then(() => {
      _this.emitEvent('simulationSuccess')
    });

    _this.workFlow.clearTempData();
    _this.$widget.loading = false;
    let params = _this.params;
    let workUrl = '/workflow/work/view/work?flowInstUuid=' + _this.simulationData.flowInstUuid;
    let workUrls = _this.workUrls || [];
    workUrls.push(workUrl);
    _this.workUrls = workUrls;
    let startUserId = params.startUserId || '';
    let startUserIds = startUserId.split(";");
    if (params.runNum && params.runNum > _this.currentRunNum) {
      _this.currentRunNum++;
      // _this.simulationData = {};
      // _this.initWorkflowStartState(params.startTaskId);
      _this.reset();
      _this.doSimulation();
    } else if (startUserIds.length > (_this.startUserIndex + 1)) {
      _this.startUserIndex++;
      _this.currentRunNum = 1;
      // _this.simulationData = {};
      // _this.initWorkflowStartState(params.startTaskId);
      _this.reset();
      _this.doSimulation();
    } else {
      // if (params && params.autoViewWorkAfterSimulation) {
      //   workUrls.forEach(url => {
      //     window.open(_this.addSystemPrefix(url));
      //   });
      // } else {
      //   _this.$widget.$confirm({
      //     title: '确认框',
      //     content: '流程仿真成功，是否查看流程数据！',
      //     okText: '确定',
      //     cancelText: '取消',
      //     onOk() {
      //       workUrls.forEach(url => {
      //         window.open(_this.addSystemPrefix(url));
      //       });
      //     }
      //   });
      // }
      _this.state.code = 'success';
      _this.onSuccess && _this.onSuccess.call(_this, _this.workUrls);
    }
  }

  /**
   *
   * @param {*} eventName
   */
  emitEvent(eventName) {
    let pageContext = this.$widget.pageContext || $app.pageContext;
    pageContext.emitEvent(eventName, this.simulationData);
  }

  /**
   * @param {*} url
   * @returns
   */
  addSystemPrefix(url) {
    return addSystemPrefix(url, this.$widget);
  }

}

export default WorkflowSimulation;
