<template>
  <div class="wf-condition-component">
    <div :class="['condition-container']">
      <div class="condition-item flex" v-for="(record, index) in dataList" :key="index">
        <div class="f_g_1">{{ record.label }}</div>
        <div class="f_s_0">
          <a-button type="link" size="small" @click="setFormData(record, index)" icon="setting"></a-button>
          <a-button type="link" size="small" @click="delFormData(record, index)" icon="delete"></a-button>
        </div>
      </div>
      <a-button type="link" size="small" @click="addFormData" icon="plus">添加{{ text }}</a-button>
    </div>
    <a-modal
      class="wf-condition-component-modal"
      :maskClosable="false"
      :title="title"
      :visible="visible"
      :okText="okText"
      cancelText="取消"
      :width="800"
      :bodyStyle="{ padding: '12px 20px', height: '600px', 'overflow-y': 'auto' }"
      @cancel="onCancelModal"
      @ok="onOkModal"
      ref="modal"
    >
      <a-form-model
        ref="form"
        :model="formData"
        :colon="false"
        :rules="rules"
        labelAlign="left"
        :label-col="{ flex: '120px' }"
        :wrapper-col="{ flex: 'auto' }"
      >
        <a-form-model-item label="条件类型">
          <a-radio-group v-model="formData.type" :options="typeOption" @change="typeChange" />
        </a-form-model-item>
        <a-form-model-item label="与前一个条件的逻辑关系" prop="connector">
          <w-select
            style="width: 150px"
            :options="conditionAndOr"
            placeholder="请选择逻辑关系"
            :formData="formData"
            formDataFieldName="connectorName"
            v-model="formData.connector"
            :replaceFields="{ title: 'text', key: 'value', value: 'value' }"
          ></w-select>
        </a-form-model-item>
        <div class="flex f_warp condition-data" v-show="formData.type != '4'">
          <div style="width: 100px">
            <a-form-model-item prop="leftBracket">
              <template slot="label"></template>
              <w-select :options="conditionLBracket" v-model="formData.leftBracket"></w-select>
            </a-form-model-item>
          </div>
          <!-- 通过字段值比较 -->
          <template v-if="formData.type == '1'">
            <div style="width: 200px">
              <a-form-model-item prop="fieldName">
                <template slot="label"></template>
                <main-dyform-fields-select
                  v-model="formData.fieldName"
                  :formData="formData"
                  formDataFieldName="fieldText"
                ></main-dyform-fields-select>
              </a-form-model-item>
            </div>
            <div style="width: 135px">
              <a-form-model-item prop="operator">
                <template slot="label"></template>
                <w-select
                  :options="logicalOperators"
                  placeholder="请选择操作符"
                  v-model="formData.operator"
                  :formData="formData"
                  formDataFieldName="operatorName"
                  :replaceFields="{ title: 'text', key: 'data', value: 'data' }"
                ></w-select>
              </a-form-model-item>
            </div>
            <div style="width: 200px">
              <a-form-model-item prop="value">
                <template slot="label"></template>
                <a-input v-model="formData.value" :allowClear="true" />
              </a-form-model-item>
            </div>
          </template>
          <!-- 通过办理人归属判断 -->
          <template v-if="formData.type == '3'">
            <div style="width: 260px">
              <a-form-model-item prop="groupType">
                <template slot="label"></template>
                <w-select
                  :options="conditionGroupTypeSub"
                  placeholder="请选择"
                  :allowClear="false"
                  v-model="formData.groupType"
                  :formData="formData"
                  formDataFieldName="groupTypeName"
                  style="width: 260px"
                  @change="changeGroupType"
                ></w-select>
              </a-form-model-item>
            </div>
            <div style="width: 260px" v-if="formData.groupType">
              <a-form-model-item prop="groupValue" v-if="formData.groupType == '0'">
                <template slot="label"></template>
                <org-select
                  :orgVersionId="orgVersionId"
                  :orgVersionIds="orgVersionIds"
                  v-model="formData.groupValue"
                  v-if="orgVersionId"
                  @change="changeOrg"
                />
              </a-form-model-item>
              <a-form-model-item prop="groupDataValue" v-else-if="formData.groupType == '1'">
                <template slot="label"></template>
                <!-- <user-select
                  v-model="formData.groupDataValue"
                  :types="userTypes"
                  title="选择人员"
                  :orgVersionId="orgVersionId"
                  @change="usersChange"
                /> -->
                <user-select-list v-model="formData.groupDataValue" :types="userTypes" title="选择人员" @change="usersChange" />
              </a-form-model-item>
            </div>
          </template>
          <!-- 自定义条件 -->
          <template v-if="formData.type == '5'">
            <div style="width: 550px">
              <a-form-model-item prop="expression">
                <template slot="label"></template>
                <a-textarea
                  v-model="formData.expression"
                  placeholder="请输入自定义条件"
                  :auto-size="{ minRows: 3, maxRows: 5 }"
                  style="margin-top: 4px"
                />
                <div style="color: var(--w-text-color-light); line-height: var(--w-line-height)">{{ defineTip }}</div>
              </a-form-model-item>
            </div>
          </template>
          <div style="width: 100px">
            <a-form-model-item prop="rightBracket">
              <template slot="label"></template>
              <w-select :options="conditionRBracket" v-model="formData.rightBracket"></w-select>
            </a-form-model-item>
          </div>
        </div>
      </a-form-model>
    </a-modal>
  </div>
</template>

<script type="text/babel">
import { filter, map, each, findIndex, isInteger, assignIn } from 'lodash';
import OrgSelect from '@admin/app/web/lib/org-select.vue';
import { deepClone } from '@framework/vue/utils/util';
import {
  conditionLogicType,
  conditionAndOr,
  conditionLBracket,
  conditionRBracket,
  conditionGroupTypeSub,
  logicalOperators
} from '../designer/constant';
import WSelect from '../components/w-select';
import mainDyformFieldsSelect from '../commons/main-dyform-fields-select';
import UserSelect from '../commons/user-select.vue';
import UserSelectList from '../commons/user-select-list.vue';

export default {
  name: 'ConditionSetting',
  inject: ['designer', 'graph', 'workFlowData'],
  components: {
    OrgSelect,
    mainDyformFieldsSelect,
    WSelect,
    UserSelect,
    UserSelectList
  },
  props: {
    text: {
      type: String,
      default: '分发条件'
    },
    value: {
      type: null
    },
    types: {
      type: Array,
      default: () => {
        return ['1', '3', '4', '5'];
      }
    },
    separator: {
      // 分隔符
      type: String,
      default: ';'
    },
    orgVersionId: {
      type: String
    },
    okText: {
      type: String,
      default: '确定'
    }
  },
  data() {
    let validateBracket = (rule, value, callback) => {
      if (rule.field == 'lBracket') {
        if (!value && this.formData.rBracket) {
          callback(new Error('请选择！'));
        } else {
          callback();
        }
      } else {
        if (!value && this.formData.lBracket) {
          callback(new Error('请选择！'));
        } else {
          callback();
        }
      }
    };
    let dataList = [];
    if (this.value) {
      dataList = JSON.parse(this.value) ? JSON.parse(this.value).conditionConfigs : [];
    }
    let initFormData = {
      connector: '',
      expression: '',
      fieldName: '',
      groupArgValue: '',
      groupType: '',
      groupValue: '',
      groupDataValue: [],
      label: '',
      leftBracket: '',
      operator: '',
      rightBracket: '',
      type: '1',
      value: ''
    };
    return {
      validateBracket,
      conditionGroupTypeSub,
      conditionLBracket,
      conditionRBracket,
      logicalOperators,
      conditionAndOr,
      userTypes: 'unit/bizOrg/field/task/filter',
      dataList,
      visible: false,
      initFormData,
      formData: deepClone(initFormData), // 弹框内容
      currentIndex: -1,
      title: '分发条件',
      defineTip:
        '动态表单变量使用${dyform.表单字段}。变量支持>、>=、<、<=、==、!=、contains、notcontains等操作。字符串常量用单引号括起来，多个条件用逻辑与&&、逻辑或||、左右括号等组合。'
    };
  },
  filters: {},
  computed: {
    orgVersionIds() {
      return this.workFlowData.property.orgVersionIds || [];
    },
    typeOption() {
      let options = [];
      each(conditionLogicType, item => {
        let index = this.types.indexOf(item.value);
        if (index > -1) {
          options.push(item);
        }
      });
      return options;
    },
    rules() {
      let rules = {
        groupType: { required: true, message: '请选择！', trigger: ['blur', 'change'] },
        operator: { required: true, message: '请选择操作符！', trigger: ['blur', 'change'] },
        value: { required: true, message: '请填写内容！', trigger: ['blur', 'change'] },
        expression: { required: true, message: '请填写自定义条件', trigger: ['blur', 'change'] },
        groupValue: { required: true, message: '请选择业务组织', trigger: ['blur', 'change'] },
        groupDataValue: { required: true, message: '请选择指定人员', trigger: ['blur', 'change'] },
        rBracket: { required: true, validator: this.validateBracket, trigger: ['blur', 'change'] },
        lBracket: { required: true, validator: this.validateBracket, trigger: ['blur', 'change'] },
        fieldName: { required: true, message: '请选择字段！', trigger: ['blur', 'change'] }
      };
      if (this.formData.type == '4') {
        rules.connector = { required: true, message: '请选择！', trigger: ['blur', 'change'] };
      }
      return rules;
    }
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {
    changeOrg({ value, label, nodes }) {
      this.formData.groupArgValue = label;
    },
    addFormData() {
      // 初始默认第一个
      this.currentIndex = -1;
      this.formData = this.setDetailFormData(
        {
          type: this.types[0]
        },
        true
      );
      this.title = '添加' + this.text;
      this.visible = true;
      this.$nextTick(() => {
        this.$refs.form.clearValidate();
      });
    },
    setFormData(data, index) {
      this.currentIndex = index;
      let cdata = deepClone(data);
      this.formData = this.setDetailFormData(cdata);
      this.title = this.text;
      this.visible = true;
      this.$nextTick(() => {
        this.$refs.form.clearValidate();
      });
    },
    delFormData(data, index) {
      this.dataList.splice(index, 1);
      let conditions = {
        conditionConfigs: this.dataList
      };
      this.$emit('input', JSON.stringify(conditions));
      this.$emit('change', JSON.stringify(conditions));
    },
    setDetailFormData(data, isClear) {
      if (isClear) {
        data = assignIn({}, deepClone(this.initFormData), data);
        data.connector = this.formData.connector || '';
      }
      if (data.type == '3' && data.groupType == '1' && data.groupValue && !data.groupDataValue) {
        data.groupDataValue = this.usersData(data.groupValue);
      }
      return data;
    },
    // 根据id所在位置，获取对应的type值
    getTypeByIndex(index) {
      if (index == 0 || index == 1) {
        //unit
        return 1;
      } else if (index == 2) {
        //'field'
        return 2;
      } else if (index == 4) {
        //'option'
        return 8;
      } else if (index == 3) {
        //'task'
        return 4;
      } else if (index == 5) {
        //'custom'
        return 16;
      } else if (code == 'direction') {
        // return "16";
      }
      return 32;
    },
    usersData(ids) {
      let data = [];
      if (ids) {
        each(ids.split(','), (item, index) => {
          if (item) {
            let type = this.getTypeByIndex(index);
            if (index == 0) {
              let values = ids.split(',')[1].split(';');
              each(item.split(';'), (citem, cindex) => {
                data.push({
                  value: values[cindex],
                  argValue: citem,
                  type: type
                });
              });
            } else if (index > 1) {
              each(item.split(';'), (citem, cindex) => {
                data.push({
                  value: citem,
                  argValue: citem,
                  type: type
                });
              });
            }
          }
        });
      }
      return data;
    },
    titleSet(text) {
      if (text.indexOf('|') > -1) {
        let _text = text.split('|');
        return _text[0];
      }
      return text;
    },
    aLogicBuildValue(formData) {
      let data = deepClone(formData);
      // 条件显示信息
      var label = '';
      if (data.connector) {
        label = data.connectorName + ' ';
      }

      // 通过字段值比较
      if (data.type === '1') {
        label += data.leftBracket + '[' + data.fieldText + '] ' + data.operatorName + ' ' + data.value + data.rightBracket;
      } else if (data.type === '3') {
        // 按办理人归属
        label += data.leftBracket + '[' + data.groupTypeName + '] ' + data.groupArgValue + data.rightBracket;
      } else if (data.type === '5') {
        // 自定义条件
        label += data.leftBracket + '[自定义条件] ' + data.expression + data.rightBracket;
      }
      data.label = label;
      // console.log(data);
      return data;
    },
    emitValueChange() {
      let value = this.aLogicBuildValue(this.formData);
      if (this.currentIndex == -1) {
        this.dataList.push(value);
      } else {
        this.dataList.splice(this.currentIndex, 1, value);
      }
      let data = {
        conditionConfigs: this.dataList
      };
      this.$emit('input', JSON.stringify(data));
      this.$emit('change', { value: JSON.stringify(data), data: this.dataList });
    },
    onCancelModal() {
      let _this = this;
      this.visible = false;
      _this.$emit('cancel');
    },
    onOkModal() {
      this.$refs.form.validate((valid, error) => {
        if (valid) {
          this.visible = false;
          this.emitValueChange();
        }
      });
    },
    typeChange(e) {
      let type = e.target.value;
      this.formData = this.setDetailFormData(
        {
          type: type
        },
        true
      );
    },
    usersChange({ value: valueNodes, selectedNodes }) {
      let value = [],
        argValue = [],
        optionValue = [];
      this.userTypes.split('/').forEach(type => {
        if (type == 'unit' || type == 'bizOrg') {
          let curArgValue = [];
          selectedNodes[type].data.forEach(item => {
            if (item.argValue) {
              argValue.push(item.argValue);
              curArgValue.push(item.argValue);
            }
          });
          value.push(curArgValue.join(';'));
        } else if (type == 'task') {
          selectedNodes[type].data.forEach(item => {
            if (item.value) {
              item.value.split(';').forEach(v => {
                argValue.push('<' + v + '>');
              });
            }
          });
        } else if (type == 'field') {
          selectedNodes[type].data.forEach(item => {
            if (item.value) {
              item.value.split(';').forEach(v => {
                argValue.push('{' + v + '}');
              });
            }
          });
        }
        selectedNodes[type].data.forEach(item => {
          if (item.userOptions && item.userOptions.length) {
            item.userOptions.forEach(uo => {
              optionValue.push(uo.value);
            });
          }
        });
        if (type !== 'filter') {
          value.push(map(selectedNodes[type].data, 'value').join(';'));
        }
      });
      value.push(optionValue.join(';'));
      optionValue.forEach(uo => {
        argValue.push('[' + uo + ']');
      });
      this.formData.groupValue = value.join(',');
      this.formData.groupArgValue = argValue.join(';');
    },
    changeGroupType() {
      this.formData.groupValue = '';
      this.formData.groupArgValue = '';
      this.formData.groupDataValue = [];
    },
    changeValue(v) {
      // this.dataList = deepClone(v);
    }
  },

  watch: {
    value: {
      deep: true,
      handler(v) {
        this.changeValue(v);
      }
    }
  }
};
</script>
<style lang="less">
.wf-condition-component {
  .condition-container {
    border: 1px dashed var(--w-border-color-light);
    border-radius: var(--w-border-radius-base);
    padding-top: var(--w-padding-3xs);
  }
  .condition-item {
    background: var(--w-fill-color-light);
    border-radius: var(--w-border-radius-base);
    margin: 0 var(--w-margin-3xs) var(--w-margin-2xs);
    padding-left: var(--w-padding-3xs);
    padding-top: var(--w-padding-3xs);
    padding-bottom: var(--w-padding-3xs);
    line-height: var(--w-line-height);
  }
}
.wf-condition-component-modal {
  .ant-row {
    margin-bottom: 4px;
    display: flex;
    .ant-form-item-control-wrapper {
      flex: 1 1 auto;
    }
    .ant-form-item-label-left {
      line-height: var(--w-line-height);
      white-space: normal;
    }
  }
  .condition-data {
    > div {
      margin-right: var(--w-margin-3xs);
      margin-bottom: var(--w-margin-2xs);
    }
  }
  .org-select-component {
    padding-top: 4px;
  }
}
</style>
