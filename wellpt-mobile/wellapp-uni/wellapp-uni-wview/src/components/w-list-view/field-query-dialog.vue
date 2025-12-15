<template>
  <view class="field-query-dialog">
    <view class="field-query-content">
      <uni-forms class="field-query-form">
        <template v-for="(criterion, index) in criterions">
          <uni-forms-item
            v-if="criterion.queryType == 'text' || criterion.queryType == 'input'"
            :label="criterion.label"
            :key="index"
          >
            <view class="input-container">
              <input class="input-text" type="text" v-model="criterion.value" placeholder="请输入" />
            </view>
          </uni-forms-item>
          <uni-forms-item
            v-else-if="criterion.queryType == 'date' || criterion.queryType == 'datePicker'"
            :label="criterion.label"
            :key="index"
          >
            <uni-datetime-picker
              class="date-picker uni-mb-4"
              placeholder="请选择开始时间"
              type="date"
              :modelValue="criterion.fromValue"
              v-model="criterion.fromValue"
            ></uni-datetime-picker>
            <uni-datetime-picker
              class="date-picker"
              placeholder="请选择结束时间"
              type="date"
              :modelValue="criterion.toValue"
              v-model="criterion.toValue"
            ></uni-datetime-picker>
          </uni-forms-item>
          <uni-forms-item
            v-else-if="criterion.queryType == 'unit' || criterion.queryType == 'organization'"
            :label="criterion.label"
            :key="index"
          >
            <view class="input-container">
              <input
                class="input-text"
                type="text"
                v-model="criterion.displayValue"
                disabled
                placeholder="请选择"
                @tap="openUnit(criterion)"
              />
            </view>
          </uni-forms-item>
          <field-query-select-form-item
            v-else-if="criterion.queryType == 'select2' || criterion.queryType == 'select'"
            :criterion="criterion"
            :key="index"
          />
          <field-query-radio-form-item v-else-if="criterion.queryType == 'radio'" :criterion="criterion" :key="index" />
          <field-query-checkbox-form-item
            v-else-if="criterion.queryType == 'checkbox'"
            :criterion="criterion"
            :key="index"
          />
        </template>
      </uni-forms>
    </view>
    <view class="field-query-button-group">
      <view class="field-query-button" @click="onCancel">
        <button type="primary" plain="true">取消</button>
        <!-- <text class="field-query-button-text">取消</text> -->
      </view>
      <view class="field-query-button border-left" @click="onClear">
        <button type="primary" plain="true">清空</button>
        <!-- <text class="field-query-button-text button-color">清空</text> -->
      </view>
      <view class="field-query-button border-left" @click="onOk">
        <button type="primary">查询</button>
        <!-- <text class="field-query-button-text button-color">查询</text> -->
      </view>
    </view>
  </view>
</template>

<script>
import _ from "lodash";
import { unit } from "wellapp-uni-framework";
import fieldQuerySelectFormItem from "./field-query-select-form-item.vue";
import fieldQueryRadioFormItem from "./field-query-radio-form-item.vue";
import fieldQueryCheckboxFormItem from "./field-query-checkbox-form-item.vue";
export default {
  components: { fieldQuerySelectFormItem, fieldQueryRadioFormItem, fieldQueryCheckboxFormItem },
  props: {
    queryFields: Array,
  },
  data() {
    let criterions = [];
    _.forEach(this.queryFields, function (querField) {
      let criterion = querField;
      if (!_.isEmpty(criterion.defaultValue)) {
        criterion.value = criterion.defaultValue;
        criterion.defaultValue = "";
      }
      criterion.queryType = criterion.queryOptions.queryType;
      criterions.push(criterion);
    });
    return {
      criterions,
    };
  },
  mounted() {
    //this.$refs.fromDatePick[0].initPicker("2016-05-01");
  },
  methods: {
    onCancel: function () {
      this.$emit("cancel");
    },
    onReset: function () {
      _.forEach(this.criterions, function (criterion) {
        criterion.value = criterion.defaultValue;
        criterion.displayValue = criterion.defaultValue;
        if (criterion.fromValue) {
          criterion.fromValue = "";
        }
        if (criterion.toValue) {
          criterion.toValue = "";
        }
      });
    },
    onClear: function () {
      var _self = this;
      _.forEach(_self.criterions, function (criterion) {
        _self.$set(criterion, "value", "");
        _self.$set(criterion, "displayValue", "");
        if (criterion.fromValue) {
          criterion.fromValue = "";
        }
        if (criterion.toValue) {
          criterion.toValue = "";
        }
      });
    },
    onOk: function () {
      this.$emit("ok", this.collectCriterions());
    },
    collectCriterions: function () {
      let _self = this;
      let retCriterions = [];
      _.forEach(_self.criterions, function (criterion) {
        let queryType = criterion.queryType;
        let condition = {
          columnIndex: criterion.name,
          value: criterion.value,
          type: criterion.operator,
        };
        if (queryType == "date" || queryType == "datePicker") {
          if (_.isEmpty(criterion.fromValue) && _.isEmpty(criterion.toValue)) {
            return;
          }
          condition.type = "between";
          condition.value = [criterion.fromValue || "", criterion.toValue || ""];
        } else {
          if (_.isEmpty(condition.value)) {
            return;
          }
        }
        retCriterions.push(condition);
      });
      return retCriterions;
    },
    /**
     * 打开组织选择框
     */
    openUnit: function (criterion) {
      const _self = this;
      let queryOptions = (criterion.queryOptions && criterion.queryOptions.options) || criterion.queryOptions || {};
      let fieldValue = criterion.value;
      let displayValue = criterion.displayValue;
      let separator = ";";
      let mutiSelect = (queryOptions.multiple || queryOptions.multiSelect) == "1"; //选择方式 单选、多选
      let filterCondition = queryOptions.jsonParams;
      let typeList = queryOptions.orgTypes && queryOptions.orgTypes.split(";"); //组织类型
      if (!typeList) {
        typeList = queryOptions.orgType || [];
      }
      let selectTypes = queryOptions.nodeTypes && queryOptions.nodeTypes.split(";"); //节点类型
      if (!selectTypes) {
        selectTypes = this.getNodeTypes(queryOptions.checkableTypes) || [];
      }
      let valueFormat = queryOptions.valueFormat || queryOptions.isPathValue ? "all" : "justId"; // 完整格式或仅id
      let defaultType = queryOptions.defaultType || queryOptions.defaultOrgType; //默认类型
      let viewStyles = queryOptions.orgStyle || this.getOrgViewStyle(queryOptions.inputDisplayStyle); //风格
      unit.open({
        title: "组织选择",
        initValues: _.isEmpty(fieldValue) ? [] : fieldValue.split(";"),
        initLabels: _.isEmpty(displayValue) ? [] : displayValue.split(";"),
        separator: separator,
        otherParams: null,
        excludeValues: [],
        // selectType : _self.getSelectType(),
        type: typeList.join(";"),
        // showType : _self.definition.showUnitType,
        multiple: mutiSelect,
        filterCondition: filterCondition,
        typeList: typeList,
        selectTypes: selectTypes.join(";"),
        valueFormat: valueFormat,
        defaultType: defaultType,
        ok: function (returnValue, treeNodes) {
          criterion.value = returnValue.id;
          _self.$set(criterion, "displayValue", returnValue.name);
          // _self.afterUnitChoose();
          // setExtValue(treeNodes, oldValue); // 设置扩增属性
        },
        viewStyles: viewStyles,
        // computeInitData: computeInitData
      });
    },
    getNodeTypes(checkableTypes) {
      return _.map(checkableTypes, (item) => {
        if (item == "unit") {
          return "O";
        } else if (item == "dept") {
          return "D";
        } else if (item == "job") {
          return "J";
        } else if (item == "user") {
          return "U";
        } else if (item == "group") {
          return "G";
        } else if (item == "duty") {
          return "DU";
        }
        return item;
      });
    },
    getOrgViewStyle(inputDisplayStyle) {
      if (inputDisplayStyle == "IconLabel") {
        //标签
        return "org-style0";
      } else if (inputDisplayStyle == "GroupIconLabel") {
        //标签分组
        return "org-style2";
      } else if (inputDisplayStyle == "Label") {
        //纯文本(以分号;分隔)
        return "org-style1";
      }
      return "org-style0";
    },
  },
};
</script>

<style lang="scss" scoped>
.field-query-dialog {
  background-color: $uni-bg-secondary-color;

  ::v-deep .uni-date-x {
    background-color: $uni-bg-secondary-color;
    color: $uni-text-color;
  }
  ::v-deep .uni-forms-item {
    color: $uni-text-color;
  }
  ::v-deep .uni-forms-item__label .label-text {
    color: $uni-text-color;
  }
  ::v-deep .checklist-text {
    color: $uni-text-color !important;
  }

  .field-query-content {
    /* #ifndef APP-NVUE */
    display: flex;
    /* #endif */
    flex-direction: row;
    justify-content: center;
    align-items: center;
    padding: 20px;
  }

  .input-container {
    /* #ifndef APP-NVUE */
    display: flex;
    box-sizing: border-box;
    /* #endif */
    flex-direction: row;
    align-items: center;
    border: 1px solid $uni-border-1;
    border-radius: 4px;

    font-size: 14px;
    min-height: 36px;
  }

  .input-text {
    width: 100%;
    padding-left: 10px;
  }

  .field-query-form {
    width: 100%;

    ::v-deep .uni-forms-item__inner {
      padding-bottom: 12px;
    }
  }

  .date-from,
  .date-to {
    width: 100%;
    display: flex;
    flex-direction: row;
    justify-content: flex-start;
    align-items: center;
  }
  .date-to {
    margin-top: 10px;
  }
  .date-from-label,
  .date-to-label {
    width: 70px;
  }
  .date-picker {
    flex: 1;
    ::v-deep .uni-date__x-input {
      height: 36px;
      line-height: 36px;
    }
  }

  .field-query-content-text {
    font-size: 14px;
    color: #6c6c6c;
  }

  .field-query-button-group {
    /* #ifndef APP-NVUE */
    display: flex;
    /* #endif */
    justify-content: space-between;
    // align-items: center;
    flex-direction: row;
    // border-top-color: #f5f5f5;
    // border-top-style: solid;
    // border-top-width: 1px;
    height: 55px;
    padding: 0 20px;
  }

  .field-query-button {
    width: calc(33% - 6px);
  }

  .border-left {
    // border-left-color: #f0f0f0;
    // border-left-style: solid;
    // border-left-width: 1px;
  }

  .field-query-button-text {
    font-size: 16px;
    color: $uni-text-color;
  }

  .button-color {
    color: #007aff;
  }
}
</style>
