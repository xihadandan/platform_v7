import WorkFlowErrorHandler from '@workflow/app/web/page/workflow-work/component/WorkFlowErrorHandler.js';
import WorkFlowInteraction from '@workflow/app/web/page/workflow-work/component/WorkFlowInteraction.js';
import { isEmpty } from 'lodash';

class WorkflowSimulation {
  constructor({ flowDefId, $widget }) {
    this.flowDefId = flowDefId;
    this.$widget = $widget;
    this.simulationParams = {};
    this.simulationData = {
      flowDefId
    };
    this.errorHandler = new WorkFlowErrorHandler(this);
    this.workFlow = new WorkFlowInteraction();
  }

  /**
   * 启动仿真
   */
  start() {
    const _this = this;
    let Modal = Vue.extend({
      template: `<a-modal dialogClass="pt-modal" :title="title" :visible="visible" width="800px" :maskClosable="false" @ok="handleOk" @cancel="handleCancel">
        <WorkflowSimulation ref="form" :flowDefId="flowDefId"></WorkflowSimulation>
        <template slot="footer">
          <a-button type="primary" @click="handleOk">开始仿真</a-button>
        </template>
      </a-modal>`,
      components: { WorkflowSimulation: () => import('./template/workflow-simulation.vue') },
      data: function () {
        return { title: '设置仿真参数', flowDefId: _this.flowDefId, visible: true };
      },
      created() { },
      methods: {
        handleCancel() {
          this.visible = false;
          this.$destroy();
        },
        handleOk() {
          this.$refs.form
            .collect()
            .then(formData => {
              // 设置仿真参数
              _this.simulationParams = Object.assign({ userInteractedTasks: [] }, formData);
              _this.simulationData.simulationParams = _this.simulationParams;
              _this.doSimulation();
              this.visible = false;
              this.$destroy();
            })
            .catch(() => {
              _this.$widget.$message.error('仿真失败！');
            });
        }
      }
    });
    let modal = new Modal();
    modal.$mount();
  }

  /**
   * 仿真处理
   *
   * @param {*}
   */
  doSimulation() {
    const _this = this;
    let simulationData = _this.simulationData;
    _this.workFlow._tempData2WorkData();
    let interactionTaskData = _this.workFlow.getWorkData();
    simulationData = Object.assign({}, simulationData, interactionTaskData);
    let taskName = simulationData.taskName;
    let todoUserName = simulationData.todoUserName;
    let superviseUserName = simulationData.superviseUserName;
    let tip = '正在仿真...';
    if (!isEmpty(taskName) && !isEmpty(todoUserName)) {
      tip = '正在仿真 ' + todoUserName + ' 提交环节 ' + taskName + '...';
    } else if (!isEmpty(taskName) && !isEmpty(superviseUserName)) {
      tip = '正在仿真 ' + superviseUserName + ' 完成环节 ' + taskName + '...';
    }
    _this.$widget.loading = {
      spinning: true,
      tip
    };
    $axios
      .post('/api/workflow/simulation/simulationSubmit', simulationData)
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

  handlerError(result) {
    const _this = this;
    _this.$widget.loading = false;

    let options = {};
    options.callback = _this.doSimulation;
    options.callbackContext = _this;
    options.workFlow = _this.workFlow;
    if (!_this.formDataInteractionIfRequired(result)) {
      _this.errorHandler.handle(result, null, null, options);
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
    // appContext.require(["DmsDataServices"], function (DmsDataServices) {
    //   // 记录当前仿真信息
    //   window.workFlowSimulationContext = {
    //     interaction: faultData.data,
    //     context: _self
    //   };
    //   var simulationData = _self.simulationData;
    //   new DmsDataServices().openDialog({
    //     urlParams: {
    //       bug: "bug",
    //       ac_id: "btn_list_view_edit",
    //       dms_id: "wDataManagementViewer_C93D4DB6A59000013726CF00E3E01085",
    //       target: "_dialog",
    //       idKey: "uuid",
    //       idValue: faultData.data.dataUuid,
    //       formUuid: faultData.data.formUuid,
    //       dataUuid: faultData.data.dataUuid,
    //       ep_ac_get: "btn_workflow_simulation_dyform_get",
    //       ep_view_mode: faultData.data.canEditForm,
    //       ep_flowDefId: simulationData.flowDefId,
    //       ep_flowInstUuid: simulationData.flowInstUuid || "",
    //       ep_dataInteractionTaskId: faultData.data.dataInteractionTaskId
    //     },
    //     ui: _this
    //   });
    // });
    return true;
  }

  showEditSimulationFormDataModal(interactionData) {
    const _this = this;
    let simulationData = _this.simulationData;
    let Modal = Vue.extend({
      template: `<a-config-provider :locale="locale">
        <a-modal dialogClass="pt-modal" :title="title" :visible="visible" width="800px" :maskClosable="false" @ok="handleOk" @cancel="handleCancel">
          <Scroll style="height: 500px">
            <WidgetDyform ref="dyform" :displayState="dyformDisplayState" :formUuid="formUuid" :dataUuid="dataUuid" />
          </Scroll>
        </a-modal>
      </a-config-provider>`,
      data: function () {
        return {
          title: '编辑仿真数据',
          visible: true,
          dyformDisplayState: 'edit',
          formUuid: interactionData.data.formUuid,
          dataUuid: interactionData.data.dataUuid,
          locale: _this.$widget.locale
        };
      },
      provide() {
        return {
          pageContext: _this.$widget.pageContext,
          namespace: _this.$widget.namespace,
          vPageState: _this.$widget.vPageState,
          $pageJsInstance: _this.$widget.$pageJsInstance,
          designMode: false
        };
      },
      created() {
        if (Vue.prototype.$store == null) {
          Vue.prototype.$store = _this.$widget.$store;
        }
      },
      methods: {
        handleCancel() {
          this.visible = false;
          this.$destroy();
        },
        handleOk() {
          let formData = this.$refs.dyform.collectFormData();
          $axios.post('/proxy/api/dyform/data/saveFormData', formData.dyFormData).then(({ data: result }) => {
            // 记录已交互的环节
            if (result.success) {
              let userInteractedTasks = simulationData.simulationParams.userInteractedTasks || [];
              userInteractedTasks.push(interactionData.data.dataInteractionTaskId);
              simulationData.simulationParams.userInteractedTasks = userInteractedTasks;
              simulationData.formUuid = formData.formUuid;
              simulationData.dataUuid = result.data;
              _this.doSimulation();
            } else {
              _this.$widget.$message.error(`仿真失败——${result.msg}`);
            }
            this.visible = false;
            this.$destroy();
          });
        }
      }
    });
    let modal = new Modal();
    modal.$mount();
  }

  /**
   * 启动下一步仿真
   */
  startNextStepSimulationIfRequire(result) {
    let _this = this;
    let data = result.data;
    _this.workFlow.clearTempData();
    $axios.get(`/api/workflow/simulation/getSimulationData?flowInstUuid=${data.flowInstUuid}`).then(({ data: result }) => {
      // 仿真参数
      _this.simulationData = result.data;
      _this.simulationData.simulationParams = _this.simulationParams;
      // 进入下一步仿真
      if (_this.simulationData.isOver !== true) {
        _this.doSimulation();
      } else {
        _this.onSimulationSuccess();
      }
    });
  }

  /**
   * 仿真成功处理
   */
  onSimulationSuccess() {
    const _this = this;
    _this.$widget.loading = false;
    let simulationParams = _this.simulationParams;
    let workUrl = '/workflow/work/view/work?flowInstUuid=' + _this.simulationData.flowInstUuid;
    if (simulationParams && simulationParams.autoViewWorkAfterSimulation) {
      window.open(workUrl);
    } else {
      _this.$widget.$confirm({
        title: '确认框',
        content: '流程仿真成功，是否查看流程数据！',
        okText: '确定',
        cancelText: '取消',
        onOk() {
          window.open(workUrl);
        }
      });
    }
  }
}

export default WorkflowSimulation;
