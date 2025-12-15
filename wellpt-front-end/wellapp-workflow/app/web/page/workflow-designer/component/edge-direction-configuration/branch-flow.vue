<template>
  <!-- 流向属性-分支流 -->
  <div>
    <a-form-model-item class="form-item-vertical" label="分支模式">
      <a-radio-group v-model="formData.branchMode" size="small" :options="branchModeConfig" @change="changeBranchMode" />
      <template v-if="formData.branchMode === branchModeConfig[1]['value']">
        <div class="title-expression-value">
          <div class="branch-dynamic-title">分支实例</div>
          <a-radio-group v-model="formData.branchInstanceType" size="small" :options="branchInstanceTypeConfig" />
          <div class="branch-dynamic-title">来源</div>
          <w-select v-model="formData.branchCreateWay" :options="createWayOptions" />
          <template v-if="formData.branchCreateWay === createWayOptions[1]['value']">
            <div class="branch-dynamic-title">接口</div>
            <w-select
              v-model="formData.branchInterface"
              formDataFieldName="branchInterfaceName"
              :formData="formData"
              :options="interfaceOptions"
              :replaceFields="replaceFields"
            />
          </template>
          <template v-else>
            <div class="branch-dynamic-title">表单</div>
            <a-select
              v-model="branchCreateInstanceFormId"
              placeholder="请选择"
              @change="changeFormId"
              :getPopupContainer="getPopupContainerByPs()"
              :dropdownClassName="getDropdownClassName()"
            >
              <a-select-opt-group v-for="(opt, i) in formOptions" :key="i" :label="opt.label">
                <a-select-option v-for="item in opt.children" :key="item.value" :value="item.value" :title="item.label">
                  {{ item.label }}
                </a-select-option>
              </a-select-opt-group>
            </a-select>
            <div class="branch-dynamic-title">字段</div>
            <w-select
              v-model="formData.branchTaskUsers"
              formDataFieldName="taskUsersName"
              :formData="formData"
              :options="fieldOptions"
              :replaceFields="{
                title: 'name',
                key: 'code',
                value: 'code'
              }"
            />
            <div class="branch-dynamic-title">创建方式</div>
            <w-select v-model="formData.branchCreateInstanceWay" :formData="formData" :options="createInstanceWayOptions" />
            <div v-if="showCreateInstanceBatch">
              <w-checkbox v-model="formData.branchCreateInstanceBatch">按从表行分批次生成实例</w-checkbox>
            </div>
          </template>
        </div>
      </template>
      <div>
        <w-checkbox v-model="formData.shareBranch">共享分支</w-checkbox>
      </div>
    </a-form-model-item>
    <a-form-model-item class="form-item-vertical" label="聚合">
      <w-checkbox v-model="formData.isIndependentBranch">该流向不参与聚合</w-checkbox>
    </a-form-model-item>
  </div>
</template>

<script>
import { branchModeConfig, branchInstanceTypeConfig, createWayOptions, createInstanceWayOptions } from '../designer/constant';
import { fetchSubtaskDispatcherCustomInterfaces } from '../api/index';
import WCheckbox from '../components/w-checkbox';
import WSelect from '../components/w-select';
import { getPopupContainerByPs, getDropdownClassName } from '@dyform/app/web/page/dyform-designer/utils';

export default {
  name: 'EdgeDirectionBranchFlow',
  inject: ['designer'],
  props: {
    formData: {
      type: Object,
      default: () => {}
    }
  },
  components: {
    WCheckbox,
    WSelect
  },
  data() {
    let branchCreateInstanceFormId = this.formData.branchCreateInstanceFormId ? this.formData.branchCreateInstanceFormId : undefined;
    return {
      replaceFields: {
        title: 'name',
        key: 'id',
        value: 'id'
      },
      branchCreateInstanceFormId,
      branchModeConfig,
      branchInstanceTypeConfig,
      createWayOptions,
      createInstanceWayOptions,
      interfaceOptions: []
    };
  },
  computed: {
    showCreateInstanceBatch() {
      let show = false;
      if (this.formData.branchCreateInstanceFormId && this.formData.branchCreateInstanceFormId !== this.designer.formFieldDefinition.uuid) {
        show = true;
      }
      return show;
    },
    //流程实例-表单选项
    formOptions() {
      let optionGroup = [
        {
          label: '主表',
          children: []
        }
      ];
      if (this.designer.formDefinition) {
        const data = this.designer.formFieldDefinition;
        optionGroup[0]['children'] = [
          {
            label: data.name,
            value: data.uuid
          }
        ];
      }
      if (Object.keys(this.designer.subformFieldMap)) {
        const subformFieldMap = this.designer.subformFieldMap;
        optionGroup.push({
          label: '从表',
          children: []
        });
        let subform = [];
        for (const key in subformFieldMap) {
          const item = subformFieldMap[key];
          subform.push({
            label: item.name,
            value: item.uuid
          });
        }
        optionGroup[1]['children'] = subform;
      }
      return optionGroup;
    },
    // 流程实例-字段选项
    fieldOptions() {
      let fieldOptions = [];
      if (this.formData.branchCreateInstanceFormId) {
        if (this.formData.branchCreateInstanceFormId === this.designer.formFieldDefinition.uuid) {
          fieldOptions = this.designer.formFieldDefinition.fields;
        } else {
          if (this.designer.subformFieldMap[this.formData.branchCreateInstanceFormId]) {
            fieldOptions = this.designer.subformFieldMap[this.formData.branchCreateInstanceFormId]['fields'];
          }
        }
      }
      return fieldOptions;
    }
  },
  created() {
    fetchSubtaskDispatcherCustomInterfaces().then(res => {
      this.interfaceOptions = res;
    });
  },
  methods: {
    getPopupContainerByPs,
    getDropdownClassName,
    // 更改流程实例表单
    changeFormId(value) {
      this.formData.branchCreateInstanceFormId = value;
      this.formData.branchTaskUsers = '';
    },
    changeBranchMode(event) {
      const value = event.target.value;
      if (value === this.branchModeConfig[1]['value']) {
        this.formData.branchInstanceType = this.branchInstanceTypeConfig[0]['value'];
      }
    }
  }
};
</script>

<style lang="less">
.branch-dynamic-title {
  padding-top: 10px;
  line-height: 20px;
  &:first-child {
    padding-top: 0;
  }
}
</style>
