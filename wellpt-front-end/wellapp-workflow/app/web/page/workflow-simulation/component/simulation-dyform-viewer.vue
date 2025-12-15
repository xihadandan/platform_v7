<template>
  <div class="simulation-dyform-viewer">
    <a-row
      v-if="workData.dyFormData"
      :style="{ padding: 'var(--w-padding-md) var(--w-padding-md) var(--w-padding-2xs) var(--w-padding-md)' }"
    >
      <a-col span="14">
        <h3>{{ simulationWorkData && simulationWorkData.workData && simulationWorkData.workData.title }}</h3>
      </a-col>
      <a-col span="10">
        <a-select v-model="taskInstUuid" show-search style="width: 67%" :filter-option="filterSelectOption" @change="taskChange">
          <a-select-option v-for="d in taskOptions" :key="d.taskInstUuid">
            {{ d.taskName }}
            <a-tag
              style="
                position: absolute;
                right: 0px;
                top: 4px;
                max-width: 150px;
                white-space: nowrap;
                text-overflow: ellipsis;
                overflow: hidden;
              "
              :title="d.details.taskUserNames"
            >
              {{ d.details.taskUserNames }}
            </a-tag>
          </a-select-option>
        </a-select>
        <a-select v-model="aclRole" style="width: 30%; float: right" :options="aclRoleOptions" @change="aclRoleChange"></a-select>
      </a-col>
    </a-row>
    <PerfectScrollbar
      :style="{
        height: 'calc(100vh - 120px)'
      }"
    >
      <WidgetDyform
        v-if="workData.dyFormData && definitionVjson && displayState"
        ref="dyform"
        :isNewFormData="false"
        :displayState="displayState"
        :formUuid="workData.dyFormData.formUuid"
        :definitionVjson="definitionVjson"
        :dataUuid="workData.dyFormData.dataUuid || workData.dataUuid"
        :formDatas="workData.dyFormData.formDatas"
        :formElementRules="formElementRules"
        @mounted="onDyformMounted"
        @formDataChanged="onDyformDataChanged"
        :dyformStyle="{ padding: 'var(--w-padding-md)' }"
      />
      <a-empty v-else :style="{ padding: 'var(--w-padding-md)' }"></a-empty>
      <WorkflowSubflowViewer
        v-if="workData.dyFormData && definitionVjson && displayState"
        ref="subflowViewer"
        :workView="workView"
      ></WorkflowSubflowViewer>
    </PerfectScrollbar>
  </div>
</template>

<script>
import WorkView from '../../workflow-work/component/work-view.vue';
import WorkViewJsModule from '../../workflow-work/component/WorkView.js';
import { filterSelectOption } from '@framework/vue/utils/function';
const formElementRules = WorkView.computed.formElementRules;
export default {
  components: {},
  inject: ['simulation'],
  data() {
    return {
      workView: null,
      taskInstUuid: '',
      aclRole: 'TODO',
      displayState: '',
      simulationWorkData: {
        recordUuid: undefined,
        record: undefined,
        workData: undefined
      },
      aclRoleOptions: [
        { label: '待办', value: 'TODO' },
        { label: '已办', value: 'DONE' }
      ]
    };
  },
  watch: {
    'simulation.simulationData': {
      handler(newVal, oldVal) {
        if (this.$el.offsetParent) {
          if (newVal && newVal.recordUuid) {
            this.loadWorkData(newVal.recordUuid);
          } else {
            this.simulationWorkData.workData = null;
          }
        }
      }
    },
    'simulation.activeKey': function (newVal, oldVal) {
      if (newVal == 'form' && this.simulation.simulationData && this.simulation.simulationData.recordUuid) {
        this.$loading();
        this.loadWorkData(this.simulation.simulationData.recordUuid);
      }
    }
  },
  computed: {
    workData() {
      return this.simulationWorkData.workData || {};
    },
    definitionVjson() {
      let definitionVjson = null;
      if (this.simulationWorkData.workData && this.simulationWorkData.workData.dyFormData) {
        definitionVjson = JSON.parse(this.simulationWorkData.workData.dyFormData.definitionVjson);
      }
      return definitionVjson;
    },
    formElementRules,
    taskOptions() {
      let options = [];
      let record = this.simulationWorkData.record;
      if (!record || !record.items) {
        return options;
      }
      options = record.items.filter(item => item.flowInstUuid == record.flowInstUuid);
      options.forEach(item => {
        if (typeof item.details == 'string') {
          item.details = JSON.parse(item.details);
        }
      });
      return options;
    }
  },
  mounted() {
    this.$tempStorage.setDriver && this.$tempStorage.setDriver(this.$tempStorage.LOCALSTORAGE);
    if (this.simulation.simulationData && this.simulation.simulationData.recordUuid) {
      this.$loading();
      this.loadWorkData(this.simulation.simulationData.recordUuid);
    }
  },
  methods: {
    filterSelectOption,
    loadWorkData(recordUuid, taskInstUuid = '') {
      const _this = this;
      _this.simulationWorkData = {};
      $axios
        .get(`/proxy/api/workflow/simulation/getWorkData?recordUuid=${recordUuid}&taskInstUuid=${taskInstUuid}`)
        .then(({ data: result }) => {
          _this.$loading(false);
          if (result.data) {
            _this.simulationWorkData = result.data;
            if (_this.simulationWorkData && _this.simulationWorkData.workData) {
              _this.workView = new WorkViewJsModule({
                workData: _this.workData,
                $widget: _this,
                onLoad() {
                  if (_this.simulationWorkData.workData && _this.simulationWorkData.workData.dyFormData.formDefinition) {
                    let formDefinition = JSON.parse(_this.simulationWorkData.workData.dyFormData.formDefinition);
                    _this.tabs = formDefinition.tabs;
                  }
                }
              });
              _this.taskInstUuid = _this.simulationWorkData.workData.taskInstUuid;
              _this.aclRole = _this.simulationWorkData.workData.aclRole || _this.aclRole;
            }
            _this.aclRoleChange();
          }
        });
    },
    onDyformMounted() {},
    onDyformDataChanged() {},
    taskChange() {
      this.$loading();
      this.loadWorkData(this.simulationWorkData.recordUuid, this.taskInstUuid);
    },
    aclRoleChange() {
      const _this = this;
      _this.displayState = null;
      let displayState = '';
      if (_this.aclRole == 'DONE') {
        displayState = 'label';
      } else {
        displayState = this.workData && this.workData.canEditForm == false ? 'label' : 'edit'; // 表单默认状态可编辑
      }
      _this.$nextTick(() => {
        _this.displayState = displayState;
      });
    }
  }
};
</script>

<style lang="less" scoped></style>
