<template>
  <dyform-field-base class="w-unit" :dyformField="dyformField">
    <template slot="editable-input">
      <view @click="openUnit">
        <uni-easyinput
          v-model="displayValue"
          :clearable="false"
          :disabled="true"
          type="text"
          placeholder="请选择"
          autocomplete="off"
          @focus="openUnit"
        />
      </view>
    </template>
    <template slot="displayAsLabel">
      <uni-forms-item :label="fieldLabel" :required="isRequired">
        <label>{{ displayValue }}</label>
      </uni-forms-item>
    </template>
    <template slot="disabled">
      <uni-forms-item :label="fieldLabel" :required="isRequired">
        <label>{{ displayValue }}</label>
      </uni-forms-item>
    </template>
  </dyform-field-base>
</template>

<script>
import dyformFieldBase from "./field-base.vue";
import { unit } from "wellapp-uni-framework";
const _ = require("lodash");
import mixin from "./field-mixin";
export default {
  mixins: [mixin],
  components: { dyformFieldBase },
  data() {
    return {
      allowOpen: false,
      separator: ";",
      otherParams: null,
      excludeValues: [],
      typeList: [],
      mutiSelect: false,
      filterCondition: null,
      selectTypeList: [],
      valueFormat: null,
      defaultType: null,
      viewStyles: null,
      displayValue: "",
    };
  },
  created() {
    var _self = this;
    _self.initOptionData();
    _self.initDiaplsyValue();
  },
  methods: {
    initOptionData: function () {
      var _self = this;
      var excludeValues = [];
      var otherParams = {};
      // var valueFormat = _self.fieldDefinition.valueFormat;
      var filterCondition = _self.fieldDefinition.filterCondition;
      if (filterCondition) {
        //按中英文的逗号和分号分割
        var paramsSchema = "otherParams://";
        if (filterCondition.indexOf(paramsSchema) === 0) {
          try {
            otherParams = eval("(" + filterCondition.substr(paramsSchema.length) + ")");
            if (otherParams && otherParams.filterCondition) {
              excludeValues = otherParams.filterCondition.split(/;|,|；|，/);
            }
          } catch (ex) {
            console.error(ex);
          }
        } else {
          //按中英文的逗号和分号分割
          excludeValues = filterCondition.split(/;|,|；|，/);
        }
      }
      var viewStyle = _self.fieldDefinition.viewStyle;
      var viewStyles = {};
      if (_.isArray(viewStyle) && viewStyle.length == _self.fieldDefinition.typeList.length) {
        _.each(_self.fieldDefinition.typeList, function (type, idx) {
          viewStyles[type] = viewStyle[idx];
        });
      }

      _self.otherParams = otherParams;
      _self.excludeValues = excludeValues;
      _self.typeList = _self.fieldDefinition.typeList || ["all"];
      _self.mutiSelect = _self.fieldDefinition.mutiSelect;
      _self.filterCondition = filterCondition;
      _self.selectTypeList = _self.fieldDefinition.selectTypeList || ["all"];
      _self.valueFormat = _self.fieldDefinition.valueFormat || "justId";
      _self.defaultType = _self.fieldDefinition.defaultType;
      _self.viewStyles = viewStyles;
    },
    initDiaplsyValue: function () {
      var _self = this;
      var fieldValue = _.trim(_self.dyformField.getValue());
      if (!_.isEmpty(fieldValue)) {
        var reqValues = fieldValue.split(";");
        uni.request({
          method: "POST",
          url: "/api/org/facade/getNameByOrgEleIds",
          dataType: "json",
          data: {
            orgIds: reqValues,
          },
          success: function (result) {
            var data = result.data.data || {};
            var displayValues = [];
            _.each(reqValues, function (val) {
              displayValues.push(data[val]);
            });
            _self.displayValue = displayValues.join(";");
          },
        });
      }
    },
    // 打开组织选择框
    openUnit: function () {
      var _self = this;
      if (!_self.allowOpen) {
        return;
      }
      var fieldValue = _.trim(_self.dyformField.getValue());
      var displayValue = _.trim(_self.displayValue);
      unit.open({
        title: "组织选择",
        initValues: _.isEmpty(fieldValue) ? [] : fieldValue.split(";"),
        initLabels: _.isEmpty(displayValue) ? [] : displayValue.split(";"),
        separator: _self.separator,
        otherParams: _self.otherParams,
        excludeValues: _self.excludeValues,
        // selectType : _self.getSelectType(),
        type: _self.typeList.join(";"),
        // showType : _self.definition.showUnitType,
        multiple: _self.mutiSelect,
        filterCondition: _self.filterCondition,
        typeList: _self.typeList,
        selectTypes: _self.selectTypeList.join(";"),
        valueFormat: _self.valueFormat,
        defaultType: _self.defaultType,
        ok: function (returnValue, treeNodes) {
          _self.setValue2Object(returnValue);
          _self.setToRealDisplayColumn(returnValue);
          console.log(treeNodes);
          // _self.afterUnitChoose();
          // setExtValue(treeNodes, oldValue); // 设置扩增属性
        },
        viewStyles: _self.viewStyles,
        // computeInitData: computeInitData
      });
    },
    setValue2Object: function (returnValue) {
      this.dyformField.setValue(returnValue.id);
      this.displayValue = returnValue.name;
    },
    setToRealDisplayColumn: function (returnValue) {
      var _self = this;
      if (!_self.fieldDefinition.realDisplay) {
        return;
      }
      console.log(returnValue);
      var real = _.trim(_self.fieldDefinition.realDisplay.real);
      var display = _.trim(_self.fieldDefinition.realDisplay.display);

      if (!_.isEmpty(real)) {
        _self.formData[real] = _self.dyformField.getValue();
      }
      if (!_.isEmpty(display)) {
        _self.formData[display] = _self.displayValue;
      }
    },
  },
  computed: {
    isRequired: function () {
      return this.dyformField.isRequired();
    },
  },
  mounted() {
    var _self = this;
    // 避免自动获取焦点打开组织选择框
    setTimeout(() => {
      _self.allowOpen = true;
    }, 1000);
  },
};
</script>

<style lang="scss" scoped>
.w-unit {
}
</style>
