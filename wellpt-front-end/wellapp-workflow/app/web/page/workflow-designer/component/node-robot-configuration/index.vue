<template>
  <a-form-model
    ref="form"
    :model="formData"
    :colon="false"
    labelAlign="left"
    :label-col="{ span: 6 }"
    :wrapper-col="{ span: 17, style: { textAlign: 'right' } }"
  >
    <a-tabs v-model="tabKey" size="small" class="flex-card-tabs flow-flex-card-tabs">
      <a-tab-pane key="0" tab="基本属性">
        <a-form-model-item class="form-item-vertical" prop="name" label="名称">
          <a-input v-model.trim="formData.name" @blur="changeName">
            <template slot="addonAfter">
              <w-i18n-input :target="formData" :code="formData.id + '.taskName'" v-model="formData.name" />
            </template>
          </a-input>
        </a-form-model-item>
        <a-form-model-item class="form-item-vertical">
          <template slot="label">事件监听</template>
          <task-listener-tree-select v-model="formData.listener" :treeCheckable="true" />
        </a-form-model-item>
        <!-- 事件脚本 -->
        <a-form-model-item class="form-item-vertical" label="事件脚本">
          <event-scripts v-model="formData.eventScripts" showPointcut="task" />
        </a-form-model-item>
      </a-tab-pane>
    </a-tabs>
  </a-form-model>
</template>

<script>
import { debounce } from 'lodash';
import TaskListenerTreeSelect from '../commons/task-listener-tree-select.vue';
import EventScripts from '../commons/event-scripts.vue';
import WI18nInput from '@framework/vue/designer/w-i18n-input.vue';

export default {
  name: 'NodeRobotConfiguration',
  inject: ['graph'],
  props: {
    formData: {
      type: Object,
      default: () => {}
    },
    graphItem: {
      type: Object,
      default: () => {}
    }
  },
  components: {
    TaskListenerTreeSelect,
    EventScripts,
    WI18nInput
  },
  data() {
    return {
      tabKey: '0',
      selectedCellId: null
    };
  },
  created() {
    this.setSelectedCellId();
  },
  methods: {
    setSelectedCellId() {
      this.selectedCellId = this.graph.instance.selectedId;
    },
    // 更改名称
    changeName: function (event) {
      const value = event.target.value;
      if (this.graph.instance) {
        this.graph.instance.setEdgesLablesByName(value, this.selectedCellId);
      }
    }
  }
};
</script>
