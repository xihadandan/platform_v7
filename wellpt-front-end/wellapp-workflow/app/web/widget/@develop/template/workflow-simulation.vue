<template>
  <a-form-model ref="form" :model="formData" :label-col="{ span: 4 }" :wrapper-col="{ span: 19 }" :colon="false">
    <a-tabs default-active-key="1">
      <a-tab-pane key="1" tab="基本信息">
        <a-form-model-item label="启动人" prop="startUserId">
          <OrgSelect v-model="formData.startUserId" :checkableTypes="['user']" :multiSelect="false" />
          注：启动人为空时取当前用户
        </a-form-model-item>
        <a-form-model-item label=" ">
          <a-checkbox v-model="formData.generateSerialNumber">是否生成流水号</a-checkbox>
          <a-checkbox v-model="formData.sendMsg">是否发送消息</a-checkbox>
          <a-checkbox v-model="formData.archive">是否归档</a-checkbox>
          <a-checkbox v-model="formData.autoViewWorkAfterSimulation">仿真结束后自动打开查看流程数据</a-checkbox>
        </a-form-model-item>
      </a-tab-pane>
      <a-tab-pane key="2" tab="表单数据">
        <a-form-model-item label=" ">
          <a-checkbox v-model="formData.autoCreateEmptyFormData" disabled>自动创建空表单</a-checkbox>
        </a-form-model-item>
        <a-form-model-item label="数据交互环节">
          <a-select
            v-model="formData.interactionTasks"
            mode="multiple"
            show-search
            :options="interactionTaskOptions"
            :filterOption="filterOption"
          ></a-select>
        </a-form-model-item>
      </a-tab-pane>
      <a-tab-pane key="3" tab="办理意见">
        <a-row>
          <a-col :span="6" :push="18" style="text-align: right">
            <a-button type="primary" @click="addOpinion">添加</a-button>
            <a-button type="danger" @click="deleteOpinion">删除</a-button>
          </a-col>
        </a-row>
        <p />
        <a-table
          rowKey="id"
          size="small"
          :pagination="false"
          :bordered="false"
          :columns="optionColumns"
          :data-source="formData.opinions"
          :rowSelection="rowSelection"
          :locale="locale"
          :class="['widget-table-option-table no-border']"
        >
          <template slot="taskIdSlot" slot-scope="text, record, index">
            <a-select v-model="record.taskId" show-search :options="interactionTaskOptions" :filterOption="filterOption"></a-select>
          </template>
          <template slot="taskUserIdSlot" slot-scope="text, record, index">
            <OrgSelect v-model="record.taskUserId" :checkableTypes="['user']" :multiSelect="false" />
          </template>
          <template slot="opinionTextSlot" slot-scope="text, record, index">
            <a-input v-model="record.opinionText"></a-input>
          </template>
          <template slot="opinionValueSlot" slot-scope="text, record, index">
            <a-select
              v-model="record.opinionValue"
              show-search
              :options="getOpinionValueOptions(record.taskId)"
              :filterOption="filterOption"
              @change="opinionValueChange(record)"
            ></a-select>
          </template>
        </a-table>
      </a-tab-pane>
    </a-tabs>
  </a-form-model>
</template>

<script type="text/babel">
import OrgSelect from '@admin/app/web/lib/org-select.vue';
import { generateId } from '@framework/vue/utils/util';
import { isEmpty } from 'lodash';

export default {
  props: {
    flowDefId: String
  },
  components: { OrgSelect },
  data() {
    return {
      formData: {
        startUserId: '',
        generateSerialNumber: false,
        sendMsg: false,
        archive: false,
        autoViewWorkAfterSimulation: false,
        autoCreateEmptyFormData: true,
        interactionTasks: [],
        opinions: []
      },
      initTasks: [],
      interactionTaskOptions: [],
      optionColumns: [
        { title: '环节', dataIndex: 'taskId', width: '110px', scopedSlots: { customRender: 'taskIdSlot' } },
        { title: '使用人', dataIndex: 'taskUserId', width: '180px', scopedSlots: { customRender: 'taskUserIdSlot' } },
        { title: '办理意见', dataIndex: 'opinionText', scopedSlots: { customRender: 'opinionTextSlot' } },
        { title: '意见立场', dataIndex: 'opinionValue', scopedSlots: { customRender: 'opinionValueSlot' } }
      ],
      rowSelection: {
        selectedRowKeys: [],
        selectedRows: [],
        onChange: this.onSelectRowChange
      },
      locale: {
        emptyText: <span>暂无数据</span>
      }
    };
  },
  created() {
    this.loadFormDataInteractionTasks();
  },
  methods: {
    loadFormDataInteractionTasks() {
      $axios.get(`/api/workflow/simulation/listTaskByFlowDefId?flowDefId=${this.flowDefId}`).then(({ data: result }) => {
        if (result.data) {
          this.initTasks = result.data;
          this.interactionTaskOptions = result.data.map(item => ({ label: item.text, value: item.id }));
        }
      });
    },
    filterOption(inputValue, option) {
      return (
        (option.componentOptions.propsData.value &&
          option.componentOptions.propsData.value.toLowerCase().indexOf(inputValue.toLowerCase()) >= 0) ||
        option.componentOptions.children[0].text.toLowerCase().indexOf(inputValue.toLowerCase()) >= 0
      );
    },
    onSelectRowChange(selectedRowKeys, selectedRows) {
      this.rowSelection.selectedRowKeys = selectedRowKeys;
      this.rowSelection.selectedRows = selectedRows;
    },
    addOpinion() {
      this.formData.opinions.push({
        id: generateId()
      });
    },
    deleteOpinion() {
      const _this = this;
      if (_this.rowSelection.selectedRowKeys.length == 0) {
        _this.$message.error('请选择记录！');
        return;
      }

      _this.rowSelection.selectedRowKeys.forEach(key => {
        let index = _this.formData.opinions.findIndex(opinion => opinion.id == key);
        _this.formData.opinions.splice(index, 1);
      });
      _this.rowSelection.selectedRowKeys = [];
      _this.rowSelection.selectedRows = [];
    },
    getOpinionValueOptions(taskId) {
      if (isEmpty(taskId)) {
        return [];
      }

      let taskInfo = this.initTasks.find(task => task.id == taskId);
      if (!taskInfo || !taskInfo.opinions) {
        return [];
      }

      return taskInfo.opinions.map(item => ({ label: item.name, value: item.value }));
    },
    opinionValueChange(record) {
      let opinion = this.getOpinionValueOptions(record.taskId).find(item => item.value == record.opinionValue);
      if (opinion) {
        record.opinionLabel = opinion.label;
      }
    },
    collect() {
      return Promise.resolve(this.formData);
    }
  }
};
</script>

<style></style>
