<template>
  <view class="field-query-dialog">
    <view style="max-height: 70vh; overflow-y: auto">
      <view class="field-query-content">
        <uni-forms class="field-query-form" labelPosition="top" labelWidth="auto">
          <template v-for="(criterion, index) in criterions">
            <uni-forms-item
              v-if="criterion.queryType == 'text' || criterion.queryType == 'input'"
              :label="$t(criterion.uuid, criterion.label)"
              :key="criterion.uuid"
            >
              <uni-w-easyinput
                type="text"
                v-model="criterion.value"
                :placeholder="$t('global.placeholder', '请输入')"
                :ref="'fieldQuery_' + criterion.queryType + '_' + criterion.uuid + 'Ref'"
              />
            </uni-forms-item>
            <field-query-date-form-item
              v-else-if="criterion.queryType == 'date' || criterion.queryType == 'datePicker'"
              :criterion="criterion"
              :key="criterion.uuid"
              :ref="'fieldQuery_' + criterion.queryType + '_' + criterion.uuid + 'Ref'"
            >
            </field-query-date-form-item>
            <field-query-org-select-form-item
              v-else-if="criterion.queryType == 'unit' || criterion.queryType == 'organization'"
              :criterion="criterion"
              :key="criterion.uuid"
              :ref="'fieldQuery_' + criterion.queryType + '_' + criterion.uuid + 'Ref'"
            />
            <field-query-select-form-item
              v-else-if="criterion.queryType == 'select2' || criterion.queryType == 'select'"
              :criterion="criterion"
              :key="criterion.uuid"
              :cacheKey="key + '_' + criterion.uuid"
              :ref="'fieldQuery_' + criterion.queryType + '_' + criterion.uuid + 'Ref'"
            />
            <field-query-select-form-item
              v-else-if="criterion.queryType == 'groupSelect'"
              :criterion="criterion"
              :key="criterion.uuid"
              :cacheKey="key + '_' + criterion.uuid"
              :ref="'fieldQuery_' + criterion.queryType + '_' + criterion.uuid + 'Ref'"
            />
            <field-query-tree-select-form-item
              v-else-if="criterion.queryType == 'treeSelect'"
              :criterion="criterion"
              :key="criterion.uuid"
              :cacheKey="key + '_' + criterion.uuid"
              :ref="'fieldQuery_' + criterion.queryType + '_' + criterion.uuid + 'Ref'"
            />
            <field-query-radio-form-item
              v-else-if="criterion.queryType == 'radio'"
              :criterion="criterion"
              :key="criterion.uuid"
              :cacheKey="key + '_' + criterion.uuid"
              :ref="'fieldQuery_' + criterion.queryType + '_' + criterion.uuid + 'Ref'"
            />
            <field-query-checkbox-form-item
              v-else-if="criterion.queryType == 'checkbox'"
              :criterion="criterion"
              :key="criterion.uuid"
              :cacheKey="key + '_' + criterion.uuid"
              :ref="'fieldQuery_' + criterion.queryType + '_' + criterion.uuid + 'Ref'"
            />
          </template>
        </uni-forms>
      </view>
    </view>
    <view class="field-query-button-group">
      <uni-w-button-group :buttons="buttons" :gutter="16" @click="onTrigger"></uni-w-button-group>
    </view>
  </view>
</template>

<script>
import { isEmpty, each as forEach } from "lodash";
import fieldQuerySelectFormItem from "./field-query/field-query-select-form-item.vue";
import fieldQueryTreeSelectFormItem from "./field-query/field-query-tree-select-form-item.vue";
import fieldQueryRadioFormItem from "./field-query/field-query-radio-form-item.vue";
import fieldQueryCheckboxFormItem from "./field-query/field-query-checkbox-form-item.vue";
import fieldQueryOrgSelectFormItem from "./field-query/field-query-org-select-form-item.vue";
import fieldQueryDateFormItem from "./field-query/field-query-date-form-item.vue";
export default {
  components: {
    fieldQuerySelectFormItem,
    fieldQueryTreeSelectFormItem,
    fieldQueryRadioFormItem,
    fieldQueryCheckboxFormItem,
    fieldQueryOrgSelectFormItem,
    fieldQueryDateFormItem,
  },
  inject: ["widgetListContext"],
  props: {
    queryFields: Array,
    widget: Object,
  },
  data() {
    let criterions = [];
    let key = "wListViewFieldQueryItems_" + this.widget.id;
    forEach(this.queryFields, function (querField) {
      let criterion = querField;
      criterion.queryType = criterion.queryOptions.queryType;
      criterions.push(criterion);
    });
    return {
      key,
      cachesItems: {},
      criterions,
      buttons: [
        // {
        //   title: this.$t('global.cancel',"取消"),
        //   code: "onCancel",
        // },
        {
          title: this.$t("global.clear", "清空"),
          code: "onClear",
        },
        // {
        //   title: this.$t("global.reset", "重置"),
        //   code: "onReset",
        // },
        {
          title: this.$t("global.search", "查询"),
          code: "onOk",
          type: "primary",
        },
      ],
    };
  },
  created() {},
  mounted() {},
  methods: {
    $t() {
      if (this.widgetListContext != undefined) {
        return this.widgetListContext.$t(...arguments);
      }
      return this.$i18n.t(...arguments);
    },
    onCancel: function () {
      this.$emit("cancel");
    },
    onReset: function () {
      var _self = this;
      forEach(_self.criterions, function (criterion, index) {
        _self.$set(criterion, "value", criterion.initValue);
        _self.$set(criterion, "displayValue", criterion.initValue);
      });
    },
    onClear: function () {
      var _self = this;
      forEach(_self.criterions, function (criterion) {
        _self.$set(criterion, "value", "");
        _self.$set(criterion, "displayValue", "");
        if (criterion.queryType == "unit" || criterion.queryType == "organization") {
          _self.$refs["fieldQuery_" + criterion.queryType + "_" + criterion.uuid + "Ref"][0].onClear();
        }
        if (criterion.queryType == "date" || criterion.queryType == "datePicker") {
          _self.$refs["fieldQuery_" + criterion.queryType + "_" + criterion.uuid + "Ref"][0].onClear();
        }
        if (
          criterion.queryType == "select2" ||
          criterion.queryType == "select" ||
          criterion.queryType == "groupSelect" ||
          criterion.queryType == "treeSelect"
        ) {
          _self.$refs["fieldQuery_" + criterion.queryType + "_" + criterion.uuid + "Ref"][0].onValueChange();
        }
      });
    },
    onOk: function () {
      this.$emit("ok", this.collectCriterions());
    },
    collectCriterions: function () {
      let _self = this;
      let retCriterions = [];
      forEach(_self.criterions, function (criterion) {
        let queryType = criterion.queryType;
        let condition = {
          columnIndex: criterion.name,
          value: criterion.value,
          type: criterion.operator,
        };
        if (queryType == "date" || queryType == "datePicker") {
          let range = criterion.queryOptions.options.range;
          if (range) {
            if (criterion.value.length != 2) {
              return;
            }
            condition.type = "between";
          } else if (isEmpty(condition.value)) {
            return;
          }
        } else {
          if (queryType == "radio") {
            if (_self.$refs["fieldQuery_" + criterion.queryType + "_" + criterion.uuid + "Ref"][0].isSwitch) {
              // 开关值为true||false,都通过
            } else if (isEmpty(condition.value)) {
              return;
            }
          } else if (isEmpty(condition.value)) {
            return;
          }
        }
        retCriterions.push(condition);
      });
      return retCriterions;
    },
    onTrigger(e, button) {
      if (button && this[button.code]) {
        this[button.code]();
      }
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
    margin-bottom: 16px;
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
    padding: 16px 16px 0;
  }

  .input-container {
    /* #ifndef APP-NVUE */
    display: flex;
    box-sizing: border-box;
    /* #endif */
    flex-direction: row;
    align-items: center;
    border: 1px solid var(--w-border-color-mobile);
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

    ::v-deep .uni-forms-item__label {
      font-size: var(--w-font-size-lg);
      color: var(--w-text-color-mobile);
      font-weight: bold;
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
    padding: 8px 16px;
  }
}
</style>
