<template>
  <uni-forms-item :label="criterion.label">
    <template v-if="options.range">
      <uni-w-datetime-picker
        :format="fixedFormat"
        :valueFormat="fixedFormat"
        :mode="mode"
        :start="minDate"
        :end="maxDate"
        type="range"
        v-model="value"
        @change="onChange"
        return-type="string"
        :startPlaceholder="$t('uni-datetime-picker.startTime', '开始时间')"
        :endPlaceholder="$t('uni-datetime-picker.endTime', '结束时间')"
        ref="fromDatePick"
      />
    </template>
    <uni-w-datetime-picker
      v-else
      :format="fixedFormat"
      :valueFormat="fixedFormat"
      :mode="mode"
      :start="minDate"
      :end="maxDate"
      :modelValue="criterion.value"
      v-model="criterion.value"
      return-type="string"
      :placeholder="$t('uni-datetime-picker.selectTime', '选择时间')"
      ref="fromDatePick"
    />
  </uni-forms-item>
</template>

<script>
import _ from "lodash";
import mixin from "./file-query-option-mixin";
import moment from "moment";
export default {
  mixins: [mixin],
  data() {
    let value = this.criterion.value;
    return {
      value,
    };
  },
  computed: {
    fixedFormat() {
      return this.options.datePattern;
    },
    mode() {
      if (this.options.datePattern.indexOf("HH") > -1 || this.options.datePattern.indexOf("DD") > -1) {
        return "date";
      } else if (this.options.datePattern.indexOf("MM") > -1) {
        return "month";
      }
      return "year";
    },
    // 今天之前不可选，beforeTodayUnselect
    minDate() {
      if (this.options.selectRangeLimitType == "beforeTodayUnselect") {
        return moment().endOf("day").add(-1, "d").format(this.fixedFormat);
      }
      return null;
    },
    // 今天之后不可选，afterTodayUnselect
    maxDate() {
      if (this.options.selectRangeLimitType == "afterTodayUnselect") {
        return moment().endOf("day").format(this.fixedFormat);
      }
      return null;
    },
  },
  mounted() {},
  methods: {
    onChange(date) {
      this.$set(this.criterion, "value", date);
    },
    onClear() {
      this.$refs.fromDatePick.clear();
    },
  },
};
</script>

<style lang="scss" scoped>
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
</style>
