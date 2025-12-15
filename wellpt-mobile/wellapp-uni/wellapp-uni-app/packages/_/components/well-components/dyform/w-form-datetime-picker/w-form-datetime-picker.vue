<template>
  <uni-forms-item
    :ref="fieldCode"
    :label="itemLabel"
    :name="formModelItemProp"
    :label-position="widget.configuration.labelPosition"
    :class="widgetClass"
    :style="itemStyle"
  >
    <template v-if="!displayAsLabel">
      <template v-if="range">
        <uni-w-datetime-picker
          :fieldCode="fieldCode"
          :clearIcon="widget.configuration.clearBtnShow || false"
          :format="fixedFormat"
          :valueFormat="contentFormat"
          :mode="mode"
          :start="minDate"
          :end="maxDate"
          :type="pickerType"
          v-model="date"
          @change="onChange"
          return-type="string"
          :disabled="disable || readonly"
          :startPlaceholder="placeholder"
          :endPlaceholder="endPlaceholder"
          :border="widget.configuration.uniConfiguration ? widget.configuration.uniConfiguration.bordered : false"
        />
      </template>

      <template v-else>
        <uni-w-datetime-picker
          :fieldCode="fieldCode"
          :clearIcon="widget.configuration.clearBtnShow || false"
          :format="fixedFormat"
          :valueFormat="contentFormat"
          :mode="mode"
          :start="minDate"
          :end="maxDate"
          :type="pickerType"
          v-model="date"
          @change="onChange"
          return-type="string"
          :disabled="disable || readonly"
          :placeholder="placeholder"
          :border="widget.configuration.uniConfiguration ? widget.configuration.uniConfiguration.bordered : false"
        />
      </template>
    </template>
    <view v-else class="textonly">{{ computedLabel }} </view>
  </uni-forms-item>
</template>

<script>
/* 转换成完整日期时间进行计算 */
import { find } from "lodash";
import formElement from "../w-dyform/form-element.mixin";
import moment from "moment";
import formCommonMixin from "../w-dyform/form-common.mixin";
import { getOptions } from "./date-pattern-options.js";

export default {
  mixins: [formElement, formCommonMixin],
  components: {},
  data() {
    if (!this.widget.configuration.hasOwnProperty("uniConfiguration")) {
      this.$set(this.widget.configuration, "uniConfiguration", { bordered: false });
    }
    let mode =
      this.widget.configuration.datePatternJson && this.widget.configuration.datePatternJson.mode
        ? this.widget.configuration.datePatternJson.mode
        : "date";
    const range = this.widget.configuration.range;
    const date = range ? [] : null;
    let endDateFieldKey;
    if (range) {
      endDateFieldKey = this.widget.configuration.endDateField;
    }
    return {
      range,
      date,
      mode,
      endDateFieldKey,
      minValueSetting: this.widget.configuration.minValueSetting,
      minDate: null,
      maxValueSetting: this.widget.configuration.maxValueSetting,
      maxDate: null,
      defaultValueSetting: this.widget.configuration.defaultValueSetting,
      endDefaultValueSetting: this.widget.configuration.endDefaultValueSetting,
      defaultPattern: "yyyy-MM-DD HH:mm:ss",
      systemDate: undefined, // 系统时间
      endPlaceholder: this.$t("WidgetFormDatePicker.endDate", "结束时间"),
    };
  },
  computed: {
    // 固定时间 输出格式
    fixedFormat() {
      let format = this.widget.configuration.datePattern;
      //需要判断是否补0，同步格式
      if (this.widget.configuration.datePatternJson && this.widget.configuration.datePatternJson.key) {
        format =
          this.widget.configuration.zeroShow && this.widget.configuration.datePatternJson
            ? this.widget.configuration.datePatternJson.key0
            : this.widget.configuration.datePatternJson.key;
      }
      return format;
    },
    placeholder() {
      let placeholder = this.$t("WidgetFormDatePicker.placeholder", "请选择时间");
      let placeholder_t = this.$t("placeholder", this.widget.configuration.placeholder);
      if (this.range) {
        placeholder = this.$t("WidgetFormDatePicker.startDate", "开始时间");
        if (placeholder_t) {
          let _placeholder = placeholder_t.split(";");
          placeholder = _placeholder[0];
          this.endPlaceholder = _placeholder[1];
        }

        return placeholder;
      }
      return placeholder_t || placeholder;
    },
    // 针对没有完整日期，无法通过moment处理的值，使用内容格式用于保存
    contentFormat() {
      let datePatternOptions = getOptions();
      let datePatternJson = this.widget.configuration.datePatternJson;
      if (!datePatternJson.contentFormat) {
        datePatternJson.contentFormat = find(datePatternOptions[this.widget.configuration.datePatternType], {
          key0: datePatternJson.key0,
        }).contentFormat;
      }
      return datePatternJson.contentFormat;
    },
    pickerType() {
      let type = this.widget.configuration.datePatternType;
      if (this.range) {
        type += "range";
      }
      return type;
    },
    computedLabel() {
      let date = "",
        endDate = "";
      if (this.range) {
        if (this.date.length) {
          date = this.date[0];
          endDate = this.date[1];
        }
      } else {
        date = this.date;
      }
      if (date) {
        if (date.length < 19) {
          date = moment(date, this.contentFormat).format(this.fixedFormat);
        } else {
          date = moment(date).format(this.fixedFormat);
        }
      }
      if (endDate) {
        if (endDate.length < 19) {
          endDate = moment(endDate, this.contentFormat).format(this.fixedFormat);
        } else {
          endDate = moment(endDate).format(this.fixedFormat);
        }
      }
      if (this.range && (date || endDate)) {
        date = `${date} ~ ${endDate}`;
      }
      return date;
    },
  },
  created() {
    if (this.widget.configuration.isDatabaseField && this.widget.subtype == "Range") {
      if (
        this.widget.configuration.endDateField != undefined &&
        !this.form.formData.hasOwnProperty(this.widget.configuration.endDateField)
      ) {
        this.$set(this.form.formData, this.widget.configuration.endDateField, undefined);
      }
      if (this.form.$fieldset == undefined) {
        this.form.$fieldset = {};
      }
      if (this.widget.configuration.endDateField != undefined) {
        this.form.$fieldset[this.widget.configuration.endDateField] = this.proxyEndFieldInst();
      }
    }
    this.setRangeByFixedDate();
    this.setRangeBySystemDate();
    this.setValue(this.formData[this.fieldCode]);
  },
  methods: {
    setValue(value) {
      let newValue = value;
      this.date = undefined;
      if (newValue) {
        if (this.range) {
          if (!Array.isArray(this.date)) {
            this.date = [];
          }
          this.date[0] = newValue;
          if (this.formData[this.endDateFieldKey]) {
            this.date[1] = this.formData[this.endDateFieldKey];
          }
        } else {
          this.date = newValue;
        }
      } else {
        this.date = undefined;
        if (this.range) {
          this.date = [];
        }
        if (this.widget.configuration.valueCreateMethod === "4") {
          this.setDefaultByFixed();
          this.setDefaultBySystem();
        }
      }
    },
    // 通过系统时间设置默认值
    setDefaultBySystem() {
      let newValue, endValue;
      this.setSystemDate().then(() => {
        let date = this.systemDate;
        this.getSystemDate({
          date,
          setting: this.defaultValueSetting,
        }).then(({ date }) => {
          newValue = date.format(this.contentFormat);
          this.formData[this.fieldCode] = newValue;
          const fullDate = date.format(this.defaultPattern);
          if (this.range) {
            this.$set(this.date, 0, fullDate);
          } else {
            this.date = fullDate;
          }
          this.emitChange();
        });

        if (this.range) {
          this.getSystemDate({
            date,
            setting: this.endDefaultValueSetting,
          }).then(({ date }) => {
            endValue = date.format(this.contentFormat);
            const fullDate = date.format(this.defaultPattern);
            this.$set(this.date, 1, fullDate);
            this.form.setFieldValue(this.endDateFieldKey, endValue);
            this.emitChange();
          });
        }
      });
    },
    // 通过固定时间设置默认值
    setDefaultByFixed() {
      let newValue, endValue;
      if (
        this.defaultValueSetting &&
        this.defaultValueSetting.valueType === "fixed" &&
        this.defaultValueSetting.fixedDateValue
      ) {
        newValue = this.defaultValueSetting.fixedDateValue;
      }
      if (
        this.range &&
        this.endDefaultValueSetting &&
        this.endDefaultValueSetting.valueType === "fixed" &&
        this.endDefaultValueSetting.fixedDateValue
      ) {
        endValue = this.endDefaultValueSetting.fixedDateValue;
      }
      if (this.range) {
        if (newValue) {
          this.$set(this.date, 0, newValue);
        }
        if (endValue) {
          this.$set(this.date, 1, endValue);
        }
      } else {
        if (newValue) {
          this.date = newValue;
        }
      }
    },
    //  通过系统时间设置范围（最小时间和最大时间）
    setRangeBySystemDate() {
      this.setSystemDate().then(() => {
        let date = this.systemDate;

        this.getSystemDate({
          date,
          setting: this.minValueSetting,
        }).then(({ date }) => {
          this.minDate = date.format(this.defaultPattern);
        });

        this.getSystemDate({
          date,
          setting: this.maxValueSetting,
        }).then(({ date }) => {
          this.maxDate = date.format(this.defaultPattern);
        });
      });
    },
    // 通过固定时间设置范围（最小时间和最大时间）
    setRangeByFixedDate() {
      if (this.minValueSetting && this.minValueSetting.valueType === "fixed" && this.minValueSetting.fixedDateValue) {
        let newVal = this.minValueSetting.fixedDateValue;

        this.minDate = newVal;
      }
      if (this.maxValueSetting && this.maxValueSetting.valueType === "fixed" && this.maxValueSetting.fixedDateValue) {
        let newVal = this.maxValueSetting.fixedDateValue;
        this.maxDate = newVal;
      }
    },
    // 设置系统时间
    setSystemDate() {
      return new Promise((resolve, reject) => {
        if (this.systemDate) {
          resolve();
        } else {
          if (
            this.defaultValueSetting.valueType === "system" ||
            this.minValueSetting.valueType === "system" ||
            this.maxValueSetting.valueType === "system" ||
            (this.endDefaultValueSetting && this.endDefaultValueSetting.valueType === "system")
          ) {
            this.getWorkingDayOffset().then(() => {
              resolve();
            });
          }
        }
      });
    },
    // 获取系统日期 传入完整日期时间字符串 返回moment 对象
    getSystemDate({ date, setting }) {
      return new Promise((resolve, reject) => {
        if (setting && setting.valueType === "system") {
          if (setting.offset) {
            this.getSystemOffset({
              date,
              setting,
            }).then((date) => {
              resolve({ date, setting });
            });
          } else {
            // 没有偏移量
            resolve({ date: moment(date, this.defaultPattern), setting });
          }
        }
      });
    },
    // 计算系统时间偏移量
    getSystemOffset({ date, setting }) {
      return new Promise((resolve, reject) => {
        if (setting.offsetType == "naturalDay" || setting.offsetUnit == "year" || setting.offsetUnit == "month") {
          // 偏移“年、月”按自然日计算
          resolve(this.getNaturalDayOffset({ date, setting }));
        } else {
          this.getWorkingDayOffset({
            date,
            setting,
          }).then((newDate) => {
            resolve(newDate);
          });
        }
      });
    },
    // 计算自然日偏移量
    getNaturalDayOffset({ date, setting }) {
      let calculateDate = null;
      if (setting.offset > 0) {
        calculateDate = moment(date).add(setting.offset, setting.offsetUnit);
      } else {
        calculateDate = moment(date).subtract(Math.abs(setting.offset), setting.offsetUnit);
      }
      return calculateDate;
    },
    // 计算工作日偏移量
    getWorkingDayOffset({ date, setting = {} } = {}) {
      const offset = setting.offset || 0; //偏移量
      let offsetUnit = "WorkingDay"; //WorkingDay WorkingHour WorkingMinute
      switch (setting.offsetUnit) {
        case "minute": //'分'
          offsetUnit = "WorkingMinute";
          break;
        case "hour": //'时'
          offsetUnit = "WorkingHour";
          break;
      }
      if (!date) {
        date = moment();
      }
      if (typeof date === "object") {
        date = date.format(this.defaultPattern);
      }
      return new Promise((resolve, reject) => {
        this.$axios
          .get("/api/ts/work/time/plan/getWorkDate", {
            params: {
              code: this.widget.configuration.code,
              amout: offset,
              fromDate: date,
              workUnit: offsetUnit,
              autoDelay: true,
            },
          })
          .then((res) => {
            const { data, header } = res;
            if (data.code == 0 && data.data) {
              if (!this.systemDate) {
                this.systemDate = moment(header.date).format(this.defaultPattern);
              }
              resolve(moment(data.data, this.defaultPattern));
            }
          });
      });
    },
    onChange(date) {
      if (this.range) {
        this.formData[this.fieldCode] = date[0];
        this.form.setFieldValue(this.endDateFieldKey, date[1]);
      } else {
        this.formData[this.fieldCode] = date;
      }
      this.widgetDyformContext.$refs.form.setValue(this.fieldCode, this.formData[this.fieldCode]);
      this.emitChange();
    },
    getFormattedMoment() {
      if (this.date != undefined) {
        let date = Array.isArray(this.date) ? this.date[0] : this.date;
        date = moment(date, this.contentFormat).format(this.defaultPattern);
        return date ? moment(date, this.defaultPattern) : undefined;
      }
      return undefined;
    },
    proxyEndFieldInst() {
      let _this = this;
      let handler = {
        setValue(val) {
          _this.formData[_this.widget.configuration.endDateField] = val;
        },
        getFormattedMoment() {
          if (this.date != undefined) {
            let date = Array.isArray(this.date) ? this.date[1] : this.date;
            date = moment(date, this.contentFormat).format(this.defaultPattern);
            return date ? moment(date, this.defaultPattern) : undefined;
          }
          return undefined;
        },
        getValue() {
          return _this.formData[_this.widget.configuration.endDateField];
        },
      };
      return new Proxy(this, {
        get(target, propKey) {
          if (typeof target[propKey] == "function") {
            if (handler[propKey] != undefined) {
              return handler[propKey];
            }
          }
          return target[propKey];
        },
      });
    },
    // 主要用于生成moment时间，便以实现时间转换
    getMomentData(val, datePattern) {
      let data = moment(val, datePattern);
      if (data._d == "Invalid Date") {
        if (moment(val)._d == "Invalid Date") {
          return moment(val, this.defaultPattern);
        }
        return moment(val);
      }
      return data;
    },
    onFilter({ searchValue, comparator, source, ignoreCase }) {
      let sourceMoment = typeof source == "string" ? this.getMomentData(source, this.datePattern) : source;
      let searchMoment = null;
      if (Array.isArray(searchValue)) {
        searchMoment =
          typeof searchValue[0] == "string" || searchValue[0] == undefined
            ? [
                searchValue == "" ? undefined : this.getMomentData(searchValue[0], this.datePattern),
                searchValue[1] == "" ? undefined : this.getMomentData(searchValue[1], this.datePattern),
              ]
            : searchValue;
      } else {
        searchMoment = typeof searchValue == "string" ? this.getMomentData(searchValue, this.datePattern) : searchValue;
      }
      if (sourceMoment != undefined) {
        if (comparator == "like") {
          return typeof source == "string " ? source.indexOf(searchValue) != -1 : false;
        } else if (comparator == ">=") {
          return sourceMoment.isSameOrAfter(searchMoment);
        } else if (comparator == "<=") {
          return sourceMoment.isSameOrBefore(searchMoment);
        } else if (comparator == "<") {
          return sourceMoment.isBefore(searchMoment);
        } else if (comparator == ">") {
          return sourceMoment.isAfter(searchMoment);
        } else if (comparator == "!=") {
          return !searchMoment.isSame(sourceMoment);
        } else if (comparator == "between") {
          return sourceMoment.isBetween(searchMoment[0], searchMoment[1], undefined, "[]");
        }
        return searchMoment.isSame(sourceMoment);
      }

      return false;
    },
  },
};
</script>
