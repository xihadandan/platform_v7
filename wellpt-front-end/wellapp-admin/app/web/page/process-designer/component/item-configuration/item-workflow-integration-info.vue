<template>
  <div>
    <a-form-model-item label="流程定义">
      <a-select
        v-model="workflowIntegration.flowDefId"
        show-search
        style="width: 100%"
        :filter-option="filterSelectOption"
        @change="handleFlowDefChange"
        :disabled="inputDisabled"
      >
        <a-select-option v-for="d in flowDefOptions" :key="d.id">
          {{ d.text }}
        </a-select-option>
      </a-select>
    </a-form-model-item>
    <a-form-model-item>
      <template slot="label">
        <a-space>
          表单数据
          <a-popover>
            <template slot="content">
              直接使用事项办理单数据：工作流系统的表单和数据使用事项办理单和事项办理数据
              <br />
              通过单据转换获取：工作流系统的表单使用工作流表单，数据由事项办理数据通过单据转换获取
            </template>
            <a-icon type="info-circle" />
          </a-popover>
        </a-space>
      </template>
      <a-radio-group style="text-align: left; padding-left: 20px" v-model="workflowIntegration.formDataType" @change="onFormDataTypeChange">
        <a-radio value="1" :disabled="inputDisabled">直接使用事项办理单数据</a-radio>
        <a-radio value="2" :disabled="inputDisabled">通过单据转换获取</a-radio>
      </a-radio-group>
    </a-form-model-item>
    <template v-if="workflowIntegration.formDataType == '2'">
      <a-form-model-item label="单据转换规则">
        <a-select
          v-model="workflowIntegration.copyBotRuleId"
          show-search
          style="width: 100%"
          :filter-option="filterSelectOption"
          :disabled="inputDisabled"
        >
          <a-select-option v-for="d in botRuleOptions" :key="d.id">
            {{ d.text }}
          </a-select-option>
        </a-select>
      </a-form-model-item>
      <a-form-model-item :label-col="{ span: 12 }">
        <template slot="label">
          <a-space>
            工作流办结时回写数据
            <a-popover>
              <template slot="content">工作流办结时通过数据转换回写数据至事项办理单</template>
              <a-icon type="info-circle" />
            </a-popover>
          </a-space>
        </template>
        <a-switch v-model="workflowIntegration.returnWithOver" checked-children="是" un-checked-children="否" :disabled="inputDisabled" />
      </a-form-model-item>
      <a-form-model-item :label-col="{ span: 9 }">
        <template slot="label">
          <a-space>
            流向中回写数据
            <a-popover>
              <template slot="content">在指定工作流流向通过数据转换回写数据至事项办理单</template>
              <a-icon type="info-circle" />
            </a-popover>
          </a-space>
        </template>
        <a-switch
          v-model="workflowIntegration.returnWithDirection"
          checked-children="是"
          un-checked-children="否"
          :disabled="inputDisabled"
        />
      </a-form-model-item>
      <a-form-model-item v-show="workflowIntegration.returnWithDirection" label="选择流向">
        <a-select
          v-model="workflowIntegration.returnDirectionIds"
          mode="multiple"
          show-search
          style="width: 100%"
          :filter-option="filterSelectOption"
          :disabled="inputDisabled"
        >
          <a-select-option v-for="d in flowOptionData.directions" :key="d.id">{{ d.text }} ({{ d.id }})</a-select-option>
        </a-select>
      </a-form-model-item>
      <a-form-model-item v-show="workflowIntegration.returnWithOver || workflowIntegration.returnWithDirection" label="单据转换规则">
        <a-select
          v-model="workflowIntegration.returnBotRuleId"
          show-search
          style="width: 100%"
          :filter-option="filterSelectOption"
          :disabled="inputDisabled"
        >
          <a-select-option v-for="d in botRuleOptions" :key="d.id">
            {{ d.text }}
          </a-select-option>
        </a-select>
      </a-form-model-item>
    </template>
    <!-- <a-form-model-item label="里程碑">
      <ItemMilestonesConfiguration
        :closeOpenDrawer="closeOpenDrawer"
        :disabled="inputDisabled"
        :workflowIntegration="workflowIntegration"
      ></ItemMilestonesConfiguration>
    </a-form-model-item> -->
    <a-form-model-item label="事件发布">
      <ItemWorkflowEventPublishConfiguration
        :closeOpenDrawer="closeOpenDrawer"
        :disabled="inputDisabled"
        :workflowIntegration="workflowIntegration"
      ></ItemWorkflowEventPublishConfiguration>
    </a-form-model-item>
    <a-form-model-item label="计时信息">
      <a-checkbox v-model="workflowIntegration.syncTimerInfo" :disabled="inputDisabled">计时信息同步至事项办理单</a-checkbox>
    </a-form-model-item>
    <a-form-model-item label="状态管理">
      <ItemWorkflowStateConfiguration
        :closeOpenDrawer="closeOpenDrawer"
        :disabled="inputDisabled"
        :workflowIntegration="workflowIntegration"
      ></ItemWorkflowStateConfiguration>
    </a-form-model-item>
    <!-- <a-form-model-item label="发起业务事项">
      <ItemNewItemConfiguration
        :closeOpenDrawer="closeOpenDrawer"
        :disabled="inputDisabled"
        :workflowIntegration="workflowIntegration"
      ></ItemNewItemConfiguration>
    </a-form-model-item> -->
  </div>
</template>

<script>
// import ItemMilestonesConfiguration from './item-milestones-configuration.vue';
import ItemWorkflowEventPublishConfiguration from './item-workflow-event-publish-configuration.vue';
import ItemWorkflowStateConfiguration from './item-workflow-state-configuration.vue';
// import ItemNewItemConfiguration from './item-new-item-configuration.vue';
export default {
  props: {
    workflowIntegration: Object,
    closeOpenDrawer: {
      type: Boolean,
      default: true
    }
  },
  components: { ItemWorkflowEventPublishConfiguration, ItemWorkflowStateConfiguration },
  provide() {
    return {
      flowOptionData: this.flowOptionData
    };
  },
  inject: ['designer', 'filterSelectOption', 'getCacheData'],
  data() {
    const _this = this;
    if (_this.workflowIntegration.returnDirectionId) {
      _this.$set(_this.workflowIntegration, 'returnDirectionIds', _this.workflowIntegration.returnDirectionId.split(';'));
    } else {
      _this.$set(_this.workflowIntegration, 'returnDirectionIds', []);
    }
    return {
      flowDefOptions: [],
      botRuleOptions: [],
      flowOptionData: {
        directions: [],
        formFields: [],
        taskIds: []
      }
    };
  },
  computed: {
    inputDisabled() {
      return this.workflowIntegration.configType == '1';
    }
  },
  watch: {
    'workflowIntegration.returnDirectionIds': function (newVal, oldVal) {
      this.workflowIntegration.returnDirectionId = newVal.join(';');
    }
  },
  created() {
    this.refresh();
  },
  methods: {
    refresh() {
      this.handleFlowDefSearch();
      if (this.workflowIntegration.flowDefId) {
        this.handleFlowDefChange(this.workflowIntegration.flowDefId);
      }
      if (this.workflowIntegration.formDataType == '2') {
        this.handleBotRuleSearch();
      }
    },
    reset() {
      this.workflowIntegration = {
        type: '1',
        configType: '2',
        formDataType: '1'
      };
      this.refresh();
    },
    handleFlowDefSearch(value = '') {
      let _this = this;
      _this.$axios
        .post('/common/select2/query', {
          serviceName: 'flowSchemeService',
          queryMethod: 'loadSelectData',
          searchValue: value,
          pageSize: 1000,
          pageNo: 1
        })
        .then(({ data }) => {
          if (data.results) {
            _this.flowDefOptions = data.results;
          }
        });
    },
    handleFlowDefChange(flowDefId) {
      let _this = this;
      if (!flowDefId) {
        return;
      }
      _this.$axios.get(`/proxy/api/workflow/business/definition/getSelectDataByFlowDefId//${flowDefId}`).then(({ data: result }) => {
        if (result.data) {
          _this.flowOptionData = result.data;
          _this._provided.flowOptionData = _this.flowOptionData;
        }
      });
      // _this.$axios.get(`/proxy/api/workflow/business/definition/getSelectDataByFlowBizDefId/${flowBizDefId}`).then(({ data }) => {
      //   if (data.data) {
      //     _this.flowOptionData = data.data;
      //     _this._provided.flowOptionData = _this.flowOptionData;
      //   }
      // });
    },
    onFormDataTypeChange(e) {
      if (this.workflowIntegration.formDataType == '2') {
        this.handleBotRuleSearch();
      }
    },
    handleBotRuleSearch(value = '') {
      let _this = this;
      _this
        .getCacheData('botRuleOptions', (resolve, reject) => {
          _this.$axios
            .post('/common/select2/query', {
              serviceName: 'botRuleConfFacadeService',
              queryMethod: 'loadSelectData',
              searchValue: value,
              pageSize: 1000,
              pageNo: 1
            })
            .then(({ data }) => {
              if (data.results) {
                resolve(data.results);
              }
            })
            .catch(err => reject(err));
        })
        .then(botRuleOptions => {
          _this.botRuleOptions = botRuleOptions;
        });
    }
  }
};
</script>

<style></style>
