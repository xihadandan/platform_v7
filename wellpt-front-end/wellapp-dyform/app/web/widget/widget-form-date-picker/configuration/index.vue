<template>
  <div>
    <a-form-model
      ref="form"
      :model="widget.configuration"
      :rules="rules"
      labelAlign="left"
      :wrapper-col="{ style: { textAlign: 'right' } }"
    >
      <a-tabs default-active-key="1">
        <a-tab-pane key="1" tab="设置">
          <FieldNameInput :widget="widget" @change="inputNameChange" />
          <FieldCodeInput
            :widget="widget"
            :label="widget.configuration.range ? '开始字段编码' : '字段编码'"
            @change="inputStartCode"
            :formatTranslateValue="v => formatTranslateValue(v, 'start')"
          />

          <template v-if="widget.configuration.range">
            <FieldCodeInput
              :widget="widget"
              label="结束字段编码"
              propName="endDateField"
              @change="inputEndCode"
              :formatTranslateValue="v => formatTranslateValue(v, 'end')"
            />
          </template>
          <div class="item-sub-title" v-if="widget.configuration.range">
            <a-icon type="minus" :rotate="90" :style="{ color: '#1890ff' }" />
            <span>默认时间</span>
          </div>
          <DateFieldDefaultValue
            :isDefault="true"
            :starDefDate="true"
            :label="widget.configuration.range ? '开始时间' : '默认时间'"
            :setting="widget.configuration.defaultValueSetting"
            :configuration="widget.configuration"
          />
          <DateFieldDefaultValue
            v-if="widget.configuration.range"
            label="结束时间"
            :isDefault="true"
            :setting="widget.configuration.endDefaultValueSetting"
            :configuration="widget.configuration"
            @changeDefType="setEndDateCfg"
            @changeFixedDate="setEndDateCfg"
          />
          <a-form-model-item label="计算模式" v-if="showCreateMethod">
            <a-radio-group size="small" v-model="widget.configuration.valueCreateMethod" @change="changeCreateMethod" button-style="solid">
              <a-radio-button value="4">显示时计算</a-radio-button>
              <a-radio-button value="3">提交时计算</a-radio-button>
            </a-radio-group>
          </a-form-model-item>
          <div class="item-sub-title">
            <a-icon type="minus" :rotate="90" :style="{ color: '#1890ff' }" />
            <span>日期格式</span>
          </div>
          <DatePatternValue :configuration="widget.configuration" />
          <a-form-model-item label="默认状态" class="item-lh">
            <a-radio-group size="small" v-model="widget.configuration.defaultDisplayState" button-style="solid">
              <a-radio-button value="edit">可编辑</a-radio-button>
              <a-radio-button value="unedit">不可编辑</a-radio-button>
              <a-radio-button value="hidden">隐藏</a-radio-button>
            </a-radio-group>
          </a-form-model-item>
          <div>
            <a-collapse :bordered="false" expandIconPosition="right">
              <a-collapse-panel key="edit_mode_properties" header="编辑模式属性">
                <a-form-model-item label="显示清空按钮" class="item-lh" :wrapper-col="{ style: { textAlign: 'right' } }">
                  <a-switch checked-children="是" un-checked-children="否" v-model="widget.configuration.clearBtnShow" />
                </a-form-model-item>
                <DateFieldDefaultValue
                  label="最小时间"
                  :designer="designer"
                  :widget="widget"
                  :setting="widget.configuration.minValueSetting"
                  :configuration="widget.configuration"
                />
                <DateFieldDefaultValue
                  label="最大时间"
                  :designer="designer"
                  :widget="widget"
                  weekMax
                  :setting="widget.configuration.maxValueSetting"
                  :configuration="widget.configuration"
                />
              </a-collapse-panel>
              <a-collapse-panel key="un_edit_mode_properties" header="不可编辑模式属性">
                <a-form-model-item label="不可编辑状态" class="item-lh">
                  <a-radio-group size="small" v-model="widget.configuration.uneditableDisplayState" button-style="solid">
                    <a-radio-button value="label">纯文本</a-radio-button>
                    <a-radio-button value="readonly">只读(显示组件样式)</a-radio-button>
                  </a-radio-group>
                </a-form-model-item>
              </a-collapse-panel>
              <a-collapse-panel key="other_properties" header="其它属性">
                <a-form-model-item label="应用于">
                  <FieldApplySelect v-model="widget.configuration.applyToDatas" />
                </a-form-model-item>
                <a-form-model-item>
                  <template slot="label">
                    <label>
                      输入框提示语
                      <a-tooltip placement="topRight" :arrowPointAtCenter="true" v-if="widget.subtype == 'Range'">
                        <span slot="title">【日期时间范围】开始和结束的提示语请用英文分号;分开</span>
                        <a-icon type="exclamation-circle" />
                      </a-tooltip>
                    </label>
                  </template>
                  <a-input placeholder="输入框提示语" v-model="widget.configuration.placeholder" style="width: 100%">
                    <template slot="addonAfter">
                      <WI18nInput :widget="widget" :designer="designer" code="placeholder" v-model="widget.configuration.placeholder" />
                    </template>
                  </a-input>
                </a-form-model-item>
                <a-form-model-item label="描述" :wrapper-col="{ style: { marginTop: '2px' } }">
                  <a-textarea :rows="4" placeholder="请输入内容" v-model="widget.configuration.note" :maxLength="200" />
                  <span class="textLengthShow">{{ widget.configuration.note | textLengthFilter }}/200</span>
                </a-form-model-item>
              </a-collapse-panel>
              <template v-if="designer.terminalType == 'mobile'">
                <a-collapse-panel key="component_style" header="组件样式">
                  <a-form-model-item label="显示边框" class="item-lh" :wrapper-col="{ style: { textAlign: 'right' } }">
                    <a-switch checked-children="是" un-checked-children="否" v-model="widget.configuration.uniConfiguration.bordered" />
                  </a-form-model-item>
                </a-collapse-panel>
              </template>
            </a-collapse>
          </div>
        </a-tab-pane>
        <a-tab-pane key="2" tab="校验规则">
          <ValidateRuleConfiguration :widget="widget" validatorFunction></ValidateRuleConfiguration>
        </a-tab-pane>
        <a-tab-pane key="3" tab="事件设置">
          <WidgetEventConfiguration :widget="widget" :designer="designer"></WidgetEventConfiguration>
        </a-tab-pane>
      </a-tabs>
    </a-form-model>
  </div>
</template>
<style></style>
<script type="text/babel">
import formConfigureMixin from '../../mixin/formConfigure.mixin';
import moment from 'moment';
import DatePatternValue from './components/date-pattern-setting.vue';
import DateFieldDefaultValue from './components/field-default-value.vue';
export default {
  name: 'WidgetFormDatePickerConfiguration',
  mixins: [formConfigureMixin],
  props: {
    widget: Object,
    designer: Object
  },
  components: { DatePatternValue, DateFieldDefaultValue },
  data() {
    return {
      lastEndDateField: this.widget.configuration.endDateField,
      rules: {},
      rulesOptions: {
        name: {
          required: true,
          message: <a-icon type="close-circle" theme="filled" title="字段名称必填" />,
          trigger: ['blur', 'change'],
          whitespace: true
        },
        code: {
          required: true,
          message: <a-icon type="close-circle" theme="filled" title="字段编码必填" />,
          trigger: ['blur', 'change'],
          whitespace: true
        },
        endDateField: {
          required: true,
          message: <a-icon type="close-circle" theme="filled" title="结束字段编码必填" />,
          trigger: ['blur', 'change'],
          whitespace: true
        }
      },
      endDateCfg: {}
    };
  },
  filters: {
    textLengthFilter(text) {
      return text ? text.length : 0;
    }
  },
  computed: {
    showCreateMethod() {
      let isShow = false,
        hasDef = false,
        hasEndDef = false;
      const { valueType, fixedDateValue, systemValue } = this.widget.configuration.defaultValueSetting;
      if (valueType !== 'no' && (systemValue || fixedDateValue)) {
        hasDef = true;
      }
      const endDefaultValueSetting = this.widget.configuration.endDefaultValueSetting;
      if (endDefaultValueSetting) {
        const { valueType, fixedDateValue, systemValue } = endDefaultValueSetting;
        if (valueType !== 'no' && (systemValue || fixedDateValue)) {
          hasEndDef = true;
        }
      }
      if (hasDef || hasEndDef) {
        isShow = true;
      }
      return isShow;
    }
  },
  created() {
    if (!this.widget.configuration.hasOwnProperty('uniConfiguration')) {
      this.$set(this.widget.configuration, 'uniConfiguration', { bordered: false });
    }
    if (this.widget.configuration.range) {
      if (this.widget.column != undefined && this.widget.column.relaColumns && this.widget.configuration.relaFieldConfigures.length == 0) {
        // 初始化关联字段为结束字段
        this.widget.configuration.relaFieldConfigures.push({
          code: this.widget.column.relaColumns[0].column.toLowerCase(),
          name: this.widget.column.relaColumns[0].name || this.widget.column.title + '(结束)'
        });
        this.widget.configuration.endDateField = this.widget.column.relaColumns[0].column.toLowerCase();
      }
      this.endDateCfg = this.widget.configuration.relaFieldConfigures[0] || {};
      this.updateRelaFieldInfo();
    } else {
      this.$delete(this.rulesOptions, 'endDateField');
    }
    this.rules = this.rulesOptions;
  },
  methods: {
    formatTranslateValue(v, key) {
      let code = key == 'start' ? this.widget.configuration.endDateField : this.widget.configuration.code;
      if (code == v) {
        // 编码重复要修改
        if (v.length == 30) {
          v = v.substring(0, 28);
        }
        v = v + '_2';
      }
      return v;
    },
    moment,
    inputNameChange(name) {
      this.endDateCfg.name = name + '(结束)';
      this.updateRelaFieldInfo();
    },
    inputStartCode(value) {
      if (value == this.widget.configuration.endDateField) {
        this.$message.error('与结束字段编码重复');
        this.widget.configuration.code = undefined;
        return;
      }
    },
    inputEndCode(value) {
      if (value == this.widget.configuration.code) {
        this.$message.error('与开始字段编码重复');
        this.widget.configuration.endDateField = undefined;
        return;
      }
      this.endDateCfg.code = value;
      this.endDateCfg.name = this.widget.name + '(结束)';
      this.widget.configuration.relaFieldConfigures = [this.endDateCfg];
      this.updateRelaFieldInfo();
    },
    updateRelaFieldInfo() {
      // 更新字段信息
      for (let i = 0, len = this.designer.SimpleFieldInfos.length; i < len; i++) {
        if (this.designer.SimpleFieldInfos[i].id == this.widget.id) {
          let fieldInfo = this.designer.SimpleFieldInfos[i];
          fieldInfo.relaFields.splice(0, fieldInfo.relaFields.length);
          fieldInfo.relaFields.push({
            code: this.endDateCfg.code,
            name: this.endDateCfg.name
          });

          break;
        }
      }
    },
    setEndDateCfg() {
      const { valueType, fixedDateValue, systemValue } = this.widget.configuration.endDefaultValueSetting;
      if (valueType !== 'no' && (systemValue || fixedDateValue)) {
        this.endDateCfg.hasDefaultValue = true;
        this.endDateCfg.defaultValue = valueType === 'fixed' ? fixedDateValue : systemValue;
        this.endDateCfg.valueCreateMethod = this.widget.configuration.valueCreateMethod;
      } else {
        this.endDateCfg.valueCreateMethod = '4';
        this.endDateCfg.hasDefaultValue = false;
        this.endDateCfg.defaultValue = '';
      }
      this.widget.configuration.relaFieldConfigures = [this.endDateCfg];
    },
    changeCreateMethod(e) {
      const mode = e.target.value;
      this.endDateCfg.valueCreateMethod = mode;
      this.widget.configuration.relaFieldConfigures = [this.endDateCfg];
    },
    onChangeEndDateField(field) {
      let datePickers = this.designer.WidgetFormDatePickers;
      for (let i = 0, len = datePickers.length; i < len; i++) {
        if (datePickers[i].configuration.code == field) {
          // 修改结束日期字段的配置
          datePickers[i].configuration.hidden = true;
          datePickers[i].configuration.asEndDate = true;
          datePickers[i].configuration.datePattern = this.widget.configuration.datePattern;
        }
        if (datePickers[i].configuration.code == this.lastEndDateField) {
          datePickers[i].configuration.hidden = false;
          datePickers[i].configuration.asEndDate = false;
        }
      }
      this.lastEndDateField = field;
    },
    validate() {
      let results = [];
      this.$refs.form.validate((success, msg) => {
        if (!success) {
          for (let k in msg) {
            let message = msg[k][0].message;
            if (typeof message === 'string') {
              results.push(message);
            } else if (message.constructor && message.constructor.name == 'VNode') {
              if (message.data && message.data.attrs && message.data.attrs.title) {
                results.push(message.data.attrs.title);
              }
            }
          }
        }
      });
      return results;
    }
  },
  mounted() {}
};
</script>
