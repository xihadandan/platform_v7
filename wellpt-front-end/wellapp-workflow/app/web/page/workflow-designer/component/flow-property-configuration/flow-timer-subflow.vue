<template>
  <div>
    <div class="message-template-item" v-for="(record, index) in dataSource" :key="index">
      <div class="message-template-name">{{ record.taskName }}</div>
      <div class="message-template-btn-group record-btn-group">
        <a-button type="link" size="small" @click="setItem(record, index)">
          <Icon type="pticon iconfont icon-ptkj-shezhi" />
        </a-button>
        <a-button type="link" size="small" @click="delItem(index)">
          <Icon type="pticon iconfont icon-ptkj-shanchu" />
        </a-button>
      </div>
    </div>
    <a-button type="link" @click="addItem" icon="plus">添加</a-button>

    <modal
      title="子流程环节计时设置"
      v-model="visible"
      :container="getContainer"
      :ok="saveItme"
      okText="保存"
      :width="800"
      wrapperClass="flow-timer-modal-wrap"
    >
      <template slot="content">
        <a-form-model
          ref="form"
          :model="currentItem"
          :rules="rules"
          :colon="false"
          labelAlign="left"
          :label-col="{ span: 6 }"
          :wrapper-col="{ span: 17 }"
        >
          <a-form-model-item prop="taskId" label="环节名称">
            <node-subflow-select v-model="currentItem.taskId" :placeholder="rules['taskId']['message']" :filterId="existTimerSubTaskId" />
          </a-form-model-item>
          <a-form-model-item prop="timingMode">
            <template slot="label">
              <label>计时方式</label>
              <a-tooltip placement="topRight" :arrowPointAtCenter="true">
                <div slot="title">
                  按当前计时器计时：当前子流程环节的办理时限同普通流程环节，和子流程实例的办理时限无关。
                  <br />
                  按子流程计时器计时：当前子流程环节的办理时限和子流程实例的办理时限保持一致。
                </div>
                <a-icon type="exclamation-circle" />
              </a-tooltip>
            </template>
            <a-radio :defaultChecked="true" :value="currentItem.timingMode">按当前计时器计时</a-radio>
          </a-form-model-item>
        </a-form-model>
      </template>
    </modal>
  </div>
</template>

<script>
import { generateId } from '@framework/vue/utils/util';
import Modal from '@pageAssembly/app/web/lib/modal.vue';
import NodeSubflowSelect from '../commons/node-subflow-select';

export default {
  name: 'FlowTimerSubflow',
  inject: ['workFlowData', 'graph'],
  props: {
    value: {
      type: Array,
      default: []
    },
    formData: {
      type: Object,
      default: () => {}
    }
  },
  components: {
    Modal,
    NodeSubflowSelect
  },
  data() {
    let dataSource = [];
    if (this.value) {
      dataSource = this.value;
    }
    return {
      rules: {
        taskId: { required: true, message: '请选择' },
        timingMode: { required: true, message: '必填' }
      },
      currentIndex: 0,
      currentItem: {},
      visible: false,
      createItem: () => {
        return {
          id: generateId(),
          taskId: '',
          taskName: '',
          timingMode: '1',
          timers: []
        };
      }
    };
  },
  computed: {
    dataSource() {
      let data = [];
      if (this.value && this.value.length) {
        this.value.forEach(item => {
          const subflow = this.subflowsData.find(s => s.id === item.taskId);
          if (subflow) {
            item.taskName = subflow.name;
            item.id = generateId();
            data.push(item);
          }
        });
      }
      return data;
    },
    // 子流程数据
    subflowsData() {
      let tasksData = [];
      if (this.graph.instance) {
        tasksData = this.graph.instance.subflowsData;
      }
      return tasksData;
    },
    // 已经存在计时的子流程
    existTimerSubTaskId() {
      let taskIds = [];
      this.workFlowData.timers.forEach(item => {
        if (item.timerId !== this.formData.timerId) {
          item.subTasks.forEach(t => {
            taskIds.push(t.value);
          });
        }
      });
      return taskIds;
    }
  },
  methods: {
    setItem(record, index) {
      this.currentIndex = index;
      this.currentItem = JSON.parse(JSON.stringify(record));
      this.visible = true;
    },
    delItem(index) {
      this.dataSource.splice(index, 1);
      this.$emit('input', this.dataSource);
    },
    addItem() {
      this.currentItem = this.createItem();
      this.visible = true;
    },
    // 保存子流程环节计时
    saveItme(callback) {
      this.$refs.form.validate((valid, error) => {
        if (valid) {
          const findIndex = this.dataSource.findIndex(item => {
            return item.id === this.currentItem.id;
          });

          if (findIndex === -1) {
            this.dataSource.push(this.currentItem);
          } else {
            this.dataSource.splice(findIndex, 1, this.currentItem);
          }
          this.$emit('input', this.dataSource);
          callback(true);
        }
      });
    },
    getContainer() {
      return document.querySelector('.edit-widget-property-container');
    }
  }
};
</script>
