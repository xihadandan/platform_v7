<template>
  <picker mode="multiSelector" :value="currentValue" :range="timeArray" @change="onChange" @columnchange="columnchange">
    <slot :valueLabel="valueLabel">
      <view class="uni-time-x--border">{{ valueLabel }}</view>
    </slot>
  </picker>
</template>

<script>
import moment from "moment";
import { padDateTime } from "./util.js";
export default {
  name: "TimeModelPicker",
  props: {
    value: {
      type: String,
      default: "",
    },
    valueFormat: {
      type: String,
      default: "HH:mm:ss",
    },
    format: {
      type: String,
      default: "HH时mm分ss秒",
    },
    start: {
      type: String,
      default: "",
    },
    end: {
      type: String,
      default: "",
    },
  },
  data() {
    const colCount = this.valueFormat.split(":").length; // 列数 时分秒
    const dataFormat = "yyyy-MM-DD";
    return {
      colCount,
      timeArray: [],
      currentValue: [],
      valueLabel: "",
      dataFormat,
      curDate: this.moment().format(dataFormat) + " ",
    };
  },
  watch: {
    value: {
      immediate: true,
      handler(newValue) {
        this.currentValue = this.checkValue(newValue);
      },
    },
    start: {
      immediate: true,
      handler() {
        this.currentValue = this.checkValue(this.value);
      },
    },
    end: {
      immediate: true,
      handler() {
        this.currentValue = this.checkValue(this.value);
      },
    },
  },
  created() {
    this.createTime();
  },
  methods: {
    moment,
    checkValue(value, syncLabel = true) {
      let valueArr = [];
      if (value) {
        let fullStart,
          fullEnd,
          fullValue = padDateTime(value);

        if (this.start) {
          fullStart = padDateTime(this.start);
        }
        if (this.end) {
          fullEnd = padDateTime(this.end);
        }

        if (fullStart && new Date(fullValue) < new Date(fullStart)) {
          fullValue = fullStart;
        } else if (fullEnd && new Date(fullValue) > new Date(fullEnd)) {
          fullValue = fullEnd;
        }
        fullValue = this.moment(fullValue).format(this.dataFormat + " " + this.valueFormat);
        value = fullValue.split(" ")[1];

        if (syncLabel) {
          this.valueLabel = this.moment(fullValue).format(this.format);
        }
        valueArr = value.split(":");
      } else {
        // 默认值
        valueArr = this.moment().format(this.valueFormat).split(":");
      }
      return valueArr;
    },
    createTime() {
      let hours = [],
        minutes = [],
        seconds = [];

      hours.splice(0, hours.length);
      for (let i = 0; i < 24; i++) {
        hours.push((i < 10 ? "0" : "") + i);
      }
      this.timeArray.push(hours);
      if (this.colCount > 1) {
        minutes.splice(0, minutes.length);
        for (let i = 0; i < 60; i++) {
          minutes.push((i < 10 ? "0" : "") + i);
        }
        this.timeArray.push(minutes);
      }
      if (this.colCount > 2) {
        seconds.splice(0, seconds.length);
        for (let i = 0; i < 60; i++) {
          seconds.push((i < 10 ? "0" : "") + i);
        }
        this.timeArray.push(seconds);
      }
    },
    onChange(event) {
      const valueArray = event.detail.value;
      let value = valueArray.map((val, i) => this.timeArray[i][val]).join(":");
      this.$emit("input", value);

      this.valueLabel = value;
      this.$emit("change", {
        value,
        fullDate: this.curDate + value,
      });
    },
    columnchange(event) {
      this.$emit("columnchange", ...arguments);
      const column = event.detail.column;

      const curValue = this.timeArray[column][event.detail.value];
      const currentValue = JSON.parse(JSON.stringify(this.currentValue));
      currentValue[column] = curValue;

      this.currentValue = this.checkValue(currentValue.join(":"), false);

      if (this.$listeners.error) {
      }
    },
    clearCalender() {
      this.valueLabel = "";
    },
  },
};
</script>

<style lang="scss">
.uni-time-x--border {
  height: 35px;
  line-height: 35px;
  font-size: 14px;
  padding-left: 5px;
  box-sizing: border-box;
  border-radius: 4px;
  border: 1px solid #e5e5e5;
}
</style>
