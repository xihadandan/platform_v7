<template>
  <uni-forms-item
    :label="itemLabel"
    :ref="widget.configuration.code"
    :name="formModelItemProp"
    :label-position="widget.configuration.labelPosition"
    :class="widgetClass"
    :style="itemStyle"
  >
    <template v-if="!displayAsLabel">
      <template v-if="widget.configuration.styleType === 'button'">
        <template v-if="widget.configuration.buttonSelectedStyle === 'solid'">
          <!-- 按钮风格-填充底色 -->
          <uni-w-data-checkbox
            :disabled="disable || readonly"
            :multiple="false"
            mode="tag"
            v-model="formData[widget.configuration.code]"
            :localdata="radioOptions"
            @change="onChange"
          />
        </template>
        <template v-else-if="widget.configuration.buttonSelectedStyle === 'outline'">
          <uni-w-data-checkbox
            :disabled="disable || readonly"
            :multiple="false"
            mode="button"
            class="w-form-radio-outline"
            v-model="formData[widget.configuration.code]"
            :localdata="radioOptions"
            :radioOutline="true"
            @change="onChange"
            @click="onClick"
          />
        </template>
      </template>
      <template v-else-if="widget.configuration.styleType === 'radio'">
        <!-- 单选框风格 -->
        <template v-if="widget.configuration.layout == 'horizontal'">
          <!-- 水平 -->
          <uni-w-data-checkbox
            :disabled="disable || readonly"
            :multiple="false"
            mode="default"
            :class="radioClass"
            v-model="formData[widget.configuration.code]"
            :localdata="radioOptions"
            :styleBox="radioStyle"
            @change="onChange"
            @click="onClick"
          />
        </template>
        <template v-else-if="widget.configuration.layout === 'vertical'">
          <uni-w-data-checkbox
            :disabled="disable || readonly"
            :multiple="false"
            mode="list"
            :class="radioClass"
            v-model="formData[widget.configuration.code]"
            :localdata="radioOptions"
            @change="onChange"
            @click="onClick"
          />
        </template>
      </template>
    </template>
    <view v-if="displayAsLabel" class="textonly" :title="checkedLabel">
      {{ checkedLabel }}
    </view>
  </uni-forms-item>
</template>

<script>
const _ = require("lodash");
import formElement from "../w-dyform/form-element.mixin";
import formCommonMixin from "../w-dyform/form-common.mixin";

export default {
  mixins: [formElement, formCommonMixin],
  components: {},
  data() {
    return {
      radioOptions: [],
      checkedLabel: null,
    };
  },
  computed: {
    radioClass() {
      return "w-form-radio-" + this.widget.configuration.layout;
    },
    radioStyle() {
      let style = {};
      if (this.widget.configuration.layout == "horizontal" && this.widget.configuration.alignType == "fixedWidth") {
        style.width = this.widget.configuration.itemWidth + "px";
        style["box-sizing"] = "border-box";
      }
      return style;
    },
    defaultEvents() {
      let eventParams = [];
      if (this.optionType === "selfDefine" || this.optionType === "dataDictionary") {
        let eventParamKeyOptions = {
          paramKey: "options",
          remark: "选项",
          valueSource: {
            inputType: "multi-select",
            options: this.radioOptions,
          },
        };
        eventParams.push(eventParamKeyOptions);
      }
      if (this.optionType == "dataSource" || this.optionType == "dataModel") {
        eventParams.push({
          paramKey: "removeCache",
          remark: "在从表里，该值为true会清除缓存；主表里默认清除缓存,该参数无效",
          valueSource: {
            inputType: "select",
            options: [
              { label: "是", value: true },
              { label: "否", value: false },
            ],
          },
        });
      }
      return [
        {
          id: "refreshOptions",
          title: "重新加载备选项",
          eventParams,
        },
      ];
    },
  },
  mounted() {
    this.init();
  },
  methods: {
    init() {
      if (!this.designMode) {
        if (this.optionDataAutoSet && this.relateKey) {
          this.listenRelateChanged(() => {
            this.checkRelateValue({
              relateValue: this.relateValue,
              configuration: this.widget.configuration,
              callback: this.optionsChangeAfter,
            });
          });
        }
      }
      if (!this.optionDataAutoSet || this.designMode || this.formData[this.fieldCode]) {
        this.fetchRadioOptions();
      }
    },
    // 判断联动方式
    checkRelateValue(arg) {
      if (this.optionType === "selfDefine") {
        // 常量
        this.relateSelfDefine(arg);
      }
      if (this.optionType === "dataDictionary") {
        // 数据字典
        this.fetchRadioOptionByDataDic(this.relateValue);
      }
    },
    fetchRadioOptions(callback) {
      // 常量
      if (
        this.widget.configuration.options.type == "selfDefine" &&
        this.widget.configuration.options.defineOptions.length
      ) {
        this.fetchedOptions = true;
        this.radioOptions = this.convertTextValueOptions(this.widget.configuration.options.defineOptions);
        this.setCheckdLabel();
        this.emitChange({}, false);
        if (typeof callback === "function") {
          callback();
        }
      } else if (this.widget.configuration.options.type == "dataSource") {
        this.fetchRadioOptionByDataSource(this.widget.configuration.options, () => {
          this.fetchedOptions = true;
          if (typeof callback === "function") {
            callback();
          }
        });
      } else if (this.widget.configuration.options.type == "dataDictionary") {
        this.fetchRadioOptionByDataDic(this.widget.configuration.options.dataDictionaryUuid, () => {
          this.fetchedOptions = true;
          if (typeof callback === "function") {
            callback();
          }
        });
      } else if (this.widget.configuration.options.type == "dataModel") {
        this.fetchRadioOptionByDataModel(this.widget.configuration.options, () => {
          this.fetchedOptions = true;
          if (typeof callback === "function") {
            callback();
          }
        });
      } else if (this.widget.configuration.options.type == "apiLinkService") {
        this.fetchRadioOptionByApiLink(this.widget.configuration.options, () => {
          this.fetchedOptions = true;
          if (typeof callback === "function") {
            callback();
          }
        });
      }
    },
    fetchRadioOptionByApiLink(options, callback) {
      let _this = this;
      _this.radioOptions.splice(0, _this.radioOptions.length);
      this.fetchDataByApiLinkInvocation(options.apiInvocationConfig).then((result) => {
        if (Array.isArray(result)) {
          this.radioOptions = this.convertTextValueOptions(result);
          this.setCheckdLabel();
          this.emitChange({}, false);
          if (typeof callback === "function") {
            callback();
          }
        }
      });
    },
    fetchRadioOptionByDataModel(options, callback) {
      this.getLabelValueOptionByDataModel(options, (result) => {
        this.radioOptions = this.convertTextValueOptions(result);
        this.setCheckdLabel();
        this.emitChange({}, false);
        if (typeof callback === "function") {
          callback();
        }
      });
    },
    fetchRadioOptionByDataSource(options, callback) {
      this.getLabelValueOptionByDataSource(options, (result) => {
        this.radioOptions = this.convertTextValueOptions(result);
        this.setCheckdLabel();
        this.emitChange({}, false);
        if (typeof callback === "function") {
          callback();
        }
      });
    },
    fetchRadioOptionByDataDic(dataDicUuid, callback) {
      this.getLabelValueOptionByDataDic(dataDicUuid, (result) => {
        this.radioOptions = this.convertTextValueOptions(result);
        this.setCheckdLabel();
        this.emitChange({}, false);
        if (typeof callback === "function") {
          callback();
        }
      });
    },

    onChange({ detail = {} } = {}) {
      this.setCheckdLabel(detail.value);
      this.emitChange();
    },

    setRelaFieldValue(value) {
      if (this.widget.configuration.displayValueField) {
        this.form.setFieldValue(this.widget.configuration.displayValueField, value);
      }
    },

    onClick(evt, ref, val) {
      if (val == this.getValue() && this.widget.configuration.cancleChecked) {
        //取消选中
        this.setValue(null);
        this.$refs[this.fieldCode].onFieldChange(); // 触发校验
        this.emitChange();
      }
    },

    setCheckdLabel(value) {
      if (value == undefined) {
        value = this.form.formData[this.fieldCode];
        this.checkedLabel = null;
      }
      for (let i = 0, len = this.radioOptions.length; i < len; i++) {
        if (value === this.radioOptions[i].value) {
          this.checkedLabel = this.radioOptions[i].text;
          break;
        }
      }
      // 设置关联显示值字段
      this.setRelaFieldValue(this.checkedLabel);
      return this.checkedLabel;
    },
    setValue(value) {
      this.formData[this.fieldCode] = value;
      if (value && !this.fetchedOptions) {
        this.fetchRadioOptions(() => {
          this.formData[this.fieldCode] = value;
          this.setCheckdLabel();
          this.clearValidate();
        });
      } else {
        this.setCheckdLabel();
        this.clearValidate();
      }
    },
    displayValue(value) {
      // 提供其他组件调用获取显示值的方法
      if (value != undefined) {
        for (let i = 0, len = this.radioOptions.length; i < len; i++) {
          if (value === this.radioOptions[i].value) {
            return this.radioOptions[i].text;
          }
        }
      }
      return this.checkedLabel;
    },
    // 选项变化后，当前值重置,避免选项被隐藏，当前值还在的问题
    optionsChangeAfter(radioOptions) {
      this.radioOptions = radioOptions;
      let val = this.form.formData[this.fieldCode];
      if (val) {
        let index = findIndex(this.radioOptions, { value: val });
        if (index == -1) {
          this.setValue(null);
        }
      }
    },
    onFilter({ searchValue, comparator, source, ignoreCase }) {
      return new Promise((resolve, reject) => {
        if (source != undefined) {
          if (comparator == "like") {
            // 模糊匹配
            if (this.radioValueOptionMap[source] != undefined) {
              let label = this.radioValueOptionMap[source].label;
              if (
                ignoreCase
                  ? label.toLowerCase().indexOf(searchValue.toLowerCase()) != -1
                  : label.indexOf(searchValue) != -1
              ) {
                resolve(true);
                return;
              }
            }
          } else {
            if (source == searchValue) {
              resolve(true);
              return;
            }
          }
          resolve(false);
        }
        //TODO: 判断本组件值是否匹配
        resolve(false);
      });
    },
    // 通过事件配置的选项值，筛选常量和字典备选项
    getSelectOptionByValue(values) {
      this.radioOptions.splice(0, this.radioOptions.length);
      each(this.initOptions, (opt) => {
        if (values.indexOf(opt.value) > -1) {
          this.radioOptions.push(opt);
        }
      });
    },
  },
};
</script>
