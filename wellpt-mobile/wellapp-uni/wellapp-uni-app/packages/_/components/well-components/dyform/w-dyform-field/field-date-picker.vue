<template>
  <dyform-field-base class="w-date-picker" :dyformField="dyformField">
    <template slot="editable-input">
      <uni-datetime-picker
        v-if="type == 'date' || type == 'datetime'"
        :type="type"
        :start="start"
        :end="end"
        :modelValue="formData[fieldDefinition.name]"
        v-model="formData[fieldDefinition.name]"
        placeholder="请选择"
      />
    </template>
  </dyform-field-base>
</template>

<script>
import dyformFieldBase from "./field-base.vue";
const dyDateFmt = {
  yearMonthDate: "1", // 当前日期(2000-01-01)
  yearMonthDateCn: "2", // 当前日期(2000年1月1日)
  yearCn: "3", // 当前日期(2000年)
  yearMonthCn: "4", // 当前日期(2000年1月)
  monthDateCn: "5", // 当前日期(1月1日)
  weekCn: "6", // 当前日期(星期一)
  year: "7", // 当前年份(2000)
  timeHour: "8", // 当前时间(12)
  timeMin: "9", // 当前时间(12:00)
  timeSec: "10", // 当前时间(12:00:00)
  dateTimeHour: "11", // 日期到时 当前日期时间(2000-01-01 12)
  dateTimeMin: "12", // 日期到分 当前日期时间(2000-01-01 12:00)
  dateTimeSec: "13", // 日期到秒 当前日期时间(2000-01-01 12:00:00)
  yearMonth: "14", // 当前日期(2000-01)
  week: "15", // 周
};
export default {
  components: { dyformFieldBase },
  props: {
    dyformField: {
      type: Object,
      required: true,
    },
  },
  data() {
    return {
      type: "",
      fieldDefinition: this.dyformField.getDefinition(),
      formData: this.dyformField.getFormData(),
    };
  },
  created() {
    // console.log(JSON.stringify(this.fieldDefinition));
    this.type = this.getType();
    // console.log("type: " + this.type);
  },
  methods: {
    getType: function () {
      var format = "";
      var fmt = this.fieldDefinition.contentFormat;
      if (fmt == dyDateFmt.yearMonthDate) {
        format = "date";
      } else if (fmt == dyDateFmt.dateTimeHour) {
        format = "hour";
      } else if (fmt == dyDateFmt.dateTimeMin) {
        format = "datetime";
      } else if (fmt == dyDateFmt.dateTimeSec) {
        format = "datetime";
      } else if (fmt == dyDateFmt.timeHour) {
        format = "time";
      } else if (fmt == dyDateFmt.timeMin) {
        format = "time";
      } else if (fmt == dyDateFmt.timeSec) {
        format = "time";
      } else if (fmt == dyDateFmt.yearMonthDateCn) {
        format = "date";
      } else if (fmt == dyDateFmt.yearCn) {
        format = "month";
      } else if (fmt == dyDateFmt.yearMonthCn) {
        format = "month";
      } else if (fmt == dyDateFmt.monthDateCn) {
        format = "date";
      } else if (fmt == dyDateFmt.year) {
        format = "month";
      } else if (fmt == dyDateFmt.yearMonth) {
        format = "month";
      }
      return format;
    },
  },
  computed: {
    isRequired: function () {
      return this.dyformField.isRequired();
    },
    start: function () {
      return this.fieldDefinition.minDate;
    },
    end: function () {
      return this.fieldDefinition.maxDate;
    },
  },
};
</script>

<style lang="scss" scoped>
.w-date-picker {
}
</style>
