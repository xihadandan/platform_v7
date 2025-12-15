<template>
  <a-popover :mouseEnterDelay="mouseEnterDelay" :trigger="trigger" :placement="placement" @mouseenter="mouseenter">
    <template slot="content">
      <a-spin v-if="loading">
        <a-icon slot="indicator" type="loading" style="font-size: 14px" spin />
      </a-spin>
      <div v-else-if="actionTip" class="action-tip">
        <template v-if="actionTip.list">
          {{ $t('WorkflowWork.actionPopover.submitTo', '提交至') }}
          <div v-for="(item, index) in actionTip.list" :key="index" class="action-info">
            {{ $t('WorkflowView.' + item.toTaskId + '.taskName', item.toTaskName) }}
            <span v-if="item.todoUserNames && item.todoUserNames.length > 0">({{ item.todoUserNames.join('，') }})</span>
          </div>
        </template>
        <template v-else-if="actionTip.toTaskName">
          <ActionInfoDisplay :title="getToTaskName(actionTip)">
            <div class="action-info">
              {{ getToTaskName(actionTip) }}
              <span v-if="actionTip.todoUserNames && actionTip.todoUserNames.length > 0">({{ actionTip.todoUserNames.join('，') }})</span>
            </div>
          </ActionInfoDisplay>
          <template v-if="actionTip.remark">
            <a-divider class="divider"></a-divider>
            <span class="action-remark">{{ actionTip.remark }}</span>
          </template>
        </template>
        <template v-else>
          <template v-if="actionTip.actionType == 'Submit'">
            <div class="action-info">{{ $t('WorkflowWork.actionPopover.submitToNextTask', '提交时选择下一环节') }}</div>
            <a-divider v-if="actionTip.remark" class="divider"></a-divider>
          </template>
          <template v-if="actionTip.remark">
            <span class="action-remark">
              {{ getRemark(actionTip) }}
            </span>
          </template>
        </template>
      </div>
    </template>
    <slot />
  </a-popover>
</template>

<script>
import { trim } from 'lodash';
let ActionInfoDisplay = EASY_ENV_IS_BROWSER
  ? Vue.extend({
      template: `<div>
        <div class="init-content" style="display:none"><slot/></div>
        <div :style="firstRowStyle">{{firstRowText}}<span class='row-end'></span></div>
        <div :style="secondStyle">{{secondRowText}}</div>
      </div>`,
      data() {
        return {
          splitRowThreshold: 230,
          maxCharWidth: 0,
          firstRowText: '',
          secondRowText: '',
          firstRowStyle: {
            overflow: 'hidden',
            maxWidth: '250px',
            position: 'relative'
          },
          secondStyle: {
            wordBreak: 'break-all',
            overflow: 'hidden',
            textOverflow: 'ellipsis',
            whiteSpace: 'nowrap'
          }
        };
      },
      mounted() {
        const _this = this;
        let text = trim(_this.$el.querySelector('.init-content').innerText);
        _this.adjustDisplayText(text, 10);
      },
      methods: {
        adjustDisplayText(text, position) {
          const _this = this;
          if (!text) {
            return;
          }
          if (text.length < 10 || text.length < position) {
            _this.firstRowText = text;
            return;
          }

          let fistText = text.substring(0, position);
          let secondText = text.substring(position);
          _this.firstRowText = fistText;
          _this.secondRowText = secondText;
          _this.$nextTick(() => {
            let offsetLeft = _this.$el.querySelector('.row-end').offsetLeft;
            if (_this.latestOffsetLeft) {
              let charWidth = offsetLeft - _this.latestOffsetLeft;
              if (charWidth > _this.maxCharWidth) {
                _this.maxCharWidth = charWidth;
              }
            }
            _this.latestOffsetLeft = offsetLeft;
            if (offsetLeft < _this.splitRowThreshold && offsetLeft + _this.maxCharWidth < 250) {
              _this.adjustDisplayText(text, position + 1);
            }
          });
        }
      }
    })
  : undefined;
export default {
  name: 'WorkflowActionPopover',
  inject: ['$workView'],
  components: {
    ActionInfoDisplay
  },
  props: {
    workFlow: Object,
    action: {
      type: Object,
      default() {
        return { id: '', code: '', toTaskId: '', remark: '' };
      }
    },
    showActionTip: {
      type: Boolean,
      default: false
    },
    placement: {
      type: String,
      default: 'bottom'
    },
    rollbackTaskInstUuid: String
  },
  data() {
    this.supports = this.isSupportsActionTip();
    return {
      title: undefined,
      actionTip: undefined,
      loading: false,
      mouseEnterDelay: 0.5,
      trigger: this.supports ? 'hover' : 'none',
      visible: false,
      $developJsInstance: null
    };
  },
  created() {
    if (
      this.action &&
      this.action.eventHandler &&
      this.action.eventHandler.actionType === 'customJsModule' &&
      this.action.eventHandler.customJsModule
    ) {
      this.initCustomJsModule(this.action.eventHandler.customJsModule);
      this.invokeDevelopmentMethod('created', this);
    }
  },
  beforeMount() {
    this.invokeDevelopmentMethod('beforeMount', this);
  },
  mounted() {
    this.invokeDevelopmentMethod('mounted', this);
  },
  beforeUpdate() {
    this.invokeDevelopmentMethod('beforeUpdate', this);
  },
  updated() {
    this.invokeDevelopmentMethod('updated', this);
  },
  beforeDestroy() {
    this.invokeDevelopmentMethod('beforeDestroy', this);
  },
  destroyed() {
    this.invokeDevelopmentMethod('destroyed', this);
  },
  methods: {
    initCustomJsModule(customJsModule) {
      if (!this.$developJsInstance) {
        this.$developJsInstance = [];
      }
      this.$developJsInstance.push(new this.__developScript[customJsModule].default(this.$workView));
    },
    // 调用二开方法
    invokeDevelopmentMethod() {
      if (!this.$developJsInstance) {
        return;
      }
      this.$developJsInstance.forEach(developJsInstance => {
        let method = arguments[0];
        let args = [];
        if (arguments.length > 1) {
          for (let i = 1, len = arguments.length; i < len; i++) {
            args.push(arguments[i]);
          }
        }
        if (typeof developJsInstance[method] === 'function') {
          developJsInstance[method].apply(developJsInstance, args);
        }
      });
    },
    getToTaskName(actionTip) {
      let workView;
      if (this.$workView && this.$workView.workView) {
        workView = this.$workView.workView;
      }
      let toTaskName = actionTip.toTaskName;
      if (actionTip.toTaskName === '当前环节') {
        toTaskName = this.$t('WorkflowWork.currentTask', actionTip.toTaskName);
      } else if (actionTip.toTaskId) {
        toTaskName = this.$t('WorkflowView.' + actionTip.toTaskId + '.taskName', actionTip.toTaskName);
      } else if (actionTip.actionType === 'Rollback') {
        // 退回
        if (workView && workView.taskProcess && workView.taskProcess.previous) {
          toTaskName = this.$t('WorkflowView.' + workView.taskProcess.previous.taskId + '.taskName', actionTip.toTaskName);
        }
      }
      return (
        this.$t('WorkflowWork.operation.' + actionTip.actionType, actionTip.actionName) +
        ' ' +
        this.$t('WorkflowWork.actionPopover.to', '至') +
        ' ' +
        toTaskName
      );
    },
    getRemark(actionTip) {
      let remark = actionTip.remark;
      if (remark === '提交结束流程') {
        remark = this.$t('WorkflowWork.submitToEnd', actionTip.remark);
      } else if (remark === '选择退回环节') {
        remark = this.$t('WorkflowWork.selectReturnProcess', actionTip.remark);
      }
      return remark;
    },
    isSupportsActionTip() {
      const _this = this;
      let supports = false;
      if (!_this.showActionTip) {
        supports = false;
        return supports;
      }
      let actionCode = _this.action.code;
      // 提交、退回按钮
      if (
        _this.workFlow.actionCode.submit == actionCode ||
        _this.workFlow.actionCode.rollback == actionCode ||
        _this.workFlow.actionCode.directRollback == actionCode
      ) {
        supports = true;
      }
      return supports;
    },
    mouseenter() {
      const _this = this;
      if (!this.supports || _this.actionTip || _this.loading) {
        return;
      }
      _this.loading = true;
      if (_this.rollbackTaskInstUuid) {
        _this
          .loadTaskOwnerNames(_this.rollbackTaskInstUuid)
          .then(({ data: result }) => {
            if (result.data) {
              _this.actionTip = { remark: _this.$t('WorkflowWork.transactor', '办理人') + '：' + result.data.join('，') };
            }
          })
          .finally(() => {
            _this.loading = false;
          });
      } else {
        _this
          .loadActionTip(_this.action.id, _this.action.code, _this.action.toTaskId)
          .then(({ data: result }) => {
            if (result.data) {
              _this.actionTip = result.data;
              _this.actionTip.toTaskId = _this.action.toTaskId || result.data.taskId;
              if (!_this.actionTip.remark) {
                _this.actionTip.remark = _this.action.remark;
              }
            }
          })
          .finally(() => {
            _this.loading = false;
          });
      }
    },
    loadTaskOwnerNames(taskInstUuid) {
      return $axios.get(`/proxy/api/workflow/work/getTaskOwnerNames/${taskInstUuid}`);
    },
    loadActionTip(actionId, actionCode, toTaskId = '') {
      const _self = this;
      const workData = _self.workFlow.getWorkData();
      return $axios({
        method: 'get',
        url: `/proxy/api/workflow/work/getActionTipWithActionId/${actionId}`,
        params: {
          actionCode: actionCode,
          taskInstUuid: workData.taskInstUuid || '',
          taskId: workData.taskId || '',
          toTaskId: encodeURIComponent(toTaskId || ''),
          flowInstUuid: workData.flowInstUuid || '',
          flowDefUuid: workData.flowDefUuid || '',
          taskIdentityUuid: workData.taskIdentityUuid || ''
        },
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
      });
    }
  }
};
</script>

<style lang="less" scoped>
.action-tip {
  max-width: 250px;
  .action-info {
    word-break: break-all;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .divider {
    margin: 6px 0;
  }
}
</style>
