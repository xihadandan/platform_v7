<template>
  <a-form-model
    class="basic-info"
    :model="newItemConfig"
    labelAlign="left"
    ref="basicForm"
    :rules="rules"
    :label-col="{ span: 7 }"
    :wrapper-col="{ span: 17, style: { textAlign: 'right' } }"
    :colon="false"
  >
    <a-form-model-item label="标题" prop="title">
      <a-input v-model="newItemConfig.title" />
    </a-form-model-item>
    <a-form-model-item label="发起方式" prop="startItemWay">
      <a-radio-group v-model="newItemConfig.startItemWay" default-value="1">
        <a-radio value="1">流程办结时发起</a-radio>
        <a-radio value="2">流流向流转时发起</a-radio>
      </a-radio-group>
    </a-form-model-item>
    <a-form-model-item v-show="newItemConfig.startItemWay == '2'" label="发起流向" prop="startItemDirectionId">
      <a-select v-model="newItemConfig.startItemDirectionId" show-search style="width: 100%" :filter-option="false">
        <a-select-option v-for="d in flowOptionData.directions" :key="d.id">
          {{ d.text }}
        </a-select-option>
      </a-select>
    </a-form-model-item>
    <a-form-model-item label="发起事项" prop="startItemId">
      <a-select
        v-model="newItemConfig.startItemId"
        show-search
        style="width: 100%"
        :filter-option="false"
        :options="itemOptions"
        @change="handleStartItemChange"
      ></a-select>
    </a-form-model-item>
    <a-form-model-item label="工作流表单数据" prop="formDataType">
      <a-radio-group v-model="newItemConfig.formDataType" default-value="1" @change="onFormDataTypeChange">
        <a-radio value="1">使用源流程数据</a-radio>
        <a-radio value="2">使用单据转换</a-radio>
      </a-radio-group>
    </a-form-model-item>
    <a-form-model-item v-show="newItemConfig.formDataType == '2'" label="单据转换规则" prop="copyBotRuleId">
      <a-select v-model="newItemConfig.copyBotRuleId" show-search style="width: 100%" :filter-option="filterSelectOption">
        <a-select-option v-for="d in botRuleOptions" :key="d.id">
          {{ d.text }}
        </a-select-option>
      </a-select>
    </a-form-model-item>
    <a-form-model-item label="流程集成提交环节" prop="toTaskId">
      <a-select v-model="newItemConfig.toTaskId" show-search style="width: 100%" :filter-option="false">
        <a-select-option :value="'AUTO_SUBMIT'">自动提交</a-select-option>
        <a-select-option v-for="d in newItemFlowOptionData.taskIds" :key="d.id">
          {{ d.text }}
        </a-select-option>
      </a-select>
    </a-form-model-item>
    <a-form-model-item label="办理人来源" prop="taskUserSource">
      <a-radio-group v-model="newItemConfig.taskUserSource" default-value="1">
        <a-radio value="1">按流程定义配置</a-radio>
        <a-radio value="2">现在指定</a-radio>
      </a-radio-group>
    </a-form-model-item>
    <div v-show="newItemConfig.taskUserSource == '2'">
      <a-form-model-item label="办理人类型" prop="taskUserType">
        <a-radio-group v-model="newItemConfig.taskUserType" default-value="1">
          <a-radio value="1">组织机构</a-radio>
          <a-radio value="4">源流程历史环节办理人</a-radio>
          <a-radio value="8">人员选项</a-radio>
        </a-radio-group>
      </a-form-model-item>
      <a-form-model-item v-show="newItemConfig.taskUserType == '1'" label="组织机构" prop="taskUserId">
        <OrgSelect v-model="newItemConfig.taskUserId" @change="onOrgSelectChange"></OrgSelect>
      </a-form-model-item>
      <a-form-model-item v-show="newItemConfig.taskUserType == '4'" label="办理环节" prop="taskUserId4">
        <a-select mode="multiple" v-model="newItemConfig.taskUserId4" show-search style="width: 100%" :filter-option="false">
          <a-select-option v-for="d in flowOptionData.taskIds" :key="d.id">
            {{ d.text }}
          </a-select-option>
        </a-select>
      </a-form-model-item>
      <a-form-model-item v-show="newItemConfig.taskUserType == '8'" label="人员选项" prop="taskUserId8">
        <a-checkbox-group v-model="newItemConfig.taskUserId8">
          <a-checkbox value="PriorUser">当前用户</a-checkbox>
          <a-checkbox value="Creator">源流程申请人</a-checkbox>
        </a-checkbox-group>
      </a-form-model-item>
    </div>
  </a-form-model>
</template>

<script>
import { generateId } from '../../designer/utils';
import OrgSelect from '@admin/app/web/lib/org-select.vue';
export default {
  props: {
    newItemConfig: {
      type: Object,
      default() {
        return {
          id: generateId(),
          taskUserType: '1',
          taskUserId4: [],
          taskUserId8: []
        };
      }
    }
  },
  components: { OrgSelect },
  inject: ['flowOptionData', 'designer', 'filterSelectOption', 'getCacheData'],
  data() {
    return {
      triggerTypeOptions: [
        { value: '30', label: '环节创建' },
        { value: '40', label: '环节完成' },
        { value: '50', label: '流向流转' }
      ],
      rules: {
        title: [{ required: true, message: '名称不能为空！', trigger: 'blur' }],
        startItemId: [{ required: true, message: '事项不能为空！', trigger: 'blur' }]
      },
      itemOptions: [],
      botRuleOptions: [],
      newItemFlowOptionData: {
        directions: [],
        formFields: [],
        taskIds: []
      }
    };
  },
  watch: {
    'newItemConfig.taskUserType': function (val) {
      if (val == '4') {
        this.newItemConfig.taskUserId = this.newItemConfig.taskUserId4.join(';');
      } else if (val == '8') {
        this.newItemConfig.taskUserId = this.newItemConfig.taskUserId8.join(';');
      }
    },
    'newItemConfig.taskUserId4': function (val) {
      this.newItemConfig.taskUserId = val.join(';');
    },
    'newItemConfig.taskUserId8': function (val) {
      this.newItemConfig.taskUserId = val.join(';');
    }
  },
  created() {
    const _this = this;
    let itemDataList = _this.designer.processTree.getTreeDataList('item');
    let itemOptions = [];
    let itemMap = new Map();
    itemDataList.forEach(item => {
      if (item.data && item.data.configuration) {
        let configuration = item.data.configuration;
        itemOptions.push({ value: configuration.id, label: configuration.itemName });
        itemMap.set(configuration.id, configuration);
      }
    });
    _this.itemOptions = itemOptions;
    _this.itemMap = itemMap;

    // 办理人类型的人员选项
    if (_this.newItemConfig.taskUserType == '4') {
      if (_this.newItemConfig.taskUserId) {
        _this.$set(_this.newItemConfig, 'taskUserId4', _this.newItemConfig.taskUserId.split(';'));
      } else {
        _this.$set(_this.newItemConfig, 'taskUserId4', []);
      }
    } else if (this.newItemConfig.taskUserType == '8') {
      if (_this.newItemConfig.taskUserId) {
        _this.$set(_this.newItemConfig, 'taskUserId8', _this.newItemConfig.taskUserId.split(';'));
      } else {
        _this.$set(_this.newItemConfig, 'taskUserId8', []);
      }
    }

    _this.onFormDataTypeChange();
    if (_this.newItemConfig.startItemId) {
      _this.handleStartItemChange(_this.newItemConfig.startItemId);
    }
  },
  methods: {
    collect() {
      return this.$refs.basicForm
        .validate()
        .then(valid => {
          if (valid) {
            return this.newItemConfig;
          }
          return null;
        })
        .catch(valid => {
          console.log('valid ', valid);
        });
    },
    onFormDataTypeChange() {
      if (this.newItemConfig.formDataType && this.botRuleOptions.length == 0) {
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
    },
    handleStartItemChange(itemId) {
      let _this = this;
      let itemDefinition = _this.itemMap.get(itemId);
      if (!itemId || !itemDefinition) {
        _this.newItemFlowOptionData = {
          directions: [],
          formFields: [],
          taskIds: []
        };
        return;
      }

      let businessIntegrationConfigs = itemDefinition.businessIntegrationConfigs || [];
      let flowBizDefId = '';
      businessIntegrationConfigs.forEach(item => {
        if (item.type == '1') {
          flowBizDefId = item.flowBizDefId;
        }
      });

      if (flowBizDefId) {
        _this.$axios.get(`/proxy/api/workflow/business/definition/getSelectDataByFlowBizDefId/${flowBizDefId}`).then(({ data }) => {
          if (data.data) {
            _this.newItemFlowOptionData = data.data;
          }
        });
      } else {
        _this.newItemFlowOptionData = {
          directions: [],
          formFields: [],
          taskIds: []
        };
      }
    },
    onOrgSelectChange({ value, label, nodes }) {
      this.newItemConfig.taskUserName = label;
    }
  }
};
</script>

<style></style>
