<template>
  <uni-forms-item
    v-if="!hidden"
    :label="itemLabel"
    :name="formModelItemProp"
    :label-position="widget.configuration.labelPosition"
    :class="[widgetClass, thisClass]"
    :ref="fieldCode"
  >
    <template v-if="!displayAsLabel">
      <view v-if="widget.configuration.type == 'progress'" style="width: 100%">
        <view v-show="showInput" style="margin-bottom: 8px" class="flex f_y_c widget-form-numerical-bar__input">
          <uni-w-number-box
            v-model="formData[widget.configuration.code]"
            :inputBorder="
              widget.configuration.uniConfiguration ? widget.configuration.uniConfiguration.inputBorder : false
            "
            :min="0"
            :width="50"
            style="width: auto"
            :precision="2"
            @change="onChange"
          />
          <text style="padding-left: 4px">%</text>
        </view>
        <template v-if="widget.configuration.progress.isTooltips">
          <uni-tooltip placement="top" style="width: 100%">
            <template slot="content">
              <CustomContent
                v-if="widget.configuration.progress.tooltip"
                :content="widget.configuration.progress.tooltip"
                :percent="percent"
                :status="progressStatus"
                :form="form"
                :formData="formData"
              ></CustomContent>
              <view v-else> {{ percent }}% </view>
            </template>
            <uni-w-progress
              :type="widget.configuration.progress.type"
              :percent="percent"
              :showPercent="widget.configuration.progress.showInfo"
              :activeColor="activeColor"
              :inactiveColor="inactiveColor"
              :width="widget.configuration.progress.width"
              :height="widget.configuration.progress.strokeWidth"
              :success="widget.configuration.progress.success"
              :exception="widget.configuration.progress.exception"
              :textStyle="textStyle"
              :status="progressStatus"
              :round="strokeLinecap == 'round'"
              :gapDegree="gapDegree"
              :gapPosition="widget.configuration.progress.gapPosition"
            >
              <template v-if="widget.configuration.progress.isUseFormat && widget.configuration.progress.format">
                <CustomContent
                  :content="widget.configuration.progress.format"
                  :percent="percent"
                  :status="progressStatus"
                  :form="form"
                  :formData="formData"
                ></CustomContent>
              </template>
            </uni-w-progress>
          </uni-tooltip>
        </template>
        <uni-w-progress
          v-else
          :type="widget.configuration.progress.type"
          :percent="percent"
          :showPercent="widget.configuration.progress.showInfo"
          :activeColor="activeColor"
          :inactiveColor="inactiveColor"
          :width="widget.configuration.progress.width"
          :height="widget.configuration.progress.strokeWidth"
          :success="widget.configuration.progress.success"
          :exception="widget.configuration.progress.exception"
          :textStyle="textStyle"
          :status="progressStatus"
          :round="strokeLinecap == 'round'"
          :gapDegree="gapDegree"
          :gapPosition="widget.configuration.progress.gapPosition"
        >
          <template v-if="widget.configuration.progress.isUseFormat && widget.configuration.progress.format">
            <CustomContent
              :content="widget.configuration.progress.format"
              :percent="percent"
              :status="progressStatus"
              :form="form"
              :formData="formData"
            ></CustomContent>
          </template>
        </uni-w-progress>
      </view>
      <view v-if="widget.configuration.type == 'rate'">
        <uni-w-rate
          v-model="formData[widget.configuration.code]"
          :allowClear="widget.configuration.rate.allowClear"
          :allowHalf="widget.configuration.rate.allowHalf"
          :disabled="disable || readonly"
          :count="widget.configuration.rate.count"
          :activeColor="activeColor"
          :inactiveColor="inactiveColor"
          :activeIcon="rateIcon"
          :inactiveIcon="rateIcon"
          :size="widget.configuration.rate.fontSize"
          @change="onChange"
        >
          <template v-if="hasCharacterSlot" slot="character">
            <span class="string" v-if="widget.configuration.rate.characterType == 'string'">
              {{ widget.configuration.rate.characterString.trim() }}
            </span>
          </template>
        </uni-w-rate>
      </view>
    </template>
    <span v-else class="textonly">
      {{ displayValue() }}
    </span>
  </uni-forms-item>
</template>
<script type="text/babel">
import { isArray } from "lodash";
import formElement from "../w-dyform/form-element.mixin";
import formCommonMixin from "../w-dyform/form-common.mixin";
import CustomContent from "./w-form-numerical-bar-custom-content";
export default {
  mixins: [formElement, formCommonMixin],
  props: {},
  components: { CustomContent },
  computed: {
    // 显示输入框
    showInput() {
      return !(this.disable || this.readonly); //this.widget.configuration.progress.showInput &&
    },
    // 圆角
    strokeLinecap() {
      return this.widget.configuration.progress.round ? "round" : "square";
    },
    textStyle() {
      let style = undefined;
      if (this.widget.configuration.type == "progress") {
        if (this.widget.configuration.progress.defaultTextStyle == "define") {
          style = {
            color: this.widget.configuration.progress.textColor,
            fontSize: this.widget.configuration.progress.fontSize,
            fontWeight: this.widget.configuration.progress.fontWeight,
          };
        }
      }
      return style;
    },
    activeColor() {
      if (this.widget.configuration.type == "progress") {
        return this.widget.configuration.progress.defaultColor !== "default" &&
          this.widget.configuration.progress.strokeColor
          ? this.widget.configuration.progress.strokeColor
          : undefined;
      } else if (this.widget.configuration.type == "rate") {
        return this.widget.configuration.rate.defaultStyle == "define" && this.widget.configuration.rate.selectedColor
          ? this.widget.configuration.rate.selectedColor
          : undefined;
      }
      return undefined;
    },
    inactiveColor() {
      if (this.widget.configuration.type == "progress") {
        return this.widget.configuration.progress.defaultBgColor !== "default" &&
          this.widget.configuration.progress.bgColor
          ? this.widget.configuration.progress.bgColor
          : undefined;
      } else if (this.widget.configuration.type == "rate") {
        return this.widget.configuration.rate.defaultStyle == "define" && this.widget.configuration.rate.bgColor
          ? this.widget.configuration.rate.bgColor
          : undefined;
      }
      return undefined;
    },
    // 缺口角度
    gapDegree() {
      if (this.widget.configuration.progress.type == "dashboard") {
        return this.widget.configuration.progress.gapDegree === 0
          ? 0.1
          : this.widget.configuration.progress.gapDegree || 90;
      }
      return 0;
    },
    thisClass() {
      if (this.widget.configuration.type == "progress") {
        return this.widget.configuration.type + "-" + this.widget.configuration.progress.type;
      }
      return this.widget.configuration.type;
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
        (this.widget.configuration.rate.characterType == "string" &&
          this.widget.configuration.rate.characterString.trim()) ||
        (this.widget.configuration.rate.characterType == "icon" && this.widget.configuration.rate.characterIcon)
      );
    },
    rateIcon() {
      if (this.widget.configuration.rate.characterType == "icon" && this.widget.configuration.rate.characterIcon) {
        return this.widget.configuration.rate.characterIcon;
      }
      return undefined;
    },
  },
  data() {
    let progressStatus = this.widget.configuration.progress.status || "";
    return {
      percent: 0,
      progressStatus,
    };
  },
  beforeCreate() {},
  created() {
    if (!this.widget.configuration.hasOwnProperty("uniConfiguration")) {
      this.$set(this.widget.configuration, "uniConfiguration", { inputBorder: false });
    }
  },
  beforeMount() {},
  mounted() {
    this.changePercent();
  },
  methods: {
    setValue(value) {
      this.formData[this.widget.configuration.code] = value && typeof value === "string" ? Number(value) : value;
      this.changePercent();
      this.emitChange();
    },
    onChange() {
      this.changePercent();
      this.emitChange();
    },
    changePercent() {
      if (this.widget.configuration.type == "progress") {
        let percent = this.formData[this.widget.configuration.code] || 0;
        this.percent = percent && typeof percent === "string" ? Number(percent) : percent;
        this.judgeStatus();
      }
    },
    //success exception normal active(仅限 line)
    setProgressStatus(status) {
      this.progressStatus = status;
    },
    // 根据配置的成功失败逻辑判断
    judgeStatus() {
      if (this.widget.configuration.progress.statusShowType == "change") {
        let successPass = this.judgeHandler(this.widget.configuration.progress.success);
        let exceptionPass = this.judgeHandler(this.widget.configuration.progress.exception);
        if (successPass) {
          this.setProgressStatus("success");
        } else if (exceptionPass) {
          this.setProgressStatus("exception");
        } else {
          this.setProgressStatus("normal");
        }
      }
    },
    // 根据配置的成功失败逻辑判断
    judgeHandler(obj) {
      let s_operator = obj.operator || "";
      let s_num = obj.number || 0;
      let ispass = false;
      // 区间，后面一个数大于0
      if (s_operator.indexOf("range") > -1 && obj.number1) {
        let s_num1 = obj.number1;
        if (s_operator == "range") {
          // range>= 区间
          if (this.percent > s_num && this.percent < s_num1) {
            ispass = true;
          }
        } else if (s_operator == "range>=") {
          // range>= 区间（左闭右开）
          if (this.percent >= s_num && this.percent < s_num1) {
            ispass = true;
          }
        } else if (s_operator == "range<=") {
          // range>= 区间（左开右闭）
          if (this.percent > s_num && this.percent <= s_num1) {
            ispass = true;
          }
        } else if (s_operator == "range==") {
          // range== 区间（闭区间，含等于）
          if (this.percent >= s_num && this.percent <= s_num1) {
            ispass = true;
          }
        } else if (s_operator == "range<>") {
          // range<> 区间（小于且大于）
          if (this.percent < s_num || this.percent > s_num1) {
            ispass = true;
          }
        } else if (s_operator == "range<=|>") {
          // range<=|> 区间（小于闭且大于）
          if (this.percent <= s_num || this.percent > s_num1) {
            ispass = true;
          }
        } else if (s_operator == "range<|>") {
          // range<|> 区间（小于或大于）
          if (this.percent < s_num || this.percent > s_num1) {
            ispass = true;
          }
        } else if (s_operator == "range<|>=") {
          // range<|>= 区间（小于且大于闭）
          if (this.percent < s_num || this.percent >= s_num1) {
            ispass = true;
          }
        } else if (s_operator == "range<=|>=") {
          // range<=|>= 区间（小于且大于，含等于）
          if (this.percent <= s_num || this.percent >= s_num1) {
            ispass = true;
          }
        }
      } else if (s_num && s_operator) {
        if (s_operator == ">") {
          if (this.percent > s_num) {
            ispass = true;
          }
        } else if (s_operator == ">=") {
          if (this.percent >= s_num) {
            ispass = true;
          }
        } else if (s_operator == "<") {
          if (this.percent < s_num) {
            ispass = true;
          }
        } else if (s_operator == "<=") {
          if (this.percent <= s_num) {
            ispass = true;
          }
        }
      }
      return ispass;
    },
    strokeColorGet(obj, defaultColor) {
      let strokeColor = obj.strokeColor;
      if (obj.defaultColor == "value" && strokeColor) {
        if (typeof strokeColor == "string") {
          return this.getColorValue(strokeColor);
        } else if (isArray(strokeColor)) {
          if (strokeColor.length == 1 && strokeColor[0]) {
            return this.getColorValue(strokeColor[0]);
          } else if (strokeColor.length == 2 && strokeColor[0] && strokeColor[1]) {
            return {
              from: this.getColorValue(strokeColor[0]),
              to: this.getColorValue(strokeColor[1]),
            };
          }
        } else if (typeof strokeColor == "Object") {
          return strokeColor;
        }
      }
      return defaultColor || "";
    },
    getColorValue(color) {
      if (color) {
        return color.startsWith("#") ? color : `var(${color})`;
      }
      return "";
    },
    displayValue() {
      if (this.widget.configuration.type == "progress") {
        if (this.formData[this.widget.configuration.code]) {
          return this.formData[this.widget.configuration.code] + "%";
        }
      } else if (this.widget.configuration.type == "rate") {
        return this.formData[this.widget.configuration.code] || 0;
      }
      return 0;
    },
    onFilter({ searchValue, comparator, source }) {
      if (source != undefined) {
        if (comparator == ">=") {
          return Number(searchValue) >= Number(source);
        } else if (comparator == "<=") {
          return Number(searchValue) <= Number(source);
        } else if (comparator == "<") {
          return Number(searchValue) < Number(source);
        } else if (comparator == ">") {
          return Number(searchValue) > Number(source);
        } else if (comparator == "!=") {
          return Number(searchValue) != Number(source);
        } else if (comparator == "like") {
          return (source + "").indexOf(searchValue + "") > -1;
        }
        return searchValue == source;
      }
      //TODO: 判断本组件值是否匹配
      return false;
    },
  },
};
</script>

<style lang="scss">
.widget-form-numerical-bar {
}
</style>
