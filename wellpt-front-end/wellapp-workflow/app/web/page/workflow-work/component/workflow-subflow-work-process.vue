<template>
  <div class="work-process" :style="workProcessStyle">
    <template v-for="(item, index) in workProcesses">
      <div v-if="index == 0 || !collapsed" class="work-process-item" :key="index">
        <a-row>
          <a-col
            span="5"
            class="flex"
            :title="$t('WorkflowView.subflow.' + item.flowDefId + '.' + item.taskId + '.taskName', item.taskName)"
          >
            <div>[</div>
            <div class="task-name">{{ $t('WorkflowView.subflow.' + item.flowDefId + '.' + item.taskId + '.taskName', item.taskName) }}</div>
            <div>]</div>
          </a-col>
          <a-col span="8" :title="item.assignee" class="task-assignee">{{ item.assignee }}</a-col>
          <a-col span="11" class="work-end-time" :title="item.endTime">{{ item.endTime }}</a-col>
        </a-row>
        <div class="work-process-opinion" v-show="!collapsed && item.opinion">{{ item.opinion }}</div>
      </div>
    </template>
    <a-button class="arrow-btn" v-show="collapsed" type="link" @click="onClickCollapse">
      <Icon type="pticon iconfont icon-ptkj-zhankai" />
      <span>{{ $t('orgSelect.expand', '展开') }}{{ $t('WorkflowWork.all', '全部') }}</span>
    </a-button>
    <a-button class="arrow-btn" v-show="!collapsed" type="link" @click="onClickCollapse">
      <Icon type="pticon iconfont icon-ptkj-zhedie" style="transform: rotate(-90deg)" />
      <span>{{ $t('orgSelect.collapse', '折叠') }}{{ $t('WorkflowWork.all', '全部') }}</span>
    </a-button>
  </div>
</template>

<script>
export default {
  name: 'WorkflowSubflowWorkProcess',
  props: {
    workProcesses: Array,
    record: Object,
    index: Number
  },
  inject: ['table'],
  data() {
    return { collapsed: true };
  },
  mounted() {
    this.$emit('mounted', { record: this.record, index: this.index, vcTable: this.table });
  },
  computed: {
    workProcessStyle() {
      if (this.collapsed) {
        return { height: '53px' };
      }
      return { height: '100%' };
    }
  },
  methods: {
    onClickCollapse() {
      this.collapsed = !this.collapsed;
      this.$nextTick(() => {
        this.table.syncFixedTableRowHeight();
      });
    }
  }
};
</script>

<style lang="less" scoped>
.work-process {
  overflow: hidden;
  position: relative;
  .work-process-item {
    margin-bottom: 7px;
  }
  .work-end-time {
    color: var(--w-gray-color-9);
  }
  .work-process-opinion {
    max-width: 400px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    padding: 6px 8px;
    font-size: 12px;
    line-height: 12px;
    border-radius: 4px;
    color: var(--w-gray-color-11);
    background-color: var(--w-gray-color-2);
  }
  .arrow-btn {
    // position: absolute;
    // bottom: 0;
    font-size: 12px;
    padding-left: 0;
    .iconfont {
      font-size: 12px;
    }
  }
}

.task-name {
  /* font-weight: bold;
  text-decoration: underline; */
}
</style>
