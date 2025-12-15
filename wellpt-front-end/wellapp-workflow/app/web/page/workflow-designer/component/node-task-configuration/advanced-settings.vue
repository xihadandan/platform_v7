<template>
  <!-- 环节属性-高级设置 -->
  <div>
    <!-- 信息记录 -->
    <a-form-model-item class="form-item-vertical">
      <template slot="label">
        <a-tooltip placement="topRight" :arrowPointAtCenter="true">
          <div slot="title">添加本环节需要记录到表单的信息， 例如环节办理意见、办理人等</div>
          <label>
            信息记录
            <a-icon type="exclamation-circle" />
          </label>
        </a-tooltip>
      </template>
      <div class="message-templates-container">
        <div class="message-template-item" v-for="(record, index) in formData.records" :key="index">
          <div class="message-template-name">{{ record.name }}</div>
          <div class="message-template-btn-group record-btn-group">
            <a-button type="link" size="small" @click="setRecord(record, index)">
              <Icon type="pticon iconfont icon-ptkj-shezhi" />
            </a-button>
            <a-button type="link" size="small" @click="delRecord(record, index)">
              <Icon type="pticon iconfont icon-ptkj-shanchu" />
            </a-button>
          </div>
        </div>
      </div>
      <a-button type="link" size="small" @click="addRecord" icon="plus">添加</a-button>
    </a-form-model-item>
    <a-form-model-item class="form-item-vertical">
      <template slot="label">
        <a-tooltip placement="topRight" :arrowPointAtCenter="true">
          <div slot="title">添加仅供本环节使用的打印模板（需要配置套打权限）</div>
          <label>
            打印模板
            <a-icon type="exclamation-circle" />
          </label>
        </a-tooltip>
      </template>
      <print-template-tree-select :formData="formData" />
    </a-form-model-item>
    <a-form-model-item class="form-item-vertical">
      <template slot="label">
        <a-tooltip placement="topRight" :arrowPointAtCenter="true">
          <div slot="title">当流程流转至本环节时，自动生成流水号</div>
          <label>
            生成流水号
            <a-icon type="exclamation-circle" />
          </label>
        </a-tooltip>
      </template>
      <w-select
        :options="serialNumbers"
        placeholder="请选择"
        v-model="formData.serialNo"
        :formData="formData"
        formDataFieldName="snName"
        :replaceFields="{
          title: 'name',
          key: 'data',
          value: 'data'
        }"
      />
    </a-form-model-item>
    <a-form-model-item class="form-item-vertical">
      <template slot="label">加载JS模块</template>
      <!-- <w-select v-model="formData.customJsModule" :options="flowDevelops" /> -->
      <custom-js-module v-model="formData.customJsModule" />
    </a-form-model-item>
    <a-form-model-item class="form-item-vertical">
      <template slot="label">事件监听</template>
      <task-listener-tree-select v-model="formData.listener" :treeCheckable="true" />
    </a-form-model-item>
    <!-- 事件脚本 -->
    <a-form-model-item class="form-item-vertical" label="事件脚本">
      <event-scripts v-model="formData.eventScripts" showPointcut="task"></event-scripts>
    </a-form-model-item>
    <a-modal
      title="信息记录"
      :visible="recordInfoVisible"
      @ok="saveRecord"
      @cancel="recordInfoVisible = false"
      :width="800"
      :bodyStyle="{ height: '600px', 'overflow-y': 'auto' }"
      wrapClassName="flow-timer-modal-wrap"
      :getContainer="getContainer"
      :destroyOnClose="true"
    >
      <record-info ref="recordInfoRef" :data="currentRecordInfo" :orgVersionId="orgVersionId" :isTask="true" />
    </a-modal>
  </div>
</template>

<script>
import Modal from '@pageAssembly/app/web/lib/modal.vue';
import PrintTemplateTreeSelect from '../commons/print-template-tree-select.vue';
import TaskListenerTreeSelect from '../commons/task-listener-tree-select.vue';
import RecordInfo from '../commons/record-info.vue';
import EventScripts from '../commons/event-scripts.vue';
import WSelect from '../components/w-select';
import CustomJsModule from '../commons/custom-js-module.vue';

export default {
  name: 'TaskPropertyAdvancedSettings',
  inject: ['designer', 'workFlowData'],
  props: {
    formData: {
      type: Object,
      default: () => {}
    }
  },
  components: {
    Modal,
    PrintTemplateTreeSelect,
    TaskListenerTreeSelect,
    RecordInfo,
    EventScripts,
    WSelect,
    CustomJsModule
  },
  data() {
    return {
      recordInfoVisible: false,
      currentRecordInfo: {},
      currentRecordInfoIndex: -1,
      flowDevelops: [],
      serialNumbers: []
    };
  },
  computed: {
    orgVersionId() {
      return this.workFlowData.property.orgVersionId || '';
    }
  },
  created() {
    // this.getJavaScriptDevelops();
    this.getSerialNumbers();
  },
  methods: {
    // 获取环节流程JS二开
    getJavaScriptDevelops() {
      const params = {
        serviceName: 'appJavaScriptModuleMgr',
        queryMethod: '',
        pageSize: 1000,
        pageNo: 1,
        dependencyFilter: 'WorkViewFragment'
      };
      this.$axios
        .post('/common/select2/query', {
          ...params
        })
        .then(res => {
          if (res.status === 200) {
            if (res.data && res.data.results) {
              const data = res.data.results;
              this.flowDevelops = data;
            }
          }
        });
    },
    // 获取流水号
    getSerialNumbers() {
      const params = {
        args: '[-1]',
        methodName: 'getSerialNumbers',
        serviceName: 'flowSchemeService'
      };
      this.$axios
        .post('/json/data/services', {
          ...params
        })
        .then(res => {
          if (res.status === 200) {
            if (res.data && res.data.data) {
              const data = res.data.data;
              this.serialNumbers = data;
            }
          }
        });
    },
    getContainer() {
      return document.querySelector('.edit-widget-property-container');
    },
    // 信息记录
    saveRecord() {
      this.$refs.recordInfoRef.save(({ valid, error, data }) => {
        if (valid) {
          if (this.currentRecordInfoIndex == -1) {
            this.formData.records.push(data);
          } else {
            this.formData.records.splice(this.currentRecordInfoIndex, 1, data);
          }
          this.recordInfoVisible = false;
        }
      });
    },
    addRecord() {
      this.currentRecordInfo = {};
      this.currentRecordInfoIndex = -1;
      this.recordInfoVisible = true;
    },
    setRecord(data, index) {
      this.currentRecordInfoIndex = index;
      this.currentRecordInfo = data;
      this.recordInfoVisible = true;
    },
    delRecord(data, index) {
      this.formData.records.splice(index, 1);
    }
  }
};
</script>
