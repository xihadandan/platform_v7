import { isEmpty } from 'lodash';
class WorkflowStartNewWork {
  constructor($widget) {
    this.$widget = $widget;
  }

  /**
   * 发起工作
   *
   * @param {
   *  {String} title 发起工作弹窗标题
   *  {String} flowDefId 指定的流程定义ID
   *  {String} categoryCode 过滤的流程分类代码，多个以分号隔开
   *  {String, Boolean} showRecentUsed 显示最近使用
   *  {Boolean} autoRelateFlowBizDefId 自动关联流程业务定义ID
   *  {String} renderTo 渲染到的页面元素
   *  {Object} itemLayout 每行显示的布局
   *  {Function} onOk 选择流程后回调
   * } options
   */
  startNewWork(options) {
    let { flowDefId, autoRelateFlowBizDefId = true } = options;
    const _this = this;
    // 指定流程定义直接发起工作
    if (!isEmpty(flowDefId)) {
      _this.openNewWork(flowDefId, autoRelateFlowBizDefId);
    } else {
      // 打开发起工作选择
      _this.openChooseNewWork(options);
    }
  }

  /**
   * 打开发起工作选择弹出框
   *
   * @param {*} param0
   */
  openChooseNewWork({
    title = '发起工作',
    categoryCode,
    showRecentUsed = !categoryCode,
    autoRelateFlowBizDefId = true,
    renderTo,
    itemLayout,
    onOk
  }) {
    const _this = this;
    let locale = (_this.$widget && _this.$widget.locale) || {};
    let template = `<a-modal class="pt-modal" :title="title" :visible="true" width="900px" :maskClosable="false" :footer="null" @ok="handleOk" @cancel="handleCancel">
    <div style="height: 590px; overflow:auto">
      <WorkflowStartNewWork ref="startNewWork" :categoryCode="categoryCode" :showRecentUsed="showRecentUsed"
        :itemLayout="itemLayout" :bodyStyle="bodyStyle" :evtWidget="evtWidget" @select="onSelectFlow">
      </WorkflowStartNewWork>
    </div>
    </a-modal>`;
    let bodyStyle = {
      height: '510px'
    };
    if (renderTo) {
      template = `<WorkflowStartNewWork ref="startNewWork" :categoryCode="categoryCode" :showRecentUsed="showRecentUsed"
        :itemLayout="itemLayout" :bodyStyle="bodyStyle" :evtWidget="evtWidget" @select="onSelectFlow">
      </WorkflowStartNewWork>`;
      bodyStyle = {
        height: 'calc(100vh - 220px)'
      };
    }
    let Modal = Vue.extend({
      template,
      components: { WorkflowStartNewWork: () => import('./template/workflow-start-new-work.vue') },
      data: function () {
        return {
          title,
          categoryCode,
          showRecentUsed,
          itemLayout,
          bodyStyle,
          locale,
          evtWidget: _this.$widget
        };
      },
      methods: {
        handleCancel() {
          if (renderTo) {
            return;
          }
          this.visible = false;
          this.$destroy();
        },
        handleOk() {
          if (renderTo) {
            return;
          }
          this.visible = false;
          this.$destroy();
        },
        onSelectFlow(item) {
          let flowDefId = item.data;
          if (onOk) {
            onOk.call(_this, flowDefId);
          } else {
            _this.openNewWork(flowDefId, autoRelateFlowBizDefId);
          }
          // 记录最近使用
          _this.addRecentUsed(flowDefId).then(() => {
            this.handleOk();
          });
        }
      }
    });
    let modal = new Modal();
    modal.$mount(renderTo);
  }

  /**
   * 打开新建工作页面
   *
   * @param {*} flowDefId
   * @param {*} relateBizDef
   */
  openNewWork(flowDefId, relateBizDef) {
    let newWorkUrl = `/workflow/work/new/${flowDefId}?relateBizDef=${relateBizDef}`;
    if (this.$widget._$SYSTEM_ID) {
      newWorkUrl = `/sys/${this.$widget._$SYSTEM_ID}/_` + newWorkUrl;
    }
    window.open(newWorkUrl, '_blank');
  }

  /**
   * 记录最近使用
   *
   * @param {*} flowDefId
   */
  addRecentUsed(flowDefId) {
    return $axios.post('/json/data/services', {
      serviceName: 'recentUseFacadeService',
      methodName: 'use',
      args: JSON.stringify([flowDefId, 'WORKFLOW'])
    });
  }
}

export default WorkflowStartNewWork;
