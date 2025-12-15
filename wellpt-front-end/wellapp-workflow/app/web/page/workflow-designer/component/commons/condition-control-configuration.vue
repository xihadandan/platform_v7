<template>
  <div>
    <a-form-model-item label="显示">
      <a-switch v-model="configuration[configCode].defaultVisible" />
    </a-form-model-item>
    <a-form-model-item label="条件">
      <div>
        <a-checkbox v-model="configuration[configCode].enable" />
        <template v-if="compact">
          满足
          <a-select
            size="small"
            :options="[
              { label: '全部', value: 'all' },
              { label: '任一', value: 'any' }
            ]"
            style="width: 65px"
            v-model="configuration[configCode].match"
          />
          条件时{{ configuration[configCode].defaultVisible ? '显示' : '隐藏' }}
          <a-button v-if="compact && configuration[configCode].enable" size="small" type="link" @click="addVisibleCondition">
            <Icon type="pticon iconfont icon-ptkj-jiahao"></Icon>
            添加
          </a-button>
        </template>
        <template v-else>满足条件时{{ configuration[configCode].defaultVisible ? '显示' : '隐藏' }}</template>
      </div>
      <template v-if="configuration[configCode].enable">
        <div style="padding: 5px; background: #fff; border-radius: 2px; border: 1px solid #e8e8e8">
          <template v-if="compact && configuration[configCode].conditions != undefined">
            <template v-for="(con, i) in configuration[configCode].conditions">
              <a-row type="flex" :key="'visible_con_' + i" v-if="true">
                <a-col flex="calc(100% - 60px)">
                  <div>
                    <a-input v-if="codeInput" v-model="con.code" :style="{ width: '100%' }"></a-input>
                    <a-select
                      v-else
                      v-model="con.code"
                      :allowClear="true"
                      :showSearch="true"
                      :filterOption="filterOption"
                      :style="{ width: '100%' }"
                    >
                      <slot name="extraAutoCompleteSelectGroup" />
                      <template v-if="underDyformScope && fieldVarOptions.length">
                        <a-select-opt-group>
                          <span slot="label">
                            <a-icon type="code" />
                            表单数据
                          </span>
                          <a-select-option v-for="opt in fieldVarOptions" :key="opt.value" readonly :value="opt.value" :title="opt.label">
                            {{ opt.label }}
                          </a-select-option>
                        </a-select-opt-group>
                      </template>
                      <a-select-opt-group v-if="underDyformScope && subformFieldVarCollectionOptions.length > 0">
                        <span slot="label">
                          <a-icon type="code" />
                          从表数据集
                        </span>
                        <a-select-option
                          v-for="opt in subformFieldVarCollectionOptions"
                          :key="opt.value"
                          readonly
                          :value="opt.value"
                          :title="opt.label"
                        >
                          {{ opt.label }}
                        </a-select-option>
                      </a-select-opt-group>
                      <a-select-opt-group v-if="underDyformScope">
                        <span slot="label">
                          <a-icon type="code" />
                          表单状态
                        </span>
                        <a-select-option readonly value="__DYFORM__.editable" title="表单可编辑">表单可编辑</a-select-option>
                      </a-select-opt-group>
                      <a-select-opt-group v-if="pageParamOptions.length">
                        <span slot="label">
                          <a-icon type="code" />
                          页面参数
                        </span>
                        <a-select-option
                          v-for="(opt, i) in pageParamOptions"
                          :title="opt.label"
                          :value="opt.value"
                          :key="'page_param_opt_' + i"
                        >
                          <div style="width: 220px; display: flex; align-items: center; justify-content: space-between">
                            {{ opt.label }}
                            <a-tag style="float: right">{{ opt.value }}</a-tag>
                          </div>
                        </a-select-option>
                      </a-select-opt-group>
                      <a-select-opt-group>
                        <span slot="label">
                          <a-icon type="code" />
                          用户数据
                        </span>
                        <a-select-option v-for="opt in userDataOptions" :key="opt.value" readonly :value="opt.value" :title="opt.label">
                          {{ opt.label }}
                        </a-select-option>
                      </a-select-opt-group>
                      <a-select-opt-group>
                        <span slot="label">
                          <a-icon type="code" />
                          日期时间
                        </span>
                        <a-select-option v-for="opt in timeDataOptions" :key="opt.value" readonly :value="opt.value" :title="opt.label">
                          {{ opt.label }}
                        </a-select-option>
                      </a-select-opt-group>
                      <a-select-opt-group>
                        <span slot="label">
                          <a-icon type="code" />
                          工作流数据
                        </span>
                        <a-select-option v-for="opt in flowDataOptions" :key="opt.value" readonly :value="opt.value" :title="opt.label">
                          {{ opt.label }}
                        </a-select-option>
                      </a-select-opt-group>
                      <a-select-opt-group>
                        <span slot="label">
                          <a-icon type="code" />
                          其他
                        </span>
                        <a-select-option readonly value="_URL_PARAM_." title="URL地址参数">URL地址参数</a-select-option>
                      </a-select-opt-group>
                    </a-select>
                    <a-input
                      v-if="con.code != undefined && con.code.endsWith('.')"
                      v-model="con.codeValue"
                      :placeholder="'请输入' + { '_URL_PARAM_.': 'URL地址参数' }[con.code]"
                    />
                  </div>
                  <a-input-group compact>
                    <a-select
                      :options="getOperatorOptions(con)"
                      :key="'key_' + con.code + i"
                      v-model="con.operator"
                      :style="{ width: !['true', 'false'].includes(con.operator) ? '50%' : '100%' }"
                    />
                    <a-input :style="{ width: '50%' }" v-model="con.value" v-if="!['true', 'false'].includes(con.operator)" />
                  </a-input-group>
                </a-col>
                <a-col flex="50px" style="align-self: center; text-align: center">
                  <a-button type="link" size="small" @click="configuration[configCode].conditions.splice(i, 1)">
                    <Icon type="pticon iconfont icon-ptkj-shanchu"></Icon>
                  </a-button>
                </a-col>
              </a-row>
              <a-divider style="margin: 7px 0px" v-if="i != configuration[configCode].conditions.length - 1" />
            </template>
          </template>
          <template v-else>
            <div>
              <a-select
                v-model="configuration[configCode].code"
                :allowClear="true"
                :showSearch="true"
                :filterOption="filterOption"
                :style="{ width: '100%' }"
              >
                <slot name="extraAutoCompleteSelectGroup" />
                <template v-if="underDyformScope && fieldVarOptions.length > 0">
                  <a-select-opt-group>
                    <span slot="label">
                      <a-icon type="code" />
                      表单数据
                    </span>
                    <a-select-option v-for="opt in fieldVarOptions" :key="opt.value" readonly :value="opt.value" :title="opt.label">
                      {{ opt.label }}
                    </a-select-option>
                  </a-select-opt-group>
                </template>
                <a-select-opt-group v-if="underDyformScope">
                  <span slot="label">
                    <a-icon type="code" />
                    表单状态
                  </span>
                  <a-select-option readonly value="__DYFORM__.editable" title="表单可编辑">表单可编辑</a-select-option>
                </a-select-opt-group>
                <a-select-opt-group v-if="pageParamOptions.length">
                  <span slot="label">
                    <a-icon type="code" />
                    页面参数
                  </span>
                  <a-select-option v-for="(opt, i) in pageParamOptions" :title="opt.label" :value="opt.value" :key="'page_param_opt_' + i">
                    <div style="width: 220px; display: flex; align-items: center; justify-content: space-between">
                      {{ opt.label }}
                      <a-tag style="float: right">{{ opt.value }}</a-tag>
                    </div>
                  </a-select-option>
                </a-select-opt-group>

                <a-select-opt-group>
                  <span slot="label">
                    <a-icon type="code" />
                    用户数据
                  </span>
                  <a-select-option v-for="opt in userDataOptions" :key="opt.value" readonly :value="opt.value" :title="opt.label">
                    {{ opt.label }}
                  </a-select-option>
                </a-select-opt-group>
                <a-select-opt-group>
                  <span slot="label">
                    <a-icon type="code" />
                    日期时间
                  </span>
                  <a-select-option v-for="opt in timeDataOptions" :key="opt.value" readonly :value="opt.value" :title="opt.label">
                    {{ opt.label }}
                  </a-select-option>
                </a-select-opt-group>
                <a-select-opt-group>
                  <span slot="label">
                    <a-icon type="code" />
                    工作流数据
                  </span>
                  <a-select-option v-for="opt in flowDataOptions" :key="opt.value" readonly :value="opt.value" :title="opt.label">
                    {{ opt.label }}
                  </a-select-option>
                </a-select-opt-group>
                <a-select-opt-group>
                  <span slot="label">
                    <a-icon type="code" />
                    其他
                  </span>
                  <a-select-option readonly value="_URL_PARAM_." title="URL地址参数">URL地址参数</a-select-option>
                </a-select-opt-group>
              </a-select>
              <a-input
                v-if="configuration[configCode].code != undefined && configuration[configCode].code.endsWith('.')"
                v-model="configuration[configCode].codeValue"
                :placeholder="'请输入' + { '_URL_PARAM_.': 'URL地址参数' }[configuration[configCode].code]"
              />

              <a-input-group compact>
                <a-select
                  :options="operatorOptions"
                  v-model="configuration[configCode].operator"
                  :style="{ width: !['true', 'false'].includes(configuration[configCode].operator) ? '50%' : '100%' }"
                />
                <a-input
                  v-model="configuration[configCode].value"
                  :style="{ width: '50%' }"
                  v-if="!['true', 'false'].includes(configuration[configCode].operator)"
                />
              </a-input-group>
            </div>
          </template>
        </div>
      </template>
    </a-form-model-item>
  </div>
</template>

<script>
const operatorOptions = [
  { label: '等于', value: '==' },
  { label: '不等于', value: '!=' },
  { label: '为真', value: 'true' },
  { label: '为假', value: 'false' },
  { label: '大于', value: '>' },
  { label: '大于等于', value: '>=' },
  { label: '小于', value: '<' },
  { label: '小于等于', value: '<=' },
  { label: '包含于', value: 'in' },
  { label: '不包含于', value: 'not in' },
  { label: '包含', value: 'contain' },
  { label: '不包含', value: 'not contain' }
];
import { debounce, deepClone } from 'lodash';
import moment from 'moment';

export default {
  name: 'ConditionControlConfiguration',
  inject: ['pageParams', 'dyform'],
  props: {
    configuration: Object,
    designer: Object,
    widget: Object,
    compact: {
      type: Boolean,
      default: true
    },
    codeRule: Object,
    configCode: String,
    fieldOptionsProp: Array,
    controlDescription: String
  },
  data() {
    let now = moment(this._$SERVER_TIMESTAMP);
    return {
      inputReadonly: false,
      dataSource: [],
      operatorOptions,
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
      ]
    };
  },
  computed: {
    underDyformScope() {
      return this.dyform != undefined || this.fieldOptionsProp !== undefined;
    },
    pageParamOptions() {
      let opt = [];
      if (this.pageParams != undefined) {
        for (let i = 0, len = this.pageParams.length; i < len; i++) {
          opt.push({
            label: this.pageParams[i].name || this.pageParams[i].code,
            value: this.pageParams[i].code
          });
        }
      }
      return opt;
    },
    pageVarOptions() {
      // 页面变量路径
      let options = [];
      if (this.designer.pageVarKeyPaths) {
        let paths = this.designer.pageVarKeyPaths();
        for (let i = 0, len = paths.length; i < len; i++) {
          options.push({ label: paths[i], value: paths[i] });
        }
      }
      return options;
    },
    // 表单字段
    fieldVarOptions() {
      let fieldOptions;
      if (this.fieldOptionsProp) {
        fieldOptions = this.fieldOptionsProp;
      } else if (this.designer.SimpleFieldInfos) {
        fieldOptions = this.designer.SimpleFieldInfos;
      }
      let opt = [];
      if (fieldOptions) {
        // 表单设计
        for (let i = 0, len = fieldOptions.length; i < len; i++) {
          let info = fieldOptions[i];
          opt.push({
            label: info.name,
            value: `_FORM_DATA_.${info.code}`
          });
        }
      }
      return opt;
    },
    subformFieldVarCollectionOptions() {
      let opt = [];
      let WidgetSubforms;
      if (this.designer.definitionVjson && this.designer.definitionVjson.subforms) {
        WidgetSubforms = this.designer.definitionVjson.subforms;
      } else if (this.designer.WidgetSubforms) {
        WidgetSubforms = this.designer.WidgetSubforms;
      }
      if (WidgetSubforms) {
        for (let widget of WidgetSubforms) {
          let { columns, title, formUuid, formName } = widget.configuration;
          for (let c of columns) {
            opt.push({
              label: `${formName} - [字段] ${c.title} 值集合`,
              value: '_SUBFORM_DATA_.`' + formUuid + '`.' + c.dataIndex
            });
          }
          opt.push({
            label: `${formName} - 总记录数`,
            value: '$count(_SUBFORM_DATA_.`' + formUuid + '`)'
          });
        }
      }
      return opt;
    }
  },
  created() {
    if (
      !this.configuration.hasOwnProperty(this.configCode) ||
      (this.configuration[this.configCode] && !Object.keys(this.configuration[this.configCode]).length)
    ) {
      let obj = this.compact
        ? {
            enable: false,
            match: 'all',
            conditions: [{ operator: 'true', code: undefined, value: undefined }]
          }
        : { enable: false, operator: 'true', code: undefined, value: undefined };
      this.$set(this.configuration, this.configCode, obj);
    }
  },
  methods: {
    getOperatorOptions(con) {
      if (con.code) {
        let opt = [];
        for (let i = 0, len = this.operatorOptions.length; i < len; i++) {
          if (con.code.startsWith('_SUBFORM_DATA_.') && ['contain', 'not contain'].includes(this.operatorOptions[i].value)) {
            // 从表字段只能用包含或者包含于
            opt.push(this.operatorOptions[i]);
            continue;
          } else if (con.code.startsWith('$count(') && ['==', '!=', '>', '<', '<=', '>='].includes(this.operatorOptions[i].value)) {
            opt.push(this.operatorOptions[i]);
            continue;
          }
          if (
            this.codeRule &&
            this.codeRule[con.code] &&
            this.codeRule[con.code].allowOperator &&
            this.codeRule[con.code].allowOperator.includes(this.operatorOptions[i].value)
          ) {
            opt.push(this.operatorOptions[i]);
          } else {
            opt.push(this.operatorOptions[i]);
          }
        }
        return opt;
      }
      return this.operatorOptions;
    },
    addVisibleCondition() {
      this.configuration[this.configCode].conditions.push({
        code: undefined,
        operator: true,
        value: undefined
      });
    },

    filterOption(input, option) {
      if (option.componentOptions.tag == 'a-select-option') {
        let { title, value } = option.componentOptions.propsData;
        return value.toLowerCase().indexOf(input.toLowerCase()) >= 0 || (title && title.toLowerCase().indexOf(input.toLowerCase()) >= 0);
      }
      return false;
    },
    cascadeObjectKeyPath(obj, keyPaths, parentKeyPath) {
      for (let k in obj) {
        if (['string', 'number', 'boolean'].includes(typeof obj[k])) {
          keyPaths.push(parentKeyPath ? `${parentKeyPath}.${k}` : k);
        } else if (!Array.isArray(obj[k])) {
          // 非数组对象则继续遍历路径
          this.cascadeObjectKeyPath(obj[k], keyPaths, parentKeyPath ? `${parentKeyPath}.${k}` : k);
        }
      }
    },
    refetchPageVarKeyPaths: debounce(function (v) {
      if (this.designer.pageVarKeyPaths) {
        this.dataSource = this.designer.pageVarKeyPaths();
      }
    }, 500)
  }
};
</script>
