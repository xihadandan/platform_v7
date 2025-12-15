<template>
  <a-row class="task-process">
    <a-col v-if="previous" span="7" class="previous">
      <a-tooltip placement="bottom" :align="{ offset: [0, -8] }" overlayClassName="task-process-tooltip">
        <template slot="title">
          <div class="ant-tooltip-inner__header">{{ $t('WorkflowWork.transactor', '办理人') }}</div>
          <div class="ant-tooltip-inner__content">{{ previous.assignee }}</div>
        </template>
        <span class="task-name">
          {{
            previous.supplemented
              ? $t('WorkflowView.' + previous.taskId + '.taskName', previous.taskName) +
                ' (' +
                $t('WorkflowWork.extraApproveExtraTask', '补审补办') +
                ')'
              : previous.taskId
              ? $t('WorkflowView.' + previous.taskId + '.taskName', previous.taskName)
              : previous.taskName
          }}
        </span>
      </a-tooltip>
    </a-col>
    <a-col v-if="current" span="8" class="current">
      <a-tooltip v-if="current.todoUserNames" placement="bottom" :align="{ offset: [0, -8] }" overlayClassName="task-process-tooltip">
        <template slot="title">
          <div class="ant-tooltip-inner__header">{{ $t('WorkflowWork.pendingTransactor', '待办办理人') }}</div>
          <div class="ant-tooltip-inner__content">{{ getTodoUserNames(current) }}</div>
        </template>
        <span class="task-name">
          {{
            current.supplemented
              ? $t('WorkflowView.' + current.taskId + '.taskName', current.taskName) +
                ' (' +
                $t('WorkflowWork.extraApproveExtraTask', '补审补办') +
                ')'
              : current.taskId
              ? $t('WorkflowView.' + current.taskId + '.taskName', current.taskName)
              : current.taskName == '结束'
              ? $t('WorkflowWork.endTaskName', current.taskName)
              : current.taskName
          }}
        </span>
      </a-tooltip>
      <span v-else class="task-name">
        {{
          current.supplemented
            ? $t('WorkflowView.supplementTaskName', current.taskName)
            : current.taskId
            ? $t('WorkflowView.' + current.taskId + '.taskName', current.taskName)
            : current.taskName == '结束'
            ? $t('WorkflowWork.endTaskName', current.taskName)
            : current.taskName
        }}
      </span>
    </a-col>
    <a-col v-if="next" span="7">
      <a-tooltip placement="bottom" :align="{ offset: [0, -8] }" overlayClassName="task-process-tooltip">
        <template slot="title">
          <div class="ant-tooltip-inner__header">{{ $t('WorkflowWork.transactor', '办理人') }}</div>
          <div class="ant-tooltip-inner__content">{{ next.assignee }}</div>
        </template>
        <span class="task-name">
          {{
            next.taskId
              ? $t('WorkflowView.' + next.taskId + '.taskName', next.taskName)
              : next.taskName == '结束'
              ? $t('WorkflowWork.endTaskName', next.taskName)
              : next.taskName
          }}
        </span>
      </a-tooltip>
    </a-col>
  </a-row>
</template>
<script type="text/babel">
import { addDbHeader } from '@framework/vue/utils/function';
export default {
  name: 'WorkflowTaskProcess',
  props: {
    workView: Object,
    timeout: {
      type: Number,
      default: 0
    }
  },
  data() {
    return {
      previous: null,
      current: null,
      next: null
    };
  },
  created() {},
  methods: {
    getTodoUserNames(current) {
      return current.todoUserNames
        .map(item => {
          return this.workView.getMsgI18ns(null, item, 'WorkflowWork.opinionManager');
        })
        .join(';');
    },
    loadTaskProcess: function (callback) {
      const _self = this;
      let taskProcess = _self.workView.getTaskProcess();
      if (taskProcess) {
        callback.call(_self, taskProcess);
        return;
      }
      var workData = _self.workView.getWorkData();
      var taskInstUuid = workData.taskInstUuid;
      var flowDef = workData.flowDefId;
      let resultCallback = result => {
        let data = (result.data && result.data.data) || {};
        _self.workView.setTaskProcess(data);
        callback.call(_self, data);
        _self.previous = data.previous;
        _self.current = data.current;
        _self.next = data.next;
      };
      if (_self.workView.loadTaskProcessPromise) {
        _self.workView.loadTaskProcessPromise.then(result => {
          resultCallback(result);
        });
      } else {
        var config = {
          params: { taskInstUuid: taskInstUuid, flowDefId: flowDef }
        };
        config = addDbHeader(config);
        _self.workView.loadTaskProcessPromise = $axios.get(`/api/workflow/work/getTaskProcess`, config);
        _self.workView.loadTaskProcessPromise.then(result => {
          resultCallback(result);
        });
      }
    }
  },
  beforeMount() {
    const _self = this;
    var callback = function (taskProcess) {
      _self.previous = taskProcess.previous;
      _self.current = taskProcess.current;
      _self.next = taskProcess.next;
    };
    if (_self.timeout > 0) {
      setTimeout(() => {
        _self.loadTaskProcess(data => {
          callback(data);
        });
      }, _self.timeout);
    } else {
      _self.loadTaskProcess(data => {
        callback(data);
      });
    }
  }
};
</script>
<style scoped>
.task-process {
  padding: 7px 0 8px;
  border-bottom: 1px var(--w-border-color-light) solid;
}
.task-process .task-name {
  display: block;
  text-align: center;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.task-process >>> .current.ant-col {
  font-weight: bold;
  color: #ffffff;
  background: var(--w-primary-color);
}
.task-process >>> .previous.ant-col {
  background: var(--w-primary-color-2);
}
.task-process >>> .ant-col {
  height: 34px;
  display: block;
  margin-left: 5px;
  line-height: 34px;
  background: var(--w-fill-color-light);
  color: var(--w-text-color-dark);
  font-size: var(--w-font-size-base);
  padding-left: 10px;
}
.task-process >>> .ant-col::before {
  position: absolute;
  top: 0;
  right: -14px;
  content: '';
  border-top: 17px solid transparent;
  border-bottom: 17px solid transparent;
  border-left: 10px solid #ffffff;
  z-index: 3;
}
.task-process >>> .ant-col::after {
  position: absolute;
  top: 0;
  right: -10px;
  content: '';
  border-top: 17px solid transparent;
  border-bottom: 17px solid transparent;
  border-left: 10px solid var(--w-fill-color-light);
  z-index: 5;
}
.task-process >>> .current.ant-col::after {
  border-left-color: var(--w-primary-color);
}
.task-process >>> .previous.ant-col::after {
  border-left-color: var(--w-primary-color-2);
}
</style>
<style lang="less">
.task-process-tooltip {
  .ant-tooltip-arrow:before {
    background-color: #ffffff;
  }
  .ant-tooltip-inner {
    background-color: #ffffff;
    color: var(--w-text-color-dark);
    min-width: 112px;
    padding: 8px 12px;

    .ant-tooltip-inner__header {
      color: var(--w-text-color-light);
      padding-bottom: 4px;
      border-bottom: 1px solid var(--w-border-color-light);
    }
    .ant-tooltip-inner__content {
      padding-top: 6px;
      font-size: var(--w-font-size-sm);
    }
  }
}
</style>
