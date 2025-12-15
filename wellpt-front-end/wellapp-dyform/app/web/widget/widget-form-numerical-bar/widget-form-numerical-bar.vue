<template>
  <a-form-model-item
    :style="[itemStyle, progressStyle, progressBgStyle]"
    :prop="formModelItemProp"
    :rules="rules"
    :ref="widget.configuration.code"
    :label="itemLabel"
    :colon="displayAsLabel"
    :class="[widgetClass, thisClass]"
  >
    <template v-if="!displayAsLabel">
      <template v-if="configuration.type == 'progress'">
        <div v-show="showInput" style="margin-bottom: 8px">
          <a-input-number v-model="formData[widget.configuration.code]" :min="0" @change="onChange" />
          %
        </div>
        <template v-if="configuration.progress.isTooltips">
          <a-tooltip placement="top">
            <template slot="title">
              <component
                v-if="tooltipComponent"
                :is="tooltipComponent"
                :percent="percent"
                :successPercent="successPercent"
                :status="progressStatus"
                :form="form"
                :formData="formData"
              />
              <div v-else>{{ percent }}%</div>
            </template>
            <a-progress
              :percent="percent"
              :stroke-linecap="strokeLinecap"
              :type="configuration.progress.type"
              :showInfo="configuration.progress.showInfo"
              :strokeColor="strokeColor"
              :strokeWidth="configuration.progress.strokeWidth"
              :width="configuration.progress.width"
              :gapDegree="gapDegree"
              :gapPosition="configuration.progress.gapPosition"
              :successPercent="successPercent"
              :status="progressStatus"
            >
              <template v-if="configuration.progress.isUseFormat" #format="percent, successPercent">
                <component
                  :is="component"
                  :percent="percent"
                  :successPercent="successPercent"
                  :status="progressStatus"
                  :form="form"
                  :formData="formData"
                />
              </template>
            </a-progress>
          </a-tooltip>
        </template>
        <template v-else>
          <a-progress
            :percent="percent"
            :stroke-linecap="strokeLinecap"
            :type="configuration.progress.type"
            :showInfo="configuration.progress.showInfo"
            :strokeColor="strokeColor"
            :strokeWidth="configuration.progress.strokeWidth"
            :width="configuration.progress.width"
            :gapDegree="gapDegree"
            :gapPosition="configuration.progress.gapPosition"
            :successPercent="successPercent"
            :status="progressStatus"
          >
            <template v-if="configuration.progress.isUseFormat" #format="percent, successPercent">
              <component
                :is="component"
                :percent="percent"
                :successPercent="successPercent"
                :status="progressStatus"
                :form="form"
                :formData="formData"
              />
            </template>
          </a-progress>
        </template>
      </template>
      <template v-else-if="configuration.type == 'rate'">
        <a-rate
          v-model="formData[widget.configuration.code]"
          :allowClear="configuration.rate.allowClear"
          :allowHalf="configuration.rate.allowHalf"
          :disabled="disable || readonly"
          :count="configuration.rate.count"
          :tooltips="rateTooltips"
          @change="onChange"
        >
          <template slot="character" v-if="hasCharacterSlot">
            <span class="string" v-if="widget.configuration.rate.characterType == 'string'">
              {{ widget.configuration.rate.characterString.trim() }}
            </span>
            <template v-else-if="widget.configuration.rate.characterType == 'icon'">
              <Icon :type="widget.configuration.rate.characterIcon"></Icon>
            </template>
          </template>
        </a-rate>
      </template>
    </template>
    <span v-else class="textonly">
      {{ displayValue() }}
    </span>
  </a-form-model-item>
</template>

<script>
import { isArray } from 'lodash';
import widgetMixin from '@framework/vue/mixin/widgetMixin';
import { FormElement } from '../../framework/vue/dyform/form-element';
import formCommonMixin from '../mixin/form-common.mixin';
export default {
  extends: FormElement,
  name: 'WidgetFormNumericalBar',
  mixins: [widgetMixin, formCommonMixin],
  props: {},
  data() {
    let progressStatus = this.widget.configuration.progress.status || '';
    return {
      percent: (this.widget.configuration.type == 'progress' && this.formData[this.widget.configuration.code]) || 0,
      progressStatus
    };
  },
  computed: {
    // 显示输入框
    showInput() {
      return !(this.disable || this.readonly); //this.widget.configuration.progress.showInput &&
    },
    // 进度条提示
    tooltipComponent() {
      if (this.widget.configuration.type == 'progress') {
        if (this.configuration.progress.isTooltips && this.configuration.progress.tooltip) {
          let _this = this;
          return {
            template: `<span>${this.configuration.progress.tooltip}</span>`,
            props: ['percent', 'successPercent', 'status', 'form', 'formData'],
            data: function () {
              return {};
            },
            render: undefined,
            mounted() {},
            methods: {}
          };
        }
      }
      return null;
    },
    // 内容自定义组件
    component() {
      if (this.widget.configuration.type == 'progress') {
        if (this.configuration.progress.isUseFormat && this.configuration.progress.format) {
          let _this = this;
          return {
            template: `<span>${this.configuration.progress.format}</span>`,
            props: ['percent', 'successPercent', 'status', 'form', 'formData'],
            data: function () {
              return {};
            },
            render: undefined,
            mounted() {},
            methods: {}
          };
        }
      }
      return null;
    },
    // 圆角
    strokeLinecap() {
      return this.widget.configuration.progress.round ? 'round' : 'square';
    },
    // 线段颜色
    strokeColor() {
      if (this.progressStatus == 'success') {
        return this.strokeColorGet(this.widget.configuration.progress.success);
      } else if (this.progressStatus == 'exception') {
        return this.strokeColorGet(this.widget.configuration.progress.exception);
      }
      return this.strokeColorGet(this.widget.configuration.progress, 'var(--w-primary-color)');
    },
    // 缺口角度
    gapDegree() {
      if (this.widget.configuration.progress.type == 'dashboard') {
        return this.widget.configuration.progress.gapDegree === 0 ? 0.1 : this.widget.configuration.progress.gapDegree;
      }
      return 0;
    },
    // 背景颜色
    progressBgStyle() {
      let style = {};
      if (this.widget.configuration.type == 'progress') {
        if (
          this.progressStatus == 'success' &&
          this.widget.configuration.progress.success.defaultBgColor !== 'default' &&
          this.widget.configuration.progress.success.bgColor
        ) {
          style['--w-widget-numerical-bar-progress-inner-bg-color'] = this.getColorValue(
            this.widget.configuration.progress.success.bgColor
          );
        } else if (
          this.progressStatus == 'exception' &&
          this.widget.configuration.progress.exception.defaultBgColor !== 'default' &&
          this.widget.configuration.progress.exception.bgColor
        ) {
          style['--w-widget-numerical-bar-progress-inner-bg-color'] = this.getColorValue(
            this.widget.configuration.progress.exception.bgColor
          );
        } else if (this.widget.configuration.progress.defaultBgColor !== 'default' && this.widget.configuration.progress.bgColor) {
          style['--w-widget-numerical-bar-progress-inner-bg-color'] = this.getColorValue(this.widget.configuration.progress.bgColor);
        }
      }
      return style;
    },
    progressStyle() {
      let style = {};
      if (this.widget.configuration.type == 'progress') {
        if (this.widget.configuration.progress.defaultTextStyle == 'define') {
          if (this.widget.configuration.progress.textColor) {
            style['--w-progress-text-color'] = this.getColorValue(this.widget.configuration.progress.textColor);
          }
          if (this.widget.configuration.progress.fontSize) {
            style['--w-progress-text-size'] = this.widget.configuration.progress.fontSize + 'px';
          }
          if (this.widget.configuration.progress.fontWeight) {
            style['--w-progress-text-weight'] = this.widget.configuration.progress.fontWeight;
          }
          if (this.widget.configuration.progress.success.textColor) {
            style['--w-progress-text-success-color'] = this.getColorValue(this.widget.configuration.progress.success.textColor);
          }
          if (this.widget.configuration.progress.exception.textColor) {
            style['--w-progress-text-exception-color'] = this.getColorValue(this.widget.configuration.progress.exception.textColor);
          }
        }
      } else if (this.widget.configuration.type == 'rate') {
        if (this.widget.configuration.rate.defaultStyle == 'define') {
          if (this.widget.configuration.rate.selectedColor) {
            style['--w-widget-numerical-bar-rate-color'] = this.getColorValue(this.widget.configuration.rate.selectedColor);
          }
          if (this.widget.configuration.rate.bgColor) {
            style['--w-widget-numerical-bar-rate-bg-color'] = this.getColorValue(this.widget.configuration.rate.bgColor);
          }
          if (this.widget.configuration.rate.fontSize) {
            style['--w-widget-numerical-bar-rate-size'] = this.widget.configuration.rate.fontSize + 'px';
          }
          if (this.widget.configuration.rate.fontWeight) {
            style['--w-widget-numerical-bar-rate-weight'] = this.widget.configuration.rate.fontWeight;
          }
        }
      }
      return style;
    },
    thisClass() {
      if (this.widget.configuration.type == 'progress') {
        return this.widget.configuration.type + '-' + this.widget.configuration.progress.type;
      }
      return this.widget.configuration.type;
    },
    // 已完成数百分比
    successPercent() {
      if (this.widget.configuration.type == 'progress') {
        let configuration = this.widget.configuration.progress;
        // 显示已完成的分段, 有已完成数字段、总数字段，且表单里总数有值
        if (
          configuration.showSuccessPercent &&
          configuration.successField &&
          configuration.totalField &&
          this.formData[configuration.totalField]
        ) {
          return ((this.formData[configuration.successField] || 0) / this.formData[configuration.totalField]).toFixed(2) * 100;
        }
      }
      return 0;
    },
    // 评分提示语
    rateTooltips() {
      if (this.widget.configuration.rate.isTooltips) {
        return this.widget.configuration.rate.tooltips;
      }
      return [];
    },
    // 判断是否有自定义字符
    hasCharacterSlot() {
      return (
        (this.widget.configuration.rate.characterType == 'string' && this.widget.configuration.rate.characterString.trim()) ||
        (this.widget.configuration.rate.characterType == 'icon' && this.widget.configuration.rate.characterIcon)
      );
    }
  },
  mounted() {
    if (this.designMode) {
      this.setValue(55);
    }
  },
  methods: {
    setValue(value) {
      this.formData[this.widget.configuration.code] = value && typeof value === 'string' ? Number(value) : value;
      this.changePercent();
      this.emitChange();
    },
    onChange() {
      this.changePercent();
      this.emitChange();
      this.$refs[this.configuration.code].onFieldChange();
      console.log(this.formData[this.widget.configuration.code]);
    },
    changePercent() {
      if (this.widget.configuration.type == 'progress') {
        this.percent = this.formData[this.widget.configuration.code];
        this.judgeStatus();
      }
    },
    //success exception normal active(仅限 line)
    setProgressStatus(status) {
      this.progressStatus = status;
    },
    // 根据配置的成功失败逻辑判断
    judgeStatus() {
      if (this.widget.configuration.progress.statusShowType == 'change') {
        let successPass = this.judgeHandler(this.widget.configuration.progress.success);
        let exceptionPass = this.judgeHandler(this.widget.configuration.progress.exception);
        if (successPass) {
          this.setProgressStatus('success');
        } else if (exceptionPass) {
          this.setProgressStatus('exception');
        } else {
          this.setProgressStatus('normal');
        }
      }
    },
    // 根据配置的成功失败逻辑判断
    judgeHandler(obj) {
      let s_operator = obj.operator || '';
      let s_num = obj.number || 0;
      let ispass = false;
      // 区间，后面一个数大于0
      if (s_operator.indexOf('range') > -1 && obj.number1) {
        let s_num1 = obj.number1;
        if (s_operator == 'range') {
          // range>= 区间
          if (this.percent > s_num && this.percent < s_num1) {
            ispass = true;
          }
        } else if (s_operator == 'range>=') {
          // range>= 区间（左闭右开）
          if (this.percent >= s_num && this.percent < s_num1) {
            ispass = true;
          }
        } else if (s_operator == 'range<=') {
          // range>= 区间（左开右闭）
          if (this.percent > s_num && this.percent <= s_num1) {
            ispass = true;
          }
        } else if (s_operator == 'range==') {
          // range== 区间（闭区间，含等于）
          if (this.percent >= s_num && this.percent <= s_num1) {
            ispass = true;
          }
        } else if (s_operator == 'range<>') {
          // range<> 区间（小于且大于）
          if (this.percent < s_num || this.percent > s_num1) {
            ispass = true;
          }
        } else if (s_operator == 'range<=|>') {
          // range<=|> 区间（小于闭且大于）
          if (this.percent <= s_num || this.percent > s_num1) {
            ispass = true;
          }
        } else if (s_operator == 'range<|>') {
          // range<|> 区间（小于或大于）
          if (this.percent < s_num || this.percent > s_num1) {
            ispass = true;
          }
        } else if (s_operator == 'range<|>=') {
          // range<|>= 区间（小于且大于闭）
          if (this.percent < s_num || this.percent >= s_num1) {
            ispass = true;
          }
        } else if (s_operator == 'range<=|>=') {
          // range<=|>= 区间（小于且大于，含等于）
          if (this.percent <= s_num || this.percent >= s_num1) {
            ispass = true;
          }
        }
      } else if (s_num && s_operator) {
        if (s_operator == '>') {
          if (this.percent > s_num) {
            ispass = true;
          }
        } else if (s_operator == '>=') {
          if (this.percent >= s_num) {
            ispass = true;
          }
        } else if (s_operator == '<') {
          if (this.percent < s_num) {
            ispass = true;
          }
        } else if (s_operator == '<=') {
          if (this.percent <= s_num) {
            ispass = true;
          }
        }
      }
      return ispass;
    },
    strokeColorGet(obj, defaultColor) {
      let strokeColor = obj.strokeColor;
      if (obj.defaultColor !== 'default' && strokeColor) {
        if (typeof strokeColor == 'string') {
          return this.getColorValue(strokeColor);
        } else if (isArray(strokeColor)) {
          if (strokeColor.length == 1 && strokeColor[0]) {
            return this.getColorValue(strokeColor[0]);
          } else if (strokeColor.length == 2 && strokeColor[0] && strokeColor[1]) {
            return {
              from: this.getColorValue(strokeColor[0]),
              to: this.getColorValue(strokeColor[1])
            };
          }
        } else if (typeof strokeColor == 'Object') {
          return strokeColor;
        }
      }
      return defaultColor || '';
    },
    getColorValue(color) {
      if (color) {
        return color.startsWith('#') ? color : `var(${color})`;
      }
      return '';
    },
    onFilter({ searchValue, comparator, source }) {
      if (source != undefined) {
        if (comparator == '>=') {
          return Number(searchValue) >= Number(source);
        } else if (comparator == '<=') {
          return Number(searchValue) <= Number(source);
        } else if (comparator == '<') {
          return Number(searchValue) < Number(source);
        } else if (comparator == '>') {
          return Number(searchValue) > Number(source);
        } else if (comparator == '!=') {
          return Number(searchValue) != Number(source);
        } else if (comparator == 'like') {
          return (source + '').indexOf(searchValue + '') > -1;
        }
        return searchValue == source;
      }
      //TODO: 判断本组件值是否匹配
      return false;
    },
    displayValue() {
      if (this.widget.configuration.type == 'progress') {
        if (this.formData[this.widget.configuration.code]) {
          return this.formData[this.widget.configuration.code] + '%';
        }
      } else if (this.widget.configuration.type == 'rate') {
        return this.formData[this.widget.configuration.code] || 0;
      }
      return 0;
    }
  }
};
</script>
<style lang="less">
.widget-form-numerical-bar {
  --w-widget-numerical-bar-progress-inner-bg-color: #f5f5f5;
  --w-widget-numerical-bar-progress-success-bg-color: var(--w-success-color);
  &.progress-line {
    .ant-progress-inner {
      background-color: var(--w-widget-numerical-bar-progress-inner-bg-color);
    }
  }
  &.progress-circle,
  &.progress-dashboard {
    .ant-progress-circle-trail {
      stroke: var(--w-widget-numerical-bar-progress-inner-bg-color) !important;
    }
  }
  &.rate {
    --w-widget-numerical-bar-rate-color: #fadb14;
    --w-widget-numerical-bar-rate-bg-color: #e8e8e8;
    --w-widget-numerical-bar-rate-size: 20px;
    --w-widget-numerical-bar-rate-weight: 'normal';
    .ant-rate {
      color: var(--w-widget-numerical-bar-rate-color);
      font-size: var(--w-widget-numerical-bar-rate-size);
      font-weight: var(--w-widget-numerical-bar-rate-weight);

      .iconfont {
        font-size: var(--w-widget-numerical-bar-rate-size);
        vertical-align: middle;
        line-height: normal;
      }
      .string {
        vertical-align: middle;
        line-height: var(--w-widget-numerical-bar-rate-size);
      }
    }
    .ant-rate-star-first,
    .ant-rate-star-second {
      color: var(--w-widget-numerical-bar-rate-bg-color);
      height: var(--w-widget-numerical-bar-rate-size);
      min-height: 26px;
    }

    .ant-rate-star-full .ant-rate-star-second,
    .ant-rate-star-half .ant-rate-star-first {
      color: inherit;
    }
  }
}
.widget-edit-container.selected {
  .ant-progress.ant-progress-circle {
    width: 100%;
  }
}
</style>
