<template>
  <PerfectScrollbar :style="{ height: '600px' }" ref="scroll">
    <a-form-model ref="form" :model="formData" :colon="false" :rules="rules" :label-col="{ span: 3 }" :wrapper-col="{ span: 20 }">
      <a-form-model-item label="信息名称" prop="name">
        <a-input v-model="formData.name" :allowClear="true" />
      </a-form-model-item>
      <a-form-model-item label="记录字段" prop="field">
        <dyform-fields-tree-select
          v-model="formData.field"
          :formData="formData"
          prop="field"
          :treeCheckable="false"
        ></dyform-fields-tree-select>
      </a-form-model-item>
      <a-form-model-item class="line-height-size-base">
        <template slot="label">&nbsp;</template>
        <w-checkbox v-model="formData.fieldNotValidate">
          <a-tooltip placement="right" :arrowPointAtCenter="true">
            <div slot="title">选中后，记录字段的字段值发生变更时，不影响整个表单数据的数据变更校验结果</div>
            <label>
              字段不参与表单数据的变更校验
              <a-icon type="exclamation-circle" style="color: var(--w-text-color-light)" />
            </label>
          </a-tooltip>
        </w-checkbox>
      </a-form-model-item>
      <a-form-model-item label="记录方式">
        <a-radio-group v-model="formData.way" :options="recordWay" />
      </a-form-model-item>
      <a-form-model-item label="内容组织">
        <w-select :options="recordAssembler" v-model="formData.assembler"></w-select>
      </a-form-model-item>
      <a-form-model-item label="历史内容来源">
        <a-radio-group v-model="formData.contentOrigin" :options="recordContentOrigin" />
      </a-form-model-item>
      <a-form-model-item>
        <template slot="label">&nbsp;</template>
        <w-checkbox v-model="formData.ignoreEmpty">忽略空意见</w-checkbox>
      </a-form-model-item>
      <a-form-model-item>
        <template slot="label">&nbsp;</template>
        <w-checkbox v-model="formData.enableWysiwyg">
          意见即时显示
          <span style="color: var(--w-text-color-light)">| 记录的信息包含用户签署的意见时，即时显示</span>
        </w-checkbox>
      </a-form-model-item>
      <a-form-model-item label="信息格式" prop="value">
        <w-select
          :options="formatOptions"
          v-model="formData.value"
          :replaceFields="{
            title: 'name',
            key: 'value',
            value: 'value'
          }"
        ></w-select>
      </a-form-model-item>
      <a-form-model-item label="记录环节" v-if="!isTask">
        <node-task-select mode="multiple" :selectAll="true" v-model="formData.taskIds"></node-task-select>
        <div style="color: var(--w-text-color-light); line-height: var(--w-font-size-base)">(为空时默认对所有未配置信息记录的环节生效)</div>
      </a-form-model-item>
      <a-form-model-item label="前置条件" prop="conditions">
        <w-switch v-model="formData.enablePreCondition" @change="conditionsChange" />
        <template v-if="formData.enablePreCondition == '1'">
          <span style="color: var(--w-text-color-light); padding-left: var(--w-padding-3xs)">满足以下前置条件时记录信息</span>
          <condition-setting v-model="formData.conditions" :orgVersionId="orgVersionId" :isTask="isTask" @change="conditionsChange" />
        </template>
      </a-form-model-item>
    </a-form-model>
  </PerfectScrollbar>
</template>

<script>
import { recordWay, recordAssembler, recordContentOrigin } from '../designer/constant';
import { deepClone } from '@framework/vue/utils/util';
import { filter, map, each, findIndex, some } from 'lodash';
import WSwitch from '../components/w-switch.js';
import WSelect from '../components/w-select';
import WCheckbox from '../components/w-checkbox.js';
import NodeTaskSelect from '../commons/node-task-select';
import DyformFieldsTreeSelect from '../commons/dyform-fields-tree-select.vue';
import ConditionSetting from '../commons/condition-setting.vue';

export default {
  name: 'RecordInfo',
  inject: ['designer'],
  props: {
    data: {
      type: Object,
      default: () => {
        return {};
      }
    },
    orgVersionId: {
      type: String
    },
    isTask: {
      type: Boolean,
      default: false
    }
  },
  components: {
    WSelect,
    WCheckbox,
    NodeTaskSelect,
    WSwitch,
    DyformFieldsTreeSelect,
    ConditionSetting
  },
  data() {
    let formData = deepClone(this.data);
    this.initParamVal(formData, 'fieldNotValidate', '0');
    this.initParamVal(formData, 'ignoreEmpty', '0');
    this.initParamVal(formData, 'enableWysiwyg', '0');
    this.initParamVal(formData, 'enablePreCondition', '0');
    this.initParamVal(formData, 'way', '1');
    this.initParamVal(formData, 'contentOrigin', '1');
    this.initParamVal(formData, 'conditions', []);
    return {
      formData,
      recordWay,
      recordAssembler,
      recordContentOrigin
    };
  },
  computed: {
    rules() {
      let rules = {
        field: { required: true, message: '请选择记录字段！', trigger: ['blur', 'change'] },
        name: { required: true, message: '请输入信息名称！', trigger: ['blur'] },
        value: { required: true, message: '请选择信息格式', trigger: ['blur', 'change'] }
      };
      if (this.formData.enablePreCondition == '1') {
        rules.conditions = { required: true, message: '请选择前置条件', trigger: ['blur', 'change'] };
      }
      return rules;
    },
    formatOptions() {
      return this.designer.diction.formats || [];
    }
  },
  filters: {},
  created() {},
  methods: {
    initParamVal(formData, params, value) {
      if (formData[params] === undefined || formData[params] === null) {
        formData[params] = value;
      }
    },
    conditionsChange() {
      this.$nextTick(() => {
        if (this.formData.enablePreCondition == '1') {
          this.$refs.form.validateField('conditions');
        } else {
          this.$refs.form.clearValidate('conditions');
        }
      });
    },
    save(callback) {
      this.$refs.form.validate((valid, error) => {
        let data = deepClone(this.formData);
        callback({ valid, error, data: data });
      });
    }
  },
  watch: {
    formData: {
      deep: true,
      handler(v) {
        // console.log(v);
      }
    }
  }
};
</script>
<style lang="less" scope>
.flow-timer-modal-wrap {
  .distributer-item {
    border: 1px dashed var(--w-border-color-light);
    border-radius: var(--w-border-radius-base);
    padding-top: var(--w-padding-3xs);
  }
  .distribution-item {
    background: var(--w-fill-color-light);
    border-radius: var(--w-border-radius-base);
    margin: 0 var(--w-margin-3xs) var(--w-margin-2xs);
    padding-left: var(--w-padding-3xs);
  }
  .org-select-component {
    padding-top: 4px;
  }
  .line-height-size-base {
    .ant-form-item-control,
    .ant-form-item-label {
      line-height: var(--w-font-size-base);
    }
  }
}
</style>
