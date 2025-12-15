<template>
  <div>
    <a-form-model ref="form" :model="widget.configuration" labelAlign="left" :wrapper-col="{ style: { textAlign: 'right' } }">
      <a-tabs default-active-key="1">
        <a-tab-pane key="1" tab="设置">
          <a-form-model-item label="名称">
            <a-input v-model="widget.configuration.title" @change="widget.title = widget.configuration.title">
              <template slot="addonAfter">
                <a-switch :checked="widget.configuration.titleHidden !== true" @change="onChangeTitleHidden" title="显示名称"></a-switch>
                <WI18nInput
                  v-show="widget.configuration.titleHidden !== true"
                  :widget="widget"
                  :designer="designer"
                  :code="widget.id"
                  v-model="widget.configuration.title"
                />
              </template>
            </a-input>
          </a-form-model-item>
          <a-form-model-item label="编码">
            <a-input v-model="widget.configuration.code" />
          </a-form-model-item>
          <a-form-model-item>
            <template slot="label">
              文字提示
              <a-checkbox v-model="widget.configuration.enableTooltip" />
            </template>
            <template v-if="widget.configuration.enableTooltip">
              <a-radio-group size="small" v-model="widget.configuration.tooltipDisplayType" button-style="solid">
                <a-radio-button value="popover">气泡卡片</a-radio-button>
                <a-radio-button value="tooltip">气泡浮层</a-radio-button>
              </a-radio-group>
              <a-input placeholder="文字提示内容" style="width: 100%; float: right" v-model="widget.configuration.tooltip" allow-clear>
                <template slot="addonAfter">
                  <WI18nInput :widget="widget" :designer="designer" :code="widget.id + '_tooltip'" v-model="widget.configuration.tooltip" />
                </template>
              </a-input>
            </template>
          </a-form-model-item>
          <a-form-model-item label="默认状态">
            <a-radio-group size="small" v-model="widget.configuration.defaultDisplayState" button-style="solid">
              <a-radio-button value="edit">可编辑</a-radio-button>
              <a-radio-button value="unedit">不可编辑</a-radio-button>
              <!-- <a-radio-button value="hidden">隐藏</a-radio-button> -->
            </a-radio-group>
          </a-form-model-item>
          <a-form-model-item label="不可编辑状态" class="item-lh" v-if="widget.configuration.defaultDisplayState == 'unedit'">
            <a-radio-group size="small" v-model="widget.configuration.uneditableDisplayState" button-style="solid">
              <a-radio-button value="label">纯文本</a-radio-button>
              <a-radio-button value="readonly">只读(显示组件样式)</a-radio-button>
            </a-radio-group>
          </a-form-model-item>
          <a-form-model-item label="日期类型">
            <a-select v-model="widget.configuration.datePatternType" style="margin-right: 8px" @change="onSelectTypeChange">
              <a-select-option v-for="item in datePatternTypeOptions" :key="item.key">
                {{ item.label }}
              </a-select-option>
            </a-select>
          </a-form-model-item>
          <a-form-model-item label="格式">
            <a-select v-model="datePattern" @change="onSelectChange">
              <a-select-option v-for="item in datePatternOptions[widget.configuration.datePatternType]" :key="item.key" :value="item.key0">
                {{ item.label }}
              </a-select-option>
            </a-select>
          </a-form-model-item>
          <DateFieldDefaultValue label="最小时间" :setting="widget.configuration.minValueSetting" :configuration="widget.configuration" />
          <DateFieldDefaultValue label="最大时间" :setting="widget.configuration.maxValueSetting" :configuration="widget.configuration" />

          <DefaultVisibleConfiguration compact :designer="designer" :configuration="widget.configuration" :widget="widget">
            <template slot="extraAutoCompleteSelectGroup">
              <a-select-opt-group>
                <span slot="label">
                  <a-icon type="code" />
                  表单数据
                </span>
                <a-select-option v-for="opt in formVarOptions" :key="opt.value" :title="opt.label">{{ opt.label }}</a-select-option>
              </a-select-opt-group>
            </template>
          </DefaultVisibleConfiguration>
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
import { getTypeOptions, getOptions } from '@dyformWidget/widget-form-date-picker/configuration/components/date-pattern-options.js';
import editFormElementConfigureMixin from '../../editFormElementConfigure.mixin';
import { find } from 'lodash';
import DateFieldDefaultValue from './components/field-default-value.vue';
export default {
  name: 'WidgetDatepickerConfiguration',
  mixins: [editFormElementConfigureMixin],
  props: {
    widget: Object,
    designer: Object
  },

  data() {
    return { datePattern: undefined, datePatternTypeOptions: getTypeOptions(), datePatternOptions: getOptions() };
  },
  filters: {},
  beforeCreate() {},
  components: { DateFieldDefaultValue },
  computed: {},
  created() {
    if (!this.widget.configuration.hasOwnProperty('datePatternJson') || !this.widget.configuration.datePatternJson.key) {
      this.$set(this.widget.configuration, 'datePatternJson', {});
      this.onSelectChange(this.widget.configuration.datePattern);
    }
    this.datePattern = this.widget.configuration.datePattern;
    if (!this.widget.configuration.hasOwnProperty('minValueSetting')) {
      this.$set(this.widget.configuration, 'minValueSetting', {
        valueType: 'no',
        fixedDateValue: null,
        systemValue: '',
        offset: '',
        offsetUnit: 'day',
        offsetType: 'weekday'
      });

      this.$set(this.widget.configuration, 'maxValueSetting', {
        valueType: 'no',
        fixedDateValue: null,
        systemValue: '',
        offset: '',
        offsetUnit: 'day',
        offsetType: 'weekday'
      });
    }
  },
  methods: {
    // 选择格式
    onSelectChange(v) {
      let datePatternJson = find(this.datePatternOptions[this.widget.configuration.datePatternType], { key0: v });
      let datePattern = datePatternJson.key0;
      if (!this.widget.configuration.zeroShow) {
        datePattern = datePatternJson.key;
      }
      this.widget.configuration.datePatternJson = datePatternJson;
      this.widget.configuration.datePattern = datePattern;
    },
    // 选择类型 日期/时间/日期时间
    onSelectTypeChange(v) {
      const datePattern = this.datePatternOptions[v][0].key0;
      this.datePattern = datePattern;
      this.onSelectChange(datePattern);
    }
  },
  mounted() {},
  updated() {},
  configuration() {
    return {
      title: '日期选择',
      code: '',
      defaultDisplayState: 'edit',
      uneditableDisplayState: 'label',
      datePattern: 'yyyy-MM-DD',
      minValueSetting: {
        valueType: 'no',
        fixedDateValue: null,
        systemValue: '',
        offset: '',
        offsetUnit: 'day',
        offsetType: 'weekday'
      },
      maxValueSetting: {
        valueType: 'no',
        fixedDateValue: null,
        systemValue: '',
        offset: '',
        offsetUnit: 'day',
        offsetType: 'weekday'
      },
      datePatternJson: {
        key: 'yyyy-M-D',
        key0: 'yyyy-MM-DD',
        label: 'yyyy-M-D（eg：2022-1-1）',
        label0: 'yyyy-MM-DD（eg：2022-01-01）',
        mode: 'date'
      },
      datePatternType: 'date'
    };
  }
};
</script>
