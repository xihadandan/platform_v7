<template>
  <div class="ant-select-selection ant-select-selection--single" v-if="configuration.valueOption != undefined">
    <div
      class="ant-select-selection__rendered"
      :style="{
        marginRight: selectedValue.valueType ? '80px' : '24px'
      }"
    >
      <a-input
        v-if="configuration.valueOption.valueType == 'constant' || configuration.valueOption.valueType == 'jsonpath'"
        v-model="configuration.valueOption.value"
        size="small"
        style="border: unset; box-shadow: none; padding: 0px"
        placeholder="请输入值"
      />
      <a-tag
        class="primary-color"
        v-else-if="configuration.valueOption.valueType == 'variable' && configuration.valueOption.value != undefined"
      >
        {{ selectedVarName }}
      </a-tag>
    </div>
    <div class="ant-select-arrow" style="right: 5px">
      <Modal title="值设置" :ok="onConfirmSelectValue" :cancel="onCancelSelectValue" ref="modal" :container="getContainer" :zIndex="1000">
        <template slot="content">
          <a-form-model :colon="false">
            <a-form-model-item>
              <a-radio-group slot="label" v-model="selectedValue.valueType" button-style="solid">
                <a-radio-button value="constant">常量</a-radio-button>
                <a-radio-button value="variable">变量</a-radio-button>
                <a-radio-button value="jsonpath" v-if="jsonPaths != undefined">JSON路径值</a-radio-button>
              </a-radio-group>
              <a-input
                v-model="selectedValue.constant"
                v-if="selectedValue.valueType === 'constant'"
                placeholder="请输入常量值"
                allow-clear
              />
              <a-select
                v-model="selectedValue.value"
                v-if="selectedValue.valueType === 'variable'"
                placeholder="请选择变量"
                allow-clear
                :showSearch="true"
                :filterOption="filterSelectOption"
              >
                <a-select-opt-group label="表单字段">
                  <a-select-option v-for="(item, index) in dyformFieldVarOptions" :key="'dyform-field' + index" :value="item.value">
                    {{ item.label }}
                  </a-select-option>
                </a-select-opt-group>

                <a-select-opt-group label="用户数据">
                  <a-select-option v-for="(item, index) in userDataOptions" :key="'user-field' + index" :value="item.value">
                    {{ item.label }}
                  </a-select-option>
                </a-select-opt-group>

                <a-select-opt-group label="工作流数据">
                  <a-select-option v-for="(item, index) in flowDataOptions" :key="'flow-field' + index" :value="item.value">
                    {{ item.label }}
                  </a-select-option>
                </a-select-opt-group>

                <a-select-opt-group label="日期时间">
                  <a-select-option v-for="(item, index) in timeDataOptions" :key="'timedate-field' + index" :value="item.value">
                    {{ item.label }}
                  </a-select-option>
                </a-select-opt-group>
              </a-select>
              <a-alert type="info" show-icon v-if="selectedValue.valueType === 'jsonpath'">
                <template slot="message">可以输入JSON路径取值, 例如: data.name</template>
              </a-alert>

              <a-auto-complete
                :dropdown-match-select-width="false"
                :dropdown-style="{ width: '300px' }"
                style="width: 100%"
                optionLabelProp="value"
                v-model="selectedValue.jsonPathValue"
                @change="onAutoCompleteChange"
                @blur="onAutoCompleteBlur"
                @dropdownVisibleChange="onAutoCompleteVisibleChange"
                @select="onAutoCompleteSelect"
                v-if="selectedValue.valueType === 'jsonpath' && jsonPaths != undefined"
                :allowClear="true"
              >
                <template slot="dataSource">
                  <template v-for="(p, i) in jsonPaths">
                    <a-select-option
                      :key="p.value"
                      v-if="
                        !autoCompleteSearch ||
                        (autoCompleteSearch && p.value.toLowerCase().indexOf(selectedValue.jsonPathValue.toLowerCase()) > -1)
                      "
                    >
                      {{ p.value }}
                      <div v-if="p.description" style="color: #999; font-size: 12px">{{ p.description }}</div>
                    </a-select-option>
                  </template>
                </template>
              </a-auto-complete>
            </a-form-model-item>
            <a-form-model-item>
              <a-radio-group slot="label" v-model="selectedValue.formatter.type" button-style="solid">
                <a-radio-button value="selfDefine">自定义值格式化函数</a-radio-button>
                <a-radio-button value="useFormatter">使用内置值格式化函数</a-radio-button>
              </a-radio-group>
              <a-select
                v-if="selectedValue.formatter.type == 'useFormatter'"
                v-model="selectedValue.formatter.use"
                :filterOption="filterSelectOption"
                style="width: 100%"
                :showSearch="true"
                allow-clear
                @change="onChangeSelectFormatter"
              >
                <a-select-opt-group v-for="(group, i) in commonFormatter" :label="group.label" :key="'formattergroup_' + i">
                  <a-select-option v-for="(item, j) in group.functions" :key="'group_' + i + '_' + j" :value="item.value">
                    {{ item.label }}
                  </a-select-option>
                </a-select-opt-group>
              </a-select>
              <template v-else>
                <a-alert type="info" style="margin-bottom: 8px">
                  <template slot="description">
                    <p>入参说明:</p>
                    <ul>
                      <li>value: 上述JSON路径取值</li>
                      <li>lodash: 在函数内可以使用 lodash v4.17.x版本工具类</li>
                    </ul>
                    <p>通过代码 return 返回值, 例如: return value.toLowerCase(); // 将结果输出为小写</p>
                  </template>
                </a-alert>
                <WidgetCodeEditor v-model="selectedValue.formatter.function" width="auto" height="300px" lang="js"></WidgetCodeEditor>
              </template>
            </a-form-model-item>
            <template
              v-if="
                selectedValue.formatter.type === 'useFormatter' &&
                selectedValue.formatter.use != undefined &&
                valueFormatterOptionNames.includes(capitalize(selectedValue.formatter.use) + 'FormatterOption')
              "
            >
              <a-divider orientation="left">格式选项</a-divider>
              <component :is="capitalize(selectedValue.formatter.use) + 'FormatterOption'" :options="selectedValue.formatter.options" />
            </template>
          </a-form-model>
        </template>
        <a-tag
          v-if="selectedValue.valueType != undefined"
          style="margin-right: 0px; font-size: 9px; padding: 0px 5px; height: 20px; line-height: 20px"
          :color="selectedValue.valueType == 'variable' ? 'green' : selectedValue.valueType == 'jsonpath' ? 'purple' : undefined"
        >
          {{ selectedValueTypeLabel }}
        </a-tag>
        <a-icon v-else type="setting" />
        <a-icon type="down" />
      </Modal>
    </div>
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
import Modal from '@pageAssembly/app/web/lib/modal.vue';
import moment from 'moment';
import { filterSelectOption } from '@framework/vue/utils/function.js';
import ValueFormatterOptions from './value-formatter-option/index';
import { capitalize } from 'lodash';
import WidgetCodeEditor from '@pageWidget/commons/widget-code-editor.vue';

export default {
  name: 'valueOption',
  props: {
    configuration: Object,
    designer: Object,
    jsonPaths: Array
  },
  components: { Modal, ...ValueFormatterOptions, WidgetCodeEditor },
  computed: {
    // 表单字段
    dyformFieldVarOptions() {
      let opt = [];
      if (this.designer.SimpleFieldInfos) {
        // 表单设计
        for (let i = 0, len = this.designer.SimpleFieldInfos.length; i < len; i++) {
          let info = this.designer.SimpleFieldInfos[i];
          opt.push({
            label: info.name,
            value: `_FORM_DATA_.${info.code}`
          });
        }
      }
      return opt;
    },
    selectedVarName() {
      if (
        this.configuration.valueOption != undefined &&
        this.configuration.valueOption.value != undefined &&
        this.configuration.valueOption.valueType == 'variable'
      ) {
        let prefix = this.configuration.valueOption.value.split('.')[0];
        let map = {
          _FORM_DATA_: this.dyformFieldVarOptions,
          _USER_: this.userDataOptions,
          _DATETIME_: this.timeDataOptions,
          _WORKFLOW_: this.flowDataOptions
        };
        if (map[prefix]) {
          for (let opt of map[prefix]) {
            if (opt.value == this.configuration.valueOption.value) {
              let prefixLabel = {
                _FORM_DATA_: '表单字段: ',
                _USER_: '用户数据: ',
                _DATETIME_: '时间数据: ',
                _WORKFLOW_: '流程数据: '
              }[prefix];
              return prefixLabel + opt.label;
            }
          }
        }
      }
      return undefined;
    },
    selectedValueTypeLabel() {
      return this.selectedValue.valueType
        ? {
            constant: '常量',
            variable: '变量',
            jsonpath: 'JSON路径'
          }[this.selectedValue.valueType]
        : undefined;
    }
  },
  data() {
    let now = moment(this._$SERVER_TIMESTAMP);
    console.log('jsonpaths', this.jsonPaths);
    return {
      valueFormatterOptionNames: Object.keys(ValueFormatterOptions),
      userDataOptions: [
        { label: '用户名', value: '_USER_.userName' },
        { label: '用户ID', value: '_USER_.userId' },
        { label: '登录账号', value: '_USER_.loginName' },
        { label: '用户包含角色ID集合', value: '_USER_.roles' },
        { label: '用户主部门ID', value: '_USER_.mainDeptId' },
        { label: '用户主部门ID路径', value: '_USER_.mainDeptIdPath' },
        { label: '用户主部门名称', value: '_USER_.mainDeptName' },
        { label: '用户主部门名称路径', value: '_USER_.mainDeptNamePath' },
        { label: '用户主职位ID', value: '_USER_.mainJobId' },
        { label: '用户主职位ID路径', value: '_USER_.mainJobIdPath' },
        { label: '用户主职位名称', value: '_USER_.mainJobName' },
        { label: '用户主职位名称路径', value: '_USER_.mainJobNamePath' },
        { label: '用户单位ID', value: '_USER_.unitId' },
        { label: '用户单位名称', value: '_USER_.unitName' },
        { label: '用户职位ID集合', value: '_USER_.jobIds' },
        { label: '用户职位名称集合', value: '_USER_.jobNames' },
        { label: '用户部门ID集合', value: '_USER_.deptIds' },
        { label: '用户部门名称集合', value: '_USER_.deptNames' }
      ],
      timeDataOptions: [
        { label: `当前日期数值(${now.format('YYYYMMDD')})`, value: '_DATETIME_.currentDateString' },
        { label: `当前日期时间数值(${now.format('YYYYMMDDHHmmss')})`, value: '_DATETIME_.currentFullDateTimeString' },
        { label: '当前星期几(1~7)', value: '_DATETIME_.currentWeekDay' },
        { label: '当前月份(1~12)', value: '_DATETIME_.currentMonth' },
        { label: '当前季度(1~4)', value: '_DATETIME_.currentQuarter' },
        { label: '当前年份', value: '_DATETIME_.currentYear' },
        { label: '当前日(1~31)', value: '_DATETIME_.currentDay' },
        { label: '当前小时(0~23)', value: '_DATETIME_.currentHour' }
      ],
      flowDataOptions: [
        { label: '当前环节ID', value: '_WORKFLOW_.taskId' },
        { label: '当前环节名称', value: '_WORKFLOW_.taskName' },
        { label: '当前流程ID', value: '_WORKFLOW_.flowDefId' },
        { label: '当前流程版本号', value: '_WORKFLOW_.version' }
      ],
      selectedValue: {
        value: undefined,
        constant: undefined,
        valueType: 'constant',
        jsonPathValue: undefined,
        formatter: {
          type: 'useFormatter',
          use: undefined,
          options: {},
          function: undefined
        }
      },
      commonFormatter: [
        {
          label: '字符串',
          functions: [
            {
              label: '拆分字符串',
              value: 'split'
            },
            {
              label: '转为大写字符串',
              value: 'toUpper'
            },
            {
              label: '转为小写字符串',
              value: 'toLower'
            },
            {
              label: '首字母大写',
              value: 'capitalize'
            },
            {
              label: '转为驼峰格式字符串',
              value: 'camelCase'
            },
            {
              label: '反转字符串',
              value: 'reverse'
            }
          ]
        },
        {
          label: '数组',
          functions: [
            {
              label: '合并为字符串',
              value: 'join'
            },
            {
              label: '反转数组',
              value: 'reverseArray'
            }
          ]
        }
      ],
      autoCompleteSearch: false,
      autoCompleteOpen: false
    };
  },
  beforeCreate() {},
  created() {
    if (!this.configuration.hasOwnProperty('valueOption') || this.configuration.valueOption == undefined) {
      this.$set(this.configuration, 'valueOption', {
        value: undefined,
        formatter: {
          use: undefined,
          options: {},
          function: undefined
        },
        valueType: 'constant' // constant / variable
      });
    }
    this.selectedValue.value = this.configuration.valueOption.valueType == 'variable' ? this.configuration.valueOption.value : undefined;
    this.selectedValue.constant =
      this.configuration.valueOption.valueType === 'constant' ? this.configuration.valueOption.value : undefined;
    this.selectedValue.valueType = this.configuration.valueOption.valueType;
    this.selectedValue.jsonPathValue =
      this.configuration.valueOption.valueType === 'jsonpath' ? this.configuration.valueOption.value : undefined;

    if (!this.configuration.valueOption.hasOwnProperty('formatter')) {
      this.$set(this.configuration.valueOption, 'formatter', {
        use: undefined,
        options: {},
        function: undefined
      });
    }

    this.selectedValue.formatter.type = this.configuration.valueOption.formatter.use == undefined ? 'selfDefine' : 'useFormatter';
    this.selectedValue.formatter.use = this.configuration.valueOption.formatter.use;
    this.selectedValue.formatter.function = this.configuration.valueOption.formatter.function;
  },
  beforeMount() {},
  mounted() {},
  methods: {
    filterAutoCompleteOption(inputValue, option) {
      return option.data && option.data.key.toLowerCase().indexOf(inputValue.toLowerCase()) != -1;
    },
    onAutoCompleteChange(value) {
      if (value != '') {
        this.autoCompleteSearch = true;
      } else {
        this.autoCompleteSearch = false;
      }
    },
    onAutoCompleteBlur() {
      this.autoCompleteSearch = false;
    },
    onAutoCompleteSelect() {
      this.autoCompleteSearch = false;
    },
    onAutoCompleteVisibleChange(open) {
      this.autoCompleteOpen = open;
      if (!this.autoCompleteOpen) {
        this.autoCompleteSearch = false;
      }
    },
    getContainer() {
      return document.querySelector('body');
    },
    onChangeSelectFormatter() {
      this.$set(this.selectedValue.formatter, 'options', {});
    },
    capitalize,
    filterSelectOption,
    onConfirmSelectValue(e) {
      if (this.selectedValue.valueType == 'constant') {
        this.configuration.valueOption.value = this.selectedValue.constant;
        this.selectedValue.value = undefined;
        this.selectedValue.jsonPathValue = undefined;
      } else if (this.selectedValue.valueType == 'variable') {
        this.configuration.valueOption.value = this.selectedValue.value;
        this.selectedValue.constant = undefined;
        this.selectedValue.jsonPathValue = undefined;
      } else if (this.selectedValue.valueType == 'jsonpath') {
        this.configuration.valueOption.value = this.selectedValue.jsonPathValue;
        this.selectedValue.value = undefined;
        this.selectedValue.constant = undefined;
      }
      this.configuration.valueOption.valueType = this.selectedValue.valueType;
      this.configuration.valueOption.formatter.use = this.selectedValue.formatter.use;
      this.configuration.valueOption.formatter.function = this.selectedValue.formatter.function;
      this.configuration.valueOption.formatter.options = this.selectedValue.formatter.options;

      e(true);
    }
  },
  onCancelSelectValue() {
    this.selectedValue.value = this.configuration.valueOption.valueType == 'variable' ? this.configuration.valueOption.value : undefined;
    this.selectedValue.constant =
      this.configuration.valueOption.valueType === 'constant' ? this.configuration.valueOption.value : undefined;
    this.selectedValue.valueType = this.configuration.valueOption.valueType;
    this.selectedValue.jsonPathValue =
      this.configuration.valueOption.valueType === 'jsonpath' ? this.configuration.valueOption.value : undefined;
    this.selectedValue.formatter.type = this.configuration.valueOption.formatter.use == undefined ? 'selfDefine' : 'useFormatter';
    this.selectedValue.formatter.use = this.configuration.valueOption.formatter.use;
    this.selectedValue.formatter.function = this.configuration.valueOption.formatter.function;
  },
  watch: {
    'configuration.valueOption.value': {
      handler(val) {
        if (this.configuration.valueOption.valueType == 'jsonpath') {
          this.selectedValue.jsonPathValue = val;
        }

        if (this.configuration.valueOption.valueType == 'constant') {
          this.selectedValue.constant = val;
        }
      }
    }
  }
};
</script>
