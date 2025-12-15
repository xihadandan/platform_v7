<template>
  <div class="wf-condition-component">
    <div :class="['condition-container']">
      <div class="condition-item flex" v-for="(record, index) in value" :key="index">
        <div class="f_g_1">{{ record.argValue }}</div>
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
        <a-form-model-item label="与前一个条件的逻辑关系" prop="andOr">
          <w-select
            style="width: 150px"
            :options="conditionAndOr"
            placeholder="请选择逻辑关系"
            :formData="formData"
            formDataFieldName="andOrName"
            v-model="formData.andOr"
          />
        </a-form-model-item>
        <div class="flex f_warp condition-data" v-show="formData.type != '4'">
          <div style="width: 100px">
            <a-form-model-item prop="lBracket">
              <template slot="label"></template>
              <w-select :options="conditionLBracket" v-model="formData.lBracket" />
            </a-form-model-item>
          </div>
          <!-- 通过字段值比较 -->
          <template v-if="formData.type == '1'">
            <div style="width: 150px">
              <a-form-model-item prop="field">
                <template slot="label"></template>
                <main-dyform-fields-select
                  v-model="formData.field"
                  :formData="formData"
                  formDataFieldName="fieldName"
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
                  :replaceFields="{ title: 'text', key: 'value', value: 'value' }"
                ></w-select>
              </a-form-model-item>
            </div>
            <div style="width: 150px" v-if="formData.option">
              <a-form-model-item prop="value">
                <template slot="label"></template>
                <template v-if="formData.option == '2'">
                  <main-dyform-fields-select
                    v-model="formData.value"
                    :formData="formData"
                    formDataFieldName="valueName"
                  ></main-dyform-fields-select>
                </template>
                <template v-else-if="formData.option == '1'">
                  <a-input v-model="formData.value" :allowClear="true" />
                </template>
              </a-form-model-item>
            </div>
            <div style="width: 120px">
              <a-form-model-item prop="option">
                <template slot="label"></template>
                <w-select
                  :options="fieldOptions"
                  placeholder="请选择"
                  :allowClear="false"
                  v-model="formData.option"
                  :formData="formData"
                  formDataFieldName="optionName"
                  @change="optionChange"
                ></w-select>
              </a-form-model-item>
            </div>
          </template>
          <!-- 通过投票比例设置条件 -->
          <template v-if="formData.type == '2'">
            <div style="width: 150px">
              <a-form-model-item prop="param">
                <template slot="label"></template>
                <w-select
                  :options="voteOptions"
                  placeholder="请选择投票意见"
                  :allowClear="false"
                  v-model="formData.param"
                  :formData="formData"
                  formDataFieldName="paramName"
                ></w-select>
              </a-form-model-item>
            </div>
            <div style="width: 135px">
              <a-form-model-item prop="operator">
                <template slot="label"></template>
                <w-select
                  :options="logicalOperatorsNotLike"
                  placeholder="请选择操作符"
                  v-model="formData.operator"
                  :formData="formData"
                  formDataFieldName="operatorName"
                  :replaceFields="{ title: 'text', key: 'value', value: 'value' }"
                ></w-select>
              </a-form-model-item>
            </div>
            <div style="width: 150px" v-if="formData.option">
              <a-form-model-item prop="value">
                <template slot="label"></template>
                <template v-if="formData.option == '2'">
                  <main-dyform-fields-select
                    v-model="formData.value"
                    :formData="formData"
                    formDataFieldName="valueName"
                  ></main-dyform-fields-select>
                </template>
                <template v-else-if="formData.option == '1'">
                  <a-input-number
                    style="width: 100%"
                    v-model="formData.value"
                    :min="0"
                    :max="100"
                    :formatter="value => `${value}%`"
                    :parser="value => value.replace('%', '')"
                  />
                </template>
              </a-form-model-item>
            </div>
            <div style="width: 120px">
              <a-form-model-item prop="option">
                <template slot="label"></template>
                <w-select
                  :options="fieldOptions"
                  placeholder="请选择"
                  :allowClear="false"
                  v-model="formData.option"
                  :formData="formData"
                  formDataFieldName="optionName"
                  @change="optionChange"
                ></w-select>
              </a-form-model-item>
            </div>
          </template>
          <!-- 通过意见立场判断 -->
          <template v-if="formData.type == '6'">
            <div style="width: 150px">
              <a-form-model-item prop="param">
                <template slot="label"></template>
                <w-select
                  :options="positionOptions"
                  placeholder="请选择"
                  :allowClear="false"
                  v-model="formData.param"
                  :formData="formData"
                  formDataFieldName="paramName"
                />
              </a-form-model-item>
            </div>
            <div style="width: 135px">
              <a-form-model-item prop="operator">
                <template slot="label"></template>
                <w-select
                  :options="positionOperateor"
                  placeholder="请选择操作符"
                  v-model="formData.operator"
                  :formData="formData"
                  formDataFieldName="operatorName"
                ></w-select>
              </a-form-model-item>
            </div>
            <div style="width: 240px">
              <a-form-model-item prop="value">
                <template slot="label"></template>
                <w-select
                  :options="voteOptions"
                  placeholder="请选择"
                  :allowClear="false"
                  v-model="formData.value"
                  :formData="formData"
                  formDataFieldName="valueName"
                ></w-select>
              </a-form-model-item>
            </div>
          </template>
          <!-- 通过办理人归属判断 -->
          <template v-if="formData.type == '3'">
            <div style="width: 150px">
              <a-form-model-item prop="param" label="">
                <w-select
                  :options="conditionGroupType"
                  :allowClear="false"
                  v-model="formData.param"
                  :formData="formData"
                  formDataFieldName="paramName"
                  style="width: 150px"
                />
              </a-form-model-item>
            </div>
            <div style="width: 120px" v-if="formData.param == '0'">
              <a-form-model-item prop="field" label="">
                <main-dyform-fields-select v-model="formData.field" :formData="formData" formDataFieldName="fieldName" />
              </a-form-model-item>
            </div>
            <div style="line-height: 40px; width: 55px">归属于</div>
            <div style="width: 200px" v-if="formData.option">
              <a-form-model-item prop="value" label="">
                <template v-if="formData.option == '2'">
                  <main-dyform-fields-select v-model="formData.value" :formData="formData" formDataFieldName="valueName" />
                </template>
                <template v-else-if="formData.option == '1'">
                  <!-- <user-select v-model="formData.value" types="unit/field/option/task" title="选择人员" :orgVersionId="orgVersionId" /> -->
                  <user-select-list v-model="formData.value" types="unit/bizOrg/field/task/filter" title="选择人员" />
                </template>
              </a-form-model-item>
            </div>
            <div style="width: 120px">
              <a-form-model-item prop="option" label="">
                <w-select
                  :options="orgOptions"
                  :allowClear="false"
                  v-model="formData.option"
                  :formData="formData"
                  formDataFieldName="optionName"
                  @change="optionChange"
                />
              </a-form-model-item>
            </div>
          </template>
          <!-- 通过职等职级判断 -->
          <template v-if="formData.type == '7'">
            <div style="width: 150px">
              <a-form-model-item prop="param" label="">
                <w-select
                  :options="conditionDutyData"
                  :allowClear="false"
                  v-model="formData.param"
                  :formData="formData"
                  formDataFieldName="paramName"
                  @change="dutyChange"
                />
              </a-form-model-item>
            </div>
            <div style="width: 135px">
              <a-form-model-item prop="operator" label="">
                <w-select
                  :options="levelOperateors"
                  placeholder="请选择操作符"
                  v-model="formData.operator"
                  :formData="formData"
                  formDataFieldName="operatorName"
                />
              </a-form-model-item>
            </div>
            <div style="width: 150px" v-if="formData.option && formData.param">
              <a-form-model-item prop="value" label="">
                <template v-if="formData.option == '2'">
                  <main-dyform-fields-select v-model="formData.value" :formData="formData" formDataFieldName="valueName" />
                </template>
                <template v-else-if="formData.option == '1'">
                  <template v-if="['A1', 'A3'].indexOf(formData.param) > -1">
                    <a-input-number style="width: 100%" v-model="formData.value" :min="0" :max="100" />
                  </template>
                  <template v-if="['A2', 'A4'].indexOf(formData.param) > -1">
                    <w-tree-select
                      :treeData="JobRankOptions"
                      v-model="formData.value"
                      :formData="formData"
                      formDataFieldName="valueName"
                      :replaceFields="{
                        children: 'children',
                        title: 'name',
                        key: 'id',
                        value: 'id'
                      }"
                    />
                  </template>
                </template>
              </a-form-model-item>
            </div>
            <div style="width: 120px">
              <a-form-model-item prop="option" label="">
                <w-select
                  :options="fieldOptions"
                  placeholder="请选择"
                  :allowClear="false"
                  v-model="formData.option"
                  :formData="formData"
                  formDataFieldName="optionName"
                  @change="optionChange"
                />
              </a-form-model-item>
            </div>
          </template>
          <!-- 自定义条件 -->
          <template v-if="formData.type == '5'">
            <div style="width: 550px">
              <a-form-model-item prop="value">
                <template slot="label"></template>
                <a-textarea
                  v-model="formData.value"
                  placeholder="请输入自定义条件"
                  :auto-size="{ minRows: 3, maxRows: 5 }"
                  style="margin-top: 4px"
                />
                <div style="color: var(--w-text-color-light); line-height: var(--w-line-height)">{{ defineTip }}</div>
              </a-form-model-item>
            </div>
          </template>
          <div style="width: 100px">
            <a-form-model-item prop="rBracket">
              <template slot="label"></template>
              <w-select :options="conditionRBracket" v-model="formData.rBracket" />
            </a-form-model-item>
          </div>
        </div>
      </a-form-model>
    </a-modal>
  </div>
</template>

<script type="text/babel">
import { fetchJobRankTree } from '../api';
import { filter, map, each, findIndex, isInteger, assignIn } from 'lodash';
import OrgSelect from '@admin/app/web/lib/org-select.vue';
import { deepClone } from '@framework/vue/utils/util';
import {
  conditionLogicType,
  conditionDutyData,
  conditionAndOr,
  conditionLBracket,
  conditionRBracket,
  conditionGroupType,
  logicalOperators,
  levelOperateors
} from '../designer/constant';
import DyformFieldsTreeSelect from './dyform-fields-tree-select.vue';
import WSelect from '../components/w-select';
import NodeTaskSelect from './node-task-select';
import mainDyformFieldsSelect from './main-dyform-fields-select';
import UserSelect from './user-select.vue';
import UserSelectList from './user-select-list.vue';
import WTreeSelect from '../components/w-tree-select';

export default {
  name: 'WfConditionSetting',
  inject: ['designer', 'graph'],
  components: {
    OrgSelect,
    DyformFieldsTreeSelect,
    NodeTaskSelect,
    mainDyformFieldsSelect,
    WSelect,
    UserSelect,
    UserSelectList,
    WTreeSelect
  },
  props: {
    text: {
      type: String,
      default: '前置条件'
    },
    value: {
      type: Array
    },
    types: {
      type: Array,
      default: () => {
        return ['1', '3', '4', '5', '6'];
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
    },
    isTask: {
      type: Boolean,
      default: false
    }
  },
  data() {
    let validateBracket = (rule, value, callback) => {
      callback();
      // if (rule.field == 'lBracket') {
      //   if (!value && this.formData.rBracket) {
      //     callback(new Error('请选择！'));
      //   } else {
      //     callback();
      //   }
      // } else {
      //   if (!value && this.formData.lBracket) {
      //     callback(new Error('请选择！'));
      //   } else {
      //     callback();
      //   }
      // }
    };
    let dataList = deepClone(this.value);
    let initFormData = {
      option: '',
      param: '',
      field: '',
      operator: '',
      value: '',
      andOr: '',
      rBracket: '',
      lBracket: '',
      type: '1',
      andOr: ''
    };
    return {
      validateBracket,
      levelOperateors,
      conditionDutyData,
      conditionGroupType,
      conditionLBracket,
      conditionRBracket,
      logicalOperators,
      conditionAndOr,
      dataList,
      visible: false,
      initFormData,
      formData: deepClone(initFormData), // 弹框内容
      currentIndex: -1,
      title: '前置条件',
      fieldOptions: [
        { label: '常量', value: '1' },
        { label: '字段值', value: '2' }
      ],
      orgOptions: [
        { label: '组织', value: '1' },
        { label: '表单字段值', value: '2' }
      ],
      positionOptions: [
        {
          id: 'PositionValue',
          text: '意见立场值'
        }
      ],
      positionOperateor: [
        {
          id: '==',
          text: '等于'
        },
        {
          id: '!=',
          text: '不等于'
        }
      ],
      JobRankOptions: [],
      defineTip:
        '动态表单变量使用${dyform.表单字段}。变量支持>、>=、<、<=、==、!=、contains、notcontains等操作。字符串常量用单引号括起来，多个条件用逻辑与&&、逻辑或||、左右括号等组合。'
    };
  },
  filters: {},
  computed: {
    typeOption() {
      let options = [];
      each(conditionLogicType, item => {
        let index = this.types.indexOf(item.value);
        if (index > -1) {
          options.push(item);
        }
      });
      if (!this.designer.flowDefinition.enabledJobDuty) {
        options = options.filter(item => {
          return item.value !== conditionLogicType[4]['value'];
        });
      }
      return options;
    },
    voteOptions() {
      let options = [];
      let taskData = this.graph.instance.tasksData;
      each(taskData, item => {
        let optNames = item.optNames;
        if (optNames) {
          each(optNames, citem => {
            options.push({
              id: citem.value,
              text: citem.argValue + '|' + citem.value
            });
          });
        }
      });
      return options;
    },
    rules() {
      let rules = {
        option: { required: true, message: '请选择！', trigger: ['blur', 'change'] },
        param: { required: true, message: '请选择！', trigger: ['blur', 'change'] },
        operator: { required: true, message: '请选择操作符！', trigger: ['blur', 'change'] },
        value: { required: true, message: '必填！', trigger: ['blur', 'change'] },
        rBracket: { required: true, validator: this.validateBracket, trigger: ['blur', 'change'] },
        lBracket: { required: true, validator: this.validateBracket, trigger: ['blur', 'change'] },
        field: { required: true, message: '请选择字段！', trigger: ['blur', 'change'] }
      };
      if (this.formData.type == '4') {
        rules.andOr = { required: true, message: '请选择！', trigger: ['blur', 'change'] };
      }
      return rules;
    },
    logicalOperatorsNotLike() {
      let operators = [];
      each(logicalOperators, item => {
        // 包含 不包含
        if (['14', '15'].indexOf(item.id) == -1) {
          operators.push(item);
        }
      });
      return operators;
    }
  },
  beforeCreate() {},
  created() {
    this.queryJobRankSelect();
  },
  beforeMount() {},
  mounted() {},
  methods: {
    // 职级常量下拉
    queryJobRankSelect() {
      fetchJobRankTree().then(data => {
        data.forEach(item => {
          if (item.parent) {
            item.selectable = false;
          }
          if (item.children) {
            item.children.forEach(citem => {
              if (citem.parent) {
                citem.selectable = false;
              }
            });
          }
        });

        this.JobRankOptions = data;
      });
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
      if (!cdata.data) {
        let type = cdata.type ? Number(cdata.type) : 1;
        type = Math.log2(type) + 1 + '';
        cdata.data = {
          type: type
        };
        let value = decodeURIComponent(data.value);
        if (type == '4') {
          cdata.data.andOr = value.trim();
        }
      } else {
        cdata.data = JSON.parse(cdata.data);
      }
      this.formData = this.setDetailFormData(cdata.data);
      this.title = this.text;
      this.visible = true;
      this.$nextTick(() => {
        this.$refs.form.clearValidate();
      });
    },
    delFormData(data, index) {
      this.dataList.splice(index, 1);
      this.$emit('input', this.dataList);
      this.$emit('change', this.dataList);
    },
    setDetailFormData(data, isClear) {
      if (isClear) {
        data = assignIn({}, deepClone(this.initFormData), data);
        data.andOr = this.formData.andOr || '';
        data.option = '1';
        data.value = this.optionChange(1);
      }
      return data;
    },
    titleSet(text) {
      if (text && text.indexOf('|') > -1) {
        let _text = text.split('|');
        return _text[0];
      }
      return text;
    },
    aLogicBuildValue(formData) {
      let lsDLogic = '',
        lsLogic = '',
        value = '',
        argValue = '',
        type = '';
      if (formData.andOr) {
        lsDLogic = formData.andOrName + ' ';
        lsLogic = ' ' + formData.andOr + ' ';
      }
      if (formData.type == '1') {
        type = '1';
        let valueName = formData.option == '1' ? formData.value : formData.valueName;
        argValue =
          lsDLogic + formData.lBracket + '[' + formData.fieldName + '] ' + formData.operatorName + ' ' + valueName + formData.rBracket;
        value =
          lsLogic +
          formData.lBracket +
          formData.field +
          ' ' +
          formData.operator +
          ' ' +
          formData.value +
          ':' +
          formData.option +
          formData.rBracket;
      } else if (formData.type == '2') {
        // 通过投票比例设置条件
        type = '2';
        let valueName = formData.option == '1' ? formData.value + '%' : formData.valueName;
        argValue =
          lsDLogic + formData.lBracket + '[' + formData.paramName + '] ' + formData.operatorName + ' ' + valueName + formData.rBracket;
        value =
          lsLogic +
          formData.lBracket +
          '[VOTE=' +
          formData.param +
          '] ' +
          formData.operator +
          ' ' +
          formData.value +
          ':' +
          formData.option +
          formData.rBracket;
      } else if (formData.type == '3') {
        // 通过办理人归属判断
        type = '4';
        let valueName = formData.valueName;
        let value1 = formData.value;
        if (formData.option == '1') {
          // 组织
          let valueMap = {
            unitLabel: [],
            unitValue: [],
            formField: [],
            tasks: [],
            options: []
            // userCustom: []
          };
          let valueNameMap = {
            unitLabel: [],
            formField: [],
            tasks: [],
            options: []
          };
          formData.value.forEach(item => {
            if (item.type === 1) {
              valueMap.unitValue.push(item.value);
              valueMap.unitLabel.push(this.titleSet(item.argValue));
              valueNameMap.unitLabel.push(this.titleSet(item.argValue));
            } else if (item.type === 2) {
              valueMap.formField.push(item.value);
              valueNameMap.formField.push(`{${item.argValue}}`);
            } else if (item.type === 4) {
              valueMap.tasks.push(item.value);
              valueNameMap.tasks.push(`<${item.argValue}>`);
            } else if (item.type === 8) {
              valueMap.options.push(item.value);
              valueNameMap.options.push(`[${item.argValue}]`);
            }
          });
          value1 = [];
          for (const key in valueMap) {
            value1.push(valueMap[key].join(';'));
          }
          value1 = value1.join(',');

          valueName = [];
          for (const key in valueNameMap) {
            if (valueNameMap[key].length) {
              valueName.push(valueNameMap[key].join(';'));
            }
          }
          valueName = valueName.join(';');
        }
        // formData.param == '1' ? 当前办理人为所选人员之一
        let lsField = formData.param == '1' ? '<CURUSER>' : formData.field;
        let lsDField = formData.param == '1' ? '' : formData.fieldName;
        argValue = lsDLogic + formData.lBracket + '@ISMEMBER("' + lsDField + '","' + valueName + '")' + formData.rBracket;
        value = lsLogic + formData.lBracket + '@ISMEMBER("' + lsField + '","' + value1 + ':' + formData.option + '")' + formData.rBracket;
      } else if (formData.type == '4') {
        type = '8';
        argValue = lsDLogic;
        value = lsLogic;
      } else if (formData.type == '5') {
        type = '16';
        argValue = lsDLogic + formData.lBracket + '[自定义条件]' + formData.value + formData.rBracket;
        value = lsLogic + formData.lBracket + formData.value + formData.rBracket;
      } else if (formData.type == '6') {
        type = '32';
        argValue = lsDLogic + formData.lBracket + '[意见立场] ' + formData.operatorName + ' ' + formData.valueName + formData.rBracket;
        value = lsLogic + formData.lBracket + formData.param + ' ' + formData.operator + ' ' + formData.value + formData.rBracket;
      } else if (formData.type == '7') {
        type = '64';
        let valueName =
          formData.option == '1'
            ? formData.param == 'A1' || formData.param == 'A3'
              ? formData.value
              : formData.valueName
            : formData.valueName;
        argValue =
          lsDLogic + formData.lBracket + '[' + formData.paramName + '] ' + formData.operatorName + ' ' + valueName + formData.rBracket;
        value =
          lsLogic +
          formData.lBracket +
          '@DUTYGRADE("' +
          formData.param +
          ' ' +
          formData.operator +
          ' ' +
          formData.value +
          ':' +
          formData.option +
          '")' +
          formData.rBracket;
      }
      value = encodeURIComponent(value);
      return {
        type,
        argValue,
        value,
        data: JSON.stringify(formData)
      };
    },
    emitValueChange() {
      let value = this.aLogicBuildValue(this.formData);
      if (this.currentIndex == -1) {
        this.dataList.push(value);
      } else {
        this.dataList.splice(this.currentIndex, 1, value);
      }
      this.$emit('input', this.dataList);
      this.$emit('change', this.dataList);
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
      if (type == '6') {
        this.formData.param = this.positionOptions[0]['id'];
      }
    },
    optionChange(value) {
      if (value == '1') {
        if (this.formData.type == '3') {
          this.formData.value = [];
        } else if (['2', '7'].indexOf(this.formData.type) > -1) {
          this.formData.value = 0;
        } else {
          this.formData.value = '';
        }
      } else {
        this.formData.value = '';
      }
      return this.formData.value;
    },
    dutyChange() {
      this.formData.value = '';
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
