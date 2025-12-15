<template>
  <uni-forms-item
    :label="itemLabel"
    :ref="widget.configuration.code"
    :name="formModelItemProp"
    :label-position="widget.configuration.labelPosition"
  >
    <template v-if="!displayAsLabel">
      <template v-if="widget.configuration.layout == 'horizontal'">
        <!-- 水平 -->
        <uni-w-data-checkbox
          :disabled="disable || readonly"
          :multiple="true"
          v-model="value"
          :localdata="checkboxOptions"
          mode="default"
          :class="checkboxClass"
          :showCheckAll="widget.configuration.checkboxAll"
          :checkedAll="checkAll"
          @change="onChange"
          @changeAll="onCheckAllChange"
        />
      </template>
      <template v-else-if="widget.configuration.layout === 'vertical'">
        <!-- 垂直 -->
        <uni-w-data-checkbox
          :disabled="disable || readonly"
          :multiple="true"
          v-model="value"
          :localdata="checkboxOptions"
          mode="list"
          :class="checkboxClass"
          :showCheckAll="widget.configuration.checkboxAll"
          :checkedAll="checkAll"
          @change="onChange"
          @changeAll="onCheckAllChange"
        />
      </template>
    </template>
    <view v-if="displayAsLabel" class="textonly" :title="checkedLabel">{{ checkedLabel }}</view>
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
      checkboxOptions: [],
      checkedLabels: [],
      value: [],
      indeterminate: false,
      checkAll: false,
    };
  },
  watch: {
    checkboxOptions: {
      handler(newVal) {
        this.checkAll = this.value.length === newVal.length;
      },
      deep: true,
    },
  },
  computed: {
    checkboxClass() {
      return "w-form-checkbox-" + this.widget.configuration.layout;
    },
    checkedLabel() {
      return this.checkedLabels.join(this.widget.configuration.tokenSeparators);
    },
    checkboxStyle() {
      let style = {};
      if (this.widget.configuration.layout == "horizontal" && this.widget.configuration.alignType == "fixedWidth") {
        style.width = this.widget.configuration.itemWidth + "px";
        style.boxSizing = "border-box";
      }

      return style;
    },
  },
  created() {
    if (!this.designMode) {
      this.addSelectedNumRules({
        min: this.widget.configuration.minCount,
        max: this.widget.configuration.maxCount,
      });
    }
  },
  mounted() {
    if (this.widget.configuration.layout == "horizontal") {
      /* 水平 */
      this.$nextTick(() => {
        this.$el.querySelectorAll(".checklist-box").forEach((item) => {
          Object.assign(item.style, this.checkboxStyle);
        });
      });
    }
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
      this.setValue(this.formData[this.fieldCode]); // 设初始值
      // 不是联动获取备选项
      if (!this.optionDataAutoSet || this.designMode || this.formData[this.fieldCode]) {
        this.fetchCheckboxOptions();
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
        this.fetchCheckboxOptionByDataDic(this.relateValue);
      }
    },
    fetchCheckboxOptions() {
      // 常量
      if (
        this.widget.configuration.options.type == "selfDefine" &&
        this.widget.configuration.options.defineOptions.length
      ) {
        if (this.widget.configuration.options.defineOptions.length) {
          this.checkboxOptions = this.convertTextValueOptions(this.widget.configuration.options.defineOptions);
        }
        this.setCheckedLabels();
        if (this.formData[this.fieldCode] != undefined) {
          this.emitChange({}, false);
        }
      } else if (this.widget.configuration.options.type == "dataSource") {
        this.fetchCheckboxOptionByDataSource(this.widget.configuration.options);
      } else if (this.widget.configuration.options.type == "dataDictionary") {
        this.fetchCheckboxOptionByDataDic(this.widget.configuration.options.dataDictionaryUuid);
      } else if (this.widget.configuration.options.type == "dataModel") {
        this.fetchCheckboxOptionByDataModel(this.widget.configuration.options);
      }
    },
    fetchCheckboxOptionByDataModel(options) {
      let _this = this;
      this.getLabelValueOptionByDataModel(options, function (result) {
        _this.checkboxOptions = _this.convertTextValueOptions(result);
        _this.setCheckedLabels();
        _this.emitChange({}, false);
      });
    },

    fetchCheckboxOptionByDataSource(options) {
      let _this = this;
      this.getLabelValueOptionByDataSource(options, function (result) {
        _this.checkboxOptions = _this.convertTextValueOptions(result);
        _this.setCheckedLabels();
        _this.emitChange({}, false);
      });
    },
    fetchCheckboxOptionByDataDic(dataDicUuid) {
      let _this = this;
      this.getLabelValueOptionByDataDic(dataDicUuid, function (result) {
        _this.checkboxOptions = _this.convertTextValueOptions(result);
        _this.setCheckedLabels();
        _this.emitChange({}, false);
      });
    },
    onChange(args) {
      let values = [];
      if (Array.isArray(args)) {
        values = args;
      } else {
        values = args.detail.value;
      }
      this.setCheckedLabels(values);
      this.formData[this.fieldCode] = values ? values.join(this.widget.configuration.tokenSeparators) : null;
      this.indeterminate = !!values.length && values.length < this.checkboxOptions.length;
      this.checkAll = values.length === this.checkboxOptions.length;
      // this.setOptionsDisabled();
      this.emitChange();
      this.$refs[this.fieldCode].onFieldChange(this.formData[this.fieldCode]); // 触发校验
    },
    setRelaFieldValue(value) {
      if (this.widget.configuration.displayValueField) {
        // 设置关联显示值字段
        this.form.setFieldValue(this.widget.configuration.displayValueField, value, false);
      }
    },
    setCheckedLabels(values) {
      this.checkedLabels.splice(0, this.checkedLabels.length);
      if (values == undefined) {
        values = this.value;
      }
      if (values == undefined) {
        return [];
      }
      for (let i = 0, len = this.checkboxOptions.length; i < len; i++) {
        if (values.indexOf(this.checkboxOptions[i].value) != -1) {
          this.checkedLabels.push(this.checkboxOptions[i].text);
        }
      }
      this.setRelaFieldValue(this.checkedLabels.join(this.widget.configuration.tokenSeparators));
      return this.checkedLabels;
    },
    displayValue(value) {
      // 提供其他组件调用获取显示值的方法
      if (value != undefined) {
        let labels = [];
        let values = value;
        if (typeof value == "string") {
          values = value.split(this.widget.configuration.tokenSeparators);
        }
        for (let i = 0, len = this.checkboxOptions.length; i < len; i++) {
          if (values.indexOf(this.checkboxOptions[i].value) != -1) {
            labels.push(this.checkboxOptions[i].text);
          }
        }
        return labels.join(this.widget.configuration.tokenSeparators);
      }

      return this.checkedLabel;
    },

    setValue(v) {
      this.value = v;
      if (typeof v === "string") {
        this.value = v.split(this.widget.configuration.tokenSeparators);
      }
      if (v == undefined && !this.designMode) {
        this.value = this.widget.configuration.defaultValue
          ? this.widget.configuration.defaultValue.split(this.widget.configuration.tokenSeparators)
          : [];
      }
      this.formData[this.fieldCode] = Array.isArray(v) ? v.join(this.widget.configuration.tokenSeparators) : v;
      this.setCheckedLabels();
      this.clearValidate();
    },
    // 全选
    onCheckAllChange(checked) {
      this.checkAll = checked;
      this.formData[this.fieldCode] = "";
      if (this.checkAll) {
        var checkedList = [];
        for (let i = 0, len = this.checkboxOptions.length; i < len; i++) {
          checkedList.push(this.checkboxOptions[i].value);
        }
        this.onChange(checkedList);
        this.setValue(checkedList.join(";"));
      } else {
        this.onChange([]);
        this.setValue("");
      }
      this.indeterminate = false;
    },
    // 设置选项不可选
    setOptionsDisabled() {
      // 最多可选个数
      let max = this.widget.configuration.checkboxMax;
      if (this.widget.configuration.checkboxMax) {
        let size = this.value.length;
        if (size < max) {
          // 个数小于最大值时，全部选项都可操作
          for (let i = 0, len = this.checkboxOptions.length; i < len; i++) {
            this.checkboxOptions[i].disabled = false;
          }
        } else {
          // 个数大于最大值时，全部选项都可操作
          for (let i = 0, len = this.checkboxOptions.length; i < len; i++) {
            if (this.value.indexOf(this.checkboxOptions[i].value) != -1) {
              this.checkboxOptions[i].disabled = false;
            } else {
              this.checkboxOptions[i].disabled = true;
            }
          }
        }
      }
    },
    // 选项变化后，当前值重置,避免选项被隐藏，当前值还在的问题
    optionsChangeAfter(checkboxOptions) {
      this.checkboxOptions = checkboxOptions;
      let checkedList = this.value || [];
      for (let i = 0, len = checkedList.length; i < len; i++) {
        var index = findIndex(this.checkboxOptions, { value: checkedList[i] });
        if (index == -1) {
          checkedList.splice(index, 1);
        }
      }
      this.onChange(checkedList);
      this.setValue(checkedList.join(";"));
    },
  },
};
</script>

<style lang="scss" scoped>
::v-deep .w-form-checkbox-vertical {
  .checklist-group {
    .checklist-box {
      &.is--list {
        &.is-list-border {
          border-top: none;
        }
      }
    }
  }
}
</style>
