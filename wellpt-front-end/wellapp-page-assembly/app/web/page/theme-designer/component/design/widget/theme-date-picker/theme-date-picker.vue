<template>
  <div class="theme-style-panel">
    <a-page-header title="日期时间">
      <div slot="subTitle">平台表单日期时间</div>
      <!-- 基础样式 -->
      <a-card :class="[selectedKey == 'basicStyle' ? 'border-selected' : '']" :style="vStyle" @click="onClickCard('basicStyle')">
        <template #title>
          基础样式
          <a-button size="small" type="link" icon="undo" @click.stop="undoThemePropConfig('basicStyle')" />
        </template>
        <a-descriptions layout="vertical" :colon="false" :column="2">
          <a-descriptions-item label="默认  Default">
            <a-date-picker class="full-line80" style="width: 280px" />
          </a-descriptions-item>
          <a-descriptions-item label="悬停  Hover">
            <a-date-picker ref="themeDatePickerHoverRef" class="full-line80 theme-date-picker-hover" style="width: 280px" />
          </a-descriptions-item>
          <a-descriptions-item label="焦点  Focus">
            <a-date-picker class="full-line80 theme-date-picker-focus" style="width: 280px" />
          </a-descriptions-item>
          <a-descriptions-item label="禁用  Disable">
            <a-date-picker
              class="full-line80 theme-date-picker-disabled"
              style="width: 280px"
              :default-value="moment('2015-06-06', 'YYYY-MM-DD')"
              :disabled="true"
            />
          </a-descriptions-item>
        </a-descriptions>
      </a-card>
      <!-- 错误提示 -->
      <a-card :class="[selectedKey == 'errorStyle' ? 'border-selected' : '']" :style="vStyle" @click="onClickCard('errorStyle')">
        <template #title>
          错误提示
          <a-button size="small" type="link" icon="undo" @click.stop="undoThemePropConfig('errorStyle')" />
        </template>
        <a-form-model layout="vertical" :colon="false" :model="testForm" :rules="testRules" ref="testForm">
          <a-descriptions layout="vertical" :colon="false" :column="2">
            <a-descriptions-item label="错误提示 Error">
              <a-form-model-item prop="testDatePicker" required class="full-line80">
                <a-date-picker class="full-line80" style="width: 280px" />
              </a-form-model-item>
            </a-descriptions-item>
            <a-descriptions-item label="悬停  Hover">
              <a-form-model-item prop="testDatePicker" required class="full-line80 theme-ant-select-error-hover">
                <a-date-picker class="full-line80 theme-date-picker-error-hover" style="width: 280px" />
              </a-form-model-item>
            </a-descriptions-item>
            <a-descriptions-item label="获取焦点  Focus">
              <a-form-model-item prop="testDatePicker" required class="full-line80">
                <a-date-picker class="full-line80 theme-date-picker-error-focus" style="width: 280px" />
              </a-form-model-item>
            </a-descriptions-item>
            <a-descriptions-item :label="null"></a-descriptions-item>
          </a-descriptions>
        </a-form-model>
      </a-card>
      <!-- 允许清空 -->
      <a-card :class="[selectedKey == 'clearStyle' ? 'border-selected' : '']" :style="vStyle" @click="onClickCard('clearStyle')">
        <template #title>
          允许清空
          <a-button size="small" type="link" icon="undo" @click.stop="undoThemePropConfig('clearStyle')" />
        </template>
        <a-descriptions layout="vertical" :colon="false" :column="2">
          <a-descriptions-item label="清空按钮样式">
            <a-date-picker class="full-line80 theme-date-picker-clear" :default-value="moment()" style="width: 280px" />
          </a-descriptions-item>
          <a-descriptions-item>
            <a-time-picker class="full-line80 theme-date-picker-clear" :default-value="moment()" style="width: 280px" allowClear />
          </a-descriptions-item>
        </a-descriptions>
      </a-card>
      <!-- 选择面板 -->
      <a-card :style="vStyle">
        <template #title>
          选择面板
          <a-button size="small" type="link" icon="undo" @click.stop="undoThemePropConfig('panelStyle')" />
        </template>
        <div
          :class="[selectedKey == 'panelStyle' ? 'border-selected theme-card-item' : 'theme-card-item']"
          @click="onClickCard('panelStyle')"
        >
          <a-descriptions layout="vertical" :colon="false" :column="3" class="theme-date-picker-panel">
            <a-descriptions-item label="选择日期面板">
              <a-date-picker
                class="full-line80"
                style="width: 280px; min-height: 344px"
                :open="true"
                :getCalendarContainer="triggerNode => triggerNode.parentNode"
              />
            </a-descriptions-item>
            <a-descriptions-item label="选择年份面板">
              <a-date-picker
                mode="year"
                class="full-line80"
                style="width: 280px; min-height: 344px"
                :open="true"
                :getCalendarContainer="triggerNode => triggerNode.parentNode"
              />
            </a-descriptions-item>
            <a-descriptions-item label="选择时间面板">
              <a-time-picker
                class="full-line80"
                style="width: 280px"
                :open="true"
                format="HH时mm分"
                :getPopupContainer="triggerNode => triggerNode.parentNode"
              />
            </a-descriptions-item>
          </a-descriptions>
        </div>
      </a-card>
      <!-- 选择样式 -->
      <!-- <a-card
        title="选择样式"
        :style="vStyle"
        >
        <div
          :class="[selectedKey == 'selectedStyle' ? 'border-selected theme-card-item' : 'theme-card-item']"
          @click="onClickCard('selectedStyle')"
        >
          <a-descriptions
            layout="vertical"
            :colon="false"
            :column="2"
            class="theme-date-picker-selected"
          >
            <a-descriptions-item label="年份">
             <a-date-picker
                mode="year"
                class="full-line80"
                style="width:280px; min-height:344px"
                :open="true"
                :getCalendarContainer="(triggerNode) => triggerNode.parentNode"
              />
            </a-descriptions-item>
            <a-descriptions-item label="月份">
            <a-month-picker
              class="full-line80"
              style="width:280px; min-height:344px"
              :open="true"
              :getCalendarContainer="(triggerNode) => triggerNode.parentNode"
            />
            </a-descriptions-item>
          </a-descriptions>
        </div>
      </a-card> -->
    </a-page-header>
  </div>
</template>
<script type="text/babel">
import moment from 'moment';
import themePropMixin from '@framework/vue/mixin/themePropMixin';
import { kebabCase } from 'lodash';

export default {
  name: 'ThemeDatePicker',
  mixins: [themePropMixin],
  props: {
    config: Object
  },
  title: '日期时间',
  category: 'formBasicComp',
  themePropConfig: {
    // 基础样式
    basicStyle: {
      wDateBorderColor: '--w-border-color-base',
      wDateBorderWidth: '--w-border-width-base',
      wDateBorderStyle: '--w-border-style-base',

      wDateBorderRadius: '--w-border-radius-2',

      wDateTextColor: '--w-text-color-dark',
      wDatePlaceholderColor: '--w-text-color-lighter',
      wDateTextSize: '--w-font-size-base',
      wDateTextWeight: '--w-font-weight-regular',

      wDatePickerIconColor: '--w-text-color-light',
      wDatePickerIconSize: '--w-font-size-base',

      wDatePickerInputBg: '--w-fill-color-base',

      wDatePickerIconRight: '--w-margin-xs',

      wDatePickerInputHeight: '--w-height-sm',

      wDateBorderColorHover: '--w-primary-color',
      wDatePickerIconColorHover: '--w-text-color-light',

      wDateBorderColorFocus: '--w-primary-color',
      wDatePickerIconColorFocus: '--w-text-color-light',

      wDateBorderColorDisabled: '--w-border-color-base',
      wDateTextColorDisabled: '--w-text-color-light',
      wDatePickerIconColorDisabled: '--w-text-color-light',
      wDatePickerBgDisabled: '--w-fill-color-light',

      wDatePickerInputBoxShadow: undefined,
      wDatePickerInputBoxShadowHover: undefined,
      wDatePickerInputBoxShadowFocus: undefined
    },
    errorStyle: {
      wDatePickerErrorBorderColor: '--w-danger-color',
      wDatePickerErrorBorderStyle: '--w-border-style-base',
      wDatePickerErrorBorderWidth: '--w-border-width-base',
      wDatePickerErrorTextColor: '--w-danger-color',
      wDatePickerErrorTextSize: '--w-font-size-base',
      wDatePickerErrorTextWeight: '--w-font-weight-regular',
      wDatePickerErrorIconSize: '--w-font-size-base',
      wDatePickerErrorIconColor: '--w-danger-color',
      wDatePickerErrorBorderColorHover: '--w-danger-color',
      wDatePickerErrorBorderColorFocus: '--w-danger-color',
      wDatePickerInputErrorBoxShadow: undefined,
      wDatePickerInputErrorBoxShadowHover: undefined,
      wDatePickerInputErrorBoxShadowFocus: undefined
    },
    clearStyle: {
      wDateClearFontSize: '--w-font-size-base',
      wDateClearFontColor: '--w-text-color-lighter',
      wDateClearRightPadding: undefined,
      wDateClearSizeHover: '--w-font-size-base',
      wDateClearColorHover: '--w-text-color-light',
      wDateClearSizeActive: '--w-font-size-base',
      wDateClearColorActive: '--w-text-color-light'
    },
    panelStyle: {
      wDatePanelBorderColor: '--w-border-color-inverse',
      wDatePanelBorderWidth: '--w-border-width-base',
      wDatePanelBorderStyle: '--w-border-style-base',
      wDatePanelBorderRadius: '--w-border-radius-base',
      wDatePickerPanelBg: '--w-fill-color-base',

      wDatePickerPanelTodayDayColor: '--w-primary-color',
      wDatePickerPanelTodayDayBorderColor: '--w-border-color-inverse',
      wDatePickerPanelDayDateBackground: '--w-fill-color-base',
      wDatePickerPanelColorActive: '--w-gray-color-1',
      wDatePickerPanelBackgroundActive: '--w-primary-color',
      wDatePickerPanelColorHover: '--w-primary-color',
      wDatePickerPanelBackgroundHover: undefined,
      wDatePickerPanelBorderColorHover: '--w-primary-color',
      wDatePickerPanelSelectedColor: '--w-gray-color-1',
      wDatePickerPanelSelectedBackground: '--w-primary-color',
      wDatePickerPanelFooterBtnColor: '--w-primary-color',
      wDatePickerRangePanelSelectedBackground: '--w-primary-color-2',
      wDatePickerPanelButtonPrimaryColor: '--w-gray-color-1',
      wDatePickerPanelButtonPrimaryBackground: '--w-primary-color',
      wDatePickerPanelButtonPrimaryBackgroundFocus: '--w-primary-color-7',
      wDatePickerPanelButtonPrimaryBorderColor: '--w-primary-color'
    }
  },
  computed: {
    vStyle() {
      let colorVars = {},
        keys = ['basicStyle', 'errorStyle', 'panelStyle', 'clearStyle'];
      for (let prop of keys) {
        if (this.config[prop]) {
          for (let key in this.config[prop]) {
            if (this.config[prop][key] != undefined) {
              if (key.toLowerCase().indexOf('boxshadow') != -1) {
                // 阴影样式处理
                colorVars['--' + kebabCase(key)] = this.config[prop][key].startsWith('inset ')
                  ? 'inset var(' + this.config[prop][key].split('inset ')[1] + ')'
                  : `var(${this.config[prop][key]})`;
              } else {
                colorVars['--' + kebabCase(key)] = this.config[prop][key].startsWith('--w-')
                  ? `var(${this.config[prop][key]})`
                  : this.config[prop][key];
              }
            }
          }
        }
      }

      let vars = { ...colorVars, ...this.basicStyleCssVars };
      const themeStyle = {};
      themeStyle[this.$options.name] = vars;
      this.$emit('setThemeStyle', themeStyle);
      return vars;
    }
  },
  data() {
    return {
      selectedKey: 'basicStyle',
      testForm: { testDatePicker: undefined },
      testRules: {
        testDatePicker: [
          {
            required: true,
            message: (
              <span class="ant-calendar-picker-error">
                <a-icon type="warning" /> 必填
              </span>
            )
          }
        ]
      }
    };
  },
  mounted() {
    this.$emit('select', this.config.basicStyle);
    this.$refs.testForm.validate();
    this.$nextTick(() => {
      setTimeout(() => {
        // 选择面板默认打开会让页面滚到底部，给悬停控件做聚焦，让页面滚动到顶部
        this.$refs.themeDatePickerHoverRef.focus();
      }, 200);
    });
  },
  methods: {
    moment,
    onClickCard(key, refresh) {
      if (this.selectedKey != key || refresh) {
        let keyComp = {
          basicStyle: 'ThemeDatePickerProp',
          errorStyle: 'ThemeDatePickerError',
          panelStyle: 'ThemeDatePickerPanel',
          clearStyle: 'ThemeDateClearProp'
        };
        this.selectedKey = key;
        if (!this.config[key]) {
          this.$set(this.config, key, JSON.parse(JSON.stringify(this.$options.themePropConfig[key])));
        }
        this.$emit('select', this.config[key], null, keyComp[key]);
      }
    },
    undoThemePropConfig(key) {
      this.config[key] = JSON.parse(JSON.stringify(this.$options.themePropConfig[key]));
      this.onClickCard(key, true);
    }
  }
};
</script>
