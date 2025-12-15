<template>
  <div>
    <a-form-model-item label="触发时机" v-if="trigger">
      <a-checkbox-group
        v-model="widget.configuration.validateRule.trigger"
        :options="[
          { label: '失焦时', value: 'blur' },
          { label: '变更时', value: 'change' }
        ]"
        @change="onChange"
      />
      <!-- <a-radio-group v-model="widget.configuration.validateRule.trigger">
        <a-radio value="blur">失焦时</a-radio>
        <a-radio value="change">变更时</a-radio>
      </a-radio-group> -->
    </a-form-model-item>
    <a-form-model-item
      label="字段隐藏时必须校验"
      :label-col="{ span: 10, style: { width: '200px' } }"
      :wrapper-col="{ span: 14, style: { textAlign: 'right' } }"
    >
      <a-switch v-model="widget.configuration.validateRule.validateWhenFieldIsHidden" />
    </a-form-model-item>
    <a-form-model-item label="是否必填" v-show="required">
      <a-tooltip title="数据模型字段非空要求" v-if="requiredDisabled" placement="left">
        <a-switch v-model="widget.configuration.required" :disabled="requiredDisabled" />
      </a-tooltip>
      <a-switch v-else v-model="widget.configuration.required" :disabled="requiredDisabled" />
    </a-form-model-item>
    <div
      class="validate-required-condition"
      v-if="widget.configuration.requiredCondition != undefined && widget.configuration.required && required"
    >
      <a-form-model-item>
        <template slot="label">
          满足
          <a-select
            :options="[
              { label: '全部', value: 'all' },
              { label: '任一', value: 'any' }
            ]"
            style="width: 65px"
            v-model="widget.configuration.requiredCondition.match"
          />
          条件时必填
        </template>
        <a-button
          size="small"
          type="link"
          @click="
            () => {
              widget.configuration.requiredCondition.conditions.push({
                code: undefined,
                value: undefined,
                operator: '=='
              });
            }
          "
        >
          <Icon type="pticon iconfont icon-ptkj-jiahao"></Icon>
          添加条件
        </a-button>
      </a-form-model-item>
      <div
        style="background: #fff; margin-bottom: 12px"
        v-if="widget.configuration.requiredCondition != undefined && widget.configuration.requiredCondition.conditions.length > 0"
      >
        <div style="outline: 1px solid var(--w-border-color-light); padding: 8px 0px 1px 8px; margin: 0 20px; border-radius: 4px">
          <template v-for="(item, i) in widget.configuration.requiredCondition.conditions">
            <a-row type="flex" :key="'requireCon_' + i" style="margin-bottom: 8px; flex-wrap: nowrap">
              <a-col flex="auto">
                <a-input-group compact>
                  <a-select
                    v-if="fieldVarOptions.length > 0"
                    v-model="item.code"
                    :allowClear="true"
                    :showSearch="true"
                    :filterOption="filterOption"
                    :style="{ width: 'calc( 50% - 50px)' }"
                  >
                    <a-select-opt-group>
                      <span slot="label">
                        <a-icon type="code" />
                        表单数据
                      </span>
                      <a-select-option v-for="opt in fieldVarOptions" :key="opt.value" :title="opt.label">{{ opt.label }}</a-select-option>
                    </a-select-opt-group>
                  </a-select>
                  <a-input v-else v-model="item.code" :style="{ width: 'calc( 50% - 50px)' }" />
                  <a-select :options="operatorOptions" v-model="item.operator" :style="{ width: '100px' }" />
                  <a-input
                    v-model="item.value"
                    v-show="!['true', 'false'].includes(item.operator)"
                    :style="{ width: 'calc( 50% - 50px)' }"
                  />
                </a-input-group>
              </a-col>
              <a-col flex="30px">
                <a-button type="link" size="small" @click="removeRequireCondition(i)" title="删除">
                  <Icon type="pticon iconfont icon-ptkj-shanchu"></Icon>
                </a-button>
              </a-col>
            </a-row>
          </template>
        </div>
      </div>
    </div>

    <a-form-model-item label="必填提示" v-show="required && widget.configuration.required">
      <a-input v-model="widget.configuration.requiredMsg">
        <template slot="addonAfter">
          <WI18nInput
            :widget="widget"
            :designer="designer"
            code="validateRuleRequiredMessage"
            :target="widget.configuration.validateRule"
            v-model="widget.configuration.requiredMsg"
          />
        </template>
      </a-input>
    </a-form-model-item>

    <a-form-model-item label="唯一校验" v-if="unique">
      <a-select
        v-model="widget.configuration.validateRule.uniqueType"
        :style="{ width: '100%' }"
        allowClear
        :disabled="uniqueSelectDisabled"
      >
        <a-select-option value="globalUnique">全局唯一</a-select-option>
        <a-select-option value="tenantUnique">租户唯一</a-select-option>
      </a-select>
    </a-form-model-item>
    <a-form-model-item label="唯一提示" v-if="unique && widget.configuration.validateRule.uniqueType != null">
      <a-input placeholder="唯一提示" v-model="widget.configuration.validateRule.uniqueMsg">
        <template slot="addonAfter">
          <WI18nInput
            :widget="widget"
            :designer="designer"
            code="validateRuleDataExistError"
            :target="widget.configuration.validateRule"
            v-model="widget.configuration.validateRule.uniqueMsg"
          />
        </template>
      </a-input>
    </a-form-model-item>

    <a-form-model-item label="校验规则" v-if="regExp">
      <a-select v-model="widget.configuration.validateRule.regExp.type" allowClear @change="onRegExpChange">
        <a-select-option value="identity">身份证</a-select-option>
        <a-select-option value="telephoneNumber">固定电话</a-select-option>
        <a-select-option value="cellphoneNumber">手机号码</a-select-option>
        <a-select-option value="postCode">邮政编码</a-select-option>
        <a-select-option value="url">网址</a-select-option>
        <a-select-option value="email">邮箱</a-select-option>
        <a-select-option value="chinese">中文</a-select-option>
        <a-select-option value="selfDefine">自定义</a-select-option>
      </a-select>
      <a-input
        v-show="widget.configuration.validateRule.regExp.type != null"
        v-model="widget.configuration.validateRule.regExp.value"
        :readOnly="regExpReadOnly"
      >
        <a-icon
          v-if="widget.configuration.validateRule.regExp.type != 'selfDefine'"
          slot="prefix"
          :type="regExpReadOnly ? 'lock' : 'unlock'"
          @click="onClickEditRegExp"
          :title="regExpReadOnly ? '解锁' : '锁定'"
        />

        <a-icon slot="addonAfter" type="bug" @click="showTestRegExpInput = true" title="验证" />
      </a-input>
      <a-input v-show="showTestRegExpInput" v-model="testRegExpContent" @change="onInputTestRegExp">
        <a-icon
          :style="{ color: testRegExpResult ? '#52c41a' : '#f5222d' }"
          slot="suffix"
          :type="testRegExpResult != null ? (testRegExpResult ? 'check-circle' : 'close-circle') : ''"
        />
      </a-input>
    </a-form-model-item>
    <a-form-model-item label="规则提示" v-if="regExp">
      <a-input v-model="widget.configuration.validateRule.errorMsg">
        <template slot="addonAfter">
          <WI18nInput
            :widget="widget"
            :designer="designer"
            code="validateRuleDataFormateError"
            :target="widget.configuration.validateRule"
            v-model="widget.configuration.validateRule.errorMsg"
          />
        </template>
      </a-input>
    </a-form-model-item>
    <a-form-model-item label="校验函数" v-if="validatorFunction">
      <WidgetCodeEditor
        @save="value => (widget.configuration.validateRule.script = value)"
        :value="widget.configuration.validateRule.script"
      >
        <template slot="default">
          <a-button icon="code" size="small" :type="widget.configuration.validateRule.script ? 'primary' : 'link'">编写代码</a-button>
        </template>
        <template slot="help">
          <div style="line-height: 32px">
            必须使用
            <a-tag>callback</a-tag>
            函数返回校验结果，例如：
          </div>
          <ul style="line-height: 32px">
            <li>
              校验通过，
              <a-tag>callback();</a-tag>
            </li>
            <li>
              校验不通过，
              <a-tag>callback('这是校验不通过的提示信息');</a-tag>
            </li>
          </ul>
        </template>
      </WidgetCodeEditor>
    </a-form-model-item>
  </div>
</template>
<style></style>
<script type="text/babel">
import { commonRegExp } from './constant';
import { debounce } from 'lodash';

export default {
  name: 'ValidateRuleConfiguration',
  props: {
    widget: Object,
    required: {
      // 必填性规则
      type: Boolean,
      default: true
    },
    unique: {
      // 唯一性规则
      type: Boolean,
      default: false
    },
    trigger: {
      type: Boolean,
      default: false
    },
    regExp: {
      type: Boolean,
      default: false
    },
    defaultFieldVarOptions: {
      type: Array
    },
    validatorFunction: {
      type: Boolean,
      default: false
    }
  },
  inject: ['designer'],
  data() {
    let regExpReadOnly = this.widget.configuration.validateRule.regExp.type != 'selfDefine';
    return {
      commonRegExp,
      regExpReadOnly,
      showTestRegExpInput: false,
      testRegExpResult: null,
      testRegExpContent: '',
      requiredDisabled: this.widget.column != undefined && this.widget.column.notNull === true, // 数据模型的要求必填，则表单不允许修改未非必填
      uniqueSelectDisabled:
        this.widget.column != undefined && (this.widget.column.unique == 'GLOBAL' || this.widget.column.unique == 'TENANT'),
      operatorOptions: [
        { label: '等于', value: '==' },
        { label: '不等于', value: '!=' },
        { label: '为真', value: 'true' },
        { label: '为假', value: 'false' },
        { label: '包含于', value: 'in' },
        { label: '不包含于', value: 'not in' },
        { label: '包含', value: 'contain' },
        { label: '不包含', value: 'not contain' }
      ]
    };
  },

  beforeCreate() {},
  components: {},
  computed: {
    fieldVarOptions() {
      let opt = [];
      if (this.designer.SimpleFieldInfos != undefined) {
        for (let i = 0, len = this.designer.SimpleFieldInfos.length; i < len; i++) {
          let info = this.designer.SimpleFieldInfos[i];
          opt.push({
            label: info.name,
            value: info.code
          });
        }
      }

      return opt;
    }
  },
  created() {
    if (!this.widget.configuration.hasOwnProperty('validateRule')) {
      this.$set(this.widget.configuration, 'validateRule', { trigger: ['change'], regExp: {}, validateWhenFieldIsHidden: false });
    }
    if (!this.widget.configuration.hasOwnProperty('requiredCondition')) {
      this.$set(this.widget.configuration, 'requiredCondition', {
        match: 'all', // all 、any
        conditions: []
      });
    }
  },
  methods: {
    onChange() {},
    removeRequireCondition(i) {
      this.widget.configuration.requiredCondition.conditions.splice(i, 1);
    },
    filterOption(input, option) {
      if (option.componentOptions.tag == 'a-select-option') {
        let { title, value } = option.componentOptions.propsData;
        return value.toLowerCase().indexOf(input.toLowerCase()) >= 0 || (title && title.toLowerCase().indexOf(input.toLowerCase()) >= 0);
      }
      return false;
    },
    onInputTestRegExp: debounce(function (v) {
      let value = this.widget.configuration.validateRule.regExp.value;
      let start = value.indexOf('/'),
        end = value.lastIndexOf('/'),
        regExp;
      if (start === 0) {
        regExp = end == value.length - 1 ? new RegExp(value.substring(1, end)) : new RegExp(value.substring(1, end), value.substr(end + 1));
      } else {
        regExp = new RegExp(value);
      }
      this.testRegExpResult = regExp.test(this.testRegExpContent);
    }, 300),

    onClickEditRegExp() {
      this.regExpReadOnly = !this.regExpReadOnly;
    },
    onRegExpChange(v) {
      this.widget.configuration.validateRule.regExp.value = null;
      this.regExpReadOnly = v !== 'selfDefine';
      this.testRegExpResult = null;
      if (v && v !== 'selfDefine') {
        this.widget.configuration.validateRule.regExp.value = this.commonRegExp[v].toString();
      }
    }
  },
  mounted() {}
};
</script>
