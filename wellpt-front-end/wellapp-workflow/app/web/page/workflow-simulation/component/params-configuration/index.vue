<template>
  <a-form-model ref="form" :model="formData" labelAlign="left" :label-col="{ span: 7 }" :wrapper-col="{ span: 17 }" :colon="false">
    <PerfectScrollbar style="height: calc(100vh - 102px)">
      <a-collapse :bordered="false" defaultActiveKey="1" expandIconPosition="right">
        <a-collapse-panel key="1" header="基本信息">
          <a-form-model-item label="启动人" prop="startUserId">
            <OrgSelect
              v-if="simulation.inited"
              v-model="formData.startUserId"
              :checkableTypes="['user']"
              :multiSelect="simulation.multiStartUser"
              @change="({ label }) => (formData.startUserName = label)"
            />
          </a-form-model-item>
          <a-form-model-item prop="taskUserId">
            <template slot="label">
              <a-space>
                办理人
                <a-popover>
                  <template slot="content">
                    仿真办理人，解析规则以优先级最高的配置为准，
                    <br />
                    仿真环节配置的办理人 > 此配置项的办理人 > 流程定义环节配置的办理人
                  </template>
                  <a-icon type="info-circle" />
                </a-popover>
              </a-space>
            </template>
            <OrgSelect v-model="formData.taskUserId" :checkableTypes="['user']" />
          </a-form-model-item>
          <a-form-model-item label="执行次数" prop="runNum">
            <a-input-number v-model="formData.runNum" :min="1" :max="10" :precision="0" />
          </a-form-model-item>
          <a-form-model-item label="开始执行环节" prop="startTasks">
            <a-select
              v-model="formData.startTaskId"
              allow-clear
              show-search
              style="width: 100%"
              :filter-option="filterSelectOption"
              placeholder="默认从开始环节执行"
            >
              <a-select-option v-for="d in startTaskOptions" :key="d.id">
                {{ d.name }}
              </a-select-option>
            </a-select>
          </a-form-model-item>
          <a-form-model-item label="暂停执行环节" prop="pauseTasks">
            <a-select
              mode="multiple"
              v-model="formData.pauseTasks"
              allow-clear
              show-search
              style="width: 100%"
              :filter-option="filterSelectOption"
              placeholder="进入环节时暂停执行"
            >
              <a-select-option v-for="d in taskOptions" :key="d.id">
                {{ d.name }}
              </a-select-option>
            </a-select>
          </a-form-model-item>
          <a-form-model-item label="结束执行环节" prop="endTasks">
            <a-select
              mode="multiple"
              v-model="formData.endTasks"
              allow-clear
              show-search
              style="width: 100%"
              :filter-option="filterSelectOption"
              placeholder="环节执行后结束执行"
            >
              <a-select-option v-for="d in taskOptions" :key="d.id">
                {{ d.name }}
              </a-select-option>
            </a-select>
          </a-form-model-item>
        </a-collapse-panel>
        <a-collapse-panel key="2" header="表单数据">
          <a-form-model-item label="数据来源" prop="formDataSource">
            <a-radio-group size="small" v-model="formData.formDataSource" :default-value="'create'">
              <a-radio-button value="create">新建表单数据</a-radio-button>
              <a-radio-button value="record">使用仿真报告数据</a-radio-button>
            </a-radio-group>
          </a-form-model-item>
          <a-form-model-item v-if="formData.formDataSource == 'record'" label="仿真报告" prop="recordUuid">
            <a-select v-model="formData.recordUuid" allow-clear show-search style="width: 100%" :filter-option="filterSelectOption">
              <a-select-option v-for="d in recordOptions" :key="d.uuid">{{ d.operatorName }} ({{ d.operatorTime }})</a-select-option>
            </a-select>
          </a-form-model-item>
          <a-form-model-item label="数据交互" prop="interactionMode">
            <a-radio-group size="small" v-model="formData.interactionMode" :default-value="'requiredField'">
              <a-radio-button value="requiredField">按必填字段为空</a-radio-button>
              <a-radio-button value="task">按指定环节</a-radio-button>
            </a-radio-group>
          </a-form-model-item>
          <a-form-model-item v-if="formData.interactionMode == 'task'" label="交互环节" prop="interactionTasks">
            <a-select
              mode="multiple"
              v-model="formData.interactionTasks"
              allow-clear
              show-search
              style="width: 100%"
              :filter-option="filterSelectOption"
            >
              <a-select-option v-for="d in userTaskOptions" :key="d.id">
                {{ d.name }}
              </a-select-option>
            </a-select>
          </a-form-model-item>
        </a-collapse-panel>
        <a-collapse-panel key="3" header="仿真环节">
          <TasksConfiguration :params="formData" :flowDefinition="flowDefinition"></TasksConfiguration>
        </a-collapse-panel>
        <a-collapse-panel key="4" header="其他设置">
          <div>
            <a-row>
              <a-col span="10" offset="2">
                <a-checkbox v-model="formData.generateSerialNumber">是否生成流水号</a-checkbox>
              </a-col>
              <a-col span="12">
                <a-checkbox v-model="formData.sendMsg">是否发送消息</a-checkbox>
              </a-col>
            </a-row>
            <a-row>
              <a-col span="10" offset="2">
                <a-checkbox v-model="formData.archive">是否归档</a-checkbox>
              </a-col>
              <!-- <a-col span="12">
                <a-checkbox v-model="formData.autoViewWorkAfterSimulation">仿真结束后自动打开查看流程数据</a-checkbox>
              </a-col> -->
            </a-row>
          </div>
        </a-collapse-panel>
      </a-collapse>
    </PerfectScrollbar>
  </a-form-model>
</template>

<script>
import OrgSelect from '@admin/app/web/lib/org-select.vue';
import TasksConfiguration from './tasks-configuration.vue';
import { filterSelectOption } from '@framework/vue/utils/function';
export default {
  components: { OrgSelect, TasksConfiguration },
  inject: ['simulation', 'flowDefinition'],
  data() {
    return {
      formData: this.simulation.params
    };
  },
  watch: {
    'formData.formDataSource': function (newVal, oldVal) {
      this.formDataSourceChange();
    }
  },
  computed: {
    taskOptions() {
      return this.flowDefinition.tasks;
    },
    startTaskOptions() {
      return this.flowDefinition.tasks.filter(task => task.type == '1');
    },
    userTaskOptions() {
      return this.flowDefinition.tasks.filter(task => task.type == '1' || task.type == '3');
    },
    recordOptions() {
      return this.simulation.records;
    }
  },
  mounted() {
    if (this.formData.formDataSource == 'record') {
      this.listRecord();
    }
  },
  methods: {
    filterSelectOption,
    listRecord() {
      $axios.get(`/proxy/api/workflow/simulation/record/list?flowDefUuid=${this.flowDefinition.uuid}`).then(({ data: result }) => {
        if (result.data) {
          this.simulation.records = result.data;
        }
      });
    },
    formDataSourceChange() {
      if (!this.recordOptions.length && this.formData.formDataSource == 'record') {
        this.listRecord();
      }
    }
  }
};
</script>

<style></style>
